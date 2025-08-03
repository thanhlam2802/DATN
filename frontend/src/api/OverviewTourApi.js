import axios from "axios";
const DASHBOARD_API_URL = "/api/v1/dashboardTour";

const callApi = (endpoint, params) => {
  return axios.get(`${DASHBOARD_API_URL}/${endpoint}`, { params });
};

export const getStatistics = ({ startDate, endDate }) => {
  return callApi("stats", { startDate, endDate });
};

export const getBookings = ({ startDate, endDate, page = 0, size = 5 }) => {
  return callApi("bookings", { startDate, endDate, page, size });
};

export default { getStatistics, getBookings };
