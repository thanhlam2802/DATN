package backend.backend.service;

import java.util.List;

import backend.backend.dto.SupplierApplicationDetailDto;
import backend.backend.dto.SupplierApplicationRequest;
import backend.backend.entity.SupplierApplication;
import backend.backend.entity.User;

public interface SupplierService {

    /**
     * Tạo một đơn đăng ký mới để trở thành nhà cung cấp.
     *
     * @param request DTO chứa thông tin đơn đăng ký từ người dùng.
     * @param user    Đối tượng người dùng đã được xác thực đang gửi đơn.
     */
	SupplierApplication  createApplication(SupplierApplicationRequest request, User user);

    /**
     * Phê duyệt một đơn đăng ký nhà cung cấp.
     * Phương thức này sẽ thay đổi trạng thái đơn và cập nhật vai trò của người dùng.
     *
     * @param applicationId ID của đơn đăng ký cần phê duyệt.
     */
    void approveApplication(Integer applicationId, String newRoleName);

    /**
     * Từ chối một đơn đăng ký nhà cung cấp.
     * Phương thức này sẽ thay đổi trạng thái của đơn đăng ký.
     *
     * @param applicationId ID của đơn đăng ký cần từ chối.
     */
    void rejectApplication(Integer applicationId);
    
    /**
     * Đếm số lượng đơn đăng ký nhà cung cấp đang chờ duyệt.
     *
     * @return Số lượng đơn đăng ký với trạng thái CHỜ DUYỆT.
     */
    long countPendingApplications();
    
    List<SupplierApplicationDetailDto> getPendingApplications();
}