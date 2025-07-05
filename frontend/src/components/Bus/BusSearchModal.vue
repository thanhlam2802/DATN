<script setup>
import { ref, computed, watch } from 'vue'
import { mockBusTrips, timeSlots, availableFacilities } from '@/data/locationData.js'
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

// Mock search results (in real app, this would come from API)
const searchResults = ref(mockBusTrips)

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
  console.log('Time slots updated:', filters.value.timeSlots)
}

const toggleFacility = (facilityId) => {
  const index = filters.value.facilities.indexOf(facilityId)
  if (index > -1) {
    filters.value.facilities.splice(index, 1)
  } else {
    filters.value.facilities.push(facilityId)
  }
  console.log('Facilities updated:', filters.value.facilities)
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
  console.log('Arrival time slots updated:', filters.value.arrivalTimeSlots)
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
  console.log('Filters cleared')
}

// Watch for filter changes and log them
watch(() => filters.value, (newFilters) => {
  console.log('Filters changed:', newFilters)
  console.log('Filtered results count:', filteredResults.value.length)
}, { deep: true })

// Watch for availableSeats changes
watch(() => filters.value.availableSeats, (newValue) => {
  console.log('Available seats filter changed:', newValue)
})

// Booking flow functions
const startBookingFlow = (trip) => {
  selectedTrip.value = trip
  currentStep.value = 2 // Move to seat selection
  showBookingFlow.value = true
  console.log('Starting booking flow for trip:', trip)
}

const handleStepChange = (step) => {
  currentStep.value = step
  console.log('Step changed to:', step)
}

const handleBookingComplete = (bookingData) => {
  console.log('Booking completed:', bookingData)
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
    return 'bg-blue-500 text-white' // Current
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
         class="fixed inset-0 z-50 bg-black-100 mt-10 bg-opacity-50 backdrop-blur-sm"
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
        <div v-if="show" class="flex items-center justify-center min-h-screen p-4">
          <div class="bg-white rounded-lg shadow-xl w-full max-w-7xl max-h-[85vh] overflow-hidden"
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
            <div class="flex" style="height: calc(85vh - 120px); min-height: 400px; max-height: 600px;">
              
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

                  <!-- Bus Cards -->
                  <div class="space-y-4">
                    <BusCard 
                      v-for="trip in filteredResults"
                      :key="trip.id"
                      :trip="trip"
                      @book-trip="startBookingFlow"
                    />
                  </div>

                  <!-- No Results -->
                  <div v-if="filteredResults.length === 0" 
                       class="text-center py-12">
                    <i class="fas fa-search text-gray-400 text-4xl mb-4"></i>
                    <h3 class="text-lg font-medium text-gray-900 mb-2">Không tìm thấy chuyến xe</h3>
                    <p class="text-gray-600">Thử điều chỉnh bộ lọc hoặc thay đổi tiêu chí tìm kiếm</p>
                  </div>

                </div>
              </div>
              </template>

              <!-- Step 2-5: Booking Flow -->
              <template v-else>
                <div class="w-full">
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