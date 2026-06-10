// 1. Import your custom API instance instead of raw axios
import API from './api'; // Check your folder depth (might be '../services/api')

export const getCustomers = async (search = '') => {
    // 2. Use the relative path. Your instance automatically prepends the Render URL + '/api'
    const response = await API.get(`/customers?search=${search}`);
    return response.data;
};

// Utility to get the auth token
const getAuthHeader = () => {
  const token = localStorage.getItem('token');
  return { Authorization: `Bearer ${token}` };
};

const customerService = {
  getCustomers: async (searchQuery = '') => {
    const response = await axios.get(`${API_URL}?search=${searchQuery}`, {
      headers: getAuthHeader(),
    });
    return response.data;
  },

  getCustomerById: async (id) => {
    const response = await axios.get(`${API_URL}/${id}`, {
      headers: getAuthHeader(),
    });
    return response.data;
  },

  createCustomer: async (customerData) => {
    const response = await axios.post(API_URL, customerData, {
      headers: getAuthHeader(),
    });
    return response.data;
  },

  updateCustomer: async (id, customerData) => {
    const response = await axios.put(`${API_URL}/${id}`, customerData, {
      headers: getAuthHeader(),
    });
    return response.data;
  }
};

export default customerService;