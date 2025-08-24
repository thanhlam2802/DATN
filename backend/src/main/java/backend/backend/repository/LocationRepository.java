package backend.backend.repository;

import backend.backend.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    
    Optional<Location> findByName(String name);
    
    @Query("SELECT l FROM Location l WHERE l.name LIKE %:name%")
    List<Location> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT l FROM Location l WHERE l.provinceCity LIKE %:provinceName%")
    List<Location> findByProvinceNameContaining(@Param("provinceName") String provinceName);
    
    // Removed findByProvinceId as Location entity doesn't have provinceId field
}
