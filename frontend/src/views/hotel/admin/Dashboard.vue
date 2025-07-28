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
                        <div class="flex items-center gap-3">
                          <div class="font-medium text-lg">Biểu đồ doanh thu {{ chartTimeLabel }}</div>
                          <div class="flex items-center gap-2 text-lg font-bold text-orange-600">
                            <div class="w-5 h-5 bg-orange-500 rounded-full flex items-center justify-center">
                              <i class="fas fa-arrow-right text-white text-xs"></i>
                            </div>
                            {{ formatCurrency(chartTotalRevenue) }}
                          </div>
                        </div>
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

                    <div class="bg-white rounded-lg shadow-sm p-3 mb-4">
                      <div class="flex justify-between items-center mb-2">
                        <div class="font-medium text-lg">Doanh thu theo khách sạn</div>
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

                    <div class="bg-white rounded-lg shadow-sm p-3">
                      <div class="flex justify-between items-center mb-2">
                        <div class="flex items-center gap-3">
                          <div class="font-medium text-lg">Top loại phòng đươc đặt nhiều nhất {{ topRoomsTimeLabel }}</div>
                        </div>
                        <div class="w-36">
                          <CustomSelect
                            v-model="selectedTopRoomsTimePeriod"
                            :options="timeOptions"
                            placeholder="Chọn thời gian"
                            @update:modelValue="onTopRoomsTimePeriodChange"
                          />
                        </div>
                      </div>
                      
                      <div class="mb-4">
                        <div class="relative w-64">
                          <input
                            v-model="topRoomsSearchQuery"
                            type="text"
                            placeholder="Tìm kiếm tên phòng"
                            class="w-full px-4 py-2 pl-10 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent text-sm"
                          />
                          <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                            <i class="fas fa-search text-gray-400 text-sm"></i>
                          </div>
                        </div>
                      </div>
                      
                      <template v-if="topRoomsChartData">
                        <TopRoomsChart :data="filteredTopRoomsChartData" :height="400" />
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
import TopRoomsChart from '@/components/Hotel/HotelAdmin/TopRoomsChart.vue';
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
const selectedTopRoomsTimePeriod = ref('this_month');
const topRoomsSearchQuery = ref('');

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

const chartTimeLabel = computed(() => {
  switch (selectedChartTimePeriod.value) {
    case 'today':
      return 'hôm nay';
    case 'yesterday':
      return 'hôm qua';
    case 'last_7_days':
      return '7 ngày qua';
    case 'this_month':
      return 'tháng này';
    case 'last_month':
      return 'tháng trước';
    default:
      return 'Tháng này';
  }
});

const pieChartTimeLabel = computed(() => {
  switch (selectedPieChartTimePeriod.value) {
    case 'today':
      return 'Hôm nay';
    case 'yesterday':
      return 'Hôm qua';
    case 'last_7_days':
      return '7 ngày qua';
    case 'this_month':
      return 'Tháng này';
    case 'last_month':
      return 'Tháng trước';
    default:
      return 'Tháng này';
  }
});

const topRoomsTimeLabel = computed(() => {
  switch (selectedTopRoomsTimePeriod.value) {
    case 'today':
      return 'hôm nay';
    case 'yesterday':
      return 'hôm qua';
    case 'last_7_days':
      return '7 ngày qua';
    case 'this_month':
      return 'tháng này';
    case 'last_month':
      return 'tháng trước';
    default:
      return 'tháng này';
  }
});

const chartTotalRevenue = computed(() => {
  if (!revenueChartData.value || !revenueChartData.value.datasets) {
    return 0;
  }
  
  let total = 0;
  
  revenueChartData.value.datasets.forEach(dataset => {
    if (dataset.data && Array.isArray(dataset.data)) {
      total += dataset.data.reduce((sum, value) => sum + (value || 0), 0);
    }
  });
  
  return total;
});

const filteredTopRoomsChartData = computed(() => {
  if (!topRoomsChartData.value || !topRoomsChartData.value.labels) {
    return { labels: [], data: [], hotels: [] };
  }
  
  const searchQuery = topRoomsSearchQuery.value.toLowerCase().trim();
  
  if (!searchQuery) {
    return topRoomsChartData.value;
  }
  
  const filteredLabels = [];
  const filteredData = [];
  const filteredHotels = [];
  
  topRoomsChartData.value.labels.forEach((label, index) => {
    if (label.toLowerCase().includes(searchQuery)) {
      filteredLabels.push(label);
      filteredData.push(topRoomsChartData.value.data[index]);
      filteredHotels.push(topRoomsChartData.value.hotels[index]);
    }
  });
  
  return {
    labels: filteredLabels,
    data: filteredData,
    hotels: filteredHotels
  };
});

const revenueChartData = ref({ labels: [], data: [] });
const revenuePieChartData = ref({ labels: [], data: [] });
const topRoomsChartData = ref({ labels: [], data: [], hotels: [] });

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

const onTopRoomsTimePeriodChange = (value) => {
  selectedTopRoomsTimePeriod.value = value;
  fetchTopRoomsChart();
};

onMounted(() => {
  const breadcrumbStore = useAdminBreadcrumbStore();
  breadcrumbStore.setBreadcrumb([
    { label: 'Thống kê', active: true }
  ]);
  fetchDashboardStatistics();
  fetchRevenueChart();
  fetchRevenuePieChart();
  fetchTopRoomsChart();
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

const fetchTopRoomsChart = async () => {
  try {
    console.log('=== TOP ROOMS CHART DEBUG ===');
    console.log('Calling top rooms chart API with period:', selectedTopRoomsTimePeriod.value);
    const res = await hotelApi.getTopRoomsChart(selectedTopRoomsTimePeriod.value);
    console.log('Top rooms chart API Response:', res);
    console.log('Top rooms chart data:', res.data);
    console.log('Top rooms chart data.data:', res.data?.data);
    console.log('Labels:', res.data?.data?.labels);
    console.log('Data:', res.data?.data?.data);
    console.log('Hotels:', res.data?.data?.hotels);
    
    if (res.data && res.data.data) {
      console.log('Setting top rooms chart data:', res.data.data);
      topRoomsChartData.value = res.data.data;
      console.log('Updated topRoomsChartData:', topRoomsChartData.value);
    } else {
      console.log('No top rooms chart data found in response');
      topRoomsChartData.value = { labels: [], data: [], hotels: [] };
    }
  } catch (e) {
    console.error('Error fetching top rooms chart:', e);
    topRoomsChartData.value = { labels: [], data: [], hotels: [] };
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