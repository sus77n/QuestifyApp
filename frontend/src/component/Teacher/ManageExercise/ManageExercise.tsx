import React, {useMemo, useRef, useState, useEffect} from "react"; // Added useEffect
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

import {useLocation, useParams} from "react-router-dom";
import {ExerciseDTO, ExerciseType} from "../../../model/ExerciseDTO";

import {
    useCreateExerciseMutation,
    useDeleteExerciseMutation,
    useGetExercisesQuery,
    useUpdateExerciseMutation
} from "../../../API/service/exercise.service";

import MyButton from "../../material/material";
import TeacherPage from "../TeacherPage";

import MultipleChoiceBuilder, {
    ExerciseBuilderValue
} from "./exerciseBuilders/MultipleChoiceBuilder";

import SelectMultipleBuilder from "./exerciseBuilders/SelectMultipleBuilder";
import ShortAnswerBuilder from "./exerciseBuilders/ShortAnswerBuilder";
import TrueFalseBuilder from "./exerciseBuilders/TrueFalseBuilder";
import MatchingBuilder, {
    MatchingBuilderValue,
    MatchingOption
} from "./exerciseBuilders/MatchingBuilder";
import ReorderingBuilder from "./exerciseBuilders/ReorderingBuilder";
import FillInTheBlankBuilder, {FillInBlankValue} from "./exerciseBuilders/FillInTheBlankBuilder";
import {FilterConfirmProps, FilterDropdownProps} from "antd/es/table/interface";
import {InboxStackIcon, RectangleGroupIcon, SparklesIcon} from "@heroicons/react/24/solid";
import LessonConfigPanel from "./LessonConfigPanel";
import {
    useGetLearningUnitDetailsByIdQuery,
    useInitializeLessonConfigAndCateMutation
} from "../../../API/service/learningUnit.service";
import AIGenerateModal from "./AIGenerateModal";

