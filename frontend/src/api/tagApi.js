import axios from "axios";

const apiClient = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: { "Content-Type": "application/json" },
});

// Xử lý lỗi chung
const handleError = (error) => {
  const message =
    error.response?.data?.message ||
    error.message ||
    "Lỗi kết nối hoặc máy chủ.";
  console.error(`Lỗi API Tags: ${message}`);
  throw new Error(message);
};

export default {
  getAllTags() {
    console.log("--- [API] Chuẩn bị gọi GET /api/tags ---");

    return apiClient
      .get("/tags")
      .then((response) => response.data)
      .catch(handleError);
  },
};
