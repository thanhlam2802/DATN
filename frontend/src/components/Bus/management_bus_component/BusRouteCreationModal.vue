<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 overflow-y-auto" @click="closeModal">
    <div class="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
      <!-- Background overlay -->
      <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"></div>
      
      <!-- Modal panel -->
      <div @click.stop class="relative inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-2xl sm:w-full">
        <div class="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
          <div class="sm:flex sm:items-start">
            <div class="w-full mt-3 text-center sm:mt-0 sm:text-left">
              <h3 class="text-lg leading-6 font-medium text-gray-900 mb-6">
                {{ isEditing ? 'Chỉnh sửa chuyến xe' : 'Thêm chuyến xe mới' }}
              </h3>
              
              <!-- Form -->
              <form @submit.prevent="handleSubmit" class="space-y-6">
                <!-- Select Route -->
                <div>
                  <label for="routeId" class="block text-sm font-medium text-gray-700 mb-2">
                    Chọn tuyến đường <span class="text-red-500">*</span>
                  </label>
                  <select
                    id="routeId"
                    v-model="form.routeId"
                    required
                    class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-green-500 focus:border-green-500"
                  >
                    <option value="">-- Chọn tuyến đường --</option>
                    <option v-for="route in availableRoutes" :key="route.id" :value="route.id">
                      {{ route.origin }} → {{ route.destination }} ({{ route.distanceKm }}km, {{ minutesToTimeString(route.estimatedDurationMinutes) }})
                    </option>
                  </select>
                  <p v-if="errors.routeId" class="text-red-500 text-xs mt-1">{{ errors.routeId }}</p>
                </div>

                <!-- Select Bus -->
                <div>
                  <label for="busId" class="block text-sm font-medium text-gray-700 mb-2">
                    Chọn xe <span class="text-red-500">*</span>
                  </label>
                  <select
                    id="busId"
                    v-model="form.busId"
                    required
                    class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-green-500 focus:border-green-500"
                  >
                    <option value="">-- Chọn xe --</option>
                    <option v-for="bus in availableBuses" :key="bus.id" :value="bus.id">
                      {{ bus.name }} - {{ getBusTypeName(bus.categoryId) }} ({{ formatTime(bus.departureTime) }} - {{ formatTime(bus.arrivalTime) }})
                    </option>
                  </select>
                  <p v-if="errors.busId" class="text-red-500 text-xs mt-1">{{ errors.busId }}</p>
                </div>

                <!-- Travel Date -->
                <div>
                  <label for="travelDate" class="block text-sm font-medium text-gray-700 mb-2">
                    Ngày khởi hành <span class="text-red-500">*</span>
                  </label>
                  <input
                    id="travelDate"
                    v-model="form.travelDate"
                    type="date"
                    required
                    :min="minDate"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-green-500 focus:border-green-500"
                  />
                  <p v-if="errors.travelDate" class="text-red-500 text-xs mt-1">{{ errors.travelDate }}</p>
                </div>

                <!-- Price -->
                <div>
                  <label for="price" class="block text-sm font-medium text-gray-700 mb-2">
                    Giá vé (VNĐ) <span class="text-red-500">*</span>
                  </label>
                  <div class="relative">
                    <input
                      id="price"
                      v-model.number="form.price"
                      type="number"
                      min="10000"
                      max="10000000"
                      step="1000"
                      required
                      placeholder="VD: 420000"
                      class="w-full px-3 py-2 pr-12 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-green-500 focus:border-green-500"
                    />
                    <div class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none">
                      <span class="text-gray-500 sm:text-sm">đ</span>
                    </div>
                  </div>
                  <p v-if="errors.price" class="text-red-500 text-xs mt-1">{{ errors.price }}</p>
                  <p class="text-xs text-gray-500 mt-1">Nhập giá từ 10,000đ đến 10,000,000đ</p>
                </div>

                <!-- Status (for editing) -->
                <div v-if="isEditing">
                  <label for="status" class="block text-sm font-medium text-gray-700 mb-2">
                    Trạng thái
                  </label>
                  <select
                    id="status"
                    v-model="form.status"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-green-500 focus:border-green-500"
                  >
                    <option value="active">Đang hoạt động</option>
                    <option value="inactive">Tạm dừng</option>
                    <option value="cancelled">Đã hủy</option>
                  </select>
                </div>

                <!-- Preview -->
                <div v-if="previewData" class="mt-6 p-4 bg-gray-50 rounded-md">
                  <h4 class="text-sm font-medium text-gray-900 mb-3">Xem trước chuyến xe:</h4>
                  <div class="space-y-2 text-sm text-gray-600">
                    <div class="flex items-center">
                      <svg class="mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7"/>
                      </svg>
                      <strong>Tuyến:</strong> {{ previewData.route?.origin }} → {{ previewData.route?.destination }}
                    </div>
                    <div class="flex items-center">
                      <svg class="mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17a2 2 0 11-4 0 2 2 0 014 0zM19 17a2 2 0 11-4 0 2 2 0 014 0z"/>
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 6h3l2 7H9M9 17v-2a2 2 0 00-2-2H4"/>
                      </svg>
                      <strong>Xe:</strong> {{ previewData.bus?.name }} - {{ getBusTypeName(previewData.bus?.categoryId) }}
                    </div>
                    <div class="flex items-center">
                      <svg class="mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                      </svg>
                      <strong>Ngày:</strong> {{ formatDate(form.travelDate) }}
                    </div>
                    <div class="flex items-center">
                      <svg class="mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                      </svg>
                      <strong>Giờ:</strong> {{ formatTime(previewData.bus?.departureTime) }} - {{ formatTime(previewData.bus?.arrivalTime) }}
                    </div>
                    <div class="flex items-center">
                      <svg class="mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2"/>
                      </svg>
                      <strong>Giá:</strong> {{ formatPrice(form.price) }}đ
                    </div>
                  </div>
                </div>
              </form>
            </div>
          </div>
        </div>
        
        <!-- Modal actions -->
        <div class="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
          <button
            type="button"
            @click="handleSubmit"
            :disabled="isSubmitting || !canSubmit"
            class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-green-600 text-base font-medium text-white hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 sm:ml-3 sm:w-auto sm:text-sm disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <svg v-if="isSubmitting" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ isSubmitting ? 'Đang xử lý...' : (isEditing ? 'Cập nhật chuyến' : 'Tạo chuyến xe') }}
          </button>
          <button
            type="button"
            @click="closeModal"
            :disabled="isSubmitting"
            class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm disabled:opacity-50"
          >
            Hủy
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { createBusRoute, updateBusRoute } from '@/api/busRouteApi'
import { getAllRoutes } from '@/api/routeApi'
import { getAllBuses } from '@/api/busApi'
import { formatPrice, minutesToTimeString } from '@/utils/busHelper'

