package CampusCare.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRoleAndVerificationStatus(
            String role,
            String verificationStatus
    );

    boolean existsByEnrollmentNumber(String enrollmentNumber);

    boolean existsByFacultyId(String facultyId);

    // Dashboard statistics

    long countByRoleIgnoreCase(String role);

    long countByRoleIgnoreCaseAndVerificationStatusIgnoreCase(
            String role,
            String verificationStatus
    );

    long countByVerificationStatusIgnoreCase(String verificationStatus);
}