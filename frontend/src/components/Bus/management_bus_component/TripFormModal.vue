<template>
  <teleport to="body">
    <transition name="modal-overlay" appear>
      <div v-if="visible" @click="handleBackdropClick" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
        <transition name="modal-content" appear>
          <div @click.stop class="relative w-full max-w-2xl bg-white rounded-xl shadow-2xl mx-4 overflow-hidden">
            
            <!-- Modal Header -->
            <div class="bg-white px-6 py-4 border-b border-gray-200">
              <div class="flex items-center justify-between">
                <div class="flex items-center space-x-3">
                  <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
                    <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3a1 1 0 011-1h6a1 1 0 011 1v4h3a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9a2 2 0 012-2h3z"/>
                    </svg>
                  </div>
                  <div>
                    <h3 class="text-xl font-semibold text-gray-900">{{ isEditing ? 'Sửa chuyến xe' : 'Thêm chuyến xe mới' }}</h3>
                    <p class="text-gray-600 text-sm">{{ isEditing ? 'Cập nhật thông tin chuyến xe' : 'Tạo chuyến xe cho tuyến đường' }}</p>
                  </div>
                </div>
                <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 focus:outline-none transition-colors duration-200">
                  <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                  </svg>
                </button>
              </div>
            </div>

            <!-- Modal Content -->
            <div class="px-6 py-6">
              <form @submit.prevent="handleSubmit" class="space-y-6">
                
                <!-- Trip Information Section -->
                <div class="space-y-4">
                  <div class="flex items-center space-x-2 mb-4">
                    <div class="w-2 h-2 bg-blue-600 rounded-full"></div>
                    <h4 class="text-lg font-medium text-gray-900">Thông tin chuyến xe</h4>
                  </div>
                  
                  <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <!-- Bus Selection -->
                    <div class="space-y-2">
                      <label class="block text-sm font-medium text-gray-700">
                        Xe buýt <span class="text-red-500">*</span>
                      </label>
                      <div class="relative">
                        <select 
                          v-model="form.busId" 
                          required 
                          :disabled="loadingBuses"
                          :class="[
                            'w-full pl-10 pr-4 py-3 border rounded-lg shadow-sm focus:outline-none focus:ring-2 transition-all duration-200 hover:border-gray-400',
                            isDuplicateTrip && !isEditing ? 'border-orange-300 bg-orange-50' : 'border-gray-300 focus:ring-blue-500 focus:border-blue-500'
                          ]"
                        >
                          <option value="">
                            {{ loadingBuses ? 'Đang tải xe bus...' : (availableBuses.length === 0 ? 'Không có xe bus nào' : 'Chọn xe bus') }}
                          </option>
                          <option v-for="bus in availableBuses" :key="bus.id" :value="bus.id">
                            {{ bus.name }} ({{ bus.licensePlate }}) - {{ bus.totalSeats }} ghế
                          </option>
                        </select>
                        <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 14v3m4-3v3m4-3v3M3 21h18M3 10h18M3 7l9-4 9 4M4 10.5V21"/>
                          </svg>
                        </div>
                        <div class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none">
                          <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                          </svg>
                        </div>
                      </div>
                    </div>
                    
                    <!-- Route Selection -->
                    <div class="space-y-2">
                      <label class="block text-sm font-medium text-gray-700">
                        Tuyến đường <span class="text-red-500">*</span>
                      </label>
                      <div class="relative">
                        <select 
                          v-model="form.routeId" 
                          required 
                          :disabled="loadingRoutes"
                          :class="[
                            'appearance-none w-full pl-10 pr-10 py-3 border rounded-lg shadow-sm focus:outline-none focus:ring-2 transition-all duration-200 hover:border-gray-400 bg-white disabled:bg-gray-100 disabled:cursor-not-allowed',
                            isDuplicateTrip && !isEditing ? 'border-orange-300 bg-orange-50' : 'border-gray-300 focus:ring-blue-500 focus:border-blue-500'
                          ]"
                        >
                          <option value="">
                            {{ loadingRoutes ? 'Đang tải tuyến đường...' : (availableRoutes.length === 0 ? 'Không có tuyến đường nào' : 'Chọn tuyến đường') }}
                          </option>
                          <option v-for="route in availableRoutes" :key="route.id" :value="route.id">
                            {{ route.origin }} → {{ route.destination }} ({{ route.distanceKm }}km - {{ Math.round(route.estimatedDurationMinutes / 60) }}h)
                          </option>
                        </select>
                        <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
                          </svg>
                        </div>
                        <div class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none">
                          <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                          </svg>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <!-- Date -->
                    <div class="space-y-2">
                      <label class="block text-sm font-medium text-gray-700">
                        Ngày khởi hành <span class="text-red-500">*</span>
                      </label>
                      <div class="relative">
                        <input 
                          v-model="form.slotDate" 
                          type="date" 
                          required 
                          :min="minDate"
                          :class="[
                            'w-full pl-10 pr-4 py-3 border rounded-lg shadow-sm focus:outline-none focus:ring-2 transition-all duration-200 hover:border-gray-400',
                            isDuplicateTrip && !isEditing ? 'border-orange-300 bg-orange-50' : 'border-gray-300 focus:ring-blue-500 focus:border-blue-500'
                          ]"
                        >
                        <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3a1 1 0 011-1h6a1 1 0 011 1v4h3a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9a2 2 0 012-2h3z"/>
                          </svg>
                        </div>
                      </div>
                    </div>
                    
                    <!-- Departure Time -->
                    <div class="space-y-2">
                      <label class="block text-sm font-medium text-gray-700">
                        Giờ khởi hành <span class="text-red-500">*</span>
                      </label>
                      <div class="relative">
                        <input 
                          v-model="form.departureTime" 
                          type="time" 
                          required 
                          class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 hover:border-gray-400"
                        >
                        <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                          </svg>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <!-- Price -->
                    <div class="space-y-2">
                      <label class="block text-sm font-medium text-gray-700">
                        Giá vé <span class="text-red-500">*</span>
                      </label>
                      <div class="relative">
                        <input 
                          v-model.number="form.price" 
                          type="number" 
                          required 
                          min="10000"
                          step="10000"
                          placeholder="500000"
                          :class="[
                            'w-full pl-10 pr-4 py-3 border rounded-lg shadow-sm focus:outline-none focus:ring-2 transition-all duration-200 hover:border-gray-400',
                            priceAutoFilled ? 'border-green-300 bg-green-50' : 'border-gray-300'
                          ]"
                          @input="priceAutoFilled = false; priceAutoFillMessage = ''"
                        >
                        <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <svg :class="priceAutoFilled ? 'text-green-500' : 'text-gray-400'" class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1"/>
                          </svg>
                        </div>
                        <!-- Auto-fill indicator -->
                        <div v-if="priceAutoFilled" class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none">
                          <svg class="h-5 w-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                          </svg>
                        </div>
                      </div>
                      <div class="text-xs space-y-1">
                        <p class="text-gray-500">Đơn vị: VNĐ</p>
                        <p v-if="priceAutoFilled" class="text-green-600 flex items-center">
                          <svg class="h-3 w-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
                          </svg>
                          {{ priceAutoFillMessage }}
                        </p>
                      </div>
                    </div>
                    
                    <!-- Total Seats -->
                    <div class="space-y-2">
                      <label class="block text-sm font-medium text-gray-700">
                        Số ghế <span class="text-red-500">*</span>
                      </label>
                      <div class="relative">
                        <input 
                          v-model.number="form.totalSeats" 
                          type="number" 
                          required 
                          min="1"
                          max="60"
                          placeholder="40"
                          class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 hover:border-gray-400"
                        >
                        <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"/>
                          </svg>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </form>
            </div>

            <!-- Duplicate Warning -->
            <div v-if="isDuplicateTrip" class="px-6 py-4 bg-orange-50 border-t border-l-4 border-orange-400">
              <div class="flex items-start space-x-3">
                <div class="flex-shrink-0 mt-0.5">
                  <svg class="w-5 h-5 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.732-.833-2.464 0L4.35 16.5c-.77.833.192 2.5 1.732 2.5z"/>
                  </svg>
                </div>
                <div class="flex-1">
                  <h4 class="text-sm font-semibold text-orange-800 mb-1">Chuyến xe đã tồn tại</h4>
                  <p class="text-sm text-orange-700 mb-2">{{ duplicateWarningMessage }}</p>
                  <div class="text-xs text-orange-600 space-y-1">
                    <p class="flex items-center">
                      <svg class="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5-5 5M6 12h12"/>
                      </svg>
                      Chọn xe khác hoặc ngày khác để tạo chuyến mới
                    </p>
                    <p class="flex items-center">
                      <svg class="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5-5 5M6 12h12"/>
                      </svg>
                      Hoặc chỉnh sửa chuyến xe hiện có thay vì tạo mới
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Modal Footer -->
            <div class="bg-gray-50 px-6 py-4 border-t border-gray-200 flex items-center justify-between">
              <div class="text-sm text-gray-500">
                <span class="flex items-center">
                  <svg class="w-4 h-4 mr-1 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h5v-4l-2-2h-3v6z"/>
                  </svg>
                  Quản lý chuyến xe
                </span>
              </div>
              <div class="flex space-x-3">
                <button 
                  @click="$emit('close')" 
                  type="button" 
                  class="px-6 py-2.5 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2 transition-all duration-200 shadow-sm"
                >
                  Hủy bỏ
                </button>
                <button 
                  @click="handleSubmit" 
                  type="submit" 
                  :disabled="loading || loadingBuses || loadingRoutes || (isDuplicateTrip && !isEditing)"
                  class="px-6 py-2.5 text-sm font-medium text-white bg-gradient-to-r from-blue-600 to-blue-700 border border-transparent rounded-lg hover:from-blue-700 hover:to-blue-800 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-all duration-200 shadow-md hover:shadow-lg transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
                >
                  <div v-if="loading" class="flex items-center">
                    <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    {{ isEditing ? 'Đang cập nhật...' : 'Đang tạo...' }}
                  </div>
                  <span v-else-if="isDuplicateTrip && !isEditing">
                    ⚠️ Trùng lặp chuyến xe
                  </span>
                  <span v-else>
                    {{ isEditing ? 'Cập nhật chuyến' : 'Tạo chuyến xe' }}
                  </span>
                </button>
              </div>
            </div>
          </div>
        </transition>
      </div>
    </transition>
  </teleport>
