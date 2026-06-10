import api from './api'; // Leverages your central axios configuration instance

export const userService = {
  // 🚀 POST /api/users
  createUser: async (userData) => {
    const response = await api.post('/users', userData);
    return response.data;
  },

  // Matches UserController line 46: PUT /api/users/{id}
  updateUser: async (id, updatePayload) => {
    const response = await api.put(`/users/${id}`, updatePayload);
    return response.data;
  },

  // 🚀 GET /api/users
  getAllUsers: async () => {
    const response = await api.get('/users');
    return response.data;
  },

  // 🚀 PATCH /api/users/{id}/deactivate
  deactivateUser: async (id) => {
    const response = await api.patch(`/users/${id}/deactivate`);
    return response.data;
  },

  // 🚀 PATCH /api/users/{id}/activate
  activateUser: async (id) => {
    const response = await api.patch(`/users/${id}/activate`);
    return response.data;
  }
};