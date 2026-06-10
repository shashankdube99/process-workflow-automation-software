import API from './api'; 

const machineService = {
  getMachines: async () => {
    const response = await API.get('/machines');
    return response.data;
  },

  createMachine: async (machineData) => {
    const response = await API.post('/machines', machineData);
    return response.data;
  },

  updateMachine: async (id, machineData) => {
    const response = await API.put(`/machines/${id}`, machineData);
    return response.data;
  },

  deleteMachine: async (id) => {
    const response = await API.delete(`/machines/${id}`);
    return response.data;
  },

  allocateMachine: async (machineId, jobId) => {
    const response = await API.post(`/machines/${machineId}/allocate`, { jobId });
    return response.data;
  },

  releaseMachine: async (machineId) => {
    const response = await API.post(`/machines/${machineId}/release`, {});
    return response.data;
  }
};

export default machineService;