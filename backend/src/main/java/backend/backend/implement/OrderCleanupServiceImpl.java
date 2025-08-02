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
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderCleanupServiceImpl implements OrderCleanupService {


    private final OrderDAO orderDAO;
    private final HotelBookingDAO hotelBookingDAO;
    private final HotelRoomDAO hotelRoomDAO;
    private final BusBookingDAO busBookingDAO;
    private final BusSeatDAO busSeatDAO;


    @Scheduled(fixedRate = 300000)
    @Transactional
    public void cancelExpiredOrders() {
        System.out.println("Running scheduled task to cancel expired orders...");
        List<Order> expiredOrders = orderDAO.findAllByStatusAndExpiresAtBefore("PENDING_PAYMENT", LocalDateTime.now());

        if (!expiredOrders.isEmpty()) {
            for (Order order : expiredOrders) {
                System.out.println("Expired order: " + order.getId());

                // ✅ EXISTING: Handle hotel bookings
                handleExpiredHotelBookings(order);

                // ✅ NEW: Handle bus bookings
                handleExpiredBusBookings(order);

                order.setStatus("CANCELLED");
            }
            orderDAO.saveAll(expiredOrders);
            System.out.println("Cancelled " + expiredOrders.size() + " expired orders.");
        }
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

    private void handleExpiredBusBookings(Order order) {
        List<BusBooking> busBookings = busBookingDAO.findByOrderIdWithSeats(order.getId());

        for (BusBooking booking : busBookings) {
            System.out.println("  Bus Booking id: " + booking.getId() + ", seats: " + booking.getNumPassengers());

            // Only handle RESERVED bookings (similar to hotel pattern)
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
}
