import axios from "axios";
import { saveAccessToken } from "@/services/TokenService";
const apiClient = axios.create({
  baseURL: "http://localhost:8080/api", // sửa lại nếu baseURL khác
  headers: {
    "Content-Type": "application/json",
  },
});

export const AuthApi = {
  register: async (request) => {
    try {
      const response = await apiClient.post("/v1/auth/register", request);
      console.log("Register response", response.data);
      return response.data;
    } catch (err) {
      throw err.response?.data || err;
    }
  },

  login: async (request) => {
    try {
      const res = await apiClient.post("/v1/auth/login", request);
      if (res.data && res.data.accessToken) {
        saveAccessToken(res.data.accessToken);
      }
      return res.data;
    } catch (err) {
      throw err.response?.data || err;
    }
  },
  resetPassWord: async (request) => {
    try {
      const res = await apiClient.post(
        "/v1/auth/forgot-password/reset",
        request
      );
      return res.data;
    } catch (err) {
      throw err.response?.data || err;
    }
  },
  verifyResetPassLink: async (request) => {
    try {
      const res = await apiClient.post(
        "/v1/auth/reset-password/verify-link",
        request
      );
      return res.data;
    } catch (err) {
      throw err.response?.data || err;
    }
  },
  verifyAccount: async (request) => {
    try {
      const res = await apiClient.post("/v1/auth/verify-account", request);
      return res.data;
    } catch (err) {
      throw err.response?.data || err;
    }
  },
  verifyAccountResend: async (request) => {
    try {
      const res = await apiClient.post(
        "/v1/auth/verify-account/resend",
        request
      );
      return res.data;
    } catch (err) {
      throw err.response?.data || err;
    }
  },
};
