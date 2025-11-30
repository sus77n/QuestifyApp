import React from "react";
import { Button, Input } from "antd";
import {ExerciseBuilderProps} from "./MultipleChoiceBuilder";

const TrueFalseBuilder: React.FC<ExerciseBuilderProps> = ({
                                                              value,
                                                              onChange
                                                          }) => {

    const updateText = (index: number, text: string) => {
        const newOptions = [...value.options];
        newOptions[index].text = text;

        onChange({
            ...value,
            options: newOptions
        });
    };

    const markTrue = (index: number) => {
        const header = value.options[index].header;

        let newCorrect = [...value.correctAnswers];

        if (!newCorrect.includes(header)) {
            newCorrect.push(header);
        }

        onChange({
            ...value,
            correctAnswers: newCorrect
        });
    };

    const markFalse = (index: number) => {
        const header = value.options[index].header;

        const newCorrect = value.correctAnswers.filter(h => h !== header);

        onChange({
            ...value,
            correctAnswers: newCorrect
        });
    };

    const addOption = () => {
        const nextHeader = String(value.options.length + 1);

        const newOption = {
            id: crypto.randomUUID(),
            header: nextHeader,
            text: ""
        };

        onChange({
            ...value,
            options: [...value.options, newOption]
        });
    };

    const removeOption = (index: number) => {
        const filtered = value.options.filter((_, i) => i !== index);

        // Re-generate headers: 1..n
        const reheadered = filtered.map((o, idx) => ({
            ...o,
            header: String(idx + 1)
        }));

        // Update correctAnswers accordingly
        const newCorrect = value.correctAnswers.filter(h =>
            reheadered.some(o => o.header === h)
        );

        onChange({
            options: reheadered,
            correctAnswers: newCorrect
        });
    };

    return (
        <div className="space-y-4">

            {value.options.map((opt, index) => {
                const isTrue = value.correctAnswers.includes(opt.header);

                return (
                    <div key={opt.header} className="border p-3 rounded-lg space-y-2">

                        <div className="flex items-center gap-2">
                            <Input
                                placeholder="Statement"
                                value={opt.text}
                                onChange={(e) => updateText(index, e.target.value)}
                            />

                            <Button danger size="small" onClick={() => removeOption(index)}>
                                X
                            </Button>
                        </div>

                        <div className="flex gap-3">

                            <Button
                                type={isTrue ? "primary" : "default"}
                                onClick={() => markTrue(index)}
                            >
                                True
                            </Button>

                            <Button
                                type={!isTrue ? "primary" : "default"}
                                onClick={() => markFalse(index)}
                            >
                                False
                            </Button>

                        </div>

                    </div>
                );
            })}

            <Button type="dashed" onClick={addOption}>
                + Add Statement
            </Button>

        </div>
    );
};

export default TrueFalseBuilder;
