import axios from "axios";

const API_URL = "http://localhost:8080/api/admin";

const apiClient = axios.create({
  baseURL: API_URL,
  headers: { "Content-Type": "application/json" },
});

const handleResponse = (response) => {
  console.log("--- [API] Nhận được phản hồi RAW từ máy chủ: ---", response);
  const apiResponse = response.data;

  if (
    apiResponse &&
    typeof apiResponse.statusCode === "number" &&
    apiResponse.statusCode >= 200 &&
    apiResponse.statusCode < 300
  ) {
    console.log(
      "--- [API] Phản hồi được xác định là THÀNH CÔNG. Trả về data: ---",
      apiResponse.data
    );
    return apiResponse.data;
  } else {
    console.error(
      "--- [API] Phản hồi được xác định là THẤT BẠI (có chủ đích từ backend). Ném lỗi: ---",
      apiResponse.message
    );
    throw new Error(apiResponse.message || "Có lỗi không xác định từ API.");
  }
};

const handleError = (error) => {
  console.error(
    "--- [API] Đã xảy ra lỗi trong khối CATCH của API call. Chi tiết lỗi: ---",
    error
  );
  if (error.response) {
    console.error("--- [API] Backend trả về mã lỗi: ---", {
      status: error.response.status,
      data: error.response.data,
    });
  } else if (error.request) {
    console.error(
      "--- [API] Yêu cầu đã được gửi đi nhưng không nhận được phản hồi (Network Error?): ---",
      error.request
    );
  } else {
    console.error(
      "--- [API] Lỗi xảy ra khi thiết lập yêu cầu: ---",
      error.message
    );
  }
  const message =
    error.response?.data?.message ||
    error.message ||
    "Lỗi kết nối hoặc máy chủ.";
  throw new Error(message);
};

export default {
  getAllTours() {
    console.log("--- [API] Chuẩn bị gọi GET /tours ---");
    return apiClient.get("/tours").then(handleResponse).catch(handleError);
  },
  deleteTour(id) {
    console.log(`--- [API] Chuẩn bị gọi DELETE /tours/${id} ---`);
    return apiClient
      .delete(`/tours/${id}`)
      .then(handleResponse)
      .catch(handleError);
  },
  createTour(formData) {
    console.log("--- [API] Chuẩn bị gọi POST /tours ---");
    return apiClient
      .post("/tours", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      })
      .then(handleResponse)
      .catch(handleError);
  },
  updateTour(id, formData) {
    console.log(`--- [API] Chuẩn bị gọi PUT /tours/${id} ---`);
    return apiClient
      .put(`/tours/${id}`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      })
      .then(handleResponse)
      .catch(handleError);
  },
};
