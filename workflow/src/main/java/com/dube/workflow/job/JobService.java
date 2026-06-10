package com.dube.workflow.job;

import com.dube.workflow.job.dto.JobRequestDTO;
import com.dube.workflow.job.dto.JobResponseDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

public interface JobService {
    JobResponseDTO createJob(JobRequestDTO request, String username);
    JobResponseDTO updateJob(UUID id, JobRequestDTO request);
    void deleteJob(UUID id);
    JobResponseDTO getJobById(UUID id);
    List<JobResponseDTO> getAllJobs(String search);
    
    // File operations
    String uploadJobFile(UUID jobId, MultipartFile file, String fileType, String username);
}