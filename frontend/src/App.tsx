import React from "react";
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Login from "./component/Login/Login";
import Signup from "./component/Login/Signup";
import HeroSection from "./component/HeroSection/HeroSection";
import { NavigationProvider } from "./context/NavigationContext";
import Topic from "./component/Student/Topic";
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import ProtectedRoute from "./component/ProtectedRoute";

function App() {
    return (
        <BrowserRouter>
            <NavigationProvider>
                <ToastContainer />
                <Routes>
                    <Route path="/signup" element={<Signup />} />
                    <Route path="/login" element={<Login />} />
                    <Route
                        path="/*"
                        element={
                            <ProtectedRoute>
                                <HeroSection />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/topics/:courseId"
                        element={<Topic />}
                    />
                </Routes>
            </NavigationProvider>
        </BrowserRouter>
    );
}

export default App;