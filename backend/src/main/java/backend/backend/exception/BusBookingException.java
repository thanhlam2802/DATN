package backend.backend.exception;

public class BusBookingException extends RuntimeException {
    private final String errorCode;

    public BusBookingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusBookingException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}