import React from "react";
import { Input, Button } from "antd";

export interface BuilderOption {
    header: string; // "1", "2", "3"
    text: string;
}

export interface ExerciseBuilderValue {
    options: BuilderOption[];
    correctAnswers: string[];
}

export interface ExerciseBuilderProps {
    value: ExerciseBuilderValue;
    onChange: (value: ExerciseBuilderValue) => void;
}

const MultipleChoiceBuilder: React.FC<ExerciseBuilderProps> = ({
                                                                   value,
                                                                   onChange,
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

    const removeOption = (index: number) => {
        const header = String(index + 1);

        onChange({
            options: value.options.filter((_, i) => i !== index),
            correctAnswers: value.correctAnswers.filter((c) => c !== header),
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
            {options.map((opt, index) => (
                <div key={index} className="flex items-center gap-2">
                    <input
                        type="radio"
                        checked={correctAnswers.includes(String(index + 1))}
                        onChange={() => selectCorrect(index)}
                    />

                    <Input
                        placeholder={`Option ${index + 1}`}
                        value={opt.text}
                        onChange={(e) => handleOptionChange(index, e.target.value)}
                    />

                    <Button type="dashed" danger size="small" onClick={() => removeOption(index)}>
                        X
                    </Button>
                </div>
            ))}

            <Button type="dashed" onClick={addOption}>
                + Add Option
            </Button>
        </div>
    );
};

export default MultipleChoiceBuilder;
