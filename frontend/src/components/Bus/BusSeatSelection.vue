<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { toast } from '@/utils/notifications'

const props = defineProps({
  busSlot: {
    type: Object,
    required: true
  },
  selectedSeats: {
    type: Array,
    default: () => []
  },
  maxSeats: {
    type: Number,
    default: 10
  }
})

const emit = defineEmits(['seat-selected', 'seat-deselected', 'selection-change'])

// State management
const seats = ref([])
const loading = ref(true)
const selectedSeatIds = ref([...props.selectedSeats])

// Computed layout properties
const seatLayout = computed(() => {
  if (!seats.value.length) return { levels: [], busType: 'SHUTTLE_STANDARD' }
  
  const totalSeats = seats.value.length
  const busType = determineBusType(totalSeats)
  
  // Phân chia ghế theo tầng cho xe 45 chỗ
  if (totalSeats > 30) {
    return createTwoLevelLayout(seats.value, busType)
  } else {
    return createSingleLevelLayout(seats.value, busType)
  }
})

// Create two-level layout for large buses (45 seats)
const createTwoLevelLayout = (allSeats, busType) => {
  const level1Seats = allSeats.filter(seat => {
    const { row, col } = parseSeatNumber(seat.seatNumber, busType)
    // Tầng 1: A1-A8, B1-B8, C1-C8 (24 ghế)
    return ['A', 'B', 'C'].includes(row) && col <= 8
  })
  
  const level2Seats = allSeats.filter(seat => {
    const { row, col } = parseSeatNumber(seat.seatNumber, busType)
    // Tầng 2: ghế còn lại
    return !((['A', 'B', 'C'].includes(row) && col <= 8))
  })
  
  return {
    busType,
    levels: [
      {
        name: 'Tầng 1',
        rows: createRowsFromSeats(level1Seats, ['A', 'B', 'C'])
      },
      {
        name: 'Tầng 2', 
        rows: createRowsFromSeats(level2Seats, ['A', 'B', 'C'])
      }
    ]
  }
}

// Create single-level layout for smaller buses
const createSingleLevelLayout = (allSeats, busType) => {
  const rowLetters = busType === 'BED_DOUBLE_ROOM' ? ['A', 'B'] : ['A', 'B', 'C']
  
  return {
    busType,
    levels: [{
      name: 'Tầng đơn',
      rows: createRowsFromSeats(allSeats, rowLetters)
    }]
  }
}

// Helper function to create rows from seats
const createRowsFromSeats = (seatsList, rowLetters) => {
  const seatsByPosition = {}
  
  seatsList.forEach(seat => {
    const { row, col } = parseSeatNumber(seat.seatNumber, 'BED_SINGLE_ROOM')
    if (!seatsByPosition[row]) {
      seatsByPosition[row] = {}
    }
    seatsByPosition[row][col] = seat
  })
  
  // Tạo rows theo thứ tự A, B, C
  return rowLetters
    .filter(rowKey => seatsByPosition[rowKey]) // Chỉ lấy row có ghế
    .map(rowKey => ({
      id: rowKey,
      seats: Object.keys(seatsByPosition[rowKey])
        .sort((a, b) => parseInt(a) - parseInt(b))
        .map(colKey => seatsByPosition[rowKey][colKey])
    }))
}

// Determine bus type for layout
const determineBusType = (totalSeats) => {
  if (totalSeats <= 20) return 'SHUTTLE_STANDARD' // 16 chỗ: A1-A16
  if (totalSeats <= 30) return 'BED_DOUBLE_ROOM'  // 20 chỗ: 2 dãy AB
  return 'BED_SINGLE_ROOM' // 40+ chỗ: 3 dãy ABC
}

// Parse seat number to row/col
const parseSeatNumber = (seatNumber, busType) => {
  // Xử lý các format: A1, B2, C3, hoặc A1, A2, A3...
  const match = seatNumber.match(/^([A-Z])?(\d+)$/)
  
  if (match) {
    const [, letter, number] = match
    
    if (letter) {
      // Format: A1, B2, C3
      return {
        row: letter,
        col: parseInt(number)
      }
    } else {
      // Format: 1, 2, 3... (for SHUTTLE_STANDARD)
      return {
        row: 'A',
        col: parseInt(number)
      }
    }
  }
  
  // Fallback
  return { row: 'A', col: 1 }
}

// Seat selection methods
const selectSeat = (seat) => {
  if (seat.isBooked) {
    toast.warning('Ghế này đã được đặt')
    return
  }
  
  if (selectedSeatIds.value.includes(seat.id)) {
    // Deselect
    selectedSeatIds.value = selectedSeatIds.value.filter(id => id !== seat.id)
    emit('seat-deselected', seat)
  } else {
    // Check max seats limit
    if (selectedSeatIds.value.length >= props.maxSeats) {
      toast.warning(`Chỉ có thể chọn tối đa ${props.maxSeats} ghế`)
      return
    }
    
    // Select
    selectedSeatIds.value.push(seat.id)
    emit('seat-selected', seat)
  }
  
  // Emit selection change
  const selectedSeats = seats.value.filter(seat => selectedSeatIds.value.includes(seat.id))
  emit('selection-change', selectedSeats)
}

