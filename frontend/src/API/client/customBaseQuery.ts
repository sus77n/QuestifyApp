import { fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import type {
  BaseQueryFn,
  FetchArgs,
  FetchBaseQueryError,
} from "@reduxjs/toolkit/query";
import {ApiResponse} from "../../model/api";

const rawBaseQuery = fetchBaseQuery({
  baseUrl: "/api",
  prepareHeaders: (headers) => {
    const token = localStorage.getItem("token");
    if (token) {
      headers.set("Authorization", `Bearer ${token}`);
    }
    return headers;
  },
});

export interface CustomMeta {
  success: boolean;
  message: string;
  errorCode: string | null;
}

export const customBaseQuery: BaseQueryFn<
    string | FetchArgs,
    unknown,
    FetchBaseQueryError
> = async (args, api, extraOptions) => {
  const result = await rawBaseQuery(args, api, extraOptions);

  if (result.error) {
    const status = result.error.status;
    if (status === 401) {
      const currentPath = window.location.pathname;
      if (currentPath.includes("/login")) {
        return result;
      }
      localStorage.clear();
      window.location.href = "/login";
    }else if (status === 403) {
      window.location.href = "/403";
    } else if (status === 404) {
      window.location.href = "/404";
    } else if (status === 500) {
      window.location.href = "/500";
    }
    return result;
  }

  if (result.data && typeof result.data === "object" && "data" in result.data) {
    const apiRes = result.data as ApiResponse<unknown>;
    return {
      ...result,
      data: apiRes.data,
      meta: {
        success: apiRes.success,
        message: apiRes.message,
        errorCode: apiRes.errorCode,
      } as CustomMeta,
    };
  }

  return result;
};
