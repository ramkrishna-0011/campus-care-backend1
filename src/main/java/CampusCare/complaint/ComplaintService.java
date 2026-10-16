package CampusCare.complaint;

import CampusCare.complaint.dto.ComplaintResponse;
import CampusCare.complaint.dto.CreateComplaintRequest;
import CampusCare.user.User;
import CampusCare.user.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    public ComplaintService(
            ComplaintRepository complaintRepository,
            UserRepository userRepository) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ComplaintResponse create(
            String authenticatedEmail,
            CreateComplaintRequest request) {

        User student = userRepository.findByEmail(
                authenticatedEmail.trim().toLowerCase(Locale.ROOT)
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "User not found"));

        if (!"STUDENT".equalsIgnoreCase(student.getRole())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only students can submit complaints");
        }

        String type = normalize(request.getType());
        String priority = normalize(request.getPriority());

        if (!type.equals("COMPLAINT") && !type.equals("SUGGESTION")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Type must be COMPLAINT or SUGGESTION");
        }

        if (!priority.equals("LOW")
                && !priority.equals("MEDIUM")
                && !priority.equals("HIGH")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Priority must be LOW, MEDIUM or HIGH");
        }

        Complaint complaint = new Complaint();
        complaint.setTitle(request.getTitle().trim());
        complaint.setCategory(request.getCategory().trim());
        complaint.setDepartment(request.getDepartment().trim());
        complaint.setDescription(request.getDescription().trim());
        complaint.setType(type);
        complaint.setPriority(priority);
        complaint.setStatus("SUBMITTED");
        complaint.setStudent(student);
        complaint.setTrackingId(generateTrackingId());

        Complaint saved = complaintRepository.save(complaint);

        return ComplaintResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> getMyComplaints(
            String authenticatedEmail) {

        User student = userRepository.findByEmail(
                authenticatedEmail.trim().toLowerCase(Locale.ROOT)
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "User not found"));

        return complaintRepository
                .findByStudent_IdOrderByCreatedAtDesc(student.getId())
                .stream()
                .map(ComplaintResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ComplaintResponse getByTrackingId(
            String authenticatedEmail,
            String trackingId) {

        User student = userRepository.findByEmail(
                authenticatedEmail.trim().toLowerCase(Locale.ROOT)
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "User not found"));

        Complaint complaint = complaintRepository
                .findByTrackingIdAndStudent_Id(
                        trackingId, student.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Complaint not found"));

        return ComplaintResponse.from(complaint);
    }

    private String generateTrackingId() {
        String trackingId;

        do {
            trackingId = "CC-" + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase(Locale.ROOT);
        } while (complaintRepository.existsByTrackingId(trackingId));

        return trackingId;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value.trim().toUpperCase(Locale.ROOT);
    }
}