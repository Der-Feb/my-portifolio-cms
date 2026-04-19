package tech.derfeb.portfolio_cms.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import tech.derfeb.portfolio_cms.Model.RoleModel;
import tech.derfeb.portfolio_cms.Model.UserModel;
import tech.derfeb.portfolio_cms.Repository.RoleRepository;
import tech.derfeb.portfolio_cms.Repository.UserRepository;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            // Ensure Roles exist
            createRolesIfNotFound(roleRepository);

            // Ensure Admin User exists
            createAdminUserIfNotFound(userRepository, roleRepository);
        };
    }

    private void createRolesIfNotFound(RoleRepository roleRepository) {
        // Create Admin Roles
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            RoleModel adminRole = new RoleModel();
            adminRole.setName("ROLE_ADMIN");
            adminRole.setDescription("Full access to manage projects and users");
            roleRepository.save(adminRole);
            System.out.println("✅ ROLE_ADMIN created.");
        }

        // Create User Role
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            RoleModel userRole = new RoleModel();
            userRole.setName("ROLE_USER");
            userRole.setDescription("Limited access to view or interact with content");
            roleRepository.save(userRole);
            System.out.println("✅ ROLE_USER created.");
        }
    }

    private void createAdminUserIfNotFound(UserRepository userRepository, RoleRepository roleRepository) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            // Find the admin role we created in the other method
            RoleModel adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Error: Role not found."));

            UserModel admin = new UserModel();
            admin.setUsername("admin");
            // Encrypting the password
            admin.setPassword(new BCryptPasswordEncoder().encode("your_secure_password"));
            admin.setRoles(Set.of(adminRole));

            userRepository.save(admin);
            System.out.println("Admin user 'admin' initialized successfully!");
        }
    }
}