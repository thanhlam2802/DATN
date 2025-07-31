<template>
  <div class="space-y-6">
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
      <Widget
        title="Doanh thu hôm nay"
        icon="fas fa-coins"
        :value="formatCurrency(summary.todayRevenue)"
      />
      <Widget
        title="Booking mới"
        icon="fas fa-ticket-alt"
        :value="summary.newBookings"
      />
      <Widget
        title="Khách hàng mới"
        icon="fas fa-users"
        :value="summary.newCustomers"
      />
      <Widget
        title="Đánh giá chờ duyệt"
        icon="fas fa-comments"
        :value="summary.pendingReviews"
      />
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-5 gap-6">
      <div class="lg:col-span-3">
        <RevenueChart :data="revenueData" />
      </div>
      <div class="lg:col-span-2">
        <BookingChart :data="bookingData" />
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div class="lg:col-span-1">
        <RecentActivities :activities="recentActivities" />
      </div>
      <div class="lg:col-span-2">
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
import { ref } from "vue";
// Thay đổi các đường dẫn ở đây
import Widget from "@/components/admin/Widget.vue";
import RevenueChart from "@/components/admin/RevenueChart.vue";
import BookingChart from "@/components/admin/BookingChart.vue";
import RecentActivities from "@/components/admin/RecentActivities.vue";

// --- Dữ liệu giả (mock data) ---
// (Phần còn lại của script giữ nguyên)
const summary = ref({
  todayRevenue: 12500000,
  newBookings: 32,
  newCustomers: 15,
  pendingReviews: 8,
});

const revenueData = ref({
  labels: ["T2", "T3", "T4", "T5", "T6", "T7", "CN"],
  data: [12, 19, 8, 15, 22, 30, 25],
});

const bookingData = ref({
  labels: ["Tour", "Khách sạn", "Chuyến bay", "Xe bus"],
  data: [120, 190, 85, 40],
});

const recentActivities = ref([
  { time: "1 phút", user: "Admin", action: "xác nhận đơn hàng #1124" },
  { time: "5 phút", user: "Thanh Nguyen", action: "đặt tour Đà Nẵng" },
  { time: "10 phút", user: "Admin", action: "cập nhật khách sạn Rex" },
  { time: "1 giờ", user: "Le Pham", action: "để lại một đánh giá" },
  { time: "2 giờ", user: "Admin", action: "đã hủy đơn hàng #1055" },
]);

const recentOrders = ref([
  {
    id: 1124,
    customer: "Thanh Nguyen",
    service: "Tour",
    amount: 5200000,
    status: "Đã thanh toán",
  },
  {
    id: 1123,
    customer: "An Tran",
    service: "Khách sạn",
    amount: 1800000,
    status: "Đã thanh toán",
  },
  {
    id: 1122,
    customer: "Minh Le",
    service: "Chuyến bay",
    amount: 2100000,
    status: "Chờ thanh toán",
  },
  {
    id: 1121,
    customer: "Bao Vo",
    service: "Tour",
    amount: 8500000,
    status: "Đã hủy",
  },
]);

// --- Hàm hỗ trợ ---
const formatCurrency = (value) => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(value);
};

const getStatusClass = (status) => {
  const map = {
    "Đã thanh toán": "bg-green-100 text-green-800",
    "Chờ thanh toán": "bg-yellow-100 text-yellow-800",
    "Đã hủy": "bg-red-100 text-red-800",
  };
  return map[status] || "bg-gray-100 text-gray-800";
};
</script>
