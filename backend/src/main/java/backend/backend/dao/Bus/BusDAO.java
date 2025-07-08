package backend.backend.dao.Bus;

import backend.backend.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusDAO extends JpaRepository<Bus, Integer> {
    List<Bus> findByCompanyName(String companyName);

    // Additional custom query methods can be defined here if needed
}
