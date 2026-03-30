export interface ProgressDTO {
  id: string;
  attemptId: string;
  userFullName: string;
  courseId: string;
  courseName: string;
  courseCode: string;
  attemptCount: number;
  completedExercises: number;
  totalExercises: number;
  status: string;
  progressPercent: number;
  bestScore: number;
  lastActivityAt: string;
}