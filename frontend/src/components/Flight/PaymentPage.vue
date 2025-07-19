<template>
    <div class="min-h-screen 
   after:absolute
    after:w-96
    after:h-96
    after:bg-sky-300
    after:-z-10
    after:rounded-full
    after:-top-70
    after:left-4
    after:blur-xl
    after:[box-shadow:-100px_50px_30px_100px_#7dd3fc]">


        <!-- ========== NỘI DUNG CHÍNH ========== -->
        <div class="max-w-7xl mx-auto px-4 py-8 grid grid-cols-1 lg:grid-cols-12 gap-8">
            <!-- --- Cột trái: Thông tin hành khách --- -->
            <div class="lg:col-span-8 space-y-8">
                <!-- Passenger #1 -->
                <section class="bg-white rounded-lg shadow-md p-6 overflow-hidden relative">
                    <div
                        class="absolute w-34 h-34 bg-sky-300 rounded-full bottom-4 -right-40 blur-xl [box-shadow:-100px_50px_30px_100px_#7dd3fc]">
                    </div>
                    <h2 class="text-lg font-medium text-gray-700 mb-4 z-10 relative">Thông tin khách hàng</h2>
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-6 z-10 relative">
                        <!-- Họ và tên -->
                        <div>
                            <label class="block text-sm font-medium text-gray-600 mb-1">Họ và tên</label>
                            <input v-model="customer.fullName" type="text" placeholder="Nhập họ tên đầy đủ"
                                class="w-full border border-gray-300 rounded-lg px-4 py-2 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
                        </div>
                        <!-- Giới tính -->
                        <div>
                            <label class="block text-sm font-medium text-gray-600 mb-1">Giới tính</label>
                            <select v-model="customer.gender" class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500">
                                <option :value="true">Nam</option>
                                <option :value="false">Nữ</option>
                                <option :value="null">Khác</option>
                            </select>
                        </div>
                        <!-- Ngày sinh -->
                        <div>
                            <label class="block text-sm font-medium text-gray-600 mb-1">Ngày sinh</label>
                            <input v-model="customer.dob" type="date"
                                class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
                        </div>
                        <!-- Số hộ chiếu -->
                        <div>
                            <label class="block text-sm font-medium text-gray-600 mb-1">Số hộ chiếu</label>
                            <input v-model="customer.passport" type="text" placeholder="Nhập số hộ chiếu"
                                class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
                        </div>
                        <!-- Email -->
                        <div>
                            <label class="block text-sm font-medium text-gray-600 mb-1">Email</label>
                            <input v-model="customer.email" type="email" placeholder="Nhập email"
                                class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
                        </div>
                        <!-- Số điện thoại -->
                        <div>
                            <label class="block text-sm font-medium text-gray-600 mb-1">Số điện thoại</label>
                            <input v-model="customer.phone" type="text" placeholder="Nhập số điện thoại"
                                class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
                        </div>
                    </div>
                </section>
                <!-- Passenger #2 -->
                <div class="bg-white rounded-lg shadow-md p-6 relative overflow-hidden">
                    <div
                        class="absolute w-34 h-34 bg-sky-300 rounded-full -top-20 -right-40 blur-xl [box-shadow:-100px_50px_30px_100px_#7dd3fc]">
                    </div>
                    <h3 class="text-lg font-medium text-gray-700 mb-4">Pay with</h3>
                    <!-- Tabs -->
                    <div class="flex space-x-2 mb-4">
                        <button v-for="method in paymentMethods" :key="method.key" @click="activeMethod = method.key"
                            :class="activeMethod === method.key
                                ? 'bg-indigo-600 text-white'
                                : 'bg-white text-gray-600 border border-gray-300 hover:bg-gray-100'"
                            class="px-4 py-2 rounded-full text-sm font-medium transition-colors">
                            {{ method.label }}
                        </button>
                    </div>

                    <!-- Form tương ứng với từng phương thức -->
                    <div>
                        <!-- ====== Credit Card Form ====== -->
                        <div v-if="activeMethod === 'credit'">
                            <div class="space-y-4">
                                <!-- Card Number -->
                                <div>
                                    <label for="cardNumber" class="block text-sm font-medium text-gray-600 mb-1">Card
                                        Number</label>
                                    <input id="cardNumber" v-model="card.cardNumber" type="text"
                                        placeholder="1234 5678 1234 5678"
                                        class="w-full border border-gray-300 rounded-lg px-4 py-2 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
                                </div>
                                <!-- Card Holder -->
                                <div>
                                    <label for="cardHolder" class="block text-sm font-medium text-gray-600 mb-1">Card
                                        Holder</label>
                                    <input id="cardHolder" v-model="card.cardHolder" type="text"
                                        placeholder="Name on card"
                                        class="w-full border border-gray-300 rounded-lg px-4 py-2 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
                                </div>
                                <!-- Expiry & CVV -->
                                <div class="grid grid-cols-2 gap-4">
                                    <div>
                                        <label for="expiryDate"
                                            class="block text-sm font-medium text-gray-600 mb-1">Expiration Date</label>
                                        <input id="expiryDate" v-model="card.expiryDate" type="month"
                                            class="w-full border border-gray-300 rounded-lg px-4 py-2 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
                                    </div>
                                    <div>
                                        <label for="cvv"
                                            class="block text-sm font-medium text-gray-600 mb-1">CVV</label>
                                        <input id="cvv" v-model="card.cvv" type="password" maxlength="4"
                                            placeholder="123"
                                            class="w-full border border-gray-300 rounded-lg px-4 py-2 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
                                    </div>
                                </div>
                                <!-- Save Card Checkbox -->
                                <div class="flex items-center space-x-2">
                                    <input id="saveCard" v-model="card.saveCard" type="checkbox"
                                        class="h-4 w-4 text-indigo-600 border-gray-300 rounded focus:ring-indigo-500" />
                                    <label for="saveCard" class="text-sm text-gray-600">Save my card for future
                                        reservations</label>
                                </div>
                            </div>
                        </div>

                        <!-- ====== PayPal ====== -->
                        <div v-else-if="activeMethod === 'paypal'" class="flex justify-center">
                            <button @click="payWithPayPal"
                                class="bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-lg transition-colors">
                                Pay with PayPal
                            </button>
                        </div>

                        <!-- ====== Google Pay ====== -->
                        <div v-else-if="activeMethod === 'googlepay'" class="flex justify-center">
                            <button @click="payWithGooglePay"
                                class="bg-gray-800 hover:bg-gray-900 text-white px-6 py-3 rounded-lg transition-colors">
                                Google Pay
                            </button>
                        </div>
                    </div>
                </div>

                
            </div>

            <!-- --- Cột phải: Tóm tắt chuyến đi & Thanh toán --- -->
            <div class="lg:col-span-4 space-y-6">
                <!-- 🎫 Thẻ tóm tắt chuyến đi -->
                <div class="bg-gradient-to-br from-white to-gray-50 rounded-2xl shadow-lg border border-gray-100 p-6 hover:shadow-xl transition-all duration-300">
    <!-- Loading state -->
    <div v-if="loading" class="text-center py-8">
        <div class="relative">
            <div class="animate-spin rounded-full h-12 w-12 border-4 border-gray-200 border-t-indigo-600 mx-auto"></div>
            <div class="absolute inset-0 rounded-full h-12 w-12 border-4 border-transparent border-t-indigo-300 animate-ping mx-auto"></div>
        </div>
        <p class="text-gray-600 mt-4 font-medium">Đang tải thông tin...</p>
    </div>

    <!-- Error state -->
    <div v-else-if="error" class="text-center py-8">
        <div class="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <svg class="w-8 h-8 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
        </div>
        <p class="text-red-600 font-medium">{{ error }}</p>
    </div>

    <!-- Content -->
    <div v-else-if="flight && availableSlot" class="space-y-5">
        <!-- Header với logo hãng + route -->
        <div class="flex items-start justify-between">
            <div class="flex items-center space-x-4">
                <div class="relative">
                    <img v-if="flight.airline && flight.airline.name" 
                        :src="flight.images[0].imageUrl"
                        :alt="flight.airline.name" 
                        class="w-14 h-14 object-contain rounded-xl shadow-sm ring-2 ring-white" />
                    <div class="absolute -bottom-1 -right-1 w-4 h-4 bg-green-500 rounded-full border-2 border-white"></div>
                </div>
                <div class="space-y-1">
                    <div class="flex items-center space-x-2">
                        <h3 class="text-sm font-bold text-gray-800">
                            {{ flight.departureAirport?.name || 'N/A' }}
                        </h3>
                        <svg class="w-5 h-5 text-indigo-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3"></path>
                        </svg>
                        <h3 class="text-sm font-bold text-gray-800">
                            {{ flight.arrivalAirport?.name || 'N/A' }}
                        </h3>
                    </div>
                    <p class="text-sm text-gray-500 flex items-center">
                        <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                        </svg>
                        {{ formatDate(flight.departureTime) }}
                    </p>
                </div>
            </div>
        </div>
        
        <!-- Flight details với timeline -->
        <div class="bg-white rounded-xl border border-gray-100 p-4 shadow-sm">
            <div class="flex items-center justify-between mb-3">
                <h4 class="font-semibold text-gray-800 flex items-center">
                    <svg class="w-5 h-5 mr-2 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"></path>
                    </svg>
                    {{ flight.flightNumber }}
                </h4>
                <span class="text-xs bg-indigo-100 text-indigo-800 px-2 py-1 rounded-full font-medium">
                    {{ availableSlot.isBusiness ? 'Business' : 'Economy' }}
                </span>
            </div>
            
            <div class="flex items-center justify-between">
                <div class="text-center">
                    <p class="text-2xl font-bold text-gray-800">{{ formatTime(flight.departureTime) }}</p>
                    <p class="text-xs text-gray-500">Khởi hành</p>
                </div>
                <div class="flex-1 mx-4">
                    <div class="relative">
                        <div class="h-0.5 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-full"></div>
                        <div class="absolute left-0 top-1/2 transform -translate-y-1/2 w-2 h-2 bg-indigo-500 rounded-full"></div>
                        <div class="absolute right-0 top-1/2 transform -translate-y-1/2 w-2 h-2 bg-purple-500 rounded-full"></div>
                        <div class="absolute left-1/2 top-1/2 transform -translate-x-1/2 -translate-y-1/2">
                            <svg class="w-4 h-4 text-indigo-600" fill="currentColor" viewBox="0 0 20 20">
                                <path d="M10.894 2.553a1 1 0 00-1.788 0l-7 14a1 1 0 001.169 1.409l5-1.429A1 1 0 009 15.571V11a1 1 0 112 0v4.571a1 1 0 00.725.962l5 1.428a1 1 0 001.17-1.408l-7-14z"/>
                            </svg>
                        </div>
                    </div>
                </div>
                <div class="text-center">
                    <p class="text-2xl font-bold text-gray-800">{{ formatTime(flight.arrivalTime) }}</p>
                    <p class="text-xs text-gray-500">Hạ cánh</p>
                </div>
            </div>
        </div>

        <!-- Thông tin ghế với card design -->
        <div class="grid grid-cols-2 gap-4">
            <div class="bg-gradient-to-br from-blue-50 to-indigo-50 rounded-xl p-4 border border-blue-100">
                <div class="flex items-center justify-between mb-2">
                    <h5 class="text-sm font-semibold text-gray-700">Thông tin ghế</h5>
                    <svg class="w-5 h-5 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
                    </svg>
                </div>
                <div class="space-y-2">
                    <div class="flex items-center justify-between">
                        <span class="text-sm text-gray-600">Số ghế:</span>
                        <span class="font-bold text-indigo-700 text-lg">{{ availableSlot.seatNumber }}</span>
                    </div>
                    <div class="flex items-center justify-between">
                        <span class="text-sm text-gray-600">Vị trí:</span>
                        <span class="text-xs px-2 py-1 rounded-full font-medium" 
                              :class="availableSlot.isWindow ? 'bg-blue-100 text-blue-800' : availableSlot.isAisle ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'">
                            {{ availableSlot.isWindow ? 'Cửa sổ' : availableSlot.isAisle ? 'Lối đi' : 'Thường' }}
                        </span>
                    </div>
                </div>
            </div>

            <div class="bg-gradient-to-br from-green-50 to-emerald-50 rounded-xl p-4 border border-green-100">
                <div class="flex items-center justify-between mb-2">
                    <h5 class="text-sm font-semibold text-gray-700">Hành lý</h5>
                    <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"></path>
                    </svg>
                </div>
                <div class="text-center">
                    <p class="text-2xl font-bold text-green-700">{{ availableSlot.carryOnLuggage }}</p>
                    <p class="text-xs text-gray-600">kg xách tay</p>
                </div>
            </div>
        </div>

        <!-- Giá và CTA -->
        <div class=" border border-indigo-600 border-2 rounded-xl p-4 text-indigo-600">
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-sm opacity-90">Tổng giá vé</p>
                    <p class="text-3xl font-bold">{{ formatCurrency(availableSlot.price) }} VND</p>
                </div>
                
            </div>
        </div>
    </div>
</div>

                <!-- 🎯 Phần chọn phương thức thanh toán -->
                
                <div class="flex justify-center">
                    <router-link to="/plane/getticket" @click="confirmAndPay"
                        class="w-full block text-center bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-3 rounded-lg transition-colors shadow-md">
                        Confirm and Pay
                    </router-link>
                </div>
                
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { payForFlight, getFlightDetailPublic, findFirstAvailableSlot } from '@/api/flightApi'

const router = useRouter()
const route = useRoute()

/** ========== Dữ liệu từ props hoặc route ========== **/
const flight = ref(null)
const availableSlot = ref(null)
const loading = ref(false)
const error = ref('')

// Lấy DTO từ route query
const getDtoFromRoute = () => {
  try {
    const dtoString = route.query.dto
    if (dtoString) {
      return JSON.parse(dtoString)
    }
  } catch (e) {
    console.error('Lỗi khi parse DTO từ route:', e)
  }
  return null
}

// Gọi 2 API khi component mount
onMounted(async () => {
  const dto = getDtoFromRoute()
  const flightId = route.params.flightId

  if (!dto ) {
    console.error('Thiếu thông tin DTO ')
    console.log('DTO:', dto)
    return
  }

  loading.value = true
  error.value = ''

  try {
    // Gọi API 1: Lấy thông tin flight
    console.log('Gọi API getFlightDetailPublic với flightId:', dto.flightId)
    const flightResponse = await getFlightDetailPublic( dto.flightId)
    flight.value = flightResponse.data
    console.log('Flight data:', flight.value)

    // Gọi API 2: Tìm vé khả dụng
    console.log('Gọi API findFirstAvailableSlot với DTO:', dto)
    const slotResponse = await findFirstAvailableSlot(dto)
    availableSlot.value = slotResponse.data
    console.log('Available slot:', availableSlot.value)

  } catch (e) {
    console.error('Lỗi khi load dữ liệu:', e)
    error.value = 'Không thể tải thông tin chuyến bay'
  } finally {
    loading.value = false
  }
})


const customer = ref({
    fullName: '',
    gender: null,
    dob: '',
    passport: '',
    email: '',
    phone: '',
})

/** ========== Mã giảm giá ========== **/
const discountCode = ref('')
const discountAmount = ref(0) // số tiền giảm (ví dụ từ backend trả về)
function applyDiscount() {
    // Giả sử nếu code === "VIETNAM10" thì giảm 10%
    if (discountCode.value.trim().toUpperCase() === 'VIETNAM10') {
        const basePrice = availableSlot.value ? availableSlot.value.price : 0
        discountAmount.value = Math.floor(basePrice * 0.1)
        alert(`Áp dụng thành công: giảm ${formatCurrency(discountAmount.value)} VND`)
    } else {
        discountAmount.value = 0
        alert('Mã giảm giá không hợp lệ hoặc đã hết hạn')
    }
}

/** ========== Tính toán subtotal (tổng tiền tạm tính) ========== **/
const subtotal = computed(() => {
    if (!availableSlot.value) return 0
    // Giả định 2 hành khách
    const base = availableSlot.value.price * 1 // Assuming only one customer for now
    return base - discountAmount.value * 1
})

/** ========== Phương thức thanh toán ========== **/
const paymentMethods = [
    { key: 'credit', label: 'Credit Card' },
    { key: 'paypal', label: 'PayPal' },
    { key: 'googlepay', label: 'Google Pay' },
]
const activeMethod = ref('credit')

/** ========== Dữ liệu cho form credit card ========== **/
const card = reactive({
    cardNumber: '',
    cardHolder: '',
    expiryDate: '',
    cvv: '',
    saveCard: false,
})

/** ========== Các hàm hành động ========== **/
function formatCurrency(value) {
    if (!value) return '0'
    // format 5000000 => "5.000.000"
    return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.')
}

function formatTime(timeString) {
    if (!timeString) return 'N/A'
    const date = new Date(timeString)
    return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })
}

