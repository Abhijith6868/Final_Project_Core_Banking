//package com.corebank.coreb.service;
//
//import com.corebank.coreb.entity.ChequeBook;
//import com.corebank.coreb.repository.ChequeBookRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class ChequeBookService {
//
//    @Autowired
//    private ChequeBookRepository chequeBookRepository;
//
//    public ChequeBook save(ChequeBook chequeBook) {
//        return chequeBookRepository.save(chequeBook);
//    }
//
//    public Optional<ChequeBook> getById(Long id) {
//        return chequeBookRepository.findById(id);
//    }
//
//    public List<ChequeBook> getAll() {
//        return chequeBookRepository.findAll();
//    }
//
//    public boolean delete(Long id) {
//        Optional<ChequeBook> chequeBookOpt = chequeBookRepository.findById(id);
//
//        if (chequeBookOpt.isPresent()) {
//            ChequeBook chequeBook = chequeBookOpt.get();
//
//            // Only allow delete if status is "inactive" or "used"
//            if ("inactive".equalsIgnoreCase(chequeBook.getStatus()) ||
//                "used".equalsIgnoreCase(chequeBook.getStatus())) {
//
//                chequeBookRepository.deleteById(id);
//                return true; // successfully deleted
//            }
//        }
//
//        return false; // delete not allowed
//    }
//}
