// File: src/api/bookingTourApi.js

import axios from "axios";

// Cấu hình một instance axios.
const apiClient = axios.create({
  baseURL: "http://localhost:8080", // Trỏ đến gốc của backend
  headers: {
    "Content-Type": "application/json",
    // 'Authorization': `Bearer ${token}` // Thêm token xác thực nếu cần
  },
});

// Helper xử lý response từ backend
const handleResponse = (response) => {
  // SỬA LỖI TẠI ĐÂY: Kiểm tra statusCode là số 200
  if (response.data && response.data.statusCode === 200) {
    return response.data.data; // Trả về phần dữ liệu chính
  }
  return Promise.reject(response.data);
};

// Helper để xử lý lỗi
const handleError = (error) => {
  console.error("API call failed. ", error);
  throw error;
};

//================================================================
// API cho Tour
//================================================================
export const tourApi = {
  /**
   * Lấy tất cả tour cho trang admin.
   * Gọi đến: GET /api/admin/tours
   */
  getAll: () => {
    return apiClient
      .get("/api/admin/tours")
      .then(handleResponse)
      .catch(handleError);
  },
};

//================================================================
// API cho Ngày khởi hành
//================================================================
export const departureApi = {
  /**
   * Lấy danh sách ngày khởi hành theo ID của tour.
   * Gọi đến: GET /api/tours/{tourId}/departures
   */
  getByTourId: (tourId) => {
    return apiClient
      .get(`/api/tours/${tourId}/departures`)
      .then(handleResponse)
      .catch(handleError);
  },
};

//================================================================
// API cho Booking
//================================================================
const formatBookingForFrontend = (dto) => {
  const mapBookingStatus = (status) => {
    if (status === "PAID" || status === "CONFIRMED") return "Đã xác nhận";
    if (status === "PENDING_PAYMENT") return "Chờ xác nhận";
    if (status === "CANCELLED") return "Đã hủy";
    return status;
  };
  const mapPaymentStatus = (status) => {
    if (status === "PAID") return "Đã thanh toán";
    if (status === "PENDING_PAYMENT") return "Chờ thanh toán";
    return status;
  };

  return {
    id: dto.bookingId,
    tour: dto.tourName,
    customer: dto.name,
    phone: dto.phone,
    email: dto.email,
    bookingDate: dto.bookingDate,
    tourDate: dto.departureDate,
    guests: (dto.numberOfAdults || 0) + (dto.numberOfChildren || 0),
    totalAmount: dto.totalPrice.toLocaleString("vi-VN"),
    status: mapBookingStatus(dto.orderStatus),
    paymentStatus: mapPaymentStatus(dto.orderStatus),
    notes: dto.note,
  };
};

export const bookingApi = {
  /**
   * [ADMIN] Lấy danh sách tất cả booking để quản lý.
   */
  getAdminBookings: async (params = {}) => {
    const response = await apiClient.get("/api/v1/bookings/tours/admin/all", {
      params,
    });
    const rawData = handleResponse(response);
    return rawData.map(formatBookingForFrontend);
  },

  /**
   * [USER] Lấy danh sách booking của người dùng đang đăng nhập.
   */
  getMyBookings: async () => {
    const response = await apiClient.get("/api/v1/bookings/tours/my-bookings");
    const rawData = handleResponse(response);
    return rawData.map(formatBookingForFrontend);
  },

  /**
   * [USER] Tạo một booking mới (thêm vào giỏ hàng).
   */
  create: (bookingRequest) => {
    return apiClient
      .post("/api/v1/bookings/tours", bookingRequest)
      .then(handleResponse)
      .catch(handleError);
  },

  // --- BỔ SUNG CÁC HÀM CÒN THIẾU ---
  /**
   * [ADMIN] Xác nhận một booking.
   * Giả sử endpoint là: POST /api/admin/bookings/tours/{id}/confirm
   */
  confirm: (bookingId) => {
    return apiClient
      .post(`/api/admin/bookings/tours/${bookingId}/confirm`)
      .then(handleResponse)
      .catch(handleError);
  },

  /**
   * [ADMIN] Hủy (từ chối) một booking.
   * Giả sử endpoint là: POST /api/admin/bookings/tours/{id}/cancel
   */
  cancel: (bookingId) => {
    return apiClient
      .post(`/api/admin/bookings/tours/${bookingId}/cancel`)
      .then(handleResponse)
      .catch(handleError);
  },
};
