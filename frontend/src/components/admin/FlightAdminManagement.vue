<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <h2 class="text-2xl font-bold text-gray-800">Quản lý Admin Chuyến bay</h2>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="flex justify-center items-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="text-center py-8">
      <div class="text-red-600 mb-4">
        <i class="fas fa-exclamation-triangle text-2xl"></i>
      </div>
      <p class="text-gray-600">{{ error }}</p>
      <button
        @click="loadFlightAdmins"
        class="mt-4 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
      >
        Thử lại
      </button>
    </div>

    <!-- Content -->
    <div v-else class="space-y-6">
      <!-- Summary Cards -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        <div class="bg-white rounded-xl shadow p-6">
          <div class="flex items-center">
            <div class="p-3 rounded-full bg-blue-100 text-blue-600">
              <i class="fas fa-users text-xl"></i>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Tổng Admin</p>
              <p class="text-2xl font-bold text-gray-900">{{ flightAdmins.length }}</p>
            </div>
          </div>
        </div>
        
        <div class="bg-white rounded-xl shadow p-6">
          <div class="flex items-center">
            <div class="p-3 rounded-full bg-green-100 text-green-600">
              <i class="fas fa-plane text-xl"></i>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Tổng Chuyến bay</p>
              <p class="text-2xl font-bold text-gray-900">{{ totalFlights }}</p>
            </div>
          </div>
        </div>
        
        <div class="bg-white rounded-xl shadow p-6">
          <div class="flex items-center">
            <div class="p-3 rounded-full bg-yellow-100 text-yellow-600">
              <i class="fas fa-ticket-alt text-xl"></i>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Tổng Booking</p>
              <p class="text-2xl font-bold text-gray-900">{{ totalBookings }}</p>
            </div>
          </div>
        </div>
        
        <div class="bg-white rounded-xl shadow p-6">
          <div class="flex items-center">
            <div class="p-3 rounded-full bg-purple-100 text-purple-600">
              <i class="fas fa-coins text-xl"></i>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Tổng Doanh thu</p>
              <p class="text-2xl font-bold text-gray-900">{{ formatCurrency(totalRevenue) }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Admin List -->
      <div class="bg-white rounded-xl shadow">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-800">Danh sách Admin Chuyến bay</h3>
        </div>
        
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Admin
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Chuyến bay
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Booking
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Doanh thu
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Tỷ lệ lấp đầy
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="admin in flightAdmins" :key="admin.adminId" class="hover:bg-gray-50">
                <td class="px-6 py-4 whitespace-nowrap">
                  <div>
                    <div class="text-sm font-medium text-gray-900">{{ admin.adminName }}</div>
                    <div class="text-sm text-gray-500">{{ admin.adminEmail }}</div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ admin.totalFlights }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ admin.totalBookings }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ formatCurrency(admin.totalRevenue) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ admin.averageOccupancyRate.toFixed(1) }}%
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  <button
                    @click="viewAdminDetail(admin)"
                    class="text-blue-600 hover:text-blue-900"
                  >
                    <i class="fas fa-eye mr-1"></i>
                    Xem chi tiết
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Admin Detail Modal -->
    <div v-if="showDetailModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
      <div class="relative top-20 mx-auto p-5 border w-11/12 max-w-4xl shadow-lg rounded-md bg-white">
        <div class="mt-3">
          <!-- Header -->
          <div class="flex justify-between items-center mb-6">
            <h3 class="text-lg font-semibold text-gray-900">
              Chi tiết Admin: {{ selectedAdmin?.adminName }}
            </h3>
            <button
              @click="showDetailModal = false"
              class="text-gray-400 hover:text-gray-600"
            >
              <i class="fas fa-times text-xl"></i>
            </button>
          </div>

          <!-- Loading Detail -->
          <div v-if="loadingDetail" class="flex justify-center items-center py-8">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          </div>

          <!-- Detail Content -->
          <div v-else-if="adminDetail" class="space-y-6">
            <!-- Admin Info -->
            <div class="bg-gray-50 rounded-lg p-4">
              <h4 class="font-semibold text-gray-800 mb-3">Thông tin Admin</h4>
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <p class="text-sm text-gray-600">Tên:</p>
                  <p class="font-medium">{{ adminDetail.adminName }}</p>
                </div>
                <div>
                  <p class="text-sm text-gray-600">Email:</p>
                  <p class="font-medium">{{ adminDetail.adminEmail }}</p>
                </div>
              </div>
            </div>

            <!-- Statistics -->
            <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
              <div class="bg-blue-50 rounded-lg p-4 text-center">
                <p class="text-2xl font-bold text-blue-600">{{ adminDetail.totalFlights }}</p>
                <p class="text-sm text-gray-600">Chuyến bay</p>
              </div>
              <div class="bg-green-50 rounded-lg p-4 text-center">
                <p class="text-2xl font-bold text-green-600">{{ adminDetail.totalBookings }}</p>
                <p class="text-sm text-gray-600">Booking</p>
              </div>
              <div class="bg-yellow-50 rounded-lg p-4 text-center">
                <p class="text-2xl font-bold text-yellow-600">{{ formatCurrency(adminDetail.totalRevenue) }}</p>
                <p class="text-sm text-gray-600">Doanh thu</p>
              </div>
              <div class="bg-purple-50 rounded-lg p-4 text-center">
                <p class="text-2xl font-bold text-purple-600">{{ adminDetail.averageOccupancyRate.toFixed(1) }}%</p>
                <p class="text-sm text-gray-600">Tỷ lệ lấp đầy</p>
              </div>
            </div>

            <!-- Recent Flights -->
            <div>
              <h4 class="font-semibold text-gray-800 mb-3">Chuyến bay gần đây</h4>
              <div class="overflow-x-auto">
                <table class="w-full text-sm">
                  <thead class="bg-gray-50">
                    <tr>
                      <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Mã chuyến bay</th>
                      <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Tuyến bay</th>
                      <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Thời gian</th>
                      <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Ghế</th>
                      <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Doanh thu</th>
                      <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Thao tác</th>
                    </tr>
                  </thead>
                  <tbody class="divide-y divide-gray-200">
                    <tr v-for="flight in adminDetail.recentFlights" :key="flight.flightId" class="hover:bg-gray-50">
                      <td class="px-4 py-2 font-medium">{{ flight.flightNumber }}</td>
                      <td class="px-4 py-2">{{ flight.departureAirport }} → {{ flight.arrivalAirport }}</td>
                      <td class="px-4 py-2">{{ formatDateTime(flight.departureTime) }}</td>
                      <td class="px-4 py-2">{{ flight.bookedSeats }}/{{ flight.totalSeats }}</td>
                      <td class="px-4 py-2 font-medium">{{ formatCurrency(flight.revenue) }}</td>
                      <td class="px-4 py-2">
                        <button
                          @click="viewFlight(flight.flightId)"
                          class="text-blue-600 hover:text-blue-900 text-sm"
                        >
                          <i class="fas fa-external-link-alt mr-1"></i>
                          Xem
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getFlightAdminSummaries, getFlightAdminDetail } from '@/api/adminApi'
import { useRouter } from 'vue-router'

