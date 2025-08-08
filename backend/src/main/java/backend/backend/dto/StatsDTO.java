// File: backend/backend/dto/StatsDTO.java
package backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO chứa các chỉ số thống kê cho trang dashboard.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsDTO {
    // Các chỉ số tính theo khoảng thời gian được chọn
    private BigDecimal revenue;
    private long customers;
    private long bookingsCount;
    private long toursWithBookings;

    // Các chỉ số tính trên toàn bộ hệ thống
    private long systemTotalTours;
    private long systemActiveTours;

    /**
     * ✅ BƯỚC 1: THÊM CONSTRUCTOR NÀY
     * Constructor này được sử dụng bởi câu lệnh query JPQL trong BookingTourDAO
     * để tạo đối tượng StatsDTO từ kết quả truy vấn.
     * @param revenue Doanh thu (từ SUM)
     * @param customers Lượng khách (từ COUNT DISTINCT)
     * @param bookingsCount Số booking (từ COUNT)
     * @param toursWithBookings Số tour có booking (từ COUNT DISTINCT)
     */
    public StatsDTO(BigDecimal revenue, long customers, long bookingsCount, long toursWithBookings) {
        this.revenue = revenue;
        this.customers = customers;
        this.bookingsCount = bookingsCount;
        this.toursWithBookings = toursWithBookings;
    }
}