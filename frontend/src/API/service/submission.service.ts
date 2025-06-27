import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

interface SubmitAnswerRequest {
    exerciseId: number;
    userId: number;
    text: string;
    optionId: number;
}

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
        submitAnswer: builder.mutation<number, SubmitAnswerRequest>({
            query: (body) => ({
                url: `/submissions/submit`,
                method: 'POST',
                body,
            }),
            invalidatesTags: ['submission'],
        }),
    }),
});

export const {
    useSubmitAnswerMutation,
} = submissionService;
