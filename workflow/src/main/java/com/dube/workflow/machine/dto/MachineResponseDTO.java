package com.dube.workflow.machine.dto;
import java.util.UUID;

public class MachineResponseDTO {
    private UUID id;
    private String machineCode;
    private String machineName;
    private String machineType;
    private String status;
    private String location;

    public MachineResponseDTO(UUID id, String machineCode, String machineName, String machineType, String status, String location) {
        this.id = id;
        this.machineCode = machineCode;
        this.machineName = machineName;
        this.machineType = machineType;
        this.status = status;
        this.location = location;
    }

    // Getters
    public UUID getId() { return id; }
    public String getMachineCode() { return machineCode; }
    public String getMachineName() { return machineName; }
    public String getMachineType() { return machineType; }
    public String getStatus() { return status; }
    public String getLocation() { return location; }
}