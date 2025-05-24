import {OptionDTO} from "./OptionDTO";

export interface ExerciseDTO {
    id: string;
    question: string;
    answer: string;
    type: 'Multiple-choice' | 'Constructed-response';
    createdAt: string;
    updatedAt: string;
    options: OptionDTO[];
}