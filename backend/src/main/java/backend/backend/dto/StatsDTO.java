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

    private BigDecimal revenue;
    private long customers;
    private long bookingsCount;
    private long toursWithBookings;
    private long systemTotalTours;
    private long systemActiveTours;

    /**
     * Thêm constructor này để phục vụ cho câu lệnh query JPQL trong Repository.
     * Hibernate sẽ sử dụng constructor này để tạo đối tượng từ kết quả query.
     *
     * @param revenue Doanh thu (từ SUM)
     * @param customers Số lượng khách hàng (từ COUNT)
     * @param bookingsCount Tổng số booking (từ COUNT)
     * @param toursWithBookings Số tour có booking (từ COUNT)
     */
    public StatsDTO(BigDecimal revenue, long customers, long bookingsCount, long toursWithBookings) {
        this.revenue = revenue;
        this.customers = customers;
        this.bookingsCount = bookingsCount;
        this.toursWithBookings = toursWithBookings;
    }
}