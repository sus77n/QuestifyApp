import React from "react";
import { Bars3Icon, XCircleIcon } from "@heroicons/react/24/outline";
import { MyButtonAdvanced, Spinner } from "../../material/material";
import { TopicCardDropdown } from "./OtherComponents/TopicCardDropdown";
import ExerciseRenderer from "./OtherComponents/ExerciseRenderer";
import { MyAlert } from "../../material/MyAlert";
import { MyConfirm } from "../../material/MyConfirm";
import { LearningUnitChildDto } from "../../../model/LearningUnitChildDto";
import {SubmitCommonProps} from "./SubmitCommonProps";

const MobileView = ({
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
    return (
        <div className="h-screen flex flex-col bg-light-background overflow-hidden">
            {/* Mobile Header */}
            <div className="flex justify-between items-center bg-text-color p-4 text-white">
                <button onClick={() => setShowSidebar(!showSidebar)}>
                    <Bars3Icon className="w-6 h-6" />
                </button>
                <h1 className="text-xl font-semibold">
                    {isLoadingCourse ? <Spinner /> : course?.name}
                </h1>
                <button
                    onClick={() => {
                        showConfirm(
                            "Leave this lesson?",
                            "Are you sure you want to leave? Your progress may not be saved.",
                            () => navigate("/my-courses")
                        );
                    }}
                >
                    <XCircleIcon className="w-6 h-6 text-red-300" />
                </button>
            </div>

            {/* Mobile Sidebar */}
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

            {/* Mobile Main */}
            <div className="flex-1 flex flex-col overflow-hidden">
                {selectedLesson?.exercises?.length ? (
                    <div className="p-2 bg-white overflow-x-auto whitespace-nowrap">
                        {selectedLesson.exercises.map((exe:any, i:any) => (
                            <button
                                key={exe.id}
                                onClick={() => handleExerciseChange(exe)}
                                className={`inline-flex items-center justify-center mx-1 w-10 h-10 rounded-full border-2
                                    ${
                                    selectedExercise?.id === exe.id
                                        ? "border-text-color bg-light-background"
                                        : "border-gray-300"
                                }
                                    ${
                                    submissions[exe.id]?.userAnswerJson
                                        ? "border-green-400"
                                        : ""
                                }`}
                            >
                                <span
                                    className={`font-medium ${
                                        submissions[exe.id]?.userAnswerJson
                                            ? "text-green-500"
                                            : "text-gray-700"
                                    }`}
                                >
                                    {i + 1}
                                </span>
                                {submissions[exe.id]?.userAnswerJson && (
                                    <span className="absolute -bottom-1 -right-1 text-green-500 text-xs">
                                        ✓
                                    </span>
                                )}
                            </button>
                        ))}
                    </div>
                ) : (
                    <div className="p-2 bg-white text-center text-gray-500">No exercises available</div>
                )}

                {/* Content */}
                <div className="flex-1 overflow-y-auto bg-white p-4">
                    {selectedExercise ? (
                        <ExerciseRenderer
                            exercise={selectedExercise}
                            submission={submissions[selectedExercise.id]}
                            onSubmissionChange={(s) =>
                                setSubmissions((prev:any) => ({ ...prev, [s.exerciseId]: s }))
                            }
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

                {/* Footer */}
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
                        disabled={isSubmitting || Object.keys(submissions).length === 0}
                    >
                        {isSubmitting ? "Submitting..." : "Submit"}
                    </MyButtonAdvanced>
                    <div className="bg-white p-3 border-t flex justify-between">

                        <button
                            onClick={() => {
                                const list = selectedLesson?.exercises;
                                if (!list) return;

                                const currentIndex = list.findIndex(ex => ex.id === selectedExercise?.id);
                                if (currentIndex > 0) {
                                    handleExerciseChange(list[currentIndex - 1]);
                                }
                            }}
                            className="text-sm px-4 py-2 border rounded-lg"
                        >
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
                            className="text-sm px-4 py-2 border rounded-lg"
                        >
                            Next
                        </button>
                    </div>

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

export default MobileView;
