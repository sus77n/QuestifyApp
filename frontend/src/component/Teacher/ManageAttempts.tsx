import React, { useState, useMemo } from "react";
import { Table, Tag, Input, Empty } from "antd";
import { SearchOutlined, EyeOutlined, ArrowLeftOutlined } from "@ant-design/icons";
import { useParams, useNavigate } from "react-router-dom";
import TeacherPage from "./TeacherPage";
import MyButton from "../material/material";
import { useGetAttemptsByLessonQuery, useGetAttemptByIdQuery } from "../../API/service/attempt.service";
import { useGetLearningUnitDetailsByIdQuery } from "../../API/service/learningUnit.service";

export default function ManageAttempts() {
    const { learningUnitId } = useParams<{ learningUnitId: string }>();
    const navigate = useNavigate();

    const { data: attemptsResponse, isLoading: isAttemptsLoading } = useGetAttemptsByLessonQuery(learningUnitId!);
    const { data: unitData } = useGetLearningUnitDetailsByIdQuery({ id: learningUnitId! });

    const [searchText, setSearchText] = useState("");

    const attemptsList = useMemo(() => {
        if (!attemptsResponse) return [];

        return attemptsResponse.map((item: any) => ({
            id: item.attemptId,
            studentId: item.userId,
            userFullName: item.username || `User ${item.userId.substring(0, 5)}`,
            email: item.userEmail || "N/A",
            rawSubmitDate: item.submittedAt ? new Date(item.submittedAt) : null,
            rawStartDate: item.startedAt ? new Date(item.startedAt) : null,
            score: item.score || 0,
            status: item.status,
        }));
    }, [attemptsResponse]);

    const filteredData = useMemo(() => {
        return attemptsList.filter((item) => {
            const searchLower = searchText.toLowerCase();
            const matchName = item.userFullName.toLowerCase().includes(searchLower);
            const matchEmail = item.email.toLowerCase().includes(searchLower);

            return matchName || matchEmail;
        });
    }, [attemptsList, searchText]);

    const columns: any = [
        {
            title: "Student",
            dataIndex: "userFullName",
            key: "userFullName",
            render: (text: string, record: any) => (
                <div className="flex flex-col">
                    <span className="font-semibold text-gray-800">
                        {text}
                    </span>
                    <span className="text-xs text-gray-500 font-medium">
                        {record.email}
                    </span>
                </div>
            ),
        },
        {
            title: "Start At",
            dataIndex: "rawStartDate",
            key: "startDate",
            render: (date: Date) => date ? date.toLocaleString() : "N/A",
            sorter: (a: any, b: any) => (a.rawStartDate?.getTime() || 0) - (b.rawStartDate?.getTime() || 0),
        },
        {
            title: "Submit At",
            dataIndex: "rawSubmitDate",
            key: "submitDate",
            render: (date: Date) => date ? date.toLocaleString() : "N/A",
            sorter: (a: any, b: any) => (a.rawSubmitDate?.getTime() || 0) - (b.rawSubmitDate?.getTime() || 0),
        },
        {
            title: "Score",
            dataIndex: "score",
            key: "score",
            sorter: (a: any, b: any) => a.score - b.score,
            render: (score: number) => (
                <span className={`font-semibold ${score >= 50 ? 'text-green-600' : 'text-red-600'}`}>
                    {score}
                </span>
            ),
        },
        {
            title: "Status",
            dataIndex: "status",
            key: "status",
            // Filter trực tiếp trên cột
            filters: [
                // { text: 'PASSED', value: 'PASSED' },
                { text: 'GRADED', value: 'GRADED' },
                // { text: 'FAILED', value: 'FAILED' },
                { text: 'IN_PROGRESS', value: 'IN_PROGRESS' },
            ],
            onFilter: (value: string, record: any) => record.status === value,
            render: (status: string) => {
                let color = "blue";
                if (status === "PASSED" || status === "GRADED") color = "green";
                if (status === "FAILED" || status === "Failed") color = "red";
                return <Tag color={color}>{status?.toUpperCase() || "UNKNOWN"}</Tag>;
            },
        },
        {
            title: "Action",
            key: "action",
            render: (_: any, record: any) => (
                <MyButton
                    icon={<EyeOutlined />}
                    text="View Details"
                    onClick={() => navigate(`/teacher/attempt-detail/${record.id}`)}
                />
            ),
        },
    ];

    return (
        <TeacherPage
            title={`${unitData?.name || "Loading..."}`}
            breadcrumb={[
                { label: "Home", path: "/teacher/dashboard" },
                { label: "Courses", path: "/teacher/courses" },
                { label: unitData?.name || "Unit Details", path: `/teacher/course/${unitData?.parentId}/lessons` },
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
                        className="w-80"
                        allowClear
                    />
                </div>

                {/* TABLE */}
                <div className="flex-1 overflow-auto bg-white rounded-lg shadow-sm border border-gray-200">
                    <Table
                        columns={columns}
                        dataSource={filteredData}
                        rowKey="id"
                        loading={isAttemptsLoading}
                        pagination={{
                            pageSize: 8,
                            showSizeChanger: true,
                        }}
                        locale={{ emptyText: <Empty description="No attempts found" /> }}
                    />
                </div>
            </div>
        </TeacherPage>
    );
}