<template>
  <div class="flex h-screen bg-gray-100 font-sans">
    <!-- Sidebar -->
    <aside 
      :class="[
        'fixed inset-y-0 left-0 z-30 w-64 bg-gradient-to-b from-white to-blue-50 border-r border-blue-200 transform transition-transform duration-300 ease-in-out lg:translate-x-0 shadow-xl flex flex-col',
        isSidebarOpen ? 'translate-x-0' : '-translate-x-full'
      ]"
    >
      <!-- Brand Header - Nổi bật -->
      <div class="h-16 flex items-center justify-center border-b border-purple-300 bg-gradient-to-r from-pink-500 via-purple-500 via-blue-500 to-cyan-500 relative overflow-hidden">
        <!-- Background decoration -->
        <div class="absolute inset-0 bg-gradient-to-r from-yellow-400/20 to-pink-400/20"></div>
        <div class="absolute -top-5 -right-5 w-16 h-16 bg-gradient-to-br from-yellow-300 to-orange-400 rounded-full opacity-30 animate-pulse"></div>
        <div class="absolute -bottom-3 -left-3 w-12 h-12 bg-gradient-to-br from-green-300 to-blue-400 rounded-full opacity-30 animate-bounce"></div>
        <div class="absolute top-2 left-4 w-8 h-8 bg-gradient-to-br from-red-400 to-pink-500 rounded-full opacity-40 animate-pulse"></div>
        <div class="absolute bottom-3 right-8 w-6 h-6 bg-gradient-to-br from-indigo-400 to-purple-500 rounded-full opacity-50 animate-bounce"></div>
        <div class="absolute top-8 right-2 w-4 h-4 bg-gradient-to-br from-emerald-400 to-teal-500 rounded-full opacity-60 animate-ping"></div>
        
        <!-- Logo and title -->
        <div class="relative flex items-center space-x-3">
          <div class="w-10 h-10 bg-gradient-to-br from-yellow-400 via-red-500 to-pink-500 rounded-xl flex items-center justify-center backdrop-blur-sm logo-hover floating shadow-2xl ring-2 ring-white/50">
            <BusIcon class="w-6 h-6 text-white drop-shadow-lg" />
          </div>
          <div>
            <h1 class="text-xl font-bold bg-gradient-to-r from-white via-yellow-100 to-white bg-clip-text text-transparent drop-shadow-lg">BusManager</h1>
            <p class="text-xs text-yellow-100 -mt-1 font-semibold">✨ Pro Dashboard ✨</p>
          </div>
        </div>
      </div>

      <!-- User Info Section - Trắng sáng -->
      <div class="px-4 py-4 border-b border-blue-100">
        <div class="flex items-center space-x-3 p-3 bg-gradient-to-r from-blue-50 via-indigo-50 to-purple-50 rounded-xl border border-blue-100 shadow-sm hover:shadow-md transition-all duration-300">
          <div class="w-10 h-10 bg-gradient-to-br from-blue-500 via-purple-500 to-pink-500 rounded-full flex items-center justify-center shadow-lg">
            <span class="text-white font-semibold text-sm">A</span>
          </div>
          <div class="flex-1 min-w-0">
            <p class="text-sm font-medium text-gray-800 truncate">Admin User</p>
            <p class="text-xs text-gray-600 truncate">admin@busmanager.com</p>
          </div>
          <div class="flex flex-col items-center">
            <div class="w-2 h-2 bg-green-500 rounded-full status-glow"></div>
            <span class="text-xs text-green-600 font-medium mt-1">Online</span>
          </div>
        </div>
      </div>

      <!-- Main Content Area -->
      <div class="flex-1 overflow-y-auto">
        <!-- Navigation Menu - Trắng sáng -->
        <nav class="mt-4 px-4">
          <div class="mb-3">
            <p class="text-xs font-semibold text-gray-500 uppercase tracking-wider px-3">Quản lý chính</p>
          </div>
          <ul class="space-y-1">
            <li v-for="item in menuItems" :key="item.id">
              <router-link
                :to="{ name: item.routeName }"
                @click="closeSidebarOnMobile"
                :class="[
                  'group flex items-center px-3 py-3 mx-0 rounded-lg transition-all duration-200 relative menu-item transform-hover w-full',
                  $route.name === item.routeName
                    ? 'bg-gradient-to-r from-blue-500 to-purple-500 text-white shadow-lg transform scale-105 active-menu-item'
                    : 'text-gray-700 hover:bg-blue-50 hover:text-blue-700 hover:scale-102',
                ]"
              >
                <!-- Active indicator -->
                <div v-if="$route.name === item.routeName" class="absolute left-0 top-0 bottom-0 w-1 bg-white rounded-r-full"></div>
                
                <!-- Icon container -->
                <div :class="[
                  'flex-shrink-0 w-6 h-6 mr-3 transition-transform duration-200',
                  $route.name === item.routeName ? 'text-white' : 'text-gray-500 group-hover:text-blue-500'
                ]">
                  <component :is="item.icon" class="w-5 h-5" />
                </div>
                
                <!-- Menu text -->
                <span class="text-sm font-medium">{{ item.name }}</span>
                
                <!-- Badge (if needed) -->
                <span v-if="item.badge?.value || item.badge" class="ml-auto inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-blue-500 text-white">
                  {{ item.badge?.value || item.badge }}
                </span>
                
                <!-- Hover indicator -->
                <div class="absolute right-2 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
                  <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
                  </svg>
                </div>
              </router-link>
            </li>
          </ul>

          <!-- Quick Stats Section -->
          <div class="mt-8 px-3">
            <div class="flex items-center justify-between mb-3">
              <p class="text-xs font-semibold text-gray-500 uppercase tracking-wider">Thống kê nhanh</p>
              <button 
                @click="loadStatsData" 
                :disabled="statsData.loading"
                class="text-xs text-blue-600 hover:text-blue-800 disabled:opacity-50"
                title="Refresh stats"
              >
                <svg :class="['w-3 h-3', { 'animate-spin': statsData.loading }]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
                </svg>
              </button>
            </div>
            
            <div class="space-y-2">
              <!-- Total Buses Card -->
              <div class="bg-gradient-to-r from-green-50 to-emerald-50 border border-green-200 rounded-lg p-3 transition-all duration-300 hover:shadow-md">
                <div class="flex items-center justify-between">
                  <div class="flex items-center space-x-2">
                    <div class="w-5 h-5 bg-gradient-to-br from-green-400 to-emerald-500 rounded-md flex items-center justify-center">
                      <BusIcon class="w-3 h-3 text-white" />
                    </div>
                    <span class="text-xs text-gray-600 font-medium">Tổng xe buýt</span>
                  </div>
                  <div class="text-right">
                    <span v-if="statsData.loading" class="text-sm text-gray-400">...</span>
                    <span v-else-if="statsData.error" class="text-sm text-red-500">!</span>
                    <span v-else class="text-sm font-bold text-gray-800">{{ statsData.totalBuses }}</span>
                  </div>
                </div>
                <div class="mt-2 w-full bg-green-100 rounded-full h-1.5">
                  <div 
                    class="bg-gradient-to-r from-green-400 to-emerald-500 h-1.5 rounded-full transition-all duration-1000 ease-out"
                    :style="{ width: statsData.loading ? '20%' : `${Math.min(100, (statsData.totalBuses / 50) * 100)}%` }"
                  ></div>
                </div>
              </div>
              
              <!-- Today's Trips Card -->
              <div class="bg-gradient-to-r from-blue-50 to-indigo-50 border border-blue-200 rounded-lg p-3 transition-all duration-300 hover:shadow-md">
                <div class="flex items-center justify-between">
                  <div class="flex items-center space-x-2">
                    <div class="w-5 h-5 bg-gradient-to-br from-blue-400 to-purple-500 rounded-md flex items-center justify-center">
                      <TicketIcon class="w-3 h-3 text-white" />
                    </div>
                    <span class="text-xs text-gray-600 font-medium">Chuyến hôm nay</span>
                  </div>
                  <div class="text-right">
                    <span v-if="statsData.loading" class="text-sm text-gray-400">...</span>
                    <span v-else-if="statsData.error" class="text-sm text-red-500">!</span>
                    <span v-else class="text-sm font-bold text-gray-800">{{ statsData.todayTrips }}</span>
                  </div>
                </div>
                <div class="mt-2 w-full bg-blue-100 rounded-full h-1.5">
                  <div 
                    class="bg-gradient-to-r from-blue-400 to-purple-500 h-1.5 rounded-full transition-all duration-1000 ease-out"
                    :style="{ width: statsData.loading ? '30%' : `${Math.min(100, (statsData.todayTrips / 20) * 100)}%` }"
                  ></div>
                </div>
              </div>
              
              <!-- Error message -->
              <div v-if="statsData.error" class="text-xs text-red-500 text-center mt-2 p-2 bg-red-50 rounded border border-red-200">
                {{ statsData.error }}
              </div>
            </div>
          </div>
        </nav>
      </div>

      <!-- Footer Section - Sticky to bottom -->
      <div class="p-4 border-t border-blue-100 flex-shrink-0">
        <div class="flex items-center justify-center text-xs text-gray-500 mb-3">
          <div class="flex items-center space-x-1">
            <div class="w-2 h-2 bg-green-500 rounded-full status-glow"></div>
            <span>Online</span>
          </div>
        </div>
        
        <!-- Version info at absolute bottom -->
        <div class="text-center">
          <span class="text-xs text-gray-400 font-medium">v2.1.0</span>
        </div>
      </div>
    </aside>

    <!-- Overlay for mobile when sidebar is open -->
    <div v-if="isSidebarOpen" @click="isSidebarOpen = false" class="fixed inset-0 z-20 bg-black/30 lg:hidden"></div>

    <!-- Main content -->
    <div class="flex-1 flex flex-col overflow-hidden lg:pl-64">
      <header class="h-16 bg-white border-b border-gray-200 flex items-center justify-between px-6 shadow-sm">
        <div class="flex items-center">
          <!-- Hamburger Menu Button -->
          <button @click.stop="isSidebarOpen = !isSidebarOpen" class="lg:hidden mr-4 p-2 rounded-lg hover:bg-gray-100 transition-colors duration-200">
            <div class="space-y-1.5">
              <span :class="['block w-5 h-0.5 bg-slate-600 transition-all duration-300 ease-in-out', { 'rotate-45 translate-y-2': isSidebarOpen }]"></span>
              <span :class="['block w-5 h-0.5 bg-slate-600 transition-all duration-300 ease-in-out', { 'opacity-0': isSidebarOpen }]"></span>
              <span :class="['block w-5 h-0.5 bg-slate-600 transition-all duration-300 ease-in-out', { '-rotate-45 -translate-y-2': isSidebarOpen }]"></span>
            </div>
          </button>
          
          <!-- Page Title with breadcrumb style -->
          <div class="flex items-center space-x-2">
            <div class="hidden sm:flex items-center space-x-2 text-sm text-gray-500">
              <span>Dashboard</span>
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
              </svg>
            </div>
            <h2 class="text-lg font-semibold text-gray-800">
              {{ currentPageTitle }}
            </h2>
          </div>
        </div>
        
        <!-- Header actions -->
        <div class="flex items-center space-x-4">
          <!-- Notifications -->
          <button class="relative p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-lg transition-colors duration-200">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-3.5-3.5M15 17l-3.5-3.5M15 17V4a2 2 0 00-2-2H5a2 2 0 00-2 2v13a2 2 0 002 2h8a2 2 0 002-2z"></path>
            </svg>
            <span class="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full notification-badge"></span>
          </button>
          
          <!-- Settings -->
          <button class="p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-lg transition-colors duration-200">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"></path>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
            </svg>
          </button>
          
          <!-- Home link -->
          <router-link to="/" class="inline-flex items-center px-3 py-2 text-sm font-medium text-blue-600 hover:text-blue-800 hover:bg-blue-50 rounded-lg transition-all duration-200">
            <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"></path>
            </svg>
            Trang chính
          </router-link>
        </div>
      </header>
      
      <main class="flex-1 overflow-x-hidden overflow-y-auto bg-gray-100 p-6">
        <div class="container mx-auto">
          <!-- Router view để hiển thị nested routes -->
          <router-view v-slot="{ Component }" :key="$route.path">
            <component :is="Component" />
          </router-view>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import {
  MapIcon,
  TicketIcon,
  CircleDollarSignIcon,
  BarChart3Icon,
  LayoutGridIcon,
  BusIcon
} from 'lucide-vue-next'

