package com.dube.workflow.machine;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "machines")
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "machine_code", unique = true, length = 50)
    private String machineCode;

    @Column(name = "machine_name", nullable = false, length = 150)
    private String machineName;

    @Column(name = "machine_type", length = 100)
    private String machineType;

    @Column(nullable = false, length = 30)
    private String status = "AVAILABLE"; // AVAILABLE, BUSY, MAINTENANCE, INACTIVE

    @Column(length = 150)
    private String location;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.machineCode == null) {
            this.machineCode = "MAC-" + System.currentTimeMillis();
        }
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getMachineCode() { return machineCode; }
    public void setMachineCode(String machineCode) { this.machineCode = machineCode; }
    public String getMachineName() { return machineName; }
    public void setMachineName(String machineName) { this.machineName = machineName; }
    public String getMachineType() { return machineType; }
    public void setMachineType(String machineType) { this.machineType = machineType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}