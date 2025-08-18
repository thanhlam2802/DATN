<template>
     <div class="w-80 bg-white rounded-lg shadow-sm p-4 border border-slate-200" :style="notifications.length <= 3 ? 'height: fit-content;' : 'height: 1000px;'">
    <div id="toast-container" class="fixed top-4 right-4 z-50 space-y-2"></div>
    
    <div class="flex items-center justify-between mb-4">
      <div class="flex items-center gap-2">
        <h3 class="font-semibold text-slate-800 text-sm">Thông Báo</h3>
        <span v-if="notifications.length > 0" class="bg-red-500 text-white text-xs px-2 py-1 rounded-full">
          {{ notifications.length }}
        </span>
      </div>
      <div class="flex gap-2">
        <button @click="clearOldNotifications" class="text-gray-500 text-xs hover:text-gray-700"
          :disabled="notifications.length === 0">
          Xóa cũ
        </button>
        <button @click="clearAllNotifications" class="text-blue-500 text-xs hover:text-blue-700"
          :disabled="notifications.length === 0">
          Xóa tất cả
        </button>
      </div>
    </div>

         <div class="space-y-3" :style="notifications.length <= 3 ? 'height: auto;' : 'height: calc(100% - 60px); overflow-y: auto;'">
      <div v-for="notification in notifications" :key="notification.id"
        class="flex items-start space-x-3 p-3 rounded-lg transition-all duration-200 hover:shadow-sm animate-fade-in"
        :class="getNotificationClass(notification.type)">
        <div class="w-2 h-2 rounded-full mt-2 flex-shrink-0" :class="getNotificationDotClass(notification.type)"></div>
        <div class="flex-1 min-w-0">
          <p class="text-xs font-medium text-slate-800">{{ notification.title }}</p>
          <p class="text-xs text-slate-600 mt-1 break-words">{{ notification.message }}</p>
          <p class="text-xs text-slate-400 mt-1">{{ formatTime(notification.timestamp) }}</p>
        </div>
        <button @click="removeNotification(notification.id)" class="text-slate-400 hover:text-slate-600 text-xs ml-2">
          <i class="fas fa-times"></i>
        </button>
      </div>

      <div v-if="notifications.length === 0" class="text-center text-slate-400 text-xs py-8">
        <i class="fas fa-bell text-2xl mb-2"></i>
        <p>Chưa có thông báo nào</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import SockJS from "sockjs-client/dist/sockjs.min.js";
import Stomp from "stompjs";

const notifications = ref([]);
const isConnected = ref(false);
const stompClient = ref(null);
const notificationId = ref(0);

const loadNotificationsFromStorage = () => {
  try {
    const stored = localStorage.getItem('hotelAdminNotifications');
    if (stored) {
      const parsed = JSON.parse(stored);
      let storedNotifications = parsed.notifications || [];
      notificationId.value = parsed.nextId || 0;

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
      nextId: notificationId.value
    };
    localStorage.setItem('hotelAdminNotifications', JSON.stringify(data));
  } catch (e) {
    console.error('Error saving notifications to storage:', e);
  }
};

const getNotificationClass = (type) => {
  switch (type) {
    case 'booking':
      return 'bg-blue-50 hover:bg-blue-100';
    case 'payment':
      return 'bg-green-50 hover:bg-green-100';
    case 'review':
      return 'bg-yellow-50 hover:bg-yellow-100';
    case 'cancellation':
      return 'bg-red-50 hover:bg-red-100';
    case 'hotel-created':
      return 'bg-green-50 hover:bg-green-100';
    case 'hotel-updated':
      return 'bg-blue-50 hover:bg-blue-100';
    case 'hotel-deleted':
      return 'bg-red-50 hover:bg-red-100';
    case 'system':
      return 'bg-gray-50 hover:bg-gray-100';
    default:
      return 'bg-blue-50 hover:bg-blue-100';
  }
};

