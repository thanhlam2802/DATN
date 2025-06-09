<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Sidebar -->
    <div class="fixed inset-y-0 left-0 z-50 w-64 transform bg-white shadow-lg transition-transform duration-300 ease-in-out lg:translate-x-0" :class="{ '-translate-x-full': !sidebarOpen, 'translate-x-0': sidebarOpen }">
      <div class="flex h-16 items-center justify-between px-4 border-b border-gray-200">
        <div class="flex items-center">
          <h1 class="text-xl font-bold text-gray-900">Quản lý Bus</h1>
        </div>
        <button @click="toggleSidebar" class="lg:hidden p-2 rounded-md text-gray-400 hover:text-gray-600 hover:bg-gray-100">
          <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
      
      <nav class="flex-1 px-4 py-6 space-y-2">
        <div v-for="item in navigationItems" :key="item.name">
          <button
            @click="selectMenuItem(item)"
            :class="[
              'group flex w-full items-center rounded-md px-3 py-2 text-sm font-medium transition-colors duration-200',
              currentView === item.component 
                ? 'bg-blue-50 text-blue-700 border-r-2 border-blue-700' 
                : 'text-gray-700 hover:bg-gray-50 hover:text-gray-900'
            ]"
          >
            <component :is="item.icon" class="mr-3 h-5 w-5 flex-shrink-0" :class="currentView === item.component ? 'text-blue-500' : 'text-gray-400 group-hover:text-gray-500'" />
            {{ item.name }}
          </button>
        </div>
      </nav>
    </div>

    <!-- Mobile overlay -->
    <div v-if="sidebarOpen" class="fixed inset-0 z-40 lg:hidden" @click="toggleSidebar">
      <div class="absolute inset-0 bg-gray-600 opacity-75"></div>
    </div>

    <!-- Main content -->
    <div class="lg:pl-64">
      <!-- Header -->
      <div class="sticky top-0 z-10 bg-white shadow-sm border-b border-gray-200">
        <div class="flex h-16 items-center justify-between px-4 sm:px-6 lg:px-8">
          <div class="flex items-center">
            <button @click="toggleSidebar" class="lg:hidden p-2 rounded-md text-gray-400 hover:text-gray-600 hover:bg-gray-100">
              <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
              </svg>
            </button>
            <h2 class="ml-4 text-lg font-semibold text-gray-900 lg:ml-0">{{ currentTitle }}</h2>
          </div>
          
          <div class="flex items-center space-x-4">
            <!-- Back to main site -->
            <router-link to="/" class="inline-flex items-center px-3 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50">
              <svg class="-ml-1 mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"/>
              </svg>
              Về trang chính
            </router-link>
            
            <!-- Notification button -->
            <button class="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-md">
              <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-5 5v-5zM7 7v10h10V7H7z"/>
              </svg>
            </button>
            
            <!-- User menu -->
            <div class="relative">
              <button class="flex items-center space-x-2 p-2 text-sm text-gray-700 hover:bg-gray-100 rounded-md">
                <div class="h-8 w-8 rounded-full bg-blue-500 flex items-center justify-center">
                  <span class="text-xs font-medium text-white">AD</span>
                </div>
                <span class="hidden sm:block">Admin</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Page content -->
      <main class="p-4 sm:p-6 lg:p-8">
        <div class="mx-auto max-w-7xl">
          <component :is="currentComponent" />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineAsyncComponent } from 'vue'

// Lazy load components
const TripManagement = defineAsyncComponent(() => import('./TripManagement.vue'))
const RouteManagement = defineAsyncComponent(() => import('./RouteManagement.vue'))
const Statistics = defineAsyncComponent(() => import('./Statistics.vue'))
const PriceManagement = defineAsyncComponent(() => import('./PriceManagement.vue'))

// State
const sidebarOpen = ref(false)
const currentView = ref('TripManagement')

// Component mapping
const components = {
  TripManagement,
  RouteManagement,
  Statistics,
  PriceManagement
}

// Navigation items
const navigationItems = ref([
  {
    name: 'Quản lý Chuyến xe',
    component: 'TripManagement',
    title: 'Quản lý Chuyến xe',
    icon: 'TruckIcon'
  },
  {
    name: 'Đăng Tuyến đường',
    component: 'RouteManagement', 
    title: 'Quản lý Tuyến đường',
    icon: 'MapIcon'
  },
  {
    name: 'Thống kê',
    component: 'Statistics',
    title: 'Thống kê & Báo cáo',
    icon: 'ChartBarIcon'
  },
  {
    name: 'Quản lý Giá vé',
    component: 'PriceManagement',
    title: 'Quản lý Giá vé',
    icon: 'CurrencyDollarIcon'
  }
])

// Computed
const currentTitle = computed(() => {
  const item = navigationItems.value.find(item => item.component === currentView.value)
  return item ? item.title : 'Dashboard'
})

const currentComponent = computed(() => {
  return components[currentView.value] || TripManagement
})

// Methods
const toggleSidebar = () => {
  sidebarOpen.value = !sidebarOpen.value
}

const selectMenuItem = (item) => {
  currentView.value = item.component
  sidebarOpen.value = false // Close sidebar on mobile after selection
}

// Icons components
const TruckIcon = {
  template: `
    <svg fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h5v-4l-2-2h-3v6zM8 16H3V8h5v8zM8 8V4H3v4h5z"/>
    </svg>
  `
}

const MapIcon = {
  template: `
    <svg fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7"/>
    </svg>
  `
}

const ChartBarIcon = {
  template: `
    <svg fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
    </svg>
  `
}

const CurrencyDollarIcon = {
  template: `
    <svg fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
    </svg>
  `
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