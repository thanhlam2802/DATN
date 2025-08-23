<template>
  <div class="bg-white rounded-xl shadow-lg p-6 relative">
    <div v-if="loading" class="absolute inset-0 bg-white/60 backdrop-blur-sm z-10 flex items-center justify-center">
      <span class="animate-spin w-8 h-8 border-2 border-indigo-500 border-t-transparent rounded-full"></span>
      <span class="ml-3 text-gray-700 font-medium">Đang tải...</span>
    </div>
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
              Thao tác
            </th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="res in filteredBookings" :key="res.booking.bookingId" class="hover:bg-gray-50">
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
              {{ res.booking.bookingId }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              {{ res.booking.flight.name }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              {{ res.customer?.fullName || 'N/A' }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              {{ formatDate(res.booking.createdAt) }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              {{ formatCurrency(res.booking.totalPrice) }}
            </td>
            
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              <div class="flex items-center space-x-3">
                <button @click="viewDetails(res)" class="text-blue-600 hover:text-blue-900" :disabled="loading">
                  <i class="fas fa-eye"></i>
                </button>
                <button v-if="res.booking.status === 'pending'" @click="confirmBooking(res)" class="text-green-600 hover:text-green-900" :disabled="loading">
                  <i class="fas fa-check"></i>
                </button>
                <button v-if="['pending', 'confirmed'].includes(res.booking.status)" @click="cancelBooking(res)" class="text-yellow-600 hover:text-yellow-800" :disabled="loading">
                  <i class="fas fa-ban"></i>
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
        <div class="fixed inset-0 transition-opacity z-0" aria-hidden="true">
          <div class="absolute inset-0 bg-gray-500 opacity-75"></div>
        </div>

        <div class="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full relative z-10">
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
                      <p class="mt-1 text-sm text-gray-900">{{ selectedBooking.booking.bookingId }}</p>
                    </div>
                    <div>
                      <p class="text-sm font-medium text-gray-500">Trạng thái</p>
                      <p class="mt-1 text-sm text-gray-900">{{ getStatusText(selectedBooking.booking.status) }}</p>
                    </div>
                    <div>
                      <p class="text-sm font-medium text-gray-500">Chuyến bay</p>
                      <p class="mt-1 text-sm text-gray-900">{{ selectedBooking.booking.flight.name }}</p>
                    </div>
                    <div>
                      <p class="text-sm font-medium text-gray-500">Hành khách</p>
                      <p class="mt-1 text-sm text-gray-900">{{ selectedBooking.customer?.fullName || 'N/A' }}</p>
                    </div>
                    <div>
                      <p class="text-sm font-medium text-gray-500">Ngày đặt</p>
                      <p class="mt-1 text-sm text-gray-900">{{ formatDate(selectedBooking.booking.createdAt) }}</p>
                    </div>
                    <div>
                      <p class="text-sm font-medium text-gray-500">Tổng tiền</p>
                      <p class="mt-1 text-sm text-gray-900">{{ formatCurrency(selectedBooking.booking.totalPrice) }}</p>
                    </div>
                  </div>
                  <!-- Editable form -->
                  <div class="mt-4 border-t pt-4">
                    <h4 class="font-semibold mb-2">Cập nhật thông tin</h4>
                    <div class="grid grid-cols-2 gap-4">
                      <div>
                        <label class="block text-sm text-gray-600 mb-1">Họ tên khách</label>
                        <input v-model="editForm.customer.fullName" class="w-full border rounded px-3 py-2" />
                      </div>
                      <div>
                        <label class="block text-sm text-gray-600 mb-1">Email</label>
                        <input v-model="editForm.customer.email" class="w-full border rounded px-3 py-2" />
                      </div>
                      <div>
                        <label class="block text-sm text-gray-600 mb-1">SĐT</label>
                        <input v-model="editForm.customer.phone" class="w-full border rounded px-3 py-2" />
                      </div>
                      <div>
                        <label class="block text-sm text-gray-600 mb-1">Hộ chiếu</label>
                        <input v-model="editForm.customer.passport" class="w-full border rounded px-3 py-2" />
                      </div>
                      <div>
                        <label class="block text-sm text-gray-600 mb-1">Số ghế</label>
                        <input v-model="editForm.flightSlot.seatNumber" class="w-full border rounded px-3 py-2" />
                      </div>
                      <div>
                        <label class="block text-sm text-gray-600 mb-1">Giá</label>
                        <input type="number" v-model.number="editForm.flightSlot.price" class="w-full border rounded px-3 py-2" />
                      </div>
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
            <button
              type="button"
              @click="saveEdit()"
              class="mt-3 w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-indigo-600 text-white text-base font-medium hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
            >
              Lưu thay đổi
            </button>
            <button
              type="button"
              @click="goEditSeat()"
              class="mt-3 w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-sky-600 text-white text-base font-medium hover:bg-sky-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-sky-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
            >
              Chỉnh sửa vé ghế
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue'
import { getAdminFlightBookings, updateAdminFlightBookingStatus, deleteAdminFlightBooking, updateAdminFlightBooking } from '@/api/flightApi'
import FlightBooking from '@/entity/FlightBooking'

const searchQuery = ref('')
const statusFilter = ref('')
const dateFilter = ref('')
const currentPage = ref(1)
const pageSize = 10
const selectedBooking = ref(null)
const editForm = ref({ customer: {}, flightSlot: {} })
const selectMenuItem = inject('selectMenuItem', null)
const bookings = ref([]) // array of FlightOrderReservationDto
const loading = ref(false)
const error = ref('')

onMounted(loadBookings)

async function loadBookings() {
  loading.value = true
  try {
    const res = await getAdminFlightBookings({ page: currentPage.value - 1, size: pageSize })
    bookings.value = res.data
  } catch (e) {
    error.value = 'Không thể tải danh sách đặt chỗ.'
    window.$toast?.(error.value, 'error')
  } finally {
    loading.value = false
  }
}

const filteredBookings = computed(() => {
  let result = bookings.value
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(res => 
      (res.booking?.bookingId && res.booking.bookingId.toString().includes(query))
    )
  }
  if (dateFilter.value) {
    result = result.filter(res => 
      res.booking?.createdAt && res.booking.createdAt.startsWith(dateFilter.value)
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

const totalPages = computed(() => Math.max(1, Math.ceil(filteredBookings.value.length / pageSize)))

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

function viewDetails(res) {
  selectedBooking.value = res
  editForm.value = {
    customer: {
      fullName: res.customer?.fullName || '',
      email: res.customer?.email || '',
      phone: res.customer?.phone || '',
      passport: res.customer?.passport || ''
    },
    flightSlot: {
      seatNumber: res.flightSlot?.seatNumber || '',
      price: res.flightSlot?.price || 0,
      isAisle: res.flightSlot?.isAisle || false,
      isWindow: res.flightSlot?.isWindow || false,
      isBusiness: res.flightSlot?.isBusiness || false,
      carryOnLuggage: res.flightSlot?.carryOnLuggage || 0
    }
  }
}
async function saveEdit() {
  if (!selectedBooking.value) return
  try {
    loading.value = true
    const dto = {
      customer: editForm.value.customer,
      flightSlot: editForm.value.flightSlot,
      booking: { totalPrice: selectedBooking.value.booking.totalPrice }
    }
    await updateAdminFlightBooking(selectedBooking.value.booking.bookingId, dto)
    window.$toast?.('Cập nhật booking thành công', 'success')
    selectedBooking.value = null
    await loadBookings()
  } catch (e) {
    window.$toast?.('Cập nhật booking thất bại', 'error')
  } finally {
    loading.value = false
  }
}

async function confirmBooking(res) {
  try {
    loading.value = true
    await updateAdminFlightBookingStatus(res.booking.bookingId, 'confirmed')
    window.$toast?.('Đã xác nhận đặt chỗ', 'success')
    await loadBookings()
  } catch (e) {
    window.$toast?.('Xác nhận thất bại', 'error')
  } finally {
    loading.value = false
  }
}

async function cancelBooking(res) {
  try {
    loading.value = true
    await updateAdminFlightBookingStatus(res.booking.bookingId, 'cancelled')
    window.$toast?.('Đã hủy đặt chỗ', 'success')
    await loadBookings()
  } catch (e) {
    window.$toast?.('Hủy thất bại', 'error')
  } finally {
    loading.value = false
  }
}

async function removeBooking(res) {
  try {
    loading.value = true
    await deleteAdminFlightBooking(res.booking.bookingId)
    window.$toast?.('Xóa đặt chỗ thành công', 'success')
    await loadBookings()
  } catch (e) {
    window.$toast?.('Xóa đặt chỗ thất bại', 'error')
  } finally {
    loading.value = false
  }
}

function goEditSeat() {
  if (!selectedBooking.value) return
  const flightId = selectedBooking.value.booking?.flight?.id
  if (selectMenuItem && flightId) {
    selectMenuItem({ component: 'DetailFlightAdmin' }, flightId)
  }
}
</script> 