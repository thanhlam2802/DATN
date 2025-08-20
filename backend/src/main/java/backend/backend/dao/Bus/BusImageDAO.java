package backend.backend.dao.Bus;

import backend.backend.entity.BusImage;
import backend.backend.entity.BusImageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusImageDAO extends JpaRepository<BusImage, BusImageId> { // Phải là BusImageId
    List<BusImage> findById_BusId(Integer busId);
}