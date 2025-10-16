import { createApi } from "@reduxjs/toolkit/query/react";
import { UserDTO } from "../../model/UserDTO";
import {customBaseQuery} from "../client/customBaseQuery";

export const userService = createApi({
  reducerPath: "userService",
  baseQuery: customBaseQuery,
  tagTypes: ["User"],
  endpoints: (builder) => ({
    getUserById: builder.query<UserDTO, number>({
      query: (id) => `/users/${id}`,
      providesTags: (result, error, id) => [{ type: "User", id }],
    }),

    getAllUsers: builder.query<UserDTO[], void>({
      query: () => `/users`,
      providesTags: ["User"],
    }),

    editUser: builder.mutation<UserDTO, Partial<UserDTO> & { id: number }>({
      query: ({ id, ...data }) => ({
        url: `/users/${id}`,
        method: "PUT",
        body: data,
      }),
      invalidatesTags: (result, error, { id }) => [{ type: "User", id }],
    }),

    deleteUser: builder.mutation<void, number>({
      query: (id) => ({
        url: `/users/${id}`,
        method: "DELETE",
      }),
      invalidatesTags: ["User"],
    }),
  }),
});

export const { useEditUserMutation } = userService;
