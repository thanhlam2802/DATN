<template>
  <div>
    <h1 class="text-3xl font-bold text-gray-800 mb-6">
      Quản lý Booking Toàn hệ thống
    </h1>

    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
      <Widget
        title="Tổng số Booking"
        icon="fas fa-receipt"
        :value="bookings.length"
      />
      <Widget
        title="Chờ xác nhận"
        icon="fas fa-hourglass-half"
        :value="pendingCount"
      />
      <Widget
        title="Doanh thu"
        icon="fas fa-dollar-sign"
        :value="formatCurrency(totalRevenue)"
      />
      <Widget
        title="Đã hủy"
        icon="fas fa-times-circle"
        :value="cancelledCount"
      />
    </div>

    <div class="bg-white p-4 rounded-xl shadow-lg mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <input
          type="text"
          v-model="filters.search"
          placeholder="Tìm ID, tên, email..."
          class="border p-2 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
        <select
          v-model="filters.status"
          class="border p-2 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
        >
          <option value="">Tất cả trạng thái</option>
          <option value="Paid">Đã thanh toán</option>
          <option value="Pending">Chờ thanh toán</option>
          <option value="Confirmed">Đã xác nhận</option>
          <option value="Cancelled">Đã hủy</option>
        </select>
        <select
          v-model="filters.type"
          class="border p-2 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
        >
          <option value="">Tất cả dịch vụ</option>
          <option value="Tour">Tour</option>
          <option value="Hotel">Khách sạn</option>
          <option value="Flight">Chuyến bay</option>
        </select>
        <input
          type="date"
          v-model="filters.date"
          class="border p-2 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>
    </div>

    <div class="bg-white p-6 rounded-xl shadow-lg">
      <div class="overflow-x-auto">
        <table class="w-full text-sm">
          <thead class="text-xs text-gray-500 uppercase bg-gray-50">
            <tr>
              <th class="px-4 py-3 w-12">
                <input
                  type="checkbox"
                  v-model="isAllSelected"
                  @change="selectAll"
                />
              </th>
              <th class="px-4 py-3">Booking ID / Loại</th>
              <th class="px-4 py-3">Khách hàng</th>
              <th class="px-4 py-3">Ngày đặt</th>
              <th class="px-4 py-3">Tổng tiền</th>
              <th class="px-4 py-3">Trạng thái</th>
              <th class="px-4 py-3 text-center">Hành động</th>
            </tr>
          </thead>
          <tbody class="divide-y">
            <tr
              v-for="booking in filteredBookings"
              :key="booking.id"
              class="hover:bg-gray-50"
            >
              <td class="px-4 py-3">
                <input
                  type="checkbox"
                  v-model="selectedBookings"
                  :value="booking.id"
                />
              </td>
              <td class="px-4 py-3">
                <div class="font-bold">{{ booking.id }}</div>
                <div
                  class="text-xs flex items-center gap-1 mt-1"
                  :class="getServiceClass(booking.type).text"
                >
                  <i :class="getServiceClass(booking.type).icon"></i>
                  <span>{{ booking.type }}</span>
                </div>
              </td>
              <td class="px-4 py-3">
                <div class="font-semibold">{{ booking.customer.name }}</div>
                <div class="text-xs text-gray-600">
                  {{ booking.customer.email }}
                </div>
              </td>
              <td class="px-4 py-3">{{ formatDate(booking.date) }}</td>
              <td class="px-4 py-3 font-semibold">
                {{ formatCurrency(booking.total) }}
              </td>
              <td class="px-4 py-3">
                <span
                  :class="getStatusClass(booking.status)"
                  class="px-2 py-1 text-xs font-medium rounded-full"
                >
                  {{ booking.status }}
                </span>
              </td>
              <td class="px-4 py-3 text-center">
                <button
                  @click="viewDetails(booking)"
                  class="text-indigo-600 hover:text-indigo-800"
                >
                  Xem & Xử lý
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div
      v-if="isModalOpen"
      class="fixed inset-0 bg-black bg-opacity-60 z-50 flex justify-center items-center p-4"
    >
      <div
        class="bg-white rounded-lg shadow-2xl w-full max-w-4xl max-h-[90vh] flex flex-col"
      >
        <div class="flex justify-between items-center p-6 border-b">
          <h2 class="text-2xl font-bold">
            Chi tiết Booking #{{ currentBooking.id }}
          </h2>
          <button
            @click="isModalOpen = false"
            class="text-3xl text-gray-500 hover:text-gray-800"
          >
            &times;
          </button>
        </div>

        <div class="p-6 overflow-y-auto">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div class="md:col-span-2 space-y-6">
              <div class="bg-gray-50 p-4 rounded-lg">
                <h3 class="font-bold text-lg mb-2">Thông tin dịch vụ</h3>
                <p><strong>Loại:</strong> {{ currentBooking.type }}</p>
                <p><strong>Tên:</strong> {{ currentBooking.details.name }}</p>
                <p v-if="currentBooking.type === 'Tour'">
                  <strong>Ngày khởi hành:</strong>
                  {{ formatDate(currentBooking.details.departureDate) }}
                </p>
                <p v-if="currentBooking.type === 'Hotel'">
                  <strong>Check-in/out:</strong>
                  {{ formatDate(currentBooking.details.checkIn) }} -
                  {{ formatDate(currentBooking.details.checkOut) }}
                </p>
              </div>
              <div class="bg-gray-50 p-4 rounded-lg">
                <h3 class="font-bold text-lg mb-2">Thông tin khách hàng</h3>
                <p><strong>Tên:</strong> {{ currentBooking.customer.name }}</p>
                <p>
                  <strong>Email:</strong> {{ currentBooking.customer.email }}
                </p>
                <p><strong>SĐT:</strong> {{ currentBooking.customer.phone }}</p>
              </div>
            </div>

            <div class="space-y-6">
              <div class="border p-4 rounded-lg">
                <h3 class="font-bold text-lg mb-2">Trạng thái & Xử lý</h3>
                <label class="block mb-1 font-semibold"
                  >Cập nhật trạng thái:</label
                >
                <select
                  v-model="currentBooking.status"
                  class="w-full border p-2 rounded-lg"
                >
                  <option value="Pending">Chờ thanh toán</option>
                  <option value="Paid">Đã thanh toán</option>
                  <option value="Confirmed">Đã xác nhận</option>
                  <option value="Cancelled">Đã hủy</option>
                  <option value="Refunded">Đã hoàn tiền</option>
                </select>
              </div>
              <div class="border p-4 rounded-lg">
                <h3 class="font-bold text-lg mb-2">Thông tin thanh toán</h3>
                <div class="space-y-1 text-sm">
                  <p class="flex justify-between">
                    <span>Tạm tính:</span>
                    <span>{{ formatCurrency(currentBooking.total) }}</span>
                  </p>
                  <p class="flex justify-between">
                    <span>Giảm giá:</span>
                    <span>- {{ formatCurrency(0) }}</span>
                  </p>
                  <p
                    class="flex justify-between font-bold text-base border-t pt-1"
                  >
                    <span>Tổng cộng:</span>
                    <span>{{ formatCurrency(currentBooking.total) }}</span>
                  </p>
                </div>
                <p class="text-sm mt-2">
                  <strong>Phương thức:</strong>
                  {{ currentBooking.paymentMethod }}
                </p>
              </div>
            </div>
          </div>
        </div>

        <div class="p-6 border-t bg-gray-50 flex justify-end gap-4">
          <button
            @click="isModalOpen = false"
            class="bg-gray-200 py-2 px-6 rounded-lg hover:bg-gray-300"
          >
            Đóng
          </button>
          <button
            @click="saveBooking"
            class="bg-indigo-600 text-white font-bold py-2 px-6 rounded-lg hover:bg-indigo-700"
          >
            Lưu thay đổi
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import Widget from "@/components/admin/Widget.vue";

