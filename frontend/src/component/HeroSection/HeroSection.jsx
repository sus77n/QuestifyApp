import React from 'react';
import Navbar from '../Navbar/Navbar';
import {NavigationProvider, useNavigation} from "../../context/NavigationContext";
import Home from "../Home/Home";
import Profile from "../Profile/Profile";
import Course from "../Course/Course";
import MyCourse from "../Course/MyCourse";

const HeroSection = () => {
    const { activeTab } = useNavigation();

    return (
        <div className="flex h-screen bg-light-background">
            <Navbar/>
            <main className="flex">
                {activeTab === 'Home' && <Home/>}
                {activeTab === 'Courses' && <Course/>}
                {activeTab === 'My profile' && <Profile/>}
                {activeTab === 'My courses' && <MyCourse/>}
            </main>
        </div>
    );
};

export default HeroSection;