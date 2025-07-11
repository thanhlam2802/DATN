<template>
  <div class="bg-white rounded-xl shadow-lg p-6">
    <!-- Header -->
    <div class="flex justify-between items-center mb-6">
      <div class="flex items-center space-x-4">
        <h2 class="text-2xl font-bold text-gray-800">Quản lý đặt chỗ</h2>
        <div class="relative">
          <input
            type="text"
            v-model="searchQuery"
            placeholder="Tìm kiếm đặt chỗ..."
            class="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          />
          <i class="fas fa-search absolute left-3 top-3 text-gray-400"></i>
        </div>
      </div>
    </div>

    <!-- Filters -->
    <div class="flex flex-wrap gap-4 mb-6">
      <select
        v-model="statusFilter"
        class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
      >
        <option value="">Tất cả trạng thái</option>
        <option value="confirmed">Đã xác nhận</option>
        <option value="pending">Đang chờ</option>
        <option value="cancelled">Đã hủy</option>
        <option value="completed">Đã hoàn thành</option>
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
              Mã đặt chỗ
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Chuyến bay
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Hành khách
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Ngày đặt
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Tổng tiền
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
          <tr v-for="booking in filteredBookings" :key="booking.id" class="hover:bg-gray-50">
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
              {{ booking.id }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              {{ booking.flightSlot.flight.name }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              {{ booking.customer.name }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              {{ formatDate(booking.bookingDate) }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              {{ formatCurrency(booking.ticketDetail.price) }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span
                :class="[
                  'px-2 inline-flex text-xs leading-5 font-semibold rounded-full',
                  getStatusClass(booking.status)
                ]"
              >
                {{ getStatusText(booking.status) }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              <div class="flex items-center space-x-3">
                <button
                  @click="viewDetails(booking)"
                  class="text-blue-600 hover:text-blue-900"
                >
                  <i class="fas fa-eye"></i>
                </button>
                <button
                  v-if="booking.status === 'pending'"
                  @click="confirmBooking(booking)"
                  class="text-green-600 hover:text-green-900"
                >
                  <i class="fas fa-check"></i>
                </button>
                <button
                  v-if="['pending', 'confirmed'].includes(booking.status)"
                  @click="cancelBooking(booking)"
                  class="text-red-600 hover:text-red-900"
                >
                  <i class="fas fa-times"></i>
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

    <!-- Booking Details Modal -->
    <div v-if="selectedBooking" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4 pt-4 pb-20 text-center sm:block sm:p-0">
        <div class="fixed inset-0 transition-opacity" aria-hidden="true">
          <div class="absolute inset-0 bg-gray-500 opacity-75"></div>
        </div>

        <div class="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
          <div class="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
            <div class="sm:flex sm:items-start">
              <div class="mt-3 text-center sm:mt-0 sm:text-left w-full">
                <h3 class="text-lg leading-6 font-medium text-gray-900 mb-4">
                  Chi tiết đặt chỗ
                </h3>
                <div class="mt-2 space-y-4">
                  <div class="grid grid-cols-2 gap-4">
                    <div>
                      <p class="text-sm font-medium text-gray-500">Mã đặt chỗ</p>
                      <p class="mt-1 text-sm text-gray-900">{{ selectedBooking.id }}</p>
                    </div>
                    <div>
                      <p class="text-sm font-medium text-gray-500">Trạng thái</p>
                      <p class="mt-1 text-sm text-gray-900">{{ getStatusText(selectedBooking.status) }}</p>
                    </div>
                    <div>
                      <p class="text-sm font-medium text-gray-500">Chuyến bay</p>
                      <p class="mt-1 text-sm text-gray-900">{{ selectedBooking.flightSlot.flight.name }}</p>
                    </div>
                    <div>
                      <p class="text-sm font-medium text-gray-500">Hành khách</p>
                      <p class="mt-1 text-sm text-gray-900">{{ selectedBooking.customer.name }}</p>
                    </div>
                    <div>
                      <p class="text-sm font-medium text-gray-500">Ngày đặt</p>
                      <p class="mt-1 text-sm text-gray-900">{{ formatDate(selectedBooking.bookingDate) }}</p>
                    </div>
                    <div>
                      <p class="text-sm font-medium text-gray-500">Tổng tiền</p>
                      <p class="mt-1 text-sm text-gray-900">{{ formatCurrency(selectedBooking.ticketDetail.price) }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
            <button
              type="button"
              @click="selectedBooking = null"
              class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
            >
              Đóng
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getAdminFlightBookings } from '@/api/flightApi'
import FlightBooking from '@/entity/FlightBooking'

const searchQuery = ref('')
const statusFilter = ref('')
const dateFilter = ref('')
const currentPage = ref(1)
const pageSize = 10
const selectedBooking = ref(null)
const bookings = ref([])
const loading = ref(false)
const error = ref('')

onMounted(async () => {
  loading.value = true
  try {
    const res = await getAdminFlightBookings()
    bookings.value = res.data
  } catch (e) {
    error.value = 'Không thể tải danh sách đặt chỗ.'
  } finally {
    loading.value = false
  }
})

const filteredBookings = computed(() => {
  let result = bookings.value
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(booking => 
      (booking.id && booking.id.toString().includes(query))
    )
  }
  if (dateFilter.value) {
    result = result.filter(booking => 
      booking.bookingDate && booking.bookingDate.startsWith(dateFilter.value)
    )
  }
  return result
})

const paginationInfo = computed(() => {
  const total = filteredBookings.value.length
  const start = (currentPage.value - 1) * pageSize + 1
  const end = Math.min(currentPage.value * pageSize, total)
  return { start, end, total }
})

const totalPages = computed(() => Math.ceil(filteredBookings.value.length / pageSize))

function prevPage() {
  if (currentPage.value > 1) currentPage.value--
}
function nextPage() {
  if (currentPage.value < totalPages.value) currentPage.value++
}

function formatDate(dt) {
  if (!dt) return ''
  return new Date(dt).toLocaleDateString('vi-VN')
}

function formatCurrency(value) {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value)
}

function getStatusClass(status) {
  const classes = {
    'confirmed': 'bg-green-100 text-green-800',
    'pending': 'bg-yellow-100 text-yellow-800',
    'cancelled': 'bg-red-100 text-red-800',
    'completed': 'bg-gray-100 text-gray-800'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

function getStatusText(status) {
  const texts = {
    'confirmed': 'Đã xác nhận',
    'pending': 'Đang chờ',
    'cancelled': 'Đã hủy',
    'completed': 'Đã hoàn thành'
  }
  return texts[status] || status
}

function clearFilters() {
  statusFilter.value = ''
  dateFilter.value = ''
}

function viewDetails(booking) {
  selectedBooking.value = booking
}

function confirmBooking(booking) {
  // Implement confirm functionality
  console.log('Confirm booking:', booking)
}

function cancelBooking(booking) {
  // Implement cancel functionality
  console.log('Cancel booking:', booking)
}
</script> 