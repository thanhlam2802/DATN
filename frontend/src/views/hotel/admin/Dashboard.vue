<template>
    <main class="flex-grow bg-white rounded-xl shadow-xl overflow-hidden">
        <div class="p-6">
            <div class="mb-4 flex justify-between items-center">
                <h1 class="text-xl font-bold text-slate-800">Thống Kê</h1>
                <div class="w-40 mr-86">
                    <CustomSelect
                        v-model="selectedTimePeriod"
                        :options="timeOptions"
                        placeholder="Chọn thời gian"
                        @update:modelValue="onTimePeriodChange"
                    />
                </div>
            </div>

            <div class="flex gap-6">
                <div class="flex-1 max-w-4xl">
                    <div class="grid grid-cols-2 lg:grid-cols-4 gap-3 mb-6">
                        <div
                            class="bg-white rounded-lg shadow-sm p-3 border border-slate-200 transition-all duration-300 hover:shadow-md hover:-translate-y-1">
                            <div class="flex items-start justify-between mb-2">
                                <div class="text-slate-600 font-medium text-xs">Tổng Đặt Phòng</div>
                                <div class="bg-blue-50 p-1.5 rounded-full">
                                    <i class="fas fa-calendar-check text-blue-500 text-sm"></i>
                                </div>
                            </div>
                            <div class="text-lg font-bold text-slate-900">{{ loading ? '--' : formatNumber(stats.totalBookings) }}</div>
                            <div :class="[
                                stats.bookingGrowth > 0 ? 'text-green-600' : 
                                stats.bookingGrowth < 0 ? 'text-red-600' : 'text-gray-500'
                            ]" class="text-xs mt-1 flex items-center">
                                <i :class="[
                                    stats.bookingGrowth > 0 ? 'fas fa-arrow-up' : 
                                    stats.bookingGrowth < 0 ? 'fas fa-arrow-down' : 'fas fa-minus'
                                ]" class="mr-1 text-xs"></i>
                                <span>
                                    {{ stats.bookingGrowth > 0 ? '+' : '' }}{{ stats.bookingGrowth }}% so với {{ comparisonLabel }}
                                </span>
                            </div>
                        </div>
                        <div
                            class="bg-white rounded-lg shadow-sm p-3 border border-slate-200 transition-all duration-300 hover:shadow-md hover:-translate-y-1">
                            <div class="flex items-start justify-between mb-2">
                                <div class="text-slate-600 font-medium text-xs">Doanh Thu</div>
                                <div class="bg-green-50 p-1.5 rounded-full">
                                    <i class="fas fa-dollar-sign text-green-500 text-sm"></i>
                                </div>
                            </div>
                            <div class="text-lg font-bold text-slate-900">{{ loading ? '--' : formatCurrency(stats.totalRevenue) }}</div>
                            <div :class="[
                                stats.revenueGrowth > 0 ? 'text-green-600' : 
                                stats.revenueGrowth < 0 ? 'text-red-600' : 'text-gray-500'
                            ]" class="text-xs mt-1 flex items-center">
                                <i :class="[
                                    stats.revenueGrowth > 0 ? 'fas fa-arrow-up' : 
                                    stats.revenueGrowth < 0 ? 'fas fa-arrow-down' : 'fas fa-minus'
                                ]" class="mr-1 text-xs"></i>
                                <span>
                                    {{ stats.revenueGrowth > 0 ? '+' : '' }}{{ stats.revenueGrowth }}% so với {{ comparisonLabel }}
                                </span>
                            </div>
                        </div>
                        <div
                            class="bg-white rounded-lg shadow-sm p-3 border border-slate-200 transition-all duration-300 hover:shadow-md hover:-translate-y-1">
                            <div class="flex items-start justify-between mb-2">
                                <div class="text-slate-600 font-medium text-xs">Tổng Khách Hàng</div>
                                <div class="bg-purple-50 p-1.5 rounded-full">
                                    <i class="fas fa-users text-purple-500 text-sm"></i>
                                </div>
                            </div>
                            <div class="text-lg font-bold text-slate-900">{{ loading ? '--' : formatNumber(stats.totalCustomers) }}</div>
                            <div :class="[
                                stats.customerGrowth > 0 ? 'text-green-600' : 
                                stats.customerGrowth < 0 ? 'text-red-600' : 'text-gray-500'
                            ]" class="text-xs mt-1 flex items-center">
                                <i :class="[
                                    stats.customerGrowth > 0 ? 'fas fa-arrow-up' : 
                                    stats.customerGrowth < 0 ? 'fas fa-arrow-down' : 'fas fa-minus'
                                ]" class="mr-1 text-xs"></i>
                                <span>
                                    {{ stats.customerGrowth > 0 ? '+' : '' }}{{ stats.customerGrowth }}% so với {{ comparisonLabel }}
                                </span>
                            </div>
                        </div>
                        <div
                            class="bg-white rounded-lg shadow-sm p-3 border border-slate-200 transition-all duration-300 hover:shadow-md hover:-translate-y-1">
                            <div class="flex items-start justify-between mb-2">
                                <div class="text-slate-600 font-medium text-xs">Đánh Giá</div>
                                <div class="bg-yellow-50 p-1.5 rounded-full">
                                    <i class="fas fa-star text-yellow-500 text-sm"></i>
                                </div>
                            </div>
                            <div class="text-lg font-bold text-slate-900">{{ loading ? '--' : formatNumber(stats.totalReviews) }}</div>
                            <div :class="[
                                stats.reviewGrowth > 0 ? 'text-green-600' : 
                                stats.reviewGrowth < 0 ? 'text-red-600' : 'text-gray-500'
                            ]" class="text-xs mt-1 flex items-center">
                                <i :class="[
                                    stats.reviewGrowth > 0 ? 'fas fa-arrow-up' : 
                                    stats.reviewGrowth < 0 ? 'fas fa-arrow-down' : 'fas fa-minus'
                                ]" class="mr-1 text-xs"></i>
                                <span>
                                    {{ stats.reviewGrowth > 0 ? '+' : '' }}{{ stats.reviewGrowth }}% so với {{ comparisonLabel }}
                                </span>
                            </div>
                        </div>
                    </div>

                    <div class="bg-white rounded-lg shadow-sm p-3 mb-4">
                      <div class="flex justify-between items-center mb-2">
                        <div class="font-medium text-sm">Biểu đồ doanh thu</div>
                        <div class="w-36">
                          <CustomSelect
                            v-model="selectedChartTimePeriod"
                            :options="timeOptions"
                            placeholder="Chọn thời gian"
                            @update:modelValue="onChartTimePeriodChange"
                          />
                        </div>
                      </div>
                      <template v-if="revenueChartData">
                        <RevenueChart :data="revenueChartData" :height="400" />
                      </template>
                      <template v-else>
                        <div class="text-center text-gray-500 py-12 text-sm">Không có dữ liệu</div>
                      </template>
                    </div>

                    <div class="bg-white rounded-lg shadow-sm p-3">
                      <div class="flex justify-between items-center mb-2">
                        <div class="font-medium text-sm">Doanh thu theo khách sạn</div>
                        <div class="w-36">
                          <CustomSelect
                            v-model="selectedPieChartTimePeriod"
                            :options="timeOptions"
                            placeholder="Chọn thời gian"
                            @update:modelValue="onPieChartTimePeriodChange"
                          />
                        </div>
                      </div>
                      <template v-if="revenuePieChartData">
                        <RevenuePieChart :data="revenuePieChartData" :height="350" />
                      </template>
                      <template v-else>
                        <div class="text-center text-gray-500 py-12 text-sm">Không có dữ liệu</div>
                      </template>
                    </div>
                </div>

                <div class="w-80 bg-white rounded-lg shadow-sm p-4 border border-slate-200">
                    <div class="flex items-center justify-between mb-4">
                        <h3 class="font-semibold text-slate-800 text-sm">Thông Báo</h3>
                        <button class="text-blue-500 text-xs hover:text-blue-700">Xem tất cả</button>
                    </div>
                    
                    <div class="space-y-3">
                        <div class="flex items-start space-x-3 p-3 bg-blue-50 rounded-lg">
                            <div class="w-2 h-2 bg-blue-500 rounded-full mt-2 flex-shrink-0"></div>
                            <div class="flex-1">
                                <p class="text-xs font-medium text-slate-800">Đặt phòng mới</p>
                                <p class="text-xs text-slate-600 mt-1">Khách hàng Nguyễn Văn A vừa đặt phòng tại Grand Hotel</p>
                                <p class="text-xs text-slate-400 mt-1">2 phút trước</p>
                            </div>
                        </div>
                        
                        <div class="flex items-start space-x-3 p-3 bg-green-50 rounded-lg">
                            <div class="w-2 h-2 bg-green-500 rounded-full mt-2 flex-shrink-0"></div>
                            <div class="flex-1">
                                <p class="text-xs font-medium text-slate-800">Thanh toán thành công</p>
                                <p class="text-xs text-slate-600 mt-1">Đơn hàng #12345 đã được thanh toán</p>
                                <p class="text-xs text-slate-400 mt-1">5 phút trước</p>
                            </div>
                        </div>
                        
                        <div class="flex items-start space-x-3 p-3 bg-yellow-50 rounded-lg">
                            <div class="w-2 h-2 bg-yellow-500 rounded-full mt-2 flex-shrink-0"></div>
                            <div class="flex-1">
                                <p class="text-xs font-medium text-slate-800">Đánh giá mới</p>
                                <p class="text-xs text-slate-600 mt-1">Khách hàng đã đánh giá 5 sao cho Seaside Resort</p>
                                <p class="text-xs text-slate-400 mt-1">10 phút trước</p>
                            </div>
                        </div>
                        
                        <div class="flex items-start space-x-3 p-3 bg-red-50 rounded-lg">
                            <div class="w-2 h-2 bg-red-500 rounded-full mt-2 flex-shrink-0"></div>
                            <div class="flex-1">
                                <p class="text-xs font-medium text-slate-800">Hủy đặt phòng</p>
                                <p class="text-xs text-slate-600 mt-1">Đơn hàng #12340 đã bị hủy</p>
                                <p class="text-xs text-slate-400 mt-1">15 phút trước</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import hotelApi from '@/api/hotelApi';
