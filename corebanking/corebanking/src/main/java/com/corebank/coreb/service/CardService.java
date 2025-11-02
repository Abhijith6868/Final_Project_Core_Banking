package com.corebank.coreb.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.corebank.coreb.entity.Card;
import com.corebank.coreb.repository.CardRepository;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private JobService jobService; // For logging card interest jobs

    // Save or update card
    public Card saveCard(Card card) {
        return cardRepository.save(card);
    }

    // Get card by ID
    public Optional<Card> getCardById(Long id) {
        return cardRepository.findById(id);
    }

    // Get all cards
    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    // Safe delete: only inactive & not linked cards
    public boolean safeDeleteCard(Long cardId) {
        Optional<Card> cardOpt = cardRepository.findById(cardId);
        if (cardOpt.isPresent()) {
            Card card = cardOpt.get();
            if (card.getAccount() != null) return false;
            if (!"inactive".equalsIgnoreCase(card.getStatus())) return false;
            cardRepository.deleteById(cardId);
            return true;
        }
        return false;
    }

    // Deactivate (soft delete)
    public boolean deactivateCard(Long cardId) {
        Optional<Card> cardOpt = cardRepository.findById(cardId);
        if (cardOpt.isPresent()) {
            Card card = cardOpt.get();
            card.setStatus("inactive");
            cardRepository.save(card);
            return true;
        }
        return false;
    }

//    // Auto deactivate expired debit cards
    public int deactivateExpiredCards() {
        List<Card> cards = cardRepository.findAll();
        LocalDate today = LocalDate.now();
        int expiredCards = 0;

        for (Card card : cards) {
            if ("Debit".equalsIgnoreCase(card.getCardType())
                    && "active".equalsIgnoreCase(card.getStatus())
                    && card.getExpiryDate() != null
                    && card.getExpiryDate().isBefore(today)) {

                card.setStatus("inactive");
                cardRepository.save(card);
                expiredCards++;
            }
        }

        return expiredCards;
    }

    // The service **does not calculate tiered interest anymore**.
    // Job will handle the calculation.
    
//    @Autowired
//    private CreditCardJob creditCardJob;
//
//    public void runCreditCardInterestJob() {
//        creditCardJob.processCreditCards(); // calls the job logic
//    }

}
