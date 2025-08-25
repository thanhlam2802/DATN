package backend.backend.controller;

import java.util.List;

import backend.backend.dto.MailResultDto;
import backend.backend.dto.SupplierApplicationDetailDto; // <-- THÊM DÒNG NÀY
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import backend.backend.dto.SupplierApplicationRequest;
import backend.backend.entity.ApiResponse;
import backend.backend.entity.SupplierApplication;
import backend.backend.entity.User;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.service.SupplierService;
import backend.backend.utils.ResponseFactory;
import jakarta.validation.Valid;

/**
 * Controller chịu trách nhiệm xử lý các yêu cầu liên quan đến nhà cung cấp (suppliers).
 */
@RestController
@RequestMapping("/api/v1/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;
    

    
    /**
     * Endpoint cho người dùng đã đăng nhập gửi đơn đăng ký trở thành nhà cung cấp.
     *
     * @param request     Dữ liệu đơn đăng ký từ request body.
     * @param currentUser Người dùng đang đăng nhập, được inject bởi Spring Security.
     * @return ResponseEntity chứa kết quả của việc tạo đơn.
     */
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<SupplierApplication>> applyToBeSupplier(
            @Valid @RequestBody SupplierApplicationRequest request,
            @AuthenticationPrincipal User currentUser) {
        // Giả sử service `createApplication` trả về đối tượng đã lưu
        System.out.println(currentUser);
        SupplierApplication savedApplication = supplierService.createApplication(request, currentUser);
        return ResponseFactory.created(savedApplication, "Đơn đăng ký đã được gửi thành công.");
    }

    /**
     * Endpoint dành cho Admin để phê duyệt một đơn đăng ký.
     *
     * @param applicationId ID của đơn đăng ký cần duyệt.
     * @param roleName      Tên vai trò mới sẽ được gán cho người dùng (ví dụ: HOTEL_SUPPLIER).
     * @return ResponseEntity chứa kết quả của việc phê duyệt.
     */
    @PostMapping("/applications/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveApplication(
            @PathVariable("id") Integer applicationId,
            @RequestParam("role") String roleName) {
        try {
            supplierService.approveApplication(applicationId, roleName); 
            return ResponseFactory.success(null, "Đơn đăng ký đã được phê duyệt thành công.");
        } catch (ResourceNotFoundException e) {
            return ResponseFactory.error(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseFactory.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
 // Trong SupplierController.java
    @GetMapping("/applications/pending")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    // SỬA LẠI 1: Cập nhật kiểu dữ liệu trả về của phương thức
    public ResponseEntity<ApiResponse<List<SupplierApplicationDetailDto>>> getPendingApplications() {
        
        // SỬA LẠI 2: Cập nhật kiểu dữ liệu của biến để khớp với service
        List<SupplierApplicationDetailDto> pendingApplications = supplierService.getPendingApplications();

        return ResponseFactory.success(pendingApplications, "Danh sách đơn chờ duyệt.");
    }
    /**
     * Endpoint dành cho Admin để từ chối một đơn đăng ký.
     *
     * @param applicationId ID của đơn đăng ký cần từ chối.
     * @return ResponseEntity chứa kết quả của việc từ chối.
     */
    @PostMapping("/applications/{id}/reject")
  
    public ResponseEntity<ApiResponse<Void>> rejectApplication(
            @PathVariable("id") Integer applicationId) {
        try {
            supplierService.rejectApplication(applicationId); 
            return ResponseFactory.success(null, "Đơn đăng ký đã được từ chối thành công.");
        } catch (ResourceNotFoundException e) {
            return ResponseFactory.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    @GetMapping("/pending-count")
    public ResponseEntity<ApiResponse<Long>> getPendingApplicationCount() {
        long count = supplierService.countPendingApplications();
        return ResponseFactory.success(count, "Số lượng đơn chờ duyệt.");
    }
    
  

}
