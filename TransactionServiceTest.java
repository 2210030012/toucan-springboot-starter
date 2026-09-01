package com.example.transactionstarter.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(transactionRepository);
    }

    // Test 1: Create transaction successfully
    @Test
    void shouldCreateTransactionSuccessfully() {

        Transaction transaction = new Transaction(
                "TXN001",
                "CUST001",
                new BigDecimal("1500.00"),
                "INR",
                TransactionType.PAYMENT,
                TransactionStatus.PENDING
        );

        when(transactionRepository.existsById("TXN001"))
                .thenReturn(false);

        when(transactionRepository.save(transaction))
                .thenReturn(transaction);

        Transaction result = transactionService.createTransaction(transaction);

        assertNotNull(result);
        assertEquals("TXN001", result.getTransactionId());
        assertEquals("CUST001", result.getCustomerId());
        assertEquals(new BigDecimal("1500.00"), result.getAmount());
        assertEquals("INR", result.getCurrency());
        assertEquals(TransactionType.PAYMENT, result.getTransactionType());
        assertEquals(TransactionStatus.PENDING, result.getStatus());

        verify(transactionRepository).existsById("TXN001");
        verify(transactionRepository).save(transaction);
    }

    // Test 2: Get transaction successfully
    @Test
    void shouldGetTransactionSuccessfully() {

        Transaction transaction = new Transaction(
                "TXN002",
                "CUST002",
                new BigDecimal("2500.00"),
                "USD",
                TransactionType.TRANSFER,
                TransactionStatus.PENDING
        );

        when(transactionRepository.findById("TXN002"))
                .thenReturn(Optional.of(transaction));

        Transaction result = transactionService.getTransaction("TXN002");

        assertNotNull(result);
        assertEquals("TXN002", result.getTransactionId());
        assertEquals("CUST002", result.getCustomerId());

        verify(transactionRepository).findById("TXN002");
    }

    // Test 3: Update transaction status successfully
    @Test
    void shouldUpdateTransactionStatusSuccessfully() {

        Transaction transaction = new Transaction(
                "TXN003",
                "CUST003",
                new BigDecimal("1000.00"),
                "INR",
                TransactionType.PAYMENT,
                TransactionStatus.PENDING
        );

        when(transactionRepository.findById("TXN003"))
                .thenReturn(Optional.of(transaction));

        when(transactionRepository.save(transaction))
                .thenReturn(transaction);

        Transaction result = transactionService.updateTransactionStatus(
                "TXN003",
                TransactionStatus.COMPLETED
        );

        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());

        verify(transactionRepository).findById("TXN003");
        verify(transactionRepository).save(transaction);
    }

    // Test 4: Get all transactions for a customer
    @Test
    void shouldGetAllTransactionsForCustomer() {

        Transaction transaction1 = new Transaction(
                "TXN004",
                "CUST004",
                new BigDecimal("500.00"),
                "INR",
                TransactionType.PAYMENT,
                TransactionStatus.PENDING
        );

        Transaction transaction2 = new Transaction(
                "TXN005",
                "CUST004",
                new BigDecimal("750.00"),
                "INR",
                TransactionType.REFUND,
                TransactionStatus.COMPLETED
        );

        when(transactionRepository.findByCustomerId("CUST004"))
                .thenReturn(List.of(transaction1, transaction2));

        List<Transaction> result =
                transactionService.getCustomerTransactions("CUST004");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("TXN004", result.get(0).getTransactionId());
        assertEquals("TXN005", result.get(1).getTransactionId());

        verify(transactionRepository).findByCustomerId("CUST004");
    }

    // Test 5: Duplicate transaction ID should be rejected
    @Test
    void shouldRejectDuplicateTransactionId() {

        Transaction transaction = new Transaction(
                "TXN006",
                "CUST006",
                new BigDecimal("1000.00"),
                "INR",
                TransactionType.PAYMENT,
                TransactionStatus.PENDING
        );

        when(transactionRepository.existsById("TXN006"))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.createTransaction(transaction)
        );

        assertEquals(
                "Transaction with ID TXN006 already exists",
                exception.getMessage()
        );

        verify(transactionRepository).existsById("TXN006");
        verify(transactionRepository, never()).save(any());
    }

    // Test 6: Invalid amount should be rejected
    @Test
    void shouldRejectInvalidAmount() {

        Transaction transaction = new Transaction(
                "TXN007",
                "CUST007",
                new BigDecimal("-100.00"),
                "INR",
                TransactionType.PAYMENT,
                TransactionStatus.PENDING
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.createTransaction(transaction)
        );

        assertEquals(
                "Amount must be greater than zero",
                exception.getMessage()
        );

        verify(transactionRepository, never()).existsById(anyString());
        verify(transactionRepository, never()).save(any());
    }

    // Test 7: Completed transaction cannot change status
    @Test
    void shouldRejectStatusUpdateFromCompletedTransaction() {

        Transaction transaction = new Transaction(
                "TXN008",
                "CUST008",
                new BigDecimal("2000.00"),
                "INR",
                TransactionType.PAYMENT,
                TransactionStatus.COMPLETED
        );

        when(transactionRepository.findById("TXN008"))
                .thenReturn(Optional.of(transaction));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> transactionService.updateTransactionStatus(
                        "TXN008",
                        TransactionStatus.FAILED
                )
        );

        assertEquals(
                "Transaction status cannot be changed from COMPLETED",
                exception.getMessage()
        );

        verify(transactionRepository).findById("TXN008");
        verify(transactionRepository, never()).save(any());
    }
}

// Test 8: Non-existent transaction should be rejected
@Test
void shouldRejectNonExistentTransaction() {

    when(transactionRepository.findById("TXN999"))
            .thenReturn(Optional.empty());

    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> transactionService.getTransaction("TXN999")
    );

    assertEquals(
            "Transaction with ID TXN999 not found",
            exception.getMessage()
    );

    verify(transactionRepository).findById("TXN999");
}
