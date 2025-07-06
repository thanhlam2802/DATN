package backend.backend.exception;


public class BaseException extends RuntimeException {
    protected ErrorCode errorCode;

    public BaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
