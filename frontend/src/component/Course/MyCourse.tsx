import React from "react";
import { ChevronRightIcon, CheckCircleIcon } from "@heroicons/react/24/solid";
import { useNavigate } from "react-router-dom";

// Mock data - replace with your actual API calls
const ongoingCourses = [
    { id: 1, code: "CSE106", name: "Data Structures", progress: 20, total: 30 },
    { id: 2, code: "CSE205", name: "Algorithms", progress: 15, total: 25 },
];

const completedCourses = [
    { id: 3, code: "CSE101", name: "Introduction to Programming", completedDate: "04/06/2025" },
    { id: 4, code: "MATH201", name: "Discrete Mathematics", completedDate: "12/15/2024" },
];

const MyCourse = () => {
    const navigate = useNavigate();

    const calculatePercentage = (progress: number, total: number) => {
        return Math.round((progress / total) * 100);
    };

    return (
        <div className="h-screen flex ml-1">
            <div className="m-[8px] bg-white h-[98vh] w-full rounded-xl flex flex-col p-8 overflow-y-auto">
                <h1 className="text-3xl font-semibold text-text-color mb-8">My Courses</h1>

                {/* Ongoing Courses Section */}
                <section className="mb-12">
                    <h2 className="text-2xl font-bold text-text-color mb-6">Unfinished Subjects</h2>

                    <div className="space-y-4">
                        {ongoingCourses.map((course) => (
                            <div
                                key={course.id}
                                className="border-2 border-gray-200 rounded-xl p-5 hover:shadow-md transition-shadow cursor-pointer"
                                onClick={() => navigate(`/course/${course.id}`)}
                            >
                                <div className="flex justify-between items-center">
                                    <div>
                                        <h3 className="text-xl font-bold text-text-color">{course.code} - {course.name}</h3>
                                        <div className="flex items-center mt-2">
                                            <div className="w-64 bg-gray-200 rounded-full h-4 mr-4">
                                                <div
                                                    className="bg-primary h-4 rounded-full"
                                                    style={{ width: `${calculatePercentage(course.progress, course.total)}%` }}
                                                ></div>
                                            </div>
                                            <span className="text-gray-600">
                        {course.progress}/{course.total} ({calculatePercentage(course.progress, course.total)}%)
                      </span>
                                        </div>
                                    </div>
                                    <ChevronRightIcon className="h-6 w-6 text-gray-400" />
                                </div>
                            </div>
                        ))}

                        {ongoingCourses.length === 0 && (
                            <div className="text-center py-8 text-gray-400">
                                No ongoing courses found
                            </div>
                        )}
                    </div>
                </section>

                {/* Completed Courses Section */}
                <section>
                    <h2 className="text-2xl font-bold text-text-color mb-6">Finished Subjects</h2>

                    <div className="space-y-3">
                        {completedCourses.map((course) => (
                            <div
                                key={course.id}
                                className="flex items-center p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors cursor-pointer"
                                onClick={() => navigate(`/course/${course.id}`)}
                            >
                                <CheckCircleIcon className="h-6 w-6 text-green-500 mr-4" />
                                <div>
                                    <h3 className="font-semibold text-text-color">{course.code} - {course.name}</h3>
                                    <p className="text-sm text-gray-500">Completed on {course.completedDate}</p>
                                </div>
                            </div>
                        ))}

                        {completedCourses.length === 0 && (
                            <div className="text-center py-8 text-gray-400">
                                No completed courses yet
                            </div>
                        )}
                    </div>
                </section>
            </div>
        </div>
    );
};

const ProgressLine = ({ progress, total }: { progress: number; total: number }) => {
    // Calculate percentage (with safeguard against division by zero)
    const percentage = total > 0 ? Math.round((progress / total) * 100) : 0;

    return (
        <div className="flex items-center gap-4">
            {/* Progress bar visualization */}
            <div className="w-full bg-gray-200 rounded-full h-4">
                <div
                    className="bg-blue-500 h-4 rounded-full transition-all duration-300"
                    style={{ width: `${percentage}%` }}
                />
            </div>

            {/* Numeric display */}
            <span className="text-sm font-medium text-gray-700 whitespace-nowrap">
        {progress}/{total} ({percentage}%)
      </span>
        </div>
    );
};

export default MyCourse;