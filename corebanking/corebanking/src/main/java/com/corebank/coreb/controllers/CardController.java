package com.corebank.coreb.controllers;

import com.corebank.coreb.entity.Card;
import com.corebank.coreb.scheduler.CreditCardJob;
import com.corebank.coreb.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    // Create card
    @PostMapping
    public ResponseEntity<Card> createCard(@RequestBody Card card) {
        return ResponseEntity.ok(cardService.saveCard(card));
    }

    // Get card by ID
    @GetMapping("/{id}")
    public ResponseEntity<Card> getCardById(@PathVariable Long id) {
        return cardService.getCardById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all cards
    @GetMapping
    public List<Card> getAllCards() {
        return cardService.getAllCards();
    }

    // Update card
    @PutMapping("/{id}")
    public ResponseEntity<Card> updateCard(@PathVariable Long id, @RequestBody Card cardDetails) {
        return cardService.getCardById(id)
                .map(card -> {
                    card.setAccount(cardDetails.getAccount());
                    card.setCardNumber(cardDetails.getCardNumber());
                    card.setCardType(cardDetails.getCardType());
                    card.setExpiryDate(cardDetails.getExpiryDate());
                    card.setStatus(cardDetails.getStatus());
                    return ResponseEntity.ok(cardService.saveCard(card));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Deactivate card (soft delete)
    @PatchMapping("/deactivate/{id}")
    public ResponseEntity<String> deactivateCard(@PathVariable Long id) {
        boolean success = cardService.deactivateCard(id);
        if (success) {
            return ResponseEntity.ok("Card deactivated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Safe delete: delete only if not linked with account and inactive
    @DeleteMapping("/{id}")
    public ResponseEntity<String> safeDeleteCard(@PathVariable Long id) {
        boolean deleted = cardService.safeDeleteCard(id);
        if (deleted) {
            return ResponseEntity.ok("Card deleted successfully.");
        } else {
            return ResponseEntity.badRequest()
                    .body("Cannot delete card: either not found, still active, or linked to an account.");
        }
    }

    // Manual trigger: deactivate expired debit cards
    @PostMapping("/deactivate-expired")
    public ResponseEntity<String> deactivateExpiredCardsManually() {
        cardService.deactivateExpiredCards();
        return ResponseEntity.ok("Expired cards checked and deactivated successfully.");
    }

    @Autowired
    private CreditCardJob creditCardJob; // <-- inject the job

    // -------------------------------
    // Manual trigger: Apply tiered interest
    // -------------------------------
    @PostMapping("/process-credit-cards")
    public ResponseEntity<String> processCreditCardsManually() {
        try {
            creditCardJob.processCreditCards();  // handles interest + deactivation
            return ResponseEntity.ok("Credit cards processed successfully (interest applied and expired cards deactivated).");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing credit cards: " + e.getMessage());
        }
    }

}
