package backend.backend.dao.Bus;

import backend.backend.entity.RouteBusCategoryPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RouteBusCategoryPriceDAO extends JpaRepository<RouteBusCategoryPrice, Integer> {
    // Tìm quy tắc giá hiệu lực vào một ngày cụ thể cho cặp tuyến/loại xe
    @Query("SELECT rbc FROM RouteBusCategoryPrice rbc " +
            "WHERE rbc.route.id = :routeId " +
            "AND rbc.busCategory.id = :busCategoryId " +
            "AND :currentDate BETWEEN rbc.validFrom AND rbc.validTo")
    List<RouteBusCategoryPrice> findActivePriceRule(
            @Param("routeId") Integer routeId,
            @Param("busCategoryId") Integer busCategoryId,
            @Param("currentDate") LocalDate currentDate
    );
}