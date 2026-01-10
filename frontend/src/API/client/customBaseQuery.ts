import { fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import type {
  BaseQueryFn,
  FetchArgs,
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
    any,
    { status: number; data: ApiResponse<any> }
> = async (args, api, extraOptions) => {
  const result = await rawBaseQuery(args, api, extraOptions);

  // CASE 1 – HTTP ERROR (404, 401, 500, CORS…)
  if (result.error) {
    const { status, data } = result.error;

    // Nếu BE có trả ApiResponse trong body lỗi → dùng nó
    if (data && typeof data === "object" && "success" in data) {
      return {
        error: {
          status: status as number,
          data: data as ApiResponse<any>
        }
      };
    }

    // Nếu BE không trả ApiResponse (ví dụ Spring Security default 401) → chuẩn hóa lại
    return {
      error: {
        status: status as number,
        data: {
          success: false,
          message: "Unauthorized",
          data: null,
          errorCode: "UNAUTHORIZED"
        } as ApiResponse<any>
      }
    };
  }

  // CASE 2 – BE trả JSON ApiResponse thành công
  const apiRes = result.data as ApiResponse<any>;

  if (!apiRes.success) {
    return {
      error: {
        status: 400,
        data: apiRes,
      },
    };
  }

  // CASE 3 – Thành công
  return { data: apiRes.data };
};

