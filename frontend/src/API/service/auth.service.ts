import { createApi } from "@reduxjs/toolkit/query/react";
import { LoginDTO, LoginResponseDTO, SignupDTO } from "../../model/AuthDTO";
import { UserDTO } from "../../model/UserDTO";
import {customBaseQuery} from "../client/customBaseQuery";
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
  errorCode?: string | null;
}

export const authService = createApi({
  reducerPath: "authService",
  baseQuery: customBaseQuery,
  endpoints: (builder) => ({
    login: builder.mutation<LoginResponseDTO, LoginDTO>({
      query: (body) => ({
        url: "/auth/login",
        method: "POST",
        body,
      }),
    }),

    signup: builder.mutation<string, SignupDTO>({
      query: (userData) => ({
        url: "/auth/register",
        method: "POST",
        body: userData,
        responseHandler: (response) => response.text(),
      }),
    }),
    getCurrentUser: builder.query<UserDTO, void>({
      query: () => ({
        url: "/auth/me",
        method: "GET",
      }),
    }),
  }),
});

export const { useGetCurrentUserQuery, useLoginMutation, useSignupMutation } =
  authService;
