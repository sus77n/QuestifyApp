import React, {useEffect, useState} from "react";
import { Modal, Steps, Checkbox, Button, List, Input, message, Spin, Card } from "antd";
import { RobotOutlined, UnorderedListOutlined, SaveOutlined } from "@ant-design/icons";
import { useGenerateCategoryContentMutation, useGenerateExercisesMutation } from "../../../API/service/learningUnit.service";
import { useAddListExerciseMutation } from "../../../API/service/exercise.service";
import { ExerciseDTO } from "../../../model/ExerciseDTO";
import {LearningUnitDTO} from "../../../model/LearningUnitDTO";

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

    const [generateCategories, { isLoading: isLoadingCats }] = useGenerateCategoryContentMutation();
    const [generateExercises, { isLoading: isLoadingExs }] = useGenerateExercisesMutation();
    const [saveBulkExercises, { isLoading: isSaving }] = useAddListExerciseMutation();

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
        try {
            const data = await generateExercises({
                categories: selectedCategoryIds,
                learningUnitId
            }).unwrap();
            setGeneratedExercises(data);
            setCurrentStep(1);
        } catch (err) {
            message.error("Failed to generate exercises");
        }
    };

    const handleEditQuestion = (index: number, newVal: string) => {
        const newExercises = [...generatedExercises];
        newExercises[index].question = newVal;
        setGeneratedExercises(newExercises);
    };

    const handleDeletePreview = (index: number) => {
        const newExercises = generatedExercises.filter((_, i) => i !== index);
        setGeneratedExercises(newExercises);
    };

    const handleFinalSave = async () => {
        try {
            await saveBulkExercises({
                exercises: generatedExercises,
                learningUnitId
            }).unwrap();
            message.success("Saved successfully!");
            onClose();
            // Reset state
            setCurrentStep(0);
            setSuggestedCategories([]);
            setGeneratedExercises([]);
        } catch (err) {
            message.error("Failed to save exercises.");
        }
    };

    return (
        <Modal
            title={<><RobotOutlined /> AI Generation Wizard</>}
            open={isOpen}
            onCancel={onClose}
            width={800}
            footer={null} // Tự custom footer
            maskClosable={false}
        >
            <Steps
                current={currentStep}
                items={[
                    { title: 'Select Categories', icon: <UnorderedListOutlined /> },
                    { title: 'Review Exercises', icon: <SaveOutlined /> },
                ]}
                className="mb-6"
            />

            {/* BƯỚC 1: CHỌN CATEGORY */}
            {currentStep === 0 && (
                <div className="flex flex-col gap-4">
                    {isLoadingCats ? (
                        <div className="text-center py-10"><Spin tip="AI is thinking categories..." /></div>
                    ) : (
                        <>
                            <p className="text-gray-500">Select topics you want AI to generate exercises for:</p>
                            <div className="max-h-[400px] overflow-y-auto border p-4 rounded bg-gray-50">
                                <Checkbox.Group
                                    className="flex flex-col gap-3"
                                    value={selectedCategoryIds}
                                    onChange={(vals) => setSelectedCategoryIds(vals as string[])}
                                >
                                    {suggestedCategories.map(cat => (
                                        <Checkbox key={cat.id} value={cat.id}>
                                            <span className="font-semibold">{cat.name}</span>
                                            {/* Giả sử cat có description */}
                                            {/* <span className="text-gray-400 text-xs ml-2">- {cat.description}</span> */}
                                        </Checkbox>
                                    ))}
                                </Checkbox.Group>

                                {suggestedCategories.length === 0 && <p>No categories found. Try refreshing.</p>}
                            </div>

                            <div className="flex justify-end gap-2 mt-4">
                                <Button onClick={handleFetchCategories}>Refresh</Button>
                                <Button
                                    type="primary"
                                    onClick={handleNextToExercises}
                                    loading={isLoadingExs}
                                >
                                    Generate Exercises & Next
                                </Button>
                            </div>
                        </>
                    )}
                </div>
            )}

            {/* BƯỚC 2: REVIEW EXERCISES */}
            {currentStep === 1 && (
                <div className="flex flex-col gap-4">
                    <p className="text-gray-500">
                        AI generated <b>{generatedExercises.length}</b> exercises. Review before saving.
                    </p>

                    <div className="max-h-[400px] overflow-y-auto pr-2">
                        <List
                            dataSource={generatedExercises}
                            renderItem={(item, index) => (
                                <Card size="small" className="mb-3 bg-gray-50"
                                      title={`#${index + 1} (${item.type})`}
                                      extra={<Button danger type="text" size="small" onClick={() => handleDeletePreview(index)}>Remove</Button>}
                                >
                                    <Input.TextArea
                                        rows={2}
                                        value={item.question}
                                        onChange={(e) => handleEditQuestion(index, e.target.value)}
                                    />
                                    <div className="mt-2 text-xs text-gray-500">
                                        Answer: {item.correctAnswers}
                                    </div>
                                </Card>
                            )}
                        />
                    </div>

                    <div className="flex justify-between mt-4">
                        <Button onClick={() => setCurrentStep(0)}>Back</Button>
                        <Button
                            type="primary"
                            onClick={handleFinalSave}
                            loading={isSaving}
                            icon={<SaveOutlined />}
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