package CampusCare.complaint;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComplaintRepository
        extends JpaRepository<Complaint, Long> {

    boolean existsByTrackingId(String trackingId);

    List<Complaint> findByStudent_IdOrderByCreatedAtDesc(
            Long studentId);

    Optional<Complaint> findByTrackingIdAndStudent_Id(
            String trackingId,
            Long studentId);
}