package com.dube.workflow.machine.dto;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class AllocationRequestDTO {
    @NotNull(message = "Job ID is required")
    private UUID jobId;

    public UUID getJobId() { return jobId; }
    public void setJobId(UUID jobId) { this.jobId = jobId; }
}