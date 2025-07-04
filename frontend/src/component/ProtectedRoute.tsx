import { Navigate } from 'react-router-dom';
import { isAuthenticated, getUserRole } from '../utils/AuthUtils';

const ProtectedRoute = ({
                            allowedRoles,
                            children,
                        }: {
    allowedRoles: string[];
    children: React.ReactNode;
}) => {
    if (!isAuthenticated()) {
        return <Navigate to="/login" replace />;
    }

    const role = getUserRole();

    if (!role || !allowedRoles.includes(role)) {
        return <Navigate to="/403" replace />;
    }

    return <>{children}</>;
};

export default ProtectedRoute;
