/**
 * API service for Bus entity operations
 */

// Mock API client (sẽ thay thế bằng axios hoặc fetch thật)
const mockApiClient = {
  post: async (url, data) => {
    console.log('Mock POST:', url, data)
    await new Promise(resolve => setTimeout(resolve, 1000)) // Simulate network delay
    return {
      data: {
        id: Date.now(),
        ...data,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }
    }
  },
  
  get: async (url) => {
    console.log('Mock GET:', url)
    await new Promise(resolve => setTimeout(resolve, 500))
    return {
      data: [] // Mock empty array
    }
  },
  
  put: async (url, data) => {
    console.log('Mock PUT:', url, data)
    await new Promise(resolve => setTimeout(resolve, 1000))
    return {
      data: {
        ...data,
        updatedAt: new Date().toISOString()
      }
    }
  },
  
  delete: async (url) => {
    console.log('Mock DELETE:', url)
    await new Promise(resolve => setTimeout(resolve, 500))
    return { data: { success: true } }
  }
}

/**
 * Tạo Bus mới
 * @param {object} busData - Dữ liệu bus
 * @returns {Promise} - API response
 */
export const createBus = async (busData) => {
  try {
    const response = await mockApiClient.post('/api/buses', busData)
    return response
  } catch (error) {
    console.error('Error creating bus:', error)
    throw error
  }
}

/**
 * Lấy danh sách Bus theo owner
 * @param {number} ownerId - ID chủ xe
 * @returns {Promise} - API response
 */
export const getBusesByOwner = async (ownerId) => {
  try {
    const response = await mockApiClient.get(`/api/buses?ownerId=${ownerId}`)
    return response
  } catch (error) {
    console.error('Error fetching buses by owner:', error)
    throw error
  }
}

/**
 * Lấy thông tin Bus theo ID
 * @param {number} busId - ID bus
 * @returns {Promise} - API response
 */
export const getBusById = async (busId) => {
  try {
    const response = await mockApiClient.get(`/api/buses/${busId}`)
    return response
  } catch (error) {
    console.error('Error fetching bus by ID:', error)
    throw error
  }
}

/**
 * Cập nhật Bus
 * @param {number} busId - ID bus
 * @param {object} busData - Dữ liệu cập nhật
 * @returns {Promise} - API response
 */
export const updateBus = async (busId, busData) => {
  try {
    const response = await mockApiClient.put(`/api/buses/${busId}`, busData)
    return response
  } catch (error) {
    console.error('Error updating bus:', error)
    throw error
  }
}

/**
 * Xóa Bus
 * @param {number} busId - ID bus
 * @returns {Promise} - API response
 */
export const deleteBus = async (busId) => {
  try {
    const response = await mockApiClient.delete(`/api/buses/${busId}`)
    return response
  } catch (error) {
    console.error('Error deleting bus:', error)
    throw error
  }
}

/**
 * Lấy danh sách tất cả Bus (cho admin)
 * @returns {Promise} - API response
 */
export const getAllBuses = async () => {
  try {
    const response = await mockApiClient.get('/api/buses')
    return response
  } catch (error) {
    console.error('Error fetching all buses:', error)
    throw error
  }
} 