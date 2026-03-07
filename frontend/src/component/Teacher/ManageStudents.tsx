import React, { useState } from "react";
import { Table, Input, Popconfirm, Button, Progress, message } from "antd";
import { SearchOutlined, UserAddOutlined, DeleteOutlined, ArrowLeftOutlined } from "@ant-design/icons";
import { useParams, useNavigate } from "react-router-dom";
import TeacherPage from "./TeacherPage"; // Nhớ import đúng đường dẫn
import MyButton from "../material/material"; // Nhớ import đúng đường dẫn

// Mock Data
const MOCK_STUDENTS = Array.from({ length: 12 }).map((_, i) => ({
    id: `stu_${i + 1}`,
    name: `Alice ${i + 1} Nguyen`,
    email: `alice${i + 1}@student.edu`,
    joinedDate: `2024-01-0${(i % 9) + 1}`,
    progress: Math.floor(Math.random() * 100), // % hoàn thành
}));

export default function ManageStudents() {
    const { learningUnitId } = useParams();
    const navigate = useNavigate();

    const [searchText, setSearchText] = useState("");
    const [students, setStudents] = useState(MOCK_STUDENTS);

    const handleRemoveStudent = (id: string) => {
        setStudents(students.filter(s => s.id !== id));
        message.success("Removed student successfully!");
    };

    const filteredData = students.filter((item) =>
        item.name.toLowerCase().includes(searchText.toLowerCase()) ||
        item.email.toLowerCase().includes(searchText.toLowerCase())
    );

    const columns = [
        {
            title: "Student Info",
            dataIndex: "name",
            render: (text: string, record: any) => (
                <div>
                    <div className="font-medium text-gray-800">{text}</div>
                    <div className="text-sm text-gray-500">{record.email}</div>
                </div>
            ),
        },
        { title: "Joined Date", dataIndex: "joinedDate", width: 150 },
        {
            title: "Learning Progress",
            dataIndex: "progress",
            width: 300,
            render: (percent: number) => (
                <Progress percent={percent} size="small" status={percent === 100 ? "success" : "active"} />
            ),
        },
        {
            title: "Actions",
            width: 150,
            render: (_: any, record: any) => (
                <Popconfirm
                    title="Remove this student?"
                    description="Are you sure you want to remove this student from the lesson?"
                    onConfirm={() => handleRemoveStudent(record.id)}
                    okText="Yes"
                    cancelText="No"
                >
                    <Button danger type="text" icon={<DeleteOutlined />}>
                        Remove
                    </Button>
                </Popconfirm>
            ),
        },
    ];

    return (
        <TeacherPage
            title="Manage Students"
            breadcrumb={[
                { label: "Home", path: "/teacher/dashboard" },
                { label: "Courses", path: "/teacher/courses" },
                { label: "Unit Details" },
                { label: "Students" },
            ]}
            extra={
                <div className="flex gap-2">
                    <MyButton
                        text="Back"
                        icon={<ArrowLeftOutlined />}
                        onClick={() => navigate(-1)}
                        className="bg-gray-500 border-gray-500 hover:text-gray-500"
                    />
                </div>
            }
        >
            <div className="flex flex-col h-full">
                {/* TOOLBAR */}
                <div className="flex mb-4">
                    <Input
                        placeholder="Search by name or email..."
                        prefix={<SearchOutlined />}
                        value={searchText}
                        onChange={(e) => setSearchText(e.target.value)}
                        className="w-80"
                    />
                </div>

                {/* TABLE */}
                <div className="flex-1 overflow-auto bg-white rounded-lg shadow-sm border border-gray-200">
                    <Table
                        columns={columns}
                        dataSource={filteredData}
                        rowKey="id"
                        pagination={{ pageSize: 8 }}
                    />
                </div>
            </div>
        </TeacherPage>
    );
}