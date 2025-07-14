<template>
  <div class="space-y-6">
    <!-- Header Section -->
    <div class="sm:flex sm:items-center sm:justify-between">
      <div>
        <h3 class="text-lg font-medium leading-6 text-gray-900">Quản lý Loại xe</h3>
        <p class="mt-1 text-sm text-gray-500">Tạo và quản lý các loại xe bus (Trung chuyển, Giường nằm, Limousine...)</p>
      </div>
      <div class="mt-4 sm:mt-0">
        <button @click="handleAddCategory" class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-purple-600 hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500">
          <svg class="-ml-1 mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          Thêm loại xe
        </button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-purple-500 rounded-md flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Tổng loại xe</dt>
                <dd class="text-lg font-medium text-gray-900">{{ categories.length }}</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>
      
      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-blue-500 rounded-md flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17a2 2 0 11-4 0 2 2 0 014 0zM19 17a2 2 0 11-4 0 2 2 0 014 0z"/>
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 6h3l2 7H9M9 17v-2a2 2 0 00-2-2H4"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Xe đang sử dụng</dt>
                <dd class="text-lg font-medium text-gray-900">{{ getTotalBusesCount() }}</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-green-500 rounded-md flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Phổ biến nhất</dt>
                <dd class="text-lg font-medium text-gray-900">{{ getMostPopularCategory() }}</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-yellow-500 rounded-md flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Cập nhật gần đây</dt>
                <dd class="text-lg font-medium text-gray-900">{{ getRecentlyUpdated() }}</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Categories Grid -->
    <div class="bg-white shadow rounded-lg">
      <div class="px-4 py-5 sm:p-6">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div v-if="isLoading" class="col-span-full text-center py-8">
            <svg class="animate-spin h-8 w-8 text-purple-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 00-12.562-9.06" />
            </svg>
            <p class="mt-2 text-gray-600">Đang tải danh sách loại xe...</p>
          </div>
          
          <div v-else-if="error" class="col-span-full text-center py-8 text-red-600">
            {{ error }}
          </div>
          
          <div v-else-if="categories.length === 0" class="col-span-full text-center py-12">
            <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2"/>
            </svg>
            <h3 class="mt-2 text-sm font-medium text-gray-900">Chưa có loại xe nào</h3>
            <p class="mt-1 text-sm text-gray-500">Bắt đầu bằng cách tạo loại xe đầu tiên.</p>
            <div class="mt-6">
              <button @click="handleAddCategory" class="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-purple-600 hover:bg-purple-700">
                <svg class="-ml-1 mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                </svg>
                Thêm loại xe
              </button>
            </div>
          </div>
          
          <div v-else v-for="category in categories" :key="category.id" class="relative bg-white border border-gray-300 rounded-lg p-6 hover:shadow-md transition-shadow duration-200">
            <div class="flex items-center justify-between mb-4">
              <div class="flex items-center space-x-3">
                <div class="flex-shrink-0">
                  <div :class="getCategoryIconClass(category.name)" class="w-10 h-10 rounded-lg flex items-center justify-center">
                    <svg class="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17a2 2 0 11-4 0 2 2 0 014 0zM19 17a2 2 0 11-4 0 2 2 0 014 0z"/>
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 6h3l2 7H9M9 17v-2a2 2 0 00-2-2H4"/>
                    </svg>
                  </div>
                </div>
                <div>
                  <h4 class="text-lg font-medium text-gray-900">{{ category.name }}</h4>
                  <p class="text-sm text-gray-500">Loại xe {{ category.id }}</p>
                </div>
              </div>
              
              <!-- Actions dropdown -->
              <div class="relative">
                <button @click="toggleDropdown(category.id)" class="text-gray-400 hover:text-gray-600 focus:outline-none">
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 5v.01M12 12v.01M12 19v.01"/>
                  </svg>
                </button>
                
                <div v-if="activeDropdown === category.id" class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg z-10 border border-gray-200">
                  <button @click="handleEditCategory(category)" class="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center">
                    <svg class="mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/>
                    </svg>
                    Chỉnh sửa
                  </button>
                  <button @click="handleDuplicateCategory(category)" class="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center">
                    <svg class="mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"/>
                    </svg>
                    Nhân bản
                  </button>
                  <hr class="my-1">
                  <button @click="handleDeleteCategory(category.id)" class="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 flex items-center">
                    <svg class="mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                    </svg>
                    Xóa
                  </button>
                </div>
              </div>
            </div>

            <!-- Category Stats -->
            <div class="grid grid-cols-2 gap-4 mt-4">
              <div class="text-center">
                <div class="text-2xl font-bold text-gray-900">{{ getBusCount(category.id) }}</div>
                <div class="text-xs text-gray-500">Số xe</div>
              </div>
              <div class="text-center">
                <div class="text-2xl font-bold text-gray-900">{{ getRouteCount(category.id) }}</div>
                <div class="text-xs text-gray-500">Tuyến hoạt động</div>
              </div>
            </div>

            <!-- Category Badge -->
            <div class="mt-4 flex justify-between items-center">
              <span :class="getCategoryBadgeClass(category.name)" class="inline-flex px-2 py-1 text-xs font-semibold rounded-full">
                {{ getCategoryDisplayName(category.name) }}
              </span>
              <div class="text-xs text-gray-500">
                ID: #{{ category.id }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Bus Category Modal -->
    <BusCategoryModal
      ref="categoryModal"
      @category-created="handleCategoryCreated"
      @category-updated="handleCategoryUpdated"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import BusCategoryModal from './BusCategoryModal.vue'
