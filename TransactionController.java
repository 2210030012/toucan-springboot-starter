package com.example.transactionstarter.transaction;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Validated
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // 1. Create a transaction
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction createTransaction(
            @Valid @RequestBody Transaction transaction) {

        return transactionService.createTransaction(transaction);
    }

    // 2. Get a transaction by ID
    @GetMapping("/{transactionId}")
    public Transaction getTransaction(
            @PathVariable @NotBlank String transactionId) {

        return transactionService.getTransaction(transactionId);
    }

    // 3. Update transaction status
    @PatchMapping("/{transactionId}/status")
    public Transaction updateTransactionStatus(
            @PathVariable @NotBlank String transactionId,
            @RequestBody TransactionStatus newStatus) {

        return transactionService.updateTransactionStatus(
                transactionId,
                newStatus
        );
    }

    // 4. Get all transactions for a customer
    @GetMapping("/customer/{customerId}")
    public List<Transaction> getCustomerTransactions(
            @PathVariable @NotBlank String customerId) {

        return transactionService.getCustomerTransactions(customerId);
    }
}