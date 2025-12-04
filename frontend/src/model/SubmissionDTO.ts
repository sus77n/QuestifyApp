export interface SubmissionDTO {
  id?: string;
  exerciseId: string;
  userAnswerJson?: string | null;
  score?: number;
}