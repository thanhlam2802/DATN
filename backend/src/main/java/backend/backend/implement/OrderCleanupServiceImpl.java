package backend.backend.implement;

import backend.backend.dao.Bus.BusSeatDAO;
import backend.backend.dao.BusBookingDAO;
import backend.backend.dao.OrderDAO;
import backend.backend.dao.HotelBookingDAO;
import backend.backend.dao.Hotel.HotelRoomDAO;
import backend.backend.entity.*;
import backend.backend.entity.enumBus.BusBookingStatus;
import backend.backend.service.OrderCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderCleanupServiceImpl implements OrderCleanupService {


    private final OrderDAO orderDAO;
    private final HotelBookingDAO hotelBookingDAO;
    private final HotelRoomDAO hotelRoomDAO;
    private final BusBookingDAO busBookingDAO;
    private final BusSeatDAO busSeatDAO;


    @Scheduled(fixedRate = 30000) // ✅ RE-ENABLED: Auto-cancel expired orders every 30 seconds
    @Transactional
    public void cancelExpiredOrders() {

        System.out.println("Running scheduled task to cancel expired orders...");

        // ✅ FIXED: Handle expired PENDING_PAYMENT orders
        List<Order> expiredOrders = orderDAO.findAllByStatusAndExpiresAtBefore("PENDING_PAYMENT", LocalDateTime.now());

        if (!expiredOrders.isEmpty()) {
            for (Order order : expiredOrders) {
                // ✅ EXISTING: Handle hotel bookings
                handleExpiredHotelBookings(order);
                handleExpireBusBooking(order);

                order.setStatus("CANCELLED");
            }
            orderDAO.saveAll(expiredOrders);
            System.out.println("Cancelled " + expiredOrders.size() + " expired orders.");
        }

        // ✅ NEW: Handle RESERVED bus bookings that should be expired
        handleExpiredBusBookings();
    }

    // ✅ REFACTORED: Extract hotel logic
    private void handleExpiredHotelBookings(Order order) {
        List<HotelBooking> hotelBookings = hotelBookingDAO.findByOrderId(order.getId());
        for (HotelBooking booking : hotelBookings) {
            System.out.println("  Hotel Booking id: " + booking.getId() + ", rooms: " + booking.getRooms());
            HotelRoomVariant variant = booking.getRoomVariant();
            if (variant != null && variant.getRoom() != null && booking.getRooms() != null) {
                HotelRoom room = variant.getRoom();
                Short currentQty = room.getRoomQuantity();
                short roomsBooked = booking.getRooms();
                room.setRoomQuantity((short) (currentQty + roomsBooked));
                hotelRoomDAO.save(room);
                System.out.println("Order " + order.getId() + " hoàn lại " + roomsBooked + " phòng cho roomId=" + room.getId());
            }
        }
    }
    private void handleExpireBusBooking(Order order) {
        System.out.println("Expired order: " + order.getId());
        List<BusBooking> busBookings = busBookingDAO.findByOrderIdWithSeats(order.getId());
        System.out.println("Vao bus" + busBookings.get(0));
        for (BusBooking booking : busBookings) {
            System.out.println("  Bus Booking id: " + booking.getId() + ", seats: " + booking.getNumPassengers());

            // ✅ FIXED: Only handle RESERVED bookings (not CANCELLED)
            if (booking.getStatus() == BusBookingStatus.RESERVED) {
                try {
                    // Release seats back to available pool
                    if (booking.getSelectedSeats() != null) {
                        for (BusSeat seat : booking.getSelectedSeats()) {
                            seat.setIsBooked(false);
                            busSeatDAO.save(seat); // ✅ THÊM save operation
                            System.out.println("    Released seat: " + seat.getSeatNumber());
                        }
                    }

                    // Update booking status to expired
                    booking.setStatus(BusBookingStatus.EXPIRED);
                    booking.setNotes(booking.getNotes() + "\n[Auto-expired at: " + LocalDateTime.now() + "]");
                    busBookingDAO.save(booking);

                    System.out.println("Order " + order.getId() + " hoàn lại " + booking.getNumPassengers() + " ghế cho busSlotId=" + booking.getBusSlot().getId());

                } catch (Exception e) {
                    System.err.println("Error releasing bus seats for booking " + booking.getId() + ": " + e.getMessage());
                }
            }
        }
    }

    // ✅ NEW: Handle expired bus bookings independently
    @Transactional
    public void handleExpiredBusBookings() {
        System.out.println("🔍 Checking for expired bus bookings...");
        
        // Find all RESERVED bus bookings that have expired
        List<BusBooking> expiredBusBookings = busBookingDAO.findExpiredReservations(
            BusBookingStatus.RESERVED, 
            LocalDateTime.now()
        );
        
        System.out.println("📊 Found " + expiredBusBookings.size() + " expired bus bookings");
        
        if (!expiredBusBookings.isEmpty()) {
            for (BusBooking booking : expiredBusBookings) {
                try {
                    System.out.println("🔄 Processing expired bus booking: " + booking.getId());
                    System.out.println("   - Status: " + booking.getStatus());
                    System.out.println("   - ExpiresAt: " + booking.getExpiresAt());
                    System.out.println("   - Order: " + (booking.getOrder() != null ? booking.getOrder().getId() : "null"));
                    System.out.println("   - Seats: " + (booking.getSelectedSeats() != null ? booking.getSelectedSeats().size() : 0));
                    
                    // Release seats back to available pool
                    if (booking.getSelectedSeats() != null) {
                        for (BusSeat seat : booking.getSelectedSeats()) {
                            System.out.println("   - Releasing seat: " + seat.getSeatNumber() + " (was booked: " + seat.getIsBooked() + ")");
                            seat.setIsBooked(false);
                            busSeatDAO.save(seat);
                            System.out.println("   ✅ Released seat: " + seat.getSeatNumber());
                        }
                    }

                    // Update booking status to expired
                    booking.setStatus(BusBookingStatus.EXPIRED);
                    booking.setNotes(booking.getNotes() + "\n[Auto-expired at: " + LocalDateTime.now() + "]");
                    busBookingDAO.save(booking);

                    System.out.println("✅ Bus booking " + booking.getId() + " expired and seats released");

                } catch (Exception e) {
                    System.err.println("❌ Error processing expired bus booking " + booking.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("✅ No expired bus bookings found");
        }
    }
}
