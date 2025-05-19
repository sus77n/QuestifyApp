import {useEffect, useState} from 'react';
import {useNavigation} from '../../context/NavigationContext';
import {useNavigate, useParams} from "react-router-dom";
import {ChevronDownIcon, ChevronUpIcon, XCircleIcon} from "@heroicons/react/24/outline";
import {useGetExerciseOptionsQuery} from "../../API/service/exercise.service";
import {OptionDTO} from "../../model/OptionDTO";
import {ExerciseDTO} from "../../model/ExerciseDTO";
import {useGetChaptersByCourseQuery} from "../../API/service/course.service";

export const Topic = () => {
    const { setActiveTab } = useNavigation();
    const { courseId } = useParams();
    const navigate = useNavigate();
    const [selectedLesson, setSelectedLesson] = useState(null);
    const [selectedExercise, setSelectedExercise] = useState(null);

    const { data: chapters = [], isLoading, isError } = useGetChaptersByCourseQuery(Number(courseId), {
        skip: !courseId,
    });

    useEffect(() => {
        setActiveTab('My courses');
    }, []);

    const handleLessonClick = (lesson) => {
          setSelectedLesson(lesson);
        setSelectedExercise(lesson.exercises[0]);
    };

    if (isLoading) {
        return <div>Loading...</div>;
    }

    if (isError) {
        return <div>Error loading chapters</div>;
    }

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

function TopicCardDropdown({index, name, children}) {
    const [isOpen, setIsOpen] = useState(false);
    return (
        <div className="bg-white rounded-xl border-2 border-text-color mb-2">
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
                <div className="pt-2 border-t-2 border-text-color">
                    {children}
                </div>
            </div>
        </div>
    );
}

function LessonCard({index, name, isSelected, onClick}) {
    return (
        <div
            className={`p-3 mb-2 ml-2 rounded-lg cursor-pointer ${isSelected ? 'bg-text-color' : 'hover:bg-light-background'}`}
            onClick={onClick}
        >
            <h3 className={`font-medium text-text-color ${isSelected ? 'text-white' : 'text-text-color'}`}>Lesson {index}: {name}</h3>
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

const ExerciseCard = ({ exercise }: { exercise: ExerciseDTO }) => {
    const [selectedOption, setSelectedOption] = useState<number | null>(null);
    const [isSubmitted, setIsSubmitted] = useState(false);

    // Fetch options with proper typing
    const { data: options = [], isLoading, isError } =
        useGetExerciseOptionsQuery(exercise.id.toString(), {
            selectFromResult: ({ data }) => ({
                data: data as OptionDTO[],
                isLoading,
                isError
            })
        });

    const isCorrect = selectedOption === exercise.answer;

    const handleSubmit = () => setIsSubmitted(true);

    if (isLoading) return <div>Loading options...</div>;
    if (isError) return <div>Error loading options</div>;

    return (
        <div className="p-6 border border-gray-200 rounded-lg shadow-sm">
            <h3 className="font-medium text-xl mb-4 text-gray-800">{exercise.question}</h3>

            <div className="space-y-3 mb-6">
                {options.map((option) => (
                    <div key={option.id} className="flex items-center">
                        <input
                            type="radio"
                            id={`ex-${exercise.id}-opt-${option.id}`}
                            name={`exercise-${exercise.id}`}
                            checked={selectedOption === option.id}
                            onChange={() => !isSubmitted && setSelectedOption(option.id)}
                            className="hidden"
                            disabled={isSubmitted}
                        />
                        <label
                            htmlFor={`ex-${exercise.id}-opt-${option.id}`}
                            className={`flex items-center space-x-3 cursor-pointer w-full py-2 px-3 rounded-lg transition-all duration-200
                ${selectedOption === option.id ? 'bg-blue-50' : 'hover:bg-gray-50'}
                ${isSubmitted && selectedOption === option.id && (isCorrect ? 'bg-green-50' : 'bg-red-50')}`}
                        >
                            <OptionIndicator
                                isSelected={selectedOption === option.id}
                                isSubmitted={isSubmitted}
                                isCorrect={isCorrect}
                            />
                            <span className={`text-gray-700
                ${selectedOption === option.id ? 'font-medium text-text-color' : ''}
                ${isSubmitted && selectedOption === option.id && (isCorrect ? 'text-green-700' : 'text-red-700')}`}>
                {option.content}
              </span>
                        </label>
                    </div>
                ))}
            </div>
        </div>
    );
};

const OptionIndicator = ({
                             isSelected,
                             isSubmitted,
                             isCorrect
                         }: {
    isSelected: boolean;
    isSubmitted: boolean;
    isCorrect: boolean;
}) => (
    <div className={`relative flex-shrink-0 w-5 h-5 rounded-full border-2 
    ${isSelected ? 'border-text-color' : 'border-gray-300'}
    ${isSubmitted && isSelected && (isCorrect ? 'border-green-500' : 'border-red-500')}
    transition-all duration-200`}>
        {isSelected && (
            <div className={`absolute inset-1 rounded-full
        ${isSubmitted ? (isCorrect ? 'bg-green-500' : 'bg-red-500') : 'bg-text-color'}
        transform scale-100 opacity-100
        transition-all duration-200`}
            />
        )}
    </div>
);

export default ExerciseCard;