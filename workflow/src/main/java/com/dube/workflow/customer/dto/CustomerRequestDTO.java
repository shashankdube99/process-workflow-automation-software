package com.dube.workflow.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CustomerRequestDTO {

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Contact person name is required")
    private String contactPerson;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^$|[0-9]{10}", message = "Mobile number must be exactly 10 digits")
    private String mobile;

    private String address;
    private String gstNumber;

    // Getters and Setters
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }
}