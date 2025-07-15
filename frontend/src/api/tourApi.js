import axios from "axios";

const API_BASE_URL = "https://spring-backend-383387500697.asia-southeast1.run.app/api/tours";

export const getAllTours = () => {
  return axios.get(API_BASE_URL);
};

export const getTourById = (id) => {
  return axios.get(`${API_BASE_URL}/${id}`);
};

export const createTour = (tourData) => {
  return axios.post(API_BASE_URL, tourData);
};

export const updateTour = (id, tourData) => {
  return axios.put(`${API_BASE_URL}/${id}`, tourData);
};

export const deleteTour = (id) => {
  return axios.delete(`${API_BASE_URL}/${id}`);
};

export const searchTours = (params) => {
  return axios.get(`${API_BASE_URL}/search`, { params });
};

/**
 * Lấy các tour nổi bật
 */
export const getFeaturedTours = () => {
  return axios.get(`${API_BASE_URL}/featured`);
};

/**
 * Lấy các tour phổ biến
 */
export const getPopularTours = () => {
  return axios.get(`${API_BASE_URL}/popular`);
};
               

