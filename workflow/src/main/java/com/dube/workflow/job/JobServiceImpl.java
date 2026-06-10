package com.dube.workflow.job;

import com.dube.workflow.customer.Customer;
import com.dube.workflow.customer.CustomerRepository;
import com.dube.workflow.exception.ResourceNotFoundException;
import com.dube.workflow.job.dto.JobRequestDTO;
import com.dube.workflow.job.dto.JobResponseDTO;
import com.dube.workflow.user.User;
import com.dube.workflow.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final JobFileRepository jobFileRepository;
    
    private final String UPLOAD_DIR = "uploads/designs/";

    public JobServiceImpl(JobRepository jobRepository, CustomerRepository customerRepository, 
                          UserRepository userRepository, JobFileRepository jobFileRepository) {
        this.jobRepository = jobRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.jobFileRepository = jobFileRepository;
        
        // Ensure upload directory exists
        new File(UPLOAD_DIR).mkdirs();
    }

    @Override
    @Transactional
    public JobResponseDTO createJob(JobRequestDTO request, String username) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
                
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Job job = new Job();
        job.setJobNumber(generateJobNumber());
        job.setCustomer(customer);
        job.setProductName(request.getProductName());
        job.setQuantity(request.getQuantity());
        job.setDescription(request.getDescription());
        job.setDueDate(request.getDueDate());
        job.setPriority(request.getPriority());
        job.setCreatedBy(user);

        Job savedJob = jobRepository.save(job);
        return mapToDTO(savedJob);
    }

    @Override
    @Transactional
    public JobResponseDTO updateJob(UUID id, JobRequestDTO request) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getCustomer().getId().equals(request.getCustomerId())) {
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
            job.setCustomer(customer);
        }

        job.setProductName(request.getProductName());
        job.setQuantity(request.getQuantity());
        job.setDescription(request.getDescription());
        job.setDueDate(request.getDueDate());
        job.setPriority(request.getPriority());

        return mapToDTO(jobRepository.save(job));
    }

    @Override
    @Transactional
    public void deleteJob(UUID id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        job.setDeleted(true); // Soft delete
        jobRepository.save(job);
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponseDTO getJobById(UUID id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        return mapToDTO(job);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponseDTO> getAllJobs(String search) {
        List<Job> jobs = (search != null && !search.isEmpty()) ? 
                         jobRepository.searchJobs(search) : 
                         jobRepository.findByIsDeletedFalse();
                         
        return jobs.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String uploadJobFile(UUID jobId, MultipartFile file, String fileType, String username) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
                
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = job.getJobNumber() + "-" + fileType.toLowerCase() + "-" + System.currentTimeMillis() + extension;
            
            Path path = Paths.get(UPLOAD_DIR + newFileName);
            Files.write(path, file.getBytes());

            JobFile jobFile = new JobFile();
            jobFile.setJob(job);
            jobFile.setFileName(originalFilename);
            jobFile.setFileUrl(path.toString());
            jobFile.setFileType(fileType);
            jobFile.setUploadedBy(user);
            jobFileRepository.save(jobFile);

            return "File uploaded successfully: " + newFileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    // --- UTILITIES ---

    private String generateJobNumber() {
        int currentYear = Year.now().getValue();
        String latestJobNumber = jobRepository.findLatestJobNumberForYear(currentYear).orElse(null);

        if (latestJobNumber == null) {
            return "JOB-" + currentYear + "-0001";
        }

        String[] parts = latestJobNumber.split("-");
        int sequence = Integer.parseInt(parts[2]);
        return String.format("JOB-%d-%04d", currentYear, sequence + 1);
    }

    private JobResponseDTO mapToDTO(Job j) {
        return new JobResponseDTO(
                j.getId(), 
                j.getJobNumber(), 
                j.getCustomer().getId(),             // 👈 Add Customer ID mapping
                j.getCustomer().getCompanyName(),
                j.getProductName(), 
                j.getQuantity(), 
                j.getDescription(),                  // 👈 Add Description mapping
                j.getDueDate(),
                j.getStatus(), 
                j.getPriority()
        );
    }
}