package backend.backend.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import backend.backend.entity.ApiResponse;

public class ResponseFactory {

    /**
     * Tạo một response thành công với mã 200 OK.
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>(HttpStatus.OK.value(), message, data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return success(data, "Yêu cầu thực hiện thành công.");
    }

    /**
     * Tạo một response khi tạo mới thành công với mã 201 CREATED.
     */
    public static <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>(HttpStatus.CREATED.value(), message, data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return created(data, "Tạo mới thành công.");
    }

    /**
     * Tạo một response lỗi.
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String message, T data) {
        ApiResponse<T> response = new ApiResponse<>(status.value(), message, data);
        return new ResponseEntity<>(response, status);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String message) {
        return error(status, message, null);
    }
}