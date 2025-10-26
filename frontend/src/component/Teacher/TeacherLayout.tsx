import { Outlet } from "react-router-dom";
import ProtectedRoute from "../ProtectedRoute";
import Navbar from "../Navbar/Navbar";

const TeacherLayout = () => (
    <ProtectedRoute allowedRoles={["TEACHER"]}>
        <div className="flex">
            <Navbar />
            <Outlet />
        </div>
    </ProtectedRoute>
);

export default TeacherLayout;
