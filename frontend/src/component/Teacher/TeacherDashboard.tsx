import React, { useState } from "react";
// Import useNavigate để chuyển trang
import { useNavigate } from "react-router-dom";

// Import Icons từ Ant Design
import {
    ThunderboltOutlined,
    DownOutlined,
    ClockCircleOutlined
} from "@ant-design/icons";
import { PrimaryInput } from "../material/material";

// --- MOCK DATA ---
const MOCK_SEMESTERS = [
    { id: 1, name: "Spring 2024", isActive: false },
    { id: 2, name: "Summer 2024", isActive: true },
    { id: 3, name: "Fall 2024", isActive: false },
];

const MOCK_COURSES = [
    {
        // Giả lập ID dạng UUID như bạn yêu cầu
        id: "4befbfbd-3946-57ef-bfbd-4f16efbfbd0d",
        title: "Lập trình Java Căn Bản",
        code: "JAVA101",
        stats: {
            students: 45,
            attempts: 1250,
            avgScore: 7.8,
            minScore: 2.5,
            maxScore: 10.0,
            avgProgress: 65,
        },
        activities: [
            { id: 1, student: "Nguyễn Văn A", lesson: "Vòng lặp For", score: 9.0, time: "5m ago" },
            { id: 2, student: "Trần Thị B", lesson: "OOP Basics", score: 4.5, time: "15m ago" },
            { id: 3, student: "Lê Văn C", lesson: "Mảng 2 chiều", score: 8.0, time: "1h ago" },
        ]
    },
    {
        id: "5ceacafe-1234-5678-abcd-effacedec0de",
        title: "Cấu Trúc Dữ Liệu & GT",
        code: "DSA201",
        stats: {
            students: 60,
            attempts: 890,
            avgScore: 6.5,
            minScore: 1.0,
            maxScore: 9.5,
            avgProgress: 40,
        },
        activities: [
            { id: 4, student: "Phạm D", lesson: "Linked List", score: 10.0, time: "2m ago" },
            { id: 5, student: "Hoàng E", lesson: "Binary Tree", score: 3.0, time: "10m ago" },
        ]
    },
    {
        id: "6deadbeef-9876-5432-dcba-1234567890ab",
        title: "Phát triển Web Frontend",
        code: "WEB301",
        stats: {
            students: 32,
            attempts: 450,
            avgScore: 8.2,
            minScore: 5.0,
            maxScore: 10.0,
            avgProgress: 80,
        },
        activities: [
            { id: 7, student: "Đặng G", lesson: "React Hooks", score: 8.5, time: "1h ago" },
        ]
    },
    {
        id: "7fabcdef-1111-2222-3333-444455556666",
        title: "Cơ sở dữ liệu",
        code: "DB101",
        stats: {
            students: 55,
            attempts: 1100,
            avgScore: 7.0,
            minScore: 3.0,
            maxScore: 9.0,
            avgProgress: 55,
        },
        activities: [
            { id: 9, student: "Vũ K", lesson: "SQL Select", score: 6.0, time: "10m ago" },
            { id: 10, student: "Lý L", lesson: "Normalization", score: 7.5, time: "45m ago" },
        ]
    }
];

