package backend.backend.exception;

public class AuthException extends BaseException {
    public AuthException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
