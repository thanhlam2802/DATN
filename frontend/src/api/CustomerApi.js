import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL
});

// Customer CRUD
export const getAllCustomers = () => api.get('/v1/customers');
export const getCustomerById = (id) => api.get(`/v1/customers/${id}`);
export const createCustomer = (data) => api.post('/v1/customers', data);
export const updateCustomer = (id, data) => api.put(`/v1/customers/${id}`, data);
export const deleteCustomer = (id) => api.delete(`/v1/customers/${id}`); 