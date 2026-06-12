import { Navigate } from 'react-router-dom';
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { GoogleOAuthProvider } from '@react-oauth/google'; // <-- NEW IMPORT

import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import DashboardLayout from './layouts/DashboardLayout';
import ProtectedRoute from './components/ProtectedRoute';
import UserManagement from './pages/UserManagement';
import CustomerManagement from './pages/CustomerManagement'; 
import JobManagement from './pages/JobManagement'; 
import MachineManagement from './pages/MachineManagement'; 
import Notifications from './pages/Notifications'; 

function App() {
  return (
    <GoogleOAuthProvider clientId={process.env.REACT_APP_GOOGLE_CLIENT_ID}>
      <Router>
        <Routes>
          {/* 🔓 Open/Public Route */}
          <Route path="/" element={<Login />} />

          {/* 🔒 Protected Dashboard Core Route */}
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <DashboardLayout>
                  <Dashboard />
                </DashboardLayout>
              </ProtectedRoute>
            }
          />

          {/* 🔒 Protected User Control Console Route */}
          <Route
            path="/dashboard/users"
            element={
              <ProtectedRoute>
                <DashboardLayout>
                  <UserManagement />
                </DashboardLayout>
              </ProtectedRoute>
            }
          />

          {/* 🔒 NEW Protected Analytics Route - Keeps it from bouncing to Login! */}
          <Route
            path="/dashboard/analytics"
            element={
              <ProtectedRoute>
                <DashboardLayout>
                  <div className="container-fluid p-3">
                    <h2 className="fw-bold text-dark mb-1">Analytics Board</h2>
                    <p className="text-muted small">Metrics processing engine pipeline coming soon...</p>

                    {/* Quick placeholder structural card */}
                    <div className="card shadow-sm border-0 rounded-3 p-4 mt-4 text-center text-muted">
                      📊 Chart engines and system health visualizations will be mapped here on Day 5.
                    </div>
                  </div>
                </DashboardLayout>
              </ProtectedRoute>
            }
          />

          <Route
            path="/dashboard/customers"
            element={
              <ProtectedRoute>
                <DashboardLayout>
                  <CustomerManagement />
                </DashboardLayout>
              </ProtectedRoute>
            }
          />

          <Route
            path="/dashboard/jobs"
            element={
              <ProtectedRoute>
                <DashboardLayout>
                  <JobManagement />
                </DashboardLayout>
              </ProtectedRoute>
            }
          />

          <Route
            path="/dashboard/machines"
            element={
              <ProtectedRoute>
                <DashboardLayout>
                  <MachineManagement />
                </DashboardLayout>
              </ProtectedRoute>
            }
          />

          <Route
            path="/dashboard/notifications"
            element={
              <ProtectedRoute>
                <DashboardLayout>
                  <Notifications />
                </DashboardLayout>
              </ProtectedRoute>
            }
          />

          {/* Fallback route to bounce messy URL targets */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Router>
    </GoogleOAuthProvider>
  );
}

export default App;