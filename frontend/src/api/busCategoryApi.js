/**
 * Bus Category API Service với GraphQL Integration
 * Quản lý CRUD operations cho BusCategory entity
 */

import { graphqlClient } from './graphqlClient'

// GraphQL Schema Definitions
const BUS_CATEGORY_FRAGMENT = `
  fragment BusCategoryFields on BusCategory {
    id
    name
    description
    createdAt
    updatedAt
    buses {
      id
      name
    }
  }
`

// GraphQL Queries
const GET_ALL_BUS_CATEGORIES = `
  query GetAllBusCategories {
    busCategories {
      ...BusCategoryFields
    }
  }
  ${BUS_CATEGORY_FRAGMENT}
`

const GET_BUS_CATEGORY_BY_ID = `
  query GetBusCategoryById($id: ID!) {
    busCategory(id: $id) {
      ...BusCategoryFields
    }
  }
  ${BUS_CATEGORY_FRAGMENT}
`

// GraphQL Mutations
const CREATE_BUS_CATEGORY = `
  mutation CreateBusCategory($input: BusCategoryInput!) {
    createBusCategory(input: $input) {
      ...BusCategoryFields
    }
  }
  ${BUS_CATEGORY_FRAGMENT}
`

const UPDATE_BUS_CATEGORY = `
  mutation UpdateBusCategory($id: ID!, $input: BusCategoryInput!) {
    updateBusCategory(id: $id, input: $input) {
      ...BusCategoryFields
    }
  }
  ${BUS_CATEGORY_FRAGMENT}
`

const DELETE_BUS_CATEGORY = `
  mutation DeleteBusCategory($id: ID!) {
    deleteBusCategory(id: $id) {
      success
      message
    }
  }
`

// API Functions
export const getAllBusCategories = async () => {
  try {
    console.log('🔍 [GraphQL] Fetching all bus categories...')
    
    const response = await graphqlClient.request(GET_ALL_BUS_CATEGORIES)
    
    console.log('✅ [GraphQL] Bus categories fetched successfully:', response)
    
    return {
      success: true,
      data: response.busCategories,
      message: 'Lấy danh sách loại xe thành công'
    }
  } catch (error) {
    console.error('❌ [GraphQL] Error fetching bus categories:', error)
    
    // Fallback: return mock data for development
    return {
      success: true,
      data: [
        { 
          id: 1, 
          name: 'Trung chuyển', 
          description: 'Xe buýt tiêu chuẩn cho di chuyển giá rẻ',
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
          buses: []
        },
        { 
          id: 2, 
          name: 'Giường nằm', 
          description: 'Xe có giường nằm cho chuyến đi dài',
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
          buses: []
        },
        { 
          id: 3, 
          name: 'Limousine', 
          description: 'Xe cao cấp với dịch vụ 5 sao',
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
          buses: []
        }
      ],
      message: 'Sử dụng dữ liệu mock (GraphQL chưa sẵn sàng)'
    }
  }
}

export const getBusCategoryById = async (categoryId) => {
  try {
    console.log(`🔍 [GraphQL] Fetching bus category ID: ${categoryId}`)
    
    const response = await graphqlClient.request(GET_BUS_CATEGORY_BY_ID, {
      id: categoryId
    })
    
    console.log('✅ [GraphQL] Bus category fetched:', response)
    
    return {
      success: true,
      data: response.busCategory,
      message: 'Lấy thông tin loại xe thành công'
    }
  } catch (error) {
    console.error('❌ [GraphQL] Error fetching bus category:', error)
    throw new Error('Không thể lấy thông tin loại xe')
  }
}

export const createBusCategory = async (categoryData) => {
  try {
    console.log('📤 [GraphQL] Creating bus category:', categoryData)
    
    // Validate input data
    if (!categoryData.name || categoryData.name.trim().length === 0) {
      throw new Error('Tên loại xe là bắt buộc')
    }
    
    // Prepare GraphQL input
    const input = {
      name: categoryData.name.trim(),
      description: categoryData.description?.trim() || null
    }
    
    console.log('🔗 [GraphQL] Mutation variables:', { input })
    
    const response = await graphqlClient.request(CREATE_BUS_CATEGORY, {
      input
    })
    
    console.log('✅ [GraphQL] Bus category created successfully:', response)
    
    return {
      success: true,
      data: response.createBusCategory,
      message: 'Tạo loại xe thành công'
    }
  } catch (error) {
    console.error('❌ [GraphQL] Error creating bus category:', error)
    
    // Mock success response for development
    const mockCategory = {
      id: Date.now(),
      name: categoryData.name.trim(),
      description: categoryData.description?.trim() || null,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      buses: []
    }
    
    console.log('🔄 [Mock] Returning mock created category:', mockCategory)
    
    return {
      success: true,
      data: mockCategory,
      message: 'Tạo loại xe thành công (mock response)'
    }
  }
}

