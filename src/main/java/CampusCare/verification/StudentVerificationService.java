
package CampusCare.verification;

import CampusCare.user.User;
import CampusCare.user.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class StudentVerificationService {

    private final UserRepository userRepository;
    private final StudentRegistryRepository studentRegistryRepository;

    public StudentVerificationService(
            UserRepository userRepository,
            StudentRegistryRepository studentRegistryRepository) {
        this.userRepository = userRepository;
        this.studentRegistryRepository = studentRegistryRepository;
    }

    @Transactional
    public User verifyStudent(Long userId, String enrollmentNumber) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Student account not found"));

        if (!"STUDENT".equals(user.getRole())) {
            throw new IllegalArgumentException("Account is not a Student");
        }

        if (!"PENDING".equals(user.getVerificationStatus())) {
            throw new IllegalArgumentException(
                    "Student account is not pending verification");
        }

        if (enrollmentNumber == null || enrollmentNumber.isBlank()) {
            throw new IllegalArgumentException("Enrollment number is required");
        }

        String email = user.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        String enrollment = enrollmentNumber.trim();

        StudentRegistry registry = studentRegistryRepository
                .findByEnrollmentNumberAndEmailAndActiveTrue(enrollment, email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Enrollment number and official email do not match"));

        if (userRepository.existsByEnrollmentNumber(enrollment)) {
            throw new IllegalArgumentException(
                    "Enrollment number is already assigned to an account");
        }

        user.setEnrollmentNumber(registry.getEnrollmentNumber());
        user.setVerificationStatus("VERIFIED");

        return userRepository.save(user);
    }
}
