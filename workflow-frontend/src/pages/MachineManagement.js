import React, { useState, useEffect } from 'react';
import machineService from '../services/machineService';
import jobService from '../services/jobService';

function MachineManagement() {
  const [machines, setMachines] = useState([]);
  const [activeJobs, setActiveJobs] = useState([]);
  const [loading, setLoading] = useState(true);

  // Machine Form State
  const [showModal, setShowModal] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [currentMachineId, setCurrentMachineId] = useState(null);
  const [formData, setFormData] = useState({
    machineName: '', machineType: '', location: '', status: 'AVAILABLE'
  });

  // Allocation State
  const [showAllocateModal, setShowAllocateModal] = useState(false);
  const [selectedJobId, setSelectedJobId] = useState('');

  useEffect(() => {
    fetchMachines();
    fetchActiveJobs();
  }, []);

  const fetchMachines = async () => {
    try {
      setLoading(true);
      const data = await machineService.getMachines();
      setMachines(data);
    } catch (err) {
      console.error('Failed to fetch machines', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchActiveJobs = async () => {
    try {
      // Fetch jobs to populate the allocation dropdown
      const data = await jobService.getJobs();
      // Filter out completed or cancelled jobs
      const ongoing = data.filter(j => j.status !== 'COMPLETED' && j.status !== 'CANCELLED');
      setActiveJobs(ongoing);
    } catch (err) {
      console.error('Failed to fetch jobs', err);
    }
  };

  // --- CRUD Handlers ---
  const handleInputChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

  const openModal = (machine = null) => {
    if (machine) {
      setIsEditing(true);
      setCurrentMachineId(machine.id);
      setFormData({
        machineName: machine.machineName, machineType: machine.machineType || '',
        location: machine.location || '', status: machine.status
      });
    } else {
      setIsEditing(false);
      setCurrentMachineId(null);
      setFormData({ machineName: '', machineType: '', location: '', status: 'AVAILABLE' });
    }
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (isEditing) {
        await machineService.updateMachine(currentMachineId, formData);
      } else {
        await machineService.createMachine(formData);
      }
      setShowModal(false);
      fetchMachines();
    } catch (err) {
      alert(err.response?.data?.message || 'Error saving machine.');
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this machine?')) {
      try {
        await machineService.deleteMachine(id);
        fetchMachines();
      } catch (err) {
        alert('Error deleting machine.');
      }
    }
  };

  // --- Allocation Handlers ---
  const openAllocateModal = (machineId) => {
    setCurrentMachineId(machineId);
    setSelectedJobId('');
    setShowAllocateModal(true);
  };

  const handleAllocateSubmit = async (e) => {
    e.preventDefault();
    try {
      await machineService.allocateMachine(currentMachineId, selectedJobId);
      setShowAllocateModal(false);
      fetchMachines();
    } catch (err) {
      alert(err.response?.data?.message || 'Error allocating machine.');
    }
  };

  const handleRelease = async (machineId) => {
    if (window.confirm('Release this machine from its current job?')) {
      try {
        await machineService.releaseMachine(machineId);
        fetchMachines();
      } catch (err) {
        alert(err.response?.data?.message || 'Error releasing machine.');
      }
    }
  };

  return (
    <div className="container-fluid p-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2 className="fw-bold text-dark">Machine Fleet</h2>
        <button className="btn btn-primary" onClick={() => openModal()}>+ Add Machine</button>
      </div>

      {loading ? (
        <div className="text-center p-5"><div className="spinner-border text-primary"></div></div>
      ) : (
        <div className="row g-4">
          {machines.map((machine) => (
            <div className="col-md-4 col-lg-3" key={machine.id}>
              <div className="card shadow-sm border-0 h-100">
                <div className={`card-header text-white fw-bold py-3 ${
                  machine.status === 'AVAILABLE' ? 'bg-success' : 
                  machine.status === 'BUSY' ? 'bg-danger' : 'bg-warning text-dark'
                }`}>
                  {machine.status}
                </div>
                <div className="card-body">
                  <h5 className="card-title fw-bold">{machine.machineName}</h5>
                  <p className="card-text text-muted mb-1 small">
                    <strong>Code:</strong> {machine.machineCode}
                  </p>
                  <p className="card-text text-muted mb-1 small">
                    <strong>Type:</strong> {machine.machineType || 'N/A'}
                  </p>
                  <p className="card-text text-muted mb-3 small">
                    <strong>Location:</strong> {machine.location || 'Main Floor'}
                  </p>
                  
                  <div className="d-flex gap-2 mt-auto">
                    {machine.status === 'AVAILABLE' && (
                      <button className="btn btn-sm btn-outline-success w-100" onClick={() => openAllocateModal(machine.id)}>
                        Allocate Job
                      </button>
                    )}
                    {machine.status === 'BUSY' && (
                      <button className="btn btn-sm btn-outline-danger w-100" onClick={() => handleRelease(machine.id)}>
                        Release
                      </button>
                    )}
                  </div>
                </div>
                <div className="card-footer bg-white border-0 d-flex justify-content-between pb-3">
                  <button className="btn btn-link btn-sm text-secondary p-0" onClick={() => openModal(machine)}>Edit</button>
                  <button className="btn btn-link btn-sm text-danger p-0" onClick={() => handleDelete(machine.id)}>Delete</button>
                </div>
              </div>
            </div>
          ))}
          {machines.length === 0 && <div className="col-12 text-center text-muted mt-5">No machines registered.</div>}
        </div>
      )}

      {/* --- ADD/EDIT MACHINE MODAL --- */}
      {showModal && (
        <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog">
            <div className="modal-content border-0 shadow">
              <div className="modal-header">
                <h5 className="modal-title fw-bold">{isEditing ? 'Edit Machine' : 'Register Machine'}</h5>
                <button type="button" className="btn-close" onClick={() => setShowModal(false)}></button>
              </div>
              <form onSubmit={handleSubmit}>
                <div className="modal-body row g-3">
                  <div className="col-12">
                    <label className="form-label">Machine Name *</label>
                    <input type="text" className="form-control" name="machineName" value={formData.machineName} onChange={handleInputChange} required />
                  </div>
                  <div className="col-12">
                    <label className="form-label">Machine Type</label>
                    <input type="text" className="form-control" name="machineType" placeholder="e.g., Industrial Printer, CNC Router" value={formData.machineType} onChange={handleInputChange} />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label">Location</label>
                    <input type="text" className="form-control" name="location" value={formData.location} onChange={handleInputChange} />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label">Status</label>
                    <select className="form-select" name="status" value={formData.status} onChange={handleInputChange}>
                      <option value="AVAILABLE">Available</option>
                      <option value="MAINTENANCE">Maintenance</option>
                      <option value="INACTIVE">Inactive</option>
                    </select>
                  </div>
                </div>
                <div className="modal-footer bg-light mt-3">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                  <button type="submit" className="btn btn-primary">Save Machine</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}

      {/* --- ALLOCATE JOB MODAL --- */}
      {showAllocateModal && (
        <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog">
            <div className="modal-content border-0 shadow">
              <div className="modal-header">
                <h5 className="modal-title fw-bold">Allocate Job to Machine</h5>
                <button type="button" className="btn-close" onClick={() => setShowAllocateModal(false)}></button>
              </div>
              <form onSubmit={handleAllocateSubmit}>
                <div className="modal-body">
                  <label className="form-label">Select Active Job *</label>
                  <select className="form-select" value={selectedJobId} onChange={(e) => setSelectedJobId(e.target.value)} required>
                    <option value="">-- Choose a Job --</option>
                    {activeJobs.map(job => (
                      <option key={job.jobId} value={job.jobId}>
                        {job.jobNumber} - {job.productName} ({job.customerName})
                      </option>
                    ))}
                  </select>
                </div>
                <div className="modal-footer bg-light mt-3">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowAllocateModal(false)}>Cancel</button>
                  <button type="submit" className="btn btn-success">Allocate</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default MachineManagement;