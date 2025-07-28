import axios from "axios";
import {getBearerToken} from "@/services/TokenService.js";

const BASE_URL = import.meta.env.VITE_API_URL;


const apiClient = axios.create({
    baseURL: BASE_URL + "/api",
    headers: {
        "Content-Type": "application/json",
    },
});


export const AuthApi = {
    register: async (request) => {
        try {
            const res = await apiClient.post("/v1/auth/register", request);
            return res.data;
        } catch (err) {
            return {
                errorCode: err.response.data.errorCode
            }
        }
    },
    login: async (request) => {
        try {
            const res = await apiClient.post("/v1/auth/login", request)
            return res.data;
        } catch (err) {
            return {
                errorCode: err.response.data.errorCode
            }
        }
    },
    resetPassWord: async (request) => {
        try {
            const res = await apiClient.post("/v1/auth/forgot-password/reset", request)
            return res.data;
        } catch (err) {
            return {
                errorCode: err.response.data.errorCode
            }
        }
    },
    verifyResetPassLink: async (request) => {
        try {
            const res = await apiClient.post("/v1/auth/reset-password/verify-link", request)
            return res.data;
        } catch (err) {
            return {
                errorCode: err.response.data.errorCode
            }
        }
    },
    verifyAccount: async (request) => {
        try {
            const res = await apiClient.post("/v1/auth/verify-account", request)
            return res.data;
        } catch (err) {
            return {
                errorCode: err.response.data.errorCode
            }
        }
    },
    verifyAccountResend: async (request) => {
        try {
            const res = await apiClient.post("/v1/auth/verify-account/resend", request)
            return res.data;
        } catch (err) {
            return {
                errorCode: err.response.data.errorCode
            }
        }
    }
}