package backend.backend.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.backend.entity.Review;

@Repository
public interface ReviewDAO extends JpaRepository<Review, Integer> {
	
	
	@Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.entityType = 'TOUR' AND r.entityId = :tourId")
    List<Review> findReviewsForTourWithUser(@Param("tourId") Integer tourId);

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.entityId = :tourId AND r.entityType = 'TOUR'")
    Double getAverageRatingByTourId(@Param("tourId") Long tourId);

    @Query("SELECT COUNT(r.id) FROM Review r WHERE r.entityId = :tourId AND r.entityType = 'TOUR'")
    Integer countByTourId(@Param("tourId") Long tourId);

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.entityType = :entityType AND r.entityId = :entityId")
    Double getAverageRatingByEntity(@Param("entityType") String entityType, @Param("entityId") Integer entityId);

    @Query("SELECT COUNT(r.id) FROM Review r WHERE r.entityType = :entityType AND r.entityId = :entityId")
    Integer countByEntity(@Param("entityType") String entityType, @Param("entityId") Integer entityId);

    List<Review> findByEntityTypeAndEntityId(String entityType, Integer entityId);

    List<Review> findByUserId(Integer userId);
    
    
  
   @Query("SELECT r FROM Review r WHERE r.entityType = :entityType AND r.entityId IN " +
          "(SELECT t.id FROM Tour t WHERE t.owner.id = :ownerId)")
   List<Review> findReviewsForToursByOwner(
           @Param("ownerId") Long ownerId,
           @Param("entityType") String entityType
   );
}