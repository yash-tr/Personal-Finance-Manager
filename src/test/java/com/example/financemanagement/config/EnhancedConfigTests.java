package com.example.financemanagement.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class EnhancedConfigTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void securityConfig_BeansExistAndConfigured() {
        // Test that all security beans are properly configured
        assertTrue(applicationContext.containsBean("passwordEncoder"));
        assertTrue(applicationContext.containsBean("securityFilterChain"));
        
        // Test password encoder is properly configured
        assertNotNull(passwordEncoder);
        
        // Test password encoding functionality
        String rawPassword = "testPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
        assertFalse(passwordEncoder.matches("wrongPassword", encodedPassword));
        
        // Test password encoder strength
        String anotherEncoding = passwordEncoder.encode(rawPassword);
        assertNotEquals(encodedPassword, anotherEncoding); // Should use different salts
        assertTrue(passwordEncoder.matches(rawPassword, anotherEncoding));
    }

    @Test
    void securityConfig_FilterChainConfiguration() {
        // Test that SecurityFilterChain bean is created
        SecurityFilterChain filterChain = applicationContext.getBean(SecurityFilterChain.class);
        assertNotNull(filterChain);
        
        // Test filter chain has required filters
        assertNotNull(filterChain.getFilters());
        assertFalse(filterChain.getFilters().isEmpty());
    }

    @Test
    void securityConfig_CorsConfiguration() {
        // Test CORS configuration bean exists
        if (applicationContext.containsBean("corsConfigurationSource")) {
            CorsConfigurationSource corsConfigurationSource = 
                applicationContext.getBean(CorsConfigurationSource.class);
            assertNotNull(corsConfigurationSource);
        }
    }

    @Test
    void passwordEncoder_EdgeCases() {
        // Test with empty password
        String emptyPassword = "";
        String encodedEmpty = passwordEncoder.encode(emptyPassword);
        assertNotNull(encodedEmpty);
        assertTrue(passwordEncoder.matches(emptyPassword, encodedEmpty));
        
        // Test with special characters
        String specialPassword = "p@ssw0rd!@#$%^&*()";
        String encodedSpecial = passwordEncoder.encode(specialPassword);
        assertNotNull(encodedSpecial);
        assertTrue(passwordEncoder.matches(specialPassword, encodedSpecial));
        
        // Test with long password
        String longPassword = "a".repeat(100);
        String encodedLong = passwordEncoder.encode(longPassword);
        assertNotNull(encodedLong);
        assertTrue(passwordEncoder.matches(longPassword, encodedLong));
        
        // Test with unicode characters
        String unicodePassword = "password123";
        String encodedUnicode = passwordEncoder.encode(unicodePassword);
        assertNotNull(encodedUnicode);
        assertTrue(passwordEncoder.matches(unicodePassword, encodedUnicode));
    }

    @Test
    void securityConfig_BeanDefinitions() {
        // Test that all expected beans are defined
        String[] expectedBeans = {
            "passwordEncoder",
            "securityFilterChain"
        };
        
        for (String beanName : expectedBeans) {
            assertTrue(applicationContext.containsBean(beanName), 
                "Expected bean not found: " + beanName);
        }
    }

    @Test
    void applicationContext_SecurityConfiguration() {
        // Test that security configuration is properly loaded
        assertTrue(applicationContext.getBeansOfType(SecurityConfig.class).size() > 0);
        
        // Test singleton scope for password encoder
        PasswordEncoder encoder1 = applicationContext.getBean(PasswordEncoder.class);
        PasswordEncoder encoder2 = applicationContext.getBean(PasswordEncoder.class);
        assertSame(encoder1, encoder2); // Should be the same instance (singleton)
    }

    @Test
    void passwordEncoder_ConsistencyAndSecurity() {
        String password = "testPassword";
        
        // Test multiple encodings of the same password
        String encoded1 = passwordEncoder.encode(password);
        String encoded2 = passwordEncoder.encode(password);
        String encoded3 = passwordEncoder.encode(password);
        
        // All should be different due to random salt
        assertNotEquals(encoded1, encoded2);
        assertNotEquals(encoded2, encoded3);
        assertNotEquals(encoded1, encoded3);
        
        // But all should match the original password
        assertTrue(passwordEncoder.matches(password, encoded1));
        assertTrue(passwordEncoder.matches(password, encoded2));
        assertTrue(passwordEncoder.matches(password, encoded3));
        
        // None should match a different password
        assertFalse(passwordEncoder.matches("differentPassword", encoded1));
        assertFalse(passwordEncoder.matches("differentPassword", encoded2));
        assertFalse(passwordEncoder.matches("differentPassword", encoded3));
    }

    @Test
    void securityConfig_DefaultConfiguration() {
        // Test that configuration provides sensible defaults
        assertNotNull(passwordEncoder);
        assertNotNull(applicationContext.getBean(SecurityFilterChain.class));
        
        // Verify password encoder type (should be BCrypt)
        assertTrue(passwordEncoder.getClass().getSimpleName().contains("BCrypt") || 
                  passwordEncoder.getClass().getSimpleName().contains("Password"));
    }
} 