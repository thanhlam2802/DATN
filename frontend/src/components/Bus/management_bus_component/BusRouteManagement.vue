<template>
  <div class="space-y-6">
    <!-- Header Section -->
    <div class="sm:flex sm:items-center sm:justify-between">
      <div>
        <h3 class="text-lg font-medium leading-6 text-gray-900">Quản lý Chuyến xe</h3>
        <p class="mt-1 text-sm text-gray-500">Tạo chuyến xe bằng cách kết hợp Bus và Route, thiết lập giá và lịch trình</p>
      </div>
      <div class="mt-4 sm:mt-0">
        <button @click="handleAddBusRoute" class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500">
          <svg class="-ml-1 mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          Thêm chuyến xe
        </button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-green-500 rounded-md flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Tổng chuyến</dt>
                <dd class="text-lg font-medium text-gray-900">{{ busRoutes.length }}</dd>
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
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Đang hoạt động</dt>
                <dd class="text-lg font-medium text-gray-900">{{ busRoutes.filter(br => br.status === 'active').length }}</dd>
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
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Giá trung bình</dt>
                <dd class="text-lg font-medium text-gray-900">{{ busRoutes.length ? formatPrice(busRoutes.reduce((sum, br) => sum + br.price, 0) / busRoutes.length) : 0 }}đ</dd>
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
                <dt class="text-sm font-medium text-gray-500 truncate">Hôm nay</dt>
                <dd class="text-lg font-medium text-gray-900">{{ getTodayBusRoutes() }}</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Filters -->
    <div class="bg-white shadow rounded-lg p-4">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Lọc theo trạng thái</label>
          <select v-model="filters.status" class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-green-500 focus:border-green-500">
            <option value="">Tất cả</option>
            <option value="active">Đang hoạt động</option>
            <option value="inactive">Tạm dừng</option>
            <option value="cancelled">Đã hủy</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Lọc theo tuyến</label>
          <select v-model="filters.routeId" class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-green-500 focus:border-green-500">
            <option value="">Tất cả tuyến</option>
            <option v-for="route in availableRoutes" :key="route.id" :value="route.id">
              {{ route.origin }} → {{ route.destination }}
            </option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Từ ngày</label>
          <input 
            v-model="filters.fromDate" 
            type="date" 
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-green-500 focus:border-green-500"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Đến ngày</label>
          <input 
            v-model="filters.toDate" 
            type="date" 
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-green-500 focus:border-green-500"
          />
        </div>
      </div>
    </div>

    <!-- Bus Routes Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-if="isLoading" class="col-span-full text-center py-8">
        <svg class="animate-spin h-8 w-8 text-green-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 00-12.562-9.06" />
        </svg>
        <p class="mt-2 text-gray-600">Đang tải chuyến xe...</p>
      </div>
      
      <div v-else-if="error" class="col-span-full text-center py-8 text-red-600">
        {{ error }}
      </div>
      
      <div v-else-if="filteredBusRoutes.length === 0" class="col-span-full text-center py-12">
        <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
        </svg>
        <h3 class="mt-2 text-sm font-medium text-gray-900">Chưa có chuyến xe nào</h3>
        <p class="mt-1 text-sm text-gray-500">Bắt đầu bằng cách tạo chuyến xe đầu tiên.</p>
        <div class="mt-6">
          <button @click="handleAddBusRoute" class="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700">
            <svg class="-ml-1 mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
            Thêm chuyến xe
          </button>
        </div>
      </div>
      
      <div v-else v-for="busRoute in filteredBusRoutes" :key="busRoute.id" class="bg-white overflow-hidden shadow rounded-lg hover:shadow-md transition-shadow duration-200">
        <div class="p-6">
          <div class="flex items-center justify-between mb-4">
            <div class="flex items-center space-x-3">
              <div class="flex-shrink-0">
                <div class="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
                  <svg class="w-6 h-6 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                  </svg>
                </div>
              </div>
              <div>
                <h4 class="text-lg font-medium text-gray-900">{{ busRoute.route.origin }} → {{ busRoute.route.destination }}</h4>
                <p class="text-sm text-gray-500">{{ formatDate(busRoute.travelDate) }}</p>
              </div>
            </div>
            <div class="flex items-center space-x-2">
              <span :class="getStatusBadgeClass(busRoute.status)" class="inline-flex px-2 py-1 text-xs font-semibold rounded-full">
                {{ getStatusText(busRoute.status) }}
              </span>
            </div>
          </div>

          <div class="space-y-3">
            <!-- Bus info -->
            <div class="flex items-center text-sm text-gray-600">
              <svg class="mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17a2 2 0 11-4 0 2 2 0 014 0zM19 17a2 2 0 11-4 0 2 2 0 014 0z"/>
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 6h3l2 7H9M9 17v-2a2 2 0 00-2-2H4"/>
              </svg>
              <span class="font-medium">{{ getBusTypeName(busRoute.bus.categoryId) }}</span>
              <span class="mx-2">•</span>
              <span>{{ busRoute.bus.name }}</span>
            </div>

            <!-- Time info -->
            <div class="flex items-center text-sm text-gray-600">
              <svg class="mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
              <span>{{ formatTime(busRoute.bus.departureTime) }} - {{ formatTime(busRoute.bus.arrivalTime) }}</span>
              <span class="mx-2">•</span>
              <span>{{ minutesToTimeString(busRoute.route.estimatedDurationMinutes) }}</span>
            </div>

            <!-- Distance and Price -->
            <div class="flex items-center justify-between text-sm">
              <div class="flex items-center text-gray-600">
                <svg class="mr-1 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 11h.01"/>
                </svg>
                {{ busRoute.route.distanceKm }}km
              </div>
              <div class="text-lg font-semibold text-green-600">
                {{ formatPrice(busRoute.price) }}đ
              </div>
            </div>

            <!-- Actions -->
            <div class="pt-3 border-t border-gray-200">
              <div class="flex justify-between items-center">
                <div class="text-sm text-gray-500">
                  Tạo: {{ formatDate(busRoute.createdAt) }}
                </div>
                <div class="flex space-x-2">
                  <button @click="handleEditBusRoute(busRoute)" class="text-green-600 hover:text-green-800 text-sm font-medium">
                    Sửa
                  </button>
                  <button @click="deleteBusRoute(busRoute.id)" class="text-red-600 hover:text-red-800 text-sm font-medium">
                    Xóa
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Bus Route Modal -->
    <BusRouteCreationModal
      ref="busRouteModal"
      @bus-route-created="handleBusRouteCreated"
      @bus-route-updated="handleBusRouteUpdated"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import BusRouteCreationModal from './BusRouteCreationModal.vue'
