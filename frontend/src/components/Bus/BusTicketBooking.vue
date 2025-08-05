<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router' // ✅ THÊM để redirect
import { SeatAPI, BookingAPI } from '@/api/busAPI_Client'
import { CartAPI, CustomerAPI } from '@/api/busAPI_Client/busbookingApi'
// ✅ Import theo cách của Flight
import { createCustomer } from '@/api/CustomerApi'
import { addItemToCart } from '@/api/OrderApi'
import BusSeatSelection from './BusSeatSelection.vue'
import { getUserIdFromToken } from '@/services/TokenService' // ✅ THÊM

// DEV MODE: No auth required for booking

const router = useRouter() // ✅ THÊM router instance

// ✅ LEGACY HELPERS - Minimal for compatibility 
/**
 * Lấy Cart ID từ localStorage (cho debug purposes)
 */
const getCartIdFromStorage = () => {
  const cartId = localStorage.getItem('activeCartId')
  return cartId && cartId !== 'null' ? parseInt(cartId) : null
}

/**
 * Debug function để log trạng thái cart
 */
const debugCartState = async () => {
  const cartId = getCartIdFromStorage()
  const userId = getUserIdFromToken()
  
  // ✅ DEBUG: Kiểm tra cart có thuộc về user hiện tại không
  if (cartId) {
    try {
      await validateExistingCart(cartId, userId)
      await CartAPI.getCart(cartId)
    } catch (error) {
      // Silent error handling
    }
  }
}

/**
 * Test function để kiểm tra cart flow (for development)
 */
const testCartFlow = async () => {
  try {
    // Test 1: Clear cart và test tạo mới
    clearCartFromStorage()
    await ensureValidCart()
    
    // Test 2: Test reuse existing cart
    await ensureValidCart()
    
    // Test 3: Test với cart ID invalid
    saveCartIdToStorage(99999) // Fake cart ID
    await ensureValidCart()
    
    // Test 4: Test với cart thuộc user khác
    saveCartIdToStorage(509) // Cart với user=null hoặc user khác
    await ensureValidCart()
    
  } catch (error) {
    // Silent error handling
  }
}

/**
 * Debug token để kiểm tra JWT payload
 */
const debugToken = () => {
  try {
    const token = localStorage.getItem('t_')
    if (!token) return null
    
    const parts = token.split('.')
    if (parts.length !== 3) return null
    
    return JSON.parse(atob(parts[1]))
  } catch (error) {
    return null
  }
}

/**
 * Force tạo cart mới (ignore cart hiện tại)
 */
const forceCreateNewCart = async () => {
  try {
    const userId = getUserIdFromToken()
    if (!userId) return null
    
    clearCartFromStorage() // Xóa cart cũ
    return await createNewCart(userId)
  } catch (error) {
    return null
  }
}

/**
 * Debug function để kiểm tra cart state (legacy compatibility)
 */
const debugCartButtonState = () => {
  getCartIdFromStorage()
}

// Expose test functions to window for manual testing (LEGACY)
if (typeof window !== 'undefined') {
  // ✅ LEGACY FUNCTIONS (minimal for compatibility)
  window.debugBusTicketBookingState = debugCartButtonState
  window.debugBusCartState = debugCartState
  window.debugBusToken = debugToken
  // Note: Main cart logic is now in BusSearchModal.vue
}

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

