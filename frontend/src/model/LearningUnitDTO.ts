import { LearningUnitChildDto } from "./LearningUnitChildDto";
import { LearningUnitType } from "./LearningUnitType";
import { ExerciseDTO } from "./ExerciseDTO";
import {LessonConfigDTO} from "./LessonConfigDTO";

export interface LearningUnitDTO {
  id: string;
  name: string;
  code: string;
  description: string;
  type: LearningUnitType;
  status: number;
  createdAt: Date;
  createdBy: string;
  updatedAt?: string;
  children?: LearningUnitChildDto[];
  exerciseCategories?: LearningUnitChildDto[];
  exercises?: ExerciseDTO[];
  lessonConfig?: LessonConfigDTO;
  parentId: string;
  numberOfComplete: number;
  numberOfExercise: number;
}

export interface CourseDTO {
  id: string;
  name: string;
  code: string;
  status: string;
  description: string;
  createdAt?: string;
  updatedAt?: string;
  numberOfComplete: number;
  numberOfExercise: number;
}

export interface LearningUnitWithChildren {
  id: string;
  name: string;
  code: string;
  description: string;
  type: LearningUnitType;
  status: number;
  createdAt: Date;
  createdBy: string;
  children: LearningUnitWithChildren[];
  numberOfExercises: number;
  updatedAt?: string;
  parentId: string;
}

export interface LearningUnitWithLessonConfig{
  id: string;
  name: string;
  code: string;
  description: string;
  type: LearningUnitType;
  status: number;
  createdAt: Date;
  createdBy: string;
  updatedAt?: string;
  exerciseCategories?: LearningUnitChildDto[];
  lessonConfig?: LessonConfigDTO;
  parentId: string;
}