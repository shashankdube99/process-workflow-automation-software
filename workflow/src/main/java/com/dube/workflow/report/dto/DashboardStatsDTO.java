package com.dube.workflow.report.dto;

import java.math.BigDecimal;

public class DashboardStatsDTO {
    private BigDecimal totalRevenue;
    private long activeJobs;
    private long pendingApprovals;
    private long totalCustomers;

    public DashboardStatsDTO(BigDecimal totalRevenue, long activeJobs, long pendingApprovals, long totalCustomers) {
        this.totalRevenue = totalRevenue;
        this.activeJobs = activeJobs;
        this.pendingApprovals = pendingApprovals;
        this.totalCustomers = totalCustomers;
    }

    // Getters
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public long getActiveJobs() { return activeJobs; }
    public long getPendingApprovals() { return pendingApprovals; }
    public long getTotalCustomers() { return totalCustomers; }
}