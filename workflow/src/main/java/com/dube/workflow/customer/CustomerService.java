package com.dube.workflow.customer;

import com.dube.workflow.customer.dto.CustomerRequestDTO;
import com.dube.workflow.customer.dto.CustomerResponseDTO;
import java.util.List;
import java.util.UUID;

public interface CustomerService {
    CustomerResponseDTO createCustomer(CustomerRequestDTO request);
    CustomerResponseDTO updateCustomer(UUID id, CustomerRequestDTO request);
    CustomerResponseDTO getCustomerById(UUID id);
    List<CustomerResponseDTO> getAllCustomers();
    List<CustomerResponseDTO> searchCustomers(String query);
}