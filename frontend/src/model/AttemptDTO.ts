import {ExerciseDTO, ExerciseType} from "./ExerciseDTO";

export interface AttemptDTO {
    id: string;
    userId: string;
    lessonId: string;
    startTime: string;
    endTime?: string;
}

export interface AttemptStartResponseDTO {
    attemptId: string;
    attemptNo: number;
    startTime: string;
    questions: ExerciseDTO[];
    lessonId: string;
}

//feedback on an exercise
export interface AttemptChildDto{
    exerciseId: string;
    exerciseType: ExerciseType;
    question: string;
    correct: boolean;
    userAnswer: string;
    expectedAnswer: string;
    score: number;
}

export interface AttemptResponseDTO {
    attemptId: string;
    score: number;
    feedback?: string;
    feedbacks: AttemptChildDto[];
    submittedAt: string;
}