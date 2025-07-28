import axios from "axios";

// Cấu hình một axios instance với baseURL trỏ đến API của bạn.
// Điều này giúp bạn không cần lặp lại 'http://localhost:8080' ở mọi nơi.
const apiClient = axios.create({
  baseURL: "http://localhost:8080/api", // Đảm bảo đây là địa chỉ backend của bạn
  headers: {
    "Content-Type": "application/json",
  },
});

// Xuất ra một đối tượng chứa tất cả các hàm gọi API liên quan đến Voucher
export default {
  /**
   * Lấy tất cả voucher
   */
  getAllVouchers() {
    return apiClient.get("/vouchers");
  },

  /**
   * Lấy voucher theo ID
   * @param {number} id - ID của voucher
   */
  getVoucherById(id) {
    return apiClient.get(`/vouchers/${id}`);
  },

  /**
   * Lấy voucher theo mã code
   * @param {string} code - Mã code của voucher
   */
  getVoucherByCode(code) {
    return apiClient.get(`/vouchers/code/${code}`);
  },

  /**
   * Tạo một voucher mới
   * @param {object} voucherData - Dữ liệu của voucher (VoucherDTO)
   */
  createVoucher(voucherData) {
    return apiClient.post("/vouchers", voucherData);
  },

  /**
   * Cập nhật một voucher
   * @param {number} id - ID của voucher cần cập nhật
   * @param {object} voucherData - Dữ liệu mới của voucher (VoucherDTO)
   */
  updateVoucher(id, voucherData) {
    return apiClient.put(`/vouchers/${id}`, voucherData);
  },

  /**
   * Xóa một voucher
   * @param {number} id - ID của voucher cần xóa
   */
  deleteVoucher(id) {
    return apiClient.delete(`/vouchers/${id}`);
  },
};
