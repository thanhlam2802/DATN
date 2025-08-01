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
            console.log('AccountApi.getProfile - calling API...');
            const token = getBearerToken();
            console.log('AccountApi.getProfile - token:', token ? 'exists' : 'not found');
            
            const res = await apiClient.get("/v1/account/profile", {
                headers: {
                    Authorization: token
                }
            })
            console.log('AccountApi.getProfile - response:', res.data);
            return res.data;
        } catch (err) {
            console.error('AccountApi.getProfile - error:', err);
            return {
                errorCode: err.response?.data?.errorCode || 'UNKNOWN_ERROR'
            }
        }
    },
}