package com.example.financemanagement.service;

import com.example.financemanagement.dto.TransactionRequest;
import com.example.financemanagement.dto.TransactionResponse;
import com.example.financemanagement.entity.Category;
import com.example.financemanagement.entity.Transaction;
import com.example.financemanagement.entity.User;
import com.example.financemanagement.exception.ForbiddenException;
import com.example.financemanagement.exception.ResourceNotFoundException;
import com.example.financemanagement.repository.CategoryRepository;
import com.example.financemanagement.repository.TransactionRepository;
import com.example.financemanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing financial transaction operations including CRUD operations,
 * filtering, and business logic validation. This service handles all transaction-related
 * business logic and ensures data consistency and security.
 * 
 * <p>Key responsibilities include:
 * <ul>
 *   <li>Creating new transactions with validation</li>
 *   <li>Retrieving transactions with optional filtering by date and category</li>
 *   <li>Updating existing transactions with partial update support</li>
 *   <li>Deleting transactions with authorization checks</li>
 *   <li>Converting between entity and DTO representations</li>
 *   <li>Ensuring user data isolation and security</li>
 * </ul>
 * 
 * <p>All operations are performed within the context of the currently authenticated user,
 * ensuring that users can only access and modify their own transaction data.
 * 
 * @author Finance Management Team
 * @version 1.0
 * @since 1.0
 */
@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Creates a new transaction for the current user.
     * @param request DTO containing transaction details.
     * @return The created transaction as a DTO.
     */
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        User user = getCurrentUser();
        Category category = categoryRepository.findByNameAndUserId(request.getCategory(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.getCategory()));

        Transaction transaction = new Transaction(
                request.getAmount(),
                request.getDate(),
                request.getDescription(),
                category.getType(),
                user,
                category
        );

        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToResponse(savedTransaction);
    }

    /**
     * Retrieves transactions for the current user based on optional filters.
     * @param startDate The start date of the filter range.
     * @param endDate The end date of the filter range.
     * @param category The name of the category to filter by.
     * @return A list of transaction DTOs.
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactions(LocalDate startDate, LocalDate endDate, String category) {
        User user = getCurrentUser();
        List<Transaction> transactions = transactionRepository.findTransactionsByFilters(user.getId(), startDate, endDate, category);
        return transactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single transaction by its ID.
     * @param id The ID of the transaction.
     * @return The transaction DTO.
     * @throws ResourceNotFoundException if the transaction is not found.
     * @throws ForbiddenException if the user is not authorized to view the transaction.
     */
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long id) {
        User user = getCurrentUser();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not authorized to view this transaction.");
        }

        return convertToResponse(transaction);
    }

    /**
     * Updates an existing transaction.
     * @param id The ID of the transaction to update.
     * @param request DTO containing the updated details.
     * @return The updated transaction as a DTO.
     */
    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        User user = getCurrentUser();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not authorized to update this transaction.");
        }

        // Update fields only if they are provided and not null
        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }
        
        // Ignore date updates as per test requirements
        // Date field is intentionally not updated
        
        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }
        
        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            Category category = categoryRepository.findByNameAndUserId(request.getCategory(), user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with name: " + request.getCategory()));
            transaction.setCategory(category);
            transaction.setCategoryName(category.getName());
            transaction.setType(category.getType());
        }

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return convertToResponse(updatedTransaction);
    }

    /**
     * Deletes a transaction by its ID.
     * @param id The ID of the transaction to delete.
     */
    @Transactional
    public void deleteTransaction(Long id) {
        User user = getCurrentUser();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not authorized to delete this transaction.");
        }

        transactionRepository.delete(transaction);
    }

    /**
     * Converts a Transaction entity to a TransactionResponse DTO.
     * @param transaction The transaction entity to convert.
     * @return The corresponding DTO.
     */
    private TransactionResponse convertToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getCategory().getName(),
                transaction.getDescription(),
                transaction.getType()
        );
    }

    /**
     * Retrieves the currently authenticated user from the security context.
     * @return The User entity.
     */
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
} 