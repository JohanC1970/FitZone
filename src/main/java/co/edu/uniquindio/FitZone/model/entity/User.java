package co.edu.uniquindio.FitZone.model.entity;

import co.edu.uniquindio.FitZone.model.enums.DocumentType;
import co.edu.uniquindio.FitZone.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad User - Representa a un usuario del sistema.
 * Contiene información personal, de contacto y de autenticación.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;

    @Column(name = "document_number", nullable = false, unique = true, length = 50)
    private String documentNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "emergency_contact_name", nullable = false, length = 20)
    private String emergencyContactPhone;

    @Column(name = "medical_conditions", length = 255)
    private String medicalConditions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "principal_sede_id")
    private Sede principalSede;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;



    public User(Long idUser, String firstName, String lastName, String email, DocumentType documentType, String documentNumber, String password, String phoneNumber, LocalDate birthDate, String emergencyContactPhone, String medicalConditions, Sede principalSede, UserRole role, boolean isActive) {
        this.idUser = idUser;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.emergencyContactPhone = emergencyContactPhone;
        this.medicalConditions = medicalConditions;
        this.principalSede = principalSede;
        this.role = role;
        this.isActive = isActive;

    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


}
