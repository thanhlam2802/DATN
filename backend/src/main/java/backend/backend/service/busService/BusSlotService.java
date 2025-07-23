package backend.backend.service.busService;

import backend.backend.dto.BusDTO.*;
import backend.backend.entity.enumBus.BusSlotStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BusSlotService {
    // CRUD Operations
    BusSlotResponse createBusSlot(CreateBusSlotRequest request);
    BusSlotResponse updateBusSlot(Integer id, UpdateBusSlotRequest request);
    void deleteBusSlot(Integer id);
    Optional<BusSlotResponse> findBusSlotById(Integer id);
    List<BusSlotResponse> findAllBusSlots();

    // Specific Queries
    List<BusSlotResponse> findBusSlotsByBusId(Integer busId);
    List<BusSlotResponse> findBusSlotsByRouteId(Integer routeId);
    List<BusSlotResponse> findBusSlotsByStatus(BusSlotStatus status);

    // Advanced Search for End-Users
    List<BusSlotResponse> searchBusSlots(
            String departureLocationId,
            String arrivalLocationId,
            LocalDate slotDate,
            Integer busCategoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minAvailableSeats,
            BusSlotStatus status
    );

    // --- Status Update Mutations (Legacy) ---
    BusSlotResponse activateBusSlot(Integer id);
    BusSlotResponse completeBusSlot(Integer id);
    BusSlotResponse cancelBusSlot(Integer id);

    // --- Real-time Management Mutations ---
    BusSlotResponse updateActualTimes(Integer id, UpdateActualTimesRequest request);
    BusSlotResponse quickStatusUpdate(Integer id, QuickStatusUpdateRequest request);
    BusSlotResponse markInProgress(Integer id); // Đã đổi tên từ markBusSlotInProgress
    BusSlotResponse markCompleted(Integer id); // Đã đổi tên từ markBusSlotCompleted
    BusSlotResponse archiveTrip(Integer id); // Đã đổi tên từ archiveBusSlot
    List<BusSlotResponse> findTripsNeedingStatusUpdate();
    List<BusSlotResponse> findCompletedTripsForArchive(Integer daysOld);
}
