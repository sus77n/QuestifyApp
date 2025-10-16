import { createApi } from "@reduxjs/toolkit/query/react";
import { SubmissionDTO } from "../../model/SubmissionDTO";
import {customBaseQuery} from "../client/customBaseQuery";

export const submissionService = createApi({
  reducerPath: "submissionService",
  baseQuery: customBaseQuery,
  tagTypes: ["submission"],
  endpoints: (builder) => ({
    submitAnswer: builder.mutation<SubmissionDTO, SubmissionDTO>({
      query: (submit) => ({
        url: `/submissions/submit`,
        method: "POST",
        body: submit,
      }),
      invalidatesTags: ["submission"],
    }),

    submitByLesson: builder.mutation<number, SubmissionDTO[]>({
      query: (submit) => ({
        url: `/submissions/submit-all`,
        method: "POST",
        body: submit,
      }),
      invalidatesTags: ["submission"],
    }),
  }),
});

export const { useSubmitAnswerMutation, useSubmitByLessonMutation } =
  submissionService;
