import React from "react";
import {
    ArrowRightStartOnRectangleIcon,
    UserIcon,
    ArchiveBoxIcon,
    UserGroupIcon,
    BookOpenIcon,
    PresentationChartBarIcon
} from "@heroicons/react/24/outline";
import { useLocation, useNavigate } from "react-router-dom";

const Navbar = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const role = localStorage.getItem("role");

    // Define route paths for tabs
    const navItemsByRole: Record<string, { icon: any; label: string; path: string }[]> = {
        STUDENT: [
            { icon: ArchiveBoxIcon, label: "My courses", path: "/my-courses" },
            { icon: BookOpenIcon, label: "Courses", path: "/courses" },
        ],
        TEACHER: [
            { icon: BookOpenIcon, label: "My courses", path: "/my-courses" },
            { icon: BookOpenIcon, label: "Courses", path: "/courses" },
        ],
        ADMIN: [
            { icon: PresentationChartBarIcon, label: "Dashboard", path: "/admin/dashboard" },
            { icon: UserGroupIcon, label: "Users", path: "/admin/users" },
            { icon: BookOpenIcon, label: "Manage courses", path: "/admin/courses" },
        ],
    };

    const navIcons = navItemsByRole[role || ""] || [];

    return (
        <div className="h-screen mr-0">
            <nav className="m-[8px] mr-0 bg-text-color h-[98vh] w-[80px] rounded-xl flex flex-col items-center pt-2">
                {/* Top - Profile Icon */}
                <NavIcon
                    icon={UserIcon}
                    label="My profile"
                    path="/profile"
                    currentPath={location.pathname}
                    onClick={() => navigate("/profile")}
                />

                {/* Center - Role-based Icons */}
                <div className="absolute top-1/2 transform -translate-y-1/2 flex flex-col gap-4">
                    {navIcons.map(({ icon, label, path }) => (
                        <NavIcon
                            key={path}
                            icon={icon}
                            label={label}
                            path={path}
                            currentPath={location.pathname}
                            onClick={() => navigate(path)}
                        />
                    ))}
                </div>

                {/* Bottom - Logout */}
                <div className="absolute bottom-5">
                    <NavIcon
                        icon={ArrowRightStartOnRectangleIcon}
                        label="Logout"
                        onClick={() => {
                            localStorage.clear();
                            window.location.href = "/login";
                        }}
                    />
                </div>
            </nav>
        </div>
    );
};

const NavIcon = ({
                     icon: Icon,
                     label,
                     path,
                     currentPath,
                     onClick,
                 }: {
    icon: React.ComponentType<React.SVGProps<SVGSVGElement>>;
    label?: string;
    path?: string;
    currentPath?: string;
    onClick: () => void;
}) => {
    const isActive = path && currentPath?.startsWith(path);

    return (
        <button
            onClick={onClick}
            className={`flex flex-col items-center p-2 w-[67px] rounded-xl transition-all ${
                isActive ? "bg-white" : "hover:bg-white/10"
            }`}
        >
            <Icon className={`w-8 h-8 ${isActive ? "text-text-color" : "text-white"}`} />
            {label && (
                <span className={`text-[10px] mt-1 ${isActive ? "text-text-color" : "text-white"}`}>
                    {label}
                </span>
            )}
        </button>
    );
};

export default Navbar;
