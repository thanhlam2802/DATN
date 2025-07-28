package backend.backend.dao.Bus;

import backend.backend.entity.BusRoute;
import backend.backend.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteDAO extends JpaRepository<Route, Integer> {

    @Query("SELECT r FROM Route r JOIN FETCH r.originLocation JOIN FETCH r.destinationLocation")
    List<Route> findAllWithLocations();

    // Thêm phương thức để tải Route theo ID và JOIN FETCH Location
    @Query("SELECT r FROM Route r JOIN FETCH r.originLocation JOIN FETCH r.destinationLocation WHERE r.id = :id")
    Optional<Route> findByIdWithLocations(@Param("id") Integer id);



    Optional<Route> findByOriginLocation_IdAndDestinationLocation_Id(Integer originLocationId, Integer destinationLocationId);

}
