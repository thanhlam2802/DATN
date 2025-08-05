package backend.backend.service.busService;

import backend.backend.dto.BusDTO.BusBookingRequest;
import backend.backend.dto.BusDTO.BusBookingResponse;
import backend.backend.entity.BusBooking;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface BusBookingService {
    
    // ===================================================================
    // CORE BOOKING OPERATIONS
    // ===================================================================
    
    /**
     * Create bus booking for cart (temporary reservation)
     */
    BusBooking createBusBookingForCart(BusBookingRequest request);

    /**
     * Confirm bus booking (when checkout)
     */
    BusBooking confirmBusBooking(Integer bookingId);

    /**
     * Cancel single bus booking
     */
    void cancelBusBooking(Integer bookingId);

    /**
     * Get bus booking details
     */
    BusBookingResponse getBusBookingDetail(Integer bookingId);

    /**
     * ✅ NEW: Get detailed bus booking information for display
     */
    backend.backend.dto.BusDTO.BusBookingDetailDto getBusBookingDetailForDisplay(Integer bookingId);

    /**
     * Get customer's bus bookings
     */
    List<BusBookingResponse> getCustomerBookings(Integer customerId);

    // ===================================================================
    // BULK OPERATIONS
    // ===================================================================
    
    /**
     * Cancel multiple bookings (simple version)
     */
    List<BusBookingResponse> cancelMultipleBookings(List<Integer> bookingIds);

    /**
     * Cancel multiple bookings with reason (admin version)
     */
    void cancelMultipleBookingsWithReason(List<Integer> bookingIds, String reason);

    // ===================================================================
    // VALIDATION & INFO
    // ===================================================================
    
    /**
     * Check if booking can be cancelled by ID
     */
    boolean canBeCancelled(Integer bookingId);

    /**
     * Check if booking entity can be cancelled
     */
    boolean canBeCancelled(BusBooking booking);

    /**
     * Get cancellation info by booking ID
     */
    Map<String, Object> getCancellationInfo(Integer bookingId);

    /**
     * Get cancellation info by booking entity
     */
    String getCancellationInfo(BusBooking booking);

    // ===================================================================
    // REFUND OPERATIONS
    // ===================================================================
    
    /**
     * Request refund for booking
     */
    BusBookingResponse requestRefund(Integer bookingId);

    /**
     * Process refund (admin function)
     */
    BusBookingResponse processRefund(Integer bookingId, boolean approve);

    /**
     * Get pending refund requests
     */
    List<BusBookingResponse> getPendingRefundRequests();

    /**
     * Calculate refund amount
     */
    BigDecimal calculateRefundAmount(Integer bookingId);

    // ===================================================================
    // SCHEDULED OPERATIONS
    // ===================================================================
    
    /**
     * Release expired reservations (scheduled task)
     */
    void releaseExpiredReservations();
}