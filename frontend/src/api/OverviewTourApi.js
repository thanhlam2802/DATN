import axios from "axios";

const DASHBOARD_API_URL = "/api/v1/dashboardTour";

/**
 * NÂNG CẤP: Một hàm duy nhất để lấy tất cả dữ liệu cho trang tổng quan.
 * @param {object} params - Các tham số lọc và phân trang.
 * @param {number} params.userId - ID của người dùng đang đăng nhập.
 * @param {string|null} params.startDate - Ngày bắt đầu (YYYY-MM-DD).
 * @param {string|null} params.endDate - Ngày kết thúc (YYYY-MM-DD).
 * @param {number} params.page - Số trang hiện tại.
 * @param {number} params.size - Kích thước trang.
 * @returns {Promise} - Promise chứa response từ API.
 */
const getOverviewData = (params) => {
  // Hàm này đã linh hoạt, không cần thay đổi.
  // Component gọi hàm này sẽ chịu trách nhiệm thêm userId vào object params.
  return axios.get(`${DASHBOARD_API_URL}/overview`, { params });
};

// Xuất ra một object chứa hàm mới
export default { getOverviewData };
