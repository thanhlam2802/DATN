<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { timeSlots, availableFacilities } from '@/data/locationData.js'
import BusCard from './BusCard.vue'
import BusTicketBooking from './BusTicketBooking.vue'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  searchParams: {
    type: Object,
    default: () => ({})
  },
  searchResults: {
    type: Object,
    default: () => ({ busSlots: [], searchParams: {} })
  }
})

const emit = defineEmits(['close'])

// Modal state management
const currentStep = ref(1)
const selectedTrip = ref(null)
const showBookingFlow = ref(false)

// Loading state
const loadingResults = ref(false)
const searchError = ref('')

// Component lifecycle tracking
const isMounted = ref(false)

// Filter states
const filters = ref({
  timeSlots: [],
  arrivalTimeSlots: [],
  facilities: [],
  availableSeats: ''
})

// Real search results from props
const rawSearchResults = ref([])
const searchResults = ref([])

// Transform BusSlotResponse to match UI expectations
const transformBusSlotToTrip = (busSlot) => {
  // Parse time từ OffsetDateTime về HH:mm format
  const formatTime = (offsetDateTime) => {
    if (!offsetDateTime) return '00:00'
    try {
      const date = new Date(offsetDateTime)
      return date.toLocaleTimeString('vi-VN', { 
        hour: '2-digit', 
        minute: '2-digit',
        hour12: false 
      })
    } catch {
      return '00:00'
    }
  }

  // Calculate duration từ departure và arrival
  const calculateDuration = (departureTime, arrivalTime) => {
    if (!departureTime || !arrivalTime) return '8h 00m'
    try {
      const departure = new Date(departureTime)
      const arrival = new Date(arrivalTime)
      const diffMs = arrival.getTime() - departure.getTime()
      const diffMins = Math.floor(diffMs / (1000 * 60))
      const hours = Math.floor(diffMins / 60)
      const mins = diffMins % 60
      return `${hours}h ${mins.toString().padStart(2, '0')}m`
    } catch {
      return '8h 00m'
    }
  }

  // Get image URL directly from busImages
  const getImageUrl = (bus) => {
    if (bus?.busImages && Array.isArray(bus.busImages) && bus.busImages.length > 0) {
      // Lấy hình đầu tiên từ busImages (nested structure)
      return bus.busImages[0].image.url
    }
    
    // Fallback placeholder khi không có hình
    return `data:image/svg+xml,${encodeURIComponent(`
      <svg width="400" height="200" xmlns="http://www.w3.org/2000/svg">
        <rect width="100%" height="100%" fill="#f3f4f6"/>
        <text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" 
              font-family="Arial, sans-serif" font-size="14" fill="#6b7280">
          ${bus?.name || 'Xe khách'}
        </text>
      </svg>
    `)}`
  }
  
  const imageUrl = getImageUrl(busSlot.bus)
  
  // Get all images for gallery (nếu cần hiển thị nhiều hình)
  const getAllImages = (bus) => {
    if (bus?.busImages && Array.isArray(bus.busImages)) {
      return bus.busImages.map(busImg => busImg.image.url)
    }
    return [getImageUrl(bus)]
  }

  // Mock facilities based on bus category
  const facilities = generateFacilitiesFromCategory(busSlot.bus?.categoryName)

  return {
    id: busSlot.id,
    busSlotId: busSlot.id, // Keep reference for booking
    company: busSlot.bus?.name || 'Nhà xe không xác định',
    busOperator: busSlot.bus?.name || 'Nhà xe không xác định',
    busType: busSlot.bus?.categoryName || 'Xe khách',
    route: {
      from: busSlot.route?.originLocation?.name || 'N/A',
      to: busSlot.route?.destinationLocation?.name || 'N/A',
      distance: 'N/A' // TODO: Calculate from locations if needed
    },
    schedule: {
      departure: formatTime(busSlot.departureTime),
      arrival: formatTime(busSlot.arrivalTime),
      duration: calculateDuration(busSlot.departureTime, busSlot.arrivalTime),
      nextDay: false // TODO: Logic to determine if arrival is next day
    },
    price: busSlot.price || 0,
    totalSeats: busSlot.totalSeats || 0,
    availableSeats: busSlot.availableSeats || 0,
    facilities: facilities,
    image: imageUrl,
    images: getAllImages(busSlot.bus), // Tất cả hình ảnh của xe
    status: busSlot.status,
    currency: 'đ',
    // Additional fields for detail tabs
    features: {
      seats: busSlot.totalSeats || 0,
      seatLayout: determineSeatLayout(busSlot.totalSeats),
      facilities: facilities.map(f => ({ name: f, icon: getFacilityIcon(f) }))
    },
    policies: {
      reschedule: {
        available: true,
        description: 'Có thể đổi lịch trước 2 giờ khởi hành'
      },
      refund: {
        available: true,
        description: 'Hoàn tiền 80% trước 4 giờ khởi hành'
      }
    },
    // For booking flow
    type: determineBusType(busSlot.totalSeats),
    activeTab: determineBusType(busSlot.totalSeats),
    // ✅ Include seat data for booking
    seats: busSlot.seats || []
  }
}

