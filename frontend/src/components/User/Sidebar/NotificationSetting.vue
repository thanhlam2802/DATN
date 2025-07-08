<template>
   <h2 class="text-2xl font-semibold text-gray-800 mb-3">
      Notification setting
   </h2>

   <div class="flex border-b border-gray-300">
      <button
         v-for="(tab, index) in tabs"
         :key="index"
         @click="activeTab = index"
         :class="[
            'px-4 py-2 font-medium',
            activeTab === index
               ? 'border-b-2 border-blue-600 text-blue-600'
               : 'text-gray-600 hover:text-blue-500',
         ]"
      >
         {{ tab.label }}
      </button>
   </div>

   <component class="mt-5" :is="tabs[activeTab].component" />
</template>

<script setup>
import { ref } from 'vue';
import EmailNotificationSetting from '@/components/User/Sidebar/components/EmailNotificationSetting.vue';
import SmsNotificationSetting from '@/components/User/Sidebar/components/SmsNotificationSetting.vue';

const tabs = [
   { label: 'Email', component: EmailNotificationSetting },
   { label: 'SMS', component: SmsNotificationSetting },
];

const activeTab = ref(0);
</script>
