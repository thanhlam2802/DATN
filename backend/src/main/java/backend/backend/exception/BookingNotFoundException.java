package backend.backend.exception;

public class BookingNotFoundException extends BusBookingException {

    public BookingNotFoundException(String bookingId, String message) {
        super("BOOKING_NOT_FOUND", String.format("Booking %s: %s", bookingId, message));
    }
}
