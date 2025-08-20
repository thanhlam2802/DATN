<template>
  <div class="space-y-6">
    <!-- Header Section -->
    <div class="sm:flex sm:items-center sm:justify-between">
      <div>
        <h3 class="text-lg font-medium leading-6 text-gray-900">Thống kê & Báo cáo</h3>
        <p class="mt-1 text-sm text-gray-500">Theo dõi hiệu suất kinh doanh và phân tích dữ liệu</p>
      </div>
      <div class="mt-4 sm:mt-0 flex space-x-3">
        <div class="relative">
          <select v-model="selectedPeriod" class="appearance-none bg-white border border-gray-300 rounded-lg shadow-sm pl-4 pr-10 py-2.5 text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
            <option value="7days">7 ngày qua</option>
            <option value="30days">30 ngày qua</option>
            <option value="90days">3 tháng qua</option>
            <option value="12months">12 tháng qua</option>
          </select>
          <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
            <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
            </svg>
          </div>
        </div>
        <button @click="exportReport" class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50">
          <svg class="-ml-1 mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          Xuất báo cáo
        </button>
      </div>
    </div>

  

    <!-- Loading State -->
    <div v-if="loading" class="flex items-center justify-center py-12">
      <div class="text-center">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mx-auto"></div>
        <p class="mt-4 text-gray-600">Đang tải dữ liệu thống kê...</p>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-lg p-6">
      <div class="flex items-center">
        <div class="flex-shrink-0">
          <svg class="h-5 w-5 text-red-400" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
          </svg>
        </div>
        <div class="ml-3">
          <h3 class="text-sm font-medium text-red-800">Lỗi tải dữ liệu</h3>
          <p class="mt-1 text-sm text-red-700">{{ error }}</p>
        </div>
        <div class="ml-auto pl-3">
          <button @click="fetchStatisticsData" class="text-sm font-medium text-red-800 hover:text-red-900">
            Thử lại
          </button>
        </div>
      </div>
    </div>

    <!-- Overview Stats -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-green-500 rounded-md flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 flex-1 min-w-0">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Tổng doanh thu</dt>
                <dd class="flex items-baseline flex-wrap">
                  <div class="text-2xl font-semibold text-gray-900 mr-2">{{ formatCurrency(kpiData.totalRevenue) }}</div>
                  <div v-if="loading" class="text-sm text-gray-500">Đang tải...</div>
                </dd>
              </dl>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-blue-500 rounded-md flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 flex-1 min-w-0">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Tổng đặt chỗ</dt>
                <dd class="flex items-baseline flex-wrap">
                  <div class="text-2xl font-semibold text-gray-900 mr-2">{{ kpiData.totalBookings.toLocaleString() }}</div>
                  <div v-if="loading" class="text-sm text-gray-500">Đang tải...</div>
                </dd>
              </dl>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-yellow-500 rounded-md flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h5v-4l-2-2h-3v6z"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 flex-1 min-w-0">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Tổng chuyến xe</dt>
                <dd class="flex items-baseline flex-wrap">
                  <div class="text-2xl font-semibold text-gray-900 mr-2">{{ kpiData.totalTrips }}</div>
                  <div v-if="loading" class="text-sm text-gray-500">Đang tải...</div>
                </dd>
              </dl>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-purple-500 rounded-md flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 flex-1 min-w-0">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Tỷ lệ lấp đầy</dt>
                <dd class="flex items-baseline flex-wrap">
                  <div class="text-2xl font-semibold text-gray-900 mr-2">{{ kpiData.occupancyRate }}%</div>
                  <div v-if="loading" class="text-sm text-gray-500">Đang tải...</div>
                </dd>
              </dl>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Charts Section -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Revenue Chart -->
      <div class="bg-white shadow rounded-lg">
        <div class="px-6 py-4 border-b border-gray-200">
          <h4 class="text-lg font-medium text-gray-900">Doanh thu theo thời gian</h4>
        </div>
        <div class="p-6">
          <div class="h-80 flex items-center justify-center bg-gray-50 rounded-lg">
            <div class="text-center">
              <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
              </svg>
              <h3 class="mt-2 text-sm font-medium text-gray-900">Biểu đồ doanh thu</h3>
              <p class="mt-1 text-sm text-gray-500">Tích hợp Chart.js hoặc thư viện biểu đồ khác</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Route Performance -->
      <div class="bg-white shadow rounded-lg">
        <div class="px-6 py-4 border-b border-gray-200">
          <h4 class="text-lg font-medium text-gray-900">Hiệu suất tuyến đường</h4>
        </div>
        <div class="p-6">
          <div class="space-y-4">
            <div v-for="route in topRoutes" :key="route.id" class="flex items-center justify-between">
              <div class="flex items-center">
                <div class="w-4 h-4 rounded bg-blue-500 mr-3"></div>
                <span class="text-sm font-medium text-gray-900">{{ route.origin }} → {{ route.destination }}</span>
              </div>
              <div class="flex items-center space-x-4">
                <span class="text-sm text-gray-500">{{ route.revenue.toLocaleString() }}đ</span>
                <div class="w-20 bg-gray-200 rounded-full h-2">
                  <div class="bg-blue-600 h-2 rounded-full" :style="`width: ${route.percentage}%`"></div>
                </div>
                <span class="text-sm text-gray-500 w-10">{{ route.percentage }}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Customer Analytics & Trip Stats -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Customer Demographics -->
      <div class="bg-white shadow rounded-lg">
        <div class="px-6 py-4 border-b border-gray-200">
          <h4 class="text-lg font-medium text-gray-900">Phân tích khách hàng</h4>
        </div>
        <div class="p-6">
          <div class="space-y-6">
            <div>
              <div class="flex items-center justify-between mb-2">
                <span class="text-sm font-medium text-gray-700">Khách hàng mới</span>
                <span class="text-sm text-gray-500">65%</span>
              </div>
              <div class="w-full bg-gray-200 rounded-full h-2">
                <div class="bg-green-600 h-2 rounded-full" style="width: 65%"></div>
              </div>
            </div>
            
            <div>
              <div class="flex items-center justify-between mb-2">
                <span class="text-sm font-medium text-gray-700">Khách hàng quay lại</span>
                <span class="text-sm text-gray-500">35%</span>
              </div>
              <div class="w-full bg-gray-200 rounded-full h-2">
                <div class="bg-blue-600 h-2 rounded-full" style="width: 35%"></div>
              </div>
            </div>

            <div class="pt-4 border-t border-gray-200">
              <h5 class="text-sm font-medium text-gray-900 mb-3">Độ tuổi khách hàng</h5>
              <div class="space-y-3">
                <div class="flex justify-between items-center">
                  <span class="text-sm text-gray-600">18-25</span>
                  <span class="text-sm font-medium">28%</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-sm text-gray-600">26-35</span>
                  <span class="text-sm font-medium">42%</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-sm text-gray-600">36-50</span>
                  <span class="text-sm font-medium">22%</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-sm text-gray-600">50+</span>
                  <span class="text-sm font-medium">8%</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Recent Bookings -->
      <div class="bg-white shadow rounded-lg">
        <div class="px-6 py-4 border-b border-gray-200">
          <h4 class="text-lg font-medium text-gray-900">Đặt vé gần đây</h4>
        </div>
        <div class="p-6">
          <div class="space-y-4">
            <div v-for="booking in recentBookings" :key="booking.id" class="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
              <div class="flex items-center space-x-3">
                <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
                  <span class="text-xs font-medium text-blue-600">{{ booking.customer.charAt(0) }}</span>
                </div>
                <div>
                  <p class="text-sm font-medium text-gray-900">{{ booking.customer }}</p>
                  <p class="text-xs text-gray-500">{{ booking.route }}</p>
                </div>
              </div>
              <div class="text-right">
                <p class="text-sm font-medium text-gray-900">{{ formatCurrency(booking.amount) }}</p>
                <p class="text-xs text-gray-500">{{ booking.date }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Monthly Report Table -->
    <div class="bg-white shadow overflow-hidden sm:rounded-md">
      <div class="px-6 py-4 border-b border-gray-200">
        <h4 class="text-lg font-medium text-gray-900">Báo cáo theo tháng</h4>
      </div>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tháng</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Doanh thu</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Số chuyến</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Khách hàng</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tỷ lệ lấp đầy</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tăng trưởng</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="report in monthlyReports" :key="report.month">
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ report.month }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatCurrency(report.revenue) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ report.trips }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ report.customers.toLocaleString() }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ report.occupancy }}%</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <span :class="report.growth >= 0 ? 'text-green-600' : 'text-red-600'" class="font-medium">
                  {{ report.growth >= 0 ? '+' : '' }}{{ report.growth }}%
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, reactive, watch } from 'vue';
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale, ArcElement, PointElement, LineElement, Filler } from 'chart.js';
import { Bar, Doughnut, Line } from 'vue-chartjs';
import { BusStatisticsAPI, type OverviewStatistics } from '@/api/busApi/statistics';
import { toast } from '@/utils/notifications';



ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale, ArcElement, PointElement, LineElement, Filler);

