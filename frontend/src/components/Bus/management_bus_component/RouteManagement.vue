<template>
  <div class="space-y-6">
    <!-- Header Section -->
    <div class="sm:flex sm:items-center sm:justify-between">
      <div>
        <h3 class="text-lg font-medium leading-6 text-gray-900">Quản lý Tuyến đường</h3>
        <p class="mt-1 text-sm text-gray-500">Tạo và quản lý các tuyến đường cơ bản (điểm đi - điểm đến)</p>
      </div>
      <div class="mt-4 sm:mt-0">
        <button @click="handleAddRoute" class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
          <svg class="-ml-1 mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          Thêm tuyến đường
        </button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-indigo-500 rounded-md flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Tổng tuyến</dt>
                <dd class="text-lg font-medium text-gray-900">{{ routes.length }}</dd>
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
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 11h.01"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Khoảng cách TB</dt>
                <dd class="text-lg font-medium text-gray-900">{{ routes.length ? Math.round(routes.reduce((sum, r) => sum + r.distanceKm, 0) / routes.length) : 0 }}km</dd>
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
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Thời gian TB</dt>
                <dd class="text-lg font-medium text-gray-900">{{ routes.length ? minutesToTimeString(routes.reduce((sum, r) => sum + r.estimatedDurationMinutes, 0) / routes.length) : '0' }}</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-purple-500 rounded-md flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Phổ biến nhất</dt>
                <dd class="text-lg font-medium text-gray-900">{{ getMostPopularRoute() }}</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Routes Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-if="isLoading" class="col-span-full text-center py-8">
        <svg class="animate-spin h-8 w-8 text-indigo-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 00-12.562-9.06" />
        </svg>
        <p class="mt-2 text-gray-600">Đang tải tuyến đường...</p>
      </div>
      
      <div v-else-if="error" class="col-span-full text-center py-8 text-red-600">
        {{ error }}
      </div>
      
      <div v-else-if="routes.length === 0" class="col-span-full text-center py-12">
        <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7"/>
        </svg>
        <h3 class="mt-2 text-sm font-medium text-gray-900">Chưa có tuyến đường nào</h3>
        <p class="mt-1 text-sm text-gray-500">Bắt đầu bằng cách tạo tuyến đường đầu tiên.</p>
        <div class="mt-6">
          <button @click="handleAddRoute" class="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700">
            <svg class="-ml-1 mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
            Thêm tuyến đường
          </button>
        </div>
      </div>
      
      <div v-else v-for="route in routes" :key="route.id" class="bg-white overflow-hidden shadow rounded-lg hover:shadow-md transition-shadow duration-200">
        <div class="p-6">
          <div class="flex items-center justify-between mb-4">
            <div class="flex items-center space-x-3">
              <div class="flex-shrink-0">
                <div class="w-10 h-10 bg-indigo-100 rounded-lg flex items-center justify-center">
                  <svg class="w-6 h-6 text-indigo-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7"/>
                  </svg>
                </div>
              </div>
              <div>
                <h4 class="text-lg font-medium text-gray-900">{{ route.origin }} → {{ route.destination }}</h4>
                <p class="text-sm text-gray-500">Tuyến số {{ route.id }}</p>
              </div>
            </div>
            <div class="flex items-center space-x-2">
              <span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800">
                Hoạt động
              </span>
            </div>
          </div>

          <div class="space-y-3">
            <!-- Thông tin cơ bản -->
            <div class="grid grid-cols-2 gap-4 text-sm">
              <div class="flex items-center text-gray-600">
                <svg class="mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 11h.01"/>
                </svg>
                <span class="font-medium">{{ route.distanceKm }}km</span>
              </div>
              <div class="flex items-center text-gray-600">
                <svg class="mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
                <span class="font-medium">{{ minutesToTimeString(route.estimatedDurationMinutes) }}</span>
              </div>
            </div>

            <!-- Ngày tạo -->
            <div class="text-sm text-gray-500">
              Tạo ngày: {{ formatDate(route.createdAt) }}
            </div>

            <!-- Actions -->
            <div class="pt-3 border-t border-gray-200">
              <div class="flex justify-between items-center">
                <div class="text-sm text-gray-500">
                  <span class="font-medium">{{ getBusRouteCount(route.id) }}</span> chuyến xe
                </div>
                <div class="flex space-x-2">
                  <button @click="handleEditRoute(route)" class="text-indigo-600 hover:text-indigo-800 text-sm font-medium">
                    Sửa
                  </button>
                  <button @click="deleteRoute(route.id)" class="text-red-600 hover:text-red-800 text-sm font-medium">
                    Xóa
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Route Modal - CHỈ TẠO ROUTE -->
    <RouteModal
      ref="routeModal"
      @route-created="handleRouteCreated"
      @route-updated="handleRouteUpdated"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import RouteModal from './RouteModal.vue'
import { getAllRoutes } from '@/api/routeApi'
import { minutesToTimeString } from '@/utils/busHelper'

// State
const routes = ref([])
const isLoading = ref(false)
const error = ref(null)

// Modal ref
const routeModal = ref(null)

// Methods
const loadRoutes = async () => {
  isLoading.value = true
  error.value = null
  
  try {
    const response = await getAllRoutes()
    
    // Mock routes data for now
    routes.value = [
      {
        id: 1,
        origin: 'Hà Nội',
        destination: 'TP. Hồ Chí Minh',
        distanceKm: 1700,
        estimatedDurationMinutes: 720,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      },
      {
        id: 2,
        origin: 'Hà Nội',
        destination: 'Đà Nẵng',
        distanceKm: 800,
        estimatedDurationMinutes: 720,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      },
      {
        id: 3,
        origin: 'TP. Hồ Chí Minh',
        destination: 'Cần Thơ',
        distanceKm: 170,
        estimatedDurationMinutes: 240,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }
    ]
    
    console.log('✅ Loaded routes:', routes.value)
  } catch (err) {
    error.value = 'Không thể tải danh sách tuyến đường'
    console.error('❌ Error loading routes:', err)
  } finally {
    isLoading.value = false
  }
}

const handleAddRoute = () => {
  console.log('🚀 Opening route creation modal')
  routeModal.value?.openModal()
}

const handleEditRoute = (route) => {
  console.log('✏️ Editing route:', route)
  routeModal.value?.openModal(route)
}

const handleRouteCreated = (data) => {
  console.log('🎉 New route created:', data)
  loadRoutes() // Reload danh sách
}

const handleRouteUpdated = (data) => {
  console.log('🔄 Route updated:', data)
  loadRoutes() // Reload danh sách
}

const deleteRoute = async (routeId) => {
  if (confirm('Bạn có chắc chắn muốn xóa tuyến đường này?')) {
    try {
      // TODO: Implement delete API call
      console.log('🗑️ Deleting route:', routeId)
      loadRoutes()
    } catch (err) {
      console.error('❌ Error deleting route:', err)
      alert('Không thể xóa tuyến đường')
    }
  }
}

// Helper methods
const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('vi-VN')
}

const getBusRouteCount = (routeId) => {
  // TODO: Get actual count from BusRoute API
  return Math.floor(Math.random() * 10) + 1
}

const getMostPopularRoute = () => {
  if (routes.value.length === 0) return 'N/A'
  const randomRoute = routes.value[Math.floor(Math.random() * routes.value.length)]
  return `${randomRoute.origin} - ${randomRoute.destination}`
}

// Lifecycle
onMounted(() => {
  loadRoutes()
})
</script> 