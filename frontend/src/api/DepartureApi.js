import axios from "axios";

// --- CẤU HÌNH AXIOS ---
// Tạo một instance của axios để sử dụng trong file này
const apiClient = axios.create({
  // !!! QUAN TRỌNG: Thay đổi URL này thành địa chỉ backend của bạn !!!
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

/* Tùy chọn: Thêm Interceptor để tự động gắn token xác thực vào mỗi request
  Nếu hệ thống của bạn có đăng nhập, bạn có thể bỏ comment phần này.
*/
// apiClient.interceptors.request.use(config => {
//   const token = localStorage.getItem('authToken'); // Lấy token từ localStorage hoặc store
//   if (token) {
//     config.headers.Authorization = `Bearer ${token}`;
//   }
//   return config;
// }, error => {
//   return Promise.reject(error);
// });

// --- CÁC HÀM XỬ LÝ CHUNG ---

/**
 * Xử lý response thành công từ API.
 * @param {object} response - Response từ axios.
 * @returns {object} Dữ liệu trong `response.data.data`.
 */
const handleResponse = (response) => {
  if (
    response.data &&
    (response.data.statusCode === 200 || response.data.statusCode === 201)
  ) {
    return response.data.data;
  }
  throw new Error(response.data.message || "Có lỗi xảy ra.");
};

/**
 * Xử lý lỗi từ API.
 * @param {object} error - Đối tượng lỗi từ axios.
 */
const handleError = (error) => {
  const errorMessage =
    error.response?.data?.message || error.message || "Lỗi không xác định.";
  console.error("Lỗi API:", errorMessage);
  throw new Error(errorMessage);
};

// --- CÁC HÀM API ---

// Tạo một object chứa tất cả các hàm gọi API liên quan đến Departure
export const departureApi = {
  /**
   * Tạo một ngày khởi hành mới cho một tour.
   * Gửi request: POST /tours/{tourId}/departures
   * @param {number} tourId - ID của tour.
   * @param {object} departureData - Dữ liệu của ngày khởi hành mới.
   * @returns {Promise<object>} Dữ liệu của ngày khởi hành vừa được tạo.
   */
  createDeparture(tourId, departureData) {
    console.log(`--- [API] Gọi POST /tours/${tourId}/departures ---`);
    return apiClient
      .post(`/tours/${tourId}/departures`, departureData)
      .then(handleResponse)
      .catch(handleError);
  },

  /**
   * Cập nhật một ngày khởi hành đã có.
   * Gửi request: PUT /departures/{departureId}
   * @param {number} departureId - ID của ngày khởi hành cần cập nhật.
   * @param {object} departureData - Dữ liệu cập nhật.
   * @returns {Promise<object>} Dữ liệu của ngày khởi hành sau khi cập nhật.
   */
  updateDeparture(departureId, departureData) {
    console.log(`--- [API] Gọi PUT /departures/${departureId} ---`);
    return apiClient
      .put(`/departures/${departureId}`, departureData)
      .then(handleResponse)
      .catch(handleError);
  },

  /**
   * Xóa một ngày khởi hành.
   * Gửi request: DELETE /departures/{departureId}
   * @param {number} departureId - ID của ngày khởi hành cần xóa.
   * @returns {Promise<any>}
   */
  deleteDeparture(departureId) {
    console.log(`--- [API] Gọi DELETE /departures/${departureId} ---`);
    return apiClient
      .delete(`/departures/${departureId}`)
      .then(handleResponse)
      .catch(handleError);
  },
};
