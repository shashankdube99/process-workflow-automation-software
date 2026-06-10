package com.dube.workflow.job.dto;

import java.time.LocalDate;
import java.util.UUID;

public class JobResponseDTO {
    private UUID jobId;
    private String jobNumber;
    private UUID customerId;       // 👈 Add this
    private String customerName;
    private String productName;
    private Integer quantity;
    private String description;    // 👈 Add this
    private LocalDate dueDate;
    private String status;
    private String priority;

    // 👈 Update the constructor to include the new fields
    public JobResponseDTO(UUID jobId, String jobNumber, UUID customerId, String customerName, 
                          String productName, Integer quantity, String description, LocalDate dueDate, 
                          String status, String priority) {
        this.jobId = jobId;
        this.jobNumber = jobNumber;
        this.customerId = customerId;
        this.customerName = customerName;
        this.productName = productName;
        this.quantity = quantity;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.priority = priority;
    }

    // Standard Getters
    public UUID getJobId() { return jobId; }
    public String getJobNumber() { return jobNumber; }
    public UUID getCustomerId() { return customerId; } // 👈 New Getter
    public String getCustomerName() { return customerName; }
    public String getProductName() { return productName; }
    public Integer getQuantity() { return quantity; }
    public String getDescription() { return description; } // 👈 New Getter
    public LocalDate getDueDate() { return dueDate; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }
}