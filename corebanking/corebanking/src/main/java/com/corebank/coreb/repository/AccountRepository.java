package com.corebank.coreb.repository;

import com.corebank.coreb.entity.Account;
import com.corebank.coreb.entity.Customer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Check if the customer has any account with a given status
    boolean existsByCustomerAndStatus(Customer customer, String status);

    // Optional: find all accounts for a customer
    List<Account> findByCustomer(Customer customer);
}
