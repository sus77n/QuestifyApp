import React, { useEffect, useState } from "react";
import { PencilIcon } from "@heroicons/react/24/solid";
import { useEditUserMutation } from "../../API/service/user.service";
import {
  CartesianGrid,
  Line,
  LineChart,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { Spinner } from "../material/material";
import { HandThumbUpIcon } from "@heroicons/react/16/solid";
import { CourseDTO } from "../../model/LearningUnitDTO";
import {
  useGetAllCompletedCoursesByUserIdQuery,
  useGetAllIncompletedCoursesByUserIdQuery,
} from "../../API/service/learningUnit.service";
import { toast } from "react-toastify";
import { useGetCurrentUserQuery } from "../../API/service/auth.service";

type SortKey = keyof CourseDTO;
type SortConfig = {
  key: SortKey;
  direction: "asc" | "desc";
};

const Profile = () => {
  const [isMobileView, setIsMobileView] = useState(window.innerWidth < 768);
  const {
    data: user,
    isLoading: isLoadingUser,
    refetch,
  } = useGetCurrentUserQuery();
  const { data: completedCourses } = useGetAllCompletedCoursesByUserIdQuery(
    user?.id || 0,
    {
      skip: !user?.id,
    },
  );

  useEffect(() => {
    const handleResize = () => {
      setIsMobileView(window.innerWidth < 768);
    };
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  const userId = user?.id || 0;
  const { data: ongoingCourses } = useGetAllIncompletedCoursesByUserIdQuery(
    userId,
    {
      skip: !userId,
    },
  );

  const profileData = {
    username: user?.username,
    email: user?.email,
    joinedDate: user?.createdAt
      ? new Date(user.createdAt).toLocaleString("default", {
          month: "long",
          year: "numeric",
        })
      : "",
    firstName: user?.firstName || null,
    lastName: user?.lastName || null,
    completedCourses: completedCourses?.length || 0,
    ongoingCourses: ongoingCourses?.length || 0,
  };

  const [isEditing, setIsEditing] = useState(false);
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");

  useEffect(() => {
    if (user) {
      setFirstName(user.firstName ?? "");
      setLastName(user.lastName ?? "");
    }
  }, [user]);

  const [editUser] = useEditUserMutation();

  const handleSave = async () => {
    try {
      await editUser({ firstName, lastName }).unwrap();
      toast.success("Saved successfully!");
      setIsEditing(false);
      refetch();
    } catch (err) {
      console.error(err);
      toast.error("Failed to update name");
    }
  };

  const [searchTerm, setSearchTerm] = useState("");
  const [sortConfig, setSortConfig] = useState<SortConfig>({
    key: "name",
    direction: "asc",
  });

  const requestSort = (key: SortKey) => {
    let direction: "asc" | "desc" = "asc";
    if (sortConfig.key === key && sortConfig.direction === "asc") {
      direction = "desc";
    }
    setSortConfig({ key, direction });
  };

  const sortedCourses = [...(completedCourses ?? [])].sort((a, b) => {
    const valA = a[sortConfig.key];
    const valB = b[sortConfig.key];

    if (valA == null) return 1;
    if (valB == null) return -1;

    if (valA < valB) return sortConfig.direction === "asc" ? -1 : 1;
    if (valA > valB) return sortConfig.direction === "asc" ? 1 : -1;
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

  const [timeRange, setTimeRange] = useState<"week" | "month" | "year">("week");
  const [activityData, setActivityData] = useState<
    DayData[] | WeekData[] | MonthData[]
  >([]);

  useEffect(() => {
    const mockData = {
      week: [
        { day: "Mon", accesses: 12, time: "morning" },
        { day: "Tue", accesses: 8, time: "afternoon" },
        { day: "Wed", accesses: 15, time: "evening" },
        { day: "Thu", accesses: 10, time: "morning" },
        { day: "Fri", accesses: 18, time: "evening" },
        { day: "Sat", accesses: 5, time: "afternoon" },
        { day: "Sun", accesses: 3, time: "night" },
      ],
      month: [
        { week: "Week 1", accesses: 45 },
        { week: "Week 2", accesses: 38 },
        { week: "Week 3", accesses: 52 },
        { week: "Week 4", accesses: 41 },
      ],
      year: [
        { month: "Jan", accesses: 120 },
        { month: "Feb", accesses: 95 },
        { month: "Mar", accesses: 95 },
        { month: "Apr", accesses: 95 },
        { month: "May", accesses: 95 },
        { month: "June", accesses: 95 },
        { month: "July", accesses: 95 },
      ],
    };
    setActivityData(mockData[timeRange]);
  }, [timeRange]);

  if (isMobileView) {
    return (
      <div className="h-screen flex flex-col bg-white overflow-y-auto pb-16">
        {/* Mobile Profile View */}
        <div className="bg-text-color pt-3 pb-3 pl-5 pr-5 rounded-b-xl sticky top-0 z-10">
          <h1 className="text-2xl font-semibold text-white">Profile</h1>
        </div>

        <div className="p-4 flex flex-col items-center">
          {isLoadingUser ? (
            <div className="flex justify-center items-center h-full">
              <Spinner />
            </div>
          ) : (
            <>
              <img
                src={`/img/ava1.png`}
                className="w-32 h-32 rounded-full object-cover mb-4 border-4 border-primary"
                alt="Profile avatar"
              />
              <h2 className="text-xl font-bold text-text-color mb-2">
                @{profileData.username}
              </h2>

              <div className="flex items-center mb-4">
                {isEditing ? (
                  <div className="flex flex-col items-center w-full">
                    <input
                      className="border p-2 rounded w-full mb-2"
                      value={firstName}
                      onChange={(e) => setFirstName(e.target.value)}
                      placeholder="First name"
                    />
                    <input
                      className="border p-2 rounded w-full"
                      value={lastName}
                      onChange={(e) => setLastName(e.target.value)}
                      placeholder="Last name"
                    />
                    <div className="flex mt-2 w-full justify-end">
                      <button
                        className="text-sm text-green-600 font-semibold mr-3"
                        onClick={handleSave}
                      >
                        Save
                      </button>
                      <button
                        className="text-sm text-gray-600"
                        onClick={() => {
                          setIsEditing(false);
                          setFirstName(profileData.firstName ?? "");
                          setLastName(profileData.lastName ?? "");
                        }}
                      >
                        Cancel
                      </button>
                    </div>
                  </div>
                ) : (
                  <>
                    <h2 className="text-xl font-bold">
                      {profileData.firstName} {profileData.lastName}
                    </h2>
                    <PencilIcon
                      className="w-5 ml-2 cursor-pointer"
                      onClick={() => setIsEditing(true)}
                    />
                  </>
                )}
              </div>

              <div className="w-full bg-gray-50 p-4 rounded-lg mb-4">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm text-gray-500">Email</p>
                    <p className="font-medium">{profileData.email}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-500">Member since</p>
                    <p className="font-medium">{profileData.joinedDate}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-500">Completed</p>
                    <p className="font-medium">
                      {profileData.completedCourses} courses
                    </p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-500">Ongoing</p>
                    <p className="font-medium">
                      {profileData.ongoingCourses} courses
                    </p>
                  </div>
                </div>
              </div>
            </>
          )}
        </div>

        {/* Mobile Activities View */}
        <div className="bg-white p-4 rounded-t-xl shadow-md">
          <h3 className="text-lg font-semibold text-text-color mb-4">
            Your activities
          </h3>
          <div className="flex space-x-2 mb-4">
            <button
              onClick={() => setTimeRange("week")}
              className={`px-3 py-1 rounded-md text-sm ${timeRange === "week" ? "bg-text-color text-white" : "bg-gray-100"}`}
            >
              Week
            </button>
            <button
              onClick={() => setTimeRange("month")}
              className={`px-3 py-1 rounded-md text-sm ${timeRange === "month" ? "bg-text-color text-white" : "bg-gray-100"}`}
            >
              Month
            </button>
            <button
              onClick={() => setTimeRange("year")}
              className={`px-3 py-1 rounded-md text-sm ${timeRange === "year" ? "bg-text-color text-white" : "bg-gray-100"}`}
            >
              Year
            </button>
          </div>

          <div className="h-64">
            <LineChart
              width={window.innerWidth - 40}
              height={250}
              data={activityData}
              margin={{ top: 5, right: 2, left: 5, bottom: 5 }}
            >
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis
                dataKey={
                  timeRange === "week"
                    ? "day"
                    : timeRange === "month"
                      ? "week"
                      : "month"
                }
              />
              <YAxis />
              <Tooltip />
              <Line type="monotone" dataKey="accesses" stroke="#02457A" />
            </LineChart>
          </div>
        </div>

        {/* Mobile Completed Courses View */}
        <div className="bg-white p-4 mt-2 rounded-t-xl shadow-md">
          <div className="flex flex-col mb-4">
            <div className="flex items-center justify-between">
              <h3 className="text-lg font-semibold text-text-color">
                Completed {completedCourses?.length} courses
                <HandThumbUpIcon className="h-5 w-5 text-text-color inline ml-1" />
              </h3>
              <div className="relative w-40">
                <input
                  type="text"
                  placeholder="Search courses..."
                  className="w-full pl-3 pr-8 py-1.5 border rounded-lg focus:outline-none focus:border-text-color text-sm"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
                <svg
                  className="absolute right-2 top-2 h-4 w-4 text-text-color"
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                  />
                </svg>
              </div>
            </div>

            {/* Sort buttons for mobile */}
            <div className="flex space-x-2 mt-2">
              <button
                onClick={() => requestSort("code")}
                className={`px-2 py-1 rounded-md text-xs ${sortConfig.key === "code" ? "bg-text-color text-white" : "bg-gray-100"}`}
              >
                Sort by Code{" "}
                {sortConfig.key === "code" &&
                  (sortConfig.direction === "asc" ? "↑" : "↓")}
              </button>
              <button
                onClick={() => requestSort("name")}
                className={`px-2 py-1 rounded-md text-xs ${sortConfig.key === "name" ? "bg-text-color text-white" : "bg-gray-100"}`}
              >
                Sort by Name{" "}
                {sortConfig.key === "name" &&
                  (sortConfig.direction === "asc" ? "↑" : "↓")}
              </button>
            </div>
          </div>

          <div className="space-y-3">
            {filteredCourses.length > 0 ? (
              filteredCourses.map((course, index) => (
                <div key={course.id} className="border-b pb-3">
                  <div className="flex justify-between items-start">
                    <div>
                      <div className="font-medium text-text-color">
                        {course.code}
                      </div>
                      <p className="text-sm text-gray-700">{course.name}</p>
                    </div>
                  </div>
                </div>
              ))
            ) : (
              <p className="text-center text-gray-500 py-4">No courses found</p>
            )}
          </div>
        </div>
      </div>
    );
  }

  // Desktop View (same as original)
  return (
    <div className="h-screen flex ml-1">
      {/* Left Profile Section */}
      <div className="m-[8px] h-[98vh] w-[40vw] bg-white rounded-xl flex flex-col overflow-y-auto">
        <h1 className="text-2xl font-semibold text-white bg-text-color pt-3 pb-3 pl-5 pr-5">
          Profile
        </h1>
        <div className="flex justify-center h-full w-full p-4">
          {isLoadingUser ? (
            <div className="flex flex-col items-center w-full h-full">
              <Spinner />
            </div>
          ) : (
            <div className="flex flex-col items-center w-full">
              <img
                src={`/img/ava1.png`}
                className="w-48 h-48 rounded-full object-cover mb-6 border-4 border-primary"
                alt="Profile avatar"
              />
              <h2 className="text-2xl font-bold text-text-color mb-4">
                @{profileData.username}
              </h2>
              <div className="flex items-center justify-center w-full mb-4 text-text-color text-sm">
                {isEditing ? (
                  <div>
                    <div>
                      <input
                        className="border p-1 mr-2 rounded w-44"
                        value={firstName}
                        onChange={(e) => setFirstName(e.target.value)}
                      />
                      <input
                        className="border p-1 mr-2 rounded w-44"
                        value={lastName}
                        onChange={(e) => setLastName(e.target.value)}
                      />
                    </div>
                    <div className="flex justify-end mt-2 mr-2">
                      <button
                        className="text-sm text-green-600 font-semibold mr-2"
                        onClick={handleSave}
                      >
                        Save
                      </button>
                      <button
                        className="text-sm text-gray-600"
                        onClick={() => {
                          setIsEditing(false);
                          setFirstName(profileData.firstName ?? "");
                          setLastName(profileData.lastName ?? "");
                        }}
                      >
                        Cancel
                      </button>
                    </div>
                  </div>
                ) : (
                  <>
                    <h2 className="text-2xl font-bold">
                      {profileData.lastName} {profileData.firstName}
                    </h2>
                    <PencilIcon
                      className="w-6 ml-2 cursor-pointer"
                      onClick={() => setIsEditing(true)}
                    />
                  </>
                )}
              </div>

              <div className="w-full p-2 text-text-color">
                <div className="flex justify-between mb-3">
                  <span>Email</span>
                  <span>{profileData.email}</span>
                </div>
                <div className="flex justify-between mb-3">
                  <span>Member since</span>
                  <span>{profileData.joinedDate}</span>
                </div>
                <div className="flex justify-between mb-3">
                  <span>Completed courses</span>
                  <span>{profileData.completedCourses}</span>
                </div>
                <div className="flex justify-between">
                  <span>Ongoing courses</span>
                  <span>{profileData.ongoingCourses}</span>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Right Content Section */}
      <div className="m-[8px] w-full">
        <div className="w-full h-[40vh] bg-white rounded-xl flex flex-col p-4">
          <div className="flex justify-between items-center mb-6">
            <h3 className="text-xl font-semibold text-text-color">
              Your activities
            </h3>
            <div className="flex space-x-2">
              <button
                onClick={() => setTimeRange("week")}
                className={`px-3 py-1 rounded-md text-sm ${timeRange === "week" ? "bg-text-color text-white" : "bg-gray-100"}`}
              >
                Week
              </button>
              <button
                onClick={() => setTimeRange("month")}
                className={`px-3 py-1 rounded-md text-sm ${timeRange === "month" ? "bg-text-color text-white" : "bg-gray-100"}`}
              >
                Month
              </button>
              <button
                onClick={() => setTimeRange("year")}
                className={`px-3 py-1 rounded-md text-sm ${timeRange === "year" ? "bg-text-color text-white" : "bg-gray-100"}`}
              >
                Year
              </button>
            </div>
          </div>

          <div className="p-3 rounded-lg">
            <div className="h-48">
              <LineChart
                width={850}
                height={200}
                data={activityData}
                margin={{ top: 5, right: 5, left: 5, bottom: 5 }}
              >
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis
                  dataKey={
                    timeRange === "week"
                      ? "day"
                      : timeRange === "month"
                        ? "week"
                        : "month"
                  }
                />
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
                <h3 className="text-[20px] text-text-color">
                  Congratulation !! You completed {completedCourses?.length}{" "}
                  courses
                </h3>
                <HandThumbUpIcon className="h-5 w-5 text-text-color" />
              </div>
            </div>
            <div className="relative w-64">
              <input
                type="text"
                placeholder="Search courses..."
                className="w-full pl-4 pr-10 py-2 border-2 rounded-lg focus:outline-none focus:border-text-color"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
              <svg
                className="absolute right-3 top-2.5 h-5 w-5 text-text-color"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                />
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
                      <button onClick={() => requestSort("code")}>
                        <svg
                          className={`w-3 h-3 ms-1.5 ${sortConfig.key === "code" ? "text-gray-700" : "text-gray-400"}`}
                          aria-hidden="true"
                          xmlns="http://www.w3.org/2000/svg"
                          fill="currentColor"
                          viewBox="0 0 24 24"
                        >
                          <path d="M8.574 11.024h6.852a2.075 2.075 0 0 0 1.847-1.086 1.9 1.9 0 0 0-.11-1.986L13.736 2.9a2.122 2.122 0 0 0-3.472 0L6.837 7.952a1.9 1.9 0 0 0-.11 1.986 2.074 2.074 0 0 0 1.847 1.086Zm6.852 1.952H8.574a2.072 2.072 0 0 0-1.847 1.087 1.9 1.9 0 0 0 .11 1.985l3.426 5.05a2.123 2.123 0 0 0 3.472 0l3.427-5.05a1.9 1.9 0 0 0 .11-1.985 2.074 2.074 0 0 0-1.846-1.087Z" />
                        </svg>
                      </button>
                    </div>
                  </th>
                  <th scope="col" className="px-6 py-3">
                    <div className="flex items-center">
                      Course Name
                      <button onClick={() => requestSort("name")}>
                        <svg
                          className={`w-3 h-3 ms-1.5 ${sortConfig.key === "name" ? "text-gray-700" : "text-gray-400"}`}
                          aria-hidden="true"
                          xmlns="http://www.w3.org/2000/svg"
                          fill="currentColor"
                          viewBox="0 0 24 24"
                        >
                          <path d="M8.574 11.024h6.852a2.075 2.075 0 0 0 1.847-1.086 1.9 1.9 0 0 0-.11-1.986L13.736 2.9a2.122 2.122 0 0 0-3.472 0L6.837 7.952a1.9 1.9 0 0 0-.11 1.986 2.074 2.074 0 0 0 1.847 1.086Zm6.852 1.952H8.574a2.072 2.072 0 0 0-1.847 1.087 1.9 1.9 0 0 0 .11 1.985l3.426 5.05a2.123 2.123 0 0 0 3.472 0l3.427-5.05a1.9 1.9 0 0 0 .11-1.985 2.074 2.074 0 0 0-1.846-1.087Z" />
                        </svg>
                      </button>
                    </div>
                  </th>
                </tr>
              </thead>
              <tbody>
                {filteredCourses.length > 0 ? (
                  filteredCourses.map((course, index) => (
                    <tr
                      key={course.id}
                      className="bg-white border-b hover:bg-gray-50 text-black"
                    >
                      <td className="px-6 py-4 whitespace-nowrap">
                        {index + 1}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        {course.code}
                      </td>
                      <td className="px-6 py-4">{course.name}</td>
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
