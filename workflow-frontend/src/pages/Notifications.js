import React, { useState, useEffect } from 'react';
import notificationService from '../services/notificationService';

function Notifications() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {
    try {
      setLoading(true);
      const data = await notificationService.getNotifications();
      setNotifications(data);
    } catch (err) {
      console.error('Failed to fetch notifications', err);
    } finally {
      setLoading(false);
    }
  };

  const handleMarkAsRead = async (id) => {
    try {
      await notificationService.markAsRead(id);
      // Update local state to feel instant
      setNotifications(notifications.map(n => n.id === id ? { ...n, read: true } : n));
    } catch (err) {
      console.error('Failed to mark as read', err);
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await notificationService.markAllAsRead();
      setNotifications(notifications.map(n => ({ ...n, read: true })));
    } catch (err) {
      console.error('Failed to mark all as read', err);
    }
  };

  return (
    <div className="container-fluid p-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2 className="fw-bold text-dark">Notifications</h2>
        <button 
          className="btn btn-outline-primary btn-sm" 
          onClick={handleMarkAllAsRead}
          disabled={!notifications.some(n => !n.read)}
        >
          Mark All as Read
        </button>
      </div>

      {loading ? (
        <div className="text-center p-5"><div className="spinner-border text-primary"></div></div>
      ) : (
        <div className="card shadow-sm border-0">
          <div className="list-group list-group-flush">
            {notifications.length === 0 ? (
              <div className="text-center p-5 text-muted">You have no notifications.</div>
            ) : (
              notifications.map((notif) => (
                <div key={notif.id} className={`list-group-item p-4 ${notif.read ? 'bg-light text-muted' : 'bg-white'}`}>
                  <div className="d-flex w-100 justify-content-between align-items-center">
                    <div>
                      <h6 className={`mb-1 ${!notif.read ? 'fw-bold text-dark' : ''}`}>
                        {!notif.read && <span className="badge bg-danger rounded-circle p-1 me-2" style={{ width: '10px', height: '10px', display: 'inline-block' }}></span>}
                        {notif.title}
                      </h6>
                      <p className="mb-1 small">{notif.message}</p>
                      <small className="text-muted">{new Date(notif.createdAt).toLocaleString()}</small>
                    </div>
                    {!notif.read && (
                      <button className="btn btn-sm btn-link text-decoration-none" onClick={() => handleMarkAsRead(notif.id)}>
                        Mark Read
                      </button>
                    )}
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
}

export default Notifications;