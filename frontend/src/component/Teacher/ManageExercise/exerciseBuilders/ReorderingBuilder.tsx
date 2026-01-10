import React from "react";
import { Input, Button } from "antd";
import { ExerciseBuilderProps, BuilderOption } from "./MultipleChoiceBuilder";

const ReorderingBuilder: React.FC<ExerciseBuilderProps> = ({
                                                               value,
                                                               onChange,
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

    const randomizeOrder = () => {
        const shuffled = [...options].sort(() => Math.random() - 0.5);

        // Gán lại header theo thứ tự random
        const remapped = shuffled.map((o, i) => ({
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

                <Button onClick={randomizeOrder}>Randomize</Button>
            </div>
        </div>
    );
};

export default ReorderingBuilder;
