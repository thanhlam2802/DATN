<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  selectedTrip: {
    type: Object,
    default: () => ({})
  },
  currentStep: {
    type: Number,
    default: 2 // Bước 2 = Chọn ghế (sau bước 1 = Tìm kiếm)
  }
})

const emit = defineEmits(['close', 'step-change', 'booking-complete'])

// Booking state
const bookingData = ref({
  selectedSeats: [],
  passengerInfo: {
    fullName: '',
    phoneNumber: '',
    notes: ''
  },
  voucherCode: '',
  paymentMethod: '',
  totalAmount: 0,
  discount: 0
})

// Seat layouts
const seatLayouts = {
  'shuttle-bus': {
    total: 16,
    rows: 4,
    seatsPerRow: 4,
    layout: 'single' // Single level
  },
  'sleeping-bus': {
    total: 36,
    rows: 6,
    seatsPerRow: 3,
    layout: 'double', // Double level (upper/lower)
    levels: ['Tầng trên', 'Tầng dưới']
  }
}

// Mock seat availability (in real app, this comes from API)
const seatAvailability = ref({
  // For shuttle bus (16 seats)
  'shuttle-bus': {
    available: [1, 2, 3, 5, 7, 8, 10, 12, 13, 15, 16], // Green seats
    occupied: [4, 6, 9, 11, 14], // Red seats  
    pending: [] // Yellow seats (being booked by others)
  },
  // For sleeping bus (36 seats)
  'sleeping-bus': {
    available: [1, 2, 3, 5, 7, 8, 10, 12, 13, 15, 16, 19, 20, 21, 23, 25, 26, 28, 30, 31, 33, 35, 36], // Green seats
    occupied: [4, 6, 9, 11, 14, 17, 18, 22, 24, 27, 29, 32, 34], // Red seats  
    pending: [] // Yellow seats (being booked by others)
  }
})

// Payment methods
const paymentMethods = [
  { id: 'credit-card', name: 'Thẻ tín dụng', icon: 'fas fa-credit-card' },
  { id: 'e-wallet', name: 'Ví điện tử', icon: 'fas fa-wallet' },
  { id: 'bank-transfer', name: 'Chuyển khoản', icon: 'fas fa-university' }
]

// Computed properties
const currentLayout = computed(() => {
  // Map activeTab to seat layout type
  let busType = 'shuttle-bus' // default
  
  if (props.selectedTrip.type) {
    busType = props.selectedTrip.type
  } else {
    // If no type, derive from activeTab naming convention
    // 'sleeping-bus' tab -> 'sleeping-bus' layout
    // 'shuttle-bus' tab -> 'shuttle-bus' layout
    busType = props.selectedTrip.activeTab || 'shuttle-bus'
  }
  
  console.log('Current bus type for layout:', busType, 'from trip:', props.selectedTrip)
  return seatLayouts[busType]
})

const totalSeats = computed(() => currentLayout.value.total)

const selectedSeatsCount = computed(() => bookingData.value.selectedSeats.length)

const basePrice = computed(() => {
  const price = props.selectedTrip.price || 0
  return typeof price === 'string' ? parseInt(price.replace(/[^\d]/g, '')) : price
})

const finalAmount = computed(() => {
  const total = basePrice.value * selectedSeatsCount.value
  const discountAmount = (total * bookingData.value.discount) / 100
  return total - discountAmount
})

// Seat selection functions
const getSeatStatus = (seatNumber) => {
  const busType = currentLayout.value.layout === 'single' ? 'shuttle-bus' : 'sleeping-bus'
  const availability = seatAvailability.value[busType]
  
  if (bookingData.value.selectedSeats.includes(seatNumber)) return 'selected'
  if (availability.available.includes(seatNumber)) return 'available'
  if (availability.occupied.includes(seatNumber)) return 'occupied'
  if (availability.pending.includes(seatNumber)) return 'pending'
  return 'available'
}

const getSeatClass = (seatNumber) => {
  const status = getSeatStatus(seatNumber)
  const classes = {
    available: 'bg-green-100 border-green-300 text-green-700 hover:bg-green-200',
    occupied: 'bg-red-100 border-red-300 text-red-700 cursor-not-allowed',
    pending: 'bg-yellow-100 border-yellow-300 text-yellow-700 cursor-not-allowed',
    selected: 'bg-indigo-600 border-indigo-700 text-white'
  }
  return `${classes[status]} border-2 rounded-lg w-12 h-12 flex items-center justify-center text-sm font-medium transition-all duration-200`
}

