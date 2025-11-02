package com.corebank.coreb.repository;

import com.corebank.coreb.entity.Loan;
import com.corebank.coreb.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // Check if a customer has any loan with a specific status
    boolean existsByCustomerAndStatus(Customer customer, String status);
}
