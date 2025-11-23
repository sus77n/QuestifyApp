// DesktopView.tsx
import React from "react";
import { Bars3Icon, XCircleIcon } from "@heroicons/react/24/outline";
import { Spinner } from "../../material/material";
import { TopicCardDropdown } from "./OtherComponents/TopicCardDropdown";
import { AttemptResult } from "./OtherComponents/AttemptResult";
import ExerciseRenderer from "./OtherComponents/ExerciseRenderer";
import { MyAlert } from "../../material/MyAlert";
import { MyConfirm } from "../../material/MyConfirm";
import { IndexExerciseButton } from "./OtherComponents/IndexExerciseButton";
import { LearningUnitChildDto } from "../../../model/LearningUnitChildDto";
import { isSubmissionAnswered } from "../../../utils/userAnswerJsonUtils";
import {SubmitCommonProps} from "./SubmitCommonProps";
import {ChevronRightIcon} from "@heroicons/react/24/solid";
import {ChevronLeftIcon} from "@heroicons/react/16/solid";
const DesktopView = ({
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
                         handleSubmit,
                         handleClearAll,
                         handleLessonChange,
                         handleExerciseChange,
                         setSubmissions,
                         setShowSidebar,
                         setIsSidebarOpen,
                         showAlert,
                         showConfirm
                     }: SubmitCommonProps) => {
    if (attemptResult) {
        return (
            <AttemptResult
                result={attemptResult}
                onBack={() => {
                    if (course?.id) navigate(`/topics/${course.id}`);
                }}
            />
        );
    }

    return (
        <div className="h-screen flex bg-light-background">
            <div className="h-screen flex bg-light-background relative overflow-hidden">
                {isSidebarOpen && (
                    <div
                        className="fixed inset-0 bg-gray-800/40 z-30 flex items-center justify-center text-white text-xl font-semibold"
                        onClick={() => setIsSidebarOpen(false)}
                    >
                        <p className="ml-[30%] pointer-events-none"> Select a lesson to begin</p>
                    </div>
                )}

                {/* LEFT SIDEBAR (slide-in) */}
                <div
                    className={`fixed top-0 left-0 z-40 h-full w-[28vw] min-w-[320px] transition-transform duration-500 ease-in-out ${
                        isSidebarOpen ? "translate-x-0" : "-translate-x-full"
                    }`}
                >
                    <div className="bg-white h-full rounded-r-xl flex flex-col shadow-2xl">
                        {/* Header */}
                        <div className="flex justify-between items-center text-white bg-text-color py-3 px-5 rounded-tr-xl">
                            <div className="flex-1">
                                {isLoadingCourse ? (
                                    <Spinner />
                                ) : (
                                    <>
                                        <h1 className="text-lg font-semibold text-white">{course?.code}</h1>
                                        <h1 className="text-2xl font-semibold text-white">{course?.name}</h1>
                                    </>
                                )}
                            </div>
                            <button onClick={() => setIsSidebarOpen(false)} className="hover:opacity-80 transition">
                                <XCircleIcon className="w-7 h-7 text-red-400 hover:text-white" />
                            </button>
                        </div>

                        {/* Chapters → Lessons */}
                        <div
                            className="mt-3 p-3 overflow-y-auto flex-1 overflow-x-hidden
                            [scrollbar-width:none] [-ms-overflow-style:none] [&::-webkit-scrollbar]:hidden"
                        >
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
                            onClick={() => navigate("/my-courses")}
                            className="text-sm w-[100px] px-6 py-3 bg-text-color text-white rounded-xl font-semibold hover:bg-text-color/90 transition ml-[72%] mb-4"
                        >
                            Back
                        </button>
                    </div>
                </div>

                {/* STATIC LEFT PANEL */}
                <div className="w-[28vw] min-w-[320px] h-full bg-transparent flex-shrink-0 relative">
                    <div className="h-[98vh] absolute w-[28vw] top-2 left-1 z-10 flex flex-col items-start gap-4 bg-white rounded-2xl">
                        <div className="flex w-[99%] justify-between items-center text-white bg-text-color py-3 px-5 rounded-t-2xl">
                            <div className="flex-1">
                                {isLoadingCourse ? (
                                    <Spinner />
                                ) : (
                                    <>
                                        <h1 className="text-lg font-semibold text-white">{course?.code || "Course"}</h1>
                                        <h1 className="text-2xl font-semibold text-white">
                                            {selectedLesson?.name || "Lesson"}
                                        </h1>
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
                            {selectedLesson?.exercises?.map((exe:any, i:any) => (
                                <IndexExerciseButton
                                    key={`${exe.id}-${clearVersion}`}
                                    index={i + 1}
                                    isDone={isSubmissionAnswered(submissions[exe.id])}
                                    isActive={selectedExercise?.id === exe.id}
                                    onClick={() => handleExerciseChange(exe)}
                                />
                            ))}
                        </div>
                    </div>
                </div>
            </div>

            {/* RIGHT PANEL */}
            <div className="flex-1 m-[8px] h-[98vh] flex flex-col transition-all duration-300 ">
                {selectedLesson ? (
                    selectedLesson.exercises && selectedLesson.exercises.length > 0 ? (
                        <div className="mb-2 bg-white rounded-xl flex justify-between items-center px-5 py-3 shadow-sm border border-gray-200">
                            {/* Timer */}
                            <div className="flex items-center gap-2 text-gray-700">
                                    <svg
                                        xmlns="http://www.w3.org/2000/svg"
                                        className="w-5 h-5 text-text-color"
                                        fill="none"
                                        viewBox="0 0 24 24"
                                        stroke="currentColor"
                                    >
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                                    </svg>
                                    <span className="font-semibold">{formatTime(elapsedTime)}</span>
                            </div>



                            {/* Actions */}
                            <div className="flex items-center justify-between gap-3">
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
                        </div>
                    ) : (
                        <div className="mb-2 h-[70px] bg-white items-center justify-center rounded-xl flex"></div>
                    )
                ) : (
                    <div className="flex-1 flex items-center justify-center bg-white rounded-xl">
                        <p className="text-xl text-gray-500">Select a lesson</p>
                    </div>
                )}

                {/* Exercise content */}
                <div className="h-[90vh] bg-white rounded-xl">
                    {selectedLesson ? (
                        selectedExercise ? (
                            <div>
                            <ExerciseRenderer
                                exercise={selectedExercise}
                                submission={submissions[selectedExercise.id]}
                                onSubmissionChange={(s) =>
                                    setSubmissions((prev:any) => ({ ...prev, [s.exerciseId]: s }))
                                }
                            />
                            <div className="flex items-center justify-end gap-3 pr-6">
                                <button
                                    onClick={() => {
                                        const list = selectedLesson?.exercises;
                                        if (!list) return;

                                        const currentIndex = list.findIndex(ex => ex.id === selectedExercise?.id);
                                        if (currentIndex > 0) {
                                            handleExerciseChange(list[currentIndex - 1]);
                                        }
                                    }}
                                    className="px-3 py-2 text-sm border border-text-color text-text-color rounded-lg flex items-center gap-2"
                                >
                                    <ChevronLeftIcon className="w-4 h-4" />
                                    Previous
                                </button>

                                <button
                                    onClick={() => {
                                        const list = selectedLesson?.exercises;
                                        if (!list) return;

                                        const currentIndex = list.findIndex(ex => ex.id === selectedExercise?.id);

                                        if (currentIndex < list.length - 1) {
                                            handleExerciseChange(list[currentIndex + 1]);
                                        }
                                    }}
                                    className="px-3 py-2 text-sm border border-text-color text-text-color rounded-lg flex items-center gap-2"
                                >
                                    Next
                                    <ChevronRightIcon className="w-4 h-4" />

                                </button>
                            </div>
                            </div>
                        ) : (
                            <div className="h-full flex items-center justify-center">
                                <p className="text-gray-500">
                                    {selectedLesson.exercises?.length === 0 ? "No exercises available" : ""}
                                </p>
                            </div>
                        )
                    ) : (
                        <div className="h-full flex items-center justify-center"></div>
                    )}
                </div>
            </div>

            {/* Alerts */}
            {alertState.show && (
                <MyAlert
                    title={alertState.title}
                    content={alertState.message}
                    onClose={() => {
                        alertState.onClose();
                        alertState.show = false;
                    }}
                />
            )}

            {confirmState.show && (
                <MyConfirm
                    title={confirmState.title}
                    content={confirmState.message}
                    onCancel={() => (confirmState.show = false)}
                    onConfirm={() => {
                        confirmState.onConfirm();
                        confirmState.show = false;
                    }}
                />
            )}
        </div>
    );
};

export default DesktopView;