const getNotificationDotClass = (type) => {
  switch (type) {
    case 'booking':
      return 'bg-blue-500';
    case 'payment':
      return 'bg-green-500';
    case 'review':
      return 'bg-yellow-500';
    case 'cancellation':
      return 'bg-red-500';
    case 'hotel-created':
      return 'bg-green-500';
    case 'hotel-updated':
      return 'bg-blue-500';
    case 'hotel-deleted':
      return 'bg-red-500';
    case 'system':
      return 'bg-gray-500';
    default:
      return 'bg-blue-500';
  }
};

const formatTime = (timestamp) => {
  const now = new Date();
  const time = new Date(timestamp);
  const diffMs = now - time;
  const diffMins = Math.floor(diffMs / 60000);
  const diffHours = Math.floor(diffMins / 60);
  const diffDays = Math.floor(diffHours / 24);

  if (diffMins < 1) return 'Vừa xong';
  if (diffMins < 60) return `${diffMins} phút trước`;
  if (diffHours < 24) return `${diffHours} giờ trước`;
  if (diffDays < 7) return `${diffDays} ngày trước`;
  return time.toLocaleDateString('vi-VN');
};

const addNotification = (type, title, message) => {
  const existingNotification = notifications.value.find(n => 
    n.type === type && 
    n.title === title && 
    n.message === message &&
    Math.abs(new Date(n.timestamp) - new Date()) < 5000
  );
  
  if (existingNotification) {
    console.log('Notification already exists, skipping...');
    return;
  }

  const notification = {
    id: ++notificationId.value,
    type,
    title,
    message,
    timestamp: new Date()
  };

  notifications.value.unshift(notification);

  if (notifications.value.length > 50) {
    notifications.value = notifications.value.slice(0, 50);
  }

  saveNotificationsToStorage();
  window.dispatchEvent(new CustomEvent('notificationsUpdated', { 
    detail: { notifications: notifications.value, nextId: notificationId.value }
  }));

  showWindowNotification(type, title, message);
};

const removeNotification = (id) => {
  const index = notifications.value.findIndex(n => n.id === id);
  if (index > -1) {
    notifications.value.splice(index, 1);
    saveNotificationsToStorage();
    
    window.dispatchEvent(new CustomEvent('notificationsUpdated', { 
      detail: { notifications: notifications.value, nextId: notificationId.value }
    }));
    
    if (window.$toast) {
      window.$toast('Đã xóa thông báo', 'success');
    } else {
      showCustomToast('system', 'Thông báo', 'Đã xóa thông báo');
    }
  }
};

const clearAllNotifications = () => {
  notifications.value = [];
  notificationId.value = 0;
  saveNotificationsToStorage();
  window.dispatchEvent(new CustomEvent('notificationsUpdated', { 
    detail: { notifications: [], nextId: 0 }
  }));
  
  if (window.$toast) {
    window.$toast('Đã xóa tất cả thông báo', 'success');
  } else {
    showCustomToast('system', 'Thông báo', 'Đã xóa tất cả thông báo');
  }
};

const clearOldNotifications = () => {
  const oneDayAgo = new Date();
  oneDayAgo.setDate(oneDayAgo.getDate() - 1);

  const oldCount = notifications.value.length;
  notifications.value = notifications.value.filter(notification => {
    const notificationDate = new Date(notification.timestamp);
    return notificationDate > oneDayAgo;
  });

  if (oldCount !== notifications.value.length) {
    saveNotificationsToStorage();
    if (window.$toast) {
      window.$toast(`Đã xóa ${oldCount - notifications.value.length} thông báo cũ`, 'info');
    }
  }
};

