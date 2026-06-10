import React, { useState, useEffect } from 'react';
import customerService from '../services/customerService';

function CustomerManagement() {
  const [customers, setCustomers] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Form State
  const [showModal, setShowModal] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [currentCustomerId, setCurrentCustomerId] = useState(null);
  const [formData, setFormData] = useState({
    companyName: '',
    contactPerson: '',
    email: '',
    mobile: '',
    address: '',
    gstNumber: ''
  });

  useEffect(() => {
    fetchCustomers();
  }, []);

  const fetchCustomers = async (query = '') => {
    try {
      setLoading(true);
      const data = await customerService.getCustomers(query);
      setCustomers(data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch customers.');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    fetchCustomers(searchQuery);
  };

  const handleInputChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const openModal = (customer = null) => {
    if (customer) {
      setIsEditing(true);
      setCurrentCustomerId(customer.id);
      setFormData({
        companyName: customer.companyName || '',
        contactPerson: customer.contactPerson || '',
        email: customer.email || '',
        mobile: customer.mobile || '',
        address: customer.address || '',
        gstNumber: customer.gstNumber || ''
      });
    } else {
      setIsEditing(false);
      setCurrentCustomerId(null);
      setFormData({ companyName: '', contactPerson: '', email: '', mobile: '', address: '', gstNumber: '' });
    }
    setShowModal(true);
  };

  const closeModal = () => setShowModal(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (isEditing) {
        await customerService.updateCustomer(currentCustomerId, formData);
      } else {
        await customerService.createCustomer(formData);
      }
      closeModal();
      fetchCustomers(searchQuery); // Refresh list
    } catch (err) {
      alert(err.response?.data?.message || 'Error saving customer.');
    }
  };

  return (
    <div className="container-fluid p-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2 className="fw-bold text-dark">Customer Management</h2>
        <button className="btn btn-primary" onClick={() => openModal()}>
          + New Customer
        </button>
      </div>

      {/* Search Bar */}
      <div className="card shadow-sm border-0 mb-4">
        <div className="card-body">
          <form onSubmit={handleSearch} className="d-flex">
            <input
              type="text"
              className="form-control me-2"
              placeholder="Search by Company, Mobile, or GST..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
            <button type="submit" className="btn btn-outline-secondary">Search</button>
            <button 
              type="button" 
              className="btn btn-outline-light text-dark ms-2"
              onClick={() => { setSearchQuery(''); fetchCustomers(''); }}
            >
              Clear
            </button>
          </form>
        </div>
      </div>

      {/* Error & Loading States */}
      {error && <div className="alert alert-danger">{error}</div>}
      {loading ? (
        <div className="text-center p-5"><div className="spinner-border text-primary" role="status"></div></div>
      ) : (
        /* Customer Table */
        <div className="card shadow-sm border-0">
          <div className="table-responsive">
            <table className="table table-hover mb-0">
              <thead className="table-light">
                <tr>
                  <th>Code</th>
                  <th>Company Name</th>
                  <th>Contact Person</th>
                  <th>Mobile</th>
                  <th>Status</th>
                  <th className="text-end">Actions</th>
                </tr>
              </thead>
              <tbody>
                {customers.length === 0 ? (
                  <tr><td colSpan="6" className="text-center py-4 text-muted">No customers found.</td></tr>
                ) : (
                  customers.map((c) => (
                    <tr key={c.id}>
                      <td className="align-middle fw-semibold">{c.customerCode}</td>
                      <td className="align-middle">{c.companyName}</td>
                      <td className="align-middle">{c.contactPerson}</td>
                      <td className="align-middle">{c.mobile}</td>
                      <td className="align-middle">
                        <span className={`badge ${c.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary'}`}>
                          {c.status}
                        </span>
                      </td>
                      <td className="text-end align-middle">
                        <button className="btn btn-sm btn-outline-primary" onClick={() => openModal(c)}>
                          Edit
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Create / Edit Modal */}
      {showModal && (
        <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog modal-lg">
            <div className="modal-content border-0 shadow">
              <div className="modal-header">
                <h5 className="modal-title fw-bold">{isEditing ? 'Edit Customer' : 'Add New Customer'}</h5>
                <button type="button" className="btn-close" onClick={closeModal}></button>
              </div>
              <form onSubmit={handleSubmit}>
                <div className="modal-body">
                  <div className="row g-3">
                    <div className="col-md-6">
                      <label className="form-label">Company Name *</label>
                      <input type="text" className="form-control" name="companyName" value={formData.companyName} onChange={handleInputChange} required />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Contact Person *</label>
                      <input type="text" className="form-control" name="contactPerson" value={formData.contactPerson} onChange={handleInputChange} required />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Email</label>
                      <input type="email" className="form-control" name="email" value={formData.email} onChange={handleInputChange} />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Mobile</label>
                      <input type="text" className="form-control" name="mobile" value={formData.mobile} onChange={handleInputChange} pattern="[0-9]{10}" title="Must be exactly 10 digits" />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">GST Number</label>
                      <input type="text" className="form-control" name="gstNumber" value={formData.gstNumber} onChange={handleInputChange} />
                    </div>
                    <div className="col-12">
                      <label className="form-label">Address</label>
                      <textarea className="form-control" name="address" rows="2" value={formData.address} onChange={handleInputChange}></textarea>
                    </div>
                  </div>
                </div>
                <div className="modal-footer bg-light">
                  <button type="button" className="btn btn-secondary" onClick={closeModal}>Cancel</button>
                  <button type="submit" className="btn btn-primary">Save Customer</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default CustomerManagement;