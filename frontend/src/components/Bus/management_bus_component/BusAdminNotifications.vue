<template>
  <!-- 🔔 Bus Admin Notification System -->
  <div class="relative">
    <!-- Notification Button with Badge -->
    <button 
      @click="toggleNotifications"
      class="relative p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-lg transition-colors duration-200"
      :class="{ 'text-blue-600 bg-blue-50': showNotifications }"
    >
      <!-- Bell Icon -->
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
      </svg>
      
      <!-- Dynamic Badge với Animation -->
      <span 
        v-if="unreadCount > 0"
        class="absolute -top-1 -right-1 inline-flex items-center justify-center px-2 py-1 text-xs font-bold leading-none text-white bg-red-600 rounded-full min-w-[18px] h-[18px] notification-badge animate-pulse"
      >
        {{ unreadCount > 99 ? '99+' : unreadCount }}
      </span>

      <!-- Connection Status Indicator -->
      <div 
        :class="[
          'absolute -bottom-1 -right-1 w-3 h-3 rounded-full border-2 border-white',
          isConnected ? 'bg-green-500' : 'bg-red-500'
        ]"
        :title="isConnected ? 'Kết nối WebSocket' : 'Mất kết nối WebSocket'"
      ></div>
    </button>
    
    <!-- Dropdown Panel -->
    <transition name="notification-dropdown">
      <div 
        v-if="showNotifications"
        v-click-outside="closeNotifications"
        class="absolute right-0 mt-2 w-96 bg-white rounded-lg shadow-xl border border-gray-200 z-50"
      >
        <!-- Header -->
        <div class="px-4 py-3 border-b border-gray-200 flex justify-between items-center bg-gray-50 rounded-t-lg">
          <div class="flex items-center space-x-2">
            <h3 class="text-lg font-semibold text-gray-900">🔔 Thông báo Bus</h3>
            <span 
              v-if="unreadCount > 0"
              class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-red-100 text-red-800"
            >
              {{ unreadCount }} mới
            </span>
          </div>
          <div class="flex items-center space-x-2">
            <button 
              @click="markAllAsRead"
              v-if="unreadCount > 0"
              class="text-sm text-blue-600 hover:text-blue-800 font-medium"
            >
              Đọc tất cả
            </button>
            <button 
              @click="closeNotifications"
              class="text-gray-400 hover:text-gray-600"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>
        
        <!-- Notification List -->
        <div class="max-h-80 overflow-y-auto">
          <div 
            v-for="notification in recentNotifications" 
            :key="notification.id"
            :class="[
              'px-4 py-3 border-b border-gray-100 hover:bg-gray-50 cursor-pointer transition-colors duration-200',
              !notification.read ? 'bg-blue-50 border-l-4 border-l-blue-500' : ''
            ]"
            @click="markAsRead(notification.id)"
          >
            <!-- Notification Content -->
            <div class="flex items-start space-x-3">
              <!-- Icon -->
              <div class="flex-shrink-0 mt-1">
                <div class="w-8 h-8 rounded-full flex items-center justify-center" :class="getNotificationIconBg(notification.type)">
                  <span class="text-sm">{{ getNotificationIcon(notification.type) }}</span>
                </div>
              </div>
              
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-gray-900">{{ notification.title }}</p>
                <p class="text-sm text-gray-600 mt-1">{{ notification.message }}</p>
                
                <!-- Additional Details -->
                <div v-if="notification.details" class="mt-2 text-xs text-gray-500 bg-gray-100 rounded p-2">
                  <div v-if="notification.details.routeName" class="flex justify-between">
                    <span>Tuyến:</span>
                    <span class="font-medium">{{ notification.details.routeName }}</span>
                  </div>
                  <div v-if="notification.details.customerName" class="flex justify-between">
                    <span>Khách hàng:</span>
                    <span class="font-medium">{{ notification.details.customerName }}</span>
                  </div>
                  <div v-if="notification.details.amount" class="flex justify-between">
                    <span>Số tiền:</span>
                    <span class="font-medium text-green-600">{{ formatCurrency(notification.details.amount) }}</span>
                  </div>
                  <div v-if="notification.details.seatCount" class="flex justify-between">
                    <span>Số ghế:</span>
                    <span class="font-medium">{{ notification.details.seatCount }}</span>
                  </div>
                </div>
                
                <p class="text-xs text-gray-400 mt-2">{{ formatTime(notification.timestamp) }}</p>
              </div>
              
              <!-- Unread indicator -->
              <div v-if="!notification.read" class="flex-shrink-0 mt-2">
                <div class="w-2 h-2 bg-blue-600 rounded-full"></div>
              </div>
            </div>
          </div>
          
          <!-- Empty state -->
          <div v-if="recentNotifications.length === 0" class="px-4 py-8 text-center text-gray-500">
            <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
            </svg>
            <p class="mt-2">Chưa có thông báo nào</p>
            <p class="text-xs">Bạn sẽ nhận được thông báo khi có đặt vé mới</p>
          </div>
        </div>
        
        <!-- Footer -->
        <div class="px-4 py-3 border-t border-gray-200 text-center bg-gray-50 rounded-b-lg">
          <button 
            @click="showAllNotifications"
            class="text-sm text-blue-600 hover:text-blue-800 font-medium"
          >
            Xem tất cả thông báo →
          </button>
        </div>
      </div>
    </transition>
  </div>

  <!-- Toast Notification cho hiển thị 3s -->
  <transition name="toast-slide">
    <div 
      v-if="toastNotification"
      class="fixed top-4 right-4 z-[9999] bg-white rounded-lg shadow-xl border border-gray-200 p-4 max-w-sm min-w-[300px]"
    >
      <div class="flex items-start space-x-3">
        <!-- Toast Icon -->
        <div class="flex-shrink-0">
          <div class="w-10 h-10 rounded-full flex items-center justify-center" :class="getNotificationIconBg(toastNotification.type)">
            <span class="text-lg">{{ getNotificationIcon(toastNotification.type) }}</span>
          </div>
        </div>
        
        <div class="flex-1">
          <p class="text-sm font-medium text-gray-900">{{ toastNotification.title }}</p>
          <p class="text-sm text-gray-600 mt-1">{{ toastNotification.message }}</p>
          
          <!-- Toast Details -->
          <div v-if="toastNotification.details" class="mt-2 text-xs text-gray-500">
            <span v-if="toastNotification.details.customerName">{{ toastNotification.details.customerName }}</span>
            <span v-if="toastNotification.details.routeName" class="ml-2">• {{ toastNotification.details.routeName }}</span>
            <span v-if="toastNotification.details.amount" class="ml-2 text-green-600 font-medium">{{ formatCurrency(toastNotification.details.amount) }}</span>
          </div>
        </div>
        
        <button 
          @click="dismissToast"
          class="flex-shrink-0 text-gray-400 hover:text-gray-600"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
      
      <!-- Progress bar -->
      <div class="mt-3 w-full bg-gray-200 rounded-full h-1">
        <div 
          class="bg-blue-600 h-1 rounded-full transition-all duration-100"
          :style="{ width: `${toastProgress}%` }"
        ></div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useBusAdminWebSocket } from '@/composables/useBusAdminWebSocket'

