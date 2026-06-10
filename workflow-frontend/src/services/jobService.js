import axios from 'axios';

// const API_URL = 'http://localhost:8081/api/jobs';
// 1. Import your centralized instance instead of raw axios
import API from './api'; 

export const getJobs = async () => {
    // 2. Just use the relative endpoint path! 
    // The instance automatically prepends the Render URL + '/api'
    const response = await API.get('/jobs'); 
    return response.data;
};

const getAuthHeader = () => {
  const token = localStorage.getItem('token');
  return { Authorization: `Bearer ${token}` };
};

const jobService = {
  getJobs: async (searchQuery = '') => {
    const response = await axios.get(`${API_URL}?search=${searchQuery}`, {
      headers: getAuthHeader(),
    });
    return response.data;
  },

  getJobById: async (id) => {
    const response = await axios.get(`${API_URL}/${id}`, {
      headers: getAuthHeader(),
    });
    return response.data;
  },

  createJob: async (jobData) => {
    const response = await axios.post(API_URL, jobData, {
      headers: getAuthHeader(),
    });
    return response.data;
  },

  updateJob: async (id, jobData) => {
    const response = await axios.put(`${API_URL}/${id}`, jobData, {
      headers: getAuthHeader(),
    });
    return response.data;
  },

  deleteJob: async (id) => {
    const response = await axios.delete(`${API_URL}/${id}`, {
      headers: getAuthHeader(),
    });
    return response.data;
  },

  uploadFile: async (jobId, file, fileType) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('fileType', fileType);

    const response = await axios.post(`${API_URL}/${jobId}/files`, formData, {
      headers: {
        ...getAuthHeader(),
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  }
};

export default jobService;