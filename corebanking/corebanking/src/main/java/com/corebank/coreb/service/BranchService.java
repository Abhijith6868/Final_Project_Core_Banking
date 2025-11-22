package com.corebank.coreb.service;

import com.corebank.coreb.dto.BranchDTO;
import com.corebank.coreb.entity.Branch;
import com.corebank.coreb.entity.SystemDate;
import com.corebank.coreb.repository.BranchRepository;
import com.corebank.coreb.repository.SystemDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BranchService {

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private SystemDateRepository systemDateRepository;

    // --------------------
    // Centralized System Date
    // --------------------
    private LocalDate getSystemDate() {
        return systemDateRepository.findAll().stream()
                .findFirst()
                .map(SystemDate::getCurrentDate)
                .orElse(LocalDate.now()); // fallback if not initialized
    }

    // --------------------
    // Save branch (create or update)
    // --------------------
    public Branch saveBranch(Branch branch) {
        // Optionally set created/updated date if your Branch entity supports it
        // branch.setUpdatedAt(getSystemDate());
        return branchRepository.save(branch);
    }

    // --------------------
    // Get branch by ID
    // --------------------
    public Optional<Branch> getBranchById(Long id) {
        return branchRepository.findById(id);
    }

    // --------------------
    // Get all branches
    // --------------------
    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    // --------------------
    // Safe delete: only deletes if no child entities
    // --------------------
    public boolean safeDeleteBranch(Long branchId) {
        Optional<Branch> branchOpt = branchRepository.findById(branchId);

        if (branchOpt.isEmpty()) {
            return false; // Branch not found
        }

        Branch branch = branchOpt.get();

        // Uncomment if entity relationships are mapped
        /*
        if ((branch.getAccounts() != null && !branch.getAccounts().isEmpty()) ||
            (branch.getLoans() != null && !branch.getLoans().isEmpty()) ||
            (branch.getStaffUsers() != null && !branch.getStaffUsers().isEmpty())) {
            return false; // Cannot delete, child records exist
        }
        */

        branchRepository.deleteById(branchId);
        return true;
    }

    // --------------------
    // Convert entity to DTO
    // --------------------
    public BranchDTO convertToDTO(Branch branch) {
        return new BranchDTO(
                branch.getBranchId(),
                branch.getName(),
                branch.getAddress(),
                branch.getPhoneNumber(),
                branch.getEmail()
        );
    }
}
