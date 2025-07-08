package backend.backend.dao.Hotel;

import backend.backend.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenityDAO extends JpaRepository<Amenity, Integer> {
    
}