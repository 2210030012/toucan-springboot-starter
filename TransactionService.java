package com.example.transactionstarter.transaction;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // 1. Create a new transaction
    public Transaction createTransaction(Transaction transaction) {

        // Basic validation
        if (transaction.getTransactionId() == null ||
                transaction.getTransactionId().isBlank()) {
            throw new IllegalArgumentException("Transaction ID is required");
        }

        if (transaction.getCustomerId() == null ||
                transaction.getCustomerId().isBlank()) {
            throw new IllegalArgumentException("Customer ID is required");
        }

        if (transaction.getAmount() == null ||
                transaction.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (transaction.getCurrency() == null ||
                transaction.getCurrency().isBlank()) {
            throw new IllegalArgumentException("Currency is required");
        }

        if (transaction.getTransactionType() == null) {
            throw new IllegalArgumentException("Transaction type is required");
        }

        if (transaction.getStatus() == null) {
            throw new IllegalArgumentException("Transaction status is required");
        }

        // A new transaction must start in PENDING state
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalArgumentException(
                    "New transaction must have PENDING status"
            );
        }

        // Transaction ID must be unique
        if (transactionRepository.existsById(transaction.getTransactionId())) {
            throw new IllegalArgumentException(
                    "Transaction with ID " + transaction.getTransactionId()
                            + " already exists"
            );
        }

        // Store currency consistently in uppercase
        transaction.setCurrency(transaction.getCurrency().trim().toUpperCase());

        return transactionRepository.save(transaction);
    }

    // 2. Get a transaction by ID
    public Transaction getTransaction(String transactionId) {

        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction ID is required");
        }

        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Transaction with ID " + transactionId + " not found"
                ));
    }

    // 3. Update transaction status
    public Transaction updateTransactionStatus(
            String transactionId,
            TransactionStatus newStatus) {

        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction ID is required");
        }

        if (newStatus == null) {
            throw new IllegalArgumentException("New status is required");
        }

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Transaction with ID " + transactionId + " not found"
                ));

        TransactionStatus currentStatus = transaction.getStatus();

        // Only PENDING transactions can change status
        if (currentStatus != TransactionStatus.PENDING) {
            throw new IllegalStateException(
                    "Transaction status cannot be changed from "
                            + currentStatus
            );
        }

        // PENDING can move only to a final status
        if (newStatus == TransactionStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Transaction is already in PENDING status"
            );
        }

        transaction.setStatus(newStatus);

        return transactionRepository.save(transaction);
    }

    // 4. Get all transactions for a customer
    public List<Transaction> getCustomerTransactions(String customerId) {

        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Customer ID is required");
        }

        return transactionRepository.findByCustomerId(customerId);
    }
}