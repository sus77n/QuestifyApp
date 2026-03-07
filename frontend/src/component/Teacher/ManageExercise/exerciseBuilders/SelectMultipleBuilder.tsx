import React from "react";
import {Input, Button, Alert} from "antd";
import { ExerciseBuilderProps } from "./MultipleChoiceBuilder";

const SelectMultipleBuilder: React.FC<ExerciseBuilderProps> = ({
                                                                   value,
                                                                   onChange,
                                                                   error,
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
            <Alert
                message="Instructions"
                description={
                    <ul className="list-disc list-inside mt-1 text-sm text-gray-600 space-y-1">
                        <li>After entering the question above, fill in the option text below.</li>
                        <li>Click the <strong>"+ Add Option"</strong> button to add a new choice.</li>
                        <li>Select the <strong>checkboxes</strong> next to the options to mark them as correct. You can select <strong>multiple</strong> correct answers.</li>
                    </ul>
                }
                type="info"
                showIcon
                closable
            />

            {/* Error display */}
            {error && <Alert message={error} type="error" showIcon className="mb-2" />}

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

                    <Button danger type="dashed" size="small" onClick={() => removeOption(index)}>
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
