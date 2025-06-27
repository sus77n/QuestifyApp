import React from "react";
import { useNavigation } from '../../context/NavigationContext';
import { ArrowRightStartOnRectangleIcon, UserIcon, ListBulletIcon, ArchiveBoxIcon } from "@heroicons/react/24/outline";

const Navbar = () => {
    const { activeTab, setActiveTab } = useNavigation();
    const navIcons = [
        { icon: ArchiveBoxIcon, id: 'My courses' },
        // { icon: HomeIcon, id: 'Home' },
        { icon: ListBulletIcon, id: 'Courses' },
    ];

    return (
        <div className="h-screen mr-0">
            <nav className="m-[8px] mr-0 bg-text-color h-[98vh] w-[80px] rounded-xl flex flex-col items-center pt-2">
                <NavIcon
                    icon={UserIcon}
                    id="My profile"
                    activeTab={activeTab}
                    onClick={() => setActiveTab('My profile')}
                />
                <div className="absolute top-1/2 transform -translate-y-1/2 flex flex-col gap-4">
                    {navIcons.map(({ icon, id }) => (
                        <NavIcon
                            key={id}
                            icon={icon}
                            id={id}
                            activeTab={activeTab}
                            onClick={() => setActiveTab(id)}
                        />
                    ))}
                </div>
                <div className="absolute bottom-5">
                    <NavIcon
                        icon={ArrowRightStartOnRectangleIcon}
                        id="Logout"  // Added id for the logout button
                        onClick={() => console.log('Logout')}
                    />
                </div>
            </nav>
        </div>
    );
};

const NavIcon = ({
                     icon: Icon,
                     id,
                     activeTab,
                     onClick,
                 }: {
    icon: React.ComponentType<React.SVGProps<SVGSVGElement>>;
    id?: string;
    activeTab?: string;
    onClick: () => void;
}) => {
    const isActive = id && activeTab === id;

    return (
        <button
            onClick={onClick}
            className={`flex flex-col items-center p-2 w-[67px] rounded-xl transition-all ${
                isActive ? 'bg-white' : 'hover:bg-white/10'
            }`}
        >
            <Icon className={`w-8 h-8 ${isActive ? 'text-text-color' : 'text-white'}`} />
            {id && (
                <span className={`text-[10px] mt-1 ${isActive ? 'text-text-color' : 'text-white'}`}>
          {id}
        </span>
            )}
        </button>
    );
};


export default Navbar;