import {useNavigate, useParams} from "react-router-dom";
import React, {useEffect, useState} from "react";

import {LearningUnitDTO} from "../../../model/LearningUnitDTO";
import {ExerciseDTO} from "../../../model/ExerciseDTO";
import {useGetLearningUnitByIdQuery, useLazyGetLearningUnitByIdQuery} from "../../../API/service/learningUnit.service";
import {useStartAttemptMutation, useSubmitAttemptMutation} from "../../../API/service/attempt.service";
import {AttemptResponseDTO} from "../../../model/AttemptDTO";
import {buildSubmission, isSubmissionAnswered} from "../../../utils/userAnswerJsonUtils";

import MobileView from "./MobileView";
import DesktopView from "./DesktopView";

type LocalSubmission = {
    exerciseId: string;
    userAnswerJson: string;
};

const SubmitScreen = () => {
    const { courseId } = useParams();
    const navigate = useNavigate();

    const [selectedLesson, setSelectedLesson] = useState<LearningUnitDTO | null>(null);
    const [selectedExercise, setSelectedExercise] = useState<ExerciseDTO | null>(null);

    const userId = localStorage.getItem("id")!;
    const courseIdNumber = courseId!;

    const [isMobileView, setIsMobileView] = useState(window.innerWidth < 768);
    const [showSidebar, setShowSidebar] = useState(false);

    const { data: course, isLoading: isLoadingCourse } = useGetLearningUnitByIdQuery(
        { userId, id: courseIdNumber },
        { skip: !courseId }
    );


    const chapters = course?.children || [];
    console.log("chapter", chapters);

    const [fetchUnit] = useLazyGetLearningUnitByIdQuery();

    useEffect(() => {
        const handleResize = () => {
            setIsMobileView(window.innerWidth < 768);
            if (window.innerWidth >= 768) setShowSidebar(false);
        };
        window.addEventListener("resize", handleResize);
        return () => window.removeEventListener("resize", handleResize);
    }, []);

    useEffect(() => {
        const loadFirstUnit = async () => {
            if (chapters.length > 0 && userId) {
                const firstChapter = chapters[0];
                const res = await fetchUnit({ userId, id: firstChapter.id }).unwrap();
                console.log("res", res)
                if (res) {
                    setSelectedLesson(res);
                    if (res.exercises?.length) setSelectedExercise(res.exercises[0]);
                }
            }
        };
        loadFirstUnit();
    }, [chapters, fetchUnit, userId]);

    const handleExerciseChange = (exercise: ExerciseDTO) => setSelectedExercise(exercise);

    const [submissions, setSubmissions] = useState<Record<string, LocalSubmission>>({});
    const [attemptId, setAttemptId] = useState<string | null>(null);

    const [submitAttempt, { isLoading: isSubmitting }] = useSubmitAttemptMutation();
    const [attemptResult, setAttemptResult] = useState<AttemptResponseDTO | null>(null);

    const [alertState, setAlertState] = useState({
        show: false,
        title: "",
        message: "",
        onClose: () => {},
    });

    const [confirmState, setConfirmState] = useState({
        show: false,
        title: "",
        message: "",
        onConfirm: () => {},
    });

    const showAlert = (title: string, message: string, onClose?: () => void) => {
        setAlertState({
            show: true,
            title,
            message,
            onClose: onClose || (() => {}),
        });
    };

    const showConfirm = (title: string, message: string, onConfirm: () => void) => {
        setConfirmState({
            show: true,
            title,
            message,
            onConfirm,
        });
    };

    const [isSidebarOpen, setIsSidebarOpen] = useState(true);
    const [isAttemptStarted, setIsAttemptStarted] = useState(false);
    const [elapsedTime, setElapsedTime] = useState(0);

    const [startAttempt] = useStartAttemptMutation();

    useEffect(() => {
        let timer: any;
        if (isAttemptStarted) {
            timer = setInterval(() => setElapsedTime((prev) => prev + 1), 1000);
        }
        return () => clearInterval(timer);
    }, [isAttemptStarted]);

    const formatTime = (seconds: number) => {
        const m = Math.floor(seconds / 60).toString().padStart(2, "0");
        const s = (seconds % 60).toString().padStart(2, "0");
        return `${m}:${s}`;
    };

    const [clearVersion, setClearVersion] = useState(0);

    const handleClearAll = () => {
        if (!selectedLesson) return;
        showConfirm("Clear Answers", "Are you sure you want to clear all answers?", () => {
            setSubmissions({});
            setSelectedExercise(null);
            setClearVersion((v) => v + 1);
        });
    };

    const handleLessonChange = async (lessonId: string) => {
        const userId =localStorage.getItem("id")|| "";
        if (!userId ) return;

        const hasActualAnswers = selectedLesson?.exercises?.some((exercise) =>
            isSubmissionAnswered(submissions[exercise.id])
        );

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
                setSubmissions({});
            }
        };

        if (hasActualAnswers) {
            showConfirm(
                "Unsaved Answers",
                "You have unsaved answers in this lesson. Are you sure you want to switch?",
                async () => {
                    const newSubmissions = { ...submissions };
                    selectedLesson?.exercises?.forEach((exercise) => {
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

    const handleSubmit = async () => {
        if (!selectedLesson || !userId || !attemptId) return;

        try {
            if (!selectedLesson.exercises || selectedLesson.exercises.length === 0) {
                showAlert("No Exercises", "No exercises found in this lesson");
                return;
            }

            const completeSubmissions = selectedLesson.exercises.map((exercise) =>
                buildSubmission(exercise.id, submissions[exercise.id])
            );

            const hasAnswers = completeSubmissions.some((sub) => isSubmissionAnswered(sub as any));
            if (!hasAnswers) {
                showAlert("Submission Required", "Please answer at least one question before submitting");
                return;
            }

            const allAnswered = selectedLesson.exercises.every((exercise) =>
                isSubmissionAnswered(submissions[exercise.id])
            );

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
                showConfirm("Confirm Submission", "You are about to submit your answers. Are you sure?", submitAndHandleResponse);
            }
        } catch {
            showAlert("Submission Failed", "Failed to submit answers. Please try again.");
        }
    };

    const commonProps = {
        course,
        chapters,
        selectedLesson,
        selectedExercise,
        submissions,
        attemptResult,
        isLoadingCourse,
        isSubmitting,
        alertState,
        confirmState,
        showSidebar,
        navigate,
        clearVersion,
        elapsedTime,
        isSidebarOpen,
        formatTime,

        // MUST ADD
        showAlert,
        showConfirm,

        // handlers
        handleSubmit,
        handleClearAll,
        handleLessonChange,
        handleExerciseChange,

        // setters
        setShowSidebar,
        setSubmissions,
        setIsSidebarOpen
    };

    return isMobileView ? (
        <MobileView {...commonProps} />
    ) : (
        <DesktopView {...commonProps} />
    );
};

export default SubmitScreen;