// Import APIs
import { BusAPI } from '@/api/busApi'
import { toast } from '@/utils/notifications'

// Router
const route = useRoute()

// State
const isSidebarOpen = ref(false)

// Stats data
const statsData = ref({
  totalBuses: 0,
  todayTrips: 0,
  loading: false,
  error: null
})

// Computed badges
const busBadge = computed(() => statsData.value.totalBuses > 0 ? statsData.value.totalBuses.toString() : '')
const tripBadge = computed(() => statsData.value.todayTrips > 0 ? statsData.value.todayTrips.toString() : '')

// Menu items with corresponding route names
const menuItems = ref([
  { id: 'route', name: 'Tuyến đường', icon: MapIcon, routeName: 'RouteManagement' },
  { id: 'category', name: 'Loại xe', icon: LayoutGridIcon, routeName: 'BusCategoryManagement' },
  { id: 'bus', name: 'Xe buýt', icon: BusIcon, routeName: 'BusManagement', badge: busBadge },
  { id: 'trip', name: 'Chuyến đi', icon: TicketIcon, routeName: 'TripManagement', badge: tripBadge },
  { id: 'price', name: 'Giá vé', icon: CircleDollarSignIcon, routeName: 'PriceManagement' },
  { id: 'statistics', name: 'Thống kê', icon: BarChart3Icon, routeName: 'Statistics' }
])