import { getAllBusRoutes } from '@/api/busRouteApi'
import { getAllRoutes } from '@/api/routeApi'
import { formatPrice, minutesToTimeString } from '@/utils/busHelper'

// State
const busRoutes = ref([])
const availableRoutes = ref([])
const isLoading = ref(false)
const error = ref(null)

// Modal ref
const busRouteModal = ref(null)

// Filters
const filters = reactive({
  status: '',
  routeId: '',
  fromDate: '',
  toDate: ''
})

// Computed
const filteredBusRoutes = computed(() => {
  let filtered = busRoutes.value

  if (filters.status) {
    filtered = filtered.filter(br => br.status === filters.status)
  }
  
  if (filters.routeId) {
    filtered = filtered.filter(br => br.route.id == filters.routeId)
  }
  
  if (filters.fromDate) {
    filtered = filtered.filter(br => br.travelDate >= filters.fromDate)
  }
  
  if (filters.toDate) {
    filtered = filtered.filter(br => br.travelDate <= filters.toDate)
  }

  return filtered
})

// Methods
const loadBusRoutes = async () => {
  isLoading.value = true
  error.value = null
  
  try {
    const response = await getAllBusRoutes()
    
    // Mock data for demo
    busRoutes.value = [
      {
        id: 1,
        bus: {
          id: 1,
          name: 'Xe Limousine - 29A-123.45',
          categoryId: 3,
          departureTime: '2024-01-15T08:00:00',
          arrivalTime: '2024-01-15T20:00:00'
        },
        route: {
          id: 1,
          origin: 'Hà Nội',
          destination: 'TP. Hồ Chí Minh',
          distanceKm: 1700,
          estimatedDurationMinutes: 720
        },
        travelDate: '2024-01-15',
        price: 850000,
        status: 'active',
        createdAt: '2024-01-10T10:00:00',
        updatedAt: '2024-01-10T10:00:00'
      },
      {
        id: 2,
        bus: {
          id: 2,
          name: 'Xe Giường nằm - 30B-678.90',
          categoryId: 2,
          departureTime: '2024-01-15T22:00:00',
          arrivalTime: '2024-01-16T10:00:00'
        },
        route: {
          id: 2,
          origin: 'Hà Nội',
          destination: 'Đà Nẵng',
          distanceKm: 800,
          estimatedDurationMinutes: 720
        },
        travelDate: '2024-01-15',
        price: 420000,
        status: 'active',
        createdAt: '2024-01-10T10:00:00',
        updatedAt: '2024-01-10T10:00:00'
      }
    ]
    
    console.log('✅ Loaded bus routes:', busRoutes.value)
  } catch (err) {
    error.value = 'Không thể tải danh sách chuyến xe'
    console.error('❌ Error loading bus routes:', err)
  } finally {
    isLoading.value = false
  }
}

