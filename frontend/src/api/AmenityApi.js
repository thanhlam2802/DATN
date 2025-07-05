import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api/v1/amenities";

export const getAllAmenities = () => {
    return axios.get(API_BASE_URL);
};