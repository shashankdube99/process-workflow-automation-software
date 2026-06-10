import React, { useState, useEffect } from 'react';
import jobService from '../services/jobService';
import customerService from '../services/customerService';
import workflowService from '../services/workflowService';

// --- NEW PAYMENT IMPORTS ---
import paymentService from '../services/paymentService';
import { loadRazorpayScript } from '../utils/loadRazorpay';

function JobManagement() {
  const [jobs, setJobs] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [loading, setLoading] = useState(true);

  // Job Form State
  const [showModal, setShowModal] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [currentJobId, setCurrentJobId] = useState(null);
  const [formData, setFormData] = useState({
    customerId: '', productName: '', quantity: '', description: '', dueDate: '', priority: 'NORMAL'
  });

  // Workflow State
  const [showStatusModal, setShowStatusModal] = useState(false);
  const [showTimelineModal, setShowTimelineModal] = useState(false);
  const [timeline, setTimeline] = useState([]);
  const [statusForm, setStatusForm] = useState({ status: '', remarks: '' });
  const [isCustomerApproval, setIsCustomerApproval] = useState(false);

  // Constants for Workflow Steps
  const JOB_STATUSES = [
    "DRAFT", "DESIGN_IN_PROGRESS", "AWAITING_CUSTOMER_APPROVAL", 
    "DESIGN_REJECTED", "DESIGN_APPROVED", "PRODUCTION_IN_PROGRESS", 
    "FINISHING_IN_PROGRESS", "QUALITY_CHECK", "QA_REJECTED", 
    "APPROVED", "COMPLETED", "CANCELLED"
  ];

  useEffect(() => {
    fetchJobs();
    fetchCustomersForDropdown();
  }, []);

  const fetchJobs = async (query = '') => {
    try { setLoading(true); setJobs(await jobService.getJobs(query)); } 
    catch (err) { console.error('Failed to fetch jobs', err); } 
    finally { setLoading(false); }
  };

  const fetchCustomersForDropdown = async () => {
    try { setCustomers(await customerService.getCustomers()); } 
    catch (err) { console.error('Failed to fetch customers', err); }
  };

  // --- JOB CRUD HANDLERS ---
  const handleSearch = (e) => { e.preventDefault(); fetchJobs(searchQuery); };
  const handleInputChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });
  
  const openModal = (job = null) => {
    if (job) {
      setIsEditing(true); setCurrentJobId(job.jobId);
      setFormData({
        customerId: job.customerId || '', productName: job.productName || '',
        quantity: job.quantity || '', description: job.description || '',
        dueDate: job.dueDate || '', priority: job.priority || 'NORMAL'
      });
    } else {
      setIsEditing(false); setCurrentJobId(null);
      setFormData({ customerId: '', productName: '', quantity: '', description: '', dueDate: '', priority: 'NORMAL' });
    }
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      isEditing ? await jobService.updateJob(currentJobId, formData) : await jobService.createJob(formData);
      setShowModal(false); fetchJobs(searchQuery);
    } catch (err) { alert(err.response?.data?.message || 'Error saving job.'); }
  };

  // --- WORKFLOW HANDLERS ---
  const openStatusModal = (job) => {
    setCurrentJobId(job.jobId);
    setStatusForm({ status: job.status, remarks: '' });
    setIsCustomerApproval(job.status === 'AWAITING_CUSTOMER_APPROVAL');
    setShowStatusModal(true);
  };

  const handleStatusSubmit = async (e) => {
    e.preventDefault();
    try {
      if (isCustomerApproval) {
        await workflowService.submitCustomerApproval(currentJobId, { 
          status: statusForm.status, comments: statusForm.remarks 
        });
      } else {
        await workflowService.updateStatus(currentJobId, statusForm);
      }
      setShowStatusModal(false);
      fetchJobs(searchQuery);
    } catch (err) { alert(err.response?.data?.message || 'Error updating status.'); }
  };

  const openTimelineModal = async (jobId) => {
    try {
      setCurrentJobId(jobId);
      const data = await workflowService.getTimeline(jobId);
      setTimeline(data);
      setShowTimelineModal(true);
    } catch (err) { alert('Failed to load timeline.'); }
  };

  // --- NEW: PAYMENT HANDLER ---
  const handlePayment = async (job) => {
    // 1. Load the Razorpay Script
    const isLoaded = await loadRazorpayScript();
    if (!isLoaded) {
      alert('Razorpay SDK failed to load. Are you online?');
      return;
    }

    try {
      // 2. Create the Order on the backend (Hardcoded 5000 for test)
      const testAmount = 5000; 
      const orderResponse = await paymentService.createOrder(job.jobId, testAmount);

      // 3. Configure the Razorpay Popup
      const options = {
        key: 'rzp_test_Sz56fzICmSu1XC', // Your specific Razorpay Test Key ID
        amount: testAmount * 100, // Amount in paise
        currency: 'INR',
        name: 'Workflow Automation',
        description: `Payment for Job: ${job.jobNumber}`,
        order_id: orderResponse.razorpayOrderId,
        
        // 4. Handle the success callback
        handler: async function (response) {
          try {
            await paymentService.verifyPayment({
              razorpayOrderId: response.razorpay_order_id,
              razorpayPaymentId: response.razorpay_payment_id,
              razorpaySignature: response.razorpay_signature
            });
            alert('Payment Successful and Verified!');
            fetchJobs(); // Refresh the job list to show PAID status
          } catch (err) {
            alert('Payment Verification Failed!');
          }
        },
        prefill: {
          name: job.customerName,
          email: 'test@customer.com',
          contact: '9999999999'
        },
        theme: {
          color: '#0d6efd' // Bootstrap primary blue
        }
      };

      // 5. Open the Popup
      const rzp = new window.Razorpay(options);
      rzp.on('payment.failed', function (response) {
        alert(`Payment Failed: ${response.error.description}`);
      });
      rzp.open();

    } catch (err) {
      alert(err.response?.data?.message || 'Error initiating payment.');
    }
  };

  return (
    <div className="container-fluid p-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2 className="fw-bold text-dark">Job Management</h2>
        <button className="btn btn-primary" onClick={() => openModal()}>+ Create Job</button>
      </div>

      <div className="card shadow-sm border-0 mb-4">
        <div className="card-body">
          <form onSubmit={handleSearch} className="d-flex">
            <input type="text" className="form-control me-2" placeholder="Search Jobs or Customers..." value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)} />
            <button type="submit" className="btn btn-outline-secondary">Search</button>
          </form>
        </div>
      </div>

      {loading ? (
        <div className="text-center p-5"><div className="spinner-border text-primary"></div></div>
      ) : (
        <div className="card shadow-sm border-0">
          <div className="table-responsive">
            <table className="table table-hover mb-0">
              <thead className="table-light">
                <tr>
                  <th>Job Number</th>
                  <th>Customer</th>
                  <th>Product</th>
                  <th>Due Date</th>
                  <th>Status</th>
                  <th className="text-end">Actions</th>
                </tr>
              </thead>
              <tbody>
                {jobs.map((job) => (
                  <tr key={job.jobId}>
                    <td className="align-middle fw-semibold text-primary">{job.jobNumber}</td>
                    <td className="align-middle">{job.customerName}</td>
                    <td className="align-middle">{job.productName}</td>
                    <td className="align-middle">{job.dueDate}</td>
                    <td className="align-middle">
                      <span className={`badge ${job.status.includes('REJECTED') ? 'bg-danger' : job.status === 'COMPLETED' ? 'bg-success' : 'bg-info text-dark'}`}>
                        {job.status.replace(/_/g, ' ')}
                      </span>
                    </td>
                    <td className="text-end align-middle">
                      {/* --- NEW: Pay Now Button --- */}
                      {job.status === 'COMPLETED' && (
                        <button className="btn btn-sm btn-success me-2" onClick={() => handlePayment(job)}>
                          Pay Now
                        </button>
                      )}
                      
                      <button className="btn btn-sm btn-outline-primary me-2" onClick={() => openStatusModal(job)}>Status</button>
                      <button className="btn btn-sm btn-outline-dark me-2" onClick={() => openTimelineModal(job.jobId)}>Timeline</button>
                      <button className="btn btn-sm btn-outline-secondary" onClick={() => openModal(job)}>Edit</button>
                    </td>
                  </tr>
                ))}
                {jobs.length === 0 && <tr><td colSpan="6" className="text-center py-4 text-muted">No jobs found.</td></tr>}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* --- CREATE / EDIT MODAL --- */}
      {showModal && (
        <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog modal-lg">
            <div className="modal-content border-0 shadow">
              <div className="modal-header">
                <h5 className="modal-title fw-bold">{isEditing ? 'Edit Job' : 'Create New Job'}</h5>
                <button type="button" className="btn-close" onClick={() => setShowModal(false)}></button>
              </div>
              <form onSubmit={handleSubmit}>
                <div className="modal-body row g-3">
                  <div className="col-md-6">
                    <label className="form-label">Customer *</label>
                    <select className="form-select" name="customerId" value={formData.customerId} onChange={handleInputChange} required>
                      <option value="">Select Customer...</option>
                      {customers.map(c => <option key={c.id} value={c.id}>{c.companyName}</option>)}
                    </select>
                  </div>
                  <div className="col-md-6">
                    <label className="form-label">Product Name *</label>
                    <input type="text" className="form-control" name="productName" value={formData.productName} onChange={handleInputChange} required />
                  </div>
                  <div className="col-md-4">
                    <label className="form-label">Quantity *</label>
                    <input type="number" className="form-control" name="quantity" value={formData.quantity} onChange={handleInputChange} required />
                  </div>
                  <div className="col-md-4">
                    <label className="form-label">Due Date</label>
                    <input type="date" className="form-control" name="dueDate" value={formData.dueDate} onChange={handleInputChange} />
                  </div>
                  <div className="col-md-4">
                    <label className="form-label">Priority</label>
                    <select className="form-select" name="priority" value={formData.priority} onChange={handleInputChange}>
                      <option value="LOW">Low</option>
                      <option value="NORMAL">Normal</option>
                      <option value="HIGH">High</option>
                    </select>
                  </div>
                  <div className="col-12">
                    <label className="form-label">Description</label>
                    <textarea className="form-control" name="description" rows="3" value={formData.description} onChange={handleInputChange}></textarea>
                  </div>
                </div>
                <div className="modal-footer bg-light">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                  <button type="submit" className="btn btn-primary">Save Job</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}

      {/* --- UPDATE STATUS MODAL --- */}
      {showStatusModal && (
        <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog">
            <div className="modal-content border-0 shadow">
              <div className="modal-header">
                <h5 className="modal-title fw-bold">
                  {isCustomerApproval ? 'Record Customer Decision' : 'Update Job Status'}
                </h5>
                <button type="button" className="btn-close" onClick={() => setShowStatusModal(false)}></button>
              </div>
              <form onSubmit={handleStatusSubmit}>
                <div className="modal-body">
                  <div className="mb-3">
                    <label className="form-label">New Status *</label>
                    {isCustomerApproval ? (
                      <select className="form-select" value={statusForm.status} onChange={(e) => setStatusForm({...statusForm, status: e.target.value})} required>
                        <option value="">Select Decision...</option>
                        <option value="APPROVED">Design Approved</option>
                        <option value="REJECTED">Design Rejected</option>
                      </select>
                    ) : (
                      <select className="form-select" value={statusForm.status} onChange={(e) => setStatusForm({...statusForm, status: e.target.value})} required>
                        {JOB_STATUSES.map(status => <option key={status} value={status}>{status.replace(/_/g, ' ')}</option>)}
                      </select>
                    )}
                  </div>
                  <div className="mb-3">
                    <label className="form-label">Remarks / Comments</label>
                    <textarea className="form-control" rows="3" placeholder="Add context to this status change..." value={statusForm.remarks} onChange={(e) => setStatusForm({...statusForm, remarks: e.target.value})}></textarea>
                  </div>
                </div>
                <div className="modal-footer bg-light">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowStatusModal(false)}>Cancel</button>
                  <button type="submit" className="btn btn-success">Confirm Update</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}

      {/* --- TIMELINE MODAL --- */}
      {showTimelineModal && (
        <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog modal-dialog-scrollable">
            <div className="modal-content border-0 shadow">
              <div className="modal-header">
                <h5 className="modal-title fw-bold">Job Timeline</h5>
                <button type="button" className="btn-close" onClick={() => setShowTimelineModal(false)}></button>
              </div>
              <div className="modal-body">
                {timeline.length === 0 ? (
                  <p className="text-muted text-center">No status changes recorded yet.</p>
                ) : (
                  <ul className="list-group list-group-flush">
                    {timeline.map((entry, index) => (
                      <li key={index} className="list-group-item px-0 py-3">
                        <div className="d-flex w-100 justify-content-between">
                          <h6 className="mb-1 fw-bold text-primary">{entry.newStatus.replace(/_/g, ' ')}</h6>
                          <small className="text-muted">{new Date(entry.changedAt).toLocaleString()}</small>
                        </div>
                        <p className="mb-1 small">Previous: <span className="text-muted">{entry.previousStatus.replace(/_/g, ' ')}</span></p>
                        {entry.remarks && <p className="mb-1 text-dark"><strong>Remarks:</strong> {entry.remarks}</p>}
                        <small className="text-muted">Updated by: {entry.changedBy}</small>
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default JobManagement;