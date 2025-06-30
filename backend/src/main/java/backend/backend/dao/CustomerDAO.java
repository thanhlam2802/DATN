package backend.backend.dao;

import backend.backend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerDAO extends JpaRepository<Customer, Integer> {
    List<Customer> findByBookingId(Long bookingId);
} 