const toggleSeat = (seatNumber) => {
  const status = getSeatStatus(seatNumber)
  if (status === 'occupied' || status === 'pending') return
  
  const index = bookingData.value.selectedSeats.indexOf(seatNumber)
  if (index > -1) {
    bookingData.value.selectedSeats.splice(index, 1)
  } else {
    bookingData.value.selectedSeats.push(seatNumber)
  }
  
  updateTotalAmount()
}

// Form validation functions
const validatePassengerInfo = () => {
  const { fullName, phoneNumber } = bookingData.value.passengerInfo
  return fullName.trim() && phoneNumber.trim() && phoneNumber.length >= 10
}

const validatePayment = () => {
  return bookingData.value.paymentMethod && selectedSeatsCount.value > 0
}

// Voucher functions
const applyVoucher = () => {
  // Mock voucher validation (in real app, call API)
  const vouchers = {
    'GIAM10': 10,
    'GIAM20': 20,
    'NEWUSER': 15
  }
  
  const code = bookingData.value.voucherCode.toUpperCase()
  if (vouchers[code]) {
    bookingData.value.discount = vouchers[code]
    updateTotalAmount()
    console.log(`Áp dụng mã giảm giá ${code}: ${vouchers[code]}%`)
  } else {
    bookingData.value.discount = 0
    console.log('Mã voucher không hợp lệ')
  }
}

const removeVoucher = () => {
  bookingData.value.voucherCode = ''
  bookingData.value.discount = 0
  updateTotalAmount()
}

// Price calculation functions
const updateTotalAmount = () => {
  bookingData.value.totalAmount = finalAmount.value
}

// Navigation functions
const goToNextStep = () => {
  if (props.currentStep < 5) {
    emit('step-change', props.currentStep + 1)
  }
}

const goToPrevStep = () => {
  if (props.currentStep === 2) {
    // Go back to search results
    emit('step-change', 1)
  } else if (props.currentStep > 1) {
    emit('step-change', props.currentStep - 1)
  }
}

const canProceedToNext = () => {
  switch (props.currentStep) {
    case 2: // Chọn ghế
      return selectedSeatsCount.value > 0
    case 3: // Thông tin
      return validatePassengerInfo()
    case 4: // Thanh toán
      return validatePayment()
    default:
      return true
  }
}

// Booking completion
const completeBooking = () => {
  const booking = {
    trip: props.selectedTrip,
    seats: bookingData.value.selectedSeats,
    passenger: bookingData.value.passengerInfo,
    payment: {
      method: bookingData.value.paymentMethod,
      amount: bookingData.value.totalAmount,
      voucher: bookingData.value.voucherCode,
      discount: bookingData.value.discount
    },
    bookingTime: new Date().toISOString()
  }
  
  console.log('Booking completed:', booking)
  emit('booking-complete', booking)
}

// Close modal
const closeModal = () => {
  emit('close')
}
</script>

