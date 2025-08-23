<template>
  <div class="bg-white rounded-xl shadow-lg p-6">
    <!-- Header -->
    <div class="flex justify-between items-center mb-6">
      <div class="flex items-center space-x-4">
        <h2 class="text-2xl font-bold text-gray-800">Danh sách chuyến bay</h2>
        
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
      <div class="flex items-center space-x-2">
        <select
          v-model="monthFilter"
          class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        >
          <option value="">Tất cả tháng</option>
          <option v-for="month in 12" :key="month" :value="month">Tháng {{ month }}</option>
        </select>
        
        <select
          v-model="yearFilter"
          class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        >
          <option value="">Tất cả năm</option>
          <option v-for="year in availableYears" :key="year" :value="year">{{ year }}</option>
        </select>
        
        <button
          @click="clearFilters"
          class="px-4 py-2 text-gray-600 hover:text-gray-800"
        >
          <i class="fas fa-times"></i>
        </button>
      </div>
    </div>

    <!-- Loading Skeleton -->
    <div v-if="loading" class="overflow-x-auto">
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
              Thao tác
            </th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="n in 5" :key="n" class="hover:bg-gray-50">
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="h-4 bg-gray-200 rounded animate-pulse"></div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="h-4 bg-gray-200 rounded animate-pulse"></div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="h-4 bg-gray-200 rounded animate-pulse"></div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="h-4 bg-gray-200 rounded animate-pulse"></div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="h-6 w-20 bg-gray-200 rounded-full animate-pulse"></div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="flex items-center space-x-3">
                <div class="h-4 w-4 bg-gray-200 rounded animate-pulse"></div>
                <div class="h-4 w-4 bg-gray-200 rounded animate-pulse"></div>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Error Message -->
    <div v-else-if="error" class="text-center py-8">
      <div class="text-red-600 mb-4">
        <i class="fas fa-exclamation-triangle text-2xl"></i>
      </div>
      <p class="text-gray-600">{{ error }}</p>
      <button
        @click="loadFlights"
        class="mt-4 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
      >
        Thử lại
      </button>
    </div>

    <!-- Table -->
    <div v-else class="overflow-x-auto">
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
              Thao tác
            </th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="flight in paginatedFlights" :key="flight.id" class="hover:bg-gray-50">
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
              {{ flight.flightNumber }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              {{ flight.departureAirport?.name }} → {{ flight.arrivalAirport?.name }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              {{ formatDateTime(flight.departureTime) }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              {{ flight.name }}
            </td>
            
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              <div class="flex items-center space-x-3">
                <button
                  @click="goToDetail(flight.id)"
                  class="text-blue-600 hover:text-blue-900"
                >
                  <i class="fas fa-edit"></i>
                </button>
                <button
                  @click="deleteFlight(flight)"
                  :disabled="loadingDelete[flight.id]"
                  class="text-red-600 hover:text-red-900 flex items-center"
                >
                  <i v-if="!loadingDelete[flight.id]" class="fas fa-trash"></i>
                  <span v-else class="fa fa-spinner fa-spin"></span>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div v-if="!loading && !error" class="flex justify-between items-center mt-6">
      <div class="text-sm text-gray-700">
        Hiển thị {{ paginationInfo.start }} - {{ paginationInfo.end }} của {{ paginationInfo.total }} kết quả
      </div>
      <div class="flex items-center space-x-2">
        <button
          @click="goToPage(1)"
          :disabled="currentPage === 1"
          class="px-3 py-1 border border-gray-300 rounded-md disabled:opacity-50"
        >
          <i class="fas fa-angle-double-left"></i>
        </button>
        <button
          @click="prevPage"
          :disabled="currentPage === 1"
          class="px-3 py-1 border border-gray-300 rounded-md disabled:opacity-50"
        >
          <i class="fas fa-chevron-left"></i>
        </button>
        
        <div class="flex items-center space-x-1">
          <template v-for="page in displayedPages" :key="page">
            <button 
              v-if="page !== '...'"
              @click="goToPage(page)" 
              :class="[
                'px-3 py-1 border border-gray-300 rounded-md',
                currentPage === page ? 'bg-blue-500 text-white' : 'hover:bg-gray-100'
              ]"
            >
              {{ page }}
            </button>
            <span v-else class="px-2">{{ page }}</span>
          </template>
        </div>
        
        <button
          @click="nextPage"
          :disabled="currentPage === totalPages"
          class="px-3 py-1 border border-gray-300 rounded-md disabled:opacity-50"
        >
          <i class="fas fa-chevron-right"></i>
        </button>
        <button
          @click="goToPage(totalPages)"
          :disabled="currentPage === totalPages"
          class="px-3 py-1 border border-gray-300 rounded-md disabled:opacity-50"
        >
          <i class="fas fa-angle-double-right"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue'
import { getAdminFlights, deleteAdminFlight } from '@/api/flightApi'
import Flight from '@/entity/Flight'
import { useRouter } from 'vue-router'


// State
const searchQuery = ref('')
const monthFilter = ref('')
const yearFilter = ref('')
const currentPage = ref(1)
const pageSize = 10
const flights = ref([])
const loading = ref(false)
const error = ref('')
const loadingDelete = ref({})

// Load flights function
async function loadFlights() {
  loading.value = true
  error.value = ''
  try {
    const res = await getAdminFlights()
    flights.value = res.data
  } catch (e) {
    error.value = 'Không thể tải danh sách chuyến bay.'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadFlights()
})

// Computed
const availableYears = computed(() => {
  const years = new Set()
  const currentYear = new Date().getFullYear()
  
  // Thêm năm hiện tại và 5 năm tiếp theo
  for (let i = 0; i <= 5; i++) {
    years.add(currentYear + i)
  }
  
  // Thêm các năm từ dữ liệu chuyến bay
  flights.value.forEach(flight => {
    if (flight.departureTime) {
      const year = new Date(flight.departureTime).getFullYear()
      years.add(year)
    }
  })
  
  return Array.from(years).sort((a, b) => a - b)
})

const filteredFlights = computed(() => {
  let result = flights.value
  
  // Lọc theo từ khóa tìm kiếm
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(flight => 
      (flight.flightNumber && flight.flightNumber.toLowerCase().includes(query)) ||
      (flight.name && flight.name.toLowerCase().includes(query))
    )
  }
  
  // Lọc theo tháng
  if (monthFilter.value) {
    result = result.filter(flight => {
      if (!flight.departureTime) return false
      const month = new Date(flight.departureTime).getMonth() + 1 // getMonth() trả về 0-11
      return month === parseInt(monthFilter.value)
    })
  }
  
  // Lọc theo năm
  if (yearFilter.value) {
    result = result.filter(flight => {
      if (!flight.departureTime) return false
      const year = new Date(flight.departureTime).getFullYear()
      return year === parseInt(yearFilter.value)
    })
  }
  
  return result
})

// Phân trang
const paginatedFlights = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  const end = start + pageSize
  return filteredFlights.value.slice(start, end)
})

