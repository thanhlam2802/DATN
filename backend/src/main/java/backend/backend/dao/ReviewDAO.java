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

    @Query("SELECT COUNT(r.id) FROM Review r WHERE r.entityType = 'Hotel'")
    Long countTotalHotelReviews();

    @Query(value = "SELECT COUNT(*) FROM reviews WHERE entity_type = 'Hotel' AND CAST(created_at AS DATE) = CAST(GETDATE() AS DATE)", nativeQuery = true)
    Long countTotalHotelReviewsToday();

    @Query(value = "SELECT COUNT(*) FROM reviews WHERE entity_type = 'Hotel' AND CAST(created_at AS DATE) = CAST(DATEADD(DAY, -1, GETDATE()) AS DATE)", nativeQuery = true)
    Long countTotalHotelReviewsYesterday();

    @Query(value = "SELECT COUNT(*) FROM reviews WHERE entity_type = 'Hotel' AND CAST(created_at AS DATE) >= CAST(DATEADD(DAY, -7, GETDATE()) AS DATE)", nativeQuery = true)
    Long countTotalHotelReviewsLast7Days();

    @Query(value = "SELECT COUNT(*) FROM reviews WHERE entity_type = 'Hotel' AND YEAR(created_at) = YEAR(GETDATE()) AND MONTH(created_at) = MONTH(GETDATE())", nativeQuery = true)
    Long countTotalHotelReviewsThisMonth();

    @Query(value = "SELECT COUNT(*) FROM reviews WHERE entity_type = 'Hotel' AND YEAR(created_at) = YEAR(DATEADD(MONTH, -1, GETDATE())) AND MONTH(created_at) = MONTH(DATEADD(MONTH, -1, GETDATE()))", nativeQuery = true)
    Long countTotalHotelReviewsLastMonth();

    @Query(value = "SELECT COUNT(*) FROM reviews WHERE entity_type = 'Hotel' AND CAST(created_at AS DATE) = CAST(DATEADD(DAY, -2, GETDATE()) AS DATE)", nativeQuery = true)
    Long countTotalHotelReviews2DaysAgo();

    @Query(value = "SELECT COUNT(*) FROM reviews WHERE entity_type = 'Hotel' AND CAST(created_at AS DATE) >= CAST(DATEADD(DAY, -14, GETDATE()) AS DATE) AND CAST(created_at AS DATE) < CAST(DATEADD(DAY, -7, GETDATE()) AS DATE)", nativeQuery = true)
    Long countTotalHotelReviewsPrevious7Days();

    @Query(value = "SELECT COUNT(*) FROM reviews WHERE entity_type = 'Hotel' AND YEAR(created_at) = YEAR(DATEADD(MONTH, -2, GETDATE())) AND MONTH(created_at) = MONTH(DATEADD(MONTH, -2, GETDATE()))", nativeQuery = true)
    Long countTotalHotelReviews2MonthsAgo();

    default Long countTotalHotelReviewsByPeriod(String period) {
        switch (period) {
            case "today":
                return countTotalHotelReviewsToday();
            case "yesterday":
                return countTotalHotelReviewsYesterday();
            case "last_7_days":
                return countTotalHotelReviewsLast7Days();
            case "this_month":
                return countTotalHotelReviewsThisMonth();
            case "last_month":
                return countTotalHotelReviewsLastMonth();
            case "2_days_ago":
                return countTotalHotelReviews2DaysAgo();
            case "previous_7_days":
                return countTotalHotelReviewsPrevious7Days();
            case "2_months_ago":
                return countTotalHotelReviews2MonthsAgo();
            default:
                return countTotalHotelReviewsThisMonth();
        }
    }
}