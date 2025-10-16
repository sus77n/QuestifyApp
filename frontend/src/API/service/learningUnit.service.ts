import { createApi } from "@reduxjs/toolkit/query/react";
import {CourseDTO, LearningUnitDTO, LearningUnitWithChildren} from "../../model/LearningUnitDTO";
import { customBaseQuery } from "../client/customBaseQuery";

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

    getLearningUnitById: builder.query<LearningUnitDTO, { userId?: number; id: number }>({
      query: ({ userId, id }) =>
          userId
              ? `/learning-units/${id}?userId=${userId}`
              : `/learning-units/${id}`,
    }),

    getLearningUnitWithChildren: builder.query<LearningUnitWithChildren, { id: number }>({
      query: ({ id }) =>
          `/learning-units/getLearningUnitWithChildren/${id}`,
    }),

    getAllLearningUnitsByLevel: builder.query<LearningUnitDTO[], number>({
      query: (level) => `/learning-units/type/level/${level}`,
    }),

    countLearningUnitById: builder.query<number, number>({
      query: (id) => `/learning-units/count/${id}`,
    }),

    createLearningUnit: builder.mutation<LearningUnitDTO, Partial<LearningUnitDTO>>({
      query: (body) => ({
        url: "/learning-units",
        method: "POST",
        body,
      }),
      invalidatesTags: [{ type: "LearningUnit", id: "LIST" }],
    }),

    updateLearningUnit: builder.mutation<LearningUnitDTO, Partial<LearningUnitDTO> & { id: number }>({
      query: ({ id, ...body }) => ({
        url: `/learning-units/${id}`,
        method: "PUT",
        body,
      }),
      invalidatesTags: (result, error, { id }) => [{ type: "LearningUnit", id }],
    }),

    deleteLearningUnit: builder.mutation<void, number>({
      query: (id) => ({
        url: `/learning-units/${id}`,
        method: "DELETE",
      }),
      invalidatesTags: [{ type: "LearningUnit", id: "LIST" }],
    }),

    getAllCompletedCoursesByUserId: builder.query<CourseDTO[], number>({
      query: (userId) => `/learning-units/courses/completed/${userId}`,
    }),

    getAllIncompletedCoursesByUserId: builder.query<CourseDTO[], number>({
      query: (userId) => `/learning-units/courses/incompleted/${userId}`,
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
} = learningUnitService;
