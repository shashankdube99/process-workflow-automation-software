package com.dube.workflow.machine;

import com.dube.workflow.machine.dto.AllocationRequestDTO;
import com.dube.workflow.machine.dto.MachineRequestDTO;
import com.dube.workflow.machine.dto.MachineResponseDTO;

import java.util.List;
import java.util.UUID;

public interface MachineService {
    MachineResponseDTO createMachine(MachineRequestDTO request);
    MachineResponseDTO updateMachine(UUID id, MachineRequestDTO request);
    void deleteMachine(UUID id);
    MachineResponseDTO getMachineById(UUID id);
    List<MachineResponseDTO> getAllMachines();
    List<MachineResponseDTO> getAvailableMachines();
    void allocateMachine(UUID machineId, AllocationRequestDTO request);
    void releaseMachine(UUID machineId);
}