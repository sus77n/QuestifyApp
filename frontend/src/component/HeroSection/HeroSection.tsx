import Navbar from '../Navbar/Navbar';
import { useNavigation } from "../../context/NavigationContext";
import Home from "../Home/Home";
import Profile from "../Profile/Profile";
import Course from "../Student/Course";
import MyCourse from "../Student/MyCourse";
import Dashboard from "../Admin/Dashboard";
import ManageUser from "../Admin/ManageUser";
import ManageCourse from "../Admin/ManageCourse";

const HeroSection = () => {
    const { activeTab } = useNavigation();

    const renderContent = () => {
        switch (activeTab) {
            case 'Home':
                return <Home />;
            case 'Courses':
                return <Course />;
            case 'My courses':
                return <MyCourse />;
            case 'My profile':
                return <Profile />;
            case 'Dashboard':
                return <Dashboard />;
            case 'Users':
                return <ManageUser />;
                case 'Manage courses':
                    return <ManageCourse/>
            default:
                return <div className="p-10 text-xl">Page not found.</div>;
        }
    };

    return (
        <div className="flex h-screen bg-light-background">
            <Navbar />
            <main className="flex-1 overflow-auto">{renderContent()}</main>
        </div>
    );
};

export default HeroSection;
