package backend.backend.entity;



import lombok.*;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Data
@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "alt_text", length = 255)
    private String altText;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();
    
    /**
     * Thêm trường này để lưu public_id từ Cloudinary.
     * Rất quan trọng cho việc xóa hoặc cập nhật ảnh.
     */
    @Column(name = "public_id")
    private String publicId;
}