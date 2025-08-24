package backend.backend.repository;

import backend.backend.entity.BusBooking;
import backend.backend.entity.BusSlot;
import backend.backend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BusBookingRepository extends JpaRepository<BusBooking, Integer> {
    
    List<BusBooking> findByBusSlot(BusSlot busSlot);
    
    List<BusBooking> findByCustomer(Customer customer);
    
    @Query("SELECT bb FROM BusBooking bb WHERE bb.bookingDate BETWEEN :startDate AND :endDate")
    List<BusBooking> findByBookingDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(bb) FROM BusBooking bb WHERE bb.bookingDate BETWEEN :startDate AND :endDate")
    long countByBookingDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    BusBooking findByBookingReference(String bookingReference);
    
    List<BusBooking> findByStatus(String status);
    
    @Query("SELECT bb FROM BusBooking bb WHERE bb.busSlot.owner.id = :ownerId")
    List<BusBooking> findByOwnerId(@Param("ownerId") Integer ownerId);
}
