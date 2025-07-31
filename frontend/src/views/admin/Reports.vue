<template>
  <div>
    <h1 class="text-3xl font-bold text-gray-800 mb-6">Báo cáo & Phân tích</h1>

    <div class="bg-white p-4 rounded-xl shadow-lg mb-6 flex items-center gap-4">
      <div class="flex-1">
        <label class="block text-sm font-medium text-gray-700 mb-1"
          >Khoảng thời gian</label
        >
        <select
          v-model="filters.dateRange"
          class="w-full border-gray-300 rounded-lg shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
        >
          <option value="7d">7 ngày qua</option>
          <option value="30d">30 ngày qua</option>
          <option value="this_month">Tháng này</option>
          <option value="last_month">Tháng trước</option>
        </select>
      </div>
      <div class="flex-1">
        <label class="block text-sm font-medium text-gray-700 mb-1"
          >Loại dịch vụ</label
        >
        <select
          v-model="filters.serviceType"
          class="w-full border-gray-300 rounded-lg shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
        >
          <option value="all">Tất cả dịch vụ</option>
          <option value="Tour">Tour</option>
          <option value="Hotel">Khách sạn</option>
          <option value="Flight">Chuyến bay</option>
          <option value="Bus">Xe bus</option>
        </select>
      </div>
      <div class="pt-6">
        <button
          @click="fetchReportData"
          class="bg-indigo-600 text-white font-bold py-2 px-6 rounded-lg hover:bg-indigo-700 transition"
        >
          <i v-if="isLoading" class="fas fa-spinner fa-spin"></i>
          <span v-else>Áp dụng</span>
        </button>
      </div>
    </div>

    <div v-if="isLoading" class="text-center py-20">
      <i class="fas fa-spinner fa-spin text-5xl text-indigo-500"></i>
      <p class="mt-4 text-gray-600 text-lg">Đang tải dữ liệu báo cáo...</p>
    </div>

    <div v-else class="space-y-8">
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        <Widget
          title="Tổng Doanh thu"
          icon="fas fa-dollar-sign"
          :value="formatCurrency(kpiData.grossRevenue)"
        />
        <Widget
          title="Tổng số Booking"
          icon="fas fa-receipt"
          :value="kpiData.totalBookings.toLocaleString('vi-VN')"
        />
        <Widget
          title="Khách hàng mới"
          icon="fas fa-user-plus"
          :value="kpiData.newCustomers.toLocaleString('vi-VN')"
        />
        <Widget
          title="Giá trị đơn hàng TB"
          icon="fas fa-balance-scale"
          :value="formatCurrency(kpiData.averageOrderValue)"
        />
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <h3 class="font-semibold mb-4 text-gray-700">Xu hướng Doanh thu</h3>
          <RevenueChart :data="revenueTrendData" />
        </div>
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <h3 class="font-semibold mb-4 text-gray-700">
            Tỷ trọng Booking theo Dịch vụ
          </h3>
          <BookingChart :data="bookingByTypeData" type="doughnut" />
        </div>
      </div>

      <div class="bg-white p-6 rounded-xl shadow-lg">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-xl font-bold text-gray-700">Báo cáo chi tiết</h3>
          <button
            class="border border-green-500 text-green-600 font-semibold py-2 px-4 rounded-lg hover:bg-green-50 transition text-sm"
          >
            <i class="fas fa-file-excel mr-2"></i>Xuất ra Excel
          </button>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <div>
            <h4 class="font-semibold text-gray-600 mb-2">
              Top 5 Sản phẩm/Dịch vụ Bán chạy
            </h4>
            <table class="w-full text-sm">
              <thead class="text-xs text-gray-500 uppercase bg-gray-50">
                <tr>
                  <th class="px-4 py-2 text-left">Tên Dịch vụ</th>
                  <th class="px-4 py-2 text-right">Số Booking</th>
                  <th class="px-4 py-2 text-right">Doanh thu</th>
                </tr>
              </thead>
              <tbody class="divide-y">
                <tr v-for="item in topProducts" :key="item.name">
                  <td class="px-4 py-2 font-medium">{{ item.name }}</td>
                  <td class="px-4 py-2 text-right">
                    {{ item.bookings.toLocaleString("vi-VN") }}
                  </td>
                  <td class="px-4 py-2 text-right font-semibold">
                    {{ formatCurrency(item.revenue) }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div>
            <h4 class="font-semibold text-gray-600 mb-2">
              Top 5 Khách hàng Chi tiêu nhiều nhất
            </h4>
            <table class="w-full text-sm">
              <thead class="text-xs text-gray-500 uppercase bg-gray-50">
                <tr>
                  <th class="px-4 py-2 text-left">Tên Khách hàng</th>
                  <th class="px-4 py-2 text-right">Tổng chi tiêu</th>
                </tr>
              </thead>
              <tbody class="divide-y">
                <tr v-for="customer in topCustomers" :key="customer.name">
                  <td class="px-4 py-2 font-medium">{{ customer.name }}</td>
                  <td class="px-4 py-2 text-right font-semibold">
                    {{ formatCurrency(customer.spent) }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from "vue";
import Widget from "@/components/admin/Widget.vue";
// Giả sử 2 component biểu đồ này có thể nhận prop "type" để thay đổi loại biểu đồ
import RevenueChart from "@/components/admin/RevenueChart.vue";
import BookingChart from "@/components/admin/BookingChart.vue";

const isLoading = ref(true);

const filters = reactive({
  dateRange: "30d",
  serviceType: "all",
});

// --- Dữ liệu giả ---
const kpiData = ref({});
const revenueTrendData = ref({});
const bookingByTypeData = ref({});
const topProducts = ref([]);
const topCustomers = ref([]);

// --- Hàm mô phỏng việc gọi API để lấy dữ liệu ---
const fetchReportData = async () => {
  isLoading.value = true;
  console.log("Fetching report data with filters:", filters);

  // Mô phỏng độ trễ mạng
  await new Promise((resolve) => setTimeout(resolve, 800));

  // Dựa trên filters, API sẽ trả về dữ liệu tương ứng.
  // Ở đây chúng ta chỉ tạo dữ liệu giả
  kpiData.value = {
    grossRevenue: 385000000,
    totalBookings: 1234,
    newCustomers: 456,
    averageOrderValue: 312000,
  };

  revenueTrendData.value = {
    labels: ["01/07", "05/07", "10/07", "15/07", "20/07", "25/07", "29/07"],
    data: [12, 19, 15, 25, 22, 30, 28], // Đơn vị: triệu VNĐ
  };

  bookingByTypeData.value = {
    labels: ["Tour", "Khách sạn", "Chuyến bay", "Xe bus"],
    data: [450, 350, 280, 154],
  };

  topProducts.value = [
    { name: "Tour Khám phá Quy Nhơn 3N2Đ", bookings: 120, revenue: 62400000 },
    { name: "Tuyến xe Sài Gòn - Đà Lạt", bookings: 95, revenue: 28500000 },
    { name: "Khách sạn FLC Luxury", bookings: 88, revenue: 158400000 },
    { name: "Chuyến bay VJ123: SGN -> HAN", bookings: 76, revenue: 114000000 },
    { name: "Tour Đảo Jeju Hàn Quốc", bookings: 65, revenue: 975000000 },
  ];

  topCustomers.value = [
    { name: "Nguyễn Văn An", spent: 15200000 },
    { name: "Trần Thị Bình", spent: 12500000 },
    { name: "Lê Minh Cường", spent: 9800000 },
    { name: "Phạm Thị Duyên", spent: 7600000 },
    { name: "Hoàng Văn Em", spent: 6500000 },
  ];

  isLoading.value = false;
};

// --- Watcher để tự động cập nhật khi filter thay đổi ---
// watch(filters, fetchReportData); // Bạn có thể bỏ comment dòng này nếu muốn tự động tải lại

// --- Tải dữ liệu lần đầu khi component được tạo ---
onMounted(fetchReportData);

// --- Helpers ---
const formatCurrency = (value) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    value || 0
  );
</script>
