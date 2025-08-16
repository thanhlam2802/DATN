<template>
  <div class="space-y-6">
    <!-- Month/Year Selector -->
    <div class="bg-white rounded-xl shadow-lg p-6">
      <div class="flex items-center justify-between mb-6">
        <h2 class="text-2xl font-bold text-gray-800">Thống kê chuyến bay theo tháng</h2>
        <div class="flex items-center space-x-4">
          <div class="flex items-center space-x-2">
            <select
              v-model="selectedYear"
              class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option v-for="year in availableYears" :key="year" :value="year">
                {{ year }}
              </option>
            </select>
            <select
              v-model="selectedMonth"
              class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option v-for="month in 12" :key="month" :value="month">
                {{ month }}
              </option>
            </select>
          </div>
          <button
            @click="fetchAllData"
            :disabled="loading"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <i class="fas fa-sync-alt mr-2" :class="{ 'fa-spin': loading }"></i>
            {{ loading ? 'Đang tải...' : 'Cập nhật' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Error Message -->
    <div v-if="error" class="bg-red-50 border border-red-200 rounded-lg p-4">
      <div class="flex">
        <div class="flex-shrink-0">
          <i class="fas fa-exclamation-triangle text-red-400"></i>
        </div>
        <div class="ml-3">
          <p class="text-sm text-red-800">{{ error }}</p>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
    </div>

    <!-- Statistics Cards -->
    <div v-if="!loading && monthlyStats" class="grid grid-cols-1 md:grid-cols-4 gap-6">
      <div class="bg-white rounded-xl shadow-lg p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-600">Tổng chuyến bay</p>
            <p class="text-2xl font-bold text-gray-900">{{ monthlyStats.totalFlightsCurrentMonth }}</p>
          </div>
          <div class="p-3 bg-blue-100 rounded-full">
            <i class="fas fa-plane text-blue-600 text-xl"></i>
          </div>
        </div>
        <div class="mt-4">
          <span class="text-sm" :class="getChangeColor(monthlyStats.flightsChangePercentage)">
            <i :class="getChangeIcon(monthlyStats.flightsChangePercentage)"></i>
            {{ formatPercentage(monthlyStats.flightsChangePercentage) }}%
          </span>
          <span class="text-sm text-gray-500 ml-2">so với tháng trước</span>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-lg p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-600">Tổng đặt chỗ</p>
            <p class="text-2xl font-bold text-gray-900">{{ monthlyStats.totalBookingsCurrentMonth }}</p>
          </div>
          <div class="p-3 bg-green-100 rounded-full">
            <i class="fas fa-ticket-alt text-green-600 text-xl"></i>
          </div>
        </div>
        <div class="mt-4">
          <span class="text-sm" :class="getChangeColor(monthlyStats.bookingsChangePercentage)">
            <i :class="getChangeIcon(monthlyStats.bookingsChangePercentage)"></i>
            {{ formatPercentage(monthlyStats.bookingsChangePercentage) }}%
          </span>
          <span class="text-sm text-gray-500 ml-2">so với tháng trước</span>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-lg p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-600">Doanh thu</p>
            <p class="text-2xl font-bold text-gray-900">{{ formatCurrency(monthlyStats.totalRevenueCurrentMonth) }}</p>
          </div>
          <div class="p-3 bg-purple-100 rounded-full">
            <i class="fas fa-money-bill-wave text-purple-600 text-xl"></i>
          </div>
        </div>
        <div class="mt-4">
          <span class="text-sm" :class="getChangeColor(monthlyStats.revenueChangePercentage)">
            <i :class="getChangeIcon(monthlyStats.revenueChangePercentage)"></i>
            {{ formatPercentage(monthlyStats.revenueChangePercentage) }}%
          </span>
          <span class="text-sm text-gray-500 ml-2">so với tháng trước</span>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-lg p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-600">Tỷ lệ lấp đầy</p>
            <p class="text-2xl font-bold text-gray-900">{{ formatPercentage(monthlyStats.averageOccupancyRateCurrentMonth) }}%</p>
          </div>
          <div class="p-3 bg-yellow-100 rounded-full">
            <i class="fas fa-chart-line text-yellow-600 text-xl"></i>
          </div>
        </div>
        <div class="mt-4">
          <span class="text-sm" :class="getChangeColor(monthlyStats.occupancyChangePercentage)">
            <i :class="getChangeIcon(monthlyStats.occupancyChangePercentage)"></i>
            {{ formatPercentage(monthlyStats.occupancyChangePercentage) }}%
          </span>
          <span class="text-sm text-gray-500 ml-2">so với tháng trước</span>
        </div>
      </div>
    </div>

    <!-- Comparison Table -->
    <div v-if="!loading && monthlyStats" class="bg-white rounded-xl shadow-lg p-6">
      <h3 class="text-lg font-semibold text-gray-800 mb-4">So sánh với tháng trước</h3>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Chỉ số
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Tháng {{ monthlyStats.month }}/{{ monthlyStats.year }}
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Tháng {{ getPreviousMonthText() }}
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Thay đổi
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                Tổng chuyến bay
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ monthlyStats.totalFlightsCurrentMonth }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ monthlyStats.totalFlightsPreviousMonth }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm" :class="getChangeColor(monthlyStats.flightsChangePercentage)">
                {{ formatPercentage(monthlyStats.flightsChangePercentage) }}%
              </td>
            </tr>
            <tr>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                Tổng đặt chỗ
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ monthlyStats.totalBookingsCurrentMonth }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ monthlyStats.totalBookingsPreviousMonth }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm" :class="getChangeColor(monthlyStats.bookingsChangePercentage)">
                {{ formatPercentage(monthlyStats.bookingsChangePercentage) }}%
              </td>
            </tr>
            <tr>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                Doanh thu
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ formatCurrency(monthlyStats.totalRevenueCurrentMonth) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ formatCurrency(monthlyStats.totalRevenuePreviousMonth) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm" :class="getChangeColor(monthlyStats.revenueChangePercentage)">
                {{ formatPercentage(monthlyStats.revenueChangePercentage) }}%
              </td>
            </tr>
            <tr>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                Tỷ lệ lấp đầy
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ formatPercentage(monthlyStats.averageOccupancyRateCurrentMonth) }}%
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ formatPercentage(monthlyStats.averageOccupancyRatePreviousMonth) }}%
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm" :class="getChangeColor(monthlyStats.occupancyChangePercentage)">
                {{ formatPercentage(monthlyStats.occupancyChangePercentage) }}%
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Charts -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <!-- Bookings by Destination -->
      <div class="bg-white rounded-xl shadow-lg p-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">Đặt chỗ theo điểm đến</h3>
        <div class="h-80">
          <Bar
            v-if="bookingsByDestinationData"
            :data="bookingsByDestinationData"
            :options="barChartOptions"
          />
          <div v-else class="h-full flex items-center justify-center text-gray-500">
            <p>Không có dữ liệu</p>
          </div>
        </div>
      </div>

      <!-- Revenue by Seat Class -->
      <div class="bg-white rounded-xl shadow-lg p-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">Doanh thu theo nhóm ghế</h3>
        <div class="h-80">
          <Pie
            v-if="revenueBySeatClassData"
            :data="revenueBySeatClassData"
            :options="pieChartOptions"
          />
          <div v-else class="h-full flex items-center justify-center text-gray-500">
            <p>Không có dữ liệu</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Top Routes Table -->
    
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { Bar, Pie } from 'vue-chartjs';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement
} from 'chart.js';
import { getMonthlyFlightStatistics, getBookingsByDestination, getRevenueBySeatClass } from '@/api/flightApi';

