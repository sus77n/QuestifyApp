import { useRef, useState } from "react";
import {
    Table,
    Button,
    Input,
    Space,
    Tag,
    Modal,
    Form,
    Popconfirm,
    message,
} from "antd";
import {
    SearchOutlined,
    EditOutlined,
    PlusOutlined,
    DeleteOutlined,
    BookOutlined,
} from "@ant-design/icons";

import type { InputRef } from "antd";
import type { ColumnType, ColumnsType } from "antd/es/table";

import {
    useGetAllCoursesQuery,
    useAddCourseMutation,
    useEditCourseMutation,
    useDeleteCourseMutation,
} from "../../API/service/course.service";

import { CourseDTO } from "../../model/LearningUnitDTO";
import {useNavigate} from "react-router-dom";
import MyButton from "../material/material";
import TeacherPage from "./TeacherPage";

export default function TeacherCourse() {
    const { data: courses = [], isLoading, isError, refetch } = useGetAllCoursesQuery();
    const [addCourse] = useAddCourseMutation();
    const [editCourse] = useEditCourseMutation();
    const [deleteCourse] = useDeleteCourseMutation();

    const navigate = useNavigate();
    const [form] = Form.useForm();
    const searchInput = useRef<InputRef>(null);

    const [isModalVisible, setIsModalVisible] = useState(false);
    const [editingCourse, setEditingCourse] = useState<CourseDTO | null>(null);

    // ----------------------- SEARCH -----------------------
    const getColumnSearchProps = (
        dataIndex: keyof CourseDTO
    ): ColumnType<CourseDTO> => ({
        filterDropdown: ({ setSelectedKeys, selectedKeys, confirm }) => (
            <div style={{ padding: 8 }}>
                <Input
                    ref={searchInput}
                    placeholder={`Search ${String(dataIndex)}`}
                    value={selectedKeys[0]}
                    onChange={(e) => {
                        const value = e.target.value;
                        setSelectedKeys(value ? [value] : []);
                        confirm({ closeDropdown: false });
                    }}
                    style={{ marginBottom: 8, display: "block" }}
                />
            </div>
        ),
        filterIcon: (filtered: boolean) => (
            <SearchOutlined style={{ color: filtered ? "#1677ff" : undefined }} />
        ),
        onFilter: (value, record) =>
            (record[dataIndex] ?? "")
                .toString()
                .toLowerCase()
                .includes((value as string).toLowerCase()),
    });

    // ----------------------- OPEN MODAL -----------------------
    const openAddModal = () => {
        setEditingCourse(null);
        form.resetFields();
        setIsModalVisible(true);
    };

    const openEditModal = (course: CourseDTO) => {
        setEditingCourse(course);
        form.setFieldsValue(course);
        setIsModalVisible(true);
    };

    const handleSubmit = async () => {
        try {
            const values = await form.validateFields();

            if (editingCourse) {
                await editCourse({ id: editingCourse.id, ...values }).unwrap();
                message.success("Course updated successfully!");
            } else {
                await addCourse(values).unwrap();
                message.success("Course added successfully!");
            }

            setIsModalVisible(false);
            form.resetFields();
            refetch();

        } catch (err) {
            message.error("Failed to save course.");
        }
    };


    // ----------------------- DELETE -----------------------
    const handleDelete = async (id: string) => {
        try {
            await deleteCourse(id).unwrap();
            message.success("Course deleted successfully!");
            refetch();
        } catch {
            message.error("Failed to delete course.");
        }
    };

    // ----------------------- TABLE COLUMNS -----------------------
    const columns: ColumnsType<CourseDTO> = [
        {
            title: "#",
            width: 60,
            render: (_: any, __: any, index: number) => index + 1,
        },
        {
            title: "Code",
            dataIndex: "code",
            sorter: (a, b) => a.code.localeCompare(b.code),
            ...getColumnSearchProps("code"),
        },
        {
            title: "Name",
            dataIndex: "name",
            sorter: (a, b) => a.name.localeCompare(b.name),
            ...getColumnSearchProps("name"),
        },
        {
            title: "Status",
            dataIndex: "status",
            filters: [
                { text: "Enabled", value: 1 },
                { text: "Disabled", value: 0 },
            ],
            onFilter: (value, record) => record?.status === value,
            render: (status) =>
                status === 1 ? (
                    <Tag color="green">Enabled</Tag>
                ) : (
                    <Tag color="red">Disabled</Tag>
                ),
        },
        {
            title: "Created At",
            dataIndex: "createdAt",
            render: (v: string | null) =>
                v ? new Date(v).toLocaleString() : "-",
            sorter: (a, b) =>
                (a.createdAt ?? "").localeCompare(b.createdAt ?? ""),
            ...getColumnSearchProps("createdAt"),
        },
        {
            title: "Actions",
            key: "actions",
            width: 350,
            render: (_, record: CourseDTO) => (
                <Space>
                    {/* Edit */}
                    <MyButton icon={<EditOutlined />} onClick={() => openEditModal(record)}/>

                    {/* Manage Lesson */}
                    <MyButton text="Manage Lessons" icon={<BookOutlined />} onClick={() => navigate(`/teacher/course/${record.id}/lessons`)}/>

                    {/* Delete */}
                    <Popconfirm
                        title="Delete this course?"
                        onConfirm={() => handleDelete(record.id)}
                    >
                        <Button type="dashed" danger icon={<DeleteOutlined />} />
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    // ----------------------- UI -----------------------
    if (isError)
        return (
            <div className="text-center py-8 text-red-500">
                Error loading courses
            </div>
        );

    return (
        <TeacherPage
            title="My Courses"
            breadcrumb={[
                { label: "Home", path: "/teacher/dashboard" },
                { label: "Courses" }
            ]}
            extra={
                <MyButton text="Add Course" icon={<PlusOutlined />} onClick={openAddModal}/>
            }
        >
            <div className="flex-1">

                <Table
                    columns={columns}
                    dataSource={courses}
                    loading={isLoading}
                    rowKey="id"
                    bordered
                    scroll={{ x: true }}
                />
            </div>

            {/* ---------------- MODAL ---------------- */}
            <Modal
                title={editingCourse ? "Edit Course" : "Add Course"}
                open={isModalVisible}
                onOk={handleSubmit}
                onCancel={() => setIsModalVisible(false)}
            >
                <Form layout="vertical" form={form}>
                    <Form.Item
                        label="Course Name"
                        name="name"
                        rules={[{ required: true }]}
                    >
                        <Input />
                    </Form.Item>

                    <Form.Item
                        label="Course Code"
                        name="code"
                        rules={[{ required: true }]}
                    >
                        <Input />
                    </Form.Item>

                    <Form.Item label="Description" name="description">
                        <Input.TextArea rows={4} />
                    </Form.Item>
                </Form>
            </Modal>
        </TeacherPage>
    );
}
