import React from "react";

export const IndexExerciseButton = ({
                                 index,
                                 isDone,
                                 isActive,
                                 onClick,
                             }: {
    index: number;
    isDone: boolean;
    isActive: boolean;
    onClick: () => void;
}) => {
    return (
        <button
            className={`relative m-2 flex justify-center items-center rounded-xl w-[65px] h-[65px]
        border-2 ${
                isDone
                    ? "border-blue-300 bg-blue-50"
                    : isActive
                        ? "border-background-color bg-light-background"
                        : "border-gray-300 bg-white"
            }
        transition-all duration-200
        hover:border-blue-300 hover:bg-blue-50
        active:scale-95`}
            onClick={onClick}
        >
            <h1
                className={`font-bold text-2xl ${
                    isDone ? "text-blue-500" : isActive ? "text-text-color" : "text-gray-700"
                }`}
            >
                {index}
            </h1>

            {isDone && (
                <div className="absolute bottom-1 right-1">
                    <svg className="w-5 h-5 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="M5 13l4 4L19 7" />
                    </svg>
                </div>
            )}
        </button>
    );
};
