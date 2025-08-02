package backend.backend.dao;

import backend.backend.entity.FlightBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightBookingDAO extends JpaRepository<FlightBooking, Integer> {
	
	
    List<FlightBooking> findByCustomerId(Integer customerId);
    List<FlightBooking> findByFlightSlotId(Integer slotId);
    Optional<FlightBooking> findById(Integer id);
    @Query("SELECT COUNT(fb) FROM FlightBooking fb WHERE fb.flightSlot.flight.id = :flightId")
    int countSoldSeatsByFlightId(@Param("flightId") Integer flightId);
    @Query("SELECT COALESCE(SUM(fb.flightSlot.price), 0) "
            + "FROM FlightBooking fb "
            + "WHERE fb.flightSlot.flight.id = :flightId")
    Double sumRevenueByFlightId(@Param("flightId") Integer flightId);
    
    // Thống kê theo tháng
    @Query("SELECT COUNT(fb) FROM FlightBooking fb WHERE YEAR(fb.bookingDate) = :year AND MONTH(fb.bookingDate) = :month")
    Long countBookingsByMonth(@Param("year") Integer year, @Param("month") Integer month);
    
    @Query("SELECT COALESCE(SUM(fb.totalPrice), 0) FROM FlightBooking fb WHERE YEAR(fb.bookingDate) = :year AND MONTH(fb.bookingDate) = :month")
    Double sumRevenueByMonth(@Param("year") Integer year, @Param("month") Integer month);
    
    // Thống kê đặt chỗ theo điểm đến (tối ưu hóa)
    @Query("SELECT fb.flightSlot.flight.arrivalAirport.name as destination, COUNT(fb) as bookingCount " +
           "FROM FlightBooking fb " +
           "WHERE YEAR(fb.bookingDate) = :year AND MONTH(fb.bookingDate) = :month " +
           "GROUP BY fb.flightSlot.flight.arrivalAirport.name " +
           "ORDER BY bookingCount DESC")
    List<Object[]> getBookingsByDestinationOptimized(@Param("year") Integer year, @Param("month") Integer month);
    
    // Thống kê doanh thu theo nhóm ghế (tối ưu hóa)
    @Query("SELECT " +
           "CASE WHEN fb.flightSlot.isBusiness = true THEN 'Business' ELSE 'Economy' END as seatClass, " +
           "COALESCE(SUM(fb.totalPrice), 0) as revenue " +
           "FROM FlightBooking fb " +
           "WHERE YEAR(fb.bookingDate) = :year AND MONTH(fb.bookingDate) = :month " +
           "GROUP BY fb.flightSlot.isBusiness " +
           "ORDER BY revenue DESC")
    List<Object[]> getRevenueBySeatClassOptimized(@Param("year") Integer year, @Param("month") Integer month);
    
    // Method này sẽ được implement trong service để tính toán phức tạp hơn
    // @Query("SELECT COALESCE(AVG(CASE WHEN totalSlots > 0 THEN (bookedSlots * 100.0 / totalSlots) ELSE 0 END), 0) " +
    //        "FROM (SELECT f.id, " +
    //        "COUNT(fs) as totalSlots, " +
    //        "COUNT(fb) as bookedSlots " +
    //        "FROM Flight f " +
    //        "LEFT JOIN f.flightSlots fs " +
    //        "LEFT JOIN FlightBooking fb ON fb.flightSlot.id = fs.id " +
    //        "WHERE YEAR(f.createdAt) = :year AND MONTH(f.createdAt) = :month " +
    //        "GROUP BY f.id)")
    // Double getAverageOccupancyRateByMonth(@Param("year") Integer year, @Param("month") Integer month);
    
	List<FlightBooking> findByOrderId(Integer id);
} 