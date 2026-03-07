import React from "react";
import {Alert, Input} from "antd";
import {ExerciseBuilderProps} from "./MultipleChoiceBuilder";

const ShortAnswerBuilder: React.FC<ExerciseBuilderProps> = ({
                                                                value,
                                                                onChange,
                                                                error,
                                                            }) => {
    return (
        <div className="space-y-2">
            <Alert
                message="Instructions"
                description={
                    <ul className="list-disc list-inside mt-1 text-sm text-gray-600 space-y-1">
                        <li>After entering the question above, type the exact correct answer in the text box below.</li>
                        <li>Keep the answer concise, as students will need to type this exact text.</li>
                    </ul>
                }
                type="info"
                showIcon
                closable
            />

            {/* Error display */}
            {error && <Alert message={error} type="error" showIcon className="mb-2"/>}
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
