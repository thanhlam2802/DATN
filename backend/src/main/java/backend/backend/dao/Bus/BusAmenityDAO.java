package backend.backend.dao.Bus;

import backend.backend.entity.BusAmenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusAmenityDAO extends JpaRepository<BusAmenity, Integer> {

    Optional<BusAmenity> findById(@Argument Integer id);
    Optional<BusAmenity> findByName(@Argument String name);

}
