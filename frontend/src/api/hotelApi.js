import axios from "axios";
import { getAccessToken } from "@/services/TokenService.js";

const API_BASE_URL = `${import.meta.env.VITE_API_URL}/api/v1/hotels`;
const API_ADMIN_BASE_URL = `${import.meta.env.VITE_API_URL}/api/v1/admin/hotels`;

export const searchHotels = (params) => {
    return axios.get(API_BASE_URL, { params });
};

export const getHotelById = (id, params) => {
    return axios.get(`${API_BASE_URL}/${id}`, { params });
};

export const getHotelReviews = (hotelId) => {
    return axios.get(`${API_BASE_URL}/${hotelId}/reviews`);
};

export const createHotel = (formData) => {
    return axios.post(API_ADMIN_BASE_URL, formData, { headers: { 'Content-Type': 'multipart/form-data' } });
};

export const updateHotel = (id, formData) => {
    return axios.put(`${API_ADMIN_BASE_URL}/${id}`, formData, { headers: { 'Content-Type': 'multipart/form-data' } });
};

export const deleteHotel = (id) => {
    return axios.delete(`${API_ADMIN_BASE_URL}/${id}`);
};

export const createHotelReview = (hotelId, data) => {
    const token = getAccessToken();
    const headers = token ? { Authorization: `Bearer ${token}` } : {};
    return axios.post(`${API_BASE_URL}/${hotelId}/reviews`, data, { headers });
};

export const bookHotel = (data) => {
    const token = getAccessToken();
    const headers = token ? { Authorization: `Bearer ${token}` } : {};
    return axios.post(`${API_BASE_URL}/book`, data, { headers });
};

export const updateHotelBooking = (data) => {
    const token = getAccessToken();
    const headers = token ? { Authorization: `Bearer ${token}` } : {};
    return axios.put(`${API_BASE_URL}/bookings/update`, data, { headers });
};

export const addItemToCart = (orderId, data) => {
    return axios.post(`/api/v1/cart/${orderId}/items`, data);
};

export const getHotelCustomers = (hotelId) => {
    return axios.get(`${API_ADMIN_BASE_URL}/${hotelId}/customers`);
};

export const getAllHotelCustomers = () => {
    return axios.get(`${API_ADMIN_BASE_URL}/customers`);
};

export const getCustomerBookedRooms = (customerId) => {
    return axios.get(`${API_ADMIN_BASE_URL}/customers/${customerId}/booked-rooms`);
};

export const getAllHotelBookings = () => {
    return axios.get(`${API_ADMIN_BASE_URL}/bookings`);
};

export const getDashboardStatistics = (timePeriod = 'this_month') => {
  return axios.get(`${API_ADMIN_BASE_URL}/dashboard-statistics`, {
    params: { timePeriod }
  });
};

export const getHotelRevenueChart = (timePeriod = 'this_month') => {
  return axios.get(`${API_ADMIN_BASE_URL}/revenue-chart`, {
    params: { timePeriod }
  });
};

export const getHotelRevenuePieChart = (timePeriod = 'this_month') => {
  return axios.get(`${API_ADMIN_BASE_URL}/revenue-pie-chart`, {
    params: { timePeriod }
  });
};

export const getTopRoomsChart = (timePeriod = 'this_month') => {
  return axios.get(`${API_ADMIN_BASE_URL}/top-rooms-chart`, {
    params: { timePeriod }
  });
};

export const getAllHotelReviews = () => {
  return axios.get(`${API_ADMIN_BASE_URL}/reviews`);
};

export const deleteHotelReview = (reviewId) => {
  return axios.delete(`${API_ADMIN_BASE_URL}/reviews/${reviewId}`);
};

export default {
    searchHotels,
    getHotelById,
    getHotelReviews,
    createHotel,
    updateHotel,
    deleteHotel,
    createHotelReview,
    bookHotel,
    addItemToCart,
    getHotelCustomers,
    getAllHotelCustomers,
    getCustomerBookedRooms,
    getAllHotelBookings,
    getDashboardStatistics,
    getHotelRevenueChart,
    getHotelRevenuePieChart,
    getTopRoomsChart,
    getAllHotelReviews,
    deleteHotelReview
};