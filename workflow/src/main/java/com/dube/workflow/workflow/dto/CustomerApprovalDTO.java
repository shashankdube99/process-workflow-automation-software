package com.dube.workflow.workflow.dto;
import jakarta.validation.constraints.NotBlank;

public class CustomerApprovalDTO {
    @NotBlank(message = "Status is required (APPROVED or REJECTED)")
    private String status;
    private String comments;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}