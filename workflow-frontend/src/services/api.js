import axios from 'axios';

// Create the centralized Axios instance pointing to your Spring Boot server
const API = axios.create({
baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8081/api',    headers: {
        'Content-Type': 'application/json',
    }
});

// 🏆 THE MISSING LINK: Dynamic Request Interceptor
API.interceptors.request.use(
    (config) => {
        // Look into browser storage for the token saved during login
        const token = localStorage.getItem('token');
        
        if (token) {
            // Inject the matching Authorization Bearer format Spring Security expects
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default API;