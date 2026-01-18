import React from "react";
import {
  ArrowRightStartOnRectangleIcon,
  UserIcon,
  ArchiveBoxIcon,
  UserGroupIcon,
  BookOpenIcon,
  PresentationChartBarIcon,
} from "@heroicons/react/24/outline";
import { useLocation, useNavigate } from "react-router-dom";

const Navbar = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const role = localStorage.getItem("role");


  // Define route paths for tabs
  const navItemsByRole: Record<
    string,
    { icon: any; label: string; path: string }[]
  > = {
    STUDENT: [
      { icon: ArchiveBoxIcon, label: "My courses", path: "/my-courses" },
      { icon: BookOpenIcon, label: "Courses", path: "/courses" },
    ],
    TEACHER: [
      { icon: PresentationChartBarIcon, label: "Dashboard", path: "/teacher/dashboard" },
      { icon: BookOpenIcon, label: "Courses", path: "/teacher/courses" },

    ],
    ADMIN: [
      {
        icon: PresentationChartBarIcon,
        label: "Dashboard",
        path: "/admin/dashboard",
      },
      { icon: UserGroupIcon, label: "Users", path: "/admin/users" },
      { icon: BookOpenIcon, label: "Manage courses", path: "/admin/courses" },
    ],
  };

  const navIcons = navItemsByRole[role || ""] || [];

  return (
    <>
      {/* Desktop Navigation (hidden on mobile) */}
      <div className="hidden md:block h-screen mr-0 bg-light-background">
        <nav className="m-[8px] mr-0 bg-text-color h-[98vh] w-[80px] rounded-xl flex flex-col items-center pt-2">
          {/* Desktop nav content */}
          <NavIcon
            icon={UserIcon}
            label="My profile"
            path="/profile"
            currentPath={location.pathname}
            onClick={() => navigate("/profile")}
          />

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

      {/* Mobile Navigation (hidden on desktop) */}
      <div className="md:hidden fixed bottom-0 left-0 right-0 bg-text-color z-50">
        <nav className="flex justify-around items-center p-2">
          {/* Profile Icon */}
          <MobileNavIcon
            icon={UserIcon}
            label="My profile"
            path="/profile"
            currentPath={location.pathname}
            onClick={() => navigate("/profile")}
          />

          {/* Role-based Icons */}
          {navIcons.map(({ icon, label, path }) => (
            <MobileNavIcon
              key={path}
              icon={icon}
              label={label} // Shorten label for mobile
              path={path}
              currentPath={location.pathname}
              onClick={() => navigate(path)}
            />
          ))}

          {/* Logout Icon */}
          <MobileNavIcon
            icon={ArrowRightStartOnRectangleIcon}
            label="Logout"
            onClick={() => {
              localStorage.clear();
              window.location.href = "/login";
            }}
          />
        </nav>
      </div>
    </>
  );
};

// Mobile-specific icon component
const MobileNavIcon = ({
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
  const isActive =
      (path && currentPath?.startsWith(path)) ||
      (path === "/teacher/courses" &&
          currentPath?.startsWith("/teacher/course/"));

  return (
    <button
      onClick={onClick}
      className={`flex flex-col items-center align-middle w-16 h-14 pt-2 rounded-xl transition-all ${
        isActive ? "bg-white" : "hover:bg-white/10"
      }`}
    >
      <Icon
        className={`w-6 h-6 ${isActive ? "text-text-color" : "text-white"}`}
      />
      {label && (
        <span
          className={`text-[10px] mt-1 ${isActive ? "text-text-color" : "text-white"}`}
        >
          {label}
        </span>
      )}
    </button>
  );
};

// Original NavIcon component remains the same
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
  const isActive =
      (path && currentPath?.startsWith(path)) ||
      (path === "/teacher/courses" &&
          currentPath?.startsWith("/teacher/course/"));

  return (
    <button
      onClick={onClick}
      className={`flex flex-col items-center p-2 w-[67px] rounded-xl transition-all ${
        isActive ? "bg-white" : "hover:bg-white/10"
      }`}
    >
      <Icon
        className={`w-8 h-8 ${isActive ? "text-text-color" : "text-white"}`}
      />
      {label && (
        <span
          className={`text-[10px] mt-1 ${isActive ? "text-text-color" : "text-white"}`}
        >
          {label}
        </span>
      )}
    </button>
  );
};

export default Navbar;
