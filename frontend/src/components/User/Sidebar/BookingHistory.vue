<template>
  <div class="max-w-4xl mx-auto font-sans text-sm text-blue-900 p-4 sm:p-0">
    <h1 class="text-3xl font-bold mb-6 text-gray-800">Chuyến đi của tôi</h1>

    <div class="grid grid-cols-3 text-center border-b border-blue-200 mb-4">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        @click="activeTab = tab.value"
        :class="[
          'py-2 font-semibold border-b-2 transition whitespace-nowrap',
          activeTab === tab.value
            ? `border-${tab.color}-500 text-${tab.color}-600`
            : 'border-transparent text-gray-500 hover:text-gray-700',
        ]"
      >
        {{ tab.label }}
      </button>
    </div>

    <div v-if="isLoading" class="text-center py-16">
      <i class="fas fa-spinner fa-spin text-4xl text-blue-500"></i>
      <p class="mt-4 text-gray-600">Đang tải các chuyến đi...</p>
    </div>

    <div v-else-if="error" class="text-center py-16 bg-red-50 p-6 rounded-lg">
      <i class="fas fa-exclamation-triangle text-4xl text-red-500"></i>
      <p class="mt-4 font-semibold text-red-700">Đã xảy ra lỗi</p>
      <p class="text-red-600">{{ error }}</p>
      <router-link
        v-if="!user"
        to="/login"
        class="mt-4 inline-block bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700"
      >
        Đi đến trang đăng nhập
      </router-link>
    </div>

    <div v-else>
      <div v-if="filteredOrders.length > 0" class="space-y-4">
        <div
          v-for="order in filteredOrders"
          :key="order.id"
          class="bg-white p-4 sm:p-6 rounded-lg shadow-sm border flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 hover:shadow-md transition-shadow"
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
              {{ order.mainProduct || "Nhiều dịch vụ" }}
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

      <div v-else>
        <div
          class="text-center bg-blue-50 p-6 rounded-lg border border-blue-100 mb-8 mt-4"
        >
          <img
            src="https://img.icons8.com/ios-filled/100/suitcase.png"
            alt="Luggage"
            class="mx-auto mb-4"
            style="
              filter: invert(35%) sepia(50%) saturate(2500%) hue-rotate(200deg)
                brightness(95%) contrast(90%);
            "
          />
          <h2 class="text-xl font-bold text-blue-800 mb-1">
            Chưa có chuyến đi nào trong mục này
          </h2>
          <p class="text-blue-600 text-sm">
            Tất cả các đơn hàng của bạn sẽ được hiển thị ở đây.
          </p>
        </div>

        <div>
          <h3 class="text-lg font-bold mb-4 text-gray-800">
            Bắt đầu lên kế hoạch cho chuyến đi tiếp theo?
          </h3>
          <div class="grid grid-cols-2 sm:grid-cols-3 gap-4">
            <div
              v-for="option in planningOptions"
              :key="option.label"
              class="bg-blue-50 border border-blue-200 p-4 rounded-lg text-center hover:bg-white hover:shadow-lg cursor-pointer transition"
              @click="goTo(option.path)"
            >
              <img :src="option.icon" alt="" class="w-10 h-10 mx-auto mb-2" />
              <p class="text-sm font-semibold text-blue-700">
                {{ option.label }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import {
  getAccessToken,
  clearToken,
  getBearerToken,
} from "@/services/TokenService"; // Giả định đường dẫn

// --- STATE MANAGEMENT ---
const orders = ref([]);
const isLoading = ref(true);
const error = ref(null);
const user = ref(null);
const router = useRouter();

// Trạng thái cho UI
const tabs = ref([
  { label: "Chờ thanh toán", value: "PENDING_PAYMENT", color: "orange" },
  { label: "Hoàn tất", value: "PAID", color: "green" },
  { label: "Đã hủy", value: "CANCELLED", color: "red" },
]);
const activeTab = ref("PENDING_PAYMENT"); // Mặc định là tab 'Chờ thanh toán'

const planningOptions = ref([
  {
    label: "Tìm Nơi lưu trú",
    icon: "https://img.icons8.com/ios/50/bed.png",
    path: "/hotels",
  },
  {
    label: "Tìm Chuyến bay",
    icon: "https://img.icons8.com/ios/50/airplane-take-off.png",
    path: "/flights",
  },
  {
    label: "Khám phá Tours",
    icon: "https://img.icons8.com/ios/50/bus.png",
    path: "/tours",
  },
]);

// --- API & DATA FETCHING ---
const fetchMyOrders = async () => {
  if (!user.value) {
    error.value = "Bạn cần đăng nhập để xem các chuyến đi.";
    isLoading.value = false;
    return;
  }

  isLoading.value = true;
  error.value = null;
  try {
    const userId = user.value.userId;
    const response = await fetch(
      `http://localhost:8080/api/v1/orders/my-orders?userId=${userId}`,
      {
        headers: {
          Authorization: getBearerToken(),
          "Content-Type": "application/json",
        },
      }
    );

    if (!response.ok) {
      if (response.status === 401 || response.status === 403) {
        throw new Error(`Xác thực thất bại. Vui lòng đăng nhập lại.`);
      }
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

// --- LIFECYCLE HOOKS ---
onMounted(() => {
  const token = getAccessToken();
  if (token) {
    try {
      const payloadBase64 = token.split(".")[1];
      const decodedJson = atob(
        payloadBase64.replace(/-/g, "+").replace(/_/g, "/")
      );
      user.value = JSON.parse(decodedJson);
      fetchMyOrders();
    } catch (e) {
      console.error("Token không hợp lệ, đang xóa token:", e);
      clearToken();
      error.value = "Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.";
      isLoading.value = false;
    }
  } else {
    error.value = "Bạn cần đăng nhập để xem trang này.";
    isLoading.value = false;
  }
});

// --- COMPUTED PROPERTIES ---
const filteredOrders = computed(() => {
  return orders.value.filter((order) => order.status === activeTab.value);
});

// --- HELPER & EVENT HANDLER FUNCTIONS ---
const formatPrice = (price) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    price || 0
  );

const formatDate = (dateString) =>
  new Date(dateString).toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });

const getStatusInfo = (status) => {
  const statusMap = {
    PAID: { text: "Hoàn tất", color: "green" },
    PENDING_PAYMENT: { text: "Chờ thanh toán", color: "orange" },
    CANCELLED: { text: "Đã hủy", color: "red" },
  };
  return statusMap[status] || { text: status, color: "gray" };
};

const addMoreServices = (orderId) => {
  localStorage.setItem("activeCartId", orderId);
  router.push("/");
};

const goTo = (path) => {
  if (path) router.push(path);
};
</script>

<style scoped>
/* Scoped styles nếu cần */
</style>
