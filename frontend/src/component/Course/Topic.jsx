import {useEffect, useState} from 'react';
import {useNavigation} from '../../context/NavigationContext';
import {useNavigate, useParams} from "react-router-dom";
import {ChevronDownIcon, ChevronUpIcon, XCircleIcon} from "@heroicons/react/24/outline";

const Topic = () => {
    const {setActiveTab} = useNavigation();
    const {courseId} = useParams();
    const navigate = useNavigate();
    const [chapters, setChapters] = useState([]);
    const [selectedLesson, setSelectedLesson] = useState(null);
    const [selectedExercise, setSelectedExercise] = useState(null);

    useEffect(() => {
        if (!courseId) return;

        const fetchData = async () => {
            try {
                const response = await fetch(`/api/chapter/${courseId}`);
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                const data = await response.json();
                setChapters(data);
            } catch (error) {
                console.error("Error fetching chapters:", error);
            }
        };

        fetchData();
    }, [courseId]);

    useEffect(() => {
        setActiveTab('My courses');
    }, []);

    const handleLessonClick = (lesson) => {
        setSelectedLesson(lesson);
        setSelectedExercise(lesson.exercises[0]);
    };


    return (
        <div className="h-screen flex">

            <div className="m-[8px] bg-white h-[98vh] w-[28vw] rounded-xl flex flex-col p-4">
                <div className="flex justify-between">
                    <h1 className="text-3xl font-semibold text-text-color">CSE 100</h1>
                    <button
                        onClick={() => navigate('/my-courses')}
                        className="mb-4 p-1 rounded-full transition-all duration-200 hover:bg-red-50 hover:shadow-sm"
                    >
                        <XCircleIcon
                            className="w-8 h-8 text-red-500 hover:text-red-600 transition-colors duration-200 transform hover:scale-105"/>
                    </button>
                </div>
                <div
                    className="mt-3 overflow-y-auto flex-1 overflow-x-hidden [scrollbar-width:none] [-ms-overflow-style:none] [&::-webkit-scrollbar]:hidden">
                    {chapters.map((topic, i) => (
                        <TopicCardDropdown key={topic.id} index={i+1} name={topic.title}>
                            {topic.lessons
                                .map((lesson, i) => (
                                    <LessonCard
                                        key={lesson.id}
                                        index={i+1}
                                        name={lesson.title}
                                        isSelected={selectedLesson?.id === lesson.id}
                                        onClick={() => handleLessonClick(lesson)}
                                    >
                                    </LessonCard>
                                ))}
                        </TopicCardDropdown>
                    ))}
                </div>
            </div>
            <div className="m-[8px] ml-1 h-[98vh] w-[70vw] flex flex-col ">
                    {selectedLesson ? (
                            <div className="h-[10vh] mb-2 bg-white rounded-xl flex">
                                {selectedLesson.exercises.map((exe, i) => (
                                    <IndexExerciseButton
                                        key={exe.id}
                                        index={i + 1}
                                        isDone={false}
                                        isActive={selectedExercise?.id === exe.id}
                                        onClick={() => setSelectedExercise(exe)}
                                    />
                                ))}
                            </div>
                    ) : (
                        <div className="flex-1 flex items-center justify-center bg-white rounded-xl">
                            <div className="text-xl text-gray-500">
                                Select a lesson to begin
                            </div>
                        </div>
                    )}
                <div className="h-[90vh] bg-white rounded-xl">
                    {selectedExercise ? (
                        <ExerciseCard exercise={selectedExercise}/>
                    ) : (
                        <div className="h-full flex items-center justify-center">
                            {selectedLesson ? (
                                <p className="text-gray-500">No exercises available for this lesson</p>
                            ) : (
                                <p className="text-gray-500">Select a lesson to begin</p>
                            )}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Topic;

function TopicCardDropdown({index, name, children}) {
    const [isOpen, setIsOpen] = useState(false);

    return (
        <div className="bg-white rounded-xl border-2 border-background-color mb-2">
            <div
                className="flex justify-between items-center p-3 rounded-xl cursor-pointer "
                onClick={() => setIsOpen(!isOpen)}
            >
                <div className="flex items-center">
                    <h3 className="font-medium text-text-color">Chapter {index} : {name}</h3>
                </div>
                {isOpen ? (
                    <ChevronUpIcon className="w-5 h-5 text-gray-500 transition-transform"/>
                ) : (
                    <ChevronDownIcon className="w-5 h-5 text-gray-500 transition-transform"/>
                )}
            </div>

            <div className={`px-4 pb-4 ${isOpen ? 'block' : 'hidden'}`}>
                <div className="pt-2 border-t border-gray-100">
                    {children}
                </div>
            </div>
        </div>
    );
}

function LessonCard({index, name, isSelected, onClick}) {
    return (
        <div
            className={`p-3 mb-2 ml-2 rounded-lg cursor-pointer ${isSelected ? 'bg-background-color' : 'hover:bg-light-background'}`}
            onClick={onClick}
        >
            <h3 className="font-medium text-text-color">Lesson {index}: {name}</h3>
        </div>
    );
}

function IndexExerciseButton({ index, isDone, isActive, onClick }) {
    return (
        <button
            className={`relative m-2 flex justify-center items-center rounded-xl w-[65px] h-[65px]
                border-2 ${isDone ? 'border-green-400' :
                isActive ? 'border-background-color' : 'border-gray-300'}
                ${isActive ? 'bg-light-background' : 'bg-white'}
                transition-all duration-200
                hover:border-blue-300 hover:bg-blue-50
                active:scale-95
                focus:outline-none focus:ring-2 focus:ring-blue-200`}
            onClick={onClick}
        >
            <h1 className={`font-bold text-2xl ${
                isDone ? 'text-green-500' :
                    isActive ? 'text-text-color' : 'text-gray-700'
            }`}>
                {index}
            </h1>

            {isDone && (
                <div className="absolute bottom-1 right-1">
                    <svg className="w-5 h-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="M5 13l4 4L19 7"/>
                    </svg>
                </div>
            )}
        </button>
    );
}

function ExerciseCard({ exercise }) {
    const [selectedOption, setSelectedOption] = useState(null);
    const [isSubmitted, setIsSubmitted] = useState(false);
    const isCorrect = selectedOption === exercise.answer;

    const handleSubmit = () => {
        setIsSubmitted(true);
    };

    return (
        <div className="p-6 border border-gray-200 rounded-lg shadow-sm">
            <h3 className="font-medium text-xl mb-4 text-gray-800">{exercise.question}</h3>
            <div className="space-y-3 mb-6">
                {/*{exercise.options.map((option, i) => (*/}
                {/*    <div key={i} className="flex items-center">*/}
                {/*        <input*/}
                {/*            type="radio"*/}
                {/*            id={`ex-${exercise.index}-opt-${i}`}*/}
                {/*            name={`exercise-${exercise.index}`}*/}
                {/*            checked={selectedOption === i}*/}
                {/*            onChange={() => !isSubmitted && setSelectedOption(i)}*/}
                {/*            className="hidden"*/}
                {/*            disabled={isSubmitted}*/}
                {/*        />*/}
                {/*        <label*/}
                {/*            htmlFor={`ex-${exercise.index}-opt-${i}`}*/}
                {/*            className={`flex items-center space-x-3 cursor-pointer w-full py-2 px-3 rounded-lg transition-all duration-200*/}
                {/*                ${selectedOption === i ? 'bg-blue-50' : 'hover:bg-gray-50'}*/}
                {/*                ${isSubmitted && selectedOption === i && (isCorrect ? 'bg-green-50' : 'bg-red-50')}`}*/}
                {/*        >*/}
                {/*            <div className={`relative flex-shrink-0 w-5 h-5 rounded-full border-2 */}
                {/*                ${selectedOption === i ? 'border-text-color' : 'border-gray-300'}*/}
                {/*                ${isSubmitted && selectedOption === i && (isCorrect ? 'border-green-500' : 'border-red-500')}*/}
                {/*                transition-all duration-200`}>*/}
                {/*                {selectedOption === i && (*/}
                {/*                    <div className={`absolute inset-1 rounded-full*/}
                {/*                        ${isSubmitted ? (isCorrect ? 'bg-green-500' : 'bg-red-500') : 'bg-text-color'}*/}
                {/*                        transform scale-100 opacity-100*/}
                {/*                        transition-all duration-200`}*/}
                {/*                    />*/}
                {/*                )}*/}
                {/*            </div>*/}
                {/*            <span className={`text-gray-700*/}
                {/*                ${selectedOption === i ? 'font-medium text-text-color' : ''}*/}
                {/*                ${isSubmitted && selectedOption === i && (isCorrect ? 'text-green-700' : 'text-red-700')}`}>*/}
                {/*                {option}*/}
                {/*            </span>*/}
                {/*        </label>*/}
                {/*    </div>*/}
                {/*))}*/}
            </div>

            {/*{!isSubmitted ? (*/}
            {/*    <div className="flex justify-end">*/}
            {/*        <button*/}
            {/*            className={`px-6 py-2 rounded-lg bg-text-color text-white font-medium*/}
            {/*                transition-all duration-200*/}
            {/*                hover:bg-opacity-90*/}
            {/*                active:scale-95*/}
            {/*                ${selectedOption === null ? 'opacity-50 cursor-not-allowed' : ''}`}*/}
            {/*            disabled={selectedOption === null}*/}
            {/*            onClick={handleSubmit}*/}
            {/*        >*/}
            {/*            Submit*/}
            {/*        </button>*/}
            {/*    </div>*/}
            {/*) : (*/}
            {/*    <div*/}
            {/*        className={`mt-4 p-4 rounded-lg flex items-center ${isCorrect ? 'bg-green-50 text-green-800' : 'bg-red-50 text-red-800'}`}>*/}
            {/*        {isCorrect ? (*/}
            {/*            <>*/}
            {/*                <img*/}
            {/*                    src={'/img/correct.gif'}*/}
            {/*                    alt={'Correct'}*/}
            {/*                    className="w-[200px] object-contain"*/}
            {/*                />*/}
            {/*                <span className="font-medium ml-4">Correct</span>*/}
            {/*            </>*/}
            {/*        ) : (*/}
            {/*            <>*/}
            {/*                <img*/}
            {/*                    src={'/img/incorrect.gif'}*/}
            {/*                    alt={'Incorrect'}*/}
            {/*                    className="w-[200px] object-contain"*/}
            {/*                />*/}
            {/*                <span className="font-medium ml-4">Incorrect</span>*/}
            {/*            </>*/}
            {/*        )}*/}
            {/*    </div>*/}


            {/*)}*/}

            {/*<div className="flex justify-end mt-4">*/}
            {/*    <button*/}
            {/*        className="px-6 py-2 rounded-lg bg-gray-200 text-gray-700 font-medium*/}
            {/*                    transition-all duration-200 hover:bg-gray-300"*/}
            {/*        onClick={() => {*/}
            {/*            setIsSubmitted(false);*/}
            {/*            setSelectedOption(null);*/}
            {/*        }}*/}
            {/*    >*/}
            {/*        Try Again*/}
            {/*    </button>*/}
            {/*</div>*/}
        </div>
    );
}
