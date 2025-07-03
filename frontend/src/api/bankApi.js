import axios from "axios";

const API_BASE_URL = "https://api.vietqr.io/v2/banks"

export const getBankList = () => {
    return axios.get(API_BASE_URL);
};