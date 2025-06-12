package com.example.financemanagement.integration;

import com.example.financemanagement.entity.Category;
import com.example.financemanagement.entity.CategoryType;
import com.example.financemanagement.entity.User;
import com.example.financemanagement.repository.CategoryRepository;
import com.example.financemanagement.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class IntegrationTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Create test user
        testUser = new User("test@example.com", passwordEncoder.encode("password123"), "Test User", "+1234567890");
        testUser = userRepository.save(testUser);

        // Create default categories for the user
        createDefaultCategories();
    }

    private void createDefaultCategories() {
        // Income categories
        categoryRepository.save(new Category("Salary", CategoryType.INCOME, false, testUser));
        
        // Expense categories
        categoryRepository.save(new Category("Food", CategoryType.EXPENSE, false, testUser));
        categoryRepository.save(new Category("Rent", CategoryType.EXPENSE, false, testUser));
        categoryRepository.save(new Category("Transportation", CategoryType.EXPENSE, false, testUser));
        categoryRepository.save(new Category("Entertainment", CategoryType.EXPENSE, false, testUser));
        categoryRepository.save(new Category("Healthcare", CategoryType.EXPENSE, false, testUser));
        categoryRepository.save(new Category("Utilities", CategoryType.EXPENSE, false, testUser));
    }

    @Test
    void userRegistration_Success() throws Exception {
        Map<String, String> registrationData = new HashMap<>();
        registrationData.put("username", "newuser@example.com");
        registrationData.put("password", "password123");
        registrationData.put("fullName", "New User");
        registrationData.put("phoneNumber", "+1987654321");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.userId").exists());
    }

    @Test
    void userRegistration_DuplicateUsername() throws Exception {
        Map<String, String> registrationData = new HashMap<>();
        registrationData.put("username", "test@example.com"); // Already exists
        registrationData.put("password", "password123");
        registrationData.put("fullName", "Duplicate User");
        registrationData.put("phoneNumber", "+1987654321");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isConflict());
    }

    @Test
    void createTransaction_Success() throws Exception {
        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("amount", 50000.00);
        transactionData.put("date", LocalDate.now().toString());
        transactionData.put("category", "Salary");
        transactionData.put("description", "Monthly Salary");

        mockMvc.perform(post("/api/transactions")
                .with(user("test@example.com").password("password123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(50000.00))
                .andExpect(jsonPath("$.category").value("Salary"))
                .andExpect(jsonPath("$.type").value("INCOME"));
    }

    @Test
    void createTransaction_InvalidCategory() throws Exception {
        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("amount", 100.00);
        transactionData.put("date", LocalDate.now().toString());
        transactionData.put("category", "NonExistentCategory");
        transactionData.put("description", "Test");

        mockMvc.perform(post("/api/transactions")
                .with(user("test@example.com").password("password123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionData)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found: NonExistentCategory"));
    }

    @Test
    void getTransactions_Success() throws Exception {
        // First create a transaction
        createTestTransaction();

        mockMvc.perform(get("/api/transactions")
                .with(user("test@example.com").password("password123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions").isArray())
                .andExpect(jsonPath("$.transactions[0].description").value("Test Transaction"));
    }

    @Test
    void getTransactions_WithFilters() throws Exception {
        createTestTransaction();

        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        mockMvc.perform(get("/api/transactions")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .with(user("test@example.com").password("password123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions").isArray());
    }

    @Test
    void updateTransaction_Success() throws Exception {
        Long transactionId = createTestTransaction();

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("amount", 200.00);
        updateData.put("description", "Updated Transaction");

        mockMvc.perform(put("/api/transactions/" + transactionId)
                .with(user("test@example.com").password("password123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(200.00))
                .andExpect(jsonPath("$.description").value("Updated Transaction"));
    }

    @Test
    void deleteTransaction_Success() throws Exception {
        Long transactionId = createTestTransaction();

        mockMvc.perform(delete("/api/transactions/" + transactionId)
                .with(user("test@example.com").password("password123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction deleted successfully"));
    }

    @Test
    void getCategories_Success() throws Exception {
        mockMvc.perform(get("/api/categories")
                .with(user("test@example.com").password("password123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories[?(@.name=='Salary')].type").value("INCOME"))
                .andExpect(jsonPath("$.categories[?(@.name=='Food')].type").value("EXPENSE"));
    }

    @Test
    void createCustomCategory_Success() throws Exception {
        Map<String, String> categoryData = new HashMap<>();
        categoryData.put("name", "CustomIncome");
        categoryData.put("type", "INCOME");

        mockMvc.perform(post("/api/categories")
                .with(user("test@example.com").password("password123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("CustomIncome"))
                .andExpect(jsonPath("$.type").value("INCOME"))
                .andExpect(jsonPath("$.isCustom").value(true));
    }

    @Test
    void createCustomCategory_DuplicateName() throws Exception {
        Map<String, String> categoryData = new HashMap<>();
        categoryData.put("name", "Food"); // Already exists
        categoryData.put("type", "EXPENSE");

        mockMvc.perform(post("/api/categories")
                .with(user("test@example.com").password("password123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryData)))
                .andExpect(status().isConflict());
    }

    @Test
    void createSavingsGoal_Success() throws Exception {
        Map<String, Object> goalData = new HashMap<>();
        goalData.put("goalName", "Emergency Fund");
        goalData.put("targetAmount", 5000.00);
        goalData.put("targetDate", LocalDate.now().plusMonths(6).toString());
        goalData.put("startDate", LocalDate.now().toString());

        mockMvc.perform(post("/api/goals")
                .with(user("test@example.com").password("password123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goalData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.goalName").value("Emergency Fund"))
                .andExpect(jsonPath("$.targetAmount").value(5000.00))
                .andExpect(jsonPath("$.progressPercentage").exists());
    }

    @Test
    void getSavingsGoals_Success() throws Exception {
        createTestSavingsGoal();

        mockMvc.perform(get("/api/goals")
                .with(user("test@example.com").password("password123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goals").isArray())
                .andExpect(jsonPath("$.goals[0].goalName").value("Test Goal"));
    }

    @Test
    void updateSavingsGoal_Success() throws Exception {
        Long goalId = createTestSavingsGoal();

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("targetAmount", 6000.00);

        mockMvc.perform(put("/api/goals/" + goalId)
                .with(user("test@example.com").password("password123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetAmount").value(6000.00));
    }

    @Test
    void getMonthlyReport_Success() throws Exception {
        createTestTransaction();

        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        mockMvc.perform(get("/api/reports/monthly/" + currentYear + "/" + currentMonth)
                .with(user("test@example.com").password("password123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value(currentMonth))
                .andExpect(jsonPath("$.year").value(currentYear))
                .andExpect(jsonPath("$.totalIncome").exists())
                .andExpect(jsonPath("$.totalExpenses").exists())
                .andExpect(jsonPath("$.netSavings").exists());
    }

    @Test
    void getYearlyReport_Success() throws Exception {
        createTestTransaction();

        int currentYear = LocalDate.now().getYear();

        mockMvc.perform(get("/api/reports/yearly/" + currentYear)
                .with(user("test@example.com").password("password123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(currentYear))
                .andExpect(jsonPath("$.totalIncome").exists())
                .andExpect(jsonPath("$.totalExpenses").exists())
                .andExpect(jsonPath("$.netSavings").exists());
    }

    @Test
    void unauthorizedAccess_Returns401() throws Exception {
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessOtherUserData_Returns403() throws Exception {
        // Create another user
        User anotherUser = new User("another@example.com", passwordEncoder.encode("password"), "Another User", "+9999999999");
        userRepository.save(anotherUser);

        // Try to access with different user credentials
        mockMvc.perform(get("/api/transactions/999") // Non-existent transaction
                .with(user("another@example.com").password("password")))
                .andExpect(status().isNotFound());
    }

    private Long createTestTransaction() throws Exception {
        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("amount", 150.00);
        transactionData.put("date", LocalDate.now().toString());
        transactionData.put("category", "Food");
        transactionData.put("description", "Test Transaction");

        MvcResult result = mockMvc.perform(post("/api/transactions")
                .with(user("test@example.com").password("password123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionData)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(responseBody, Map.class);
        return Long.valueOf(response.get("id").toString());
    }

    private Long createTestSavingsGoal() throws Exception {
        Map<String, Object> goalData = new HashMap<>();
        goalData.put("goalName", "Test Goal");
        goalData.put("targetAmount", 1000.00);
        goalData.put("targetDate", LocalDate.now().plusMonths(3).toString());
        goalData.put("startDate", LocalDate.now().toString());

        MvcResult result = mockMvc.perform(post("/api/goals")
                .with(user("test@example.com").password("password123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goalData)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(responseBody, Map.class);
        return Long.valueOf(response.get("id").toString());
    }
} 