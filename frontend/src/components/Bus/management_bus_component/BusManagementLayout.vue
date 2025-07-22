<template>
  <div class="flex h-screen bg-gray-100 font-sans">
    <!-- Sidebar -->
    <aside 
      :class="[
        'fixed inset-y-0 left-0 z-30 w-64 bg-white border-r border-gray-200 transform transition-transform duration-300 ease-in-out lg:translate-x-0',
        isSidebarOpen ? 'translate-x-0' : '-translate-x-full'
      ]"
    >
      <div class="h-16 flex items-center justify-center border-b border-gray-200">
        <h1 class="text-xl font-bold text-gray-800">Quản lý Xe buýt</h1>
      </div>
      <nav class="mt-6 px-4">
        <ul>
          <li v-for="item in menuItems" :key="item.id">
            <button
              @click="selectMenuItem(item.id)"
              :class="[
                'w-full flex items-center px-4 py-2.5 my-1 rounded-lg transition-colors duration-200',
                activeTab === item.id
                  ? 'bg-indigo-50 text-indigo-600 font-semibold'
                  : 'text-gray-600 hover:bg-gray-50',
              ]"
            >
              <component :is="item.icon" class="h-5 w-5 mr-3" />
              <span>{{ item.name }}</span>
            </button>
          </li>
        </ul>
      </nav>
    </aside>

    <!-- Overlay for mobile when sidebar is open -->
    <div v-if="isSidebarOpen" @click="isSidebarOpen = false" class="fixed inset-0 z-20 bg-black/30 lg:hidden"></div>

    <!-- Main content -->
    <div class="flex-1 flex flex-col overflow-hidden lg:pl-64">
      <header class="h-16 bg-white border-b border-gray-200 flex items-center justify-between px-6">
        <div class="flex items-center">
          <!-- Hamburger Menu Button -->
          <button @click.stop="isSidebarOpen = !isSidebarOpen" class="lg:hidden mr-4">
            <div class="space-y-1.5">
              <span :class="['block w-6 h-0.5 bg-gray-600 transition-transform duration-300 ease-in-out', { 'rotate-45 translate-y-2': isSidebarOpen }]"></span>
              <span :class="['block w-6 h-0.5 bg-gray-600 transition-opacity duration-300 ease-in-out', { 'opacity-0': isSidebarOpen }]"></span>
              <span :class="['block w-6 h-0.5 bg-gray-600 transition-transform duration-300 ease-in-out', { '-rotate-45 -translate-y-2': isSidebarOpen }]"></span>
            </div>
          </button>
          <h2 class="text-lg font-semibold text-gray-800">
            {{ menuItems.find(i => i.id === activeTab)?.name || 'Dashboard' }}
          </h2>
        </div>
        <div class="flex items-center space-x-4">
           <router-link to="/" class="text-sm text-indigo-600 hover:underline">Về trang chính</router-link>
        </div>
      </header>
      
      <main class="flex-1 overflow-x-hidden overflow-y-auto bg-gray-100 p-6">
        <div class="container mx-auto">
          <keep-alive>
            <component :is="activeComponent" />
          </keep-alive>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineAsyncComponent } from 'vue'
import {
  MapIcon,
  TicketIcon,
  CircleDollarSignIcon,
  BarChart3Icon,
  LayoutGridIcon,
  BusIcon
} from 'lucide-vue-next'

// Lazy load components
const RouteManagement = defineAsyncComponent(() => import('./RouteManagement.vue'))
const BusCategoryManagement = defineAsyncComponent(() => import('./BusCategoryManagement.vue'))
const BusManagement = defineAsyncComponent(() => import('./BusManagement.vue'))
const TripManagement = defineAsyncComponent(() => import('./TripManagement.vue'))
const PriceManagement = defineAsyncComponent(() => import('./PriceManagement.vue'))
const Statistics = defineAsyncComponent(() => import('./Statistics.vue'))

// State
const isSidebarOpen = ref(false);
const activeTab = ref('routes')

const tabs = {
  routes: RouteManagement,
  categories: BusCategoryManagement,
  buses: BusManagement,
  trips: TripManagement,
  prices: PriceManagement,
  stats: Statistics
}

const menuItems = ref([
  { id: 'routes', name: 'Tuyến đường', icon: MapIcon },
  { id: 'categories', name: 'Loại xe', icon: LayoutGridIcon },
  { id: 'buses', name: 'Xe buýt', icon: BusIcon },
  { id: 'trips', name: 'Chuyến đi', icon: TicketIcon },
  { id: 'prices', name: 'Giá vé', icon: CircleDollarSignIcon },
  { id: 'stats', name: 'Thống kê', icon: BarChart3Icon }
])

const activeComponent = computed(() => tabs[activeTab.value])

const selectMenuItem = (id) => {
  activeTab.value = id;
  if (window.innerWidth < 1024) { // lg breakpoint
    isSidebarOpen.value = false;
  }
}
</script>

<style scoped>
/* Custom scrollbar for sidebar */
nav::-webkit-scrollbar {
  width: 4px;
}

nav::-webkit-scrollbar-track {
  background: #f1f5f9;
}

nav::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 2px;
}

nav::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style> 