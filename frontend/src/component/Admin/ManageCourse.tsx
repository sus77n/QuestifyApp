import { useRef } from "react";
import {
    Table,
    Button,
    Input,
    Space,
    Tag,
    message,
    Popconfirm,
} from "antd";
import { SearchOutlined } from "@ant-design/icons";
import type { InputRef } from "antd";
import type { ColumnType, ColumnsType } from "antd/es/table";

import { LearningUnitDTO } from "../../model/LearningUnitDTO";
import {
    useGetAllLearningUnitsByLevelQuery,
    useUpdateLearningUnitMutation,
} from "../../API/service/learningUnit.service";

export default function ManageCourse() {
    const {
        data: courses = [],
        isLoading,
        isError,
        refetch,
    } = useGetAllLearningUnitsByLevelQuery(1);

    const [updateLearningUnit] = useUpdateLearningUnitMutation();

    const searchInput = useRef<InputRef>(null);

    // ----------------------- SEARCH -----------------------
    const getColumnSearchProps = (
        dataIndex: keyof LearningUnitDTO
    ): ColumnType<LearningUnitDTO> => ({
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

    // ----------------------- ENABLE / DISABLE -----------------------
    const handleToggleStatus = async (course: LearningUnitDTO) => {
        const newStatus = course.status === 1 ? 0 : 1;

        try {
            // tách id ra khỏi course để tránh duplicated
            const { id, ...rest } = course;

            await updateLearningUnit({
                id,
                ...rest,
                status: newStatus,
            }).unwrap();

            message.success(
                `Course "${course.name}" is now ${newStatus === 1 ? "Enabled" : "Disabled"}`
            );

            refetch();
        } catch (error) {
            message.error("Failed to update course status.");
        }
    };

    // ----------------------- CREATOR FILTER -----------------------
    const creatorFilters =
        Array.from(new Set(courses.map((c) => c.createdBy)))
            .filter(Boolean)
            .map((creator) => ({
                text: creator,
                value: creator,
            }));

    // ----------------------- TABLE COLUMNS -----------------------
    const columns: ColumnsType<LearningUnitDTO> = [
        {
            title: "ID",
            dataIndex: "id",
            sorter: (a, b) => a.id! - b.id!,
            ...getColumnSearchProps("id"),
            width: 80,
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
            onFilter: (value, record) => record.status === value,
            render: (v: number) =>
                v === 1 ? (
                    <Tag color="green">Enabled</Tag>
                ) : (
                    <Tag color="red">Disabled</Tag>
                ),
        },
        {
            title: "Created By",
            dataIndex: "createdBy",
            filters: creatorFilters,
            onFilter: (value, record) =>
                record.createdBy?.toLowerCase() === (value as string).toLowerCase(),
            sorter: (a, b) => a.createdBy.localeCompare(b.createdBy),
            render: (text: string) => (
                <Tag color="blue" style={{ textTransform: "capitalize" }}>
                    {text}
                </Tag>
            ),
        },
        {
            title: "Created At",
            dataIndex: "createdAt",
            sorter: (a, b) =>
                new Date(a.createdAt ?? 0).getTime() -
                new Date(b.createdAt ?? 0).getTime(),
            render: (v: string | null) => (v ? new Date(v).toLocaleString() : "-"),
        },
        {
            title: "Updated At",
            dataIndex: "updatedAt",
            sorter: (a, b) =>
                new Date(a.updatedAt ?? 0).getTime() -
                new Date(b.updatedAt ?? 0).getTime(),
            render: (v: string | null) =>
                v ? new Date(v).toLocaleString() : "No update yet",
        },
        {
            title: "Actions",
            key: "actions",
            fixed: "right",
            render: (_, record: LearningUnitDTO) => (
                <Space>
                    <Popconfirm
                        title={`Do you want to ${
                            record.status === 1 ? "disable" : "enable"
                        } this course?`}
                        onConfirm={() => handleToggleStatus(record)}
                    >
                        <Button
                            type={record.status === 1 ? "default" : "primary"}
                            danger={record.status === 1}
                        >
                            {record.status === 1 ? "Disable" : "Enable"}
                        </Button>
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
        <div className="flex h-screen bg-light-background w-screen">
            <div className="flex-1 overflow-auto p-6">
                <div className="flex items-center justify-between mb-6">
                    <h1 className="text-3xl font-bold text-text-color">Manage Courses</h1>
                    <h1 className="text-xl font-bold text-text-color">
                        Total: {courses.length} courses
                    </h1>
                </div>

                <Table
                    columns={columns}
                    dataSource={courses}
                    loading={isLoading}
                    rowKey="id"
                    bordered
                    scroll={{ x: true }}
                />
            </div>
        </div>
    );
}
