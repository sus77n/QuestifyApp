import React from "react";
import {Alert, Input} from "antd";

export interface FillInBlankValue {
    correctAnswers: string[];
}

export interface FillInBlankProps {
    value: FillInBlankValue;
    onChange: (v: FillInBlankValue) => void;
    error?: string | null;
}

const FillInTheBlankBuilder: React.FC<FillInBlankProps> = ({
                                                               value,
                                                               onChange,error
                                                           }) => {

    const updateAnswers = (text: string) => {
        const lines = text
            .split("\n")
            .map(l => l.trim())
            .filter(l => l !== "");

        onChange({
            correctAnswers: lines
        });
    };

    return (
        <div className="space-y-2">

            <Alert
                message="Instructions"
                description={
                    <ul className="list-disc list-inside mt-1 text-sm text-gray-600 space-y-1">
                        <li>In the <strong>Question</strong> box above, use three underscores (<strong>___</strong>) to represent a blank space. <em>(e.g., "The sky is ___.")</em></li>
                        <li>You can click the button below to quickly copy the <strong>___</strong> characters.</li>
                        <li>Enter the correct answers below. Type <strong>one answer per line</strong> in the exact order the blanks appear in the question.</li>
                    </ul>
                }
                type="info"
                showIcon
                closable
            />

            {/* Error display */}
            {error && <Alert message={error} type="error" showIcon className="mb-2" />}
            <p className="font-semibold">
                Correct Answers (enter one per line)
            </p>

            <Input.TextArea
                rows={6}
                value={value.correctAnswers.join("\n")}
                placeholder={`Enter correct answers, one per line.\nExample:\ngo\nschool\nblue`}
                onChange={(e) => updateAnswers(e.target.value)}
            />
        </div>
    );
};

export default FillInTheBlankBuilder;
