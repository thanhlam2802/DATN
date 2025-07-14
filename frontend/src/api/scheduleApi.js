import axios from "axios";

// API này trỏ đến TourScheduleController
const apiClient = axios.create({
  baseURL: "http://localhost:8080/api/v1/tour-schedules",
  headers: { "Content-Type": "application/json" },
});

const handleApiError = (error) => {
  const message =
    error.response?.data?.message ||
    error.message ||
    "Lỗi kết nối hoặc máy chủ.";
  console.error(`Lỗi API Lịch trình: ${message}`, error);
  throw new Error(message);
};

export default {
  /**
   * Tạo một lịch trình mới.
   * @param {object} scheduleData - Dữ liệu DTO của lịch trình.
   */
  create(scheduleData) {
    // POST đến /api/v1/tour-schedules
    return apiClient
      .post("", scheduleData)
      .then((res) => res.data)
      .catch(handleApiError);
  },

  /**
   * Cập nhật một lịch trình.
   * @param {number} id - ID của lịch trình cần cập nhật.
   * @param {object} scheduleData - Dữ liệu DTO mới.
   */
  update(id, scheduleData) {
    // PUT đến /api/v1/tour-schedules/{id}
    return apiClient
      .put(`/${id}`, scheduleData)
      .then((res) => res.data)
      .catch(handleApiError);
  },

  /**
   * Xóa một lịch trình.
   * @param {number} id - ID của lịch trình cần xóa.
   */
  delete(id) {
    // DELETE đến /api/v1/tour-schedules/{id}
    return apiClient
      .delete(`/${id}`)
      .then((res) => res.data)
      .catch(handleApiError);
  },
};
