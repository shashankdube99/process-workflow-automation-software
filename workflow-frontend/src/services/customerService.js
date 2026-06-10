import API from './api'; 

const customerService = {
  getCustomers: async (searchQuery = '') => {
    const response = await API.get(`/customers?search=${searchQuery}`);
    return response.data;
  },

  getCustomerById: async (id) => {
    const response = await API.get(`/customers/${id}`);
    return response.data;
  },

  createCustomer: async (customerData) => {
    const response = await API.post('/customers', customerData);
    return response.data;
  },

  updateCustomer: async (id, customerData) => {
    const response = await API.put(`/customers/${id}`, customerData);
    return response.data;
  }
};

export default customerService;