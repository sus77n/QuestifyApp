import React, { useState} from "react";
import {ChevronRightIcon, MagnifyingGlassIcon} from "@heroicons/react/24/solid";
import {useNavigate} from "react-router-dom";
import {useGetChaptersByCourseQuery, useGetCoursesQuery, useSearchCoursesQuery} from '../../API/service/course.service'
import {CourseDTO} from "../../model/CourseDTO";
import {Spinner} from "../material/material";
import {ChapterDTO} from "../../model/ChapterDTO";


const Course = () => {
    const [selectedCourse, setSelectedCourse] = useState<(CourseDTO & { index: number }) | null>(null);
    const [searchTerm, setSearchTerm] = useState("");
    const navigate = useNavigate();

    const {data: allCourses = [], isLoading: isAllCoursesLoading} = useGetCoursesQuery();

    const {
        data: searchedCourses = [],
        isLoading: isSearchLoading
    } = useSearchCoursesQuery(searchTerm, {
        skip: searchTerm.length < 1
    }) as { data: CourseDTO[], isLoading: boolean };

    const {
        data: chapters = [],
        isLoading: isChaptersLoading,
    } = useGetChaptersByCourseQuery(selectedCourse?.id ?? 0, {
        skip: !selectedCourse
    });

    const handleSelectCourse = () => {
        navigate(`/topics/${selectedCourse?.id}`);
    }

    const displayCourses = searchTerm.length > 0 ? searchedCourses : allCourses;

    const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSearchTerm(e.target.value);
    };

    const isLoadingCourses = isAllCoursesLoading || isSearchLoading;

    return (
        <div className="h-screen flex ml-1">
            {/* Left sidebar */}
            <div className="m-[8px] bg-white h-[98vh] w-[28vw] rounded-xl flex flex-col position-relative">
                <div className="bg-text-color pt-3 pb-3 pl-5 pr-5 rounded-t-xl">
                    <h1 className="text-2xl font-semibold text-white">Courses</h1>
                </div>
                <div className="relative ml-3 mr-3 mt-3">
                    <input
                        placeholder="Find your course..."
                        className="border-2 border-text-color p-2 pl-5 pr-10 text-lg rounded-xl
                         focus:outline-none transition-all duration-200
                         placeholder-gray-400 placeholder-opacity-75
                         focus:shadow-md focus:ring-1 focus:ring-primary focus:ring-opacity-50
                         text-text-color w-full"
                        value={searchTerm}
                        onChange={handleSearchChange}
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
                        displayCourses.map((course: CourseDTO, index: number) => (
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
                    {searchTerm.length > 0 && searchedCourses.length === 0 && !isSearchLoading && (
                        <div className="text-center py-4 text-gray-400">No courses found</div>
                    )}
                </div>
            </div>

            <div className="m-[8px] ml-1 bg-white h-[98vh] w-[64vw] rounded-xl flex flex-col p-8">
                {selectedCourse ? (
                    <div className="flex relative">
                        <div>
                            <div className="flex">
                                <img
                                    src={`/img/ava${(selectedCourse?.index % 3 + 1)}.png`}
                                    className="w-48 h-48 rounded-xl object-cover mb-6"
                                    alt="Course avatar"
                                />
                                <div className="ml-8 w-1/2">
                                    <p className="text-gray-400 font-semibold text-2xl mb-2">
                                        {selectedCourse.code}
                                    </p>
                                    <h2 className="text-[35px] font-bold text-text-color mb-2">
                                        {selectedCourse.name}
                                    </h2>
                                </div>
                                <div className="flex pt-14 absolute justify-end right-8">
                                    <button
                                        className="bg-text-color text-white rounded-xl px-16 py-4 text-2xl font-bold
                                         border-2 border-text-color transition-colors duration-300
                                         hover:bg-white hover:text-text-color"
                                        onClick={() => handleSelectCourse()}
                                    >
                                        Join
                                    </button>
                                </div>
                            </div>
                            <div className="mb-5">
                                <h2 className="text-[20px] text-text-color font-bold mb-1">Description</h2>
                                <p className="text-[16px] text-justify">{selectedCourse.description}</p>
                            </div>
                            {/*<div className="flex items-start justify-between pt-4">*/}
                            {/*    <div>*/}
                            {/*        <h1 className="text-[20px] text-text-color font-bold mb-2">Statistic</h1>*/}
                            {/*        <div className="w-[27vw] mr-14">*/}
                            {/*            <div className="flex justify-between bg-background-color rounded-2xl p-4">*/}
                            {/*                <div className="border-l-2 border-text-color pl-2 text-text-color">*/}
                            {/*                    <h3 className="font-bold">Lessons</h3>*/}
                            {/*                    <p className="text-2xl">10</p>*/}
                            {/*                </div>*/}
                            {/*                <div className="border-l-2 border-text-color pl-2 text-text-color">*/}
                            {/*                    <h3 className="">Exercises</h3>*/}
                            {/*                    <p className="text-2xl">150</p>*/}
                            {/*                </div>*/}
                            {/*                <div className="border-l-2 border-text-color pl-2 text-text-color">*/}
                            {/*                    <h3 className="">Participants</h3>*/}
                            {/*                    <p className="text-2xl">300</p>*/}
                            {/*                </div>*/}
                            {/*            </div>*/}
                            {/*        </div>*/}
                            {/*    </div>*/}
                            {/*</div>*/}
                            <div>
                                <h1 className="text-[20px] text-text-color font-bold mb-2">Content</h1>
                                <div className="relative overflow-auto rounded">
                                    <table className="w-full text-left rtl:text-right rounded-2xl text-[15px]">
                                        <colgroup>
                                            <col style={{ width: '10%' }} />
                                            <col style={{ width: '60%' }} />
                                            <col style={{ width: '30%' }} />
                                        </colgroup>
                                        <thead className="uppercase bg-text-color text-[15px] font-medium text-white">
                                            <tr>
                                                <th scope="col" className="px-6 py-3 text-[15px] font-medium">Index</th>
                                                <th scope="col" className="px-6 py-3 text-[15px] font-medium">Name</th>
                                                <th scope="col" className="px-6 py-3 text-[15px] font-medium">Lessons</th>
                                            </tr>
                                        </thead>
                                        {isChaptersLoading ? (
                                            <tbody>
                                            <tr className="border-t border-gray-200">
                                                <td colSpan={3} className="px-6 py-4 text-center">
                                                    <Spinner />
                                                </td>
                                            </tr>
                                            </tbody>
                                        ):(
                                            <tbody>
                                            {/*{chapters.map((chapter: ChapterDTO, index: number) => (*/}
                                            {/*    <tr className="border-t border-gray-200">*/}
                                            {/*        <th scope="row" className="px-6 py-4">{index +1}</th>*/}
                                            {/*        <td className="px-6 py-4">{chapter.title}</td>*/}
                                            {/*        <td className="px-6 py-4">{chapter.lessons.length}</td>*/}
                                            {/*    </tr>*/}
                                            {/*))}*/}
                                            <tr className="border-t border-text-color bg-gray-100">
                                                <th scope="row" className="px-6 py-4">1</th>
                                                <td className="px-6 py-4">The course provides</td>
                                                <td className="px-6 py-4">10</td>
                                            </tr>
                                            <tr className="  bg-gray-200">
                                                <th scope="row" className="px-6 py-4">1</th>
                                                <td className="px-6 py-4">The course provides</td>
                                                <td className="px-6 py-4">10</td>
                                            </tr>
                                            <tr className="bg-gray-100">
                                                <th scope="row" className="px-6 py-4">1</th>
                                                <td className="px-6 py-4">The course provides</td>
                                                <td className="px-6 py-4">10</td>
                                            </tr>
                                            <tr className="bg-gray-200">
                                                <th scope="row" className="px-6 py-4">1</th>
                                                <td className="px-6 py-4">The course provides</td>
                                                <td className="px-6 py-4">10</td>
                                            </tr>

                                            </tbody>
                                        )}
                                    </table>
                                </div>
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
                <p className="text-text-color font-bold text-[18px] mt-2">{courseName}</p>
            </div>
            <div className="absolute right-5 flex items-center justify-end">
                <ChevronRightIcon className={`h-5 w-5 transition-all duration-300   ${
                    isSelected ? "text-text-color translate-x-1" : "text-gray-400"
                } `}/>
            </div>
        </div>
    )
}