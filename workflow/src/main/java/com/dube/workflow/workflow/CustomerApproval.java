package com.dube.workflow.workflow;

import com.dube.workflow.customer.Customer;
import com.dube.workflow.job.Job;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customer_approvals")
public class CustomerApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false, length = 20)
    private String status; // APPROVED or REJECTED

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(name = "approved_at", updatable = false)
    private LocalDateTime approvedAt;

    @PrePersist
    protected void onCreate() {
        this.approvedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
}