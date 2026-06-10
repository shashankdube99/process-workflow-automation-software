package com.dube.workflow.machine;

import com.dube.workflow.job.Job;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "machine_allocations")
public class MachineAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", nullable = false)
    private Machine machine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(name = "allocated_at", nullable = false, updatable = false)
    private LocalDateTime allocatedAt;

    @Column(name = "released_at")
    private LocalDateTime releasedAt;

    @PrePersist
    protected void onCreate() {
        this.allocatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Machine getMachine() { return machine; }
    public void setMachine(Machine machine) { this.machine = machine; }
    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }
    public LocalDateTime getAllocatedAt() { return allocatedAt; }
    public LocalDateTime getReleasedAt() { return releasedAt; }
    public void setReleasedAt(LocalDateTime releasedAt) { this.releasedAt = releasedAt; }
}