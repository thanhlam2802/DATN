<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h2 class="text-3xl font-bold text-gray-800">Quản lý Booking</h2>
    </div>

    <div
      class="grid grid-cols-1 md:grid-cols-4 gap-4 p-4 bg-gray-50 rounded-lg"
    >
      <div>
        <label for="tour-filter" class="block text-sm font-medium text-gray-700"
          >Tour</label
        >
        <select
          id="tour-filter"
          v-model="filters.tourId"
          class="mt-1 block w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500"
        >
          <option value="all">Tất cả Tour</option>
          <option
            v-for="tour in availableTours"
            :key="tour.id"
            :value="tour.id"
          >
            {{ tour.name }}
          </option>
        </select>
      </div>
      <div>
        <label for="date-filter" class="block text-sm font-medium text-gray-700"
          >Ngày khởi hành</label
        >
        <input
          type="date"
          id="date-filter"
          v-model="filters.departureDate"
          class="mt-1 block w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500"
        />
      </div>
      <div>
        <label
          for="status-filter"
          class="block text-sm font-medium text-gray-700"
          >Trạng thái</label
        >
        <select
          id="status-filter"
          v-model="filters.status"
          class="mt-1 block w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500"
        >
          <option value="all">Tất cả</option>
          <option value="PENDING_PAYMENT">Chờ xác nhận</option>
          <option value="PAID">Đã xác nhận</option>
          <option value="CANCELLED">Đã hủy</option>
        </select>
      </div>
      <div>
        <label class="block text-sm font-medium text-transparent">Action</label>
        <button
          @click="resetFilters"
          class="mt-1 w-full bg-gray-600 text-white py-2 px-4 rounded-lg hover:bg-gray-700 text-sm"
        >
          Reset
        </button>
      </div>
    </div>

    <div v-if="isLoading" class="text-center py-12 text-gray-500">
      Đang tải dữ liệu...
    </div>
    <div v-else-if="error" class="text-center py-12 text-red-500">
      Lỗi: {{ error.message || "Không thể tải dữ liệu." }}
    </div>
    <div v-else-if="bookings.length === 0" class="text-center py-12">
      <CalendarIcon class="w-12 h-12 text-gray-400 mx-auto mb-4" />
      <p class="text-gray-500">Không có booking nào phù hợp với bộ lọc.</p>
    </div>

    <div v-else class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <div
        v-for="booking in bookings"
        :key="booking.id"
        class="bg-white rounded-lg shadow-md p-6 flex flex-col"
      >
        <div class="flex items-start justify-between mb-4">
          <div>
            <h3 class="text-lg font-semibold text-gray-800">
              {{ booking.tour }}
            </h3>
            <p class="text-sm text-gray-500">Booking ID: #{{ booking.id }}</p>
          </div>
          <div class="flex flex-col items-end space-y-1 text-right">
            <span
              class="px-2 py-1 text-xs rounded-full font-medium"
              :class="getStatusColor(booking.status)"
            >
              {{ booking.status }}
            </span>
            <span
              class="px-2 py-1 text-xs rounded-full font-medium"
              :class="getPaymentStatusColor(booking.paymentStatus)"
            >
              {{ booking.paymentStatus }}
            </span>
          </div>
        </div>

        <div class="space-y-3 text-sm text-gray-600">
          <div class="flex items-center">
            <UserIcon class="w-4 h-4 mr-2 text-gray-400" />
            <span class="font-medium">{{ booking.customer }}</span>
            <span class="ml-auto text-gray-500"
              >{{ booking.guests }} khách</span
            >
          </div>
          <div class="flex items-center">
            <PhoneIcon class="w-4 h-4 mr-2 text-gray-400" />
            {{ booking.phone }}
          </div>
          <div class="flex items-center">
            <MailIcon class="w-4 h-4 mr-2 text-gray-400" />
            {{ booking.email }}
          </div>
          <div class="flex items-center">
            <CalendarIcon class="w-4 h-4 mr-2 text-gray-400" />
            Ngày tour:
            <span class="font-medium ml-2">{{ booking.tourDate }}</span>
          </div>
        </div>

        <div class="mt-4 pt-4 border-t border-gray-200">
          <div class="flex items-center justify-between">
            <span class="text-sm text-gray-600">Tổng tiền:</span>
            <span class="text-lg font-semibold text-blue-600">
              ₫{{ booking.totalAmount }}
            </span>
          </div>
          <div class="flex items-center justify-between text-sm text-gray-500">
            <span>Ngày đặt: {{ booking.bookingDate }}</span>
          </div>
        </div>

        <div class="mt-4 flex space-x-2 pt-4 border-t border-gray-200">
          <template v-if="booking.status === 'Chờ xác nhận'">
            <button
              @click="handleConfirm(booking.id)"
              class="flex-1 bg-green-600 text-white py-2 px-4 rounded-lg hover:bg-green-700 text-sm font-semibold"
            >
              Xác nhận
            </button>
            <button
              @click="handleCancel(booking.id)"
              class="flex-1 bg-red-600 text-white py-2 px-4 rounded-lg hover:bg-red-700 text-sm font-semibold"
            >
              Từ chối
            </button>
          </template>
          <template v-else>
            <button
              class="w-full bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 text-sm font-semibold"
            >
              Xem chi tiết
            </button>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from "vue";
