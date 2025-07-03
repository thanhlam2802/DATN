package backend.backend.exception;

import backend.backend.entity.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(exception = BadRequestException.class)
    ResponseEntity<?> handleBadRequestException(BadRequestException e) {
        ApiResponse<?> apiResponse = new ApiResponse<>(400, e.getMessage(), e.errorCode.code, null);
        return ResponseEntity
                .badRequest()
                .body(apiResponse);
    }
}
