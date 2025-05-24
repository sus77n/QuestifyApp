import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { ChapterDTO } from "../../model/ChapterDTO";

export const chapterService = createApi({
    reducerPath: 'chapterService',
    baseQuery: fetchBaseQuery({
        baseUrl: '/api',
        // prepareHeaders: (headers) => {
        //     const token = localStorage.getItem('token');
        //     if (token) headers.set('Authorization', `Bearer ${token}`);
        //     return headers;
        // },
    }),
    tagTypes: ['Chapter'],
    endpoints: (builder) => ({
        // Get chapters by course ID
        getChaptersByCourse: builder.query<ChapterDTO[], number>({
            query: (courseId) => `/chapter/byCourseId/${courseId}`,
            providesTags: ['Chapter'],
        }),
    }),
});

export const { useGetChaptersByCourseQuery } = chapterService;