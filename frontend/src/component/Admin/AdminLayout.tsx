import { Outlet } from 'react-router-dom';
import ProtectedRoute from '../ProtectedRoute';

const AdminLayout = () => (
    <ProtectedRoute allowedRoles={['ADMIN']}>
        <div className="flex">
            {/* Optional Admin Navbar */}
            <Outlet />
        </div>
    </ProtectedRoute>
);

export default AdminLayout;
