import axios from "axios";
import { getBearerToken } from "@/services/TokenService"; // Import hàm lấy Bearer Token

// Cấu hình chung cho axios
const apiClient = axios.create({
  baseURL: "http://localhost:8080/api/v1",
  headers: {
    "Content-Type": "application/json",
  },
});

/**
 * Interceptor: Đoạn mã này sẽ chạy trước MỖI request được gửi đi.
 * Nó sẽ tự động lấy token và đính kèm vào header 'Authorization'.
 * Đây là cách làm chuyên nghiệp và giúp code ở các component sạch sẽ hơn.
 */
apiClient.interceptors.request.use(
  (config) => {
    const token = getBearerToken();
    if (token) {
      config.headers["Authorization"] = token;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Tập hợp các hàm gọi API liên quan đến chức năng đánh giá
export const reviewService = {
  /**
   * Lấy danh sách các dịch vụ đã hoàn thành cho một user
   * @param {number} userId - ID của người dùng
   * @returns {Promise<axios.AxiosResponse<any>>}
   */
  getCompletedServices(userId) {
    // Bây giờ không cần truyền header nữa, interceptor sẽ tự làm
    return apiClient.get(`/reviews/user/${userId}/completed-services`);
  },

  /**
   * Gửi một đánh giá mới lên server
   * @param {object} reviewData - Dữ liệu từ ReviewRequestDTO
   * @returns {Promise<axios.AxiosResponse<any>>}
   */
  createReview(reviewData) {
    return apiClient.post("/reviews", reviewData);
  },
};

// Bạn có thể export thêm các service khác ở đây
// export const orderService = { ... };