// WebSocket connection
const { 
  isConnected, 
  notifications, 
  connect, 
  disconnect,
  markNotificationAsRead,
  markAllNotificationsAsRead 
} = useBusAdminWebSocket()

// UI State
const showNotifications = ref(false)
const toastNotification = ref(null)
const toastProgress = ref(100)
let toastTimer = null
let progressTimer = null

// Click outside directive
const vClickOutside = {
  mounted(el, binding) {
    el.clickOutsideEvent = function(event) {
      if (!(el === event.target || el.contains(event.target))) {
        binding.value()
      }
    }
    document.addEventListener('click', el.clickOutsideEvent)
  },
  unmounted(el) {
    document.removeEventListener('click', el.clickOutsideEvent)
  }
}

// Computed properties
const unreadCount = computed(() => {
  return notifications.value.filter(n => !n.read).length
})

const recentNotifications = computed(() => {
  return notifications.value
    .sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))
    .slice(0, 20) // Hiển thị 20 thông báo gần nhất
})

// Methods
const toggleNotifications = () => {
  showNotifications.value = !showNotifications.value
}

const closeNotifications = () => {
  showNotifications.value = false
}

const markAsRead = (notificationId) => {
  markNotificationAsRead(notificationId)
}

const markAllAsRead = () => {
  markAllNotificationsAsRead()
}

