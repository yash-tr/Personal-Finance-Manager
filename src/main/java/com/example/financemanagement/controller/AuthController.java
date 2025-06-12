package com.example.financemanagement.controller;

import com.example.financemanagement.dto.LoginRequest;
import com.example.financemanagement.dto.UserRegistrationRequest;
import com.example.financemanagement.entity.User;
import com.example.financemanagement.exception.ResourceConflictException;
import com.example.financemanagement.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.HashMap;

/**
 * REST controller for handling user authentication operations including registration,
 * login, and logout functionality. This controller provides endpoints for user
 * account management and session-based authentication.
 * 
 * <p>Supported operations:
 * <ul>
 *   <li>User registration with validation and automatic default category creation</li>
 *   <li>User login with session establishment</li>
 *   <li>User logout with session invalidation</li>
 * </ul>
 * 
 * <p>All endpoints return appropriate HTTP status codes and standardized JSON responses.
 * The controller integrates with Spring Security for authentication and session management.
 * 
 * @author Finance Management Team
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Registers a new user account in the system.
     * 
     * <p>This endpoint creates a new user account with the provided credentials and
     * personal information. Upon successful registration, default income and expense
     * categories are automatically created for the user.
     * 
     * <p>The password is securely hashed using BCrypt before storage, and the username
     * (email) is validated for uniqueness.
     * 
     * @param registrationRequest the user registration data including username, password,
     *                          full name, and phone number
     * @return ResponseEntity containing success message (201 Created) or error details
     * @throws ResourceConflictException if the username already exists (409 Conflict)
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody UserRegistrationRequest registrationRequest) {
        try {
            userService.registerUser(registrationRequest);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ResourceConflictException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    /**
     * Authenticates a user and establishes a session.
     * 
     * <p>This endpoint validates user credentials and creates an authenticated session
     * if the credentials are valid. The session is maintained through HTTP cookies
     * and can be used for subsequent authenticated requests.
     * 
     * <p>Authentication is performed through Spring Security's AuthenticationManager,
     * which handles password verification and account status checks.
     * 
     * @param loginRequest the login credentials containing username and password
     * @param request the HTTP servlet request for session management
     * @param response the HTTP servlet response for setting authentication cookies
     * @return ResponseEntity containing success message (200 OK) or error details
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        try {
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Create new session
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Login successful");
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Logs out the current user.
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @return A response entity with a success message.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }
} 