// Emits
const emit = defineEmits(['bus-route-created', 'bus-route-updated'])

// State
const isOpen = ref(false)
const isEditing = ref(false)
const isSubmitting = ref(false)
const editingBusRouteId = ref(null)

// Available data
const availableRoutes = ref([])
const availableBuses = ref([])

// Form data
const form = reactive({
  routeId: '',
  busId: '',
  travelDate: '',
  price: null,
  status: 'active'
})

// Form errors
const errors = ref({})

// Computed
const minDate = computed(() => {
  return new Date().toISOString().split('T')[0]
})

const canSubmit = computed(() => {
  return form.routeId && form.busId && form.travelDate && form.price
})

const previewData = computed(() => {
  if (!canSubmit.value) return null
  
  const route = availableRoutes.value.find(r => r.id == form.routeId)
  const bus = availableBuses.value.find(b => b.id == form.busId)
  
  return { route, bus }
})

// Methods
const openModal = async (busRouteData = null) => {
  console.log('🚀 Opening BusRouteCreationModal:', busRouteData)
  
  // Load data first
  await loadData()
  
  // Reset form
  resetForm()
  
  if (busRouteData) {
    // Edit mode
    isEditing.value = true
    editingBusRouteId.value = busRouteData.id
    form.routeId = busRouteData.route.id
    form.busId = busRouteData.bus.id
    form.travelDate = busRouteData.travelDate
    form.price = busRouteData.price
    form.status = busRouteData.status
  } else {
    // Create mode
    isEditing.value = false
    editingBusRouteId.value = null
  }
  
  isOpen.value = true
}

