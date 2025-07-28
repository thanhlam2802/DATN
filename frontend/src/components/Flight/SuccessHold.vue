<template>
  <main class="max-w-3xl mx-auto mt-12 px-2 md:px-0 font-inter rounded-2xl">
    <div class="flex flex-col items-center mb-10">
      <div class="relative mb-4">
        <div class="bg-gradient-to-tr from-indigo-500 via-sky-400 to-cyan-300 rounded-full p-6 shadow-2xl">
          <i class="fa-solid fa-plane-departure text-white text-6xl drop-shadow-lg"></i>
        </div>
        
      </div>
      <h1 class="text-4xl font-extrabold text-transparentfrom-green-500 text-indigo-500 mb-2  drop-shadow">Giữ chỗ thành công!</h1>
      <p class="text-gray-700 text-center mb-2 text-lg">Vui lòng hoàn tất thanh toán trước khi hết hạn để đảm bảo vé của bạn.</p>
      <div v-if="countdown" class="flex items-center gap-2 mt-2">
        <i class="fa-regular fa-clock text-red-500 text-2xl"></i>
        <span class="text-2xl font-mono font-bold text-white bg-gradient-to-tr from-red-500 via-yellow-400 to-green-400 px-4 py-1 rounded-lg shadow-lg tracking-widest">
          {{ countdown }}
        </span>
      </div>
    </div>
    <section class="bg-white/80 backdrop-blur-lg border border-gray-200 rounded-3xl shadow-2xl p-8 text-base text-gray-800 space-y-10">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
        <!-- Thông tin giữ chỗ -->
        <div class="rounded-2xl bg-gradient-to-br from-indigo-50 via-white to-sky-50 shadow-lg p-6 border border-indigo-100 hover:shadow-indigo-200 transition-shadow duration-300">
          <h2 class="font-bold text-indigo-700 mb-4 flex items-center gap-2 text-lg">
            <i class="fa-solid fa-ticket-alt"></i> Thông tin giữ chỗ
          </h2>
          <ul class="space-y-2 text-base">
            <li><span class="font-semibold">Ngày giữ chỗ:</span> {{ formatDate(order.createdAt) }}</li>
            <li><span class="font-semibold">Hết hạn:</span> {{ formatDate(order.expiresAt) }}</li>
            <li>
              <span class="font-semibold">Số tiền cần thanh toán:</span>
              <span class="text-green-700 font-extrabold text-lg">{{ formatCurrency(order.amount) }}</span>
            </li>
            <li><span class="font-semibold">Mã giữ chỗ:</span> <span class="bg-indigo-100 px-2 py-1 rounded-lg font-mono">{{ order.id }}</span></li>
            <li>
              <span class="font-semibold">Trạng thái:</span>
              <span :class="order.status === 'PENDING_PAYMENT' ? 'bg-yellow-100 text-yellow-700' : 'bg-green-100 text-green-700'" class="px-2 py-1 rounded-lg font-bold ml-2">
                {{ order.status === 'PENDING_PAYMENT' ? 'Chờ thanh toán' : order.status }}
              </span>
            </li>
          </ul>
        </div>
        <!-- Thông tin vé/ghế -->
        <div class="rounded-2xl bg-gradient-to-br from-indigo-100 via-white to-sky-100 shadow-lg p-6 border border-indigo-200 hover:shadow-indigo-300 transition-shadow duration-300">
          <h2 class="font-bold text-indigo-700 mb-4 flex items-center gap-2 text-lg">
            <i class="fa-solid fa-chair"></i> Thông tin vé/ghế
          </h2>
          <div v-if="flightSlot" class="space-y-2">
            <div class="flex items-center gap-2">
              <span class="font-semibold">Mã ghế:</span>
              <span class="bg-indigo-200 px-2 py-1 rounded font-mono">{{ flightSlot.id }}</span>
            </div>
            <div><span class="font-semibold">Số ghế:</span> {{ flightSlot.seatNumber }}</div>
            <div>
              <span class="font-semibold">Loại ghế:</span>
              <span :class="flightSlot.isBusiness ? 'text-yellow-700 font-bold' : 'text-indigo-700 font-bold'">
                {{ flightSlot.isBusiness ? 'Thương gia' : 'Phổ thông' }}
              </span>
            </div>
            <div>
              <span class="font-semibold">Vị trí:</span>
              <span>{{ flightSlot.isWindow ? 'Cửa sổ' : (flightSlot.isAisle ? 'Lối đi' : 'Khác') }}</span>
            </div>
            <div>
              <span class="font-semibold">Giá vé:</span>
              <span class="font-bold text-green-700">{{ formatCurrency(flightSlot.price) }}</span>
            </div>
            <div>
              <span class="font-semibold">Hành lý xách tay:</span> {{ flightSlot.carryOnLuggage }} kg
            </div>
          </div>
          <div v-else class="text-gray-400 italic">Không tìm thấy thông tin vé/ghế.</div>
        </div>
      </div>
      <!-- Thông tin khách hàng -->
      <div class="rounded-2xl bg-gradient-to-br from-cyan-50 via-white to-indigo-50 shadow-lg p-6 border border-cyan-100 hover:shadow-cyan-200 transition-shadow duration-300">
        <h2 class="font-bold text-indigo-700 mb-4 flex items-center gap-2 text-lg">
          <i class="fa-regular fa-user"></i> Thông tin khách hàng
        </h2>
        <div v-if="customer" class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div><span class="font-semibold">Họ tên:</span> {{ customer.fullName }}</div>
          <div><span class="font-semibold">Số điện thoại:</span> {{ customer.phone }}</div>
          <div><span class="font-semibold">Email:</span> {{ customer.email }}</div>
          <div><span class="font-semibold">Giới tính:</span> {{ customer.gender ? 'Nam' : 'Nữ' }}</div>
          <div><span class="font-semibold">Ngày sinh:</span> {{ customer.dob }}</div>
          <div><span class="font-semibold">Passport:</span> {{ customer.passport }}</div>
        </div>
        <div v-else class="text-gray-400 italic">Không tìm thấy thông tin khách hàng.</div>
      </div>
      <div class="flex flex-col items-center mt-8">
        <router-link
          :to="`/plane/pay?orderId=${order.id}`"
          class="bg-gradient-to-tr from-indigo-500 via-sky-400 to-cyan-300 hover:from-indigo-600 hover:to-sky-500 text-white font-bold py-4 px-16 rounded-2xl text-xl transition-all duration-300 "
        >
          <i class="fa-solid fa-credit-card mr-2"></i> Thanh toán ngay
        </router-link>
        <span class="text-xs text-gray-400 mt-2">Bạn có thể thanh toán sau trong mục Đơn hàng của tôi.</span>
      </div>
    </section>
  </main>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;600;800&display=swap');
