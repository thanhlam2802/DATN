<template>
  <div class="min-h-screen bg-gray-50 flex items-center justify-center p-4 mt-8">
    <!-- Container chính (giới hạn chiều ngang max-w-4xl) -->
    <div class="max-w-4xl w-full">
      <!-- Card chính -->
      <div class="relative bg-white rounded-2xl p-8 border border-gray-200 flex flex-col lg:flex-row gap-8">
        <!-- Góc bo viền xanh dương ở 4 góc -->
        <div class="absolute top-0 left-0 w-20 h-20 border-t-2 border-l-2 border-blue-600 rounded-tl-2xl"></div>
        <div class="absolute top-0 right-0 w-20 h-20 border-t-2 border-r-2 border-blue-600 rounded-tr-2xl"></div>
        <div class="absolute bottom-0 left-0 w-20 h-20 border-b-2 border-l-2 border-blue-600 rounded-bl-2xl"></div>
        <div class="absolute bottom-0 right-0 w-20 h-20 border-b-2 border-r-2 border-blue-600 rounded-br-2xl"></div>

        <!-- Badge ngày bay (đã định vị tuyệt đối) -->
        <div class="absolute -top-12 left-0 px-6 py-2 bg-blue-600 rounded-full text-sm text-white">
          {{ formatDate(flightDate) }}
        </div>

        <!-- ===== Bên trái: Thông tin vé ===== -->
        <div class="flex-1 flex flex-col gap-6">
          <!-- Logo + Tiêu đề chính -->
          <div class="flex items-center space-x-3">
            <img
              src="https://www.vietnamairlines.com/~/media/Images/VNANew/Home/Logo/logo_vna-mobile.png"
              alt="Vietnam Airlines"
              class="h-10 w-auto"
            />
            <h2 class="text-3xl font-bold text-black">Vietnam Airlines</h2>
          </div>

          <!-- Subtitle: Lộ trình bay (gradient xanh dương) -->
          <h3
            class="text-2xl font-semibold text-transparent bg-clip-text bg-gradient-to-t from-blue-600 to-blue-300"
          >
            {{ departureCode }} → {{ arrivalCode }}
          </h3>

          <!-- Mô tả chi tiết (description) -->
          <p class="text-lg text-gray-700">
            <span class="font-medium">Sân bay đi:</span> {{ departureAirport }}<br />
            <span class="font-medium">Thời gian bay:</span> {{ flightTime }}<br />
            <span class="font-medium">Số hành khách:</span> {{ passengers }} &bull;
            <span class="font-medium">Hạng ghế:</span> {{ seatClass }} &bull;
            <span class="font-medium">Hành lý:</span> {{ baggage }}
          </p>

          <!-- Nhóm tags thông tin: giá, mã đặt chỗ, ngày phát hành -->
          <div class="flex flex-wrap gap-3">
            <span
              class="px-4 py-2 rounded-full bg-blue-50 border border-blue-200 text-blue-700 text-sm"
            >
              Giá vé: {{ formattedPrice }}
            </span>
            <span
              class="px-4 py-2 rounded-full bg-blue-50 border border-blue-200 text-blue-700 text-sm"
            >
              Mã đặt chỗ: {{ reservationCode }}
            </span>
            <span
              class="px-4 py-2 rounded-full bg-blue-50 border border-blue-200 text-blue-700 text-sm"
            >
              Ngày phát hành: {{ formattedDateShort(flightDate) }}
            </span>
          </div>

          <!-- Thẻ thông tin chuyến bay (card con) -->
          <div class="flex flex-col sm:flex-row bg-gray-100 rounded-lg overflow-hidden border border-gray-200">
            <!-- Ảnh minh hoạ -->
            <div class="sm:w-1/3 w-full h-40">
              <img
                :src="flightImage"
                alt="Flight Image"
                class="object-cover w-full h-full"
              />
            </div>
            <!-- Thông tin chi tiết -->
            <div class="sm:w-2/3 w-full p-4 flex flex-col justify-between">
              <div>
                <p class="text-sm text-gray-500 mb-1">Vietnam Airlines</p>
                <h4 class="text-xl font-semibold text-gray-800 mb-1">
                  {{ departureCode }} → {{ arrivalCode }}
                </h4>
                <p class="text-gray-600 text-sm">
                  {{ passengers }} hành khách &bull; {{ seatClass }} &bull; {{ baggage }}
                </p>
              </div>
              <div class="mt-4">
                <button
                  class="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition"
                >
                  Một chiều
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- ===== Bên phải: QR Code + Nút ===== -->
        <div class="w-full lg:w-1/3 flex flex-col items-center gap-6">
          <!-- QR Code -->
        <!-- QR Code with Uiverse-style spinner -->
