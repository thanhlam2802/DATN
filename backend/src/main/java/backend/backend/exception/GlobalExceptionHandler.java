package backend.backend.exception;

import backend.backend.entity.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(org.springframework.security.authentication.AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAuth(AuthenticationCredentialsNotFoundException ex) {
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("status", 401);
        body.put("error", "UNAUTHORIZED");
        body.put("message", "Bạn chưa đăng nhập");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Error");
        response.put("message", "Dữ liệu đầu vào không hợp lệ");
        response.put("errors", errors);

        log.error("VALIDATION_ERROR - Errors: {}", errors);
        
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Invalid Argument");
        response.put("message", ex.getMessage());

        log.error("ILLEGAL_ARGUMENT_ERROR - Message: {}", ex.getMessage());
        
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Internal Server Error");
        response.put("message", "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.");

        log.error("RUNTIME_ERROR - Message: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(exception = BadRequestException.class)
    ResponseEntity<?> handleBadRequestException(BadRequestException e) {
        ApiResponse<?> apiResponse = new ApiResponse<>(400, e.getMessage(), e.errorCode.getFullErrorCode(), null);
        return ResponseEntity
                .badRequest()
                .body(apiResponse);
    }

    @ExceptionHandler(exception = AuthException.class)
    ResponseEntity<?> handleAuthException(AuthException e) {
        ApiResponse<?> apiResponse = new ApiResponse<>(401, e.getMessage(), e.errorCode.getFullErrorCode(), null);
        return ResponseEntity.status(401).body(apiResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Internal Server Error");
        response.put("message", "Đã xảy ra lỗi không xác định. Vui lòng thử lại sau.");

        log.error("GENERIC_ERROR - Message: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
} 