<template>
  <div v-if="show" class="flex flex-col min-h-full">
    
    <!-- Booking Content -->
    <div class="booking-content flex-1 overflow-y-auto px-4 md:px-6 py-4" style="max-height: calc(90vh - 250px);">
    
    <!-- Step 2: Seat Selection -->
    <div v-if="currentStep === 2" class="seat-selection">
      <div class="mb-4">
        <h3 class="text-lg font-semibold text-gray-900 mb-2">Chọn ghế</h3>
        <p class="text-sm text-gray-600">
          {{ selectedTrip.company }} - {{ selectedTrip.busType }}
        </p>
      </div>

      <!-- Seat Legend -->
      <div class="flex items-center justify-center space-x-4 mb-4 text-sm">
        <div class="flex items-center">
          <div class="w-4 h-4 bg-green-100 border-2 border-green-300 rounded mr-2"></div>
          <span>Trống</span>
        </div>
        <div class="flex items-center">
          <div class="w-4 h-4 bg-red-100 border-2 border-red-300 rounded mr-2"></div>
          <span>Đã đặt</span>
        </div>
        <div class="flex items-center">
          <div class="w-4 h-4 bg-yellow-100 border-2 border-yellow-300 rounded mr-2"></div>
          <span>Đang đặt</span>
        </div>
        <div class="flex items-center">
          <div class="w-4 h-4 bg-indigo-600 border-2 border-indigo-700 rounded mr-2"></div>
          <span>Đã chọn</span>
        </div>
      </div>

      <!-- Seat Layout & Summary - 2 Columns -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 max-w-5xl mx-auto">
        
        <!-- Left Column: Seat Layout -->
        <div class="flex flex-col items-center">
          <!-- Driver section -->
          <div class="text-center mb-4">
            <div class="inline-flex items-center justify-center w-16 h-8 bg-gray-200 rounded-t-lg">
              <i class="fas fa-steering-wheel text-gray-600"></i>
            </div>
            <p class="text-xs text-gray-500 mt-1">Tài xế</p>
          </div>

          <!-- Shuttle Bus Layout (16 seats) -->
          <div v-if="currentLayout.layout === 'single'" class="space-y-3">
            <div v-for="row in currentLayout.rows" :key="row" class="flex justify-center space-x-2">
              <button
                v-for="seat in currentLayout.seatsPerRow"
                :key="(row - 1) * currentLayout.seatsPerRow + seat"
                @click="toggleSeat((row - 1) * currentLayout.seatsPerRow + seat)"
                :class="getSeatClass((row - 1) * currentLayout.seatsPerRow + seat)"
              >
                {{ (row - 1) * currentLayout.seatsPerRow + seat }}
              </button>
            </div>
          </div>

          <!-- Sleeping Bus Layout (36 seats - 2 levels side by side) -->
          <div v-else-if="currentLayout.layout === 'double'" class="flex justify-center space-x-6">
            <div v-for="(level, levelIndex) in currentLayout.levels" :key="level" class="border rounded-lg p-3">
              <h4 class="text-sm font-medium text-gray-700 mb-2 text-center">{{ level }}</h4>
              <div class="space-y-2">
                <div v-for="row in currentLayout.rows" :key="row" class="flex justify-center space-x-2">
                  <button
                    v-for="seat in currentLayout.seatsPerRow"
                    :key="levelIndex * 18 + (row - 1) * currentLayout.seatsPerRow + seat"
                    @click="toggleSeat(levelIndex * 18 + (row - 1) * currentLayout.seatsPerRow + seat)"
                    :class="getSeatClass(levelIndex * 18 + (row - 1) * currentLayout.seatsPerRow + seat)"
                  >
                    {{ levelIndex * 18 + (row - 1) * currentLayout.seatsPerRow + seat }}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Right Column: Selected Seats Summary -->
        <div class="flex flex-col justify-start">
          <!-- Always show summary area, even when no seats selected -->
          <div class="p-4 bg-gray-50 rounded-lg border-2 border-dashed border-gray-200">
            <h4 class="font-medium text-gray-900 mb-3">Thông tin đặt chỗ</h4>
            
            <!-- When no seats selected -->
            <div v-if="selectedSeatsCount === 0" class="text-center text-gray-500 py-8">
              <i class="fas fa-chair text-3xl mb-3 text-gray-300"></i>
              <p class="text-sm">Vui lòng chọn ghế</p>
            </div>
            
            <!-- When seats are selected -->
            <div v-else class="space-y-4">
              <!-- Selected seats -->
              <div>
                <h5 class="text-sm font-medium text-gray-700 mb-2">Ghế đã chọn:</h5>
                <div class="flex flex-wrap gap-2">
                  <span v-for="seat in bookingData.selectedSeats" :key="seat" 
                        class="px-3 py-1 bg-indigo-100 text-indigo-800 rounded-full text-sm font-medium">
                    Ghế {{ seat }}
                  </span>
                </div>
              </div>
              
              <!-- Trip info -->
              <div class="pt-3 border-t border-gray-200">
                <h5 class="text-sm font-medium text-gray-700 mb-2">Thông tin chuyến:</h5>
                <div class="text-sm text-gray-600 space-y-1">
                  <p>{{ selectedTrip.company }}</p>
                  <p>{{ selectedTrip.busType }}</p>
                  <p class="text-xs text-indigo-600 font-medium">
                    {{ currentLayout.layout === 'single' ? 'Xe Trung chuyển' : 'Xe Giường nằm' }} 
                    ({{ currentLayout.total }} ghế)
                  </p>
                  <p>{{ selectedTrip.route?.from }} → {{ selectedTrip.route?.to }}</p>
                </div>
              </div>
              
              <!-- Price calculation -->
              <div class="pt-3 border-t border-gray-200">
                <div class="flex justify-between items-center mb-2">
                  <span class="text-sm text-gray-600">{{ selectedSeatsCount }} ghế × {{ basePrice.toLocaleString() }}đ</span>
                  <span class="text-sm text-gray-900">{{ (basePrice * selectedSeatsCount).toLocaleString() }}đ</span>
                </div>
                <div class="flex justify-between items-center pt-2 border-t">
                  <span class="font-semibold text-gray-900">Tổng cộng:</span>
                  <span class="font-bold text-lg text-indigo-600">{{ (basePrice * selectedSeatsCount).toLocaleString() }}đ</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
      </div>
    </div>

    <!-- Step 3: Passenger Information -->
    <div v-if="currentStep === 3" class="passenger-info">
      <div class="mb-4">
        <h3 class="text-lg font-semibold text-gray-900 mb-2">Thông tin hành khách</h3>
        <p class="text-sm text-gray-600">Vui lòng điền thông tin chính xác</p>
      </div>

      <div class="max-w-md mx-auto space-y-3">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Họ và tên <span class="text-red-500">*</span>
          </label>
          <input
            v-model="bookingData.passengerInfo.fullName"
            type="text"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
            placeholder="Nhập họ và tên"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Số điện thoại <span class="text-red-500">*</span>
          </label>
          <input
            v-model="bookingData.passengerInfo.phoneNumber"
            type="tel"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
            placeholder="Nhập số điện thoại"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Ghi chú (tùy chọn)
          </label>
          <textarea
            v-model="bookingData.passengerInfo.notes"
            rows="3"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
            placeholder="Ghi chú thêm..."
          ></textarea>
        </div>
      </div>
    </div>

    <!-- Step 4: Payment -->
    <div v-if="currentStep === 4" class="payment-section">
      <div class="mb-4">
        <h3 class="text-lg font-semibold text-gray-900 mb-2">Thanh toán</h3>
        <p class="text-sm text-gray-600">Chọn phương thức thanh toán</p>
      </div>

      <div class="max-w-md mx-auto space-y-4">
        <!-- Voucher Section -->
        <div class="border rounded-lg p-4">
          <h4 class="font-medium text-gray-900 mb-3">Mã giảm giá</h4>
          <div class="flex space-x-2">
            <input
              v-model="bookingData.voucherCode"
              type="text"
              class="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
              placeholder="Nhập mã voucher"
            />
            <button
              @click="applyVoucher"
              class="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors"
            >
              Áp dụng
            </button>
          </div>
          <div v-if="bookingData.discount > 0" class="mt-2 flex items-center justify-between text-sm">
            <span class="text-green-600">Giảm {{ bookingData.discount }}%</span>
            <button @click="removeVoucher" class="text-red-500 hover:text-red-700">
              <i class="fas fa-times"></i>
            </button>
          </div>
        </div>

        <!-- Payment Methods -->
        <div class="space-y-3">
          <h4 class="font-medium text-gray-900">Phương thức thanh toán</h4>
          <div v-for="method in paymentMethods" :key="method.id" class="border rounded-lg p-3">
            <label class="flex items-center cursor-pointer">
              <input
                v-model="bookingData.paymentMethod"
                :value="method.id"
                type="radio"
                class="w-4 h-4 text-indigo-600 border-gray-300 focus:ring-indigo-500"
              />
              <i :class="method.icon" class="mx-3 text-gray-500"></i>
              <span class="text-sm font-medium">{{ method.name }}</span>
            </label>
          </div>
        </div>

        <!-- Price Summary -->
        <div class="border-t p-4 space-y-2">
          <div class="flex justify-between text-sm">
            <span>Giá vé ({{ selectedSeatsCount }} ghế)</span>
            <span>{{ (basePrice * selectedSeatsCount).toLocaleString() }}đ</span>
          </div>
          <div v-if="bookingData.discount > 0" class="flex justify-between text-sm text-green-600">
            <span>Giảm giá ({{ bookingData.discount }}%)</span>
            <span>-{{ ((basePrice * selectedSeatsCount * bookingData.discount) / 100).toLocaleString() }}đ</span>
          </div>
          <div class="flex justify-between font-semibold text-lg border-t pt-2">
            <span>Tổng cộng</span>
            <span class="text-indigo-600">{{ finalAmount.toLocaleString() }}đ</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Step 5: Confirmation -->
    <div v-if="currentStep === 5" class="confirmation">
      <div class="mb-4 text-center">
        <div class="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-3">
          <i class="fas fa-check text-green-600 text-lg"></i>
        </div>
        <h3 class="text-lg font-semibold text-gray-900 mb-2">Xác nhận đặt vé</h3>
        <p class="text-sm text-gray-600">Vui lòng kiểm tra thông tin trước khi hoàn tất</p>
      </div>

      <div class="max-w-md mx-auto space-y-3">
        <!-- Trip Info -->
        <div class="border rounded-lg p-3">
          <h4 class="font-medium text-gray-900 mb-2">Thông tin chuyến xe</h4>
          <div class="text-sm space-y-1">
            <p>{{ selectedTrip.company }} - {{ selectedTrip.busType }}</p>
            <p>{{ selectedTrip.route }}</p>
            <p>{{ selectedTrip.schedule?.departure }} - {{ selectedTrip.schedule?.arrival }}</p>
          </div>
        </div>

        <!-- Seats -->
        <div class="border rounded-lg p-3">
          <h4 class="font-medium text-gray-900 mb-2">Ghế đã chọn</h4>
          <div class="flex flex-wrap gap-2">
            <span v-for="seat in bookingData.selectedSeats" :key="seat" 
                  class="px-2 py-1 bg-indigo-100 text-indigo-800 rounded text-sm">
              Ghế {{ seat }}
            </span>
          </div>
        </div>

        <!-- Passenger -->
        <div class="border rounded-lg p-3">
          <h4 class="font-medium text-gray-900 mb-2">Thông tin hành khách</h4>
          <div class="text-sm space-y-1">
            <p>{{ bookingData.passengerInfo.fullName }}</p>
            <p>{{ bookingData.passengerInfo.phoneNumber }}</p>
            <p v-if="bookingData.passengerInfo.notes">{{ bookingData.passengerInfo.notes }}</p>
          </div>
        </div>

        <!-- Payment -->
        <div class="border rounded-lg p-3">
          <h4 class="font-medium text-gray-900 mb-2">Thanh toán</h4>
          <div class="text-sm space-y-1">
            <p>{{ paymentMethods.find(m => m.id === bookingData.paymentMethod)?.name }}</p>
            <p class="font-semibold text-indigo-600">{{ finalAmount.toLocaleString() }}đ</p>
          </div>
        </div>
      </div>
    </div>
    
    </div> <!-- Close booking-content -->

    <!-- Action Buttons -->
    <div class="flex justify-between items-center p-4 md:p-6 border-t bg-white sticky bottom-0 z-10 shadow-lg">
      <button
        v-if="currentStep >= 2"
        @click="goToPrevStep"
        class="flex items-center px-4 py-2 text-gray-600 hover:text-gray-800 transition-colors"
      >
        <i class="fas fa-chevron-left mr-2"></i>
        {{ currentStep === 2 ? 'Quay lại tìm kiếm' : 'Quay lại' }}
      </button>
      <div v-else></div>

      <button
        v-if="currentStep < 5"
        @click="goToNextStep"
        :disabled="!canProceedToNext()"
        :class="canProceedToNext() 
          ? 'bg-indigo-600 hover:bg-indigo-700 text-white' 
          : 'bg-gray-300 text-gray-500 cursor-not-allowed'"
        class="px-6 py-2 rounded-lg font-medium transition-colors"
      >
        Tiếp tục
        <i class="fas fa-chevron-right ml-2"></i>
      </button>
      <button
        v-else
        @click="completeBooking"
        class="px-6 py-2 bg-green-500 hover:bg-green-600 text-white rounded-lg font-medium transition-colors"
      >
        Hoàn tất đặt vé
      </button>
    </div>

  </div>
</template>

<style scoped>
.booking-content {
  min-height: 400px;
}

/* Seat hover effects */
.seat-button:hover:not(.cursor-not-allowed) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* Smooth transitions */
.booking-content > div {
  animation: slideInFromRight 0.3s ease-out;
}

@keyframes slideInFromRight {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* Better spacing for sections */
.booking-content .seat-selection,
.booking-content .passenger-info,
.booking-content .payment-section,
.booking-content .confirmation {
  @apply space-y-4;
}

/* Responsive padding for mobile */
@media (max-width: 768px) {
  .booking-content {
    @apply p-3;
  }
}
</style> 