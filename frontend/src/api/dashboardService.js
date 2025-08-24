import axios from "axios";

const API_URL = "http://localhost:8080/api/dashboard";

class DashboardService {
  getSummary() {
    return axios.get(`${API_URL}/summary`);
  }

  getRevenueChartData(period = "day") {
    return axios.get(`${API_URL}/revenue-chart`, {
      params: { period },
    });
  }
}

export default new DashboardService();
