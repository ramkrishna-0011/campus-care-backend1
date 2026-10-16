package CampusCare.complaint.dto;

import CampusCare.complaint.Complaint;
import java.time.LocalDateTime;

public record ComplaintResponse(
        Long id,
        String trackingId,
        String title,
        String category,
        String department,
        String description,
        String priority,
        String status,
        String type,
        String staffResponse,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ComplaintResponse from(Complaint complaint) {
        return new ComplaintResponse(
                complaint.getId(),
                complaint.getTrackingId(),
                complaint.getTitle(),
                complaint.getCategory(),
                complaint.getDepartment(),
                complaint.getDescription(),
                complaint.getPriority(),
                complaint.getStatus(),
                complaint.getType(),
                complaint.getStaffResponse(),
                complaint.getCreatedAt(),
                complaint.getUpdatedAt()
        );
    }
}