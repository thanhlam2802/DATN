/**
 * API service for Route entity operations
 */

// Reuse mock API client
const mockApiClient = {
  post: async (url, data) => {
    console.log('Mock POST:', url, data)
    await new Promise(resolve => setTimeout(resolve, 1000))
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
      data: []
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
 * Tạo Route mới
 * @param {object} routeData - Dữ liệu route
 * @returns {Promise} - API response
 */
export const createRoute = async (routeData) => {
  try {
    const response = await mockApiClient.post('/api/routes', routeData)
    return response
  } catch (error) {
    console.error('Error creating route:', error)
    throw error
  }
}

/**
 * Lấy danh sách tất cả Routes
 * @returns {Promise} - API response
 */
export const getAllRoutes = async () => {
  try {
    const response = await mockApiClient.get('/api/routes')
    return response
  } catch (error) {
    console.error('Error fetching routes:', error)
    throw error
  }
}

/**
 * Lấy Route theo ID
 * @param {number} routeId - ID route
 * @returns {Promise} - API response
 */
export const getRouteById = async (routeId) => {
  try {
    const response = await mockApiClient.get(`/api/routes/${routeId}`)
    return response
  } catch (error) {
    console.error('Error fetching route by ID:', error)
    throw error
  }
}

/**
 * Tìm Route theo origin và destination
 * @param {string} origin - Điểm đi
 * @param {string} destination - Điểm đến
 * @returns {Promise} - API response
 */
export const findRouteByOriginDestination = async (origin, destination) => {
  try {
    const response = await mockApiClient.get(`/api/routes/search?origin=${origin}&destination=${destination}`)
    return response
  } catch (error) {
    console.error('Error finding route by origin-destination:', error)
    throw error
  }
}

/**
 * Cập nhật Route
 * @param {number} routeId - ID route
 * @param {object} routeData - Dữ liệu cập nhật
 * @returns {Promise} - API response
 */
export const updateRoute = async (routeId, routeData) => {
  try {
    const response = await mockApiClient.put(`/api/routes/${routeId}`, routeData)
    return response
  } catch (error) {
    console.error('Error updating route:', error)
    throw error
  }
}

/**
 * Xóa Route
 * @param {number} routeId - ID route
 * @returns {Promise} - API response
 */
export const deleteRoute = async (routeId) => {
  try {
    const response = await mockApiClient.delete(`/api/routes/${routeId}`)
    return response
  } catch (error) {
    console.error('Error deleting route:', error)
    throw error
  }
}

/**
 * Lấy Routes phổ biến (top routes)
 * @returns {Promise} - API response
 */
export const getPopularRoutes = async () => {
  try {
    const response = await mockApiClient.get('/api/routes/popular')
    return response
  } catch (error) {
    console.error('Error fetching popular routes:', error)
    throw error
  }
} 