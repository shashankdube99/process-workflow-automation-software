import axios from 'axios';

// const API_URL = 'http://localhost:8081/api/machines';
// 1. Import your centralized instance instead of raw axios
import API from './api'; 

export const getMachines = async () => {
    // 2. Just use the relative endpoint path! 
    // The instance automatically prepends the Render URL + '/api'
    const response = await API.get('/machines'); 
    return response.data;
};

const getAuthHeader = () => {
  const token = localStorage.getItem('token');
  return { Authorization: `Bearer ${token}` };
};

const machineService = {
  getMachines: async () => {
    const response = await axios.get(API_URL, { headers: getAuthHeader() });
    return response.data;
  },

  createMachine: async (machineData) => {
    const response = await axios.post(API_URL, machineData, { headers: getAuthHeader() });
    return response.data;
  },

  updateMachine: async (id, machineData) => {
    const response = await axios.put(`${API_URL}/${id}`, machineData, { headers: getAuthHeader() });
    return response.data;
  },

  deleteMachine: async (id) => {
    const response = await axios.delete(`${API_URL}/${id}`, { headers: getAuthHeader() });
    return response.data;
  },

  allocateMachine: async (machineId, jobId) => {
    const response = await axios.post(`${API_URL}/${machineId}/allocate`, { jobId }, { headers: getAuthHeader() });
    return response.data;
  },

  releaseMachine: async (machineId) => {
    const response = await axios.post(`${API_URL}/${machineId}/release`, {}, { headers: getAuthHeader() });
    return response.data;
  }
};

export default machineService;