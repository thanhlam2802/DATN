<template>
  <div class="bg-white rounded-lg shadow-sm p-4 border border-slate-200">
    <div class="flex items-center justify-between mb-4">
      <div class="flex items-center gap-2">
        <h3 class="font-semibold text-slate-800 text-sm">Push Notifications</h3>
        <div class="w-2 h-2 rounded-full" :class="isSubscribed ? 'bg-green-500' : 'bg-red-500'"></div>
      </div>
    </div>

    <div class="space-y-3">
      <div v-if="!isSupported" class="text-red-500 text-xs">
        <i class="fas fa-exclamation-triangle mr-1"></i>
        {{ errorMessage }}
      </div>

      <div v-else-if="!isSubscribed" class="text-center">
        <p class="text-xs text-slate-600 mb-3">
          Nhận thông báo ngay cả khi không mở trang web
        </p>
        <button 
          @click="subscribeToPushNotifications"
          :disabled="isLoading"
          class="bg-blue-500 hover:bg-blue-600 disabled:bg-gray-400 text-white px-4 py-2 rounded-lg text-xs transition-colors"
        >
          <i v-if="isLoading" class="fas fa-spinner fa-spin mr-1"></i>
          <i v-else class="fas fa-bell mr-1"></i>
          {{ isLoading ? 'Đang đăng ký...' : 'Bật thông báo' }}
        </button>
      </div>

      <div v-else class="space-y-2">
        <div class="flex items-center justify-between text-xs">
          <span class="text-green-600">
            <i class="fas fa-check-circle mr-1"></i>
            Đã bật thông báo
          </span>
          <button 
            @click="unsubscribeFromPushNotifications"
            :disabled="isLoading"
            class="text-red-500 hover:text-red-700 disabled:text-gray-400 text-xs"
          >
            <i v-if="isLoading" class="fas fa-spinner fa-spin mr-1"></i>
            <i v-else class="fas fa-times mr-1"></i>
            Tắt thông báo
          </button>
        </div>

        <div class="text-xs text-slate-500">
          <p>Bạn sẽ nhận được thông báo khi:</p>
          <ul class="list-disc list-inside mt-1 space-y-1">
            <li>Có đặt phòng mới</li>
            <li>Có thanh toán thành công</li>
            <li>Có đánh giá mới</li>
            <li>Có hủy đặt phòng</li>
          </ul>
        </div>

        <div class="mt-3 p-2 bg-blue-50 rounded text-xs text-blue-700">
          <i class="fas fa-info-circle mr-1"></i>
          Thông báo sẽ hiển thị ngay cả khi trình duyệt đóng
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { VAPID_PUBLIC_KEY, urlBase64ToUint8Array, isValidVapidKey } from '@/utils/vapidKeys';
import pushNotificationApi from '@/api/pushNotificationApi';
import { getAccessToken } from '@/services/TokenService.js';

const isSupported = ref(false);
const isSubscribed = ref(false);
const isLoading = ref(false);
const errorMessage = ref('');

onMounted(async () => {
  console.log('PushNotificationManager mounted');
  
  const token = getAccessToken();
  let userId = null;
  
  if (token) {
    try {
      const payloadBase64 = token.split(".")[1];
      const decodedJson = atob(
        payloadBase64.replace(/-/g, "+").replace(/_/g, "/")
      );
      const userData = JSON.parse(decodedJson);
      userId = userData.userId || userData.sub;
      console.log('User data from token:', userData);
      console.log('UserId from token:', userId);
    } catch (error) {
      console.error('Error parsing token:', error);
    }
  }
  
  console.log('Final userId:', userId);
  console.log('VAPID_PUBLIC_KEY:', VAPID_PUBLIC_KEY);
  
  await checkPushNotificationSupport();
  await checkSubscriptionStatus();
});

const checkPushNotificationSupport = async () => {
  isSupported.value = 'serviceWorker' in navigator && 'PushManager' in window;
  
  if (!isSupported.value) {
    errorMessage.value = 'Trình duyệt không hỗ trợ Push Notifications';
    return;
  }
  
  if (!isValidVapidKey(VAPID_PUBLIC_KEY)) {
    errorMessage.value = 'VAPID keys chưa được cấu hình. Vui lòng liên hệ admin.';
    return;
  }
};

const checkSubscriptionStatus = async () => {
  if (!isSupported.value) return;

  try {
    const registration = await navigator.serviceWorker.ready;
    const subscription = await registration.pushManager.getSubscription();
    isSubscribed.value = !!subscription;
  } catch (error) {
    console.error('Error checking subscription status:', error);
  }
};

const subscribeToPushNotifications = async () => {
  if (!isSupported.value) return;

  isLoading.value = true;
  console.log('Starting push notification subscription...');
  
  try {
    console.log('Registering service worker...');
    const registration = await navigator.serviceWorker.register('/sw.js');
    console.log('Service worker registered:', registration);
    
    console.log('Requesting notification permission...');
    const permission = await Notification.requestPermission();
    console.log('Notification permission:', permission);
    
    if (permission !== 'granted') {
      alert('Cần cấp quyền thông báo để sử dụng tính năng này');
      return;
    }

    console.log('Creating push subscription...');
    console.log('VAPID_PUBLIC_KEY:', VAPID_PUBLIC_KEY);
    const subscription = await registration.pushManager.subscribe({
      userVisibleOnly: true,
      applicationServerKey: urlBase64ToUint8Array(VAPID_PUBLIC_KEY)
    });
    console.log('Push subscription created:', subscription);

    console.log('Sending subscription to server...');
    await sendSubscriptionToServer(subscription);
    console.log('Subscription sent to server successfully');
    
    isSubscribed.value = true;
    
    console.log('Showing test notification...');
    await registration.showNotification('Đăng ký thành công!', {
      body: 'Bạn sẽ nhận được thông báo khi có sự kiện mới',
      icon: '/favicon.ico',
      badge: '/favicon.ico'
    });

  } catch (error) {
    console.error('Error subscribing to push notifications:', error);
    alert('Có lỗi xảy ra khi đăng ký thông báo: ' + error.message);
  } finally {
    isLoading.value = false;
  }
};

const unsubscribeFromPushNotifications = async () => {
  if (!isSupported.value) return;

  isLoading.value = true;
  
  try {
    const registration = await navigator.serviceWorker.ready;
    const subscription = await registration.pushManager.getSubscription();
    
    if (subscription) {
      await subscription.unsubscribe();
      await removeSubscriptionFromServer(subscription);
    }
    
    isSubscribed.value = false;
    
  } catch (error) {
    console.error('Error unsubscribing from push notifications:', error);
    alert('Có lỗi xảy ra khi hủy đăng ký thông báo');
  } finally {
    isLoading.value = false;
  }
};

const sendSubscriptionToServer = async (subscription) => {
  try {
    console.log('Calling savePushSubscription API...');
    const response = await pushNotificationApi.savePushSubscription(subscription);
    console.log('API response:', response);
    return response;
  } catch (error) {
    console.error('Error sending subscription to server:', error);
    console.error('Error details:', error.response?.data);
    throw error;
  }
};

const removeSubscriptionFromServer = async (subscription) => {
  try {
    await pushNotificationApi.removePushSubscription(subscription);
  } catch (error) {
    console.error('Error removing subscription from server:', error);
    throw error;
  }
};
</script> 