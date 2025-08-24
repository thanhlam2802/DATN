package backend.backend.repository;

import backend.backend.entity.Route;
import backend.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import backend.backend.entity.enumBus.RouteStatus;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Integer> {
    
    List<Route> findByOwner(User owner);
    
    @Query("SELECT r FROM Route r WHERE r.owner.id = :ownerId")
    List<Route> findByOwnerId(@Param("ownerId") Integer ownerId);
    
    @Query("SELECT r FROM Route r WHERE r.originLocation.name LIKE %:origin%")
    List<Route> findByOriginLocationNameContaining(@Param("origin") String origin);
    
    @Query("SELECT r FROM Route r WHERE r.destinationLocation.name LIKE %:destination%")
    List<Route> findByDestinationLocationNameContaining(@Param("destination") String destination);
    
    // Removed findByStatus as Route entity doesn't have status field
    
    @Query("SELECT r FROM Route r WHERE r.originLocation.id = :originId AND r.destinationLocation.id = :destinationId")
    List<Route> findByOriginAndDestination(@Param("originId") Integer originId, @Param("destinationId") Integer destinationId);
    
    @Query("SELECT r FROM Route r WHERE r.owner IS NOT NULL")
    List<Route> findActiveRoutes();
}
