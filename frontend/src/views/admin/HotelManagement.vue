<template>
  <div>
    <h1 class="text-3xl font-bold text-gray-800 mb-4">
      Quản lý hệ thống Khách sạn
    </h1>

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
            v-if="tab.id === 'pending' && pendingHotels.length > 0"
            class="ml-2 bg-red-100 text-red-600 text-xs font-bold px-2 py-0.5 rounded-full"
            >{{ pendingHotels.length }}</span
          >
        </button>
      </nav>
    </div>

    <div>
      <div v-if="activeTab === 'pending'">
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <h2 class="text-xl font-bold text-gray-700 mb-4">
            Danh sách Khách sạn chờ phê duyệt
          </h2>
          <p
            v-if="pendingHotels.length === 0"
            class="text-center py-10 text-gray-500"
          >
            Không có khách sạn nào chờ phê duyệt.
          </p>
        </div>
      </div>

      <div v-if="activeTab === 'all_hotels'">
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <h2 class="text-xl font-bold text-gray-700 mb-4">
            Tất cả Khách sạn trên hệ thống
          </h2>
        </div>
      </div>

      <div v-if="activeTab === 'amenities'">
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-bold text-gray-700">
              Quản lý Tiện ích chung
            </h2>
            <button
              class="bg-indigo-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-indigo-700"
            >
              <i class="fas fa-plus mr-2"></i>Thêm Tiện ích
            </button>
          </div>
        </div>
      </div>

      <div v-if="activeTab === 'areas'">
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-bold text-gray-700">
              Quản lý Khu vực / Địa điểm
            </h2>
            <button
              class="bg-indigo-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-indigo-700"
            >
              <i class="fas fa-plus mr-2"></i>Thêm Khu vực
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";

const activeTab = ref("pending");
const tabs = ref([
  { id: "pending", name: "Chờ Phê duyệt", icon: "fas fa-hourglass-half" },
  { id: "all_hotels", name: "Tất cả Khách sạn", icon: "fas fa-list-ul" },
  { id: "amenities", name: "Quản lý Tiện ích", icon: "fas fa-swimming-pool" },
  { id: "areas", name: "Quản lý Khu vực", icon: "fas fa-map-marked-alt" },
]);

const allHotels = ref([
  {
    id: 1,
    name: "FLC Luxury Hotel Quy Nhon",
    vendor: "FLC Group",
    location: "Quy Nhơn",
    status: "PENDING",
  },
  {
    id: 2,
    name: "InterContinental Hanoi",
    vendor: "IHG",
    location: "Hà Nội",
    status: "ACTIVE",
  },
]);

const pendingHotels = computed(() =>
  allHotels.value.filter((h) => h.status === "PENDING")
);
</script>
