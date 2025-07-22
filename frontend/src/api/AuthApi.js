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
        return apiClient.post("/v1/auth/register", request).then(res => {
            return res.data
        })
    },
    login: async (request) => {
        return apiClient.post("/v1/auth/login", request).then(res => {
            return res.data
        })
    }
}