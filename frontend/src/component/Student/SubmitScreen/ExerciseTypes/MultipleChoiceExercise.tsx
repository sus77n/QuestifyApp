import React, { useState, useEffect } from "react";
import {ExerciseDTO} from "../../../../model/ExerciseDTO";
import {SubmissionDTO} from "../../../../model/SubmissionDTO";

export default function MultipleChoiceExercise({
                                                   exercise,
                                                   submission,
                                                   onSubmissionChange,
                                               }: {
    exercise: ExerciseDTO;
    submission?: SubmissionDTO;
    onSubmissionChange: (s: SubmissionDTO) => void;
}) {
    const [selected, setSelected] = useState<string | null>(
        submission?.userAnswerJson
            ? JSON.parse(submission.userAnswerJson)[0]
            : null
    );

    useEffect(() => {
        if (submission?.userAnswerJson) {
            setSelected(JSON.parse(submission.userAnswerJson)[0]);
        } else {
            setSelected(null);
        }
    }, [exercise.id]);

    const handleSelect = (header: string | undefined) => {
        const value = header ?? "";
        setSelected(value);

        onSubmissionChange({
            exerciseId: exercise.id,
            userAnswerJson: JSON.stringify([value]),
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
                        key={opt.header} // dùng header làm key cũng OK
                        className={`flex items-center px-4 py-2 mb-2 rounded-lg cursor-pointer border transition-all duration-200
                            ${selected === opt.header
                            ? "border-text-color bg-light-background"
                            : "border-gray-300 hover:bg-gray-50"
                        }`}
                    >
                        <input
                            type="radio"
                            checked={selected === opt.header}
                            onChange={() => handleSelect(opt.header)} // <--- gửi header
                            className="mr-3"
                        />
                        <span>{opt.text}</span>
                    </label>
                ))}
            </div>
        </div>
    );
}