// Generate facilities based on bus category
const generateFacilitiesFromCategory = (categoryName) => {
  const categoryLower = categoryName?.toLowerCase() || ''
  const facilities = []
  
  if (categoryLower.includes('vip') || categoryLower.includes('luxury')) {
    facilities.push('wifi', 'ac', 'water', 'blanket', 'entertainment')
  } else if (categoryLower.includes('giường nằm') || categoryLower.includes('sleeping')) {
    facilities.push('ac', 'water', 'blanket')
  } else if (categoryLower.includes('ghế ngồi') || categoryLower.includes('seat')) {
    facilities.push('ac', 'water')
  } else {
    // Default facilities
    facilities.push('ac')
  }
  
  return facilities
}

// Helper functions
const determineSeatLayout = (totalSeats) => {
  if (totalSeats <= 20) return '2-2'
  if (totalSeats <= 30) return '2-1'
  return '1-1-1'
}

const determineBusType = (totalSeats) => {
  if (totalSeats <= 20) return 'shuttle-bus'
  return 'sleeping-bus'
}

const getFacilityIcon = (facility) => {
  const icons = {
    wifi: 'fas fa-wifi',
    ac: 'fas fa-snowflake',
    water: 'fas fa-tint',
    blanket: 'fas fa-bed',
    entertainment: 'fas fa-tv'
  }
  return icons[facility] || 'fas fa-check'
}

// Mount/unmount tracking
onMounted(() => {
  isMounted.value = true
})

onUnmounted(() => {
  isMounted.value = false
})

// Watch for searchResults prop changes
watch(() => props.searchResults, (newResults) => {
  if (newResults?.busSlots && isMounted.value) {
    rawSearchResults.value = newResults.busSlots
    searchResults.value = newResults.busSlots.map(transformBusSlotToTrip)
    loadingResults.value = false
    searchError.value = ''
  }
}, { immediate: true, deep: true })

// Watch for modal show/hide
watch(() => props.show, (isVisible) => {
  if (isVisible && props.searchResults?.busSlots?.length > 0 && isMounted.value) {
    // Data already available from props
    rawSearchResults.value = props.searchResults.busSlots
    searchResults.value = props.searchResults.busSlots.map(transformBusSlotToTrip)
  }
})

// Filtered results based on filters (existing logic)
const filteredResults = computed(() => {
  let results = [...searchResults.value]
  
  // Filter by departure time slots
  if (filters.value.timeSlots.length > 0) {
    results = results.filter(trip => {
      const departureTime = trip.schedule.departure
      return filters.value.timeSlots.some(slot => {
        const slotData = timeSlots.find(ts => ts.id === slot)
        if (!slotData) return false
        
        const tripHour = parseInt(departureTime.split(':')[0])
        const startHour = parseInt(slotData.start.split(':')[0])
        const endHour = parseInt(slotData.end.split(':')[0])
        
        if (endHour === 0) return tripHour >= startHour || tripHour <= 6
        return tripHour >= startHour && tripHour < endHour
      })
    })
  }
  
  // Filter by arrival time slots
  if (filters.value.arrivalTimeSlots.length > 0) {
    results = results.filter(trip => {
      const arrivalTime = trip.schedule.arrival
      return filters.value.arrivalTimeSlots.some(slot => {
        const slotData = timeSlots.find(ts => ts.id === slot)
        if (!slotData) return false
        
        const tripHour = parseInt(arrivalTime.split(':')[0])
        const startHour = parseInt(slotData.start.split(':')[0])
        const endHour = parseInt(slotData.end.split(':')[0])
        
        if (endHour === 0) return tripHour >= startHour || tripHour <= 6
        return tripHour >= startHour && tripHour < endHour
      })
    })
  }
  
  // Filter by facilities
  if (filters.value.facilities.length > 0) {
    results = results.filter(trip => {
      return filters.value.facilities.every(facility => 
        trip.facilities.includes(facility)
      )
    })
  }
  
  // Filter by available seats
  if (filters.value.availableSeats) {
    const minSeats = parseInt(filters.value.availableSeats)
    results = results.filter(trip => trip.availableSeats >= minSeats)
  }
  
  return results
})

