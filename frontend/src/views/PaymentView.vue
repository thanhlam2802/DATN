<script setup>
import { ref, onMounted, onUnmounted, computed } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const orderId = route.params.orderId;

const orderDetails = ref(null);
const timeLeft = ref(0); 
const timerInterval = ref(null);
const hasExpired = ref(false);

const minutes = computed(() => Math.floor(timeLeft.value / 60));
const seconds = computed(() => timeLeft.value % 60);

// Hàm để gọi API lấy chi tiết đơn hàng
const fetchOrderDetails = async () => {
  try {
    const response = await fetch(
      `http://localhost:8080/api/v1/orders/${orderId}`
    );
    const result = await response.json();
    if (response.ok) {
      orderDetails.value = result.data;
      // Bắt đầu đếm ngược sau khi có dữ liệu
      startCountdown(result.data.expiresAt);
    } else {
      console.error("Lỗi khi lấy thông tin đơn hàng:", result.message);
    }
  } catch (error) {
    console.error("Lỗi mạng:", error);
  }
};

// Hàm bắt đầu đếm ngược dựa trên thời gian từ server
const startCountdown = (expiresAtString) => {
  if (!expiresAtString) return;

  const expirationTime = new Date(expiresAtString).getTime();

  // Cập nhật thời gian còn lại mỗi giây
  timerInterval.value = setInterval(() => {
    const now = new Date().getTime();
    const remaining = Math.round((expirationTime - now) / 1000);

    if (remaining > 0) {
      timeLeft.value = remaining;
    } else {
      timeLeft.value = 0;
      hasExpired.value = true;
      clearInterval(timerInterval.value);
    }
  }, 1000);
};

// Lấy dữ liệu khi component được tạo
onMounted(fetchOrderDetails);

// Dọn dẹp interval khi component bị hủy
onUnmounted(() => {
  clearInterval(timerInterval.value);
});

const formatPrice = (price) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    price || 0
  );
</script>

<template>
  <div
    class="bg-gray-50 min-h-screen flex items-center justify-center py-12 px-4"
  >
    <div class="max-w-lg w-full bg-white p-8 rounded-2xl shadow-lg">
      <div class="text-center">
        <h1 class="text-2xl font-bold text-gray-800">Thanh toán Đơn hàng</h1>
        <p class="text-gray-500 mt-2">
          Vui lòng hoàn tất thanh toán để xác nhận đặt chỗ của bạn.
        </p>
      </div>

      <div
        class="mt-8 p-4 border border-red-200 bg-red-50 rounded-lg text-center"
      >
        <p v-if="!hasExpired" class="text-sm text-red-700">
          Thời gian giữ chỗ của bạn sẽ hết sau:
        </p>
        <p v-else class="text-sm font-semibold text-red-700">
          Đã hết thời gian giữ chỗ!
        </p>
        <p
          class="text-3xl font-mono font-bold text-red-600 tracking-wider mt-1"
        >
          <span>{{ String(minutes).padStart(2, "0") }}</span>
          <span class="animate-pulse">:</span>
          <span>{{ String(seconds).padStart(2, "0") }}</span>
        </p>
      </div>

      <div v-if="orderDetails" class="mt-6 border-t pt-6">
        <h2 class="text-lg font-semibold mb-3">
          Chi tiết đơn hàng #{{ orderDetails.id }}
        </h2>
        <div class="space-y-2 text-sm">
          <div class="flex justify-between">
            <span class="text-gray-600">Tổng tiền:</span>
            <span class="font-medium text-lg text-blue-600">{{
              formatPrice(orderDetails.amount)
            }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-600">Trạng thái:</span>
            <span
              class="font-medium text-yellow-600 bg-yellow-100 px-2 py-0.5 rounded-full"
              >{{ orderDetails.status }}</span
            >
          </div>
          <div class="flex justify-between">
            <span class="text-gray-600">Ngày tạo:</span>
            <span class="font-medium">{{
              new Date(orderDetails.createdAt).toLocaleString("vi-VN")
            }}</span>
          </div>
        </div>
      </div>

      <div class="mt-8">
        <h3 class="text-lg font-semibold mb-4">Chọn phương thức thanh toán</h3>
        <div class="space-y-3">
          <button
            :disabled="hasExpired"
            class="w-full flex items-center justify-between p-4 border rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <div class="flex items-center gap-4">
              <img
                src="https://upload.wikimedia.org/wikipedia/vi/f/fe/MoMo_Logo.png"
                alt="MoMo"
                class="h-8 w-8 object-contain"
              />
              <span class="font-medium">Ví MoMo</span>
            </div>
            <i class="fas fa-chevron-right text-gray-400"></i>
          </button>
          <button
            :disabled="hasExpired"
            class="w-full flex items-center justify-between p-4 border rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <div class="flex items-center gap-4">
              <img
                src="https://vnpay.vn/s1/statics.vnpay.vn/2023/9/06n1b74p/logo-vnpay-qr.png"
                alt="VNPAY"
                class="h-8 object-contain"
              />
              <span class="font-medium">VNPAY-QR</span>
            </div>
            <i class="fas fa-chevron-right text-gray-400"></i>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
