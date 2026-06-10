import axios from 'axios';

// const API_URL = 'http://localhost:8081/api/payments';
// 1. Import your centralized instance instead of raw axios
import API from './api'; 

export const getPayments = async () => {
    // 2. Just use the relative endpoint path! 
    // The instance automatically prepends the Render URL + '/api'
    const response = await API.get('/payments'); 
    return response.data;
};

const getAuthHeader = () => {
  const token = localStorage.getItem('token');
  return { Authorization: `Bearer ${token}` };
};

const paymentService = {
  createOrder: async (jobId, amount) => {
    const response = await axios.post(`${API_URL}/create-order`, { jobId, amount }, { headers: getAuthHeader() });
    return response.data;
  },

  verifyPayment: async (verificationData) => {
    const response = await axios.post(`${API_URL}/verify`, verificationData, { headers: getAuthHeader() });
    return response.data;
  }
};

export default paymentService;