// Mount/unmount tracking với minimal debug
onMounted(async () => {
  isMounted.value = true
  
  // Debug simple state khi component load
  
  try {
    // ✅ MINIMAL: Debug cart state
    debugCartButtonState()
  } catch (error) {
  }
  
  // Optional: Pre-warm cart (không tạo mới, chỉ check)
  const cartId = getCartIdFromStorage()
  if (cartId) {
    const userId = getUserIdFromToken()
    if (userId) {
      try {
        const isValid = await validateExistingCart(cartId, userId)
        if (isValid) {
        } else {
        }
      } catch (error) {
      }
    } else {
    }
  } else {
  }
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

// ✅ REMOVED: Button logic moved to BusSearchModal.vue

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
  
  updateTotalAmount()
}

// Price calculation functions
const updateTotalAmount = () => {
  bookingData.value.totalAmount = finalAmount.value
}

// ✅ Navigation functions - Only 3 steps
const goToNextStep = () => {
  if (props.currentStep < 3) {
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
      return false // Chỉ có Step 1, 2 và 3
  }
}

// ✅ REMOVED: addToCart logic moved to BusSearchModal.vue

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

    // Validate direct booking request
    const validation = BookingAPI.validateDirectBookingRequest(bookingRequest)
    if (!validation.valid) {
      alert(`❌ Dữ liệu không hợp lệ:\n${validation.errors.join('\n')}`)
      return
    }

    // Create direct booking
    const result = await BookingAPI.createDirectBooking(bookingRequest)
    
    if (isMounted.value) {
      
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
  <div v-if="show" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-lg shadow-xl w-full max-w-4xl max-h-[90vh] flex flex-col">
      
      <!-- Header -->
      <div class="flex items-center justify-between p-4 border-b">
        <div>
          <h2 class="text-lg font-semibold">Đặt vé xe khách</h2>
          <p class="text-sm text-gray-500">{{ selectedTrip?.route?.startLocation || 'Điểm đi' }} → {{ selectedTrip?.route?.endLocation || 'Điểm đến' }}</p>
        </div>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600">
          <i class="fas fa-times"></i>
        </button>
      </div>

      <!-- Progress -->
      <div class="px-4 py-3 border-b bg-gray-50">
        <div class="flex items-center justify-between">
          <!-- Steps -->
          <div class="flex items-center space-x-6">
            <!-- Step 1: Search (Always completed) -->
            <div class="flex items-center">
              <div class="w-6 h-6 rounded-full bg-green-500 flex items-center justify-center">
                <i class="fas fa-check text-white text-xs"></i>
              </div>
              <span class="ml-2 text-sm text-green-600 font-medium">Tìm kiếm</span>
            </div>
            
            <!-- Step 2: Seat Selection -->
            <div class="flex items-center">
              <div :class="[
                currentStep === 2 ? 'bg-blue-500 text-white' : 
                currentStep > 2 ? 'bg-green-500 text-white' : 
                'bg-gray-300 text-gray-500'
              ]" class="w-6 h-6 rounded-full flex items-center justify-center">
                <i v-if="currentStep > 2" class="fas fa-check text-white text-xs"></i>
                <span v-else class="text-xs font-medium">2</span>
              </div>
              <span :class="[
                currentStep === 2 ? 'text-blue-600 font-medium' : 
                currentStep > 2 ? 'text-green-600 font-medium' : 
                'text-gray-500'
              ]" class="ml-2 text-sm">Chọn ghế</span>
            </div>
            
            <!-- Step 3: Passenger Info -->
            <div class="flex items-center">
              <div :class="[
                currentStep === 3 ? 'bg-blue-500 text-white' : 
                currentStep > 3 ? 'bg-green-500 text-white' : 
                'bg-gray-300 text-gray-500'
              ]" class="w-6 h-6 rounded-full flex items-center justify-center">
                <i v-if="currentStep > 3" class="fas fa-check text-white text-xs"></i>
                <span v-else class="text-xs font-medium">3</span>
              </div>
              <span :class="[
                currentStep === 3 ? 'text-blue-600 font-medium' : 
                currentStep > 3 ? 'text-green-600 font-medium' : 
                'text-gray-500'
              ]" class="ml-2 text-sm">Thông tin</span>
            </div>
          </div>

          <!-- Seat Preview (when seats are selected) -->
          <div v-if="bookingData.selectedSeatNumbers && bookingData.selectedSeatNumbers.length > 0" 
               class="flex items-center bg-blue-50 border border-blue-200 rounded-lg px-3 py-1">
            <i class="fas fa-chair text-blue-600 mr-2"></i>
            <span class="text-sm text-blue-700 font-medium">
              Ghế: {{ bookingData.selectedSeatNumbers.join(', ') }}
            </span>
            <span class="ml-2 text-xs text-blue-600 bg-blue-100 px-2 py-1 rounded">
              {{ finalAmount?.toLocaleString?.('vi-VN') || '0' }} ₫
            </span>
          </div>
        </div>
      </div>

      <!-- Content -->
      <div class="flex-1 overflow-y-auto p-4">
        
        <!-- Step 2: Seat Selection -->
        <div v-if="currentStep === 2">
          <div class="mb-4">
            <div class="flex items-center justify-between mb-2">
              <h3 class="text-lg font-semibold">Chọn ghế ngồi</h3>
              <div class="text-sm text-gray-500">
                {{ selectedTrip?.company || 'Nhà xe' }} - {{ selectedTrip?.busType || 'Loại xe' }}
              </div>
            </div>
          </div>

          <!-- Use Seat Selection Component -->
          <BusSeatSelection 
            :busSlot="selectedTrip"
            :selectedSeats="bookingData.selectedSeats"
            :maxSeats="10"
            @selection-change="handleSeatSelectionChange"
          />
        </div>

        <!-- Step 3: Passenger Info -->
        <div v-if="currentStep === 3">
          <h3 class="text-lg font-semibold mb-4">Thông tin hành khách</h3>
          
          <!-- Validation Errors -->
          <div v-if="showValidationErrors && validationErrors.length > 0" 
               class="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg">
            <div class="flex items-center mb-1">
              <i class="fas fa-exclamation-triangle text-red-500 mr-2"></i>
              <span class="text-sm font-medium text-red-700">Vui lòng kiểm tra lại:</span>
            </div>
            <ul class="text-sm text-red-600 ml-6">
              <li v-for="error in validationErrors" :key="error">• {{ error }}</li>
            </ul>
          </div>
          
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="md:col-span-2">
              <label class="block text-sm font-medium text-gray-700 mb-1">Họ và tên *</label>
              <div class="relative">
                <i class="fas fa-user absolute left-3 top-3 text-gray-400"></i>
                <input v-model="bookingData.passengerInfo.fullName" 
                       type="text" 
                       class="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                       placeholder="Nhập họ và tên"
                       @blur="validatePassengerInfo">
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Số điện thoại *</label>
              <div class="relative">
                <i class="fas fa-phone absolute left-3 top-3 text-gray-400"></i>
                <input v-model="bookingData.passengerInfo.phoneNumber" 
                       type="tel" 
                       class="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                       placeholder="Nhập số điện thoại"
                       @blur="validatePassengerInfo">
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Email</label>
              <div class="relative">
                <i class="fas fa-envelope absolute left-3 top-3 text-gray-400"></i>
                <input v-model="bookingData.passengerInfo.email" 
                       type="email" 
                       class="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                       placeholder="Nhập email">
              </div>
            </div>

            <div class="md:col-span-2">
              <label class="block text-sm font-medium text-gray-700 mb-1">Ghi chú</label>
              <div class="relative">
                <i class="fas fa-comment-alt absolute left-3 top-3 text-gray-400"></i>
                <textarea v-model="bookingData.passengerInfo.notes" 
                          class="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                          rows="2"
                          placeholder="Ghi chú thêm"
                          maxlength="300"></textarea>
              </div>
            </div>
          </div>


        </div>

      </div>

      <!-- Footer -->
      <div class="border-t p-4">
        <div class="flex items-center justify-between">
          <button v-if="currentStep >= 2" 
                  @click="goToPrevStep" 
                  class="px-4 py-2 text-gray-600 border border-gray-300 rounded-md hover:bg-gray-50">
            <i class="fas fa-arrow-left mr-2"></i>Quay lại
          </button>
          <div v-else></div>

          <div class="flex space-x-3">
            <!-- ✅ REMOVED: Booking buttons logic moved to BusSearchModal.vue -->
            <div class="text-sm text-gray-500 text-center w-full">
              <i class="fas fa-info-circle mr-2"></i>
              Booking logic has been moved to BusSearchModal.vue
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<style scoped>
/* ✅ Simple and clean styles */
.seat-button:hover:not(.cursor-not-allowed) {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
}

/* ✅ Smooth step transitions */
.seat-selection,
.passenger-info {
  animation: fadeInUp 0.3s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ✅ Mobile responsive */
@media (max-width: 768px) {
  .grid-cols-1.md\\:grid-cols-2 {
    grid-template-columns: 1fr;
  }
}
</style> 