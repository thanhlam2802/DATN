package backend.backend.service.implement;

import backend.backend.dao.FlightBookingDAO;
import backend.backend.dao.FlightDAO;
import backend.backend.entity.Flight;
import backend.backend.entity.FlightBooking;
import backend.backend.entity.FlightSlot;
import backend.backend.entity.User;
import backend.backend.dto.FlightBookingDetailDto;
import backend.backend.dto.FlightBookingDto;
import backend.backend.dto.PaymentRequestDto;
import backend.backend.dto.PaymentStatusDto;
import backend.backend.dto.FlightDto;
import backend.backend.service.FlightBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class FlightBookingServiceImpl implements FlightBookingService {
    @Autowired
    private FlightBookingDAO flightBookingDAO;
    @Autowired
    private FlightDAO flightDAO;

    @Override
    public FlightBookingDetailDto bookFlight(FlightBookingDto bookingDto) {
        // Lấy flight và slot
        Flight flight = flightDAO.findById(bookingDto.getFlightId()).orElse(null);
        if (flight == null || flight.getFlightSlots() == null || flight.getFlightSlots().isEmpty()) return null;
        // Chọn slot phù hợp (theo seatClass)
        FlightSlot slot = flight.getFlightSlots().stream()
                .filter(s -> s.getSeatClass().equalsIgnoreCase(bookingDto.getSeatCodes().get(0)))
                .findFirst().orElse(null);
        if (slot == null || slot.getCapacity() < bookingDto.getPassengerInfo().size()) return null;
        // Tạo booking
        FlightBooking booking = new FlightBooking();
        // Mapping user (giả lập, thực tế lấy từ context hoặc bookingDto)
        User user = new User();
        user.setId(bookingDto.getUserId() != null ? bookingDto.getUserId().intValue() : 1); // giả lập userId
        booking.setUser(user);
        booking.setFlightSlot(slot);
        booking.setNumPassengers(bookingDto.getPassengerInfo().size());
        booking.setTotalPrice(BigDecimal.valueOf(bookingDto.getTotalPrice()));
        booking.setBookingDate(LocalDateTime.now());
        // Lưu booking
        flightBookingDAO.save(booking);
        // Trừ capacity slot
        slot.setCapacity(slot.getCapacity() - bookingDto.getPassengerInfo().size());
        // Mapping sang DTO
        return toBookingDetailDto(booking, flight);
    }

    @Override
    public PaymentStatusDto payForFlight(PaymentRequestDto paymentRequestDto) {
        FlightBooking booking = flightBookingDAO.findById(paymentRequestDto.getBookingId()).orElse(null);
        if (booking == null) return null;
        // Giả lập thanh toán thành công
        PaymentStatusDto status = new PaymentStatusDto();
        status.setBookingId(booking.getId().longValue());
        status.setStatus("PAID");
        status.setMessage("Thanh toán thành công");
        return status;
    }

    @Override
    public List<FlightBookingDetailDto> getUserFlightBookings(Long userId) {
        List<FlightBooking> bookings = flightBookingDAO.findByUserId(userId);
        return bookings.stream().map(b -> toBookingDetailDto(b, b.getFlightSlot().getFlight())).collect(Collectors.toList());
    }

    @Override
    public FlightBookingDetailDto getFlightBookingDetail(Long bookingId) {
        FlightBooking booking = flightBookingDAO.findById(bookingId).orElse(null);
        if (booking == null) return null;
        return toBookingDetailDto(booking, booking.getFlightSlot().getFlight());
    }

    @Override
    public PaymentStatusDto cancelFlightBooking(Long bookingId) {
        FlightBooking booking = flightBookingDAO.findById(bookingId).orElse(null);
        if (booking == null) return null;
        // Giả lập hủy booking
        PaymentStatusDto status = new PaymentStatusDto();
        status.setBookingId(booking.getId().longValue());
        status.setStatus("CANCELLED");
        status.setMessage("Đã hủy vé thành công");
        // Trả lại capacity slot
        FlightSlot slot = booking.getFlightSlot();
        slot.setCapacity(slot.getCapacity() + booking.getNumPassengers());
        return status;
    }

    private FlightBookingDetailDto toBookingDetailDto(FlightBooking booking, Flight flight) {
        FlightBookingDetailDto dto = new FlightBookingDetailDto();
        dto.setBookingId(booking.getId().longValue());
        dto.setFlight(toFlightDto(flight));
        dto.setPassengerInfo(null); // Có thể mapping chi tiết nếu cần
        dto.setContactInfo(null);
        dto.setSeatCodes(null);
        dto.setTotalPrice(booking.getTotalPrice().doubleValue());
        dto.setStatus("BOOKED");
        dto.setCreatedAt(booking.getBookingDate());
        return dto;
    }

    private FlightDto toFlightDto(Flight flight) {
        FlightDto dto = new FlightDto();
        dto.setId(flight.getId().longValue());
        dto.setCode(flight.getFlightNumber());
        dto.setAirline(flight.getName());
        dto.setDeparture(flight.getDepartureAirport());
        dto.setDestination(flight.getArrivalAirport());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        dto.setFlightCategory(flight.getCategory() != null ? flight.getCategory().getName() : null);
        if (flight.getFlightSlots() != null && !flight.getFlightSlots().isEmpty()) {
            dto.setPrice(flight.getFlightSlots().stream().map(s -> s.getPrice().doubleValue()).min(Double::compareTo).orElse(0.0));
            dto.setAvailableSeats(flight.getFlightSlots().stream().mapToInt(s -> s.getCapacity() != null ? s.getCapacity() : 0).sum());
            dto.setSeatClass(flight.getFlightSlots().get(0).getSeatClass());
        }
        return dto;
    }
} 