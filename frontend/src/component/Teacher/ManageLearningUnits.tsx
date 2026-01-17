import { useState } from "react";
import {
    Table,
    Button,
    Modal,
    Form,
    Input,
    Space,
    Tag,
    Popconfirm,
    message,
} from "antd";
import {
    PlusOutlined,
    EditOutlined,
    DeleteOutlined,
    ApartmentOutlined,
} from "@ant-design/icons";
import {
    useGetLearningUnitWithChildrenQuery,
    useCreateLearningUnitMutation,
    useUpdateLearningUnitMutation,
    useDeleteLearningUnitMutation,
} from "../../API/service/learningUnit.service";
import {LearningUnitWithChildren} from "../../model/LearningUnitDTO";
import {useParams, useNavigate} from "react-router-dom";
import MyButton from "../material/material";
import TeacherPage from "./TeacherPage";

export default function ManageLearningUnits() {
    const { courseId } = useParams();
    const navigate = useNavigate();

    if (!courseId) {
        return <div className="p-4 text-red-500">Invalid Course ID</div>;
    }
    const { data: treeData, isLoading, refetch } =
        useGetLearningUnitWithChildrenQuery({ id: courseId! });

    const courseName = treeData?.name || "Course";

    const mapTree = (
        nodes: LearningUnitWithChildren[] | null | undefined,
        prefix: string = ""
    ): any[] => {

        if (!Array.isArray(nodes)) return [];

        return nodes
            .filter((node) => node !== null && node !== undefined)
            .map((node, index) => {
                const currentNumber = prefix
                    ? `${prefix}.${index + 1}`
                    : `${index + 1}`;

                return {
                    key: node.id,
                    numbering: currentNumber,
                    name: node.name,
                    children: Array.isArray(node.children)
                        ? mapTree(node.children, currentNumber)
                        : [],
                    raw: node,
                };
            });
    };

    const rootNodes: LearningUnitWithChildren[] =
        treeData?.children ?? [];

    const tableData = Array.isArray(rootNodes)
        ? mapTree(rootNodes)
        : [];


    const [addUnit] = useCreateLearningUnitMutation();
    const [editUnit] = useUpdateLearningUnitMutation();
    const [deleteUnit] = useDeleteLearningUnitMutation();

    // Modal state
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [modalMode, setModalMode] = useState<"add" | "edit">("add");
    const [currentParentId, setCurrentParentId] = useState<string | null>(null);
    const [editingUnit, setEditingUnit] = useState<LearningUnitWithChildren | null>(null);

    const [form] = Form.useForm();

    const openAddModal = (parentId: string | null) => {
        setModalMode("add");
        setCurrentParentId(parentId);
        form.resetFields();
        setIsModalOpen(true);
    };

    const openEditModal = (unit: LearningUnitWithChildren) => {
        setModalMode("edit");
        setEditingUnit(unit);
        form.setFieldsValue({ name: unit.name });
        setIsModalOpen(true);
    };

    const handleSubmit = async () => {
        const values = await form.validateFields();

        try {
            if (modalMode === "add") {
                await addUnit({
                    parentId: currentParentId || "",
                    name: values.name,
                }).unwrap();
                message.success("Added successfully!");
            } else {
                await editUnit({
                    id: editingUnit!.id,
                    name: values.name,
                }).unwrap();
                message.success("Updated successfully!");
            }

            setIsModalOpen(false);
            refetch();
        } catch (err) {
            message.error("Something went wrong.");
        }
    };

    const handleDelete = async (id: string) => {
        try {
            await deleteUnit(id).unwrap();
            message.success("Deleted successfully!");
            refetch();
        } catch (err) {
            message.error("Failed to delete.");
        }
    };


    const columns = [
        {
            title: "Lesson",
            dataIndex: "numbering",
            render: (v: string) => <Tag color="blue">{v}</Tag>,
        },

        {
            title: "Learning Unit",
            dataIndex: "name",
            render: (_: any, record: any) => (
                <span style={{ fontWeight: 500 }}>{record.name}</span>
            ),
        },
        {
            title: "Actions",
            width: 470,
            render: (_: any, record: any) => {
                const unit = record.raw;

                const hasChildren = unit.children && unit.children.length > 0;
                const isLeaf = !hasChildren;
                const noExercise = unit.numberOfExercise === 0;

                const showAddSub = noExercise || hasChildren;
                const shouldInitializeFirst = isLeaf && noExercise;
                console.log(`should init: ${shouldInitializeFirst} and ${unit.id}` );

                return (
                    <Space>
                        {/* Edit */}
                        <Button
                            icon={<EditOutlined />}
                            onClick={() => openEditModal(unit)}
                        >
                            Name
                        </Button>

                        {/* Add Sub Button */}
                        {showAddSub && (
                            <MyButton
                                height="h-[35px]"
                                text="Add Sub"
                                icon={<PlusOutlined />}
                                onClick={() => openAddModal(unit.id)}
                            />
                        )}

                        {/* Manage Exercise Button */}
                        {isLeaf && (
                            <MyButton
                                text="Manage Exercise"
                                height="h-[35px]"
                                icon={<ApartmentOutlined />}
                                onClick={() =>
                                    navigate(`/teacher/learning-unit/${unit.id}/exercises`, {
                                        state: {
                                            lessonName: unit.name,
                                            courseName: courseName,
                                            courseId: courseId,
                                            shouldInit: shouldInitializeFirst
                                        }
                                    })
                                }
                            />
                        )}

                        {/* Delete */}
                        <Popconfirm
                            title="Delete this learning unit?"
                            onConfirm={() => handleDelete(unit.id)}
                        >
                            <Button type="dashed" danger icon={<DeleteOutlined />} />
                        </Popconfirm>
                    </Space>
                );
            }
            },
    ];

    return (
        <TeacherPage
            title={courseName}
            breadcrumb={[
                { label: "Home", path: "/teacher/dashboard" },
                { label: "My Courses", path: "/teacher/courses" },
                { label: courseName }
            ]}
            extra={
                <MyButton
                    text="Add Lesson"
                    icon={<PlusOutlined />}
                    onClick={() => openAddModal(courseId!)}
                />
            }
        >

            <div className="flex-1 overflow-auto">


                <Table
                    columns={columns}
                    dataSource={tableData}
                    tableLayout="auto"
                    loading={isLoading}
                    rowKey="key"
                    bordered
                    pagination={false}
                    expandable={{ defaultExpandAllRows: true }}
                />
            </div>

            <Modal
                title={modalMode === "add" ? "Add Learning Unit" : "Edit Learning Unit"}
                open={isModalOpen}
                okText={modalMode === "add" ? "Add" : "Save"}
                onOk={handleSubmit}
                onCancel={() => setIsModalOpen(false)}
            >
                <Form form={form} layout="vertical">
                    <Form.Item
                        label="Name"
                        name="name"
                        rules={[{ required: true, message: "Please enter learning unit name" }]}
                    >
                        <Input placeholder="Enter name..." />
                    </Form.Item>
                </Form>
            </Modal>
        </TeacherPage>
    );
}
