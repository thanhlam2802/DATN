package backend.backend.dao.Bus;

import backend.backend.entity.Bus;
import backend.backend.entity.BusRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BusDAO extends JpaRepository<Bus, Integer> {
    List<Bus> findBusByBusRoutes(List<BusRoute> busRoutes);
    List<Bus> findBusByOwner_Id(Integer ownerId);
}
