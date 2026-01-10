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
        <div className="h-screen bg-light-background w-auto p-4 ">
            {/* HEADER */}
            <div className="relative flex items-center mb-2 h-[48px]">

                {/* LEFT: breadcrumb */}
                <div className="flex items-center">
                    <Breadcrumb
                        items={breadcrumb.map((item) => ({
                            title: item.path ? (
                                <span
                                    style={{ cursor: "pointer" }}
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

                {/* CENTER: fixed title */}
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


                {/* RIGHT: extra buttons */}
                <div className="ml-auto flex gap-3">
                    {extra}
                </div>
            </div>


            {/* PAGE CONTENT */}
            {children}
        </div>
    );
};

export default TeacherPage;