// Development check
const isDevelopment = computed(() => {
  return import.meta.env.DEV || import.meta.env.MODE === 'development'
})

// State
const loading = ref(false);
const error = ref<string | null>(null);
const selectedPeriod = ref('7days');

// Statistics data
const statisticsData = ref<OverviewStatistics | null>(null);
const kpiData = ref({
  totalRevenue: 0,
  totalBookings: 0,
  totalTrips: 0,
  totalBuses: 0,
  totalCustomers: 0,
  occupancyRate: 0,
});

const revenueData = ref({
  labels: [] as string[],
  datasets: [{
    label: 'Doanh thu',
    data: [] as number[],
    borderColor: '#3b82f6',
    backgroundColor: 'rgba(59, 130, 246, 0.1)',
    fill: true,
    tension: 0.4
  }]
});

const occupancyData = ref({
  labels: [] as string[],
  datasets: [{
    label: 'Tỷ lệ lấp đầy',
    data: [] as number[],
    backgroundColor: ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6'],
  }]
});

const topRoutes = ref<any[]>([]);

// Mock data for demo
const recentBookings = ref([
  { id: 1, customer: 'Nguyễn Văn A', route: 'Hà Nội → Hồ Chí Minh', amount: 500000, date: '2024-01-15' },
  { id: 2, customer: 'Trần Thị B', route: 'Đà Nẵng → Hà Nội', amount: 400000, date: '2024-01-14' },
  { id: 3, customer: 'Lê Văn C', route: 'Hồ Chí Minh → Đà Nẵng', amount: 350000, date: '2024-01-13' },
]);

