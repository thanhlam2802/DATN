import axios from "axios";
import { getBearerToken } from "@/services/TokenService.js";

const adminApi = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
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
      console.error("Unauthorized access. Please login again.");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export const hotelAdminApi = {
  createHotel: (formData) => {
    return adminApi.post("/api/v1/admin/hotels", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  },

  updateHotel: (id, formData) => {
    return adminApi.put(`/api/v1/admin/hotels/${id}`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  },

  deleteHotel: (id) => {
    return adminApi.delete(`/api/v1/admin/hotels/${id}`);
  },

  searchHotels: (params) => {
    return adminApi.get("/api/v1/admin/hotels/search", { params });
  },

  getHotelById: (id, params) => {
    return adminApi.get(`/api/v1/admin/hotels/${id}`, { params });
  },

  getHotelCustomers: (hotelId) => {
    return adminApi.get(`/api/v1/admin/hotels/${hotelId}/customers`);
  },

  getAllHotelCustomers: () => {
    return adminApi.get("/api/v1/admin/hotels/customers");
  },

  getCustomerBookedRooms: (customerId) => {
    return adminApi.get(
      `/api/v1/admin/hotels/customers/${customerId}/booked-rooms`
    );
  },

  getAllHotelBookings: () => {
    return adminApi.get("/api/v1/admin/hotels/bookings");
  },

  getHotelBookingById: (bookingId) => {
    return adminApi.get(`/api/v1/admin/hotels/bookings/${bookingId}`);
  },

  getDashboardStatistics: (timePeriod = "this_month") => {
    return adminApi.get("/api/v1/admin/hotels/dashboard-statistics", {
      params: { timePeriod },
    });
  },

  getHotelRevenueChart: (timePeriod = "this_month", chartType = "by_day") => {
    return adminApi.get("/api/v1/admin/hotels/revenue-chart", {
      params: { timePeriod, chartType },
    });
  },

  getHotelRevenuePieChart: (timePeriod = "this_month") => {
    return adminApi.get("/api/v1/admin/hotels/revenue-pie-chart", {
      params: { timePeriod },
    });
  },

  getTopRoomsChart: (timePeriod = "this_month") => {
    return adminApi.get("/api/v1/admin/hotels/top-rooms-chart", {
      params: { timePeriod },
    });
  },

  approveHotel: (id) => {
    return adminApi.put(`/api/v1/admin/hotels/${id}/approve`);
  },

  rejectHotel: (id, data) => {
    return adminApi.put(`/api/v1/admin/hotels/${id}/reject`, data);
  },

  resubmitHotel: (id) => {
    return adminApi.put(`/api/v1/admin/hotels/${id}/resubmit`);
  },

  updateHotelStatus: (id, status) => {
    return adminApi.put(`/api/v1/admin/hotels/${id}/status`, { status });
  },

  getHotelStatistics: () => {
    return adminApi.get("/api/v1/admin/hotels/statistics");
  },

  getAllHotelReviews: () => {
    return adminApi.get("/api/v1/admin/hotels/reviews");
  },

  deleteHotelReview: (reviewId) => {
    return adminApi.delete(`/api/v1/admin/hotels/reviews/${reviewId}`);
  },
};

export const respondToHotelReview = (reviewId, response) => {
  return adminApi.post(`/api/v1/admin/hotels/reviews/${reviewId}/respond`, {
    response: response,
  });
};

// ===== Super Admin Dashboard =====
export const getFlightAdminSummaries = () => {
  return adminApi.get("/api/admin/super-admin/flight-admins");
};

export const getFlightAdminDetail = (adminId) => {
  return adminApi.get(`/api/admin/super-admin/flight-admins/${adminId}`);
};

export const getFlightsByAdminId = (
  adminId,
  page = 0,
  size = 10,
  filter = ""
) => {
  return adminApi.get(
    `/api/admin/super-admin/flight-admins/${adminId}/flights`,
    {
      params: { page, size, filter },
    }
  );
};

export const provinceAdminApi = {
  getAll: () => adminApi.get("/api/v1/provinces"),
  create: (payload) => adminApi.post("/api/v1/provinces", payload),
  update: (id, payload) => adminApi.put(`/api/v1/provinces/${id}`, payload),
  remove: (id) => adminApi.delete(`/api/v1/provinces/${id}`),
  createMultipart: ({ name, file, imageUrl }) => {
    const form = new FormData();
    form.append("name", name);
    if (file) form.append("image", file);
    if (imageUrl) form.append("imageUrl", imageUrl);
    return adminApi.post("/api/v1/provinces", form, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  },
  updateMultipart: (id, { name, file, imageUrl }) => {
    const form = new FormData();
    if (name != null) form.append("name", name);
    if (file) form.append("image", file);
    if (imageUrl) form.append("imageUrl", imageUrl);
    return adminApi.put(`/api/v1/provinces/${id}`, form, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  },
};

export const amenityAdminApi = {
  getAll: () => adminApi.get("/api/v1/amenities"),
  create: (payload) => adminApi.post("/api/v1/amenities", payload),
  update: (id, payload) => adminApi.put(`/api/v1/amenities/${id}`, payload),
  remove: (id) => adminApi.delete(`/api/v1/amenities/${id}`),
};


export default adminApi;
