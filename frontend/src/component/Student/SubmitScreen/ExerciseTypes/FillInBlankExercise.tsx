import React, { useState, useRef, useEffect } from "react";
import { ExerciseDTO } from "../../../../model/ExerciseDTO";
import { SubmissionDTO } from "../../../../model/SubmissionDTO";
import SpecialSymbolPicker from "../OtherComponents/SpecialSymbolPicker";

export default function FillInBlankExercise({
                                                exercise,
                                                submission,
                                                onSubmissionChange,
                                            }: {
    exercise: ExerciseDTO;
    submission?: SubmissionDTO;
    onSubmissionChange: (s: SubmissionDTO) => void;
}) {
    const parts = exercise.question.split(/_{3,}/g);
    const blankCount = parts.length - 1;

    const [answers, setAnswers] = useState<string[]>(
        submission?.userAnswerJson
            ? JSON.parse(submission.userAnswerJson)
            : Array(blankCount).fill("")
    );

    const [currentIndex, setCurrentIndex] = useState<number | null>(null);

    const inputRefs = useRef<(HTMLInputElement | null)[]>([]);

    useEffect(() => {
        inputRefs.current = Array(blankCount).fill(null);
    }, [blankCount]);

    const insertSymbol = (symbol: string) => {
        if (currentIndex === null) return;

        const input = inputRefs.current[currentIndex];
        if (!input) return;

        const start = input.selectionStart ?? 0;
        const end = input.selectionEnd ?? 0;

        const updated = [...answers];
        updated[currentIndex] =
            updated[currentIndex].slice(0, start) +
            symbol +
            updated[currentIndex].slice(end);

        setAnswers(updated);

        onSubmissionChange({
            exerciseId: exercise.id,
            userAnswerJson: JSON.stringify(updated),
        });

        setTimeout(() => {
            input.focus();
            input.selectionStart = input.selectionEnd = start + symbol.length;
        });
    };

    return (
        <div className="p-6 rounded-lg">
            <div className="text-xl font-medium text-gray-900 flex flex-wrap">
                {parts.map((text, i) => (
                    <React.Fragment key={i}>
                        <span>{text}</span>

                        {i < blankCount && (
                            <input
                                ref={(el) => {
                                    inputRefs.current[i] = el;
                                }}
                                type="text"
                                value={answers[i]}
                                onFocus={() => setCurrentIndex(i)}
                                onChange={(e) => {
                                    const updated = [...answers];
                                    updated[i] = e.target.value;
                                    setAnswers(updated);

                                    onSubmissionChange({
                                        exerciseId: exercise.id,
                                        userAnswerJson: JSON.stringify(updated),
                                    });
                                }}
                                className="border rounded border-gray-600 w-32 mx-2 px-1"
                            />
                        )}
                    </React.Fragment>
                ))}
            </div>

            <SpecialSymbolPicker onInsert={insertSymbol} />
        </div>
    );
}
