import React, { useState, useEffect } from 'react';
import reportService from '../services/reportService';
import { useNavigate } from 'react-router-dom';

function Dashboard() {
  const [stats, setStats] = useState({
    totalRevenue: 0,
    activeJobs: 0,
    pendingApprovals: 0,
    totalCustomers: 0
  });
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const data = await reportService.getDashboardStats();
        setStats(data);
      } catch (error) {
        console.error("Failed to load dashboard stats", error);
        // If they get a 403 Forbidden, they might not be an Admin/Manager
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  // --- NEW: Download PDF Handler ---
  const handleDownloadReport = async () => {
    try {
      const blob = await reportService.downloadReport();
      // Create a temporary URL for the downloaded file
      const url = window.URL.createObjectURL(new Blob([blob]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'Business_Report.pdf'); // Set filename
      document.body.appendChild(link);
      link.click(); // Simulate a click to download
      link.remove(); // Clean up
    } catch (error) {
      alert("Failed to download report.");
      console.error(error);
    }
  };

  if (loading) {
    return <div className="text-center p-5"><div className="spinner-border text-primary"></div></div>;
  }

  return (
    <div className="container-fluid p-4">
      {/* --- UPDATED: Header with Download Button --- */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2 className="fw-bold text-dark mb-0">Process Automation Dashboard</h2>
        <button className="btn btn-dark" onClick={handleDownloadReport}>
          📄 Download PDF Report
        </button>
      </div>
      
      {/* Metric Cards Row */}
      <div className="row g-4 mb-5">
        
        {/* Revenue Card */}
        <div className="col-12 col-sm-6 col-xl-3">
          <div className="card shadow-sm border-0 border-start border-success border-4 h-100">
            <div className="card-body">
              <div className="text-muted text-uppercase small fw-bold mb-1">Total Revenue</div>
              <div className="d-flex align-items-center">
                <h3 className="fw-bold mb-0 text-dark">
                  ₹{stats.totalRevenue ? stats.totalRevenue.toLocaleString('en-IN') : '0.00'}
                </h3>
              </div>
            </div>
          </div>
        </div>

        {/* Active Jobs Card */}
        <div className="col-12 col-sm-6 col-xl-3">
          <div className="card shadow-sm border-0 border-start border-primary border-4 h-100">
            <div className="card-body">
              <div className="text-muted text-uppercase small fw-bold mb-1">Active Jobs</div>
              <div className="d-flex align-items-center">
                <h3 className="fw-bold mb-0 text-dark">{stats.activeJobs}</h3>
              </div>
            </div>
          </div>
        </div>

        {/* Pending Approvals Card */}
        <div className="col-12 col-sm-6 col-xl-3">
          <div className="card shadow-sm border-0 border-start border-warning border-4 h-100">
            <div className="card-body">
              <div className="text-muted text-uppercase small fw-bold mb-1">Pending Approvals</div>
              <div className="d-flex align-items-center">
                <h3 className="fw-bold mb-0 text-dark">{stats.pendingApprovals}</h3>
              </div>
            </div>
          </div>
        </div>

        {/* Total Customers Card */}
        <div className="col-12 col-sm-6 col-xl-3">
          <div className="card shadow-sm border-0 border-start border-info border-4 h-100">
            <div className="card-body">
              <div className="text-muted text-uppercase small fw-bold mb-1">Total Customers</div>
              <div className="d-flex align-items-center">
                <h3 className="fw-bold mb-0 text-dark">{stats.totalCustomers}</h3>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Quick Actions Row */}
      <h4 className="fw-bold text-dark mb-3">Quick Actions</h4>
      <div className="row g-4">
        <div className="col-md-4">
          <button 
            className="btn btn-outline-primary w-100 p-4 shadow-sm"
            onClick={() => navigate('/dashboard/jobs')}
          >
            <h5 className="mb-0">Manage Jobs</h5>
          </button>
        </div>
        <div className="col-md-4">
          <button 
            className="btn btn-outline-success w-100 p-4 shadow-sm"
            onClick={() => navigate('/dashboard/customers')}
          >
            <h5 className="mb-0">Manage Customers</h5>
          </button>
        </div>
        <div className="col-md-4">
          <button 
            className="btn btn-outline-dark w-100 p-4 shadow-sm"
            onClick={() => navigate('/dashboard/machines')}
          >
            <h5 className="mb-0">View Machines</h5>
          </button>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;