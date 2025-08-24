<template>
  <div>
    <h1 class="text-3xl font-bold text-gray-800 mb-4">Quản lý hệ thống Tour</h1>

    <div class="border-b border-gray-200 mb-6">
      <nav class="-mb-px flex space-x-6">
        <button
          v-for="tab in tabs"
          :key="tab.id"
          @click="activeTab = tab.id"
          :class="[
            'whitespace-nowrap py-3 px-1 border-b-2 font-medium text-sm flex items-center gap-2',
            activeTab === tab.id
              ? 'border-indigo-500 text-indigo-600'
              : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300',
          ]"
        >
          <i :class="tab.icon"></i>
          <span>{{ tab.name }}</span>
        </button>
      </nav>
    </div>

    <div v-if="isLoading" class="text-center py-20">
      <i class="fas fa-spinner fa-spin text-5xl text-indigo-500"></i>
    </div>

    <div v-else>
      <!-- TAB 1: Vendors -->
      <div v-if="activeTab === 'vendors'">
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <h2 class="text-xl font-bold text-gray-700 mb-4">
            Danh sách Nhà cung cấp Tour
          </h2>
          <table class="w-full text-sm">
            <thead class="text-xs text-gray-500 uppercase bg-gray-50">
              <tr>
                <th class="px-4 py-3 text-left">Tên Nhà cung cấp</th>
                <th class="px-4 py-3 text-center">Số lượng Tour</th>
                <th class="px-4 py-3 text-center">Hành động</th>
              </tr>
            </thead>
            <tbody class="divide-y">
              <tr
                v-for="vendor in vendorsWithTourCount"
                :key="vendor.vendorId"
                class="hover:bg-gray-50"
              >
                <td class="px-4 py-3 font-semibold">{{ vendor.vendorName }}</td>
                <td class="px-4 py-3 text-center">
                  <span
                    class="bg-indigo-100 text-indigo-800 text-xs font-medium px-2.5 py-0.5 rounded-full"
                  >
                    {{ vendor.tourCount }}
                  </span>
                </td>
                <td class="px-4 py-3 text-center">
                  <button
                    class="text-blue-600 hover:underline text-xs"
                    @click="
                      toggleVendorTours(vendor.vendorId, vendor.vendorName)
                    "
                  >
                    {{
                      showVendorTours && selectedVendor === vendor.vendorName
                        ? "Ẩn danh sách Tour"
                        : "Xem danh sách Tour"
                    }}
                  </button>
                </td>
              </tr>

              <tr v-if="vendorsWithTourCount.length === 0">
                <td colspan="3" class="text-center py-10 text-gray-500">
                  Chưa có nhà cung cấp nào đăng ký.
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- TAB 2: All Tours -->
      <div v-if="activeTab === 'all_tours'">
        <div class="bg-white p-4 rounded-xl shadow-lg mb-6">
          <h2 class="text-xl font-bold text-gray-700 mb-4 px-2 pt-2">
            Bộ lọc Tour
          </h2>
          <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
            <input
              type="text"
              placeholder="Tìm tên, mã tour..."
              class="border p-2 rounded-lg"
            />
            <select class="border p-2 rounded-lg">
              <option value="">Tất cả trạng thái</option>
              <option value="ACTIVE">Đang bán</option>
              <option value="PENDING">Chờ duyệt</option>
              <option value="DISABLED">Đã vô hiệu hóa</option>
            </select>
            <select class="border p-2 rounded-lg">
              <option value="">Tất cả nhà cung cấp</option>
              <option
                v-for="vendor in vendors"
                :key="vendor.vendorId"
                :value="vendor.vendorId"
              >
                {{ vendor.vendorName }}
              </option>
            </select>
          </div>
        </div>
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <table class="w-full text-sm">
            <thead class="text-xs text-gray-500 uppercase bg-gray-50">
              <tr>
                <th class="px-4 py-3 text-left">Tên Tour / Nhà cung cấp</th>
                <th class="px-4 py-3 text-left">Điểm đến</th>
                <th class="px-4 py-3 text-center">Trạng thái</th>
                <th class="px-4 py-3 text-center">Hành động</th>
              </tr>
            </thead>
            <tbody class="divide-y">
              <tr
                v-for="tour in allTours"
                :key="tour.id"
                class="hover:bg-gray-50"
              >
                <td class="px-4 py-3">
                  <div class="font-bold text-gray-800">{{ tour.name }}</div>
                  <div class="text-xs text-gray-500">{{ tour.vendor }}</div>
                </td>
                <td class="px-4 py-3">{{ tour.destination }}</td>
                <td class="px-4 py-3 text-center">
                  <span
                    :class="getStatusClass(tour.status)"
                    class="px-2 py-1 text-xs font-medium rounded-full"
                    >{{ getStatusText(tour.status) }}</span
                  >
                </td>
                <td class="px-4 py-3 text-center space-x-2">
                  <button class="text-indigo-600 hover:underline text-xs">
                    Xem
                  </button>
                  <button class="text-red-600 hover:underline text-xs">
                    Vô hiệu hóa
                  </button>
                </td>
              </tr>
              <tr v-if="allTours.length === 0">
                <td colspan="4" class="text-center py-10 text-gray-500">
                  Không có tour nào.
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- TAB 3: Trends (ẩn tạm hoặc placeholder) -->
      <div v-if="activeTab === 'trends'">
        <div
          class="bg-white p-6 rounded-xl shadow-lg text-center text-gray-500"
        >
          Tính năng Xu hướng tìm kiếm sẽ được bổ sung sau.
        </div>
      </div>
    </div>
  </div>
  <div v-if="showVendorTours" class="mt-6 bg-white p-6 rounded-xl shadow-lg">
    <h3 class="text-lg font-bold text-gray-700 mb-4">
      Danh sách Tour của {{ selectedVendor }}
    </h3>
    <table class="w-full text-sm">
      <thead class="text-xs text-gray-500 uppercase bg-gray-50">
        <tr>
          <th class="px-4 py-3 text-left">Tên Tour</th>
          <th class="px-4 py-3 text-left">Điểm đến</th>
          <th class="px-4 py-3 text-center">Trạng thái</th>
        </tr>
      </thead>
      <tbody class="divide-y">
        <tr v-for="tour in vendorTours" :key="tour.id" class="hover:bg-gray-50">
          <td class="px-4 py-3 font-semibold">{{ tour.name }}</td>
          <td class="px-4 py-3">{{ tour.destination }}</td>
          <td class="px-4 py-3 text-center">
            <span
              :class="getStatusClass(tour.status)"
              class="px-2 py-1 text-xs font-medium rounded-full"
            >
              {{ getStatusText(tour.status) }}
            </span>
          </td>
        </tr>
        <tr v-if="vendorTours.length === 0">
          <td colspan="3" class="text-center py-10 text-gray-500">
            Nhà cung cấp này chưa có tour nào.
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import axios from "axios";

