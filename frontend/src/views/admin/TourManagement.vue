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
          <span
            v-if="tab.id === 'pending' && pendingTours.length > 0"
            class="ml-2 bg-red-100 text-red-600 text-xs font-bold px-2 py-0.5 rounded-full"
            >{{ pendingTours.length }}</span
          >
        </button>
      </nav>
    </div>

    <div v-if="isLoading" class="text-center py-20">
      <i class="fas fa-spinner fa-spin text-5xl text-indigo-500"></i>
    </div>
    <div v-else>
      <div v-if="activeTab === 'pending'">
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <h2 class="text-xl font-bold text-gray-700 mb-4">
            Danh sách Tour chờ phê duyệt
          </h2>
          <table class="w-full text-sm">
            <thead class="text-xs text-gray-500 uppercase bg-gray-50">
              <tr>
                <th class="px-4 py-3 text-left">Tên Tour</th>
                <th class="px-4 py-3 text-left">Nhà cung cấp</th>
                <th class="px-4 py-3 text-left">Điểm đến</th>
                <th class="px-4 py-3 text-center">Hành động</th>
              </tr>
            </thead>
            <tbody class="divide-y">
              <tr
                v-for="tour in pendingTours"
                :key="tour.id"
                class="hover:bg-gray-50"
              >
                <td class="px-4 py-3 font-semibold">{{ tour.name }}</td>
                <td class="px-4 py-3">{{ tour.vendor }}</td>
                <td class="px-4 py-3">{{ tour.destination }}</td>
                <td class="px-4 py-3 text-center space-x-2">
                  <button class="text-blue-600 hover:underline text-xs">
                    Xem trước
                  </button>
                  <button
                    class="bg-green-500 text-white px-3 py-1 rounded-md text-xs hover:bg-green-600"
                  >
                    Duyệt
                  </button>
                  <button
                    class="bg-red-500 text-white px-3 py-1 rounded-md text-xs hover:bg-red-600"
                  >
                    Từ chối
                  </button>
                </td>
              </tr>
              <tr v-if="pendingTours.length === 0">
                <td colspan="4" class="text-center py-10 text-gray-500">
                  Không có tour nào chờ phê duyệt.
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-if="activeTab === 'all_tours'">
        <div class="bg-white p-4 rounded-xl shadow-lg mb-6">
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
              <option value="DISABLED">Bị vô hiệu hóa</option>
            </select>
            <select class="border p-2 rounded-lg">
              <option value="">Tất cả nhà cung cấp</option>
            </select>
            <select class="border p-2 rounded-lg">
              <option value="">Tất cả điểm đến</option>
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
                <th class="px-4 py-3 text-center">Nổi bật</th>
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
                    >{{ tour.status }}</span
                  >
                </td>
                <td class="px-4 py-3 text-center">
                  <label
                    class="relative inline-flex items-center cursor-pointer"
                  >
                    <input
                      type="checkbox"
                      v-model="tour.isFeatured"
                      class="sr-only peer"
                    />
                    <div
                      class="w-11 h-6 bg-gray-200 rounded-full peer peer-checked:after:translate-x-full after:content-[''] after:absolute after:top-0.5 after:left-[2px] after:bg-white after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-indigo-600"
                    ></div>
                  </label>
                </td>
                <td class="px-4 py-3 text-center">
                  <button class="text-indigo-600 hover:underline text-xs">
                    Sửa
                  </button>
                  <button class="ml-2 text-red-600 hover:underline text-xs">
                    Vô hiệu hóa
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-if="activeTab === 'destinations'">
        <div class="flex justify-end mb-4">
          <button
            class="bg-indigo-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-indigo-700 transition"
          >
            <i class="fas fa-plus mr-2"></i>Thêm Điểm đến
          </button>
        </div>
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <table class="w-full text-sm">
            <thead class="text-xs text-gray-500 uppercase bg-gray-50">
              <tr>
                <th class="px-4 py-3 text-left">Tên Điểm đến</th>
                <th class="px-4 py-3 text-center">Số Tour</th>
                <th class="px-4 py-3 text-center">Hành động</th>
              </tr>
            </thead>
            <tbody class="divide-y">
              <tr v-for="dest in destinations" :key="dest.id">
                <td class="px-4 py-3 font-semibold">{{ dest.name }}</td>
                <td class="px-4 py-3 text-center">{{ dest.tourCount }}</td>
                <td class="px-4 py-3 text-center">
                  <button class="text-indigo-600 hover:underline text-xs">
                    Sửa
                  </button>
                  <button class="ml-2 text-red-600 hover:underline text-xs">
                    Xóa
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-if="activeTab === 'tags'">
        <div class="flex justify-end mb-4">
          <button
            class="bg-indigo-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-indigo-700 transition"
          >
            <i class="fas fa-plus mr-2"></i>Thêm Thẻ mới
          </button>
        </div>
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <table class="w-full text-sm">
            <thead class="text-xs text-gray-500 uppercase bg-gray-50">
              <tr>
                <th class="px-4 py-3 text-left">Tên Thẻ</th>
                <th class="px-4 py-3 text-center">Số Tour</th>
                <th class="px-4 py-3 text-center">Hành động</th>
              </tr>
            </thead>
            <tbody class="divide-y">
              <tr v-for="tag in tags" :key="tag.id">
                <td class="px-4 py-3 font-semibold">{{ tag.name }}</td>
                <td class="px-4 py-3 text-center">{{ tag.tourCount }}</td>
                <td class="px-4 py-3 text-center">
                  <button class="text-indigo-600 hover:underline text-xs">
                    Sửa
                  </button>
                  <button class="ml-2 text-red-600 hover:underline text-xs">
                    Xóa
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";

