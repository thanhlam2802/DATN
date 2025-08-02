import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL
});
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('t_'); 
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Flights
export const searchFlights = (params) => api.post('/flights/search', params );
export const getFlightDetail = (flightId) => api.get(`/admin/flights/${flightId}`);
export const getFlightDetailPublic = (flightId) => api.get(`/flights/${flightId}`);
export const flightBooked = (flightId) => api.get(`/admin/flight-booked/${flightId}`);
export function getAvailableSeats(flightId) {
  return api.get(`/flights/${flightId}/available-seats`);
}

// Bookings & Payments
export const bookFlight = (data) => api.post('/bookings/flights', data);
export const payForFlight = (data) => api.post('/payments/flights', data);

// Customer
export const getCustomerFlightBookings = (customerId) =>
  api.get(`/customers/${customerId}/flight-bookings`);

// Booking detail & cancel
export const getFlightBookingDetail = (bookingId) =>
  api.get(`/bookings/flights/${bookingId}`);
export const cancelFlightBooking = (bookingId) =>
  api.get(`/bookings/flights/${bookingId}/cancel`);

// Admin – Flights
export const getAdminFlights = (params) =>
  api.get('/admin/flights', { params });
export const createAdminFlight = (data) =>
  api.post('/admin/flights', data, {
    headers: {
      'Content-Type': 'application/json'
    }
  });

export const uploadFlightImages = (flightId, formData) =>
  api.post(`/admin/flights/${flightId}/images`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
export const updateAdminFlight = (flightId, data) =>
  api.put(`/admin/flights/${flightId}`, data);
export const deleteAdminFlight = (flightId) =>
  api.delete(`/admin/flights/${flightId}`);
// export const updateAdminFlightImages = (flightId, formData) =>
//   api.put(`/admin/flights/${flightId}/images`, formData, {
//     headers: { 'Content-Type': 'multipart/form-data' }
//   });

// Thêm API mới cho xử lý ảnh riêng biệt
export const addFlightImages = (flightId, formData) =>
  api.post(`/admin/flights/${flightId}/imagesAdd`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });

export const deleteFlightImage = (flightId, imageId) =>
  api.delete(`/admin/flights/${flightId}/images/${imageId}`);

// Admin – Seats
export const getAdminSeats = (flightId) =>
  api.get(`/admin/flights/${flightId}/seats`);
export const updateAdminSeats = (flightId, data) =>
  api.put(`/admin/flights/${flightId}/seats`, data);
export const updateAdminSeat = ( slotId, data) =>
  api.put(`/admin/flights/seats/${slotId}`, data);
export const deleteAdminSeat = (flightId, slotId) =>
  api.delete(`/admin/flights/${flightId}/seats/${slotId}`);

// Admin – Bookings
export const getAdminFlightBookings = (params) =>
  api.get('/admin/flight-bookings', { params });
export const getAdminFlightBookingDetail = (bookingId) =>
  api.get(`/admin/flight-bookings/${bookingId}`);
export const updateAdminFlightBookingStatus = (bookingId, status) =>
  api.put(`/admin/flight-bookings/${bookingId}`, null, {
    params: { status }
  });

// Admin – Statistics & Airports
export const getAdminFlightStatistics = (params) =>
  api.get('/admin/flights/statistics', { params });
export const getMonthlyFlightStatistics = (year, month) =>
  api.get('/admin/flights/monthly-statistics', { params: { year, month } });
export const getBookingsByDestination = (year, month) =>
  api.get('/admin/flights/bookings-by-destination', { params: { year, month } });
export const getRevenueBySeatClass = (year, month) =>
  api.get('/admin/flights/revenue-by-seat-class', { params: { year, month } });
export const getAdminAirports = () => api.get('/admin/airports');
export const createAdminAirport = (data) =>
  api.post('/admin/airports', data);
export const updateAdminAirport = (airportId, data) =>
  api.put(`/admin/airports/${airportId}`, data);
export const deleteAdminAirport = (airportId) =>
  api.delete(`/admin/airports/${airportId}`);

// Airlines
export const getAllAirlines = () => api.get('/flights/airlines');

// Airports
export const getAllAirports = () => api.get('/flights/airports');

// Flight Categories
export const getAllFlightCategories = () => api.get('/flights/categories');

// Find Available Slot
export const findFirstAvailableSlot = (requestDto) => 
  api.post('/flights/find-available-slot', requestDto);

// Đặt giữ chỗ vé máy bay trực tiếp (mua ngay)
export const reserveFlightDirect = (dto) =>
  api.post('/v1/orders/reserve-flight-direct', dto);

// Lấy thông tin tổng hợp giữ chỗ chuyến bay (reservation summary)
export const getFlightReservationSummary = (bookingId) =>
  api.get(`/bookings/flights/reservation-summary/${bookingId}`);


export const updateGroupSeat = (flightId,dto) =>
  api.put(`/admin/updateGroupSeat/${flightId}`,dto);
export const updateCustomer = (id,customerDto) =>
  api.put(`/flights/update-customer/${id}`,customerDto);