const isLoading = ref(true);
const activeTab = ref("vendors");
const vendorTours = ref([]);
const selectedVendor = ref(null);
const showVendorTours = ref(false);

const fetchVendorTours = async (vendorId, vendorName) => {
  try {
    isLoading.value = true;
    selectedVendor.value = vendorName;
    const res = await axios.get(
      `http://localhost:8080/api/manage/vendors/${vendorId}/tours`
    );
    vendorTours.value = res.data.data || [];
    showVendorTours.value = true;
  } catch (err) {
    console.error("Lỗi khi load tour của vendor:", err);
  } finally {
    isLoading.value = false;
  }
};
const toggleVendorTours = async (vendorId, vendorName) => {
  if (showVendorTours.value && selectedVendor.value === vendorName) {
    // Nếu đang mở, click sẽ ẩn
    showVendorTours.value = false;
    selectedVendor.value = null;
    vendorTours.value = [];
  } else {
    // Nếu đang ẩn, click sẽ load và hiện
    await fetchVendorTours(vendorId, vendorName);
  }
};

const tabs = ref([
  { id: "vendors", name: "Nhà cung cấp", icon: "fas fa-building" },
  { id: "all_tours", name: "Quản lý Tour", icon: "fas fa-list-ul" },
  { id: "trends", name: "Xu hướng tìm kiếm", icon: "fas fa-chart-line" },
]);

// --- Dữ liệu ---
const allTours = ref([]);
const vendors = ref([]);

// --- Lấy dữ liệu từ API ---
const fetchData = async () => {
  try {
    isLoading.value = true;

    // 1. Vendors summary
    const vendorRes = await axios.get(
      "http://localhost:8080/api/manage/vendors/summary"
    );
    vendors.value = vendorRes.data.data || [];

    // 2. All tours
    const toursRes = await axios.get("http://localhost:8080/api/manage/tours");
    allTours.value = toursRes.data.data || [];
  } catch (err) {
    console.error("Lỗi khi load dữ liệu:", err);
  } finally {
    isLoading.value = false;
  }
};

// --- Lifecycle ---
onMounted(() => {
  fetchData();
});

// --- Computed ---
const vendorsWithTourCount = computed(() =>
  vendors.value.map((vendor) => ({
    ...vendor,
    tourCount: vendor.tourCount || 0,
  }))
);

// --- Helpers ---
const getStatusClass = (status) =>
  ({
    ACTIVE: "bg-green-100 text-green-800",
    PENDING: "bg-yellow-100 text-yellow-800",
    DISABLED: "bg-gray-200 text-gray-800",
  }[status] || "bg-gray-100");

const getStatusText = (status) =>
  ({
    ACTIVE: "Đang bán",
    PENDING: "Chờ duyệt",
    DISABLED: "Vô hiệu hóa",
  }[status] || "Không xác định");
</script>
