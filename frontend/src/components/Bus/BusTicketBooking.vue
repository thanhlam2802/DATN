<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router' // ✅ THÊM để redirect
import { SeatAPI, BookingAPI } from '@/api/busAPI_Client'
import { CartAPI, CustomerAPI } from '@/api/busAPI_Client/busbookingApi'
import BusSeatSelection from './BusSeatSelection.vue'
import { getUserIdFromToken } from '@/services/TokenService' // ✅ THÊM

// DEV MODE: No auth required for booking

const router = useRouter() // ✅ THÊM router instance

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
    default: 2 // Bước 2 = Chọn ghế → Bước 3 = Thông tin → Redirect đến PaymentView
  }
})

const emit = defineEmits(['close', 'step-change', 'booking-complete'])

// Loading states
const loadingSeatData = ref(false)
const seatDataError = ref('')

// Component lifecycle tracking
const isMounted = ref(false)

// Real seat data from API
const seatOccupancy = ref(null)
const realSeatLayout = ref(null)

// Booking state
const bookingData = ref({
  selectedSeats: [],
  selectedSeatNumbers: [], // ✅ THÊM để lưu seat numbers
  passengerInfo: {
    fullName: '',
    phoneNumber: '',
    email: '',
    notes: ''
  },
  voucherCode: '',
  // ✅ paymentMethod đã bị loại bỏ vì không còn Step 4 Payment
  totalAmount: 0,
  discount: 0
})

// Form validation states
const validationErrors = ref([])
const showValidationErrors = ref(false)
const isValidatingForm = ref(false)

// Booking action states
const bookingAction = ref('') // 'cart' or 'direct'
const isProcessingBooking = ref(false)

// ✅ Payment methods đã bị loại bỏ vì không còn Step 4 Payment
// const paymentMethods = [...]

// Mount/unmount tracking
onMounted(() => {
  isMounted.value = true
})

onUnmounted(() => {
  isMounted.value = false
})

// Load real seat data when component mounts or trip changes
const loadSeatData = async () => {
  if (!isMounted.value || !props.selectedTrip?.busSlotId) {
    return
  }

  try {
    loadingSeatData.value = true
    seatDataError.value = ''

    // Get real seat occupancy from API
    const occupancy = await SeatAPI.getSeatOccupancy(props.selectedTrip.busSlotId)
    
    // Check if still mounted before updating state
    if (isMounted.value) {
      seatOccupancy.value = occupancy
      // Generate dynamic layout based on total seats
      const layout = SeatAPI.generateSeatLayout(occupancy.totalSeats)
      realSeatLayout.value = layout
    }

  } catch (error) {
    console.error('❌ Error loading seat data:', error)
    
    if (isMounted.value) {
      seatDataError.value = 'Không thể tải thông tin ghế. Sử dụng dữ liệu mẫu.'
      
      // Fallback to mock data
      const mockOccupancy = SeatAPI.generateMockSeatMap(props.selectedTrip)
      seatOccupancy.value = {
        busSlotId: props.selectedTrip.busSlotId,
        totalSeats: props.selectedTrip.totalSeats || 16,
        availableSeats: props.selectedTrip.availableSeats || 10,
        occupiedSeats: (props.selectedTrip.totalSeats || 16) - (props.selectedTrip.availableSeats || 10),
        pendingSeats: 0,
        seatMap: mockOccupancy
      }
      
      realSeatLayout.value = SeatAPI.generateSeatLayout(props.selectedTrip.totalSeats || 16)
    }
  } finally {
    if (isMounted.value) {
      loadingSeatData.value = false
    }
  }
}

// Watch for trip changes
watch(() => props.selectedTrip, async (newTrip) => {
  if (newTrip?.busSlotId && isMounted.value) {
    await loadSeatData()
  }
}, { immediate: true })

// Watch when component becomes visible
watch(() => props.show, async (isVisible) => {
  if (isVisible && props.selectedTrip?.busSlotId && isMounted.value) {
    await loadSeatData()
  }
})

// Computed properties using real data
const currentLayout = computed(() => {
  if (realSeatLayout.value) {
    return realSeatLayout.value
  }
  
  // Fallback layout
  const totalSeats = props.selectedTrip?.totalSeats || 16
  return SeatAPI.generateSeatLayout(totalSeats)
})

const totalSeats = computed(() => {
  return seatOccupancy.value?.totalSeats || currentLayout.value.total || 16
})

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

