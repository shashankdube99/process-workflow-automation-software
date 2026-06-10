package com.dube.workflow.workflow.dto;
import java.time.LocalDateTime;

public class TimelineResponseDTO {
    private String previousStatus;
    private String newStatus;
    private String remarks;
    private String changedBy;
    private LocalDateTime changedAt;

    public TimelineResponseDTO(String previousStatus, String newStatus, String remarks, String changedBy, LocalDateTime changedAt) {
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.remarks = remarks;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }

    // Getters
    public String getPreviousStatus() { return previousStatus; }
    public String getNewStatus() { return newStatus; }
    public String getRemarks() { return remarks; }
    public String getChangedBy() { return changedBy; }
    public LocalDateTime getChangedAt() { return changedAt; }
}