package com.corebank.coreb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.corebank.coreb.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Check if a customer has any active loans
    boolean existsByCustomerIdAndLoansStatus(Long customerId, String status);

    // Optional: find all active customers
    // List<Customer> findByStatus(String status);
}
