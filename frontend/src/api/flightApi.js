import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL
});

// Flights
export const searchFlights = (params) => api.get('/flights/search', { params });
export const getFlightDetail = (flightId) => api.get(`/admin/flights/${flightId}`);
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
  api.post(`/bookings/flights/${bookingId}/cancel`);

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
export const updateAdminSeat = (flightId, slotId, data) =>
  api.put(`/admin/flights/${flightId}/seats/${slotId}`, data);
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
