package backend.backend.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor    // Rất quan trọng cho JPA
@AllArgsConstructor   // Rất tiện lợi khi khởi tạo
public class BusImageId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L; // Thêm serialVersionUID

    private Integer busId;
    private Integer imageId;
}
