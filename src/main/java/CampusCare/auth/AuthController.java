
package CampusCare.auth;

import CampusCare.auth.dto.AuthResponse;
import CampusCare.auth.dto.LoginRequest;
import CampusCare.auth.dto.RegisterRequest;
import CampusCare.user.User;
import CampusCare.user.UserRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(
            AuthService authService,
            UserRepository userRepository) {

        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request) {

        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(authService.register(request));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {

        try {
            AuthResponse result = authService.login(request);
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "Authentication required."
                    ));
        }

        String email = authentication.getName()
                .trim()
                .toLowerCase(Locale.ROOT);

        return userRepository.findByEmail(email)
                .filter(user ->
                        "STUDENT".equalsIgnoreCase(user.getRole())
                        || "VERIFIED".equalsIgnoreCase(
                                user.getVerificationStatus()
                        )
                )
                .<ResponseEntity<?>>map(user -> {
                    Map<String, Object> profile = new LinkedHashMap<>();

                    profile.put("id", user.getId());
                    profile.put("name", user.getName());
                    profile.put("email", user.getEmail());
                    profile.put("role", user.getRole());

                    // Additional profile fields
                    profile.put("enrollmentNumber",
                            user.getEnrollmentNumber());
                    profile.put("facultyId", user.getFacultyId());
                    profile.put("department", user.getDepartment());
                    profile.put("course", user.getCourse());
                    profile.put("branch", user.getBranch());
                    profile.put("semester", user.getSemester());
                    profile.put("phoneNumber", user.getPhoneNumber());

                    return ResponseEntity.ok(profile);
                })
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of(
                                        "message",
                                        "Account verify nahi hua ya user nahi mila."
                                ))
                );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(
                Map.of("message", "Logged out successfully")
        );
    }
}
