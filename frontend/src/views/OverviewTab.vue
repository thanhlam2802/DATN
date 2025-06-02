<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <h2 class="text-3xl font-bold text-gray-800">Tổng quan</h2>
      <div class="flex items-center text-sm text-gray-600">
        <TrendingUpIcon class="w-4 h-4 mr-1" />
        Cập nhật: {{ currentDate }}
      </div>
    </div>

    <!-- Stats Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div
        v-for="(stat, index) in stats"
        :key="index"
        class="bg-white rounded-lg shadow p-6"
      >
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-600">{{ stat.title }}</p>
            <p class="text-2xl font-bold text-gray-900">{{ stat.value }}</p>
          </div>
          <div :class="[stat.color, 'p-3 rounded-full']">
            <component :is="stat.icon" class="w-6 h-6 text-white" />
          </div>
        </div>
      </div>
    </div>

    <!-- Recent Bookings -->
    <div class="bg-white rounded-lg shadow">
      <div class="p-6 border-b">
        <h3 class="text-lg font-semibold text-gray-800">Booking gần đây</h3>
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
                Trạng thái
              </th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            <tr v-for="booking in recentBookings" :key="booking.id">
              <td
                class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900"
              >
                {{ booking.tour }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ booking.customer }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ booking.date }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  :class="[
                    'px-2 py-1 text-xs rounded-full',
                    booking.status === 'Đã xác nhận'
                      ? 'bg-green-100 text-green-800'
                      : 'bg-yellow-100 text-yellow-800',
                  ]"
                >
                  {{ booking.status }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import {
  UsersIcon,
  MapPinIcon,
  CalendarIcon,
  DollarSignIcon,
  TrendingUpIcon,
} from "lucide-vue-next";

const currentDate = new Date().toLocaleDateString("vi-VN");

const stats = ref([
  {
    title: "Tổng số Tour",
    value: "45",
    icon: MapPinIcon,
    color: "bg-blue-500",
  },
  {
    title: "Tour đang hoạt động",
    value: "32",
    icon: CalendarIcon,
    color: "bg-green-500",
  },
  {
    title: "Khách hàng",
    value: "1,234",
    icon: UsersIcon,
    color: "bg-purple-500",
  },
  {
    title: "Doanh thu tháng",
    value: "₫125M",
    icon: DollarSignIcon,
    color: "bg-yellow-500",
  },
]);

const recentBookings = ref([
  {
    id: 1,
    tour: "Hạ Long Bay 3N2Đ",
    customer: "Nguyễn Văn A",
    date: "2024-01-15",
    status: "Đã xác nhận",
  },
  {
    id: 2,
    tour: "Sapa Trekking 2N1Đ",
    customer: "Trần Thị B",
    date: "2024-01-14",
    status: "Chờ xác nhận",
  },
  {
    id: 3,
    tour: "Phú Quốc 4N3Đ",
    customer: "Lê Văn C",
    date: "2024-01-13",
    status: "Đã xác nhận",
  },
]);
</script>
