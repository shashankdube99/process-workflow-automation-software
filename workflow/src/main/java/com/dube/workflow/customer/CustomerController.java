package com.dube.workflow.customer;

import com.dube.workflow.customer.dto.CustomerRequestDTO;
import com.dube.workflow.customer.dto.CustomerResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // 🚀 POST /api/customers - Accessible by ADMIN and DESIGNER
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DESIGNER')")
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDTO request) {
        CustomerResponseDTO createdCustomer = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    // 🚀 GET /api/customers - Accessible by ADMIN, DESIGNER, and RECEPTIONIST
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DESIGNER', 'RECEPTIONIST')")
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers(@RequestParam(required = false) String search) {
        List<CustomerResponseDTO> customers = customerService.searchCustomers(search);
        return ResponseEntity.ok(customers);
    }

    // 🚀 GET /api/customers/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DESIGNER', 'RECEPTIONIST')")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable UUID id) {
        CustomerResponseDTO customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    // 🚀 PUT /api/customers/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DESIGNER')")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerRequestDTO request) {
        CustomerResponseDTO updatedCustomer = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(updatedCustomer);
    }
}