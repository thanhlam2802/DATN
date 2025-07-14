/**
 * API service for BusCategory entity operations
 */

// Reuse mock API client
const mockApiClient = {
  get: async (url) => {
    console.log('Mock GET:', url)
    await new Promise(resolve => setTimeout(resolve, 300))
    
    // Mock bus categories data
    const mockCategories = [
      {
        id: 1,
        name: 'Trung chuyển',
        description: 'Xe 16 chỗ ngồi thường',
        seatCount: 16,
        features: ['Ghế ngồi thường', 'Điều hòa', 'WiFi']
      },
      {
        id: 2,
        name: 'Giường nằm',
        description: 'Xe 36 giường nằm',
        seatCount: 36,
        features: ['Giường nằm', 'Điều hòa', 'WiFi', 'Chăn gối', 'Màn cửa riêng tư']
      },
      {
        id: 3,
        name: 'Limousine',
        description: 'Xe 24 ghế VIP',
        seatCount: 24,
        features: ['Ghế massage', 'TV cá nhân', 'Điều hòa', 'WiFi', 'Đồ ăn nhẹ', 'Nước uống']
      }
    ]
    
    return {
      data: mockCategories
    }
  }
}

/**
 * Lấy danh sách tất cả BusCategory
 * @returns {Promise} - API response
 */
export const getAllBusCategories = async () => {
  try {
    const response = await mockApiClient.get('/api/bus-categories')
    return response
  } catch (error) {
    console.error('Error fetching bus categories:', error)
    throw error
  }
}

/**
 * Lấy BusCategory theo ID
 * @param {number} categoryId - ID category
 * @returns {Promise} - API response
 */
export const getBusCategoryById = async (categoryId) => {
  try {
    const allCategories = await getAllBusCategories()
    const category = allCategories.data.find(cat => cat.id === categoryId)
    
    return {
      data: category || null
    }
  } catch (error) {
    console.error('Error fetching bus category by ID:', error)
    throw error
  }
}

/**
 * Lấy danh sách BusCategory cho dropdown
 * @returns {Promise} - API response với format đơn giản
 */
export const getBusCategoriesForDropdown = async () => {
  try {
    const response = await getAllBusCategories()
    
    // Format cho dropdown
    const dropdownOptions = response.data.map(category => ({
      value: category.id,
      label: `${category.name} (${category.seatCount} chỗ)`,
      seatCount: category.seatCount
    }))
    
    return {
      data: dropdownOptions
    }
  } catch (error) {
    console.error('Error fetching bus categories for dropdown:', error)
    throw error
  }
} 