<template>
  <h2 class="text-2xl font-semibold text-gray-800 mb-5">Thông tin cá nhân</h2>

  <div class="space-y-6">
    <div
        v-for="item in infoItems"
        :key="item.label"
        class="flex items-center justify-between pb-4 border-b border-gray-100 last:border-b-0"
    >
      <div class="flex items-start space-x-3">
        <i :class="item.icon + ' text-gray-500 text-lg mt-0.5'"></i>
        <div>
          <p class="text-sm text-gray-500 font-medium">{{ item.label }}</p>
          <p class="text-base text-gray-800 font-semibold mt-1">{{ item.value }}</p>
        </div>
      </div>
      <button class="text-sm font-medium text-blue-600 hover:text-blue-800 hover:underline transition">
        {{ item.action }}
      </button>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {AuthApi} from "@/api/AuthApi.js";
import {AccountApi} from "@/api/AccountApi.js";

const infoItems = ref([])

onMounted(async () => {
  try {
    const response = await AccountApi.getProfile();
    const data = response.data;
    // map data từ API thành infoItems
    infoItems.value = [
      {label: 'Full name', value: data.name, action: 'Edit', icon: 'fas fa-user'},
      {label: 'Gender', value: data.gender || 'Not specified', action: 'Edit', icon: 'fas fa-venus-mars'},
      {label: 'Date of birth', value: formatDate(data.birthday), action: 'Edit', icon: 'fas fa-birthday-cake'},
    ]
  } catch (err) {
    console.error('Lỗi khi tải profile:', err)
  }
})

function formatDate(dateStr) {
  if (!dateStr) return 'Not provided'
  const date = new Date(dateStr)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}
</script>
