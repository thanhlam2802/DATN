/**
 * API service for BusRoute entity operations
 * BusRoute là entity liên kết giữa Bus và Route
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
 * Tạo BusRoute mới
 * @param {object} busRouteData - Dữ liệu bus route
 * @returns {Promise} - API response
 */
export const createBusRoute = async (busRouteData) => {
  try {
    const response = await mockApiClient.post('/api/bus-routes', busRouteData)
    return response
  } catch (error) {
    console.error('Error creating bus route:', error)
    throw error
  }
}

/**
 * Lấy danh sách BusRoute theo owner (chủ xe)
 * @param {number} ownerId - ID chủ xe
 * @returns {Promise} - API response với thông tin Bus + Route đầy đủ
 */
export const getBusRoutesByOwner = async (ownerId) => {
  try {
    const response = await mockApiClient.get(`/api/bus-routes?ownerId=${ownerId}`)
    
    // Mock data với thông tin đầy đủ từ 3 entities
    const mockBusRoutes = [
      {
        id: 1,
        busId: 1,
        routeId: 1,
        travelDate: new Date().toISOString(),
        price: 350000,
        status: 'active',
        // Thông tin Bus
        bus: {
          id: 1,
          name: 'Giường nằm VIP - 51A-123.45',
          categoryId: 2,
          origin: 'hanoi',
          destination: 'hcm',
          departureTime: '2024-01-01T20:00:00',
          arrivalTime: '2024-01-02T08:00:00'
        },
        // Thông tin Route
        route: {
          id: 1,
          origin: 'hanoi',
          destination: 'hcm',
          distanceKm: 1700,
          estimatedDurationMinutes: 720
        }
      },
      {
        id: 2,
        busId: 2,
        routeId: 2,
        travelDate: new Date().toISOString(),
        price: 220000,
        status: 'active',
        bus: {
          id: 2,
          name: 'Trung chuyển - 29B-456.78',
          categoryId: 1,
          origin: 'hanoi',
          destination: 'danang',
          departureTime: '2024-01-01T06:00:00',
          arrivalTime: '2024-01-01T18:00:00'
        },
        route: {
          id: 2,
          origin: 'hanoi',
          destination: 'danang',
          distanceKm: 800,
          estimatedDurationMinutes: 720
        }
      }
    ]
    
    return { ...response, data: mockBusRoutes }
  } catch (error) {
    console.error('Error fetching bus routes by owner:', error)
    throw error
  }
}

/**
 * Lấy BusRoute theo ID
 * @param {number} busRouteId - ID bus route
 * @returns {Promise} - API response
 */
export const getBusRouteById = async (busRouteId) => {
  try {
    const response = await mockApiClient.get(`/api/bus-routes/${busRouteId}`)
    return response
  } catch (error) {
    console.error('Error fetching bus route by ID:', error)
    throw error
  }
}

/**
 * Cập nhật BusRoute
 * @param {number} busRouteId - ID bus route
 * @param {object} busRouteData - Dữ liệu cập nhật
 * @returns {Promise} - API response
 */
export const updateBusRoute = async (busRouteId, busRouteData) => {
  try {
    const response = await mockApiClient.put(`/api/bus-routes/${busRouteId}`, busRouteData)
    return response
  } catch (error) {
    console.error('Error updating bus route:', error)
    throw error
  }
}

/**
 * Xóa BusRoute
 * @param {number} busRouteId - ID bus route
 * @returns {Promise} - API response
 */
export const deleteBusRoute = async (busRouteId) => {
  try {
    const response = await mockApiClient.delete(`/api/bus-routes/${busRouteId}`)
    return response
  } catch (error) {
    console.error('Error deleting bus route:', error)
    throw error
  }
}

/**
 * Lấy BusRoute theo Route ID
 * @param {number} routeId - ID route
 * @returns {Promise} - API response
 */
export const getBusRoutesByRoute = async (routeId) => {
  try {
    const response = await mockApiClient.get(`/api/bus-routes?routeId=${routeId}`)
    return response
  } catch (error) {
    console.error('Error fetching bus routes by route:', error)
    throw error
  }
}

/**
 * Lấy BusRoute theo Bus ID
 * @param {number} busId - ID bus
 * @returns {Promise} - API response
 */
export const getBusRoutesByBus = async (busId) => {
  try {
    const response = await mockApiClient.get(`/api/bus-routes?busId=${busId}`)
    return response
  } catch (error) {
    console.error('Error fetching bus routes by bus:', error)
    throw error
  }
}

/**
 * Tìm BusRoute để khách hàng đặt vé
 * @param {object} searchParams - Tham số tìm kiếm
 * @returns {Promise} - API response
 */
export const searchBusRoutes = async (searchParams) => {
  const { origin, destination, travelDate } = searchParams
  try {
    const response = await mockApiClient.get(
      `/api/bus-routes/search?origin=${origin}&destination=${destination}&date=${travelDate}`
    )
    return response
  } catch (error) {
    console.error('Error searching bus routes:', error)
    throw error
  }
}

/**
 * Cập nhật trạng thái BusRoute
 * @param {number} busRouteId - ID bus route
 * @param {string} status - Trạng thái mới
 * @returns {Promise} - API response
 */
export const updateBusRouteStatus = async (busRouteId, status) => {
  try {
    const response = await mockApiClient.put(`/api/bus-routes/${busRouteId}/status`, { status })
    return response
  } catch (error) {
    console.error('Error updating bus route status:', error)
    throw error
  }
} 