package backend.backend.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

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

}
