	package com.corebank.coreb.repository;
	
	import com.corebank.coreb.entity.Card;
	
	import java.time.LocalDate;
	import java.util.List;
	
	import org.springframework.data.jpa.repository.JpaRepository;
	import org.springframework.data.jpa.repository.Modifying;
	import org.springframework.data.jpa.repository.Query;
	import org.springframework.data.repository.query.Param;
	import org.springframework.stereotype.Repository;
	import org.springframework.transaction.annotation.Transactional;
	
	@Repository
	public interface CardRepository extends JpaRepository<Card, Long> {
		
		 @Transactional
		    @Modifying
		    @Query("UPDATE Card c SET c.status = 'Inactive' WHERE c.account.accountId = :accountId")
		    void deactivateCardsByAccountId(@Param("accountId") Long accountId);
	
		    @Transactional
		    @Modifying
		    @Query("DELETE FROM Card c WHERE c.account.accountId = :accountId")
		    void deleteByAccountId(@Param("accountId") Long accountId);
		    
		    List<Card> findByCardTypeAndStatus(String cardType, String status);
		    List<Card> findByCardTypeAndStatusAndExpiryDateBefore(String cardType, String status, LocalDate expiryDate);
	
	
	}
