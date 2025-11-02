//package com.corebank.coreb.repository;
//
//import com.corebank.coreb.entity.ChequeBook;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//@Repository
//public interface ChequeBookRepository extends JpaRepository<ChequeBook, Long> {
//	
//    @Transactional
//    @Modifying
//    @Query("UPDATE ChequeBook cb SET cb.status = 'Inactive' WHERE cb.account.accountId = :accountId")
//    void deactivateChequeBooksByAccountId(@Param("accountId") Long accountId);
//
//    @Transactional
//    @Modifying
//    @Query("DELETE FROM ChequeBook cb WHERE cb.account.accountId = :accountId")
//    void deleteByAccountId(@Param("accountId") Long accountId);
//
//}
