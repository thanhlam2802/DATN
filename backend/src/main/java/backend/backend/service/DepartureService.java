package backend.backend.service;

import backend.backend.dto.DepartureDto;
import java.util.List;

public interface DepartureService {

    /**
     * Lấy danh sách các ngày khởi hành theo ID của tour.
     * @param tourId ID của tour.
     * @return Danh sách các DepartureDto.
     */
    List<DepartureDto> getDeparturesByTourId(Long tourId);

    /**
     * Tạo một ngày khởi hành mới cho một tour.
     * @param tourId ID của tour mà ngày khởi hành này thuộc về.
     * @param departureDto Dữ liệu của ngày khởi hành mới.
     * @return DepartureDto của ngày khởi hành vừa được tạo (bao gồm cả ID mới).
     */
    DepartureDto createDeparture(Long tourId, DepartureDto departureDto);

    /**
     * Cập nhật thông tin một ngày khởi hành đã có.
     * @param departureId ID của ngày khởi hành cần cập nhật.
     * @param departureDto Dữ liệu mới để cập nhật.
     * @return DepartureDto của ngày khởi hành đã được cập nhật.
     */
    DepartureDto updateDeparture(Long departureId, DepartureDto departureDto);

    /**
     * Xóa một ngày khởi hành khỏi hệ thống.
     * @param departureId ID của ngày khởi hành cần xóa.
     */
    void deleteDeparture(Long departureId);


    /**
     * MỚI: Khai báo phương thức tạo hàng loạt ngày khởi hành.
     * Chữ ký này phải khớp với phương thức trong DepartureServiceImpl.
     *
     * @param tourId       ID của tour.
     * @param templateDto  DTO chứa thông tin mẫu (ngày bắt đầu, giá, số chỗ).
     * @param intervalDays Khoảng cách giữa các lần lặp lại (tính bằng ngày).
     * @param count        Số lần lặp lại.
     * @return Danh sách các ngày khởi hành đã được tạo.
     */
    List<DepartureDto> createRecurringDepartures(Long tourId, DepartureDto templateDto, int intervalDays, int count);
}