<div class="flex items-center justify-center">
  <!-- Wrapper chính để căn giữa -->
  <div class="relative w-40 h-40">
    <!-- Vòng spinner thứ nhất (xoay thuận) -->
    <div
      class="absolute inset-0 rounded-full border-[3px] border-gray-100/10 border-r-[#4f39f6] border-b-[#4f39f6] animate-spin"
      style="animation-duration: 3s;"
    ></div>

    <!-- Vòng spinner thứ hai (xoay ngược) -->
    <div
      class="absolute inset-0 rounded-full border-[3px] border-gray-100/10 border-t-[#4f39f6] animate-spin"
      style="animation-duration: 2s; animation-direction: reverse;"
    ></div>

    <!-- Lớp glow (mờ) chạy hiệu ứng pulse phía sau spinner -->
    <div
      class="absolute inset-0 rounded-full bg-gradient-to-tr from-[#4f39f6]/10 via-transparent to-[#4f39f6]/5 animate-pulse blur-sm"
    ></div>

    <!-- Ảnh QR code đặt ở chính giữa -->
    <div class="absolute inset-0 flex items-center justify-center">
      <img
        :src="qrCodeImage"
        alt="QR Code"
        class="w-32 h-32 object-contain rounded-md shadow-lg"
      />
    </div>
  </div>
</div>

          <p class="text-gray-600 text-center">
            Vui lòng lưu mã QR để lấy vé
          </p>

          <!-- Nút hành động -->
          <div class="flex flex-col gap-4 w-full">
            <a
              href="#"
              class="group flex items-center justify-center gap-2 px-6 py-3 rounded-full bg-blue-600 hover:bg-blue-700 transition text-white text-sm font-medium"
            >
              Tải vé (Download)
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="16"
                height="16"
                class="group-hover:rotate-12 transition-transform duration-300"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path d="M12 5v14m7-7H5"></path>
              </svg>
            </a>

            <a
              href="#"
              class="group flex items-center justify-center gap-2 px-6 py-3 rounded-full border border-blue-600 hover:bg-blue-50 transition-colors text-blue-600 text-sm font-medium"
            >
              In vé (Print)
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="group-hover:rotate-12 transition-transform duration-300"
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path d="M6 9V2h12v7m-6 3v8m-6-6h12v6H6z"></path>
              </svg>
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getFlightBookingDetail } from '@/api/flightApi'

const bookingId = ref('') // Lấy bookingId từ route hoặc props thực tế
const ticket = ref(null)
const loading = ref(false)
const error = ref('')

onMounted(async () => {
  loading.value = true
  try {
    const res = await getFlightBookingDetail(bookingId.value)
    ticket.value = res.data
  } catch (e) {
    error.value = 'Không thể tải thông tin vé.'
  } finally {
    loading.value = false
  }
})

// --- Dữ liệu mẫu (bạn có thể thay bằng props hoặc API) ---
const flightDate   = ref(new Date(2025, 5, 13)) // Tháng bắt đầu từ 0 → 5 = tháng 6
const departureAirport = ref('Cảng Hàng Không Nha Trang')
const flightTime       = ref('2:00 giờ')
const price            = ref(5000000)
const reservationCode  = ref('HMNK55PBZK')
const flightImage      = ref('https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=60')
const departureCode    = ref('HAN')
const arrivalCode      = ref('CXR')
const passengers       = ref('2')
const seatClass        = ref('Phổ thông')
const baggage          = ref('7kg hành lý')
// QR code (generate placeholder từ API)
const qrCodeImage      = ref('https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=' + reservationCode.value)

// --- Hàm hỗ trợ format ---
function formatDate(date) {
  // Ví dụ: Fri, 13 Jun 2025
  const options = { weekday: 'short', day: '2-digit', month: 'short', year: 'numeric' }
  return date.toLocaleDateString('en-US', options)
}
function formattedDateShort(date) {
  // Ví dụ: 13/06/2025
  const d = date.getDate().toString().padStart(2, '0')
  const m = (date.getMonth() + 1).toString().padStart(2, '0')
  const y = date.getFullYear()
  return `${d}/${m}/${y}`
}
const formattedPrice = computed(() => {
  return price.value.toLocaleString('vi-VN') + ' VND'
})
</script>

<style>
/* Toàn bộ styling đã sử dụng Tailwind CSS, không cần thêm CSS thuần */
</style>
