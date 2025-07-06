// src/services/customBaseQuery.ts
import { fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import type { BaseQueryFn, FetchArgs, FetchBaseQueryError } from "@reduxjs/toolkit/query";
import { createBrowserHistory } from "history";

const history = createBrowserHistory();

const rawBaseQuery = fetchBaseQuery({
    baseUrl: "/api",
    prepareHeaders: (headers) => {
        // Add auth token if needed
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

        if (status === 403) history.push("/403");
        else if (status === 404) history.push("/404");
        else if (status === 500) history.push("/500");
    }

    return result;
};
