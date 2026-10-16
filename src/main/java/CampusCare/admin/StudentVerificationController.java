
package CampusCare.admin;

import CampusCare.user.User;
import CampusCare.user.UserRepository;
import CampusCare.verification.StudentVerificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/students")
public class StudentVerificationController {

    private final StudentVerificationService verificationService;
    private final UserRepository userRepository;

    public StudentVerificationController(
            StudentVerificationService verificationService,
            UserRepository userRepository) {
        this.verificationService = verificationService;
        this.userRepository = userRepository;
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<?> verifyStudent(
            @PathVariable Long id,
            @RequestBody VerifyStudentRequest request,
            Authentication authentication) {

        User admin = getAuthenticatedAdmin(authentication);

        User student = verificationService.verifyStudent(
                id,
                request.getEnrollmentNumber()
        );

        return ResponseEntity.ok(Map.of(
                "message", "Student verified successfully",
                "studentId", student.getId(),
                "status", student.getVerificationStatus()
        ));
    }

    private User getAuthenticatedAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }

        return userRepository.findByEmail(authentication.getName())
                .filter(user -> "ADMIN".equals(user.getRole()))
                .filter(user -> "VERIFIED".equals(
                        user.getVerificationStatus()))
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "Verified Admin access required"));
    }

    public static class VerifyStudentRequest {
        private String enrollmentNumber;

        public String getEnrollmentNumber() {
            return enrollmentNumber;
        }

        public void setEnrollmentNumber(String enrollmentNumber) {
            this.enrollmentNumber = enrollmentNumber;
        }
    }
}
