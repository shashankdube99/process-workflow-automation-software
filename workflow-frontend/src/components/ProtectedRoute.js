import React from 'react';
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children, allowedRoles }) => {
    const token = localStorage.getItem('token');
    const userRole = localStorage.getItem('userRole');

    // ⛔ Rule 1: If there is no token badge stored, bounce them immediately to login
    if (!token) {
        return <Navigate to="/" replace />;
    }

    // ⛔ Rule 2: If the specific route requires special clearance level and they don't have it, deny access
    if (allowedRoles && !allowedRoles.includes(userRole)) {
        return (
            <div className="container vh-100 d-flex flex-column justify-content-center align-items-center">
                <div className="alert alert-danger text-center p-4 shadow" style={{ maxWidth: '500px' }}>
                    <h4 className="fw-bold">🚨 Access Explicitly Denied</h4>
                    <p className="mb-0 mt-2 text-secondary">
                        Your account clearance status (<strong>{userRole}</strong>) does not possess authority parameters to read this endpoint interface.
                    </p>
                </div>
            </div>
        );
    }

    // 🟢 Authorization passes - Render requested views normally
    return children;
};

export default ProtectedRoute;