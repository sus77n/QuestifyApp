import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import {ChapterDTO} from "../../model/ChapterDTO";
import {CourseDTO} from "../../model/CourseDTO";

export const courseService = createApi({
    reducerPath: 'courseService',
    baseQuery: fetchBaseQuery({ baseUrl: '/api' }),
    tagTypes: ['Course'],
    endpoints: (builder) => ({
        getCourses: builder.query<CourseDTO[], void>({
            query: () => '/courses',
            providesTags: ['Course'],
        }),
        getCourseById: builder.query<CourseDTO, number, undefined>({
            query: (id) => `/courses/${id}`,
            providesTags: (result, error, id) => [{ type: 'Course', id }],
        }),
        searchCourses: builder.query<CourseDTO[], string>({
            query: (term) => ({ url: '/courses/search', params: { searchTerm: term } }),
        }),
        getChaptersByCourse: builder.query<ChapterDTO[], number>({
            query: (courseId) => `/api/courses/${courseId}/chapters`,
            providesTags: (result, error, courseId) => [{ type: 'Course', id: courseId }],
        }),
        getTotalExercises: builder.query<number, number>({
            query: (courseId) => ({
                url: `/api/courses/${courseId}/total/exercises`,
                method: 'GET',
            }),
            transformResponse: (response: { total?: number }) => response?.total ?? 0,
        }),
    }),
});

export const {
    useGetCoursesQuery,
    useGetCourseByIdQuery,
    useSearchCoursesQuery,
    useGetChaptersByCourseQuery,
    useGetTotalExercisesQuery,
} = courseService;