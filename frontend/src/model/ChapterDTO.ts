import {LessonDTO} from "./LessonDTO";

export interface ChapterDTO {
    id: number;
    title: string;
    lessons: LessonDTO[];
}
