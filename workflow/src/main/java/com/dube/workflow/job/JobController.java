package com.dube.workflow.job;

import com.dube.workflow.job.dto.JobRequestDTO;
import com.dube.workflow.job.dto.JobResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // 🚀 CREATE JOB
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DESIGNER')")
    public ResponseEntity<JobResponseDTO> createJob(@Valid @RequestBody JobRequestDTO request, Authentication authentication) {
        JobResponseDTO job = jobService.createJob(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(job);
    }

    // 🚀 GET ALL JOBS
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DESIGNER', 'RECEPTIONIST', 'MACHINE_OPERATOR', 'FINISHING_TEAM', 'QUALITY_TEAM')")
    public ResponseEntity<List<JobResponseDTO>> getAllJobs(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(jobService.getAllJobs(search));
    }

    // 🚀 GET JOB BY ID
    @GetMapping("/{id}")
    public ResponseEntity<JobResponseDTO> getJobById(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    // 🚀 UPDATE JOB
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DESIGNER')")
    public ResponseEntity<JobResponseDTO> updateJob(@PathVariable UUID id, @Valid @RequestBody JobRequestDTO request) {
        return ResponseEntity.ok(jobService.updateJob(id, request));
    }

    // 🚀 DELETE JOB (Soft)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteJob(@PathVariable UUID id) {
        jobService.deleteJob(id);
        return ResponseEntity.ok(Map.of("message", "Job deleted successfully"));
    }

    // 🚀 UPLOAD FILE
    @PostMapping("/{id}/files")
    @PreAuthorize("hasAnyRole('ADMIN', 'DESIGNER', 'MACHINE_OPERATOR', 'QUALITY_TEAM')")
    public ResponseEntity<Map<String, String>> uploadFile(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileType") String fileType,
            Authentication authentication) {
            
        String result = jobService.uploadJobFile(id, file, fileType, authentication.getName());
        return ResponseEntity.ok(Map.of("message", result));
    }
}