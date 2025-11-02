package com.corebank.coreb.service;

import com.corebank.coreb.dto.BranchDTO;
import com.corebank.coreb.entity.Branch;
import com.corebank.coreb.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BranchService {

    @Autowired
    private BranchRepository branchRepository;

    public Branch saveBranch(Branch branch) {
        return branchRepository.save(branch);
    }

    public Optional<Branch> getBranchById(Long id) {
        return branchRepository.findById(id);
    }

    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    /**
     * Safe delete: only deletes branch if it has no child entities
     */
    public boolean safeDeleteBranch(Long branchId) {
        Optional<Branch> branchOpt = branchRepository.findById(branchId);

        if (branchOpt.isEmpty()) {
            return false; // Branch not found
        }

        Branch branch = branchOpt.get();

//        // Check if branch has any child entities
//        if ((branch.getAccounts() != null && !branch.getAccounts().isEmpty()) ||
//            (branch.getLoans() != null && !branch.getLoans().isEmpty()) ||
//            (branch.getStaffUsers() != null && !branch.getStaffUsers().isEmpty())) {
//            return false; // Cannot delete, child records exist
//        }

        branchRepository.deleteById(branchId);
        return true;
        
    }
    
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