import CustomSelect from '@/components/CustomSelect.vue';
import RevenueChart from '@/components/Hotel/HotelAdmin/RevenueChart.vue';
import RevenuePieChart from '@/components/Hotel/HotelAdmin/RevenuePieChart.vue';
import { useAdminBreadcrumbStore } from '@/store/useAdminBreadcrumbStore';

const stats = ref({
  totalBookings: 0,
  totalRevenue: 0,
  totalCustomers: 0,
  totalReviews: 0,
  bookingGrowth: 0,
  revenueGrowth: 0,
  customerGrowth: 0,
  reviewGrowth: 0
});
const loading = ref(true);

const selectedTimePeriod = ref('this_month');
const selectedChartTimePeriod = ref('this_month');
const selectedPieChartTimePeriod = ref('this_month');

const timeOptions = [
  { value: 'today', label: 'Hôm nay' },
  { value: 'yesterday', label: 'Hôm qua' },
  { value: 'last_7_days', label: '7 ngày qua' },
  { value: 'this_month', label: 'Tháng này' },
  { value: 'last_month', label: 'Tháng trước' }
];

const comparisonLabel = computed(() => {
  switch (selectedTimePeriod.value) {
    case 'today':
      return 'hôm qua';
    case 'yesterday':
      return '2 ngày trước';
    case 'last_7_days':
      return '7 ngày trước đó';
    case 'this_month':
      return 'tháng trước';
    case 'last_month':
      return '2 tháng trước';
    default:
      return 'tháng trước';
  }
});

