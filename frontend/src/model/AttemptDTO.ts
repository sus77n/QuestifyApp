import {ExerciseDTO, ExerciseType} from "./ExerciseDTO";

export interface AttemptDTO {
    id: number;
    userId: number;
    lessonId: number;
    startTime: string;
    endTime?: string;
}

export interface AttemptStartResponseDTO {
    attemptId: number;
    attemptNo: number;
    startTime: string;
    questions: ExerciseDTO[];
    lessonId: number;
}

//feedback on an exercise
export interface AttemptChildDto{
    exerciseId: number;
    exerciseType: ExerciseType;
    question: string;
    correct: boolean;
    userAnswer: string;
    expectedAnswer: string;
    score: number;
}

export interface AttemptResponseDTO {
    attemptId: number;
    score: number;
    feedback?: string;
    feedbacks: AttemptChildDto[];
    submittedAt: string;
}