export default function TeacherDashboard() {
    const navigate = useNavigate(); // Hook điều hướng

    const currentActiveSemester = MOCK_SEMESTERS.find(s => s.isActive);
    const [semester, setSemester] = useState(currentActiveSemester ? currentActiveSemester.id : MOCK_SEMESTERS[0].id);

    const [search, setSearch] = useState("");
    const [courses, setCourses] = useState(MOCK_COURSES);

    const filteredCourses = courses.filter(c =>
        c.title.toLowerCase().includes(search.toLowerCase()) ||
        c.code.toLowerCase().includes(search.toLowerCase())
    );

    // Hàm xử lý khi click vào Card
    const handleCourseClick = (courseId: string) => {
        navigate(`/teacher/course/${courseId}/lessons`);
    };

    return (
        <div className="flex h-screen bg-light-background w-full">
            {/* Main Container - scrollbar-hide */}
            <div className="flex-1 p-6 overflow-y-auto h-screen scrollbar-hide">

                {/* --- HEADER SECTION --- */}
                <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 gap-4">
                    <div>
                        <h1 className="text-3xl font-bold text-[#02457A]">Instructor Dashboard</h1>
                        <p className="text-gray-500 mt-1">
                            Overview for <b>{MOCK_SEMESTERS.find(s => s.id === semester)?.name}</b>
                        </p>
                    </div>

                    <div className="flex items-center gap-4">
                        <div className="relative group">
                            <select
                                value={semester}
                                onChange={(e) => setSemester(Number(e.target.value))}
                                className="appearance-none bg-white border border-gray-300 text-gray-700 py-3 px-4 pr-10 rounded-full leading-tight focus:outline-none focus:bg-white focus:border-[#02457A] font-medium cursor-pointer hover:shadow-sm transition-all"
                            >
                                {MOCK_SEMESTERS.map(sem => (
                                    <option key={sem.id} value={sem.id}>
                                        {sem.name} {sem.isActive ? "(Current)" : ""}
                                    </option>
                                ))}
                            </select>
                            <div className="pointer-events-none absolute inset-y-0 right-0 flex items-center px-3 text-gray-500">
                                <DownOutlined style={{ fontSize: '12px' }} />
                            </div>
                        </div>

                        <div className="hidden md:block">
                            <PrimaryInput
                                placeholder="Search courses..."
                                w="w-[300px]"
                                value={search}
                                onChange={(e: any) => setSearch(e.target.value)}
                            />
                        </div>
                    </div>
                </div>

                {/* --- GRID COURSES (2 Columns per row) --- */}
                <div className="grid grid-cols-1 xl:grid-cols-2 gap-6 pb-10">

                    {filteredCourses.map((course) => (
                        <div
                            key={course.id}
                            onClick={() => handleCourseClick(course.id)}
                            className="bg-white rounded-xl shadow-sm hover:shadow-lg hover:border-[#02457A] transition-all duration-300 border border-gray-100 flex flex-col h-full overflow-hidden cursor-pointer group"
                        >

                            {/* Card Header */}
                            <div className="p-5 border-b border-gray-100 bg-white group-hover:bg-gray-50 transition-colors">
                                <div className="flex justify-between items-start mb-2">
                                    <div>
                                        <h3 className="text-[#02457A] font-bold text-xl truncate leading-tight group-hover:text-blue-700 transition-colors" title={course.title}>
                                            {course.title}
                                        </h3>
                                        <p className="text-gray-400 text-sm font-mono mt-0.5">{course.code}</p>
                                    </div>
                                    <span className="bg-green-100 text-green-700 text-xs px-2 py-1 rounded-full font-bold">
                                        ACTIVE
                                    </span>
                                </div>

                                {/* Progress Bar */}
                                <div className="mt-3">
                                    <div className="flex justify-between text-xs mb-1">
                                        <span className="text-gray-500 font-medium">Avg Class Progress</span>
                                        <span className="text-[#02457A] font-bold">{course.stats.avgProgress}%</span>
                                    </div>
                                    <div className="w-full bg-gray-200 rounded-full h-2.5">
                                        <div
                                            className="bg-[#02457A] h-2.5 rounded-full transition-all duration-500"
                                            style={{ width: `${course.stats.avgProgress}%` }}
                                        ></div>
                                    </div>
                                </div>
                            </div>

                            {/* Section 1: Detailed Stats Grid */}
                            <div className="p-4 bg-gray-50/50 border-b border-gray-100">
                                <div className="grid grid-cols-5 gap-2 text-center divide-x divide-gray-200">
                                    <div className="px-1">
                                        <div className="text-[10px] uppercase text-gray-400 font-semibold mb-1">Students</div>
                                        <div className="font-bold text-gray-700 text-sm">{course.stats.students}</div>
                                    </div>
                                    <div className="px-1">
                                        <div className="text-[10px] uppercase text-gray-400 font-semibold mb-1">Attempts</div>
                                        <div className="font-bold text-gray-700 text-sm">{course.stats.attempts}</div>
                                    </div>
                                    <div className="px-1">
                                        <div className="text-[10px] uppercase text-gray-400 font-semibold mb-1">Avg</div>
                                        <div className={`font-bold text-sm ${course.stats.avgScore >= 7 ? 'text-green-600' : 'text-yellow-600'}`}>
                                            {course.stats.avgScore}
                                        </div>
                                    </div>
                                    <div className="px-1">
                                        <div className="text-[10px] uppercase text-gray-400 font-semibold mb-1">Min</div>
                                        <div className="font-bold text-red-500 text-sm">{course.stats.minScore}</div>
                                    </div>
                                    <div className="px-1">
                                        <div className="text-[10px] uppercase text-gray-400 font-semibold mb-1">Max</div>
                                        <div className="font-bold text-green-600 text-sm">{course.stats.maxScore}</div>
                                    </div>
                                </div>
                            </div>

                            {/* Section 2: Activity Feed */}
                            <div className="flex-1 p-4 bg-white">
                                <div className="flex justify-between items-center mb-3">
                                    <h4 className="text-[11px] font-bold text-gray-400 uppercase tracking-wider flex items-center gap-1">
                                        <ThunderboltOutlined style={{ color: '#f59e0b' }} /> Recent Activity
                                    </h4>
                                </div>

                                <div className="space-y-4">
                                    {course.activities.length > 0 ? (
                                        course.activities.map((act) => (
                                            <div key={act.id} className="flex items-start gap-3 text-sm group/item">
                                                <img
                                                    src="/img/ava1.png"
                                                    alt="avt"
                                                    className="w-9 h-9 rounded-full border border-gray-100 flex-shrink-0 bg-gray-100 object-cover"
                                                />
                                                <div className="flex-1 min-w-0">
                                                    <div className="flex justify-between items-start">
                                                        <p className="font-semibold text-gray-700 truncate text-xs">{act.student}</p>
                                                        <span className="text-[10px] text-gray-400 whitespace-nowrap flex items-center gap-0.5">
                                                            <ClockCircleOutlined style={{fontSize: '8px'}}/> {act.time}
                                                        </span>
                                                    </div>
                                                    <p className="text-[11px] text-gray-500 truncate mt-0.5 group-hover/item:text-[#02457A] transition-colors" title={act.lesson}>
                                                        Submitted: {act.lesson}
                                                    </p>
                                                </div>
                                                <div className={`text-[10px] font-bold px-2 py-1 rounded-md border ${
                                                    act.score >= 5
                                                        ? 'bg-green-50 text-green-600 border-green-100'
                                                        : 'bg-red-50 text-red-600 border-red-100'
                                                }`}>
                                                    {act.score}
                                                </div>
                                            </div>
                                        ))
                                    ) : (
                                        <div className="text-center py-6 text-gray-400 text-xs italic bg-gray-50 rounded-lg border border-dashed border-gray-200">
                                            No recent activity in this semester
                                        </div>
                                    )}
                                </div>
                            </div>

                            {/* Removed Button Footer */}
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}