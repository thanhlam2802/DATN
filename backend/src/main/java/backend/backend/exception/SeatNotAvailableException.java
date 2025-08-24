package backend.backend.exception;

public class SeatNotAvailableException  extends BusBookingException {

    public SeatNotAvailableException(String seatNumber, String message) {
        super("SEAT_NOT_AVAILABLE", String.format("Seat %s: %s", seatNumber, message));
    }
}
