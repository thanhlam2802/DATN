package backend.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "supplier_applications")
public class SupplierApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(name = "service_type", nullable = false, length = 50) 
    private String serviceType;

    @Column(name = "business_name", nullable = false, length = 100) // <-- SỬA LỖI
    private String businessName;

    @Column(name = "tax_id", nullable = false, unique = true, length = 20) // <-- SỬA LỖI
    private String taxId;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "contact_person", nullable = false, length = 100) // <-- SỬA LỖI
    private String contactPerson;

    @Column(name = "business_phone", nullable = false, length = 15) // <-- SỬA LỖI
    private String businessPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ApplicationStatus status;

    @Column(name = "created_at", nullable = false, updatable = false) // <-- SỬA LỖI
    private LocalDateTime createdAt;

    @Column(name = "reviewed_at") // <-- SỬA LỖI
    private LocalDateTime reviewedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    
}