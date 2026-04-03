import React, { useEffect, useState, useRef } from "react";
import {
    Modal,
    Steps,
    Button,
    List,
    Input,
    message,
    Spin,
    Card,
    Checkbox,
    Tag,
    Space,
    Popconfirm,
    Badge
} from "antd";
import {
    RobotOutlined,
    UnorderedListOutlined,
    SaveOutlined,
    EditOutlined,
    DeleteOutlined,
    CheckOutlined
} from "@ant-design/icons";

import {
    useGenerateCategoryContentMutation,
    useGenerateExercisesMutation
} from "../../../API/service/learningUnit.service";
import { useAddListExerciseMutation } from "../../../API/service/exercise.service";
import { ExerciseDTO } from "../../../model/ExerciseDTO";
import { LearningUnitDTO } from "../../../model/LearningUnitDTO";

interface Props {
    isOpen: boolean;
    onClose: () => void;
    learningUnitId: string;
    defaultCategoryId: string;
    categories: any[];
}

const AIGenerateModal: React.FC<Props> = ({
    isOpen,
    onClose,
    learningUnitId,
    defaultCategoryId,
    categories
}) => {
    // --- STATE ---
    const [currentStep, setCurrentStep] = useState(0);
    const [suggestedCategories, setSuggestedCategories] = useState<LearningUnitDTO[]>([]);
    const [selectedCategoryIds, setSelectedCategoryIds] = useState<string[]>([]);
    const [generatedExercises, setGeneratedExercises] = useState<ExerciseDTO[]>([]);
    const [editModes, setEditModes] = useState<{ [key: number]: boolean }>({});

    const hasAutoCalledRef = useRef(false);

    // --- API ---
    const [generateCategories, { isLoading: isLoadingCats }] = useGenerateCategoryContentMutation();
    const [generateExercises, { isLoading: isLoadingExs }] = useGenerateExercisesMutation();
    const [saveBulkExercises, { isLoading: isSaving }] = useAddListExerciseMutation();

    // --- EFFECTS ---

    useEffect(() => {
        if (!isOpen) {
            setCurrentStep(0);
            setGeneratedExercises([]);
            setEditModes({});
            setSuggestedCategories([]);
            setSelectedCategoryIds([]);
            hasAutoCalledRef.current = false;
        }
    }, [isOpen]);

    // Auto-detect & skip step 0 if categories exist
    useEffect(() => {
        if (isOpen && !hasAutoCalledRef.current) {
            const isDefaultOnly = categories.length === 1 && categories[0].name === "Default Exercise Category";
            const shouldSkipCategorySelection = categories.length > 0 && !isDefaultOnly;

            hasAutoCalledRef.current = true;

            if (shouldSkipCategorySelection) {
                console.log("Auto-skip: Generating for existing categories...");
                setCurrentStep(1);
                handleGenerate(categories);
            } else {
                handleFetchSuggestedCategories();
            }
        }
        // eslint-disable-next-line
    }, [isOpen, categories]);

    // --- HANDLERS ---

    const handleFetchSuggestedCategories = async () => {
        if (!defaultCategoryId) return;
        try {
            const data = await generateCategories({ originalExCateId: defaultCategoryId }).unwrap();
            setSuggestedCategories(data);
        } catch (err) {
            console.error(err);
            message.error("Failed to fetch AI categories.");
        }
    };

    // --- CORE LOGIC: MAP RESPONSE DATA ---
    const handleGenerate = async (targetCategories: any[]) => {
        if (!targetCategories.length) {
            message.warning("No categories provided.");
            return;
        }

        try {
            const rawResponse = await generateExercises({
                categories: targetCategories,
                lessonId: learningUnitId
            }).unwrap();

            console.log("Raw Response:", rawResponse);

            const dataToProcess = rawResponse;
            let allMappedExercises: ExerciseDTO[] = [];

            dataToProcess.forEach((catBlock: any) => {
                const categoryId = catBlock.categoryId;
                const categoryName = catBlock.categoryName;
                const exercises = catBlock.exercises || [];

                exercises.forEach((ex: any) => {
                    const options = (ex.predefinedAnswers || []).map((opt: any) => ({
                        header: opt.header,
                        text: opt.text,
                        side:
                            opt.metadata === "left"
                                ? "left"
                                : opt.metadata === "right"
                                    ? "right"
                                    : null
                    }));

                    const correctAnswers = JSON.stringify(
                        ex.correctAnswerJson?.correctAnswers || []
                    );

                    allMappedExercises.push({
                        question: ex.question,
                        type: ex.type,
                        difficulty: ex.difficulty,
                        options,
                        correctAnswers,
                        parentId: categoryId,
                        parentName: categoryName
                    });
                });
            });

            setGeneratedExercises(allMappedExercises);
            setCurrentStep(1);
        } catch (err) {
            console.error("Generate Error", err);
            message.error("Failed to generate exercises.");
            if (currentStep === 1) setCurrentStep(0);
        }
    };

    const handleManualNext = () => {
        if (selectedCategoryIds.length === 0) {
            message.warning("Select at least one category.");
            return;
        }
        const selectedCats = suggestedCategories.filter(cat =>
            selectedCategoryIds.includes(cat.id || (cat as any).code)
        );
        handleGenerate(selectedCats);
    };

    const buildExportedCategories = (exercises: ExerciseDTO[]) => {
        const map = new Map<string, any>();

        exercises.forEach(ex => {
            const categoryId = ex.parentId;

            if (!map.has(categoryId as string)) {
                if (categoryId != null) {
                    map.set(categoryId, {
                        categoryId,
                        categoryName: ex.parentName,
                        exercises: []
                    });
                }
            }

            if (categoryId != null) {
                map.get(categoryId).exercises.push({
                    id: ex.id ?? null,
                    question: ex.question,
                    type: ex.type,
                    difficulty: ex.difficulty ?? null,
                    predefinedAnswers: (ex.options || []).map(o => ({
                        id: null,
                        text: o.text,
                        header: o.header,
                        metadata: o.side
                            ? { side: o.side }
                            : null
                    })),
                    correctAnswerJson: {
                        correctAnswers: JSON.parse(ex.correctAnswers || "[]")
                    }
                });
            }
        });

        return Array.from(map.values());
    };


    const handleSaveAll = async () => {
        if (!generatedExercises.length) return;

        try {
            const payload = buildExportedCategories(generatedExercises);

            await saveBulkExercises(payload).unwrap();

            message.success(`Saved ${generatedExercises.length} exercises!`);
            onClose();
            window.location.reload();
        } catch (err) {
            console.error(err);
            message.error("Failed to save.");
        }
    };


    // --- EDITING HELPERS ---
    const updateExField = (idx: number, field: keyof ExerciseDTO, val: any) => {
        const list = [...generatedExercises];
        list[idx] = { ...list[idx], [field]: val };
        setGeneratedExercises(list);
    };

    const toggleEdit = (idx: number) => setEditModes(p => ({ ...p, [idx]: !p[idx] }));
    const removeEx = (idx: number) => {
        const list = [...generatedExercises];
        list.splice(idx, 1);
        setGeneratedExercises(list);
    };

    // --- RENDER CARD ---
    const renderCard = (ex: ExerciseDTO, idx: number) => {
        const isEditing = editModes[idx];
        let parsedCorrect: any[] = [];
        try { parsedCorrect = JSON.parse(ex.correctAnswers || "[]"); } catch { }

        // Helper check correct cho UI preview (hỗ trợ cả string "1" lẫn object matching)
        const isCorrect = (header: string) => {
            if (!Array.isArray(parsedCorrect)) return false;
            // Nếu là mảng string (Multiple choice)
            if (typeof parsedCorrect[0] === 'string') return parsedCorrect.includes(header);
            // Nếu là mảng object (Matching)
            return false; // Matching hiển thị phức tạp hơn, ở đây demo simple
        };

        return (
            <Card
                key={idx}
                size="small"
                className={`mb-4 border shadow-sm ${isEditing ? 'border-blue-400 bg-blue-50' : 'border-gray-200'}`}
                title={
                    <div className="flex items-center gap-2">
                        <Tag>#{idx + 1}</Tag>
                        <Tag>{ex.type}</Tag>
                        {ex.difficulty && <Tag color="orange">Lvl {ex.difficulty}</Tag>}
                    </div>
                }
                extra={
                    <Space>
                        <Button type={isEditing ? "primary" : "default"} size="small" icon={isEditing ? <CheckOutlined /> : <EditOutlined />} onClick={() => toggleEdit(idx)} />
                        <Popconfirm title="Delete?" onConfirm={() => removeEx(idx)}>
                            <Button danger size="small" icon={<DeleteOutlined />} />
                        </Popconfirm>
                    </Space>
                }
            >
                <div className="mb-3">
                    {isEditing ? (
                        <Input.TextArea
                            value={ex.question}
                            onChange={e => updateExField(idx, 'question', e.target.value)}
                            autoSize
                        />
                    ) : (
                        <p className="font-medium text-base whitespace-pre-wrap">{ex.question}</p>
                    )}
                </div>

                {/* Render Options Preview */}
                <div className="flex flex-col gap-2">
                    {ex.type === 'MATCHING' ? (
                        <div className="grid grid-cols-2 gap-4">
                            <div className="bg-gray-50 p-2 rounded">
                                <span className="text-xs font-bold text-gray-400">LEFT</span>
                                {ex.options?.filter(o => o.side === 'left').map(o => (
                                    <div key={o.header} className="border-b py-1">{o.header}. {o.text}</div>
                                ))}
                            </div>
                            <div className="bg-gray-50 p-2 rounded">
                                <span className="text-xs font-bold text-gray-400">RIGHT</span>
                                {ex.options?.filter(o => o.side === 'right').map(o => (
                                    <div key={o.header} className="border-b py-1">{o.header}. {o.text}</div>
                                ))}
                            </div>
                        </div>
                    ) : (
                        // Standard types (Choice, etc)
                        ex.options?.map((opt, oIdx) => (
                            <div key={oIdx} className={`flex items-center gap-2 p-2 rounded ${!isEditing && isCorrect(opt.header as string) ? 'bg-green-50 border border-green-200' : 'bg-white border border-transparent'}`}>
                                <Badge count={opt.header} style={{ backgroundColor: '#d9d9d9' }} />
                                {isEditing ? (
                                    <Input
                                        value={opt.text}
                                        onChange={e => {
                                            const newOpts = [...(ex.options || [])];
                                            newOpts[oIdx] = { ...newOpts[oIdx], text: e.target.value };
                                            updateExField(idx, 'options', newOpts);
                                        }}
                                    />
                                ) : (
                                    <span className={isCorrect(opt.header as string) ? 'text-green-700 font-medium' : ''}>{opt.text}</span>
                                )}
                            </div>
                        ))
                    )}
                </div>
            </Card>
        );
    };

    return (
        <Modal
            title={<div className="flex items-center gap-2 text-lg text-white"><RobotOutlined /> AI Exercise Generator</div>}
            open={isOpen}
            onCancel={onClose}
            width={900}
            footer={null}
            maskClosable={false}
            destroyOnClose
        >
            <Steps
                current={currentStep}
                items={[{ title: 'Select Categories', icon: <UnorderedListOutlined /> }, { title: 'Review & Edit', icon: <EditOutlined /> }]}
                className="mb-6 px-4"
            />

            {currentStep === 0 && (
                <div className="flex flex-col h-[500px]">
                    <div className="bg-blue-50 p-4 rounded mb-4 text-blue-800 border border-blue-100">
                        AI analyzes content and suggests categories.
                    </div>
                    {isLoadingCats ? (
                        <div className="flex-1 flex flex-col items-center justify-center"><Spin size="large" /><p className="mt-2 text-gray-400">Analyzing...</p></div>
                    ) : (
                        <div className="flex-1 overflow-y-auto pr-2">
                            <List
                                grid={{ gutter: 16, column: 2 }}
                                dataSource={suggestedCategories}
                                renderItem={(item) => {
                                    const id = item.id || (item as any).code;
                                    const isSel = selectedCategoryIds.includes(id);
                                    return (
                                        <List.Item>
                                            <Card
                                                hoverable
                                                className={`border-2 ${isSel ? 'border-purple-500 bg-purple-50' : 'border-transparent'}`}
                                                onClick={() => setSelectedCategoryIds(p => isSel ? p.filter(x => x !== id) : [...p, id])}
                                            >
                                                <div className="flex justify-between"><h4 className="font-bold">{item.name}</h4><Checkbox checked={isSel} /></div>
                                                <p className="text-gray-500 text-xs mt-1 line-clamp-2">{item.description}</p>
                                            </Card>
                                        </List.Item>
                                    );
                                }}
                            />
                        </div>
                    )}
                    <div className="flex justify-end pt-4 border-t mt-2">
                        <Button onClick={onClose} className="mr-2">Cancel</Button>
                        <Button type="primary" onClick={handleManualNext} loading={isLoadingExs} icon={<RobotOutlined />}>Generate</Button>
                    </div>
                </div>
            )}

            {currentStep === 1 && (
                <div className="flex flex-col h-[600px]">
                    {isLoadingExs ? (
                        <div className="flex-1 flex flex-col items-center justify-center"><Spin size="large" /><p className="mt-4 text-purple-600 font-medium">Generating exercises...</p></div>
                    ) : (
                        <>
                            <div className="flex justify-between items-center mb-4 px-2">
                                <span className="text-gray-600 font-medium">Found {generatedExercises.length} exercises.</span>
                                <Space>
                                    <Button onClick={() => setCurrentStep(0)}>Back</Button>
                                    <Button type="primary" icon={<SaveOutlined />} onClick={handleSaveAll} loading={isSaving} className="bg-green-600 hover:bg-green-500">Save All</Button>
                                </Space>
                            </div>
                            <div className="flex-1 overflow-y-auto bg-gray-100 p-4 rounded border border-gray-200">
                                {generatedExercises.map((ex, idx) => renderCard(ex, idx))}
                                {generatedExercises.length === 0 && <div className="text-center text-gray-400 mt-10">No exercises found.</div>}
                            </div>
                        </>
                    )}
                </div>
            )}
        </Modal>
    );
};

export default AIGenerateModal;