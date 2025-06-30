import axios from 'axios';

export const searchFlights = (params) => axios.get('/api/flights/search', { params });
export const getFlightDetail = (flightId) => axios.get(`/api/flights/${flightId}`);
export const bookFlight = (data) => axios.post('/api/bookings/flights', data);
export const payForFlight = (data) => axios.post('/api/payments/flights', data);
export const getUserFlightBookings = (userId) => axios.get(`/api/users/${userId}/flight-bookings`);
export const getFlightBookingDetail = (bookingId) => axios.get(`/api/bookings/flights/${bookingId}`);
export const cancelFlightBooking = (bookingId) => axios.post(`/api/bookings/flights/${bookingId}/cancel`);

// Admin
export const getAdminFlights = (params) => axios.get('/api/admin/flights', { params });
export const createAdminFlight = (data) => axios.post('/api/admin/flights', data);
export const updateAdminFlight = (flightId, data) => axios.put(`/api/admin/flights/${flightId}`, data);
export const deleteAdminFlight = (flightId) => axios.delete(`/api/admin/flights/${flightId}`);
export const getAdminSeats = (flightId) => axios.get(`/api/admin/flights/${flightId}/seats`);
export const updateAdminSeats = (flightId, data) => axios.put(`/api/admin/flights/${flightId}/seats`, data);
export const getAdminFlightBookings = (params) => axios.get('/api/admin/flight-bookings', { params });
export const getAdminFlightBookingDetail = (bookingId) => axios.get(`/api/admin/flight-bookings/${bookingId}`);
export const updateAdminFlightBookingStatus = (bookingId, status) => axios.put(`/api/admin/flight-bookings/${bookingId}?status=${status}`);
export const getAdminFlightStatistics = (params) => axios.get('/api/admin/flights/statistics', { params });
export const getAdminAirlines = () => axios.get('/api/admin/airlines');
export const createAdminAirline = (data) => axios.post('/api/admin/airlines', data);
export const updateAdminAirline = (airlineId, data) => axios.put(`/api/admin/airlines/${airlineId}`, data);
export const deleteAdminAirline = (airlineId) => axios.delete(`/api/admin/airlines/${airlineId}`); 