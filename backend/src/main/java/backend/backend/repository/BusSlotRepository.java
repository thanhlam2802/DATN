package backend.backend.repository;

import backend.backend.entity.BusSlot;
import backend.backend.entity.Route;
import backend.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BusSlotRepository extends JpaRepository<BusSlot, Integer> {
    
    List<BusSlot> findByOwner(User owner);
    
    List<BusSlot> findByRoute(Route route);
    
    List<BusSlot> findBySlotDate(LocalDate slotDate);
    
    List<BusSlot> findBySlotDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<BusSlot> findByOwnerAndSlotDateBetween(User owner, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT bs FROM BusSlot bs WHERE bs.route = :route AND bs.slotDate > :date")
    List<BusSlot> findByRouteAndSlotDateAfter(@Param("route") Route route, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(bs) FROM BusSlot bs WHERE bs.route = :route AND bs.slotDate > :date")
    long countByRouteAndSlotDateAfter(@Param("route") Route route, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(bs) FROM BusSlot bs WHERE bs.owner = :owner AND bs.slotDate BETWEEN :startDate AND :endDate")
    long countByOwnerAndSlotDateBetween(@Param("owner") User owner, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    List<BusSlot> findByBusId(Integer busId);
    
    List<BusSlot> findByStatus(String status);
}
