package backend.backend.dto;


import lombok.Data;
import java.math.BigDecimal;

@Data
public class TourSearchRequestDto {
    // Từ SideBar (giả định)
    private String keyword;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Double minRating;
    private String category;

    // Từ dropdown sắp xếp
    // Giá trị mặc định là "popular" giống như trong state 'sortBy' của Vue
    private String sortBy = "popular";

    // Cho phân trang
    private int page = 0; // Trang bắt đầu từ 0
    private int size = 12; // Số kết quả mỗi trang
}