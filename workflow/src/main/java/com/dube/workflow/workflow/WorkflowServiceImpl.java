package com.dube.workflow.workflow;

import com.dube.workflow.exception.ResourceNotFoundException;
import com.dube.workflow.job.Job;
import com.dube.workflow.job.JobRepository;
import com.dube.workflow.user.User;
import com.dube.workflow.user.UserRepository;
import com.dube.workflow.workflow.dto.CustomerApprovalDTO;
import com.dube.workflow.workflow.dto.StatusUpdateRequestDTO;
import com.dube.workflow.workflow.dto.TimelineResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    private final JobRepository jobRepository;
    private final WorkflowHistoryRepository workflowHistoryRepository;
    private final CustomerApprovalRepository customerApprovalRepository;
    private final UserRepository userRepository;

    public WorkflowServiceImpl(JobRepository jobRepository, WorkflowHistoryRepository workflowHistoryRepository,
                               CustomerApprovalRepository customerApprovalRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.workflowHistoryRepository = workflowHistoryRepository;
        this.customerApprovalRepository = customerApprovalRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void updateJobStatus(UUID jobId, StatusUpdateRequestDTO request, String username) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        User user = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String oldStatus = job.getStatus();
        job.setStatus(request.getStatus().toUpperCase());
        jobRepository.save(job);

        // Record the movement
        WorkflowHistory history = new WorkflowHistory();
        history.setJob(job);
        history.setPreviousStatus(oldStatus);
        history.setNewStatus(request.getStatus().toUpperCase());
        history.setRemarks(request.getRemarks());
        history.setChangedBy(user);
        workflowHistoryRepository.save(history);
    }

    @Override
    @Transactional
    public void handleCustomerApproval(UUID jobId, CustomerApprovalDTO request, String username) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        User user = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Save the formal approval/rejection document
        CustomerApproval approval = new CustomerApproval();
        approval.setJob(job);
        approval.setCustomer(job.getCustomer());
        approval.setStatus(request.getStatus().toUpperCase());
        approval.setComments(request.getComments());
        customerApprovalRepository.save(approval);

        // Map it to a workflow status change automatically
        String newStatus = request.getStatus().equalsIgnoreCase("APPROVED") ? "DESIGN_APPROVED" : "DESIGN_REJECTED";
        
        StatusUpdateRequestDTO statusUpdate = new StatusUpdateRequestDTO();
        statusUpdate.setStatus(newStatus);
        statusUpdate.setRemarks("Customer action: " + request.getComments());
        
        updateJobStatus(jobId, statusUpdate, username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimelineResponseDTO> getJobTimeline(UUID jobId) {
        return workflowHistoryRepository.findByJobIdOrderByChangedAtDesc(jobId).stream()
                .map(h -> new TimelineResponseDTO(
                        h.getPreviousStatus(),
                        h.getNewStatus(),
                        h.getRemarks(),
                        h.getChangedBy().getFirstName() + " " + h.getChangedBy().getLastName(),
                        h.getChangedAt()
                )).collect(Collectors.toList());
    }
}