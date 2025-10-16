import { createApi } from "@reduxjs/toolkit/query/react";
import { customBaseQuery } from "../client/customBaseQuery";
import {AttemptDTO, AttemptResponseDTO, AttemptStartResponseDTO} from "../../model/AttemptDTO";
import {SubmissionDTO} from "../../model/SubmissionDTO";

export const attemptService = createApi({
    reducerPath: "attemptService",
    baseQuery: customBaseQuery,
    tagTypes: ["Attempt"],
    endpoints: (builder) => ({
        // Create attempt
        createAttempt: builder.mutation<AttemptDTO, Partial<AttemptDTO>>({
            query: (body) => ({
                url: "/attempts",
                method: "POST",
                body,
            }),
            invalidatesTags: [{ type: "Attempt", id: "LIST" }],
        }),

        // Get attempt by id
        getAttemptById: builder.query<AttemptDTO, number>({
            query: (id) => `/attempts/${id}`,
            providesTags: (result, error, id) => [{ type: "Attempt", id }],
        }),

        // Get attempts by user
        getAttemptsByUser: builder.query<AttemptDTO[], number>({
            query: (userId) => `/attempts/user/${userId}`,
            providesTags: [{ type: "Attempt", id: "LIST" }],
        }),

        // Get attempts by lesson
        getAttemptsByLesson: builder.query<AttemptDTO[], number>({
            query: (lessonId) => `/attempts/lesson/${lessonId}`,
            providesTags: [{ type: "Attempt", id: "LIST" }],
        }),

        // Delete attempt
        deleteAttempt: builder.mutation<void, number>({
            query: (id) => ({
                url: `/attempts/${id}`,
                method: "DELETE",
            }),
            invalidatesTags: [{ type: "Attempt", id: "LIST" }],
        }),

        // Start attempt
        startAttempt: builder.mutation<AttemptStartResponseDTO, { userId: number; lessonId: number }>({
            query: ({ userId, lessonId }) => ({
                url: `/attempts/start?userId=${userId}&lessonId=${lessonId}`,
                method: "POST",
            }),
        }),

        // Submit attempt
        submitAttempt: builder.mutation<
            AttemptResponseDTO,
            { attemptId: number; submissions: SubmissionDTO[] }
        >({
            query: ({ attemptId, submissions }) => ({
                url: `/attempts/${attemptId}/submit`,
                method: "POST",
                body: submissions,
            }),
        }),
    }),
});

export const {
    useCreateAttemptMutation,
    useGetAttemptByIdQuery,
    useGetAttemptsByUserQuery,
    useGetAttemptsByLessonQuery,
    useDeleteAttemptMutation,
    useStartAttemptMutation,
    useSubmitAttemptMutation,
} = attemptService;