const closeModal = () => {
  if (isSubmitting.value) return
  
  isOpen.value = false
  resetForm()
  
  // Wait for animation to complete
  setTimeout(() => {
    isEditing.value = false
    editingBusRouteId.value = null
  }, 300)
}

const resetForm = () => {
  form.routeId = ''
  form.busId = ''
  form.travelDate = ''
  form.price = null
  form.status = 'active'
  errors.value = {}
}

const loadData = async () => {
  try {
    // Load routes
    const routesResponse = await getAllRoutes()
    availableRoutes.value = [
      { id: 1, origin: 'Hà Nội', destination: 'TP. Hồ Chí Minh', distanceKm: 1700, estimatedDurationMinutes: 720 },
      { id: 2, origin: 'Hà Nội', destination: 'Đà Nẵng', distanceKm: 800, estimatedDurationMinutes: 720 },
      { id: 3, origin: 'TP. Hồ Chí Minh', destination: 'Cần Thơ', distanceKm: 170, estimatedDurationMinutes: 240 }
    ]
    
    // Load buses
    const busesResponse = await getAllBuses()
    availableBuses.value = [
      { id: 1, name: 'Xe Limousine - 29A-123.45', categoryId: 3, departureTime: '2024-01-15T08:00:00', arrivalTime: '2024-01-15T20:00:00' },
      { id: 2, name: 'Xe Giường nằm - 30B-678.90', categoryId: 2, departureTime: '2024-01-15T22:00:00', arrivalTime: '2024-01-16T10:00:00' },
      { id: 3, name: 'Xe Trung chuyển - 31C-456.78', categoryId: 1, departureTime: '2024-01-15T06:00:00', arrivalTime: '2024-01-15T18:00:00' }
    ]
    
    console.log('✅ Loaded data for modal:', { routes: availableRoutes.value, buses: availableBuses.value })
  } catch (err) {
    console.error('❌ Error loading data:', err)
  }
}

const validateForm = () => {
  const newErrors = {}
  
  if (!form.routeId) {
    newErrors.routeId = 'Vui lòng chọn tuyến đường'
  }
  
  if (!form.busId) {
    newErrors.busId = 'Vui lòng chọn xe'
  }
  
  if (!form.travelDate) {
    newErrors.travelDate = 'Vui lòng chọn ngày khởi hành'
  } else if (form.travelDate < minDate.value) {
    newErrors.travelDate = 'Ngày khởi hành không thể là ngày trong quá khứ'
  }
  
  if (!form.price || form.price < 10000) {
    newErrors.price = 'Vui lòng nhập giá vé hợp lệ (tối thiểu 10,000đ)'
  } else if (form.price > 10000000) {
    newErrors.price = 'Giá vé không thể vượt quá 10,000,000đ'
  }
  
  errors.value = newErrors
  return Object.keys(newErrors).length === 0
}

const handleSubmit = async () => {
  if (!validateForm()) {
    console.log('❌ Validation failed:', errors.value)
    return
  }
  
  isSubmitting.value = true
  
  try {
    const busRouteData = {
      busId: form.busId,
      routeId: form.routeId,
      travelDate: form.travelDate,
      price: form.price,
      status: form.status
    }
    
    console.log('📤 Submitting bus route data:', busRouteData)
    
    if (isEditing.value) {
      // Update existing bus route
      const response = await updateBusRoute(editingBusRouteId.value, busRouteData)
      console.log('✅ Bus route updated:', response)
      emit('bus-route-updated', response.data)
    } else {
      // Create new bus route
      const response = await createBusRoute(busRouteData)
      console.log('✅ Bus route created:', response)
      emit('bus-route-created', response.data)
    }
    
    closeModal()
  } catch (error) {
    console.error('❌ Error saving bus route:', error)
    alert('Có lỗi xảy ra khi lưu chuyến xe. Vui lòng thử lại.')
  } finally {
    isSubmitting.value = false
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

// Expose methods for parent component
defineExpose({
  openModal
})

// Watch for ESC key
watch(isOpen, (newValue) => {
  if (newValue) {
    const handleEsc = (e) => {
      if (e.key === 'Escape') {
        closeModal()
      }
    }
    
    document.addEventListener('keydown', handleEsc)
    
    // Cleanup
    return () => {
      document.removeEventListener('keydown', handleEsc)
    }
  }
})
</script> 