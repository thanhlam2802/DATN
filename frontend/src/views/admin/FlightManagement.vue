<template>
  <div>
    <h1 class="text-3xl font-bold text-gray-800 mb-4">
      Quản lý hệ thống Chuyến bay
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
        </button>
      </nav>
    </div>

    <div>
      <div v-if="activeTab === 'airlines'">
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-bold text-gray-700">
              Quản lý Hãng hàng không
            </h2>
            <button
              class="bg-indigo-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-indigo-700"
            >
              <i class="fas fa-plus mr-2"></i>Thêm Hãng bay
            </button>
          </div>
        </div>
      </div>

      <div v-if="activeTab === 'airports'">
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-bold text-gray-700">Quản lý Sân bay</h2>
            <button
              class="bg-indigo-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-indigo-700"
            >
              <i class="fas fa-plus mr-2"></i>Thêm Sân bay
            </button>
          </div>
        </div>
      </div>

      <div v-if="activeTab === 'settings'">
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <h2 class="text-xl font-bold text-gray-700 mb-4">
            Cài đặt & Tích hợp API
          </h2>
          <div class="space-y-4 max-w-xl">
            <div>
              <label class="block font-semibold"
                >API Key nhà cung cấp (GDS)</label
              >
              <input
                type="text"
                class="w-full border p-2 rounded-lg mt-1"
                placeholder="Nhập API Key..."
              />
            </div>
            <div>
              <label class="block font-semibold"
                >Markup / Phí dịch vụ (%)</label
              >
              <input
                type="number"
                class="w-full border p-2 rounded-lg mt-1"
                value="5"
              />
            </div>
            <div class="pt-2">
              <button
                class="bg-indigo-600 text-white font-bold py-2 px-6 rounded-lg hover:bg-indigo-700"
              >
                Lưu Cài đặt
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

const activeTab = ref("airlines");
const tabs = ref([
  { id: "airlines", name: "Quản lý Hãng bay", icon: "fas fa-building" },
  { id: "airports", name: "Quản lý Sân bay", icon: "fas fa-plane-departure" },
  { id: "settings", name: "Cài đặt & Tích hợp", icon: "fas fa-cog" },
]);
</script>
