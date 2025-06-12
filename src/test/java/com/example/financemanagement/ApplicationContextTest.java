package com.example.financemanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        // Test that the Spring context loads successfully
        assertNotNull(applicationContext);
        assertTrue(applicationContext.getBeanDefinitionCount() > 0);
    }

    @Test
    void applicationHasRequiredBeans() {
        // Test that essential beans are present in the context
        assertTrue(applicationContext.containsBean("passwordEncoder"));
        assertTrue(applicationContext.containsBean("securityFilterChain"));
        
        // Test service beans
        String[] serviceBeans = {"authService", "categoryService", "transactionService", 
                               "reportService", "savingsGoalService"};
        
        for (String beanName : serviceBeans) {
            assertTrue(applicationContext.containsBean(beanName), 
                "Missing required service bean: " + beanName);
        }
        
        // Test repository beans
        String[] repositoryBeans = {"userRepository", "categoryRepository", 
                                  "transactionRepository", "savingsGoalRepository"};
        
        for (String beanName : repositoryBeans) {
            assertTrue(applicationContext.containsBean(beanName), 
                "Missing required repository bean: " + beanName);
        }
        
        // Test controller beans
        String[] controllerBeans = {"authController", "categoryController", 
                                  "transactionController", "reportController", "savingsGoalController"};
        
        for (String beanName : controllerBeans) {
            assertTrue(applicationContext.containsBean(beanName), 
                "Missing required controller bean: " + beanName);
        }
    }

    @Test
    void applicationPropertiesLoaded() {
        // Test that application properties are properly loaded
        assertNotNull(applicationContext.getEnvironment());
        
        // Test active profiles
        String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        assertTrue(activeProfiles.length > 0);
        assertEquals("test", activeProfiles[0]);
    }

    @Test
    void jpaConfigurationLoaded() {
        // Test that JPA configuration is properly loaded
        assertTrue(applicationContext.containsBean("entityManagerFactory"));
        assertTrue(applicationContext.containsBean("transactionManager"));
    }

    @Test
    void securityConfigurationLoaded() {
        // Test that security configuration is properly loaded
        assertTrue(applicationContext.containsBean("passwordEncoder"));
        assertTrue(applicationContext.containsBean("securityFilterChain"));
    }
} 