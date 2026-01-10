import {LearningUnitDTO} from "./LearningUnitDTO";

export interface LearningUnitChildDto {
  id: string;
  name: string;
  type: string;
  numberOfComplete: number;
  numberOfExercise: number;
  children: LearningUnitDTO[];

}
