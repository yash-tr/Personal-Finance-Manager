package com.example.financemanagement.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void passwordEncoder_EncodeAndMatch() {
        String rawPassword = "testPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
        assertFalse(passwordEncoder.matches("wrongPassword", encodedPassword));
    }

    @Test
    void securityConfig_ProtectedEndpoints() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Test that protected endpoints require authentication
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/goals"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/reports/monthly/2024/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void securityConfig_PublicEndpoints() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Test that public endpoints are accessible
        mockMvc.perform(get("/api/auth/register"))
                .andExpect(status().isMethodNotAllowed()); // GET not allowed, but not unauthorized

        mockMvc.perform(get("/h2-console"))
                .andExpect(status().isNotFound()); // H2 console might not be available in test context
    }
} 