const revenueChartData = ref({ labels: [], data: [] });
const revenuePieChartData = ref({ labels: [], data: [] });

const onTimePeriodChange = (value) => {
  selectedTimePeriod.value = value;
  fetchDashboardStatistics();
};

const onChartTimePeriodChange = (value) => {
  selectedChartTimePeriod.value = value;
  fetchRevenueChart();
};

const onPieChartTimePeriodChange = (value) => {
  selectedPieChartTimePeriod.value = value;
  fetchRevenuePieChart();
};

onMounted(() => {
  const breadcrumbStore = useAdminBreadcrumbStore();
  breadcrumbStore.setBreadcrumb([
    { label: 'Thống kê', active: true }
  ]);
  fetchDashboardStatistics();
  fetchRevenueChart();
  fetchRevenuePieChart();
});

const fetchDashboardStatistics = async () => {
  loading.value = true;
  try {
    console.log('Calling dashboard statistics API with period:', selectedTimePeriod.value);
    const res = await hotelApi.getDashboardStatistics(selectedTimePeriod.value);
    console.log('API Response:', res);
    console.log('Response data:', res.data);
    if (res.data && res.data.data) {
      console.log('Setting stats:', res.data.data);
      stats.value = res.data.data;
    } else {
      console.log('No data found in response');
    }
  } catch (error) {
    console.error('Error fetching dashboard statistics:', error);
  } finally {
    loading.value = false;
  }
};

