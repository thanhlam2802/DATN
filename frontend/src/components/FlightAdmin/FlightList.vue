<template>
  <div class="bg-white rounded-xl shadow-lg p-6">
    <!-- Header -->
    <div class="flex justify-between items-center mb-6">
      <div class="flex items-center space-x-4">
        <h2 class="text-2xl font-bold text-gray-800">Danh sách chuyến bay</h2>
        <div class="relative">
          <input
            type="text"
            v-model="searchQuery"
            placeholder="Tìm kiếm chuyến bay..."
            class="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          />
          <i class="fas fa-search absolute left-3 top-3 text-gray-400"></i>
        </div>
      </div>
      <button
        @click="currentView = 'FlightForm'"
        class="inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
      >
        <i class="fas fa-plus mr-2"></i>
        Thêm chuyến bay
      </button>
    </div>

    <!-- Filters -->
    <div class="flex flex-wrap gap-4 mb-6">
      <select
        v-model="statusFilter"
        class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
      >
        <option value="">Tất cả trạng thái</option>
        <option value="scheduled">Đã lên lịch</option>
        <option value="in-progress">Đang bay</option>
        <option value="completed">Đã hoàn thành</option>
        <option value="cancelled">Đã hủy</option>
      </select>

      <div class="flex items-center space-x-2">
        <input
          type="date"
          v-model="dateFilter"
          class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        />
        <button
          @click="clearFilters"
          class="px-4 py-2 text-gray-600 hover:text-gray-800"
        >
          <i class="fas fa-times"></i>
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="overflow-x-auto">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Mã chuyến bay
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Tuyến bay
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Thời gian
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Hãng hàng không
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Trạng thái
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Thao tác
            </th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="flight in filteredFlights" :key="flight.id" class="hover:bg-gray-50">
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
              {{ flight.flightNumber }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              {{ flight.route }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              {{ formatDateTime(flight.departureTime) }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              {{ flight.airline }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span
                :class="[
                  'px-2 inline-flex text-xs leading-5 font-semibold rounded-full',
                  getStatusClass(flight.status)
                ]"
              >
                {{ getStatusText(flight.status) }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              <div class="flex items-center space-x-3">
                <button
                  @click="editFlight(flight)"
                  class="text-blue-600 hover:text-blue-900"
                >
                  <i class="fas fa-edit"></i>
                </button>
                <button
                  @click="deleteFlight(flight)"
                  class="text-red-600 hover:text-red-900"
                >
                  <i class="fas fa-trash"></i>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div class="flex justify-between items-center mt-6">
      <div class="text-sm text-gray-700">
        Hiển thị {{ paginationInfo.start }} - {{ paginationInfo.end }} của {{ paginationInfo.total }} kết quả
      </div>
      <div class="flex items-center space-x-2">
        <button
          @click="prevPage"
          :disabled="currentPage === 1"
          class="px-3 py-1 border border-gray-300 rounded-md disabled:opacity-50"
        >
          <i class="fas fa-chevron-left"></i>
        </button>
        <span class="px-3 py-1">{{ currentPage }}</span>
        <button
          @click="nextPage"
          :disabled="currentPage === totalPages"
          class="px-3 py-1 border border-gray-300 rounded-md disabled:opacity-50"
        >
          <i class="fas fa-chevron-right"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// State
const searchQuery = ref('')
const statusFilter = ref('')
const dateFilter = ref('')
const currentPage = ref(1)
const pageSize = 10

// Mock data - Replace with actual API calls
const flights = ref([
  {
    id: 1,
    flightNumber: 'VN123',
    route: 'Hà Nội - TP.HCM',
    departureTime: '2024-03-20T10:00:00',
    airline: 'Vietnam Airlines',
    status: 'scheduled'
  },
  {
    id: 2,
    flightNumber: 'VN456',
    route: 'TP.HCM - Đà Nẵng',
    departureTime: '2024-03-20T14:30:00',
    airline: 'Vietnam Airlines',
    status: 'in-progress'
  },
  // Add more mock data as needed
])

// Computed
const filteredFlights = computed(() => {
  let result = flights.value

  // Apply search filter
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(flight => 
      flight.flightNumber.toLowerCase().includes(query) ||
      flight.route.toLowerCase().includes(query) ||
      flight.airline.toLowerCase().includes(query)
    )
  }

  // Apply status filter
  if (statusFilter.value) {
    result = result.filter(flight => flight.status === statusFilter.value)
  }

  // Apply date filter
  if (dateFilter.value) {
    result = result.filter(flight => 
      flight.departureTime.startsWith(dateFilter.value)
    )
  }

  return result
})

const totalPages = computed(() => 
  Math.ceil(filteredFlights.value.length / pageSize)
)

const paginationInfo = computed(() => {
  const start = (currentPage.value - 1) * pageSize + 1
  const end = Math.min(start + pageSize - 1, filteredFlights.value.length)
  return {
    start,
    end,
    total: filteredFlights.value.length
  }
})

// Methods
const formatDateTime = (dateTime) => {
  return new Date(dateTime).toLocaleString('vi-VN')
}

const getStatusClass = (status) => {
  const classes = {
    'scheduled': 'bg-blue-100 text-blue-800',
    'in-progress': 'bg-green-100 text-green-800',
    'completed': 'bg-gray-100 text-gray-800',
    'cancelled': 'bg-red-100 text-red-800'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

const getStatusText = (status) => {
  const texts = {
    'scheduled': 'Đã lên lịch',
    'in-progress': 'Đang bay',
    'completed': 'Đã hoàn thành',
    'cancelled': 'Đã hủy'
  }
  return texts[status] || status
}

const clearFilters = () => {
  statusFilter.value = ''
  dateFilter.value = ''
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

const editFlight = (flight) => {
  // Implement edit functionality
  console.log('Edit flight:', flight)
}

const deleteFlight = (flight) => {
  // Implement delete functionality
  console.log('Delete flight:', flight)
}
</script> 