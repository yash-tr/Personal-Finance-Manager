package com.example.financemanagement.service;

import com.example.financemanagement.dto.TransactionRequest;
import com.example.financemanagement.dto.TransactionResponse;
import com.example.financemanagement.entity.Category;
import com.example.financemanagement.entity.CategoryType;
import com.example.financemanagement.entity.Transaction;
import com.example.financemanagement.entity.User;
import com.example.financemanagement.exception.ForbiddenException;
import com.example.financemanagement.exception.ResourceNotFoundException;
import com.example.financemanagement.repository.CategoryRepository;
import com.example.financemanagement.repository.TransactionRepository;
import com.example.financemanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TransactionService transactionService;

    private User testUser;
    private Category testCategory;
    private Transaction testTransaction;
    private TransactionRequest transactionRequest;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "password", "Test User", "+1234567890");
        testUser.setId(1L);

        testCategory = new Category("Salary", CategoryType.INCOME, false, testUser);
        testCategory.setId(1L);

        testTransaction = new Transaction(
                new BigDecimal("1000.00"),
                LocalDate.now(),
                "Test transaction",
                CategoryType.INCOME,
                testUser,
                testCategory
        );
        testTransaction.setId(1L);

        transactionRequest = new TransactionRequest();
        transactionRequest.setAmount(new BigDecimal("1000.00"));
        transactionRequest.setDate(LocalDate.now());
        transactionRequest.setCategory("Salary");
        transactionRequest.setDescription("Test transaction");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(testUser));
    }

    @Test
    void createTransaction_Success() {
        // Arrange
        when(categoryRepository.findByNameAndUserId("Salary", 1L)).thenReturn(Optional.of(testCategory));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        TransactionResponse result = transactionService.createTransaction(transactionRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testTransaction.getId(), result.getId());
        assertEquals(testTransaction.getAmount(), result.getAmount());
        assertEquals(testTransaction.getDate(), result.getDate());
        assertEquals(testCategory.getName(), result.getCategory());
        assertEquals(testTransaction.getDescription(), result.getDescription());
        assertEquals(testTransaction.getType(), result.getType());

        verify(categoryRepository).findByNameAndUserId("Salary", 1L);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createTransaction_CategoryNotFound() {
        // Arrange
        when(categoryRepository.findByNameAndUserId("NonExistent", 1L)).thenReturn(Optional.empty());

        transactionRequest.setCategory("NonExistent");

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> transactionService.createTransaction(transactionRequest));

        assertEquals("Category not found: NonExistent", exception.getMessage());
        verify(categoryRepository).findByNameAndUserId("NonExistent", 1L);
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void getTransactions_Success() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findTransactionsByFilters(eq(1L), any(), any(), any())).thenReturn(transactions);

        // Act
        List<TransactionResponse> result = transactionService.getTransactions(null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTransaction.getId(), result.get(0).getId());

        verify(transactionRepository).findTransactionsByFilters(eq(1L), any(), any(), any());
    }

    @Test
    void getTransactions_WithFilters() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        String category = "Food";
        List<Transaction> transactions = Arrays.asList(testTransaction);
        
        when(categoryRepository.findByNameAndUserId("Food", 1L)).thenReturn(Optional.of(testCategory));
        when(transactionRepository.findTransactionsByFilters(1L, startDate, endDate, testCategory.getId()))
                .thenReturn(transactions);

        // Act
        List<TransactionResponse> result = transactionService.getTransactions(startDate, endDate, category);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(categoryRepository).findByNameAndUserId("Food", 1L);
        verify(transactionRepository).findTransactionsByFilters(1L, startDate, endDate, testCategory.getId());
    }

    @Test
    void getTransactionById_Success() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        // Act
        TransactionResponse result = transactionService.getTransactionById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testTransaction.getId(), result.getId());
        verify(transactionRepository).findById(1L);
    }

    @Test
    void getTransactionById_NotFound() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> transactionService.getTransactionById(1L));

        assertEquals("Transaction not found with id: 1", exception.getMessage());
        verify(transactionRepository).findById(1L);
    }

    @Test
    void getTransactionById_Forbidden() {
        // Arrange
        User otherUser = new User("other@example.com", "password", "Other User", "+1234567890");
        otherUser.setId(2L);
        testTransaction.setUser(otherUser);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        // Act & Assert
        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> transactionService.getTransactionById(1L));

        assertEquals("You are not authorized to view this transaction.", exception.getMessage());
        verify(transactionRepository).findById(1L);
    }

    @Test
    void updateTransaction_Success() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(categoryRepository.findByNameAndUserId("Food", 1L)).thenReturn(Optional.of(testCategory));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        TransactionRequest updateRequest = new TransactionRequest();
        updateRequest.setAmount(new BigDecimal("1500.00"));
        updateRequest.setDescription("Updated description");
        updateRequest.setCategory("Food");

        // Act
        TransactionResponse result = transactionService.updateTransaction(1L, updateRequest);

        // Assert
        assertNotNull(result);
        verify(transactionRepository).findById(1L);
        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void updateTransaction_NotFound() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        TransactionRequest updateRequest = new TransactionRequest();
        updateRequest.setAmount(new BigDecimal("1500.00"));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> transactionService.updateTransaction(1L, updateRequest));

        assertEquals("Transaction not found with id: 1", exception.getMessage());
        verify(transactionRepository).findById(1L);
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void updateTransaction_Forbidden() {
        // Arrange
        User otherUser = new User("other@example.com", "password", "Other User", "+1234567890");
        otherUser.setId(2L);
        testTransaction.setUser(otherUser);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        TransactionRequest updateRequest = new TransactionRequest();
        updateRequest.setAmount(new BigDecimal("1500.00"));

        // Act & Assert
        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> transactionService.updateTransaction(1L, updateRequest));

        assertEquals("You are not authorized to update this transaction.", exception.getMessage());
        verify(transactionRepository).findById(1L);
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void updateTransaction_CategoryNotFound() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(categoryRepository.findByNameAndUserId("NonExistent", 1L)).thenReturn(Optional.empty());

        TransactionRequest updateRequest = new TransactionRequest();
        updateRequest.setCategory("NonExistent");

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> transactionService.updateTransaction(1L, updateRequest));

        assertEquals("Category not found with name: NonExistent", exception.getMessage());
        verify(transactionRepository).findById(1L);
        verify(categoryRepository).findByNameAndUserId("NonExistent", 1L);
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void deleteTransaction_Success() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        // Act
        assertDoesNotThrow(() -> transactionService.deleteTransaction(1L));

        // Assert
        verify(transactionRepository).findById(1L);
        verify(transactionRepository).delete(testTransaction);
    }

    @Test
    void deleteTransaction_NotFound() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> transactionService.deleteTransaction(1L));

        assertEquals("Transaction not found with id: 1", exception.getMessage());
        verify(transactionRepository).findById(1L);
        verify(transactionRepository, never()).delete(any());
    }

    @Test
    void deleteTransaction_Forbidden() {
        // Arrange
        User otherUser = new User("other@example.com", "password", "Other User", "+1234567890");
        otherUser.setId(2L);
        testTransaction.setUser(otherUser);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        // Act & Assert
        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> transactionService.deleteTransaction(1L));

        assertEquals("You are not authorized to delete this transaction.", exception.getMessage());
        verify(transactionRepository).findById(1L);
        verify(transactionRepository, never()).delete(any());
    }
} 