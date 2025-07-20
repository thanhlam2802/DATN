package backend.backend.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourSearchRequestDto {

  
    private Optional<String> keyword = Optional.empty();
    private Optional<BigDecimal> minPrice = Optional.empty();
    private Optional<BigDecimal> maxPrice = Optional.empty();
    private Optional<Double> minRating = Optional.empty();

  
    private Optional<List<String>> tags = Optional.empty();

  
    private String sortBy = "popular"; 

   
    private int page = 0;
    private int size = 12;
}