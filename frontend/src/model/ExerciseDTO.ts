import { OptionDTO } from "./OptionDTO";

export interface ExerciseDTO {
  id: number;
  question: string;
  correctAnswers: string;
  type: ExerciseType;
  createdAt: string;
  updatedAt: string;
  options?: OptionDTO[];
}

export type ExerciseType =
    | "MULTIPLE_CHOICE"
    | "SELECT_MULTIPLE"
    | "TRUE_FALSE"
    | "SHORT_ANSWER"
    | "MATCHING"
    | "REORDERING"
    | "FILL_IN_THE_BLANK";
