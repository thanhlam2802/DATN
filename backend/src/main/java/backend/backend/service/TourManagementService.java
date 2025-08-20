package backend.backend.service;

import backend.backend.dto.*;
import backend.backend.entity.TourStatus;

import java.util.List;

/**
 * Interface định nghĩa các dịch vụ quản lý tour phức tạp,
 * phục vụ cho các API tổng hợp và thống kê trên dashboard.
 */
public interface TourManagementService {

    /**
     * Lấy danh sách tóm tắt các nhà cung cấp và số lượng tour của họ.
     * Tên nhà cung cấp được lấy từ 'businessName' trong SupplierApplication đã được duyệt.
     *
     * @return Danh sách các DTO chứa thông tin tóm tắt của nhà cung cấp.
     */
    List<VendorSummaryDTO> getVendorSummary();

    /**
     * Tìm kiếm và lọc danh sách tour theo nhiều tiêu chí.
     *
     * @param search      Chuỗi tìm kiếm theo tên tour.
     * @param status      Trạng thái của tour (ACTIVE, PENDING, DISABLED).
     * @param vendorId    ID của nhà cung cấp (owner).
     * @param destination Tên của điểm đến.
     * @return Danh sách các tour chi tiết (dạng DTO) phù hợp với tiêu chí.
     */
    List<TourListAdminDTO> findToursByCriteria(String search, TourStatus status, Long vendorId, String destination);

    /**
     * Cập nhật trạng thái của một tour.
     *
     * @param id     ID của tour cần cập nhật.
     * @param status Trạng thái mới (ví dụ: DISABLED).
     * @return DTO chi tiết của tour sau khi đã cập nhật.
     */
    TourListAdminDTO updateTourStatus(Long id, TourStatus status);

    
}