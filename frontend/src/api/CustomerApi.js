import axios from 'axios';
import { getBearerToken } from '@/services/TokenService.js';

const API_BASE_URL = `${import.meta.env.VITE_API_URL}/api`;

const api = axios.create({
  baseURL: API_BASE_URL
});

api.interceptors.request.use(
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

// Customer CRUD
export const getAllCustomers = () => api.get('/v1/customers');
export const getCustomerById = (id) => api.get(`/v1/customers/${id}`);
export const createCustomer = (data) => api.post('/v1/customers', data);
export const updateCustomer = (id, data) => api.put(`/v1/customers/${id}`, data);
export const deleteCustomer = (id) => api.delete(`/v1/customers/${id}`); 