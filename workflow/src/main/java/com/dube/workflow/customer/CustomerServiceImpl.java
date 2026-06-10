package com.dube.workflow.customer;

import com.dube.workflow.customer.dto.CustomerRequestDTO;
import com.dube.workflow.customer.dto.CustomerResponseDTO;
import com.dube.workflow.exception.BadRequestException;
import com.dube.workflow.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO request) {
        if (request.getEmail() != null && customerRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("A customer with this email already exists.");
        }
        if (request.getMobile() != null && customerRepository.existsByMobile(request.getMobile())) {
            throw new BadRequestException("A customer with this mobile number already exists.");
        }

        Customer customer = new Customer();
        customer.setCompanyName(request.getCompanyName());
        customer.setContactPerson(request.getContactPerson());
        customer.setEmail(request.getEmail());
        customer.setMobile(request.getMobile());
        customer.setAddress(request.getAddress());
        customer.setGstNumber(request.getGstNumber());
        customer.setStatus("ACTIVE");

        Customer savedCustomer = customerRepository.save(customer);
        return mapToDTO(savedCustomer);
    }

    @Override
    @Transactional
    public CustomerResponseDTO updateCustomer(UUID id, CustomerRequestDTO request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        // Prevent updating to an email/mobile that belongs to a different customer
        if (request.getEmail() != null && !request.getEmail().equals(customer.getEmail()) && customerRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already in use by another customer.");
        }
        if (request.getMobile() != null && !request.getMobile().equals(customer.getMobile()) && customerRepository.existsByMobile(request.getMobile())) {
            throw new BadRequestException("Mobile is already in use by another customer.");
        }

        customer.setCompanyName(request.getCompanyName());
        customer.setContactPerson(request.getContactPerson());
        customer.setEmail(request.getEmail());
        customer.setMobile(request.getMobile());
        customer.setAddress(request.getAddress());
        customer.setGstNumber(request.getGstNumber());

        Customer updatedCustomer = customerRepository.save(customer);
        return mapToDTO(updatedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
        return mapToDTO(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> searchCustomers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllCustomers();
        }
        return customerRepository.searchCustomers(query).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Utility mapper
    private CustomerResponseDTO mapToDTO(Customer c) {
        return new CustomerResponseDTO(
                c.getId(), c.getCustomerCode(), c.getCompanyName(), c.getContactPerson(),
                c.getEmail(), c.getMobile(), c.getAddress(), c.getGstNumber(),
                c.getStatus(), c.getCreatedAt()
        );
    }
}