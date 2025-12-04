import {AttemptResponseDTO} from "../../../../model/AttemptDTO";
import {CheckCircleIcon, XCircleIcon} from "@heroicons/react/24/outline";
import React from "react";
import MyButton from "../../../material/material";

const formatListArray = (arr: any[]) => arr.map(item => `${item}`);

const formatAnswer = (
    answer: string | null,
    type: string,
    isExpected: boolean = false
): string | string[] => {

    if (!answer) return "—";

    let parsed: any = answer;

    try {
        parsed = JSON.parse(answer);
    } catch {
        return answer;
    }

    if (isExpected) {
        if (parsed && typeof parsed === "object" && parsed.correctAnswers !== undefined) {
            const ca = parsed.correctAnswers;
            return Array.isArray(ca) ? formatListArray(ca) : [`${String(ca)}`];
        }
    }


    switch (type) {
        case "FILL_IN_THE_BLANK":
        case "MULTIPLE_CHOICE":
        case "SELECT_MULTIPLE":
        case "TRUE_FALSE":
        case "REORDERING":
            return Array.isArray(parsed) ? formatListArray(parsed) : [`${String(parsed)}`];

        case "MATCHING":
            return Array.isArray(parsed)
                ? formatListArray(parsed.map((p: any) => `${p.leftId} → ${p.rightId}`))
                : [`• ${JSON.stringify(parsed)}`];

        case "SHORT_ANSWER":
            return typeof parsed === "string" ? parsed : JSON.stringify(parsed);

        default:
            return typeof parsed === "object" ? JSON.stringify(parsed) : String(parsed);
    }
};


export const AttemptResult = ({result, onBack}: { result: AttemptResponseDTO; onBack: () => void }) => {
    return (
        <div className="p-8 bg-white rounded-2xl shadow-lg border border-gray-200 max-w-4xl mx-auto mt-10">

            {/* Header */}
            <div className="flex justify-between items-center mb-8">
                <div>
                    <h1 className="text-3xl font-bold text-text-color">Lesson Result</h1>
                    <p className="text-gray-500 mt-1">
                        Submitted at: {new Date(result.submittedAt).toLocaleString()}
                    </p>
                </div>

                <div className="text-right">
                    <p className="text-gray-700 text-2xl font-semibold">
                        Score: <span className="text-text-color font-bold">{result.score?.toFixed(1)}</span>
                    </p>
                </div>
            </div>

            <hr className="mb-6"/>

            {/* Feedback list */}
            <div className="space-y-6">
                {result.feedbacks.map((fb, i) => {

                    //  Tính correct dựa trên score >= 50
                    const isCorrect = (fb.score ?? 0) >= 50;

                    return (
                        <div
                            key={fb.exerciseId}
                            className={`p-5 rounded-xl border transition-all duration-200 ${
                                isCorrect ? "bg-green-50 border-green-200" : "bg-red-50 border-red-200"
                            }`}
                        >
                            <div className="flex items-start justify-between">
                                <div className="flex-1 ">
                                    <div className="flex mb-3 text-[18px]">
                                        <p className=" font-semibold text-gray-800 mr-3">Question {i + 1}:</p>
                                        <p className=" text-gray-700 ">{fb.question}</p>
                                    </div>

                                    {/* Score for each question */}
                                    <p className="text-gray-600 text-[15px] mb-2">
                                        Score:{" "}
                                        <span className="font-semibold">{fb.score}</span>
                                    </p>

                                    <div className="text-[16px]">
<span className="font-medium text-gray-600">
    {fb.exerciseType === "TRUE_FALSE"
        ? "You choose these statements are true:"
        : "Your answer:"}
</span>
                                        <div
                                            className={`text-[16px] font-semibold ${isCorrect ? "text-green-600" : "text-red-500"}`}>
                                            {(() => {
                                                const userAns = formatAnswer(fb.userAnswer, fb.exerciseType);
                                                return Array.isArray(userAns)
                                                    ? userAns.map((line, idx) => <div key={idx}>{line}</div>)
                                                    : userAns;
                                            })()}
                                        </div>
                                    </div>

                                    {fb.expectedAnswer && (
                                        <div className="text-[16px] mt-1">
                                            <span className="font-medium text-gray-600">Correct answer:</span>{" "}
                                            <div className="text-gray-800 font-semibold">
                                                {(() => {
                                                    const expectedAns = formatAnswer(fb.expectedAnswer, fb.exerciseType, true);
                                                    return Array.isArray(expectedAns)
                                                        ? expectedAns.map((line, idx) => <div key={idx}>{line}</div>)
                                                        : expectedAns;
                                                })()}
                                            </div>
                                        </div>
                                    )}

                                </div>

                                <div className="ml-4">
                                    {isCorrect ? (
                                        <CheckCircleIcon className="w-8 h-8 text-green-500"/>
                                    ) : (
                                        <XCircleIcon className="w-8 h-8 text-red-500"/>
                                    )}
                                </div>
                            </div>
                        </div>
                    );
                })}
            </div>

            <div className="flex justify-end mt-8">
                <MyButton onClick={() => window.location.reload()} text="Back to Lesson"/>
            </div>
        </div>
    );
};
