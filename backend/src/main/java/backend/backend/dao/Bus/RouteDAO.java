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

    @Query("SELECT r FROM Route r LEFT JOIN FETCH r.owner JOIN FETCH r.originLocation JOIN FETCH r.destinationLocation")
    List<Route> findAllWithLocations();

    // Thêm phương thức để tải Route theo ID và JOIN FETCH Location
    @Query("SELECT r FROM Route r LEFT JOIN FETCH r.owner JOIN FETCH r.originLocation JOIN FETCH r.destinationLocation WHERE r.id = :id")
    Optional<Route> findByIdWithLocations(@Param("id") Integer id);



    Optional<Route> findByOriginLocation_IdAndDestinationLocation_Id(Integer originLocationId, Integer destinationLocationId);

    // ✅ UPDATED: Tìm Routes theo ownerId trực tiếp (Route có owner)
    @Query("SELECT r FROM Route r LEFT JOIN FETCH r.owner LEFT JOIN FETCH r.originLocation LEFT JOIN FETCH r.destinationLocation WHERE r.owner.id = :ownerId")
    List<Route> findByOwnerId(@Param("ownerId") Integer ownerId);

}
