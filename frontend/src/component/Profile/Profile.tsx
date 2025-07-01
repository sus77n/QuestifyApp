import React, { useState } from "react";
import { ChevronRightIcon, PencilIcon } from "@heroicons/react/24/solid";
import { useGetCurrentUserQuery } from "../../API/service/user.service";

const Profile = () => {
    // Generate random avatar (1-3 as shown in your course code)
    const randomAvatar = Math.floor(Math.random() * 3) + 1;

    const { data: user, isLoading, isError } = useGetCurrentUserQuery();

    // Optionally, handle loading and error states
    if (isLoading) {
        return <div className="h-screen flex items-center justify-center">Loading...</div>;
    }
    
    if (isError || !user) {
        return <div className="h-screen flex items-center justify-center text-red-500">Failed to load user profile.</div>;
    }

    const profileData = {
        username: user.username,
        email: user.email,
        joinedDate: user.createdAt ? new Date(user.createdAt).toLocaleString('default', { month: 'long', year: 'numeric' }) : '',
        completedCourses: 5, // Example data, replace with real data if available
        ongoingCourses: 2 // Example data, replace with real data if available
    };

    const menuItems = [
        { title: "Account Settings", icon: <PencilIcon className="h-5 w-5" /> },
        { title: "Help Center", icon: <PencilIcon className="h-5 w-5" /> },
    ];

    return (
        <div className="h-screen flex ml-1">
            {/* Main content */}
            <div className="m-[8px] h-[98vh] w-[25vw] bg-white rounded-xl flex flex-col p-4">
                <h1 className="text-3xl font-semibold text-text-color mb-8">Profile</h1>

                <div className="flex justify-center h-full w-full">
                    {/* Profile section */}
                    <div className="flex flex-col items-center w-full">
                        <img
                            src={`/img/ava${randomAvatar}.png`}
                            className="w-48 h-48 rounded-full object-cover mb-6 border-4 border-primary"
                            alt="Profile avatar"
                        />
                        <h2 className="text-2xl font-bold text-text-color mb-1">{profileData.username}</h2>
                        <p className="text-gray-500 mb-6">{profileData.email}</p>

                        <div className="w-full bg-background-color rounded-xl p-4">
                            <div className="flex justify-between mb-3">
                                <span className="text-gray-500">Member since</span>
                                <span className="font-semibold">{profileData.joinedDate}</span>
                            </div>
                            <div className="flex justify-between mb-3">
                                <span className="text-gray-500">Completed courses</span>
                                <span className="font-semibold">{profileData.completedCourses}</span>
                            </div>
                            <div className="flex justify-between">
                                <span className="text-gray-500">Ongoing courses</span>
                                <span className="font-semibold">{profileData.ongoingCourses}</span>
                            </div>
                        </div>
                    </div>
                </div>

            </div>

            <div className="m-[8px] h-[98vh] w-full bg-white rounded-xl flex flex-col p-8">
                {/* Menu section */}
                <div className="w-full">
                    <h3 className="text-xl font-semibold text-text-color mb-6">Account</h3>

                    <div className="space-y-4">
                        {menuItems.map((item, index) => (
                            <div
                                key={index}
                                className="flex items-center justify-between p-4 border border-gray-200 rounded-xl cursor-pointer hover:bg-background-color transition-colors duration-200"
                            >
                                <div className="flex items-center">
                                    <div className="p-2 bg-primary/10 rounded-lg mr-4 text-primary">
                                        {item.icon}
                                    </div>
                                    <span className="font-medium">{item.title}</span>
                                </div>
                                <ChevronRightIcon className="h-5 w-5 text-gray-400" />
                            </div>
                        ))}
                    </div>

                    {/* Additional profile information */}
                    <div className="mt-10">
                        <h3 className="text-xl font-semibold text-text-color mb-6">About</h3>
                        <div className="bg-background-color rounded-xl p-6">
                            <p className="text-gray-600">
                                This is your profile page where you can manage your account settings,
                                view your course progress, and access other features.
                            </p>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    );
};

export default Profile;