import { getAllBusCategories, deleteBusCategory } from '@/api/busCategoryApi'

// State
const categories = ref([])
const isLoading = ref(false)
const error = ref(null)
const activeDropdown = ref(null)

// Modal ref
const categoryModal = ref(null)

// Methods
const loadCategories = async () => {
  isLoading.value = true
  error.value = null
  
  try {
    const response = await getAllBusCategories()
    
    // Mock data matching backend BusCategory entity
    categories.value = [
      { id: 1, name: 'Trung chuyển', busCount: 15, routeCount: 8 },
      { id: 2, name: 'Giường nằm', busCount: 25, routeCount: 12 },
      { id: 3, name: 'Limousine', busCount: 10, routeCount: 6 },
      { id: 4, name: 'VIP', busCount: 8, routeCount: 4 }
    ]
    
    console.log('✅ Loaded bus categories:', categories.value)
  } catch (err) {
    error.value = 'Không thể tải danh sách loại xe'
    console.error('❌ Error loading categories:', err)
  } finally {
    isLoading.value = false
  }
}

const handleAddCategory = () => {
  console.log('🚀 Opening category creation modal')
  activeDropdown.value = null
  categoryModal.value?.openModal()
}

const handleEditCategory = (category) => {
  console.log('✏️ Editing category:', category)
  activeDropdown.value = null
  categoryModal.value?.openModal(category)
}

const handleDuplicateCategory = (category) => {
  console.log('📋 Duplicating category:', category)
  activeDropdown.value = null
  const duplicatedCategory = {
    ...category,
    name: `${category.name} (Bản sao)`,
    id: null // Will be assigned by backend
  }
  categoryModal.value?.openModal(duplicatedCategory)
}

const handleDeleteCategory = async (categoryId) => {
  activeDropdown.value = null
  
  const category = categories.value.find(c => c.id === categoryId)
  const busCount = getBusCount(categoryId)
  
  if (busCount > 0) {
    alert(`Không thể xóa loại xe "${category.name}" vì đang có ${busCount} xe sử dụng loại này.`)
    return
  }
  
  if (confirm(`Bạn có chắc chắn muốn xóa loại xe "${category.name}"?`)) {
    try {
      await deleteBusCategory(categoryId)
      console.log('🗑️ Deleted category:', categoryId)
      loadCategories()
    } catch (err) {
      console.error('❌ Error deleting category:', err)
      alert('Không thể xóa loại xe. Vui lòng thử lại.')
    }
  }
}

const handleCategoryCreated = (data) => {
  console.log('🎉 New category created:', data)
  loadCategories()
}

const handleCategoryUpdated = (data) => {
  console.log('🔄 Category updated:', data)
  loadCategories()
}

const toggleDropdown = (categoryId) => {
  activeDropdown.value = activeDropdown.value === categoryId ? null : categoryId
}

// Helper methods
const getCategoryIconClass = (name) => {
  const iconClasses = {
    'Trung chuyển': 'bg-blue-500',
    'Giường nằm': 'bg-green-500',
    'Limousine': 'bg-purple-500',
    'VIP': 'bg-yellow-500'
  }
  return iconClasses[name] || 'bg-gray-500'
}

const getCategoryBadgeClass = (name) => {
  const badgeClasses = {
    'Trung chuyển': 'bg-blue-100 text-blue-800',
    'Giường nằm': 'bg-green-100 text-green-800',
    'Limousine': 'bg-purple-100 text-purple-800',
    'VIP': 'bg-yellow-100 text-yellow-800'
  }
  return badgeClasses[name] || 'bg-gray-100 text-gray-800'
}

const getCategoryDisplayName = (name) => {
  const displayNames = {
    'Trung chuyển': 'Standard',
    'Giường nằm': 'Sleeper',
    'Limousine': 'Luxury',
    'VIP': 'Premium'
  }
  return displayNames[name] || name
}

const getBusCount = (categoryId) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category?.busCount || 0
}

const getRouteCount = (categoryId) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category?.routeCount || 0
}

const getTotalBusesCount = () => {
  return categories.value.reduce((total, category) => total + (category.busCount || 0), 0)
}

const getMostPopularCategory = () => {
  if (categories.value.length === 0) return 'N/A'
  const mostPopular = categories.value.reduce((prev, current) => 
    (current.busCount > prev.busCount) ? current : prev
  )
  return mostPopular.name
}

const getRecentlyUpdated = () => {
  return categories.value.length > 0 ? 'Hôm nay' : 'N/A'
}

// Click outside to close dropdown
const handleClickOutside = (event) => {
  if (activeDropdown.value && !event.target.closest('.relative')) {
    activeDropdown.value = null
  }
}

// Lifecycle
onMounted(() => {
  loadCategories()
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script> 