</template>

<script setup>
import { ref, computed, watch, defineProps, defineEmits } from 'vue'
// @ts-ignore
import { toast } from '@/utils/notifications'
import { useTripManagement } from '@/composables/useTripManagement'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  editingTrip: {
    type: Object,
    default: null
  },
  availableBuses: {
    type: Array,
    default: () => []
  },
  availableRoutes: {
    type: Array,
    default: () => []
  },
  loadingBuses: {
    type: Boolean,
    default: false
  },
  loadingRoutes: {
    type: Boolean,
    default: false
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'save'])

// Initialize trip management for validation
const tripManager = useTripManagement()

// Form state
const form = ref({
  busId: '',
  routeId: '',
  slotDate: '',
  departureTime: '',
  arrivalTime: '',
  price: 500000,
  totalSeats: 40
})

// Auto-fill state
const priceAutoFilled = ref(false)
const priceAutoFillMessage = ref('')

// Computed properties
const isEditing = computed(() => !!props.editingTrip)
const minDate = computed(() => new Date().toISOString().split('T')[0])

// Duplicate validation
const isDuplicateTrip = computed(() => {
  if (!form.value.busId || !form.value.routeId || !form.value.slotDate) {
    return false
  }
  
  const isDuplicate = tripManager.hasDuplicateTrip(form.value.busId, form.value.routeId, form.value.slotDate)
  
  return isDuplicate
})