// Get seat status class
const getSeatClass = (seat) => {
  const baseClass = 'border-2 cursor-pointer transition-all duration-200 flex items-center justify-center'
  
  if (seat.isBooked) {
    return `${baseClass} bg-red-100 border-red-300 text-red-600 cursor-not-allowed`
  }
  
  if (selectedSeatIds.value.includes(seat.id)) {
    return `${baseClass} bg-blue-500 border-blue-600 text-white shadow-lg`
  }
  
  return `${baseClass} bg-green-100 border-green-300 text-green-700 hover:bg-green-200 hover:shadow-md`
}



// Watch props changes
watch(() => props.selectedSeats, (newSeats) => {
  selectedSeatIds.value = [...newSeats]
}, { deep: true })

// Load seats data
onMounted(() => {
  if (props.busSlot?.seats) {
    seats.value = [...props.busSlot.seats]
    loading.value = false
  } else {
    toast.error('Không thể tải thông tin ghế')
    loading.value = false
  }
})

// Computed selections
const selectedSeatsInfo = computed(() => {
  const selected = seats.value.filter(seat => selectedSeatIds.value.includes(seat.id))
  const totalPrice = selected.reduce((sum, seat) => sum + (seat.price || 0), 0)
  
  return {
    seats: selected,
    count: selected.length,
    totalPrice,
    seatNumbers: selected.map(s => s.seatNumber).join(', ')
  }
})

const availableSeatsCount = computed(() => {
  return seats.value.filter(seat => !seat.isBooked).length
})
</script>

