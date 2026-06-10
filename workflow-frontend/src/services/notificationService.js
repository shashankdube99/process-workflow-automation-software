import API from './api'; 

const notificationService = {
  getNotifications: async () => {
    const response = await API.get('/notifications');
    return response.data;
  },

  getUnreadCount: async () => {
    const response = await API.get('/notifications/unread-count');
    return response.data.count;
  },

  markAsRead: async (id) => {
    const response = await API.patch(`/notifications/${id}/read`, {});
    return response.data;
  },

  markAllAsRead: async () => {
    const response = await API.patch('/notifications/read-all', {});
    return response.data;
  }
};

export default notificationService;