package backend.backend.repository;

import backend.backend.entity.Bus;
import backend.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusRepository extends JpaRepository<Bus, Integer> {
    
    List<Bus> findByOwner(User owner);
    
    @Query("SELECT b FROM Bus b WHERE b.owner.id = :ownerId")
    List<Bus> findByOwnerId(@Param("ownerId") Integer ownerId);
    
    List<Bus> findByCategoryId(Integer categoryId);
    
    @Query("SELECT b FROM Bus b WHERE b.licensePlate = :licensePlate")
    Bus findByLicensePlate(@Param("licensePlate") String licensePlate);
    
    @Query("SELECT b FROM Bus b WHERE b.owner IS NOT NULL")
    List<Bus> findActiveBuses();
}
