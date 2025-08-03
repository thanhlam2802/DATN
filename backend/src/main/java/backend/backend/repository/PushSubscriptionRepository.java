package backend.backend.repository;

import backend.backend.entity.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {
    
    List<PushSubscription> findByUserIdAndIsActiveTrue(Long userId);
    
    Optional<PushSubscription> findByEndpointAndIsActiveTrue(String endpoint);
    
    @Query("SELECT ps FROM PushSubscription ps WHERE ps.user.id = :userId AND ps.endpoint = :endpoint AND ps.isActive = true")
    Optional<PushSubscription> findByUserIdAndEndpoint(@Param("userId") Long userId, @Param("endpoint") String endpoint);
    
    @Query("SELECT ps FROM PushSubscription ps WHERE ps.isActive = true")
    List<PushSubscription> findAllActive();
    
    @Query("SELECT ps FROM PushSubscription ps " +
           "JOIN ps.user u " +
           "JOIN u.userRoles ur " +
           "JOIN ur.role r " +
           "WHERE ps.isActive = true " +
           "AND r.name IN ('SUPER_ADMIN', 'ADMIN_BUSES', 'ADMIN_FLIGHTS', 'ADMIN_HOTELS', 'ADMIN_TOURS')")
    List<PushSubscription> findAllActiveAdminSubscriptions();
    
    @Query("SELECT ps FROM PushSubscription ps " +
           "JOIN ps.user u " +
           "JOIN u.userRoles ur " +
           "JOIN ur.role r " +
           "WHERE ps.isActive = true " +
           "AND r.name IN ('ADMIN_HOTELS', 'HOTEL_SUPPLIER')")
    List<PushSubscription> findAllActiveHotelAdminSubscriptions();
    
    void deleteByUserIdAndEndpoint(Long userId, String endpoint);
} 