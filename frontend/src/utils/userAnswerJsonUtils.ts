import { SubmissionDTO } from "../model/SubmissionDTO";
export const getUserAnswerJson = (submission?: SubmissionDTO): any => {
    if (!submission || !submission.userAnswerJson) return null;
    try {
        return JSON.parse(submission.userAnswerJson);
    } catch {
        return null;
    }
};


export const isSubmissionAnswered = (submission?: SubmissionDTO): boolean => {
    if (!submission || !submission.userAnswerJson) return false;
    const parsed = getUserAnswerJson(submission);

    if (parsed === null || parsed === undefined) return false;

    if (Array.isArray(parsed)) {
        return parsed.length > 0;
    }

    if (typeof parsed === "string") {
        return parsed.trim() !== "";
    }

    if (typeof parsed === "object") {
        return Object.keys(parsed).length > 0;
    }

    return !!parsed;
};


export const buildSubmission = (
    exerciseId: string,
    submission?: { userAnswerJson: string }
): SubmissionDTO => ({
    exerciseId,
    userAnswerJson: submission?.userAnswerJson ?? "null",
});

