<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'

const props = defineProps({
  selectedBus: {
    type: Object,
    required: true
  },
  busType: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['seat-selected', 'close'])

// Animation state
const isVisible = ref(false)

// Show sidebar with animation
onMounted(() => {
  nextTick(() => {
    isVisible.value = true
  })
})

// State management
const selectedSeats = ref([])

// Mock seat status data
const seatStatus = ref({
  sold: ['A11', 'A13', 'B12', 'C24', 'D21', 'E23', 'F22'],
  selecting: ['B24', 'C22'],
  unavailable: ['D24', 'F24']
})

// Seat layout generation
const seatLayout = computed(() => {
  const rows = ['A', 'B', 'C']
  const floors = [1, 2]
  const columns = [1, 2, 3, 4, 5, 6, 7]
  
  return floors.map(floor => {
    return rows.map(row => {
      return columns.map(col => {
        const seatId = `${row}${floor}${col}`
        return {
          id: seatId,
          row: row,
          floor: floor,
          column: col,
          price: getSeatPrice(row, floor, col),
          status: getSeatStatus(seatId)
        }
      })
    })
  })
})

// Price calculation
const getSeatPrice = (row, floor, col) => {
  const basePrice = props.selectedBus.price
  let floorMultiplier = floor === 2 ? 1.2 : 1
  
  if (row === 'A') {
    return Math.round(basePrice * floorMultiplier * 1.3)
  } else if (row === 'B') {
    return Math.round(basePrice * floorMultiplier * 1.1)
  } else {
    return Math.round(basePrice * floorMultiplier)
  }
}

// Get seat status
const getSeatStatus = (seatId) => {
  if (seatStatus.value.sold.includes(seatId)) return 'sold'
  if (seatStatus.value.selecting.includes(seatId)) return 'selecting'
  if (seatStatus.value.unavailable.includes(seatId)) return 'unavailable'
  if (selectedSeats.value.includes(seatId)) return 'selected'
  return 'available'
}

// Seat selection
const selectSeat = (seat) => {
  if (seat.status !== 'available' && seat.status !== 'selected') return
  
  const seatIndex = selectedSeats.value.indexOf(seat.id)
  
  if (seatIndex > -1) {
    selectedSeats.value.splice(seatIndex, 1)
  } else {
    if (selectedSeats.value.length < 4) {
      selectedSeats.value.push(seat.id)
    } else {
      alert('Chỉ có thể chọn tối đa 4 ghế')
    }
  }
}

// Total price calculation
const totalPrice = computed(() => {
  return selectedSeats.value.reduce((total, seatId) => {
    const seat = seatLayout.value.flat().flat().find(s => s.id === seatId)
    return total + (seat ? seat.price : 0)
  }, 0)
})

// Confirm selection
const confirmSelection = () => {
  if (selectedSeats.value.length === 0) {
    alert('Vui lòng chọn ít nhất một ghế')
    return
  }
  
  emit('seat-selected', {
    seats: selectedSeats.value,
    totalPrice: totalPrice.value
  })
}

// Format price
const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price)
}

// Methods for bus facilities
const getBusFacilities = () => {
  if (props.busType === 'sleeping-bus') {
    return [
      { name: 'WiFi miễn phí', icon: 'fas fa-wifi', color: 'text-blue-600' },
      { name: 'Điều hòa', icon: 'fas fa-snowflake', color: 'text-cyan-600' },
      { name: 'Chăn gối cao cấp', icon: 'fas fa-bed', color: 'text-purple-600' },
      { name: 'TV cá nhân', icon: 'fas fa-tv', color: 'text-green-600' },
      { name: 'Suất ăn miễn phí', icon: 'fas fa-utensils', color: 'text-orange-600' },
      { name: 'WC riêng', icon: 'fas fa-restroom', color: 'text-gray-600' },
      { name: 'Nước uống', icon: 'fas fa-glass-water', color: 'text-blue-500' },
      { name: 'Ổ cắm điện', icon: 'fas fa-plug', color: 'text-yellow-600' }
    ]
  } else {
    return [
      { name: 'WiFi miễn phí', icon: 'fas fa-wifi', color: 'text-blue-600' },
      { name: 'Điều hòa', icon: 'fas fa-snowflake', color: 'text-cyan-600' },
      { name: 'Ghế ngả 160°', icon: 'fas fa-chair', color: 'text-purple-600' },
      { name: 'Sạc USB', icon: 'fas fa-usb', color: 'text-green-600' },
      { name: 'Nước uống', icon: 'fas fa-glass-water', color: 'text-blue-500' },
      { name: 'Gối kê cổ', icon: 'fas fa-heart', color: 'text-pink-600' }
    ]
  }
}

