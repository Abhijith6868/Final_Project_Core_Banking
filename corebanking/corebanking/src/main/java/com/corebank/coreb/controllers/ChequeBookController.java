//package com.corebank.coreb.controllers;
//
//import com.corebank.coreb.entity.ChequeBook;
//import com.corebank.coreb.service.ChequeBookService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/cheque-books")
//public class ChequeBookController {
//
//    @Autowired
//    private ChequeBookService chequeBookService;
//
//    // Create a new cheque book
//    @PostMapping
//    public ChequeBook createChequeBook(@RequestBody ChequeBook chequeBook) {
//        return chequeBookService.save(chequeBook);
//    }
//
//    // Get all cheque books
//    @GetMapping
//    public List<ChequeBook> getAllChequeBooks() {
//        return chequeBookService.getAll();
//    }
//
//    // Get cheque book by ID
//    @GetMapping("/{id}")
//    public ResponseEntity<ChequeBook> getChequeBookById(@PathVariable Long id) {
//        return chequeBookService.getById(id)
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    // Update cheque book
//    @PutMapping("/{id}")
//    public ResponseEntity<ChequeBook> updateChequeBook(
//            @PathVariable Long id,
//            @RequestBody ChequeBook chequeBookDetails) {
//        return chequeBookService.getById(id)
//                .map(chequeBook -> {
//                    chequeBook.setAccount(chequeBookDetails.getAccount());
//                    chequeBook.setIssuedDate(chequeBookDetails.getIssuedDate());
//                    chequeBook.setNumberOfLeaves(chequeBookDetails.getNumberOfLeaves());
//                    chequeBook.setStatus(chequeBookDetails.getStatus());
//                    ChequeBook updated = chequeBookService.save(chequeBook);
//                    return ResponseEntity.ok(updated);
//                })
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    // Delete cheque book
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteChequeBook(@PathVariable Long id) {
//        return chequeBookService.getById(id)
//                .map(chequeBook -> {
//                    if ("active".equalsIgnoreCase(chequeBook.getStatus())) {
//                        return ResponseEntity.badRequest()
//                                .body("Active cheque books cannot be deleted. Mark it inactive/used first.");
//                    }
//
//                    chequeBook.setStatus("deleted"); // soft delete
//                    chequeBookService.save(chequeBook);
//
//                    return ResponseEntity.ok("Cheque book deleted successfully (soft delete).");
//                })
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//}
