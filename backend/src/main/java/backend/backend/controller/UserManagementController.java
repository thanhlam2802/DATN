package backend.backend.controller;

import backend.backend.dto.UserManagementDto;
import backend.backend.dto.UserCreateRequestDto;
import backend.backend.dto.UserUpdateRequestDto;
import backend.backend.entity.ApiResponse;
import backend.backend.service.UserManagementService;
import backend.backend.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class UserManagementController {

    private final UserManagementService userManagementService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserManagementDto>>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        Page<UserManagementDto> users = userManagementService.getAllUsers(search, role, status, pageable);
        return ResponseFactory.success(users, "Lấy danh sách người dùng thành công");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserManagementDto>> getUserById(@PathVariable Integer id) {
        UserManagementDto user = userManagementService.getUserById(id);
        return ResponseFactory.success(user, "Lấy thông tin người dùng thành công");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserManagementDto>> createUser(@Valid @RequestBody UserCreateRequestDto request) {
        UserManagementDto user = userManagementService.createUser(request);
        return ResponseFactory.created(user, "Tạo người dùng thành công");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserManagementDto>> updateUser(
            @PathVariable Integer id, 
            @Valid @RequestBody UserUpdateRequestDto request) {
        UserManagementDto user = userManagementService.updateUser(id, request);
        return ResponseFactory.success(user, "Cập nhật người dùng thành công");
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserManagementDto>> updateUserStatus(
            @PathVariable Integer id, 
            @RequestParam String status) {
        UserManagementDto user = userManagementService.updateUserStatus(id, status);
        return ResponseFactory.success(user, "Cập nhật trạng thái người dùng thành công");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteUser(@PathVariable Integer id) {
        boolean result = userManagementService.deleteUser(id);
        return ResponseFactory.success(result, "Xóa người dùng thành công");
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Object>> getUserStatistics() {
        Object statistics = userManagementService.getUserStatistics();
        return ResponseFactory.success(statistics, "Lấy thống kê người dùng thành công");
    }
}
