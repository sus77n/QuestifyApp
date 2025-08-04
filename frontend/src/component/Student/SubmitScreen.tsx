import React, {useEffect, useState} from 'react';
import { useParams} from "react-router-dom";
import {ChevronDownIcon, ChevronUpIcon, XCircleIcon} from "@heroicons/react/24/outline";
import {useGetExerciseOptionsQuery} from "../../API/service/exercise.service";
import {ExerciseDTO} from "../../model/ExerciseDTO";
import {MyButtonAdvanced, Spinner} from "../material/material";
import {useGetLearningUnitByIdQuery, useLazyGetLearningUnitByIdQuery} from "../../API/service/learningUnit.service";
import {LearningUnitDTO} from "../../model/LearningUnitDTO";
import {LearningUnitChildDto} from "../../model/LearningUnitChildDto";
import {SubmissionDTO} from "../../model/SubmissionDTO";
import {useSubmitByLessonMutation} from "../../API/service/submission.service";
import {MyConfirm} from "../material/MyConfirm";
import {MyAlert} from "../material/MyAlert";

const SubmitScreen = () => {
    const {courseId} = useParams();
    const [selectedLesson, setSelectedLesson] = useState<LearningUnitDTO | null>(null);
    const [selectedExercise, setSelectedExercise] = useState<ExerciseDTO | null>(null);
    const userId = Number(localStorage.getItem("id")!);
    const courseIdNumber = Number(courseId);

    const { data: course, isLoading: isLoadingCourse } = useGetLearningUnitByIdQuery(
        { userId, id: courseIdNumber },
        {
            skip: !courseId,
        }
    );

    const chapters = course?.childUnits || [];
    const [fetchUnit] = useLazyGetLearningUnitByIdQuery();

    useEffect(() => {
        const loadFirstUnit = async () => {
            if (chapters.length > 0 && userId) {
                const firstChapter = chapters[0];
                const res = await fetchUnit({ userId, id: firstChapter.id }).unwrap();
                if (res) {
                    setSelectedLesson(res);
                    if (res.exercises?.length) {
                        setSelectedExercise(res.exercises[0]);
                    }
                }
            }
        };
        loadFirstUnit();
    }, [chapters]);

    const handleExerciseChange = (exercise: ExerciseDTO) => {
        setSelectedExercise(exercise);
    };

    const [submissions, setSubmissions] = useState<Record<number, SubmissionDTO>>({});
    const [submitByLesson, { isLoading: isSubmitting }] = useSubmitByLessonMutation();

    const handleSubmit = async () => {
        if (!selectedLesson || !userId) return;

        try {
            if (!selectedLesson.exercises || selectedLesson.exercises.length === 0) {
                showAlert(
                    "No Exercises",
                    "No exercises found in this lesson"
                );
                return;
            }

            // Prepare all submissions
            const completeSubmissions = selectedLesson.exercises.map(exercise => {
                const existingSubmission = submissions[exercise.id];
                return {
                    exerciseId: exercise.id,
                    selectedOptionId: existingSubmission?.selectedOptionId ?? null,
                    answer: existingSubmission?.answer ?? null,
                    userId: userId,
                    learningUnitId: selectedLesson.id,
                    timestamp: new Date().toISOString()
                };
            });

            // Check if all questions are answered
            const allAnswered = selectedLesson.exercises.every(exercise => {
                const submission = submissions[exercise.id];
                return (
                    (submission?.selectedOptionId !== null && submission?.selectedOptionId !== undefined) ||
                    (submission?.answer !== null && submission?.answer !== undefined && submission.answer !== "")
                );
            });

            // Check if at least one question is answered
            const hasAnswers = completeSubmissions.some(sub =>
                sub.selectedOptionId !== null ||
                (sub.answer !== null && sub.answer !== "")
            );

            if (!hasAnswers) {
                showAlert(
                    "Submission Required",
                    "Please answer at least one question before submitting"
                );
                return;
            }

            const submitAndHandleResponse = async () => {
                const response = await submitByLesson(completeSubmissions).unwrap();

                showAlert(
                    "Submission Successful",
                    `Your score for this lesson is:: ${response}`,
                    () => setSubmissions({})
                );
            };

            if (!allAnswered) {
                showConfirm(
                    "Warning !!",
                    "You haven't answered all questions. Are you sure you want to submit?",
                    submitAndHandleResponse
                );
            } else {
                showConfirm(
                    "Warning !!",
                    "You are about to submit your answers. Are you sure?",
                    submitAndHandleResponse
                );
            }

        } catch (error) {
            showAlert(
                "Submission Failed",
                "Failed to submit answers. Please try again."
            );
        }
    };

    const handleClearAll = () => {
        if (!selectedLesson) return;

        showConfirm(
            "Clear Answers",
            "Are you sure you want to clear all answers in this lesson?",
            () => {
                const newSubmissions = {...submissions};
                selectedLesson.exercises?.forEach(exercise => {
                    delete newSubmissions[exercise.id];
                });
                setSubmissions(newSubmissions);
            }
        );
    };

    const [alertState, setAlertState] = useState({
        show: false,
        title: '',
        message: '',
        onClose: () => {} // Optional callback
    });

    const [confirmState, setConfirmState] = useState({
        show: false,
        title: '',
        message: '',
        onConfirm: () => {} // Action when confirmed
    });

    const showAlert = (title: string, message: string, onClose?: () => void) => {
        setAlertState({
            show: true,
            title,
            message,
            onClose: onClose || (() => {})
        });
    };

    const showConfirm = (title: string, message: string, onConfirm: () => void) => {
        setConfirmState({
            show: true,
            title,
            message,
            onConfirm
        });
    };

    const handleLessonChange = async (lessonId: number) => {
        const userId = Number(localStorage.getItem("id"));
        if (!userId || isNaN(userId)) return; // Optional safety check

        const hasActualAnswers = selectedLesson?.exercises?.some(exercise => {
            const submission = submissions[exercise.id];
            return (
                (submission?.selectedOptionId !== null && submission?.selectedOptionId !== undefined) ||
                (submission?.answer !== null && submission?.answer !== undefined && submission.answer.trim() !== "")
            );
        });

        const fetchAndSetLesson = async () => {
            const res = await fetchUnit({ userId, id: lessonId }).unwrap();
            if (res) {
                setSelectedLesson(res);
                setSelectedExercise(res.exercises?.[0] || null);
            }
        };

        if (hasActualAnswers) {
            showConfirm(
                "Unsaved Answers",
                "You have unsaved answers in this lesson. Are you sure you want to switch?",
                async () => {
                    const newSubmissions = { ...submissions };
                    selectedLesson?.exercises?.forEach(exercise => {
                        delete newSubmissions[exercise.id];
                    });
                    setSubmissions(newSubmissions);
                    await fetchAndSetLesson();
                }
            );
        } else {
            await fetchAndSetLesson();
        }
    };

    return (
        <div className="h-screen flex bg-light-background">
            <div className="m-[8px] bg-white h-[98vh] w-[28vw] rounded-xl flex flex-col overflow-y-auto">
                <div className="flex justify-between align-middle text-white bg-text-color pt-2 pb-2 pl-5 ">
                    <div className="flex-1">
                        {isLoadingCourse ? (
                            <Spinner/>
                        ) : (
                            <>
                                <h1 className="text-lg font-semibold text-white">{course?.code}</h1>
                                <h1 className="text-2xl font-semibold text-white">{course?.name}</h1>
                            </>
                        )}
                    </div>
                    <button
                        onClick={() => {
                            showConfirm(
                                "Leave this lesson?",
                                "Are you sure you want to leave? Your progress may not be saved.",
                                () => {
                                    window.location.href = '/my-courses';
                                }
                            );
                        }}
                        className="rounded-full hover:shadow-sm pr-4"
                    >
                        <XCircleIcon
                            className="w-8 h-8 text-red-500 hover:text-white transition-colors"
                        />
                    </button>
                </div>
                <div
                    className="mt-3 p-3 overflow-y-auto flex-1 overflow-x-hidden [scrollbar-width:none] [-ms-overflow-style:none] [&::-webkit-scrollbar]:hidden">
                    {chapters.map((topic: LearningUnitChildDto, i) => (
                        <TopicCardDropdown
                            key={topic.id}
                            index={i + 1}
                            name={topic.name}
                            childrenId={topic.id}
                            selectedLessonId={selectedLesson?.id ?? null}
                            onLessonClick={handleLessonChange}
                            numberOfComplete={topic.numberOfComplete}
                            numberOfExercise={topic.numberOfExercise}
                        />
                    ))}
                </div>
            </div>
            <div className="m-[8px] ml-1 h-[98vh] w-[70vw] flex flex-col ">
                {selectedLesson ? (
                    selectedLesson.exercises && selectedLesson.exercises.length > 0 ? (
                        <>
                            {/* Top bar with exercise index buttons */}
                            <div className="mb-2 bg-white rounded-xl flex">
                                {selectedLesson.exercises.map((exe, i) => (
                                    <IndexExerciseButton
                                        key={exe.id}
                                        index={i + 1}
                                        isDone={!!submissions[exe.id]}
                                        isActive={selectedExercise?.id === exe.id}
                                        onClick={() => handleExerciseChange(exe)}
                                    />
                                ))}
                            </div>

                        </>
                    ) : (
                        <div className="mb-2 h-[70px] bg-white items-center justify-center rounded-xl flex">
                            <p className="text-sm text-gray-500">Select a lesson to begin</p>
                        </div>
                    )
                ) : (
                    <div className="flex-1 flex items-center justify-center bg-white rounded-xl">
                        <p className="text-xl text-gray-500">Select a lesson</p>
                    </div>
                )}

                <div className="h-[83vh] bg-white rounded-xl">
                    {selectedLesson ? (
                        selectedExercise ? (
                            <ExerciseCard
                                key={selectedExercise.id}
                                exercise={selectedExercise}
                                submission={submissions[selectedExercise.id]}
                                onSubmissionChange={(submission) => {
                                    setSubmissions(prev => ({
                                        ...prev,
                                        [submission.exerciseId]: submission
                                    }));
                                }}
                            />
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
                <div className="h-[7vh] rounded-xl mt-2 flex justify-end">
                        <MyButtonAdvanced
                            size="small"
                            color="danger"
                            onClick={handleClearAll}
                            disabled={Object.keys(submissions).length === 0}
                        >
                            Clear All
                        </MyButtonAdvanced>
                    <div className="w-2"></div>
                        <MyButtonAdvanced
                            size="small"
                            color="primary"
                            onClick={handleSubmit}
                            disabled={isSubmitting || Object.keys(submissions).length === 0}
                            isLoading={isSubmitting}
                        >
                            Submit Answers
                        </MyButtonAdvanced>
                </div>
            </div>
            {/* Alert Dialog */}
            {alertState.show && (
                <MyAlert
                    title={alertState.title}
                    content={alertState.message}
                    onClose={() => {
                        setAlertState(prev => ({...prev, show: false}));
                        alertState.onClose();
                    }}
                />
            )}

            {/* Confirm Dialog */}
            {confirmState.show && (
                <MyConfirm
                    title={confirmState.title}
                    content={confirmState.message}
                    onCancel={() => setConfirmState(prev => ({...prev, show: false}))}
                    onConfirm={() => {
                        confirmState.onConfirm();
                        setConfirmState(prev => ({...prev, show: false}));
                    }}
                />
            )}
        </div>
    );
};
export default SubmitScreen;


export const ExerciseCard = ({
                                 exercise,
                                 submission,
                                 onSubmissionChange
                             }: {
    exercise: ExerciseDTO,
    submission?: SubmissionDTO,
    onSubmissionChange: (submission: SubmissionDTO) => void
}) => {
    const [selectedOptionId, setSelectedOptionId] = useState<number | null>(
        submission?.selectedOptionId ?? null
    );
    const [shortAnswer, setShortAnswer] = useState(
        submission?.answer ?? ""
    );

    const { data: options = []} = useGetExerciseOptionsQuery(
        exercise.id.toString(),
        {
            skip: exercise.type !== 'MULTIPLE_CHOICE',
        }
    );

    useEffect(() => {
        if (submission) {
            if (exercise.type === 'MULTIPLE_CHOICE') {
                setSelectedOptionId(submission.selectedOptionId ?? null);
            } else {
                setShortAnswer(submission.answer ?? "");
            }
        } else {
            setSelectedOptionId(null);
            setShortAnswer("");
        }
    }, [exercise.id, exercise.type, submission]);

    const handleOptionChange = (optionId: number) => {
        const newOptionId = optionId;
        setSelectedOptionId(newOptionId);
        onSubmissionChange({
            exerciseId: exercise.id,
            selectedOptionId: newOptionId,
            answer: ""
        });
    };

    const handleShortAnswerChange = (text: string) => {
        setShortAnswer(text);
        onSubmissionChange({
            exerciseId: exercise.id,
            selectedOptionId: null,
            answer: text
        });
    };

    return (
        <div className="p-6 rounded-lg">
            <h3 className="font-medium text-xl mb-4 text-gray-800">{exercise.question}</h3>

            {exercise.type === 'MULTIPLE_CHOICE' && (
                <div className="space-y-3 mb-6">
                    {options.map((option) => (
                        <div key={option.id} className="flex items-center">
                            <input
                                type="radio"
                                id={`ex-${exercise.id}-opt-${option.id}`}
                                name={`exercise-${exercise.id}`}
                                checked={selectedOptionId === option.id}
                                onChange={() => handleOptionChange(option.id)}
                                className="hidden"
                            />
                            <label
                                htmlFor={`ex-${exercise.id}-opt-${option.id}`}
                                className={`flex items-center space-x-3 cursor-pointer w-full py-2 px-3 rounded-lg transition-all duration-200
                  ${selectedOptionId === option.id ? 'bg-blue-50' : 'hover:bg-gray-50'}`}
                            >
                                <OptionIndicator isSelected={selectedOptionId === option.id} />
                                <span className="text-gray-700">{option.text}</span>
                            </label>
                        </div>
                    ))}
                </div>
            )}

            {exercise.type === 'SHORT_ANSWER' && (
                <textarea
                    value={shortAnswer}
                    onChange={(e) => handleShortAnswerChange(e.target.value)}
                    className="w-full p-3 border border-gray-300 rounded-lg"
                    rows={4}
                    placeholder="Type your answer here..."
                />
            )}
        </div>
    );
};

const OptionIndicator = ({ isSelected }: { isSelected: boolean }) => (
    <div
        className={`relative flex-shrink-0 w-5 h-5 rounded-full border-2 transition-all duration-200
      ${isSelected ? 'border-blue-500' : 'border-gray-300'}`}
    >
        {isSelected && (
            <div className="absolute inset-1 rounded-full bg-blue-500 scale-100 opacity-100" />
        )}
    </div>
);

const TopicCardDropdown = ({
                               index,
                               name,
                               childrenId,
                               onLessonClick,
                               selectedLessonId,
                               numberOfComplete,
                               numberOfExercise
                           }: {
    index: number;
    name: string;
    childrenId: number;
    onLessonClick: (lessonId: number) => void;
    selectedLessonId: number | null;
    numberOfComplete: number | null;
    numberOfExercise: number | null;
}) => {
    const [isOpen, setIsOpen] = useState(false);
    const [lessons, setLessons] = useState<LearningUnitChildDto[]>([]);
    const [fetchUnit] = useLazyGetLearningUnitByIdQuery();

    const handleToggle = async () => {
        setIsOpen(!isOpen);

        const userId = Number(localStorage.getItem("id"));
        if (!userId || isNaN(userId)) return;

        // only fetch if not already fetched
        if (!lessons.length && !isOpen) {
            const res = await fetchUnit({ userId, id: childrenId }).unwrap();
            if (res?.childUnits?.length) {
                setLessons(res.childUnits);
            }
        }
    };


    return (
        <div className="bg-white rounded-xl border-2 border-text-color mb-2">
            <div
                className="flex justify-between items-center p-3 rounded-xl cursor-pointer"
                onClick={handleToggle}
            >
                <div className="flex items-center gap-2">
                    <h3 className="font-medium text-text-color">
                        Chapter {index} : {name}
                        <span className={numberOfComplete !== numberOfExercise ? "text-yellow-500" : "text-green-500"}>
                        ({numberOfComplete} / {numberOfExercise})
                    </span>
                    </h3>
                    {numberOfComplete === numberOfExercise && (
                        <svg className="w-4 h-4 text-green-500" viewBox="0 0 20 20" fill="currentColor">
                            <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                        </svg>
                    )}
                </div>
                {isOpen ? (
                    <ChevronUpIcon className="w-5 h-5 text-text-color transition-transform" />
                ) : (
                    <ChevronDownIcon className="w-5 h-5 text-gray-500 transition-transform" />
                )}
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


const LessonCard = ({
                        index,
                        name,
                        isSelected,
                        onClick,
                        numberOfComplete,
                        numberOfExercise
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
            className={`p-3 mb-2 ml-2 rounded-lg cursor-pointer ${isSelected ? 'bg-text-color' : 'hover:bg-light-background'}`}
            onClick={onClick}
        >
            <h3 className={`font-medium ${isSelected ? 'text-white' : 'text-text-color'}`}>
                Lesson {index}: {name}
                {isSelected ? (
                    ` (${numberOfComplete} / ${numberOfExercise})`
                ) : (
                    <span className={numberOfComplete !== numberOfExercise ? "text-yellow-500" : "text-green-500"}>
      {" "}({numberOfComplete} / {numberOfExercise})
    </span>
                )}
            </h3>
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
