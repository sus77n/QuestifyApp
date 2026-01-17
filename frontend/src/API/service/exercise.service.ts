import { createApi } from "@reduxjs/toolkit/query/react";
import {ExerciseDTO} from "../../model/ExerciseDTO";
import { OptionDTO } from "../../model/OptionDTO";
import {customBaseQuery} from "../client/customBaseQuery";

export const exerciseService = createApi({
  reducerPath: "exerciseService",
  baseQuery: customBaseQuery,
  tagTypes: ["Exercise"],
  endpoints: (builder) => ({
    getExerciseById: builder.query<ExerciseDTO, string>({
      query: (id) => `/exercises/${id}`,
      providesTags: (result, error, id) => [{ type: "Exercise", id }],
    }),

    getExerciseOptions: builder.query<OptionDTO[], string>({
      query: (exerciseId) => `/exercises/${exerciseId}/options`,
    }),

    getExercises: builder.query<ExerciseDTO[], { lessonId?: string}>({
      query: ({ lessonId}) => {
        return `/learning-units/${lessonId}/exercises`;
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

    addListExercise: builder.mutation<void, { exercises: ExerciseDTO[], learningUnitId: string }>({
      query: (body) => ({
        url: `/bulk-create`, // Endpoint BE nhận list
        method: "POST",
        body,
      }),
      invalidatesTags: ["Exercise"],
    }),

    updateExercise: builder.mutation<ExerciseDTO, { id: string; data: ExerciseDTO }>({
      query: ({ id, data }) => ({

        url: `/exercises/${id}`,
        method: "PUT",
        body: data,
      }),
      invalidatesTags: (result, error, { id }) => [{ type: "Exercise", id }],
    }),

    deleteExercise: builder.mutation<void, string>({
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
    useAddListExerciseMutation
} = exerciseService;