const router = useRouter()

// State
const flightAdmins = ref([])
const loading = ref(false)
const error = ref('')
const showDetailModal = ref(false)
const selectedAdmin = ref(null)
const adminDetail = ref(null)
const loadingDetail = ref(false)

// Computed
const totalFlights = computed(() => 
  flightAdmins.value.reduce((sum, admin) => sum + admin.totalFlights, 0)
)

const totalBookings = computed(() => 
  flightAdmins.value.reduce((sum, admin) => sum + admin.totalBookings, 0)
)

const totalRevenue = computed(() => 
  flightAdmins.value.reduce((sum, admin) => sum + admin.totalRevenue, 0)
)

// Methods
async function loadFlightAdmins() {
  loading.value = true
  error.value = ''
  try {
    const res = await getFlightAdminSummaries()
    flightAdmins.value = res.data
  } catch (e) {
    error.value = 'Không thể tải danh sách admin chuyến bay.'
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function viewAdminDetail(admin) {
  selectedAdmin.value = admin
  showDetailModal.value = true
  loadingDetail.value = true
  adminDetail.value = null
  
  try {
    const res = await getFlightAdminDetail(admin.adminId)
    adminDetail.value = res.data
  } catch (e) {
    console.error('Không thể tải chi tiết admin:', e)
  } finally {
    loadingDetail.value = false
  }
}

function viewFlight(flightId) {
  router.push(`/plane/${flightId}`)
}

function formatCurrency(value) {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value)
}

function formatDateTime(dateTimeString) {
  if (!dateTimeString) return ''
  return new Date(dateTimeString).toLocaleString('vi-VN')
}

onMounted(() => {
  loadFlightAdmins()
})
</script>
