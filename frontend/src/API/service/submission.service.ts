import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import {SubmissionDTO} from "../../model/SubmissionDTO";


export const submissionService = createApi({
    reducerPath: 'submissionService',
    baseQuery: fetchBaseQuery({
        baseUrl: '/api',
        // Uncomment this if using auth
        // prepareHeaders: (headers) => {
        //     const token = localStorage.getItem('token');
        //     if (token) headers.set('Authorization', `Bearer ${token}`);
        //     return headers;
        // },
    }),
    tagTypes: ['submission'],
    endpoints: (builder) => ({
        submitAnswer: builder.mutation<SubmissionDTO, SubmissionDTO>({
            query: (submit) => ({
                url: `/submissions/submit`,
                method: 'POST',
                body: submit,
            }),
            invalidatesTags: ['submission'],
        }),

        submitByLesson: builder.mutation<number, SubmissionDTO[]>({
            query: (submit) => ({
                url: `/submissions/submit-all`,
                method: 'POST',
                body: submit,
            }),
            invalidatesTags: ['submission'],
        }),
    }),
});

export const {
    useSubmitAnswerMutation,
    useSubmitByLessonMutation
} = submissionService;
