<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { timeSlots, availableFacilities } from '@/data/locationData.js'
import { BusSearchAPI } from '@/api/busAPI_Client'
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
  }
})

const emit = defineEmits(['close'])

// Modal state management
const currentStep = ref(1) // 1: Tìm kiếm, 2: Chọn ghế, 3: Thông tin, 4: Thanh toán, 5: Xác nhận
const selectedTrip = ref(null)
const showBookingFlow = ref(false)

// Loading state
const loadingResults = ref(false)
const searchError = ref('')

// Component lifecycle tracking
const isMounted = ref(false)

// Steps configuration
const steps = [
  { id: 1, name: 'Tìm kiếm', icon: 'fas fa-search' },
  { id: 2, name: 'Chọn ghế', icon: 'fas fa-chair' },
  { id: 3, name: 'Thông tin', icon: 'fas fa-user' },
  { id: 4, name: 'Thanh toán', icon: 'fas fa-credit-card' },
  { id: 5, name: 'Xác nhận', icon: 'fas fa-check' }
]

// Filter states
const filters = ref({
  timeSlots: [],
  arrivalTimeSlots: [],
  facilities: [],
  availableSeats: ''
})

// Real search results from API
const rawSearchResults = ref([])
const searchResults = ref([])

// Transform BusSlot data to match UI expectations
const transformBusSlotToTrip = (busSlot) => {
  // Calculate duration from estimated minutes
  const duration = busSlot.route.estimatedDurationMinutes 
    ? `${Math.floor(busSlot.route.estimatedDurationMinutes / 60)}h ${busSlot.route.estimatedDurationMinutes % 60}m`
    : '8h 30m' // fallback

  // Get first image URL or use fallback
  const imageUrl = busSlot.bus.busImages?.[0]?.image?.url || '/default-bus.jpg'

  // Mock facilities based on bus category (real data should come from backend)
  const facilities = generateFacilitiesFromCategory(busSlot.bus.categoryName)

  return {
    id: busSlot.id,
    busSlotId: busSlot.id, // Keep reference for booking
    company: busSlot.bus.name || 'Nhà xe không xác định',
    busType: busSlot.bus.categoryName || 'Xe khách',
    route: {
      from: busSlot.route.origin,
      to: busSlot.route.destination,
      distance: busSlot.route.distanceKm ? `${busSlot.route.distanceKm}km` : 'N/A'
    },
    schedule: {
      departure: busSlot.departureTime,
      arrival: busSlot.arrivalTime,
      duration: duration
    },
    price: busSlot.price,
    totalSeats: busSlot.totalSeats,
    availableSeats: busSlot.availableSeats,
    facilities: facilities,
    image: imageUrl,
    status: busSlot.status,
    // For booking flow
    type: determineBusType(busSlot.totalSeats),
    activeTab: determineBusType(busSlot.totalSeats)
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

// Determine bus type for seat layout
const determineBusType = (totalSeats) => {
  if (totalSeats <= 20) {
    return 'shuttle-bus'
  } else {
    return 'sleeping-bus'
  }
}

// Mount/unmount tracking
onMounted(() => {
  isMounted.value = true
})

onUnmounted(() => {
  isMounted.value = false
})

// Perform search when modal opens or search params change
const performSearch = async () => {
  if (!isMounted.value || !props.searchParams.from || !props.searchParams.to || !props.searchParams.departureDate) {
    return
  }

  try {
    loadingResults.value = true
    searchError.value = ''

    const criteria = {
      from: props.searchParams.from,
      to: props.searchParams.to,
      departureDate: props.searchParams.departureDate,
      seats: props.searchParams.seats || 1,
      roundtrip: props.searchParams.roundtrip || false
    }

    const busSlots = await BusSearchAPI.searchBusSlots(criteria)
    
    // Check if still mounted before updating state
    if (isMounted.value) {
      rawSearchResults.value = busSlots
      // Transform to UI format
      searchResults.value = busSlots.map(transformBusSlotToTrip)
    }

  } catch (error) {
    if (isMounted.value) {
      searchError.value = 'Không thể tìm kiếm chuyến xe. Vui lòng thử lại.'
      searchResults.value = []
      rawSearchResults.value = []
    }
  } finally {
    if (isMounted.value) {
      loadingResults.value = false
    }
  }
}

// Watch for search params changes
watch(() => props.searchParams, async (newParams) => {
  if (props.show && newParams && isMounted.value) {
    await performSearch()
  }
}, { deep: true })

// Watch for modal show/hide
watch(() => props.show, async (isVisible) => {
  if (isVisible && isMounted.value) {
    await performSearch()
  }
})

// Filtered results based on filters
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

// Handle filter changes
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

// Clear all filters
const clearFilters = () => {
  filters.value = {
    timeSlots: [],
    arrivalTimeSlots: [],
    facilities: [],
    availableSeats: ''
  }
}

// Booking flow functions
const startBookingFlow = (trip) => {
  selectedTrip.value = {
    ...trip,
    type: props.searchParams.busType || trip.type, // Use busType from search params
    activeTab: props.searchParams.busType || trip.activeTab // Pass activeTab info
  }
  currentStep.value = 2 // Move to seat selection
  showBookingFlow.value = true
}

const handleStepChange = (step) => {
  currentStep.value = step
}

const handleBookingComplete = (bookingData) => {
  // Handle booking completion
  // In real app, send to API
  // Reset modal state
  currentStep.value = 1
  showBookingFlow.value = false
  selectedTrip.value = null
  // Show success message or redirect
  emit('close')
}

const backToSearch = () => {
  currentStep.value = 1
  showBookingFlow.value = false
  selectedTrip.value = null
}

// Step styling functions
const getStepClass = (step) => {
  if (step.id < currentStep.value) {
    return 'bg-green-500 text-white' // Completed
  } else if (step.id === currentStep.value) {
    return 'bg-indigo-600 text-white' // Current
  } else {
    return 'bg-gray-200 text-gray-500' // Pending
  }
}

const getStepConnectorClass = (stepIndex) => {
  if (stepIndex < currentStep.value - 1) {
    return 'bg-green-500' // Completed
  } else {
    return 'bg-gray-200' // Pending
  }
}

// Retry search function
const retrySearch = () => {
  performSearch()
}
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
        <div v-if="show" class="flex items-start justify-center min-h-screen p-4 py-8 mt-[100px]">
          <div class="bg-white rounded-lg shadow-xl w-full max-w-7xl max-h-[90vh] overflow-hidden"
               @click.stop>
            
            <!-- Modal Header -->
            <div class="bg-gray-50 px-6 py-4 border-b border-gray-200">
              <div class="flex items-center justify-between mb-4">
                <div>
                  <h2 class="text-xl font-bold text-gray-900">
                    {{ currentStep === 1 ? 'Kết quả tìm kiếm' : 'Đặt vé xe khách' }}
                  </h2>
                  <p class="text-sm text-gray-600 mt-1">
                    {{ searchParams.from }} → {{ searchParams.to }} • {{ searchParams.departureDate }}
                    <span v-if="searchParams.roundtrip"> • Khứ hồi</span>
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

            <!-- Modal Body -->
            <div class="flex" style="">
              
              <!-- Step 1: Search Results -->
              <template v-if="currentStep === 1">
                <!-- Sidebar Filters (col-2) -->
                <div class="w-4/12 bg-gray-50 border-r border-gray-200 overflow-y-auto flex justify-center">
                  <div class="w-full max-w-xs mx-auto p-6 space-y-6">
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

              <!-- Search Results (col-8) -->
              <div class="w-8/12 overflow-y-auto flex-1">
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

                  <!-- Loading State -->
                  <div v-if="loadingResults" class="text-center py-12">
                    <div class="flex flex-col items-center">
                      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mb-4"></div>
                      <h3 class="text-lg font-medium text-gray-900 mb-2">Đang tìm kiếm chuyến xe...</h3>
                      <p class="text-gray-600">Vui lòng chờ trong giây lát</p>
                    </div>
                  </div>

                  <!-- Error State -->
                  <div v-else-if="searchError" class="text-center py-12">
                    <div class="flex flex-col items-center">
                      <i class="fas fa-exclamation-triangle text-red-400 text-4xl mb-4"></i>
                      <h3 class="text-lg font-medium text-gray-900 mb-2">Có lỗi xảy ra</h3>
                      <p class="text-gray-600 mb-4">{{ searchError }}</p>
                      <button 
                        @click="retrySearch"
                        class="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors">
                        <i class="fas fa-redo mr-2"></i>
                        Thử lại
                      </button>
                    </div>
                  </div>

                  <!-- Bus Cards -->
                  <div v-else-if="!loadingResults && filteredResults.length > 0" class="space-y-4">
                    <BusCard 
                      v-for="trip in filteredResults"
                      :key="trip.id"
                      :trip="trip"
                      @book-trip="startBookingFlow"
                    />
                  </div>

                  <!-- No Results -->
                  <div v-else-if="!loadingResults && searchResults.length === 0" 
                       class="text-center py-12">
                    <i class="fas fa-search text-gray-400 text-4xl mb-4"></i>
                    <h3 class="text-lg font-medium text-gray-900 mb-2">Không tìm thấy chuyến xe</h3>
                    <p class="text-gray-600 mb-4">Không có chuyến xe nào phù hợp với tiêu chí tìm kiếm</p>
                    <button 
                      @click="clearFilters"
                      class="px-4 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors">
                      <i class="fas fa-filter mr-2"></i>
                      Xóa bộ lọc
                    </button>
                  </div>

                  <!-- Filtered No Results -->
                  <div v-else-if="!loadingResults && searchResults.length > 0 && filteredResults.length === 0" 
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
                  <div class="">
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
/* Custom scrollbar */
.overflow-y-auto::-webkit-scrollbar {
  width: 4px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 2px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* Slide left animation */
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