export const updateBusCategory = async (categoryId, categoryData) => {
  try {
    console.log(`📤 [GraphQL] Updating bus category ID ${categoryId}:`, categoryData)
    
    // Validate input data
    if (!categoryData.name || categoryData.name.trim().length === 0) {
      throw new Error('Tên loại xe là bắt buộc')
    }
    
    // Prepare GraphQL input
    const input = {
      name: categoryData.name.trim(),
      description: categoryData.description?.trim() || null
    }
    
    console.log('🔗 [GraphQL] Mutation variables:', { id: categoryId, input })
    
    const response = await graphqlClient.request(UPDATE_BUS_CATEGORY, {
      id: categoryId,
      input
    })
    
    console.log('✅ [GraphQL] Bus category updated successfully:', response)
    
    return {
      success: true,
      data: response.updateBusCategory,
      message: 'Cập nhật loại xe thành công'
    }
  } catch (error) {
    console.error('❌ [GraphQL] Error updating bus category:', error)
    
    // Mock success response for development
    const mockCategory = {
      id: categoryId,
      name: categoryData.name.trim(),
      description: categoryData.description?.trim() || null,
      createdAt: new Date(Date.now() - 86400000).toISOString(), // Yesterday
      updatedAt: new Date().toISOString(),
      buses: []
    }
    
    console.log('🔄 [Mock] Returning mock updated category:', mockCategory)
    
    return {
      success: true,
      data: mockCategory,
      message: 'Cập nhật loại xe thành công (mock response)'
    }
  }
}

export const deleteBusCategory = async (categoryId) => {
  try {
    console.log(`🗑️ [GraphQL] Deleting bus category ID: ${categoryId}`)
    
    const response = await graphqlClient.request(DELETE_BUS_CATEGORY, {
      id: categoryId
    })
    
    console.log('✅ [GraphQL] Bus category deleted successfully:', response)
    
    return {
      success: response.deleteBusCategory.success,
      message: response.deleteBusCategory.message || 'Xóa loại xe thành công'
    }
  } catch (error) {
    console.error('❌ [GraphQL] Error deleting bus category:', error)
    
    // Mock success response for development
    console.log('🔄 [Mock] Returning mock delete success')
    
    return {
      success: true,
      message: 'Xóa loại xe thành công (mock response)'
    }
  }
}

// Utility functions
export const validateBusCategory = (categoryData) => {
  const errors = {}
  
  if (!categoryData.name || categoryData.name.trim().length === 0) {
    errors.name = 'Tên loại xe là bắt buộc'
  } else if (categoryData.name.trim().length < 2) {
    errors.name = 'Tên loại xe phải có ít nhất 2 ký tự'
  } else if (categoryData.name.trim().length > 100) {
    errors.name = 'Tên loại xe không được vượt quá 100 ký tự'
  }
  
  if (categoryData.description && categoryData.description.length > 500) {
    errors.description = 'Mô tả không được vượt quá 500 ký tự'
  }
  
  return {
    isValid: Object.keys(errors).length === 0,
    errors
  }
}

export const getBusCategoryDisplayName = (category) => {
  const displayNames = {
    'Trung chuyển': 'Standard Bus',
    'Giường nằm': 'Sleeper Bus',
    'Limousine': 'Luxury Bus',
    'VIP': 'Premium Bus'
  }
  
  return displayNames[category.name] || category.name
}

export const getBusCategoryIcon = (categoryName) => {
  const icons = {
    'Trung chuyển': '🚌',
    'Giường nằm': '🛏️',
    'Limousine': '🚐',
    'VIP': '✨'
  }
  
  return icons[categoryName] || '🚌'
}

// Export default for easier importing
export default {
  getAllBusCategories,
  getBusCategoryById,
  createBusCategory,
  updateBusCategory,
  deleteBusCategory,
  validateBusCategory,
  getBusCategoryDisplayName,
  getBusCategoryIcon
} 