package backend.backend.implement.busImplement;

import backend.backend.dao.BusBookingDAO;
import backend.backend.dao.Bus.BusSeatDAO;
import backend.backend.dao.Bus.BusSlotDAO;
import backend.backend.dao.CustomerDAO;
import backend.backend.dto.BusDTO.*;
import backend.backend.entity.*;
import backend.backend.entity.enumBus.BusBookingStatus;
import backend.backend.exception.*;
import backend.backend.service.busService.BusBookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class BusBookingServiceImpl implements BusBookingService {

    private final BusBookingDAO busBookingDAO;
    private final BusSeatDAO busSeatDAO;
    private final BusSlotDAO busSlotDAO;
    private final CustomerDAO customerDAO;

    // ===================================================================
    // CORE BOOKING OPERATIONS
    // ===================================================================

    @Override
    @Transactional
    public BusBooking createBusBookingForCart(BusBookingRequest request) {
        log.info("Creating bus booking for cart: busSlotId={}, seatIds={}",
                request.getBusSlotId(), request.getSelectedSeatIds());

        // 1. Validate bus slot
        BusSlot busSlot = busSlotDAO.findById(request.getBusSlotId())
                .orElseThrow(() -> new BusBookingException("BUS_SLOT_NOT_FOUND",
                        "Bus slot not found with ID: " + request.getBusSlotId()));

        // 2. Get or create customer
        Customer customer = getOrCreateCustomer(request);

        // 3. Validate and lock seats
        List<BusSeat> selectedSeats = validateAndLockSeats(request.getSelectedSeatIds(), busSlot);

        // 4. Create booking
        BusBooking booking = createBooking(busSlot, customer, selectedSeats, request);

        booking = busBookingDAO.save(booking);

        log.info("Bus booking created successfully: bookingId={}, reference={}",
                booking.getId(), booking.getBookingReference());

        return booking;
    }

    @Override
    @Transactional
    public BusBooking confirmBusBooking(Integer bookingId) {
//        tao order truoc gan no vao booking set status la dang pending
        log.info("Confirming bus booking: {}", bookingId);

        BusBooking booking = findBookingById(bookingId);

        // Validate booking can be confirmed
        if (booking.getStatus() != BusBookingStatus.RESERVED) {
            throw new BusBookingException("INVALID_STATUS",
                    "Can only confirm bookings in RESERVED status, current: " + booking.getStatus());
        }

        // Check if expired
        validateBookingNotExpired(booking);

        // Update to PAID status and clear expiration
        booking.setStatus(BusBookingStatus.PAID);
        booking.setExpiresAt(null);

        return busBookingDAO.save(booking);
    }

    @Override
    @Transactional
    public void cancelBusBooking(Integer bookingId) {
        log.info("Cancelling bus booking: {}", bookingId);

        BusBooking booking = findBookingById(bookingId);

        // Validate if booking can be cancelled
        validateBookingCanBeCancelled(booking);

        // Release seats
        Optional.ofNullable(booking.getSelectedSeats())
                .ifPresent(seats -> seats.forEach(this::releaseSeat));

        // Update booking status
        booking.setStatus(BusBookingStatus.CANCELLED);
        booking.setExpiresAt(null);
        booking.setNotes(Optional.ofNullable(booking.getNotes()).orElse("")
                + "\n[Cancelled at: " + LocalDateTime.now() + "]");

        busBookingDAO.save(booking);

        log.info("Bus booking cancelled successfully: {}", bookingId);
    }

    @Override
    @Transactional(readOnly = true)
    public BusBookingResponse getBusBookingDetail(Integer bookingId) {
        BusBooking booking = findBookingById(bookingId);
        return convertToResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusBookingResponse> getCustomerBookings(Integer customerId) {
        return busBookingDAO.findByCustomerIdOrderByBookingDateDesc(customerId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ===================================================================
    // BULK OPERATIONS
    // ===================================================================

    @Override
    @Transactional
    public List<BusBookingResponse> cancelMultipleBookings(List<Integer> bookingIds) {
        log.info("Bulk cancelling {} bookings (returning responses)", bookingIds.size());

        List<BusBookingResponse> results = new ArrayList<>();
        int successCount = 0;

        for (Integer bookingId : bookingIds) {
            try {
                BusBooking booking = findBookingById(bookingId);
                validateBookingCanBeCancelled(booking);

                // Release seats
                Optional.ofNullable(booking.getSelectedSeats())
                        .ifPresent(seats -> seats.forEach(this::releaseSeat));

                // Update booking
                booking.setStatus(BusBookingStatus.CANCELLED);
                booking.setNotes(Optional.ofNullable(booking.getNotes()).orElse("")
                        + "\n[Bulk cancelled at " + LocalDateTime.now() + "]");

                BusBooking savedBooking = busBookingDAO.save(booking);
                BusBookingResponse response = convertToResponse(savedBooking);
                results.add(response);

                successCount++;

            } catch (Exception e) {
                log.error("Failed to cancel booking {}: {}", bookingId, e.getMessage());

                // Add error response
                BusBookingResponse errorResponse = new BusBookingResponse();
                errorResponse.setId(bookingId);
                errorResponse.setNotes("❌ Error: " + e.getMessage());
                results.add(errorResponse);
            }
        }

        log.info("Bulk cancellation completed: {} successful, {} errors",
                successCount, bookingIds.size() - successCount);

        return results;
    }

    // ✅ NEW: Bulk cancel method for admin use (void return)
    @Override
    @Transactional
    public void cancelMultipleBookingsWithReason(List<Integer> bookingIds, String reason) {
        log.info("Bulk cancelling {} bookings with reason: {}", bookingIds.size(), reason);

        List<String> errors = new ArrayList<>();
        int successCount = 0;

        for (Integer bookingId : bookingIds) {
            try {
                BusBooking booking = findBookingById(bookingId);
                validateBookingCanBeCancelled(booking);

                // Release seats
                Optional.ofNullable(booking.getSelectedSeats())
                        .ifPresent(seats -> seats.forEach(this::releaseSeat));

                // Update booking
                booking.setStatus(BusBookingStatus.CANCELLED);
                booking.setNotes(Optional.ofNullable(booking.getNotes()).orElse("")
                        + "\n[Bulk cancelled: " + reason + " at " + LocalDateTime.now() + "]");

                busBookingDAO.save(booking);
                successCount++;

            } catch (Exception e) {
                errors.add("Booking " + bookingId + ": " + e.getMessage());
                log.error("Failed to cancel booking {}: {}", bookingId, e.getMessage());
            }
        }

        log.info("Bulk cancellation completed: {} successful, {} errors", successCount, errors.size());

        if (!errors.isEmpty()) {
            throw new BusBookingException("BULK_CANCEL_PARTIAL_FAILURE",
                    "Some bookings could not be cancelled: " + String.join("; ", errors));
        }
    }

    // ===================================================================
    // VALIDATION & INFO
    // ===================================================================

    // ✅ NEW: Check if booking can be cancelled (for frontend validation)
    @Override
    @Transactional(readOnly = true)
    public boolean canBeCancelled(Integer bookingId) {
        try {
            BusBooking booking = findBookingById(bookingId);
            validateBookingCanBeCancelled(booking);
            return true;
        } catch (Exception e) {
            log.debug("Booking {} cannot be cancelled: {}", bookingId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean canBeCancelled(BusBooking booking) {
        try {
            validateBookingCanBeCancelled(booking);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ✅ NEW: Get cancellation info (why can't be cancelled)
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getCancellationInfo(Integer bookingId) {
        BusBooking booking = findBookingById(bookingId);

        Map<String, Object> info = new HashMap<>();
        info.put("bookingId", bookingId);
        info.put("currentStatus", booking.getStatus());
        info.put("canBeCancelled", false);
        info.put("reason", "");

        try {
            validateBookingCanBeCancelled(booking);
            info.put("canBeCancelled", true);
            info.put("reason", "Booking can be cancelled");
        } catch (BusBookingException e) {
            info.put("reason", e.getMessage());
        }

        // Add time-related info
        if (booking.getBusSlot() != null) {
            LocalDateTime departureDateTime = LocalDateTime.of(
                    booking.getBusSlot().getSlotDate(),
                    booking.getBusSlot().getDepartureTime()
            );

            long hoursUntilDeparture = java.time.Duration.between(LocalDateTime.now(), departureDateTime).toHours();
            info.put("hoursUntilDeparture", hoursUntilDeparture);
            info.put("departureTime", departureDateTime);
            info.put("minCancelTime", departureDateTime.minusHours(2));
        }

        return info;
    }

    @Override
    public String getCancellationInfo(BusBooking booking) {
        if (!canBeCancelled(booking)) {
            return "Không thể hủy hoặc hoàn tiền.";
        }

        if (booking.getBusSlot() != null) {
            LocalDateTime departureDateTime = LocalDateTime.of(
                    booking.getBusSlot().getSlotDate(),
                    booking.getBusSlot().getDepartureTime()
            );
            long hoursUntilDeparture = java.time.Duration.between(LocalDateTime.now(), departureDateTime).toHours();
            
            if (hoursUntilDeparture > 24) {
                return "Có thể hủy và hoàn 90% tiền.";
            } else if (hoursUntilDeparture > 0) {
                return "Có thể hủy và hoàn 50% tiền.";
            }
        }
        return "Có thể hủy nhưng không hoàn tiền.";
    }

    // ===================================================================
    // REFUND OPERATIONS
    // ===================================================================

    @Override
    @Transactional
    public BusBookingResponse requestRefund(Integer bookingId) {
        BusBooking booking = findBookingById(bookingId);

        if (!canBeCancelled(booking)) {
            throw new BusBookingException("REFUND_NOT_ALLOWED", "Booking cannot be refunded at this time.");
        }

        booking.setStatus(BusBookingStatus.REFUND_REQUESTED);
        booking.setNotes(Optional.ofNullable(booking.getNotes()).orElse("")
                + "\n[Refund requested at: " + LocalDateTime.now() + "]");

        busBookingDAO.save(booking);
        log.info("Refund requested for booking: {}", bookingId);

        return convertToResponse(booking);
    }

    @Override
    @Transactional
    public BusBookingResponse processRefund(Integer bookingId, boolean approve) {
        BusBooking booking = findBookingById(bookingId);

        if (booking.getStatus() != BusBookingStatus.REFUND_REQUESTED) {
            throw new BusBookingException("INVALID_STATUS", "Booking is not in refund requested status");
        }

        if (approve) {
            booking.setStatus(BusBookingStatus.REFUNDED);
            // Release seats
            Optional.ofNullable(booking.getSelectedSeats())
                    .ifPresent(seats -> seats.forEach(this::releaseSeat));
            log.info("Refund approved and processed for booking: {}", bookingId);
        } else {
            booking.setStatus(BusBookingStatus.REFUND_REJECTED);
            log.info("Refund rejected for booking: {}", bookingId);
        }

        booking.setNotes(Optional.ofNullable(booking.getNotes()).orElse("")
                + "\n[Refund " + (approve ? "approved" : "rejected") + " at: " + LocalDateTime.now() + "]");

        busBookingDAO.save(booking);
        return convertToResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusBookingResponse> getPendingRefundRequests() {
        return busBookingDAO.findByStatus(BusBookingStatus.REFUND_REQUESTED)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal calculateRefundAmount(Integer bookingId) {
        BusBooking booking = findBookingById(bookingId);

        if (booking.getBusSlot() != null) {
            LocalDateTime departureDateTime = LocalDateTime.of(
                    booking.getBusSlot().getSlotDate(),
                    booking.getBusSlot().getDepartureTime()
            );
            long hoursUntilDeparture = java.time.Duration.between(LocalDateTime.now(), departureDateTime).toHours();

            if (hoursUntilDeparture > 24) {
                return booking.getTotalPrice().multiply(BigDecimal.valueOf(0.9));
            } else if (hoursUntilDeparture > 0) {
                return booking.getTotalPrice().multiply(BigDecimal.valueOf(0.5));
            }
        }
        return BigDecimal.ZERO;
    }

    // ===================================================================
    // SCHEDULED OPERATIONS
    // ===================================================================

    @Override
    @Transactional
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void releaseExpiredReservations() {
        log.debug("Checking for expired reservations...");

        List<BusBooking> expiredBookings = busBookingDAO
                .findExpiredReservations(BusBookingStatus.RESERVED, LocalDateTime.now());

        expiredBookings.forEach(booking -> {
            // Only cancel booking without order (temporary/direct bookings)
            if (booking.getOrder() == null) {
                log.info("Releasing expired temporary reservation: {}", booking.getId());

                // Release seats
                Optional.ofNullable(booking.getSelectedSeats())
                        .ifPresent(seats -> seats.forEach(this::releaseSeat));

                // Update booking status
                booking.setStatus(BusBookingStatus.EXPIRED);
                booking.setExpiresAt(null);
                busBookingDAO.save(booking);
            } else {
                log.debug("Skipping cart booking: {} (will be handled by OrderCleanupService)", booking.getId());
            }
        });

        if (!expiredBookings.isEmpty()) {
            long releasedCount = expiredBookings.stream().filter(b -> b.getOrder() == null).count();
            if (releasedCount > 0) {
                log.info("Released {} expired temporary reservations", releasedCount);
            }
        }
    }

    // ===================================================================
    // HELPER METHODS
    // ===================================================================

    /**
     * Get existing customer or create new one from request info
     */
    private Customer getOrCreateCustomer(BusBookingRequest request) {
        // If customerId is provided, try to find existing customer
        if (request.getCustomerId() != null) {
            Optional<Customer> existingCustomer = customerDAO.findById(request.getCustomerId());
            if (existingCustomer.isPresent()) {
                return existingCustomer.get();
            }
        }

        // If no customerId or customer not found, create new customer
        Customer customer = new Customer();
        customer.setFullName(request.getPassengerName());
        customer.setPhone(request.getPassengerPhone());
        customer.setEmail(request.getPassengerEmail());

        return customerDAO.save(customer);
    }

    /**
     * Find booking by ID with proper exception handling
     */
    private BusBooking findBookingById(Integer bookingId) {
        return busBookingDAO.findByIdWithSeats(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(
                        bookingId.toString(), "Booking not found"));
    }

    /**
     * Validate booking not expired
     */
    private void validateBookingNotExpired(BusBooking booking) {
        Optional.ofNullable(booking.getExpiresAt())
                .filter(expiresAt -> expiresAt.isBefore(LocalDateTime.now()))
                .ifPresent(expiresAt -> {
                    throw new BusBookingException("BOOKING_EXPIRED",
                            "Booking " + booking.getId() + " has expired at " + expiresAt);
                });
    }

    /**
     * Validation method for cancellation
     */
    private void validateBookingCanBeCancelled(BusBooking booking) {
        // Check current status
        if (booking.getStatus() == BusBookingStatus.CANCELLED) {
            throw new BusBookingException("ALREADY_CANCELLED", "Booking is already cancelled");
        }

        if (booking.getStatus() == BusBookingStatus.EXPIRED) {
            throw new BusBookingException("BOOKING_EXPIRED", "Cannot cancel expired booking");
        }

        // Check if departure time has passed
        if (booking.getBusSlot() != null) {
            LocalDateTime departureDateTime = LocalDateTime.of(
                    booking.getBusSlot().getSlotDate(),
                    booking.getBusSlot().getDepartureTime()
            );

            if (LocalDateTime.now().isAfter(departureDateTime)) {
                throw new BusBookingException("DEPARTURE_PASSED",
                        "Cannot cancel booking after departure time");
            }

            // Minimum cancellation time (2 hours before departure)
            LocalDateTime minCancelTime = departureDateTime.minusHours(2);
            if (LocalDateTime.now().isAfter(minCancelTime)) {
                throw new BusBookingException("TOO_LATE_TO_CANCEL",
                        "Cannot cancel booking within 2 hours of departure time");
            }
        }

        // Paid bookings might need special handling
        if (booking.getStatus() == BusBookingStatus.PAID) {
            log.warn("Cancelling PAID booking: {}. This might require refund processing.", booking.getId());
        }
    }

    /**
     * Create booking with all necessary fields
     */
    private BusBooking createBooking(BusSlot busSlot, Customer customer,
                                     List<BusSeat> selectedSeats, BusBookingRequest request) {
        BusBooking booking = new BusBooking();
        booking.setBusSlot(busSlot);
        booking.setCustomer(customer);
        booking.setSelectedSeats(selectedSeats);
        booking.setNumPassengers(selectedSeats.size());
        booking.setTotalPrice(request.getTotalPrice());
        booking.setStatus(BusBookingStatus.RESERVED);
        booking.setNotes(request.getNotes());
        booking.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        return booking;
    }

    /**
     * Release a single seat
     */
    private void releaseSeat(BusSeat seat) {
        seat.setIsBooked(false);
        busSeatDAO.save(seat);
        log.debug("Released seat: {}", seat.getSeatNumber());
    }

    /**
     * Validate and lock seats for booking
     */
    private List<BusSeat> validateAndLockSeats(List<Integer> seatIds, BusSlot busSlot) {
        if (seatIds == null || seatIds.isEmpty()) {
            throw new BusBookingException("NO_SEATS_SELECTED", "No seats selected for booking");
        }

        List<BusSeat> seats = busSeatDAO.findByIds(seatIds);

        if (seats.size() != seatIds.size()) {
            throw new BusBookingException("SEATS_NOT_FOUND",
                    String.format("Expected %d seats, found %d", seatIds.size(), seats.size()));
        }

        // Validate seats
        seats.forEach(seat -> validateSeatForBooking(seat, busSlot));

        // Lock seats
        seats.forEach(seat -> {
            seat.setIsBooked(true);
            busSeatDAO.save(seat);
        });

        return seats;
    }

    /**
     * Validate individual seat for booking
     */
    private void validateSeatForBooking(BusSeat seat, BusSlot busSlot) {
        if (!seat.getBusSlot().getId().equals(busSlot.getId())) {
            throw new SeatNotAvailableException(seat.getSeatNumber(),
                    "does not belong to this bus slot");
        }

        if (seat.getIsBooked()) {
            throw new SeatNotAvailableException(seat.getSeatNumber(),
                    "is already booked");
        }
    }

    // ===================================================================
    // CONVERSION METHODS
    // ===================================================================

    /**
     * Convert BusBooking entity to BusBookingResponse DTO
     */
    private BusBookingResponse convertToResponse(BusBooking booking) {
        BusBookingResponse dto = new BusBookingResponse();
        dto.setId(booking.getId());
        dto.setBookingReference(booking.getBookingReference());
        dto.setStatus(booking.getStatus()); // ✅ Set enum directly, not string
        dto.setNumPassengers(booking.getNumPassengers());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setBookingDate(booking.getBookingDate());
        dto.setExpiresAt(booking.getExpiresAt());
        dto.setNotes(booking.getNotes());
        dto.setOrderId(booking.getOrder() != null ? booking.getOrder().getId() : null);

        // Customer info
        if (booking.getCustomer() != null) {
            dto.setCustomerId(booking.getCustomer().getId());
            dto.setCustomerName(booking.getCustomer().getFullName());
            dto.setCustomerPhone(booking.getCustomer().getPhone());
            dto.setCustomerEmail(booking.getCustomer().getEmail());
        }

        // Bus slot info
        if (booking.getBusSlot() != null) {
            BusSlot slot = booking.getBusSlot();
            dto.setBusSlotId(slot.getId());
            dto.setDepartureDate(slot.getSlotDate());
            dto.setDepartureTime(slot.getDepartureTime());
            dto.setArrivalTime(slot.getArrivalTime());

            // Bus and route info
            if (slot.getBus() != null) {
                dto.setBusName(slot.getBus().getName());
                dto.setBusLicensePlate(slot.getBus().getLicensePlate());
            }

            if (slot.getRoute() != null) {
                dto.setDepartureLocation(getLocationDisplayName(slot.getRoute().getOriginLocation()));
                dto.setArrivalLocation(getLocationDisplayName(slot.getRoute().getDestinationLocation()));
            }
        }

        // Seat numbers
        if (booking.getSelectedSeats() != null && !booking.getSelectedSeats().isEmpty()) {
            List<String> seatNumbers = booking.getSelectedSeats().stream()
                    .map(BusSeat::getSeatNumber)
                    .collect(Collectors.toList());
            dto.setSeatNumbers(seatNumbers);
        }

        return dto;
    }

    /**
     * Get display name for Location entity
     */
    private String getLocationDisplayName(Location location) {
        return Optional.ofNullable(location)
                .map(loc -> {
                    StringBuilder display = new StringBuilder();

                    appendIfNotEmpty(display, loc.getName());
                    appendIfNotEmpty(display, loc.getProvinceCity());
                    appendIfNotEmpty(display, loc.getDistrict());

                    Optional.ofNullable(loc.getAddressDetails())
                            .filter(addr -> !addr.trim().isEmpty() && addr.length() < 50)
                            .ifPresent(addr -> appendIfNotEmpty(display, addr));

                    return !display.isEmpty() ? display.toString() : "N/A";
                })
                .orElse("N/A");
    }

    /**
     * Helper method to append non-empty strings
     */
    private void appendIfNotEmpty(StringBuilder builder, String value) {
        Optional.ofNullable(value)
                .filter(v -> !v.trim().isEmpty())
                .ifPresent(v -> {
                    if (!builder.isEmpty()) builder.append(" - ");
                    builder.append(v);
                });
    }
}