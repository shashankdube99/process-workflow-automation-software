import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

const DashboardLayout = ({ children }) => {
    const navigate = useNavigate();
    const location = useLocation(); // 👈 Detects current URL path to highlight active buttons
    const userRole = localStorage.getItem('userRole') || 'USER';

    const handleLogout = () => {
        localStorage.clear(); // Wipe the JWT token and roles from the browser cache
        navigate('/');
    };

    return (
        <div className="d-flex vh-100" style={{ backgroundColor: '#f8f9fa' }}>
            {/* Sidebar Pane */}
            <div className="bg-dark text-white p-3 d-flex flex-column" style={{ width: '260px' }}>
                <h4 className="text-center fw-bold mb-4 text-primary">Workflow Engine</h4>
                <p className="text-muted small px-2 border-bottom border-secondary pb-2">
                    Logged in as: <strong className="text-warning">{userRole}</strong>
                </p>

                {/* Adaptive Sidebar Navigation links based on role */}
                <ul className="nav nav-pills flex-column mb-auto mt-2">
                    <li className="nav-item mb-2">
                        <button
                            onClick={() => navigate('/dashboard')}
                            className={`btn w-100 text-start py-2 ${location.pathname === '/dashboard' ? 'btn-primary text-white' : 'btn-dark text-white-50'}`}
                        >
                            條 Main Desk
                        </button>
                    </li>

                    {userRole === 'ADMIN' && (
                        <li className="nav-item mb-2">
                            <button
                                onClick={() => navigate('/dashboard/users')}
                                className={`btn w-100 text-start py-2 ${location.pathname === '/dashboard/users' ? 'btn-info text-dark font-weight-bold' : 'btn-dark text-info'}`}
                            >
                                ⚙️ User Management
                            </button>
                        </li>
                    )}

                    {userRole === 'ADMIN' && (
                        <li className="nav-item mb-2">
                            <button
                                onClick={() => navigate('/dashboard/customers')}
                                className={`btn w-100 text-start py-2 ${location.pathname === '/dashboard/customers' ? 'btn-info text-dark font-weight-bold' : 'btn-dark text-info'}`}
                            >
                                ⚙️ Customer Management
                            </button>
                        </li>
                    )}

                    {userRole === 'ADMIN' && (
                        <li className="nav-item mb-2">
                            <button
                                onClick={() => navigate('/dashboard/jobs')}
                                className={`btn w-100 text-start py-2 ${location.pathname === '/dashboard/jobs' ? 'btn-info text-dark font-weight-bold' : 'btn-dark text-info'}`}
                            >
                                ⚙️ Job Management
                            </button>
                        </li>
                    )}

                    {userRole === 'ADMIN' && (
                        <li className="nav-item mb-2">
                            <button
                                onClick={() => navigate('/dashboard/machines')}
                                className={`btn w-100 text-start py-2 ${location.pathname === '/dashboard/machines' ? 'btn-info text-dark font-weight-bold' : 'btn-dark text-info'}`}
                            >
                                ⚙️ Machine Management
                            </button>
                        </li>
                    )}

                    {/* Add this somewhere in your Sidebar or Header */}
                    <li className="nav-item">
                        <a className="nav-link" href="/dashboard/notifications">
                            🔔 Notifications
                        </a>
                    </li>

                    {(userRole === 'ADMIN') && (
                        <li className="nav-item mb-2">
                            <button
                                onClick={() => navigate('/dashboard/analytics')}
                                className={`btn w-100 text-start py-2 ${location.pathname === '/dashboard/analytics' ? 'btn-primary text-white' : 'btn-dark text-white-50'}`}
                            >
                                📊 Analytics Board
                            </button>
                        </li>
                    )}
                </ul>

                <button className="btn btn-danger w-100 fw-bold mt-auto py-2" onClick={handleLogout}>
                    🚪 Log Out
                </button>
            </div>

            {/* Main Content Area Viewport */}
            <div className="flex-grow-1 d-flex flex-column overflow-hidden">
                <header className="bg-white border-bottom p-3 d-flex justify-content-between align-items-center shadow-sm">
                    <span className="fw-semibold text-secondary">Process Automation Dashboard</span>
                    <span className="badge bg-primary px-3 py-2">System Active</span>
                </header>
                <main className="flex-grow-1 p-4 overflow-auto">
                    {children} {/* 👈 UserManagement will render here naturally when routing hits /dashboard/users */}
                </main>
            </div>
        </div>
    );
};

export default DashboardLayout;