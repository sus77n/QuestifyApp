import { useState, useRef } from "react";
import {
    Table,
    Button,
    Input,
    Space,
    Tag,
    Modal,
    Form,
    Select,
    Popconfirm,
    message,
} from "antd";
import {
    SearchOutlined,
    EditOutlined,
    DeleteOutlined,
} from "@ant-design/icons";
import type { InputRef } from "antd";
import type { ColumnType, ColumnsType } from "antd/es/table";

import {
    useGetAllUsersQuery,
    useEditUserMutation,
    useDeleteUserMutation,
} from "../../API/service/user.service";
import { UserDTO } from "../../model/UserDTO";

export default function ManageUser() {
    const { data: users, isLoading, refetch } = useGetAllUsersQuery();
    const [editUser] = useEditUserMutation();
    const [deleteUser] = useDeleteUserMutation();

    const [selectedUser, setSelectedUser] = useState<UserDTO | null>(null);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [form] = Form.useForm();


    const searchInput = useRef<InputRef>(null);

    const getColumnSearchProps = (dataIndex: keyof UserDTO): ColumnType<UserDTO> => ({
        filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters }) => (
            <div style={{ padding: 4 }}>
                <Input
                    ref={searchInput}
                    placeholder={`Search ${String(dataIndex)}`}
                    value={selectedKeys[0]}
                    onChange={(e) => {
                        const value = e.target.value;
                        setSelectedKeys(value ? [value] : []);
                        confirm({ closeDropdown: false }); // lọc ngay khi gõ
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

    const handleEdit = (user: UserDTO) => {
        setSelectedUser(user);
        form.setFieldsValue(user);
        setIsModalVisible(true);
    };

    const handleUpdate = async () => {
        try {
            const values = await form.validateFields();
            await editUser({ id: selectedUser!.id, ...values }).unwrap();
            message.success("User updated successfully!");
            setIsModalVisible(false);
            refetch();
        } catch (err) {
            message.error("Failed to update user.");
        }
    };

    const handleDelete = async (id: number) => {
        try {
            await deleteUser(id).unwrap();
            message.success("User deleted successfully!");
            refetch();
        } catch (err) {
            message.error("Failed to delete user.");
        }
    };

    const columns: ColumnsType<UserDTO> = [
{
            title: "No.",
            key: "stt",
            width: 70,
            align: "center",
            render: (_, __, index) => index + 1, 
        },
        {
            title: "Username",
            dataIndex: "username",
            sorter: (a, b) => a.username.localeCompare(b.username),
            ...getColumnSearchProps("username"),
        },
        {
            title: "Email",
            dataIndex: "email",
            sorter: (a, b) => a.email.localeCompare(b.email),
            ...getColumnSearchProps("email"),
        },
        {
            title: "Full Name",
            key: "fullName",
            sorter: (a, b) =>
                `${a.firstName ?? ""} ${a.lastName ?? ""}`.localeCompare(
                    `${b.firstName ?? ""} ${b.lastName ?? ""}`
                ),
            render: (_, record) => `${record.firstName ?? ""} ${record.lastName ?? ""}`,
            filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters }) => (
                <div style={{ padding: 8 }}>
                    <Input
                        placeholder="Search full name"
                        value={selectedKeys[0]}
                        onChange={(e) => {
                            const value = e.target.value;
                            setSelectedKeys(value ? [value] : []);
                            confirm({ closeDropdown: false }); // lọc realtime
                        }}
                        style={{ marginBottom: 8, display: "block" }}
                    />
                    <Button
                        onClick={() => clearFilters && clearFilters()}
                        size="small"
                        style={{ width: "100%" }}
                    >
                        Reset
                    </Button>
                </div>
            ),
            filterIcon: (filtered: boolean) => (
                <SearchOutlined style={{ color: filtered ? "#1677ff" : undefined }} />
            ),
            onFilter: (value, record) =>
                `${record.firstName ?? ""} ${record.lastName ?? ""}`
                    .toLowerCase()
                    .includes((value as string).toLowerCase()),
        },

        {
            title: "Role",
            dataIndex: "role",
            filters: [
                { text: "Admin", value: "ADMIN" },
                { text: "Teacher", value: "TEACHER" },
                { text: "Student", value: "STUDENT" },
            ],
            onFilter: (value, record) => record.role === value,
            render: (role: string) => {
                const color =
                    role === "ADMIN" ? "red" : role === "TEACHER" ? "gold" : "blue";
                return <Tag color={color}>{role}</Tag>;
            }
        },
        {
            title: "Created At",
            dataIndex: "createdAt",
            sorter: (a, b) =>
                new Date(a.createdAt ?? 0).getTime() - new Date(b.createdAt ?? 0).getTime(),
            render: (v: string | null) =>
                v ? new Date(v).toLocaleString() : "-",
        },
        {
            title: "Updated At",
            dataIndex: "updatedAt",
            sorter: (a, b) =>
                new Date(a.updatedAt ?? 0).getTime() - new Date(b.updatedAt ?? 0).getTime(),
            render: (v: string | null) =>
                v ? new Date(v).toLocaleString() : "No update yet",
        },

        {
            title: "Actions",
            key: "actions",
            fixed: "right",
            render: (_, record: UserDTO) => (
                <Space>
                    <Button type="default" icon={<EditOutlined />} onClick={() => handleEdit(record)} />
                    <Popconfirm
                        title="Delete this user?"
                        onConfirm={() => handleDelete(record.id!)}
                    >
                        <Button type="text" danger icon={<DeleteOutlined />} />
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    return (
        <div className="flex h-screen bg-light-background w-screen">
            <div className="flex-1 overflow-auto p-6">
                <div className="flex items-center justify-between mb-6">
                    <h1 className="text-3xl font-bold text-text-color">Manage Users</h1>
                    <h1  className="text-xl font-bold text-text-color">Total: {users?.length} users</h1>
                </div>

                <Table
                    columns={columns}
                    dataSource={users || []}
                    loading={isLoading}
                    rowKey="id"
                    bordered
                    scroll={{ x: true }}
                />

                <Modal
                    title="Edit User"
                    open={isModalVisible}
                    onOk={handleUpdate}
                    onCancel={() => setIsModalVisible(false)}
                    okText="Save"
                >
                    <Form form={form} layout="vertical">
                        <Form.Item
                            label="Username"
                            name="username"
                            rules={[{ required: true }]}
                        >
                            <Input />
                        </Form.Item>
                        <Form.Item label="Email" name="email" rules={[{ required: true }]}>
                            <Input />
                        </Form.Item>
                        <Form.Item label="First Name" name="firstName">
                            <Input />
                        </Form.Item>
                        <Form.Item label="Last Name" name="lastName">
                            <Input />
                        </Form.Item>
                        <Form.Item label="Role" name="role">
                            <Select>
                                <Select.Option value="ADMIN">Admin</Select.Option>
                                <Select.Option value="TEACHER">Teacher</Select.Option>
                                <Select.Option value="STUDENT">Student</Select.Option>
                            </Select>
                        </Form.Item>
                    </Form>
                </Modal>
            </div>
        </div>
    );
}
