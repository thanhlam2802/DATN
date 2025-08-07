<template>
  <div>
    <h1 class="text-3xl font-bold text-gray-800 mb-4">Quản lý Doanh thu</h1>

    <div class="border-b border-gray-200 mb-6">
      <nav class="-mb-px flex space-x-6">
        <button
          v-for="tab in tabs"
          :key="tab.id"
          @click="activeTab = tab.id"
          :class="[
            'whitespace-nowrap py-3 px-1 border-b-2 font-medium text-sm',
            activeTab === tab.id
              ? 'border-indigo-500 text-indigo-600'
              : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300',
          ]"
        >
          <i :class="tab.icon" class="mr-2"></i>{{ tab.name }}
        </button>
      </nav>
    </div>

    <div>
      <div v-if="activeTab === 'overview'">
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
          <Widget
            title="Doanh thu tháng này"
            icon="fas fa-dollar-sign"
            :value="formatCurrency(overviewStats.monthlyRevenue)"
          />
          <Widget
            title="Lợi nhuận ước tính"
            icon="fas fa-chart-pie"
            :value="formatCurrency(overviewStats.estimatedProfit)"
          />
          <Widget
            title="Chi phí tháng này"
            icon="fas fa-file-invoice-dollar"
            :value="formatCurrency(overviewStats.monthlyCosts)"
          />
          <Widget
            title="Tiền đã hoàn trả"
            icon="fas fa-undo-alt"
            :value="formatCurrency(overviewStats.refundedAmount)"
          />
        </div>
        <RevenueChart :data="revenueChartData" />
      </div>

      <div v-if="activeTab === 'transactions'">
        <div class="bg-white p-4 rounded-xl shadow-lg mb-6">
          <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
            <input
              type="text"
              v-model="filters.search"
              placeholder="Tìm ID, tên, email..."
              class="border p-2 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
            <select
              v-model="filters.status"
              class="border p-2 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              <option value="">Tất cả trạng thái</option>
              <option value="Success">Thành công</option>
              <option value="Failed">Thất bại</option>
              <option value="Pending">Đang chờ</option>
            </select>
            <select
              v-model="filters.method"
              class="border p-2 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              <option value="">Tất cả phương thức</option>
              <option value="VNPAY">VNPAY</option>
              <option value="MoMo">MoMo</option>
              <option value="Credit Card">Thẻ tín dụng</option>
            </select>
            <input
              type="date"
              v-model="filters.date"
              class="border p-2 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>
        </div>
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <div class="overflow-x-auto">
            <table class="w-full text-sm">
              <thead class="text-xs text-gray-500 uppercase bg-gray-50">
                <tr>
                  <th class="px-4 py-3">Mã Giao dịch</th>
                  <th class="px-4 py-3">Mã Đơn hàng</th>
                  <th class="px-4 py-3">Khách hàng</th>
                  <th class="px-4 py-3">Số tiền</th>
                  <th class="px-4 py-3">Phương thức</th>
                  <th class="px-4 py-3">Trạng thái</th>
                  <th class="px-4 py-3">Thời gian</th>
                </tr>
              </thead>
              <tbody class="divide-y">
                <tr
                  v-for="tx in filteredTransactions"
                  :key="tx.id"
                  class="hover:bg-gray-50"
                >
                  <td class="px-4 py-3 font-mono text-gray-600">{{ tx.id }}</td>
                  <td class="px-4 py-3 font-mono text-indigo-600">
                    {{ tx.orderId }}
                  </td>
                  <td class="px-4 py-3 font-semibold">{{ tx.customerName }}</td>
                  <td class="px-4 py-3 font-bold text-gray-800">
                    {{ formatCurrency(tx.amount) }}
                  </td>
                  <td class="px-4 py-3">{{ tx.method }}</td>
                  <td class="px-4 py-3">
                    <span
                      :class="getStatusClass(tx.status)"
                      class="px-2 py-1 text-xs font-medium rounded-full"
                      >{{ tx.status }}</span
                    >
                  </td>
                  <td class="px-4 py-3">{{ formatDateTime(tx.timestamp) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div v-if="activeTab === 'refunds'" class="space-y-8">
        <div>
          <h2 class="text-xl font-bold text-gray-700 mb-4">
            Yêu cầu đang chờ xử lý ({{ pendingRefunds.length }})
          </h2>
          <div class="bg-white p-6 rounded-xl shadow-lg">
            <div class="overflow-x-auto">
              <table class="w-full text-sm">
                <thead class="text-xs text-gray-500 uppercase bg-gray-50">
                  <tr>
                    <th class="px-4 py-3">Mã Đơn hàng</th>
                    <th class="px-4 py-3">Khách hàng</th>
                    <th class="px-4 py-3">Số tiền</th>
                    <th class="px-4 py-3">Lý do</th>
                    <th class="px-4 py-3">Ngày yêu cầu</th>
                    <th class="px-4 py-3 text-center">Hành động</th>
                  </tr>
                </thead>
                <tbody class="divide-y">
                  <tr
                    v-for="refund in pendingRefunds"
                    :key="refund.id"
                    class="hover:bg-gray-50"
                  >
                    <td class="px-4 py-3 font-mono text-indigo-600">
                      {{ refund.orderId }}
                    </td>
                    <td class="px-4 py-3 font-semibold">
                      {{ refund.customerName }}
                    </td>
                    <td class="px-4 py-3 font-bold text-gray-800">
                      {{ formatCurrency(refund.amount) }}
                    </td>
                    <td class="px-4 py-3 text-gray-600 w-1/3">
                      {{ refund.reason }}
                    </td>
                    <td class="px-4 py-3">
                      {{ formatDateTime(refund.requestDate) }}
                    </td>
                    <td class="px-4 py-3 text-center space-x-2">
                      <button
                        class="bg-green-500 text-white px-3 py-1 rounded-md text-xs hover:bg-green-600"
                      >
                        Phê duyệt
                      </button>
                      <button
                        class="bg-red-500 text-white px-3 py-1 rounded-md text-xs hover:bg-red-600"
                      >
                        Từ chối
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
        <div>
          <h2 class="text-xl font-bold text-gray-700 mb-4">
            Lịch sử Hoàn tiền
          </h2>
          <div class="bg-white p-6 rounded-xl shadow-lg">
            <div class="overflow-x-auto">
              <table class="w-full text-sm">
                <thead class="text-xs text-gray-500 uppercase bg-gray-50">
                  <tr>
                    <th class="px-4 py-3">Mã Đơn hàng</th>
                    <th class="px-4 py-3">Khách hàng</th>
                    <th class="px-4 py-3">Số tiền</th>
                    <th class="px-4 py-3">Ngày xử lý</th>
                    <th class="px-4 py-3">Người xử lý</th>
                    <th class="px-4 py-3">Trạng thái</th>
                  </tr>
                </thead>
                <tbody class="divide-y">
                  <tr
                    v-for="refund in historyRefunds"
                    :key="refund.id"
                    class="hover:bg-gray-50"
                  >
                    <td class="px-4 py-3 font-mono text-indigo-600">
                      {{ refund.orderId }}
                    </td>
                    <td class="px-4 py-3 font-semibold">
                      {{ refund.customerName }}
                    </td>
                    <td class="px-4 py-3 font-bold text-gray-800">
                      {{ formatCurrency(refund.amount) }}
                    </td>
                    <td class="px-4 py-3">
                      {{ formatDateTime(refund.processedDate) }}
                    </td>
                    <td class="px-4 py-3">{{ refund.processedBy }}</td>
                    <td class="px-4 py-3">
                      <span
                        :class="getStatusClass(refund.status)"
                        class="px-2 py-1 text-xs font-medium rounded-full"
                        >{{ refund.status }}</span
                      >
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
</template>

<script setup>
import { ref, computed } from "vue";
import Widget from "@/components/admin/Widget.vue";
import RevenueChart from "@/components/admin/RevenueChart.vue";

const activeTab = ref("overview");
const tabs = ref([
  { id: "overview", name: "Tổng quan", icon: "fas fa-chart-bar" },
  { id: "transactions", name: "Lịch sử Giao dịch", icon: "fas fa-history" },
  { id: "refunds", name: "Quản lý Hoàn tiền", icon: "fas fa-undo-alt" },
]);

// --- Dữ liệu giả ---
const overviewStats = ref({
  monthlyRevenue: 125000000,
  estimatedProfit: 25000000,
  monthlyCosts: 15000000,
  refundedAmount: 5200000,
});

const revenueChartData = ref({
  labels: ["Tuần 1", "Tuần 2", "Tuần 3", "Tuần 4", "Tuần 5"],
  data: [22, 39, 28, 45, 35], // Đơn vị: triệu VNĐ
});

const filters = ref({ search: "", status: "", method: "", date: "" });
const transactions = ref([
  {
    id: "TXN1001",
    orderId: "BK-001",
    customerName: "Võ Lê Thành Lâm",
    amount: 5200000,
    method: "VNPAY",
    status: "Success",
    timestamp: "2025-07-29T10:00:00Z",
  },
  {
    id: "TXN1002",
    orderId: "BK-002",
    customerName: "Nguyễn Văn An",
    amount: 1800000,
    method: "MoMo",
    status: "Success",
    timestamp: "2025-07-28T15:30:00Z",
  },
  {
    id: "TXN1003",
    orderId: "BK-003",
    customerName: "Trần Thị Bình",
    amount: 2100000,
    method: "Credit Card",
    status: "Failed",
    timestamp: "2025-07-28T11:00:00Z",
  },
  {
    id: "TXN1004",
    orderId: "BK-004",
    customerName: "Lê Minh Cường",
    amount: 8500000,
    method: "VNPAY",
    status: "Pending",
    timestamp: "2025-07-29T13:00:00Z",
  },
]);

const pendingRefunds = ref([
  {
    id: 1,
    orderId: "BK-005",
    customerName: "Phạm Thị Duyên",
    amount: 3500000,
    reason: "Khách hàng yêu cầu hủy tour do lịch trình thay đổi.",
    requestDate: "2025-07-29T09:00:00Z",
  },
]);

const historyRefunds = ref([
  {
    id: 2,
    orderId: "BK-006",
    customerName: "Hoàng Văn Em",
    amount: 1200000,
    processedDate: "2025-07-27T14:00:00Z",
    processedBy: "Admin",
    status: "Đã hoàn tiền",
  },
  {
    id: 3,
    orderId: "BK-007",
    customerName: "Vũ Thị Giáng",
    amount: 950000,
    processedDate: "2025-07-26T18:00:00Z",
    processedBy: "Admin",
    status: "Đã từ chối",
  },
]);

// --- Computed & Helpers ---
const filteredTransactions = computed(() => {
  return transactions.value.filter((tx) => {
    const searchMatch = filters.value.search
      ? tx.id.toLowerCase().includes(filters.value.search.toLowerCase()) ||
        tx.orderId.toLowerCase().includes(filters.value.search.toLowerCase()) ||
        tx.customerName
          .toLowerCase()
          .includes(filters.value.search.toLowerCase())
      : true;
    const statusMatch = filters.value.status
      ? tx.status === filters.value.status
      : true;
    const methodMatch = filters.value.method
      ? tx.method === filters.value.method
      : true;
    const dateMatch = filters.value.date
      ? tx.timestamp.startsWith(filters.value.date)
      : true;
    return searchMatch && statusMatch && methodMatch && dateMatch;
  });
});

const formatCurrency = (value) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    value || 0
  );
const formatDateTime = (dateString) =>
  new Date(dateString).toLocaleString("vi-VN");

const getStatusClass = (status) =>
  ({
    Success: "bg-green-100 text-green-800",
    "Thành công": "bg-green-100 text-green-800",
    "Đã hoàn tiền": "bg-blue-100 text-blue-800",
    Pending: "bg-yellow-100 text-yellow-800",
    "Đang chờ": "bg-yellow-100 text-yellow-800",
    Failed: "bg-red-100 text-red-800",
    "Thất bại": "bg-red-100 text-red-800",
    "Đã từ chối": "bg-gray-100 text-gray-800",
  }[status] || "bg-gray-100");
</script>
