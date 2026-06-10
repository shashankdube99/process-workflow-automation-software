import API from './api'; 

const jobService = {
  getJobs: async (searchQuery = '') => {
    const response = await API.get(`/jobs?search=${searchQuery}`);
    return response.data;
  },

  getJobById: async (id) => {
    const response = await API.get(`/jobs/${id}`);
    return response.data;
  },

  createJob: async (jobData) => {
    const response = await API.post('/jobs', jobData);
    return response.data;
  },

  updateJob: async (id, jobData) => {
    const response = await API.put(`/jobs/${id}`, jobData);
    return response.data;
  },

  deleteJob: async (id) => {
    const response = await API.delete(`/jobs/${id}`);
    return response.data;
  },

  uploadFile: async (jobId, file, fileType) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('fileType', fileType);

    const response = await API.post(`/jobs/${jobId}/files`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  }
};

export default jobService;