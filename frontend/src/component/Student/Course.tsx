import React, { useState} from "react";
import {ChevronRightIcon, MagnifyingGlassIcon} from "@heroicons/react/24/solid";
import {useNavigate} from "react-router-dom";
import {Spinner} from "../material/material";
import {LearningUnitDTO} from "../../model/LearningUnitDTO";
// import {LearningUnitChildDto} from "../../model/LearningUnitChildDto";
import {useGetAllLearningUnitsByLevelQuery} from "../../API/service/learningUnit.service";
import {LearningUnitChildDto} from "../../model/LearningUnitChildDto";

const Course = () => {
    const [selectedCourse, setSelectedCourse] = useState<(LearningUnitDTO & { index: number }) | null>(null);
    const navigate = useNavigate();
    const {data: allCourses = [], isLoading: isAllCoursesLoading} = useGetAllLearningUnitsByLevelQuery(1);
    const chapters = selectedCourse?.childUnits
    const [searchQuery, setSearchQuery] = useState('');

    const handleSelectCourse = () => {
        navigate(`/topics/${selectedCourse?.id}`);
    }

    const displayCourses = allCourses.filter((course) =>
        course.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        course.code.toLowerCase().includes(searchQuery.toLowerCase())
    );

    const isLoadingCourses = isAllCoursesLoading;

    return (
        <div className="h-screen flex ml-1">
            {/* Left sidebar */}
            <div className="m-[8px] bg-white h-[98vh] w-[35vw] rounded-xl flex flex-col position-relative">
                <div className="bg-text-color pt-3 pb-3 pl-5 pr-5 rounded-t-xl">
                    <h1 className="text-2xl font-semibold text-white">Courses</h1>
                </div>
                <div className="relative ml-3 mr-3 mt-3">
                    <input
                        placeholder="Find your course..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        className="border-2 border-gray-400 p-2 pl-5 pr-10 text-lg rounded-xl
                         focus:outline-none transition-all duration-200
                         placeholder-gray-400 placeholder-opacity-75
                         focus:shadow-md focus:border-text-color
                         text-gray-900 w-full"
                    />
                    <button
                        type="button"
                        className="absolute right-3 top-[25px] transform -translate-y-1/2"
                    >
                        <MagnifyingGlassIcon className="h-6 w-6 text-text-color"/>
                    </button>
                </div>
                <div
                    className="p-3 overflow-y-auto flex-1 overflow-x-hidden [scrollbar-width:none] [-ms-overflow-style:none] [&::-webkit-scrollbar]:hidden">
                    {isLoadingCourses ? (
                        <div className="flex items-center justify-center h-full mb-2">
                            <Spinner />
                        </div>
                    ) : (
                        displayCourses.map((course: LearningUnitDTO, index: number) => (
                            <div
                                key={course.code}
                                onClick={() => setSelectedCourse({...course, index})}
                                className="cursor-pointer"
                            >
                                <CardCourseMini
                                    courseCode={course.code}
                                    courseName={course.name}
                                    index={index}
                                    isSelected={selectedCourse?.code === course.code}
                                />
                            </div>
                        ))
                    )}
                </div>
            </div>

            <div className="m-[8px] ml-1 bg-white h-[98vh] w-[65vw] rounded-xl flex flex-col p-8">
                {selectedCourse ? (
                    <div>
                    <div className="flex relative">
                        <div>
                            <div className="flex">
                                <img
                                    src={`/img/ava${(selectedCourse?.index % 3 + 1)}.png`}
                                    className="w-40 h-40 rounded-xl object-cover mb-6 shadow-md"
                                    alt="Course avatar"
                                />
                                <div className="ml-8 w-[80%]">
                                    <p className="text-gray-400 font-semibold text-xl mb-2">
                                        {selectedCourse.code}
                                    </p>
                                    <h2 className="text-[30px] font-bold text-gray-900 mb-2">
                                        {selectedCourse.name}
                                    </h2>
                                    <p className="text-gray-400 text-lg mt-2">
                                        Total of exercises: {selectedCourse.numberOfExercise}
                                    </p>
                                    <p className="text-gray-400 text-lg mt-2">
                                        Created by: {(selectedCourse.createdBy?.firstName || "") + " " + (selectedCourse.createdBy?.lastName || "")}
                                    </p>
                                </div>
                                <div className="flex pt-14 absolute justify-end right-10">
                                    <button
                                        className="bg-text-color text-white rounded-xl px-12 py-4 text-xl font-bold
              border-2 border-text-color transition-colors duration-300
              hover:bg-white hover:text-text-color
              shadow-md hover:shadow-lg"
                                        onClick={() => handleSelectCourse()}
                                    >
                                        Join
                                    </button>
                                </div>
                            </div>
                            <div className="mb-10">
                                <h2 className="text-[20px] text-gray-900 font-bold mb-1">Description</h2>
                                <p className="text-[16px] text-justify">{selectedCourse.description}</p>
                            </div>
                        </div>
                    </div>
                    <div>
                        <h1 className="text-[20px] text-gray-900 font-bold mb-4">Content</h1>
                        <div className="flex flex-wrap justify-between w-full">
                            {chapters && chapters.map((chapter: LearningUnitChildDto, index: number) => (
                                <CardChapter
                                    key={chapter.id}
                                    numberOfExercise={chapter.numberOfExercise}
                                    courseName={chapter.name}
                                    index={index + 1}
                                />
                            ))}
                        </div>
                    </div>
                    </div>
                    ) : (
                    <div className="flex items-center justify-center h-full text-gray-400">
                        <p>Select a course to view details</p>
                    </div>
                )}
            </div>
        </div>
    );
};
export default Course;

const CardCourseMini = ({
                            courseCode,
                            courseName,
                            index,
                            isSelected,
                        }: {
    courseCode: string;
    courseName: string;
    index: number;
    isSelected: boolean;
}) => {
    const avatarIndex = (index % 3) + 1;
    const avatarSrc = `/img/ava${avatarIndex}.png`;

    return (
        <div className={`p-3 relative flex items-center h-30 mt-3 border-2 rounded-xl transition-all duration-200 ${
            isSelected
                ? "border-text-color shadow-lg bg-white/5"
                : "border-transparent hover:border-text-color hover:shadow-lg hover:bg-white/5"
        }`}>
            <img src={avatarSrc} className="w-20 h-20 rounded-xl" alt="avatarCourse"/>
            <div className="ml-3 relative">
                <p className="text-gray-400 font-semibold text-[15px]">{courseCode}</p>
                <p className="text-gray-900 font-bold text-[16px] mt-2">{courseName}</p>
            </div>
            <div className="absolute right-5 flex items-center justify-end">
                <ChevronRightIcon className={`h-5 w-5 transition-all duration-300   ${
                    isSelected ? "text-text-color translate-x-1" : "text-gray-400"
                } `}/>
            </div>
        </div>
    )
}

const CardChapter = ({
                            numberOfExercise,
                            courseName,
                            index,
                        }: {
    numberOfExercise: number;
    courseName: string;
    index: number;
}) => {
    return (
        <div className="flex w-[30%] mb-5">
            <div className="w-14 h-14 flex justify-center items-center bg-background-color border rounded-md mr-3 text-xl font-semibold text-white">
                <p>{index}</p>
            </div>
            <div className="">
                <p className="text-lg text-gray-900 mb-2">{courseName}</p>
                <p className="text-sm text-gray-600">Number of exercises: {numberOfExercise}</p>
            </div>
        </div>
    )
}