import { createApi } from "@reduxjs/toolkit/query/react";
import {customBaseQuery} from "../client/customBaseQuery";
import {CourseDTO} from "../../model/LearningUnitDTO";

export const courseService = createApi({
    reducerPath: "courseService",
    baseQuery: customBaseQuery,
    tagTypes: ["Course"],
    endpoints: (builder) => ({
        getCourseById: builder.query<CourseDTO, string>({
            query: (id) => `/courses/${id}`,
            providesTags: (result, error, id) => [{ type: "Course", id }],
        }),

        getAllCourses: builder.query<CourseDTO[], void>({
            query: () => `/courses`,
            providesTags: ["Course"],
        }),

        getAllCoursesWithAuth: builder.query<CourseDTO[], void>({
            query: () => `/courses`,
            providesTags: ["Course"],
        }),

        addCourse: builder.mutation<CourseDTO, Partial<CourseDTO>>({
            query: (CourseDTO) => ({
                url: "/courses",
                method: "POST",
                body: CourseDTO,
            }),
            invalidatesTags: ["Course"],
        }),

        editCourse: builder.mutation<CourseDTO, Partial<CourseDTO> & { id: string }>({
            query: ({ id, ...data }) => ({
                url: `/courses/${id}`,
                method: "PUT",
                body: data,
            }),
            invalidatesTags: (result, error, { id }) => [{ type: "Course", id }],
        }),

        deleteCourse: builder.mutation<void, string>({
            query: (id) => ({
                url: `/courses/${id}`,
                method: "DELETE",
            }),
            invalidatesTags: ["Course"],
        }),
    }),
});

export const { useGetAllCoursesWithAuthQuery,useAddCourseMutation,useEditCourseMutation, useDeleteCourseMutation, useGetAllCoursesQuery, useGetCourseByIdQuery } = courseService;
