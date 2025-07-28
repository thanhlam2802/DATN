<template>
  <div class="space-y-6">
    <!-- Header Section -->
    <div class="sm:flex sm:items-center sm:justify-between">
      <div>
        <h3 class="text-lg font-medium leading-6 text-gray-900">Quản lý Loại xe</h3>
        <p class="mt-1 text-sm text-gray-500">Tạo và quản lý các loại xe bus (Trung chuyển, Giường nằm, Limousine...)</p>
        <div v-if="lastRefreshTime" class="mt-1 text-xs text-gray-400">
          Cập nhật lần cuối: {{ lastRefreshTime.toLocaleTimeString('vi-VN') }}
          <span v-if="isLoadingStats" class="ml-2 text-blue-500">🔄 Đang cập nhật...</span>
        </div>
      </div>
      <div class="mt-4 sm:mt-0 flex space-x-3">
        <button 
          @click="refreshStats" 
          :disabled="isLoadingStats"
          class="inline-flex items-center px-3 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500 disabled:opacity-50"
        >
          <svg class="w-4 h-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 00-12.562-9.06" />
          </svg>
          {{ isLoadingStats ? 'Đang cập nhật...' : 'Làm mới' }}
        </button>
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
                <dd class="text-lg font-medium text-gray-900">
                  <span v-if="isLoading" class="animate-pulse bg-gray-200 h-6 w-8 rounded"></span>
                  <span v-else>{{ categories.length }}</span>
                </dd>
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
                <dt class="text-sm font-medium text-gray-500 truncate">Xe của bạn</dt>
                <dd class="text-lg font-medium text-gray-900">
                  <span v-if="isLoadingStats" class="animate-pulse bg-gray-200 h-6 w-8 rounded"></span>
                  <span v-else class="flex items-center">
                    {{ getTotalBusesCount() }}
                    <span v-if="getTotalBusesCount() > 0" class="ml-2 text-xs text-green-600 bg-green-100 px-2 py-1 rounded-full">
                      Hoạt động
                    </span>
                  </span>
                </dd>
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
                <dd class="text-lg font-medium text-gray-900">
                  <span v-if="isLoadingStats" class="animate-pulse bg-gray-200 h-6 w-16 rounded"></span>
                  <span v-else class="flex flex-col">
                    <span class="text-sm font-semibold">{{ getMostPopularCategory() }}</span>
                    <span v-if="Object.keys(busStatsByCategory).length > 0" class="text-xs text-gray-500">
                      {{ Object.values(busStatsByCategory).reduce((max, stat) => Math.max(max, stat.count), 0) }} xe
                    </span>
                  </span>
                </dd>
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
                <dt class="text-sm font-medium text-gray-500 truncate">Cập nhật hôm nay</dt>
                <dd class="text-lg font-medium text-gray-900">
                  <span v-if="isLoadingStats" class="animate-pulse bg-gray-200 h-6 w-12 rounded"></span>
                  <span v-else class="text-sm">{{ getRecentlyUpdated() }}</span>
                </dd>
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
                <div class="text-2xl font-bold text-gray-900">{{ getBusCount(category) }}</div>
                <div class="text-xs text-gray-500">Số xe</div>
              </div>
              <div class="text-center">
                <div class="text-2xl font-bold text-gray-900">{{ getRouteCount(category) }}</div>
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

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import BusCategoryModal from './BusCategoryModal.vue'
import { BusCategoryAPI } from '@/api/busApi'
import { BusAPI } from '@/api/busApi/bus/api'
import { BusSlotAPI } from '@/api/busApi/busSlot/api'
import type { BusCategory } from '@/api/busApi/bus/types'
import type { Bus, BusSlot, BusSlotResponse } from '@/api/busApi/types/common.types'
// @ts-ignore
import { toast, confirm, handleError } from '@/utils/notifications'
// @ts-ignore
import { CurrentUser } from '@/utils/auth'

// State
const categories = ref<BusCategory[]>([])
const userBuses = ref<Bus[]>([])
const userBusSlots = ref<BusSlotResponse[]>([])
const isLoading = ref(false)
const isLoadingStats = ref(false)
const error = ref<string | null>(null)
const activeDropdown = ref<string | null>(null)
const lastRefreshTime = ref<Date | null>(null)
const autoRefreshInterval = ref<NodeJS.Timeout | null>(null)

