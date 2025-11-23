import React, { useState } from "react";
import { ExerciseDTO } from "../../../../model/ExerciseDTO";
import { SubmissionDTO } from "../../../../model/SubmissionDTO";

export default function MatchingExercise({
                                             exercise,
                                             submission,
                                             onSubmissionChange,
                                         }: {
    exercise: ExerciseDTO;
    submission?: SubmissionDTO;
    onSubmissionChange: (s: SubmissionDTO) => void;
}) {
    const left = exercise.options?.filter(o => o.side === "left") ?? [];
    const right = exercise.options?.filter(o => o.side === "right") ?? [];

    // Right side order (draggable list)
    const initialOrder = submission?.userAnswerJson
        ? JSON.parse(submission.userAnswerJson).map((p: any) => p.rightId)
        : right.map(r => r.id);

    const [rightOrder, setRightOrder] = useState<number[]>(initialOrder);
    const [dragOverIndex, setDragOverIndex] = useState<number | null>(null);

    const handleDragStart = (e: React.DragEvent<HTMLDivElement>, index: number) => {
        e.dataTransfer.setData("dragIndex", index.toString());
    };

    const handleDragOver = (e: React.DragEvent<HTMLDivElement>, index: number) => {
        e.preventDefault();
        setDragOverIndex(index);
    };

    const handleDrop = (e: React.DragEvent<HTMLDivElement>, dropIndex: number) => {
        const dragIndex = Number(e.dataTransfer.getData("dragIndex"));
        const updated = [...rightOrder];
        const [dragged] = updated.splice(dragIndex, 1);
        updated.splice(dropIndex, 0, dragged);

        setRightOrder(updated);
        setDragOverIndex(null);

        const pairs = updated.map((rightId, i) => ({
            leftId: left[i]?.id,
            rightId: rightId,
        }));

        onSubmissionChange({
            exerciseId: exercise.id,
            userAnswerJson: JSON.stringify(pairs),
        });
    };

    const findRightText = (id: number) =>
        right.find(r => r.id === id)?.text ?? "";

    return (
        <div className="p-6 rounded-lg">
            <h3 className="font-medium text-xl mb-6 text-gray-800">{exercise.question}</h3>

            <div className="grid grid-cols-2 gap-6">
                {/* LEFT FIXED LIST */}
                <div className="flex flex-col">
                    {left.map((l, i) => (
                        <div
                            key={l.id}
                            className="p-3 mb-3 border rounded-lg bg-gray-100 font-semibold text-gray-900"
                        >
                            {i + 1}. {l.text}
                        </div>
                    ))}
                </div>

                <div>
                    {rightOrder.map((rightId, i) => (
                        <div
                            key={`${rightId}-${i}`}
                            draggable
                            onDragStart={(e) => handleDragStart(e, i)}
                            onDragOver={(e) => handleDragOver(e, i)}
                            onDrop={(e) => handleDrop(e, i)}
                            className={`p-3 mb-3 border rounded-lg bg-white cursor-move transition-all
                                ${dragOverIndex === i ? "bg-blue-50 border-blue-500" : "hover:bg-gray-50"}
                            `}
                        >
                            {findRightText(rightId)}
                        </div>
                    ))}
                </div>
            </div>

            <p className="text-gray-600 mt-4 text-sm">
                Drag items on the right to match each item on the left.
            </p>
        </div>
    );
}
