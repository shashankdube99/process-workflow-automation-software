package com.dube.workflow.workflow;

import com.dube.workflow.workflow.dto.CustomerApprovalDTO;
import com.dube.workflow.workflow.dto.StatusUpdateRequestDTO;
import com.dube.workflow.workflow.dto.TimelineResponseDTO;

import java.util.List;
import java.util.UUID;

public interface WorkflowService {
    void updateJobStatus(UUID jobId, StatusUpdateRequestDTO request, String username);
    void handleCustomerApproval(UUID jobId, CustomerApprovalDTO request, String username);
    List<TimelineResponseDTO> getJobTimeline(UUID jobId);
}