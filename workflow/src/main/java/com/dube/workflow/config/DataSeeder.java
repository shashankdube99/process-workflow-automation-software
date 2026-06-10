package com.dube.workflow.config;

import com.dube.workflow.user.User;
import com.dube.workflow.user.UserRepository;
import com.dube.workflow.auth.Role;
import com.dube.workflow.auth.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, 
                                   RoleRepository roleRepository, 
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Find or create the ADMIN role using your exact 'roleName' field
            Role adminRole;
            Optional<Role> existingRole = roleRepository.findByRoleName("ADMIN");
            
            if (existingRole.isEmpty()) {
                adminRole = new Role();
                adminRole.setRoleName("ADMIN");
                adminRole.setDescription("Super Administrator with full access");
                roleRepository.save(adminRole);
            } else {
                adminRole = existingRole.get();
            }

            // 2. Create the initial Admin User if the database is empty
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setEmail("admin@workflow.com");
                admin.setPassword(passwordEncoder.encode("admin123")); // Hashed for Neon DB
                admin.setRole(adminRole); // Assigns the UUID-based role relationship

                userRepository.save(admin);
                System.out.println("=================================================");
                System.out.println("DEFAULT ADMIN USER SEEDED: admin@workflow.com");
                System.out.println("=================================================");
            }
        };
    }
}