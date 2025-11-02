package com.corebank.coreb.controllers;

import com.corebank.coreb.dto.CustomerDTO;
import com.corebank.coreb.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * Create a new customer
     */
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO) {
        CustomerDTO saved = customerService.saveCustomer(customerDTO);
        return ResponseEntity.status(201).body(saved); // 201 Created
    }

    /**
     * Get customer by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get all customers
     */
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    /**
     * Update existing customer
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
        CustomerDTO updated = customerService.updateCustomer(id, customerDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deactivate a customer (soft delete)
     */
    @PostMapping("/deactivate/{id}")
    public ResponseEntity<String> deactivateCustomer(@PathVariable Long id) {
        boolean result = customerService.deactivateCustomer(id);
        return ResponseEntity.ok(result ? "Customer deactivated" : "Customer remains active");
    }

    /**
     * Safe delete a customer (hard delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        boolean deleted = customerService.safeDeleteCustomer(id);
        return deleted
                ? ResponseEntity.noContent().build() // 204 No Content
                : ResponseEntity.status(400).body("Cannot delete customer with active loans/accounts");
    }
}
