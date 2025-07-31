<template>
  <div>
    <h1 class="text-3xl font-bold text-gray-800 mb-4">
      Quản lý hệ thống Xe bus
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
      <div v-if="activeTab === 'companies'">
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-bold text-gray-700">
              Quản lý Nhà xe đối tác
            </h2>
            <button
              class="bg-indigo-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-indigo-700"
            >
              <i class="fas fa-plus mr-2"></i>Thêm Nhà xe
            </button>
          </div>
        </div>
      </div>

      <div v-if="activeTab === 'routes'">
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-bold text-gray-700">Quản lý Tuyến xe</h2>
            <button
              class="bg-indigo-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-indigo-700"
            >
              <i class="fas fa-plus mr-2"></i>Thêm Tuyến xe
            </button>
          </div>
        </div>
      </div>

      <div v-if="activeTab === 'amenities'">
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-bold text-gray-700">Quản lý Tiện ích Xe</h2>
            <button
              class="bg-indigo-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-indigo-700"
            >
              <i class="fas fa-plus mr-2"></i>Thêm Tiện ích
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

const activeTab = ref("companies");
const tabs = ref([
  { id: "companies", name: "Quản lý Nhà xe", icon: "fas fa-building" },
  { id: "routes", name: "Quản lý Tuyến xe", icon: "fas fa-route" },
  { id: "amenities", name: "Quản lý Tiện ích", icon: "fas fa-wifi" },
]);
</script>
