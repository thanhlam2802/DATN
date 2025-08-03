<template>
  <div class="relative">
    <button 
      @click="toggleDropdown" 
      class="relative text-slate-500 hover:text-blue-600 focus:outline-none transition-colors duration-200"
    >
      <i class="fas fa-bell text-xl"></i>
      <span 
        v-if="notificationCount > 0"
        class="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center font-bold"
      >
        {{ notificationCount > 99 ? '99+' : notificationCount }}
      </span>
    </button>

    <div 
      v-if="isOpen" 
      class="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-xl border border-slate-200 z-50"
    >
      <div class="px-4 py-3 border-b border-slate-200">
        <div class="flex items-center justify-between">
          <h3 class="text-lg font-semibold text-slate-800">Thông báo</h3>
          <button 
            @click="markAllAsRead"
            class="text-sm text-blue-600 hover:text-blue-700 font-medium"
            :disabled="notificationCount === 0"
          >
            Đánh dấu đã đọc
          </button>
        </div>
      </div>

      <div class="max-h-96 overflow-y-auto">
        <div v-if="loading" class="px-4 py-8 text-center text-slate-500">
          <i class="fas fa-spinner fa-spin text-2xl mb-2"></i>
          <p class="text-sm">Đang tải thông báo...</p>
        </div>
        
        <div v-else-if="notifications.length === 0" class="px-4 py-8 text-center text-slate-500">
          <i class="fas fa-bell-slash text-2xl mb-2"></i>
          <p class="text-sm">Không có thông báo mới</p>
        </div>
        
        <div v-else class="divide-y divide-slate-100">
          <div 
            v-for="notification in notifications" 
            :key="notification.id"
            class="px-4 py-3 hover:bg-slate-50 transition-colors duration-200 cursor-pointer"
            @click="markAsRead(notification.id)"
          >
            <div class="flex items-start space-x-3">
              <div class="flex-shrink-0">
                                  <div 
                    :class="[
                      'w-8 h-8 rounded-full flex items-center justify-center',
                      notification.type === 'booking' ? 'bg-blue-100 text-blue-600' :
                      notification.type === 'payment' ? 'bg-green-100 text-green-600' :
                      notification.type === 'review' ? 'bg-yellow-100 text-yellow-600' :
                      notification.type === 'cancellation' ? 'bg-red-100 text-red-600' :
                      notification.type === 'hotel-created' ? 'bg-green-100 text-green-600' :
                      notification.type === 'hotel-updated' ? 'bg-blue-100 text-blue-600' :
                      notification.type === 'hotel-deleted' ? 'bg-red-100 text-red-600' :
                      'bg-gray-100 text-gray-600'
                    ]"
                  >
                    <i 
                      :class="[
                        notification.type === 'booking' ? 'fas fa-calendar-check' :
                        notification.type === 'payment' ? 'fas fa-credit-card' :
                        notification.type === 'review' ? 'fas fa-star' :
                        notification.type === 'cancellation' ? 'fas fa-times-circle' :
                        notification.type === 'hotel-created' ? 'fas fa-plus-circle' :
                        notification.type === 'hotel-updated' ? 'fas fa-edit' :
                        notification.type === 'hotel-deleted' ? 'fas fa-trash' :
                        'fas fa-info-circle'
                      ]"
                      class="text-sm"
                    ></i>
                  </div>
              </div>
              
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-slate-900 line-clamp-2">
                  {{ notification.title }}
                </p>
                <p class="text-xs text-slate-500 mt-1">
                  {{ notification.message }}
                </p>
                <p class="text-xs text-slate-400 mt-1">
                  {{ formatTime(notification.timestamp) }}
                </p>
              </div>
              
              <div v-if="!notification.read" class="flex-shrink-0">
                <div class="w-2 h-2 bg-blue-500 rounded-full"></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="notifications.length > 0" class="px-4 py-3 border-t border-slate-200">
        <button 
          @click="viewAllNotifications"
          class="w-full text-center text-sm text-blue-600 hover:text-blue-700 font-medium"
        >
          Xem tất cả thông báo
        </button>
      </div>
    </div>

    <div 
      v-if="isOpen" 
      @click="closeDropdown"
      class="fixed inset-0 z-40"
    ></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import SockJS from "sockjs-client/dist/sockjs.min.js";
import Stomp from "stompjs";

const isOpen = ref(false);
const loading = ref(true);
const notifications = ref([]);
const stompClient = ref(null);
const isConnected = ref(false);

const notificationCount = computed(() => {
  return notifications.value.filter(n => !n.read).length;
});

const connectWebSocket = () => {
  try {
    console.log('Connecting to WebSocket for notifications...');
    const socket = new SockJS('http://localhost:8080/ws');
    stompClient.value = Stomp.over(socket);
    
    stompClient.value.connect({}, (frame) => {
      console.log('Connected to WebSocket for notifications:', frame);
      isConnected.value = true;
      
      stompClient.value.subscribe('/topic/admin/hotel/actions', (response) => {
        handleHotelActionNotification(JSON.parse(response.body));
      });
      
      loadNotificationsFromStorage();
      loading.value = false;
    }, (error) => {
      console.error('WebSocket connection error:', error);
      loadNotificationsFromStorage();
      loading.value = false;
    });
  } catch (error) {
    console.error('Error connecting to WebSocket:', error);
    loadNotificationsFromStorage();
    loading.value = false;
  }
};

