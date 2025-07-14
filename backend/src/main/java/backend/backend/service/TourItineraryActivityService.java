package backend.backend.service;

import backend.backend.entity.TourItineraryActivity;
import java.util.List;
import java.util.Optional;

public interface TourItineraryActivityService {

    /**
     * Lưu một hoạt động mới của lịch trình tour.
     *
     * @param tourItineraryActivity đối tượng TourItineraryActivity cần lưu.
     * @return đối tượng TourItineraryActivity đã được lưu.
     */
    TourItineraryActivity createTourItineraryActivity(TourItineraryActivity tourItineraryActivity);

    /**
     * Cập nhật thông tin một hoạt động của lịch trình tour.
     *
     * @param id id của hoạt động cần cập nhật.
     * @param tourItineraryActivityDetails đối tượng chứa thông tin cập nhật.
     * @return đối tượng TourItineraryActivity đã được cập nhật.
     */
    TourItineraryActivity updateTourItineraryActivity(Long id, TourItineraryActivity tourItineraryActivityDetails);

    /**
     * Xóa một hoạt động của lịch trình tour theo id.
     *
     * @param id id của hoạt động cần xóa.
     */
    void deleteTourItineraryActivity(Long id);

    /**
     * Lấy tất cả các hoạt động của lịch trình tour.
     *
     * @return danh sách các TourItineraryActivity.
     */
    List<TourItineraryActivity> getAllTourItineraryActivities();

    /**
     * Tìm một hoạt động của lịch trình tour theo id.
     *
     * @param id id của hoạt động cần tìm.
     * @return một Optional chứa TourItineraryActivity nếu tìm thấy.
     */
    Optional<TourItineraryActivity> getTourItineraryActivityById(Long id);
}