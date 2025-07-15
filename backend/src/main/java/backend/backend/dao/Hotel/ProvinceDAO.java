package backend.backend.dao.Hotel;

import backend.backend.entity.Province;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProvinceDAO extends JpaRepository<Province, Integer> {
}