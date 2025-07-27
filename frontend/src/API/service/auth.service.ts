import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import {LoginDTO, LoginResponseDTO, SignupDTO} from "../../model/AuthDTO";
import {UserDTO} from "../../model/UserDTO";


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
        signup: builder.mutation<string, SignupDTO>({
            query: (userData) => ({
                url: '/auth/register',
                method: 'POST',
                body: userData,
                responseHandler: (response) => response.text(),
            }),
        }),
        getCurrentUser: builder.query<UserDTO, void>({
            query: () => ({
                url: '/auth/me',
                method: 'GET',
            }),
        }),
    }),
});

export const {useGetCurrentUserQuery, useLoginMutation, useSignupMutation } = authService;