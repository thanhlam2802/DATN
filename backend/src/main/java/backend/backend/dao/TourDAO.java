package backend.backend.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import backend.backend.dto.TourDetailAdminDTO;
import backend.backend.entity.Tour;

@Repository
public interface TourDAO  extends JpaRepository<Tour, Long> ,JpaSpecificationExecutor<Tour>{

	List<Tour> findByOwner_Id(Integer userId);



	
	
}
