import React from "react";
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Login from "./component/Login/Login";
import Signup from "./component/Login/Signup";
import HeroSection from "./component/HeroSection/HeroSection";
import SubmitScreen from "./component/Student/SubmitScreen";
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import ProtectedRoute from "./component/ProtectedRoute";
import AdminLayout from "./component/Admin/AdminLayout";
import Dashboard from "./component/Admin/Dashboard";
import ManageUser from "./component/Admin/ManageUser";
import ManageCourse from "./component/Admin/ManageCourse";
import { Error403Page, Error404Page, Error500Page } from "./component/material/errorPage";

function App() {
    return (
        <BrowserRouter>
            <ToastContainer />
            <Routes>
                <Route path="/signup" element={<Signup />} />
                <Route path="/login" element={<Login />} />
                <Route
                    path="/*"
                    element={
                        <ProtectedRoute allowedRoles={['ADMIN', 'STUDENT', 'TEACHER']}>
                            <HeroSection />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/topics/:courseId"
                    element={
                        <ProtectedRoute allowedRoles={['STUDENT']}>
                            <SubmitScreen />
                        </ProtectedRoute>
                    }
                />

                <Route path="/admin" element={<AdminLayout />}>
                    <Route path="dashboard" element={<Dashboard />} />
                    <Route path="users" element={<ManageUser />} />
                    <Route path="courses" element={<ManageCourse />} />
                </Route>

                <Route path="/403" element={<Error403Page />} />
                <Route path="/404" element={<Error404Page />} />
                <Route path="/500" element={<Error500Page />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
