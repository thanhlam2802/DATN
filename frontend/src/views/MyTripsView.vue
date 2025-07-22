<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";

const orders = ref([]);
const isLoading = ref(true);
const error = ref(null);
const selectedStatus = ref("ALL");
const router = useRouter();

const fetchMyOrders = async () => {
  isLoading.value = true;
  error.value = null;
  try {
    const userId = 1;
    const response = await fetch(
      `http://localhost:8080/api/v1/orders/my-orders?userId=${userId}`
    );

    if (!response.ok) {
      throw new Error(`Lỗi server: ${response.status}`);
    }

    const data = await response.json();

    if (data.statusCode === 200) {
      orders.value = data.data;
    } else {
      throw new Error(data.message || "Không thể tải danh sách chuyến đi.");
    }
  } catch (e) {
    console.error("Lỗi khi fetchMyOrders:", e);
    error.value = e.message;
  } finally {
    isLoading.value = false;
  }
};

// Gọi API khi component được tạo
onMounted(fetchMyOrders);

// --- COMPUTED PROPERTIES ---
// Lọc danh sách đơn hàng dựa trên tab được chọn
const filteredOrders = computed(() => {
  if (selectedStatus.value === "ALL") {
    return orders.value;
  }
  return orders.value.filter((order) => order.status === selectedStatus.value);
});

// --- HELPER FUNCTIONS ---
// Định dạng giá tiền
const formatPrice = (price) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    price || 0
  );

// Định dạng ngày tháng
const formatDate = (dateString) =>
  new Date(dateString).toLocaleDateString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });

// Lấy thông tin hiển thị cho từng trạng thái
const getStatusInfo = (status) => {
  switch (status) {
    case "PAID":
      return { text: "Đã thanh toán", color: "green" };
    case "PENDING_PAYMENT":
      return { text: "Chờ thanh toán", color: "orange" };
    case "CANCELLED":
      return { text: "Đã hủy", color: "red" };
    default:
      return { text: status, color: "gray" };
  }
};

/**
 * Xử lý khi người dùng muốn thêm dịch vụ vào đơn hàng đang chờ.
 */
const addMoreServices = (orderId) => {
  localStorage.setItem("activeCartId", orderId);
  router.push("/");
};
</script>

<template>
  <div class="bg-gray-50  w-full">
    <div class="container mx-auto px-4 py-8">
      <h1 class="text-3xl font-bold mb-6">Chuyến đi của tôi</h1>

      <div class="mb-6 border-b border-gray-200">
        <nav class="-mb-px flex space-x-6">
          <button
            @click="selectedStatus = 'ALL'"
            :class="[
              selectedStatus === 'ALL'
                ? 'border-blue-500 text-blue-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300',
            ]"
            class="whitespace-nowrap py-3 px-1 border-b-2 font-medium text-sm"
          >
            Tất cả
          </button>
          <button
            @click="selectedStatus = 'PENDING_PAYMENT'"
            :class="[
              selectedStatus === 'PENDING_PAYMENT'
                ? 'border-orange-500 text-orange-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300',
            ]"
            class="whitespace-nowrap py-3 px-1 border-b-2 font-medium text-sm"
          >
            Chờ thanh toán
          </button>
          <button
            @click="selectedStatus = 'PAID'"
            :class="[
              selectedStatus === 'PAID'
                ? 'border-green-500 text-green-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300',
            ]"
            class="whitespace-nowrap py-3 px-1 border-b-2 font-medium text-sm"
          >
            Đã xác nhận
          </button>
          <button
            @click="selectedStatus = 'CANCELLED'"
            :class="[
              selectedStatus === 'CANCELLED'
                ? 'border-red-500 text-red-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300',
            ]"
            class="whitespace-nowrap py-3 px-1 border-b-2 font-medium text-sm"
          >
            Đã hủy
          </button>
        </nav>
      </div>

      <div v-if="isLoading" class="text-center py-16">
        <i class="fas fa-spinner fa-spin text-4xl text-blue-500"></i>
        <p class="mt-4 text-gray-600">Đang tải các chuyến đi...</p>
      </div>

      <div v-else-if="error" class="text-center py-16 bg-red-50 p-6 rounded-lg">
        <i class="fas fa-exclamation-triangle text-4xl text-red-500"></i>
        <p class="mt-4 font-semibold text-red-700">Đã xảy ra lỗi</p>
        <p class="text-red-600">{{ error }}</p>
      </div>

      <div v-else-if="filteredOrders.length > 0" class="space-y-4">
        <div
          v-for="order in filteredOrders"
          :key="order.id"
          class="bg-white p-4 sm:p-6 rounded-lg shadow-sm border flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4"
        >
          <div class="flex-grow">
            <div class="flex items-center gap-3 mb-2">
              <span class="font-semibold text-gray-800"
                >Đơn hàng #{{ order.id }}</span
              >
              <span
                :class="`bg-${getStatusInfo(order.status).color}-100 text-${
                  getStatusInfo(order.status).color
                }-700`"
                class="text-xs font-medium px-2.5 py-0.5 rounded-full"
              >
                {{ getStatusInfo(order.status).text }}
              </span>
            </div>
            <p class="text-lg font-bold text-gray-900">
              {{ order.mainProduct }}
            </p>
            <p class="text-sm text-gray-500 mt-1">
              Ngày đặt: {{ formatDate(order.createdAt) }}
            </p>
          </div>
          <div class="flex-shrink-0 text-left sm:text-right w-full sm:w-auto">
            <p class="text-sm text-gray-600">Tổng cộng</p>
            <p class="text-xl font-bold text-blue-600">
              {{ formatPrice(order.amount) }}
            </p>

            <div
              class="mt-2 flex flex-col sm:flex-row sm:items-center gap-2 sm:gap-4"
            >
              <button
                v-if="order.status === 'PENDING_PAYMENT'"
                @click="addMoreServices(order.id)"
                class="w-full sm:w-auto text-sm text-gray-700 bg-white border border-gray-300 rounded-md px-3 py-1.5 hover:bg-gray-50"
              >
                Thêm dịch vụ
              </button>

              <router-link
                :to="`/orders/${order.id}`"
                class="w-full sm:w-auto text-sm text-blue-600 hover:underline font-medium text-center"
              >
                Xem chi tiết <i class="fas fa-arrow-right text-xs ml-1"></i>
              </router-link>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="text-center py-16 border-2 border-dashed rounded-lg">
        <i class="fas fa-inbox text-4xl text-gray-400"></i>
        <p class="mt-4 font-semibold text-gray-700">Không có chuyến đi nào</p>
        <p class="text-gray-500">
          Các chuyến đi bạn đã đặt sẽ xuất hiện ở đây.
        </p>
      </div>
    </div>
  </div>
</template>