main {
  font-family: 'Inter', 'Segoe UI', Arial, sans-serif;
  background: linear-gradient(120deg, #f0f4ff 0%, #e0f7fa 100%);
}
.bg-gradient-to-tr {
  background: linear-gradient(135deg, #6366f1 0%, #38bdf8 100%);
}
.shadow-2xl {
  box-shadow: 0 10px 40px 0 rgba(80, 80, 180, 0.12), 0 2px 4px 0 rgba(0,0,0,0.04);
}
.animate-glow {
  box-shadow: 0 0 16px 4px #38bdf8, 0 0 32px 8px #6366f1;
  animation: glow 2s infinite alternate;
}
@keyframes glow {
  0% { box-shadow: 0 0 16px 4px #38bdf8, 0 0 32px 8px #6366f1; }
  100% { box-shadow: 0 0 32px 8px #6366f1, 0 0 16px 4px #38bdf8; }
}
</style>

<script setup>
import { useRoute } from 'vue-router'
import { ref, onMounted, onUnmounted } from 'vue'
import { getFlightReservationSummary } from '@/api/flightApi'

const route = useRoute()
const bookingId = route.params.id || route.query.id
const order = ref({})
const booking = ref(null)
const customer = ref(null)
const flightSlot = ref(null)
const loading = ref(true)

const countdown = ref('')
let timer = null

function formatDate(val) {
  if (!val) return ''
  const d = new Date(val)
  return d.toLocaleString('vi-VN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}
function formatCurrency(val) {
  if (!val) return ''
  return Number(val).toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })
}
function updateCountdown() {
  if (!order.value.expiresAt) {
    countdown.value = ''
    return
  }
  const now = new Date()
  const expires = new Date(order.value.expiresAt)
  let diff = Math.floor((expires - now) / 1000)
  if (diff <= 0) {
    countdown.value = 'Đã hết hạn giữ chỗ'
    clearInterval(timer)
    return
  }
  const m = Math.floor(diff / 60)
  const s = diff % 60
  countdown.value = `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}

onMounted(async () => {
  loading.value = true
  try {
    // Lấy thông tin tổng hợp giữ chỗ chuyến bay
    const res = await getFlightReservationSummary(bookingId)
    order.value = res.data.order
    booking.value = res.data.booking
    customer.value = res.data.customer
    flightSlot.value = res.data.flightSlot
    updateCountdown()
    timer = setInterval(updateCountdown, 1000)
  } catch (e) {
    // Xử lý lỗi nếu cần
  } finally {
    loading.value = false
  }
})
onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

