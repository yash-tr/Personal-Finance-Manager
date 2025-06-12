package com.example.financemanagement.controller;

import com.example.financemanagement.dto.TransactionRequest;
import com.example.financemanagement.dto.TransactionResponse;
import com.example.financemanagement.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling transaction-related API requests.
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /**
     * Creates a new transaction.
     * @param request The transaction data from the request body.
     * @return The created transaction.
     */
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionRequest request) {
        TransactionResponse createdTransaction = transactionService.createTransaction(request);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    /**
     * Retrieves a list of transactions, optionally filtered by date and category.
     * @param startDate The start date for the filter.
     * @param endDate The end date for the filter.
     * @param category The name of the category to filter by.
     * @return A list of transactions.
     */
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String category) {
        List<TransactionResponse> transactions = transactionService.getTransactions(startDate, endDate, category);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Retrieves a single transaction by its ID.
     * @param id The ID of the transaction to retrieve.
     * @return The transaction.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {
        TransactionResponse transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }

    /**
     * Updates an existing transaction.
     * @param id The ID of the transaction to update.
     * @param request The updated transaction data.
     * @return The updated transaction.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionRequest request) {
        TransactionResponse updatedTransaction = transactionService.updateTransaction(id, request);
        return ResponseEntity.ok(updatedTransaction);
    }

    /**
     * Deletes a transaction by its ID.
     * @param id The ID of the transaction to delete.
     * @return A success message.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok(Map.of("message", "Transaction deleted successfully"));
    }
} 