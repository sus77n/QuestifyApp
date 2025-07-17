import {LearningUnitChildDto} from "./LearningUnitChildDto";
import {UserDTO} from "./UserDTO";
import {LearningUnitType} from "./LearningUnitType";
import {ExerciseDTO} from "./ExerciseDTO";

export interface LearningUnitDTO {
    id: number;
    name: string;
    code: string;
    description: string;
    type: LearningUnitType;
    status: number;
    createdAt: Date;
    createdBy: UserDTO;
    childUnits?: LearningUnitChildDto[];
    exercises?: ExerciseDTO[];
    parentId: number;
    numberOfExercise: number;
}

export interface CourseDTO {
    id: number,
    name: string,
    code: string;
    totalOfExercise: number,
    completedExercises: number
}