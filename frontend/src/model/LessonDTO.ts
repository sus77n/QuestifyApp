import {ExerciseDTO} from "./ExerciseDTO";

export interface LessonDTO {
    id: number;
    title: string;
    chapter_id: number;
    exercises: ExerciseDTO[];
}