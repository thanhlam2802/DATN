<script setup>
import { ref } from 'vue'
import { facilityIcons } from '@/data/locationData.js'

const props = defineProps({
  trip: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['book-trip'])

// Active tab state (only one tab can be open)
const activeTab = ref('')

// Toggle tab function
const toggleTab = (tabName) => {
  if (activeTab.value === tabName) {
    activeTab.value = ''
  } else {
    activeTab.value = tabName
  }
}

// Format price
const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN').format(price)
}

// Format arrival time with next day indicator
const formatArrivalTime = (time, nextDay) => {
  return nextDay ? `${time}` : time
}

// Handle book button click
const handleBookTrip = () => {
  emit('book-trip', props.trip)
}
</script>

<template>
  <div class="bg-white border border-gray-200 rounded-lg shadow-sm overflow-hidden">
    
    <!-- Main Card Content -->
    <div class="p-6">
      
      <!-- Header: Bus Operator & Type -->
      <div class="mb-4">
        <h3 class="font-bold text-gray-900 text-lg">{{ trip.busOperator }}</h3>
        <p class="text-sm text-gray-500">{{ trip.busType }}</p>
      </div>

      <!-- Schedule Info Row -->
      <div class="grid grid-cols-12 gap-4 items-center mb-4">
        
        <!-- Time & Route (col 1-5) -->
        <div class="col-span-5">
          <div class="flex items-center space-x-4">
            <div class="text-center min-w-0">
              <div class="font-bold text-lg text-gray-900">{{ trip.schedule.departure }}</div>
              <div class="text-sm text-gray-600 truncate">{{ trip.route.from }}</div>
            </div>
            <div class="flex-1 flex items-center px-2">
              <div class="w-full border-t border-gray-300 relative">
                <i class="fas fa-arrow-right absolute -top-2 left-1/2 transform -translate-x-1/2 bg-white px-1 text-gray-400"></i>
              </div>
            </div>
            <div class="text-center min-w-0">
              <div class="font-bold text-lg text-gray-900">
                {{ formatArrivalTime(trip.schedule.arrival, trip.schedule.nextDay) }}
              </div>
              <div class="text-sm text-gray-600 truncate">{{ trip.route.to }}</div>
            </div>
          </div>
        </div>

        <!-- Duration (col 6-7) -->
        <div class="col-span-2 text-center">
          <div class="font-medium text-gray-900">{{ trip.schedule.duration }}</div>
          <div class="text-xs text-gray-500">Thời gian di chuyển</div>
        </div>

        <!-- Facilities Icons (col 8-9) -->
        <div class="col-span-2">
          <div class="flex items-center justify-center space-x-2 flex-wrap">
            <i v-for="facility in trip.facilities.slice(0, 4)" 
               :key="facility"
               :class="facilityIcons[facility]" 
               class="text-gray-500 text-lg"
               :title="facility">
            </i>
            <span v-if="trip.facilities.length > 4" 
                  class="text-xs text-gray-500">
              +{{ trip.facilities.length - 4 }}
            </span>
          </div>
        </div>

        <!-- Price & Book Button (col 10-12) -->
        <div class="col-span-3 text-right">
          <div class="mb-3">
            <div class="text-xl font-bold text-orange-600">
              {{ formatPrice(trip.price) }} {{ trip.currency }}
            </div>
            <div class="text-xs text-gray-500">/khách</div>
          </div>
          <button 
            @click="handleBookTrip"
            class="w-full bg-orange-500 hover:bg-orange-600 text-white px-4 py-2 rounded-lg font-medium text-sm transition-colors duration-200">
            Đặt ngay
          </button>
        </div>

      </div>

      <!-- Available Seats Info -->
      <div class="flex items-center justify-between text-sm text-gray-600 mb-6">
        <span>Còn {{ trip.availableSeats }} ghế trống</span>
        <span>{{ trip.totalSeats }} ghế</span>
      </div>

      <!-- Tab Buttons -->
      <div class="flex space-x-1 border-t border-gray-200 pt-4">
        <button 
          @click="toggleTab('features')"
          :class="[
            'flex-1 py-2 px-4 text-sm font-medium rounded-lg transition-colors duration-200',
            activeTab === 'features' 
              ? 'bg-blue-100 text-blue-700 border border-blue-200' 
              : 'text-gray-600 hover:text-gray-900 hover:bg-gray-50'
          ]">
          <i class="fas fa-star mr-2"></i>
          Tiện ích
        </button>
        <button 
          @click="toggleTab('route')"
          :class="[
            'flex-1 py-2 px-4 text-sm font-medium rounded-lg transition-colors duration-200',
            activeTab === 'route' 
              ? 'bg-blue-100 text-blue-700 border border-blue-200' 
              : 'text-gray-600 hover:text-gray-900 hover:bg-gray-50'
          ]">
          <i class="fas fa-route mr-2"></i>
          Tuyến đường
        </button>
        <button 
          @click="toggleTab('ticket')"
          :class="[
            'flex-1 py-2 px-4 text-sm font-medium rounded-lg transition-colors duration-200',
            activeTab === 'ticket' 
              ? 'bg-blue-100 text-blue-700 border border-blue-200' 
              : 'text-gray-600 hover:text-gray-900 hover:bg-gray-50'
          ]">
          <i class="fas fa-ticket-alt mr-2"></i>
          Thông tin vé
        </button>
      </div>

    </div>

    <!-- Expandable Content -->
    <Transition
      enter-active-class="transition-all duration-300 ease-out"
      enter-from-class="max-h-0 opacity-0"
      enter-to-class="max-h-96 opacity-100"
      leave-active-class="transition-all duration-200 ease-in"
      leave-from-class="max-h-96 opacity-100"
      leave-to-class="max-h-0 opacity-0"
    >
      <div v-if="activeTab" class="overflow-hidden">
        <div class="border-t border-gray-200 bg-gray-50 p-6">

          <!-- Features Tab -->
          <div v-if="activeTab === 'features'">
            <h4 class="font-semibold text-gray-900 mb-4">Thông số xe</h4>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <!-- Fleet Specification -->
              <div>
                <h5 class="font-medium text-gray-900 mb-3">Thông số kỹ thuật</h5>
                <div class="space-y-2 text-sm">
                  <div class="flex justify-between">
                    <span class="text-gray-600">Số ghế:</span>
                    <span class="font-medium">{{ trip.features.seats }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-gray-600">Bố trí ghế:</span>
                    <span class="font-medium">{{ trip.features.seatLayout }}</span>
                  </div>
                </div>
              </div>

              <!-- Facilities -->
              <div>
                <h5 class="font-medium text-gray-900 mb-3">Tiện ích:</h5>
                <div class="grid grid-cols-2 gap-2">
                  <div v-for="facility in trip.features.facilities" 
                       :key="facility.name"
                       class="flex items-center space-x-2 text-sm">
                    <i :class="facility.icon" class="text-blue-500"></i>
                    <span>{{ facility.name }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Bus Image -->
            <div class="mt-4">
              <img :src="trip.image" 
                   :alt="`${trip.busOperator} ${trip.busType}`"
                   class="w-full h-48 object-cover rounded-lg">
            </div>
          </div>

          <!-- Route Tab -->
          <div v-if="activeTab === 'route'">
            <h4 class="font-semibold text-gray-900 mb-4">Tuyến đường chi tiết</h4>
            
            <div class="space-y-4">
              <!-- Route Points -->
              <div class="flex items-start space-x-4">
                <div class="flex flex-col items-center">
                  <div class="w-3 h-3 bg-green-500 rounded-full"></div>
                  <div class="w-0.5 h-12 bg-gray-300"></div>
                  <div class="w-3 h-3 bg-red-500 rounded-full"></div>
                </div>
                <div class="flex-1 space-y-8">
                  <div>
                    <div class="font-medium text-gray-900">{{ trip.route.from }}</div>
                    <div class="text-sm text-gray-600">Điểm đón - {{ trip.schedule.departure }}</div>
                  </div>
                  <div>
                    <div class="font-medium text-gray-900">{{ trip.route.to }}</div>
                    <div class="text-sm text-gray-600">
                      Điểm trả - {{ formatArrivalTime(trip.schedule.arrival, trip.schedule.nextDay) }}
                    </div>
                  </div>
                </div>
              </div>

              <!-- Additional Route Info -->
              <div class="bg-blue-50 p-3 rounded-lg">
                <div class="flex items-center text-blue-700 text-sm">
                  <i class="fas fa-info-circle mr-2"></i>
                  <span>Thời gian di chuyển: {{ trip.schedule.duration }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Ticket Tab -->
          <div v-if="activeTab === 'ticket'">
            <h4 class="font-semibold text-gray-900 mb-4">Chính sách vé & Hoàn tiền</h4>
            
            <div class="space-y-4">
              <!-- Reschedule Policy -->
              <div class="flex items-start space-x-3">
                <div class="flex-shrink-0 mt-1">
                  <i v-if="!trip.policies.reschedule.available" 
                     class="fas fa-times-circle text-red-500"></i>
                  <i v-else 
                     class="fas fa-check-circle text-green-500"></i>
                </div>
                <div>
                  <h5 class="font-medium text-gray-900">
                    {{ trip.policies.reschedule.available ? 'Có thể đổi lịch' : 'Không thể đổi lịch' }}
                  </h5>
                  <p class="text-sm text-gray-600 mt-1">
                    {{ trip.policies.reschedule.description }}
                  </p>
                </div>
              </div>

              <!-- Refund Policy -->
              <div class="flex items-start space-x-3">
                <div class="flex-shrink-0 mt-1">
                  <i v-if="trip.policies.refund.available" 
                     class="fas fa-check-circle text-green-500"></i>
                  <i v-else 
                     class="fas fa-times-circle text-red-500"></i>
                </div>
                <div>
                  <h5 class="font-medium text-gray-900">
                    {{ trip.policies.refund.available ? 'Có thể hoàn tiền' : 'Không thể hoàn tiền' }}
                  </h5>
                  <p class="text-sm text-gray-600 mt-1">
                    {{ trip.policies.refund.description }}
                    <a v-if="trip.policies.refund.link" 
                       href="#" 
                       class="text-blue-600 hover:text-blue-800 ml-1">
                      {{ trip.policies.refund.link }}
                    </a>
                  </p>
                </div>
              </div>
            </div>
          </div>

        </div>
      </div>
    </Transition>

  </div>
</template>

<style scoped>
/* Custom transition for smooth expand/collapse */
.transition-all {
  transition-property: max-height, opacity;
}
</style> 