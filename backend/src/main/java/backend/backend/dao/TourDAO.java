package backend.backend.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import backend.backend.dto.TourDetailAdminDTO;
import backend.backend.entity.Tour;
import backend.backend.entity.TourStatus;
import backend.backend.entity.User;

@Repository
public interface TourDAO  extends JpaRepository<Tour, Long> ,JpaSpecificationExecutor<Tour>{

	List<Tour> findByOwner_Id(Integer userId);
	
	 long countByOwnerId(Long ownerId);
	    long countByOwnerIdAndStatus(Long ownerId, TourStatus status);
    
	    /**
	     * Tự động tạo query: "SELECT t FROM Tour t WHERE t.owner.id = ?1"
	     * @param ownerId ID của người dùng (chủ tour)
	     * @return Danh sách các tour thuộc về người dùng đó.
	     */
	    List<Tour> findByOwnerId(Integer ownerId);

		long countByOwner(User vendorUser);
		
		
	    @Query("SELECT t.name FROM Tour t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	    List<String> findTourNamesByKeyword(String keyword, Pageable pageable);

	    
	    @Query("SELECT DISTINCT t.destination FROM Tour t WHERE LOWER(t.destination) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	    List<String> findDistinctDestinationsByKeyword(String keyword, Pageable pageable);

		



	
	
}
