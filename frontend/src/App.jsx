import React, {useEffect} from "react";
import { BrowserRouter, Routes, Route } from 'react-router';
import Login from "./component/Login/Login";
import Signup from "./component/Login/Signup";
import HeroSection from "./component/HeroSection/HeroSection";
import {NavigationProvider} from "./context/NavigationContext";
import Topic from "./component/Course/Topic";
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

function App() {
    return (
        <BrowserRouter>
            <NavigationProvider>
                <ToastContainer />
                <Routes>
                    <Route path="/signup" element={<Signup />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/*" element={<HeroSection />} />
                    <Route path="/topics/:courseId" element={<Topic />} />
                </Routes>
            </NavigationProvider>
        </BrowserRouter>
    )
}
export default App;