const monthlyReports = ref([
  { month: 'Tháng 1/2024', revenue: 15000000, trips: 45, customers: 1200, occupancy: 85, growth: 12 },
  { month: 'Tháng 12/2023', revenue: 13500000, trips: 42, customers: 1100, occupancy: 78, growth: 8 },
  { month: 'Tháng 11/2023', revenue: 12500000, trips: 38, customers: 1050, occupancy: 75, growth: -3 },
]);

// Chart options
const lineChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    y: {
      beginAtZero: true
    }
  }
};

const doughnutChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
};

// Fetching data
const fetchStatisticsData = async () => {
  try {
    loading.value = true;
    error.value = null;

    // Lấy ownerId từ token
    const token = localStorage.getItem('t_');
    if (!token) {
      throw new Error("Không thể xác thực người dùng. Vui lòng đăng nhập lại.");
    }

    // Parse token để lấy userId (ownerId)
    const payload = JSON.parse(atob(token.split('.')[1]));
    const ownerId = payload.userId;
    
    if (!ownerId) {
      throw new Error("Không thể xác định ownerId từ token.");
    }

    // Lấy thống kê tổng quan
    const overviewStats = await BusStatisticsAPI.getOverviewStatistics(ownerId);
    statisticsData.value = overviewStats;

    // Cập nhật KPI data
    kpiData.value = {
      totalRevenue: overviewStats.totalRevenue || 0,
      totalBookings: overviewStats.totalBookings || 0,
      totalTrips: overviewStats.totalTrips || 0,
      totalBuses: overviewStats.totalBuses || 0,
      totalCustomers: overviewStats.totalCustomers || 0,
      occupancyRate: calculateAverageOccupancy(overviewStats.occupancyRates),
    };

  } catch (err: any) {
    error.value = err.message || "Đã có lỗi xảy ra khi tải dữ liệu thống kê.";
    toast.error(error.value || "Đã có lỗi xảy ra khi tải dữ liệu thống kê.");
  } finally {
    loading.value = false;
  }
};

// Helper function để tính tỷ lệ lấp đầy trung bình
const calculateAverageOccupancy = (occupancyRates: Record<string, number>): number => {
  if (!occupancyRates || Object.keys(occupancyRates).length === 0) {
    return 0;
  }
  
  const values = Object.values(occupancyRates);
  const average = values.reduce((sum, value) => sum + value, 0) / values.length;
  return Math.round(average * 100) / 100; // Làm tròn 2 chữ số thập phân
};

const exportReport = () => {
  toast.info('Chức năng xuất báo cáo đang được phát triển.');
};

const formatCurrency = (value: number): string => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value || 0);
};

// Watch for period changes
watch(selectedPeriod, fetchStatisticsData);

onMounted(fetchStatisticsData);
</script> 