// Handle filter changes (existing logic)
const toggleTimeSlot = (slotId) => {
  const index = filters.value.timeSlots.indexOf(slotId)
  if (index > -1) {
    filters.value.timeSlots.splice(index, 1)
  } else {
    filters.value.timeSlots.push(slotId)
  }
}

const toggleFacility = (facilityId) => {
  const index = filters.value.facilities.indexOf(facilityId)
  if (index > -1) {
    filters.value.facilities.splice(index, 1)
  } else {
    filters.value.facilities.push(facilityId)
  }
}

const toggleArrivalTimeSlot = (slotId) => {
  const index = filters.value.arrivalTimeSlots?.indexOf(slotId) ?? -1
  if (!filters.value.arrivalTimeSlots) {
    filters.value.arrivalTimeSlots = []
  }
  if (index > -1) {
    filters.value.arrivalTimeSlots.splice(index, 1)
  } else {
    filters.value.arrivalTimeSlots.push(slotId)
  }
}

// Clear all filters
const clearFilters = () => {
  filters.value = {
    timeSlots: [],
    arrivalTimeSlots: [],
    facilities: [],
    availableSeats: ''
  }
}

// Booking flow functions (existing logic)
const startBookingFlow = (trip) => {
  selectedTrip.value = {
    ...trip,
    type: props.searchParams.busType || trip.type,
    activeTab: props.searchParams.busType || trip.activeTab
  }
  currentStep.value = 2
  showBookingFlow.value = true
}

const handleStepChange = (step) => {
  currentStep.value = step
}

const handleBookingComplete = (bookingData) => {
  currentStep.value = 1
  showBookingFlow.value = false
  selectedTrip.value = null
  emit('close')
}

const backToSearch = () => {
  currentStep.value = 1
  showBookingFlow.value = false
  selectedTrip.value = null
}

// Handle modal close
const closeModal = () => {
  emit('close')
}

// Handle outside click
const handleBackdropClick = (event) => {
  if (event.target === event.currentTarget) {
    closeModal()
  }
}

// Step styling functions (existing logic)
const getStepClass = (step) => {
  if (step.id < currentStep.value) {
    return 'bg-green-500 text-white'
  } else if (step.id === currentStep.value) {
    return 'bg-indigo-600 text-white'
  } else {
    return 'bg-gray-200 text-gray-500'
  }
}

const getStepConnectorClass = (stepIndex) => {
  if (stepIndex < currentStep.value - 1) {
    return 'bg-green-500'
  } else {
    return 'bg-gray-200'
  }
}

// Steps configuration (existing)
const steps = [
  { id: 1, name: 'Tìm kiếm', icon: 'fas fa-search' },
  { id: 2, name: 'Chọn ghế', icon: 'fas fa-chair' },
  { id: 3, name: 'Thông tin', icon: 'fas fa-user' },

]
</script>