// --- Mock Data ---
const bookings = ref([
  {
    id: "BK-001",
    customer: {
      name: "Võ Lê Thành Lâm",
      email: "lam@example.com",
      phone: "0901",
    },
    date: "2025-07-28",
    total: 5200000,
    status: "Confirmed",
    type: "Tour",
    paymentMethod: "VNPAY",
    details: {
      name: "Tour Khám phá Quy Nhơn 3N2Đ",
      departureDate: "2025-08-10",
    },
  },
  {
    id: "BK-002",
    customer: { name: "Nguyễn Văn An", email: "an@example.com", phone: "0902" },
    date: "2025-07-27",
    total: 1800000,
    status: "Paid",
    type: "Hotel",
    paymentMethod: "Credit Card",
    details: {
      name: "FLC Luxury Hotel",
      checkIn: "2025-09-01",
      checkOut: "2025-09-03",
    },
  },
  {
    id: "BK-003",
    customer: {
      name: "Trần Thị Bình",
      email: "binh@example.com",
      phone: "0903",
    },
    date: "2025-07-26",
    total: 2100000,
    status: "Pending",
    type: "Flight",
    paymentMethod: "MOMO",
    details: { name: "VJ123: SGN -> UIH" },
  },
  {
    id: "BK-004",
    customer: {
      name: "Lê Minh Cường",
      email: "cuong@example.com",
      phone: "0904",
    },
    date: "2025-07-25",
    total: 8500000,
    status: "Cancelled",
    type: "Tour",
    paymentMethod: "VNPAY",
    details: { name: "Tour xuyên Việt 7N6Đ", departureDate: "2025-07-30" },
  },
]);

