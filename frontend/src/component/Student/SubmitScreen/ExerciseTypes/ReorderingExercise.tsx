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

    // --- INITIAL FROM SUBMISSION OR FROM HEADERS ---
    const getInitialItems = () => {
        if (submission?.userAnswerJson) {
            return JSON.parse(submission.userAnswerJson);  // ["2","1","3"]
        }
        return exercise.options?.map(o => o.header!) ?? []; // ["1","2","3"]
    };

    const [items, setItems] = useState<string[]>(getInitialItems());
    const [dragOverIndex, setDragOverIndex] = useState<number | null>(null);

    useEffect(() => {
        setItems(getInitialItems());
        setDragOverIndex(null);
    }, [exercise.id]);

    // --- DRAG LOGIC ---
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

        // SEND HEADER ARRAY AS ANSWER
        onSubmissionChange({
            exerciseId: exercise.id,
            userAnswerJson: JSON.stringify(updated), // ["3", "1", "2"]
        });
    };

    // --- FIND TEXT BY HEADER ---
    const findText = (header: string) => {
        const option = exercise.options?.find(o => o.header === header);
        return option ? option.text : `Missing text (${header})`;
    };

    return (
        <div className="p-6 rounded-lg">
            <h3 className="font-medium text-xl mb-6 text-gray-800">{exercise.question}</h3>

            {items.map((header, index) => (
                <div key={header} className="flex items-center gap-4 mb-4">

                    <div className="
                        w-8 h-8 flex items-center justify-center
                        rounded-full bg-text-color text-white font-semibold
                        flex-shrink-0
                    ">
                        {index + 1}
                    </div>

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
                        {findText(header)}
                    </div>
                </div>
            ))}

            <p className="text-gray-500 text-sm">
                Notice: Drag and drop the items to reorder the steps.
            </p>
        </div>
    );
}