const connectWebSocket = () => {
  if (stompClient.value && isConnected.value) return;

  const socket = new SockJS("http://localhost:8080/ws");
  stompClient.value = Stomp.over(() => socket);
  stompClient.value.debug = function (str) {
    // Silent debug
  };

  stompClient.value.connect({}, (frame) => {
    console.log("WebSocket Connected for Admin Dashboard:", frame);
    isConnected.value = true;

    stompClient.value.subscribe('/topic/admin/hotel/bookings', (response) => {
      const data = JSON.parse(response.body);
      addNotification('booking', 'Đặt phòng mới',
        `Khách hàng ${data.customerName} vừa đặt ${data.rooms} phòng tại ${data.hotelName}`);
    });

    stompClient.value.subscribe('/topic/admin/hotel/payments', (response) => {
      const data = JSON.parse(response.body);
      addNotification('payment', 'Thanh toán thành công',
        `Đơn hàng #${data.orderId} đã được thanh toán ${data.amount} VND`);
    });

    stompClient.value.subscribe('/topic/admin/hotel/reviews', (response) => {
      const data = JSON.parse(response.body);
      addNotification('review', 'Đánh giá mới',
        `Khách hàng đã đánh giá ${data.rating} sao cho ${data.hotelName}`);
    });

        stompClient.value.subscribe('/topic/admin/hotel/cancellations', (response) => {
      const data = JSON.parse(response.body);
      if (data.type === 'ORDER_EXPIRED') {
        addNotification('cancellation', 'Hết thời gian chờ', 
          `Đơn hàng #${data.orderId} đã hết thời gian chờ và bị hủy`);
      } else {
        addNotification('cancellation', 'Hủy đặt phòng', 
          `Đơn hàng #${data.orderId} đã bị hủy bởi khách hàng`);
      }
    });

    stompClient.value.subscribe('/topic/admin/hotel/system', (response) => {
      const data = JSON.parse(response.body);
      addNotification('system', data.title || 'Thông báo hệ thống', data.message);
    });

    stompClient.value.subscribe('/topic/admin/hotel/actions', (response) => {
      handleHotelActionNotification(JSON.parse(response.body));
    });

    stompClient.value.send('/app/admin/hotel/status', {}, JSON.stringify({
      action: 'ADMIN_ONLINE',
      timestamp: new Date().toISOString()
    }));
  }, (error) => {
    console.error("WebSocket connection failed:", error);
    isConnected.value = false;
    setTimeout(connectWebSocket, 5000);
  });
};

const handleHotelActionNotification = (data) => {
  console.log('Received hotel action notification:', data);
  let notification;
  switch (data.type) {
    case 'HOTEL_CREATED':
      notification = {
        id: ++notificationId.value,
        type: 'hotel-created',
        title: 'Khách sạn mới',
        message: `Khách sạn "${data.hotelName}" đã được tạo bởi ${data.userName}`,
        timestamp: data.timestamp || Date.now()
      };
      break;
    case 'HOTEL_UPDATED':
      notification = {
        id: ++notificationId.value,
        type: 'hotel-updated',
        title: 'Khách sạn đã cập nhật',
        message: `Khách sạn "${data.hotelName}" đã được cập nhật bởi ${data.userName}`,
        timestamp: data.timestamp || Date.now()
      };
      break;
    case 'HOTEL_DELETED':
      notification = {
        id: ++notificationId.value,
        type: 'hotel-deleted',
        title: 'Khách sạn đã xóa',
        message: `Khách sạn "${data.hotelName}" đã được xóa bởi ${data.userName}`,
        timestamp: data.timestamp || Date.now()
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
    detail: { notifications: notifications.value, nextId: notificationId.value }
  }));
  
  showWindowNotification(notification.type, notification.title, notification.message);
};

const disconnectWebSocket = () => {
  if (stompClient.value && isConnected.value) {
    stompClient.value.disconnect();
    console.log("WebSocket Disconnected from Admin Dashboard.");
    isConnected.value = false;
  }
};

const showWindowNotification = (type, title, message) => {
  showDesktopNotification(type, title, message);
  
  sendPushNotification(type, title, message);
};

