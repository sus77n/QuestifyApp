import React, { useState } from "react";
import {
    Button,
    Input,
    Modal,
    Form,
    Popconfirm,
    message,
    Card,
    Tag,
    Typography,
    Empty
} from "antd";
import {
    SearchOutlined,
    EditOutlined,
    PlusOutlined,
    DeleteOutlined,
    BookOutlined,
    CalendarOutlined
} from "@ant-design/icons";

import {
    useGetAllCoursesQuery,
    useAddCourseMutation,
    useEditCourseMutation,
    useDeleteCourseMutation,
} from "../../API/service/course.service";

import { CourseDTO } from "../../model/LearningUnitDTO";
import { useNavigate } from "react-router-dom";
import MyButton, { PrimaryInput } from "../material/material";
import TeacherPage from "./TeacherPage";

const { Paragraph } = Typography;

export default function TeacherCourse() {
    // --- API HOOKS ---
    const { data: courses = [], isLoading, isError, refetch } = useGetAllCoursesQuery();
    const [addCourse] = useAddCourseMutation();
    const [editCourse] = useEditCourseMutation();
    const [deleteCourse] = useDeleteCourseMutation();

    // --- LOCAL STATE ---
    const navigate = useNavigate();
    const [form] = Form.useForm();
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [editingCourse, setEditingCourse] = useState<CourseDTO | null>(null);
    const [searchTerm, setSearchTerm] = useState("");

    // ----------------------- SEARCH LOGIC -----------------------
    const filteredCourses = courses.filter((c) =>
        c.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        c.code.toLowerCase().includes(searchTerm.toLowerCase())
    );

    // ----------------------- MODAL HANDLERS -----------------------
    const openAddModal = () => {
        setEditingCourse(null);
        form.resetFields();
        setIsModalVisible(true);
    };

    const openEditModal = (e: React.MouseEvent, course: CourseDTO) => {
        e.stopPropagation();
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

    // ----------------------- DELETE HANDLER -----------------------
    const handleDelete = async (e: React.MouseEvent, id: string) => {
        e.stopPropagation();
        try {
            await deleteCourse(id).unwrap();
            message.success("Course deleted successfully!");
            refetch();
        } catch {
            message.error("Failed to delete course.");
        }
    };

    // ----------------------- HELPER: Format Date -----------------------
    const formatDate = (dateString?: string | null) => {
        if (!dateString) return "N/A";
        return new Date(dateString).toLocaleDateString("vi-VN");
    };

    // ----------------------- RENDER UI -----------------------
    if (isError) return <div className="text-center py-8 text-red-500">Error loading courses</div>;

    return (
        <TeacherPage
            title="Course Management"
            breadcrumb={[
                { label: "Home", path: "/teacher/dashboard" },
                { label: "Courses" }
            ]}
            extra={
                <MyButton
                    text="Create Course"
                    icon={<PlusOutlined />}
                    onClick={openAddModal}
                    className="!h-[40px]"
                />
            }
        >
            <div className="flex flex-col h-full">

                {/* Search Bar */}
                <div className="mb-6 flex justify-between items-center">
                    <PrimaryInput
                        prefix={<SearchOutlined className="text-gray-400" />}
                        placeholder="Search by code or name..."
                        value={searchTerm}
                        onChange={(e: any) => setSearchTerm(e.target.value)}
                        className="h-10"
                    />
                    <div className="text-gray-500 text-sm italic hidden md:block">
                        Total: {filteredCourses.length} courses
                    </div>
                </div>

                {/* Grid Layout Cards */}
                {isLoading ? (
                    <div className="text-center py-10">Loading courses...</div>
                ) : filteredCourses.length > 0 ? (
                    <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6 pb-10">
                        {filteredCourses.map((course) => (
                            <Card
                                key={course.id}
                                hoverable
                                className="shadow-sm border border-gray-200 hover:shadow-md hover:border-blue-400 transition-all duration-300 flex flex-col h-full overflow-hidden"
                                bodyStyle={{ padding: 0, flex: 1, display: 'flex', flexDirection: 'column' }}
                                actions={[
                                    // Action 1: Edit
                                    <div
                                        className="text-blue-600 hover:bg-blue-50 transition-colors flex justify-center items-center w-full h-full py-2"
                                        onClick={(e) => openEditModal(e, course)}
                                    >
                                        <EditOutlined key="edit" /> <span className="ml-2 text-xs font-medium">Edit</span>
                                    </div>,
                                    // Action 2: Delete
                                    <Popconfirm
                                        title="Delete this course?"
                                        description="This action cannot be undone."
                                        onConfirm={(e) => {
                                            if (e) handleDelete(e as unknown as React.MouseEvent, course.id)
                                        }}
                                        onCancel={(e) => e?.stopPropagation()}
                                        okText="Yes"
                                        cancelText="No"
                                    >
                                        <div
                                            className="text-red-500 hover:bg-red-50 transition-colors flex justify-center items-center w-full h-full py-2"
                                            onClick={(e) => e.stopPropagation()}
                                        >
                                            <DeleteOutlined key="delete" /> <span className="ml-2 text-xs font-medium">Delete</span>
                                        </div>
                                    </Popconfirm>
                                ]}
                            >
                                <div className="p-5 flex-1 flex flex-col">

                                    {/* --- HEADER ROW: Name & Status --- */}
                                    <div className="flex justify-between items-start mb-2 gap-2">
                                        {/* 1. Name */}
                                        <h3
                                            className="text-lg font-bold text-[#02457A] leading-tight flex-1 truncate cursor-pointer hover:text-blue-600 transition-colors"
                                            onClick={() => navigate(`/teacher/course/${course.id}/lessons`)}
                                            title={course.name}
                                        >
                                            {course.name}
                                        </h3>

                                        {/* 2. Status Tag (Fixed width) */}
                                        <div className="flex-shrink-0">
                                            {course.status === 1 ? (
                                                <Tag color="success" className="mr-0">Active</Tag>
                                            ) : (
                                                <Tag color="error" className="mr-0">Inactive</Tag>
                                            )}
                                        </div>
                                    </div>

                                    {/* 3. CODE & CREATED AT */}
                                    <div className="flex items-center gap-3 mb-3 text-xs text-gray-500">
                                        <span className="font-mono font-bold bg-gray-100 px-2 py-0.5 rounded text-gray-600 border border-gray-200">
                                            {course.code}
                                        </span>
                                        <span className="flex items-center gap-1" title="Created Date">
                                            <CalendarOutlined /> {formatDate(course.createdAt)}
                                        </span>
                                    </div>

                                    {/* 4. Description */}
                                    <div className="flex-1 mt-1">
                                        <Paragraph
                                            ellipsis={{ rows: 3, expandable: false }}
                                            className="text-gray-500 text-sm mb-0"
                                        >
                                            {course.description || <span className="italic text-gray-300">No description available.</span>}
                                        </Paragraph>
                                    </div>

                                    {/* 5. View Details Button */}
                                    <div className="mt-4 pt-4 border-t border-gray-100">
                                        <Button
                                            type="primary"
                                            ghost
                                            block
                                            icon={<BookOutlined />}
                                            onClick={() => navigate(`/teacher/course/${course.id}/lessons`)}
                                            className="!border-[#02457A] !text-[#02457A] hover:!bg-[#02457A] hover:!text-white flex items-center justify-center gap-2"
                                        >
                                            View Details
                                        </Button>
                                    </div>
                                </div>
                            </Card>
                        ))}
                    </div>
                ) : (
                    <div className="flex justify-center items-center h-[300px] bg-white rounded-xl border border-dashed border-gray-300">
                        <Empty description="No courses found matching your search." />
                    </div>
                )}
            </div>

            {/* MODAL */}
            <Modal
                title={editingCourse ? "Edit Course" : "Create New Course"}
                open={isModalVisible}
                onOk={handleSubmit}
                onCancel={() => setIsModalVisible(false)}
                okText={editingCourse ? "Save Changes" : "Create"}
            >
                <Form layout="vertical" form={form}>
                    <Form.Item
                        label="Course Code"
                        name="code"
                        rules={[{ required: true, message: 'Please enter course code' }]}
                    >
                        <Input placeholder="e.g., JAVA101" />
                    </Form.Item>

                    <Form.Item
                        label="Course Name"
                        name="name"
                        rules={[{ required: true, message: 'Please enter course name' }]}
                    >
                        <Input placeholder="e.g., Java Programming Basics" />
                    </Form.Item>

                    <Form.Item label="Description" name="description">
                        <Input.TextArea
                            rows={4}
                            placeholder="Brief overview of what students will learn..."
                        />
                    </Form.Item>
                </Form>
            </Modal>
        </TeacherPage>
    );
}