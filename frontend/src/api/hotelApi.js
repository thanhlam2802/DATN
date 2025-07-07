import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api/v1/hotels";

export const searchHotels = (params) => {
    return axios.get(API_BASE_URL, { params });
};

export const getHotelById = (id, params) => {
    return axios.get(`${API_BASE_URL}/${id}`, { params });
};

export const getHotelReviews = (hotelId) => {
    return axios.get(`${API_BASE_URL}/${hotelId}/reviews`);
};

export const createHotel = (hotelData) => {
    return axios.post(API_BASE_URL, hotelData);
};

export const updateHotel = (id, hotelData) => {
    return axios.put(`${API_BASE_URL}/${id}`, hotelData);
};

export const deleteHotel = (id) => {
    return axios.delete(`${API_BASE_URL}/${id}`);
};

export default {
    searchHotels,
    getHotelById,
    getHotelReviews,
    createHotel,
    updateHotel,
    deleteHotel
};