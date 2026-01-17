import { createApi } from "@reduxjs/toolkit/query/react";
import {
  CourseDTO,
  LearningUnitDTO,
  LearningUnitWithChildren,
  LearningUnitWithLessonConfig
} from "../../model/LearningUnitDTO";
import { customBaseQuery } from "../client/customBaseQuery";
import {ExerciseDTO} from "../../model/ExerciseDTO";

export const learningUnitService = createApi({
  reducerPath: "learningUnitService",
  baseQuery: customBaseQuery,
  tagTypes: ["LearningUnit"],
  endpoints: (builder) => ({
    getAllLearningUnits: builder.query<LearningUnitDTO[], void>({
      query: () => "/learning-units",
      providesTags: (result) =>
          result
              ? [
                ...result.map(({ id }) => ({ type: "LearningUnit" as const, id })),
                { type: "LearningUnit" as const, id: "LIST" },
              ]
              : [{ type: "LearningUnit" as const, id: "LIST" }],
    }),

    getLearningUnitById: builder.query<LearningUnitDTO, { id: string; userId?: string; includeCategory?: boolean }>({
      query: ({ id, userId, includeCategory }) => ({
        url: `/learning-units/${id}`,
        params: {
          userId,
          includeCategory,
        },
      }),
      providesTags: (result, error, arg) => [{ type: "LearningUnit", id: arg.id }],
    }),


    getLearningUnitDetailsById: builder.query<LearningUnitDTO, { id: string}>({
      query: ({ id }) => ({
        url: `/learning-units/${id}/lesson-details`,
      }),
      providesTags: (result, error, arg) => [{ type: "LearningUnit", id: arg.id }],
    }),


    initializeLessonConfigAndCate: builder.mutation<LearningUnitWithLessonConfig, { id: string }>({
      query: ({ id }) => ({
        url: `/learning-units/${id}`,
        method: "POST",
      }),
      invalidatesTags: (result, error, arg) => [{ type: "LearningUnit", id: arg.id }],
    }),

    getLearningUnitWithChildren: builder.query<LearningUnitWithChildren, { id: string }>({
      query: ({ id }) => `/learning-units/${id}`,
    }),


    getAllLearningUnitsByLevel: builder.query<LearningUnitDTO[], number>({
      query: (level) => `/learning-units/type/level/${level}`,
    }),

    countLearningUnitById: builder.query<number, string>({
      query: (id) => `/learning-units/count/${id}`,
    }),

    createLearningUnit: builder.mutation<LearningUnitWithChildren, Partial<LearningUnitWithChildren>>({
      query: (body) => ({
        url: "/learning-units/child",
        method: "POST",
        body,
      }),
      invalidatesTags: [{ type: "LearningUnit", id: "LIST" }],
    }),

    updateLearningUnit: builder.mutation<LearningUnitDTO, Partial<LearningUnitDTO> & { id: string }>({
      query: ({ id, ...body }) => ({
        url: `/learning-units/${id}`,
        method: "PUT",
        body,
      }),
      invalidatesTags: (result, error, { id }) => [{ type: "LearningUnit", id }],
    }),

    deleteLearningUnit: builder.mutation<void, string>({
      query: (id) => ({
        url: `/learning-units/${id}`,
        method: "DELETE",
      }),
      invalidatesTags: [{ type: "LearningUnit", id: "LIST" }],
    }),

    getAllCompletedCoursesByUserId: builder.query<CourseDTO[], string>({
      query: (userId) => `/learning-units/courses/completed/${userId}`,
    }),

    getAllIncompletedCoursesByUserId: builder.query<CourseDTO[], string>({
      query: (userId) => `/learning-units/courses/incompleted/${userId}`,
    }),

    generateCategoryContent: builder.mutation<LearningUnitDTO[], { originalExCateId: string }>({
      query: ({ originalExCateId }) => ({
        url: `/learning-units/generate/categories/${originalExCateId}`,
        method: "POST",
      }),
    }),

    // 2. Generate Exercises: Trả về 10 câu hỏi (Preview)
    generateExercises: builder.mutation<ExerciseDTO[], { categories : string[], learningUnitId: string }>({
      query: (body) => ({
        url: `/learning-units/generate/exercises`,
        method: "POST",
        body,
      }),
    }),
  }),
});

export const {
  useGetAllLearningUnitsQuery,
  useGetLearningUnitByIdQuery,
  useLazyGetLearningUnitByIdQuery,
  useGetAllLearningUnitsByLevelQuery,
  useCountLearningUnitByIdQuery,
  useCreateLearningUnitMutation,
  useUpdateLearningUnitMutation,
  useDeleteLearningUnitMutation,
  useGetAllCompletedCoursesByUserIdQuery,
  useGetAllIncompletedCoursesByUserIdQuery,
  useGetLearningUnitWithChildrenQuery,
    useInitializeLessonConfigAndCateMutation,
    useGetLearningUnitDetailsByIdQuery,
    useGenerateCategoryContentMutation,
    useGenerateExercisesMutation
} = learningUnitService;
