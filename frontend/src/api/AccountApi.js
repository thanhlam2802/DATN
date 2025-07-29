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
    getProfile: async () => {
        try {
            const res = await apiClient.get("/v1/account/profile", {
                headers: {
                    Authorization: getBearerToken()
                }
            })
            return res.data;
        } catch (err) {
            return {
                errorCode: err.response.data.errorCode
            }
        }
    },
}