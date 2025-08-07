package backend.backend.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}