package backend.backend.implement.busImplement;

import backend.backend.dao.Bus.*;
import backend.backend.dao.UserDAO;
import backend.backend.dao.BusBookingDAO;
import backend.backend.dto.BusDTO.*; // Giữ các DTO imports

// <-- ĐÃ SỬA: Import các enum từ entity package -->
import backend.backend.entity.*;

// <------------------------------------------------>

import backend.backend.entity.enumBus.BusSlotStatus;
import backend.backend.implement.busImplement.helper.CreateSeatBusSlotHelper;
import backend.backend.service.BusSlotSchedulerService;
import backend.backend.service.busService.BusSlotService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class BusSlotServiceImpl implements BusSlotService {

    private final BusSlotDAO busSlotDAO;
    private final BusDAO busDAO;
    private final RouteDAO routeDAO;
    private final BusCategoryDAO busCategoryDAO;
    private final BusBookingDAO busBookingDAO; // ✅ ADD: Để xóa bookings
    private final UserDAO userDAO; // ✅ ADD: Để validate owner
    private final CreateSeatBusSlotHelper  createSeatBusSlotHelper;
    private final BusSlotSchedulerService  busSlotSchedulerService;
    // --- Helper Methods to Convert Entities to DTOs ---

    private BusResponse convertToBusResponse(Bus bus) {
        return new BusResponse(bus);
    }

    private RouteResponse convertToRouteResponse(Route route) {
        return new RouteResponse(route);
    }

    private BusSlotResponse convertToBusSlotResponse(BusSlot busSlot) {
        // Convert seats to BusSeatResponse list, handle null case
        List<BusSeatResponse> seatResponses = Optional.ofNullable(busSlot.getSeats())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::convertToBusSeatResponse)
                .collect(Collectors.toList());

        return BusSlotResponse.builder()
                .id(busSlot.getId())
                .bus(convertToBusResponse(busSlot.getBus()))
                .route(convertToRouteResponse(busSlot.getRoute()))
                .ownerId(Optional.ofNullable(busSlot.getOwner()).map(User::getId).orElse(null))
                .slotDate(busSlot.getSlotDate())
                .departureTime(Optional.ofNullable(busSlot.getDepartureTime())
                        .map(lt -> lt.atDate(busSlot.getSlotDate()).atOffset(ZoneOffset.ofHours(7)))
                        .orElse(null))
                .arrivalTime(Optional.ofNullable(busSlot.getArrivalTime())
                        .map(lt -> lt.atDate(busSlot.getSlotDate()).atOffset(ZoneOffset.ofHours(7)))
                        .orElse(null))
                .actualDepartureTime(Optional.ofNullable(busSlot.getActualDepartureTime())
                        .map(ldt -> ldt.atOffset(ZoneOffset.ofHours(7)))
                        .orElse(null))
                .actualArrivalTime(Optional.ofNullable(busSlot.getActualArrivalTime())
                        .map(ldt -> ldt.atOffset(ZoneOffset.ofHours(7)))
                        .orElse(null))

                // Real-time Management Fields
                .delayReason(busSlot.getDelayReason())
                .currentLocation(busSlot.getCurrentLocation())
                .driverContactInfo(busSlot.getDriverContactInfo())
                .allowManualOverride(busSlot.getAllowManualOverride())
                .timeToleranceMinutes(busSlot.getTimeToleranceMinutes())

                // Auto-management Fields
                .tripType(busSlot.getTripType())
                .recurringPattern(busSlot.getRecurringPattern())
                .recurringEndDate(busSlot.getRecurringEndDate())
                .autoStatusUpdate(busSlot.getAutoStatusUpdate())
                .autoResetSeats(busSlot.getAutoResetSeats())
                .price(busSlot.getPrice())
                .totalSeats(busSlot.getTotalSeats())
                .availableSeats(busSlot.getAvailableSeats())
                .status(busSlot.getStatus())

                // ✅ ADD: Include seats
                .seats(seatResponses)

                .createdAt(Optional.ofNullable(busSlot.getCreatedAt())
                        .map(ldt -> ldt.atOffset(ZoneOffset.ofHours(7)))
                        .orElse(null))
                .updatedAt(Optional.ofNullable(busSlot.getUpdatedAt())
                        .map(ldt -> ldt.atOffset(ZoneOffset.ofHours(7)))
                        .orElse(null))
                .build();
    }
    @Transactional(readOnly = true)
    public List<BusSeatResponse> getBusSeats(Integer busSlotId) {
        BusSlot busSlot = busSlotDAO.findByIdWithDetails(busSlotId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy BusSlot với ID: " + busSlotId));

        return busSlot.getSeats().stream()
                .map(this::convertToBusSeatResponse)
                .collect(Collectors.toList());
    }

    private BusSeatResponse convertToBusSeatResponse(BusSeat seat) {
        return BusSeatResponse.builder()
                .id(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .isBooked(seat.getIsBooked())
                .price(seat.getPrice())
                .seatType(seat.getSeatType())
                .build();
    }



    // --- CRUD Operations ---

    @Override
    @Transactional
    public BusSlotResponse createBusSlot(CreateBusSlotRequest request) {
        Bus bus = busDAO.findById(request.getBusId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy xe bus với ID: " + request.getBusId()));
        Route route = routeDAO.findById(request.getRouteId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tuyến đường với ID: " + request.getRouteId()));

        // Parse thời gian cho validation

        // ✅ NEW VALIDATION: CHỈ CHECK TIME OVERLAP CHO CÙNG XE, CÙNG NGÀY
        LocalTime newStart = LocalTime.parse(request.getDepartureTime(), java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        LocalTime newEnd = LocalTime.parse(request.getArrivalTime(), java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));

        log.info("🚌 [VALIDATION] Checking time overlap for bus ID: {}, date: {}, time: {} - {}",
                bus.getId(), request.getSlotDate(), newStart, newEnd);

        // ✅ SỬ DỤNG: Logic overlap detection thông minh
        List<BusSlot> conflicts = busSlotDAO.findConflictingSlots(
                bus.getId(),
                request.getSlotDate(),
                newStart,
                newEnd
        );

        // ✅ MULTI-TENANT: Chỉ check conflict trong cùng owner (nếu có ownerId)
        if (request.getOwnerId() != null) {
            conflicts = conflicts.stream()
                    .filter(slot -> slot.getOwner() != null && 
                                   slot.getOwner().getId().equals(request.getOwnerId()))
                    .collect(Collectors.toList());
            log.info("🏢 [MULTI-TENANT] Filtered conflicts by ownerId {}: {} remaining", 
                    request.getOwnerId(), conflicts.size());
        }

        if (!conflicts.isEmpty()) {
            log.warn("⚠️ [VALIDATION] Found {} conflicting trips for bus {}", conflicts.size(), bus.getName());
            for (BusSlot conflict : conflicts) {
                log.warn("   Conflict: ID={}, Time={}-{}, Status={}",
                        conflict.getId(), conflict.getDepartureTime(), conflict.getArrivalTime(), conflict.getStatus());
            }
            throw new IllegalArgumentException(
                    String.format("Thời gian chuyến xe từ %s đến %s bị trùng lặp với chuyến đã có của xe '%s'. " +
                                 "Vui lòng chọn thời gian khác để tránh xung đột.",
                            request.getDepartureTime(), request.getArrivalTime(), bus.getName())
            );
        }

        log.info("✅ [VALIDATION] No time conflicts found - trip can be created");

        // === TẠO BUS SLOT ===
        BusSlot newBusSlot = new BusSlot();
        newBusSlot.setBus(bus);
        newBusSlot.setRoute(route);
        newBusSlot.setSlotDate(request.getSlotDate());
        newBusSlot.setDepartureTime(LocalTime.parse(request.getDepartureTime(), java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
        newBusSlot.setArrivalTime(LocalTime.parse(request.getArrivalTime(), java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
        newBusSlot.setPrice(request.getPrice());
        newBusSlot.setTotalSeats(request.getTotalSeats());
        newBusSlot.setAvailableSeats(request.getTotalSeats());
        newBusSlot.setStatus(BusSlotStatus.SCHEDULED);
        
        // ✅ THÊM: Set owner từ request
        if (request.getOwnerId() != null) {
            User owner = userDAO.findById(request.getOwnerId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy owner với ID: " + request.getOwnerId()));
            newBusSlot.setOwner(owner);
        } else {
            // Fallback: lấy owner từ bus
            newBusSlot.setOwner(bus.getOwner());
        }

        Optional.ofNullable(request.getCurrentLocation()).ifPresent(newBusSlot::setCurrentLocation);
        Optional.ofNullable(request.getDriverContactInfo()).ifPresent(newBusSlot::setDriverContactInfo);
        Optional.ofNullable(request.getAllowManualOverride()).ifPresent(newBusSlot::setAllowManualOverride);
        Optional.ofNullable(request.getTimeToleranceMinutes()).ifPresent(newBusSlot::setTimeToleranceMinutes);
        Optional.ofNullable(request.getTripType()).ifPresent(newBusSlot::setTripType);
        Optional.ofNullable(request.getRecurringPattern()).ifPresent(newBusSlot::setRecurringPattern);
        Optional.ofNullable(request.getRecurringEndDate()).ifPresent(newBusSlot::setRecurringEndDate);
        Optional.ofNullable(request.getAutoStatusUpdate()).ifPresent(newBusSlot::setAutoStatusUpdate);
        Optional.ofNullable(request.getAutoResetSeats()).ifPresent(newBusSlot::setAutoResetSeats);

        // === THÊM LOGIC TẠO GHẾ ===
        List<BusSeat> seats = createSeatBusSlotHelper.createSeatsForBusSlot(newBusSlot, request.getTotalSeats());
        newBusSlot.setSeats(seats);

        // === LƯU BUS SLOT VÀ GHẾ ===
        BusSlot savedBusSlot = busSlotDAO.save(newBusSlot);
        
        // ✅ QUARTZ: Tự động lập lịch cho bus slot mới
        try {
            busSlotSchedulerService.scheduleBusSlot(savedBusSlot);
            log.info("✅ [SCHEDULER] Auto-scheduled jobs for new BusSlot ID: {}", savedBusSlot.getId());
        } catch (Exception e) {
            log.error("❌ [SCHEDULER] Failed to schedule new BusSlot ID: {}", savedBusSlot.getId(), e);
        }
        
        log.info("Đã tạo BusSlot mới với ID: {} và {} ghế", savedBusSlot.getId(), seats.size());

        return convertToBusSlotResponse(savedBusSlot);
    }

    @Override
    @Transactional
    public BusSlotResponse updateBusSlot(Integer id, UpdateBusSlotRequest request) {
        BusSlot existingBusSlot = busSlotDAO.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy BusSlot với ID: " + id));

        Optional.ofNullable(request.getBusId()).ifPresent(busId -> {
            Bus bus = busDAO.findById(busId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy xe bus với ID: " + busId));
            existingBusSlot.setBus(bus);
        });

        Optional.ofNullable(request.getRouteId()).ifPresent(routeId -> {
            Route route = routeDAO.findById(routeId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tuyến đường với ID: " + routeId));
            existingBusSlot.setRoute(route);
        });

        Optional.ofNullable(request.getSlotDate()).ifPresent(existingBusSlot::setSlotDate);
        Optional.ofNullable(request.getDepartureTime()).ifPresent(timeStr -> existingBusSlot.setDepartureTime(LocalTime.parse(timeStr, java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))));
        Optional.ofNullable(request.getArrivalTime()).ifPresent(timeStr -> existingBusSlot.setArrivalTime(LocalTime.parse(timeStr, java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))));

        Optional.ofNullable(request.getPrice()).ifPresent(existingBusSlot::setPrice);
        Optional.ofNullable(request.getTotalSeats()).ifPresent(newTotalSeats -> {
            if (newTotalSeats < existingBusSlot.getTotalSeats()) {
                log.warn("Tổng số ghế giảm từ {} xuống {}. Cần kiểm tra logic ghế đã đặt.", existingBusSlot.getTotalSeats(), newTotalSeats);
            }
            existingBusSlot.setTotalSeats(newTotalSeats);
            Optional.ofNullable(request.getAvailableSeats()).ifPresent(existingBusSlot::setAvailableSeats);
        });
        Optional.ofNullable(request.getAvailableSeats()).ifPresent(existingBusSlot::setAvailableSeats);

        Optional.ofNullable(request.getStatus()).ifPresent(existingBusSlot::setStatus);

        Optional.ofNullable(request.getActualDepartureTime()).ifPresent(odt -> existingBusSlot.setActualDepartureTime(odt.toLocalDateTime()));
        Optional.ofNullable(request.getActualArrivalTime()).ifPresent(odt -> existingBusSlot.setActualArrivalTime(odt.toLocalDateTime()));

        // Cập nhật các trường Real-time Management Fields
        Optional.ofNullable(request.getDelayReason()).ifPresent(existingBusSlot::setDelayReason);
        Optional.ofNullable(request.getCurrentLocation()).ifPresent(existingBusSlot::setCurrentLocation);
        Optional.ofNullable(request.getDriverContactInfo()).ifPresent(existingBusSlot::setDriverContactInfo);
        Optional.ofNullable(request.getAllowManualOverride()).ifPresent(existingBusSlot::setAllowManualOverride);
        Optional.ofNullable(request.getTimeToleranceMinutes()).ifPresent(existingBusSlot::setTimeToleranceMinutes);

        // Cập nhật các trường Auto-management Fields
        Optional.ofNullable(request.getTripType()).ifPresent(existingBusSlot::setTripType);
        Optional.ofNullable(request.getRecurringPattern()).ifPresent(existingBusSlot::setRecurringPattern);
        Optional.ofNullable(request.getRecurringEndDate()).ifPresent(existingBusSlot::setRecurringEndDate);
        Optional.ofNullable(request.getAutoStatusUpdate()).ifPresent(existingBusSlot::setAutoStatusUpdate);
        Optional.ofNullable(request.getAutoResetSeats()).ifPresent(existingBusSlot::setAutoResetSeats);

        // ✅ THÊM: Cập nhật owner nếu có
        Optional.ofNullable(request.getOwnerId()).ifPresent(ownerId -> {
            User owner = userDAO.findById(ownerId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy owner với ID: " + ownerId));
            existingBusSlot.setOwner(owner);
            log.info("Updated BusSlot ID: {} owner to: {}", existingBusSlot.getId(), ownerId);
        });


        // ✅ NEW VALIDATION FOR UPDATE: CHỈ CHECK TIME OVERLAP, EXCLUDE CURRENT SLOT
        List<BusSlot> overlappingSlots = busSlotDAO.findOverlappingActiveBusSlotsExcludingCurrent(
                existingBusSlot.getBus().getId(),
                existingBusSlot.getSlotDate(),
                existingBusSlot.getDepartureTime(),
                existingBusSlot.getArrivalTime(),
                existingBusSlot.getId()
        );

        if (!overlappingSlots.isEmpty()) {
            log.warn("⚠️ [UPDATE] Found {} overlapping slots when updating BusSlot ID: {}", 
                    overlappingSlots.size(), existingBusSlot.getId());
            
            throw new IllegalArgumentException(
                    String.format("Không thể cập nhật. Thời gian mới (%s - %s) bị trùng lặp với chuyến đã có. " +
                                 "Vui lòng chọn thời gian khác.",
                            existingBusSlot.getDepartureTime(), existingBusSlot.getArrivalTime())
            );
        }

        log.info("✅ [UPDATE] No time conflicts found for BusSlot ID: {}", existingBusSlot.getId());

        Optional.ofNullable(request.getTotalSeats()).ifPresent(newTotalSeats -> {
            if (newTotalSeats != existingBusSlot.getTotalSeats()) {
                log.info("Cập nhật số ghế từ {} lên {}", existingBusSlot.getTotalSeats(), newTotalSeats);

                // Xóa ghế cũ
                existingBusSlot.getSeats().clear();

                // Tạo ghế mới
                List<BusSeat> newSeats = createSeatBusSlotHelper.createSeatsForBusSlot(existingBusSlot, newTotalSeats);
                existingBusSlot.setSeats(newSeats);

                existingBusSlot.setTotalSeats(newTotalSeats);
                existingBusSlot.setAvailableSeats(newTotalSeats);
            }
        });


        BusSlot updatedBusSlot = busSlotDAO.save(existingBusSlot);
        
        // ✅ QUARTZ: Cập nhật lịch khi thay đổi thời gian
        if (request.getDepartureTime() != null || request.getArrivalTime() != null || request.getSlotDate() != null) {
            try {
                busSlotSchedulerService.updateScheduleBusSlot(updatedBusSlot);
                log.info("✅ [SCHEDULER] Updated schedule for BusSlot ID: {}", updatedBusSlot.getId());
            } catch (Exception e) {
                log.error("❌ [SCHEDULER] Failed to update schedule for BusSlot ID: {}", updatedBusSlot.getId(), e);
            }
        }
        
        log.info("Đã cập nhật BusSlot với ID: {}", updatedBusSlot.getId());
        return convertToBusSlotResponse(updatedBusSlot);
    }

    @Override
    @Transactional
    public void deleteBusSlot(Integer id) {
        BusSlot busSlot = busSlotDAO.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy BusSlot với ID: " + id));
        
        // ✅ QUARTZ: Hủy lịch trước khi xóa
        try {
            busSlotSchedulerService.unscheduleBusSlot(id);
            log.info("✅ [SCHEDULER] Unscheduled jobs for BusSlot ID: {} before deletion", id);
        } catch (Exception e) {
            log.error("❌ [SCHEDULER] Failed to unschedule BusSlot ID: {} before deletion", id, e);
        }
        
        // ✅ STEP 1: Xóa tất cả BusBookings liên quan trước
        if (busSlot.getBusBookings() != null && !busSlot.getBusBookings().isEmpty()) {
            log.info("Xóa {} bus bookings liên quan đến BusSlot ID: {}", 
                    busSlot.getBusBookings().size(), id);
            
            // Xóa từng booking để trigger cascade delete cho seats
            for (BusBooking booking : busSlot.getBusBookings()) {
                log.info("Xóa BusBooking ID: {} với {} ghế", 
                        booking.getId(), 
                        booking.getSelectedSeats() != null ? booking.getSelectedSeats().size() : 0);
            }
            // Cascade delete sẽ tự động xóa bookings khi xóa busSlot
        }
        
        // ✅ STEP 2: Xóa tất cả BusSeats liên quan
        if (busSlot.getSeats() != null && !busSlot.getSeats().isEmpty()) {
            log.info("Xóa {} seats liên quan đến BusSlot ID: {}", 
                    busSlot.getSeats().size(), id);
        }
        
        // ✅ STEP 3: Xóa BusSlot (cascade sẽ xóa bookings và seats)
        busSlotDAO.deleteById(id);
        log.info("Đã xóa BusSlot với ID: {} và tất cả dữ liệu liên quan", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BusSlotResponse> findBusSlotById(Integer id) {
        return busSlotDAO.findByIdWithDetails(id)
                .map(this::convertToBusSlotResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusSlotResponse> findAllBusSlots() {
        return busSlotDAO.findAllWithDetails().stream()
                .map(this::convertToBusSlotResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusSlotResponse> findBusSlotsByBusId(Integer busId) {
        return busSlotDAO.findByBusIdWithDetails(busId).stream()
                .map(this::convertToBusSlotResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusSlotResponse> findBusSlotsByRouteId(Integer routeId) {
        return busSlotDAO.findByRouteIdWithDetails(routeId).stream()
                .map(this::convertToBusSlotResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusSlotResponse> findBusSlotsByStatus(BusSlotStatus status) {
        return busSlotDAO.findByStatusWithDetails(status).stream()
                .map(this::convertToBusSlotResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusSlotResponse> searchBusSlotsDetailed(
            String departureProvince,
            String departureDistrict,
            String arrivalProvince,
            String arrivalDistrict,
            LocalDate slotDate,
            Integer minAvailableSeats,
            Integer busCategoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            BusSlotStatus status) {

        List<BusSlot> busSlots = busSlotDAO.searchBusSlotsDetailed(
                slotDate,
                departureProvince,
                departureDistrict,
                arrivalProvince,
                arrivalDistrict,
                busCategoryId,
                minPrice,
                maxPrice,
                minAvailableSeats,
                status
        );

        return busSlots.stream()
                .map(this::convertToBusSlotResponse)
                .collect(Collectors.toList());
    }
    // --- QUẢN LÝ QUY TẮC GIÁ MỚI (IMPLEMENTATION) ---



    // --- Status Update Mutations ---

    @Override
    @Transactional
    public BusSlotResponse activateBusSlot(Integer id) {
        BusSlot busSlot = busSlotDAO.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy BusSlot với ID: " + id));
        if (busSlot.getStatus() == BusSlotStatus.SCHEDULED) {
            busSlot.setStatus(BusSlotStatus.IN_PROGRESS);
            BusSlot updated = busSlotDAO.save(busSlot);
            log.info("Đã kích hoạt BusSlot ID: {}", id);
            return convertToBusSlotResponse(updated);
        } else {
            throw new IllegalStateException("Không thể kích hoạt BusSlot ID " + id + " vì trạng thái hiện tại là: " + busSlot.getStatus());
        }
    }

    @Override
    @Transactional
    public BusSlotResponse completeBusSlot(Integer id) {
        BusSlot busSlot = busSlotDAO.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy BusSlot với ID: " + id));
        if (busSlot.getStatus() == BusSlotStatus.IN_PROGRESS) {
            busSlot.setStatus(BusSlotStatus.COMPLETED);
            BusSlot updated = busSlotDAO.save(busSlot);
            log.info("Đã hoàn thành BusSlot ID: {}", id);
            return convertToBusSlotResponse(updated);
        } else {
            throw new IllegalStateException("Không thể hoàn thành BusSlot ID " + id + " vì trạng thái hiện tại là: " + busSlot.getStatus());
        }
    }

    @Override
    @Transactional
    public BusSlotResponse cancelBusSlot(Integer id) {
        BusSlot busSlot = busSlotDAO.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy BusSlot với ID: " + id));
        if (busSlot.getStatus() != BusSlotStatus.CANCELLED) {
            busSlot.setStatus(BusSlotStatus.CANCELLED);
            BusSlot updated = busSlotDAO.save(busSlot);
            log.info("Đã hủy BusSlot ID: {}", id);
            return convertToBusSlotResponse(updated);
        } else {
            throw new IllegalStateException("BusSlot ID " + id + " đã bị hủy rồi.");
        }
    }

    // Real-time Management Methods Implementation

    @Override
    @Transactional
    public BusSlotResponse updateActualTimes(Integer id, UpdateActualTimesRequest request) {
        BusSlot busSlot = busSlotDAO.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy BusSlot với ID: " + id));

        log.info("Updating actual times for BusSlot ID: {} with data: {}", id, request);

        if (request.getActualDepartureTime() != null) {
            busSlot.setActualDepartureTime(request.getActualDepartureTime().toLocalDateTime());
        }
        if (request.getActualArrivalTime() != null) {
            busSlot.setActualArrivalTime(request.getActualArrivalTime().toLocalDateTime());
        }

        if (request.getDelayReason() != null) {
            busSlot.setDelayReason(request.getDelayReason());
        }
        if (request.getCurrentLocation() != null) {
            busSlot.setCurrentLocation(request.getCurrentLocation());
        }

        BusSlot updated = busSlotDAO.save(busSlot);
        log.info("Successfully updated actual times for BusSlot ID: {}", id);
        return convertToBusSlotResponse(updated);
    }

    @Override
    @Transactional
    public BusSlotResponse quickStatusUpdate(Integer id, QuickStatusUpdateRequest request) {
        BusSlot busSlot = busSlotDAO.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy BusSlot với ID: " + id));

        log.info("Quick status update for BusSlot ID: {} to status: {}", id, request.getStatus());

        busSlot.setStatus(request.getStatus());

        if (request.getAutoSetActualTime() != null && request.getAutoSetActualTime()) {
            LocalDateTime now = LocalDateTime.now();
            if (request.getStatus() == BusSlotStatus.IN_PROGRESS && busSlot.getActualDepartureTime() == null) {
                busSlot.setActualDepartureTime(now);
            } else if (request.getStatus() == BusSlotStatus.COMPLETED && busSlot.getActualArrivalTime() == null) {
                busSlot.setActualArrivalTime(now);
            }
        }

        if (request.getDelayReason() != null) {
            busSlot.setDelayReason(request.getDelayReason());
        }
        if (request.getCurrentLocation() != null) {
            busSlot.setCurrentLocation(request.getCurrentLocation());
        }

        BusSlot updated = busSlotDAO.save(busSlot);
        log.info("Successfully updated status for BusSlot ID: {} to: {}", id, request.getStatus());
        return convertToBusSlotResponse(updated);
    }

    @Override
    @Transactional
    public BusSlotResponse markInProgress(Integer id) {
        QuickStatusUpdateRequest request = QuickStatusUpdateRequest.builder()
                .status(BusSlotStatus.IN_PROGRESS)
                .autoSetActualTime(true)
                .build();
        return quickStatusUpdate(id, request);
    }

    @Override
    @Transactional
    public BusSlotResponse markCompleted(Integer id) {
        QuickStatusUpdateRequest request = QuickStatusUpdateRequest.builder()
                .status(BusSlotStatus.COMPLETED)
                .autoSetActualTime(true)
                .build();
        return quickStatusUpdate(id, request);
    }

    // Auto-management Support Methods

    @Override
    @Transactional(readOnly = true)
    public List<BusSlotResponse> findTripsNeedingStatusUpdate() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        return busSlotDAO.findAllWithDetails().stream()
                .filter(slot -> {
                    BusSlotStatus status = slot.getStatus();
                    boolean isActiveOrScheduled = status == BusSlotStatus.SCHEDULED ||
                            status == BusSlotStatus.IN_PROGRESS;

                    boolean isRelevantDate = slot.getSlotDate().isEqual(today) || slot.getSlotDate().isAfter(today);

                    return isActiveOrScheduled && isRelevantDate;
                })
                .map(this::convertToBusSlotResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusSlotResponse> findCompletedTripsForArchive(Integer daysOld) {
        LocalDate cutoffDate = LocalDate.now().minusDays(daysOld != null ? daysOld : 7);

        return busSlotDAO.findAllWithDetails().stream()
                .filter(slot -> {
                    return slot.getSlotDate().isBefore(cutoffDate) &&
                            slot.getStatus() == BusSlotStatus.COMPLETED;
                })
                .map(this::convertToBusSlotResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BusSlotResponse archiveTrip(Integer id) {
        BusSlot busSlot = busSlotDAO.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy BusSlot với ID: " + id));

        busSlot.setStatus(BusSlotStatus.ARCHIVED);
        BusSlot updated = busSlotDAO.save(busSlot);
        log.info("Archived BusSlot ID: {}", id);
        return convertToBusSlotResponse(updated);
    }

    // ✅ THÊM MỚI: Owner-specific methods
    @Override
    @Transactional(readOnly = true)
    public List<BusSlotResponse> findBusSlotsByOwnerId(Integer ownerId) {
        log.info("🔍 [QUERY] Finding BusSlots by owner ID: {}", ownerId);
        List<BusSlot> slots = busSlotDAO.findByOwnerIdWithDetails(ownerId);
        log.info("✅ [QUERY] Found {} BusSlots for owner {}", slots.size(), ownerId);
        
        // Debug: Log existing trips for this owner
        if (log.isDebugEnabled()) {
            slots.forEach(slot -> 
                log.debug("   Slot: ID={}, Bus={}, Route={}, Date={}, Time={}-{}", 
                    slot.getId(), 
                    slot.getBus().getName(),
                    slot.getRoute().getOriginLocation().getName() + "→" + slot.getRoute().getDestinationLocation().getName(),
                    slot.getSlotDate(),
                    slot.getDepartureTime(), 
                    slot.getArrivalTime())
            );
        }
        
        return slots.stream()
                .map(this::convertToBusSlotResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusSlotResponse> findBusSlotsByOwnerIdAndStatus(Integer ownerId, BusSlotStatus status) {
        log.info("Finding BusSlots by owner ID: {} and status: {}", ownerId, status);
        return busSlotDAO.findByOwnerIdAndStatusWithDetails(ownerId, status).stream()
                .map(this::convertToBusSlotResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusSlotResponse> findBusSlotsByOwnerIdAndSlotDate(Integer ownerId, LocalDate slotDate) {
        log.info("Finding BusSlots by owner ID: {} and slot date: {}", ownerId, slotDate);
        return busSlotDAO.findByOwnerIdAndSlotDateWithDetails(ownerId, slotDate).stream()
                .map(this::convertToBusSlotResponse)
                .collect(Collectors.toList());
    }
}