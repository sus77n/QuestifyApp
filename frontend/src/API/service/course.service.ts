import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import {ChapterDTO} from "../../model/ChapterDTO";
import {CourseDTO} from "../../model/CourseDTO";

export const courseService = createApi({
    reducerPath: 'courseService',
    baseQuery: fetchBaseQuery({ baseUrl: '/api' }),
    tagTypes: ['Course'],
    endpoints: (builder) => ({
        // Get all courses with full structure
        getCourses: builder.query<CourseDTO[], void>({
            query: () => '/course',
            providesTags: ['Course'],
        }),

        // Get single course by ID
        getCourseById: builder.query<CourseDTO, number>({
            query: (id) => `/course/${id}`,
            providesTags: (result, error, id) => [{ type: 'Course', id }],
        }),

        // Search courses (lightweight version)
        searchCourses: builder.query<Pick<CourseDTO, 'courseCode' | 'courseName'>[], string>({
            query: (term) => ({ url: '/course/search', params: { searchTerm: term } }),
        }),

        // Get chapters for a specific course
        getChaptersByCourse: builder.query<ChapterDTO[], number>({
            query: (courseId) => `/chapter/${courseId}`,
            providesTags: (result, error, courseId) => [{ type: 'Course', id: courseId }],
        }),

        // Get exercise count
        getTotalExercises: builder.query<number, number>({
            query: (courseId) => ({
                url: '/course/totalExercise',
                method: 'POST',
                body: { id: courseId },
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