import { useNavigate } from 'react-router-dom';
import React, { useState } from 'react';
import axios from 'axios'; // 🟢 Import standard axios to bypass the instance's /api prefix

const Login = () => {
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleLoginSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            // 🟢 Hit the open Spring Security gateway explicitly on port 8081
            API.post('/auth/login', { email, password })
            
            const { accessToken, role } = response.data;

            // Persist the security tokens inside the browser cache space
            localStorage.setItem('token', accessToken);
            localStorage.setItem('userRole', role);

            // Forward the authenticated profile smoothly straight to the desk view
            navigate('/dashboard');
        } catch (err) {
            console.error("Login Error Frame:", err);
            setError(err.response?.data?.message || 'Invalid username or password match configuration.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container d-flex justify-content-center align-items-center vh-100" style={{ backgroundColor: '#f8f9fa' }}>
            <div className="card p-4 shadow-lg border-0" style={{ width: '100%', maxWidth: '400px', borderRadius: '12px' }}>
                <h3 className="text-center mb-4 text-primary fw-bold">Platform Login</h3>

                {error && <div className="alert alert-danger text-center py-2 small">{error}</div>}

                <form onSubmit={handleLoginSubmit}>
                    <div className="mb-3">
                        <label className="form-label fw-semibold text-secondary small">Corporate Email</label>
                        <input
                            type="email"
                            className="form-control"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            placeholder="name@company.com"
                        />
                    </div>
                    <div className="mb-4">
                        <label className="form-label fw-semibold text-secondary small">Password</label>
                        <input
                            type="password"
                            className="form-control"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            placeholder="••••••••"
                        />
                    </div>
                    <button
                        type="submit"
                        className="btn btn-primary w-100 fw-bold py-2"
                        disabled={loading}
                    >
                        {loading ? 'Verifying...' : 'Sign In'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default Login;