import React, {useEffect, useState} from "react";
import {useParams, useNavigate} from "react-router-dom";
import {
    ChevronDownIcon,
    ChevronUpIcon,
    XCircleIcon,
    CheckCircleIcon,
} from "@heroicons/react/24/outline";
import {Bars3Icon} from "@heroicons/react/24/outline";
import {ExerciseDTO} from "../../model/ExerciseDTO";
import {MyButtonAdvanced, Spinner} from "../material/material";
import {
    useGetLearningUnitByIdQuery,
    useLazyGetLearningUnitByIdQuery,
} from "../../API/service/learningUnit.service";
import {LearningUnitDTO} from "../../model/LearningUnitDTO";
import {LearningUnitChildDto} from "../../model/LearningUnitChildDto";
import {SubmissionDTO} from "../../model/SubmissionDTO";
import {MyConfirm} from "../material/MyConfirm";
import {MyAlert} from "../material/MyAlert";
import {specialSymbols} from "../material/sympolKeyboard";
import {useStartAttemptMutation, useSubmitAttemptMutation} from "../../API/service/attempt.service";
import {AttemptResponseDTO} from "../../model/AttemptDTO";


const SubmitScreen = () => {
    const {courseId} = useParams();
    const navigate = useNavigate();
    const [selectedLesson, setSelectedLesson] = useState<LearningUnitDTO | null>(
        null,
    );
    const [selectedExercise, setSelectedExercise] = useState<ExerciseDTO | null>(
        null,
    );
    const userId = Number(localStorage.getItem("id")!);
    const courseIdNumber = Number(courseId);
    const [isMobileView, setIsMobileView] = useState(window.innerWidth < 768);
    const [showSidebar, setShowSidebar] = useState(false);

    const {data: course, isLoading: isLoadingCourse} =
        useGetLearningUnitByIdQuery(
            {userId, id: courseIdNumber},
            {
                skip: !courseId,
            },
        );

    useEffect(() => {
        const handleResize = () => {
            setIsMobileView(window.innerWidth < 768);
            if (window.innerWidth >= 768) {
                setShowSidebar(false);
            }
        };
        window.addEventListener("resize", handleResize);
        return () => window.removeEventListener("resize", handleResize);
    }, []);

    const chapters = course?.childUnits || [];
    const [fetchUnit] = useLazyGetLearningUnitByIdQuery();

    useEffect(() => {
        const loadFirstUnit = async () => {
            if (chapters.length > 0 && userId) {
                const firstChapter = chapters[0];
                const res = await fetchUnit({userId, id: firstChapter.id}).unwrap();
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

    const [submissions, setSubmissions] = useState<Record<number, SubmissionDTO>>(
        {},
    );
    const [attemptId, setAttemptId] = useState<number | null>(null);

    const [submitAttempt, { isLoading: isSubmitting }] = useSubmitAttemptMutation();
    const [attemptResult, setAttemptResult] = useState<AttemptResponseDTO |null >(null);

    const handleSubmit = async () => {
        if (!selectedLesson || !userId || !attemptId) return;

        try {
            if (!selectedLesson.exercises || selectedLesson.exercises.length === 0) {
                showAlert("No Exercises", "No exercises found in this lesson");
                return;
            }

            const completeSubmissions = selectedLesson.exercises.map((exercise) => {
                const existingSubmission = submissions[exercise.id];
                return {
                    exerciseId: exercise.id,
                    selectedOptionId: existingSubmission?.selectedOptionId ?? null,
                    answer: existingSubmission?.answer ?? null,
                    userId: userId,
                    learningUnitId: selectedLesson.id,
                    timestamp: new Date().toISOString(),
                };
            });

            // Kiểm tra có câu nào đã trả lời
            const hasAnswers = completeSubmissions.some(
                (sub) =>
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

            const allAnswered = selectedLesson.exercises.every((exercise) => {
                const submission = submissions[exercise.id];
                return (
                    (submission?.selectedOptionId !== null &&
                        submission?.selectedOptionId !== undefined) ||
                    (submission?.answer !== null &&
                        submission?.answer !== undefined &&
                        submission.answer !== "")
                );
            });

            const submitAndHandleResponse = async () => {
                const response = await submitAttempt({
                    attemptId,
                    submissions: completeSubmissions,
                }).unwrap();

                setAttemptResult(response);
            };

            if (!allAnswered) {
                showConfirm(
                    "Warning !!",
                    "You haven't answered all questions. Are you sure you want to submit?",
                    submitAndHandleResponse
                );
            } else {
                showConfirm(
                    "Confirm Submission",
                    "You are about to submit your answers. Are you sure?",
                    submitAndHandleResponse
                );
            }
        } catch (error) {
            showAlert("Submission Failed", "Failed to submit answers. Please try again.");
        }
    };

    const [alertState, setAlertState] = useState({
        show: false,
        title: "",
        message: "",
        onClose: () => {
        }, // Optional callback
    });

    const [confirmState, setConfirmState] = useState({
        show: false,
        title: "",
        message: "",
        onConfirm: () => {
        }, // Action when confirmed
    });

    const showAlert = (title: string, message: string, onClose?: () => void) => {
        setAlertState({
            show: true,
            title,
            message,
            onClose: onClose || (() => {
            }),
        });
    };

    const showConfirm = (
        title: string,
        message: string,
        onConfirm: () => void,
    ) => {
        setConfirmState({
            show: true,
            title,
            message,
            onConfirm,
        });
    };

    const handleLessonChange = async (lessonId: number) => {
        const userId = Number(localStorage.getItem("id"));
        if (!userId || isNaN(userId)) return; // Optional safety check

        const hasActualAnswers = selectedLesson?.exercises?.some((exercise) => {
            const submission = submissions[exercise.id];
            return (
                (submission?.selectedOptionId !== null &&
                    submission?.selectedOptionId !== undefined) ||
                (submission?.answer !== null &&
                    submission?.answer !== undefined &&
                    submission.answer.trim() !== "")
            );
        });

        const fetchAndSetLesson = async () => {
            const res = await startAttempt({ userId, lessonId }).unwrap();

            if (res) {
                setAttemptId(res.attemptId);
                setIsAttemptStarted(true);
                setElapsedTime(0);

                const lessonData = {
                    id: res.lessonId,
                    name: selectedLesson?.name || "Current Lesson",
                    exercises: res.questions,
                };

                setSelectedLesson(lessonData as LearningUnitDTO);
                setSelectedExercise(res.questions?.[0] || null);
                setClearVersion((v) => v + 1);
            }
        };


        if (hasActualAnswers) {
            showConfirm(
                "Unsaved Answers",
                "You have unsaved answers in this lesson. Are you sure you want to switch?",
                async () => {
                    const newSubmissions = {...submissions};
                    selectedLesson?.exercises?.forEach((exercise) => {
                        delete newSubmissions[exercise.id];
                    });
                    setSubmissions(newSubmissions);
                    await fetchAndSetLesson();
                },
            );
        } else {
            await fetchAndSetLesson();
        }
    };

    const [isSidebarOpen, setIsSidebarOpen] = useState(true);
    const [isAttemptStarted, setIsAttemptStarted] = useState(false);
    const [elapsedTime, setElapsedTime] = useState(0);

    const [startAttempt] = useStartAttemptMutation();


    useEffect(() => {
        let timer: any;

        if (isAttemptStarted) {
            timer = setInterval(() => {
                setElapsedTime((prev) => prev + 1);
            }, 1000);
        }

        return () => clearInterval(timer);
    }, [isAttemptStarted]);

    const formatTime = (seconds: number) => {
        const m = Math.floor(seconds / 60)
            .toString()
            .padStart(2, "0");
        const s = (seconds % 60).toString().padStart(2, "0");
        return `${m}:${s}`;
    };

    const [clearVersion, setClearVersion] = useState(0);

    const handleClearAll = () => {
        if (!selectedLesson) return;

        showConfirm("Clear Answers", "Are you sure you want to clear all answers?", () => {
            setSubmissions({});
            setSelectedExercise(null); // ⬅️ reset bài hiện tại
            setClearVersion((v) => v + 1); // ⬅️ trigger re-render toàn bộ
        });
    };



  if (isMobileView) {
        return (
            <div className="h-screen flex flex-col bg-light-background overflow-hidden">
                {/* Mobile Header */}
                <div className="flex justify-between items-center bg-text-color p-4 text-white">
                    <button onClick={() => setShowSidebar(!showSidebar)}>
                        <Bars3Icon className="w-6 h-6"/>
                    </button>
                    <h1 className="text-xl font-semibold">
                        {isLoadingCourse ? <Spinner/> : course?.name}
                    </h1>
                    <button
                        onClick={() => {
                            showConfirm(
                                "Leave this lesson?",
                                "Are you sure you want to leave? Your progress may not be saved.",
                                () => navigate("/my-courses"),
                            );
                        }}
                    >
                        <XCircleIcon className="w-6 h-6 text-red-300"/>
                    </button>
                </div>

                {/* Mobile Sidebar (conditionally shown) */}
                {showSidebar && (
                    <div className="absolute inset-0 z-50 bg-white">
                        <div className="p-4 overflow-y-auto h-full">
                            <h2 className="text-lg font-bold mb-4">Chapters</h2>
                            {chapters.map((topic: LearningUnitChildDto, i) => (
                                <TopicCardDropdown
                                    key={topic.id}
                                    index={i + 1}
                                    name={topic.name}
                                    childrenId={topic.id}
                                    selectedLessonId={selectedLesson?.id ?? null}
                                    onLessonClick={(id) => {
                                        handleLessonChange(id);
                                        setShowSidebar(false);
                                    }}
                                    numberOfComplete={topic.numberOfComplete}
                                    numberOfExercise={topic.numberOfExercise}
                                />
                            ))}
                        </div>
                    </div>
                )}

                {/* Mobile Main Content */}
                <div className="flex-1 flex flex-col overflow-hidden">
                    {/* Exercise Navigation (horizontal scroll) */}
                    {selectedLesson?.exercises?.length ? (
                        <div className="p-2 bg-white overflow-x-auto whitespace-nowrap">
                            {selectedLesson.exercises.map((exe, i) => (
                                <button
                                    key={exe.id}
                                    onClick={() => handleExerciseChange(exe)}
                                    className={`inline-flex items-center justify-center mx-1 w-10 h-10 rounded-full border-2
                                        ${selectedExercise?.id === exe.id ? "border-text-color bg-light-background" : "border-gray-300"}
                                        ${submissions[exe.id] ? "border-green-400" : ""}`}
                                >
                  <span
                      className={`font-medium ${submissions[exe.id] ? "text-green-500" : "text-gray-700"}`}
                  >
                    {i + 1}
                  </span>
                                    {submissions[exe.id] && (
                                        <span className="absolute -bottom-1 -right-1 text-green-500 text-xs">
                      ✓
                    </span>
                                    )}
                                </button>
                            ))}
                        </div>
                    ) : (
                        <div className="p-2 bg-white text-center text-gray-500">
                            No exercises available
                        </div>
                    )}

                    {/* Exercise Content */}
                    <div className="flex-1 overflow-y-auto bg-white p-4">
                        {selectedExercise ? (
                            <ExerciseCard
                                exercise={selectedExercise}
                                submission={submissions[selectedExercise.id]}
                                onSubmissionChange={(submission) => {
                                    setSubmissions((prev) => ({
                                        ...prev,
                                        [submission.exerciseId]: submission,
                                    }));
                                }}
                            />
                        ) : (
                            <div className="h-full flex items-center justify-center">
                                <p className="text-gray-500">
                                    {selectedLesson?.exercises?.length === 0
                                        ? "No exercises available"
                                        : "Please select an exercise"}
                                </p>
                            </div>
                        )}
                    </div>

                    {/* Mobile Footer Buttons */}
                    <div className="bg-white p-3 border-t flex justify-between">
                        <MyButtonAdvanced
                            size="small"
                            color="danger"
                            onClick={handleClearAll}
                            disabled={Object.keys(submissions).length === 0}
                        >
                            Clear All
                        </MyButtonAdvanced>
                        <MyButtonAdvanced
                            size="small"
                            color="primary"
                            onClick={handleSubmit}
                            disabled={Object.keys(submissions).length === 0}
                        >
                            Submit
                        </MyButtonAdvanced>
                    </div>
                </div>

                {/* Alert and Confirm Dialogs (same as before) */}
                {alertState.show && (
                    <MyAlert
                        title={alertState.title}
                        content={alertState.message}
                        onClose={() => {
                            setAlertState((prev) => ({...prev, show: false}));
                            alertState.onClose();
                        }}
                    />
                )}
                {confirmState.show && (
                    <MyConfirm
                        title={confirmState.title}
                        content={confirmState.message}
                        onCancel={() =>
                            setConfirmState((prev) => ({...prev, show: false}))
                        }
                        onConfirm={() => {
                            confirmState.onConfirm();
                            setConfirmState((prev) => ({...prev, show: false}));
                        }}
                    />
                )}
            </div>
        );
    }

    if (attemptResult) {
        return (
            <AttemptResult
                result={attemptResult}
                onBack={() => {
                    if (course?.id) {
                        navigate(`/topics/${course.id}`);
                    }
                }}
            />
        );
    }

    return (
        <div className="h-screen flex bg-light-background">
            <div className="h-screen flex bg-light-background relative overflow-hidden">
                <div className={`fixed top-0 left-0 z-40 h-full w-[28vw] min-w-[320px] transition-transform duration-500 ease-in-out ${isSidebarOpen ? "translate-x-0" : "-translate-x-full"}`}>
                    <div className="bg-white h-full rounded-r-xl flex flex-col shadow-2xl">
                        {/* Header */}
                        <div className="flex justify-between items-center text-white bg-text-color py-3 px-5 rounded-tr-xl">
                            <div className="flex-1">
                                {isLoadingCourse ? <Spinner /> : (
                                    <>
                                        <h1 className="text-lg font-semibold text-white">{course?.code}</h1>
                                        <h1 className="text-2xl font-semibold text-white">{course?.name}</h1>
                                    </>
                                )}
                            </div>
                            <button
                                onClick={() => setIsSidebarOpen(false)}
                                className="hover:opacity-80 transition"
                            >
                                <XCircleIcon className="w-7 h-7 text-red-400 hover:text-white" />
                            </button>
                        </div>

                        {/* Nội dung chapters */}
                        <div className="mt-3 p-3 overflow-y-auto flex-1 overflow-x-hidden
                      [scrollbar-width:none] [-ms-overflow-style:none] [&::-webkit-scrollbar]:hidden">
                            {chapters.map((topic: LearningUnitChildDto, i) => (
                                <TopicCardDropdown
                                    key={topic.id}
                                    index={i + 1}
                                    name={topic.name}
                                    childrenId={topic.id}
                                    selectedLessonId={selectedLesson?.id ?? null}
                                    onLessonClick={(id) => {
                                        handleLessonChange(id);
                                        setIsSidebarOpen(false);
                                    }}
                                    numberOfComplete={topic.numberOfComplete}
                                    numberOfExercise={topic.numberOfExercise}
                                />
                            ))}
                        </div>

                        <button
                            onClick={ () => navigate("/my-courses")}
                            className="text-sm w-[100px] px-6 py-3 bg-text-color text-white rounded-xl font-semibold hover:bg-text-color/90 transition ml-[72%] mb-4"
                        >
                            Back
                        </button>
                    </div>
                </div>

                <div className="w-[28vw] min-w-[320px] h-full bg-transparent flex-shrink-0 relative">
                        <div className="h-[98vh] absolute w-[28vw] top-2 left-1 z-10 flex flex-col items-start gap-4 bg-white rounded-2xl">
                            <div className="flex w-[99%] justify-between items-center text-white bg-text-color py-3 px-5 rounded-t-2xl">
                                <div className="flex-1">
                                    {isLoadingCourse ? <Spinner /> : (
                                        <>
                                            <h1 className="text-lg font-semibold text-white">{course?.code || "Course"}</h1>
                                            <h1 className="text-2xl font-semibold text-white">{selectedLesson?.name || "Lesson"}</h1>
                                        </>
                                    )}
                                </div>
                                <button
                                    onClick={() => setIsSidebarOpen(true)}
                                    className="text-white rounded-lg p-2 hover:opacity-80 transition"
                                >
                                    <Bars3Icon className="w-7 h-7" />
                                </button>
                            </div>
                            <p className="text-xl text-text-color ml-3 font-semibold">List of exercise</p>
                            <div className="flex flex-wrap gap-1 overflow-y-auto max-h-[80%]">
                                {selectedLesson?.exercises?.map((exe, i) => (
                                    <IndexExerciseButton
                                        key={`${exe.id}-${clearVersion}`}
                                        index={i + 1}
                                        isDone={
                                            !!submissions[exe.id]?.selectedOptionId ||
                                            (!!submissions[exe.id]?.answer )
                                        }
                                        isActive={selectedExercise?.id === exe.id}
                                        onClick={() => handleExerciseChange(exe)}
                                    />
                                ))}
                            </div>
                        </div>
                </div>
            </div>

          <div className="flex-1 m-[8px] h-[98vh] flex flex-col transition-all duration-300 ">
                {selectedLesson ? (
                    selectedLesson.exercises && selectedLesson.exercises.length > 0 ? (
                        <>
                            <div
                                className="mb-2 bg-white rounded-xl flex justify-between items-center px-5 py-3 shadow-sm border border-gray-200">
                                {/* Left: Timer */}
                                <div className="flex items-center gap-2 text-gray-700">

                                    <svg
                                        xmlns="http://www.w3.org/2000/svg"
                                        className="w-5 h-5 text-text-color"
                                        fill="none"
                                        viewBox="0 0 24 24"
                                        stroke="currentColor"
                                    >
                                        <path
                                            strokeLinecap="round"
                                            strokeLinejoin="round"
                                            strokeWidth={2}
                                            d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
                                        />
                                    </svg>
                                    <span className="font-semibold">
      {formatTime(elapsedTime)} {/* gọi hàm formatTime ở dưới */}
    </span>
                                </div>

                                {/* Right: Tool buttons */}
                                <div className="flex items-center gap-3">
                                    <button
                                        onClick={handleClearAll}
                                        disabled={Object.keys(submissions).length === 0}
                                        className="px-3 py-2 text-sm font-semibold text-red-600 border border-red-600 rounded-lg hover:bg-red-50 transition disabled:opacity-50"
                                    >
                                        Clear All
                                    </button>

                                    <button
                                        onClick={handleSubmit}
                                        disabled={isSubmitting || Object.keys(submissions).length === 0}
                                        className="px-4 py-2 text-sm font-semibold text-white bg-text-color rounded-lg hover:bg-text-color/90 transition disabled:opacity-50"
                                    >
                                        {isSubmitting ? "Submitting..." : "Submit"}
                                    </button>
                                </div>
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

                <div className="h-[90vh] bg-white rounded-xl">
                    {selectedLesson ? (
                        selectedExercise ? (
                            <div>
                                <ExerciseCard
                                    key={`${selectedExercise.id}-${clearVersion}`}
                                    exercise={selectedExercise}
                                    submission={submissions[selectedExercise.id]}
                                    onSubmissionChange={(submission) => {
                                        setSubmissions((prev) => ({
                                            ...prev,
                                            [submission.exerciseId]: { ...submission },
                                        }));
                                    }}
                                />
                            </div>
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
            {/* Alert Dialog */}
            {alertState.show && (
                <MyAlert
                    title={alertState.title}
                    content={alertState.message}
                    onClose={() => {
                        setAlertState((prev) => ({...prev, show: false}));
                        alertState.onClose();
                    }}
                />
            )}

            {/* Confirm Dialog */}
            {confirmState.show && (
                <MyConfirm
                    title={confirmState.title}
                    content={confirmState.message}
                    onCancel={() => setConfirmState((prev) => ({...prev, show: false}))}
                    onConfirm={() => {
                        confirmState.onConfirm();
                        setConfirmState((prev) => ({...prev, show: false}));
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
                                 onSubmissionChange,
                             }: {
    exercise: ExerciseDTO;
    submission?: SubmissionDTO;
    onSubmissionChange: (submission: SubmissionDTO) => void;
}) => {
    const [selectedOptionId, setSelectedOptionId] = useState<number | null>(
        submission?.selectedOptionId ?? null
    );
    const [shortAnswer, setShortAnswer] = useState(submission?.answer ?? "");
    const [showSymbols, setShowSymbols] = useState(false);
    const textareaRef = React.useRef<HTMLTextAreaElement>(null);

    const handleInsertSymbol = (symbol: string) => {
        if (!textareaRef.current) return;
        const textarea = textareaRef.current;
        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        const newValue = shortAnswer.substring(0, start) + symbol + shortAnswer.substring(end);
        setShortAnswer(newValue);
        onSubmissionChange({
            exerciseId: exercise.id,
            selectedOptionId: null,
            answer: newValue,
        });
        setTimeout(() => {
            textarea.focus();
            textarea.selectionStart = textarea.selectionEnd = start + symbol.length;
        }, 0);
    };

    const handleOptionChange = (optionId: number) => {
        setSelectedOptionId(optionId);
        onSubmissionChange({
            exerciseId: exercise.id,
            selectedOptionId: optionId,
            answer: "",
        });
    };

    useEffect(() => {
        setSelectedOptionId(submission?.selectedOptionId ?? null);
        setShortAnswer(submission?.answer ?? "");
    }, [submission, exercise.id]);

    return (
        <div className="p-6 rounded-lg">
            <h3 className="font-medium text-xl mb-4 text-gray-800">{exercise.question}</h3>
            { exercise.options?.length != 0 ? (
                <div className="space-y-3 mb-6">
                    {exercise?.options?.map((option) => (
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
                  ${selectedOptionId === option.id ? "bg-blue-50" : "hover:bg-gray-50"}`}
                            >
                                <OptionIndicator isSelected={selectedOptionId === option.id} />
                                <span className="text-gray-700">{option.text}</span>
                            </label>
                        </div>
                    ))}
                </div>
            ) : (
                <div>
    <textarea
        ref={textareaRef}
        value={shortAnswer}
        onChange={(e) => setShortAnswer(e.target.value)} // ✅ local only
        onBlur={() =>
            onSubmissionChange({
                exerciseId: exercise.id,
                selectedOptionId: null,
                answer: shortAnswer,
            })
        } // ✅ sync khi blur
        className="w-full p-3 border border-gray-300 rounded-lg"
        rows={4}
        placeholder="Type your answer here..."
    />

                    <div className="mt-3">
                        <button
                            type="button"
                            onClick={() => setShowSymbols(!showSymbols)}
                            className="px-3 py-2 border rounded-lg bg-gray-100 hover:bg-gray-200 text-sm"
                        >
                            {showSymbols ? "Hide" : "Show special symbols"}
                        </button>
                    </div>

                    {showSymbols && (
                        <div className="space-y-4 mt-4">
                            {specialSymbols.map((group) => (
                                <div key={group.group}>
                                    <h3 className="font-semibold text-gray-700 mb-2">{group.group}</h3>
                                    <div className="flex flex-wrap gap-2">
                                        {group.symbols.map((s) => (
                                            <button
                                                key={s.value}
                                                onClick={() => handleInsertSymbol(s.value)}
                                                title={s.tooltip}
                                                type="button"
                                                className="px-3 py-2 border rounded-lg hover:bg-gray-100"
                                            >
                                                {s.label}
                                            </button>
                                        ))}
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

const OptionIndicator = ({isSelected}: { isSelected: boolean }) => (
    <div
        className={`relative flex-shrink-0 w-5 h-5 rounded-full border-2 transition-all duration-200
      ${isSelected ? "border-blue-500" : "border-gray-300"}`}
    >
        {isSelected && (
            <div className="absolute inset-1 rounded-full bg-blue-500 scale-100 opacity-100"/>
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
                               numberOfExercise,
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
            const res = await fetchUnit({userId, id: childrenId}).unwrap();
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
                        <span
                            className={
                                numberOfComplete !== numberOfExercise
                                    ? "text-yellow-500"
                                    : "text-green-500"
                            }
                        >
              ({numberOfComplete} / {numberOfExercise})
            </span>
                    </h3>
                    {numberOfComplete === numberOfExercise && (
                        <svg
                            className="w-4 h-4 text-green-500"
                            viewBox="0 0 20 20"
                            fill="currentColor"
                        >
                            <path
                                fillRule="evenodd"
                                d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                                clipRule="evenodd"
                            />
                        </svg>
                    )}
                </div>
                {isOpen ? (
                    <ChevronUpIcon className="w-5 h-5 text-text-color transition-transform"/>
                ) : (
                    <ChevronDownIcon className="w-5 h-5 text-gray-500 transition-transform"/>
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
            className={`p-3 mb-2 ml-2 rounded-lg cursor-pointer ${isSelected ? "bg-text-color" : "hover:bg-light-background"}`}
            onClick={onClick}
        >
            <h3
                className={`font-medium ${isSelected ? "text-white" : "text-text-color"}`}
            >
                Lesson {index}: {name}
                {isSelected ? (
                    ` (${numberOfComplete} / ${numberOfExercise})`
                ) : (
                    <span
                        className={
                            numberOfComplete !== numberOfExercise
                                ? "text-yellow-500"
                                : "text-green-500"
                        }
                    >
            {" "}
                        ({numberOfComplete} / {numberOfExercise})
          </span>
                )}
            </h3>
        </div>
    );
};
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
                    isDone
                        ? "text-blue-500"
                        : isActive
                            ? "text-text-color"
                            : "text-gray-700"
                }`}
            >
                {index}
            </h1>

            {isDone && (
                <div className="absolute bottom-1 right-1">
                    <svg
                        className="w-5 h-5 text-blue-500"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                    >
                        <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth={3}
                            d="M5 13l4 4L19 7"
                        />
                    </svg>
                </div>
            )}
        </button>
    );
};

const AttemptResult = ({ result, onBack }: { result: any; onBack: () => void }) => {
    return (
        <div className="p-8 bg-white rounded-2xl shadow-lg border border-gray-200 max-w-4xl mx-auto mt-10">
            {/* Header */}
            <div className="flex justify-between items-center mb-8">
                <div>
                    <h1 className="text-3xl font-bold text-gray-800">Lesson Result</h1>
                    <p className="text-gray-500 mt-1">
                        Submitted at: {new Date(result.submittedAt).toLocaleString()}
                    </p>
                </div>

                <div className="text-right">
                    <h2
                        className={`text-2xl font-bold ${
                            result.status === "PASSED" ? "text-green-600" : "text-red-500"
                        }`}
                    >
                        {result.status}
                    </h2>
                    <p className="text-gray-700 text-lg font-semibold">
                        Score:{" "}
                        <span className="text-text-color font-bold">
              {result.score?.toFixed(1)}
            </span>
                    </p>
                </div>
            </div>

            <hr className="mb-6" />

            {/* Feedback list */}
            <div className="space-y-6">
                {result.feedbacks.map((fb, i) => (
                    <div
                        key={fb.exerciseId}
                        className={`p-5 rounded-xl border transition-all duration-200 ${
                            fb.correct
                                ? "bg-green-50 border-green-200"
                                : "bg-red-50 border-red-200"
                        }`}
                    >
                        <div className="flex items-start justify-between">
                            <div className="flex-1">
                                <h3 className="text-lg font-semibold text-gray-800 mb-1">
                                    Question {i + 1}:
                                </h3>
                                <p className="text-gray-700 mb-3">{fb.question}</p>

                                <p className="text-sm">
                                    <span className="font-medium text-gray-600">Your answer:</span>{" "}
                                    <span
                                        className={`font-semibold ${
                                            fb.correct ? "text-green-600" : "text-red-500"
                                        }`}
                                    >
                    {fb.userAnswer || "—"}
                  </span>
                                </p>

                                {fb.expectedAnswer && (
                                    <p className="text-sm mt-1">
                    <span className="font-medium text-gray-600">
                      Correct answer:
                    </span>{" "}
                                        <span className="text-gray-800 font-semibold">
                      {fb.expectedAnswer}
                    </span>
                                    </p>
                                )}
                            </div>

                            <div className="ml-4">
                                {fb.correct ? (
                                    <CheckCircleIcon className="w-8 h-8 text-green-500" />
                                ) : (
                                    <XCircleIcon className="w-8 h-8 text-red-500" />
                                )}
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            {/* Footer buttons */}
            <div className="flex justify-end mt-8">
                <button
                    onClick={onBack}
                    className="px-6 py-3 bg-text-color text-white rounded-xl font-semibold hover:bg-text-color/90 transition"
                >
                    Back to Lesson
                </button>
            </div>
        </div>
    );
};

