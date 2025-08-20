package backend.backend.dto.Hotel;

import lombok.Data;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class HotelSearchRequestDto {
    private String keyword = "";
    private Integer provinceId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkInDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkOutDate;

    private Integer numAdults;
    private Integer numChildren;

    private Short minStarRating;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private List<String> amenities;
    private String sortBy = "popular";
    private int page = 0;
    private int size = 12;
    private LocalDate createdAtFrom;
    private LocalDate createdAtTo;
    
    private String roomStatus;
}