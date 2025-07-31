<template>
  <div class="w-80 bg-white rounded-lg shadow-sm p-4 border border-slate-200">
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

    <div class="space-y-3 max-h-96 overflow-y-auto">
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

    <div class="mt-4 pt-3 border-t border-slate-200">
      <div class="flex items-center justify-between text-xs">
        <span class="text-slate-500">Trạng thái kết nối:</span>
        <div class="flex items-center gap-2">
          <div class="w-2 h-2 rounded-full" :class="isConnected ? 'bg-green-500' : 'bg-red-500'"></div>
          <span :class="isConnected ? 'text-green-600' : 'text-red-600'">
            {{ isConnected ? 'Đã kết nối' : 'Đang kết nối...' }}
          </span>
        </div>
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
    const stored = localStorage.getItem('adminNotifications');
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
    localStorage.setItem('adminNotifications', JSON.stringify(data));
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

  if (window.$toast) {
    window.$toast(message, type === 'payment' ? 'success' : type === 'cancellation' ? 'error' : 'info');
  }
};

const removeNotification = (id) => {
  const index = notifications.value.findIndex(n => n.id === id);
  if (index > -1) {
    notifications.value.splice(index, 1);
    saveNotificationsToStorage();
  }
};

const clearAllNotifications = () => {
  notifications.value = [];
  saveNotificationsToStorage();
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
  stompClient.value = Stomp.over(socket);
  stompClient.value.debug = null;

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

const disconnectWebSocket = () => {
  if (stompClient.value && isConnected.value) {
    stompClient.value.disconnect();
    console.log("WebSocket Disconnected from Admin Dashboard.");
    isConnected.value = false;
  }
};

onMounted(() => {
  loadNotificationsFromStorage();

  connectWebSocket();

  if (notifications.value.length === 0) {
    setTimeout(() => {
      if (!isConnected.value) {
        addNotification('booking', 'Đặt phòng mới', 'Khách hàng Nguyễn Văn A vừa đặt phòng tại Grand Hotel');
      }
    }, 2000);

    setTimeout(() => {
      if (!isConnected.value) {
        addNotification('payment', 'Thanh toán thành công', 'Đơn hàng #12345 đã được thanh toán');
      }
    }, 4000);

    setTimeout(() => {
      if (!isConnected.value) {
        addNotification('review', 'Đánh giá mới', 'Khách hàng đã đánh giá 5 sao cho Seaside Resort');
      }
    }, 6000);
  }
});

onUnmounted(() => {
  disconnectWebSocket();
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
</style>