const duplicateWarningMessage = computed(() => {
  if (!isDuplicateTrip.value) return ''
  
  const selectedBus = props.availableBuses.find(bus => bus.id === form.value.busId)
  const selectedRoute = props.availableRoutes.find(route => route.id === form.value.routeId)
  
  const busName = selectedBus?.name || 'xe này'
  const routeName = selectedRoute ? `${selectedRoute.origin} - ${selectedRoute.destination}` : 'tuyến đường này'
  const formattedDate = new Date(form.value.slotDate).toLocaleDateString('vi-VN')
  
  return `${busName} đã có chuyến đi trên tuyến ${routeName} vào ngày ${formattedDate}`
})

// Initialize form when editing
watch(() => props.editingTrip, (newTrip) => {
  if (newTrip) {
    form.value = {
      busId: newTrip.bus?.id || '',
      routeId: newTrip.route?.id || '',
      slotDate: newTrip.slotDate || '',
      departureTime: newTrip.departureTime ? formatTime(newTrip.departureTime) : '',
      arrivalTime: newTrip.arrivalTime ? formatTime(newTrip.arrivalTime) : '',
      price: newTrip.price || 500000,
      totalSeats: newTrip.totalSeats || 40
    }
  } else {
    resetForm()
  }
}, { immediate: true })

