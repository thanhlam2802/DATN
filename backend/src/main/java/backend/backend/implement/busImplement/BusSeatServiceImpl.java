package backend.backend.implement.busImplement;

import backend.backend.dao.Bus.BusSeatDAO;
import backend.backend.dao.Bus.BusSlotDAO;
import backend.backend.dto.BusDTO.*;
import backend.backend.entity.BusSeat;
import backend.backend.entity.BusSlot;
import backend.backend.entity.User;
import backend.backend.entity.enumBus.BusSeatType;
import backend.backend.service.busService.BusSeatService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class BusSeatServiceImpl implements BusSeatService {

    private final BusSeatDAO busSeatDAO;
    private final BusSlotDAO busSlotDAO;

    // === QUERIES ===

    @Override
    @Transactional(readOnly = true)
    public List<BusSeatResponse> getBusSeats(Integer busSlotId) {
        log.info("Lấy danh sách ghế cho chuyến xe ID: {}", busSlotId);

        List<BusSeat> seats = busSeatDAO.findByBusSlotId(busSlotId);

        if (seats.isEmpty()) {
            log.warn("Không tìm thấy ghế nào cho chuyến xe ID: {}", busSlotId);
        } else {
            log.info("Tìm thấy {} ghế cho chuyến xe ID: {}", seats.size(), busSlotId);
        }

        return seats.stream()
                .map(this::convertToBusSeatResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BusSeatResponse getBusSeatById(Integer id) {
        log.info("Lấy thông tin ghế ID: {}", id);

        BusSeat seat = busSeatDAO.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy ghế với ID: " + id));

        return convertToBusSeatResponse(seat);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusSeatResponse> getAvailableSeats(Integer busSlotId) {
        log.info("Lấy danh sách ghế trống cho chuyến xe ID: {}", busSlotId);

        List<BusSeat> availableSeats = busSeatDAO.findAvailableSeatsByBusSlotId(busSlotId);

        log.info("Tìm thấy {} ghế trống cho chuyến xe ID: {}", availableSeats.size(), busSlotId);

        return availableSeats.stream()
                .map(this::convertToBusSeatResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusSeatResponse> getBookedSeats(Integer busSlotId) {
        log.info("Lấy danh sách ghế đã đặt cho chuyến xe ID: {}", busSlotId);

        List<BusSeat> bookedSeats = busSeatDAO.findBookedSeatsByBusSlotId(busSlotId);

        log.info("Tìm thấy {} ghế đã đặt cho chuyến xe ID: {}", bookedSeats.size(), busSlotId);

        return bookedSeats.stream()
                .map(this::convertToBusSeatResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BusSeatResponse getSeatByNumber(Integer busSlotId, String seatNumber) {
        log.info("Lấy thông tin ghế số {} của chuyến xe ID: {}", seatNumber, busSlotId);

        BusSeat seat = busSeatDAO.findByBusSlotIdAndSeatNumber(busSlotId, seatNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Không tìm thấy ghế số %s của chuyến xe ID: %d", seatNumber, busSlotId)));

        return convertToBusSeatResponse(seat);
    }

    @Override
    @Transactional(readOnly = true)
    public BusSlotResponse getBusSlotWithSeats(Integer id) {
        log.info("Lấy thông tin chuyến xe với ghế ID: {}", id);

        BusSlot busSlot = busSlotDAO.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chuyến xe với ID: " + id));

        // Convert to response (cần implement convertToBusSlotResponse)
        return convertToBusSlotResponse(busSlot);
    }

    // === MUTATIONS ===

    @Override
    @Transactional
    public BusSeatResponse createBusSeat(CreateBusSeatInput input) {
        log.info("Tạo ghế mới: {}", input);

        // Validate input
        if (input.getSeatNumber() == null || input.getSeatNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Số ghế không được để trống");
        }

        if (input.getPrice() == null || input.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá ghế phải lớn hơn 0");
        }

        // Check if seat already exists
        if (input.getBusSlotId() != null && busSeatDAO.existsByBusSlotIdAndSeatNumber(input.getBusSlotId(), input.getSeatNumber())) {
            throw new IllegalArgumentException(
                    String.format("Ghế số %s đã tồn tại trong chuyến xe ID: %d", input.getSeatNumber(), input.getBusSlotId()));
        }

        // Create new seat
        BusSeat seat = new BusSeat();
        seat.setSeatNumber(input.getSeatNumber());
        seat.setPrice(input.getPrice());
        seat.setSeatType(input.getSeatType());
        seat.setIsBooked(false);

        // Set bus slot if provided
        if (input.getBusSlotId() != null) {
            BusSlot busSlot = busSlotDAO.findById(input.getBusSlotId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chuyến xe với ID: " + input.getBusSlotId()));
            seat.setBusSlot(busSlot);
        }

        BusSeat savedSeat = busSeatDAO.save(seat);
        log.info("Đã tạo ghế mới với ID: {}", savedSeat.getId());

        return convertToBusSeatResponse(savedSeat);
    }

    @Override
    @Transactional
    public BusSeatResponse updateBusSeat(UpdateBusSeatInput input) {
        log.info("Cập nhật ghế: {}", input);

        BusSeat seat = busSeatDAO.findById(input.getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy ghế với ID: " + input.getId()));

        // Update fields if provided
        if (input.getSeatNumber() != null) {
            seat.setSeatNumber(input.getSeatNumber());
        }

        if (input.getPrice() != null) {
            seat.setPrice(input.getPrice());
        }

        if (input.getSeatType() != null) {
            seat.setSeatType(input.getSeatType());
        }

        if (input.getIsBooked() != null) {
            seat.setIsBooked(input.getIsBooked());
        }

        BusSeat updatedSeat = busSeatDAO.save(seat);
        log.info("Đã cập nhật ghế ID: {}", updatedSeat.getId());

        return convertToBusSeatResponse(updatedSeat);
    }

    @Override
    @Transactional
    public Boolean deleteBusSeat(Integer id) {
        log.info("Xóa ghế ID: {}", id);

        if (!busSeatDAO.existsById(id)) {
            throw new EntityNotFoundException("Không tìm thấy ghế với ID: " + id);
        }

        busSeatDAO.deleteById(id);
        log.info("Đã xóa ghế ID: {}", id);

        return true;
    }

    // === BOOKING OPERATIONS ===

    @Override
    @Transactional
    public BusSeatResponse bookSeat(BookSeatInput input) {
        log.info("Đặt ghế: {}", input);

        BusSeat seat = busSeatDAO.findById(input.getSeatId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy ghế với ID: " + input.getSeatId()));

        if (seat.getIsBooked()) {
            throw new IllegalStateException("Ghế đã được đặt");
        }

        seat.setIsBooked(true);
        // Có thể thêm thông tin booking như userId, bookingDate, etc.

        BusSeat bookedSeat = busSeatDAO.save(seat);
        log.info("Đã đặt ghế ID: {}", bookedSeat.getId());

        return convertToBusSeatResponse(bookedSeat);
    }

    @Override
    @Transactional
    public BusSeatResponse releaseSeat(ReleaseSeatInput input) {
        log.info("Hủy đặt ghế: {}", input);

        BusSeat seat = busSeatDAO.findById(input.getSeatId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy ghế với ID: " + input.getSeatId()));

        if (!seat.getIsBooked()) {
            throw new IllegalStateException("Ghế chưa được đặt");
        }

        seat.setIsBooked(false);
        // Có thể xóa thông tin booking

        BusSeat releasedSeat = busSeatDAO.save(seat);
        log.info("Đã hủy đặt ghế ID: {}", releasedSeat.getId());

        return convertToBusSeatResponse(releasedSeat);
    }

    @Override
    @Transactional
    public List<BusSeatResponse> bookMultipleSeats(List<Integer> seatIds, Integer userId) {
        log.info("Đặt nhiều ghế: {} cho user ID: {}", seatIds, userId);

        List<BusSeat> seats = busSeatDAO.findByIds(seatIds);

        if (seats.size() != seatIds.size()) {
            throw new EntityNotFoundException("Một số ghế không tồn tại");
        }

        // Check if all seats are available
        for (BusSeat seat : seats) {
            if (seat.getIsBooked()) {
                throw new IllegalStateException("Ghế " + seat.getSeatNumber() + " đã được đặt");
            }
        }

        // Book all seats
        seats.forEach(seat -> seat.setIsBooked(true));
        List<BusSeat> bookedSeats = busSeatDAO.saveAll(seats);

        log.info("Đã đặt {} ghế", bookedSeats.size());

        return bookedSeats.stream()
                .map(this::convertToBusSeatResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<BusSeatResponse> releaseMultipleSeats(List<Integer> seatIds, Integer userId) {
        log.info("Hủy đặt nhiều ghế: {} cho user ID: {}", seatIds, userId);

        List<BusSeat> seats = busSeatDAO.findByIds(seatIds);

        if (seats.size() != seatIds.size()) {
            throw new EntityNotFoundException("Một số ghế không tồn tại");
        }

        // Release all seats
        seats.forEach(seat -> seat.setIsBooked(false));
        List<BusSeat> releasedSeats = busSeatDAO.saveAll(seats);

        log.info("Đã hủy đặt {} ghế", releasedSeats.size());

        return releasedSeats.stream()
                .map(this::convertToBusSeatResponse)
                .collect(Collectors.toList());
    }

    // === HELPER METHODS ===

    /**
     * Convert BusSeat entity to DTO
     */
    private BusSeatResponse convertToBusSeatResponse(BusSeat seat) {
        return BusSeatResponse.builder()
                .id(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .isBooked(seat.getIsBooked())
                .price(seat.getPrice())
                .seatType(seat.getSeatType())
                .busSlotId(seat.getBusSlot() != null ? seat.getBusSlot().getId() : null)
                .build();
    }

    /**
     * Convert BusSlot entity to DTO (cần implement)
     */
    private BusSlotResponse convertToBusSlotResponse(BusSlot busSlot) {
        // Implement conversion logic here
        // Có thể sử dụng logic từ BusSlotServiceImpl
        return null; // Placeholder
    }
}