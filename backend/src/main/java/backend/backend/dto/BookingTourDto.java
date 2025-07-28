package backend.backend.dto;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingTourDto {
    private Integer id;
    private Long tourId;
    private String tourName;
    private LocalDate departureDate;
    
  
    private int numberOfAdults;
    private int numberOfChildren;

    
    private BigDecimal adultPrice;
    private BigDecimal childPrice;

    private BigDecimal totalPrice;
    private Integer orderId;
}