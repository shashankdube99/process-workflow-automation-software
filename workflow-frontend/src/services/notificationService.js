import axios from 'axios';

const API_URL = 'http://localhost:8081/api/notifications';

const getAuthHeader = () => {
  const token = localStorage.getItem('token');
  return { Authorization: `Bearer ${token}` };
};

const notificationService = {
  getNotifications: async () => {
    const response = await axios.get(API_URL, { headers: getAuthHeader() });
    return response.data;
  },

  getUnreadCount: async () => {
    const response = await axios.get(`${API_URL}/unread-count`, { headers: getAuthHeader() });
    return response.data.count;
  },

  markAsRead: async (id) => {
    const response = await axios.patch(`${API_URL}/${id}/read`, {}, { headers: getAuthHeader() });
    return response.data;
  },

  markAllAsRead: async () => {
    const response = await axios.patch(`${API_URL}/read-all`, {}, { headers: getAuthHeader() });
    return response.data;
  }
};

export default notificationService;