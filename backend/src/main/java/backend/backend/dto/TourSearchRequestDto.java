package backend.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourSearchRequestDto {

    // --- Tham số lọc ---
    private Optional<String> keyword = Optional.empty();
    
    // TRƯỜNG MỚI: Lọc theo điểm đến cụ thể
    private Optional<String> destination = Optional.empty();
    
    // TRƯỜNG MỚI: Lọc theo ngày khởi hành
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // Giúp Spring chuyển đổi chuỗi "YYYY-MM-DD" thành LocalDate
    private Optional<LocalDate> startDate = Optional.empty();
    
    private Optional<BigDecimal> minPrice = Optional.empty();
    private Optional<BigDecimal> maxPrice = Optional.empty();
    private Optional<Double> minRating = Optional.empty();
    private Optional<List<String>> tags = Optional.empty();

    // --- Tham số sắp xếp và phân trang ---
    private String sortBy = "popular";
    private int page = 0;
    private int size = 12;
}