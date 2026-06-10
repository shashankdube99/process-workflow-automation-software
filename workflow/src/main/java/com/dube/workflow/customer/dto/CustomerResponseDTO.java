package com.dube.workflow.customer.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerResponseDTO {
    private UUID id;
    private String customerCode;
    private String companyName;
    private String contactPerson;
    private String email;
    private String mobile;
    private String address;
    private String gstNumber;
    private String status;
    private LocalDateTime createdAt;

    public CustomerResponseDTO(UUID id, String customerCode, String companyName, String contactPerson, 
                               String email, String mobile, String address, String gstNumber, 
                               String status, LocalDateTime createdAt) {
        this.id = id;
        this.customerCode = customerCode;
        this.companyName = companyName;
        this.contactPerson = contactPerson;
        this.email = email;
        this.mobile = mobile;
        this.address = address;
        this.gstNumber = gstNumber;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters 
    public UUID getId() { return id; }
    public String getCustomerCode() { return customerCode; }
    public String getCompanyName() { return companyName; }
    public String getContactPerson() { return contactPerson; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }
    public String getAddress() { return address; }
    public String getGstNumber() { return gstNumber; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}