package backend.backend.dto.Hotel;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

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
    private String sortBy = "popular";
    private int page = 0;
    private int size = 12;
}