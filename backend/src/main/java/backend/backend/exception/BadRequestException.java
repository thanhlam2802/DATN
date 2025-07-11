package backend.backend.exception;

public class BadRequestException extends BaseException {
    public BadRequestException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
