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
      <div class="p-6 bg-white rounded-lg shadow">
        <p class="text-sm font-medium text-gray-600">
          Doanh thu
          <span class="font-bold text-blue-600">{{ filterText }}</span>
        </p>
        <p class="text-2xl font-bold text-gray-900">{{ stats.revenue }}</p>
      </div>
      <div class="p-6 bg-white rounded-lg shadow">
        <p class="text-sm font-medium text-gray-600">
          Khách hàng
          <span class="font-bold text-blue-600">{{ filterText }}</span>
        </p>
        <p class="text-2xl font-bold text-gray-900">{{ stats.customers }}</p>
      </div>
      <div class="p-6 bg-white rounded-lg shadow">
        <p class="text-sm font-medium text-gray-600">Tổng Tour</p>
        <p class="text-2xl font-bold text-gray-900">{{ stats.totalTours }}</p>
      </div>
      <div class="p-6 bg-white rounded-lg shadow">
        <p class="text-sm font-medium text-gray-600">Tour hoạt động</p>
        <p class="text-2xl font-bold text-gray-900">{{ stats.activeTours }}</p>
      </div>
    </div>

    <div class="bg-white rounded-lg shadow">
      <div class="p-6 border-b flex justify-between items-center">
        <h3 class="text-lg font-semibold text-gray-800">
          Booking {{ filterText }}
        </h3>
        <div class="flex items-center space-x-2">
          <button
            @click="changePage(pagination.currentPage - 1)"
            :disabled="pagination.currentPage === 0"
            class="px-3 py-1 text-sm bg-gray-200 rounded disabled:opacity-50"
          >
            Trước
          </button>
          <span
            >Trang {{ pagination.currentPage + 1 }} /
            {{ pagination.totalPages }}</span
          >
          <button
            @click="changePage(pagination.currentPage + 1)"
            :disabled="pagination.currentPage >= pagination.totalPages - 1"
            class="px-3 py-1 text-sm bg-gray-200 rounded disabled:opacity-50"
          >
            Sau
          </button>
        </div>
      </div>
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase"
              >
                Tour
              </th>
              <th
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase"
              >
                Khách hàng
              </th>
              <th
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase"
              >
                Ngày đặt
              </th>
              <th
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase"
              >
                Tổng tiền
              </th>
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
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, watch } from "vue";
import OverviewTourApi from "../api/OverviewTourApi";
import Datepicker from "@vuepic/vue-datepicker";
import "@vuepic/vue-datepicker/dist/main.css";

// --- State Management ---
const loading = ref(true);
const stats = reactive({
  revenue: "₫0",
  customers: 0,
  totalTours: 0,
  activeTours: 0,
});
const bookings = ref([]);
const pagination = reactive({ currentPage: 0, totalPages: 0, pageSize: 5 });
const dateRange = ref(null);
const filterText = ref("gần đây");

// --- API Fetching Logic ---
const fetchData = async (page = 0) => {
  loading.value = true;
  pagination.currentPage = page;

  const [startDate, endDate] = dateRange.value || [null, null];
  const filters = {
    startDate: startDate ? startDate.toISOString().split("T")[0] : null,
    endDate: endDate ? endDate.toISOString().split("T")[0] : null,
  };

  try {
    const [statsRes, bookingsRes] = await Promise.all([
      OverviewTourApi.getStatistics(filters),
      OverviewTourApi.getBookings({
        ...filters,
        page: pagination.currentPage,
        size: pagination.pageSize,
      }),
    ]);

    const statsData = statsRes.data.data;
    stats.revenue = formatCurrency(statsData.revenueThisMonth);
    stats.customers = statsData.totalCustomers;
    stats.totalTours = statsData.totalTours;
    stats.activeTours = statsData.activeTours;

    const bookingsPage = bookingsRes.data.data;
    bookings.value = bookingsPage.content;
    pagination.totalPages = bookingsPage.totalPages;
  } catch (error) {
    console.error("Lỗi khi tải dữ liệu:", error);
  } finally {
    loading.value = false;
  }
};

// --- Event Handlers & Watchers ---
watch(dateRange, () => {
  updateFilterText();
  fetchData(0); // Reset về trang đầu khi đổi bộ lọc
});

const setPresetRange = (preset) => {
  const end = new Date();
  let start = new Date();
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

const changePage = (page) => {
  if (page >= 0 && page < pagination.totalPages) {
    fetchData(page);
  }
};

const updateFilterText = () => {
  if (!dateRange.value) {
    filterText.value = "gần đây";
    return;
  }
  const [start, end] = dateRange.value;
  filterText.value = `từ ${formatDate(start)} đến ${formatDate(end)}`;
};

// --- Lifecycle & Helpers ---
onMounted(fetchData);

const formatCurrency = (v) =>
  v
    ? new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      }).format(v)
    : "₫0";
const formatDate = (d) => (d ? new Date(d).toLocaleDateString("vi-VN") : "N/A");
</script>

<style>
/* CSS cho nút chọn nhanh và Datepicker */
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
:root {
  --dp-input-padding: 8px 12px;
  --dp-font-size: 0.875rem;
  --dp-border-radius: 0.375rem;
}
</style>
