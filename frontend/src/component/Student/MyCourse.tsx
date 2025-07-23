import React from "react";
import { ChevronRightIcon } from "@heroicons/react/24/solid";
import { useNavigate } from "react-router-dom";
import {useGetAllIncompletedCoursesByUserIdQuery} from "../../API/service/learningUnit.service";
import {CourseDTO} from "../../model/LearningUnitDTO";
import {Spinner} from "../material/material";

const MyCourse = () => {
    const navigate = useNavigate();
    const userId = Number(localStorage.getItem("id")!);
    const username = localStorage.getItem("username");

    const { data: ongoingCourses, isLoading: isLoadingCourses } =
        useGetAllIncompletedCoursesByUserIdQuery(userId, {
            skip: !userId,
        });
    ;

    return (
        <div className="h-screen flex ml-1">
            <div className="m-[8px] bg-white h-[98vh] w-full rounded-xl flex flex-col overflow-y-auto">
                <h1 className="text-2xl font-semibold text-white bg-text-color pt-3 pb-3 pl-5 pr-5 ">My Courses</h1>
                {isLoadingCourses ? (
                        <div className="w-full h-full flex items-center justify-center">
                            <Spinner/>
                        </div>
                    ):(
                    <div className="p-4">
                        <div className="mb-12">
                            <h2 className="text-xl font-bold text-text-color mb-2">Hey, {username}, let's finish your courses !!!</h2>

                            <div className="flex flex-1 flex-wrap gap-4">
                                {ongoingCourses?.map((course: CourseDTO, index: number) => (
                                    <div className="w-[450px]" onClick={() => navigate(`/topics/${course.id}`)} key={course.id}>
                                        <CardCourseMini courseCode={course.code} courseName={course.name} index={index} progress={course.completedExercises} total={course.totalOfExercise}/>
                                    </div>
                                ))}

                                {ongoingCourses?.length === 0 && (
                                    <div className="text-center py-8 text-gray-400">
                                        No ongoing courses found
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                )}

            </div>
        </div>
    );
};

const ProgressLine = ({ progress, total }: { progress: number; total: number }) => {
    const percentage = total > 0 ? Math.round((progress / total) * 100) : 0;

    return (
        <div className="flex items-center gap-4">
            <div className="w-[170px] bg-gray-200 rounded-full h-4">
                <div
                    className="bg-background-color h-4 rounded-full transition-all duration-300"
                    style={{ width: `${percentage}%` }}
                />
            </div>

            <span className="text-sm font-medium text-gray-700 whitespace-nowrap">
                {progress}/{total} ({percentage}%)
            </span>
        </div>
    );
};

const CardCourseMini = ({courseCode, courseName, index, progress, total
                        }: {
    courseCode: string;
    courseName: string;
    index: number;
    progress: number;
    total: number;
}) => {
    const avatarIndex = (index % 3) + 1;
    const avatarSrc = `/img/ava${avatarIndex}.png`;

    return (
        <div className={`p-3 relative flex items-center h-30 mt-3 border-2 rounded-xl transition-all duration-200 hover:border-text-color hover:shadow-lg hover:bg-white/5`}
        >
            <img src={avatarSrc} className="w-24 h-24 rounded-xl mr-2" alt="avatarCourse"/>
            <div className="ml-2 relative">
                <p className="text-gray-400 font-semibold text-[15px]">{courseCode}</p>
                <p className="text-text-color font-bold text-[18px] mt-2 mb-2">{courseName}</p>
                <ProgressLine progress={progress} total={total}/>
            </div>
            <div className="absolute right-2 flex items-center justify-end">
                <ChevronRightIcon className={`h-5 w-5 transition-all duration-300 text-background-color`}/>
            </div>
        </div>
    )
}

export default MyCourse;