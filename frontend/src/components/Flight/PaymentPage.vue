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
                                :class="`w-full border rounded-lg px-4 py-2 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 ${validationErrors.fullName ? 'border-red-500' : 'border-gray-300'}`" />
                            <div v-if="validationErrors.fullName" class="text-red-500 text-sm mt-1">
                                {{ validationErrors.fullName[0] }}
                            </div>
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
                                :class="`w-full border rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500 ${validationErrors.dob ? 'border-red-500' : 'border-gray-300'}`" />
                            <div v-if="validationErrors.dob" class="text-red-500 text-sm mt-1">
                                {{ validationErrors.dob[0] }}
                            </div>
                        </div>
                        <!-- Số hộ chiếu -->
                        <div>
                            <label class="block text-sm font-medium text-gray-600 mb-1">Số hộ chiếu</label>
                            <input v-model="customer.passport" type="text" placeholder="Nhập số hộ chiếu"
                                :class="`w-full border rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500 ${validationErrors.passport ? 'border-red-500' : 'border-gray-300'}`" />
                            <div v-if="validationErrors.passport" class="text-red-500 text-sm mt-1">
                                {{ validationErrors.passport[0] }}
                            </div>
                        </div>
                        <!-- Email -->
                        <div>
                            <label class="block text-sm font-medium text-gray-600 mb-1">Email</label>
                            <input v-model="customer.email" type="email" placeholder="Nhập email"
                                :class="`w-full border rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500 ${validationErrors.email ? 'border-red-500' : 'border-gray-300'}`" />
                            <div v-if="validationErrors.email" class="text-red-500 text-sm mt-1">
                                {{ validationErrors.email[0] }}
                            </div>
                        </div>
                        <!-- Số điện thoại -->
                        <div>
                            <label class="block text-sm font-medium text-gray-600 mb-1">Số điện thoại</label>
                            <input v-model="customer.phone" type="text" placeholder="Nhập số điện thoại"
                                :class="`w-full border rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500 ${validationErrors.phone ? 'border-red-500' : 'border-gray-300'}`" />
                            <div v-if="validationErrors.phone" class="text-red-500 text-sm mt-1">
                                {{ validationErrors.phone[0] }}
                            </div>
                        </div>
                    </div>
                </section>
                
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
                    <div v-if="activeCartId">
                        <button @click="addFlightToCart"
                        class="w-full block text-center bg-indigo-600 hover:bg-indigo-700 text-white font-semibold p-3 rounded-lg transition-colors shadow-md">
                        Thêm vào đơn hàng hiện tại
                </button>
                    </div>
                    <div >
                        <button @click="confirmAndPay"
                        class="w-full block text-center bg-indigo-600 hover:bg-indigo-700 text-white font-semibold p-3 rounded-lg transition-colors shadow-md">
                        Đặt chỗ
                </button>
                    </div>
                </div>
                
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { payForFlight, getFlightDetailPublic, findFirstAvailableSlot, reserveFlightDirect } from '@/api/flightApi'
import BankTransferForm from './BankTransferForm.vue'
import { addItemToCart } from '@/api/OrderApi'
import { createCustomer } from '@/api/CustomerApi'
import { validateForm, customerInfoSchema } from '@/utils/validation'
const router = useRouter()
const route = useRoute()

let activeCartId = localStorage.getItem('activeCartId');


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

// Validation errors
const validationErrors = ref({})


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

async function addFlightToCart() {
    const customerResponse = await createCustomer(customer.value)
    const customerId = customerResponse.data.data.id;
    const data = {
    itemId: flight.value.id,
    itemType: "FLIGHT",  
    flightSlotId: availableSlot.value.id,  
    customerId: customerId
}

    const response = await addItemToCart(activeCartId, data)
    const result = response.data
    if (result.statusCode === 200 || result.statusCode === 201) {
            localStorage.removeItem('activeCartId');
            window.$toast('Giữ chỗ thành công! Vui lòng thanh toán trong thời gian quy định.', 'success')
            router.push({ name: 'order-detail',params: {id :result.data.id}})
            console.log(result.data);
        } else {
            window.$toast(result.message || 'Không thể giữ chỗ. Vé có thể đã bị đặt bởi người khác.', 'error')
        }

};

async function confirmAndPay() {
    // Validate customer information
    const { isValid, errors } = validateForm(customer.value, customerInfoSchema)
    validationErrors.value = errors
    
    if (!isValid) {
        window.$toast('Vui lòng kiểm tra lại thông tin hành khách.', 'error')
        return
    }
    
    if (!availableSlot.value) {
        window.$toast('Không tìm thấy thông tin ghế.', 'error')
        return
    }
    loading.value = true
    error.value = ''
    try {
        // Chuẩn bị DTO gửi backend
        const dto = {
            flightSlotId: availableSlot.value.id,
            customerName: customer.value.fullName,
            phone: customer.value.phone,
            email: customer.value.email,
            passport: customer.value.passport,
            gender: customer.value.gender === true ? 'male' : customer.value.gender === false ? 'female' : 'other',
            dob: customer.value.dob,
            notes: '',
        }
        // Gọi API giữ chỗ
        const response = await reserveFlightDirect(dto)
        const result = response.data
        if (response.status === 201 && result.statusCode === 201) {
            window.$toast('Giữ chỗ thành công! Vui lòng thanh toán trong thời gian quy định.', 'success')
            router.push({ name: 'SuccessHold', params: { id: result.data } })
            console.log(result.data);
        } else {
            window.$toast(result.message || 'Không thể giữ chỗ. Vé có thể đã bị đặt bởi người khác.', 'error')
        }
    } catch (e) {
        console.log(e);
        if (e.response && e.response.data && e.response.data.message) {
            window.$toast(e.response.data.message, 'error')
        } else {
            console.log(e);
            window.$toast('Lỗi kết nối máy chủ hoặc lỗi không xác định.', 'error')
        }
    } finally {
        loading.value = false
    }
}



</script>

<style scoped>
/* Bạn có thể bổ sung thêm style nếu cần */
</style>
