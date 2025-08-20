package backend.backend.entity;


import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private Integer entityId;

    @Column(nullable = false)
    private Short rating;

    @Lob
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Lob
    @Column(name = "admin_response")
    private String adminResponse;
    
    @Column(name = "admin_response_at")
    private LocalDateTime adminResponseAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_response_by")
    private User adminResponseBy;
}