// Computed properties
const currentPageTitle = computed(() => {
  const currentItem = menuItems.value.find(item => item.routeName === route.name)
  return currentItem?.name || 'Dashboard'
})

// Load real stats data
const loadStatsData = async () => {
  try {
    // Use BusAPI for better error handling and debugging
         const { BusAPI } = await import('@/api/busApi/bus/api')
     const ownerId = "11" // DEV_USER_ID
     const buses = await BusAPI.getBusesByOwnerId(ownerId)
     
     statsData.value.totalBuses = buses.length
     
     // Load today's trips data
     // TODO: Implement trip counting API when available
     statsData.value.todayTrips = Math.floor(Math.random() * 10) + 1 // Mock data for now
    
  } catch (error) {
    console.error('❌ Error loading stats:', error)
    
         // Use fallback mock data on error
     statsData.value.totalBuses = 0
     statsData.value.todayTrips = 0
    
    // Show user-friendly error
    toast.error('Không thể tải thống kê. Sử dụng dữ liệu mẫu.')
  }
}

// Debug function để test GraphQL manually
const debugGraphQL = async () => {
  try {
    const result = await BusAPI.testSimpleQuery();
    alert('GraphQL test successful! Check console for details.');
  } catch (error) {
    console.error('❌ Manual test failed:', error);
    alert(`GraphQL test failed: ${error.message}`);
  }
}