<template>
  <!-- Modal Backdrop -->
  <Transition
    enter-active-class="transition-opacity duration-300"
    enter-from-class="opacity-0"
    enter-to-class="opacity-100"
    leave-active-class="transition-opacity duration-200"
    leave-from-class="opacity-100"
    leave-to-class="opacity-0"
  >
    <div v-if="show" 
         class="fixed inset-0 z-50 bg-black-100 bg-opacity-50 backdrop-blur-sm"
         @click="handleBackdropClick">
      
      <!-- Modal Content -->
      <Transition
        enter-active-class="transition-all duration-300 ease-out"
        enter-from-class="opacity-0 transform scale-95"
        enter-to-class="opacity-100 transform scale-100"
        leave-active-class="transition-all duration-200 ease-in"
        leave-from-class="opacity-100 transform scale-100"
        leave-to-class="opacity-0 transform scale-95"
      >
        <div v-if="show" class="flex items-center justify-center min-h-screen p-2 md:p-4 mt-14">
          <div class="bg-white rounded-lg shadow-xl w-full max-w-7xl h-[95vh] md:h-[90vh] overflow-hidden flex flex-col"
               @click.stop>
            
            <!-- Modal Header - Fixed Height -->
            <div class="bg-gray-50 px-6 py-4 border-b border-gray-200 flex-shrink-0">
              <div class="flex items-center justify-between mb-4">
                <div>
                  <h2 class="text-xl font-bold text-gray-900">
                    {{ currentStep === 1 ? 'Kết quả tìm kiếm' : 'Đặt vé xe khách' }}
                  </h2>
                  <p class="text-sm text-gray-600 mt-1">
                    {{ props.searchResults?.searchParams?.departureProvince || 'N/A' }} → {{ props.searchResults?.searchParams?.arrivalProvince || 'N/A' }} • {{ props.searchResults?.searchParams?.departureDate || 'N/A' }}
                  </p>
                </div>
                <button 
                  @click="closeModal"
                  class="text-gray-400 hover:text-gray-600 transition-colors">
                  <i class="fas fa-times text-xl"></i>
                </button>
              </div>

              <!-- Progress Steps -->
              <div class="flex items-center justify-center space-x-2 md:space-x-4">
                <template v-for="(step, index) in steps" :key="step.id">
                  <!-- Step Circle -->
                  <div class="flex items-center">
                    <div 
                      :class="getStepClass(step)"
                      class="w-8 h-8 md:w-10 md:h-10 rounded-full flex items-center justify-center text-xs md:text-sm font-medium transition-all duration-300"
                    >
                      <i v-if="step.id < currentStep" class="fas fa-check"></i>
                      <i v-else :class="step.icon"></i>
                    </div>
                    <span class="ml-1 md:ml-2 text-xs md:text-sm font-medium text-gray-700 hidden sm:block">
                      {{ step.name }}
                    </span>
                  </div>
                  
                  <!-- Connector Line -->
                  <div 
                    v-if="index < steps.length - 1"
                    :class="getStepConnectorClass(index)"
                    class="h-0.5 w-6 md:w-12 transition-all duration-300"
                  ></div>
                </template>
              </div>
            </div>

            <!-- Modal Body - Flexible Height -->
            <div class="flex flex-1 overflow-hidden">
              
              <!-- Step 1: Search Results -->
              <template v-if="currentStep === 1">
                <!-- Sidebar Filters -->
                <div class="w-1/3 lg:w-1/4 bg-gray-50 border-r border-gray-200 overflow-y-auto">
                  <div class="p-6 space-y-6">
                    <!-- Time Slots -->
                    <div class="text-center">
                      <h3 class="font-semibold text-gray-900 mb-3">Khung giờ đón</h3>
                      <div class="space-y-2">
                        <label v-for="slot in timeSlots" 
                               :key="slot.id"
                               class="flex items-center justify-start cursor-pointer text-left">
                          <input 
                            type="checkbox"
                            :checked="filters.timeSlots.includes(slot.id)"
                            @change="toggleTimeSlot(slot.id)"
                            class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                          />
                          <span class="ml-2 text-sm text-gray-700">{{ slot.label }}</span>
                        </label>
                      </div>
                    </div>

                    <!-- Arrival Time -->
                    <div class="text-center">
                      <h3 class="font-semibold text-gray-900 mb-3">Khung giờ đến</h3>
                      <div class="space-y-2">
                        <label v-for="slot in timeSlots" 
                               :key="'arrival-' + slot.id"
                               class="flex items-center justify-start cursor-pointer text-left">
                          <input 
                            type="checkbox"
                            :checked="filters.arrivalTimeSlots.includes(slot.id)"
                            @change="toggleArrivalTimeSlot(slot.id)"
                            class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                          />
                          <span class="ml-2 text-sm text-gray-700">{{ slot.label }}</span>
                        </label>
                      </div>
                    </div>

                    <!-- Facilities -->
                    <div class="text-center">
                      <h3 class="font-semibold text-gray-900 mb-3">Tiện ích</h3>
                      <div class="space-y-2">
                        <label v-for="facility in availableFacilities" 
                               :key="facility.id"
                               class="flex items-center justify-start cursor-pointer text-left">
                          <input 
                            type="checkbox"
                            :checked="filters.facilities.includes(facility.id)"
                            @change="toggleFacility(facility.id)"
                            class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                          />
                          <span class="ml-2 text-sm text-gray-700 flex items-center">
                            <i :class="facility.icon" class="mr-2 text-gray-500"></i>
                            {{ facility.name }}
                          </span>
                        </label>
                      </div>
                    </div>

                    <!-- Available Seats -->
                    <div class="text-center">
                      <h3 class="font-semibold text-gray-900 mb-3">Số ghế trống</h3>
                      <select v-model="filters.availableSeats"
                              class="w-full p-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                        <option value="">Tất cả</option>
                        <option value="1">Ít nhất 1 ghế</option>
                        <option value="2">Ít nhất 2 ghế</option>
                        <option value="4">Ít nhất 4 ghế</option>
                        <option value="6">Ít nhất 6 ghế</option>
                      </select>
                    </div>

                    <!-- Clear Filters -->
                    <div class="text-center">
                      <button @click="clearFilters"
                              class="w-full text-sm text-blue-600 hover:text-blue-800 underline">
                        Xóa bộ lọc
                      </button>
                    </div>
                  </div>
                </div>

                <!-- Search Results -->
                <div class="w-2/3 lg:w-3/4 overflow-y-auto">
                  <div class="p-6">
                    
                    <!-- Results Header -->
                    <div class="flex items-center justify-between mb-6">
                      <h3 class="text-lg font-semibold text-gray-900">
                        {{ filteredResults.length }} chuyến xe được tìm thấy
                      </h3>
                      <div class="flex items-center space-x-4">
                        <span class="text-sm text-gray-600">Sắp xếp theo:</span>
                        <select class="p-2 border border-gray-300 rounded-lg text-sm">
                          <option>Giá thấp nhất</option>
                          <option>Giờ khởi hành</option>
                          <option>Thời gian di chuyển</option>
                        </select>
                      </div>
                    </div>

                    <!-- Bus Cards -->
                    <div v-if="filteredResults.length > 0" class="space-y-4">
                      <BusCard 
                        v-for="trip in filteredResults"
                        :key="trip.id"
                        :trip="trip"
                        @book-trip="startBookingFlow"
                      />
                    </div>

                    <!-- No Results -->
                    <div v-else-if="searchResults.length === 0" 
                         class="text-center py-12">
                      <i class="fas fa-search text-gray-400 text-4xl mb-4"></i>
                      <h3 class="text-lg font-medium text-gray-900 mb-2">Không tìm thấy chuyến xe</h3>
                      <p class="text-gray-600 mb-4">Không có chuyến xe nào phù hợp với tiêu chí tìm kiếm</p>
                    </div>

                    <!-- Filtered No Results -->
                    <div v-else-if="searchResults.length > 0 && filteredResults.length === 0" 
                         class="text-center py-12">
                      <i class="fas fa-filter text-gray-400 text-4xl mb-4"></i>
                      <h3 class="text-lg font-medium text-gray-900 mb-2">Không có kết quả với bộ lọc hiện tại</h3>
                      <p class="text-gray-600 mb-4">Thử điều chỉnh bộ lọc để xem thêm chuyến xe</p>
                      <button 
                        @click="clearFilters"
                        class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
                        <i class="fas fa-times mr-2"></i>
                        Xóa bộ lọc
                      </button>
                    </div>

                  </div>
                </div>
              </template>

              <!-- Step 2-5: Booking Flow -->
              <template v-else>
                <div class="w-full overflow-y-auto">
                  <Transition
                    name="slide-left"
                    mode="out-in"
                  >
                    <BusTicketBooking
                      :show="showBookingFlow"
                      :selected-trip="selectedTrip"
                      :current-step="currentStep"
                      @step-change="handleStepChange"
                      @booking-complete="handleBookingComplete"
                      @close="backToSearch"
                    />
                  </Transition>
                </div>
              </template>

            </div>
          </div>
        </div>
      </Transition>
    </div>
  </Transition>
