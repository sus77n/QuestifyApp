import React, { useState } from 'react';
// import { gsap } from 'gsap';
// import { ScrollTrigger } from 'gsap/ScrollTrigger';
// import { ScrollSmoother } from 'gsap/ScrollSmoother';
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
                                    className="w-44 h-44 rounded-xl object-cover mb-6 shadow-md"
                                    alt="Course avatar"
                                />
                                <div className="ml-8 w-[70%]">
                                    <p className="text-gray-400 font-semibold text-xl mb-1">
                                        {selectedCourse.code}
                                    </p>
                                    <h2 className="text-[30px] font-bold text-gray-900 h-24">
                                        {selectedCourse.name}
                                    </h2>
                                    <p className="text-gray-400 text-md ">
                                        Total of exercises: {selectedCourse.numberOfExercise}
                                    </p>
                                    <p className="text-gray-400 text-md">
                                        Created by: {selectedCourse.createdBy}
                                    </p>
                                </div>
                                <div className="flex pt-14 absolute justify-end right-6">
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
                        {/*    Content start*/}

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
                        {/*<TimelineChapters chapters={chapters || []}/>*/}
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
        <div className={`
    p-3 relative flex items-center h-30 mt-3
    border-b-2 ${isSelected ? "border-text-color" : "border-b-gray-200"} 
    transition-[border-color,border-radius] duration-200 ease-in-out
    ${isSelected
            ? "rounded-xl border-2 border-text-color shadow-lg bg-white/5" // Selected
            : "hover:border-2 hover:border-text-color hover:rounded-xl hover:shadow-lg" // Hover
        }
`}>
            {/* Content remains same */}
            <img src={avatarSrc} className="w-20 h-20 rounded-xl" alt="avatarCourse"/>
            <div className="ml-3 relative">
                <p className="text-gray-400 font-semibold text-[15px]">{courseCode}</p>
                <p className="text-gray-900 font-bold text-[16px] mt-2">{courseName}</p>
            </div>
            <div className="absolute right-5 flex items-center justify-end">
                <ChevronRightIcon className={`h-5 w-5 transition-all duration-300 ${
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
//
// gsap.registerPlugin(ScrollTrigger);
//
// type Chapter = {
//     id: number;
//     name: string;
//     numberOfExercise: number;
// };
//
// type Props = {
//     chapters: Chapter[];
// };
//
// const TimelineChapters: React.FC<Props> = ({ chapters }) => {
//     const containerRef = useRef<HTMLDivElement>(null);
//
//     useEffect(() => {
//         gsap.utils.toArray('.chapter-card').forEach((card, i) => {
//             gsap.fromTo(
//                 card,
//                 { x: i % 2 === 0 ? -50 : 50, opacity: 0 },
//                 {
//                     x: 0,
//                     opacity: 1,
//                     duration: 1,
//                     ease: 'power2.out',
//                     scrollTrigger: {
//                         trigger: card as Element,
//                         start: 'top 90%',
//                         toggleActions: 'play none none none',
//                     },
//                 }
//             );
//         });
//     }, [chapters]);
//
//     return (
//         <div ref={containerRef} className="relative mx-auto w-full max-w-5xl px-4 py-12">
//             <div className="absolute left-1/2 transform -translate-x-1/2 h-full w-1 bg-gray-300" />
//             <div className="grid grid-cols-3 gap-10">
//                 {chapters.map((chapter, index) => (
//                     <div
//                         key={chapter.id}
//                         className="chapter-card relative flex flex-col items-center text-center bg-white shadow-xl rounded-2xl p-4 h-[220px] w-[200px] mx-auto border border-gray-100"
//                     >
//                         <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2">
//                             <div className="w-4 h-4 bg-blue-500 rounded-full border-4 border-white shadow-lg" />
//                         </div>
//                         <div className="text-sm text-gray-400 mb-1">#{index + 1}</div>
//                         <h3 className="text-lg font-semibold text-gray-800">{chapter.name}</h3>
//                         <p className="text-sm text-gray-500 mt-2">Exercises: {chapter.numberOfExercise}</p>
//                     </div>
//                 ))}
//             </div>
//         </div>
//     );
// };



