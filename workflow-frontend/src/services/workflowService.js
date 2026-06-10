import API from './api'; 

const workflowService = {
  updateStatus: async (jobId, statusData) => {
    const response = await API.patch(`/jobs/${jobId}/status`, statusData);
    return response.data;
  },

  submitCustomerApproval: async (jobId, approvalData) => {
    const response = await API.post(`/jobs/${jobId}/customer-approval`, approvalData);
    return response.data;
  },

  getTimeline: async (jobId) => {
    const response = await API.get(`/jobs/${jobId}/timeline`);
    return response.data;
  }
};

export default workflowService;