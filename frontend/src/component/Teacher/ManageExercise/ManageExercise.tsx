import React, { useState } from "react";
import {
    Table,
    Button,
    Modal,
    Form,
    Input,
    Space,
    Tag,
    Popconfirm,
    Select,
    message,
} from "antd";

import {
    PlusOutlined,
    EditOutlined,
    DeleteOutlined,
    FileAddOutlined,
} from "@ant-design/icons";

import { useLocation, useParams } from "react-router-dom";
import { ExerciseDTO, ExerciseType } from "../../../model/ExerciseDTO";

import {
    useCreateExerciseMutation,
    useDeleteExerciseMutation,
    useGetExercisesQuery,
    useUpdateExerciseMutation
} from "../../../API/service/exercise.service";

import MyButton from "../../material/material";
import TeacherPage from "../TeacherPage";
import MultipleChoiceBuilder, {ExerciseBuilderValue} from "./exerciseBuilders/MultipleChoiceBuilder";
import SelectMultipleBuilder from "./exerciseBuilders/SelectMultipleBuilder";
import ShortAnswerBuilder from "./exerciseBuilders/ShortAnswerBuilder";
import TrueFalseBuilder from "./exerciseBuilders/TrueFalseBuilder";

const { TextArea } = Input;

const exerciseTypes: ExerciseType[] = [
    "MULTIPLE_CHOICE",
    "SELECT_MULTIPLE",
    "SHORT_ANSWER",
    "TRUE_FALSE",
    // other 4 types skipped
];

