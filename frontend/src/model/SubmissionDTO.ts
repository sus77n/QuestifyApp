export interface SubmissionDTO {
  id?: number;
  exerciseId: number;
  userId?: number;
  answer?: string | null;
  submittedAt?: Date | string;
  selectedOptionId?: number | null;
  score?: number;
}
