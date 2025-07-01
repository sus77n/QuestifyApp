import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import { UserDTO } from '../../model/UserDTO';

export const userService = createApi({
    reducerPath: 'userService',
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
        getCurrentUser: builder.query<UserDTO, void>({
            query: () => ({
                url: '/auth/user',
                method: 'GET',
            }),
        }),
    }),
});

export const { useGetCurrentUserQuery } = userService;
