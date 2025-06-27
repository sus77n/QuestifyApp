export interface ExerciseDTO {
    id: number;
    question: string;
    answer: string;
    type: 'Multiple-choice' | 'Constructed-response';
    createdAt: string;
    updatedAt: string;
}