package com.example.financemanagement.controller;

import com.example.financemanagement.dto.UserRegistrationRequest;
import com.example.financemanagement.entity.User;
import com.example.financemanagement.exception.ResourceConflictException;
import com.example.financemanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureWebMvc
@Import(com.example.financemanagement.config.SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRegistrationRequest validRequest;
    private User user;

    @BeforeEach
    void setUp() {
        validRequest = new UserRegistrationRequest(
                "test@example.com",
                "password123",
                "Test User",
                "+1234567890"
        );

        user = new User("test@example.com", "password123", "Test User", "+1234567890");
        user.setId(1L);
    }

    @Test
    void registerUser_Success() throws Exception {
        when(userService.registerUser(any(UserRegistrationRequest.class))).thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void registerUser_InvalidEmail_BadRequest() throws Exception {
        UserRegistrationRequest invalidRequest = new UserRegistrationRequest(
                "invalid-email",
                "password123",
                "Test User",
                "+1234567890"
        );

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_ShortPassword_BadRequest() throws Exception {
        UserRegistrationRequest invalidRequest = new UserRegistrationRequest(
                "test@example.com",
                "short",
                "Test User",
                "+1234567890"
        );

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_MissingFields_BadRequest() throws Exception {
        UserRegistrationRequest invalidRequest = new UserRegistrationRequest(
                "",
                "",
                "",
                ""
        );

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_DuplicateUsername_Conflict() throws Exception {
        when(userService.registerUser(any(UserRegistrationRequest.class)))
                .thenThrow(new ResourceConflictException("Username is already taken"));

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username is already taken"));
    }
} 