import React from "react";
import { Input, Button, Alert } from "antd";

export interface BuilderOption {
    header: string;
    text: string;
}

export interface ExerciseBuilderValue {
    options: BuilderOption[];
    correctAnswers: string[];
}

export interface ExerciseBuilderProps {
    value: ExerciseBuilderValue;
    onChange: (value: ExerciseBuilderValue) => void;
    error?: string | null; // <--- Thêm prop này để nhận lỗi từ cha
}

const MultipleChoiceBuilder: React.FC<ExerciseBuilderProps> = ({
                                                                   value,
                                                                   onChange,
                                                                   error // <--- Destructure error
                                                               }) => {
    const { options, correctAnswers } = value;

    const handleOptionChange = (index: number, text: string) => {
        const updated = [...options];
        updated[index].text = text;
        onChange({ ...value, options: updated });
    };

    const addOption = () => {
        const newHeader = String(options.length + 1);
        onChange({
            ...value,
            options: [...options, { header: newHeader, text: "" }],
        });
    };

    const removeOption = (indexToRemove: number) => {
        const newOptions = options.filter((_, i) => i !== indexToRemove);

        let newCorrectAnswers = [...correctAnswers];

        if (correctAnswers.length > 0) {
            const currentSelectedIndex = parseInt(correctAnswers[0]) - 1;

            if (currentSelectedIndex === indexToRemove) {
                newCorrectAnswers = [];
            } else if (currentSelectedIndex > indexToRemove) {
                newCorrectAnswers = [String(currentSelectedIndex)];
            }
        }

        onChange({
            options: newOptions,
            correctAnswers: newCorrectAnswers,
        });
    };

    const selectCorrect = (index: number) => {
        onChange({
            ...value,
            correctAnswers: [String(index + 1)],
        });
    };

    return (
        <div className="space-y-3">
            {/* Hiển thị lỗi nếu có */}
            {error && <Alert message={error} type="error" showIcon className="mb-2" />}

            {options.map((opt, index) => {
                const isSelected = correctAnswers.includes(String(index + 1));
                return (
                    <div key={index} className="flex items-center gap-2">
                        <input
                            type="radio"
                            className="w-4 h-4 cursor-pointer"
                            checked={isSelected}
                            onChange={() => selectCorrect(index)}
                        />

                        <Input
                            status={error && !isSelected && correctAnswers.length === 0 ? "error" : ""}
                            placeholder={`Option ${index + 1}`}
                            value={opt.text}
                            onChange={(e) => handleOptionChange(index, e.target.value)}
                        />

                        <Button
                            type="dashed"
                            danger
                            size="small"
                            onClick={() => removeOption(index)}
                            disabled={options.length <= 2}
                        >
                            X
                        </Button>
                    </div>
                );
            })}

            <Button type="dashed" onClick={addOption}>
                + Add Option
            </Button>
        </div>
    );
};

export default MultipleChoiceBuilder;