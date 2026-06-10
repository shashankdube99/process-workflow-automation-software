package com.dube.workflow.workflow;

import com.dube.workflow.workflow.dto.CustomerApprovalDTO;
import com.dube.workflow.workflow.dto.StatusUpdateRequestDTO;
import com.dube.workflow.workflow.dto.TimelineResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs/{id}")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    // 🚀 PATCH /api/jobs/{id}/status
    @PatchMapping("/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DESIGNER', 'MACHINE_OPERATOR', 'FINISHING_TEAM', 'QUALITY_TEAM')")
    public ResponseEntity<Map<String, String>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StatusUpdateRequestDTO request,
            Authentication authentication) {
        
        workflowService.updateJobStatus(id, request, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Job status updated to " + request.getStatus()));
    }

    // 🚀 POST /api/jobs/{id}/customer-approval
    @PostMapping("/customer-approval")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')") // In Phase 1, Admins might log it on behalf of clients
    public ResponseEntity<Map<String, String>> submitApproval(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerApprovalDTO request,
            Authentication authentication) {
        
        workflowService.handleCustomerApproval(id, request, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Customer decision recorded successfully."));
    }

    // 🚀 GET /api/jobs/{id}/timeline
    @GetMapping("/timeline")
    public ResponseEntity<List<TimelineResponseDTO>> getJobTimeline(@PathVariable UUID id) {
        return ResponseEntity.ok(workflowService.getJobTimeline(id));
    }
}