import { fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import type {
  BaseQueryFn,
  FetchArgs,
  FetchBaseQueryError,
} from "@reduxjs/toolkit/query";

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
  errorCode?: string | null;
}

const rawBaseQuery = fetchBaseQuery({
  baseUrl: "/api",
  prepareHeaders: (headers) => {
    const token = localStorage.getItem("token");
    if (token) headers.set("Authorization", `Bearer ${token}`);
    return headers;
  },
});

export const customBaseQuery: BaseQueryFn<
    string | FetchArgs,
    unknown,
    FetchBaseQueryError
> = async (args, api, extraOptions) => {
  const result = await rawBaseQuery(args, api, extraOptions);

  if (result.error) {
    const status = result.error.status;

    if (status === 401) {
      localStorage.clear();
      if (!window.location.pathname.includes("/login"))
        window.location.href = "/login";
    }
    if (status === 403) window.location.href = "/403";
    if (status === 404) window.location.href = "/404";
    if (status === 500) window.location.href = "/500";

    return result;
  }

  const apiRes = result.data as ApiResponse<unknown>;

  if (!apiRes.success) {
    return {
      error: {
        status: 400,
        data: apiRes,
      },
    };
  }

  return {
    data: apiRes.data,
  };
};
