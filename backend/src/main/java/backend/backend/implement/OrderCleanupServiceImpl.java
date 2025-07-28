package backend.backend.implement;

import backend.backend.dao.FlightBookingDAO;
import backend.backend.dao.OrderDAO;

import backend.backend.entity.FlightBooking;
import backend.backend.entity.Order;
import backend.backend.service.FlightBookingService;

import backend.backend.dao.BookingTourDAO;
import backend.backend.dao.DepartureDAO;
import backend.backend.dao.HotelBookingDAO;
import backend.backend.dao.Hotel.HotelRoomDAO;
import backend.backend.entity.Order;
import backend.backend.entity.BookingTour;
import backend.backend.entity.Departure;
import backend.backend.entity.HotelBooking;
import backend.backend.entity.HotelRoomVariant;
import backend.backend.entity.HotelRoom;

import backend.backend.service.OrderCleanupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 

import java.time.LocalDateTime;
import java.util.List;

@Service 
public class OrderCleanupServiceImpl implements OrderCleanupService {

    @Autowired
    private OrderDAO orderDAO;
    @Autowired

    private FlightBookingDAO flightBookingDAO;
    @Autowired
    private FlightBookingService flightBookingService;

    private HotelBookingDAO hotelBookingDAO;
    @Autowired
    private HotelRoomDAO hotelRoomDAO;
    @Autowired
    private BookingTourDAO bookingTourDAO;

    @Autowired
    private DepartureDAO departureDAO;
    
    
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void cancelExpiredOrders() {
        System.out.println("Running scheduled task to cancel expired orders...");
        List<Order> expiredOrders = orderDAO.findAllByStatusAndExpiresAtBefore("PENDING_PAYMENT", LocalDateTime.now());
        
        if (!expiredOrders.isEmpty()) {
            for (Order order : expiredOrders) {

                List<FlightBooking> flightBooking = flightBookingDAO.findByOrderId(order.getId());
                for (FlightBooking flightBooking1 : flightBooking) {

                    flightBookingService.cancelBooking(flightBooking1.getId());

            

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
                    }
                }
                List<BookingTour> tourBookings = bookingTourDAO.findByOrderId(order.getId());
                for (BookingTour booking : tourBookings) {
                    Departure departure = booking.getDeparture();
                    if (departure != null) {
                        int adults = booking.getNumberOfAdults() != null ? booking.getNumberOfAdults() : 0;
                        int children = booking.getNumberOfChildren() != null ? booking.getNumberOfChildren() : 0;
                        int totalPeopleToRestore = adults + children;

                        if (totalPeopleToRestore > 0) {
                            Integer currentBookedSeats = departure.getBookedSeats();
                            departure.setBookedSeats(currentBookedSeats - totalPeopleToRestore);
                            departureDAO.save(departure);
                            System.out.println("  Order " + order.getId() + " restored " + totalPeopleToRestore + " seat(s) for departureId=" + departure.getId());
                        }
                    }

                }
                order.setStatus("CANCELLED");
            }
            orderDAO.saveAll(expiredOrders);
            System.out.println("Cancelled " + expiredOrders.size() + " expired orders.");
        }
    }
}
    }
