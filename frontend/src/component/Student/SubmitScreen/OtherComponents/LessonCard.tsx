import React from "react";

export const LessonCard = ({
                        index,
                        name,
                        isSelected,
                        onClick,
                        numberOfComplete,
                        numberOfExercise,
                    }: {
    index: number;
    name: string;
    isSelected: boolean;
    onClick: () => void;
    numberOfComplete: number | null;
    numberOfExercise: number | null;
}) => {
    return (
        <div
            className={`p-3 mb-2 ml-2 rounded-lg cursor-pointer ${
                isSelected ? "bg-text-color" : "hover:bg-light-background"
            }`}
            onClick={onClick}
        >
            <h3 className={`font-medium ${isSelected ? "text-white" : "text-text-color"}`}>
                Lesson {index}: {name}
                {isSelected ? (
                    ` (${numberOfComplete} / ${numberOfExercise})`
                ) : (
                    <span className={numberOfComplete !== numberOfExercise ? "text-yellow-500" : "text-green-500"}>
            {" "}
                        ({numberOfComplete} / {numberOfExercise})
          </span>
                )}
            </h3>
        </div>
    );
};
