package backend.backend.dao.Bus;

import backend.backend.entity.BusRoute;
import backend.backend.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouteDAO extends JpaRepository<Route, Integer> {
    Optional<Route> findByOriginAndDestination(String origin, String destination);

}
