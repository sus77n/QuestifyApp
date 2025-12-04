import { Outlet } from "react-router-dom";
import ProtectedRoute from "../ProtectedRoute";
import Navbar from "../Navbar/Navbar";

const TeacherLayout = () => {
    return (
        <ProtectedRoute allowedRoles={["TEACHER"]}>
            <div className="flex">
                <Navbar />

                <div className="flex-1 overflow-auto">
                    <Outlet />
                </div>
            </div>
        </ProtectedRoute>
    );
};


export default TeacherLayout;
