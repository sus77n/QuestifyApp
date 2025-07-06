import { useGetCoursesQuery } from '../../API/service/course.service';

export default function ManageCourse() {
    const { data: courses, isLoading, isError } = useGetCoursesQuery();

    const handleEdit = (courseId) => {
        // TODO: Implement edit logic (open modal or navigate to edit page)
        alert(`Edit course ${courseId}`);
    };

    const handleDelete = (courseId) => {
        // TODO: Implement delete logic (show confirm and call delete API)
        alert(`Delete course ${courseId}`);
    };

    const handleAdd = () => {
        // TODO: Implement add logic (open modal or navigate to add page)
        alert('Add new course');
    };

    return (
        <div className="flex h-screen bg-light-background">
            <div className="flex-1 overflow-auto p-6 bg-white m-[8px] rounded-xl">
                <div className="flex justify-between items-center mb-6">
                    <h1 className="text-3xl font-bold text-text-color">Manage Courses</h1>
                    <button
                        className="bg-text-color text-white px-4 py-2 rounded-lg shadow hover:bg-dark-text transition-colors"
                        onClick={handleAdd}
                    >
                        Add New Course
                    </button>
                </div>
                {isLoading ? (
                    <div>Loading...</div>
                ) : isError ? (
                    <div className="text-red-500">Failed to load courses.</div>
                ) : (
                    <table className="min-w-full bg-white border border-gray-200 rounded-xl table-fixed ">
                        <colgroup>
                            <col style={{ width: '8%' }} />
                            <col style={{ width: '15%' }} />
                            <col style={{ width: '20%' }} />
                            <col style={{ width: '37%' }} />
                            <col style={{ width: '20%' }} />
                        </colgroup>
                        <thead>
                            <tr>
                                <th className="px-6 py-3 border-b text-left">ID</th>
                                <th className="px-6 py-3 border-b text-left">Code</th>
                                <th className="px-6 py-3 border-b text-left">Name</th>
                                <th className="px-6 py-3 border-b text-left">Description</th>
                                <th className="px-6 py-3 border-b text-center">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            {courses && courses.map((course) => (
                                <tr key={course.id} className="hover:bg-gray-50">
                                    <td className="px-6 py-4 border-b">{course.id}</td>
                                    <td className="px-6 py-4 border-b">{course.code}</td>
                                    <td className="px-6 py-4 border-b">{course.name}</td>
                                    <td className="px-6 py-4 border-b">{course.description}</td>
                                    <td className="px-6 py-4 border-b flex justify-evenly">
                                        <button
                                            className="text-white hover:bg-background-color px-4 py-2 bg-text-color rounded"
                                            onClick={() => handleEdit(course.id)}
                                        >
                                            Edit
                                        </button>
                                        <button
                                            className="text-white hover:bg-light-danger px-4 py-2 bg-danger rounded"
                                            onClick={() => handleDelete(course.id)}
                                        >
                                            Delete
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
}