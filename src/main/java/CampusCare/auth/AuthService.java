package CampusCare.auth;

import CampusCare.auth.dto.AuthResponse;
import CampusCare.auth.dto.LoginRequest;
import CampusCare.auth.dto.RegisterRequest;
import CampusCare.user.User;
import CampusCare.user.UserRepository;
import CampusCare.verification.FacultyRegistry;
import CampusCare.verification.FacultyRegistryRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FacultyRegistryRepository facultyRegistryRepository;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            FacultyRegistryRepository facultyRegistryRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.facultyRegistryRepository = facultyRegistryRepository;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Registration data is required");
        }

        String name = required(
                request.getName(), "Name is required");

        String email = required(
                request.getEmail(), "Email is required")
                .toLowerCase(Locale.ROOT);

        String password = required(
                request.getPassword(), "Password is required");

        String role = required(
                request.getRole(), "Role is required")
                .toUpperCase(Locale.ROOT);

        String department = required(
                request.getDepartment(), "Department is required");

        String phoneNumber = required(
                request.getPhoneNumber(), "Phone number is required");

        if (name.length() > 100
                || email.length() > 150
                || password.length() < 8
                || password.length() > 100
                || department.length() > 100) {

            throw new IllegalArgumentException(
                    "Invalid registration details");
        }

        if (!phoneNumber.matches("[6-9][0-9]{9}")) {
            throw new IllegalArgumentException(
                    "Enter a valid 10-digit Indian mobile number");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "This email is already registered");
        }

        if (!role.equals("STUDENT") && !role.equals("FACULTY")) {
            throw new IllegalArgumentException(
                    "Public registration allows STUDENT or FACULTY only");
        }

        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setDepartment(department);
        user.setPhoneNumber(phoneNumber);

        if (role.equals("STUDENT")) {

            // Student details are no longer checked against student_registry.
            String enrollment = required(
                    request.getEnrollmentNumber(),
                    "Enrollment number is required");

            String course = required(
                    request.getCourse(),
                    "Course is required");

            String branch = required(
                    request.getBranch(),
                    "Branch is required");

            Integer semester = request.getSemester();

            if (enrollment.length() > 50) {
                throw new IllegalArgumentException(
                        "Enrollment number is too long");
            }

            if (course.length() > 100 || branch.length() > 100) {
                throw new IllegalArgumentException(
                        "Course or branch name is too long");
            }

            if (semester == null || semester < 1 || semester > 12) {
                throw new IllegalArgumentException(
                        "Enter a valid semester between 1 and 12");
            }

            if (userRepository.existsByEnrollmentNumber(enrollment)) {
                throw new IllegalArgumentException(
                        "This enrollment number is already registered");
            }

            user.setEnrollmentNumber(enrollment);
            user.setCourse(course);
            user.setBranch(branch);
            user.setSemester(semester);

            // Students can register without admin verification.
            user.setVerificationStatus("VERIFIED");

        } else {

            // Keep the existing faculty verification process.
            String facultyId = required(
                    request.getFacultyId(),
                    "Faculty ID is required");

            FacultyRegistry registry = facultyRegistryRepository
                    .findByFacultyIdAndEmailAndActiveTrue(
                            facultyId, email)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Faculty ID and email do not match approved college records"));

            if (userRepository.existsByFacultyId(
                    registry.getFacultyId())) {

                throw new IllegalArgumentException(
                        "This Faculty ID is already registered");
            }

            user.setFacultyId(registry.getFacultyId());
            user.setVerificationStatus("PENDING_APPROVAL");
        }

        User saved = userRepository.save(user);

        String message = role.equals("STUDENT")
                ? "Student registration successful. You can now login."
                : "Faculty registration submitted. Admin approval is required.";

        return new AuthResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getRole(),
                message
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        if (request == null
                || request.getEmail() == null
                || request.getEmail().isBlank()
                || request.getPassword() == null
                || request.getPassword().isBlank()) {

            throw new IllegalArgumentException(
                    "Email and password are required");
        }

        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(), user.getPassword())) {

            throw new IllegalArgumentException(
                    "Invalid email or password");
        }

        String role = user.getRole();
        String status = user.getVerificationStatus();

        // Students do not require admin verification to login.
        // Faculty still requires the existing approval process.
        if (!"STUDENT".equalsIgnoreCase(role)
                && !"VERIFIED".equals(status)) {

            throw new IllegalArgumentException(
                    "PENDING_APPROVAL".equals(status)
                            ? "Your Faculty account is awaiting Admin approval"
                            : "Your account has not been verified");
        }

        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                "Login credentials verified"
        );
    }

    private String required(String value, String message) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }

        return value.trim();
    }
}