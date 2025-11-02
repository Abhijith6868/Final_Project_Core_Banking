package com.corebank.coreb.controllers;

import com.corebank.coreb.dto.BranchDTO;
import com.corebank.coreb.entity.Branch;
import com.corebank.coreb.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/branches")
public class BranchController {

    @Autowired
    private BranchService branchService;

 // ✅ Create Branch (returns 201 Created)
    @PostMapping
    public ResponseEntity<BranchDTO> createBranch(@RequestBody Branch branch) {
        Branch savedBranch = branchService.saveBranch(branch);
        BranchDTO dto = convertToDTO(savedBranch);
        return ResponseEntity
                .status(201) // <-- Return 201 instead of 200
                .body(dto);
    }

    // ✅ Get all branches
    @GetMapping
    public ResponseEntity<List<BranchDTO>> getAllBranches() {
        List<BranchDTO> branchDTOs = branchService.getAllBranches()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(branchDTOs);
    }

    // ✅ Get branch by ID
    @GetMapping("/{id}")
    public ResponseEntity<BranchDTO> getBranchById(@PathVariable Long id) {
        Optional<Branch> branchOpt = branchService.getBranchById(id);
        return branchOpt.map(branch -> ResponseEntity.ok(convertToDTO(branch)))
                        .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Update branch
    @PutMapping("/{id}")
    public ResponseEntity<BranchDTO> updateBranch(@PathVariable Long id, @RequestBody Branch branchDetails) {
        return branchService.getBranchById(id)
                .map(branch -> {
                    branch.setName(branchDetails.getName());
                    branch.setAddress(branchDetails.getAddress());
                    branch.setPhoneNumber(branchDetails.getPhoneNumber());
                    branch.setEmail(branchDetails.getEmail());
                    Branch updatedBranch = branchService.saveBranch(branch);
                    return ResponseEntity.ok(convertToDTO(updatedBranch));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Safe delete
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBranch(@PathVariable Long id) {
        boolean deleted = branchService.safeDeleteBranch(id);
        if (deleted) {
            return ResponseEntity.ok("Branch deleted successfully");
        } else {
            return ResponseEntity.badRequest().body("Cannot delete branch: Child records exist or branch not found");
        }
    }

    // 🔹 Convert Entity → DTO
    private BranchDTO convertToDTO(Branch branch) {
        return new BranchDTO(
                branch.getBranchId(),
                branch.getName(),
                branch.getAddress(),
                branch.getPhoneNumber(),
                branch.getEmail()
        );
    }
}
