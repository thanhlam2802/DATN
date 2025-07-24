import axios from "axios";

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
    }
}