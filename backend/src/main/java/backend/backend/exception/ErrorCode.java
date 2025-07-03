package backend.backend.exception;

public enum ErrorCode {
    AUTH_001("AUTH", "001", 400),
    AUTH_002("AUTH", "002", 400);
    public final String function;
    public final String code;
    public final int statusCode;

    ErrorCode(String function, String code, int statusCode) {
        this.function = function;
        this.code = code;
        this.statusCode = statusCode;
    }
}
