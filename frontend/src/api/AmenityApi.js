import axios from "axios";

const API_BASE_URL = "https://spring-backend-383387500697.asia-southeast1.run.app/api/v1/amenities";

export const getAllAmenities = () => {
    return axios.get(API_BASE_URL);
};

export default {
    getAllAmenities
};