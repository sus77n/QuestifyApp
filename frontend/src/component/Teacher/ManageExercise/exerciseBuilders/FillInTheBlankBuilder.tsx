import React from "react";
import { Input } from "antd";

export interface FillInBlankValue {
    correctAnswers: string[];
}

export interface FillInBlankProps {
    value: FillInBlankValue;
    onChange: (v: FillInBlankValue) => void;
}

const FillInTheBlankBuilder: React.FC<FillInBlankProps> = ({
                                                               value,
                                                               onChange
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
