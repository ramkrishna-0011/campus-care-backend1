package CampusCare.complaint;

import CampusCare.complaint.dto.ComplaintResponse;
import CampusCare.complaint.dto.CreateComplaintRequest;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    public ResponseEntity<?> create(
            Authentication authentication,
            @Valid @RequestBody CreateComplaintRequest request) {

        if (!isLoggedIn(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please sign in first"));
        }

        ComplaintResponse response = complaintService.create(
                authentication.getName(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getMyComplaints(
            Authentication authentication) {

        if (!isLoggedIn(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please sign in first"));
        }

        List<ComplaintResponse> complaints =
                complaintService.getMyComplaints(
                        authentication.getName());

        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/track/{trackingId}")
    public ResponseEntity<?> getByTrackingId(
            Authentication authentication,
            @PathVariable String trackingId) {

        if (!isLoggedIn(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please sign in first"));
        }

        ComplaintResponse response =
                complaintService.getByTrackingId(
                        authentication.getName(), trackingId);

        return ResponseEntity.ok(response);
    }

    private boolean isLoggedIn(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getName() != null
                && !authentication.getName().equals("anonymousUser");
    }
}