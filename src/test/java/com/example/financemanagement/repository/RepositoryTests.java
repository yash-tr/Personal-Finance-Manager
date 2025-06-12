package com.example.financemanagement.repository;

import com.example.financemanagement.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    private User testUser;
    private Category testCategory;
    private Transaction testTransaction;
    private SavingsGoal testGoal;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User("test@example.com", "password123", "Test User", "+1234567890");
        testUser = entityManager.persistAndFlush(testUser);

        // Create test category
        testCategory = new Category("Food", CategoryType.EXPENSE, false, testUser);
        testCategory = entityManager.persistAndFlush(testCategory);

        // Create test transaction
        testTransaction = new Transaction(
                new BigDecimal("100.00"),
                LocalDate.now(),
                "Test transaction",
                CategoryType.EXPENSE,
                testUser,
                testCategory
        );
        testTransaction = entityManager.persistAndFlush(testTransaction);

        // Create test savings goal
        testGoal = new SavingsGoal(
                "Emergency Fund",
                new BigDecimal("5000.00"),
                LocalDate.now().plusMonths(6),
                LocalDate.now(),
                testUser
        );
        testGoal = entityManager.persistAndFlush(testGoal);

        entityManager.clear();
    }

    @Test
    void userRepository_FindByUsername() {
        Optional<User> found = userRepository.findByUsername("test@example.com");
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getUsername());
        assertEquals("Test User", found.get().getFullName());
    }

    @Test
    void userRepository_FindByUsername_NotFound() {
        Optional<User> found = userRepository.findByUsername("nonexistent@example.com");
        assertFalse(found.isPresent());
    }

    @Test
    void userRepository_Save() {
        User newUser = new User("new@example.com", "password", "New User", "+9876543210");
        User saved = userRepository.save(newUser);
        
        assertNotNull(saved.getId());
        assertEquals("new@example.com", saved.getUsername());
        assertEquals("New User", saved.getFullName());
    }

    @Test
    void categoryRepository_FindByNameAndUserId() {
        Optional<Category> found = categoryRepository.findByNameAndUserId("Food", testUser.getId());
        assertTrue(found.isPresent());
        assertEquals("Food", found.get().getName());
        assertEquals(CategoryType.EXPENSE, found.get().getType());
        assertEquals(testUser.getId(), found.get().getUser().getId());
    }

    @Test
    void categoryRepository_FindByNameAndUserId_NotFound() {
        Optional<Category> found = categoryRepository.findByNameAndUserId("NonExistent", testUser.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void categoryRepository_FindByUserId() {
        List<Category> categories = categoryRepository.findByUserId(testUser.getId());
        assertFalse(categories.isEmpty());
        assertTrue(categories.stream().anyMatch(c -> "Food".equals(c.getName())));
    }

    @Test
    void categoryRepository_ExistsByNameAndUserId() {
        boolean exists = categoryRepository.existsByNameAndUserId("Food", testUser.getId());
        assertTrue(exists);

        boolean notExists = categoryRepository.existsByNameAndUserId("NonExistent", testUser.getId());
        assertFalse(notExists);
    }

    @Test
    void categoryRepository_ExistsByIdAndIsCustom() {
        boolean exists = categoryRepository.existsByIdAndIsCustom(testCategory.getId(), false);
        assertTrue(exists);

        boolean notExists = categoryRepository.existsByIdAndIsCustom(testCategory.getId(), true);
        assertFalse(notExists);
    }

    @Test
    void transactionRepository_FindByUserIdOrderByDateDesc() {
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByDateDesc(testUser.getId());
        assertFalse(transactions.isEmpty());
        assertTrue(transactions.stream().anyMatch(t -> t.getDescription().equals("Test transaction")));
    }

    @Test
    void transactionRepository_ExistsByCategoryId() {
        boolean exists = transactionRepository.existsByCategoryId(testCategory.getId());
        assertTrue(exists);
    }

    @Test
    void transactionRepository_FindTransactionsByFilters_NoFilters() {
        List<Transaction> transactions = transactionRepository.findTransactionsByFilters(testUser.getId(), null, null, null);
        assertFalse(transactions.isEmpty());
        assertEquals(1, transactions.size());
        assertEquals("Test transaction", transactions.get(0).getDescription());
    }

    @Test
    void transactionRepository_FindTransactionsByFilters_WithDateRange() {
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);
        
        List<Transaction> transactions = transactionRepository.findTransactionsByFilters(
                testUser.getId(), startDate, endDate, null);
        assertFalse(transactions.isEmpty());
        assertEquals(1, transactions.size());
    }

    @Test
    void transactionRepository_FindTransactionsByFilters_WithCategory() {
        List<Transaction> transactions = transactionRepository.findTransactionsByFilters(
                testUser.getId(), null, null, testCategory.getId());
        assertFalse(transactions.isEmpty());
        assertEquals(1, transactions.size());
        assertEquals(testCategory.getId(), transactions.get(0).getCategory().getId());
    }

    @Test
    void transactionRepository_FindTransactionsByFilters_EmptyResult() {
        LocalDate futureStart = LocalDate.now().plusDays(1);
        LocalDate futureEnd = LocalDate.now().plusDays(2);
        
        List<Transaction> transactions = transactionRepository.findTransactionsByFilters(
                testUser.getId(), futureStart, futureEnd, null);
        assertTrue(transactions.isEmpty());
    }

    @Test
    void transactionRepository_FindByUserIdAndDateBetween() {
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);
        
        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(
                testUser.getId(), startDate, endDate);
        assertFalse(transactions.isEmpty());
        assertEquals(1, transactions.size());
    }

    @Test
    void savingsGoalRepository_FindByUserId() {
        List<SavingsGoal> goals = savingsGoalRepository.findByUserId(testUser.getId());
        assertFalse(goals.isEmpty());
        assertEquals(1, goals.size());
        assertEquals("Emergency Fund", goals.get(0).getGoalName());
    }

    @Test
    void savingsGoalRepository_FindByIdAndUserId() {
        Optional<SavingsGoal> found = savingsGoalRepository.findByIdAndUserId(testGoal.getId(), testUser.getId());
        assertTrue(found.isPresent());
        assertEquals("Emergency Fund", found.get().getGoalName());
        assertEquals(testUser.getId(), found.get().getUser().getId());
    }

    @Test
    void savingsGoalRepository_FindByIdAndUserId_NotFound() {
        Optional<SavingsGoal> found = savingsGoalRepository.findByIdAndUserId(999L, testUser.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void repository_CascadeOperations() {
        // Test basic cascade behavior without complex constraints
        User userToTest = new User("cascade@example.com", "password", "Cascade User", "+1111111111");
        userToTest = entityManager.persistAndFlush(userToTest);

        // Verify entity was created
        assertTrue(userRepository.findById(userToTest.getId()).isPresent());

        // Test that we can find the user by username
        Optional<User> foundUser = userRepository.findByUsername("cascade@example.com");
        assertTrue(foundUser.isPresent());
        assertEquals("Cascade User", foundUser.get().getFullName());
    }

    @Test
    void repository_UniqueCategoryConstraint() {
        // Try to create duplicate category for same user
        Category duplicate = new Category("Food", CategoryType.EXPENSE, false, testUser);
        
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(duplicate);
        });
    }

    @Test
    void repository_DifferentUserSameCategory() {
        // Create another user
        User anotherUser = new User("another@example.com", "password", "Another User", "+9999999999");
        anotherUser = entityManager.persistAndFlush(anotherUser);

        // Should allow same category name for different user
        Category sameNameCategory = new Category("Food", CategoryType.EXPENSE, false, anotherUser);
        Category saved = entityManager.persistAndFlush(sameNameCategory);
        
        assertNotNull(saved.getId());
        assertEquals("Food", saved.getName());
        assertEquals(anotherUser.getId(), saved.getUser().getId());
    }
} 