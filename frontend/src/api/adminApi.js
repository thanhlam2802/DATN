import axios from "axios";
import { getBearerToken } from "@/services/TokenService.js";

const adminApi = axios.create({
    baseURL: import.meta.env.VITE_API_URL
});

adminApi.interceptors.request.use(
    (config) => {
        const token = getBearerToken();
        if (token) {
            config.headers.Authorization = token;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

adminApi.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        if (error.response?.status === 401) {
            console.error('Unauthorized access. Please login again.');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export const hotelAdminApi = {
    createHotel: (formData) => {
        return adminApi.post('/api/v1/admin/hotels', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    },

    updateHotel: (id, formData) => {
        return adminApi.put(`/api/v1/admin/hotels/${id}`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    },

    deleteHotel: (id) => {
        return adminApi.delete(`/api/v1/admin/hotels/${id}`);
    },

    searchHotels: (params) => {
        return adminApi.get('/api/v1/hotels', { params });
    },

    getHotelById: (id, params) => {
        return adminApi.get(`/api/v1/hotels/${id}`, { params });
    },

    getHotelCustomers: (hotelId) => {
        return adminApi.get(`/api/v1/admin/hotels/${hotelId}/customers`);
    },

    getAllHotelCustomers: () => {
        return adminApi.get('/api/v1/admin/hotels/customers');
    },

    getCustomerBookedRooms: (customerId) => {
        return adminApi.get(`/api/v1/admin/hotels/customers/${customerId}/booked-rooms`);
    },

    getAllHotelBookings: () => {
        return adminApi.get('/api/v1/admin/hotels/bookings');
    },

    getDashboardStatistics: (timePeriod = 'this_month') => {
        return adminApi.get('/api/v1/admin/hotels/dashboard-statistics', {
            params: { timePeriod }
        });
    },

    getHotelRevenueChart: (timePeriod = 'this_month') => {
        return adminApi.get('/api/v1/admin/hotels/revenue-chart', {
            params: { timePeriod }
        });
    },

    getHotelRevenuePieChart: (timePeriod = 'this_month') => {
        return adminApi.get('/api/v1/admin/hotels/revenue-pie-chart', {
            params: { timePeriod }
        });
    },

    getTopRoomsChart: (timePeriod = 'this_month') => {
        return adminApi.get('/api/v1/admin/hotels/top-rooms-chart', {
            params: { timePeriod }
        });
    },

    getAllHotelReviews: () => {
        return adminApi.get('/api/v1/admin/hotels/reviews');
    },

    deleteHotelReview: (reviewId) => {
        return adminApi.delete(`/api/v1/admin/hotels/reviews/${reviewId}`);
    }
};

export default adminApi; 