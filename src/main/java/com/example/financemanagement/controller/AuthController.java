package com.example.financemanagement.controller;

import com.example.financemanagement.dto.LoginRequest;
import com.example.financemanagement.dto.UserRegistrationRequest;
import com.example.financemanagement.entity.User;
import com.example.financemanagement.exception.ResourceConflictException;
import com.example.financemanagement.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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
     * Authenticates a user with the provided credentials and establishes an HTTP session.
     *
     * <p>This endpoint expects a JSON payload containing <code>username</code> and <code>password</code>.
     * On successful authentication, a session is created (or reused) and the standard <code>JSESSIONID</code>
     * cookie is returned to the client so that subsequent requests are authenticated.</p>
     *
     * @param loginRequest the login credentials
     * @param request      the HTTP request (needed for session handling)
     * @return 200 OK with a JSON success message, or 401 when the credentials are invalid
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest loginRequest,
                                                    HttpServletRequest request) {
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), loginRequest.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);

            // Store authentication in the security context
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            // Create or retrieve the HTTP session and attach the security context to it
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful");
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * Logs out the current user by invalidating their HTTP session and clearing the security context.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return 200 OK with a confirmation message
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        Map<String, String> res = new HashMap<>();
        res.put("message", "Logout successful");
        return ResponseEntity.ok(res);
    }
} 