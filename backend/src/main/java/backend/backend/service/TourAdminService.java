package backend.backend.service;

import backend.backend.dto.TourDetailAdminDTO;
import backend.backend.dto.TourRequestDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * Interface định nghĩa các chức năng quản trị tour.
 * Việc sử dụng interface giúp mã nguồn linh hoạt, dễ dàng thay đổi implementation
 * và thuận tiện cho việc viết Unit Test.
 */
public interface TourAdminService {

    /**
     * Lấy danh sách tất cả các tour cho trang quản trị.
     * @return Danh sách các DTO tour.
     */
    List<TourDetailAdminDTO> getAllToursForAdmin();

    /**
     * Lấy chi tiết một tour bằng ID.
     * @param id ID của tour cần tìm.
     * @return DTO chi tiết của tour.
     * @throws jakarta.persistence.EntityNotFoundException nếu không tìm thấy tour.
     */
    TourDetailAdminDTO getTourById(Long id);

    /**
     * Tạo một tour mới.
     * @param tourRequestDTO Dữ liệu của tour từ request.
     * @param images Danh sách các file hình ảnh tải lên.
     * @return DTO của tour vừa được tạo.
     */
    TourDetailAdminDTO createTour(TourRequestDTO tourRequestDTO, List<MultipartFile> images);

    /**
     * Cập nhật một tour đã có.
     * @param id ID của tour cần cập nhật.
     * @param tourRequestDTO Dữ liệu mới của tour.
     * @param newImages Danh sách các file hình ảnh mới được thêm vào.
     * @return DTO của tour đã được cập nhật.
     * @throws jakarta.persistence.EntityNotFoundException nếu không tìm thấy tour.
     */
    TourDetailAdminDTO updateTour(Long id, TourRequestDTO tourRequestDTO, List<MultipartFile> newImages);

    /**
     * Xóa một tour khỏi hệ thống.
     * @param id ID của tour cần xóa.
     * @throws jakarta.persistence.EntityNotFoundException nếu không tìm thấy tour.
     */
    void deleteTour(Long id);
}