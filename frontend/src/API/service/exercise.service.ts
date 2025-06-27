import {createApi, fetchBaseQuery} from "@reduxjs/toolkit/query/react";
import {ExerciseDTO} from "../../model/ExerciseDTO";
import {OptionDTO} from "../../model/OptionDTO";

export const exerciseService = createApi({
    reducerPath: 'exerciseService',
    baseQuery: fetchBaseQuery({
        baseUrl: '/api',
        // prepareHeaders: (headers) => {
        //     const token = localStorage.getItem('token');
        //     if (token) headers.set('Authorization', `Bearer ${token}`);
        //     return headers;
        // },
    }),
    tagTypes: ['Exercise'],
    endpoints: (builder) => ({
        // Get exercise details
        getExerciseById: builder.query<ExerciseDTO, string>({
            query: (id) => `/exercises/${id}`,
            providesTags: (result, error, id) => [{ type: 'Exercise', id }],
        }),

        getExerciseOptions: builder.query<OptionDTO[], string>({
            query: (exerciseId) => ({
                url: `/exercises/${exerciseId}/options`,
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                }
            }),
        }),

    }),
});

export const {
    useGetExerciseByIdQuery,
    useGetExerciseOptionsQuery,
} = exerciseService;