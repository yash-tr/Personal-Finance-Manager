package com.example.financemanagement.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EntityTests {

    @Test
    void user_NoArgsConstructor() {
        User user = new User();
        assertNotNull(user);
        assertNotNull(user.getTransactions());
        assertNotNull(user.getCategories());
        assertNotNull(user.getSavingsGoals());
    }

    @Test
    void user_AllArgsConstructor() {
        String username = "test@example.com";
        String password = "password123";
        String fullName = "Test User";
        String phoneNumber = "+1234567890";

        User user = new User(username, password, fullName, phoneNumber);

        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(fullName, user.getFullName());
        assertEquals(phoneNumber, user.getPhoneNumber());
        assertNotNull(user.getTransactions());
        assertNotNull(user.getCategories());
        assertNotNull(user.getSavingsGoals());
    }

    @Test
    void user_SettersAndGetters() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test@example.com");
        user.setPassword("password123");
        user.setFullName("Test User");
        user.setPhoneNumber("+1234567890");
        user.setTransactions(new ArrayList<>());
        user.setCategories(new ArrayList<>());
        user.setSavingsGoals(new ArrayList<>());

        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("Test User", user.getFullName());
        assertEquals("+1234567890", user.getPhoneNumber());
        assertNotNull(user.getTransactions());
        assertNotNull(user.getCategories());
        assertNotNull(user.getSavingsGoals());
    }

    @Test
    void category_NoArgsConstructor() {
        Category category = new Category();
        assertNotNull(category);
    }

    @Test
    void category_AllArgsConstructor() {
        User user = new User("test@example.com", "password", "Test User", "+1234567890");
        String name = "Food";
        CategoryType type = CategoryType.EXPENSE;
        boolean isCustom = true;

        Category category = new Category(name, type, isCustom, user);

        assertEquals(name, category.getName());
        assertEquals(type, category.getType());
        assertEquals(isCustom, category.isCustom());
        assertEquals(user, category.getUser());
    }

    @Test
    void category_SettersAndGetters() {
        Category category = new Category();
        User user = new User("test@example.com", "password", "Test User", "+1234567890");
        
        category.setId(1L);
        category.setName("Food");
        category.setType(CategoryType.EXPENSE);
        category.setCustom(true);
        category.setUser(user);

        assertEquals(1L, category.getId());
        assertEquals("Food", category.getName());
        assertEquals(CategoryType.EXPENSE, category.getType());
        assertTrue(category.isCustom());
        assertEquals(user, category.getUser());
    }

    @Test
    void transaction_NoArgsConstructor() {
        Transaction transaction = new Transaction();
        assertNotNull(transaction);
    }

    @Test
    void transaction_AllArgsConstructor() {
        User user = new User("test@example.com", "password", "Test User", "+1234567890");
        Category category = new Category("Food", CategoryType.EXPENSE, false, user);
        BigDecimal amount = new BigDecimal("100.00");
        LocalDate date = LocalDate.now();
        String description = "Test transaction";
        CategoryType type = CategoryType.EXPENSE;

        Transaction transaction = new Transaction(amount, date, description, type, user, category);

        assertEquals(amount, transaction.getAmount());
        assertEquals(date, transaction.getDate());
        assertEquals(description, transaction.getDescription());
        assertEquals(type, transaction.getType());
        assertEquals(user, transaction.getUser());
        assertEquals(category, transaction.getCategory());
    }

    @Test
    void transaction_SettersAndGetters() {
        Transaction transaction = new Transaction();
        User user = new User("test@example.com", "password", "Test User", "+1234567890");
        Category category = new Category("Food", CategoryType.EXPENSE, false, user);
        
        transaction.setId(1L);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setDate(LocalDate.now());
        transaction.setDescription("Test transaction");
        transaction.setType(CategoryType.EXPENSE);
        transaction.setCategoryName("Food");
        transaction.setUser(user);
        transaction.setCategory(category);

        assertEquals(1L, transaction.getId());
        assertEquals(new BigDecimal("100.00"), transaction.getAmount());
        assertEquals(LocalDate.now(), transaction.getDate());
        assertEquals("Test transaction", transaction.getDescription());
        assertEquals(CategoryType.EXPENSE, transaction.getType());
        assertEquals("Food", transaction.getCategoryName());
        assertEquals(user, transaction.getUser());
        assertEquals(category, transaction.getCategory());
    }

    @Test
    void savingsGoal_NoArgsConstructor() {
        SavingsGoal goal = new SavingsGoal();
        assertNotNull(goal);
    }

    @Test
    void savingsGoal_AllArgsConstructor() {
        User user = new User("test@example.com", "password", "Test User", "+1234567890");
        String goalName = "Emergency Fund";
        BigDecimal targetAmount = new BigDecimal("5000.00");
        LocalDate targetDate = LocalDate.now().plusMonths(6);
        LocalDate startDate = LocalDate.now();

        SavingsGoal goal = new SavingsGoal(goalName, targetAmount, targetDate, startDate, user);

        assertEquals(goalName, goal.getGoalName());
        assertEquals(targetAmount, goal.getTargetAmount());
        assertEquals(targetDate, goal.getTargetDate());
        assertEquals(startDate, goal.getStartDate());
        assertEquals(user, goal.getUser());
    }

    @Test
    void savingsGoal_SettersAndGetters() {
        SavingsGoal goal = new SavingsGoal();
        User user = new User("test@example.com", "password", "Test User", "+1234567890");
        
        goal.setId(1L);
        goal.setGoalName("Emergency Fund");
        goal.setTargetAmount(new BigDecimal("5000.00"));
        goal.setTargetDate(LocalDate.now().plusMonths(6));
        goal.setStartDate(LocalDate.now());
        goal.setUser(user);

        assertEquals(1L, goal.getId());
        assertEquals("Emergency Fund", goal.getGoalName());
        assertEquals(new BigDecimal("5000.00"), goal.getTargetAmount());
        assertEquals(LocalDate.now().plusMonths(6), goal.getTargetDate());
        assertEquals(LocalDate.now(), goal.getStartDate());
        assertEquals(user, goal.getUser());
    }

    @Test
    void categoryType_Values() {
        CategoryType[] types = CategoryType.values();
        assertEquals(2, types.length);
        assertTrue(java.util.Arrays.asList(types).contains(CategoryType.INCOME));
        assertTrue(java.util.Arrays.asList(types).contains(CategoryType.EXPENSE));
    }

    @Test
    void categoryType_ValueOf() {
        assertEquals(CategoryType.INCOME, CategoryType.valueOf("INCOME"));
        assertEquals(CategoryType.EXPENSE, CategoryType.valueOf("EXPENSE"));
    }
} 