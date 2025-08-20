// File: backend/backend/dto/TopTourDTO.java
package backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopTourDTO {
    private String tourName;
    private long bookingCount;
    private BigDecimal totalRevenue;
}