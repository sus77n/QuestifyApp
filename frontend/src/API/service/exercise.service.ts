import {createApi, fetchBaseQuery} from "@reduxjs/toolkit/query/react";
import {ExerciseDTO} from "../../model/ExerciseDTO";
import {OptionDTO} from "../../model/OptionDTO";

export const exerciseService = createApi({
    reducerPath: 'exerciseService',
    baseQuery: fetchBaseQuery({
        baseUrl: '/api',
        prepareHeaders: (headers) => {
            const token = localStorage.getItem('token');
            if (token) headers.set('Authorization', `Bearer ${token}`);
            return headers;
        },
    }),
    tagTypes: ['Exercise'],
    endpoints: (builder) => ({
        // Get exercise details
        getExerciseById: builder.query<ExerciseDTO, number>({
            query: (id) => `/exercise/${id}`,
            providesTags: (result, error, id) => [{ type: 'Exercise', id }],
        }),

        // Get options for multiple choice questions
        getExerciseOptions: builder.query<OptionDTO[], string>({
            query: (exerciseId) => ({
                url: '/exercise/getOptions',
                method: 'POST',
                body: { id: exerciseId },
            }),
            transformResponse: (response: { options?: OptionDTO[] }) =>
                response?.options || [],
        }),
    }),
});

export const {
    useGetExerciseByIdQuery,
    useGetExerciseOptionsQuery,
} = exerciseService;