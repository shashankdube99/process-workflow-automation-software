import axios from 'axios';

const API_URL = 'http://localhost:8081/api/jobs';

const getAuthHeader = () => {
  const token = localStorage.getItem('token');
  return { Authorization: `Bearer ${token}` };
};

const workflowService = {
  updateStatus: async (jobId, statusData) => {
    const response = await axios.patch(`${API_URL}/${jobId}/status`, statusData, {
      headers: getAuthHeader(),
    });
    return response.data;
  },

  submitCustomerApproval: async (jobId, approvalData) => {
    const response = await axios.post(`${API_URL}/${jobId}/customer-approval`, approvalData, {
      headers: getAuthHeader(),
    });
    return response.data;
  },

  getTimeline: async (jobId) => {
    const response = await axios.get(`${API_URL}/${jobId}/timeline`, {
      headers: getAuthHeader(),
    });
    return response.data;
  }
};

export default workflowService;