const {TextArea} = Input;

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
    const {learningUnitId} = useParams();
    const location = useLocation() as any;

    const {courseName, lessonName, courseId, shouldInit} = location.state || {};
    const [readyToFetch, setReadyToFetch] = useState(!shouldInit);

    const {
        data: learningUnitData,
        isLoading: isUnitLoading
    } = useGetLearningUnitDetailsByIdQuery(
        {id: learningUnitId!},
        {
            skip: !readyToFetch
        }
    );

    const {data: exercises = [], isLoading, refetch} =
        useGetExercisesQuery({lessonId: learningUnitId!});

    const [initializeConfig, {isLoading: isInitializing}] = useInitializeLessonConfigAndCateMutation();
    const initRef = useRef(false);

    useEffect(() => {
        if (shouldInit && !readyToFetch && !initRef.current) {

            console.log("CASE 1: Force Init detected via shouldInit flag");
            initRef.current = true;

            initializeConfig({id: learningUnitId!})
                .unwrap()
                .then(() => {
                    message.success("Initialized configuration successfully.");
                    // QUAN TRỌNG: Init xong thì mở khoá cho API Get chạy
                    setReadyToFetch(true);
                })
                .catch((err) => {
                    console.error("Init failed", err);
                    setReadyToFetch(true);
                });

            return;
        }

        if (readyToFetch && learningUnitData && !isUnitLoading && !learningUnitData.lessonConfig && !initRef.current) {

            console.log("CASE 2: Auto-fix missing config from Data");
            initRef.current = true;

            if (!isInitializing) {
                initializeConfig({id: learningUnitId!})
                    .unwrap()
                    .then(() => {
                        message.success("Lesson configuration initialized automatically.");
                    })
                    .catch((err) => {
                        console.error("Failed to init config", err);
                    });
            }
        }
    }, [shouldInit, readyToFetch, learningUnitData, isUnitLoading, learningUnitId, initializeConfig, isInitializing]);

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
            {header: "1", text: ""},
            {header: "2", text: ""}
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
            <div style={{padding: 8}}>
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
                    style={{marginBottom: 8, display: "block"}}
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
            <SearchOutlined style={{color: filtered ? "#1677ff" : undefined}}/>
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

        let defaultParentId = learningUnitId; // Mặc định là Root (nếu ko có cate nào)
        if (categories.length > 0) {
            defaultParentId = categories[0].id;
        }
        form.setFieldsValue({
            parentId: defaultParentId
        });

        setBuilderValue({
            options: [
                {header: "1", text: ""},
                {header: "2", text: ""}
            ],
            correctAnswers: []
        });

        // default: matching
        setMatchingValue({
            options: [
                {header: "1", side: "left", text: ""},
                {header: "1", side: "right", text: ""}
            ],
            correctAnswers: []
        });

        // default: reordering
        setReorderingValue({
            options: [
                {header: "1", text: ""},
                {header: "2", text: ""}
            ],
            correctAnswers: ["1", "2"]
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
        form.setFieldsValue({question: ex.question});

        let parsedCorrect: any = [];
        try {
            parsedCorrect = JSON.parse(ex.correctAnswers || "[]");
        } catch {
            parsedCorrect = [];
        }

        if (ex.type === "MATCHING") {
            const leftOptions = (ex.options || []).filter(o => o.side === "left");
            const rightOptions = (ex.options || []).filter(o => o.side === "right");

            const sortedLeft = [...leftOptions].sort((a, b) => Number(a.header) - Number(b.header));
            const sortedRight = [...rightOptions].sort((a, b) => Number(a.header) - Number(b.header));

            const pairCount = Math.min(sortedLeft.length, sortedRight.length);

            const builderOptions: MatchingOption[] = [];
            const builderCorrect: { leftHeader: string; rightHeader: string }[] = [];

            for (let i = 0; i < pairCount; i++) {
                const pairHeader = String(i + 1);

                builderOptions.push({
                    header: pairHeader,
                    side: "left",
                    text: sortedLeft[i].text
                });

                builderOptions.push({
                    header: pairHeader,
                    side: "right",
                    text: sortedRight[i].text
                });

                builderCorrect.push({
                    leftHeader: pairHeader,
                    rightHeader: pairHeader
                });
            }

            setMatchingValue({
                options: builderOptions,
                correctAnswers: builderCorrect
            });

        } else if (ex.type === "REORDERING") {
            setReorderingValue({
                options:
                    ex.options?.map(o => ({
                        header: o.header || "",
                        text: o.text
                    })) || [],
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
            const leftOptions = matchingValue.options.filter(o => o.side === "left");
            const rightOptions = matchingValue.options.filter(o => o.side === "right");

            const pairCount = Math.min(leftOptions.length, rightOptions.length);

            // === Tạo header random ===
            const randomHeaders = Array.from({length: pairCount * 2}, (_, i) =>
                String(i + 1)
            ).sort(() => Math.random() - 0.5);

            const options: { header: string; side: string; text: string }[] = [];
            const correctPairs: { leftHeader: string; rightHeader: string }[] = [];

            let headerIndex = 0;

            for (let i = 0; i < pairCount; i++) {
                const leftHeader = randomHeaders[headerIndex++];
                const rightHeader = randomHeaders[headerIndex++];

                options.push({
                    header: leftHeader,
                    side: "left",
                    text: leftOptions[i].text
                });

                options.push({
                    header: rightHeader,
                    side: "right",
                    text: rightOptions[i].text
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
            filters: exerciseTypes.map(t => ({text: t, value: t})),
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
                    <MyButton icon={<EditOutlined/>} onClick={() => openEditModal(r)}/>
                    <Popconfirm title="Delete?" onConfirm={() => handleDelete(r.id!)}>
                        <Button danger icon={<DeleteOutlined/>}/>
                    </Popconfirm>
                </Space>
            )
        }
    ];

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
                    />
                );
            case "REORDERING":
                return (
                    <ReorderingBuilder
                        value={reorderingValue}
                        onChange={setReorderingValue}
                    />
                );
            case "FILL_IN_THE_BLANK":
                return (
                    <FillInTheBlankBuilder
                        value={fillBlankValue}
                        onChange={setFillBlankValue}
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
                {label: "Home", path: "/teacher/dashboard"},
                {label: "My Courses", path: "/teacher/courses"},
                courseName && {
                    label: courseName,
                    path: `/teacher/course/${courseId}/lessons`
                }
            ].filter(Boolean) as any}
        >
            <div className="grid grid-cols grid-cols-8 h-[30%] gap-2 mb-4">
                <div className="col-span-4 bg-white rounded-lg shadow-xl p-4">
                    <div className="flex gap-2 mb-2">
                        <InboxStackIcon width={20} className="text-text-color"/>
                        <p className="text-text-color font-semibold">Exercise Categories</p>
                    </div>
                    <div
                        onClick={() => setSelectedCategoryId(null)}
                        className={`overflow-y-auto
                                        cursor-pointer p-3 rounded-md flex justify-between items-center transition-all border
                                        ${selectedCategoryId === null
                            ? "bg-blue-50 border-blue-200 shadow-sm"
                            : "bg-gray-50 border-transparent hover:bg-gray-100"
                        }
                                    `}
                    >
                        <div className="flex items-center gap-3">
                            <AppstoreOutlined
                                className={selectedCategoryId === null ? "text-blue-600" : "text-gray-500"}/>
                            <span
                                className={`font-medium ${selectedCategoryId === null ? "text-blue-700" : "text-gray-600"}`}>
                                    All Exercises
                                </span>
                        </div>
                        <Badge count={exercises.length} showZero
                               color={selectedCategoryId === null ? "#1677ff" : "#d9d9d9"}/>
                    </div>

                    {categories.map((cat: any) => {
                        const count = exercises.filter((e: any) => e.parentId === cat.id).length;
                        const isActive = selectedCategoryId === cat.id;

                        return (
                            <div
                                key={cat.id}
                                onClick={() => setSelectedCategoryId(cat.id)}
                                className={` mt-1 mb-1
                                                cursor-pointer p-3 rounded-md flex justify-between items-center transition-all border
                                                ${isActive
                                    ? "bg-blue-50 border-blue-200 shadow-sm"
                                    : "bg-white border-gray-100 hover:bg-gray-50"
                                }
                                            `}
                            >
                                <div className="flex items-center gap-3">
                                    <FolderOpenOutlined className={isActive ? "text-blue-600" : "text-gray-400"}/>
                                    <span className={`font-medium ${isActive ? "text-blue-700" : "text-gray-700"}`}>
                                            {cat.name}
                                        </span>
                                </div>
                                <Badge count={count} showZero color={isActive ? "#1677ff" : "#d9d9d9"}/>
                            </div>
                        );
                    })}

                    {categories.length === 0 && (
                        <p className="text-gray-400 text-center text-sm italic mt-4">No categories found.</p>
                    )}
                </div>

                {learningUnitId && currentConfig && (
                    <LessonConfigPanel
                        lessonId={learningUnitId}
                        data={currentConfig}
                    />
                )}

                <div className="col-span-2 bg-white rounded-lg shadow-xl p-4">
                    <div className="flex gap-2 mb-2">
                        <RectangleGroupIcon width={20} className="text-text-color"/>
                        <p className="text-text-color font-semibold">Actions</p>
                    </div>

                    <div className="flex flex-col gap-1">
                        <MyButton
                            text="Add Exercise"
                            icon={<PlusOutlined/>}
                            onClick={openAddModal}
                            className="w-full"
                        />
                        <MyButton
                            text="Add By Markdown"
                            icon={<FileAddOutlined/>}
                            onClick={() => alert("Coming soon")}
                            className="w-full bg-cyan-700 border-cyan-700 hover:bg-white hover:text-cyan-700"
                        />


                        <MyButton
                            text="Generate By AI"
                            icon={<SparklesIcon className="w-4 h-4"/>}
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
                pagination={{
                    pageSize: 10
                }}
            />

            <AIGenerateModal
                isOpen={isAIModalOpen}
                onClose={() => setIsAIModalOpen(false)}
                learningUnitId={learningUnitId!}
                defaultCategoryId={defaultCategoryId!}
            />

            <Modal
                title={mode === "add" ? "Add Exercise" : "Edit Exercise"}
                open={isModalOpen}
                width={900}
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
                        rules={[{required: true}]}
                    >
                        <TextArea rows={3}/>
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