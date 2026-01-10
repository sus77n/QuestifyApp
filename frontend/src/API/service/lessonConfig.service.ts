import { createApi } from "@reduxjs/toolkit/query/react";
import {LessonConfigDTO} from "../../model/LessonConfigDTO";
import {customBaseQuery} from "../client/customBaseQuery";

export const lessonConfigService = createApi({
    reducerPath: "LessonConfigService",
    baseQuery: customBaseQuery,
    tagTypes: ["LessonConfig"],
    endpoints: (builder) => ({
        getLessonConfigs: builder.query<LessonConfigDTO, { lessonId?: string}>({
            query: ({ lessonId}) => {
                return `/lesson-configs/${lessonId}`;
            },
            providesTags: [{ type: "LessonConfig", id: "lessonId" }],
        }),

        createLessonConfig: builder.mutation<LessonConfigDTO, LessonConfigDTO>({
            query: (body) => ({
                url: `/lesson-configs`,
                method: "POST",
                body,
            }),
            invalidatesTags: [{ type: "LessonConfig", id: "lessonId" }],
        }),

        updateLessonConfig: builder.mutation<LessonConfigDTO, { lessonId: string; data: LessonConfigDTO }>({
            query: ({ lessonId, data }) => ({

                url: `/lesson-configs/${lessonId}`,
                method: "PUT",
                body: data,
            }),
            invalidatesTags: (result, error, { lessonId }) => [{ type: "LessonConfig", lessonId }],
        }),

        deleteLessonConfig: builder.mutation<void, string>({
            query: (id) => ({
                url: `/lesson-configs/${id}`,
                method: "DELETE",
            }),
            invalidatesTags: [{ type: "LessonConfig", id: "lessonId" }],
        }),
    }),
});

export const {
    useGetLessonConfigsQuery,
    useCreateLessonConfigMutation,
    useUpdateLessonConfigMutation,
    useDeleteLessonConfigMutation,
} = lessonConfigService;

