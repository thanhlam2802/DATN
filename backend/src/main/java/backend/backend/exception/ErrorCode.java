package backend.backend.exception;

public enum ErrorCode {
    AUTH_001("AUTH", "001", 400, "User already exists"),
    AUTH_002("AUTH", "002", 400, "User not found"),
    AUTH_003("AUTH", "003", 401, "Invalid credentials"),
    AUTH_004("AUTH", "004", 401, "Invalid password");
    public final String function;
    public final String code;
    public final int statusCode;
    public final String description;

    ErrorCode(String function, String code, int statusCode, String description) {
        this.function = function;
        this.code = code;
        this.statusCode = statusCode;
        this.description = description;
    }

    public String getFullErrorCode() {
        return function + ":" + code + ":" + statusCode;
    }
}
