<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Sidebar -->
    <div class="fixed inset-y-0 left-0 z-50 w-64 transform bg-white shadow-lg transition-transform duration-300 ease-in-out lg:translate-x-0" 
         :class="{ '-translate-x-full': !sidebarOpen, 'translate-x-0': sidebarOpen }">
      <div class="flex h-16 items-center justify-between px-4 border-b border-gray-200">
        <div class="flex items-center">
          <i class="fas fa-plane text-blue-600 text-2xl mr-3"></i>
          <h1 class="text-xl font-bold text-gray-900">Flight Admin</h1>
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
            <i :class="[item.icon, 'mr-3 h-5 w-5 flex-shrink-0', currentView === item.component ? 'text-blue-500' : 'text-gray-400 group-hover:text-gray-500']"></i>
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
              <i class="fas fa-arrow-left mr-2"></i>
              Về trang chính
            </router-link>
            
            <!-- Notification button -->
            <button class="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-md">
              <i class="fas fa-bell"></i>
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
const FlightForm = defineAsyncComponent(() => import('./formAdminFlight.vue'))
const FlightList = defineAsyncComponent(() => import('./FlightList.vue'))
const FlightStatistics = defineAsyncComponent(() => import('./FlightStatistics.vue'))
const FlightBookings = defineAsyncComponent(() => import('./FlightBookings.vue'))
const FlightSettings = defineAsyncComponent(() => import('./FlightSettings.vue'))

// State
const sidebarOpen = ref(false)
const currentView = ref('FlightForm')

// Component mapping
const components = {
  FlightForm,
  FlightList,
  FlightStatistics,
  FlightBookings,
  FlightSettings
}

// Navigation items
const navigationItems = ref([
  {
    name: 'Đăng chuyến bay',
    component: 'FlightForm',
    title: 'Đăng chuyến bay mới',
    icon: 'fas fa-plus-circle'
  },
  {
    name: 'Danh sách chuyến bay',
    component: 'FlightList',
    title: 'Quản lý chuyến bay',
    icon: 'fas fa-list'
  },
  {
    name: 'Thống kê',
    component: 'FlightStatistics',
    title: 'Thống kê & Báo cáo',
    icon: 'fas fa-chart-bar'
  },
  {
    name: 'Đặt chỗ',
    component: 'FlightBookings',
    title: 'Quản lý đặt chỗ',
    icon: 'fas fa-ticket-alt'
  },
  {
    name: 'Cài đặt',
    component: 'FlightSettings',
    title: 'Cài đặt hệ thống',
    icon: 'fas fa-cog'
  }
])

// Computed
const currentTitle = computed(() => {
  const item = navigationItems.value.find(item => item.component === currentView.value)
  return item ? item.title : 'Dashboard'
})

const currentComponent = computed(() => {
  return components[currentView.value] || FlightForm
})

// Methods
const toggleSidebar = () => {
  sidebarOpen.value = !sidebarOpen.value
}

const selectMenuItem = (item) => {
  currentView.value = item.component
  sidebarOpen.value = false // Close sidebar on mobile after selection
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