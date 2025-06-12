package com.example.financemanagement.service;

import com.example.financemanagement.entity.User;
import com.example.financemanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("user@example.com", "password", "Test User", "1234567890");
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        when(userRepository.findByUsername("user@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("user@example.com");

        assertNotNull(userDetails);
        assertEquals(user.getUsername(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        verify(userRepository).findByUsername("user@example.com");
    }

    @Test
    void loadUserByUsername_shouldThrowUsernameNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findByUsername("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent@example.com");
        });

        verify(userRepository).findByUsername("nonexistent@example.com");
    }
} 