// Real seat status using API data
const getSeatStatus = (seatNumber) => {
  if (!seatOccupancy.value) {
    return 'available'
  }

  return SeatAPI.getSeatStatus(
    seatNumber, 
    seatOccupancy.value.seatMap, 
    bookingData.value.selectedSeats
  )
}

// Real seat styling using API helper
const getSeatClass = (seatNumber) => {
  const status = getSeatStatus(seatNumber)
  return SeatAPI.getSeatDisplayClass(status)
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
  isValidatingForm.value = true
  
  const customerInfo = CustomerAPI.createCustomerFromForm(bookingData.value.passengerInfo)
  const validation = CustomerAPI.validateCustomerInfo(customerInfo)
  
  validationErrors.value = validation.errors
  showValidationErrors.value = !validation.valid
  
  isValidatingForm.value = false
  return validation.valid
}

// Real-time validation watcher
watch(() => bookingData.value.passengerInfo, (newInfo) => {
  if (showValidationErrors.value) {
    validatePassengerInfo()
  }
}, { deep: true })

// ✅ validatePayment đã bị loại bỏ vì không còn Step 4 Payment

// Enhanced voucher functions using API
const applyVoucher = async () => {
  if (!isMounted.value || !bookingData.value.voucherCode.trim()) {
    return
  }

  try {
    const result = await BookingAPI.applyVoucher(
      bookingData.value.voucherCode, 
      basePrice.value * selectedSeatsCount.value
    )
    
    if (isMounted.value) {
      if (result.valid) {
        bookingData.value.discount = result.discountPercentage
        updateTotalAmount()
        // Show success message
        alert(`✅ ${result.message}`)
      } else {
        bookingData.value.discount = 0
        alert(`❌ ${result.message}`)
      }
    }
  } catch (error) {
    console.error('❌ Error applying voucher:', error)
    
    if (isMounted.value) {
      // Fallback to mock voucher validation
      const mockResult = BookingAPI.getMockVoucherDiscount(bookingData.value.voucherCode)
      
      if (mockResult.valid) {
        bookingData.value.discount = mockResult.discountPercentage
        updateTotalAmount()
        alert(`✅ ${mockResult.message}`)
      } else {
        bookingData.value.discount = 0
        alert(`❌ ${mockResult.message}`)
      }
    }
  }
}

const removeVoucher = () => {
  bookingData.value.voucherCode = ''
  bookingData.value.discount = 0
  updateTotalAmount()
}

// Handle seat selection from BusSeatSelection component
const handleSeatSelectionChange = (selectedSeats) => {
  bookingData.value.selectedSeats = selectedSeats.map(seat => seat.id)
  bookingData.value.selectedSeatNumbers = selectedSeats.map(seat => seat.seatNumber) // ✅ THÊM
  
  // 🐛 DEBUG: Log seat selection
  console.log('🪑 Seat Selection Changed:', {
    selectedSeats,
    selectedSeatIds: bookingData.value.selectedSeats,
    selectedSeatNumbers: bookingData.value.selectedSeatNumbers
  })
  
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
    case 2: // Chọn ghế -> Thông tin 
      return selectedSeatsCount.value > 0
    case 3: // Thông tin -> ĐẶT VÉ NGAY (redirect đến PaymentView)
      return false // Step 3 chỉ dùng "Đặt vé ngay", không có "Tiếp tục"
    default:
      return false // Chỉ có Step 2 và 3, không có step khác
  }
}

