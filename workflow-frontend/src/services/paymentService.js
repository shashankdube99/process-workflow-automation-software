import API from './api'; 

const paymentService = {
  createOrder: async (jobId, amount) => {
    const response = await API.post('/payments/create-order', { jobId, amount });
    return response.data;
  },

  verifyPayment: async (verificationData) => {
    const response = await API.post('/payments/verify', verificationData);
    return response.data;
  }
};

export default paymentService;