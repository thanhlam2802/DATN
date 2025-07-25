<template>
  <teleport to="body">
    <div class="fixed top-4 right-4 z-100 space-y-2">
      <transition-group name="toast" tag="div">
        <div
          v-for="toast in toasts"
          :key="toast.id"
          :class="getToastClasses(toast.type)"
          class="min-w-80 max-w-md p-4 rounded-lg shadow-lg border backdrop-blur-sm transition-all duration-300"
        >
          <div class="flex items-start space-x-3">
            <!-- Icon -->
            <div class="flex-shrink-0 mt-0.5">
              <component :is="getIcon(toast.type)" :class="getIconClasses(toast.type)" class="w-5 h-5" />
            </div>
            
            <!-- Content -->
            <div class="flex-1 min-w-0">
              <h4 v-if="toast.title" :class="getTitleClasses(toast.type)" class="text-sm font-semibold">
                {{ toast.title }}
              </h4>
              <p :class="[getMessageClasses(toast.type), 'text-sm', { 'mt-1': toast.title }]">
                {{ toast.message }}
              </p>
            </div>
            
            <!-- Close button -->
            <button
              @click="removeToast(toast.id)"
              :class="getCloseButtonClasses(toast.type)"
              class="flex-shrink-0 rounded-full p-1 hover:bg-black/10 focus:outline-none focus:ring-2 focus:ring-offset-2 transition-colors"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </button>
          </div>
          
          <!-- Progress bar -->
          <div v-if="toast.showProgress" class="mt-3 h-1 bg-black/10 rounded-full overflow-hidden">
            <div 
              :class="getProgressClasses(toast.type)"
              class="h-full transition-all ease-linear"
              :style="{ width: `${getProgress(toast)}%` }"
            ></div>
          </div>
        </div>
      </transition-group>
    </div>
  </teleport>
</template>

<script setup>
import { ref, computed } from 'vue'

// Icons
import { 
  CheckCircle, 
  AlertTriangle, 
  XCircle, 
  Info 
} from 'lucide-vue-next'

// State
const toasts = ref([])

// Toast types
const TOAST_TYPES = {
  SUCCESS: 'success',
  ERROR: 'error', 
  WARNING: 'warning',
  INFO: 'info'
}

// Methods
const addToast = (options) => {
  const id = Date.now() + Math.random()
  const toast = {
    id,
    type: options.type || TOAST_TYPES.INFO,
    title: options.title,
    message: options.message || '',
    duration: options.duration || (options.type === TOAST_TYPES.ERROR ? 6000 : 4000),
    showProgress: options.showProgress !== false,
    createdAt: Date.now()
  }
  
  toasts.value.push(toast)
  
  // Auto remove
  setTimeout(() => {
    removeToast(id)
  }, toast.duration)
  
  return id
}

const removeToast = (id) => {
  const index = toasts.value.findIndex(toast => toast.id === id)
  if (index > -1) {
    toasts.value.splice(index, 1)
  }
}

const clearAll = () => {
  toasts.value = []
}

// Computed
const getProgress = (toast) => {
  const elapsed = Date.now() - toast.createdAt
  const progress = 100 - (elapsed / toast.duration) * 100
  return Math.max(0, Math.min(100, progress))
}

// Style helpers
const getToastClasses = (type) => {
  const classes = {
    [TOAST_TYPES.SUCCESS]: 'bg-green-50 border-green-200',
    [TOAST_TYPES.ERROR]: 'bg-red-50 border-red-200', 
    [TOAST_TYPES.WARNING]: 'bg-yellow-50 border-yellow-200',
    [TOAST_TYPES.INFO]: 'bg-blue-50 border-blue-200'
  }
  return classes[type] || classes[TOAST_TYPES.INFO]
}

const getIcon = (type) => {
  const icons = {
    [TOAST_TYPES.SUCCESS]: CheckCircle,
    [TOAST_TYPES.ERROR]: XCircle,
    [TOAST_TYPES.WARNING]: AlertTriangle,
    [TOAST_TYPES.INFO]: Info
  }
  return icons[type] || icons[TOAST_TYPES.INFO]
}

const getIconClasses = (type) => {
  const classes = {
    [TOAST_TYPES.SUCCESS]: 'text-green-500',
    [TOAST_TYPES.ERROR]: 'text-red-500',
    [TOAST_TYPES.WARNING]: 'text-yellow-500', 
    [TOAST_TYPES.INFO]: 'text-blue-500'
  }
  return classes[type] || classes[TOAST_TYPES.INFO]
}

const getTitleClasses = (type) => {
  const classes = {
    [TOAST_TYPES.SUCCESS]: 'text-green-800',
    [TOAST_TYPES.ERROR]: 'text-red-800',
    [TOAST_TYPES.WARNING]: 'text-yellow-800',
    [TOAST_TYPES.INFO]: 'text-blue-800'
  }
  return classes[type] || classes[TOAST_TYPES.INFO]
}

const getMessageClasses = (type) => {
  const classes = {
    [TOAST_TYPES.SUCCESS]: 'text-green-700',
    [TOAST_TYPES.ERROR]: 'text-red-700',
    [TOAST_TYPES.WARNING]: 'text-yellow-700',
    [TOAST_TYPES.INFO]: 'text-blue-700'
  }
  return classes[type] || classes[TOAST_TYPES.INFO]
}

const getCloseButtonClasses = (type) => {
  const classes = {
    [TOAST_TYPES.SUCCESS]: 'text-green-500 focus:ring-green-500',
    [TOAST_TYPES.ERROR]: 'text-red-500 focus:ring-red-500',
    [TOAST_TYPES.WARNING]: 'text-yellow-500 focus:ring-yellow-500',
    [TOAST_TYPES.INFO]: 'text-blue-500 focus:ring-blue-500'
  }
  return classes[type] || classes[TOAST_TYPES.INFO]
}

const getProgressClasses = (type) => {
  const classes = {
    [TOAST_TYPES.SUCCESS]: 'bg-green-500',
    [TOAST_TYPES.ERROR]: 'bg-red-500',
    [TOAST_TYPES.WARNING]: 'bg-yellow-500',
    [TOAST_TYPES.INFO]: 'bg-blue-500'
  }
  return classes[type] || classes[TOAST_TYPES.INFO]
}

// Expose methods
defineExpose({
  addToast,
  removeToast,
  clearAll,
  TOAST_TYPES
})
</script>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(100%);
}

.toast-move {
  transition: transform 0.3s ease;
}
</style> 