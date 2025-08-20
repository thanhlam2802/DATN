import axios from "axios";
import { getBearerToken } from "@/services/TokenService";

// URL cơ sở của backend
const BASE_URL = "http://localhost:8080/api";

/**
 * Hàm trợ giúp để tạo headers chứa token xác thực.
 * @returns {object} - Object chứa headers cho request.
 */
const authHeaders = () => {
  const token = getBearerToken();
  if (!token) {
    // Ném lỗi sớm nếu không tìm thấy token để tránh gửi request không hợp lệ
    throw new Error("Không tìm thấy token người dùng. Vui lòng đăng nhập lại.");
  }
  return {
    headers: {
      "Content-Type": "application/json",
      Authorization: token,
    },
  };
};

export const SupplierApi = {
  /**
   * Lấy danh sách các đơn đăng ký đang chờ duyệt.
   */
  getPendingApplications: async () => {
    // SỬA LỖI: Sửa lại đường dẫn API cho đúng với backend. Thêm "/applications".
    const response = await axios.get(
      `${BASE_URL}/v1/suppliers/applications/pending`,
      authHeaders()
    );
    return response.data;
  },

  /**
   * Phê duyệt một đơn đăng ký.
   * @param {number} id - ID của đơn đăng ký.
   * @param {string} roleName - Tên vai trò mới sẽ được gán cho người dùng (ví dụ: 'HOTEL_SUPPLIER').
   */
  approveApplication: async (id, roleName) => {
    // SỬA LỖI:
    // 1. Đổi phương thức từ .put thành .post để khớp với @PostMapping ở backend.
    // 2. Sửa lại đường dẫn API, thêm "/applications".
    // 3. Thêm tham số `role` vào URL vì backend yêu cầu.
    const response = await axios.post(
      `${BASE_URL}/v1/suppliers/applications/${id}/approve?role=${roleName}`,
      null, // Không cần gửi body cho request này
      authHeaders()
    );
    return response.data;
  },

  /**
   * Từ chối một đơn đăng ký.
   * @param {number} id - ID của đơn đăng ký.
   */
  rejectApplication: async (id) => {
    // SỬA LỖI:
    // 1. Đổi phương thức từ .put thành .post.
    // 2. Sửa lại đường dẫn API, thêm "/applications".
    const response = await axios.post(
      `${BASE_URL}/v1/suppliers/applications/${id}/reject`,
      null, // Không cần gửi body
      authHeaders()
    );
    return response.data;
  },

  /**
   * Gửi đơn đăng ký làm nhà cung cấp.
   * (Hàm này đã đúng với controller nên không cần sửa)
   * @param {object} applicationData - Dữ liệu của đơn đăng ký.
   */
  submitApplication: async (applicationData) => {
    const response = await axios.post(
      `${BASE_URL}/v1/suppliers/apply`,
      applicationData,
      authHeaders()
    );
    return response.data;
  },
};