// Modal ref
const categoryModal = ref<InstanceType<typeof BusCategoryModal> | null>(null)

// Computed stats
const busStatsByCategory = computed(() => {
  const stats: { [categoryId: string]: { count: number; categoryName: string } } = {}
  
  userBuses.value.forEach(bus => {
    // Handle both Bus and BusResponse structures
    let categoryId: string
    let categoryName: string
    
    if ('category' in bus && bus.category) {
      // Bus interface (has category object)
      categoryId = (bus.category as any).id
      categoryName = (bus.category as any).name
    } else if ('categoryId' in bus) {
      // BusResponse interface (has categoryId string)
      categoryId = (bus as any).categoryId
      categoryName = (bus as any).categoryName || 'Unknown'
    } else {
      console.warn('⚠️ [DEBUG] Unknown bus structure:', bus)
      return
    }
    
    if (!stats[categoryId]) {
      stats[categoryId] = {
        count: 0,
        categoryName: categoryName
      }
    }
    stats[categoryId].count++
  })
  
  return stats
})

const activeRoutes = computed(() => {
  const routeSet = new Set()
  
  userBusSlots.value.forEach((slot: BusSlotResponse) => {
    if (slot.route?.id) {
      routeSet.add(slot.route.id)
    }
  })
  
  return routeSet.size
})

const totalUserBuses = computed(() => {
  const count = userBuses.value.length
  return count
})

const mostPopularCategory = computed(() => {
  let maxCount = 0
  let popularCategory = 'N/A'
  
  Object.values(busStatsByCategory.value).forEach(stat => {
    if (stat.count > maxCount) {
      maxCount = stat.count
      popularCategory = stat.categoryName
    }
  })
  
  return popularCategory
})

const recentlyUpdatedCount = computed(() => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  
  const count = userBuses.value.filter(bus => {
    if (!bus.updatedAt) return false
    const updatedDate = new Date(bus.updatedAt)
    updatedDate.setHours(0, 0, 0, 0)
    return updatedDate.getTime() >= today.getTime()
  }).length
  
  return count
})

// Methods
const loadCategories = async () => {
  isLoading.value = true
  error.value = null
  
  try {
    categories.value = await BusCategoryAPI.getAllBusCategories()
  } catch (err) {
    error.value = 'Không thể tải danh sách loại xe'
    console.error('Error loading categories:', err)
  } finally {
    isLoading.value = false
  }
}

const loadUserBusData = async () => {
  isLoadingStats.value = true
  
  try {
    const currentUserId = CurrentUser.getId()
    
    // Load user's buses
    userBuses.value = await BusAPI.getBusesByOwnerId(currentUserId)
    
    // Load bus slots for route information
    const busSlotPromises = userBuses.value.map(bus => 
      BusSlotAPI.findBusSlotsByBusId(bus.id).catch(err => {
        console.warn(`⚠️ [DEBUG] Failed to load slots for bus ${bus.id}:`, err)
        return []
      })
    )
    
    const busSlotResults = await Promise.all(busSlotPromises)
    userBusSlots.value = busSlotResults.flat()
    
    // Update last refresh time
    lastRefreshTime.value = new Date()
    
  } catch (err) {
    console.error('❌ [DEBUG] Error loading user bus data:', err)
    // Don't show error to user for stats, just log it
  } finally {
    isLoadingStats.value = false
  }
}

// Auto-refresh functionality
const startAutoRefresh = () => {
  if (autoRefreshInterval.value) {
    clearInterval(autoRefreshInterval.value)
  }
  
  // Refresh every 30 seconds
  autoRefreshInterval.value = setInterval(async () => {
    await loadUserBusData()
  }, 30000) // 30 seconds
}

const stopAutoRefresh = () => {
  if (autoRefreshInterval.value) {
    clearInterval(autoRefreshInterval.value)
    autoRefreshInterval.value = null
  }
}

// Manual refresh
const refreshStats = async () => {
  await loadUserBusData()
}

const handleAddCategory = () => {
  activeDropdown.value = null
  categoryModal.value?.openModal(null); // Pass null for creation
}

