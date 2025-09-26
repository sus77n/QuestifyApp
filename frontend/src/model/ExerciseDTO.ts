import { OptionDTO } from "./OptionDTO";

export interface ExerciseDTO {
  id: number;
  question: string;
  answer: string;
  type: "MULTIPLE_CHOICE" | "SHORT_ANSWER" | "ESSAY";
  createdAt: string;
  updatedAt: string;
  options?: OptionDTO[];
}
