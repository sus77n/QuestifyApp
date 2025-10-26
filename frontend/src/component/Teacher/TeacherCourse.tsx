import {useRef } from "react";
import {
  Table,
  Button,
  Input,
  Space,
  Popconfirm,
  message, Tag,
} from "antd";
import {
  SearchOutlined,
  EditOutlined,
  DeleteOutlined,
} from "@ant-design/icons";
import type { InputRef } from "antd";
import type { ColumnType, ColumnsType } from "antd/es/table";
import { LearningUnitDTO } from "../../model/LearningUnitDTO";
import { useGetAllLearningUnitsByLevelQuery } from "../../API/service/learningUnit.service";

export default function TeacherCourse() {
  const {
    data: courses = [],
    isLoading,
    isError,
  } = useGetAllLearningUnitsByLevelQuery(1);

  const searchInput = useRef<InputRef>(null);

  const getColumnSearchProps = (
      dataIndex: keyof LearningUnitDTO
  ): ColumnType<LearningUnitDTO> => ({
    filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters }) => (
        <div style={{ padding: 8 }}>
          <Input
              ref={searchInput}
              placeholder={`Search ${String(dataIndex)}`}
              value={selectedKeys[0]}
              onChange={(e) => {
                const value = e.target.value;
                setSelectedKeys(value ? [value] : []);
                confirm({ closeDropdown: false }); // search realtime
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

  const creatorFilters =
      Array.from(new Set((courses || []).map((c) => c.createdBy))).filter(Boolean).map(
          (creator) => ({
            text: creator,
            value: creator,
          })
      );

  const handleDelete = (id: number) => {
    message.success(`Deleted course #${id}`);
  };

  const handleEdit = (course: LearningUnitDTO) => {
    message.info(`Edit course: ${course.name}`);
  };

  const columns: ColumnsType<LearningUnitDTO> = [
    {
      title: "ID",
      dataIndex: "id",
      sorter: (a, b) => a.id! - b.id!,
      width: 80,
      ...getColumnSearchProps("id"),
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
            <Button
                icon={<EditOutlined />}
                onClick={() => handleEdit(record)}
            />
            <Popconfirm
                title="Delete this course?"
                onConfirm={() => handleDelete(record.id!)}
            >
              <Button
                  type="text"
                  danger
                  icon={<DeleteOutlined style={{ color: "inherit" }} />}
              />
            </Popconfirm>
          </Space>
      ),
    },
  ];

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
            <h1  className="text-xl font-bold text-text-color">Total: {courses?.length} courses</h1>
          </div>

          <Table
              columns={columns}
              dataSource={courses || []}
              loading={isLoading}
              rowKey="id"
              bordered
              scroll={{ x: true }}
          />
        </div>
      </div>
  );
}
