import React, { ReactNode } from "react";
import { Breadcrumb } from "antd";

type BreadcrumbItem = {
    label: string;
    path?: string;
};

interface TeacherPageProps {
    title: string;
    breadcrumb: BreadcrumbItem[];
    extra?: ReactNode;
    children: ReactNode;
}

const TeacherPage: React.FC<TeacherPageProps> = ({
                                                     title,
                                                     breadcrumb,
                                                     extra,
                                                     children,
                                                 }) => {
    return (
        <div className="h-screen bg-light-background w-full p-4 flex flex-col overflow-hidden">

            <div className="relative flex items-center mb-2 h-[48px] shrink-0">
                <div className="flex items-center">
                    <Breadcrumb
                        items={breadcrumb.map((item) => ({
                            title: item.path ? (
                                <span
                                    style={{ cursor: "pointer", color: "#1677ff" }} // Thêm màu cho dễ nhận diện link
                                    onClick={() => (window.location.href = item.path!)}
                                >
                                    {item.label}
                                </span>
                            ) : (
                                item.label
                            )
                        }))}
                    />
                </div>

                <h1
                    className="
                        absolute left-1/2 -translate-x-1/2
                        text-3xl font-bold text-text-color text-center
                        max-w-[500px] break-words
                        pointer-events-none
                    "
                >
                    {title}
                </h1>

                <div className="ml-auto flex gap-3">
                    {extra}
                </div>
            </div>

            <div className="flex-1 w-full overflow-hidden flex flex-col">
                {children}
            </div>

        </div>
    );
};

export default TeacherPage;