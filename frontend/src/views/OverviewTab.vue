<template>
  <div class="space-y-6">
    <div class="p-4 bg-white rounded-lg shadow">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1"
            >Chọn khoảng thời gian</label
          >
          <Datepicker
            v-model="dateRange"
            range
            :enable-time-picker="false"
            format="dd/MM/yyyy"
            auto-apply
            placeholder="Chọn một khoảng ngày để lọc"
          />
        </div>
        <div class="flex items-end space-x-2">
          <button @click="setPresetRange('today')" class="preset-btn">
            Hôm nay
          </button>
          <button @click="setPresetRange('last7days')" class="preset-btn">
            7 ngày qua
          </button>
          <button @click="setPresetRange('thisMonth')" class="preset-btn">
            Tháng này
          </button>
          <button @click="setPresetRange('thisYear')" class="preset-btn">
            Năm nay
          </button>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <StatCard
        icon="cash"
        title="Doanh thu"
        :value="stats.revenue"
        :change="stats.revenueChange"
        :filterText="filterText"
      />
      <StatCard
        icon="users"
        title="Khách hàng"
        :value="stats.customers"
        :change="stats.customersChange"
        :filterText="filterText"
      />
      <StatCard
        icon="ticket"
        title="Số Booking"
        :value="stats.bookingsCount"
        :change="stats.bookingsChange"
        :filterText="filterText"
      />
      <StatCard
        icon="briefcase"
        title="Tour có booking"
        :value="stats.toursWithBookings"
        :change="stats.toursWithBookingsChange"
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
              Trước
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
              Sau
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
            Top Tour bán chạy
            <span class="font-normal text-blue-600">{{ filterText }}</span>
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
import { ref, onMounted, reactive, watch, computed } from "vue";
import { useUserStore } from "@/store/UserStore.js";
import OverviewApi from "@/api/OverviewTourApi.js";
import Datepicker from "@vuepic/vue-datepicker";
import "@vuepic/vue-datepicker/dist/main.css";
import StatCard from "@/components/Tours/StatCard.vue";

const userStore = useUserStore();
const loading = ref(true);
const isInitialLoadDone = ref(false); // Cờ mới để kiểm soát
const dateRange = ref(null);
const bookings = ref([]);
const topTours = ref([]);
const pagination = reactive({ currentPage: 0, totalPages: 0, pageSize: 5 });
const stats = reactive({
  revenue: "₫0",
  customers: 0,
  bookingsCount: 0,
  toursWithBookings: 0,
});
const filterText = ref("");

// ✅ LOGIC GỠ RỐI CHI TIẾT TRONG onMounted
onMounted(async () => {
  console.log("--- BẮT ĐẦU onMounted ---");
  loading.value = true;

  // Bước 1: Kiểm tra trạng thái user ban đầu
  console.log("1. Trạng thái user ban đầu:", JSON.stringify(userStore.user));
  if (!userStore.user) {
    console.log("2. User chưa có, đang chờ restoreUserFromToken...");
    await userStore.restoreUserFromToken();
    console.log(
      "3. Đã chạy xong restoreUser. User hiện tại:",
      JSON.stringify(userStore.user)
    );
  }

  // Bước 2: Kiểm tra lại user ID một cách tường minh
  const userId = userStore.user?.id;
  console.log(
    `4. Kiểm tra lần cuối: userId là '${userId}' (kiểu dữ liệu: ${typeof userId})`
  );

  if (userId) {
    console.log("5. Đã có userId. Bắt đầu tải dữ liệu lần đầu.");
    setPresetRange("thisMonth");
    await fetchData(0);
  } else {
    console.error(
      "6. Vẫn không tìm thấy userId sau khi restore. Dừng tải dữ liệu."
    );
    loading.value = false;
  }

  // Đánh dấu đã tải xong lần đầu để watch có thể hoạt động
  isInitialLoadDone.value = true;
  console.log("--- KẾT THÚC onMounted ---");
});

// Watcher giờ chỉ phản ứng sau khi onMounted đã hoàn tất
watch(dateRange, (newVal) => {
  // Chỉ chạy khi onMounted đã xong và có giá trị mới
  if (!isInitialLoadDone.value || !newVal) {
    return;
  }
  console.log("Người dùng thay đổi dateRange, fetch lại dữ liệu.");
  updateFilterText();
  fetchData(0);
});

const fetchData = async (page = 0) => {
  if (!userStore.user?.id || !dateRange.value) {
    console.error("fetchData được gọi nhưng thiếu userId hoặc dateRange.");
    loading.value = false;
    return;
  }
  loading.value = true;
  pagination.currentPage = page;
  const [startDate, endDate] = dateRange.value;
  try {
    const params = {
      userId: userStore.user.id,
      startDate: startDate.toISOString().split("T")[0],
      endDate: endDate.toISOString().split("T")[0],
      page: pagination.currentPage,
      size: pagination.pageSize,
    };
    const response = await OverviewApi.getOverviewData(params);
    const overviewData = response.data?.data;
    if (!overviewData) throw new Error("API response không có trường data.");
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

// --- CÁC HÀM HELPER KHÁC ---
const setPresetRange = (preset) => {
  const end = new Date();
  let start = new Date();
  end.setHours(23, 59, 59, 999);
  start.setHours(0, 0, 0, 0);
  switch (preset) {
    case "today":
      break;
    case "last7days":
      start.setDate(end.getDate() - 6);
      break;
    case "thisMonth":
      start.setDate(1);
      break;
    case "thisYear":
      start.setMonth(0, 1);
      break;
  }
  dateRange.value = [start, end];
};

const updateFilterText = () => {
  if (!dateRange.value) {
    filterText.value = "gần đây";
    return;
  }
  const [start, end] = dateRange.value;
  if (start.toDateString() === end.toDateString()) {
    filterText.value = `trong ngày ${formatDate(start)}`;
    return;
  }
  filterText.value = `từ ${formatDate(start)} đến ${formatDate(end)}`;
};

const changePage = (page) => {
  if (page >= 0 && page < pagination.totalPages) fetchData(page);
};

const formatCurrency = (v) =>
  v
    ? new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      }).format(v)
    : "₫0";
const formatDate = (d) => (d ? new Date(d).toLocaleDateString("vi-VN") : "N/A");

const totalRevenueOfTopTours = computed(() =>
  topTours.value.reduce((sum, tour) => sum + tour.totalRevenue, 0)
);
const calculatePercentage = (revenue) =>
  totalRevenueOfTopTours.value === 0
    ? 0
    : (revenue / totalRevenueOfTopTours.value) * 100;
</script>

<style>
.preset-btn {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
  font-weight: 600;
  border-radius: 9999px;
  background-color: #e5e7eb;
  color: #374151;
  transition: background-color 0.2s;
}
.preset-btn:hover {
  background-color: #d1d5db;
}

.page-btn {
  padding: 0.25rem 0.75rem;
  font-size: 0.875rem;
  border-radius: 0.25rem;
  background-color: #e5e7eb;
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
  font-weight: 500;
  color: #6b7280;
  text-transform: uppercase;
}

:root {
  --dp-input-padding: 8px 12px;
  --dp-font-size: 0.875rem;
  --dp-border-radius: 0.375rem;
}
</style>
