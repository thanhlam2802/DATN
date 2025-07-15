package backend.backend.dao.Hotel;

import backend.backend.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelDAO extends JpaRepository<Hotel, Integer>, JpaSpecificationExecutor<Hotel>, HotelDAOCustom {
}