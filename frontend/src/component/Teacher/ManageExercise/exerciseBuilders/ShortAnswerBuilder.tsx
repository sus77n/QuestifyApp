import React from "react";
import { Input } from "antd";
import { ExerciseBuilderProps } from "./MultipleChoiceBuilder";

const ShortAnswerBuilder: React.FC<ExerciseBuilderProps> = ({
                                                                value,
                                                                onChange,
                                                            }) => {
    return (
        <div className="space-y-2">
            <Input
                placeholder="Correct answer"
                value={value.correctAnswers[0] || ""}
                onChange={(e) =>
                    onChange({
                        options: [],
                        correctAnswers: [e.target.value],
                    })
                }
            />
        </div>
    );
};

export default ShortAnswerBuilder;
