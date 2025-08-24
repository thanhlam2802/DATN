package backend.backend.dao.Bus;

import backend.backend.entity.RouteBusCategoryPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RouteBusCategoryPriceDAO extends JpaRepository<RouteBusCategoryPrice, Integer> {


        // ADD: Query với JOIN FETCH để load Route và Location data
        @Query("SELECT rbc FROM RouteBusCategoryPrice rbc " +
                "JOIN FETCH rbc.route r " +
                "JOIN FETCH r.originLocation " +
                "JOIN FETCH r.destinationLocation " +
                "JOIN FETCH rbc.busCategory")
        List<RouteBusCategoryPrice> findAllWithDetails();

        // Existing methods...
        @Query("SELECT rbc FROM RouteBusCategoryPrice rbc " +
                "WHERE rbc.route.id = :routeId " +
                "AND rbc.busCategory.id = :busCategoryId " +
                "AND :currentDate BETWEEN rbc.validFrom AND rbc.validTo")
        List<RouteBusCategoryPrice> findActivePriceRule(
                @Param("routeId") Integer routeId,
                @Param("busCategoryId") Integer busCategoryId,
                @Param("currentDate") LocalDate currentDate
        );

        // ADD: findById with details
        @Query("SELECT rbc FROM RouteBusCategoryPrice rbc " +
                "JOIN FETCH rbc.route r " +
                "JOIN FETCH r.originLocation " +
                "JOIN FETCH r.destinationLocation " +
                "JOIN FETCH rbc.busCategory " +
                "WHERE rbc.id = :id")
        Optional<RouteBusCategoryPrice> findByIdWithDetails(@Param("id") Integer id);

        void deleteByRoute_Id(Integer routeId);
        
        // ✅ THÊM MỚI: Tìm prices theo ownerId và routeId
        @Query("SELECT rbc FROM RouteBusCategoryPrice rbc " +
               "JOIN FETCH rbc.route r " +
               "JOIN FETCH r.originLocation " +
               "JOIN FETCH r.destinationLocation " +
               "JOIN FETCH rbc.busCategory " +
               "INNER JOIN BusRoute br ON br.route.id = r.id " +
               "INNER JOIN Bus b ON br.bus.id = b.id " +
               "WHERE b.owner.id = :ownerId AND r.id = :routeId")
        List<RouteBusCategoryPrice> findByOwnerIdAndRoute(
                @Param("ownerId") Integer ownerId, 
                @Param("routeId") Integer routeId
        );
        
        // ✅ THÊM MỚI: Tìm giá thấp nhất cho route
        @Query("SELECT MIN(rbc.basePrice) FROM RouteBusCategoryPrice rbc " +
               "WHERE rbc.route.id = :routeId " +
               "AND :currentDate BETWEEN rbc.validFrom AND rbc.validTo")
        Optional<java.math.BigDecimal> findMinPriceByRouteId(
                @Param("routeId") Integer routeId,
                @Param("currentDate") LocalDate currentDate
        );
}