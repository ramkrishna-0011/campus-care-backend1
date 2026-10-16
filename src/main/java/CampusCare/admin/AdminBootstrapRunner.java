
package CampusCare.admin;

import CampusCare.user.User;
import CampusCare.user.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

@Configuration
public class AdminBootstrapRunner {

    @Bean
    CommandLineRunner bootstrapInitialAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${campuscare.bootstrap-admin.email:}") String email,
            @Value("${campuscare.bootstrap-admin.password:}") String password
    ) {
        return args -> {
            boolean adminExists = userRepository.findAll().stream()
                    .anyMatch(user -> "ADMIN".equals(user.getRole()));

            if (adminExists) {
                System.out.println(
                        "Admin already exists. Bootstrap skipped.");
                return;
            }

            if (email.isBlank() || password.isBlank()) {
                System.out.println(
                        "No bootstrap credentials configured. Admin creation skipped.");
                return;
            }

            String normalizedEmail =
                    email.trim().toLowerCase(Locale.ROOT);

            if (userRepository.existsByEmail(normalizedEmail)) {
                throw new IllegalStateException(
                        "Bootstrap email already belongs to another account. "
                        + "Choose a separate Admin email.");
            }

            if (password.length() < 12) {
                throw new IllegalStateException(
                        "Bootstrap Admin password must be at least 12 characters.");
            }

            User admin = new User();
            admin.setName("CampusCare Admin");
            admin.setEmail(normalizedEmail);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole("ADMIN");
            admin.setVerificationStatus("VERIFIED");

            userRepository.save(admin);

            System.out.println(
                    "Initial Admin created. Remove bootstrap credentials now.");
        };
    }
}
