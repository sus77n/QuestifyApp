import React, {useEffect, useLayoutEffect, useMemo, useRef, useState} from "react";
import {
  ChevronRightIcon,
  MagnifyingGlassIcon,
  ArrowLeftIcon,
} from "@heroicons/react/24/solid";
import { useNavigate } from "react-router-dom";
import { Spinner } from "../material/material";
import {LearningUnitDTO, LearningUnitWithChildren} from "../../model/LearningUnitDTO";
import {
    useGetAllLearningUnitsByLevelQuery,
    useGetLearningUnitWithChildrenQuery
} from "../../API/service/learningUnit.service";
import gsap from "gsap";
import { ScrollTrigger } from "gsap/ScrollTrigger";
gsap.registerPlugin(ScrollTrigger);

const Course = () => {
    // const userId = Number(localStorage.getItem("userId"));
    const [selectedCourseId, setSelectedCourseId] = useState<string | null>(null);
    const { data: selectedCourse, isLoading: isCourseDetailLoading } =
        useGetLearningUnitWithChildrenQuery(
            { id: selectedCourseId! },
            { skip: selectedCourseId === null }
        );

  const [isMobileView, setIsMobileView] = useState(false);
  const navigate = useNavigate();
  const { data: allCourses = [], isLoading: isAllCoursesLoading } =
      useGetAllLearningUnitsByLevelQuery(1);

  const chapters = selectedCourse?.children || [];
  const [searchQuery, setSearchQuery] = useState("");

  useEffect(() => {
    const handleResize = () => {
      setIsMobileView(window.innerWidth < 768);
    };
    handleResize(); // chạy lần đầu
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  const handleSelectCourse = () => {
    if (selectedCourse?.id) {
      navigate(`/topics/${selectedCourse.id}`);
    }
  };

  const displayCourses: LearningUnitDTO[] = useMemo(
      () =>
          allCourses.filter(
              (course) =>
                  course.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                  course.code.toLowerCase().includes(searchQuery.toLowerCase()),
          ),
      [allCourses, searchQuery],
  );

  const isLoadingCourses = isAllCoursesLoading || isCourseDetailLoading;

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
                      className="absolute right-3 top-1/2 -translate-y-1/2"
                  >
                    <MagnifyingGlassIcon className="h-6 w-6 text-text-color" />
                  </button>
                </div>
                <div className="p-3 overflow-y-auto flex-1">
                  {isLoadingCourses ? (
                      <div className="flex items-center justify-center h-full mb-2">
                        <Spinner />
                      </div>
                  ) : displayCourses.length > 0 ? (
                      displayCourses.map((course: LearningUnitDTO, index: number) => {
                        return (
                            <div
                                key={course.id}
                                onClick={() => setSelectedCourseId(course.id)}
                                className="cursor-pointer"
                            >
                              <CardCourseMini
                                  courseCode={course.code}
                                  courseName={course.name}
                                  index={index}
                                  isSelected={selectedCourseId === course.id}
                              />
                            </div>
                        );
                      })
                  ) : (
                      <p className="text-center text-gray-400 mt-10">
                        No courses found
                      </p>
                  )}
                </div>
              </div>
          ) : (
              // Mobile course detail view
              <div className="flex flex-col h-full overflow-y-auto pb-20">
                <div className="bg-text-color pt-3 pb-3 pl-5 sticky top-0 z-10 flex justify-between items-center">
                  <button
                      type="button"
                      onClick={() => setSelectedCourseId(selectedCourseId)}
                      className="flex items-center text-white font-bold"
                  >
                    <ArrowLeftIcon className="h-5 w-5 mr-1" /> Back
                  </button>
                  <h1 className="text-xl font-semibold text-white">
                    {selectedCourse.code}
                  </h1>
                  <div className="w-16" /> {/* Spacer for alignment */}
                </div>

                <div className="p-4">
                  <div className="flex flex-col items-center mb-6">
                    <img
                        src={`/img/ava1}.png`}
                        className="w-32 h-32 rounded-xl object-cover mb-4 shadow-md"
                        alt="Course avatar"
                    />
                    <h2 className="text-2xl font-bold text-gray-900 text-center">
                      {selectedCourse.name}
                    </h2>
                    <p className="text-gray-400 text-sm mt-2">
                      Total exercises: {selectedCourse.numberOfExercise}
                    </p>
                    <p className="text-gray-400 text-sm mt-1">
                      Created by: {selectedCourse.createdBy || "Unknown"}
                    </p>
                  </div>

                  <div className="mb-6">
                    <h2 className="text-lg text-gray-900 font-bold mb-2">
                      Description
                    </h2>
                    <div className="prose prose-sm text-gray-700 max-w-none">
                      {selectedCourse.description}
                    </div>
                  </div>

                  <div>
                    <h1 className="text-lg text-gray-900 font-bold mb-3">
                      Content
                    </h1>
                    <div className="space-y-4">
                      {chapters &&
                          chapters.map(
                              (chapter: LearningUnitWithChildren, index: number) => (
                                  <CardChapterMobile
                                      key={chapter.id}
                                      numberOfExercise={chapter.numberOfExercise}
                                      courseName={chapter.name}
                                      index={index + 1}
                                  />
                              ),
                          )}
                    </div>
                  </div>
                </div>

                <div className="fixed bottom-0 left-0 right-0 bg-white p-4 shadow-lg border-t">
                  <button
                      type="button"
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
                className="absolute right-3 top-1/2 -translate-y-1/2"
            >
              <MagnifyingGlassIcon className="h-6 w-6 text-text-color" />
            </button>
          </div>
          <div className="p-3 overflow-y-auto flex-1 overflow-x-hidden [scrollbar-width:none] [-ms-overflow-style:none] [&::-webkit-scrollbar]:hidden">
            {isLoadingCourses ? (
                <div className="flex items-center justify-center h-full mb-2">
                  <Spinner />
                </div>
            ) : displayCourses.length > 0 ? (
                displayCourses.map((course: LearningUnitDTO, index: number) => (
                    <div
                        key={course.id}
                        onClick={() => setSelectedCourseId(course.id)}
                        className="cursor-pointer"
                    >
                      <CardCourseMini
                          courseCode={course.code}
                          courseName={course.name}
                          index={index}
                          isSelected={selectedCourse?.id === course.id}
                      />
                    </div>
                ))
            ) : (
                <p className="text-center text-gray-400 mt-10">
                  No courses found
                </p>
            )}
          </div>
        </div>

        {/* Right content - Desktop */}
        <div className="m-[8px] ml-1 bg-white h-[98vh] w-full md:w-[65vw] rounded-xl flex flex-col p-4 md:p-8">
          {selectedCourse ? (
              <div>
                <div className="flex relative flex-col md:flex-row h-[40vh]">
                  <div>
                    <div className="flex flex-col md:flex-row">
                      <img
                          src={`/img/ava1.png`}
                          className="w-32 h-32 md:w-44 md:h-44 rounded-xl object-cover mb-6 shadow-md"
                          alt="Course avatar"
                      />
                      <div className="ml-0 md:ml-8 w-full md:w-[60%]">
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
                            type="button"
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
                      <h2 className="text-lg md:text-[20px] text-gray-900 font-bold mb-1.5">
                        Description
                      </h2>
                        <p className="text-sm md:text-base leading-relaxed text-gray-700 whitespace-pre-line line-clamp-3">
                            {selectedCourse.description}
                        </p>
                    </div>
                  </div>
                </div>
                  <div className="h-[52vh]">
                      <VerticalJourneyMap
                          key={selectedCourse.id}
                          chapters={chapters || []}
                      />
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
      `}
      >
        <img
            src={avatarSrc}
            className="w-16 h-16 md:w-20 md:h-20 rounded-xl"
            alt="avatarCourse"
        />
        <div className="ml-3 relative pr-10">
          <p className="text-gray-400 font-semibold text-sm md:text-[16px]">
            {courseCode}
          </p>
          <p className="text-gray-900 font-bold text-sm md:text-[17px] mt-1 md:mt-2 line-clamp-2">
            {courseName}
          </p>
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
      <div className="flex items-start p-3 rounded-lg border border-background-color">
        <div className="w-10 h-10 flex-shrink-0 flex justify-center items-center bg-background-color rounded-md mr-3 text-lg font-semibold text-white">
          {index}
        </div>
        <div>
          <h3 className="text-base font-medium text-gray-900">{courseName}</h3>
          <p className="text-xs text-gray-500 mt-1">{numberOfExercise} exercises</p>
        </div>
      </div>
  );
};

type Props = {
    chapters: LearningUnitWithChildren[];
};
const VerticalJourneyMap: React.FC<Props> = ({ chapters }) => {
    const svgRef = useRef<SVGSVGElement | null>(null);
    const pathRef = useRef<SVGPathElement | null>(null);
    const [points, setPoints] = useState<{ x: number; y: number }[]>([]);
    const [svgHeight, setSvgHeight] = useState(600);
    const [startPoint, setStartPoint] = useState<{ x: number; y: number } | null>(null);
    const [endPoint, setEndPoint] = useState<{ x: number; y: number } | null>(null);


    // === GSAP animation ===
    useLayoutEffect(() => {
        if (!svgRef.current || points.length === 0) return;

        const scope = svgRef.current;
        const frame = requestAnimationFrame(() => {
            const ctx = gsap.context(() => {
                const road = scope.querySelector(".road-base") as SVGPathElement | null;
                if (!road) return;

                const totalLength = road.getTotalLength();
                if (!totalLength || isNaN(totalLength) || totalLength < 10) {
                    setTimeout(() => ScrollTrigger.refresh(), 200);
                    return;
                }

                // reset trạng thái
                gsap.set(road, { strokeDasharray: totalLength, strokeDashoffset: totalLength });
                gsap.set(".chapter-group", { opacity: 1 });
                gsap.set(".chapter-node circle", { scale: 0.5, opacity: 0, transformOrigin: "center" });
                gsap.set(".chapter-box div", { opacity: 0, x: (i) => ((i as number) % 2 === 0 ? -50 : 50) });

                const tl = gsap.timeline({
                    scrollTrigger: {
                        trigger: scope,
                        start: "top 85%",
                        end: () => {
                            const len = road.getTotalLength();
                            const minEnd = 400;
                            const maxEnd = 1200;
                            return "+=" + Math.min(Math.max(len / 1.6, minEnd), maxEnd);
                        },
                        scrub: true,
                        invalidateOnRefresh: true,
                        toggleActions: "play none none none",
                        scroller: scope.closest(".journey-container") || undefined,
                    },
                });

                // path hiện dần
                tl.to(road, { strokeDashoffset: 0, duration: 1, ease: "none" }, 0);

                // node và box
                points.forEach((_, i) => {
                    const progress = ((i + 1) / (points.length + 1)) * 0.4 + 0.1;
                    tl.to(
                        `.chapter-group:nth-of-type(${i + 1}) .chapter-node circle`,
                        { opacity: 1, scale: 1, duration: 0.25, ease: "back.out(1.7)" },
                        progress
                    );
                    tl.to(
                        `.chapter-group:nth-of-type(${i + 1}) .chapter-box div`,
                        { opacity: 1, x: 0, duration: 0.35, ease: "power2.out" },
                        progress - 0.1
                    );
                });
            }, scope);

            ScrollTrigger.refresh();
            return () => ctx.revert();
        });

        return () => cancelAnimationFrame(frame);
    }, [points.length]);

    useEffect(() => {
        ScrollTrigger.refresh();
    }, [chapters.length]);

    // === Path generator ===
    const generatePath = (
        chapters: LearningUnitWithChildren[],
        offsetLeft = 200,
        offsetRight = 200,
        gap = 180
    ) => {
        const startX = 300;
        const startY = 50;
        let d = `M ${startX} ${startY}`;
        chapters.forEach((_, i) => {
            const y = startY + (i + 1) * gap;
            const ctrlX = i % 2 === 0 ? startX - offsetLeft : startX + offsetRight;
            d += ` S ${ctrlX} ${y}, ${startX} ${y}`;
        });
        return d;
    };

    const d = useMemo(() => generatePath(chapters), [chapters]);

    // === node positions ===
    useEffect(() => {
        if (!pathRef.current || chapters.length === 0) return;
        const path = pathRef.current;
        const len = path.getTotalLength();
        const pts: { x: number; y: number }[] = [];

        const startOffset = -120;
        for (let i = 0; i < chapters.length; i++) {
            const distance = (len / (chapters.length + 1)) * (i + 1) + startOffset;
            const clamped = Math.max(0, Math.min(len, distance));
            const point = path.getPointAtLength(clamped);
            pts.push({ x: point.x, y: point.y });
        }

        // 🔹 xác định start & end thật theo path
        const start = path.getPointAtLength(0);
        const end = path.getPointAtLength(len);

        setPoints(pts);
        setStartPoint(start);
        setEndPoint(end);

        if (pts.length > 0) setSvgHeight(end.y + 200);
    }, [chapters]);

    if (chapters.length === 0)
        return (
            <div className="w-full h-[240px] flex items-center justify-center rounded-xl border border-dashed text-gray-400">
                No content yet
            </div>
        );

    // === Render ===
    return (
        <div
            className="journey-container relative flex-none shrink-0 h-full overflow-y-auto rounded-xl border border-gray-200 bg-white
      [box-shadow:inset_0_0_20px_#c2d6ff]
      [scrollbar-width:none] [-ms-overflow-style:none] [&::-webkit-scrollbar]:hidden"
        >
            <svg
                ref={svgRef}
                className="absolute inset-0 block w-full"
                style={{ height: svgHeight }}
                viewBox={`0 0 600 ${svgHeight}`}
                preserveAspectRatio="xMidYMin meet"
            >
                <defs>
                    <linearGradient id="roadGradient" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="0%" stopColor="#4a90e2" />
                        <stop offset="100%" stopColor="#7b4397" />
                    </linearGradient>
                    <filter id="glow">
                        <feDropShadow dx="0" dy="0" stdDeviation="4" floodColor="#4a90e2" />
                    </filter>
                </defs>

                {/* Road base */}
                <path
                    ref={pathRef}
                    d={d}
                    stroke="url(#roadGradient)"
                    strokeWidth="40"
                    fill="none"
                    strokeLinecap="round"
                    className="road-base"
                    vectorEffect="non-scaling-stroke"
                />

                {/* Lane line */}
                <path
                    d={d}
                    stroke="#fff"
                    strokeWidth="6"
                    strokeDasharray="20 20"
                    fill="none"
                    strokeLinecap="round"
                    vectorEffect="non-scaling-stroke"
                />

                {/* START node  */}
                {startPoint && (
                    <g className="fixed-start">
                        <circle cx={startPoint.x} cy={startPoint.y} r="18" fill="#fff" stroke="#4a90e2" strokeWidth="4" />
                        <circle cx={startPoint.x} cy={startPoint.y} r="10" fill="#4a90e2" />
                        <text
                            x={startPoint.x}
                            y={startPoint.y + 4}
                            textAnchor="middle"
                            fill="#fff"
                            fontWeight="bold"
                            fontSize="10"
                        >
                            Start
                        </text>
                    </g>
                )}

                {/* NODES + BOXES */}
                {points.map((p, i) => {
                    const c = chapters[i];
                    const boxWidth = 250;
                    const offset = 30;
                    const isEven = (i + 1) % 2 === 0;
                    const boxX = isEven ? p.x + offset : p.x - boxWidth - offset;
                    const boxY = p.y - 20;

                    return (
                        <g key={c.id} className="chapter-group">
                            {/* Node */}
                            <g className="chapter-node">
                                <circle cx={p.x} cy={p.y} r="18" fill="#fff" stroke="#333" strokeWidth="4" />
                                <circle cx={p.x} cy={p.y} r="12" fill="#4a90e2" />
                                <text x={p.x} y={p.y + 4} textAnchor="middle" fill="#fff" fontSize="12">
                                    {i + 1}
                                </text>
                            </g>

                            {/* Line nối node ↔ box */}
                            <line
                                x1={p.x}
                                y1={p.y}
                                x2={isEven ? boxX : boxX + boxWidth}
                                y2={boxY + 40}
                                stroke="#999"
                                strokeWidth="2"
                                strokeDasharray="4 2"
                                vectorEffect="non-scaling-stroke"
                            />

                            {/* Box */}
                            <foreignObject x={boxX} y={boxY} width={boxWidth} height={boxY} className="chapter-box">
                                <div
                                    className="bg-white rounded-xl shadow-md border border-gray-200 p-3 max-h-60 overflow-y-auto
               hover:shadow-lg transition-all duration-300"
                                >
                                    {/* Chapter Info */}
                                    <div className="mb-2">
                                        <p className="font-semibold text-gray-900 text-sm truncate">
                                            {c.name}
                                        </p>

                                    </div>

                                    {/* Lessons */}
                                    {c.children && c.children.length > 0 ? (
                                        <ul className="border-t border-gray-100 pt-2 space-y-1">
                                            {c.children.map((lesson, index) => (
                                                <li
                                                    key={lesson.id}
                                                    className="text-xs text-gray-700 flex items-center gap-1"
                                                >
            <span className="text-[10px] text-gray-500 font-medium w-4 text-right">
              {index + 1}.
            </span>
                                                    <span className="truncate flex-1">{lesson.name}</span>
                                                </li>
                                            ))}
                                        </ul>
                                    ) : (
                                        <p className="text-xs text-gray-400 italic mt-2">No lessons</p>
                                    )}
                                </div>
                            </foreignObject>
                        </g>
                    );
                })}

                {endPoint && (
                    <g className="fixed-end">
                        <circle cx={endPoint.x} cy={endPoint.y} r="18" fill="#fff" stroke="#7b4397" strokeWidth="4" />
                        <circle cx={endPoint.x} cy={endPoint.y} r="10" fill="#7b4397" />
                        <text
                            x={endPoint.x}
                            y={endPoint.y + 4}
                            textAnchor="middle"
                            fill="#fff"
                            fontWeight="bold"
                            fontSize="10"
                        >
                            End
                        </text>
                    </g>
                )}
            </svg>
        </div>
    );
};