</template>

<style scoped>
/* Modal positioning */
.fixed.inset-0 {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Scrollbar styling */
.overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: #f8fafc;
  border-radius: 3px;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* Modal responsive */
@media (max-width: 1024px) {
  .w-1\/3.lg\:w-1\/4 {
    width: 35%;
  }
  
  .w-2\/3.lg\:w-3\/4 {
    width: 65%;
  }
}

@media (max-width: 768px) {
  .w-1\/3 {
    width: 40%;
  }
  
  .w-2\/3 {
    width: 60%;
  }
  
  .space-y-6 > * + * {
    margin-top: 1rem;
  }
  
  .p-6 {
    padding: 1rem;
  }
}

@media (max-width: 640px) {
  .w-1\/3 {
    width: 100%;
    position: absolute;
    left: -100%;
    transition: left 0.3s ease;
    z-index: 10;
    background: white;
    border-right: none;
    border-bottom: 1px solid #e5e7eb;
  }
  
  .w-2\/3 {
    width: 100%;
  }
  
  /* Show filters button on mobile */
  .w-1\/3.show-filters {
    left: 0;
  }
}

.slide-left-enter-active,
.slide-left-leave-active {
  transition: all 0.3s ease-out;
}

.slide-left-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.slide-left-leave-to {
  opacity: 0;
  transform: translateX(-100%);
}

.slide-left-enter-to,
.slide-left-leave-from {
  opacity: 1;
  transform: translateX(0);
}
</style>