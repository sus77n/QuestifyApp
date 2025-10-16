import { LearningUnitChildDto } from "./LearningUnitChildDto";
import { LearningUnitType } from "./LearningUnitType";
import { ExerciseDTO } from "./ExerciseDTO";

export interface LearningUnitDTO {
  id: number;
  name: string;
  code: string;
  description: string;
  type: LearningUnitType;
  status: number;
  createdAt: Date;
  createdBy: string;
  childUnits?: LearningUnitChildDto[];
  exercises?: ExerciseDTO[];
  parentId: number;
  numberOfComplete: number;
  numberOfExercise: number;
}

export interface CourseDTO {
  id: number;
  name: string;
  code: string;
  createdAt?: string;
  totalOfExercise: number;
  completedExercises: number;
}
export interface CourseWithIndex extends LearningUnitDTO {
  index: number;
}

export interface LearningUnitWithChildren {
  id: number;
  name: string;
  code: string;
  description: string;
  type: LearningUnitType;
  status: number;
  createdAt: Date;
  createdBy: string;
  children: LearningUnitWithChildren[];
  numberOfExercises: number;
}