// Seat CSS classes
const getSeatClass = (seat) => {
  const baseClass = 'h-8 w-8 sm:h-10 sm:w-10 flex items-center justify-center rounded-lg border-2 transition-all duration-200 transform'
  
  switch (seat.status) {
    case 'sold':
      return `${baseClass} bg-red-100 border-red-300 text-red-600 cursor-not-allowed opacity-60`
    case 'selecting':
      return `${baseClass} bg-amber-100 border-amber-400 text-amber-700 cursor-not-allowed animate-pulse`
    case 'unavailable':
      return `${baseClass} bg-gray-100 border-gray-300 text-gray-400 cursor-not-allowed opacity-50`
    case 'selected':
      return `${baseClass} bg-indigo-600 border-indigo-500 text-white scale-110 shadow-lg ring-2 ring-indigo-200`
    default:
      return `${baseClass} bg-emerald-50 border-emerald-300 text-emerald-700 hover:bg-emerald-100 hover:border-emerald-400 hover:scale-105 cursor-pointer`
  }
}
</script>

<template>
  <!-- Background Overlay with animation -->
  <Transition
    enter-active-class="ease-out duration-300"
    enter-from-class="opacity-0"
    enter-to-class="opacity-100"
    leave-active-class="ease-in duration-200"
    leave-from-class="opacity-100"
    leave-to-class="opacity-0"
  >
    <div 
      v-if="isVisible"
      class="fixed inset-0 bg-gray-500 bg-opacity-40 backdrop-blur-sm transition-opacity z-40"
      @click="emit('close')"
    />
  </Transition>

  <!-- Sidebar Panel with slide animation -->
  <Transition
    enter-active-class="transform transition ease-in-out duration-500"
    enter-from-class="translate-x-full"
    enter-to-class="translate-x-0"
    leave-active-class="transform transition ease-in-out duration-300"
    leave-from-class="translate-x-0"
    leave-to-class="translate-x-full"
  >
    <div 
      v-if="isVisible"
      class="fixed inset-y-0 right-0 z-50 flex w-full max-w-sm sm:max-w-md md:max-w-lg lg:max-w-2xl xl:max-w-4xl 2xl:max-w-6xl"
    >
      <div class="relative flex w-full flex-col overflow-y-auto bg-white shadow-2xl">
        <!-- Header -->
        <div class="sticky top-0 z-10 bg-gradient-to-r from-indigo-600 via-purple-600 to-blue-600 px-4 py-6 sm:px-6">
          <div class="flex items-center justify-between">
            <div class="flex items-center space-x-3">
              <div class="flex h-10 w-10 items-center justify-center rounded-full bg-white bg-opacity-20 sm:h-12 sm:w-12">
                <svg class="h-5 w-5 text-white sm:h-6 sm:w-6" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M8.25 18.75a1.5 1.5 0 01-3 0V9a1.5 1.5 0 013 0v9.75z" />
                  <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 18.75a1.5 1.5 0 01-3 0V9a1.5 1.5 0 013 0v9.75z" />
                </svg>
              </div>
              <div>
                <h2 class="text-lg font-semibold text-white sm:text-xl">Chọn ghế yêu thích</h2>
                <p class="text-sm text-indigo-100 sm:text-base">{{ selectedBus.company }} • {{ selectedBus.route }}</p>
              </div>
            </div>
            <button
              type="button"
              @click="emit('close')"
              class="relative -m-2 p-2 text-indigo-200 hover:text-white transition-colors duration-200"
            >
              <span class="absolute -inset-0.5"></span>
              <span class="sr-only">Đóng panel</span>
              <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>

        <!-- Content -->
        <div class="flex-1 px-4 py-6 sm:px-6">
          <div class="space-y-6 lg:space-y-8">
            <!-- Bus Direction Indicator -->
            <div class="text-center">
              <div class="inline-flex items-center rounded-full bg-gradient-to-r from-blue-50 to-indigo-50 px-4 py-2 text-sm font-medium text-blue-700 ring-1 ring-inset ring-blue-600/20">
                <svg class="mr-2 h-4 w-4 animate-spin" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="m4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                <span class="mr-2">Hướng xe chạy</span>
                <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M13.5 4.5L21 12m0 0l-7.5 7.5M21 12H3" />
                </svg>
              </div>
            </div>

            <!-- Seat Layout - Responsive Grid -->
            <div class="space-y-6">
              <div 
                v-for="(floor, floorIndex) in seatLayout" 
                :key="floorIndex"
                class="overflow-hidden rounded-xl bg-gradient-to-br from-gray-50 to-blue-50 shadow-sm ring-1 ring-gray-200"
              >
                <!-- Floor Header -->
                <div class="border-b border-gray-200 bg-white px-4 py-3 sm:px-6">
                  <div class="flex items-center justify-center">
                    <div class="flex items-center space-x-2">
                      <div class="flex h-6 w-6 items-center justify-center rounded-full bg-indigo-100">
                        <span class="text-xs font-medium text-indigo-600">{{ floorIndex + 1 }}</span>
                      </div>
                      <h3 class="text-sm font-medium text-gray-900 sm:text-base">Tầng {{ floorIndex + 1 }}</h3>
                    </div>
                  </div>
                </div>

                <div class="p-4 sm:p-6">
                  <!-- Driver Area (only on floor 1) -->
                  <div v-if="floorIndex === 0" class="mb-4 flex justify-start">
                    <div class="flex h-8 w-12 items-center justify-center rounded-lg bg-gradient-to-r from-gray-600 to-gray-800 shadow-sm sm:h-10 sm:w-14">
                      <svg class="h-4 w-4 text-white sm:h-5 sm:w-5" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M8.25 18.75a1.5 1.5 0 01-3 0V9a1.5 1.5 0 013 0v9.75z" />
                      </svg>
                    </div>
                  </div>

                  <!-- Seat Rows -->
                  <div class="space-y-4">
                    <div 
                      v-for="(row, rowIndex) in floor" 
                      :key="rowIndex"
                      class="flex items-center justify-center space-x-4"
                    >
                      <!-- Row Label -->
                      <div class="flex h-8 w-8 flex-shrink-0 items-center justify-center rounded-lg bg-gradient-to-br from-gray-700 to-gray-900 text-xs font-bold text-white shadow-sm sm:h-10 sm:w-10 sm:text-sm">
                        {{ row[0].row }}{{ floorIndex + 1 }}
                      </div>
                      
                      <!-- Seats Grid -->
                      <div class="flex items-center space-x-3 sm:space-x-4">
                        <!-- Left Section (3 seats) -->
                        <div class="flex space-x-1 sm:space-x-2">
                          <button
                            v-for="seat in row.slice(0, 3)"
                            :key="seat.id"
                            @click="selectSeat(seat)"
                            :disabled="seat.status === 'sold' || seat.status === 'selecting' || seat.status === 'unavailable'"
                            :class="getSeatClass(seat)"
                            class="group relative"
                          >
                            <span class="text-xs font-bold">{{ seat.id }}</span>
                            <!-- Tooltip -->
                            <div class="absolute bottom-full left-1/2 z-20 mb-2 -translate-x-1/2 transform opacity-0 transition-opacity group-hover:opacity-100">
                              <div class="whitespace-nowrap rounded-lg bg-gray-900 px-2 py-1 text-xs text-white shadow-lg">
                                {{ formatPrice(seat.price) }}
                                <div class="absolute top-full left-1/2 -translate-x-1/2 transform">
                                  <div class="border-4 border-transparent border-t-gray-900"></div>
                                </div>
                              </div>
                            </div>
                          </button>
                        </div>
                        
                        <!-- Aisle -->
                        <div class="flex h-8 w-4 items-center justify-center sm:h-10 sm:w-6">
                          <div class="h-6 w-1 rounded-full bg-gradient-to-b from-gray-300 to-gray-400 opacity-60 sm:h-8 sm:w-1.5"></div>
                        </div>
                        
                        <!-- Right Section (4 seats) -->
                        <div class="flex space-x-1 sm:space-x-2">
                          <button
                            v-for="seat in row.slice(3)"
                            :key="seat.id"
                            @click="selectSeat(seat)"
                            :disabled="seat.status === 'sold' || seat.status === 'selecting' || seat.status === 'unavailable'"
                            :class="getSeatClass(seat)"
                            class="group relative"
                          >
                            <span class="text-xs font-bold">{{ seat.id }}</span>
                            <!-- Tooltip -->
                            <div class="absolute bottom-full left-1/2 z-20 mb-2 -translate-x-1/2 transform opacity-0 transition-opacity group-hover:opacity-100">
                              <div class="whitespace-nowrap rounded-lg bg-gray-900 px-2 py-1 text-xs text-white shadow-lg">
                                {{ formatPrice(seat.price) }}
                                <div class="absolute top-full left-1/2 -translate-x-1/2 transform">
                                  <div class="border-4 border-transparent border-t-gray-900"></div>
                                </div>
                              </div>
                            </div>
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Legend -->
            <div class="rounded-xl bg-white p-4 shadow-sm ring-1 ring-gray-200 sm:p-6">
              <h4 class="text-sm font-medium text-gray-900 sm:text-base">Chú giải trạng thái ghế</h4>
              <div class="mt-4 grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-5">
                <div class="flex items-center space-x-2">
                  <div class="h-4 w-4 rounded border-2 border-emerald-300 bg-emerald-50 sm:h-6 sm:w-6"></div>
                  <span class="text-xs text-gray-600 sm:text-sm">Trống</span>
                </div>
                <div class="flex items-center space-x-2">
                  <div class="h-4 w-4 rounded border-2 border-indigo-400 bg-indigo-600 sm:h-6 sm:w-6"></div>
                  <span class="text-xs text-gray-600 sm:text-sm">Đang chọn</span>
                </div>
                <div class="flex items-center space-x-2">
                  <div class="h-4 w-4 rounded border-2 border-red-300 bg-red-100 sm:h-6 sm:w-6"></div>
                  <span class="text-xs text-gray-600 sm:text-sm">Đã bán</span>
                </div>
                <div class="flex items-center space-x-2">
                  <div class="h-4 w-4 rounded border-2 border-amber-400 bg-amber-100 sm:h-6 sm:w-6"></div>
                  <span class="text-xs text-gray-600 sm:text-sm">Đang giữ</span>
                </div>
                <div class="flex items-center space-x-2">
                  <div class="h-4 w-4 rounded border-2 border-gray-300 bg-gray-100 sm:h-6 sm:w-6"></div>
                  <span class="text-xs text-gray-600 sm:text-sm">Không khả dụng</span>
                </div>
              </div>
            </div>

            <!-- Bus Facilities -->
            <div class="rounded-xl bg-gradient-to-br from-blue-50 to-indigo-50 p-4 shadow-sm ring-1 ring-blue-200 sm:p-6">
              <h4 class="text-sm font-medium text-gray-900 sm:text-base mb-4 flex items-center">
                <i class="fas fa-star mr-2 text-yellow-500"></i>
                Tiện ích xe {{ busType === 'sleeping-bus' ? 'Giường nằm' : 'Ghế ngồi' }}
              </h4>
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div
                  v-for="facility in getBusFacilities()"
                  :key="facility.name"
                  class="flex items-center space-x-3 p-3 bg-white rounded-lg border border-blue-100 hover:border-blue-200 transition-colors"
                >
                  <div class="flex-shrink-0">
                    <i :class="[facility.icon, facility.color, 'text-lg']"></i>
                  </div>
                  <span class="text-sm font-medium text-gray-700">{{ facility.name }}</span>
                </div>
              </div>
              
              <!-- Special note for sleeping bus -->
              <div v-if="busType === 'sleeping-bus'" class="mt-4 p-3 bg-purple-50 border border-purple-200 rounded-lg">
                <div class="flex items-start space-x-2">
                  <i class="fas fa-crown text-purple-600 mt-0.5"></i>
                  <div class="text-sm text-purple-800">
                    <p class="font-semibold mb-1">Dịch vụ VIP:</p>
                    <p>Giường nằm thương gia với không gian riêng tư, chăn gối cao cấp và dịch vụ 5 sao.</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Bus Info -->
            <div class="rounded-xl bg-white p-4 shadow-sm ring-1 ring-gray-200 sm:p-6">
              <h4 class="text-sm font-medium text-gray-900 sm:text-base mb-4 flex items-center">
                <i class="fas fa-info-circle mr-2 text-blue-500"></i>
                Thông tin chuyến xe
              </h4>
              <div class="space-y-3 text-sm">
                <div class="flex justify-between items-center">
                  <span class="text-gray-600">Nhà xe:</span>
                  <span class="font-semibold text-gray-900">{{ selectedBus.company }}</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-gray-600">Tuyến đường:</span>
                  <span class="font-medium text-gray-900">{{ selectedBus.route }}</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-gray-600">Khởi hành:</span>
                  <span class="font-semibold text-purple-600">{{ selectedBus.departureTime }}</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-gray-600">Thời gian:</span>
                  <span class="font-medium text-gray-900">{{ selectedBus.duration }}</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-gray-600">Đến nơi:</span>
                  <span class="font-semibold text-gray-900">{{ selectedBus.arrivalTime }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Sticky Footer with Booking Summary -->
        <div class="border-t border-gray-200 bg-gray-50 px-4 py-6 sm:px-6">
          <div class="space-y-4">
            <!-- Selected Seats -->
            <div>
              <h4 class="text-sm font-medium text-gray-900">Ghế đã chọn</h4>
              <div class="mt-2">
                <div v-if="selectedSeats.length > 0" class="flex flex-wrap gap-2">
                  <span
                    v-for="seatId in selectedSeats"
                    :key="seatId"
                    class="inline-flex items-center rounded-full bg-indigo-100 px-2.5 py-0.5 text-xs font-medium text-indigo-800"
                  >
                    {{ seatId }}
                  </span>
                </div>
                <p v-else class="text-sm text-gray-500">Chưa chọn ghế nào</p>
              </div>
            </div>

            <!-- Price Summary -->
            <div class="rounded-lg bg-white p-4 shadow-sm ring-1 ring-gray-200">
              <div class="flex items-center justify-between">
                <span class="text-sm text-gray-600">Số ghế:</span>
                <span class="text-sm font-medium text-gray-900">{{ selectedSeats.length }}</span>
              </div>
              <div class="mt-2 flex items-center justify-between border-t border-gray-200 pt-2">
                <span class="text-base font-medium text-gray-900">Tổng tiền:</span>
                <span class="text-lg font-bold text-indigo-600">{{ formatPrice(totalPrice) }}</span>
              </div>
            </div>

            <!-- Action Buttons -->
            <div class="flex space-x-3">
              <button
                type="button"
                @click="emit('close')"
                class="flex-1 rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
              >
                Hủy bỏ
              </button>
              <button
                type="button"
                @click="confirmSelection"
                :disabled="selectedSeats.length === 0"
                class="flex-1 rounded-md border border-transparent bg-indigo-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:bg-gray-300 disabled:cursor-not-allowed"
              >
                Xác nhận ({{ selectedSeats.length }})
              </button>
            </div>

            <!-- Notes -->
            <div class="rounded-md bg-amber-50 p-3">
              <div class="flex">
                <div class="flex-shrink-0">
                  <svg class="h-5 w-5 text-amber-400" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M8.485 2.495c.673-1.167 2.357-1.167 3.03 0l6.28 10.875c.673 1.167-.17 2.625-1.516 2.625H3.72c-1.347 0-2.189-1.458-1.515-2.625L8.485 2.495zM10 5a.75.75 0 01.75.75v3.5a.75.75 0 01-1.5 0v-3.5A.75.75 0 0110 5zm0 9a1 1 0 100-2 1 1 0 000 2z" clip-rule="evenodd" />
                  </svg>
                </div>
                <div class="ml-3">
                  <p class="text-xs text-amber-700">
                    Ghế sẽ được giữ trong 10 phút. Tối đa 4 ghế mỗi lần đặt.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Transition>
</template>

<style scoped>
/* Custom animations */
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: .5;
  }
}

.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

/* Smooth scrollbar */
.overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: #f1f5f9;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style> 