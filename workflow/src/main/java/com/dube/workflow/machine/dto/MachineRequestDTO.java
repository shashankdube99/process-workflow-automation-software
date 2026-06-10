package com.dube.workflow.machine.dto;
import jakarta.validation.constraints.NotBlank;

public class MachineRequestDTO {
    @NotBlank(message = "Machine name is required")
    private String machineName;
    private String machineType;
    private String location;
    private String status;

    public String getMachineName() { return machineName; }
    public void setMachineName(String machineName) { this.machineName = machineName; }
    public String getMachineType() { return machineType; }
    public void setMachineType(String machineType) { this.machineType = machineType; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}