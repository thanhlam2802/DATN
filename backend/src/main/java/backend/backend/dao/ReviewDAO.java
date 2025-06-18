package backend.backend.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import backend.backend.entity.Review;

public interface ReviewDAO  extends JpaRepository<Review, Integer>{
	/**
     * CẬP NHẬT: Tính rating trung bình cho một entity cụ thể với entityType là 'TOUR'.
     * Mệnh đề WHERE giờ sẽ lọc trên cả entity_id và entity_type.
     *
     * @param tourId ID của tour cần tính rating.
     * @return Rating trung bình.
     */
    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.entityId = :tourId AND r.entityType = 'TOUR'")
    Double getAverageRatingByTourId(@Param("tourId") Long tourId);

    /**
     * CẬP NHẬT: Đếm số lượng review cho một entity cụ thể với entityType là 'TOUR'.
     *
     * @param tourId ID của tour cần đếm review.
     * @return Tổng số review.
     */
    @Query("SELECT COUNT(r.id) FROM Review r WHERE r.entityId = :tourId AND r.entityType = 'TOUR'")
    Integer countByTourId(@Param("tourId") Long tourId);

    
    List<Review> findByEntityTypeAndEntityId(String entityType, Integer entityId);
    
    List<Review> findByUserId(Integer userId);
}
