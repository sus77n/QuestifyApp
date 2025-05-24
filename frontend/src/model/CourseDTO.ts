import {ChapterDTO} from "./ChapterDTO";

export interface CourseDTO {
    id: number;
    courseCode: string;
    courseName: string;
    description: string;
    chapters: ChapterDTO[];
}