import React, { useEffect, useState } from "react";
import { ExerciseDTO } from "../../../../model/ExerciseDTO";
import { SubmissionDTO } from "../../../../model/SubmissionDTO";

export default function TrueFalseExercise({
                                              exercise,
                                              submission,
                                              onSubmissionChange,
                                          }: {
    exercise: ExerciseDTO;
    submission?: SubmissionDTO;
    onSubmissionChange: (s: SubmissionDTO) => void;
}) {

    // Submission format = array of optionIds marked TRUE
    const initialTrueIds: number[] = submission?.userAnswerJson
        ? JSON.parse(submission.userAnswerJson)
        : [];

    const [trueSelected, setTrueSelected] = useState<number[]>(initialTrueIds);

    useEffect(() => {
        if (submission?.userAnswerJson) {
            setTrueSelected(JSON.parse(submission.userAnswerJson));
        } else {
            setTrueSelected([]); // reset
        }
    }, [exercise.id]);

    const toggleSelection = (optionId: number, value: boolean) => {
        let updated: number[];

        if (value === true) {
            updated = Array.from(new Set([...trueSelected, optionId]));
        } else {
            updated = trueSelected.filter(id => id !== optionId);
        }

        setTrueSelected(updated);

        onSubmissionChange({
            exerciseId: exercise.id,
            userAnswerJson: JSON.stringify(updated),
        });
    };

    return (
        <div className="p-6 rounded-lg min-h-[400px]">
            <h3 className="font-medium text-xl mb-6 text-gray-800">
                {exercise.question}
            </h3>

            <div className="space-y-4">
                {exercise.options?.map(opt => {
                    const isTrue = trueSelected.includes(opt.id);

                    return (
                        <div
                            key={opt.id}
                            className="p-4 border rounded-lg bg-white grid grid-cols-6 gap-4 items-center"
                        >
                            {/* LEFT SIDE - TEXT */}
                            <div className="col-span-5">
                                <p className="font-medium text-gray-800">{opt.text}</p>
                            </div>

                            {/* RIGHT SIDE - BUTTONS */}
                            <div className="col-span-1 flex gap-2 justify-end">
                                {/* TRUE */}
                                <button
                                    onClick={() => toggleSelection(opt.id, true)}
                                    className={`px-4 py-2 rounded-lg border font-semibold
                    ${
                                        isTrue
                                            ? "bg-green-100 border-green-600 text-green-700"
                                            : "border-gray-300 hover:bg-gray-50"
                                    }
                `}
                                >
                                    True
                                </button>

                                {/* FALSE */}
                                <button
                                    onClick={() => toggleSelection(opt.id, false)}
                                    className={`px-4 py-2 rounded-lg border font-semibold
                    ${
                                        !isTrue
                                            ? "bg-red-100 border-red-300 text-red-700"
                                            : "border-gray-300 hover:bg-gray-50"
                                    }
                `}
                                >
                                    False
                                </button>
                            </div>
                        </div>
                    );

                })}
            </div>
        </div>
    );
}
