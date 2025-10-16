export interface AttemptDTO {
    id: number;
    userId: number;
    lessonId: number;
    startTime: string;
    endTime?: string;
}

export interface AttemptStartResponseDTO {
    attemptId: number;
    startTime: string;
}

export interface AttemptResponseDTO {
    attemptId: number;
    score: number;
    feedback?: string;
}