import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { CourseDTO, LearningUnitDTO } from "../../model/LearningUnitDTO";

export const learningUnitService = createApi({
  reducerPath: "learningUnitService",
  baseQuery: fetchBaseQuery({
    baseUrl: "/api",
  }),
  tagTypes: ["LearningUnit"],
  endpoints: (builder) => ({
    // Get all learning units
    getAllLearningUnits: builder.query<LearningUnitDTO[], void>({
      query: () => "/learning-units",
      providesTags: (result) =>
        result
          ? [
              ...result.map(({ id }) => ({
                type: "LearningUnit" as const,
                id,
              })),
              { type: "LearningUnit" as const, id: "LIST" },
            ]
          : [{ type: "LearningUnit" as const, id: "LIST" }],
    }),

    // getLearningUnitById: builder.query<LearningUnitDTO, number>({
    //     query: (id) => ({
    //         url: `/learning-units/${id}`,
    //         method: 'GET',
    //         headers: {
    //             'Content-Type': 'application/json',
    //         }
    //     }),
    // }),
    getLearningUnitById: builder.query<
      LearningUnitDTO,
      { userId: number; id: number }
    >({
      query: ({ userId, id }) => ({
        url: `/learning-units/${id}?userId=${userId}`,
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      }),
    }),

    getAllLearningUnitsByLevel: builder.query<LearningUnitDTO[], number>({
      query: (level) => ({
        url: `/learning-units/type/level/${level}`,
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      }),
    }),

    getAllIncompletedCoursesByUserId: builder.query<CourseDTO[], number>({
      query: (userId) => ({
        url: `/learning-units/incompleted-courses/${userId}`,
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      }),
    }),

    getAllCompletedCoursesByUserId: builder.query<CourseDTO[], number>({
      query: (userId) => ({
        url: `/learning-units/completed-courses/${userId}`,
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      }),
    }),
  }),
});
export const {
  useGetAllLearningUnitsQuery,
  useGetLearningUnitByIdQuery,
  useLazyGetLearningUnitByIdQuery,
  useGetAllLearningUnitsByLevelQuery,
  useGetAllCompletedCoursesByUserIdQuery,
  useGetAllIncompletedCoursesByUserIdQuery,
} = learningUnitService;
