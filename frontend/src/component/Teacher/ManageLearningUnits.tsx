import { useEffect, useMemo, useState } from "react";
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
    TreeSelect, // NEW: Thêm TreeSelect để chọn nhiều lesson
} from "antd";
import {
    PlusOutlined,
    EditOutlined,
    DeleteOutlined,
    ApartmentOutlined,
    TeamOutlined,      // NEW: Icon cho Manage Students
    HistoryOutlined,   // NEW: Icon cho Manage Attempts
    ExperimentOutlined // NEW: Icon cho Add Practice
} from "@ant-design/icons";
import {
    useGetLearningUnitWithChildrenQuery,
    useCreateLearningUnitMutation,
    useUpdateLearningUnitMutation,
    useDeleteLearningUnitMutation,
} from "../../API/service/learningUnit.service";
import { LearningUnitWithChildren } from "../../model/LearningUnitDTO";
import { useParams, useNavigate } from "react-router-dom";
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

    const rootNodes: LearningUnitWithChildren[] = treeData?.children ?? [];

    const tableData = useMemo(() => {
        return Array.isArray(rootNodes) ? mapTree(rootNodes) : [];
    }, [rootNodes]);

    // NEW: Chuyển đổi dữ liệu treeData sang định dạng của TreeSelect
    const treeSelectData = useMemo(() => {
        const mapToTreeSelect = (nodes: LearningUnitWithChildren[]): any[] => {
            return nodes.map(node => ({
                title: node.name,
                value: node.id,
                key: node.id,
                children: node.children ? mapToTreeSelect(node.children) : []
            }));
        };
        return mapToTreeSelect(rootNodes);
    }, [rootNodes]);

    const [addUnit] = useCreateLearningUnitMutation();
    const [editUnit] = useUpdateLearningUnitMutation();
    const [deleteUnit] = useDeleteLearningUnitMutation();

    // Modal state
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [modalMode, setModalMode] = useState<"add" | "edit">("add");
    const [currentParentId, setCurrentParentId] = useState<string | null>(null);
    const [editingUnit, setEditingUnit] = useState<LearningUnitWithChildren | null>(null);

    // NEW: State cho form Practice
    const [isPracticeModalOpen, setIsPracticeModalOpen] = useState(false);
    const [practiceForm] = Form.useForm();
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

    // NEW: Xử lý khi submit form Add Practice
    const handleAddPractice = async () => {
        try {
            const values = await practiceForm.validateFields();
            console.log("Dữ liệu tạo Practice: ", values);

            // TODO: Gọi API generate bài tập (gửi danh sách values.selectedLessons) lên backend ở đây
            /*
            await generatePracticeMutation({
                name: values.practiceName,
                courseId: courseId,
                lessonIds: values.selectedLessons
            }).unwrap();
            */

            message.success("Practice generation requested successfully!");
            setIsPracticeModalOpen(false);
            practiceForm.resetFields();
            refetch();
        } catch (err) {
            console.error(err);
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

    const [expandedRowKeys, setExpandedRowKeys] = useState<readonly React.Key[]>([]);

    const getAllKeys = (data: any[]): React.Key[] => {
        let keys: React.Key[] = [];
        data.forEach(item => {
            keys.push(item.key);
            if (item.children && item.children.length > 0) {
                keys = [...keys, ...getAllKeys(item.children)];
            }
        });
        return keys;
    };

    useEffect(() => {
        if (tableData.length > 0) {
            setExpandedRowKeys(getAllKeys(tableData));
        }
    }, [tableData]);

    const columns = [
        {
            title: "Lesson",
            dataIndex: "numbering",
            width: 130,
            render: (v: string) => (
                <div style={{ display: 'inline-flex', alignItems: 'center' }}>
                    <Tag color="blue" style={{ margin: 0 }}>{v}</Tag>
                </div>
            ),
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
            width: 850,
            render: (_: any, record: any) => {
                const unit = record.raw;

                const hasChildren = unit.children && unit.children.length > 0;
                const isLeaf = !hasChildren;
                const noExercise = unit.numberOfExercise === 0;

                const showAddSub = noExercise || hasChildren;
                const shouldInitializeFirst = isLeaf && noExercise;

                return (
                    <Space>
                        {/* Edit */}
                        <Button
                            icon={<EditOutlined />}
                            onClick={() => openEditModal(unit)}
                        >
                            Name
                        </Button>

                        {/* Delete */}
                        <Popconfirm
                            title="Delete this learning unit?"
                            onConfirm={() => handleDelete(unit.id)}
                        >
                            <Button type="dashed" danger icon={<DeleteOutlined />} />
                        </Popconfirm>

                        {/* NEW: Manage Attempts */}
                        <MyButton
                            text="Manage Attempts"
                            height="h-[35px]"
                            icon={<HistoryOutlined />}
                            onClick={() => navigate(`/teacher/learning-unit/${unit.id}/attempts`)}
                            className="bg-indigo-600 border-indigo-600 hover:text-indigo-600"
                        />

                        {/* NEW: Manage Students */}
                        <MyButton
                            text="Manage Students"
                            height="h-[35px]"
                            icon={<TeamOutlined />}
                            onClick={() => navigate(`/teacher/learning-unit/${unit.id}/students`)}
                            className="bg-teal-600 border-teal-600 hover:text-teal-600"
                        />



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
                // NEW: Thêm flex gap-2 để các nút dàn ngang đẹp mắt
                <div className="flex gap-2">
                    <MyButton
                        text="Add Lesson"
                        icon={<PlusOutlined />}
                        onClick={() => openAddModal(courseId!)}
                    />

                    {/* NEW: Đổi onClick mở modal Practice */}
                    <MyButton
                        text="Add Practice"
                        icon={<ExperimentOutlined />}
                        onClick={() => {
                            practiceForm.resetFields();
                            setIsPracticeModalOpen(true);
                        }}
                        className="bg-purple-600 border-purple-600 hover:text-purple-600"
                    />
                </div>
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
                    expandable={{
                        expandedRowKeys: expandedRowKeys,
                        onExpand: (expanded, record) => {
                            if (expanded) {
                                setExpandedRowKeys([...expandedRowKeys, record.key]);
                            } else {
                                setExpandedRowKeys(expandedRowKeys.filter(k => k !== record.key));
                            }
                        }
                    }}
                />
            </div>

            {/* Modal Add/Edit Lesson */}
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

            {/* NEW: Modal Add Practice */}
            <Modal
                title="Add Practice Generation"
                open={isPracticeModalOpen}
                okText="Generate"
                onOk={handleAddPractice}
                onCancel={() => setIsPracticeModalOpen(false)}
            >
                <Form form={practiceForm} layout="vertical">
                    <Form.Item
                        label="Practice Name"
                        name="practiceName"
                        rules={[{ required: true, message: "Please enter practice name" }]}
                    >
                        <Input placeholder="e.g. Midterm Practice, Chapter 1-3 Review..." />
                    </Form.Item>

                    <Form.Item
                        label="Select Lessons to Include"
                        name="selectedLessons"
                        rules={[{ required: true, message: "Please select at least one lesson" }]}
                    >
                        <TreeSelect
                            treeData={treeSelectData}
                            treeCheckable={true}
                            showCheckedStrategy={TreeSelect.SHOW_PARENT}
                            placeholder="Please select lessons"
                            style={{ width: '100%' }}
                            maxTagCount="responsive"
                        />
                    </Form.Item>
                </Form>
            </Modal>
        </TeacherPage>
    );
}