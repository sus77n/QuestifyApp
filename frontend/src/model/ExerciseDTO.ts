import {OptionDTO} from "./OptionDTO";

export interface ExerciseDTO {
    id: number;
    question: string;
    answer: string;
    type: 'Multiple Choice' | 'Short Answer';
    createdAt: string;
    updatedAt: string;
    options?: OptionDTO[];
}