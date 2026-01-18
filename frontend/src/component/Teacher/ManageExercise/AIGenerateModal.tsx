import React, { useEffect, useState } from "react";
import { Modal, Steps, Checkbox, Button, List, Input, message, Spin, Card, Tag, Row, Col } from "antd";
import { RobotOutlined, UnorderedListOutlined, SaveOutlined, EyeOutlined, EditOutlined, DeleteOutlined } from "@ant-design/icons";
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';

// Import services & models của bạn
import { useGenerateCategoryContentMutation, useGenerateExercisesMutation } from "../../../API/service/learningUnit.service";
import { useAddListExerciseMutation } from "../../../API/service/exercise.service";
import { ExerciseDTO } from "../../../model/ExerciseDTO";
import { LearningUnitDTO } from "../../../model/LearningUnitDTO";

interface Props {
    isOpen: boolean;
    onClose: () => void;
    learningUnitId: string;
    defaultCategoryId: string;
}

const AIGenerateModal: React.FC<Props> = ({
                                              isOpen,
                                              onClose,
                                              learningUnitId,
                                              defaultCategoryId
                                          }) => {
    const [currentStep, setCurrentStep] = useState(0);
    const [suggestedCategories, setSuggestedCategories] = useState<LearningUnitDTO[]>([]);
    const [selectedCategoryIds, setSelectedCategoryIds] = useState<string[]>([]);
    const [generatedExercises, setGeneratedExercises] = useState<ExerciseDTO[]>([]);

    // State để quản lý chế độ xem (Preview / Edit) cho từng câu hỏi
    // key: index câu hỏi, value: true (edit mode) | false (preview mode)
    const [editModes, setEditModes] = useState<{ [key: number]: boolean }>({});

    const [generateCategories, { isLoading: isLoadingCats }] = useGenerateCategoryContentMutation();
    const [generateExercises, { isLoading: isLoadingExs }] = useGenerateExercisesMutation();
    const [saveBulkExercises, { isLoading: isSaving }] = useAddListExerciseMutation();

    // Reset state khi đóng modal
    useEffect(() => {
        if (!isOpen) {
            setCurrentStep(0);
            setGeneratedExercises([]);
            setEditModes({});
        }
    }, [isOpen]);

    const handleFetchCategories = async () => {
        if (!defaultCategoryId) {
            message.error("Default category not found. Please initialize lesson config first.");
            return;
        }
        try {
            const data = await generateCategories({ originalExCateId: defaultCategoryId }).unwrap();
            setSuggestedCategories(data);
        } catch (err) {
            console.error(err);
            message.error("Failed to fetch AI categories");
        }
    };

    useEffect(() => {
        if (isOpen && currentStep === 0 && suggestedCategories.length === 0) {
            handleFetchCategories();
        }
    }, [isOpen]);

    const handleNextToExercises = async () => {
        if (selectedCategoryIds.length === 0) {
            message.warning("Please select at least one category.");
            return;
        }

        const selectedCategoryObjects = suggestedCategories.filter(cat =>
            selectedCategoryIds.includes(cat.code)
        );

        try {
            const rawResponse = await generateExercises({
                categories: selectedCategoryObjects,
                lessonId: learningUnitId
            }).unwrap();

            let parsedData = rawResponse;

            if (typeof rawResponse === 'string') {
                parsedData = JSON.parse(rawResponse);
            } else if (rawResponse && typeof (rawResponse as any).data === 'string') {
                parsedData = JSON.parse((rawResponse as any).data);
            }

            const flatExercises: ExerciseDTO[] = [];

            if (Array.isArray(parsedData)) {
                parsedData.forEach((catItem: any) => {
                    if (catItem.exercises && Array.isArray(catItem.exercises)) {

                        catItem.exercises.forEach((ex: any) => {
                            // Map dữ liệu từ BE format sang Frontend DTO
                            const mappedEx: ExerciseDTO = {
                                ...ex,
                                parentId: catItem.categoryId || defaultCategoryId,

                                options: ex.predefinedAnswers || [],
                                correctAnswers: ex.correctAnswerJson
                                    ? JSON.stringify(ex.correctAnswerJson.correctAnswers)
                                    : "[]"
                            };
                            flatExercises.push(mappedEx);
                        });
                    }
                });
            }

            // 4. Set vào state
            if (flatExercises.length === 0) {
                message.warning("AI returns no exercises. Please try again.");
            } else {
                setGeneratedExercises(flatExercises);
                setCurrentStep(1);
            }

        } catch (err) {
            console.error("Error generating exercises:", err);
            message.error("Failed to generate exercises. Check console for details.");
        }
    };
    const handleEditQuestion = (index: number, newVal: string) => {
        const newExercises = [...generatedExercises];
        newExercises[index] = { ...newExercises[index], question: newVal };
        setGeneratedExercises(newExercises);
    };

    const handleDeletePreview = (index: number) => {
        const newExercises = generatedExercises.filter((_, i) => i !== index);
        setGeneratedExercises(newExercises);

        // Clean up edit mode state
        const newEditModes = { ...editModes };
        delete newEditModes[index];
        setEditModes(newEditModes);
    };

    const toggleEditMode = (index: number) => {
        setEditModes(prev => ({ ...prev, [index]: !prev[index] }));
    };

    const handleFinalSave = async () => {
        try {
            await saveBulkExercises({
                exercises: generatedExercises,
                learningUnitId
            }).unwrap();
            message.success("Saved successfully!");
            onClose();
        } catch (err) {
            message.error("Failed to save exercises.");
        }
    };

    // --- RENDER HELPERS ---

    // CSS class để hiển thị Markdown đẹp (giống Github)
    const markdownStyles = "prose prose-sm max-w-none p-3 bg-white border border-gray-200 rounded-md min-h-[80px]";

    return (
        <Modal
            title={
                <div className="flex items-center gap-2 text-white">
                    <RobotOutlined className="text-xl" />
                    <span className="text-lg font-bold">AI Generation Assistant</span>
                </div>
            }
            open={isOpen}
            onCancel={onClose}
            width={1000} // Tăng độ rộng Modal
            footer={null}
            maskClosable={false}
            style={{ top: 20 }} // Cách top 1 chút
            bodyStyle={{ padding: '24px 24px' }}
        >
            {/* THANH TIẾN TRÌNH */}
            <div className="mb-8 px-12">
                <Steps
                    current={currentStep}
                    items={[
                        { title: 'Select Topics', description: 'Choose context', icon: <UnorderedListOutlined /> },
                        { title: 'Review Content', description: 'Edit Markdown', icon: <SaveOutlined /> },
                    ]}
                />
            </div>

            {/* BƯỚC 1: CHỌN CATEGORY */}
            {currentStep === 0 && (
                <div className="flex flex-col gap-6">
                    {isLoadingCats ? (
                        <div className="flex flex-col items-center justify-center py-20 bg-gray-50 rounded-lg">
                            <Spin size="large" tip="AI is analyzing content..." />
                        </div>
                    ) : (
                        <>
                            <div className="bg-blue-50 p-4 rounded-lg border border-blue-100">
                                <p className="text-blue-700 font-medium mb-0">
                                    Below are topics suggested by AI based on your "Default Category". Select the ones you want to generate exercises for.
                                </p>
                            </div>

                            <div className="h-[450px] overflow-y-auto p-4 rounded bg-gray-50 border border-gray-200">
                                {suggestedCategories.length === 0 ? (
                                    <div className="text-center py-10 text-gray-400">No categories found. Try refreshing.</div>
                                ) : (
                                    <Checkbox.Group
                                        className="w-full"
                                        value={selectedCategoryIds}
                                        onChange={(vals) => setSelectedCategoryIds(vals as string[])}
                                    >
                                        <Row gutter={[16, 16]}>
                                            {suggestedCategories.map(cat => (
                                                <Col span={12} key={cat.code}>
                                                    <div className="bg-white p-3 rounded shadow-sm border border-gray-100 hover:border-purple-300 transition-all h-full">
                                                        <Checkbox value={cat.code} className="w-full">
                                                            <div className="ml-1">
                                                                <div className="font-bold text-gray-800">{cat.name}</div>
                                                                <div className="text-gray-500 text-xs mt-1 line-clamp-2">{cat.description || "No description provided by AI."}</div>
                                                            </div>
                                                        </Checkbox>
                                                    </div>
                                                </Col>
                                            ))}
                                        </Row>
                                    </Checkbox.Group>
                                )}
                            </div>

                            <div className="flex justify-between mt-2 pt-4 border-t">
                                <Button onClick={handleFetchCategories}>Refresh Suggestions</Button>
                                <Button
                                    type="primary"
                                    size="large"
                                    onClick={handleNextToExercises}
                                    loading={isLoadingExs}
                                    className="bg-purple-600 hover:bg-purple-500"
                                >
                                    Generate Exercises ({selectedCategoryIds.length} topics)
                                </Button>
                            </div>
                        </>
                    )}
                </div>
            )}

            {/* BƯỚC 2: REVIEW EXERCISES (MARKDOWN SUPPORT) */}
            {currentStep === 1 && (
                <div className="flex flex-col gap-4">
                    <div className="flex justify-between items-center mb-2">
                        <p className="text-gray-600 mb-0">
                            AI generated <b>{generatedExercises.length}</b> exercises. Please review the Markdown content before saving.
                        </p>
                        <Tag color="geekblue">Markdown Supported</Tag>
                    </div>

                    <div className="h-[500px] overflow-y-auto pr-2 pb-10">
                        <List
                            dataSource={generatedExercises}
                            renderItem={(item, index) => {
                                const isEditMode = editModes[index];

                                return (
                                    <Card
                                        size="small"
                                        className="mb-4 shadow-sm border-gray-200 hover:shadow-md transition-shadow"
                                        title={
                                            <div className="flex items-center gap-2">
                                                <Tag color="purple">#{index + 1}</Tag>
                                                <Tag>{item.type}</Tag>
                                                {/* Hiển thị Difficulty nếu có */}
                                                {/*{item.difficulty && <Tag color={item.difficulty === 'HARD' ? 'red' : item.difficulty === 'MEDIUM' ? 'orange' : 'green'}>{item.difficulty}</Tag>}*/}
                                            </div>
                                        }
                                        extra={
                                            <div className="flex gap-2">
                                                <Button
                                                    size="small"
                                                    icon={isEditMode ? <EyeOutlined /> : <EditOutlined />}
                                                    onClick={() => toggleEditMode(index)}
                                                >
                                                    {isEditMode ? "Preview" : "Edit"}
                                                </Button>
                                                <Button
                                                    danger
                                                    type="text"
                                                    size="small"
                                                    icon={<DeleteOutlined />}
                                                    onClick={() => handleDeletePreview(index)}
                                                />
                                            </div>
                                        }
                                    >
                                        <div className="flex flex-col gap-3">
                                            <div>
                                                <span className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-1 block">Question Content (Markdown)</span>

                                                {isEditMode ? (
                                                    <Input.TextArea
                                                        rows={6}
                                                        value={item.question}
                                                        onChange={(e) => handleEditQuestion(index, e.target.value)}
                                                        className="font-mono text-sm"
                                                        placeholder="Type markdown here..."
                                                    />
                                                ) : (
                                                    /* RENDER MARKDOWN Ở ĐÂY */
                                                    <div className={markdownStyles}>
                                                        <ReactMarkdown remarkPlugins={[remarkGfm]}>
                                                            {item.question}
                                                        </ReactMarkdown>
                                                    </div>
                                                )}
                                            </div>

                                            <div className="bg-gray-50 p-2 rounded border border-gray-100">
                                                <span className="text-xs font-bold text-gray-400 uppercase tracking-wider">Correct Answer: </span>
                                                <span className="text-sm font-medium text-gray-700 ml-1">{item.correctAnswers}</span>
                                            </div>
                                        </div>
                                    </Card>
                                );
                            }}
                        />
                    </div>

                    <div className="flex justify-between pt-4 border-t bg-white sticky bottom-0 z-10">
                        <Button onClick={() => setCurrentStep(0)} size="large">Back</Button>
                        <Button
                            type="primary"
                            onClick={handleFinalSave}
                            loading={isSaving}
                            icon={<SaveOutlined />}
                            size="large"
                            className="bg-green-600 hover:bg-green-500"
                        >
                            Confirm & Save All
                        </Button>
                    </div>
                </div>
            )}
        </Modal>
    );
};

export default AIGenerateModal;