<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h2 class="text-3xl font-bold text-gray-800">Tour đang Booking</h2>
      <div class="flex items-center space-x-4">
        <div class="flex items-center space-x-2">
          <FilterIcon class="w-4 h-4 text-gray-500" />
          <select
            v-model="filterStatus"
            class="border border-gray-300 rounded-lg px-3 py-1 text-sm focus:ring-2 focus:ring-blue-500"
          >
            <option value="all">Tất cả</option>
            <option value="Đã xác nhận">Đã xác nhận</option>
            <option value="Chờ xác nhận">Chờ xác nhận</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Bookings Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <div
        v-for="booking in filteredBookings"
        :key="booking.id"
        class="bg-white rounded-lg shadow-md p-6"
      >
        <div class="flex items-start justify-between mb-4">
          <div>
            <h3 class="text-lg font-semibold text-gray-800">
              {{ booking.tour }}
            </h3>
            <p class="text-sm text-gray-500">Booking ID: #{{ booking.id }}</p>
          </div>
          <div class="flex flex-col items-end space-y-1">
            <span
              class="px-2 py-1 text-xs rounded-full"
              :class="getStatusColor(booking.status)"
            >
              {{ booking.status }}
            </span>
            <span
              class="px-2 py-1 text-xs rounded-full"
              :class="getPaymentStatusColor(booking.paymentStatus)"
            >
              {{ booking.paymentStatus }}
            </span>
          </div>
        </div>

        <div class="space-y-3 text-sm text-gray-600">
          <div class="flex items-center">
            <UserIcon class="w-4 h-4 mr-2" />
            <span class="font-medium">{{ booking.customer }}</span>
            <span class="ml-2">({{ booking.guests }} khách)</span>
          </div>
          <div class="flex items-center">
            <PhoneIcon class="w-4 h-4 mr-2" />
            {{ booking.phone }}
          </div>
          <div class="flex items-center">
            <MailIcon class="w-4 h-4 mr-2" />
            {{ booking.email }}
          </div>
          <div class="flex items-center">
            <CalendarIcon class="w-4 h-4 mr-2" />
            Ngày tour: {{ booking.tourDate }}
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

        <div class="mt-4 flex space-x-2">
          <template v-if="booking.status === 'Chờ xác nhận'">
            <button
              class="flex-1 bg-green-600 text-white py-2 px-4 rounded-lg hover:bg-green-700 text-sm"
            >
              Xác nhận
            </button>
            <button
              class="flex-1 bg-red-600 text-white py-2 px-4 rounded-lg hover:bg-red-700 text-sm"
            >
              Từ chối
            </button>
          </template>
          <template v-else-if="booking.status === 'Đã xác nhận'">
            <button
              class="w-full bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 text-sm"
            >
              Xem chi tiết
            </button>
          </template>
        </div>
      </div>
    </div>

    <div v-if="filteredBookings.length === 0" class="text-center py-12">
      <CalendarIcon class="w-12 h-12 text-gray-400 mx-auto mb-4" />
      <p class="text-gray-500">Không có booking nào</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import {
  CalendarIcon,
  UserIcon,
  PhoneIcon,
  MailIcon,
  FilterIcon,
} from "lucide-vue-next";

const bookings = ref([
  {
    id: 1,
    tour: "Hạ Long Bay 3N2Đ",
    customer: "Nguyễn Văn A",
    phone: "0901234567",
    email: "nguyenvana@email.com",
    bookingDate: "2024-01-15",
    tourDate: "2024-02-10",
    guests: 4,
    totalAmount: "10,000,000",
    status: "Đã xác nhận",
    paymentStatus: "Đã thanh toán",
  },
  {
    id: 2,
    tour: "Sapa Trekking 2N1Đ",
    customer: "Trần Thị B",
    phone: "0912345678",
    email: "tranthib@email.com",
    bookingDate: "2024-01-14",
    tourDate: "2024-02-05",
    guests: 2,
    totalAmount: "3,600,000",
    status: "Chờ xác nhận",
    paymentStatus: "Chờ thanh toán",
  },
  {
    id: 3,
    tour: "Đà Nẵng - Hội An 3N2Đ",
    customer: "Lê Văn C",
    phone: "0923456789",
    email: "levanc@email.com",
    bookingDate: "2024-01-13",
    tourDate: "2024-02-15",
    guests: 6,
    totalAmount: "12,600,000",
    status: "Đã xác nhận",
    paymentStatus: "Đã cọc 50%",
  },
]);

const filterStatus = ref("all");

const filteredBookings = computed(() => {
  if (filterStatus.value === "all") return bookings.value;
  return bookings.value.filter((b) => b.status === filterStatus.value);
});

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

<style scoped>
/* Optional custom styles */
</style>
