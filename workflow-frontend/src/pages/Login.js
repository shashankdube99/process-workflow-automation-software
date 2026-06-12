import { useNavigate } from 'react-router-dom';
import React, { useState } from 'react';
import axios from 'axios'; 
import { GoogleLogin } from '@react-oauth/google'; // <-- NEW IMPORT

const Login = () => {
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    // 🏆 Dynamically find the root backend host (stripping any accidental /api suffixes)
    const getBackendHost = () => {
        if (process.env.REACT_APP_API_URL) {
            return process.env.REACT_APP_API_URL.replace('/api', '');
        }
        return 'http://localhost:8081';
    };

    // Standard Email/Password Login
    const handleLoginSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            // 🚀 Hits http://localhost:8081 locally, and your Render URL on production
            const response = await axios.post(`${getBackendHost()}/auth/login`, { 
                email, 
                password 
            });
            
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

    // --- NEW: Google Login Handler ---
    const handleGoogleSuccess = async (credentialResponse) => {
        setError('');
        setLoading(true);
        try {
            const response = await axios.post(`${getBackendHost()}/auth/google`, {
                credential: credentialResponse.credential
            });
            const { accessToken, role } = response.data;
            localStorage.setItem('token', accessToken);
            localStorage.setItem('userRole', role);
            navigate('/dashboard');
        } catch (err) {
            console.error("Google Auth Error:", err);
            setError(err.response?.data?.message || 'Google account verification failed.');
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

                {/* --- NEW: GOOGLE LOGIN SECTION --- */}
                <div className="text-center text-muted my-3 small fw-bold">OR</div>
                <div className="d-flex justify-content-center mb-2">
                    <GoogleLogin
                        onSuccess={handleGoogleSuccess}
                        onError={() => setError('Google Sign-In popup failed to initialize.')}
                        useOneTap={false}
                        shape="rectangular"
                        theme="outline"
                        text="signin_with"
                        size="large"
                    />
                </div>
                {/* --------------------------------- */}

            </div>
        </div>
    );
};

export default Login;