const loadAvailableRoutes = async () => {
  try {
    const response = await getAllRoutes()
    // Use mock data for routes
    availableRoutes.value = [
      { id: 1, origin: 'Hà Nội', destination: 'TP. Hồ Chí Minh' },
      { id: 2, origin: 'Hà Nội', destination: 'Đà Nẵng' },
      { id: 3, origin: 'TP. Hồ Chí Minh', destination: 'Cần Thơ' }
    ]
  } catch (err) {
    console.error('❌ Error loading routes:', err)
  }
}

const handleAddBusRoute = () => {
  console.log('🚀 Opening bus route creation modal')
  busRouteModal.value?.openModal()
}

const handleEditBusRoute = (busRoute) => {
  console.log('✏️ Editing bus route:', busRoute)
  busRouteModal.value?.openModal(busRoute)
}

const handleBusRouteCreated = (data) => {
  console.log('🎉 New bus route created:', data)
  loadBusRoutes()
}

const handleBusRouteUpdated = (data) => {
  console.log('🔄 Bus route updated:', data)
  loadBusRoutes()
}

const deleteBusRoute = async (busRouteId) => {
  if (confirm('Bạn có chắc chắn muốn xóa chuyến xe này?')) {
    try {
      // TODO: Implement delete API call
      console.log('🗑️ Deleting bus route:', busRouteId)
      loadBusRoutes()
    } catch (err) {
      console.error('❌ Error deleting bus route:', err)
      alert('Không thể xóa chuyến xe')
    }
  }
}

// Helper methods
const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('vi-VN')
}

const formatTime = (dateTimeString) => {
  if (!dateTimeString) return '--:--'
  return dateTimeString.split('T')[1]?.substring(0, 5) || '--:--'
}

const getBusTypeName = (categoryId) => {
  const categories = {
    1: 'Trung chuyển',
    2: 'Giường nằm',
    3: 'Limousine'
  }
  return categories[categoryId] || 'Không rõ'
}

const getStatusText = (status) => {
  const statusText = {
    'active': 'Hoạt động',
    'inactive': 'Tạm dừng',
    'cancelled': 'Đã hủy'
  }
  return statusText[status] || 'Không rõ'
}

const getStatusBadgeClass = (status) => {
  const statusClasses = {
    'active': 'bg-green-100 text-green-800',
    'inactive': 'bg-yellow-100 text-yellow-800',
    'cancelled': 'bg-red-100 text-red-800'
  }
  return statusClasses[status] || 'bg-gray-100 text-gray-800'
}

const getTodayBusRoutes = () => {
  const today = new Date().toISOString().split('T')[0]
  return busRoutes.value.filter(br => br.travelDate === today).length
}

// Lifecycle
onMounted(() => {
  loadBusRoutes()
  loadAvailableRoutes()
})
</script> 