<script setup>
import { ref, onMounted } from 'vue'
import { setupNotifications } from '@/utils/notifications'
import ToastContainer from '@/components/ToastContainer.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'

// Refs for notification components
const toastContainer = ref(null)
const confirmDialog = ref(null)

// Setup notification system
onMounted(() => {
  setupNotifications(toastContainer.value, confirmDialog.value)
})
</script>

<template>
  <div id="app">
    <router-view />
    
    <!-- Global Notifications -->
    <ToastContainer ref="toastContainer" />
    <ConfirmDialog ref="confirmDialog" />
  </div>
</template>

<style>
/* Keep existing styles minimal - don't override global CSS */
#app {
  position: relative;
}
</style>
