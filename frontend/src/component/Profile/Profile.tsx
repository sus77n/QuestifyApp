import React, {useEffect, useState} from "react";
import { PencilIcon } from "@heroicons/react/24/solid";
import { useGetCurrentUserQuery } from "../../API/service/user.service";
import {  CartesianGrid, Line, LineChart,Tooltip, XAxis, YAxis} from "recharts";
import {Spinner} from "../material/material";
import {HandThumbUpIcon} from "@heroicons/react/16/solid";

const Profile = () => {
    const { data: user, isLoading: isLoadingUser } = useGetCurrentUserQuery();

    const profileData = {
        username: user?.username,
        email: user?.email,
        joinedDate: user?.createdAt ? new Date(user.createdAt).toLocaleString('default', { month: 'long', year: 'numeric' }) : '',
        completedCourses: 5, // Example data, replace with real data if available
        ongoingCourses: 2 // Example data, replace with real data if available
    };

    const [searchTerm, setSearchTerm] = useState('');
    const [sortConfig, setSortConfig] = useState({
        key: 'createdAt', // 'code', 'name', or 'createdAt'
        direction: 'desc', // 'asc' or 'desc'
    });

    const [completedCourses] = useState([
        { id: 1, code: 'CS101', name: 'Introduction to Programming', createdAt: '2023-05-15' },
        { id: 2, code: 'MATH201', name: 'Advanced Calculus', createdAt: '2023-03-10' },
        { id: 3, code: 'ENG101', name: 'English Composition', createdAt: '2023-06-22' },
        { id: 4, code: 'PHYS101', name: 'Physics Fundamentals', createdAt: '2023-04-18' },
    ]);

    const requestSort = (key: any) => {
        let direction = 'asc';
        if (sortConfig.key === key && sortConfig.direction === 'asc') {
            direction = 'desc';
        }
        setSortConfig({ key, direction });
    };

    const sortedCourses = [...completedCourses].sort((a, b) => {
        // Handle date comparison separately
        if (sortConfig.key === 'createdAt') {
            const dateA = new Date(a.createdAt).getTime();
            const dateB = new Date(b.createdAt).getTime();
            return sortConfig.direction === 'asc' ? dateA - dateB : dateB - dateA;
        }

        if (a[sortConfig.key] < b[sortConfig.key]) {
            return sortConfig.direction === 'asc' ? -1 : 1;
        }
        if (a[sortConfig.key] > b[sortConfig.key]) {
            return sortConfig.direction === 'asc' ? 1 : -1;
        }
        return 0;
    });

    const filteredCourses = sortedCourses.filter((course) => {
        return (
            course.code.toLowerCase().includes(searchTerm.toLowerCase()) ||
            course.name.toLowerCase().includes(searchTerm.toLowerCase())
        );
    });

    type DayData = {
        day: string;
        accesses: number;
        time: string;
    };

    type WeekData = {
        week: string;
        accesses: number;
    };

    type MonthData = {
        month: string;
        accesses: number;
    };

    const [timeRange, setTimeRange] = useState<'week' | 'month' | 'year'>('week');
    const [activityData, setActivityData] = useState<DayData[] | WeekData[] | MonthData[]>([]);

    // Mock data - replace with actual API call
    useEffect(() => {
        // This would typically be an API call to your backend
        const mockData = {
            week: [
                { day: 'Mon', accesses: 12, time: 'morning' },
                { day: 'Tue', accesses: 8, time: 'afternoon' },
                { day: 'Wed', accesses: 15, time: 'evening' },
                { day: 'Thu', accesses: 10, time: 'morning' },
                { day: 'Fri', accesses: 18, time: 'evening' },
                { day: 'Sat', accesses: 5, time: 'afternoon' },
                { day: 'Sun', accesses: 3, time: 'night' },
            ],
            month: [
                { week: 'Week 1', accesses: 45 },
                { week: 'Week 2', accesses: 38 },
                { week: 'Week 3', accesses: 52 },
                { week: 'Week 4', accesses: 41 },
            ],
            year: [
                { month: 'Jan', accesses: 120 },
                { month: 'Feb', accesses: 95 },
                { month: 'Mar', accesses: 95 },
                { month: 'Apr', accesses: 95 },
                { month: 'May', accesses: 95 },
                { month: 'June', accesses: 95 },
                { month: 'July', accesses: 95 },

                // ... more months
            ]
        };

        setActivityData(mockData[timeRange]);
    }, [timeRange]);

    return (
        <div className="h-screen flex ml-1">
            {/* Main content */}
            <div className="m-[8px] h-[98vh] w-[40vw] bg-white rounded-xl flex flex-col overflow-y-auto">
                <h1 className="text-2xl font-semibold text-white bg-text-color pt-3 pb-3 pl-5 pr-5 ">Profile</h1>
                <div className="flex justify-center h-full w-full p-4">
                    {/* Profile section */}
                    {isLoadingUser ? (
                        <div className="flex flex-col items-center w-full h-full">
                            <Spinner/>
                        </div>
                    ):(
                        <div className="flex flex-col items-center w-full">
                        <img
                            src={`/img/ava1.png`}
                            className="w-48 h-48 rounded-full object-cover mb-6 border-4 border-primary"
                            alt="Profile avatar"
                        />
                        <h2 className="text-2xl font-bold text-text-color mb-4">@{profileData.username}</h2>
                        <div className="flex items-center justify-center w-full mb-4 text-text-color">
                            <h2 className="text-2xl font-bold">Bui Nguyen Hai Ngan</h2>
                            <PencilIcon className="w-6 ml-2 "/>
                        </div>
                        <div className="w-full p-2 text-text-color ">
                            <div className="flex justify-between mb-3">
                                <span>Email</span>
                                <span >{profileData.email}</span>
                            </div>
                            <div className="flex justify-between mb-3">
                                <span>Member since</span>
                                <span >{profileData.joinedDate}</span>
                            </div>
                            <div className="flex justify-between mb-3">
                                <span >Completed courses</span>
                                <span >{profileData.completedCourses}</span>
                            </div>
                            <div className="flex justify-between">
                                <span >Ongoing courses</span>
                                <span >{profileData.ongoingCourses}</span>
                            </div>
                        </div>
                    </div>)}
                </div>
            </div>

            <div className="m-[8px] w-full">
                <div className="w-full h-[40vh] bg-white rounded-xl flex flex-col p-4">
                    <div className="flex justify-between items-center mb-6">
                        <h3 className="text-xl font-semibold text-text-color">Your activities</h3>

                        <div className="flex space-x-2">
                            <button
                                onClick={() => setTimeRange('week')}
                                className={`px-3 py-1 rounded-md text-sm ${timeRange === 'week' ? 'bg-text-color text-white' : 'bg-gray-100'}`}
                            >
                                Week
                            </button>
                            <button
                                onClick={() => setTimeRange('month')}
                                className={`px-3 py-1 rounded-md text-sm ${timeRange === 'month' ? 'bg-text-color text-white' : 'bg-gray-100'}`}
                            >
                                Month
                            </button>
                            <button
                                onClick={() => setTimeRange('year')}
                                className={`px-3 py-1 rounded-md text-sm ${timeRange === 'year' ? 'bg-text-color text-white' : 'bg-gray-100'}`}
                            >
                                Year
                            </button>
                        </div>
                    </div>

                        {/* Line Chart for trend over time */}
                        <div className="p-3 rounded-lg">
                            <div className="h-48">
                                <LineChart
                                    width={850}
                                    height={200}
                                    data={activityData}
                                    margin={{ top: 5, right: 5, left: 5, bottom: 5 }}
                                >
                                    <CartesianGrid strokeDasharray="3 3" />
                                    <XAxis dataKey={timeRange === 'week' ? 'day' : timeRange === 'month' ? 'week' : 'month'} />
                                    <YAxis />
                                    <Tooltip />
                                    <Line type="monotone" dataKey="accesses" stroke="#02457A" />
                                </LineChart>
                            </div>
                        </div>
                </div>

                <div className="w-full h-[57vh] bg-white rounded-xl p-4 mt-2">
                    <div className="flex justify-between items-center mb-4">
                        <div>
                            <div className="flex items-center space-x-2">
                                {/*<h3 className="text-xl font-semibold text-text-color">Completed Courses</h3>*/}
                                <h3 className="text-[20px] text-text-color">Congratulation !! You completed {completedCourses.length} courses</h3>
                                <HandThumbUpIcon className="h-5 w-5 text-text-color"/>
                            </div>
                        </div>
                        <div className="relative w-64">
                            <input
                                type="text"
                                placeholder="Search courses..."
                                className="w-full pl-4 pr-10 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-background-color"
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                            />
                            <svg
                                className="absolute right-3 top-2.5 h-5 w-5 text-gray-400"
                                xmlns="http://www.w3.org/2000/svg"
                                fill="none"
                                viewBox="0 0 24 24"
                                stroke="currentColor"
                            >
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                            </svg>
                        </div>
                    </div>

                    <div className="relative overflow-x-auto sm:rounded-lg h-[calc(57vh-80px)]">
                        <table className="w-full text-sm text-left text-gray-500">
                            <thead className="text-xs text-gray-700 uppercase bg-gray-50">
                            <tr>
                                <th scope="col" className="px-6 py-3">
                                    #
                                </th>
                                <th scope="col" className="px-6 py-3">
                                    <div className="flex items-center">
                                        Code
                                        <button
                                            onClick={() => requestSort('code')}
                                            aria-label="Sort by course code"
                                        >
                                            <svg
                                                className={`w-3 h-3 ms-1.5 ${sortConfig.key === 'code' ? 'text-gray-700' : 'text-gray-400'}`}
                                                aria-hidden="true"
                                                xmlns="http://www.w3.org/2000/svg"
                                                fill="currentColor"
                                                viewBox="0 0 24 24"
                                            >
                                                <path d="M8.574 11.024h6.852a2.075 2.075 0 0 0 1.847-1.086 1.9 1.9 0 0 0-.11-1.986L13.736 2.9a2.122 2.122 0 0 0-3.472 0L6.837 7.952a1.9 1.9 0 0 0-.11 1.986 2.074 2.074 0 0 0 1.847 1.086Zm6.852 1.952H8.574a2.072 2.072 0 0 0-1.847 1.087 1.9 1.9 0 0 0 .11 1.985l3.426 5.05a2.123 2.123 0 0 0 3.472 0l3.427-5.05a1.9 1.9 0 0 0 .11-1.985 2.074 2.074 0 0 0-1.846-1.087Z"/>
                                            </svg>
                                        </button>
                                    </div>
                                </th>
                                <th scope="col" className="px-6 py-3">
                                    <div className="flex items-center">
                                        Course Name
                                        <button
                                            onClick={() => requestSort('name')}
                                            aria-label="Sort by course name"
                                        >
                                            <svg
                                                className={`w-3 h-3 ms-1.5 ${sortConfig.key === 'name' ? 'text-gray-700' : 'text-gray-400'}`}
                                                aria-hidden="true"
                                                xmlns="http://www.w3.org/2000/svg"
                                                fill="currentColor"
                                                viewBox="0 0 24 24"
                                            >
                                                <path d="M8.574 11.024h6.852a2.075 2.075 0 0 0 1.847-1.086 1.9 1.9 0 0 0-.11-1.986L13.736 2.9a2.122 2.122 0 0 0-3.472 0L6.837 7.952a1.9 1.9 0 0 0-.11 1.986 2.074 2.074 0 0 0 1.847 1.086Zm6.852 1.952H8.574a2.072 2.072 0 0 0-1.847 1.087 1.9 1.9 0 0 0 .11 1.985l3.426 5.05a2.123 2.123 0 0 0 3.472 0l3.427-5.05a1.9 1.9 0 0 0 .11-1.985 2.074 2.074 0 0 0-1.846-1.087Z"/>
                                            </svg>
                                        </button>
                                    </div>
                                </th>
                                <th scope="col" className="px-6 py-3">
                                    <div className="flex items-center">
                                        Completed Date
                                        <button
                                            onClick={() => requestSort('createdAt')}
                                            aria-label="Sort by completion date"
                                        >
                                            <svg
                                                className={`w-3 h-3 ms-1.5 ${sortConfig.key === 'createdAt' ? 'text-gray-700' : 'text-gray-400'}`}
                                                aria-hidden="true"
                                                xmlns="http://www.w3.org/2000/svg"
                                                fill="currentColor"
                                                viewBox="0 0 24 24"
                                            >
                                                <path d="M8.574 11.024h6.852a2.075 2.075 0 0 0 1.847-1.086 1.9 1.9 0 0 0-.11-1.986L13.736 2.9a2.122 2.122 0 0 0-3.472 0L6.837 7.952a1.9 1.9 0 0 0-.11 1.986 2.074 2.074 0 0 0 1.847 1.086Zm6.852 1.952H8.574a2.072 2.072 0 0 0-1.847 1.087 1.9 1.9 0 0 0 .11 1.985l3.426 5.05a2.123 2.123 0 0 0 3.472 0l3.427-5.05a1.9 1.9 0 0 0 .11-1.985 2.074 2.074 0 0 0-1.846-1.087Z"/>
                                            </svg>
                                        </button>
                                    </div>
                                </th>
                            </tr>
                            </thead>
                            <tbody>
                            {filteredCourses.length > 0 ? (
                                filteredCourses.map((course, index) => (
                                    <tr key={course.id} className="bg-white border-b hover:bg-gray-50 text-black">
                                        <td className="px-6 py-4 whitespace-nowrap">
                                            {index + 1}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap">
                                            {course.code}
                                        </td>
                                        <td className="px-6 py-4">
                                            {course.name}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap">
                                            {new Date(course.createdAt).toLocaleDateString('en-US', {
                                                year: 'numeric',
                                                month: 'short',
                                                day: 'numeric'
                                            })}
                                        </td>
                                    </tr>
                                ))
                            ) : (
                                <tr className="bg-white border-b hover:bg-gray-50">
                                    <td colSpan={4} className="px-6 py-4 text-center">
                                        No courses found
                                    </td>
                                </tr>
                            )}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Profile;