<template>
  <div v-if="show" class="absolute right-12 top-full mt-2 w-80 bg-white border rounded-xl shadow-xl z-50">
    <div class="px-5 py-3 font-semibold text-gray-800 text-base flex items-center gap-2">
      <i class="fas fa-bell"></i>
      Notifications
    </div>
    <ul class="text-sm text-gray-800 max-h-60 overflow-y-auto space-y-2 p-4">
      <li v-for="(notification, index) in notifications" :key="index" :class="[
                'flex items-start gap-3 p-4 rounded-lg shadow-sm hover:shadow-md transition-all duration-200 cursor-pointer',
                notification.bg
            ]">
        <div :class="['text-lg', notification.color]">
          <i :class="notification.icon"></i>
        </div>
        <div class="flex-1">
          <p class="text-gray-800 font-semibold leading-snug">
            {{ notification.message }}
          </p>
          <span class="text-xs text-gray-500 mt-1 inline-block">
                        {{ new Date().toLocaleDateString() }}
                    </span>
        </div>
      </li>
    </ul>
    <div
        class="px-5 py-3 text-center text-blue-600 hover:text-blue-800 hover:underline text-sm font-medium cursor-pointer">
      View all notifications
    </div>
  </div>
</template>

<script setup>
defineProps({
  show: Boolean,
});

const notifications = [
  {
    message: 'Your booking has been confirmed.',
    icon: 'fas fa-check-circle',
    color: 'text-green-500',
    bg: 'bg-green-50',
  },
  {
    message: 'A new tour is available in Da Nang.',
    icon: 'fas fa-map-marker-alt',
    color: 'text-purple-500',
    bg: 'bg-purple-50',
  },
  {
    message: 'Bus schedule updated.',
    icon: 'fas fa-bus',
    color: 'text-yellow-500',
    bg: 'bg-yellow-50',
  },
  {
    message: 'Don’t miss our 20% flight discount!',
    icon: 'fas fa-plane-departure',
    color: 'text-blue-500',
    bg: 'bg-blue-50',
  },
];
</script>
