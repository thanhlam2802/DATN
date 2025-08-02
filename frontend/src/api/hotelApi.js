import axios from "axios";
import { getBearerToken } from "@/services/TokenService.js";

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
    const headers = { 
        'Content-Type': 'multipart/form-data',
        Authorization: getBearerToken()
    };
    return axios.post(API_ADMIN_BASE_URL, formData, { headers });
};

export const updateHotel = (id, formData) => {
    const headers = { 
        'Content-Type': 'multipart/form-data',
        Authorization: getBearerToken()
    };
    return axios.put(`${API_ADMIN_BASE_URL}/${id}`, formData, { headers });
};

export const deleteHotel = (id) => {
    const headers = { Authorization: getBearerToken() };
    return axios.delete(`${API_ADMIN_BASE_URL}/${id}`, { headers });
};

export const createHotelReview = (hotelId, data) => {
    const headers = { Authorization: getBearerToken() };
    return axios.post(`${API_BASE_URL}/${hotelId}/reviews`, data, { headers });
};

export const bookHotel = (data) => {
    const headers = { Authorization: getBearerToken() };
    return axios.post(`${API_BASE_URL}/book`, data, { headers });
};

export const updateHotelBooking = (data) => {
    const headers = { Authorization: getBearerToken() };
    return axios.put(`${API_BASE_URL}/bookings/update`, data, { headers });
};

export const addItemToCart = (orderId, data) => {
    return axios.post(`/api/v1/cart/${orderId}/items`, data);
};

export const getHotelCustomers = (hotelId) => {
    const headers = { Authorization: getBearerToken() };
    return axios.get(`${API_ADMIN_BASE_URL}/${hotelId}/customers`, { headers });
};

export const getAllHotelCustomers = () => {
    const headers = { Authorization: getBearerToken() };
    return axios.get(`${API_ADMIN_BASE_URL}/customers`, { headers });
};

export const getCustomerBookedRooms = (customerId) => {
    const headers = { Authorization: getBearerToken() };
    return axios.get(`${API_ADMIN_BASE_URL}/customers/${customerId}/booked-rooms`, { headers });
};

export const getAllHotelBookings = () => {
    const headers = { Authorization: getBearerToken() };
    return axios.get(`${API_ADMIN_BASE_URL}/bookings`, { headers });
};

export const getDashboardStatistics = (timePeriod = 'this_month') => {
    const headers = { Authorization: getBearerToken() };
    return axios.get(`${API_ADMIN_BASE_URL}/dashboard-statistics`, {
        params: { timePeriod },
        headers
    });
};

export const getHotelRevenueChart = (timePeriod = 'this_month') => {
    const headers = { Authorization: getBearerToken() };
    return axios.get(`${API_ADMIN_BASE_URL}/revenue-chart`, {
        params: { timePeriod },
        headers
    });
};

export const getHotelRevenuePieChart = (timePeriod = 'this_month') => {
    const headers = { Authorization: getBearerToken() };
    return axios.get(`${API_ADMIN_BASE_URL}/revenue-pie-chart`, {
        params: { timePeriod },
        headers
    });
};

export const getTopRoomsChart = (timePeriod = 'this_month') => {
    const headers = { Authorization: getBearerToken() };
    return axios.get(`${API_ADMIN_BASE_URL}/top-rooms-chart`, {
        params: { timePeriod },
        headers
    });
};

export const getAllHotelReviews = () => {
    const headers = { Authorization: getBearerToken() };
    return axios.get(`${API_ADMIN_BASE_URL}/reviews`, { headers });
};

export const deleteHotelReview = (reviewId) => {
    const headers = { Authorization: getBearerToken() };
    return axios.delete(`${API_ADMIN_BASE_URL}/reviews/${reviewId}`, { headers });
};

export const notifyPaymentSuccess = (orderId, amount) => {
    return axios.post(`${import.meta.env.VITE_API_URL}/api/v1/payment-notifications/success`, {
        orderId,
        amount
    });
};

export const notifyNewReview = (hotelName, rating) => {
    return axios.post(`${import.meta.env.VITE_API_URL}/api/v1/review-notifications/new`, {
        hotelName,
        rating
    });
};

export const notifyHotelCancellation = (orderId, bookingId) => {
    return axios.post(`${import.meta.env.VITE_API_URL}/api/v1/hotel-notifications/cancellation`, {
        orderId,
        bookingId
    });
};

export const notifyHotelCreated = (hotelName, userName) => {
    return axios.post(`${import.meta.env.VITE_API_URL}/api/v1/hotel-notifications/created`, {
        hotelName,
        userName
    });
};

export const notifyHotelUpdated = (hotelName, userName) => {
    return axios.post(`${import.meta.env.VITE_API_URL}/api/v1/hotel-notifications/updated`, {
        hotelName,
        userName
    });
};

export const notifyHotelDeleted = (hotelName, userName) => {
    return axios.post(`${import.meta.env.VITE_API_URL}/api/v1/hotel-notifications/deleted`, {
        hotelName,
        userName
    });
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
    deleteHotelReview,
    notifyPaymentSuccess,
    notifyNewReview,
    notifyHotelCancellation,
    notifyHotelCreated,
    notifyHotelUpdated,
    notifyHotelDeleted
};