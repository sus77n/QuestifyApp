import React, {useEffect, useState} from "react";
import {ChevronRightIcon, MagnifyingGlassIcon} from "@heroicons/react/24/solid";
import {useNavigate} from "react-router-dom";
import {useGetChaptersByCourseQuery, useGetCoursesQuery, useSearchCoursesQuery} from '../../API/service/course.service'


const Course = () => {
    const [selectedCourse, setSelectedCourse] = useState(null);
    const [searchTerm, setSearchTerm] = useState("");
    const navigate = useNavigate();

    const {
        data: allCourses = [],
        isLoading: isAllCoursesLoading
    } = useGetCoursesQuery();

    const {
        data: searchedCourses = [],
        isLoading: isSearchLoading
    } = useSearchCoursesQuery(searchTerm, {
        skip: searchTerm.length < 1
    });

    const {
        data: chapters = [],
        isLoading: isChaptersLoading,
    } = useGetChaptersByCourseQuery(selectedCourse?.id, {
        skip: !selectedCourse
    });

    const displayCourses = searchTerm.length > 0 ? searchedCourses : allCourses;

    const handleSearchChange = (e) => {
        setSearchTerm(e.target.value);
    };

    if (isAllCoursesLoading) {
        return <div>Loading courses...</div>;
    }


    return (
        <div className="h-screen flex ml-1">
            {/* Left sidebar */}
            <div className="m-[8px] bg-white h-[98vh] w-[28vw] rounded-xl flex flex-col p-4">
                <h1 className="text-3xl font-semibold text-text-color">Courses</h1>
                <div className="relative">
                    <input
                        placeholder="Find your course..."
                        className="border-2 border-text-color p-2 pl-5 pr-10 text-lg rounded-xl mt-4
                         focus:outline-none transition-all duration-200
                         placeholder-gray-400 placeholder-opacity-75
                         focus:shadow-md focus:ring-1 focus:ring-primary focus:ring-opacity-50
                         text-text-color w-full"
                        value={searchTerm}
                        onChange={handleSearchChange}
                    />
                    <button
                        type="button"
                        className="absolute right-3 top-[38px] transform -translate-y-1/2"
                    >
                        <MagnifyingGlassIcon className="h-6 w-6 text-text-color"/>
                    </button>
                </div>
                <div
                    className="mt-3 overflow-y-auto flex-1 overflow-x-hidden [scrollbar-width:none] [-ms-overflow-style:none] [&::-webkit-scrollbar]:hidden">
                    {isSearchLoading && searchTerm.length > 0 ? (
                        <div className="text-center py-4">Searching...</div>
                    ) : (
                        displayCourses.map((course, index) => (
                            <div
                                key={course.courseCode}
                                onClick={() => setSelectedCourse({...course, index})}
                                className="cursor-pointer"
                            >
                                <CardCourseMini
                                    index={index}
                                    courseCode={course.courseCode}
                                    courseName={course.courseName}
                                    isSelected={selectedCourse?.courseCode === course.courseCode}
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
                                        {selectedCourse.courseCode}
                                    </p>
                                    <h2 className="text-[40px] font-bold text-text-color mb-2">
                                        {selectedCourse.courseName}
                                    </h2>
                                </div>
                                <div className="flex pt-14 absolute justify-end right-8">
                                    <button
                                        className="bg-text-color text-white rounded-xl px-16 py-4 text-2xl font-bold
                                         border-2 border-text-color transition-colors duration-300
                                         hover:bg-white hover:text-text-color"
                                        onClick={() => navigate(`/topics/${selectedCourse.id}`)}
                                    >
                                        Join
                                    </button>
                                </div>
                            </div>
                            <p className="mt-5">
                                {selectedCourse.description}
                            </p>
                            <div className="pt-4 w-[60vw]">
                                <h2 className="text-text-color font-semibold text-[28px] mb-5">Content</h2>
                                {isChaptersLoading ? (
                                    <div>Loading chapters...</div>
                                ) : (
                                    <div
                                        className="bg-background-color rounded-xl grid grid-cols-2 gap-x-8 gap-y-4 p-4">
                                        {chapters.map((topic) => (
                                            <div key={topic.id}>
                                                <TopicCard
                                                    index={topic.id}
                                                    numberLesson={topic.lessons.length}
                                                    name={topic.title}
                                                />
                                            </div>
                                        ))}
                                    </div>
                                )}
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

function CardCourseMini({courseName, courseCode, index, isSelected}) {
    const avatarIndex = (index % 3) + 1; // 1, 2, 3, 1, 2, 3, ...
    const avatarSrc = `/img/ava${avatarIndex}.png`;

    return (
        <div className={`p-3 relative flex items-center h-30 mt-3 border-2 rounded-xl transition-all duration-200 ${
            isSelected
                ? "border-text-color shadow-lg bg-white/5"
                : "border-transparent hover:border-text-color hover:shadow-lg hover:bg-white/5"
        }`}>
            <img src={avatarSrc} className="w-20 h-20 rounded-xl"/>
            <div className="ml-3 relative">
                <p className="text-gray-400 font-semibold text-[15px]">{courseCode}</p>
                <p className="text-text-color font-bold text-[18px] mt-2">{courseName}</p>
            </div>
            <div className="absolute right-5 flex items-center justify-end">
                <ChevronRightIcon className={`h-5 w-5 transition-all duration-300 ${
                    isSelected ? "text-primary translate-x-1" : "text-gray-400"
                }`}/>
            </div>
        </div>
    )
}

function TopicCard({name, numberLesson, index}) {
    return (
        <div className="flex gap-2 text-text-color">
            <h1 className="text-4xl bg-white pl-7 pr-7 pt-5 rounded-xl font-bold">{index}</h1>
            <div className="bg-white p-3 pl-5 pr-5 rounded-xl w-full">
                <h1 className="text-xl font-semibold mb-2">{name}</h1>
                <div className="flex justify-between">
                    <p>Lessons: {numberLesson}</p>
                </div>
            </div>
        </div>
    )
}