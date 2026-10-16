
package CampusCare.admin;

import CampusCare.user.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final UserRepository userRepository;

    public AdminDashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/stats")
    public Map<String, Long> getDashboardStats() {

        long totalStudents =
                userRepository.countByRoleIgnoreCase("STUDENT");

        long totalFaculty =
                userRepository.countByRoleIgnoreCase("FACULTY");

        long pendingStudents =
                userRepository.countByRoleIgnoreCaseAndVerificationStatusIgnoreCase(
                        "STUDENT", "PENDING"
                );

        long pendingFaculty =
                userRepository.countByRoleIgnoreCaseAndVerificationStatusIgnoreCase(
                        "FACULTY", "PENDING_APPROVAL"
                );

        long verifiedStudents =
                userRepository.countByRoleIgnoreCaseAndVerificationStatusIgnoreCase(
                        "STUDENT", "VERIFIED"
                );

        long pendingApprovals = pendingStudents + pendingFaculty;

        return Map.of(
                "totalStudents", totalStudents,
                "totalFaculty", totalFaculty,
                "pendingStudents", pendingStudents,
                "pendingFaculty", pendingFaculty,
                "pendingApprovals", pendingApprovals,
                "verifiedStudents", verifiedStudents
        );
    }
}
