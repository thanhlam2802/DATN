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
                    <h2 class="text-lg font-medium text-gray-700 mb-4 z-10 relative">Thông tin hành khách 1</h2>
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-6 z-10 relative">
                        <!-- Họ và tên (theo CMND/CCCD) -->
                        <div>
                            <label for="fullName1" class="block text-sm font-medium text-gray-600 mb-1">Họ và Tên (theo
                                CMND/CCCD)</label>
                            <input id="fullName1" v-model="passengers[0].fullName" type="text"
                                placeholder="Nhập họ tên đầy đủ"
                                class="w-full border border-gray-300 rounded-lg px-4 py-2 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
                        </div>

                        <!-- Số điện thoại -->
                        <div>
                            <label for="phone1" class="block text-sm font-medium text-gray-600 mb-1">Số điện
                                thoại</label>
                            <input id="phone1" v-model="passengers[0].phone" type="text"
                                placeholder="Nhập số điện thoại"
                                class="w-full border border-gray-300 rounded-lg px-4 py-2 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
                        </div>

                        <!-- Email -->
                        <div class="sm:col-span-2">
                            <label for="email1" class="block text-sm font-medium text-gray-600 mb-1">Email</label>
                            <input id="email1" v-model="passengers[0].email" type="email" placeholder="Nhập email"
                                class="w-full border border-gray-300 rounded-lg px-4 py-2 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
                        </div>
                    </div>
                </section>
                <!-- Passenger #2 -->
                <section class="bg-white rounded-lg shadow-md p-6 relative overflow-hidden">
                      <div
                        class="absolute w-34 h-34 bg-sky-300 rounded-full -top-20 -right-40 blur-xl [box-shadow:-100px_50px_30px_100px_#7dd3fc]">
                    </div>
                    <h2 class="text-lg font-medium text-gray-700 mb-4 z-10 relative">Thông tin hành khách 2</h2>
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-6 z-10 relative">
                        <!-- Họ và tên (theo CMND/CCCD) -->
                        <div>
                            <label for="fullName2" class="block text-sm font-medium text-gray-600 mb-1">Họ và Tên (theo
                                CMND/CCCD)</label>
                            <input id="fullName2" v-model="passengers[1].fullName" type="text"
                                placeholder="Nhập họ tên đầy đủ"
                                class="w-full border border-gray-300 rounded-lg px-4 py-2 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
                        </div>
                        <!-- Số điện thoại -->
                        <div>
                            <label for="phone2" class="block text-sm font-medium text-gray-600 mb-1">Số điện
                                thoại</label>
                            <input id="phone2" v-model="passengers[1].phone" type="text"
                                placeholder="Nhập số điện thoại"
                                class="w-full border border-gray-300 rounded-lg px-4 py-2 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
                        </div>
                        <!-- Email -->
                        <div class="sm:col-span-2">
                            <label for="email2" class="block text-sm font-medium text-gray-600 mb-1">Email</label>
                            <input id="email2" v-model="passengers[1].email" type="email" placeholder="Nhập email"
                                class="w-full border border-gray-300 rounded-lg px-4 py-2 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
                        </div>
                    </div>
                </section>
                
            </div>

            <!-- --- Cột phải: Tóm tắt chuyến đi & Thanh toán --- -->
            <div class="lg:col-span-4 space-y-6">
                <!-- 🎫 Thẻ tóm tắt chuyến đi -->
                <div class="bg-white rounded-lg shadow-md p-6">
                    <div class="space-y-3">
                        <!-- Logo hãng + thông tin cơ bản -->
                        <div class="flex items-center space-x-3">
                            <img src="https://ix-marketing.imgix.net/FINALMarketing%20Blog%20image.jpg?ixembed=1720648016530&auto=format,compress"
                                alt="Vietnam Airlines" class="w-10 h-10 object-contain" />
                            <div>
                                <p class="text-sm text-gray-500">HAN → CXR</p>
                                <p class="text-sm text-gray-500">Fri, 13 Jun 2025</p>
                            </div>
                        </div>
                        <!-- Dải giờ và giá -->
                        <div class="flex justify-between items-center pt-2 pb-4 border-t border-gray-200">
                            <div class="text-gray-600 text-sm italic">VN123 • 14:15 – 16:15</div>
                            <div class="text-lg font-semibold text-indigo-600">{{ formatCurrency(flight.price) }} VND
                            </div>
                        </div>
                        <!-- Ô nhập mã giảm giá -->
                        <div class="flex space-x-2">
                            <input v-model="discountCode" type="text" placeholder="Nhập mã giảm giá"
                                class="flex-1 border border-gray-300 rounded-lg px-3 py-2 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
                            <button @click="applyDiscount"
                                class="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg transition-colors">
                                Áp dụng
                            </button>
                        </div>
                        <!-- Tổng tạm tính -->
                        <div class="flex justify-between items-center mt-4">
                            <span class="text-gray-600">Tạm tính:</span>
                            <span class="text-lg font-semibold text-gray-800">{{ formatCurrency(subtotal) }} VND</span>
                        </div>
                    </div>
                </div>

                <!-- 🎯 Phần chọn phương thức thanh toán -->
                <div class="bg-white rounded-lg shadow-md p-6">
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

                <!-- 🎉 Nút Confirm and Pay -->
                <div class="flex justify-center">
                    <router-link to="/plane/getticket" @click="confirmAndPay"
                        class="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-3 rounded-lg transition-colors shadow-md">
                        Confirm and Pay
                    </router-link>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

/** ========== Dữ liệu giả định (bạn có thể replace bằng props hoặc API) ========== **/
const flight = reactive({
    id: 101,
    airline: 'Vietnam Airlines',
    from: 'HAN',
    to: 'CXR',
    date: '2025-06-13',
    flightNumber: 'VN123',
    departTime: '14:15',
    arriveTime: '16:15',
    price: 5000000, // 5.000.000 VND giả định
})

/** ========== Thông tin hành khách (2 người) ========== **/
const passengers = ref([
    { fullName: '', phone: '', email: '' },
    { fullName: '', phone: '', email: '' },
])

/** ========== Mã giảm giá ========== **/
const discountCode = ref('')
const discountAmount = ref(0) // số tiền giảm (ví dụ từ backend trả về)
function applyDiscount() {
    // Giả sử nếu code === "VIETNAM10" thì giảm 10%
    if (discountCode.value.trim().toUpperCase() === 'VIETNAM10') {
        discountAmount.value = Math.floor(flight.price * 0.1)
        alert(`Áp dụng thành công: giảm ${formatCurrency(discountAmount.value)} VND`)
    } else {
        discountAmount.value = 0
        alert('Mã giảm giá không hợp lệ hoặc đã hết hạn')
    }
}

/** ========== Tính toán subtotal (tổng tiền tạm tính) ========== **/
const subtotal = computed(() => {
    // Giả định 2 hành khách
    const base = flight.price * passengers.value.length
    return base - discountAmount.value * passengers.value.length
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
    // format 5000000 => "5.000.000"
    return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.')
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
    const invalidPassenger = passengers.value.some(p => !p.fullName || !p.phone || !p.email)
    if (invalidPassenger) {
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
</script>

<style scoped>
/* Bạn có thể bổ sung thêm style nếu cần */
</style>
