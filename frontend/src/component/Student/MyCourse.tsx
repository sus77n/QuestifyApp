import React, { useState } from "react";
import { ChevronRightIcon } from "@heroicons/react/24/solid";
import { useNavigate } from "react-router-dom";
import { useGetAllIncompletedCoursesByUserIdQuery } from "../../API/service/learningUnit.service";
import { CourseDTO } from "../../model/LearningUnitDTO";
import { Spinner } from "../material/material";

const MyCourse = () => {
  const [selectedCourse, setSelectedCourse] = useState<CourseDTO | null>(null);
  const [isMobileView, setIsMobileView] = useState(window.innerWidth < 768);
  const navigate = useNavigate();
  const userId = Number(localStorage.getItem("id")!);
  const username = localStorage.getItem("username");

  const { data: ongoingCourses, isLoading: isLoadingCourses } =
    useGetAllIncompletedCoursesByUserIdQuery(userId, {
      skip: !userId,
    });

  React.useEffect(() => {
    const handleResize = () => {
      setIsMobileView(window.innerWidth < 768);
    };

    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  const handleSelectCourse = (course: CourseDTO) => {
    navigate(`/topics/${course.id}`);
  };

  if (isMobileView) {
    return (
      <div className="h-screen flex flex-col bg-white">
        {!selectedCourse ? (
          // Mobile course list view
          <div className="flex flex-col h-full">
            <div className="bg-text-color pt-3 pb-3 pl-5 pr-5 rounded-b-xl sticky top-0 z-10">
              <h1 className="text-2xl font-semibold text-white">My Courses</h1>
            </div>
            <div className="p-4">
              <h2 className="text-xl font-bold text-text-color mb-4">
                Hey, {username}, let's finish your courses!
              </h2>
              <div className="overflow-y-auto flex-1">
                {isLoadingCourses ? (
                  <div className="flex items-center justify-center h-full mb-2">
                    <Spinner />
                  </div>
                ) : ongoingCourses?.length ? (
                  ongoingCourses.map((course: CourseDTO, index: number) => {
                    const courseWithIndex = {
                      ...course,
                      index,
                    };

                    return (
                      <div
                        key={course.id}
                        onClick={() => setSelectedCourse(courseWithIndex)}
                        className="cursor-pointer"
                      >
                        <CardCourseMini
                          courseCode={course.code}
                          courseName={course.name}
                          index={index}
                          progress={course.completedExercises}
                          total={course.totalOfExercise}
                          isSelected={selectedCourse?.id === course.id}
                        />
                      </div>
                    );
                  })
                ) : (
                  <div className="text-center py-8 text-gray-400">
                    No ongoing courses found
                  </div>
                )}
              </div>
            </div>
          </div>
        ) : (
          // Mobile course detail view
          <div className="flex flex-col h-full overflow-y-auto pb-20">
            <div className="bg-text-color pt-3 pb-3 pl-5 sticky top-0 z-10 flex justify-between items-center">
              <button
                onClick={() => setSelectedCourse(null)}
                className="text-white font-bold"
              >
                ← Back
              </button>
              <h1 className="text-xl font-semibold text-white">
                {selectedCourse.code}
              </h1>
              <div className="w-16"></div>
            </div>

            <div className="p-4">
              <div className="flex flex-col items-center mb-6">
                <img
                  src={`/img/ava${(selectedCourse?.id % 3) + 1}.png`}
                  className="w-32 h-32 rounded-xl object-cover mb-4 shadow-md"
                  alt="Course avatar"
                />
                <h2 className="text-2xl font-bold text-gray-900 text-center">
                  {selectedCourse.name}
                </h2>
                <div className="mt-4 w-full">
                  <ProgressLineMobile
                    progress={selectedCourse.completedExercises}
                    total={selectedCourse.totalOfExercise}
                  />
                </div>
              </div>

              <div className="fixed bottom-0 left-0 right-0 bg-white p-4 shadow-lg border-t">
                <button
                  className="bg-text-color text-white rounded-xl px-6 py-3 text-lg font-bold
                                    border-2 border-text-color transition-colors duration-300
                                    hover:bg-white hover:text-text-color
                                    shadow-md hover:shadow-lg w-full"
                  onClick={() => handleSelectCourse(selectedCourse)}
                >
                  Continue Course
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    );
  }

  // Desktop view
  return (
    <div className="h-screen flex ml-1">
      <div className="m-[8px] bg-white h-[98vh] w-full rounded-xl flex flex-col overflow-y-auto">
        <h1 className="text-2xl font-semibold text-white bg-text-color pt-3 pb-3 pl-5 pr-5">
          My Courses
        </h1>
        {isLoadingCourses ? (
          <div className="w-full h-full flex items-center justify-center">
            <Spinner />
          </div>
        ) : (
          <div className="p-4">
            <div className="mb-12">
              <h2 className="text-xl font-bold text-text-color mb-2">
                Hey, {username}, let's finish your courses!
              </h2>

              <div className="flex flex-1 flex-wrap gap-4">
                {ongoingCourses?.map((course: CourseDTO, index: number) => (
                  <div
                    className="w-[450px]"
                    onClick={() => handleSelectCourse(course)}
                    key={course.id}
                  >
                    <CardCourseMini
                      courseCode={course.code}
                      courseName={course.name}
                      index={index}
                      progress={course.completedExercises}
                      total={course.totalOfExercise}
                    />
                  </div>
                ))}

                {ongoingCourses?.length === 0 && (
                  <div className="text-center py-8 text-gray-400 w-full">
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

const ProgressLine = ({
  progress,
  total,
}: {
  progress: number;
  total: number;
}) => {
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

const ProgressLineMobile = ({
  progress,
  total,
}: {
  progress: number;
  total: number;
}) => {
  const percentage = total > 0 ? Math.round((progress / total) * 100) : 0;

  return (
    <div className="flex flex-col w-full">
      <div className="flex justify-between mb-1">
        <span className="text-sm font-medium text-gray-700">Progress</span>
        <span className="text-sm font-medium text-gray-700">
          {percentage}% complete
        </span>
      </div>
      <div className="w-full bg-gray-200 rounded-full h-2.5">
        <div
          className="bg-background-color h-2.5 rounded-full"
          style={{ width: `${percentage}%` }}
        />
      </div>
      <div className="flex justify-between mt-1">
        <span className="text-xs text-gray-500">
          {progress} exercises completed
        </span>
        <span className="text-xs text-gray-500">{total} total exercises</span>
      </div>
    </div>
  );
};

const CardCourseMini = ({
  courseCode,
  courseName,
  index,
  progress,
  total,
  isSelected = false,
}: {
  courseCode: string;
  courseName: string;
  index: number;
  progress: number;
  total: number;
  isSelected?: boolean;
}) => {
  const avatarIndex = (index % 3) + 1;
  const avatarSrc = `/img/ava${avatarIndex}.png`;

  return (
    <div
      className={`
            p-3 relative flex items-center h-24 md:h-30 mt-3
            border-b-2 ${isSelected ? "border-text-color" : "border-b-gray-200"} 
            transition-[border-color,border-radius] duration-200 ease-in-out
            ${
              isSelected
                ? "rounded-xl border-2 border-text-color shadow-lg bg-white/5"
                : "hover:border-2 hover:border-text-color hover:rounded-xl hover:shadow-lg"
            }
            cursor-pointer
        `}
    >
      <img
        src={avatarSrc}
        className="w-16 h-16 md:w-20 md:h-20 rounded-xl"
        alt="avatarCourse"
      />
      <div className="ml-3 relative flex-1">
        <p className="text-gray-400 font-semibold text-sm md:text-[15px]">
          {courseCode}
        </p>
        <p className="text-gray-900 font-bold text-sm md:text-[16px] mt-1 md:mt-2 line-clamp-2">
          {courseName}
        </p>
        <div className="mt-2">
          <ProgressLine progress={progress} total={total} />
        </div>
      </div>
      <div className="absolute right-3 md:right-5 flex items-center justify-end">
        <ChevronRightIcon
          className={`h-4 w-4 md:h-5 md:w-5 transition-all duration-300 ${
            isSelected ? "text-text-color translate-x-1" : "text-gray-400"
          }`}
        />
      </div>
    </div>
  );
};

export default MyCourse;
