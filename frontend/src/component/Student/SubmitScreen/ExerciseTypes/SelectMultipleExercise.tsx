import React, { useState, useEffect } from "react";
import { ExerciseDTO } from "../../../../model/ExerciseDTO";
import { SubmissionDTO } from "../../../../model/SubmissionDTO";

export default function SelectMultipleExercise({
                                                   exercise,
                                                   submission,
                                                   onSubmissionChange,
                                               }: {
    exercise: ExerciseDTO;
    submission?: SubmissionDTO;
    onSubmissionChange: (s: SubmissionDTO) => void;
}) {
    // selected = ["1", "3", "5"]
    const [selected, setSelected] = useState<string[]>(
        submission?.userAnswerJson ? JSON.parse(submission.userAnswerJson) : []
    );

    useEffect(() => {
        if (submission?.userAnswerJson) {
            setSelected(JSON.parse(submission.userAnswerJson));
        } else {
            setSelected([]);
        }
    }, [exercise.id]);

    const toggleOption = (header: string) => {
        const newSelected = selected.includes(header)
            ? selected.filter((x) => x !== header)
            : [...selected, header];

        setSelected(newSelected);

        onSubmissionChange({
            exerciseId: exercise.id,
            userAnswerJson: JSON.stringify(newSelected), // send headers only
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
                        key={opt.header} // use header as key
                        className={`flex items-center px-4 py-2 mb-2 rounded-lg cursor-pointer border transition-all duration-200
                            ${
                            selected.includes(opt.header!)
                                ? "border-text-color bg-light-background"
                                : "border-gray-300 hover:bg-gray-50"
                        }`}
                    >
                        <input
                            type="checkbox"
                            checked={selected.includes(opt.header!)}
                            onChange={() => toggleOption(opt.header!)} // send header
                            className="mr-3"
                        />
                        <span>{opt.text}</span>
                    </label>
                ))}
            </div>
        </div>
    );
}
