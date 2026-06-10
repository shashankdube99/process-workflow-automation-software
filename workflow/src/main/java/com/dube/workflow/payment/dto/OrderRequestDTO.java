package com.dube.workflow.payment.dto;
import java.math.BigDecimal;
import java.util.UUID;

public class OrderRequestDTO {
    private UUID jobId;
    private BigDecimal amount;

    public UUID getJobId() { return jobId; }
    public void setJobId(UUID jobId) { this.jobId = jobId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}