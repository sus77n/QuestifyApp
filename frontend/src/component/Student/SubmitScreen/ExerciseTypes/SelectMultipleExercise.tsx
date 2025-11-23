import React, { useState, useEffect } from "react";
import {ExerciseDTO} from "../../../../model/ExerciseDTO";
import {SubmissionDTO} from "../../../../model/SubmissionDTO";

export default function SelectMultipleExercise({
                                                   exercise,
                                                   submission,
                                                   onSubmissionChange,
                                               }: {
    exercise: ExerciseDTO;
    submission?: SubmissionDTO;
    onSubmissionChange: (s: SubmissionDTO) => void;
}) {
    const [selected, setSelected] = useState<number[]>(
        submission?.userAnswerJson ? JSON.parse(submission.userAnswerJson) : []
    );

    useEffect(() => {
        if (submission?.userAnswerJson) {
            setSelected(JSON.parse(submission.userAnswerJson));
        } else {
            setSelected([]);
        }
    }, [exercise.id]);

    const toggleOption = (id: number) => {
        const newSelected = selected.includes(id)
            ? selected.filter((x) => x !== id)
            : [...selected, id];
        setSelected(newSelected);
        onSubmissionChange({
            exerciseId: exercise.id,
            userAnswerJson: JSON.stringify(newSelected),
        });
    };

    return (
        <div className="p-6 rounded-lg min-h-[400px] flex flex-col justify-between">
            <div>
                <h3 className="font-medium text-xl mb-4 text-gray-800">
                    {exercise.question}
                </h3>

                {exercise.options?.map((opt) => (
                    <label
                        key={opt.id}
                        className={`flex items-center px-4 py-2 mb-2 rounded-lg cursor-pointer border transition-all duration-200
              ${
                            selected.includes(opt.id)
                                ? "border-text-color bg-light-background"
                                : "border-gray-300 hover:bg-gray-50"
                        }`}
                    >
                        <input
                            type="checkbox"
                            checked={selected.includes(opt.id)}
                            onChange={() => toggleOption(opt.id)}
                            className="mr-3"
                        />
                        <span>{opt.text}</span>
                    </label>
                ))}
            </div>

        </div>
    );
}
