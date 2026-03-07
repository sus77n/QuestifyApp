import React from "react";
import {Input, Button, Alert} from "antd";
import { ExerciseBuilderProps, BuilderOption } from "./MultipleChoiceBuilder";

const ReorderingBuilder: React.FC<ExerciseBuilderProps> = ({
                                                               value,
                                                               onChange, error
                                                           }) => {
    const { options } = value;

    const handleTextChange = (index: number, text: string) => {
        const updated = [...options];
        updated[index].text = text;

        onChange({
            ...value,
            options: updated,
            correctAnswers: updated.map((o) => o.header), // luôn 1..n
        });
    };

    const addOption = () => {
        const newHeader = String(options.length + 1);

        const newOptions: BuilderOption[] = [
            ...options,
            { header: newHeader, text: "" },
        ];

        onChange({
            options: newOptions,
            correctAnswers: newOptions.map((o) => o.header),
        });
    };

    const removeOption = (index: number) => {
        const updated = options.filter((_, i) => i !== index);

        // Reassign header: 1..n
        const remapped = updated.map((o, i) => ({
            header: String(i + 1),
            text: o.text,
        }));

        onChange({
            options: remapped,
            correctAnswers: remapped.map((o) => o.header),
        });
    };


    return (
        <div className="space-y-3">
            <Alert
                message="Instructions"
                description={
                    <ul className="list-disc list-inside mt-1 text-sm text-gray-600 space-y-1">
                        <li>In the <strong>Question</strong> box above, provide clear instructions <em>(e.g., "Arrange the following sentences in the correct order")</em>.</li>
                        <li>Enter the items in their <strong>correct, logical order</strong> below.</li>
                        <li><span className="text-orange-600 font-medium">Note:</span> The system will automatically shuffle these items for the students.</li>
                        <li>Click the <strong>"+ Add Clause"</strong> button to add more items to the sequence.</li>
                    </ul>
                }
                type="info"
                showIcon
                closable
            />

            {/* Error display */}
            {error && <Alert message={error} type="error" showIcon className="mb-2" />}

            {options.map((opt, index) => (
                <div key={index} className="flex items-center gap-2">
                    <span className="w-5 text-right">{opt.header}.</span>

                    <Input
                        placeholder={`Clause ${index + 1}`}
                        value={opt.text}
                        onChange={(e) => handleTextChange(index, e.target.value)}
                    />

                    <Button
                        danger
                        size="small"
                        onClick={() => removeOption(index)}
                    >
                        X
                    </Button>
                </div>
            ))}

            <div className="flex gap-2">
                <Button type="dashed" onClick={addOption}>
                    + Add Clause
                </Button>

            </div>
        </div>
    );
};

export default ReorderingBuilder;
