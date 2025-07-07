import {useGetCoursesQuery} from '../../API/service/course.service';
import React, {useState} from "react";
import {Spinner} from "../material/material";
import {CourseDTO} from "../../model/CourseDTO";
import {PencilIcon} from "@heroicons/react/24/solid";

export default function ManageCourse() {
    const {data: courses = [], isLoading, isError} = useGetCoursesQuery();
    // const [deleteCourses] = useDeleteCoursesMutation();
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedCourses, setSelectedCourses] = useState<number[]>([]);
    const [sortConfig, setSortConfig] = useState<{
        key: keyof CourseDTO;
        direction: 'asc' | 'desc';
    }>({
        key: 'id',
        direction: 'asc',
    });

    // Handle sorting
    const requestSort = (key: keyof CourseDTO) => {
        let direction: 'asc' | 'desc' = 'asc';
        if (sortConfig.key === key && sortConfig.direction === 'asc') {
            direction = 'desc';
        }
        setSortConfig({key, direction});
    };

    // Apply sorting
    const sortedCourses = [...courses].sort((a, b) => {
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

    // Apply search filter
    const filteredCourses = sortedCourses.filter(
        (course) =>
            course.code.toLowerCase().includes(searchTerm.toLowerCase()) ||
            course.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
            course.id.toString().includes(searchTerm)
    );

    // Handle course selection
    const toggleCourseSelection = (id: number) => {
        setSelectedCourses(prev =>
            prev.includes(id) ? prev.filter(courseId => courseId !== id) : [...prev, id]
        );
    };

    const handleBulkDelete = async () => {
        if (selectedCourses.length === 0) return;
        try {
            // await deleteCourses(selectedCourses).unwrap();
            setSelectedCourses([]);
        } catch (error) {
            console.error('Bulk delete failed:', error);
        }
    };

    if (isLoading) return <div className="flex h-screen w-full bg-light-background items-center justify-center">
        <Spinner/></div>;
    if (isError) return <div className="text-center py-8 text-red-500">Error loading courses</div>;

    return (
        <div className="flex h-screen w-full bg-light-background">
            <div className="flex-1 w-full overflow-auto bg-white m-[8px] ">
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
                    <div className="p-4 bg-white ">
                        {/* Search and Bulk Actions */}
                        <div className="mb-4 flex justify-between items-center">
                            <div className="flex flex-1 items-center justify-between">
                                <div className="">
                                    <button
                                        onClick={handleBulkDelete}
                                        disabled={selectedCourses.length === 0}
                                        className={`px-4 py-2 text-white rounded transition-colors ${
                                            selectedCourses.length === 0
                                                ? 'bg-gray-400 cursor-not-allowed'
                                                : 'bg-red-500 hover:bg-red-600'
                                        }`}
                                    >
                                        Delete {selectedCourses.length} course(s)
                                    </button>
                                </div>
                                <div className="relative">
                                    <input
                                        type="text"
                                        placeholder="Search courses..."
                                        className="w-full pl-4 pr-10 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-text-color"
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
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                                              d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
                                    </svg>
                                </div>
                            </div>
                        </div>

                        {/* Table */}
                        <div className="overflow-auto rounded ">
                            <table className="min-w-full bg-white border border-gray-200">
                                <colgroup>
                                    <col style={{width: '5%'}}/>
                                    <col style={{width: '5%'}}/>
                                    <col style={{width: '15%'}}/>
                                    <col style={{width: '60%'}}/>
                                    <col style={{width: '15%'}}/>
                                </colgroup>
                                <thead className="bg-gray-50">
                                <tr>
                                    <th className="px-4 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        <input
                                            type="checkbox"
                                            checked={selectedCourses.length === filteredCourses.length && filteredCourses.length > 0}
                                            onChange={() => {
                                                if (selectedCourses.length === filteredCourses.length) {
                                                    setSelectedCourses([]);
                                                } else {
                                                    setSelectedCourses(filteredCourses.map(course => course.id));
                                                }
                                            }}
                                            className="h-4 w-4 rounded border-gray-300 text-text-color focus:ring-text-color checked:bg-text-color checked:border-text-color"/>
                                    </th>
                                    <th
                                        className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                                        onClick={() => requestSort('id')}
                                    >
                                        <div className="flex items-center">
                                            ID
                                            <span className="ml-1">
                    <svg
                        className={`w-3 h-3 ${sortConfig.key === 'id' ? 'text-gray-700' : 'text-gray-400'}`}
                        aria-hidden="true"
                        xmlns="http://www.w3.org/2000/svg"
                        fill="currentColor"
                        viewBox="0 0 24 24"
                    >
                      <path
                          d="M8.574 11.024h6.852a2.075 2.075 0 0 0 1.847-1.086 1.9 1.9 0 0 0-.11-1.986L13.736 2.9a2.122 2.122 0 0 0-3.472 0L6.837 7.952a1.9 1.9 0 0 0-.11 1.986 2.074 2.074 0 0 0 1.847 1.086Zm6.852 1.952H8.574a2.072 2.072 0 0 0-1.847 1.087 1.9 1.9 0 0 0 .11 1.985l3.426 5.05a2.123 2.123 0 0 0 3.472 0l3.427-5.05a1.9 1.9 0 0 0 .11-1.985 2.074 2.074 0 0 0-1.846-1.087Z"/>
                    </svg>
                  </span>
                                        </div>
                                    </th>
                                    <th
                                        className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                                        onClick={() => requestSort('code')}
                                    >
                                        <div className="flex items-center">
                                            Code
                                            <span className="ml-1">
                    <svg
                        className={`w-3 h-3 ${sortConfig.key === 'code' ? 'text-gray-700' : 'text-gray-400'}`}
                        aria-hidden="true"
                        xmlns="http://www.w3.org/2000/svg"
                        fill="currentColor"
                        viewBox="0 0 24 24"
                    >
                      <path
                          d="M8.574 11.024h6.852a2.075 2.075 0 0 0 1.847-1.086 1.9 1.9 0 0 0-.11-1.986L13.736 2.9a2.122 2.122 0 0 0-3.472 0L6.837 7.952a1.9 1.9 0 0 0-.11 1.986 2.074 2.074 0 0 0 1.847 1.086Zm6.852 1.952H8.574a2.072 2.072 0 0 0-1.847 1.087 1.9 1.9 0 0 0 .11 1.985l3.426 5.05a2.123 2.123 0 0 0 3.472 0l3.427-5.05a1.9 1.9 0 0 0 .11-1.985 2.074 2.074 0 0 0-1.846-1.087Z"/>
                    </svg>
                  </span>
                                        </div>
                                    </th>
                                    <th
                                        className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                                        onClick={() => requestSort('name')}
                                    >
                                        <div className="flex items-center">
                                            Name
                                            <span className="ml-1">
                    <svg
                        className={`w-3 h-3 ${sortConfig.key === 'name' ? 'text-gray-700' : 'text-gray-400'}`}
                        aria-hidden="true"
                        xmlns="http://www.w3.org/2000/svg"
                        fill="currentColor"
                        viewBox="0 0 24 24"
                    >
                      <path
                          d="M8.574 11.024h6.852a2.075 2.075 0 0 0 1.847-1.086 1.9 1.9 0 0 0-.11-1.986L13.736 2.9a2.122 2.122 0 0 0-3.472 0L6.837 7.952a1.9 1.9 0 0 0-.11 1.986 2.074 2.074 0 0 0 1.847 1.086Zm6.852 1.952H8.574a2.072 2.072 0 0 0-1.847 1.087 1.9 1.9 0 0 0 .11 1.985l3.426 5.05a2.123 2.123 0 0 0 3.472 0l3.427-5.05a1.9 1.9 0 0 0 .11-1.985 2.074 2.074 0 0 0-1.846-1.087Z"/>
                    </svg>
                  </span>
                                        </div>
                                    </th>
                                    <th className="px-6 py-3 border-b text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Actions
                                    </th>
                                </tr>
                                </thead>
                                <tbody>
                                {filteredCourses.length > 0 ? (
                                    filteredCourses.map((course) => (
                                        <tr key={course.id} className="hover:bg-gray-50 transition-colors duration-150">
                                            <td className="px-4 py-4 border-b">
                                                <input
                                                    type="checkbox"
                                                    checked={selectedCourses.includes(course.id)}
                                                    onChange={() => toggleCourseSelection(course.id)}
                                                    className="h-4 w-4 rounded border-gray-300 text-text-color focus:ring-text-color checked:bg-text-color checked:border-text-color"                                            />
                                            </td>
                                            <td className="px-6 py-4 border-b text-sm font-medium text-gray-900">{course.id}</td>
                                            <td className="px-6 py-4 border-b text-sm text-gray-500">{course.code}</td>
                                            <td className="px-6 py-4 border-b text-sm text-gray-500">{course.name}</td>
                                            <td className="px-6 py-4 border-b">
                                                <div className="flex justify-center space-x-2">
                                                    <button
                                                        className="text-gray-500 duration-200"
                                                        onClick={() => console.log('Edit', course.id)}
                                                    >
                                                        <PencilIcon className="w-5 h-5 hover:text-text-color"/>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan={5} className="px-6 py-4 text-center text-sm text-gray-500">
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