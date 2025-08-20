import axios, { AxiosResponse } from 'axios';

// Type definitions
export interface OverviewStatistics {
  totalRevenue: number;
  totalBookings: number;
  totalTrips: number;
  totalBuses: number;
  totalCustomers: number;
  occupancyRates: Record<string, number>;
}

export interface DailyStatistics {
  date: string;
  revenue: number;
  bookings: number;
  trips: number;
  customers: number;
}

export interface WeeklyStatistics {
  weekStart: string;
  weekEnd: string;
  revenue: number;
  bookings: number;
  trips: number;
  customers: number;
  dailyStats: DailyStatistics[];
}

export interface MonthlyStatistics {
  monthYear: string;
  revenue: number;
  bookings: number;
  trips: number;
  customers: number;
  weeklyStats: WeeklyStatistics[];
}

export interface TopRoute {
  id: number;
  origin: string;
  destination: string;
  revenue: number;
  bookings: number;
  percentage: number;
}

export interface BusStatusStatistics {
  active: number;
  inactive: number;
  maintenance: number;
  total: number;
}

export interface OccupancyRates {
  [busId: string]: number;
}

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
}

// API Client với base URL
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 30000, // Tăng timeout lên 30 giây
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add auth token to requests
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('t_') || localStorage.getItem('accessToken') || localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

/**
 * 📊 Bus Statistics API Client
 * Thống kê xe buýt theo ownerId
 */
export class BusStatisticsAPI {
  private static readonly BASE_URL = '/api/v1/bus/statistics';

  /**
   * 📈 Lấy thống kê tổng quan cho owner
   * @param ownerId - ID của owner
   * @returns Thống kê tổng quan
   */
  static async getOverviewStatistics(ownerId: number): Promise<OverviewStatistics> {
    try {
      const response: AxiosResponse<ApiResponse<OverviewStatistics>> = await apiClient.get(
        `${this.BASE_URL}/overview/${ownerId}`
      );
      return response.data.data;
    } catch (error) {
      throw error;
    }
  }

  /**
   * 📅 Lấy thống kê theo ngày
   * @param ownerId - ID của owner
   * @param date - Ngày cần thống kê (yyyy-MM-dd)
   * @returns Thống kê ngày
   */
  static async getDailyStatistics(ownerId: number, date: string): Promise<DailyStatistics> {
    try {
      const response: AxiosResponse<ApiResponse<DailyStatistics>> = await apiClient.get(
        `${this.BASE_URL}/daily/${ownerId}`,
        {
          params: { date }
        }
      );
      return response.data.data;
    } catch (error) {
      console.error('❌ [Bus Statistics API] Error getting daily statistics:', error);
      throw error;
    }
  }

  /**
   * 📅 Lấy thống kê theo tuần
   * @param ownerId - ID của owner
   * @param weekStart - Ngày bắt đầu tuần (yyyy-MM-dd)
   * @returns Thống kê tuần
   */
  static async getWeeklyStatistics(ownerId: number, weekStart: string): Promise<WeeklyStatistics> {
    try {
      const response: AxiosResponse<ApiResponse<WeeklyStatistics>> = await apiClient.get(
        `${this.BASE_URL}/weekly/${ownerId}`,
        {
          params: { weekStart }
        }
      );
      return response.data.data;
    } catch (error) {
      console.error('❌ [Bus Statistics API] Error getting weekly statistics:', error);
      throw error;
    }
  }

  /**
   * 📅 Lấy thống kê theo tháng
   * @param ownerId - ID của owner
   * @param monthYear - Tháng năm (yyyy-MM)
   * @returns Thống kê tháng
   */
  static async getMonthlyStatistics(ownerId: number, monthYear: string): Promise<MonthlyStatistics> {
    try {
      const response: AxiosResponse<ApiResponse<MonthlyStatistics>> = await apiClient.get(
        `${this.BASE_URL}/monthly/${ownerId}`,
        {
          params: { monthYear }
        }
      );
      return response.data.data;
    } catch (error) {
      console.error('❌ [Bus Statistics API] Error getting monthly statistics:', error);
      throw error;
    }
  }

  /**
   * 🛣️ Lấy top tuyến đường phổ biến
   * @param ownerId - ID của owner
   * @param limit - Số lượng tuyến đường (mặc định: 5)
   * @returns Danh sách tuyến đường phổ biến
   */
  static async getTopRoutes(ownerId: number, limit: number = 5): Promise<TopRoute[]> {
    try {
      const response: AxiosResponse<ApiResponse<TopRoute[]>> = await apiClient.get(
        `${this.BASE_URL}/routes/${ownerId}`,
        {
          params: { limit }
        }
      );
      return response.data.data;
    } catch (error) {
      console.error('❌ [Bus Statistics API] Error getting top routes:', error);
      throw error;
    }
  }

  /**
   * 🚌 Lấy thống kê trạng thái xe buýt
   * @param ownerId - ID của owner
   * @returns Map trạng thái -> số lượng
   */
  static async getBusStatusStatistics(ownerId: number): Promise<BusStatusStatistics> {
    try {
      const response: AxiosResponse<ApiResponse<BusStatusStatistics>> = await apiClient.get(
        `${this.BASE_URL}/bus-status/${ownerId}`
      );
      return response.data.data;
    } catch (error) {
      console.error('❌ [Bus Statistics API] Error getting bus status statistics:', error);
      throw error;
    }
  }

  /**
   * 📊 Lấy tỷ lệ lấp đầy xe buýt
   * @param ownerId - ID của owner
   * @returns Map busId -> tỷ lệ lấp đầy
   */
  static async getOccupancyRates(ownerId: number): Promise<OccupancyRates> {
    try {
      const response: AxiosResponse<ApiResponse<OccupancyRates>> = await apiClient.get(
        `${this.BASE_URL}/occupancy/${ownerId}`
      );
      return response.data.data;
    } catch (error) {
      console.error('❌ [Bus Statistics API] Error getting occupancy rates:', error);
      throw error;
    }
  }
}
