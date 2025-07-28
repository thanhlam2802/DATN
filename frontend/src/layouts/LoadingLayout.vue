<template>
  <div
      v-if="isLoading"
      class="fixed inset-0 z-50 flex items-center justify-center bg-white/50 backdrop-blur-sm transition-all"
  >
    <div class="flex flex-col items-center space-y-2">
      <svg class="animate-spin h-8 w-8 text-purple-600" xmlns="http://www.w3.org/2000/svg" fill="none"
           viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10"
                stroke="currentColor" stroke-width="4"/>
        <path class="opacity-75" fill="currentColor"
              d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"/>
      </svg>
      <span class="text-sm text-gray-700">Loading...</span>
    </div>
  </div>
</template>

<script setup>
import {useLoadingStore} from '@/store/GlobalStore.js';
import {computed} from "vue";

const loadingStore = useLoadingStore();
const isLoading = computed(() => loadingStore.isLoading);
</script>
