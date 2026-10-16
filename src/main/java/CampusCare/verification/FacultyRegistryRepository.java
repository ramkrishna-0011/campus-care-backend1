
package CampusCare.verification;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FacultyRegistryRepository
        extends JpaRepository<FacultyRegistry, Long> {

    Optional<FacultyRegistry> findByFacultyIdAndEmailAndActiveTrue(
            String facultyId,
            String email
    );
}
