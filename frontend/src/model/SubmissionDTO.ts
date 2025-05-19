export interface SubmissionDTO {
    id: number;
    exercise_id: number;
    student_id: number;
    submission: string;
    score: number;
    submitted_at: Date | string;
}