package com.dube.workflow.machine;

import com.dube.workflow.exception.BadRequestException;
import com.dube.workflow.exception.ResourceNotFoundException;
import com.dube.workflow.job.Job;
import com.dube.workflow.job.JobRepository;
import com.dube.workflow.machine.dto.AllocationRequestDTO;
import com.dube.workflow.machine.dto.MachineRequestDTO;
import com.dube.workflow.machine.dto.MachineResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MachineServiceImpl implements MachineService {

    private final MachineRepository machineRepository;
    private final MachineAllocationRepository allocationRepository;
    private final JobRepository jobRepository;

    public MachineServiceImpl(MachineRepository machineRepository, MachineAllocationRepository allocationRepository, JobRepository jobRepository) {
        this.machineRepository = machineRepository;
        this.allocationRepository = allocationRepository;
        this.jobRepository = jobRepository;
    }

    @Override
    @Transactional
    public MachineResponseDTO createMachine(MachineRequestDTO request) {
        Machine machine = new Machine();
        machine.setMachineName(request.getMachineName());
        machine.setMachineType(request.getMachineType());
        machine.setLocation(request.getLocation());
        machine.setStatus(request.getStatus() != null ? request.getStatus().toUpperCase() : "AVAILABLE");
        return mapToDTO(machineRepository.save(machine));
    }

    @Override
    @Transactional
    public MachineResponseDTO updateMachine(UUID id, MachineRequestDTO request) {
        Machine machine = machineRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Machine not found"));
        machine.setMachineName(request.getMachineName());
        machine.setMachineType(request.getMachineType());
        machine.setLocation(request.getLocation());
        if(request.getStatus() != null) {
            machine.setStatus(request.getStatus().toUpperCase());
        }
        return mapToDTO(machineRepository.save(machine));
    }

    @Override
    @Transactional
    public void deleteMachine(UUID id) {
        Machine machine = machineRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Machine not found"));
        machineRepository.delete(machine);
    }

    @Override
    @Transactional(readOnly = true)
    public MachineResponseDTO getMachineById(UUID id) {
        return mapToDTO(machineRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Machine not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MachineResponseDTO> getAllMachines() {
        return machineRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MachineResponseDTO> getAvailableMachines() {
        return machineRepository.findByStatus("AVAILABLE").stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void allocateMachine(UUID machineId, AllocationRequestDTO request) {
        Machine machine = machineRepository.findById(machineId).orElseThrow(() -> new ResourceNotFoundException("Machine not found"));
        
        if (!machine.getStatus().equals("AVAILABLE")) {
            throw new BadRequestException("Machine is not currently available for allocation.");
        }

        Job job = jobRepository.findById(request.getJobId()).orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        MachineAllocation allocation = new MachineAllocation();
        allocation.setMachine(machine);
        allocation.setJob(job);
        allocationRepository.save(allocation);

        machine.setStatus("BUSY");
        machineRepository.save(machine);
    }

    @Override
    @Transactional
    public void releaseMachine(UUID machineId) {
        Machine machine = machineRepository.findById(machineId).orElseThrow(() -> new ResourceNotFoundException("Machine not found"));
        
        MachineAllocation allocation = allocationRepository.findByMachineIdAndReleasedAtIsNull(machineId)
                .orElseThrow(() -> new BadRequestException("No active allocation found for this machine."));

        allocation.setReleasedAt(LocalDateTime.now());
        allocationRepository.save(allocation);

        machine.setStatus("AVAILABLE");
        machineRepository.save(machine);
    }

    private MachineResponseDTO mapToDTO(Machine m) {
        return new MachineResponseDTO(m.getId(), m.getMachineCode(), m.getMachineName(), m.getMachineType(), m.getStatus(), m.getLocation());
    }
}