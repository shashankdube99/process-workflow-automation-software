package com.dube.workflow.machine;

import com.dube.workflow.machine.dto.AllocationRequestDTO;
import com.dube.workflow.machine.dto.MachineRequestDTO;
import com.dube.workflow.machine.dto.MachineResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/machines")
public class MachineController {

    private final MachineService machineService;

    public MachineController(MachineService machineService) {
        this.machineService = machineService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MachineResponseDTO> createMachine(@Valid @RequestBody MachineRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(machineService.createMachine(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MACHINE_OPERATOR', 'RECEPTIONIST')")
    public ResponseEntity<List<MachineResponseDTO>> getAllMachines() {
        return ResponseEntity.ok(machineService.getAllMachines());
    }

    @GetMapping("/availability")
    @PreAuthorize("hasAnyRole('ADMIN', 'MACHINE_OPERATOR')")
    public ResponseEntity<List<MachineResponseDTO>> getAvailableMachines() {
        return ResponseEntity.ok(machineService.getAvailableMachines());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MachineResponseDTO> updateMachine(@PathVariable UUID id, @Valid @RequestBody MachineRequestDTO request) {
        return ResponseEntity.ok(machineService.updateMachine(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteMachine(@PathVariable UUID id) {
        machineService.deleteMachine(id);
        return ResponseEntity.ok(Map.of("message", "Machine deleted successfully"));
    }

    @PostMapping("/{id}/allocate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MACHINE_OPERATOR')")
    public ResponseEntity<Map<String, String>> allocateMachine(@PathVariable UUID id, @Valid @RequestBody AllocationRequestDTO request) {
        machineService.allocateMachine(id, request);
        return ResponseEntity.ok(Map.of("message", "Machine allocated successfully"));
    }

    @PostMapping("/{id}/release")
    @PreAuthorize("hasAnyRole('ADMIN', 'MACHINE_OPERATOR')")
    public ResponseEntity<Map<String, String>> releaseMachine(@PathVariable UUID id) {
        machineService.releaseMachine(id);
        return ResponseEntity.ok(Map.of("message", "Machine released successfully"));
    }
}