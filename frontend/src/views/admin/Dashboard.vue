<template>
  <div class="space-y-6">
    <!-- Bộ lọc Lịch Mới -->
    <div class="flex justify-end items-center gap-x-2 mb-2">
      <label for="filterType" class="text-sm font-medium text-gray-600"
        >Xem theo:</label
      >
      <select
        v-model="filterType"
        id="filterType"
        class="bg-white border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block p-2"
      >
        <option value="day">Ngày</option>
        <option value="month">Tháng</option>
        <option value="year">Năm</option>
      </select>

      <input
        v-if="filterType === 'day'"
        type="date"
        v-model="selectedDate"
        class="bg-white border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block p-1.5"
      />
      <input
        v-if="filterType === 'month'"
        type="month"
        v-model="selectedMonth"
        class="bg-white border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block p-1.5"
      />
      <input
        v-if="filterType === 'year'"
        type="number"
        placeholder="YYYY"
        v-model="selectedYear"
        class="bg-white border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-28 p-1.5"
      />

      <button
        @click="applyFilter"
        class="px-5 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 focus:ring-4 focus:outline-none focus:ring-blue-300"
      >
        Xem
      </button>
    </div>

    <!-- Widgets -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
      <Widget
        :title="`Doanh thu ${filterTitleText}`"
        icon="fas fa-coins"
        :value="formatCurrency(summary.todayRevenue)"
      />
      <Widget
        :title="`Booking mới ${filterTitleText}`"
        icon="fas fa-ticket-alt"
        :value="summary.newBookings"
      />
      <Widget
        :title="`Khách hàng mới ${filterTitleText}`"
        icon="fas fa-users"
        :value="summary.newCustomers"
      />
      <Widget
        title="Đánh giá chờ duyệt"
        icon="fas fa-comments"
        :value="summary.pendingReviews"
      />
    </div>

    <!-- Charts -->
    <div class="grid grid-cols-1 lg:grid-cols-5 gap-6">
      <div class="lg:col-span-3 bg-white rounded-xl shadow p-6">
        <div class="flex justify-between items-center mb-4">
          <h3 class="font-semibold text-gray-700">Biểu đồ doanh thu</h3>
          <div class="flex space-x-2">
            <button
              @click="fetchRevenueData('day')"
              :class="getPeriodButtonClass('day')"
            >
              7 Ngày
            </button>
            <button
              @click="fetchRevenueData('month')"
              :class="getPeriodButtonClass('month')"
            >
              Tháng
            </button>
            <button
              @click="fetchRevenueData('year')"
              :class="getPeriodButtonClass('year')"
            >
              Năm
            </button>
          </div>
        </div>
        <RevenueChart
          v-if="!isLoadingRevenue && revenueData.labels.length > 0"
          :data="revenueData"
        />
        <div v-else class="flex items-center justify-center h-64 text-gray-500">
          <p>
            {{
              isLoadingRevenue
                ? "Đang tải dữ liệu..."
                : "Không có dữ liệu để hiển thị."
            }}
          </p>
        </div>
      </div>
      <div class="lg:col-span-2 bg-white rounded-xl shadow p-6">
        <h3 class="font-semibold text-gray-700 mb-4">Biểu đồ booking</h3>
        <BookingChart
          v-if="!isLoadingBookingData && bookingData.labels.length > 0"
          :data="bookingData"
        />
        <div v-else class="flex items-center justify-center h-64 text-gray-500">
          <p>
            {{
              isLoadingBookingData
                ? "Đang tải dữ liệu..."
                : "Không có dữ liệu để hiển thị."
            }}
          </p>
        </div>
      </div>
    </div>

    <!-- Recent Activities & Orders -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div class="lg:col-span-3 ">
        <div class="bg-white rounded-xl shadow p-6">
          <div class="font-semibold mb-4 text-gray-700">Đơn hàng gần đây</div>
          <div class="overflow-x-auto">
            <table class="w-full text-sm text-left">
              <thead class="text-xs text-gray-500 uppercase bg-gray-50">
                <tr>
                  <th scope="col" class="px-4 py-3">Khách hàng</th>
                  <th scope="col" class="px-4 py-3">Dịch vụ</th>
                  <th scope="col" class="px-4 py-3">Tổng tiền</th>
                  <th scope="col" class="px-4 py-3">Trạng thái</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="recentOrders.length === 0">
                  <td colspan="4" class="text-center py-4 text-gray-500">
                    Đang chờ dữ liệu đơn hàng mới...
                  </td>
                </tr>
                <tr
                  v-for="order in recentOrders"
                  :key="order.id"
                  class="border-b hover:bg-gray-50"
                >
                  <td class="px-4 py-2 font-medium text-gray-900">
                    {{ order.customer }}
                  </td>
                  <td class="px-4 py-2">{{ order.service }}</td>
                  <td class="px-4 py-2 font-semibold">
                    {{ formatCurrency(order.amount) }}
                  </td>
                  <td class="px-4 py-2">
                    <span
                      :class="getStatusClass(order.status)"
                      class="px-2 py-1 text-xs font-medium rounded-full"
                    >
                      {{ order.status }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import axios from "axios";
import Widget from "@/components/admin/Widget.vue";
import RevenueChart from "@/components/admin/RevenueChart.vue";
import BookingChart from "@/components/admin/BookingChart.vue";
import RecentActivities from "@/components/admin/RecentActivities.vue";
import SockJS from "sockjs-client/dist/sockjs.min.js";
import Stomp from "stompjs";

const API_URL = "http://localhost:8080/api/dashboard";

// State cho widgets và bộ lọc lịch
const summary = ref({
  todayRevenue: 0,
  newBookings: 0,
  newCustomers: 0,
  pendingReviews: 0,
});
const isLoadingSummary = ref(true);
const filterType = ref("day");
const selectedDate = ref(new Date().toISOString().slice(0, 10));
const selectedMonth = ref(new Date().toISOString().slice(0, 7));
const selectedYear = ref(new Date().getFullYear());

// State cho biểu đồ doanh thu
const revenueData = ref({ labels: [], data: [] });
const isLoadingRevenue = ref(true);
const selectedPeriod = ref("day");

// State cho biểu đồ booking
const bookingData = ref({ labels: [], data: [] });
const isLoadingBookingData = ref(true);

// Dữ liệu tĩnh và WebSocket
const recentActivities = ref([
  { time: "1 phút", user: "Admin", action: "xác nhận đơn hàng #1124" },
  { time: "5 phút", user: "Thanh Nguyen", action: "đặt tour Đà Nẵng" },
]);
const recentOrders = ref([]);
const stompClient = ref(null);
const isSocketConnected = ref(false); // ✅ Re-added this variable

// --- Computed Properties ---
const filterTitleText = computed(() => {
  try {
    if (filterType.value === "day") {
      const [y, m, d] = selectedDate.value.split("-");
      return `ngày ${d}/${m}/${y}`;
    }
    if (filterType.value === "month") {
      const [y, m] = selectedMonth.value.split("-");
      return `tháng ${m}/${y}`;
    }
    if (filterType.value === "year") {
      return `năm ${selectedYear.value}`;
    }
  } catch {
    return "";
  }
  return "";
});

// --- API Logic ---
const fetchSummary = async () => {
  isLoadingSummary.value = true;
  let params = { filterType: filterType.value };
  if (filterType.value === "day") params.value = selectedDate.value;
  else if (filterType.value === "month") params.value = selectedMonth.value;
  else if (filterType.value === "year") params.value = selectedYear.value;

  try {
    const response = await axios.get(`${API_URL}/summary`, { params });
    summary.value = response.data;
  } catch (error) {
    console.error(`Lỗi khi lấy dữ liệu tóm tắt:`, error);
    summary.value = {
      todayRevenue: 0,
      newBookings: 0,
      newCustomers: 0,
      pendingReviews: 0,
    };
  } finally {
    isLoadingSummary.value = false;
  }
};

const applyFilter = () => {
  fetchSummary();
};

const fetchRevenueData = async (period) => {
  if (selectedPeriod.value === period && revenueData.value.labels.length > 0)
    return;
  isLoadingRevenue.value = true;
  selectedPeriod.value = period;
  try {
    const response = await axios.get(`${API_URL}/revenue-chart`, {
      params: { period },
    });
    const labels = response.data.map((item) => formatLabel(item.label, period));
    const data = response.data.map((item) => item.value);
    revenueData.value = { labels, data };
  } catch (error) {
    console.error(`Lỗi khi lấy dữ liệu doanh thu theo ${period}:`, error);
    revenueData.value = { labels: [], data: [] };
  } finally {
    isLoadingRevenue.value = false;
  }
};

const fetchBookingData = async () => {
  isLoadingBookingData.value = true;
  try {
    const response = await axios.get(`${API_URL}/booking-chart`);
    console.log("Dữ liệu Booking Chart từ API:", response.data);
    const labels = response.data.map((item) => item.label);
    const data = response.data.map((item) => item.value);
    bookingData.value = { labels, data };
  } catch (error) {
    console.error("Lỗi khi lấy dữ liệu biểu đồ booking:", error);
    bookingData.value = { labels: [], data: [] };
  } finally {
    isLoadingBookingData.value = false;
  }
};

// ✅ Reverted to the requested WebSocket function
const connectWebSocket = () => {
  const socket = new SockJS("http://localhost:8080/ws");
  stompClient.value = Stomp.over(socket);
  stompClient.value.debug = null;
  stompClient.value.connect({}, (frame) => {
    console.log("WebSocket Connected:", frame);
    isSocketConnected.value = true;
    stompClient.value.subscribe("/topic/getTop10NewOrders", (response) => {
      recentOrders.value = JSON.parse(response.body);
      console.log(recentOrders.value);
    });
    stompClient.value.send("/app/getTop10NewOrders", {}, {});
  });
};

onMounted(() => {
  fetchSummary();
  fetchRevenueData("day");
  fetchBookingData();
  connectWebSocket();
});

// --- Helper Functions ---
const formatCurrency = (value) => {
  if (typeof value !== "number" || !value) return "0 ₫";
  if (Math.abs(value) >= 1000000000) {
    const formattedValue = (value / 1000000000).toLocaleString("vi-VN", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });
    return `${formattedValue} tỷ`;
  }
  if (Math.abs(value) >= 1000000) {
    const formattedValue = (value / 1000000).toLocaleString("vi-VN", {
      minimumFractionDigits: 1,
      maximumFractionDigits: 1,
    });
    return `${formattedValue} tr`;
  }
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(value);
};

// ✅ Reverted to the old status function
const getStatusClass = (status) => {
  const map = {
    "Đã thanh toán": "bg-green-100 text-green-800",
    "Chờ thanh toán": "bg-yellow-100 text-yellow-800",
    "Đã hủy": "bg-red-100 text-red-800",
    "Hoàn tiền": "bg-blue-100 text-blue-800",
  };
  return map[status] || "bg-gray-100 text-gray-800";
};

const getPeriodButtonClass = (period) => {
  const baseClass =
    "px-3 py-1 text-sm rounded-md transition-colors font-medium";
  if (selectedPeriod.value === period)
    return `${baseClass} bg-blue-500 text-white`;
  return `${baseClass} bg-gray-200 text-gray-700 hover:bg-gray-300`;
};

const formatLabel = (label, period) => {
  if (!label) return "";
  try {
    if (period === "day") {
      const [year, month, day] = label.split(" ")[0].split("-");
      return `Ngày ${day}/${month}`;
    }
    if (period === "month") {
      const [year, month] = label.split("-");
      return `Thg ${month}/${year}`;
    }
  } catch (e) {
    return label;
  }
  return label;
};
</script>
