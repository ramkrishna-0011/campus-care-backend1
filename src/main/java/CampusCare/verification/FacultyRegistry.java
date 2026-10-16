
package CampusCare.verification;

import jakarta.persistence.*;

@Entity
@Table(name = "faculty_registry")
public class FacultyRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "faculty_id",
            nullable = false, unique = true, length = 50)
    private String facultyId;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private boolean active = true;

    public FacultyRegistry() {
    }

    public Long getId() {
        return id;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
