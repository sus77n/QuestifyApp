import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

export const courseApi = createApi({
    reducerPath: 'courseApi',
    baseQuery: fetchBaseQuery({
        baseUrl: '/api',
        prepareHeaders: (headers) => {
            // Add auth headers if needed
            return headers
        }
    }),
    tagTypes: ['Chapters'],
    endpoints: (builder) => ({
        getChapters: builder.query({
            query: (courseId) => `chapter/${courseId}`,
            providesTags: ['Chapters']
        }),
        getLessonExercises: builder.query({
            query: (lessonId) => `lesson/${lessonId}/exercises`,
            providesTags: (result, error, arg) => [{ type: 'Exercises', id: arg }]
        })
    })
})

export const {
    useGetChaptersQuery,
    useGetLessonExercisesQuery,
    useLazyGetLessonExercisesQuery
} = courseApi