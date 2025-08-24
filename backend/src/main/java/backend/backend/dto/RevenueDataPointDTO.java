package backend.backend.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor 
public class RevenueDataPointDTO {
    private String label; // Ví dụ: "T2", "Tháng 1", "2024"
    private BigDecimal value; // Ví dụ: 12500000

   
}