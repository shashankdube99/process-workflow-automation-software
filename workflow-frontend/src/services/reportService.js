import API from './api'; 

const reportService = {
  getDashboardStats: async () => {
    const response = await API.get('/reports/dashboard-stats');
    return response.data;
  },

  downloadReport: async () => {
    const response = await API.get('/reports/download', { 
      responseType: 'blob' // Keeps binary file response handling intact
    });
    return response.data;
  }
};

export default reportService;