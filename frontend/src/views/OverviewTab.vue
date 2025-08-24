<template>
  <div class="space-y-6">
    <div class="p-4 bg-white rounded-lg shadow">
      <div class="flex justify-end items-center gap-x-2">
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
          @keyup.enter="applyFilter"
          class="bg-white border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-28 p-1.5"
        />

        <button
          @click="applyFilter"
          class="px-5 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 focus:ring-4 focus:outline-none focus:ring-blue-300"
        >
          Xem
        </button>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <StatCard
        icon="cash"
        title="Doanh thu"
        :value="stats.revenue"
        :filterText="filterText"
      />
      <StatCard
        icon="users"
        title="Khách hàng"
        :value="stats.customers"
        :filterText="filterText"
      />
      <StatCard
        icon="ticket"
        title="Số Booking"
        :value="stats.bookingsCount"
        :filterText="filterText"
      />
      <StatCard
        icon="briefcase"
        title="Tour có booking"
        :value="stats.toursWithBookings"
        :filterText="filterText"
      />
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div class="bg-white rounded-lg shadow lg:col-span-2">
        <div class="p-6 border-b flex justify-between items-center">
          <h3 class="text-lg font-semibold text-gray-800">
            Booking {{ filterText }}
          </h3>
          <div class="flex items-center space-x-2">
            <button
              @click="changePage(pagination.currentPage - 1)"
              :disabled="pagination.currentPage === 0"
              class="page-btn"
            >
              <i class="fas fa-chevron-left"></i>
            </button>
            <span
              >Trang
              {{ pagination.totalPages > 0 ? pagination.currentPage + 1 : 0 }} /
              {{ pagination.totalPages }}</span
            >
            <button
              @click="changePage(pagination.currentPage + 1)"
              :disabled="pagination.currentPage >= pagination.totalPages - 1"
              class="page-btn"
            >
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
        </div>
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="table-header">Tour</th>
                <th class="table-header">Khách hàng</th>
                <th class="table-header">Ngày đặt</th>
                <th class="table-header">Tổng tiền</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr v-if="loading">
                <td colspan="4" class="text-center py-8">Đang tải...</td>
              </tr>
              <tr v-else-if="bookings.length === 0">
                <td colspan="4" class="text-center py-8">Không có booking.</td>
              </tr>
              <tr v-for="booking in bookings" :key="booking.bookingId">
                <td class="px-6 py-4">{{ booking.tourName }}</td>
                <td class="px-6 py-4">{{ booking.name }}</td>
                <td class="px-6 py-4">{{ formatDate(booking.bookingDate) }}</td>
                <td class="px-6 py-4 font-semibold text-green-600">
                  {{ formatCurrency(booking.totalPrice) }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="bg-white rounded-lg shadow lg:col-span-1">
        <div class="p-6 border-b">
          <h3 class="text-lg font-semibold text-gray-800">
            Top Tour Bán Chạy
            <span class="font-normal text-sm text-blue-600">{{
              filterText
            }}</span>
          </h3>
        </div>
        <div class="p-4 space-y-4">
          <div v-if="loading" class="text-center py-8">Đang tải...</div>
          <div v-else-if="topTours.length === 0" class="text-center py-8">
            Không có dữ liệu.
          </div>
          <div v-for="tour in topTours" :key="tour.tourName" class="space-y-1">
            <div class="flex justify-between text-sm font-medium">
              <span class="text-gray-700 truncate pr-2">{{
                tour.tourName
              }}</span>
              <span class="font-bold text-green-600">{{
                formatCurrency(tour.totalRevenue)
              }}</span>
            </div>
            <div class="w-full bg-gray-200 rounded-full h-2.5">
              <div
                class="bg-blue-600 h-2.5 rounded-full"
                :style="{ width: calculatePercentage(tour.totalRevenue) + '%' }"
              ></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from "vue";
import { useUserStore } from "@/store/UserStore.js"; // Giả định bạn dùng Pinia
import OverviewTourApi from "@/api/OverviewTourApi.js"; // Module gọi API
import StatCard from "@/components/Tours/StatCard.vue"; // Component thẻ thống kê

// --- STATE MANAGEMENT ---
const userStore = useUserStore();
const loading = ref(true);

// State cho bộ lọc mới
const filterType = ref("day"); // 'day', 'month', 'year'
const today = new Date();
const toYYYYMMDD = (d) => d.toISOString().split("T")[0]; // Helper to format date
const toYYYYMM = (d) => d.toISOString().slice(0, 7); // Helper to format month
const selectedDate = ref(toYYYYMMDD(today));
const selectedMonth = ref(toYYYYMM(today));
const selectedYear = ref(today.getFullYear());

// State cho dữ liệu dashboard
const bookings = ref([]);
const topTours = ref([]);
const stats = reactive({
  revenue: "₫0",
  customers: 0,
  bookingsCount: 0,
  toursWithBookings: 0,
});
const pagination = reactive({
  currentPage: 0,
  totalPages: 0,
  pageSize: 5,
  startDate: null,
  endDate: null,
});

// State cho UI
const filterText = ref("");

// --- LIFECYCLE HOOKS ---
onMounted(async () => {
  loading.value = true;
  if (!userStore.user) {
    await userStore.restoreUserFromToken();
  }

  if (userStore.user?.id) {
    // Tải dữ liệu lần đầu với ngày hôm nay
    await applyFilter();
  } else {
    console.error("Không tìm thấy User ID, không thể tải dữ liệu dashboard.");
    loading.value = false;
  }
});

// --- API & DATA LOGIC ---
const applyFilter = async () => {
  let startDate, endDate;
  const now = new Date();

  switch (filterType.value) {
    case "day":
      startDate = new Date(selectedDate.value);
      endDate = new Date(selectedDate.value);
      break;
    case "month":
      const [yearM, monthM] = selectedMonth.value.split("-").map(Number);
      startDate = new Date(yearM, monthM - 1, 1);
      endDate = new Date(yearM, monthM, 0); // Ngày cuối cùng của tháng
      break;
    case "year":
      const year = selectedYear.value || now.getFullYear();
      startDate = new Date(year, 0, 1); // 1/1
      endDate = new Date(year, 11, 31); // 31/12
      break;
  }

  // Đảm bảo ngày kết thúc bao gồm cả ngày
  endDate.setHours(23, 59, 59, 999);

  // Lưu lại khoảng thời gian để dùng cho phân trang
  pagination.startDate = startDate;
  pagination.endDate = endDate;

  updateFilterText();
  await fetchData(0); // Luôn bắt đầu từ trang đầu tiên khi lọc
};

const fetchData = async (page = 0) => {
  if (!userStore.user?.id || !pagination.startDate || !pagination.endDate) {
    console.error("Thiếu thông tin để tải dữ liệu (userId hoặc khoảng ngày).");
    loading.value = false;
    return;
  }

  loading.value = true;
  pagination.currentPage = page;

  try {
    const params = {
      userId: userStore.user.id,
      startDate: pagination.startDate.toISOString().split("T")[0],
      endDate: pagination.endDate.toISOString().split("T")[0],
      page: pagination.currentPage,
      size: pagination.pageSize,
    };

    const response = await OverviewTourApi.getOverviewData(params);
    const overviewData = response.data?.data;

    if (!overviewData) throw new Error("Dữ liệu API không hợp lệ.");

    if (overviewData.stats) {
      Object.assign(stats, overviewData.stats);
      stats.revenue = formatCurrency(overviewData.stats.revenue);
    }
    bookings.value = overviewData.bookings?.content || [];
    pagination.totalPages = overviewData.bookings?.totalPages || 0;
    topTours.value = overviewData.topTours || [];
  } catch (error) {
    console.error("Lỗi khi tải dữ liệu tổng quan:", error);
    bookings.value = [];
    topTours.value = [];
    pagination.totalPages = 0;
  } finally {
    loading.value = false;
  }
};

const changePage = (page) => {
  if (page >= 0 && page < pagination.totalPages) {
    fetchData(page);
  }
};

// --- UI HELPERS ---
const updateFilterText = () => {
  switch (filterType.value) {
    case "day":
      const [y, m, d] = selectedDate.value.split("-");
      filterText.value = `trong ngày ${d}/${m}/${y}`;
      break;
    case "month":
      const [yM, mM] = selectedMonth.value.split("-");
      filterText.value = `trong tháng ${mM}/${yM}`;
      break;
    case "year":
      filterText.value = `trong năm ${selectedYear.value}`;
      break;
    default:
      filterText.value = "";
  }
};

const totalRevenueOfTopTours = computed(() =>
  topTours.value.reduce((sum, tour) => sum + tour.totalRevenue, 0)
);

const calculatePercentage = (revenue) => {
  if (totalRevenueOfTopTours.value === 0) return 0;
  return (revenue / totalRevenueOfTopTours.value) * 100;
};

const formatCurrency = (v) =>
  v
    ? new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      }).format(v)
    : "₫0";

const formatDate = (d) => (d ? new Date(d).toLocaleDateString("vi-VN") : "N/A");
</script>

<style scoped>
.page-btn {
  padding: 0.25rem 0.75rem;
  font-size: 0.875rem;
  border-radius: 0.25rem;
  background-color: #f3f4f6;
  transition: background-color 0.2s;
}
.page-btn:hover:not(:disabled) {
  background-color: #d1d5db;
}
.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.table-header {
  padding: 0.75rem 1.5rem;
  text-align: left;
  font-size: 0.75rem;
  font-weight: 600;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}
</style>
