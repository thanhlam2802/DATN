import axios from 'axios';
import { getAccessToken } from '@/services/TokenService';

const API_BASE_URL = 'http://localhost:8080/api/v1/admin/users';

const userManagementApi = {
  // Lấy danh sách người dùng với phân trang và lọc
  getAllUsers: async (params = {}) => {
    const { search, role, status, page = 0, size = 10 } = params;
    const queryParams = new URLSearchParams();
    
    if (search) queryParams.append('search', search);
    if (role) queryParams.append('role', role);
    if (status) queryParams.append('status', status);
    queryParams.append('page', page);
    queryParams.append('size', size);

    try {
      const response = await axios.get(`${API_BASE_URL}?${queryParams.toString()}`, {
        headers: {
          'Authorization': `Bearer ${getAccessToken()}`
        }
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching users:', error);
      throw error;
    }
  },

  // Lấy thông tin người dùng theo ID
  getUserById: async (id) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/${id}`, {
        headers: {
          'Authorization': `Bearer ${getAccessToken()}`
        }
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching user:', error);
      throw error;
    }
  },

  // Tạo người dùng mới
  createUser: async (userData) => {
    try {
      const response = await axios.post(API_BASE_URL, userData, {
        headers: {
          'Authorization': `Bearer ${getAccessToken()}`,
          'Content-Type': 'application/json'
        }
      });
      return response.data;
    } catch (error) {
      console.error('Error creating user:', error);
      throw error;
    }
  },

  // Cập nhật thông tin người dùng
  updateUser: async (id, userData) => {
    try {
      const response = await axios.put(`${API_BASE_URL}/${id}`, userData, {
        headers: {
          'Authorization': `Bearer ${getAccessToken()}`,
          'Content-Type': 'application/json'
        }
      });
      return response.data;
    } catch (error) {
      console.error('Error updating user:', error);
      throw error;
    }
  },

  // Cập nhật trạng thái người dùng
  updateUserStatus: async (id, status) => {
    try {
      const response = await axios.put(`${API_BASE_URL}/${id}/status?status=${status}`, {}, {
        headers: {
          'Authorization': `Bearer ${getAccessToken()}`
        }
      });
      return response.data;
    } catch (error) {
      console.error('Error updating user status:', error);
      throw error;
    }
  },

  // Lấy thống kê người dùng
  getUserStatistics: async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/statistics`, {
        headers: {
          'Authorization': `Bearer ${getAccessToken()}`
        }
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching user statistics:', error);
      throw error;
    }
  },

  // Xóa người dùng
  deleteUser: async (id) => {
    try {
      const response = await axios.delete(`${API_BASE_URL}/${id}`, {
        headers: {
          'Authorization': `Bearer ${getAccessToken()}`
        }
      });
      return response.data;
    } catch (error) {
      console.error('Error deleting user:', error);
      throw error;
    }
  }
};

export default userManagementApi;