// Auto-fill totalSeats when bus is selected
watch(() => form.value.busId, (newBusId) => {
  if (newBusId) {
    const selectedBus = props.availableBuses.find(bus => bus.id === newBusId)
    if (selectedBus && selectedBus.totalSeats) {
      form.value.totalSeats = selectedBus.totalSeats
    }
  }
})

// Calculate arrival time when route is selected
watch(() => form.value.routeId, () => {
  if (form.value.routeId && form.value.departureTime) {
    calculateArrivalTime()
  }
  
  // Auto-fill price when route is selected
  checkAndFillPriceForRoute()
})

// Also calculate when departure time changes
watch(() => form.value.departureTime, () => {
  if (form.value.routeId && form.value.departureTime) {
    calculateArrivalTime()
  }
})

// Auto-fill price when date changes (if route already selected)
watch(() => form.value.slotDate, () => {
  if (form.value.routeId) {
    checkAndFillPriceForRoute()
  }
})

function calculateArrivalTime() {
  const selectedRoute = props.availableRoutes.find(route => route.id === form.value.routeId)
  if (selectedRoute && selectedRoute.estimatedDurationMinutes && form.value.departureTime) {
    try {
      const [hours, minutes] = form.value.departureTime.split(':').map(Number)
      if (isNaN(hours) || isNaN(minutes)) return
      
      const departureMinutes = hours * 60 + minutes
      const arrivalMinutes = departureMinutes + selectedRoute.estimatedDurationMinutes
      
      const arrivalHours = Math.floor(arrivalMinutes / 60) % 24
      const arrivalMins = arrivalMinutes % 60
      
      form.value.arrivalTime = `${arrivalHours.toString().padStart(2, '0')}:${arrivalMins.toString().padStart(2, '0')}`
    } catch (error) {
      console.error('Error calculating arrival time:', error)
      // Set default arrival time 2 hours after departure if calculation fails
      try {
        const [hours, minutes] = form.value.departureTime.split(':').map(Number)
        const arrivalHours = (hours + 2) % 24
        form.value.arrivalTime = `${arrivalHours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`
      } catch {
        form.value.arrivalTime = '10:00' // fallback
      }
    }
  }
}

// New function to check and fill price from price management
const checkAndFillPriceForRoute = async () => {
  if (!form.value.routeId) {
    return // Need routeId
  }

  try {
    // Get the selected route
    const selectedRoute = props.availableRoutes.find(route => route.id === form.value.routeId)
    
    if (!selectedRoute) {
      return // Need routeId
    }

    let defaultPrice = 500000 // Base fallback
    let priceSource = 'mặc định hệ thống'

    // Option 1: If route has a default price field
    if (selectedRoute.defaultPrice && selectedRoute.defaultPrice > 0) {
      defaultPrice = selectedRoute.defaultPrice
      priceSource = 'giá mặc định của tuyến đường'
    }
    // Option 2: Calculate based on distance
    else if (selectedRoute.distanceKm) {
      // Simple calculation: 300 VND per km (can be adjusted)
      defaultPrice = Math.round(selectedRoute.distanceKm * 300)
      priceSource = `tính theo khoảng cách (${selectedRoute.distanceKm}km)`
    }
    // Option 3: Route-specific pricing based on popular routes
    else {
      const routeKey = `${selectedRoute.origin}-${selectedRoute.destination}`.toLowerCase()
      const routePricing = {
        'hà nội-tp.hcm': 800000,
        'hà nội-hồ chí minh': 800000,
        'hanoi-hochiminh': 800000,
        'hà nội-đà nẵng': 600000,
        'hanoi-danang': 600000,
        'tp.hcm-đà nẵng': 500000,
        'hochiminh-danang': 500000,
        'hà nội-hải phòng': 200000,
        'hanoi-haiphong': 200000,
      }
      
      // Try exact match or partial matches
      const matchedPrice = routePricing[routeKey] || 
        Object.entries(routePricing).find(([key, _]) => 
          routeKey.includes(key.split('-')[0]) && routeKey.includes(key.split('-')[1])
        )?.[1]

      if (matchedPrice) {
        defaultPrice = matchedPrice
        priceSource = 'giá tham khảo cho tuyến đường phổ biến'
      }
    }

    // Set the calculated price
    form.value.price = defaultPrice
    priceAutoFilled.value = true
    priceAutoFillMessage.value = `Giá ${priceSource}: ${defaultPrice.toLocaleString('vi-VN')} VND - có thể điều chỉnh`
    
  } catch (error) {
    console.error('❌ Error fetching route default price:', error)
    // Don't change price on error, let user set manually
    priceAutoFilled.value = false
    priceAutoFillMessage.value = ''
  }
}

