import React, { useEffect, useMemo, useState } from "react";
import { PencilIcon, HandThumbUpIcon } from "@heroicons/react/24/solid";
import { useEditUserMutation } from "../../API/service/user.service";
import { Spinner } from "../material/material";
import { CourseDTO } from "../../model/LearningUnitDTO";
import {
  useGetAllCompletedCoursesByUserIdQuery,
  useGetAllIncompletedCoursesByUserIdQuery,
} from "../../API/service/learningUnit.service";
import { toast } from "react-toastify";
import { useGetCurrentUserQuery } from "../../API/service/auth.service";
import {
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";

type SortKey = keyof CourseDTO;
type SortConfig = { key: SortKey; direction: "asc" | "desc" };
type TimeRange = "week" | "month" | "year";

const ActivityChart = ({
                         data,
                         timeRange,
                       }: {
  data: any[];
  timeRange: TimeRange;
}) => (
    <ResponsiveContainer width="100%" height={250}>
      <LineChart
          data={data}
          margin={{ top: 5, right: 10, left: 0, bottom: 5 }}
      >
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis
            dataKey={timeRange === "week" ? "day" : timeRange === "month" ? "week" : "month"}
        />
        <YAxis />
        <Tooltip />
        <Line type="monotone" dataKey="accesses" stroke="#02457A" />
      </LineChart>
    </ResponsiveContainer>
);

const EditNameSection = ({
                           isEditing,
                           firstName,
                           lastName,
                           setFirstName,
                           setLastName,
                           onSave,
                           onCancel,
                         }: any) =>
    isEditing ? (
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
                onClick={onSave}
            >
              Save
            </button>
            <button
                className="text-sm text-gray-600"
                onClick={onCancel}
            >
              Cancel
            </button>
          </div>
        </div>
    ) : (
        <div className="flex items-center">
          <h2 className="text-xl font-bold text-text-color">
            {firstName} {lastName}
          </h2>
          <PencilIcon
              className="w-5 ml-2 cursor-pointer text-text-color"
              onClick={() => onSave("start-edit")}
          />
        </div>
    );

const Profile = () => {
  const [isMobileView, setIsMobileView] = useState(false);
  const { data: user, isLoading: isLoadingUser, refetch } = useGetCurrentUserQuery();

  // Courses
  const { data: completedCourses } = useGetAllCompletedCoursesByUserIdQuery(user?.id || 0, { skip: !user?.id });
  const { data: ongoingCourses } = useGetAllIncompletedCoursesByUserIdQuery(user?.id || 0, { skip: !user?.id });

  // Profile data
  const profileData = {
    username: user?.username,
    email: user?.email,
    joinedDate: user?.createdAt
        ? new Date(user.createdAt).toLocaleString("default", { month: "long", year: "numeric" })
        : "",
    firstName: user?.firstName ?? "",
    lastName: user?.lastName ?? "",
    completedCourses: completedCourses?.length || 0,
    ongoingCourses: ongoingCourses?.length || 0,
  };

  // Edit profile
  const [isEditing, setIsEditing] = useState(false);
  const [firstName, setFirstName] = useState(profileData.firstName);
  const [lastName, setLastName] = useState(profileData.lastName);
  const [editUser] = useEditUserMutation();

  useEffect(() => {
    if (user) {
      setFirstName(user.firstName ?? "");
      setLastName(user.lastName ?? "");
    }
  }, [user]);

  const handleSave = async (mode?: string) => {
    if (mode === "start-edit") {
      setIsEditing(true);
      return;
    }
    try {
      await editUser({ id: user!.id, firstName, lastName }).unwrap();
      toast.success("Saved successfully!");
      setIsEditing(false);
      refetch();
    } catch {
      toast.error("Failed to update name");
    }
  };

  // Search + sort
  const [searchTerm, setSearchTerm] = useState("");
  const [sortConfig, setSortConfig] = useState<SortConfig>({ key: "name", direction: "asc" });

  const requestSort = (key: SortKey) => {
    setSortConfig((prev) => ({
      key,
      direction: prev.key === key && prev.direction === "asc" ? "desc" : "asc",
    }));
  };

  const filteredCourses = useMemo(() => {
    if (!completedCourses) return [];
    return [...completedCourses]
        .sort((a, b) => {
          const valA = a[sortConfig.key];
          const valB = b[sortConfig.key];
          if (valA == null) return 1;
          if (valB == null) return -1;
          if (valA < valB) return sortConfig.direction === "asc" ? -1 : 1;
          if (valA > valB) return sortConfig.direction === "asc" ? 1 : -1;
          return 0;
        })
        .filter(
            (course) =>
                course.code.toLowerCase().includes(searchTerm.toLowerCase()) ||
                course.name.toLowerCase().includes(searchTerm.toLowerCase())
        );
  }, [completedCourses, sortConfig, searchTerm]);

  // Activity chart data
  const [timeRange, setTimeRange] = useState<TimeRange>("week");
  const [activityData, setActivityData] = useState<any[]>([]);

  useEffect(() => {
    const mockData = {
      week: [
        { day: "Mon", accesses: 12 },
        { day: "Tue", accesses: 8 },
        { day: "Wed", accesses: 15 },
        { day: "Thu", accesses: 10 },
        { day: "Fri", accesses: 18 },
        { day: "Sat", accesses: 5 },
        { day: "Sun", accesses: 3 },
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
      ],
    };
    setActivityData(mockData[timeRange]);
  }, [timeRange]);

  // Resize for mobile detection
  useEffect(() => {
    const handleResize = () => setIsMobileView(window.innerWidth < 768);
    handleResize();
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  if (isMobileView) {
    // Mobile View
    return (
        <div className="h-screen flex flex-col bg-white overflow-y-auto pb-16">
          <div className="bg-text-color pt-3 pb-3 pl-5 pr-5 rounded-b-xl sticky top-0 z-10">
            <h1 className="text-2xl font-semibold text-white">Profile</h1>
          </div>
          <div className="p-4 flex flex-col items-center">
            {isLoadingUser ? (
                <Spinner />
            ) : (
                <>
                  <img src={`/img/ava1.png`} className="w-32 h-32 rounded-full object-cover mb-4 border-4 border-primary" />
                  <h2 className="text-xl font-bold text-text-color mb-2">@{profileData.username}</h2>
                  <div className="flex items-center mb-4">
                    <EditNameSection
                        isEditing={isEditing}
                        firstName={firstName}
                        lastName={lastName}
                        setFirstName={setFirstName}
                        setLastName={setLastName}
                        onSave={handleSave}
                        onCancel={() => {
                          setIsEditing(false);
                          setFirstName(profileData.firstName);
                          setLastName(profileData.lastName);
                        }}
                    />
                  </div>
                  {/* User info */}
                  <div className="w-full bg-gray-50 p-4 rounded-lg mb-4 grid grid-cols-2 gap-4">
                    <div><p className="text-sm text-gray-500">Email</p><p className="font-medium">{profileData.email}</p></div>
                    <div><p className="text-sm text-gray-500">Member since</p><p className="font-medium">{profileData.joinedDate}</p></div>
                    <div><p className="text-sm text-gray-500">Completed</p><p className="font-medium">{profileData.completedCourses} courses</p></div>
                    <div><p className="text-sm text-gray-500">Ongoing</p><p className="font-medium">{profileData.ongoingCourses} courses</p></div>
                  </div>
                </>
            )}
          </div>
          <div className="bg-white p-4 rounded-t-xl shadow-md">
            <h3 className="text-lg font-semibold text-text-color mb-4">Your activities</h3>
            <div className="flex space-x-2 mb-4">
              {(["week", "month", "year"] as TimeRange[]).map((range) => (
                  <button
                      key={range}
                      onClick={() => setTimeRange(range)}
                      className={`px-3 py-1 rounded-md text-sm ${
                          timeRange === range ? "bg-text-color text-white" : "bg-gray-100"
                      }`}
                  >
                    {range.charAt(0).toUpperCase() + range.slice(1)}
                  </button>
              ))}
            </div>
            <ActivityChart data={activityData} timeRange={timeRange} />
          </div>
          {/* Courses */}
          <div className="bg-white p-4 mt-2 rounded-t-xl shadow-md">
            <div className="flex flex-col mb-4">
              <div className="flex items-center justify-between">
                <h3 className="text-lg font-semibold text-text-color">
                  Completed {completedCourses?.length} courses <HandThumbUpIcon className="h-5 w-5 inline ml-1" />
                </h3>
                <input
                    type="text"
                    placeholder="Search courses..."
                    className="w-40 pl-3 pr-8 py-1.5 border rounded-lg text-sm"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>
              <div className="flex space-x-2 mt-2">
                <button onClick={() => requestSort("code")} className="px-2 py-1 rounded-md text-xs bg-gray-100">
                  Sort by Code
                </button>
                <button onClick={() => requestSort("name")} className="px-2 py-1 rounded-md text-xs bg-gray-100">
                  Sort by Name
                </button>
              </div>
            </div>
            <div className="space-y-3">
              {filteredCourses.length > 0 ? (
                  filteredCourses.map((course) => (
                      <div key={course.id} className="border-b pb-3">
                        <div className="font-medium text-text-color">{course.code}</div>
                        <p className="text-sm text-gray-700">{course.name}</p>
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

  // Desktop View
  return (
      <div className="h-screen flex ml-1">
        {/* Left Profile */}
        <div className="m-[8px] h-[98vh] w-[40vw] bg-white rounded-xl flex flex-col overflow-y-auto">
          <h1 className="text-2xl font-semibold text-white bg-text-color pt-3 pb-3 pl-5 pr-5">Profile</h1>
          <div className="flex flex-col items-center p-4">
            {isLoadingUser ? (
                <Spinner />
            ) : (
                <>
                  <img src={`/img/ava1.png`} className="w-48 h-48 rounded-full object-cover mb-6 border-4 border-primary" />
                  <h2 className="text-2xl font-bold text-text-color mb-4">@{profileData.username}</h2>
                  <EditNameSection
                      isEditing={isEditing}
                      firstName={firstName}
                      lastName={lastName}
                      setFirstName={setFirstName}
                      setLastName={setLastName}
                      onSave={handleSave}
                      onCancel={() => {
                        setIsEditing(false);
                        setFirstName(profileData.firstName);
                        setLastName(profileData.lastName);
                      }}
                  />
                  <div className="w-full p-2 text-text-color">
                    <div className="flex justify-between mb-3"><span>Email</span><span>{profileData.email}</span></div>
                    <div className="flex justify-between mb-3"><span>Member since</span><span>{profileData.joinedDate}</span></div>
                    <div className="flex justify-between mb-3"><span>Completed courses</span><span>{profileData.completedCourses}</span></div>
                    <div className="flex justify-between"><span>Ongoing courses</span><span>{profileData.ongoingCourses}</span></div>
                  </div>
                </>
            )}
          </div>
        </div>
        {/* Right Content */}
        <div className="m-[8px] w-full">
          <div className="w-full h-[40vh] bg-white rounded-xl flex flex-col p-4">
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-xl font-semibold text-text-color">Your activities</h3>
              <div className="flex space-x-2">
                {(["week", "month", "year"] as TimeRange[]).map((range) => (
                    <button
                        key={range}
                        onClick={() => setTimeRange(range)}
                        className={`px-3 py-1 rounded-md text-sm ${
                            timeRange === range ? "bg-text-color text-white" : "bg-gray-100"
                        }`}
                    >
                      {range.charAt(0).toUpperCase() + range.slice(1)}
                    </button>
                ))}
              </div>
            </div>
            <ActivityChart data={activityData} timeRange={timeRange} />
          </div>
          <div className="w-full h-[57vh] bg-white rounded-xl p-4 mt-2">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-[20px] text-text-color">
                Congratulations!! You completed {completedCourses?.length} courses
              </h3>
              <input
                  type="text"
                  placeholder="Search courses..."
                  className="w-64 pl-4 pr-10 py-2 border-2 rounded-lg"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
            <div className="relative overflow-x-auto sm:rounded-lg h-[calc(57vh-80px)]">
              <table className="w-full text-sm text-left text-gray-500">
                <thead className="text-xs text-gray-700 uppercase bg-gray-50">
                <tr>
                  <th className="px-6 py-3">#</th>
                  <th className="px-6 py-3">Code</th>
                  <th className="px-6 py-3">Course Name</th>
                </tr>
                </thead>
                <tbody>
                {filteredCourses.length > 0 ? (
                    filteredCourses.map((course, index) => (
                        <tr key={course.id} className="bg-white border-b hover:bg-gray-50 text-black">
                          <td className="px-6 py-4">{index + 1}</td>
                          <td className="px-6 py-4">{course.code}</td>
                          <td className="px-6 py-4">{course.name}</td>
                        </tr>
                    ))
                ) : (
                    <tr><td colSpan={3} className="text-center py-4">No courses found</td></tr>
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
