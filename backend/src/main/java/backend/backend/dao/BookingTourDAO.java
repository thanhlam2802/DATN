package backend.backend.dao;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import backend.backend.dto.StatsDTO;
import backend.backend.dto.TopTourDTO;
import backend.backend.entity.BookingTour;
import backend.backend.entity.User;



public interface BookingTourDAO extends JpaRepository<BookingTour, Integer>,JpaSpecificationExecutor<BookingTour> {

	List<BookingTour> findByOrderId(Integer id);
	
	/**
     * Finds all tour bookings associated with a specific user by traversing
     * through the Order entity.
     * @param user The user entity to find bookings for.
     * @return A list of tour bookings for the given user.
     */
    List<BookingTour> findByOrder_User(User user);
    
 

 // Trong BookingTourRepository.java
    @Query("SELECT bt FROM BookingTour bt " +
           "JOIN FETCH bt.departure d " +
           "JOIN FETCH d.tour t " +
           "WHERE bt.order.user.id = :customerId " +
           "AND d.departureDate <= :currentDate " +
           "AND bt.order.status = 'PAID'")
    List<BookingTour> findCompletedBookingsByCustomerId(@Param("customerId") Integer customerId, @Param("currentDate") LocalDate currentDate);
    
    
    /**
     * Lấy các chỉ số thống kê chính trong một khoảng thời gian cho một người dùng cụ thể.
     *
     * @param userId ID của người dùng (chủ tour).
     * @param startDate Ngày bắt đầu (có thể null).
     * @param endDate Ngày kết thúc (có thể null).
     * @return Một đối tượng StatsDTO chứa dữ liệu đã tính toán.
     */
    @Query("SELECT new backend.backend.dto.StatsDTO(" +
           "COALESCE(SUM(bt.totalPrice), 0), " +
           "COUNT(DISTINCT bt.email), " +
           "COUNT(bt.id), " +
           "COUNT(DISTINCT bt.departure.tour.id)) " +
           "FROM BookingTour bt " +
           // SỬA LẠI: Dùng tên trường "owner" đã được xác nhận
           "WHERE bt.departure.tour.owner.id = :userId " +
           "AND bt.order.status IN ('PAID', 'CONFIRMED') " +
           "AND (:startDate IS NULL OR bt.bookingDate >= :startDate) " +
           "AND (:endDate IS NULL OR bt.bookingDate <= :endDate)")
    StatsDTO getStatsByDateRange(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
   
    /**
     * Tìm các tour bán chạy nhất trong một khoảng thời gian cho một người dùng cụ thể.
     *
     * @param userId ID của người dùng (chủ tour).
     * @param startDate Ngày bắt đầu (có thể null).
     * @param endDate Ngày kết thúc (có thể null).
     * @param pageable Đối tượng phân trang để giới hạn số lượng kết quả.
     * @return Danh sách các tour bán chạy nhất.
     */
    @Query("SELECT new backend.backend.dto.TopTourDTO(" +
            "d.tour.name, " +
            "COUNT(bt.id), " +
            "SUM(bt.totalPrice)) " +
            "FROM BookingTour bt JOIN bt.departure d " +
            // SỬA LẠI: Dùng tên trường "owner" đã được xác nhận
            "WHERE d.tour.owner.id = :userId " +
            "AND bt.order.status IN ('PAID', 'CONFIRMED') " +
            "AND (:startDate IS NULL OR bt.bookingDate >= :startDate) " +
            "AND (:endDate IS NULL OR bt.bookingDate <= :endDate) " +
            "GROUP BY d.tour.name " +
            "ORDER BY SUM(bt.totalPrice) DESC")
    List<TopTourDTO> findTopSellingTours(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable
    );
    /**
     * Lấy danh sách các booking đã thanh toán và phân trang theo khoảng thời gian.
     * Controller sẽ sử dụng JpaSpecificationExecutor để truyền điều kiện lọc vào hàm findAll.
     * Việc kế thừa JpaSpecificationExecutor giúp repository có sẵn hàm `findAll(Specification<T> spec, Pageable pageable)`.
     * Service sẽ tạo Specification từ `startDate`, `endDate` và `status` rồi gọi hàm này.
     * Không cần định nghĩa thêm, chỉ cần kế thừa JpaSpecificationExecutor là đủ.
     */
     // Ví dụ cách gọi trong Service:
     // Specification<BookingTour> spec = createSpecification(startDate, endDate);
     // return bookingTourRepository.findAll(spec, pageable);
   
}