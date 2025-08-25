package backend.backend.implement;

import backend.backend.dao.Bus.BusSeatDAO;
import backend.backend.dao.BusBookingDAO;
import backend.backend.dao.FlightBookingDAO;
import backend.backend.dao.OrderDAO;
import backend.backend.entity.FlightBooking;
import backend.backend.entity.Order;
import backend.backend.service.FlightBookingService;
import backend.backend.dao.HotelBookingDAO;
import backend.backend.dao.Hotel.HotelRoomDAO;
import backend.backend.entity.*;
import backend.backend.entity.enumBus.BusBookingStatus;
import backend.backend.entity.HotelBooking;
import backend.backend.entity.HotelRoomVariant;
import backend.backend.entity.HotelRoom;
import backend.backend.service.OrderCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 
import backend.backend.controller.AdminWebSocketController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderCleanupServiceImpl implements OrderCleanupService {

 
    private final OrderDAO orderDAO;
    private final HotelBookingDAO hotelBookingDAO;
    private final HotelRoomDAO hotelRoomDAO;
    private final BusBookingDAO busBookingDAO;
    private final BusSeatDAO busSeatDAO;
    private final FlightBookingDAO flightBookingDAO;
    private final FlightBookingService flightBookingService;
   

    private final SimpMessagingTemplate messagingTemplate;
    private final ApplicationEventPublisher publisher;
    private final AdminWebSocketController adminWebSocketController;
    
@Scheduled(fixedDelay = 5000)
@Transactional
    public void cancelExpiredOrders() {
        System.out.println("Running scheduled task to cancel expired orders...");
        List<Order> expiredOrders = orderDAO.findAllByStatusAndExpiresAtBefore("PENDING_PAYMENT", LocalDateTime.now());
        
        if (!expiredOrders.isEmpty()) {
            for (Order order : expiredOrders) {
                List<FlightBooking> flightBooking = flightBookingDAO.findByOrderId(order.getId());
                System.out.println("size của booking: " + flightBooking.size());
                for (FlightBooking flightBooking1 : flightBooking) {
                    System.out.println("id cancel booking: " + flightBooking1.getId());
                    flightBookingService.cancelBooking(flightBooking1.getId());
                }
            
                System.out.println("Expired order: " + order.getId());
                List<HotelBooking> hotelBookings = hotelBookingDAO.findByOrderId(order.getId());
                for (HotelBooking booking : hotelBookings) {
                    System.out.println("  Booking id: " + booking.getId() + ", rooms: " + booking.getRooms());
                    HotelRoomVariant variant = booking.getRoomVariant();
                    if (variant != null && variant.getRoom() != null && booking.getRooms() != null) {
                        HotelRoom room = variant.getRoom();
                        Short currentQty = room.getRoomQuantity();
                        short roomsBooked = booking.getRooms();
                        room.setRoomQuantity((short) (currentQty + roomsBooked));
                        hotelRoomDAO.save(room);
                        System.out.println("Order " + order.getId() + " hoàn lại " + roomsBooked + " phòng cho roomId=" + room.getId());
                        
                        try {
                            Integer hotelId = room.getHotel().getId();
                            Map<String, Object> roomUpdate = new java.util.HashMap<>();
                            roomUpdate.put("hotelId", hotelId);
                            roomUpdate.put("roomId", room.getId());
                            roomUpdate.put("previousQuantity", currentQty);
                            roomUpdate.put("newQuantity", room.getRoomQuantity());
                            roomUpdate.put("action", "ROOM_RESTORED");
                            messagingTemplate.convertAndSend("/topic/hotels/" + hotelId + "/room-updates", roomUpdate);
                            System.out.println("Đã gửi WebSocket notification cho hotelId=" + hotelId + ", roomId=" + room.getId());
                            
                            adminWebSocketController.sendExpiredOrderNotification(order.getId().toString());
                        } catch (Exception e) {
                            System.out.println("Không thể gửi WebSocket notification: " + e.getMessage());
                        }
                    }
                }
                handleExpireBusBooking(order);
                order.setStatus("CANCELLED");
                publisher.publishEvent(order);
            }
            handleExpiredBusBookings();
            orderDAO.saveAll(expiredOrders);

            System.out.println("Cancelled " + expiredOrders.size() + " expired orders.");
        }

      
    }

    private void handleExpireBusBooking(Order order) {

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