// Register Chart.js components
ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement
);

// State
const monthlyStats = ref(null);
const bookingsByDestinationData = ref(null);
const revenueBySeatClassData = ref(null);
const loading = ref(false);
const error = ref('');
const selectedYear = ref(new Date().getFullYear());
const selectedMonth = ref(new Date().getMonth() + 1);

// Available years (current year and 2 years back)
const availableYears = computed(() => {
  const currentYear = new Date().getFullYear();
  return [currentYear - 2, currentYear - 1, currentYear, currentYear + 1];
});


// Chart options
const barChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false
    },
    tooltip: {
      callbacks: {
        label: function(context) {
          return `Đặt chỗ: ${context.parsed.y}`;
        }
      }
    }
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        stepSize: 1
      }
    }
  }
};

const pieChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'bottom'
    },
    tooltip: {
      callbacks: {
        label: function(context) {
          const label = context.label || '';
          const value = context.parsed;
          const total = context.dataset.data.reduce((a, b) => a + b, 0);
          const percentage = ((value / total) * 100).toFixed(1);
          return `${label}: ${formatCurrency(value)} (${percentage}%)`;
        }
      }
    }
  }
};

// Methods
const fetchAllData = async () => {
  loading.value = true;
  error.value = '';
  
  try {
    // Fetch all data in parallel
    const [monthlyResponse, bookingsResponse, revenueResponse] = await Promise.all([
      getMonthlyFlightStatistics(selectedYear.value, selectedMonth.value),
      getBookingsByDestination(selectedYear.value, selectedMonth.value),
      getRevenueBySeatClass(selectedYear.value, selectedMonth.value)
    ]);

    monthlyStats.value = monthlyResponse.data;
    
    // Process bookings by destination data
    if (bookingsResponse.data && bookingsResponse.data.length > 0) {
      bookingsByDestinationData.value = {
        labels: bookingsResponse.data.map(item => item.destination),
        datasets: [{
          label: 'Số lượng đặt chỗ',
          data: bookingsResponse.data.map(item => item.bookingCount),
          backgroundColor: [
            '#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6',
            '#06B6D4', '#84CC16', '#F97316', '#EC4899', '#6366F1'
          ],
          borderWidth: 1
        }]
      };
    } else {
      bookingsByDestinationData.value = null;
    }

    // Process revenue by seat class data
    if (revenueResponse.data && revenueResponse.data.length > 0) {
      revenueBySeatClassData.value = {
        labels: revenueResponse.data.map(item => item.seatClass),
        datasets: [{
          data: revenueResponse.data.map(item => item.revenue),
          backgroundColor: [
            '#3B82F6', // Economy
            '#10B981', // Business
            '#F59E0B', // First Class
            '#EF4444'  // Premium Economy
          ],
          borderWidth: 2,
          borderColor: '#ffffff'
        }]
      };
    } else {
      revenueBySeatClassData.value = null;
    }

  } catch (err) {
    console.error('Error fetching data:', err);
    error.value = 'Không thể tải dữ liệu thống kê. Vui lòng thử lại sau.';
    monthlyStats.value = null;
    bookingsByDestinationData.value = null;
    revenueBySeatClassData.value = null;
  } finally {
    loading.value = false;
  }
};

const formatCurrency = (value) => {
  if (!value && value !== 0) return '0 ₫';
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value);
};

const formatPercentage = (value) => {
  if (!value && value !== 0) return '0';
  return value.toFixed(1);
};

const getChangeColor = (percentage) => {
  if (!percentage && percentage !== 0) return 'text-gray-500';
  return percentage > 0 ? 'text-green-600' : percentage < 0 ? 'text-red-600' : 'text-gray-500';
};

const getChangeIcon = (percentage) => {
  if (!percentage && percentage !== 0) return 'fas fa-minus';
  return percentage > 0 ? 'fas fa-arrow-up' : percentage < 0 ? 'fas fa-arrow-down' : 'fas fa-minus';
};

const getPreviousMonthText = () => {
  if (!monthlyStats.value) return '';
  
  const prevMonth = monthlyStats.value.month - 1;
  const prevYear = monthlyStats.value.year;
  
  if (prevMonth === 0) {
    return `12/${prevYear - 1}`;
  }
  
  return `${prevMonth}/${prevYear}`;
};

// Initialize component
onMounted(() => {
  fetchAllData();
});
</script> 