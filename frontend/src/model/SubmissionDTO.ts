export interface SubmissionDTO {
    id?: number;
    exerciseId: number;
    userId?: number;
    answer?: string;
    submittedAt?: Date | string;
    selectedOptionId?: number;
    score?: number;
}