// --- State ---
const filters = ref({ search: "", status: "", type: "", date: "" });
const selectedBookings = ref([]);
const isModalOpen = ref(false);
const currentBooking = ref({});

// --- Computed Properties ---
const filteredBookings = computed(() => {
  return bookings.value.filter((b) => {
    const searchMatch = filters.value.search
      ? b.id.toLowerCase().includes(filters.value.search.toLowerCase()) ||
        b.customer.name
          .toLowerCase()
          .includes(filters.value.search.toLowerCase()) ||
        b.customer.email
          .toLowerCase()
          .includes(filters.value.search.toLowerCase())
      : true;
    const statusMatch = filters.value.status
      ? b.status === filters.value.status
      : true;
    const typeMatch = filters.value.type ? b.type === filters.value.type : true;
    const dateMatch = filters.value.date ? b.date === filters.value.date : true;
    return searchMatch && statusMatch && typeMatch && dateMatch;
  });
});

const isAllSelected = computed({
  get: () =>
    filteredBookings.value.length > 0 &&
    selectedBookings.value.length === filteredBookings.value.length,
  set: (value) => {
    selectedBookings.value = value
      ? filteredBookings.value.map((b) => b.id)
      : [];
  },
});
const pendingCount = computed(
  () =>
    bookings.value.filter((b) => b.status === "Pending" || b.status === "Paid")
      .length
);
const cancelledCount = computed(
  () => bookings.value.filter((b) => b.status === "Cancelled").length
);
const totalRevenue = computed(() =>
  bookings.value.reduce(
    (sum, b) =>
      b.status === "Confirmed" || b.status === "Paid" ? sum + b.total : sum,
    0
  )
);

// --- Methods ---
const selectAll = () => {
  // Logic handled by isAllSelected computed property's setter
};

const viewDetails = (booking) => {
  currentBooking.value = JSON.parse(JSON.stringify(booking)); // Deep copy to avoid mutating original data
  isModalOpen.value = true;
};

const saveBooking = () => {
  const index = bookings.value.findIndex(
    (b) => b.id === currentBooking.value.id
  );
  if (index !== -1) {
    bookings.value[index] = currentBooking.value;
  }
  isModalOpen.value = false;
  // Here you would typically call an API to save the changes
  alert(`Đã cập nhật trạng thái cho Booking #${currentBooking.value.id}`);
};

// --- Helper Functions ---
const formatCurrency = (value) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    value || 0
  );
const formatDate = (dateString) =>
  new Date(dateString).toLocaleDateString("vi-VN");

const getStatusClass = (status) =>
  ({
    Paid: "bg-blue-100 text-blue-800",
    Pending: "bg-yellow-100 text-yellow-800",
    Confirmed: "bg-green-100 text-green-800",
    Cancelled: "bg-red-100 text-red-800",
    Refunded: "bg-gray-100 text-gray-800",
  }[status] || "bg-gray-100");

const getServiceClass = (type) =>
  ({
    Tour: { text: "text-green-600", icon: "fas fa-map-marked-alt" },
    Hotel: { text: "text-blue-600", icon: "fas fa-hotel" },
    Flight: { text: "text-purple-600", icon: "fas fa-plane" },
  }[type] || { text: "text-gray-600", icon: "fas fa-question-circle" });
</script>
