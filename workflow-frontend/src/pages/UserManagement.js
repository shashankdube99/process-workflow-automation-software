import React, { useState, useEffect } from 'react';
import { userService } from '../services/userService';

const UserManagement = () => {
    const [users, setUsers] = useState([]);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false); 
    const [editingUserId, setEditingUserId] = useState(null); 
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        mobile: '',
        password: '',
        roleName: 'USER'
    });

    const loadUsers = async () => {
        try {
            setError('');
            const data = await userService.getAllUsers();
            setUsers(Array.isArray(data) ? data : []);
        } catch (err) {
            console.error("API Fetch Error: ", err);
            setError('Could not retrieve user directory details. Verify your backend service is running.');
        }
    };

    useEffect(() => {
        loadUsers();
    }, []);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleEditClick = (user) => {
        setIsEditMode(true);
        setEditingUserId(user.id);
        setFormData({
            firstName: user.firstName,
            lastName: user.lastName,
            email: user.email, 
            mobile: user.mobile || '',
            password: '', 
            roleName: user.role 
        });
        setIsModalOpen(true);
    };

    const openCreateModal = () => {
        setIsEditMode(false);
        setEditingUserId(null);
        setFormData({ firstName: '', lastName: '', email: '', mobile: '', password: '', roleName: 'USER' });
        setIsModalOpen(true);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        try {
            if (isEditMode) {
                // 🟢 Step 1: Core Text Profile Text Parameters Payload
                const updatePayload = {
                    firstName: formData.firstName,
                    lastName: formData.lastName,
                    mobile: formData.mobile
                };
                
                // Fire standard PUT update only
                await userService.updateUser(editingUserId, updatePayload);
                setSuccess('User profile details updated successfully!');
            } else {
                // Create new user path
                await userService.createUser(formData);
                setSuccess('User profile initialized successfully!');
            }
            
            setIsModalOpen(false);
            loadUsers(); // Refresh grid layout rows
        } catch (err) {
            console.error("Submission Error Details:", err.response?.data);
            setError(err.response?.data?.message || 'Failed to execute account data operation.');
        }
    };

    const toggleUserStatus = async (user) => {
        try {
            setError('');
            if (user.status === 'ACTIVE') {
                await userService.deactivateUser(user.id);
            } else {
                await userService.activateUser(user.id);
            }
            loadUsers();
        } catch (err) {
            setError('Failed to update target account operational status.');
        }
    };

    return (
        <div className="container-fluid p-3">
            {/* Header Content Management Block */}
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="fw-bold text-dark mb-1">System Control Console</h2>
                    <p className="text-muted small mb-0">Manage infrastructure access tokens and profile permissions.</p>
                </div>
                <button 
                    onClick={openCreateModal} 
                    className="btn btn-primary fw-semibold px-4 shadow-sm"
                >
                    + Add New User
                </button>
            </div>

            {/* Notification Elements */}
            {success && <div className="alert alert-success py-2 shadow-sm">{success}</div>}
            {error && <div className="alert alert-danger py-2 shadow-sm">{error}</div>}

            {/* Bootstrap Responsive Directory Grid Table */}
            <div className="card shadow-sm border-0 rounded-3 overflow-hidden">
                <div className="table-responsive">
                    <table className="table table-hover align-middle mb-0">
                        <thead className="table-light text-uppercase font-monospace small text-secondary">
                            <tr>
                                <th className="ps-4 py-3">Name</th>
                                <th className="py-3">Email Address</th>
                                <th className="py-3">Assigned Role</th>
                                <th className="py-3">Operational Status</th>
                                <th className="text-end pe-4 py-3">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="small">
                            {users.length === 0 ? (
                                <tr>
                                    <td colSpan="5" className="text-center py-4 text-muted">
                                        No active system users resolved inside the connection pool.
                                    </td>
                                </tr>
                            ) : (
                                users.map((user) => (
                                    <tr key={user.id}>
                                        <td className="ps-4 fw-bold text-dark">{user.firstName} {user.lastName}</td>
                                        <td className="text-secondary">{user.email}</td>
                                        <td>
                                            <span className="badge bg-light text-primary border border-primary-subtle px-2.5 py-1.5">
                                                {user.role}
                                            </span>
                                        </td>
                                        <td>
                                            <span className={`badge px-2.5 py-1.5 ${
                                                user.status === 'ACTIVE' ? 'bg-success-subtle text-success' : 'bg-secondary-subtle text-secondary'
                                            }`}>
                                                {user.status}
                                            </span>
                                        </td>
                                        <td className="text-end pe-4">
                                            <button 
                                                onClick={() => handleEditClick(user)}
                                                className="btn btn-sm btn-outline-secondary fw-semibold px-2.5 me-2"
                                                title="Edit User Profile"
                                            >
                                                ✏️ Edit
                                            </button>
                                            <button 
                                                onClick={() => toggleUserStatus(user)}
                                                className={`btn btn-sm fw-bold px-3 ${
                                                    user.status === 'ACTIVE' ? 'btn-outline-danger' : 'btn-outline-success'
                                                }`}
                                            >
                                                {user.status === 'ACTIVE' ? 'Deactivate' : 'Activate'}
                                            </button>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* Dynamic Bootstrap Modal */}
            {isModalOpen && (
                <>
                    <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(0,0,0,0.5)', backdropFilter: 'blur(3px)' }}>
                        <div className="modal-dialog modal-dialog-centered">
                            <div className="modal-content border-0 shadow-lg rounded-3">
                                <div className="modal-header border-bottom-0 pt-4 px-4">
                                    <div>
                                        <h5 className="modal-title fw-bold text-dark">
                                            {isEditMode ? 'Modify User Details' : 'Provision System User'}
                                        </h5>
                                        <p className="text-muted small mb-0">
                                            {isEditMode ? 'Updating system variables for active accounts.' : 'Initial credentials will be encrypted securely upon creation.'}
                                        </p>
                                    </div>
                                    <button type="button" className="btn-close" onClick={() => setIsModalOpen(false)}></button>
                                </div>
                                
                                <form onSubmit={handleSubmit}>
                                    <div className="modal-body px-4 py-3">
                                        <div className="row g-3">
                                            <div className="col-6">
                                                <label className="form-label small fw-semibold text-secondary mb-1">First Name</label>
                                                <input type="text" name="firstName" value={formData.firstName} onChange={handleInputChange} required className="form-control form-control-sm" />
                                            </div>
                                            <div className="col-6">
                                                <label className="form-label small fw-semibold text-secondary mb-1">Last Name</label>
                                                <input type="text" name="lastName" value={formData.lastName} onChange={handleInputChange} required className="form-control form-control-sm" />
                                            </div>
                                            <div className="col-12">
                                                <label className="form-label small fw-semibold text-secondary mb-1">Email Address</label>
                                                <input 
                                                    type="email" 
                                                    name="email" 
                                                    value={formData.email} 
                                                    onChange={handleInputChange} 
                                                    required 
                                                    disabled={isEditMode} 
                                                    className="form-control form-control-sm bg-light" 
                                                />
                                            </div>
                                            <div className="col-12">
                                                <label className="form-label small fw-semibold text-secondary mb-1">Mobile Number</label>
                                                <input type="text" name="mobile" value={formData.mobile} onChange={handleInputChange} className="form-control form-control-sm" />
                                            </div>
                                            
                                            {!isEditMode && (
                                                <div className="col-12">
                                                    <label className="form-label small fw-semibold text-secondary mb-1">Initial Password</label>
                                                    <input type="password" name="password" value={formData.password} onChange={handleInputChange} required className="form-control form-control-sm" />
                                                </div>
                                            )}
                                            
                                            <div className="col-12">
                                                <label className="form-label small fw-semibold text-secondary mb-1">Security Role Mapping</label>
                                                {/* Role dropdown left disabled for now during edits to avoid confusion */}
                                                <select 
                                                    name="roleName" 
                                                    value={formData.roleName} 
                                                    onChange={handleInputChange} 
                                                    disabled={isEditMode}
                                                    className="form-select form-select-sm bg-light"
                                                >
                                                    <option value="USER">USER</option>
                                                    <option value="ADMIN">ADMIN</option>
                                                    <option value="RECEPTIONIST">RECEPTIONIST</option>
                                                </select>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="modal-footer border-top-0 pb-4 px-4">
                                        <button type="button" className="btn btn-sm btn-light border fw-semibold px-3" onClick={() => setIsModalOpen(false)}>Cancel</button>
                                        <button type="submit" className="btn btn-sm btn-primary fw-semibold px-4">
                                            {isEditMode ? 'Update Changes' : 'Save User'}
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                    <div className="modal-backdrop fade show"></div>
                </>
            )}
        </div>
    );
};

export default UserManagement;