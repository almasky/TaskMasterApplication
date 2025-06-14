package com.maj.TaskMasterApplication.config; // Or your preferred package for such components

import com.maj.TaskMasterApplication.model.User;
import com.maj.TaskMasterApplication.model.Roles;
import com.maj.TaskMasterApplication.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // Optional, but good for DB operations

@Component // Marks this as a Spring-managed component, so it will be discovered and run
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired // Constructor injection is generally preferred
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional // Good practice to make the run method transactional if it involves multiple DB writes
    public void run(String... args) throws Exception {
        // Define the admin user details
        String adminUsername = "superadmin";
        String adminEmail = "superadmin@example.com";
        String adminPassword = "superadminpass"; // Change this in a real scenario

        // Check if the admin user already exists
        if (!userRepository.existsByUsername(adminUsername)) {
            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(adminPassword)); // Always encode passwords
            adminUser.setRole(Roles.ADMIN);
            // Any other default fields for User can be set here if necessary

            userRepository.save(adminUser);
            logger.info("Successfully created default admin user: {}", adminUsername);
        } else {
            logger.info("Admin user '{}' already exists. No action taken.", adminUsername);
        }

        // You can add more initial data here if needed, e.g., a default regular user for testing
        String testUsername = "testuser";
        if (!userRepository.existsByUsername(testUsername)) {
            User regularUser = new User();
            regularUser.setUsername(testUsername);
            regularUser.setEmail("testuser@example.com");
            regularUser.setPassword(passwordEncoder.encode("password123"));
            regularUser.setRole(Roles.USER);
            userRepository.save(regularUser);
            logger.info("Successfully created default test user: {}", testUsername);
        } else {
            logger.info("Test user '{}' already exists. No action taken.", testUsername);
        }
    }
}
