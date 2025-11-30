import { LearningUnitDTO } from "../../../model/LearningUnitDTO";
import { ExerciseDTO } from "../../../model/ExerciseDTO";
import { LearningUnitChildDto } from "../../../model/LearningUnitChildDto";

export interface SubmitCommonProps {
    course: LearningUnitDTO | undefined;
    chapters: LearningUnitChildDto[];
    selectedLesson: LearningUnitDTO | null;
    selectedExercise: ExerciseDTO | null;
    submissions: Record<string, any>;
    attemptResult: any;
    isLoadingCourse: boolean;
    isSubmitting: boolean;

    alertState: any;
    confirmState: any;

    showSidebar: boolean;
    navigate: any;

    clearVersion: number;
    elapsedTime: number;
    isSidebarOpen: boolean;
    formatTime: (s: number) => string;

    // MUST HAVE
    showAlert: (title: string, message: string, onClose?: () => void) => void;
    showConfirm: (title: string, message: string, onConfirm: () => void) => void;

    // handlers
    handleSubmit: () => void;
    handleClearAll: () => void;
    handleLessonChange: (id: string) => void;
    handleExerciseChange: (ex: ExerciseDTO) => void;

    // setters
    setShowSidebar: (value: boolean) => void;
    setSubmissions: React.Dispatch<React.SetStateAction<Record<number, any>>>;
    setIsSidebarOpen: (value: boolean) => void;
}

