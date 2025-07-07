import { ChapterDTO } from "./ChapterDTO";

export interface CourseDTO {
    id: number;
    code: string;
    name: string;
    description: string;
    chapters: ChapterDTO[];
    createdAt: Date
}