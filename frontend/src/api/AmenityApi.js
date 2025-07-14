import axios from "axios";

const API_BASE_URL = `${import.meta.env.VITE_API_URL}/api/v1/amenities`;

export const getAllAmenities = () => {
    return axios.get(API_BASE_URL);
};

export default {
    getAllAmenities
};