// Methods
const closeSidebarOnMobile = () => {
  if (window.innerWidth < 1024) { // lg breakpoint
    isSidebarOpen.value = false
  }
}

// Load data on mount
onMounted(() => {
  loadStatsData()
})
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

/* Sidebar entrance animation */
@keyframes slideInLeft {
  from {
    transform: translateX(-100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.sidebar-enter {
  animation: slideInLeft 0.3s ease-out;
}

/* Menu item hover effects */
.menu-item {
  position: relative;
  overflow: hidden;
}

.menu-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(59, 130, 246, 0.1), transparent);
  transition: left 0.5s;
}

.menu-item:hover::before {
  left: 100%;
}

/* Active menu item pulse */
@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(59, 130, 246, 0.4);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(59, 130, 246, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(59, 130, 246, 0);
  }
}

.active-menu-item {
  animation: pulse 2s infinite;
}

/* Stats bars animation */
@keyframes fillBar {
  from {
    width: 0%;
  }
  to {
    width: var(--target-width);
  }
}

.stat-bar {
  animation: fillBar 1.5s ease-out forwards;
}

/* Floating elements */
@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

.floating {
  animation: float 3s ease-in-out infinite;
}

/* Glow effect for online status */
@keyframes glow {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.1);
  }
}

.status-glow {
  animation: glow 2s ease-in-out infinite;
}

/* Brand logo rotation */
@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.logo-hover:hover {
  animation: rotate 0.5s ease-in-out;
}

/* Notification badge pulse */
@keyframes badgePulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

.notification-badge {
  animation: badgePulse 1.5s ease-in-out infinite;
}

/* Gradient text animation */
@keyframes gradientShift {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

.gradient-text {
  background: linear-gradient(-45deg, #3b82f6, #8b5cf6, #06b6d4, #10b981);
  background-size: 400% 400%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: gradientShift 3s ease infinite;
}

/* Hover transform effects */
.transform-hover {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.transform-hover:hover {
  transform: translateY(-2px);
}

/* Mobile overlay fade */
.overlay-enter-active,
.overlay-leave-active {
  transition: opacity 0.3s ease;
}

.overlay-enter-from,
.overlay-leave-to {
  opacity: 0;
}
</style> 