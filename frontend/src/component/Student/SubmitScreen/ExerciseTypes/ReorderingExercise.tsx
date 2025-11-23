import React, {useEffect, useState} from "react";
import { ExerciseDTO } from "../../../../model/ExerciseDTO";
import { SubmissionDTO } from "../../../../model/SubmissionDTO";

export default function ReorderingExercise({
                                               exercise,
                                               submission,
                                               onSubmissionChange,
                                           }: {
    exercise: ExerciseDTO;
    submission?: SubmissionDTO;
    onSubmissionChange: (s: SubmissionDTO) => void;
}) {

    const initial = submission?.userAnswerJson
        ? JSON.parse(submission.userAnswerJson)
        : exercise.options?.map(o => o.id) ?? [];

    const getInitialItems = () => {
        return submission?.userAnswerJson
            ? JSON.parse(submission.userAnswerJson)
            : exercise.options?.map(o => o.id) ?? [];
    };


    const [items, setItems] = useState<number[]>(initial);
    const [dragOverIndex, setDragOverIndex] = useState<number | null>(null);

    useEffect(() => {
        setItems(getInitialItems());
        setDragOverIndex(null);
    }, [exercise.id]);

    const handleDragStart = (e: React.DragEvent<HTMLDivElement>, index: number) => {
        e.dataTransfer.setData("dragIndex", index.toString());
    };

    const handleDragOver = (e: React.DragEvent<HTMLDivElement>, index: number) => {
        e.preventDefault();
        setDragOverIndex(index);
    };

    const handleDrop = (e: React.DragEvent<HTMLDivElement>, dropIndex: number) => {
        const dragIndex = Number(e.dataTransfer.getData("dragIndex"));

        const updated = [...items];
        const [dragged] = updated.splice(dragIndex, 1);
        updated.splice(dropIndex, 0, dragged);

        setItems(updated);
        setDragOverIndex(null);

        onSubmissionChange({
            exerciseId: exercise.id,
            userAnswerJson: JSON.stringify(updated),
        });
    };

    const findText = (id: number) => {
        const option = exercise.options?.find(o => o.id === id);
        return option ? option.text : `Missing text (${id})`;
    };


    return (
        <div className="p-6 rounded-lg">
            <h3 className="font-medium text-xl mb-6 text-gray-800">{exercise.question}</h3>

            {items.map((id, index) => (
                <div key={id} className="flex items-center gap-4 mb-4">
                    {/* Fixed number */}
                    <div className="
                        w-8 h-8 flex items-center justify-center
                        rounded-full bg-text-color text-white font-semibold
                        flex-shrink-0
                    ">
                        {index + 1}
                    </div>

                    {/* Draggable text box */}
                    <div
                        draggable
                        onDragStart={(e) => handleDragStart(e, index)}
                        onDragOver={(e) => handleDragOver(e, index)}
                        onDrop={(e) => handleDrop(e, index)}
                        className={`
                            flex-1 border rounded-lg p-3 bg-white cursor-move transition-all
                            ${dragOverIndex === index ? "bg-blue-50 border-blue-500" : "hover:bg-gray-50"}
                        `}
                    >
                        {findText(id)}
                    </div>
                </div>
            ))}

            <p className="text-gray-500 text-sm">Notice: Drag and drop the items on the right side to match the correct steps.</p>
        </div>
    );
}
