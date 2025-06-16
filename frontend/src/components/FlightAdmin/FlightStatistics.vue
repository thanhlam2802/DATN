<template>
  <div class="space-y-6">
    <!-- Date Range Picker -->
    <div class="bg-white rounded-xl shadow-lg p-6">
      <div class="flex items-center justify-between mb-6">
        <h2 class="text-2xl font-bold text-gray-800">Thống kê chuyến bay</h2>
        <div class="flex items-center space-x-4">
          <div class="flex items-center space-x-2">
            <input
              type="date"
              v-model="startDate"
              class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
            <span class="text-gray-500">đến</span>
            <input
              type="date"
              v-model="endDate"
              class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
          <button
            @click="updateStatistics"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
          >
            <i class="fas fa-sync-alt mr-2"></i>
            Cập nhật
          </button>
        </div>
      </div>
    </div>

    <!-- Statistics Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
      <div class="bg-white rounded-xl shadow-lg p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-600">Tổng chuyến bay</p>
            <p class="text-2xl font-bold text-gray-900">{{ statistics.totalFlights }}</p>
          </div>
          <div class="p-3 bg-blue-100 rounded-full">
            <i class="fas fa-plane text-blue-600 text-xl"></i>
          </div>
        </div>
        <div class="mt-4">
          <span class="text-sm text-green-600">
            <i class="fas fa-arrow-up"></i>
            {{ statistics.flightGrowth }}%
          </span>
          <span class="text-sm text-gray-500 ml-2">so với tháng trước</span>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-lg p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-600">Tổng đặt chỗ</p>
            <p class="text-2xl font-bold text-gray-900">{{ statistics.totalBookings }}</p>
          </div>
          <div class="p-3 bg-green-100 rounded-full">
            <i class="fas fa-ticket-alt text-green-600 text-xl"></i>
          </div>
        </div>
        <div class="mt-4">
          <span class="text-sm text-green-600">
            <i class="fas fa-arrow-up"></i>
            {{ statistics.bookingGrowth }}%
          </span>
          <span class="text-sm text-gray-500 ml-2">so với tháng trước</span>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-lg p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-600">Doanh thu</p>
            <p class="text-2xl font-bold text-gray-900">{{ formatCurrency(statistics.revenue) }}</p>
          </div>
          <div class="p-3 bg-purple-100 rounded-full">
            <i class="fas fa-money-bill-wave text-purple-600 text-xl"></i>
          </div>
        </div>
        <div class="mt-4">
          <span class="text-sm text-green-600">
            <i class="fas fa-arrow-up"></i>
            {{ statistics.revenueGrowth }}%
          </span>
          <span class="text-sm text-gray-500 ml-2">so với tháng trước</span>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-lg p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-600">Tỷ lệ lấp đầy</p>
            <p class="text-2xl font-bold text-gray-900">{{ statistics.occupancyRate }}%</p>
          </div>
          <div class="p-3 bg-yellow-100 rounded-full">
            <i class="fas fa-chart-line text-yellow-600 text-xl"></i>
          </div>
        </div>
        <div class="mt-4">
          <span class="text-sm text-green-600">
            <i class="fas fa-arrow-up"></i>
            {{ statistics.occupancyGrowth }}%
          </span>
          <span class="text-sm text-gray-500 ml-2">so với tháng trước</span>
        </div>
      </div>
    </div>

    <!-- Charts -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <!-- Bookings by Route -->
      <div class="bg-white rounded-xl shadow-lg p-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">Đặt chỗ theo tuyến</h3>
        <div class="h-80">
          <!-- Placeholder for chart -->
          <div class="h-full flex items-center justify-center text-gray-500">
            <p>Biểu đồ phân bố đặt chỗ theo tuyến</p>
          </div>
        </div>
      </div>

      <!-- Revenue Trend -->
      <div class="bg-white rounded-xl shadow-lg p-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">Xu hướng doanh thu</h3>
        <div class="h-80">
          <!-- Placeholder for chart -->
          <div class="h-full flex items-center justify-center text-gray-500">
            <p>Biểu đồ xu hướng doanh thu</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Top Routes Table -->
    <div class="bg-white rounded-xl shadow-lg p-6">
      <h3 class="text-lg font-semibold text-gray-800 mb-4">Top tuyến bay phổ biến</h3>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Tuyến bay
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Số chuyến
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Đặt chỗ
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Doanh thu
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Tỷ lệ lấp đầy
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="route in topRoutes" :key="route.route" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                {{ route.route }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ route.flights }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ route.bookings }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ formatCurrency(route.revenue) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ route.occupancyRate }}%
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// State
const startDate = ref('')
const endDate = ref('')

// Mock data - Replace with actual API calls
const statistics = ref({
  totalFlights: 150,
  flightGrowth: 12,
  totalBookings: 1200,
  bookingGrowth: 8,
  revenue: 1500000000,
  revenueGrowth: 15,
  occupancyRate: 85,
  occupancyGrowth: 5
})

const topRoutes = ref([
  {
    route: 'Hà Nội - TP.HCM',
    flights: 45,
    bookings: 450,
    revenue: 450000000,
    occupancyRate: 90
  },
  {
    route: 'TP.HCM - Đà Nẵng',
    flights: 30,
    bookings: 300,
    revenue: 300000000,
    occupancyRate: 85
  },
  {
    route: 'Hà Nội - Đà Nẵng',
    flights: 25,
    bookings: 250,
    revenue: 250000000,
    occupancyRate: 80
  }
])

// Methods
const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value)
}

const updateStatistics = () => {
  // Implement API call to update statistics based on date range
  console.log('Updating statistics for date range:', startDate.value, 'to', endDate.value)
}
</script> 