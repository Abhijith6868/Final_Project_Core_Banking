package com.corebank.coreb.repository;

import com.corebank.coreb.entity.Deposit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Deposit d SET d.status = 'Inactive' WHERE d.account.accountId = :accountId")
    void deactivateDepositsByAccountId(@Param("accountId") Long accountId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Deposit d WHERE d.account.accountId = :accountId")
    void deleteByAccountId(@Param("accountId") Long accountId);
}


