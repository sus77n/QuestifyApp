import React, { useMemo, useRef, useState, useEffect } from "react";
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
    Badge
} from "antd";

import {
    PlusOutlined,
    EditOutlined,
    DeleteOutlined,
    FileAddOutlined,
    SearchOutlined,
    AppstoreOutlined,
    FolderOpenOutlined
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

import MultipleChoiceBuilder, {
    ExerciseBuilderValue,
    BuilderOption
} from "./exerciseBuilders/MultipleChoiceBuilder";

import SelectMultipleBuilder from "./exerciseBuilders/SelectMultipleBuilder";
import ShortAnswerBuilder from "./exerciseBuilders/ShortAnswerBuilder";
import TrueFalseBuilder from "./exerciseBuilders/TrueFalseBuilder";
import MatchingBuilder, {
    MatchingBuilderValue,
    MatchingOption
} from "./exerciseBuilders/MatchingBuilder";
import ReorderingBuilder from "./exerciseBuilders/ReorderingBuilder";
import FillInTheBlankBuilder, { FillInBlankValue } from "./exerciseBuilders/FillInTheBlankBuilder";
import { FilterConfirmProps, FilterDropdownProps } from "antd/es/table/interface";
import { InboxStackIcon, RectangleGroupIcon, SparklesIcon } from "@heroicons/react/24/solid";
import LessonConfigPanel from "./LessonConfigPanel";
import {
    useGetLearningUnitDetailsByIdQuery,
    useInitializeLessonConfigAndCateMutation
} from "../../../API/service/learningUnit.service";
import AIGenerateModal from "./AIGenerateModal";

const { TextArea } = Input;

const exerciseTypes: ExerciseType[] = [
    "MULTIPLE_CHOICE",
    "SELECT_MULTIPLE",
    "SHORT_ANSWER",
    "TRUE_FALSE",
    "MATCHING",
    "REORDERING",
    "FILL_IN_THE_BLANK"
];

export default function ManageExercises() {
    const { learningUnitId } = useParams();
    const location = useLocation() as any;

    const { courseName, lessonName, courseId, shouldInit } = location.state || {};
    const [readyToFetch, setReadyToFetch] = useState(!shouldInit);

    const {
        data: learningUnitData,
        isLoading: isUnitLoading
    } = useGetLearningUnitDetailsByIdQuery(
        { id: learningUnitId! },
        {
            skip: !readyToFetch
        }
    );

    const { data: exercises = [], isLoading, refetch } =
        useGetExercisesQuery({ lessonId: learningUnitId! });

    const [initializeConfig, { isLoading: isInitializing }] = useInitializeLessonConfigAndCateMutation();
    const initRef = useRef(false);

    useEffect(() => {
        // Nếu đã init rồi thì thôi
        if (initRef.current) return;

        const performInit = async (reason: string) => {
            console.log(`Triggering Init: ${reason}`);
            initRef.current = true;
            try {
                await initializeConfig({ id: learningUnitId! }).unwrap();
                message.success("Configuration initialized.");
                setReadyToFetch(true);
            } catch (err) {
                console.error("Init failed", err);
                // Kể cả lỗi cũng nên cho phép fetch để user thấy data hiện tại (nếu có)
                setReadyToFetch(true);
            }
        };

        // Ưu tiên CASE 1: Flag từ Navigation
        if (shouldInit && !readyToFetch) {
            performInit("Force Init via Route State");
            return;
        }

        // CASE 2: Auto-fix khi data trả về rỗng
        if (readyToFetch && !isUnitLoading && learningUnitData && !learningUnitData.lessonConfig) {
            performInit("Auto-fix missing config");
        }
    }, [shouldInit, readyToFetch, learningUnitData, isUnitLoading, learningUnitId]);
    // initializeConfig có thể bỏ ra khỏi deps nếu nó là hàm từ RTK Query (ổn định)

    const categories = learningUnitData?.exerciseCategories || [];

    const defaultCategory = categories.length > 0 ? categories[0] : null;
    const defaultCategoryId = defaultCategory?.id;

    const currentConfig = learningUnitData?.lessonConfig;
    const [createExercise] = useCreateExerciseMutation();
    const [updateExercise] = useUpdateExerciseMutation();
    const [deleteExercise] = useDeleteExerciseMutation();

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isAIModalOpen, setIsAIModalOpen] = useState(false);
    const [mode, setMode] = useState<"add" | "edit">("add");
    const [selectedType, setSelectedType] = useState<ExerciseType | null>(null);
    const [editingExercise, setEditingExercise] =
        useState<ExerciseDTO | null>(null);

    const [selectedCategoryId, setSelectedCategoryId] = useState<string | null>(null);

    const filteredExercises = useMemo(() => {
        if (!selectedCategoryId) return exercises;
        return exercises.filter(ex => ex.parentId === selectedCategoryId);
    }, [exercises, selectedCategoryId]);

    const [validationError, setValidationError] = useState<string | null>(null);

    const [builderValue, setBuilderValue] = useState<ExerciseBuilderValue>({
        options: [],
        correctAnswers: []
    });

    const [matchingValue, setMatchingValue] = useState<MatchingBuilderValue>({
        options: [],
        correctAnswers: []
    });

    const [reorderingValue, setReorderingValue] = useState<ExerciseBuilderValue>({
        options: [
            { header: "1", text: "" },
            { header: "2", text: "" }
        ],
        correctAnswers: ["1", "2"]
    });

    const [fillBlankValue, setFillBlankValue] = useState<FillInBlankValue>({
        correctAnswers: []
    });

    const searchInput = useRef(null);

    const handleSearch = (
        selectedKeys: React.Key[],
        confirm: (param?: FilterConfirmProps) => void
    ) => {
        confirm();
    };

    const handleReset = (clearFilters: () => void) => {
        clearFilters();
    };

    const getColumnSearchProps = (dataIndex: string) => ({
        filterDropdown: ({
            setSelectedKeys,
            selectedKeys,
            confirm,
            clearFilters,
        }: FilterDropdownProps) => (
            <div style={{ padding: 8 }}>
                <Input
                    ref={searchInput}
                    placeholder={`Search ${dataIndex}`}
                    value={selectedKeys?.[0]}
                    onChange={e =>
                        setSelectedKeys(e.target.value ? [e.target.value] : [])
                    }
                    onPressEnter={() =>
                        handleSearch(selectedKeys!, confirm)
                    }
                    style={{ marginBottom: 8, display: "block" }}
                />

                <Space>
                    <Button
                        type="primary"
                        onClick={() => handleSearch(selectedKeys!, confirm)}
                        size="small"
                    >
                        Search
                    </Button>

                    <Button
                        onClick={() => clearFilters && handleReset(clearFilters)}
                        size="small"
                    >
                        Reset
                    </Button>
                </Space>
            </div>
        ),

        filterIcon: (filtered: boolean) => (
            <SearchOutlined style={{ color: filtered ? "#1677ff" : undefined }} />
        ),

        onFilter: (value: any, record: any) =>
            record[dataIndex]
                ?.toString()
                .toLowerCase()
                .includes((value as string).toLowerCase()),
    });

    const [form] = Form.useForm();

    const openAddModal = () => {
        setMode("add");
        setEditingExercise(null);
        setSelectedType(null);
        form.resetFields();

        let defaultParentId = learningUnitId; 
        if (categories.length > 0) {
            defaultParentId = categories[0].id;
        }
        form.setFieldsValue({
            parentId: defaultParentId
        });

        setBuilderValue({
            options: [
                { header: "1", text: "" },
                { header: "2", text: "" }
            ],
            correctAnswers: []
        });

        setMatchingValue({
            options: [
                { header: "1", metadata: { side: "left" }, text: "" },
                { header: "1", metadata: { side: "right" }, text: "" }
            ],
            correctAnswers: []
        });

        setReorderingValue({
            options: [
                { header: "1", text: "" },
                { header: "2", text: "" }
            ],
            correctAnswers: ["1", "2"]
        });

        setFillBlankValue({
            correctAnswers: []
        });

        setIsModalOpen(true);
    };

    const openEditModal = (ex: ExerciseDTO) => {
        setMode("edit");
        setEditingExercise({
            ...ex,
            parentId: ex.parentId ?? learningUnitId
        });
        setSelectedType(ex.type);
        form.setFieldsValue({
            question: ex.question,
            parentId: ex.parentId
        });
        
        let parsedCorrect: any = [];
        try {
            parsedCorrect = JSON.parse(ex.correctAnswers || "[]");
        } catch {
            parsedCorrect = [];
        }

        if (ex.type === "MATCHING") {
            const dbOptions = ex.options || [];
            
            let parsedCorrectPairs = [];
            try {
                const parsed = JSON.parse(ex.correctAnswers || "[]");
                if (Array.isArray(parsed) && parsed.length > 0) {
                    const inner = JSON.parse(parsed[0]);
                    parsedCorrectPairs = inner.correctAnswers || [];
                }
            } catch {
                parsedCorrectPairs = [];
            }

            let mappedOptions = dbOptions.map(o => ({
                id: o.id,
                header: o.header || "",
                text: o.text || "",
                metadata: {
                    side: (o.metadata?.side === "left" || o.metadata?.side === "right") ? (o.metadata.side as "left" | "right") : "left"
                }
            }));
            const hasSideInfo = dbOptions.some(o => o.metadata?.side);
            if (!hasSideInfo && parsedCorrectPairs.length > 0) {
                const leftHeaders = parsedCorrectPairs.map((p: any) => String(p.leftHeader));
                
                mappedOptions = dbOptions.map(o => ({
                    id: o.id,
                    header: o.header || "",
                    text: o.text || "",
                    metadata: {
                        side: leftHeaders.includes(String(o.header)) ? "left" : "right"
                    }
                }));
            }

            setMatchingValue({
                options: mappedOptions,
                correctAnswers: parsedCorrectPairs
            });
        } else if (ex.type === "REORDERING") {
            const dbOptions = ex.options?.map(o => ({
                header: o.header || "",
                text: o.text
            })) || [];
            
            // Sort options by the order in correctAnswers
            const sortedOptions = parsedCorrect.map((header:any) => 
                dbOptions.find(o => o.header === header)
            ).filter(Boolean) as BuilderOption[];

            setReorderingValue({
                options: sortedOptions,
                correctAnswers: parsedCorrect
            });

        // THÊM ĐOẠN NÀY ĐỂ BIND DATA CHO FILL IN THE BLANK
        } else if (ex.type === "FILL_IN_THE_BLANK") {
            setFillBlankValue({
                correctAnswers: parsedCorrect
            });
            
        } else {
            setBuilderValue({
                options:
                    ex.options?.map(o => ({
                        header: o.header || "",
                        text: o.text
                    })) || [],
                correctAnswers: parsedCorrect
            });
        }

        setIsModalOpen(true);
    };

    const mapToPayload = () => {
        if (selectedType === "MATCHING") {
            const leftOptions = matchingValue.options.filter(o => o.metadata?.side === "left");
            const rightOptions = matchingValue.options.filter(o => o.metadata?.side === "right");

            const pairCount = Math.min(leftOptions.length, rightOptions.length);

            // === Tạo header random ===
            const randomHeaders = Array.from({ length: pairCount * 2 }, (_, i) =>
                String(i + 1)
            ).sort(() => Math.random() - 0.5);

const options: { header: string; text: string; metadata: { side: "left" | "right" } }[] = []; 
           const correctPairs: { leftHeader: string; rightHeader: string }[] = [];

            let headerIndex = 0;

            for (let i = 0; i < pairCount; i++) {
                const leftHeader = randomHeaders[headerIndex++];
                const rightHeader = randomHeaders[headerIndex++];

                options.push({
                    header: leftHeader,
                    text: leftOptions[i].text,
                    metadata: { side: "left" }
                });

                options.push({
                    header: rightHeader,
                    text: rightOptions[i].text,
                    metadata: { side: "right" }
                });

                correctPairs.push({
                    leftHeader,
                    rightHeader
                });
            }

            return {
                options,
                correctAnswers: JSON.stringify(correctPairs)
            };
        }
        if (selectedType === "REORDERING") {
            const originalOptions = reorderingValue.options;

            const originalHeaders = originalOptions.map((_, index) =>
                String(index + 1)
            );

            const shuffledHeaders = [...originalHeaders].sort(() => Math.random() - 0.5);

            const shuffledOptions = originalOptions.map((opt, idx) => ({
                header: shuffledHeaders[idx],
                side: "",
                text: opt.text
            }));

            return {
                options: shuffledOptions,
                correctAnswers: JSON.stringify(shuffledHeaders)
            };
        }

        if (selectedType === "FILL_IN_THE_BLANK") {
            return {
                correctAnswers: JSON.stringify(fillBlankValue.correctAnswers)
            };
        }

        const optionsMapped =
            selectedType === "SHORT_ANSWER"
                ? []
                : builderValue.options.map((o, idx) => ({
                    header: String(idx + 1),
                    text: o.text
                }));
        return {
            options: optionsMapped,
            correctAnswers: JSON.stringify(builderValue.correctAnswers)
        };
    };

    const handleSave = async () => {
        setValidationError(null);

        const values = await form.validateFields();
        if (!selectedType) {
            message.error("Select exercise type.");
            return;
        }

        if (selectedType === "MULTIPLE_CHOICE") {
            if (builderValue.correctAnswers.length === 0) {
                setValidationError("Please select one correct answer.");
                message.error("Please select one correct answer.");
                return;
            }

            const hasEmptyOption = builderValue.options.some(opt => !opt.text.trim());
            if (hasEmptyOption) {
                setValidationError("Please fill in all options.");
                message.error("Option text cannot be empty.");
                return;
            }
        }

        const payload = mapToPayload();

        if (mode === "add") {
            await createExercise({
                parentId: values.parentId,
                question: values.question,
                type: selectedType,
                ...payload
            } as any).unwrap();
        } else if (editingExercise?.id) {

            await updateExercise({
                id: editingExercise.id,
                data: {
                    parentId: values.parentId,
                    difficulty: editingExercise.difficulty,
                    question: values.question,
                    type: selectedType,
                    ...payload
                }
            } as any).unwrap();
        }

        message.success("Saved");
        refetch();
        setIsModalOpen(false);
    };

    const handleDelete = async (id: string) => {
        await deleteExercise(id).unwrap();
        message.success("Deleted");
        refetch();
    };

    const columns = [
        {
            title: "#",
            width: 60,
            render: (_: any, __: any, idx: number) => idx + 1
        },
        {
            title: "Question",
            dataIndex: "question",
            sorter: (a: any, b: any) => a.question.localeCompare(b.question),
            ...getColumnSearchProps("question")
        },
        {
            title: "Type",
            dataIndex: "type",
            filters: exerciseTypes.map(t => ({ text: t, value: t })),
            onFilter: (value: any, record: any) => record.type === value,
            render: (t: ExerciseType) => <Tag>{t}</Tag>
        },
        {
            title: "Created At",
            dataIndex: "createdAt",
            sorter: (a: any, b: any) =>
                new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime(),
            render: (v: any) => new Date(v).toLocaleString()
        },
        {
            title: "Actions",
            width: 120,
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
    const questionText = Form.useWatch("question", form);

    const renderBuilder = () => {
        if (!selectedType) {
            return (
                <p className="text-gray-500 text-sm">Select exercise type.</p>
            );
        }

        const props = {
            value: builderValue,
            onChange: setBuilderValue,
            error: validationError
        };

        switch (selectedType) {
            case "MULTIPLE_CHOICE":
                return <MultipleChoiceBuilder {...props} />;
            case "SELECT_MULTIPLE":
                return <SelectMultipleBuilder {...props} />;
            case "SHORT_ANSWER":
                return <ShortAnswerBuilder {...props} />;
            case "TRUE_FALSE":
                return <TrueFalseBuilder {...props} />;
            case "MATCHING":
                return (
                    <MatchingBuilder
                        value={matchingValue}
                        onChange={setMatchingValue}
                        error={validationError}
                    />
                );
            case "REORDERING":
                return (
                    <ReorderingBuilder
                        value={reorderingValue}
                        onChange={setReorderingValue}
                        error={validationError}
                    />
                );
            case "FILL_IN_THE_BLANK":
                return (
                    <FillInTheBlankBuilder
                    question={questionText}
                        value={fillBlankValue}
                        onChange={setFillBlankValue}
                        error={validationError}
                    />
                );
            default:
                return (
                    <p className="text-yellow-600">
                        This type is not implemented.
                    </p>
                );
        }
    };
    return (
        <TeacherPage
            title={`${lessonName ?? ""}`}
            breadcrumb={[
                { label: "Home", path: "/teacher/dashboard" },
                { label: "My Courses", path: "/teacher/courses" },
                courseName && {
                    label: courseName,
                    path: `/teacher/course/${courseId}/lessons`
                }
            ].filter(Boolean) as any}
        >
            <div className="grid grid-cols grid-cols-8 h-[30vh] gap-2 mb-4">
                <div className="col-span-4 bg-white rounded-lg shadow-xl p-4 flex flex-col h-full overflow-hidden">
                    <div className="flex gap-2 mb-2 shrink-0">
                        <InboxStackIcon width={20} className="text-text-color" />
                        <p className="text-text-color font-semibold">Exercise Categories</p>
                    </div>

                    <div className="overflow-y-auto flex-1 pr-2 flex flex-col gap-1">

                        {/* Mục All Exercises */}
                        <div
                            onClick={() => setSelectedCategoryId(null)}
                            className={`shrink-0 cursor-pointer p-3 rounded-md flex justify-between items-center transition-all border
            ${selectedCategoryId === null
                                    ? "bg-blue-50 border-blue-200 shadow-sm"
                                    : "bg-gray-50 border-transparent hover:bg-gray-100"
                                }`}
                        >
                            <div className="flex items-center gap-3">
                                <AppstoreOutlined
                                    className={selectedCategoryId === null ? "text-blue-600" : "text-gray-500"} />
                                <span
                                    className={`font-medium ${selectedCategoryId === null ? "text-blue-700" : "text-gray-600"}`}>
                                    All Exercises
                                </span>
                            </div>
                            <Badge count={exercises.length} showZero
                                color={selectedCategoryId === null ? "#1677ff" : "#d9d9d9"} />
                        </div>

                        {/* Danh sách các Categories */}
                        {categories.map((cat: any) => {
                            const count = exercises.filter((e: any) => e.parentId === cat.id).length;
                            const isActive = selectedCategoryId === cat.id;

                            return (
                                <div
                                    key={cat.id}
                                    onClick={() => setSelectedCategoryId(cat.id)}
                                    className={`shrink-0 cursor-pointer p-3 rounded-md flex justify-between items-center transition-all border
                    ${isActive
                                            ? "bg-blue-50 border-blue-200 shadow-sm"
                                            : "bg-white border-gray-100 hover:bg-gray-50"
                                        }`}
                                >
                                    <div className="flex items-center gap-3">
                                        <FolderOpenOutlined className={isActive ? "text-blue-600" : "text-gray-400"} />
                                        <span className={`font-medium ${isActive ? "text-blue-700" : "text-gray-700"}`}>
                                            {cat.name}
                                        </span>
                                    </div>
                                    <Badge count={count} showZero color={isActive ? "#1677ff" : "#d9d9d9"} />
                                </div>
                            );
                        })}

                        {/* Trạng thái trống */}
                        {categories.length === 0 && (
                            <p className="text-gray-400 text-center text-sm italic mt-4">No categories found.</p>
                        )}
                    </div>
                </div>
                {learningUnitId && currentConfig && (
                    <LessonConfigPanel
                        lessonId={learningUnitId}
                        data={currentConfig}
                    />
                )}

                <div className="col-span-2 bg-white rounded-lg shadow-xl p-4">
                    <div className="flex gap-2 mb-2">
                        <RectangleGroupIcon width={20} className="text-text-color" />
                        <p className="text-text-color font-semibold">Actions</p>
                    </div>

                    <div className="flex flex-col gap-1">
                        <MyButton
                            text="Add Exercise"
                            icon={<PlusOutlined />}
                            onClick={openAddModal}
                            className="w-full"
                        />
                        <MyButton
                            text="Add By Markdown"
                            icon={<FileAddOutlined />}
                            onClick={() => alert("Coming soon")}
                            className="w-full bg-cyan-700 border-cyan-700 hover:bg-white hover:text-cyan-700"
                        />


                        <MyButton
                            text="Generate By AI"
                            icon={<SparklesIcon className="w-4 h-4" />}
                            // Thêm check: Chỉ mở modal nếu đã có category default
                            onClick={() => {
                                if (!defaultCategoryId) {
                                    message.error("Please initialize configuration first to create a Default Category.");
                                    return;
                                }
                                setIsAIModalOpen(true);
                            }}
                            className="w-full bg-purple-600 border-purple-600 hover:text-purple-600"
                        />


                    </div>
                </div>
            </div>

            <Table
                rowKey="id"
                bordered
                loading={isLoading}
                dataSource={filteredExercises}
                columns={columns}
                scroll={{
                    y: 'calc(100vh - 460px)',
                    x: 800
                }}
                pagination={false}
            />


            <AIGenerateModal
                isOpen={isAIModalOpen}
                onClose={() => setIsAIModalOpen(false)}
                learningUnitId={learningUnitId!}
                defaultCategoryId={defaultCategoryId!}
                categories={categories}
            />

            <Modal
                title={mode === "add" ? "Add Exercise" : "Edit Exercise"}
                open={isModalOpen}
                width={900}
                style={{ top: 20 }}
                styles={{
                    body: {
                        maxHeight: 'calc(100vh - 180px)',
                        overflowY: 'auto',
                        paddingRight: '8px'
                    }
                }}
                onCancel={() => setIsModalOpen(false)}
                footer={[
                    <Button key="save" type="primary" onClick={handleSave}>
                        Save
                    </Button>,
                    <Button
                        key="cancel"
                        danger
                        onClick={() => setIsModalOpen(false)}
                    >
                        Cancel
                    </Button>
                ]}
            >

                <Form form={form} layout="vertical">
                    <Form.Item label="Category" name="parentId" initialValue={learningUnitId}>
                        <Select>
                            {categories.map((cat: any) => (
                                <Select.Option key={cat.id} value={cat.id}>
                                    {cat.name}
                                </Select.Option>
                            ))}
                        </Select>
                    </Form.Item>
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
                        <h3 className="font-semibold mb-2">
                            Exercise Content
                        </h3>
                        {renderBuilder()}
                    </div>
                </Form>
            </Modal>
        </TeacherPage>
    );
}