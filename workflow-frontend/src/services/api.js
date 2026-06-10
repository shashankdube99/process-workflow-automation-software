import axios from 'axios';

// Create the centralized Axios instance pointing to your Spring Boot server
const API = axios.create({
    // 🏆 Safely tacks '/api' onto whatever base domain is provided
    baseURL: (process.env.REACT_APP_API_URL || 'http://localhost:8081') + '/api',
    headers: {
        'Content-Type': 'application/json',
    }
});

// Dynamic Request Interceptor
API.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default API;