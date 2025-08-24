<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { timeSlots, availableFacilities } from '@/data/locationData.js'
import BusCard from './BusCard.vue'
import BusSeatSelection from './BusSeatSelection.vue'
// Import cart và booking APIs
import { CartAPI, CustomerAPI, BookingAPI } from '@/api/busAPI_Client/busbookingApi'
import { createCustomer } from '@/api/CustomerApi'
import { addItemToCart } from '@/api/OrderApi'
import { getUserIdFromToken, getBearerToken } from '@/services/TokenService'
import { useAuth } from '@/composables/useAuth'

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
const router = useRouter()

// Modal state management
const currentStep = ref(1)
const selectedTrip = ref(null)
const showBookingFlow = ref(false)

// Loading state
const loadingResults = ref(false)
const searchError = ref('')

// Component lifecycle tracking
const isMounted = ref(false)

// Booking state từ BusTicketBooking.vue
const bookingData = ref({
  selectedSeats: [],
  selectedSeatNumbers: [],
  passengerInfo: {
    fullName: '',
    phoneNumber: '',
    email: '',
    notes: ''
  },
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

// ✅ Storage change handler
let storageChangeHandler = null

// Mount/unmount tracking
onMounted(() => {
  isMounted.value = true
  
  // ✅ Listen for localStorage changes để auto reload pending orders
  storageChangeHandler = (e) => {
    if (e.key === 'activeCartId') {

      // Delay to ensure the change is processed
      setTimeout(() => {
        reloadPendingOrders()
      }, 100)
    }
  }
  
  if (typeof window !== 'undefined') {
    window.addEventListener('storage', storageChangeHandler)
    
    // ✅ Expose functions for external debugging/control
    window.busSearchModal = {
      reloadPendingOrders,
      checkPendingOrders,
      getCartIdFromStorage,
      clearCartFromStorage,
      debugCartState: () => ({
        hasActiveCart: hasActiveCart.value,
        hasPendingPaymentOrder: hasPendingPaymentOrder.value,
        pendingOrders: pendingOrders.value,
        activeCartId: getCartIdFromStorage(),
        checkedPendingOrders: checkedPendingOrders.value
      })
    }

  }
})

onUnmounted(() => {
  isMounted.value = false
  
  // ✅ Cleanup event listeners
  if (typeof window !== 'undefined' && storageChangeHandler) {
    window.removeEventListener('storage', storageChangeHandler)
    delete window.busSearchModal
  }
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

// Booking flow functions - updated để reset data
const startBookingFlow = (trip) => {
  selectedTrip.value = {
    ...trip,
    type: props.searchParams.busType || trip.type,
    activeTab: props.searchParams.busType || trip.activeTab
  }
  
  // ✅ Reset booking data khi bắt đầu flow mới
  bookingData.value = {
    selectedSeats: [],
    selectedSeatNumbers: [],
    passengerInfo: {
      fullName: '',
      phoneNumber: '',
      email: '',
      notes: ''
    },
    totalAmount: 0,
    discount: 0
  }
  
  // Reset validation states
  validationErrors.value = []
  showValidationErrors.value = false
  isProcessingBooking.value = false
  bookingAction.value = ''
  
  // Reset pending order check để re-check khi start new booking
  checkedPendingOrders.value = false
  pendingOrders.value = []
  
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

// ✅ COMPUTED PROPERTIES từ BusTicketBooking.vue
const selectedSeatsCount = computed(() => bookingData.value.selectedSeats.length)

const basePrice = computed(() => {
  const price = selectedTrip.value?.price || 0
  return typeof price === 'string' ? parseInt(price.replace(/[^\d]/g, '')) : price
})

const finalAmount = computed(() => {
  const total = basePrice.value * selectedSeatsCount.value
  const discountAmount = (total * bookingData.value.discount) / 100
  return total - discountAmount
})

// ✅ State cho pending orders
const pendingOrders = ref([])
const isCheckingPendingOrders = ref(false)
const checkedPendingOrders = ref(false)

// ✅ Cart checking computed properties
const hasActiveCart = computed(() => {
  const cartId = getCartIdFromStorage()
  return cartId !== null
})

const hasPendingPaymentOrder = computed(() => {
  // Chỉ count các orders PENDING_PAYMENT, loại bỏ CANCELLED
  return pendingOrders.value.some(order => order.status === 'PENDING_PAYMENT')
})

const shouldShowAddToCartButton = computed(() => {
  // Hiển thị nút "Thêm vào đơn hàng hiện tại" khi:
  // 1. Có activeCartId trong localStorage, HOẶC
  // 2. Có đơn hàng PENDING_PAYMENT
  return hasActiveCart.value || hasPendingPaymentOrder.value
})

const shouldShowDirectBookingButton = computed(() => {
  return true // Luôn hiển thị nút "Đặt vé ngay"
})

// ✅ NAVIGATION FUNCTIONS
const goToPassengerInfo = async () => {
  // Check pending orders before showing step 3
  await checkPendingOrders()
  currentStep.value = 3
}

const goToSeatSelection = () => {
  currentStep.value = 2
}

const canProceedToNext = () => {
  switch (currentStep.value) {
    case 2: // Chọn ghế -> Thông tin 
      return selectedSeatsCount.value > 0
    case 3: // Thông tin -> ĐẶT VÉ NGAY
      return isFormValid()
    default:
      return false
  }
}

const isFormValid = () => {
  return bookingData.value.passengerInfo.fullName && 
         bookingData.value.passengerInfo.phoneNumber &&
         !showValidationErrors.value
}

// ✅ SEAT SELECTION HANDLER
const handleSeatSelectionChange = (selectedSeats) => {
  bookingData.value.selectedSeats = selectedSeats.map(seat => seat.id)
  bookingData.value.selectedSeatNumbers = selectedSeats.map(seat => seat.seatNumber)
  
  // Update total amount
  bookingData.value.totalAmount = finalAmount.value
}

// ✅ FORM VALIDATION
const validatePassengerInfo = () => {
  isValidatingForm.value = true
  
  const customerInfo = CustomerAPI.createCustomerFromForm(bookingData.value.passengerInfo)
  const validation = CustomerAPI.validateCustomerInfo(customerInfo)
  
  validationErrors.value = validation.errors
  showValidationErrors.value = !validation.valid
  
  isValidatingForm.value = false
  return validation.valid
}

// ✅ CART HELPERS từ BusTicketBooking.vue
const getCartIdFromStorage = () => {
  const cartId = localStorage.getItem('activeCartId')
  return cartId && cartId !== 'null' ? parseInt(cartId) : null
}

const saveCartIdToStorage = (cartId) => {
  localStorage.setItem('activeCartId', cartId.toString())
}

const clearCartFromStorage = () => {
  localStorage.removeItem('activeCartId')
}

const validateExistingCart = async (cartId, expectedUserId) => {
  try {
    const response = await CartAPI.getCart(cartId)
    if (!(response.statusCode === 200 || response.success)) return false
    
    const cart = response.data
    if (cart.status !== 'CART') return false
    if (cart.userId !== expectedUserId) return false
    
    return true
  } catch (error) {
    return false
  }
}

const createNewCart = async (userId) => {
  try {

    const response = await CartAPI.createCart(userId)
    
    
    if ((response.statusCode === 201 || response.success) && response.data?.id) {
      const newCartId = response.data.id
      saveCartIdToStorage(newCartId)
      return newCartId
    } else {
      throw new Error('Invalid response from create cart API')
    }
  } catch (error) {
    throw new Error('Không thể tạo giỏ hàng mới. Vui lòng thử lại.')
  }
}

const ensureValidCart = async () => {
  const userId = getUserIdFromToken()
  if (!userId) {
    throw new Error('Vui lòng đăng nhập để sử dụng giỏ hàng')
  }

  let cartId = getCartIdFromStorage()
  
  if (!cartId) {
    cartId = await createNewCart(userId)
    return cartId
  }

  const isValid = await validateExistingCart(cartId, userId)
  
  if (isValid) {
    return cartId
  } else {
    clearCartFromStorage()
    cartId = await createNewCart(userId)
    return cartId
  }
}

// ✅ NEW: Check pending orders from API
const checkPendingOrders = async () => {
  if (isCheckingPendingOrders.value || checkedPendingOrders.value) return
  
  const userId = getUserIdFromToken()
  if (!userId) {
    return
  }

  isCheckingPendingOrders.value = true
  try {
    
    const response = await fetch(
      `http://localhost:8080/api/v1/orders/my-orders?userId=${userId}`,
      {
        headers: {
          Authorization: getBearerToken(),
          'Content-Type': 'application/json',
        },
      }
    )

    if (!response.ok) {
      throw new Error(`API error: ${response.status}`)
    }

    const data = await response.json()
    
    if (data.statusCode === 200) {
      pendingOrders.value = data.data || []
      
      // Filter pending payment orders (loại bỏ CANCELLED)
      const pendingPaymentOrders = pendingOrders.value.filter(order => order.status === 'PENDING_PAYMENT')
      const cancelledOrders = pendingOrders.value.filter(order => order.status === 'CANCELLED')
      
      if (cancelledOrders.length > 0) {
        // Clear activeCartId nếu nó trỏ đến cancelled order
        const activeCartId = getCartIdFromStorage()
        if (activeCartId && cancelledOrders.some(order => order.id === parseInt(activeCartId))) {
          clearCartFromStorage()
        }
      }
      
      if (pendingPaymentOrders.length > 0) {
        // Sync activeCartId với pending order đầu tiên (nếu chưa có)
        if (!getCartIdFromStorage() && pendingPaymentOrders[0]) {
          saveCartIdToStorage(pendingPaymentOrders[0].id)
        }
      }
    }
    
    checkedPendingOrders.value = true
    
  } catch (error) {
    // Don't throw error to prevent breaking the booking flow
  } finally {
    isCheckingPendingOrders.value = false
  }
}

// ✅ Force reload pending orders (for external calls)
const reloadPendingOrders = async () => {
  checkedPendingOrders.value = false
  pendingOrders.value = []
  await checkPendingOrders()
}

// ✅ Add to cart function 
const addToCart = async () => {
  if (!isMounted.value || isProcessingBooking.value) return
  
  try {
    isProcessingBooking.value = true
    bookingAction.value = 'add_to_cart'
    
    // Step 1: Validate form
    if (!validatePassengerInfo()) {
      alert('❌ Vui lòng kiểm tra lại thông tin hành khách')
      return
    }

    // Step 2: Lấy activeCartId từ localStorage hoặc pending order
    let activeCartId = getCartIdFromStorage()
    
    // Nếu không có activeCartId, check pending orders
    if (!activeCartId && hasPendingPaymentOrder.value) {
      const pendingOrder = pendingOrders.value.find(order => order.status === 'PENDING_PAYMENT')
      if (pendingOrder) {
        activeCartId = pendingOrder.id
        saveCartIdToStorage(activeCartId) // Sync vào localStorage
      }
    }
    
    if (!activeCartId) {
      alert('❌ Không tìm thấy giỏ hàng hiện tại')
      return
    }

    // Step 3: Tạo customer
    const customerData = {
      fullName: CustomerAPI.normalizeName(bookingData.value.passengerInfo.fullName),
      phone: bookingData.value.passengerInfo.phoneNumber.replace(/\s/g, ''),
      email: bookingData.value.passengerInfo.email || ''
    }

    const customerResponse = await createCustomer(customerData)
    const customerId = customerResponse.data.data.id

    // Step 4: Chuẩn bị data theo format Flight
    const data = {
      itemId: selectedTrip.value.busSlotId,
      itemType: "BUS", 
      busSlotId: selectedTrip.value.busSlotId,
      customerId: customerId,
      selectedSeatIds: bookingData.value.selectedSeats,
      totalPrice: finalAmount.value
    }

    // Step 5: Thêm vào giỏ hàng
    const response = await addItemToCart(activeCartId, data)
    
    if (isMounted.value) {
      alert('✅ Đã thêm vé xe vào giỏ hàng thành công!')
      
      // Redirect đến giỏ hàng
      setTimeout(() => {
        window.location.href = `/orders/${activeCartId}`
      }, 1000)
    }

  } catch (error) {
    // Nếu lỗi liên quan đến cart, clear localStorage
    if (error.message.includes('cart') || error.message.includes('Cart')) {
      clearCartFromStorage()
    }
    
    if (isMounted.value) {
      alert(`❌ Lỗi thêm vào giỏ hàng: ${error.response?.data?.message || error.message}`)
    }
  } finally {
    if (isMounted.value) {
      isProcessingBooking.value = false
      bookingAction.value = ''
    }
  }
}

const bookDirectly = async () => {
  if (!isMounted.value || isProcessingBooking.value) return
  
  try {
    isProcessingBooking.value = true
    bookingAction.value = 'direct'
    
    // Validate form
    if (!validatePassengerInfo()) {
      alert('❌ Vui lòng kiểm tra lại thông tin hành khách')
      return
    }

    // Create booking request
    const bookingRequest = {
      busSlotId: selectedTrip.value.busSlotId,
      selectedSeatNumbers: bookingData.value.selectedSeatNumbers,
      customerName: CustomerAPI.normalizeName(bookingData.value.passengerInfo.fullName),
      phone: bookingData.value.passengerInfo.phoneNumber.replace(/\s/g, ''),
      email: bookingData.value.passengerInfo.email || undefined,
      notes: bookingData.value.passengerInfo.notes,
      userId: useAuth().requireUserId() // ✅ Sử dụng enhanced useAuth
    }

    // Validate request
    const validation = BookingAPI.validateDirectBookingRequest(bookingRequest)
    if (!validation.valid) {
      alert(`❌ Dữ liệu không hợp lệ:\n${validation.errors.join('\n')}`)
      return
    }

    // Create booking
    const result = await BookingAPI.createDirectBooking(bookingRequest)
    
    if (isMounted.value) {
      // Redirect to payment
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

// Step styling functions - đồng bộ với BusTicketBooking.vue
const getStepClass = (step) => {
  // Step 1 luôn completed (green)
  if (step.id === 1) {
    return 'bg-green-500 text-white'
  }
  // Step hiện tại (blue/active)
  else if (step.id === currentStep.value) {
    return 'bg-blue-500 text-white'
  }
  // Step đã hoàn thành (green)
  else if (step.id < currentStep.value) {
    return 'bg-green-500 text-white'
  }
  // Step chưa đến (gray)
  else {
    return 'bg-gray-300 text-gray-500'
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
                      <i v-if="step.id === 1 || step.id < currentStep" class="fas fa-check"></i>
                      <span v-else class="font-medium">{{ step.id }}</span>
                    </div>
                    <span :class="[
                      step.id === 1 ? 'text-green-600 font-medium' :
                      step.id === currentStep ? 'text-blue-600 font-medium' : 
                      step.id < currentStep ? 'text-green-600 font-medium' : 
                      'text-gray-500'
                    ]" class="ml-1 md:ml-2 text-xs md:text-sm hidden sm:block">
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

              <!-- Step 2: Seat Selection -->
              <template v-else-if="currentStep === 2">
                <div class="w-full overflow-y-auto">
                  <Transition name="fade-left" mode="out-in">
                    <div class="p-6">
                      <div class="mb-6">
                        <div class="flex items-center justify-between mb-4">
                          <div>
                            <h3 class="text-lg font-semibold text-gray-900">Chọn ghế ngồi</h3>
                            <p class="text-sm text-gray-600">{{ selectedTrip?.company || 'Nhà xe' }} - {{ selectedTrip?.busType || 'Loại xe' }}</p>
                          </div>
                          <!-- Seat Preview -->
                          <div v-if="bookingData.selectedSeatNumbers && bookingData.selectedSeatNumbers.length > 0" 
                               class="flex items-center bg-blue-50 border border-blue-200 rounded-lg px-3 py-2">
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

                      <!-- Seat Selection Component -->
                      <BusSeatSelection 
                        :busSlot="selectedTrip"
                        :selectedSeats="bookingData.selectedSeats"
                        :maxSeats="10"
                        @selection-change="handleSeatSelectionChange"
                      />

                      <!-- Footer Actions -->
                      <div class="mt-6 pt-4 border-t border-gray-200 flex items-center justify-between">
                        <button @click="backToSearch" 
                                class="px-4 py-2 text-gray-600 border border-gray-300 rounded-md hover:bg-gray-50">
                          <i class="fas fa-arrow-left mr-2"></i>Quay lại
                        </button>
                        <button @click="goToPassengerInfo"
                                :disabled="!canProceedToNext() || isCheckingPendingOrders"
                                :class="[
                                  'px-6 py-2 text-white rounded-md transition-colors',
                                  (canProceedToNext() && !isCheckingPendingOrders)
                                    ? 'bg-blue-600 hover:bg-blue-700' 
                                    : 'bg-gray-400 cursor-not-allowed'
                                ]">
                          <i v-if="isCheckingPendingOrders" class="fas fa-spinner fa-spin mr-2"></i>
                          {{ isCheckingPendingOrders ? 'Đang kiểm tra...' : 'Tiếp tục' }}
                          <i v-if="!isCheckingPendingOrders" class="fas fa-arrow-right ml-2"></i>
                        </button>
                      </div>
                    </div>
                  </Transition>
                </div>
              </template>

              <!-- Step 3: Passenger Info -->
              <template v-else-if="currentStep === 3">
                <div class="w-full overflow-y-auto">
                  <Transition name="fade-left" mode="out-in">
                    <div class="p-6">
                      <div class="mb-6">
                        <div class="flex items-center justify-between mb-4">
                          <div>
                            <h3 class="text-lg font-semibold text-gray-900">Thông tin hành khách</h3>
                            <p class="text-sm text-gray-600">Vui lòng điền thông tin chính xác để hoàn tất đặt vé</p>
                          </div>
                          <!-- Seat Preview -->
                          <div v-if="bookingData.selectedSeatNumbers && bookingData.selectedSeatNumbers.length > 0" 
                               class="flex items-center bg-blue-50 border border-blue-200 rounded-lg px-3 py-2">
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

                      <!-- Validation Errors -->
                      <div v-if="showValidationErrors && validationErrors.length > 0" 
                           class="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
                        <div class="flex items-center mb-2">
                          <i class="fas fa-exclamation-triangle text-red-500 mr-2"></i>
                          <span class="text-sm font-medium text-red-700">Vui lòng kiểm tra lại:</span>
                        </div>
                        <ul class="text-sm text-red-600 ml-6">
                          <li v-for="error in validationErrors" :key="error">• {{ error }}</li>
                        </ul>
                      </div>
                      
                      <!-- Passenger Form -->
                      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div class="md:col-span-2">
                          <label class="block text-sm font-medium text-gray-700 mb-2">Họ và tên *</label>
                          <div class="relative">
                            <i class="fas fa-user absolute left-3 top-3 text-gray-400"></i>
                            <input v-model="bookingData.passengerInfo.fullName" 
                                   type="text" 
                                   class="w-full pl-10 pr-3 py-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                                   placeholder="Nhập họ và tên"
                                   @blur="validatePassengerInfo">
                          </div>
                        </div>

                        <div>
                          <label class="block text-sm font-medium text-gray-700 mb-2">Số điện thoại *</label>
                          <div class="relative">
                            <i class="fas fa-phone absolute left-3 top-3 text-gray-400"></i>
                            <input v-model="bookingData.passengerInfo.phoneNumber" 
                                   type="tel" 
                                   class="w-full pl-10 pr-3 py-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                                   placeholder="Nhập số điện thoại"
                                   @blur="validatePassengerInfo">
                          </div>
                        </div>

                        <div>
                          <label class="block text-sm font-medium text-gray-700 mb-2">Email</label>
                          <div class="relative">
                            <i class="fas fa-envelope absolute left-3 top-3 text-gray-400"></i>
                            <input v-model="bookingData.passengerInfo.email" 
                                   type="email" 
                                   class="w-full pl-10 pr-3 py-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                                   placeholder="Nhập email">
                          </div>
                        </div>

                        <div class="md:col-span-2">
                          <label class="block text-sm font-medium text-gray-700 mb-2">Ghi chú</label>
                          <div class="relative">
                            <i class="fas fa-comment-alt absolute left-3 top-3 text-gray-400"></i>
                            <textarea v-model="bookingData.passengerInfo.notes" 
                                      class="w-full pl-10 pr-3 py-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                                      rows="3"
                                      placeholder="Ghi chú thêm"
                                      maxlength="300"></textarea>
                          </div>
                        </div>
                      </div>

                      <!-- Footer Actions -->
                      <div class="mt-6 pt-4 border-t border-gray-200 flex items-center justify-between">
                        <button @click="goToSeatSelection" 
                                class="px-4 py-2 text-gray-600 border border-gray-300 rounded-md hover:bg-gray-50">
                          <i class="fas fa-arrow-left mr-2"></i>Quay lại
                        </button>
                        <div class="flex space-x-3">
                          <!-- ✅ Loading state khi check pending orders -->
                          <div v-if="isCheckingPendingOrders" class="flex items-center text-sm text-gray-500">
                            <i class="fas fa-spinner fa-spin mr-2"></i>
                            Đang kiểm tra đơn hàng...
                          </div>
                          
                          <template v-else>
                            <!-- ✅ Nút "Thêm vào đơn hàng hiện tại" - hiển thị khi có activeCartId hoặc pending order -->
                            <button v-if="shouldShowAddToCartButton" 
                                    @click="addToCart"
                                    :disabled="!isFormValid() || (isProcessingBooking && bookingAction === 'add_to_cart')"
                                    class="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50">
                              <i v-if="isProcessingBooking && bookingAction === 'add_to_cart'" class="fas fa-spinner fa-spin mr-2"></i>
                              <i v-else class="fas fa-shopping-cart mr-2"></i>
                              {{ isProcessingBooking && bookingAction === 'add_to_cart' ? 'Đang thêm...' : 'Thêm vào đơn hàng hiện tại' }}
                            </button>
                           
                            <!-- ✅ Nút "Đặt vé ngay" - luôn hiển thị -->
                            <button v-if="shouldShowDirectBookingButton"
                                    @click="bookDirectly"
                                    :disabled="!isFormValid() || (isProcessingBooking && bookingAction === 'direct')"
                                    class="px-6 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 disabled:opacity-50">
                              <i v-if="isProcessingBooking && bookingAction === 'direct'" class="fas fa-spinner fa-spin mr-2"></i>
                              <i v-else class="fas fa-bolt mr-2"></i>
                              {{ isProcessingBooking && bookingAction === 'direct' ? 'Đang đặt...' : 'Đặt vé ngay' }}
                            </button>
                          </template>
                        </div>
                      </div>
                    </div>
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

/* ✅ Fade Left Animation cho step transitions */
.fade-left-enter-active,
.fade-left-leave-active {
  transition: all 0.4s ease-in-out;
}

.fade-left-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.fade-left-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-left-enter-to,
.fade-left-leave-from {
  opacity: 1;
  transform: translateX(0);
}

/* Legacy slide-left for compatibility */
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