const showDesktopNotification = (type, title, message) => {
  if (!("Notification" in window)) {
    console.log("This browser does not support desktop notification");
    return;
  }

  if (Notification.permission === "granted") {
    createDesktopNotification(type, title, message);
  } else if (Notification.permission !== "denied") {
    Notification.requestPermission().then((permission) => {
      if (permission === "granted") {
        createDesktopNotification(type, title, message);
      }
    });
  }
};

const sendPushNotification = async (type, title, message) => {
  try {
    if (!('serviceWorker' in navigator) || !('PushManager' in window)) {
      console.log('Push notifications are not supported');
      return;
    }

    const registration = await navigator.serviceWorker.register('/sw.js');
    
    const permission = await Notification.requestPermission();
    if (permission !== 'granted') {
      console.log('Push notification permission denied');
      return;
    }

    let subscription = await registration.pushManager.getSubscription();
    if (!subscription) {
      subscription = await registration.pushManager.subscribe({
        userVisibleOnly: true,
        applicationServerKey: urlBase64ToUint8Array('YOUR_VAPID_PUBLIC_KEY') 
      });
    }

    await registration.showNotification(title, {
      body: message,
      icon: getNotificationIcon(type),
      badge: '/favicon.ico',
      tag: 'hotel-admin-notification',
      requireInteraction: false,
      silent: false,
      data: {
        type: type,
        timestamp: new Date().toISOString()
      },
      actions: [
        {
          action: 'view',
          title: 'Xem chi tiết',
          icon: '/icons/view.png'
        },
        {
          action: 'dismiss',
          title: 'Đóng',
          icon: '/icons/close.png'
        }
      ]
    });

  } catch (error) {
    console.error('Error sending push notification:', error);
  }
};

const showToastNotification = (type, title, message) => {
  if (window.$toast) {
    const toastType = type === 'payment' ? 'success' : 
                     type === 'cancellation' ? 'error' : 
                     type === 'review' ? 'warning' : 'info';
    window.$toast(message, toastType);
  } else {
    showCustomToast(type, title, message);
  }
};

const urlBase64ToUint8Array = (base64String) => {
  const padding = '='.repeat((4 - base64String.length % 4) % 4);
  const base64 = (base64String + padding)
    .replace(/-/g, '+')
    .replace(/_/g, '/');

  const rawData = window.atob(base64);
  const outputArray = new Uint8Array(rawData.length);

  for (let i = 0; i < rawData.length; ++i) {
    outputArray[i] = rawData.charCodeAt(i);
  }
  return outputArray;
};

const createNotification = (type, title, message) => {
  const notification = new Notification(title, {
    body: message,
    icon: getNotificationIcon(type),
    badge: '/favicon.ico',
    tag: 'hotel-admin-notification',
    requireInteraction: false,
    silent: false
  });

  setTimeout(() => {
    notification.close();
  }, 5000);

  notification.onclick = () => {
    window.focus();
    notification.close();
  };
};

const getNotificationIcon = (type) => {
  switch (type) {
    case 'booking':
      return '/icons/booking.png';
    case 'payment':
      return '/icons/payment.png'; 
    case 'review':
      return '/icons/review.png'; 
    case 'cancellation':
      return '/icons/cancellation.png'; 
    case 'hotel-created':
      return '/icons/hotel-created.png'; 
    case 'hotel-updated':
      return '/icons/hotel-updated.png'; 
    case 'hotel-deleted':
      return '/icons/hotel-deleted.png'; 
    case 'system':
      return '/icons/system.png'; 
    default:
      return '/favicon.ico';
  }
};

