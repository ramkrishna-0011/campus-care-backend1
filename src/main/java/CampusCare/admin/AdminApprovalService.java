
package CampusCare.admin;

import CampusCare.user.User;
import CampusCare.user.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class AdminApprovalService {

    private final UserRepository userRepository;

    public AdminApprovalService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User approveFaculty(Long facultyUserId, Long adminUserId) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Admin account not found"));

        if (!"ADMIN".equals(admin.getRole())
                || !"VERIFIED".equals(admin.getVerificationStatus())) {
            throw new IllegalArgumentException(
                    "Only a verified Admin can approve Faculty");
        }

        User faculty = userRepository.findById(facultyUserId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Faculty account not found"));

        if (!"FACULTY".equals(faculty.getRole())) {
            throw new IllegalArgumentException("Selected account is not Faculty");
        }

        if (!"PENDING_APPROVAL".equals(faculty.getVerificationStatus())) {
            throw new IllegalArgumentException(
                    "Faculty account is not awaiting approval");
        }

        faculty.setVerificationStatus("VERIFIED");
        faculty.setApprovedBy(admin.getId());
        faculty.setApprovedAt(LocalDateTime.now());

        return userRepository.save(faculty);
    }

    @Transactional
    public User rejectFaculty(Long facultyUserId, Long adminUserId) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Admin account not found"));

        if (!"ADMIN".equals(admin.getRole())
                || !"VERIFIED".equals(admin.getVerificationStatus())) {
            throw new IllegalArgumentException(
                    "Only a verified Admin can reject Faculty");
        }

        User faculty = userRepository.findById(facultyUserId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Faculty account not found"));

        if (!"FACULTY".equals(faculty.getRole())) {
            throw new IllegalArgumentException("Selected account is not Faculty");
        }

        if (!"PENDING_APPROVAL".equals(faculty.getVerificationStatus())) {
            throw new IllegalArgumentException(
                    "Faculty account is not awaiting approval");
        }

        faculty.setVerificationStatus("REJECTED");
        faculty.setApprovedBy(admin.getId());
        faculty.setApprovedAt(LocalDateTime.now());

        return userRepository.save(faculty);
    }
}
