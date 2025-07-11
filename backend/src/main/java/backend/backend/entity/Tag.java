package backend.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Data
@Entity
@NoArgsConstructor    // <-- TỰ ĐỘNG TẠO CONSTRUCTOR KHÔNG THAM SỐ
@AllArgsConstructor
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    // Trong file Tag.java
    @JsonIgnore
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Tour> tours = new HashSet<>();

    // Tự động gán ngày giờ khi tạo mới
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Constructor chỉ nhận vào 'name'.
     * Dòng này sẽ sửa trực tiếp lỗi "The constructor Tag(String) is undefined".
     * @param name Tên của tag mới
     */
    public Tag(String name) {
        this.name = name;
    }

}