const showCustomToast = (type, title, message) => {
  let toastContainer = document.getElementById('toast-container');
  if (!toastContainer) {
    createToastContainer();
    toastContainer = document.getElementById('toast-container');
  }

  const toast = document.createElement('div');
  const toastId = 'toast-' + Date.now();
  toast.id = toastId;
  
  const bgColor = type === 'payment' ? 'bg-green-600' : 
                  type === 'cancellation' ? 'bg-red-600' : 
                  type === 'review' ? 'bg-yellow-600' : 
                  type === 'booking' ? 'bg-blue-600' : 
                  type === 'hotel-created' ? 'bg-green-600' : 
                  type === 'hotel-updated' ? 'bg-blue-600' : 
                  type === 'hotel-deleted' ? 'bg-red-600' : 'bg-gray-600';
  
  const icon = type === 'payment' ? 'fas fa-check-circle' : 
               type === 'cancellation' ? 'fas fa-times-circle' : 
               type === 'review' ? 'fas fa-star' : 
               type === 'booking' ? 'fas fa-calendar-check' : 
               type === 'hotel-created' ? 'fas fa-plus-circle' : 
               type === 'hotel-updated' ? 'fas fa-edit' : 
               type === 'hotel-deleted' ? 'fas fa-trash' : 'fas fa-bell';

  toast.className = `${bgColor} text-white p-4 rounded-lg shadow-xl border border-white/20 max-w-sm transform transition-all duration-300 translate-x-full opacity-0 backdrop-blur-sm`;
  toast.innerHTML = `
    <div class="flex items-start">
      <div class="flex-shrink-0">
        <i class="${icon} text-lg text-white"></i>
      </div>
      <div class="ml-3 flex-1">
        <p class="text-sm font-semibold text-white">${title}</p>
        <p class="text-sm text-white/90 mt-1">${message}</p>
      </div>
      <div class="ml-4 flex-shrink-0">
        <button class="text-white hover:text-gray-200 close-toast-btn transition-colors">
          <i class="fas fa-times"></i>
        </button>
      </div>
    </div>
  `;

  toastContainer.appendChild(toast);

  const closeBtn = toast.querySelector('.close-toast-btn');
  closeBtn.addEventListener('click', () => {
    removeToast(toast);
  });

  setTimeout(() => {
    toast.classList.remove('translate-x-full', 'opacity-0');
  }, 100);

  setTimeout(() => {
    removeToast(toast);
  }, 5000);
};

const removeToast = (toast) => {
  if (toast && toast.parentNode) {
    toast.classList.add('translate-x-full', 'opacity-0');
    setTimeout(() => {
      if (toast && toast.parentNode) {
        toast.remove();
      }
    }, 300);
  }
};

function createDesktopNotification(type, title, message) {
  const icon = '/favicon.ico';
  new Notification(title, {
    body: message,
    icon,
    tag: 'hotel-admin-notification',
    requireInteraction: false
  });
}

onMounted(() => {
  loadNotificationsFromStorage();

  createToastContainer();

  connectWebSocket();

  window.addEventListener('storage', handleStorageChange);
  
  window.addEventListener('notificationsUpdated', handleNotificationsUpdate);
});

const createToastContainer = () => {
  let toastContainer = document.getElementById('toast-container');
  
  if (!toastContainer) {
    toastContainer = document.createElement('div');
    toastContainer.id = 'toast-container';
    toastContainer.className = 'fixed top-4 right-4 z-50 space-y-2';
    document.body.appendChild(toastContainer);
  }
};

const handleStorageChange = (event) => {
  if (event.key === 'hotelAdminNotifications' && event.newValue) {
    try {
      const parsed = JSON.parse(event.newValue);
      notifications.value = parsed.notifications || [];
      notificationId.value = parsed.nextId || 0;
    } catch (e) {
      console.error('Error parsing storage change:', e);
    }
  }
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
    
    notificationId.value = event.detail.nextId || 0;
  }
};

onUnmounted(() => {
  disconnectWebSocket();
  window.removeEventListener('storage', handleStorageChange);
  window.removeEventListener('notificationsUpdated', handleNotificationsUpdate);
});
</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

#toast-container {
  pointer-events: none;
}

#toast-container > div {
  pointer-events: auto;
}

@media (max-width: 768px) {
  #toast-container {
    top: 1rem;
    right: 1rem;
    left: 1rem;
  }
  
  #toast-container > div {
    max-width: none;
  }
}
</style>