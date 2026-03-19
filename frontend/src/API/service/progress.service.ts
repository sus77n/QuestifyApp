import { createApi } from "@reduxjs/toolkit/query/react";
import { customBaseQuery } from "../client/customBaseQuery";
import { ProgressDTO } from "src/model/ProgressDTO";

export const progressService = createApi({
    reducerPath: "progressService",
    baseQuery: customBaseQuery,
    tagTypes: ["Progress"],
    endpoints: (builder) => ({
        getAllCompletedCoursesByUserId: builder.query<ProgressDTO[], void>({
            query: () => `/progress/completed`,
        }),

        getAllIncompletedCoursesByUserId: builder.query<ProgressDTO[], void>({
            query: () => `/progress/incompleted`,
        }),
    })
});

export const {
  useGetAllCompletedCoursesByUserIdQuery,
  useGetAllIncompletedCoursesByUserIdQuery,
} = progressService;