const showAllNotifications = () => {
  // TODO: Navigate to full notifications page
  console.log('🔔 Show all notifications page')
  closeNotifications()
}

// Toast methods
const showToast = (notification) => {
  // Dismiss current toast if exists
  if (toastTimer) {
    clearTimeout(toastTimer)
    clearInterval(progressTimer)
  }
  
  toastNotification.value = notification
  toastProgress.value = 100
  
  // Progress bar animation
  let progress = 100
  progressTimer = setInterval(() => {
    progress -= 100/30 // 3 seconds = 30 intervals of 100ms
    toastProgress.value = Math.max(0, progress)
  }, 100)
  
  // Auto dismiss after 3 seconds
  toastTimer = setTimeout(() => {
    dismissToast()
  }, 3000)
}

const dismissToast = () => {
  toastNotification.value = null
  toastProgress.value = 100
  if (toastTimer) {
    clearTimeout(toastTimer)
    toastTimer = null
  }
  if (progressTimer) {
    clearInterval(progressTimer)
    progressTimer = null
  }
}

// Notification type helpers
const getNotificationIcon = (type) => {
  switch(type) {
    case 'BUS_BOOKING': return '🚌'
    case 'PAYMENT_SUCCESS': return '💰'
    case 'PAYMENT_FAILED': return '❌'
    case 'CANCELLATION': return '🚫'
    case 'SYSTEM_ALERT': return '⚠️'
    default: return '🔔'
  }
}

const getNotificationIconBg = (type) => {
  switch(type) {
    case 'BUS_BOOKING': return 'bg-blue-100 text-blue-600'
    case 'PAYMENT_SUCCESS': return 'bg-green-100 text-green-600'
    case 'PAYMENT_FAILED': return 'bg-red-100 text-red-600'
    case 'CANCELLATION': return 'bg-orange-100 text-orange-600'
    case 'SYSTEM_ALERT': return 'bg-yellow-100 text-yellow-600'
    default: return 'bg-gray-100 text-gray-600'
  }
}

// Utility functions
const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  const now = new Date()
  const diffInMinutes = Math.floor((now - date) / (1000 * 60))
  
  if (diffInMinutes < 1) return 'Vừa xong'
  if (diffInMinutes < 60) return `${diffInMinutes} phút trước`
  if (diffInMinutes < 1440) return `${Math.floor(diffInMinutes / 60)} giờ trước`
  
  return date.toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatCurrency = (amount) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(amount)
}

// Listen for new notifications from WebSocket
const handleNewNotification = (notification) => {
  console.log('🔔 [Admin] New notification received:', notification)
  
  // Show toast for 3 seconds
  showToast(notification)
  
  // Play notification sound (optional)
  try {
    const audio = new Audio('/notification-sound.mp3')
    audio.volume = 0.3
    audio.play().catch(() => {
      // Ignore audio play errors
    })
  } catch (error) {
    // Ignore audio errors
  }
}

// Lifecycle
onMounted(() => {
  // Connect to WebSocket
  connect()
  
  // Listen for new notifications
  window.addEventListener('busAdminNotification', handleNewNotification)
})

onUnmounted(() => {
  // Cleanup
  disconnect()
  window.removeEventListener('busAdminNotification', handleNewNotification)
  
  if (toastTimer) clearTimeout(toastTimer)
  if (progressTimer) clearInterval(progressTimer)
})
</script>

<style scoped>
/* Notification dropdown animations */
.notification-dropdown-enter-active,
.notification-dropdown-leave-active {
  transition: all 0.2s ease;
}

.notification-dropdown-enter-from {
  opacity: 0;
  transform: translateY(-10px) scale(0.95);
}

.notification-dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.95);
}

/* Toast slide animations */
.toast-slide-enter-active,
.toast-slide-leave-active {
  transition: all 0.3s ease;
}

.toast-slide-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.toast-slide-leave-to {
  opacity: 0;
  transform: translateX(100%);
}

/* Notification badge pulse */
.notification-badge {
  animation: badgePulse 2s ease-in-out infinite;
}

@keyframes badgePulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

/* Custom scrollbar for notifications */
.overflow-y-auto::-webkit-scrollbar {
  width: 4px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 2px;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 2px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
