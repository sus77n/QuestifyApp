import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import {LoginDTO, LoginResponseDTO, SignupDTO} from "../../model/AuthDTO";


export const authService = createApi({
    reducerPath: 'authService',
    baseQuery: fetchBaseQuery({
        baseUrl: '/api',
        prepareHeaders: (headers) => {
            const token = localStorage.getItem('token');
            if (token) {
                headers.set('Authorization', `Bearer ${token}`);
            }
            return headers;
        },
    }),
    endpoints: (builder) => ({
        login: builder.mutation<LoginResponseDTO, LoginDTO>({
            query: (credentials) => ({
                url: '/auth/login',
                method: 'POST',
                body: credentials,
            }),
        }),
        signup: builder.mutation<void, SignupDTO>({
            query: (userData) => ({
                url: '/auth/register',
                method: 'POST',
                body: userData,
            }),
        }),
    }),
});

export const { useLoginMutation, useSignupMutation } = authService;