export default function ManageExercises() {
    const { learningUnitId } = useParams();
    const location = useLocation() as any;
    const { courseName, lessonName, courseId } = location.state || {};

    const {
        data: exercises = [],
        isLoading,
        refetch
    } = useGetExercisesQuery({ lessonId: learningUnitId! });

    const [createExercise] = useCreateExerciseMutation();
    const [updateExercise] = useUpdateExerciseMutation();
    const [deleteExercise] = useDeleteExerciseMutation();

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [mode, setMode] = useState<"add" | "edit">("add");
    const [selectedType, setSelectedType] = useState<ExerciseType | null>(null);
    const [editingExercise, setEditingExercise] = useState<ExerciseDTO | null>(null);

    const [builderValue, setBuilderValue] = useState<ExerciseBuilderValue>({
        options: [],
        correctAnswers: []
    });

    const [form] = Form.useForm();

    /* ---------------- OPEN MODAL ---------------- */

    const openAddModal = () => {
        setMode("add");
        setEditingExercise(null);
        setSelectedType(null);
        form.resetFields();

        setBuilderValue({
            options: [
                { header: "1", text: "" },
                { header: "2", text: "" }
            ],
            correctAnswers: []
        });

        setIsModalOpen(true);
    };


    const openEditModal = (ex: ExerciseDTO) => {
        setMode("edit");
        setEditingExercise(ex);
        setSelectedType(ex.type);

        form.setFieldsValue({ question: ex.question });

        setBuilderValue({
            options:
                ex.options?.map((o, idx) => ({
                    header: o.header || crypto.randomUUID(),
                    text: o.text || ""
                })) || [],
            correctAnswers: (() => {
                try {
                    return JSON.parse(ex.correctAnswers || "[]");
                } catch {
                    return [];
                }
            })()
        });

        setIsModalOpen(true);
    };

    /* ---------------- MAPPING BUILDER -> PAYLOAD ---------------- */

    const mapToPayload = () => {
        const optionsMapped =
            selectedType === "SHORT_ANSWER"
                ? [] // no options
                : builderValue.options.map((o:any, index:any) => ({
                    header: String(index + 1),
                    text: o.text
                }));

        return {
            options: optionsMapped,
            correctAnswers: JSON.stringify(builderValue.correctAnswers)
        };
    };

    /* ---------------- ACTIONS ---------------- */


    const handleSave = async () => {
        const values = await form.validateFields();
        const payload = mapToPayload();

        if (mode === "add") {
            await createExercise({
                lessonId: learningUnitId,
                question: values.question,
                type: selectedType!,
                ...payload
            } as any).unwrap();
        } else if (editingExercise?.id) {
            await updateExercise({
                id: editingExercise.id,
                data: {
                    question: values.question,
                    type: selectedType!,
                    ...payload
                }
            } as any).unwrap();
        }

        message.success("Saved");
        form.resetFields();
        refetch();
    };

    const handleDelete = async (id: string) => {
        await deleteExercise(id).unwrap();
        message.success("Deleted");
        refetch();
    };

    /* ---------------- TABLE ---------------- */

    const columns = [
        {
            title: "#",
            render: (_: any, __: any, idx: number) => idx + 1
        },
        {
            title: "Question",
            dataIndex: "question"
        },
        {
            title: "Type",
            dataIndex: "type",
            render: (t: ExerciseType) => <Tag>{t}</Tag>
        },
        {
            title: "Actions",
            render: (_: any, r: ExerciseDTO) => (
                <Space>
                    <MyButton icon={<EditOutlined />} onClick={() => openEditModal(r)} />
                    <Popconfirm title="Delete?" onConfirm={() => handleDelete(r.id!)}>
                        <Button danger icon={<DeleteOutlined />} />
                    </Popconfirm>
                </Space>
            )
        }
    ];

    /* ---------------- RENDER BUILDER ---------------- */

    const renderBuilder = () => {
        if (!selectedType)
            return <p className="text-gray-500 text-sm">Select exercise type.</p>;

        const props = { value: builderValue, onChange: setBuilderValue };

        switch (selectedType) {
            case "MULTIPLE_CHOICE":
                return <MultipleChoiceBuilder {...props} />;

            case "SELECT_MULTIPLE":
                return <SelectMultipleBuilder {...props} />;

            case "SHORT_ANSWER":
                return <ShortAnswerBuilder {...props} />;

            case "TRUE_FALSE":
                return <TrueFalseBuilder  {...props}/>

            default:
                return <p className="text-yellow-600">This type is not implemented.</p>;
        }
    };

    /* ---------------- PAGE RENDER ---------------- */

    return (
        <TeacherPage
            title={`Exercises - ${lessonName ?? ""}`}
            breadcrumb={[
                { label: "Home", path: "/teacher/dashboard" },
                { label: "My Courses", path: "/teacher/courses" },
                courseName && {
                    label: courseName,
                    path: `/teacher/course/${courseId}/lessons`
                }
            ].filter(Boolean) as any}
            extra={
                <div className="flex gap-2">
                    <MyButton
                        text="Add Exercise"
                        icon={<PlusOutlined />}
                        onClick={openAddModal}
                    />
                    <MyButton text="Add By Markdown File"
                        icon={<FileAddOutlined />}
                        onClick={() => alert("We will update soon")}/>
                </div>
            }
        >
            <div>
                <Table
                    rowKey="id"
                    bordered
                    loading={isLoading}
                    dataSource={exercises}
                    columns={columns}
                />
            </div>

            {/* MODAL */}
            <Modal
                title={mode === "add" ? "Add Exercise" : "Edit Exercise"}
                open={isModalOpen}
                width={900}
                onCancel={() => setIsModalOpen(false)}
                footer={[
                    <Button key="more" onClick={handleSave}>
                        Save
                    </Button>,
                    <Button type="dashed" key="cancel" danger onClick={() => setIsModalOpen(false)}>
                        Cancel
                    </Button>
                ]}
            >
                <Form form={form} layout="vertical">
                    <Form.Item label="Exercise Type" required>
                        <Select
                            value={selectedType || undefined}
                            onChange={(v) => setSelectedType(v)}
                        >
                            {exerciseTypes.map((t) => (
                                <Select.Option key={t} value={t}>
                                    {t}
                                </Select.Option>
                            ))}
                        </Select>
                    </Form.Item>

                    <Form.Item
                        label="Question"
                        name="question"
                        rules={[{ required: true }]}
                    >
                        <TextArea rows={3} />
                    </Form.Item>

                    <div className="border-t pt-3 mt-3">
                        <h3 className="font-semibold mb-2">Exercise Content</h3>
                        {renderBuilder()}
                    </div>
                </Form>
            </Modal>
        </TeacherPage>
    );
}
