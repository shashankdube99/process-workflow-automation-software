package com.dube.workflow.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserResponseDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String role; // Flattened name string from the Role object (e.g., "ROLE_ADMIN")
    private String status; // Matching your "ACTIVE" / "INACTIVE" status string
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    public UserResponseDTO(UUID id, String firstName, String lastName, String email, 
                           String mobile, String role, String status, 
                           LocalDateTime createdAt, LocalDateTime lastLogin) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobile = mobile;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }

    // Getters
    public UUID getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastLogin() { return lastLogin; }
}