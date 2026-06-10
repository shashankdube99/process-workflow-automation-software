import axios from 'axios';

// const API_URL = 'http://localhost:8081/api/reports';
// 1. Import your centralized instance instead of raw axios
import API from './api'; 

export const getReports = async () => {
    // 2. Just use the relative endpoint path! 
    // The instance automatically prepends the Render URL + '/api'
    const response = await API.get('/reports'); 
    return response.data;
};

const getAuthHeader = () => {
  const token = localStorage.getItem('token');
  return { Authorization: `Bearer ${token}` };
};

const reportService = {
  getDashboardStats: async () => {
    const response = await axios.get(`${API_URL}/dashboard-stats`, { headers: getAuthHeader() });
    return response.data;
  },

  // --- NEW: Download PDF Handler ---
  downloadReport: async () => {
    const response = await axios.get(`${API_URL}/download`, { 
      headers: getAuthHeader(),
      responseType: 'blob' // CRITICAL: Tells Axios to expect a binary file, not JSON
    });
    return response.data;
  }
};

export default reportService;