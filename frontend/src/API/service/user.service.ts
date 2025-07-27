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

        editUser: builder.mutation<UserDTO, Partial<UserDTO>>({
            query: (userData) => ({
                url: `/users/${userData.id}`,
                method: 'PUT',
                body: userData,
            }),
        })
    }),
});

export const { useEditUserMutation } = userService;
