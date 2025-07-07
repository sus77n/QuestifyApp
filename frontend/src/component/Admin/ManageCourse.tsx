import { useGetCoursesQuery } from '../../API/service/course.service';
import React, {useState} from "react";
import {Spinner} from "../material/material";
import {CourseDTO} from "../../model/CourseDTO";

export default function ManageCourse() {
    const { data: courses = [], isLoading, isError } = useGetCoursesQuery();

    const [searchTerm, setSearchTerm] = useState('');
    const [sortConfig, setSortConfig] = useState<{
        key: keyof CourseDTO;
        direction: 'asc' | 'desc';
    }>({
        key: 'id',
        direction: 'asc',
    });

    // Sort courses
    const sortedCourses = [...courses].sort((a, b) => {
        if (a[sortConfig.key] < b[sortConfig.key]) {
            return sortConfig.direction === 'asc' ? -1 : 1;
        }
        if (a[sortConfig.key] > b[sortConfig.key]) {
            return sortConfig.direction === 'asc' ? 1 : -1;
        }
        return 0;
    });

    // Filter courses by search term
    const filteredCourses = sortedCourses.filter(
        (course) =>
            course.code.toLowerCase().includes(searchTerm.toLowerCase()) ||
            course.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
            course.id.toString().includes(searchTerm)
    );

    // Request sort for a column
    const requestSort = (key: keyof CourseDTO) => {
        let direction: 'asc' | 'desc' = 'asc';
        if (sortConfig.key === key && sortConfig.direction === 'asc') {
            direction = 'desc';
        }
        setSortConfig({ key, direction });
    };

    // Handle edit and delete functions
    const handleEdit = (id: number) => {
        console.log('Edit course with id:', id);
        // Your edit logic here
    };

    const handleDelete = (id: number) => {
        console.log('Delete course with id:', id);
        // Your delete logic here
    };

    return (
        <div className="flex h-screen bg-light-background">
            <div className="flex-1 overflow-auto bg-white m-[8px] rounded-xl">
                    <div className="bg-text-color pt-3 pb-3 pl-5 pr-5 rounded-t-xl">
                        <h1 className="text-2xl font-semibold text-white">Manage Courses</h1>
                    </div>

                {isLoading ? (
                    <div>
                        <Spinner/>
                    </div>
                ) : isError ? (
                    <div className="text-red-500">Failed to load courses.</div>
                ) : (
                    <div className="p-4 bg-white rounded-xl shadow-sm border border-gray-200">
                        {/* Search Bar */}
                        <div className="mb-4 flex justify-between items-center">
                            <h2 className="text-xl font-semibold text-gray-800">Courses</h2>
                            <div className="relative w-64">
                                <input
                                    type="text"
                                    placeholder="Search courses..."
                                    className="w-full pl-4 pr-10 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
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

                        {/* Table */}
                        <div className="overflow-x-auto">
                            <table className="min-w-full bg-white border border-gray-200 rounded-xl">
                                <colgroup>
                                    <col style={{ width: '8%' }} />
                                    <col style={{ width: '15%' }} />
                                    <col style={{ width: '20%' }} />
                                    <col style={{ width: '20%' }} />
                                </colgroup>
                                <thead className="bg-gray-50">
                                <tr>
                                    <th
                                        className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                                        onClick={() => requestSort('id')}
                                    >
                                        <div className="flex items-center">
                                            ID
                                            {sortConfig.key === 'id' && (
                                                <span className="ml-1">
                      {sortConfig.direction === 'asc' ? '↑' : '↓'}
                    </span>
                                            )}
                                        </div>
                                    </th>
                                    <th
                                        className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                                        onClick={() => requestSort('code')}
                                    >
                                        <div className="flex items-center">
                                            Code
                                            {sortConfig.key === 'code' && (
                                                <span className="ml-1">
                      {sortConfig.direction === 'asc' ? '↑' : '↓'}
                    </span>
                                            )}
                                        </div>
                                    </th>
                                    <th
                                        className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                                        onClick={() => requestSort('name')}
                                    >
                                        <div className="flex items-center">
                                            Name
                                            {sortConfig.key === 'name' && (
                                                <span className="ml-1">
                      {sortConfig.direction === 'asc' ? '↑' : '↓'}
                    </span>
                                            )}
                                        </div>
                                    </th>
                                    <th className="px-6 py-3 border-b text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Action
                                    </th>
                                </tr>
                                </thead>
                                <tbody>
                                {filteredCourses.length > 0 ? (
                                    filteredCourses.map((course) => (
                                        <tr key={course.id} className="hover:bg-gray-50 transition-colors duration-150">
                                            <td className="px-6 py-4 border-b text-sm font-medium text-gray-900">{course.id}</td>
                                            <td className="px-6 py-4 border-b text-sm text-gray-500">{course.code}</td>
                                            <td className="px-6 py-4 border-b text-sm text-gray-500">{course.name}</td>
                                            <td className="px-6 py-4 border-b">
                                                <div className="flex justify-center space-x-2">
                                                    <button
                                                        className="text-white hover:bg-blue-600 px-3 py-1 bg-blue-500 rounded text-sm transition-colors duration-200"
                                                        onClick={() => handleEdit(course.id)}
                                                    >
                                                        Edit
                                                    </button>
                                                    <button
                                                        className="text-white hover:bg-red-600 px-3 py-1 bg-red-500 rounded text-sm transition-colors duration-200"
                                                        onClick={() => handleDelete(course.id)}
                                                    >
                                                        Delete
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan={4} className="px-6 py-4 text-center text-sm text-gray-500">
                                            No courses found
                                        </td>
                                    </tr>
                                )}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}