// Methods
function resetForm() {
  const tomorrow = new Date()
  tomorrow.setDate(tomorrow.getDate() + 1)
  const defaultDate = tomorrow.toISOString().split('T')[0]
  
  form.value = {
    busId: '',
    routeId: '',
    slotDate: defaultDate,
    departureTime: '08:00',  // Default departure time
    arrivalTime: '10:00',    // Default arrival time (2 hours later)
    price: 500000,
    totalSeats: 40
  }
  priceAutoFilled.value = false
  priceAutoFillMessage.value = ''
}

function formatTime(datetime) {
  if (!datetime) return ''
  try {
    if (typeof datetime === 'string' && datetime.match(/^\d{2}:\d{2}:\d{2}$/)) {
      return datetime.substring(0, 5) // HH:MM:SS -> HH:MM
    }
    return new Date(datetime).toLocaleTimeString('vi-VN', { 
      hour: '2-digit', 
      minute: '2-digit' 
    })
  } catch {
    return datetime || ''
  }
}

async function handleSubmit() {
  // Basic validation - backend will handle business logic validation
  if (!form.value.busId) {
    toast.warning('Vui lòng chọn xe bus', 'Thiếu thông tin')
    return
  }
  
  if (!form.value.routeId) {
    toast.warning('Vui lòng chọn tuyến đường', 'Thiếu thông tin')
    return
  }
  
  if (!form.value.slotDate || !form.value.departureTime) {
    toast.warning('Vui lòng nhập đầy đủ thời gian', 'Thiếu thông tin')
    return
  }

  // Check duplicate trip (only for new trips, not editing)
  if (!isEditing.value && isDuplicateTrip.value) {
    // Don't proceed - let the UI warning handle user feedback
    return
  }
  
  // Calculate arrivalTime if not provided
  if (!form.value.arrivalTime) {
    const selectedRoute = props.availableRoutes.find(route => route.id === form.value.routeId)
    if (selectedRoute && selectedRoute.estimatedDurationMinutes && form.value.departureTime) {
      const [hours, minutes] = form.value.departureTime.split(':').map(Number)
      const departureMinutes = hours * 60 + minutes
      const arrivalMinutes = departureMinutes + selectedRoute.estimatedDurationMinutes
      
      const arrivalHours = Math.floor(arrivalMinutes / 60) % 24
      const arrivalMins = arrivalMinutes % 60
      
      form.value.arrivalTime = `${arrivalHours.toString().padStart(2, '0')}:${arrivalMins.toString().padStart(2, '0')}`
    }
  }
  
  // Ensure time format is proper (HH:MM:SS for backend)
  const tripData = {
    ...form.value,
    departureTime: formatTimeForBackend(form.value.departureTime),
    arrivalTime: formatTimeForBackend(form.value.arrivalTime || form.value.departureTime) // fallback to departureTime if still empty
  }
  
  
  // Emit save event - let parent handle business logic
  emit('save', tripData)
}

function formatTimeForBackend(timeString) {
  if (!timeString) {
    // Return current time as fallback
    const now = new Date()
    return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}:00`
  }
  
  // If already in HH:MM:SS format, return as is
  if (timeString.match(/^\d{2}:\d{2}:\d{2}$/)) {
    return timeString
  }
  
  // If in HH:MM format, add :00 seconds
  if (timeString.match(/^\d{2}:\d{2}$/)) {
    return `${timeString}:00`
  }
  
  // Invalid format, return default
  return '08:00:00'
}

function handleBackdropClick() {
  emit('close')
}
</script>

<style scoped>
.modal-overlay-enter-active,
.modal-overlay-leave-active {
  transition: opacity 0.3s ease;
}

.modal-overlay-enter-from,
.modal-overlay-leave-to {
  opacity: 0;
}

.modal-content-enter-active,
.modal-content-leave-active {
  transition: all 0.3s ease;
}

.modal-content-enter-from,
.modal-content-leave-to {
  opacity: 0;
  transform: scale(0.95) translateY(-10px);
}
</style> 