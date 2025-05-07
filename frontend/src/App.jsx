import React, {useEffect} from "react";
import { BrowserRouter, Routes, Route } from 'react-router';
import Login from "./component/Login/Login";
import Signup from "./component/Login/Signup";
import HeroSection from "./component/HeroSection/HeroSection";
import {NavigationProvider} from "./context/NavigationContext";
import Topic from "./component/Course/Topic";

function App() {
    return (
        <BrowserRouter>
            <NavigationProvider>
                <Routes>
                    <Route path="/signup" element={<Signup />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/*" element={<HeroSection />} />
                    <Route path="/topics/:courseId" element={
                        <div className="flex h-screen bg-light-background">
                            {/*<Navbar />*/}
                            <main className="flex">
                                <Topic />
                            </main>
                        </div>
                    } />
                </Routes>
            </NavigationProvider>
        </BrowserRouter>
    )
}
export default App;

