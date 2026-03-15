import React, {useState} from "react";
import {LearningUnitChildDto} from "../../../../model/LearningUnitChildDto";
import {useLazyGetLearningUnitByIdQuery} from "../../../../API/service/learningUnit.service";
import {ChevronDownIcon, ChevronUpIcon} from "@heroicons/react/24/outline";
import {LessonCard} from "./LessonCard";

export const TopicCardDropdown = ({
    index,
    name,
    childrenId,
    onLessonClick,
    onTitleClick,
    selectedLessonId,
    numberOfComplete,
    numberOfExercise,
}: {
    index: number;
    name: string;
    childrenId: string;
    onLessonClick: (lessonId: string) => void;
    onTitleClick?: () => void;
    selectedLessonId: string | null;
    numberOfComplete: number | null;
    numberOfExercise: number | null;
}) => {
    const [isOpen, setIsOpen] = useState(false);
    const [lessons, setLessons] = useState<LearningUnitChildDto[]>([]);
    const [fetchUnit] = useLazyGetLearningUnitByIdQuery();

    const handleClickRow = async () => {
        if (onTitleClick) {
            onTitleClick();
        }

        setIsOpen(!isOpen);
        const userId = localStorage.getItem("id");
        if (!userId) return;

        if (!lessons.length && !isOpen) {
            const res = await fetchUnit({ userId, id: childrenId }).unwrap();
            if (res?.children?.length) setLessons(res.children);
        }
    };

    return (
        <div className="bg-white rounded-xl border-2 border-text-color mb-2">
            <div 
                className={`flex justify-between items-center p-3 rounded-xl cursor-pointer ${selectedLessonId === childrenId ? 'bg-gray-100' : ''}`} 
                onClick={handleClickRow}
            >
                <div className="flex items-center gap-2">
                    <h3 className="font-medium text-text-color">
                        Chapter {index} : {name}{" "}
                        <span className={numberOfComplete !== numberOfExercise ? "text-yellow-500" : "text-green-500"}>
                            ({numberOfComplete} / {numberOfExercise})
                        </span>
                    </h3>
                    {numberOfComplete === numberOfExercise && numberOfExercise! > 0 && (
                        <svg className="w-4 h-4 text-green-500" viewBox="0 0 20 20" fill="currentColor">
                            <path
                                fillRule="evenodd"
                                d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                                clipRule="evenodd"
                            />
                        </svg>
                    )}
                </div>
                {isOpen ? <ChevronUpIcon className="w-5 h-5 text-text-color" /> : <ChevronDownIcon className="w-5 h-5 text-gray-500" />}
            </div>

            {isOpen && (
                <div className="px-4 pb-4 pt-2 border-t-2 border-text-color">
                    {lessons.length > 0 ? (
                        lessons.map((lesson, i) => (
                            <LessonCard
                                key={lesson.id}
                                index={i + 1}
                                name={lesson.name}
                                isSelected={selectedLessonId === lesson.id}
                                onClick={() => onLessonClick(lesson.id)}
                                numberOfComplete={lesson.numberOfComplete}
                                numberOfExercise={lesson.numberOfExercise}
                            />
                        ))
                    ) : (
                        <p className="text-sm text-gray-500">No lessons found</p>
                    )}
                </div>
            )}
        </div>
    );
};