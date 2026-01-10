import { message } from "antd";
import type { Middleware } from "@reduxjs/toolkit";
import type { ApiResponse } from "./API/client/customBaseQuery";

export const apiErrorMiddleware: Middleware =
    () => (next) => (action) => {
        const act = action as {
            type?: string;
            payload?: ApiResponse<any>;
        };

        if (act.type?.endsWith("/rejected")) {
            const err = act.payload;

            if (err && !err.success) {
                switch (err.errorCode) {
                    case "INVALID_TOKEN":
                        message.error(err.message || "Token expired");
                        localStorage.clear();
                        window.location.href = "/login";
                        break;

                    case "FORBIDDEN":
                        message.error(err.message || "Access denied");
                        window.location.href = "/403";
                        break;

                    case "NOT_FOUND":
                        window.location.href = "/404";
                        break;

                    case "INTERNAL_SERVER_ERROR":
                        console.error("Server error:", err);
                        message.error("Something went wrong");
                        setTimeout(() => {
                            window.location.href = "/500";
                        }, 200); // cho React log lỗi, không redirect ngay
                        break;

                    default:
                        message.error(err.message || "An error occurred");
                        break;
                }
            }
        }

        return next(action);
    };
