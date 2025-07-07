import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { ChapterDTO } from "../../model/ChapterDTO";

export const chapterService = createApi({
    reducerPath: 'chapterService',
    baseQuery: fetchBaseQuery({
        baseUrl: '/api',
    }),
    tagTypes: ['Chapter'],
    endpoints: (builder) => ({
        getChaptersByCourse: builder.query<ChapterDTO[], number>({
            query: (courseId) => `/courses/${courseId}/chapters`,
            providesTags: ['Chapter'],
        }),
    }),
});

export const { useGetChaptersByCourseQuery } = chapterService;