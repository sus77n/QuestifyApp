import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import Navbar from "../Navbar/Navbar";
import Profile from "../Profile/Profile";
import Course from "../Student/Course";
import MyCourse from "../Student/MyCourse";
import Dashboard from "../Admin/Dashboard";
import ManageUser from "../Admin/ManageUser";
import ManageCourse from "../Admin/ManageCourse";
import { Error404Page } from "../material/errorPage";
import TeacherCourse from "../Teacher/TeacherCourse";
import TeacherDashboard from "../Teacher/TeacherDashboard";
import ManageLearningUnits from "../Teacher/ManageLearningUnits";
import ManageAssignment from "../Teacher/ManageAssignment";
import TeacherProgress from "../Teacher/TeacherProgress";

const HeroSection = () => {
  return (
    <div className="flex h-screen bg-light-background">
      <Navbar />
      <main className="flex-1 overflow-auto">
        <Routes>
          <Route path="/" element={<Navigate to="/profile" replace />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/courses" element={<Course />} />
          <Route path="/my-courses" element={<MyCourse />} />

          {/* If you want Admin routes here (optional): */}
          <Route path="/admin/dashboard" element={<Dashboard />} />
          <Route path="/admin/users" element={<ManageUser />} />
          <Route path="/admin/courses" element={<ManageCourse />} />

          <Route path="/teacher/dashboard" element={<TeacherDashboard />} />
          <Route path="/teacher/courses" element={<TeacherCourse />} />
          <Route path="/teacher/lessons/:courseId" element={<ManageLearningUnits />} />
          <Route path="/teacher/assignments/:courseId" element={<ManageAssignment />} />
          <Route path="/teacher/progress/:courseId" element={<TeacherProgress />} />

          {/* Fallback route */}
          <Route path="*" element={<Error404Page />} />
        </Routes>
      </main>
    </div>
  );
};

export default HeroSection;
