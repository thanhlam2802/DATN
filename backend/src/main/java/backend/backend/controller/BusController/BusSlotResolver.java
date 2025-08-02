package backend.backend.controller.BusController;

import backend.backend.dto.BusDTO.BusSlotResponse;
import backend.backend.entity.enumBus.BusSlotStatus;
import backend.backend.dto.BusDTO.CreateBusSlotRequest;
import backend.backend.dto.BusDTO.UpdateBusSlotRequest;
import backend.backend.dto.BusDTO.UpdateActualTimesRequest;
import backend.backend.dto.BusDTO.QuickStatusUpdateRequest;
import backend.backend.service.busService.BusSlotService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BusSlotResolver {
    private final BusSlotService busSlotService; // Tiêm BusSlotService vào

    // --- Query Mappings ---

    @QueryMapping
    public List<BusSlotResponse> findAllBusSlots() {
        return busSlotService.findAllBusSlots();
    }

    @QueryMapping
    public BusSlotResponse findBusSlotById(@Argument Integer id) {
        return busSlotService.findBusSlotById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy BusSlot với ID: " + id));
    }

    @QueryMapping
    public List<BusSlotResponse> findBusSlotsByBusId(@Argument Integer busId) {
        return busSlotService.findBusSlotsByBusId(busId);
    }

    @QueryMapping
    public List<BusSlotResponse> findBusSlotsByRouteId(@Argument Integer routeId) {
        return busSlotService.findBusSlotsByRouteId(routeId);
    }

    @QueryMapping
    public List<BusSlotResponse> findBusSlotsByStatus(@Argument BusSlotStatus status) {
        return busSlotService.findBusSlotsByStatus(status);
    }

    @QueryMapping
    public List<BusSlotResponse> searchBusSlotsDetailed(
            @Argument String departureProvince,
            @Argument String departureDistrict,
            @Argument String arrivalProvince,
            @Argument String arrivalDistrict,
            @Argument String slotDate,
            @Argument Integer minAvailableSeats,
            @Argument Integer busCategoryId,
            @Argument BigDecimal minPrice,
            @Argument BigDecimal maxPrice,
            @Argument BusSlotStatus status) {

        LocalDate parsedSlotDate = LocalDate.parse(slotDate);

        return busSlotService.searchBusSlotsDetailed(
                departureProvince,
                departureDistrict,
                arrivalProvince,
                arrivalDistrict,
                parsedSlotDate,
                minAvailableSeats,
                busCategoryId,
                minPrice,
                maxPrice,
                status
        );
    }


    @MutationMapping
    public BusSlotResponse createBusSlot(@Argument CreateBusSlotRequest input) {
        return busSlotService.createBusSlot(input);
    }

    @MutationMapping
    public BusSlotResponse updateBusSlot(@Argument Integer id, @Argument UpdateBusSlotRequest input) {
        return busSlotService.updateBusSlot(id, input);
    }

    @MutationMapping
    public Boolean deleteBusSlot(@Argument Integer id) {
        busSlotService.deleteBusSlot(id);
        return true; // Trả về true nếu xóa thành công, exception sẽ được xử lý nếu không tìm thấy
    }

    @MutationMapping
    public BusSlotResponse activateBusSlot(@Argument Integer id) {
        return busSlotService.activateBusSlot(id);
    }

    @MutationMapping
    public BusSlotResponse completeBusSlot(@Argument Integer id) {
        return busSlotService.completeBusSlot(id);
    }

    @MutationMapping
    public BusSlotResponse cancelBusSlot(@Argument Integer id) {
        return busSlotService.cancelBusSlot(id);
    }

    // Real-time Management Mutations

    @MutationMapping
    public BusSlotResponse updateActualTimes(@Argument Integer id, @Argument UpdateActualTimesRequest input) {
        return busSlotService.updateActualTimes(id, input);
    }

    @MutationMapping
    public BusSlotResponse quickStatusUpdate(@Argument Integer id, @Argument QuickStatusUpdateRequest input) {
        return busSlotService.quickStatusUpdate(id, input);
    }

    @MutationMapping
    public BusSlotResponse markBusSlotInProgress(@Argument Integer id) {
        return busSlotService.markInProgress(id);
    }

    @MutationMapping
    public BusSlotResponse markBusSlotCompleted(@Argument Integer id) {
        return busSlotService.markCompleted(id);
    }

    @MutationMapping
    public BusSlotResponse archiveBusSlot(@Argument Integer id) {
        return busSlotService.archiveTrip(id);
    }

    // Auto-management Queries

    @QueryMapping
    public List<BusSlotResponse> findTripsNeedingStatusUpdate() {
        return busSlotService.findTripsNeedingStatusUpdate();
    }

    @QueryMapping
    public List<BusSlotResponse> findCompletedTripsForArchive(@Argument Integer daysOld) {
        return busSlotService.findCompletedTripsForArchive(daysOld);
    }



}
