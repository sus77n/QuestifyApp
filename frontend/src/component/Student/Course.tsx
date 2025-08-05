import React, { useState } from 'react';
import {ChevronRightIcon, MagnifyingGlassIcon} from "@heroicons/react/24/solid";
import {useNavigate} from "react-router-dom";
import {Spinner} from "../material/material";
import {CourseWithIndex, LearningUnitDTO} from "../../model/LearningUnitDTO";
import {useGetAllLearningUnitsByLevelQuery} from "../../API/service/learningUnit.service";
import {LearningUnitChildDto} from "../../model/LearningUnitChildDto";

const Course = () => {
    const [selectedCourse, setSelectedCourse] = useState<CourseWithIndex | null>(null);
    const [isMobileView, setIsMobileView] = useState(window.innerWidth < 768);
    const navigate = useNavigate();
    const queryResult = useGetAllLearningUnitsByLevelQuery(1) as {
        data?: LearningUnitDTO[];
        isLoading: boolean;
    };

    const allCourses = queryResult.data ?? [];
    const isAllCoursesLoading = queryResult.isLoading;

    const chapters = selectedCourse?.childUnits;
    const [searchQuery, setSearchQuery] = useState('');

    React.useEffect(() => {
        const handleResize = () => {
            setIsMobileView(window.innerWidth < 768);
        };

        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    const handleSelectCourse = () => {
        navigate(`/topics/${selectedCourse?.id}`);
    }

    const displayCourses: LearningUnitDTO[] = allCourses.filter((course) =>
        course.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        course.code.toLowerCase().includes(searchQuery.toLowerCase())
    );


    const isLoadingCourses = isAllCoursesLoading;

    if (isMobileView) {
        return (
            <div className="h-screen flex flex-col bg-white">
                {!selectedCourse ? (
                    // Mobile course list view
                    <div className="flex flex-col h-full">
                        <div className="bg-text-color pt-3 pb-3 pl-5 pr-5 rounded-b-xl sticky top-0 z-10">
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
                        <div className="p-3 overflow-y-auto flex-1">
                            {isLoadingCourses ? (
                                <div className="flex items-center justify-center h-full mb-2">
                                    <Spinner />
                                </div>
                            ) : (
                                displayCourses.map((course: LearningUnitDTO, index: number) => {
                                    // Explicitly type the course object
                                    const courseWithIndex: CourseWithIndex = {
                                        ...course,
                                        index
                                    };

                                    return (
                                        <div
                                            key={course.code}
                                            onClick={() => setSelectedCourse(courseWithIndex)}
                                            className="cursor-pointer"
                                        >
                                            <CardCourseMini
                                                courseCode={course.code}
                                                courseName={course.name}
                                                index={index}
                                                isSelected={selectedCourse?.code === course.code}
                                            />
                                        </div>
                                    );
                                })
                            )}
                        </div>
                    </div>
                ) : (
                    // Mobile course detail view
                    <div className="flex flex-col h-full overflow-y-auto pb-20">
                        <div className="bg-text-color pt-3 pb-3 pl-5 pr-5 sticky top-0 z-10 flex justify-between items-center">
                            <button
                                onClick={() => setSelectedCourse(null)}
                                className="text-white font-bold"
                            >
                                ← Back
                            </button>
                            <h1 className="text-xl font-semibold text-white">{selectedCourse.code}</h1>
                            <div className="w-8"></div> {/* Spacer for alignment */}
                        </div>

                        <div className="p-4">
                            <div className="flex flex-col items-center mb-6">
                                <img
                                    src={`/img/ava${(selectedCourse?.index % 3 + 1)}.png`}
                                    className="w-32 h-32 rounded-xl object-cover mb-4 shadow-md"
                                    alt="Course avatar"
                                />
                                <h2 className="text-2xl font-bold text-gray-900 text-center">
                                    {selectedCourse.name}
                                </h2>
                                <p className="text-gray-400 text-sm mt-2">
                                    Total exercises: {selectedCourse.numberOfExercise}
                                </p>
                            </div>

                            <div className="mb-6">
                                <h2 className="text-lg text-gray-900 font-bold mb-2">Description</h2>
                                <p className="text-sm text-justify">{selectedCourse.description}</p>
                            </div>

                            <div>
                                <h1 className="text-lg text-gray-900 font-bold mb-3">Content</h1>
                                <div className="space-y-4">
                                    {chapters && chapters.map((chapter: LearningUnitChildDto, index: number) => (
                                        <CardChapterMobile
                                            key={chapter.id}
                                            numberOfExercise={chapter.numberOfExercise}
                                            courseName={chapter.name}
                                            index={index + 1}
                                        />
                                    ))}
                                </div>
                            </div>
                        </div>

                        <div className="fixed bottom-0 left-0 right-0 bg-white p-4 shadow-lg border-t">
                            <button
                                className="bg-text-color text-white rounded-xl px-6 py-3 text-lg font-bold
                                border-2 border-text-color transition-colors duration-300
                                hover:bg-white hover:text-text-color
                                shadow-md hover:shadow-lg w-full"
                                onClick={() => handleSelectCourse()}
                            >
                                Join Course
                            </button>
                        </div>
                    </div>
                )}
            </div>
        );
    }

    return (
        <div className="h-screen flex flex-col md:flex-row ml-1">
            {/* Left sidebar - Desktop */}
            <div className="m-[8px] bg-white h-[98vh] w-full md:w-[35vw] rounded-xl flex flex-col position-relative">
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

            {/* Right content - Desktop */}
            <div className="m-[8px] ml-1 bg-white h-[98vh] w-full md:w-[65vw] rounded-xl flex flex-col p-4 md:p-8">
                {selectedCourse ? (
                    <div>
                        <div className="flex relative flex-col md:flex-row">
                            <div>
                                <div className="flex flex-col md:flex-row">
                                    <img
                                        src={`/img/ava${(selectedCourse?.index % 3 + 1)}.png`}
                                        className="w-32 h-32 md:w-44 md:h-44 rounded-xl object-cover mb-6 shadow-md"
                                        alt="Course avatar"
                                    />
                                    <div className="ml-0 md:ml-8 w-full md:w-[70%]">
                                        <p className="text-gray-400 font-semibold text-lg md:text-xl mb-1">
                                            {selectedCourse.code}
                                        </p>
                                        <h2 className="text-2xl md:text-[30px] font-bold text-gray-900">
                                            {selectedCourse.name}
                                        </h2>
                                        <p className="text-gray-400 text-sm md:text-md mt-2">
                                            Total of exercises: {selectedCourse.numberOfExercise}
                                        </p>
                                        <p className="text-gray-400 text-sm md:text-md">
                                            Created by: {selectedCourse.createdBy}
                                        </p>
                                    </div>
                                    <div className="flex pt-4 md:pt-14 justify-start md:absolute md:justify-end md:right-6">
                                        <button
                                            className="bg-text-color text-white rounded-xl px-8 py-3 md:px-12 md:py-4 text-lg md:text-xl font-bold
                                            border-2 border-text-color transition-colors duration-300
                                            hover:bg-white hover:text-text-color
                                            shadow-md hover:shadow-lg"
                                            onClick={() => handleSelectCourse()}
                                        >
                                            Join
                                        </button>
                                    </div>
                                </div>
                                <div className="mb-6 md:mb-10">
                                    <h2 className="text-lg md:text-[20px] text-gray-900 font-bold mb-1">Description</h2>
                                    <p className="text-sm md:text-[16px] text-justify">{selectedCourse.description}</p>
                                </div>
                            </div>
                        </div>
                        <div>
                            <h1 className="text-lg md:text-[20px] text-gray-900 font-bold mb-4">Content</h1>
                            <div className="flex flex-col md:flex-row md:flex-wrap justify-between w-full">
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
        <div className={`
            p-3 relative flex items-center h-24 md:h-30 mt-3
            border-b-2 ${isSelected ? "border-text-color" : "border-b-gray-200"} 
            transition-[border-color,border-radius] duration-200 ease-in-out
            ${isSelected
            ? "rounded-xl border-2 border-text-color shadow-lg bg-white/5"
            : "hover:border-2 hover:border-text-color hover:rounded-xl hover:shadow-lg"
        }
        `}>
            <img src={avatarSrc} className="w-16 h-16 md:w-20 md:h-20 rounded-xl" alt="avatarCourse"/>
            <div className="ml-3 relative">
                <p className="text-gray-400 font-semibold text-sm md:text-[15px]">{courseCode}</p>
                <p className="text-gray-900 font-bold text-sm md:text-[16px] mt-1 md:mt-2 line-clamp-2">{courseName}</p>
            </div>
            <div className="absolute right-3 md:right-5 flex items-center justify-end">
                <ChevronRightIcon className={`h-4 w-4 md:h-5 md:w-5 transition-all duration-300 ${
                    isSelected ? "text-text-color translate-x-1" : "text-gray-400"
                }`}/>
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
        <div className="flex w-full md:w-[30%] mb-5">
            <div className="w-12 h-12 md:w-14 md:h-14 flex justify-center items-center bg-background-color border rounded-md mr-3 text-lg md:text-xl font-semibold text-white">
                <p>{index}</p>
            </div>
            <div className="flex-1">
                <p className="text-base md:text-lg text-gray-900 mb-1 md:mb-2 line-clamp-2">{courseName}</p>
                <p className="text-xs md:text-sm text-gray-600">Exercises: {numberOfExercise}</p>
            </div>
        </div>
    )
}

const CardChapterMobile = ({
                               numberOfExercise,
                               courseName,
                               index,
                           }: {
    numberOfExercise: number;
    courseName: string;
    index: number;
}) => {
    return (
        <div className="flex items-start p-3 bg-gray-50 rounded-lg border border-gray-200">
            <div className="w-10 h-10 flex-shrink-0 flex justify-center items-center bg-background-color rounded-md mr-3 text-lg font-semibold text-white">
                {index}
            </div>
            <div>
                <h3 className="text-base font-medium text-gray-900">{courseName}</h3>
                <p className="text-xs text-gray-500 mt-1">{numberOfExercise} exercises</p>
            </div>
        </div>
    )
}

export default Course;