const paginationInfo = computed(() => {
  const total = filteredFlights.value.length
  const start = total === 0 ? 0 : (currentPage.value - 1) * pageSize + 1
  const end = Math.min(currentPage.value * pageSize, total)
  return { start, end, total }
})

const totalPages = computed(() => Math.ceil(filteredFlights.value.length / pageSize))

// Hiển thị các trang trong phân trang
const displayedPages = computed(() => {
  if (totalPages.value <= 7) {
    // Nếu tổng số trang <= 7, hiển thị tất cả các trang
    return Array.from({ length: totalPages.value }, (_, i) => i + 1)
  }
  
  // Nếu tổng số trang > 7, hiển thị một số trang và dấu "..."
  const pages = []
  
  // Luôn hiển thị trang đầu tiên
  pages.push(1)
  
  // Xác định phạm vi trang hiển thị xung quanh trang hiện tại
  if (currentPage.value <= 3) {
    // Nếu trang hiện tại gần đầu, hiển thị 5 trang đầu tiên
    pages.push(2, 3, 4, 5)
    pages.push('...')
  } else if (currentPage.value >= totalPages.value - 2) {
    // Nếu trang hiện tại gần cuối, hiển thị 5 trang cuối cùng
    pages.push('...')
    pages.push(totalPages.value - 4, totalPages.value - 3, totalPages.value - 2, totalPages.value - 1)
  } else {
    // Nếu trang hiện tại ở giữa, hiển thị 2 trang trước và 2 trang sau trang hiện tại
    pages.push('...')
    pages.push(currentPage.value - 1, currentPage.value, currentPage.value + 1)
    pages.push('...')
  }
  
  // Luôn hiển thị trang cuối cùng
  pages.push(totalPages.value)
  
  return pages
})

// Methods
function formatDateTime(dt) {
  if (!dt) return ''
  return new Date(dt).toLocaleString('vi-VN')
}

function getStatusClass(status) {
  const classes = {
    'scheduled': 'bg-blue-100 text-blue-800',
    'in-progress': 'bg-green-100 text-green-800',
    'completed': 'bg-gray-100 text-gray-800',
    'cancelled': 'bg-red-100 text-red-800'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

function getStatusText(status) {
  const texts = {
    'scheduled': 'Đã lên lịch',
    'in-progress': 'Đang bay',
    'completed': 'Đã hoàn thành',
    'cancelled': 'Đã hủy'
  }
  return texts[status] || status
}

function clearFilters() {
  monthFilter.value = ''
  yearFilter.value = ''
  currentPage.value = 1 // Reset về trang đầu tiên khi xóa bộ lọc
}

function goToPage(page) {
  currentPage.value = page
}

function prevPage() {
  if (currentPage.value > 1) currentPage.value--
}

function nextPage() {
  if (currentPage.value < totalPages.value) currentPage.value++
}

function editFlight(flight) {
  // Implement edit functionality
  console.log('Edit flight:', flight)
}

async function deleteFlight(flight) {
  if (!confirm('Bạn có chắc chắn muốn xóa chuyến bay này?')) return;
  loadingDelete.value[flight.id] = true;
  try {
    await deleteAdminFlight(flight.id);
    flights.value = flights.value.filter(f => f.id !== flight.id);
    window.$toast('Xóa chuyến bay thành công!', 'success');
  } catch (e) {
    window.$toast('Xóa chuyến bay thất bại!', 'error');
  } finally {
    loadingDelete.value[flight.id] = false;
  }
}

const router = useRouter()
const selectMenuItem = inject('selectMenuItem')
function goToDetail(id) {
  if (selectMenuItem) {
    selectMenuItem({ component: 'DetailFlightAdmin' }, id)
  } else {
    // fallback: vẫn dùng router nếu không có inject
    router.push({ name: 'DetailFlightAdmin', params: { id } })
  }
}
</script>