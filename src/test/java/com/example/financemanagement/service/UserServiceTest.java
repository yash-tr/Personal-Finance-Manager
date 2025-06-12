package com.example.financemanagement.service;

import com.example.financemanagement.dto.UserRegistrationRequest;
import com.example.financemanagement.entity.User;
import com.example.financemanagement.exception.ResourceConflictException;
import com.example.financemanagement.repository.CategoryRepository;
import com.example.financemanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRegistrationRequest registrationRequest;

    @BeforeEach
    void setUp() {
        registrationRequest = new UserRegistrationRequest(
                "testuser@example.com",
                "password123",
                "Test User",
                "1234567890"
        );
    }

    @Test
    void registerUser_Success() {
        when(userRepository.existsByUsername(registrationRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        User result = userService.registerUser(registrationRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(registrationRequest.getUsername(), result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
    }

    @Test
    void registerUser_UsernameAlreadyTaken() {
        when(userRepository.existsByUsername(registrationRequest.getUsername())).thenReturn(true);

        assertThrows(ResourceConflictException.class, () -> {
            userService.registerUser(registrationRequest);
        });
    }
} 