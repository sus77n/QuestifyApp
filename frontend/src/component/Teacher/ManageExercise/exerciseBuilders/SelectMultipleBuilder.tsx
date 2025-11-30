import React from "react";
import { Input, Button } from "antd";
import { ExerciseBuilderProps } from "./MultipleChoiceBuilder";

const SelectMultipleBuilder: React.FC<ExerciseBuilderProps> = ({
                                                                   value,
                                                                   onChange,
                                                               }) => {
    const toggleCorrect = (index: number) => {
        const header = String(index + 1);

        const isSelected = value.correctAnswers.includes(header);

        onChange({
            ...value,
            correctAnswers: isSelected
                ? value.correctAnswers.filter((c) => c !== header)
                : [...value.correctAnswers, header],
        });
    };

    const handleOptionChange = (index: number, text: string) => {
        const updated = [...value.options];
        updated[index].text = text;
        onChange({ ...value, options: updated });
    };

    const addOption = () => {
        const newHeader = String(value.options.length + 1);
        onChange({
            ...value,
            options: [...value.options, { header: newHeader, text: "" }],
        });
    };

    const removeOption = (index: number) => {
        const header = String(index + 1);

        onChange({
            options: value.options.filter((_, i) => i !== index),
            correctAnswers: value.correctAnswers.filter((c) => c !== header),
        });
    };

    return (
        <div className="space-y-3">
            {value.options.map((opt, index) => (
                <div key={index} className="flex items-center gap-2">
                    <input
                        type="checkbox"
                        checked={value.correctAnswers.includes(String(index + 1))}
                        onChange={() => toggleCorrect(index)}
                    />

                    <Input
                        placeholder={`Option ${index + 1}`}
                        value={opt.text}
                        onChange={(e) => handleOptionChange(index, e.target.value)}
                    />

                    <Button danger size="small" onClick={() => removeOption(index)}>
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

export default SelectMultipleBuilder;
