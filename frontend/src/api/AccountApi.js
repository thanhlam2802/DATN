import axios from "axios";
import {getBearerToken} from "@/services/TokenService.js";

const BASE_URL = import.meta.env.VITE_API_URL;


const apiClient = axios.create({
    baseURL: BASE_URL + "/api",
    headers: {
        "Content-Type": "application/json",
    },
});

export const AccountApi = {
    getProfile: async (request) => {
        return apiClient.get("/v1/account/profile", {
            headers: {
                Authorization: getBearerToken()
            }
        }).then(res => {
            return res.data
        })
    },
}