// Add to cart function
const addToCart = async () => {
  if (!isMounted.value || isProcessingBooking.value) return
  
  try {
    isProcessingBooking.value = true
    bookingAction.value = 'cart'
    
    // Validate form first
    if (!validatePassengerInfo()) {
      alert('❌ Vui lòng kiểm tra lại thông tin hành khách')
      return
    }

    // ✅ FIX: Get userId from token instead of hardcode
    const userId = getUserIdFromToken()
    if (!userId) {
      alert('❌ Vui lòng đăng nhập để đặt vé')
      return
    }

    // Create cart request
    const cartRequest = {
      itemId: props.selectedTrip.busSlotId,
      itemType: 'BUS',
      selectedSeatIds: bookingData.value.selectedSeats,
      totalPrice: finalAmount.value,
      notes: bookingData.value.passengerInfo.notes,
      passengerName: CustomerAPI.normalizeName(bookingData.value.passengerInfo.fullName),
      passengerPhone: bookingData.value.passengerInfo.phoneNumber.replace(/\s/g, ''),
      passengerEmail: bookingData.value.passengerInfo.email || undefined
    }

    // Validate cart request
    const validation = CartAPI.validateCartRequest(cartRequest)
    if (!validation.valid) {
      alert(`❌ Dữ liệu không hợp lệ:\n${validation.errors.join('\n')}`)
      return
    }

    // ✅ FIX: Use dynamic userId from token
    let cartResponse
    try {
      cartResponse = await CartAPI.getCart(userId)
    } catch (error) {
      // If cart doesn't exist, create new one
      cartResponse = await CartAPI.createCart(userId)
    }

    // Add bus to cart
    const result = await CartAPI.addBusToCart(cartResponse.data.id, cartRequest)
    
    if (isMounted.value) {
      // ✅ THÊM: Cập nhật localStorage với cart ID mới
      localStorage.setItem('activeCartId', result.data.id)
      console.log('✅ Updated cart ID:', result.data.id)
      
      alert('✅ Đã thêm vé xe vào giỏ hàng thành công!')
      
      emit('booking-complete', {
        type: 'cart',
        cartId: result.data.id,
        bookingId: result.data.busBookings[result.data.busBookings.length - 1]?.id,
        trip: props.selectedTrip,
        passenger: bookingData.value.passengerInfo,
        totalAmount: finalAmount.value
      })
      
      // ✅ THÊM: Redirect đến giỏ hàng với ID đúng
      setTimeout(() => {
        window.location.href = `/orders/${result.data.id}`
      }, 1000)
    }

  } catch (error) {
    console.error('❌ Error adding to cart:', error)
    if (isMounted.value) {
      alert(`❌ ${error.message}`)
    }
  } finally {
    if (isMounted.value) {
      isProcessingBooking.value = false
      bookingAction.value = ''
    }
  }
}

// Direct booking function
const bookDirectly = async () => {
  if (!isMounted.value || isProcessingBooking.value) return
  
  try {
    isProcessingBooking.value = true
    bookingAction.value = 'direct'
    
    // Validate form first
    if (!validatePassengerInfo()) {
      alert('❌ Vui lòng kiểm tra lại thông tin hành khách')
      return
    }

    // Create booking request
    const bookingRequest = {
      busSlotId: props.selectedTrip.busSlotId,
      selectedSeatNumbers: bookingData.value.selectedSeatNumbers, // ✅ SỬA từ selectedSeatIds 
      customerName: CustomerAPI.normalizeName(bookingData.value.passengerInfo.fullName),
      phone: bookingData.value.passengerInfo.phoneNumber.replace(/\s/g, ''),
      email: bookingData.value.passengerInfo.email || undefined,
      notes: bookingData.value.passengerInfo.notes,
      userId: getUserIdFromToken() || 1 // ✅ THÊM userId với fallback
    }

    // 🐛 DEBUG: Log payload để kiểm tra
    console.log('📋 Direct Booking Payload:', {
      busSlotId: bookingRequest.busSlotId,
      selectedSeatNumbers: bookingRequest.selectedSeatNumbers,
      customerName: bookingRequest.customerName,
      phone: bookingRequest.phone,
      email: bookingRequest.email,
      userId: bookingRequest.userId,
      selectedSeatsFromBookingData: bookingData.value.selectedSeats,
      selectedSeatNumbersFromBookingData: bookingData.value.selectedSeatNumbers
    })

    // Validate direct booking request
    const validation = BookingAPI.validateDirectBookingRequest(bookingRequest)
    if (!validation.valid) {
      alert(`❌ Dữ liệu không hợp lệ:\n${validation.errors.join('\n')}`)
      return
    }

    // Create direct booking
    const result = await BookingAPI.createDirectBooking(bookingRequest)
    
    if (isMounted.value) {
      console.log('✅ Đặt vé thành công! OrderId:', result.data)
      
      // Emit booking complete event
      emit('booking-complete', {
        type: 'direct',
        orderId: result.data, // Backend trả về orderId
        trip: props.selectedTrip,
        passenger: bookingData.value.passengerInfo,
        totalAmount: finalAmount.value
      })
      
      // Redirect đến PaymentView với countdown timer
      await router.push(`/payment/${result.data}`)
    }

  } catch (error) {
    console.error('❌ Error creating direct booking:', error)
    if (isMounted.value) {
      alert(`❌ ${error.message}`)
    }
  } finally {
    if (isMounted.value) {
      isProcessingBooking.value = false
      bookingAction.value = ''
    }
  }
}