import {
  CalendarIcon,
  UserIcon,
  PhoneIcon,
  MailIcon,
  FilterIcon,
} from "lucide-vue-next";
import { tourApi, bookingApi } from "@/api/bookingTourApi.js";

// --- State ---
const bookings = ref([]);
const availableTours = ref([]);
const isLoading = ref(false);
const error = ref(null);

const filters = reactive({
  tourId: "all",
  departureDate: "",
  status: "all",
});

// --- API Calls ---
const fetchBookings = async () => {
  isLoading.value = true;
  error.value = null;
  try {
    const params = {};
    if (filters.tourId && filters.tourId !== "all") {
      params.tourId = filters.tourId;
    }
    if (filters.departureDate) {
      params.departureDate = filters.departureDate;
    }
    if (filters.status && filters.status !== "all") {
      params.status = filters.status;
    }
    bookings.value = await bookingApi.getAdminBookings(params);
  } catch (err) {
    error.value = err;
  } finally {
    isLoading.value = false;
  }
};

const fetchToursForFilter = async () => {
  try {
    availableTours.value = await tourApi.getAll();
  } catch (err) {
    console.error("Không thể tải danh sách tour để lọc.", err);
  }
};

// --- Actions ---
const handleConfirm = async (bookingId) => {
  if (!confirm("Bạn có chắc chắn muốn xác nhận booking này?")) return;
  try {
    await bookingApi.confirm(bookingId);
    alert("Xác nhận booking thành công!");
    await fetchBookings(); // Tải lại danh sách
  } catch (err) {
    alert("Có lỗi xảy ra khi xác nhận booking.");
  }
};

const handleCancel = async (bookingId) => {
  if (!confirm("Bạn có chắc chắn muốn từ chối booking này?")) return;
  try {
    await bookingApi.cancel(bookingId);
    alert("Đã từ chối booking thành công!");
    await fetchBookings(); // Tải lại danh sách
  } catch (err) {
    alert("Có lỗi xảy ra khi từ chối booking.");
  }
};

const resetFilters = () => {
  filters.tourId = "all";
  filters.departureDate = "";
  filters.status = "all";
};

// --- Lifecycle & Watchers ---
watch(filters, fetchBookings, { deep: true });

onMounted(() => {
  fetchToursForFilter();
  fetchBookings();
});

// --- Helpers ---
function getStatusColor(status) {
  switch (status) {
    case "Đã xác nhận":
      return "bg-green-100 text-green-800";
    case "Chờ xác nhận":
      return "bg-yellow-100 text-yellow-800";
    case "Đã hủy":
      return "bg-red-100 text-red-800";
    default:
      return "bg-gray-100 text-gray-800";
  }
}

function getPaymentStatusColor(status) {
  switch (status) {
    case "Đã thanh toán":
      return "bg-green-100 text-green-800";
    case "Đã cọc 50%":
      return "bg-blue-100 text-blue-800";
    case "Chờ thanh toán":
      return "bg-yellow-100 text-yellow-800";
    default:
      return "bg-gray-100 text-gray-800";
  }
}
</script>
