<template>
  <AdminLayout>
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <Widget title="Tổng doanh thu" icon="fas fa-coins" :value="revenue" />
      <Widget title="Tổng booking" icon="fas fa-ticket-alt" :value="bookings" />
      <Widget title="Người dùng" icon="fas fa-users" :value="users" />
      <Widget
        title="Tour đang hoạt động"
        icon="fas fa-map-marked-alt"
        :value="activeTours"
      />
    </div>
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <RevenueChart :data="revenueChartData" />
      <BookingChart :data="bookingChartData" />
    </div>
    <div class="mt-8">
      <RecentActivities :activities="recentActivities" />
    </div>
  </AdminLayout>
</template>

<script setup>
import AdminLayout from "../../layouts/AdminLayout.vue";
import Widget from "../../components/admin/Widget.vue";
import RevenueChart from "../../components/admin/RevenueChart.vue";
import BookingChart from "../../components/admin/BookingChart.vue";
import RecentActivities from "../../components/admin/RecentActivities.vue";

// Dữ liệu mẫu
const revenue = "2,350,000,000₫";
const bookings = 1240;
const users = 560;
const activeTours = 32;

const revenueChartData = {
  labels: ["Th1", "Th2", "Th3", "Th4", "Th5", "Th6", "Th7"],
  data: [320, 450, 380, 600, 700, 800, 900],
};
const bookingChartData = {
  labels: ["Th1", "Th2", "Th3", "Th4", "Th5", "Th6", "Th7"],
  data: [120, 180, 160, 200, 220, 240, 320],
};
const recentActivities = [
  { time: "10:30", user: "admin", action: "Tạo tour mới: Hạ Long Bay" },
  { time: "09:15", user: "user01", action: "Đặt vé tour Sapa" },
  { time: "08:50", user: "admin", action: "Cập nhật giá tour Đà Nẵng" },
  { time: "08:00", user: "user02", action: "Đăng ký tài khoản mới" },
];
</script>
