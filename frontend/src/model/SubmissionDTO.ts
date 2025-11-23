export interface SubmissionDTO {
  id?: number;
  exerciseId: number;
  userAnswerJson?: string | null;
  score?: number;
}