const handleEditCategory = (category: BusCategory) => {
  activeDropdown.value = null
  categoryModal.value?.openModal(category)
}

const handleDuplicateCategory = (category: BusCategory) => {
  activeDropdown.value = null
  // Chỉ truyền tên cho modal, modal sẽ hiểu đây là tạo mới với tên gợi ý
  const newCategoryData = {
    name: `${category.name} (Bản sao)`,
  };
  categoryModal.value?.openModal(newCategoryData);
}

const handleDeleteCategory = async (categoryId: string) => {
  const confirmed = await confirm.delete('loại xe này', {
    details: 'Hành động này sẽ xóa loại xe và có thể ảnh hưởng đến các xe buýt đang sử dụng loại này.'
  })
  
  if (!confirmed) {
    return
  }
  
  try {
    await BusCategoryAPI.deleteBusCategory(categoryId)
    // Refresh the list after deletion
    await loadCategories() 
    // Refresh stats since category changed
    await loadUserBusData()
    toast.deleted('loại xe')
  } catch (err) {
    console.error('Error deleting category:', err)
    handleError.api(err, 'xóa loại xe')
  } finally {
    activeDropdown.value = null
  }
}

const handleCategoryCreated = () => {
  loadCategories() // Refresh the list
}

const handleCategoryUpdated = () => {
  loadCategories() // Refresh the list
}

const toggleDropdown = (categoryId: string) => {
  activeDropdown.value = activeDropdown.value === categoryId ? null : categoryId
}

// Helper methods
const getCategoryIconClass = (name: string): string => {
  const iconClasses: { [key: string]: string } = {
    'Trung chuyển': 'bg-blue-500',
    'Giường nằm': 'bg-green-500',
    'Limousine': 'bg-purple-500',
    'VIP': 'bg-yellow-500',
    'Ghế ngồi': 'bg-indigo-500',
    'Xe khách': 'bg-gray-500'
  }
  return iconClasses[name] || 'bg-gray-500'
}

const getCategoryBadgeClass = (name: string): string => {
  const badgeClasses: { [key: string]: string } = {
    'Trung chuyển': 'bg-blue-100 text-blue-800',
    'Giường nằm': 'bg-green-100 text-green-800',
    'Limousine': 'bg-purple-100 text-purple-800',
    'VIP': 'bg-yellow-100 text-yellow-800',
    'Ghế ngồi': 'bg-indigo-100 text-indigo-800',
    'Xe khách': 'bg-gray-100 text-gray-800'
  }
  return badgeClasses[name] || 'bg-gray-100 text-gray-800'
}

const getCategoryDisplayName = (name: string): string => {
  const displayNames: { [key: string]: string } = {
    'Trung chuyển': 'Standard',
    'Giường nằm': 'Sleeper',
    'Limousine': 'Luxury',
    'VIP': 'Premium',
    'Ghế ngồi': 'Seat',
    'Xe khách': 'Coach'
  }
  return displayNames[name] || name
}

const getBusCount = (category: BusCategory) => {
  return busStatsByCategory.value[category.id]?.count || 0
}

const getRouteCount = (category: BusCategory) => {
  // Count unique routes for this category
  const routeSet = new Set()
  userBusSlots.value.forEach((slot: BusSlotResponse) => {
    // BusResponse has categoryId, not category object
    const slotCategoryId = slot.bus?.categoryId
    if (slotCategoryId === category.id && slot.route?.id) {
      routeSet.add(slot.route.id)
    }
  })
  return routeSet.size
}

const getTotalBusesCount = () => {
  return totalUserBuses.value
}

const getMostPopularCategory = () => {
  return mostPopularCategory.value
}

const getRecentlyUpdated = () => {
  const count = recentlyUpdatedCount.value
  return count > 0 ? `${count} xe` : 'Không có'
}

// Click outside to close dropdown
const handleClickOutside = (event: MouseEvent) => {
  if (activeDropdown.value && !(event.target as HTMLElement).closest('.relative')) {
    activeDropdown.value = null
  }
}

// Lifecycle
onMounted(async () => {
  await loadCategories()
  await loadUserBusData()
  startAutoRefresh() // Start auto-refresh
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  stopAutoRefresh() // Stop auto-refresh
  document.removeEventListener('click', handleClickOutside)
})
</script>