const handleHotelActionNotification = (data) => {
  console.log('Received hotel action notification:', data);
  let notification;
  
  switch (data.type) {
    case 'HOTEL_CREATED':
      notification = {
        id: Date.now(),
        type: 'hotel-created',
        title: 'Khách sạn mới',
        message: `Khách sạn "${data.hotelName}" đã được tạo bởi ${data.userName}`,
        timestamp: data.timestamp || Date.now(),
        read: false
      };
      break;
    case 'HOTEL_UPDATED':
      notification = {
        id: Date.now(),
        type: 'hotel-updated',
        title: 'Khách sạn đã cập nhật',
        message: `Khách sạn "${data.hotelName}" đã được cập nhật bởi ${data.userName}`,
        timestamp: data.timestamp || Date.now(),
        read: false
      };
      break;
    case 'HOTEL_DELETED':
      notification = {
        id: Date.now(),
        type: 'hotel-deleted',
        title: 'Khách sạn đã xóa',
        message: `Khách sạn "${data.hotelName}" đã được xóa bởi ${data.userName}`,
        timestamp: data.timestamp || Date.now(),
        read: false
      };
      break;
    default:
      return;
  }
  
  const existingNotification = notifications.value.find(n => 
    n.type === notification.type && 
    n.title === notification.title && 
    n.message === notification.message &&
    Math.abs(new Date(n.timestamp) - new Date(notification.timestamp)) < 5000
  );
  
  if (existingNotification) {
    console.log('Hotel action notification already exists, skipping...');
    return;
  }
  
  notifications.value.unshift(notification);
  
  if (notifications.value.length > 50) {
    notifications.value = notifications.value.slice(0, 50);
  }
  
  saveNotificationsToStorage();
  
  window.dispatchEvent(new CustomEvent('notificationsUpdated', { 
    detail: { notifications: notifications.value, nextId: Date.now() }
  }));
};

const loadNotificationsFromStorage = () => {
  try {
    const stored = localStorage.getItem('hotelAdminNotifications');
    if (stored) {
      const parsed = JSON.parse(stored);
      let storedNotifications = parsed.notifications || [];

      const sevenDaysAgo = new Date();
      sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7);

      storedNotifications = storedNotifications.filter(notification => {
        const notificationDate = new Date(notification.timestamp);
        return notificationDate > sevenDaysAgo;
      });

      notifications.value = storedNotifications;

      if (storedNotifications.length !== parsed.notifications?.length) {
        saveNotificationsToStorage();
      }
    }
  } catch (e) {
    console.error('Error loading notifications from storage:', e);
  }
};

const saveNotificationsToStorage = () => {
  try {
    const data = {
      notifications: notifications.value,
      nextId: Date.now()
    };
    localStorage.setItem('hotelAdminNotifications', JSON.stringify(data));
  } catch (e) {
    console.error('Error saving notifications to storage:', e);
  }
};

const toggleDropdown = () => {
  isOpen.value = !isOpen.value;
};

const closeDropdown = () => {
  isOpen.value = false;
};

const markAsRead = (id) => {
  const notification = notifications.value.find(n => n.id === id);
  if (notification && !notification.read) {
    notification.read = true;
    saveNotificationsToStorage();
  }
};

const markAllAsRead = () => {
  notifications.value.forEach(n => n.read = true);
  saveNotificationsToStorage();
};

const viewAllNotifications = () => {
  console.log('View all notifications');
  closeDropdown();
};

const formatTime = (timestamp) => {
  const date = new Date(timestamp);
  const now = new Date();
  const diff = now - date;
  const minutes = Math.floor(diff / (1000 * 60));
  const hours = Math.floor(diff / (1000 * 60 * 60));
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));

  if (minutes < 1) return 'Vừa xong';
  if (minutes < 60) return `${minutes} phút trước`;
  if (hours < 24) return `${hours} giờ trước`;
  if (days < 7) return `${days} ngày trước`;
  
  return date.toLocaleDateString('vi-VN');
};

const handleNotificationsUpdate = (event) => {
  if (event.detail) {
    const newNotifications = event.detail.notifications || [];
    const currentIds = notifications.value.map(n => n.id);
    
    const newNotificationsToAdd = newNotifications.filter(n => !currentIds.includes(n.id));
    
    if (newNotificationsToAdd.length > 0) {
      notifications.value.unshift(...newNotificationsToAdd);
      
      if (notifications.value.length > 50) {
        notifications.value = notifications.value.slice(0, 50);
      }
    } else {
      notifications.value.forEach(notification => {
        const updatedNotification = newNotifications.find(n => n.id === notification.id);
        if (updatedNotification) {
          notification.read = updatedNotification.read;
        }
      });
    }
  }
};

const handleClickOutside = (event) => {
  if (isOpen.value && !event.target.closest('.relative')) {
    closeDropdown();
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
  connectWebSocket();
  
  window.addEventListener('notificationsUpdated', handleNotificationsUpdate);
  
  if ('Notification' in window && Notification.permission === 'default') {
    Notification.requestPermission();
  }
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
  window.removeEventListener('notificationsUpdated', handleNotificationsUpdate);
  if (stompClient.value) {
    stompClient.value.disconnect();
  }
});
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style> 