import React, { useEffect, useRef, useState } from "react";
import { ExerciseDTO } from "../../../../model/ExerciseDTO";
import { SubmissionDTO } from "../../../../model/SubmissionDTO";
import SpecialSymbolPicker from "../OtherComponents/SpecialSymbolPicker";

export default function ShortAnswerExercise({
                                                exercise,
                                                submission,
                                                onSubmissionChange,
                                            }: {
    exercise: ExerciseDTO;
    submission?: SubmissionDTO;
    onSubmissionChange: (s: SubmissionDTO) => void;
}) {
    const textareaRef = useRef<HTMLTextAreaElement | null>(null);

    const [answer, setAnswer] = useState(
        submission?.userAnswerJson ? JSON.parse(submission.userAnswerJson) : ""
    );

    useEffect(() => {
        if (submission?.userAnswerJson) {
            setAnswer(JSON.parse(submission.userAnswerJson));
        } else {
            setAnswer("");
        }
    }, [exercise.id]);

    const handleInsertSymbol = (symbol: string) => {
        const textarea = textareaRef.current;
        if (!textarea) return;

        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;

        const newValue =
            answer.slice(0, start) + symbol + answer.slice(end);

        setAnswer(newValue);

        onSubmissionChange({
            exerciseId: exercise.id,
            userAnswerJson: JSON.stringify(newValue),
        });

        setTimeout(() => {
            textarea.focus();
            textarea.selectionStart = textarea.selectionEnd = start + symbol.length;
        });
    };

    return (
        <div className="p-6 ">
            <h3 className="font-semibold text-lg mb-3">{exercise.question}</h3>
            <textarea
                ref={textareaRef}
                value={answer}
                onChange={(e) => setAnswer(e.target.value)}
                onBlur={() =>
                    onSubmissionChange({
                        exerciseId: exercise.id,
                        userAnswerJson: JSON.stringify(answer),
                    })
                }
                className="w-full border border-gray-300 rounded-lg p-3"
                rows={4}
                placeholder="Type your answer..."
            />
            <SpecialSymbolPicker onInsert={handleInsertSymbol} />

        </div>
    );
}