<template>
  <div class="bus-seat-selection">
    <!-- Compact Header - Only essential info -->
    <div class="flex items-center justify-between mb-4 pb-3 border-b border-gray-200">
      <div class="text-sm text-gray-600">
        Còn <span class="font-medium text-green-600">{{ availableSeatsCount }}</span> ghế trống
      </div>
      
      <!-- Compact Legend -->
      <div class="flex items-center space-x-4 text-xs">
        <div class="flex items-center space-x-1">
          <div class="w-3 h-3 bg-green-100 border border-green-300 rounded"></div>
          <span class="text-gray-600">Trống</span>
        </div>
        <div class="flex items-center space-x-1">
          <div class="w-3 h-3 bg-blue-500 border border-blue-600 rounded"></div>
          <span class="text-gray-600">Đã chọn</span>
        </div>
        <div class="flex items-center space-x-1">
          <div class="w-3 h-3 bg-red-100 border border-red-300 rounded"></div>
          <span class="text-gray-600">Đã đặt</span>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex items-center justify-center py-12">
      <div class="text-center">
        <i class="fas fa-spinner fa-spin text-2xl text-gray-400 mb-2"></i>
        <p class="text-gray-600">Đang tải sơ đồ ghế...</p>
      </div>
    </div>

    <!-- Seat Layout -->
    <div v-else-if="seatLayout.levels.length > 0" class="seat-layout">
      <!-- Bus Front Indicator -->
      <div class="flex justify-center mb-4">
        <div class="bg-gray-200 px-4 py-2 rounded-full text-sm text-gray-600 font-medium">
          <i class="fas fa-steering-wheel mr-2"></i>
          Tài xế
        </div>
      </div>

      <div class="levels-container p-4 md:p-6">
  <div v-if="seatLayout.levels.length > 1" class="grid grid-cols-1 lg:grid-cols-2 gap-8 lg:gap-12">
    <div v-for="level in seatLayout.levels" :key="level.name" class="level-section bg-white border border-gray-200 rounded-lg shadow-sm p-4">

      <h4 class="text-center text-lg font-semibold text-gray-800 mb-4">
        {{ level.name }}
      </h4>

      <div class="bus-container">
        <div class="bus-interior flex flex-col items-center">
          <div class="vertical-layout flex justify-center space-x-4 md:space-x-6">
            <div v-for="(row, rowIndex) in level.rows" :key="row.id" class="seat-column flex flex-col items-center">
              <div class="row-header mb-2">
                <span class="text-sm font-medium text-gray-600">{{ row.id }}</span>
              </div>

              <div class="seats-vertical flex flex-col space-y-2">
                <button
                  v-for="seat in row.seats"
                  :key="seat.id"
                  @click="selectSeat(seat)"
                  :class="getSeatClass(seat)"
                  :disabled="seat.isBooked"
                  :title="`Ghế ${seat.seatNumber} - ${seat.isBooked ? 'Đã đặt' : formatPrice(seat.price)} đ`"
                  class="w-10 h-10 flex items-center justify-center rounded-md font-medium transition-colors duration-200 ease-in-out"
                >
                  {{ seat.seatNumber }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div v-else class="single-level-container max-w-2xl mx-auto p-4 bg-white border border-gray-200 rounded-lg shadow-sm">
    <div v-for="level in seatLayout.levels" :key="level.name" class="level-section">
      <h4 class="text-center text-lg font-semibold text-gray-800 mb-4">
        {{ level.name }}
      </h4>

      <div class="bus-container">
        <div class="bus-interior flex flex-col items-center">
          <div class="vertical-layout flex justify-center space-x-4 md:space-x-6">
            <div v-for="(row, rowIndex) in level.rows" :key="row.id" class="seat-column flex flex-col items-center">
              <div class="row-header mb-2">
                <span class="text-sm font-medium text-gray-600">{{ row.id }}</span>
              </div>

              <div class="seats-vertical flex flex-col space-y-2">
                <button
                  v-for="seat in row.seats"
                  :key="seat.id"
                  @click="selectSeat(seat)"
                  :class="getSeatClass(seat)"
                  :disabled="seat.isBooked"
                  :title="`Ghế ${seat.seatNumber} - ${seat.isBooked ? 'Đã đặt' : formatPrice(seat.price)} đ`"
                  class="w-10 h-10 flex items-center justify-center rounded-md font-medium transition-colors duration-200 ease-in-out"
                >
                  {{ seat.seatNumber }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
    </div>

    <!-- No Seats Available -->
    <div v-else class="text-center py-12">
      <i class="fas fa-exclamation-triangle text-4xl text-yellow-500 mb-4"></i>
      <h3 class="text-lg font-medium text-gray-900 mb-2">Không có ghế khả dụng</h3>
      <p class="text-gray-600">Chuyến xe này hiện không có thông tin ghế.</p>
    </div>

    
  </div>
</template>

<script>
// Helper function for price formatting
const formatPrice = (price) => {
  if (typeof price === 'number') {
    return new Intl.NumberFormat('vi-VN').format(price)
  }
  return '0'
}
</script>

<style scoped>
.seat-layout {
  user-select: none;
}

.bus-container {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

.seat-row {
  min-height: 3.5rem;
}

/* Vertical layout styling */
.vertical-layout {
  gap: 0.75rem;
  align-items: flex-start;
}

.seat-column {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 400px;
}

.seats-vertical {
  display: flex;
  flex-direction: column;
  gap: 0.125rem;
}

.seats-vertical button {
  width: 2.25rem;
  height: 2.25rem;
  font-size: 0.75rem;
  font-weight: 600;
  border-radius: 0.375rem;
}

/* Aisle styling - simplified */
.aisle-vertical {
  width: 1rem;
  min-height: 300px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.aisle-vertical::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 2px;
  height: 200px;
  background: linear-gradient(to bottom, #d1d5db 0%, #9ca3af 50%, #d1d5db 100%);
  border-radius: 1px;
}

/* Grid layout for side-by-side levels */
.levels-container .grid {
  align-items: start;
}

.level-section {
  border: 1px solid #e5e7eb;
  border-radius: 0.75rem;
  padding: 1rem;
  background: #fafafa;
  height: fit-content;
}

/* Responsive adjustments */
@media (max-width: 1024px) {
  .levels-container .grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
}

@media (max-width: 768px) {
  .bus-container {
    padding: 0.5rem;
  }
  
  .seats-vertical button {
    width: 2rem;
    height: 2rem;
    font-size: 0.625rem;
  }
  
  .vertical-layout {
    gap: 0.5rem;
  }
  
  .seat-column {
    min-height: 320px;
  }
  
  .aisle-vertical {
    width: 0.75rem;
    min-height: 250px;
  }
  
  .aisle-vertical::before {
    height: 150px;
  }
}

@media (max-width: 640px) {
  .seats-vertical button {
    width: 1.75rem;
    height: 1.75rem;
    font-size: 0.5rem;
  }
  
  .vertical-layout {
    gap: 0.375rem;
  }
  
  .seat-column {
    min-height: 280px;
  }
  
  .aisle-vertical {
    width: 0.5rem;
    min-height: 200px;
  }
  
  .aisle-vertical::before {
    height: 120px;
    width: 1px;
  }
  
  .level-section {
    padding: 0.75rem;
  }
}

/* Seat hover effects */
.seats-vertical button:not(:disabled):hover {
  transform: scale(1.1);
  z-index: 10;
}

/* Animation for selected seats */
.seats-vertical button.selected {
  animation: pulse 1s ease-in-out;
}

@keyframes pulse {
  0%, 100% { transform: scale(1.05); }
  50% { transform: scale(1.15); }
}

/* Bus interior styling */
.bus-interior {
  background: linear-gradient(90deg, #f9fafb 0%, #ffffff 50%, #f9fafb 100%);
  border-radius: 0.5rem;
  padding: 1rem 0.5rem;
}
</style> 