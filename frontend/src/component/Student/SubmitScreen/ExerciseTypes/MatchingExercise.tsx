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
// Sửa lại đường dẫn truy cập vào metadata
const left = exercise.options?.filter(o => o.metadata?.side === "left") ?? [];
const right = exercise.options?.filter(o => o.metadata?.side === "right") ?? [];

    // ---- INIT ORDER FROM SUBMISSION (HEADER-BASED) ----
    const initialOrder = submission?.userAnswerJson
        ? JSON.parse(submission.userAnswerJson).map((p: any) => p.right)
        : right.map(r => r.header); // <-- USE HEADER INSTEAD OF ID

    const [rightOrder, setRightOrder] = useState<string[]>(initialOrder);
    const [dragOverIndex, setDragOverIndex] = useState<number | null>(null);

    // ---- DRAG START ----
    const handleDragStart = (e: React.DragEvent<HTMLDivElement>, index: number) => {
        e.dataTransfer.setData("dragIndex", index.toString());
    };

    // ---- DRAG OVER ----
    const handleDragOver = (e: React.DragEvent<HTMLDivElement>, index: number) => {
        e.preventDefault();
        setDragOverIndex(index);
    };

    // ---- DROP EVENT ----
    const handleDrop = (e: React.DragEvent<HTMLDivElement>, dropIndex: number) => {
        const dragIndex = Number(e.dataTransfer.getData("dragIndex"));
        const updated = [...rightOrder];
        const [dragged] = updated.splice(dragIndex, 1);
        updated.splice(dropIndex, 0, dragged);

        setRightOrder(updated);
        setDragOverIndex(null);

        // CREATE HEADER-BASED PAIRS
        const pairs = updated.map((rightHeader, i) => ({
            leftHeader: left[i]?.header ?? "",
            rightHeader: rightHeader ?? "",
        }));

        onSubmissionChange({
            exerciseId: exercise.id,
            userAnswerJson: JSON.stringify(pairs),
        });
    };

    // ---- FIND TEXT BASED ON HEADER ----
    const findRightText = (header: string) =>
        right.find(r => r.header === header)?.text ?? "";

    return (
        <div className="p-6 rounded-lg">
            <h3 className="font-medium text-xl mb-6 text-gray-800">{exercise.question}</h3>

            <div className="grid grid-cols-2 gap-6">
                {/* LEFT LIST */}
                <div className="flex flex-col">
                    {left.map((l, i) => (
                        <div
                            key={l.header}
                            className="p-3 mb-3 border rounded-lg bg-gray-100 font-semibold text-gray-900"
                        >
                            {i + 1}. {l.text}
                        </div>
                    ))}
                </div>

                {/* RIGHT DRAGGABLE LIST */}
                <div>
                    {rightOrder.map((rightHeader, i) => (
                        <div
                            key={`${rightHeader}-${i}`}
                            draggable
                            onDragStart={(e) => handleDragStart(e, i)}
                            onDragOver={(e) => handleDragOver(e, i)}
                            onDrop={(e) => handleDrop(e, i)}
                            className={`p-3 mb-3 border rounded-lg bg-white cursor-move transition-all
                                ${dragOverIndex === i ? "bg-blue-50 border-blue-500" : "hover:bg-gray-50"}
                            `}
                        >
                            {findRightText(rightHeader)}
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
