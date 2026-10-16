
package CampusCare.admin;

import CampusCare.user.User;
import CampusCare.user.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/faculty")
public class AdminApprovalController {

    private final AdminApprovalService approvalService;
    private final UserRepository userRepository;

    public AdminApprovalController(
            AdminApprovalService approvalService,
            UserRepository userRepository) {
        this.approvalService = approvalService;
        this.userRepository = userRepository;
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(
            @PathVariable Long id,
            Authentication authentication) {

        User admin = getAuthenticatedAdmin(authentication);
        User faculty = approvalService.approveFaculty(id, admin.getId());

        return ResponseEntity.ok(Map.of(
                "message", "Faculty approved successfully",
                "facultyId", faculty.getId(),
                "status", faculty.getVerificationStatus()
        ));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(
            @PathVariable Long id,
            Authentication authentication) {

        User admin = getAuthenticatedAdmin(authentication);
        User faculty = approvalService.rejectFaculty(id, admin.getId());

        return ResponseEntity.ok(Map.of(
                "message", "Faculty rejected",
                "facultyId", faculty.getId(),
                "status", faculty.getVerificationStatus()
        ));
    }

    private User getAuthenticatedAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Authentication required");
        }

        return userRepository.findByEmail(authentication.getName())
                .filter(user ->
                        "ADMIN".equals(user.getRole())
                        && "VERIFIED".equals(user.getVerificationStatus()))
                .orElseThrow(() ->
                        new org.springframework.security.access.AccessDeniedException(
                                "Verified Admin access required"));
    }
}
