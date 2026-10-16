
package CampusCare.admin;

import CampusCare.user.User;
import CampusCare.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;

    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Authentication required."));
        }

        List<Map<String, Object>> users = userRepository.findAll()
                .stream()
                .map(user -> {
                    Map<String, Object> item = new java.util.HashMap<>();

                    item.put("id", user.getId());
                    item.put("name", user.getName());
                    item.put("email", user.getEmail());
                    item.put("role", user.getRole());
                    item.put(
                            "verificationStatus",
                            user.getVerificationStatus()
                    );
                    item.put(
                            "enrollmentNumber",
                            user.getEnrollmentNumber()
                    );
                    item.put("facultyId", user.getFacultyId());

                    return item;
                })
                .toList();

        return ResponseEntity.ok(users);
    }
}