const isLoading = ref(false);
const activeTab = ref("pending");
const tabs = ref([
  { id: "pending", name: "Chờ Phê duyệt", icon: "fas fa-hourglass-half" },
  { id: "all_tours", name: "Tất cả Tour", icon: "fas fa-list-ul" },
  {
    id: "destinations",
    name: "Quản lý Điểm đến",
    icon: "fas fa-map-marker-alt",
  },
  { id: "tags", name: "Quản lý Thẻ Tour", icon: "fas fa-tags" },
]);

// --- Dữ liệu giả ---
const allTours = ref([
  {
    id: 1,
    name: "Khám phá Vịnh Hạ Long 2N1Đ",
    vendor: "VietTravel",
    destination: "Hạ Long",
    status: "ACTIVE",
    isFeatured: true,
  },
  {
    id: 2,
    name: "Chinh phục Fansipan",
    vendor: "Sapa Tours",
    destination: "Sapa",
    status: "ACTIVE",
    isFeatured: false,
  },
  {
    id: 3,
    name: "Tour Ẩm thực Phố cổ Hà Nội",
    vendor: "Hanoi Foodies",
    destination: "Hà Nội",
    status: "PENDING",
    isFeatured: false,
  },
  {
    id: 4,
    name: "Nghỉ dưỡng tại Resort Phú Quốc",
    vendor: "BestPrice Travel",
    destination: "Phú Quốc",
    status: "DISABLED",
    isFeatured: false,
  },
]);
const destinations = ref([
  { id: 1, name: "Hạ Long", tourCount: 15 },
  { id: 2, name: "Sapa", tourCount: 12 },
  { id: 3, name: "Hà Nội", tourCount: 25 },
  { id: 4, name: "Phú Quốc", tourCount: 18 },
]);
const tags = ref([
  { id: 1, name: "Biển", tourCount: 33 },
  { id: 2, name: "Leo núi", tourCount: 12 },
  { id: 3, name: "Ẩm thực", tourCount: 25 },
  { id: 4, name: "Nghỉ dưỡng", tourCount: 18 },
]);

// --- Computed ---
const pendingTours = computed(() =>
  allTours.value.filter((tour) => tour.status === "PENDING")
);

// --- Helpers ---
const getStatusClass = (status) =>
  ({
    ACTIVE: "bg-green-100 text-green-800",
    PENDING: "bg-yellow-100 text-yellow-800",
    DISABLED: "bg-red-100 text-red-800",
  }[status] || "bg-gray-100");
</script>
