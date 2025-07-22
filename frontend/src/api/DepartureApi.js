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
  // Kiểm tra cấu trúc response chuẩn của bạn
  if (
    response.data &&
    (response.data.statusCode === 200 || response.data.statusCode === 201)
  ) {
    // Trả về phần data chứa dữ liệu chính
    return response.data.data;
  }
  // Nếu cấu trúc không khớp, có thể backend trả về dữ liệu trực tiếp
  if (response.status === 200 || response.status === 201) {
    return response.data;
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
  console.error("Lỗi API:", errorMessage, error.response);
  // Ném lỗi để component có thể bắt và xử lý
  throw new Error(errorMessage);
};

// --- CÁC HÀM API ---

// Tạo một object chứa tất cả các hàm gọi API liên quan đến Departure
export const departureApi = {
  /**
   * Tạo một ngày khởi hành mới cho một tour.
   * Gửi request: POST /tours/{tourId}/departures
   * @param {number|string} tourId - ID của tour.
   * @param {object} departureData - Dữ liệu của ngày khởi hành mới.
   * @returns {Promise<object>} Dữ liệu của ngày khởi hành vừa được tạo.
   */
  createDeparture(tourId, departureData) {
    const url = `/tours/${tourId}/departures`;
    console.log(`--- [API] Gọi POST ${url} ---`, departureData);
    return apiClient
      .post(url, departureData)
      .then(handleResponse)
      .catch(handleError);
  },

  /**
   * MỚI: Tạo hàng loạt ngày khởi hành cho một tour theo chu kỳ.
   * Gửi request: POST /tours/{tourId}/departures/recurring
   * @param {number|string} tourId - ID của tour.
   * @param {object} data - Dữ liệu bao gồm { templateDto, intervalDays, count }.
   * @returns {Promise<Array<object>>} Mảng các ngày khởi hành vừa được tạo.
   */
  createRecurringDepartures(tourId, data) {
    // URL này phải khớp với endpoint bạn đã định nghĩa trong Controller ở backend
    const url = `/tours/${tourId}/departures/recurring`;
    console.log(`--- [API] Gọi POST ${url} ---`, data);
    return apiClient.post(url, data).then(handleResponse).catch(handleError);
  },

  /**
   * Cập nhật một ngày khởi hành đã có.
   * Gửi request: PUT /departures/{departureId}
   * @param {number|string} departureId - ID của ngày khởi hành cần cập nhật.
   * @param {object} departureData - Dữ liệu cập nhật.
   * @returns {Promise<object>} Dữ liệu của ngày khởi hành sau khi cập nhật.
   */
  updateDeparture(departureId, departureData) {
    const url = `/departures/${departureId}`;
    console.log(`--- [API] Gọi PUT ${url} ---`, departureData);
    return apiClient
      .put(url, departureData)
      .then(handleResponse)
      .catch(handleError);
  },

  /**
   * Xóa một ngày khởi hành.
   * Gửi request: DELETE /departures/{departureId}
   * @param {number|string} departureId - ID của ngày khởi hành cần xóa.
   * @returns {Promise<any>}
   */
  deleteDeparture(departureId) {
    const url = `/departures/${departureId}`;
    console.log(`--- [API] Gọi DELETE ${url} ---`);
    return apiClient.delete(url).then(handleResponse).catch(handleError);
  },
};
