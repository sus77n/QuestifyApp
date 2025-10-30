import { createApi } from "@reduxjs/toolkit/query/react";
import {ExerciseDTO} from "../../model/ExerciseDTO";
import { OptionDTO } from "../../model/OptionDTO";
import {customBaseQuery} from "../client/customBaseQuery";

export const exerciseService = createApi({
  reducerPath: "exerciseService",
  baseQuery: customBaseQuery,
  tagTypes: ["Exercise"],
  endpoints: (builder) => ({
    getExerciseById: builder.query<ExerciseDTO, number>({
      query: (id) => `/exercises/${id}`,
      providesTags: (result, error, id) => [{ type: "Exercise", id }],
    }),

    getExerciseOptions: builder.query<OptionDTO[], number>({
      query: (exerciseId) => `/exercises/${exerciseId}/options`,
    }),

    getExercises: builder.query<ExerciseDTO[], { lessonId?: number; typeId?: number }>({
      query: ({ lessonId, typeId }) => {
        const params = new URLSearchParams();
        if (lessonId) params.append("lessonId", lessonId.toString());
        if (typeId) params.append("typeId", typeId.toString());
        return `/exercises?${params.toString()}`;
      },
      providesTags: [{ type: "Exercise", id: "LIST" }],
    }),

    createExercise: builder.mutation<ExerciseDTO, ExerciseDTO>({
      query: (body) => ({
        url: `/exercises`,
        method: "POST",
        body,
      }),
      invalidatesTags: [{ type: "Exercise", id: "LIST" }],
    }),

    updateExercise: builder.mutation<ExerciseDTO, { id: number; data: ExerciseDTO }>({
      query: ({ id, data }) => ({
        url: `/exercises/${id}`,
        method: "PUT",
        body: data,
      }),
      invalidatesTags: (result, error, { id }) => [{ type: "Exercise", id }],
    }),

    deleteExercise: builder.mutation<void, number>({
      query: (id) => ({
        url: `/exercises/${id}`,
        method: "DELETE",
      }),
      invalidatesTags: [{ type: "Exercise", id: "LIST" }],
    }),
  }),
});

export const {
  useGetExerciseByIdQuery,
  useGetExerciseOptionsQuery,
  useGetExercisesQuery,
  useCreateExerciseMutation,
  useUpdateExerciseMutation,
  useDeleteExerciseMutation,
} = exerciseService;

