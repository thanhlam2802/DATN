import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api/v1/provinces";

export const getAllProvinces = () => {
  return axios.get(API_BASE_URL);
};

export default {
    getAllProvinces
};