// ✅ completeBooking đã bị loại bỏ vì không còn Step 5

// Close modal
const closeModal = () => {
  emit('close')
}

// Utility function to generate seat numbers for display
const generateSeatNumbers = () => {
  if (!currentLayout.value) return []
  
  const seats = []
  const totalSeats = seatOccupancy.value?.totalSeats || currentLayout.value.total
  
  for (let i = 1; i <= totalSeats; i++) {
    seats.push(i)
  }
  
  return seats
}

// Get seats for specific level (for double-decker buses)
const getSeatsForLevel = (levelIndex) => {
  const seats = generateSeatNumbers()
  const seatsPerLevel = Math.ceil(seats.length / 2)
  const startIndex = levelIndex * seatsPerLevel
  return seats.slice(startIndex, startIndex + seatsPerLevel)
}
</script>

<template>
  <div class="bus-ticket-booking-wrapper">
    <div v-if="show" class="flex flex-col min-h-full">
    
    <!-- Booking Content -->
    <div class="booking-content flex-1 overflow-y-auto px-4 md:px-6 py-4" style="max-height: calc(90vh - 250px);">
    
    <!-- Step 2: Seat Selection -->
    <div v-if="currentStep === 2" class="seat-selection">
      <div class="mb-4">
        <div class="flex items-center justify-between mb-2">
          <h3 class="text-lg font-semibold text-gray-900">Chọn ghế</h3>
          <div class="text-sm text-gray-600">
            {{ selectedTrip.company }} - {{ selectedTrip.busType }}
          </div>
        </div>
      </div>

      <!-- Use New Seat Selection Component -->
      <BusSeatSelection 
        :busSlot="selectedTrip"
        :selectedSeats="bookingData.selectedSeats"
        :maxSeats="10"
        @selection-change="handleSeatSelectionChange"
      />
        
      </div>
    </div>

    <!-- Step 3: Passenger Information -->
    <div v-if="currentStep === 3" class="passenger-info">
      <div class="mb-4">
        <h3 class="text-lg font-semibold text-gray-900 mb-2">Thông tin hành khách</h3>
        <p class="text-sm text-gray-600">Vui lòng điền thông tin chính xác</p>
      </div>

      <!-- Validation Errors -->
      <div v-if="showValidationErrors && validationErrors.length > 0" 
           class="max-w-md mx-auto mb-4 p-3 bg-red-50 border border-red-200 rounded-lg">
        <div class="flex items-center mb-2">
          <i class="fas fa-exclamation-triangle text-red-500 mr-2"></i>
          <span class="text-sm font-medium text-red-700">Vui lòng kiểm tra lại thông tin:</span>
        </div>
        <ul class="text-sm text-red-600 space-y-1">
          <li v-for="error in validationErrors" :key="error">• {{ error }}</li>
        </ul>
      </div>

      <div class="max-w-md mx-auto space-y-3">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Họ và tên <span class="text-red-500">*</span>
          </label>
          <input
            v-model="bookingData.passengerInfo.fullName"
            type="text"
            :class="[
              'w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500',
              showValidationErrors && validationErrors.some(e => e.includes('Họ và tên')) 
                ? 'border-red-300 bg-red-50' 
                : 'border-gray-300'
            ]"
            placeholder="Nhập họ và tên đầy đủ"
            @blur="validatePassengerInfo"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Số điện thoại <span class="text-red-500">*</span>
          </label>
          <input
            v-model="bookingData.passengerInfo.phoneNumber"
            type="tel"
            :class="[
              'w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500',
              showValidationErrors && validationErrors.some(e => e.includes('điện thoại')) 
                ? 'border-red-300 bg-red-50' 
                : 'border-gray-300'
            ]"
            placeholder="0987654321"
            @blur="validatePassengerInfo"
          />
          <p class="text-xs text-gray-500 mt-1">Định dạng: 10-11 chữ số</p>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Email (tùy chọn)
          </label>
          <input
            v-model="bookingData.passengerInfo.email"
            type="email"
            :class="[
              'w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500',
              showValidationErrors && validationErrors.some(e => e.includes('Email')) 
                ? 'border-red-300 bg-red-50' 
                : 'border-gray-300'
            ]"
            placeholder="example@email.com"
            @blur="validatePassengerInfo"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Ghi chú (tùy chọn)
          </label>
          <textarea
            v-model="bookingData.passengerInfo.notes"
            rows="3"
            :class="[
              'w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500',
              showValidationErrors && validationErrors.some(e => e.includes('Ghi chú')) 
                ? 'border-red-300 bg-red-50' 
                : 'border-gray-300'
            ]"
            placeholder="Ghi chú thêm..."
            maxlength="500"
          ></textarea>
          <p class="text-xs text-gray-500 mt-1">Tối đa 500 ký tự</p>
        </div>

        <!-- Customer Summary (when valid) -->
        <div v-if="!showValidationErrors && bookingData.passengerInfo.fullName && bookingData.passengerInfo.phoneNumber" 
             class="p-3 bg-green-50 border border-green-200 rounded-lg">
          <div class="flex items-center">
            <i class="fas fa-check-circle text-green-500 mr-2"></i>
            <span class="text-sm font-medium text-green-700">Thông tin hợp lệ</span>
          </div>
          <p class="text-sm text-green-600 mt-1">
            {{ CustomerAPI.getCustomerSummary(CustomerAPI.createCustomerFromForm(bookingData.passengerInfo)) }}
          </p>
        </div>

        <!-- Information completed message for Step 3 -->
        <div v-if="!showValidationErrors || validationErrors.length === 0" class="pt-4 border-t">
          <div class="text-center">
            <div class="inline-flex items-center text-green-600 mb-2">
              <i class="fas fa-check-circle mr-2"></i>
              <span class="text-sm font-medium">Thông tin đã hoàn tất</span>
            </div>
            <p class="text-xs text-gray-500">
              Sử dụng các nút ở cuối trang để tiếp tục hoặc đặt vé ngay
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- ✅ Step 4 & 5 đã bị ẩn vì redirect đến PaymentView sau Step 3 -->
    <div v-if="false" class="payment-section">
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
    <div class="p-4 md:p-6 border-t bg-white sticky bottom-0 z-10 shadow-lg">
      <!-- Navigation Row -->
      <div class="flex justify-between items-center">
        <button
          v-if="currentStep >= 2"
          @click="goToPrevStep"
          class="flex items-center px-4 py-2 text-gray-600 hover:text-gray-800 transition-colors"
        >
          <i class="fas fa-chevron-left mr-2"></i>
          {{ currentStep === 2 ? 'Quay lại tìm kiếm' : 'Quay lại' }}
        </button>
        <div v-else></div>

        <!-- Direct Booking Button for Step 3 - same row as navigation -->
        <button
          v-if="currentStep === 3 && (!showValidationErrors || validationErrors.length === 0)"
          @click="bookDirectly"
          :disabled="isProcessingBooking"
          :class="[
            'px-6 py-2 rounded-lg font-medium transition-colors flex items-center justify-center',
            isProcessingBooking && bookingAction === 'direct'
              ? 'bg-green-400 text-white cursor-not-allowed'
              : 'bg-green-600 hover:bg-green-700 text-white'
          ]"
        >
          <i v-if="isProcessingBooking && bookingAction === 'direct'" 
             class="fas fa-spinner fa-spin mr-2"></i>
          <i v-else class="fas fa-bolt mr-2"></i>
          {{ isProcessingBooking && bookingAction === 'direct' ? 'Đang đặt...' : 'Đặt vé ngay' }}
        </button>
        <button
          v-else-if="currentStep === 2"
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
        <div v-else></div>
      </div>

      <!-- Add to Cart Button - Temporarily Hidden -->
      <!-- 
      <button
        @click="addToCart"
        :disabled="isProcessingBooking"
        :class="[
          'px-4 py-3 rounded-lg font-medium transition-colors flex items-center justify-center',
          isProcessingBooking && bookingAction === 'cart'
            ? 'bg-indigo-400 text-white cursor-not-allowed'
            : 'bg-indigo-600 hover:bg-indigo-700 text-white'
        ]"
      >
        <i v-if="isProcessingBooking && bookingAction === 'cart'" 
           class="fas fa-spinner fa-spin mr-2"></i>
        <i v-else class="fas fa-shopping-cart mr-2"></i>
        {{ isProcessingBooking && bookingAction === 'cart' ? 'Đang thêm...' : 'Thêm vào giỏ hàng' }}
      </button>
      -->
    </div>

  </div> <!-- ✅ THÊM closing div cho wrapper -->
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