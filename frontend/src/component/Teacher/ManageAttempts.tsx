import React, { useState } from "react";
import { Table, Tag, Input, Select, Button } from "antd";
import { SearchOutlined, EyeOutlined, ArrowLeftOutlined } from "@ant-design/icons";
import { useParams, useNavigate } from "react-router-dom";
import TeacherPage from "./TeacherPage"; // Nhớ import đúng đường dẫn
import MyButton from "../material/material"; // Nhớ import đúng đường dẫn

// Mock Data
const MOCK_ATTEMPTS = Array.from({ length: 15 }).map((_, i) => ({
    id: `att_${i + 1}`,
    studentName: `Student ${i + 1}`,
    email: `student${i + 1}@example.com`,
    submitDate: `2023-10-${(i % 30) + 1} 14:30`,
    score: Math.floor(Math.random() * 101),
    duration: `${Math.floor(Math.random() * 40) + 5} mins`,
    status: i % 4 === 0 ? "Failed" : i % 5 === 0 ? "In Progress" : "Passed",
}));

export default function ManageAttempts() {
    const { learningUnitId } = useParams();
    const navigate = useNavigate();

    // State cho bộ lọc
    const [searchText, setSearchText] = useState("");
    const [statusFilter, setStatusFilter] = useState("All");

    // Lọc dữ liệu giả
    const filteredData = MOCK_ATTEMPTS.filter((item) => {
        const matchName = item.studentName.toLowerCase().includes(searchText.toLowerCase());
        const matchStatus = statusFilter === "All" || item.status === statusFilter;
        return matchName && matchStatus;
    });

    const columns = [
        {
            title: "Student Name",
            dataIndex: "studentName",
            render: (text: string, record: any) => (
                <div>
                    <div className="font-medium text-gray-800">{text}</div>
                    <div className="text-xs text-gray-500">{record.email}</div>
                </div>
            ),
        },
        { title: "Submit Date", dataIndex: "submitDate" },
        { title: "Duration", dataIndex: "duration" },
        {
            title: "Score",
            dataIndex: "score",
            render: (score: number) => (
                <span className={`font-semibold ${score >= 50 ? 'text-green-600' : 'text-red-600'}`}>
                    {score}/100
                </span>
            ),
        },
        {
            title: "Status",
            dataIndex: "status",
            render: (status: string) => {
                let color = status === "Passed" ? "green" : status === "Failed" ? "red" : "blue";
                return <Tag color={color}>{status}</Tag>;
            },
        },
        {
            title: "Action",
            render: (_: any, record: any) => (
                <Button
                    type="link"
                    icon={<EyeOutlined />}
                    onClick={() => console.log("View detail attempt:", record.id)}
                >
                    View Details
                </Button>
            ),
        },
    ];

    return (
        <TeacherPage
            title="Manage Attempts"
            breadcrumb={[
                { label: "Home", path: "/teacher/dashboard" },
                { label: "Courses", path: "/teacher/courses" },
                { label: "Unit Details" }, // Thường có API sẽ lấy tên Unit thật
                { label: "Attempts" },
            ]}
            extra={
                <MyButton
                    text="Back to Lesson"
                    icon={<ArrowLeftOutlined />}
                    onClick={() => navigate(-1)}
                    className="bg-gray-500 border-gray-500 hover:text-gray-500"
                />
            }
        >
            <div className="flex flex-col h-full">
                {/* TOOLBAR */}
                <div className="flex gap-4 mb-4">
                    <Input
                        placeholder="Search student name..."
                        prefix={<SearchOutlined />}
                        value={searchText}
                        onChange={(e) => setSearchText(e.target.value)}
                        className="w-64"
                    />
                    <Select
                        defaultValue="All"
                        style={{ width: 150 }}
                        onChange={(value) => setStatusFilter(value)}
                        options={[
                            { value: 'All', label: 'All Status' },
                            { value: 'Passed', label: 'Passed' },
                            { value: 'Failed', label: 'Failed' },
                            { value: 'In Progress', label: 'In Progress' },
                        ]}
                    />
                </div>

                {/* TABLE */}
                <div className="flex-1 overflow-auto bg-white rounded-lg shadow-sm border border-gray-200">
                    <Table
                        columns={columns}
                        dataSource={filteredData}
                        rowKey="id"
                        pagination={{ pageSize: 10 }}
                    />
                </div>
            </div>
        </TeacherPage>
    );
}