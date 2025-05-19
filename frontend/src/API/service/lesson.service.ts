import {createApi, fetchBaseQuery} from "@reduxjs/toolkit/query/react";
import {LessonDTO} from "../../model/LessonDTO";
import {ExerciseDTO} from "../../model/ExerciseDTO";

export const lessonService = createApi({
    reducerPath: 'lessonService',
    baseQuery: fetchBaseQuery({
        baseUrl: '/api',
        prepareHeaders: (headers) => {
            const token = localStorage.getItem('token');
            if (token) headers.set('Authorization', `Bearer ${token}`);
            return headers;
        },
    }),
    tagTypes: ['Lesson'],
    endpoints: (builder) => ({
        // Get lessons by chapter ID
        getLessonsByChapter: builder.query<LessonDTO[], number>({
            query: (chapterId) => `/lesson/${chapterId}`,
            providesTags: ['Lesson'],
        }),

        // Get exercises by lesson ID
        getExercisesByLesson: builder.query<ExerciseDTO[], number>({
            query: (lessonId) => `/exercise/${lessonId}`,
            providesTags: (result, error, lessonId) =>
                [{ type: 'Lesson', id: lessonId }],
        }),
    }),
});

export const {
    useGetLessonsByChapterQuery,
    useGetExercisesByLessonQuery,
} = lessonService;