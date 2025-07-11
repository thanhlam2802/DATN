<template>
  <div class="toast-container">
    <transition-group name="toast-fade" tag="div">
      <div v-for="(toast, idx) in toasts" :key="toast.id" :class="['toast', toast.type]">
        <i v-if="toast.type==='success'" class="fas fa-check-circle"></i>
        <i v-else class="fas fa-exclamation-circle"></i>
        <span>{{ toast.message }}</span>
      </div>
    </transition-group>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
const toasts = ref([]);

function showToast(message, type = 'success') {
  const id = Date.now() + Math.random();
  toasts.value.push({ id, message, type });
  setTimeout(() => {
    toasts.value = toasts.value.filter(t => t.id !== id);
  }, 3000);
}

export default {
  name: 'ToastContainer',
  setup() {
    if (!window.$toast) {
      window.$toast = showToast;
    }
    return { toasts };
  }
};
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 24px;
  right: 24px;
  z-index: 99999;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.toast {
  min-width: 260px;
  max-width: 350px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.12);
  padding: 14px 20px;
  display: flex;
  align-items: center;
  font-weight: 500;
  font-size: 15px;
  gap: 10px;
  color: #fff;
  animation: slideInRight 0.4s;
}
.toast.success {
  background: #22c55e;
}
.toast.error {
  background: #ef4444;
}
.toast i {
  color: #fff;
  font-size: 18px;
}
.toast-fade-enter-active, .toast-fade-leave-active {
  transition: all 0.4s;
}
.toast-fade-enter-from, .toast-fade-leave-to {
  opacity: 0;
  transform: translateX(100px);
}
@keyframes slideInRight {
  from { opacity: 0; transform: translateX(100px); }
  to { opacity: 1; transform: translateX(0); }
}
</style> 