const fetchRevenueChart = async () => {
  try {
    console.log('=== REVENUE CHART DEBUG ===');
    console.log('Calling revenue chart API with period:', selectedChartTimePeriod.value);
    const res = await hotelApi.getHotelRevenueChart(selectedChartTimePeriod.value);
    console.log('Revenue chart API Response:', res);
    console.log('Revenue chart data:', res.data);
    console.log('Revenue chart data.data:', res.data?.data);
    console.log('Labels:', res.data?.data?.labels);
    console.log('Datasets:', res.data?.data?.datasets);
    console.log('Labels length:', res.data?.data?.labels?.length);
    console.log('Datasets length:', res.data?.data?.datasets?.length);
    
    if (res.data && res.data.data) {
      console.log('Setting revenue chart data:', res.data.data);
      revenueChartData.value = res.data.data;
      console.log('Updated revenueChartData:', revenueChartData.value);
      console.log('revenueChartData.value exists:', !!revenueChartData.value);
    } else {
      console.log('No revenue chart data found in response');
      revenueChartData.value = { labels: [], datasets: [] };
    }
  } catch (e) {
    console.error('Error fetching revenue chart:', e);
    revenueChartData.value = { labels: [], datasets: [] };
  }
};

const fetchRevenuePieChart = async () => {
  try {
    console.log('=== REVENUE PIE CHART DEBUG ===');
    console.log('Calling revenue pie chart API with period:', selectedPieChartTimePeriod.value);
    const res = await hotelApi.getHotelRevenuePieChart(selectedPieChartTimePeriod.value);
    console.log('Revenue pie chart API Response:', res);
    console.log('Revenue pie chart data:', res.data);
    console.log('Revenue pie chart data.data:', res.data?.data);
    console.log('Labels:', res.data?.data?.labels);
    console.log('Data:', res.data?.data?.data);
    console.log('Labels length:', res.data?.data?.labels?.length);
    console.log('Data length:', res.data?.data?.data?.length);
    
    if (res.data && res.data.data && res.data.data.labels && res.data.data.labels.length) {
      console.log('Setting revenue pie chart data:', res.data.data);
      revenuePieChartData.value = res.data.data;
      console.log('Updated revenuePieChartData:', revenuePieChartData.value);
    } else {
      console.log('No revenue pie chart data found in response');
      revenuePieChartData.value = { labels: [], data: [] };
    }
  } catch (e) {
    console.error('Error fetching revenue pie chart:', e);
    revenuePieChartData.value = { labels: [], data: [] };
  }
};

watch(stats, (newStats) => {
  console.log('Stats changed:', newStats);
}, { deep: true });

watch(revenueChartData, (newData) => {
  console.log('=== REVENUE CHART DATA CHANGED ===');
  console.log('New revenueChartData:', newData);
  console.log('revenueChartData exists:', !!newData);
  console.log('revenueChartData.labels:', newData?.labels);
  console.log('revenueChartData.datasets:', newData?.datasets);
}, { deep: true });

const recentBookings = ref([
    {
        id: 1,
        guestName: 'Sarah Johnson',
        guestImage: 'https://randomuser.me/api/portraits/men/46.jpg',
        hotel: 'Grand Hotel Luxury',
        checkIn: 'Jun 1, 2025',
        checkOut: 'Jun 5, 2025',
        status: 'Confirmed',
        amount: '$1,200'
    },
    {
        id: 2,
        guestName: 'Michael Chen',
        guestImage: 'https://randomuser.me/api/portraits/men/21.jpg',
        hotel: 'Seaside Resort',
        checkIn: 'Jun 3, 2025',
        checkOut: 'Jun 7, 2025',
        status: 'Pending',
        amount: '$850'
    },
    {
        id: 3,
        guestName: 'Emma Davis',
        guestImage: 'https://randomuser.me/api/portraits/women/12.jpg',
        hotel: 'Mountain View Lodge',
        checkIn: 'Jun 5, 2025',
        checkOut: 'Jun 8, 2025',
        status: 'Completed',
        amount: '$650'
    },
    {
        id: 4,
        guestName: 'James Wilson',
        guestImage: 'https://randomuser.me/api/portraits/men/13.jpg',
        hotel: 'City Center Hotel',
        checkIn: 'Jun 6, 2025',
        checkOut: 'Jun 9, 2025',
        status: 'Cancelled',
        amount: '$920'
    }
]);

const getStatusClass = (status) => {
    const classes = {
        'Confirmed': 'bg-green-100 text-green-700',
        'Pending': 'bg-yellow-100 text-yellow-700',
        'Completed': 'bg-blue-100 text-blue-700',
        'Cancelled': 'bg-red-100 text-red-700'
    };
    return classes[status] || 'bg-slate-100 text-slate-700';
};

function formatNumber(val) {
  console.log('formatNumber input:', val, typeof val);
  const result = val?.toLocaleString?.('en-US') ?? val;
  console.log('formatNumber output:', result);
  return result;
}
function formatCurrency(val) {
  if (val == null) return '--';
  return Number(val).toLocaleString('vi-VN') + ' VND';
}
</script>