function formatDate(dateString) {
    if (!dateString) return 'N/A'
    const date = new Date(dateString)
    return date.toLocaleDateString('vi-VN', { 
        weekday: 'long', 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric' 
    })
}

function goBack() {
    router.back()
}

function payWithPayPal() {
    // Ở đây bạn có thể gọi SDK PayPal hoặc redirect
    alert('Chức năng PayPal chưa được tích hợp thực tế.')
}

function payWithGooglePay() {
    // Gọi Google Pay SDK
    alert('Chức năng Google Pay chưa được tích hợp thực tế.')
}

function confirmAndPay() {
    // Validate: chắc chắn điền đúng thông tin hành khách + payment
    // Ví dụ:
    const invalidCustomer = !customer.value.fullName || !customer.value.phone || !customer.value.email
    if (invalidCustomer) {
        alert('Vui lòng điền đầy đủ thông tin hành khách.')
        return
    }

    if (activeMethod.value === 'credit') {
        if (!card.cardNumber || !card.cardHolder || !card.expiryDate || !card.cvv) {
            alert('Vui lòng điền đầy đủ thông tin thẻ tín dụng.')
            return
        }
    }

    // Giả sử gọi API tạo đơn thanh toán:
    // await api.createOrder({ flightId: flight.id, passengers: passengers.value, paymentMethod: activeMethod, cardInfo: card, total: subtotal.value, ... })

    alert('Thanh toán thành công! Cảm ơn bạn đã đặt vé.')
    // Sau khi thanh toán xong, chuyển hướng về trang hoàn tất hoặc trang Dashboard người dùng
    router.push({ name: 'BookingSuccess', params: { orderId: 'ABC12345' } })
}

const bookingId = ref('') // Lấy bookingId từ route hoặc props thực tế
const paymentMethod = ref('credit_card')
const paymentStatus = ref(null)

async function handlePayment() {
  loading.value = true
  error.value = ''
  try {
    const res = await payForFlight({ bookingId: bookingId.value, paymentMethod: paymentMethod.value })
    paymentStatus.value = res.data
  } catch (e) {
    error.value = 'Thanh toán thất bại.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* Bạn có thể bổ sung thêm style nếu cần */
</style>
