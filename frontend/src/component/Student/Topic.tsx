import React, {useEffect, useState} from 'react';
import {useNavigate, useParams} from "react-router-dom";
import {ChevronDownIcon, ChevronUpIcon, XCircleIcon} from "@heroicons/react/24/outline";
import {useGetExerciseOptionsQuery} from "../../API/service/exercise.service";
import {OptionDTO} from "../../model/OptionDTO";
import {ExerciseDTO} from "../../model/ExerciseDTO";
import {useGetChaptersByCourseQuery, useGetCourseByIdQuery} from "../../API/service/course.service";
import {LessonDTO} from "../../model/LessonDTO";
import {MyButton} from "../material/material";
import {useSubmitAnswerMutation} from "../../API/service/submission.service";

const Topic = () => {
    const {courseId} = useParams();
    const navigate = useNavigate();
    const [selectedLesson, setSelectedLesson] = useState<LessonDTO | null>(null);
    const [selectedExercise, setSelectedExercise] = useState<ExerciseDTO | null>(null);

    const {data: course, isLoading: isLoadingCourse} = useGetCourseByIdQuery(Number(courseId), {
        skip: !courseId,
    });

    const {data: chapters = [], isLoading, isError} = useGetChaptersByCourseQuery(Number(courseId), {
        skip: !courseId,
    });
    

    useEffect(() => {
        if (chapters.length > 0) {
            const firstChapter = chapters[0];
            if (firstChapter.lessons?.length > 0) {
                const firstLesson = firstChapter.lessons[0];
                setSelectedLesson(firstLesson);
                if (firstLesson.exercises?.length > 0) {
                    setSelectedExercise(firstLesson.exercises[0]);
                } else {
                    console.log("No exercises in first lesson");
                }
            }
        }
    }, [chapters]);

    const handleLessonClick = (lesson: LessonDTO) => {
        setSelectedLesson(lesson);
        setSelectedExercise(lesson.exercises?.length > 0 ? lesson.exercises[0] : null);
    };

    if (isLoading) {
        return <div>Loading...</div>;
    }

    if (isError) {
        return <div>Error loading chapters</div>;
    }

    return (
        <div className="h-screen flex bg-light-background">
            <div className="m-[8px] bg-white h-[98vh] w-[28vw] rounded-xl flex flex-col p-4">
                <div className="flex justify-between align-middle mb-5">
                    <div className="flex-1">
                        {isLoadingCourse ? (
                            <p>Loading course data...</p>
                        ) : (
                            <>
                                <h1 className="text-lg font-semibold text-gray-500">{course?.code}</h1>
                                <h1 className="text-2xl font-semibold text-text-color">{course?.name}</h1>
                            </>
                        )}
                    </div>
                    <button
                        onClick={() => navigate('/my-courses')}
                        className="mb-4 p-1 rounded-full transition-all duration-200 hover:bg-red-50 hover:shadow-sm"
                    >
                        <XCircleIcon
                            className="w-8 h-8 text-red-500 hover:text-red-600 transition-colors duration-200 transform hover:scale-105"/>
                    </button>
                </div>
                <div
                    className="mt-3 overflow-y-auto flex-1 overflow-x-hidden [scrollbar-width:none] [-ms-overflow-style:none] [&::-webkit-scrollbar]:hidden">
                    {chapters.map((topic, i) => (
                        <TopicCardDropdown key={topic.id} index={i + 1} name={topic.title}>
                            {topic.lessons
                                .map((lesson, i) => (
                                    <LessonCard
                                        key={lesson.id}
                                        index={i + 1}
                                        name={lesson.title}
                                        isSelected={selectedLesson?.id === lesson.id}
                                        onClick={() => handleLessonClick(lesson)}
                                    >
                                    </LessonCard>
                                ))}
                        </TopicCardDropdown>
                    ))}
                </div>
            </div>
            <div className="m-[8px] ml-1 h-[98vh] w-[70vw] flex flex-col ">
                {selectedLesson ? (
                    <div className="h-[10vh] mb-2 bg-white rounded-xl flex">
                        {selectedLesson.exercises.map((exe, i) => (
                            <IndexExerciseButton
                                key={exe.id}
                                index={i + 1}
                                isDone={false}
                                isActive={selectedExercise?.id === exe.id}
                                onClick={() => setSelectedExercise(exe)}
                            />
                        ))}
                    </div>
                ) : (
                    <div className="flex-1 flex items-center justify-center bg-white rounded-xl">
                        <div className="text-xl text-gray-500">
                            Select a lesson to begin
                        </div>
                    </div>
                )}
                <div className="h-[90vh] bg-white rounded-xl">
                    {selectedLesson ? (
                        selectedExercise ? (
                            <ExerciseCard exercise={selectedExercise}/>
                        ) : (
                            <div className="h-full flex items-center justify-center">
                                <p className="text-gray-500">
                                    {selectedLesson.exercises?.length === 0
                                        ? "No exercises available"
                                        : "Please select an exercise"}
                                </p>
                            </div>
                        )
                    ) : (
                        <div className="h-full flex items-center justify-center">
                            <p className="text-gray-500">Select a lesson to begin</p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};
export default Topic;
const TopicCardDropdown = ({
                               index,
                               name,
                               children,
                           }: {
    index: number;
    name: string;
    children: React.ReactNode;
}) => {
    const [isOpen, setIsOpen] = useState(false);

    return (
        <div className="bg-white rounded-xl border-2 border-text-color mb-2">
            <div
                className="flex justify-between items-center p-3 rounded-xl cursor-pointer "
                onClick={() => setIsOpen(!isOpen)}
            >
                <div className="flex items-center">
                    <h3 className="font-medium text-text-color">Chapter {index} : {name}</h3>
                </div>
                {isOpen ? (
                    <ChevronUpIcon className="w-5 h-5 text-gray-500 transition-transform"/>
                ) : (
                    <ChevronDownIcon className="w-5 h-5 text-gray-500 transition-transform"/>
                )}
            </div>

            <div className={`px-4 pb-4 ${isOpen ? 'block' : 'hidden'}`}>
                <div className="pt-2 border-t-2 border-text-color">
                    {children}
                </div>
            </div>
        </div>
    );
}

const LessonCard = ({
                        index,
                        name,
                        isSelected,
                        onClick,
                    }: {
    index: number;
    name: string;
    isSelected: boolean;
    onClick: () => void;
}) => {
    return (
        <div
            className={`p-3 mb-2 ml-2 rounded-lg cursor-pointer ${isSelected ? 'bg-text-color' : 'hover:bg-light-background'}`}
            onClick={onClick}
        >
            <h3 className={`font-medium text-text-color ${isSelected ? 'text-white' : 'text-text-color'}`}>Lesson {index}: {name}</h3>
        </div>
    );
}

const IndexExerciseButton = ({
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
                border-2 ${isDone ? 'border-green-400' :
                isActive ? 'border-background-color' : 'border-gray-300'}
                ${isActive ? 'bg-light-background' : 'bg-white'}
                transition-all duration-200
                hover:border-blue-300 hover:bg-blue-50
                active:scale-95
                focus:outline-none focus:ring-2 focus:ring-blue-200`}
            onClick={onClick}
        >
            <h1 className={`font-bold text-2xl ${
                isDone ? 'text-green-500' :
                    isActive ? 'text-text-color' : 'text-gray-700'
            }`}>
                {index}
            </h1>

            {isDone && (
                <div className="absolute bottom-1 right-1">
                    <svg className="w-5 h-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="M5 13l4 4L19 7"/>
                    </svg>
                </div>
            )}
        </button>
    );
}

export const ExerciseCard = ({ exercise }: { exercise?: ExerciseDTO }) => {
    if (!exercise) {
        return <div>No exercise data available</div>;
    }

    const [selectedOption, setSelectedOption] = useState<number | null>(null);
    const [constructedResponse, setConstructedResponse] = useState('');
    const [isSubmitted, setIsSubmitted] = useState(false);
    const [isCorrect, setIsCorrect] = useState(false);
    const [isLoadingSubmit, setIsLoadingSubmit] = useState(false);

    const { data: options = [], isLoading, isError } = useGetExerciseOptionsQuery(
        exercise.id.toString(),
        {
            selectFromResult: ({ data, ...rest }) => ({
                data: data as OptionDTO[] | undefined,
                ...rest,
            }),
            skip: exercise.type !== 'Multiple-choice'
        }
    );

    const [submitAnswer] = useSubmitAnswerMutation();

    const handleSubmit = async () => {
        if (isSubmitted) {
            // Reset the exercise
            setSelectedOption(null);
            setConstructedResponse('');
            setIsSubmitted(false);
            setIsCorrect(false);
            return;
        }

        setIsLoadingSubmit(true);
        try {
            let response;

            if (exercise.type === 'Multiple-choice') {
                if (selectedOption === null) return;
                response = await submitAnswer({
                    exerciseId: exercise.id,
                    optionId: selectedOption,
                    userId: 20, // Replace with actual user ID from your auth system
                    text: '' // Empty for multiple-choice
                }).unwrap();
            } else {
                if (!constructedResponse.trim()) return;
                response = await submitAnswer({
                    exerciseId: exercise.id,
                    optionId: 0, // 0 or null for constructed response
                    userId: 1, // Replace with actual user ID from your auth system
                    text: constructedResponse
                }).unwrap();
            }

            // Assuming the response is the submission ID (as per your service)
            // You might need to adjust this based on your actual API response
            // If your backend returns more details, you can update the state accordingly
            setIsCorrect(true); // You might need to get this from the actual response
            setIsSubmitted(true);
        } catch (error) {
            console.error('Error submitting answer:', error);
            setIsCorrect(false);
            setIsSubmitted(true);
        } finally {
            setIsLoadingSubmit(false);
        }
    };

    const isSubmitDisabled = exercise.type === 'Multiple-choice'
        ? selectedOption === null
        : !constructedResponse.trim();

    if (isLoading && exercise.type === 'Multiple-choice') {
        return <div>Loading options...</div>;
    }
    if (isError && exercise.type === 'Multiple-choice') {
        return <div>Error loading options</div>;
    }

    return (
        <div className="p-6 border border-gray-200 rounded-lg shadow-sm">
            <h3 className="font-medium text-xl mb-4 text-gray-800">{exercise.question}</h3>

            {exercise.type === 'Multiple-choice' ? (
                <div className="space-y-3 mb-6">
                    {options.map((option) => (
                        <div key={option.id} className="flex items-center">
                            <input
                                type="radio"
                                id={`ex-${exercise.id}-opt-${option.id}`}
                                name={`exercise-${exercise.id}`}
                                checked={selectedOption === option.id}
                                onChange={() => !isSubmitted && setSelectedOption(option.id)}
                                className="hidden"
                                disabled={isSubmitted}
                            />
                            <label
                                htmlFor={`ex-${exercise.id}-opt-${option.id}`}
                                className={`flex items-center space-x-3 cursor-pointer w-full py-2 px-3 rounded-lg transition-all duration-200
                                    ${selectedOption === option.id ? 'bg-blue-50' : 'hover:bg-gray-50'}
                                    ${isSubmitted && selectedOption === option.id && isCorrect ? 'bg-green-50' : ''}
                                    ${isSubmitted && selectedOption === option.id && !isCorrect ? 'bg-red-50' : ''}`}
                            >
                                <OptionIndicator
                                    isSelected={selectedOption === option.id}
                                    isSubmitted={isSubmitted}
                                    isCorrect={isSubmitted && selectedOption === option.id && isCorrect}
                                    showCorrect={isSubmitted && selectedOption === option.id}
                                />
                                <span className={`text-gray-700
                                    ${selectedOption === option.id ? 'font-medium text-text-color' : ''}
                                    ${isSubmitted && selectedOption === option.id && isCorrect ? 'text-green-700' : ''}
                                    ${isSubmitted && selectedOption === option.id && !isCorrect ? 'text-red-700' : ''}`}>
                                    {option.content}
                                </span>
                            </label>
                        </div>
                    ))}
                </div>
            ) : (
                <div className="mb-6">
                    <textarea
                        value={constructedResponse}
                        onChange={(e) => !isSubmitted && setConstructedResponse(e.target.value)}
                        className="w-full p-3 border border-gray-300 rounded-lgr"
                        rows={4}
                        placeholder="Type your answer here..."
                        disabled={isSubmitted}
                    />
                    {isSubmitted && (
                        <div className={`mt-3 p-3 rounded-lg ${isCorrect ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'}`}>
                            {isCorrect ? 'Correct!' : 'Incorrect. Try again!'}
                        </div>
                    )}
                </div>
            )}

            <MyButton
                onClick={handleSubmit}
                disabled={(!isSubmitted && isSubmitDisabled) || isLoadingSubmit}
                isLoading={isLoadingSubmit}
            >
                {isSubmitted ? 'Try Again' : 'Submit'}
            </MyButton>
        </div>
    );
};
const OptionIndicator = ({
                             isSelected,
                             isSubmitted,
                             isCorrect,
                             showCorrect
                         }: {
    isSelected: boolean;
    isSubmitted: boolean;
    isCorrect: boolean;
    showCorrect: boolean;
}) => (
    <div className={`relative flex-shrink-0 w-5 h-5 rounded-full border-2 
    ${isSelected ? 'border-text-color' : 'border-gray-300'}
    ${isSubmitted && isSelected && !isCorrect ? 'border-red-500' : ''}
    ${showCorrect && isCorrect ? 'border-green-500' : ''}
    transition-all duration-200`}>
        {isSelected && (
            <div className={`absolute inset-1 rounded-full
        ${isSubmitted ? (isCorrect ? 'bg-green-500' : 'bg-red-500') : 'bg-text-color'}
        transform scale-100 opacity-100
        transition-all duration-200`}
            />
        )}
    </div>
);
