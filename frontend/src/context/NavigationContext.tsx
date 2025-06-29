import { createContext, useContext, useState, useEffect, Dispatch, SetStateAction } from 'react';
import { useLocation } from 'react-router-dom';

type NavigationContextType = {
    activeTab: string;
    setActiveTab: Dispatch<SetStateAction<string>>; // Correct type for useState setter
};

const NavigationContext = createContext<NavigationContextType>({
    activeTab: 'Courses',
    setActiveTab: () => {}, // Matches `(value: string) => void`
});

export function NavigationProvider({ children }: { children: React.ReactNode }) {
    const [activeTab, setActiveTab] = useState('Courses');
    const location = useLocation();

    useEffect(() => {
        const role = localStorage.getItem('role');
        switch (role) {
            case 'ADMIN': setActiveTab('Dashboard'); break;
            case 'TEACHER':
            case 'STUDENT':
            default: setActiveTab('Courses');
        }
    }, [location.pathname]);

    return (
        <NavigationContext.Provider value={{ activeTab, setActiveTab }}>
            {children}
        </NavigationContext.Provider>
    );
}

export const useNavigation = () => {
    const context = useContext(NavigationContext);
    if (!context) throw new Error('useNavigation must be used within a NavigationProvider');
    return context;
};