<template>
  <transition name="modal" appear>
    <div v-if="visible" @click="handleBackdropClick" class="fixed inset-0 h-full w-full z-50 flex items-center justify-center bg-white-100 backdrop-blur-sm p-4">
      <div @click.stop class="relative w-full max-w-2xl bg-white rounded-xl shadow-2xl overflow-hidden transform">
        
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
                    Xe bus <span class="text-red-500">*</span>
                  </label>
                  <div class="relative">
                    <select 
                      v-model="form.busId" 
                      required 
                      :disabled="loadingBuses"
                      class="appearance-none w-full pl-10 pr-10 py-3 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 hover:border-gray-400 bg-white disabled:bg-gray-100 disabled:cursor-not-allowed"
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
                      class="appearance-none w-full pl-10 pr-10 py-3 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 hover:border-gray-400 bg-white disabled:bg-gray-100 disabled:cursor-not-allowed"
                    >
                      <option value="">
                        {{ loadingRoutes ? 'Đang tải tuyến đường...' : (availableRoutes.length === 0 ? 'Không có tuyến đường nào' : 'Chọn tuyến đường') }}
                      </option>
                      <option v-for="route in availableRoutes" :key="route.id" :value="route.id">
                        {{ route.name }} ({{ route.distanceKm }}km - {{ Math.round(route.estimatedDurationMinutes / 60) }}h)
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
                      class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 hover:border-gray-400"
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
                      class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 hover:border-gray-400"
                    >
                    <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                      <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1"/>
                      </svg>
                    </div>
                  </div>
                  <p class="text-xs text-gray-500">Đơn vị: VNĐ</p>
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
              :disabled="loading || loadingBuses || loadingRoutes"
              class="px-6 py-2.5 text-sm font-medium text-white bg-gradient-to-r from-blue-600 to-blue-700 border border-transparent rounded-lg hover:from-blue-700 hover:to-blue-800 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-all duration-200 shadow-md hover:shadow-lg transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
            >
              <div v-if="loading" class="flex items-center">
                <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                {{ isEditing ? 'Đang cập nhật...' : 'Đang tạo...' }}
              </div>
              <span v-else>
                {{ isEditing ? 'Cập nhật chuyến' : 'Tạo chuyến xe' }}
              </span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, computed, watch, defineProps, defineEmits } from 'vue'

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

// Computed properties
const isEditing = computed(() => !!props.editingTrip)
const minDate = computed(() => new Date().toISOString().split('T')[0])

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
      console.log('Auto-filled totalSeats:', selectedBus.totalSeats)
    }
  }
})

// Calculate arrival time when route is selected
watch(() => form.value.routeId, () => {
  if (form.value.routeId && form.value.departureTime) {
    calculateArrivalTime()
  }
})

// Also calculate when departure time changes
watch(() => form.value.departureTime, () => {
  if (form.value.routeId && form.value.departureTime) {
    calculateArrivalTime()
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
      console.log('Auto-calculated arrival time:', form.value.arrivalTime)
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
    alert('Vui lòng chọn xe bus')
    return
  }
  
  if (!form.value.routeId) {
    alert('Vui lòng chọn tuyến đường')
    return
  }
  
  if (!form.value.slotDate || !form.value.departureTime) {
    alert('Vui lòng nhập đầy đủ thời gian')
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
  
  console.log('📤 Sending trip data:', tripData)
  
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
/* Modal Transition Styles */
.modal-enter-active {
  transition: all 0.3s ease-out;
}

.modal-leave-active {
  transition: all 0.2s ease-in;
}

.modal-enter-from {
  opacity: 0;
}

.modal-enter-to {
  opacity: 1;
}

.modal-leave-from {
  opacity: 1;
}

.modal-leave-to {
  opacity: 0;
}

/* Modal Content Animation */
.modal-enter-active .transform {
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.modal-leave-active .transform {
  transition: all 0.2s ease-in-out;
}

.modal-enter-from .transform {
  opacity: 0;
  transform: scale(0.95) translateY(-20px);
}

.modal-enter-to .transform {
  opacity: 1;
  transform: scale(1) translateY(0);
}

.modal-leave-from .transform {
  opacity: 1;
  transform: scale(1) translateY(0);
}

.modal-leave-to .transform {
  opacity: 0;
  transform: scale(0.95) translateY(-10px);
}
</style> 