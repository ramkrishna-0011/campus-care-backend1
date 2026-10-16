
package CampusCare.verification;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRegistryRepository
        extends JpaRepository<StudentRegistry, Long> {

    Optional<StudentRegistry> findByEnrollmentNumberAndEmailAndActiveTrue(
            String enrollmentNumber,
            String email
    );
}
