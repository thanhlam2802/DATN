package backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightImageDto {
    private Long id;
    private Integer flightId;
    private Integer imageId;
    private String imageUrl;
    private String altText;
    private LocalDateTime uploadedAt;
    private Integer displayOrder;
    private Boolean isPrimary;
} 