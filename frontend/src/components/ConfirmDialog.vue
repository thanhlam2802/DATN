<template>
  <teleport to="body">
    <transition name="modal-overlay" appear>
      <div v-if="state.show" @click="handleBackdropClick" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
        <transition name="modal-content" appear>
          <div @click.stop class="bg-white rounded-xl shadow-2xl max-w-md w-full mx-4 overflow-hidden">
            <!-- Header -->
            <div :class="getHeaderClasses()" class="px-6 py-4 border-b">
              <div class="flex items-center space-x-3">
                <div :class="getIconContainerClasses()" class="flex-shrink-0 w-10 h-10 rounded-full flex items-center justify-center">
                  <component :is="getIcon()" :class="getIconClasses()" class="w-6 h-6" />
                </div>
                <div>
                  <h3 class="text-lg font-semibold text-gray-900">
                    {{ state.title || getDefaultTitle() }}
                  </h3>
                  <p v-if="state.subtitle" class="text-sm text-gray-500 mt-1">
                    {{ state.subtitle }}
                  </p>
                </div>
              </div>
            </div>

            <!-- Content -->
            <div class="px-6 py-4">
              <p class="text-gray-700 leading-relaxed">
                {{ state.message }}
              </p>
              
              <!-- Additional details -->
              <div v-if="state.details" class="mt-3 p-3 bg-gray-50 rounded-lg">
                <p class="text-sm text-gray-600">
                  {{ state.details }}
                </p>
              </div>

              <!-- Input field for confirmation (if required) -->
              <div v-if="state.requireConfirmText" class="mt-4">
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  Nhập "<strong>{{ state.confirmText }}</strong>" để xác nhận:
                </label>
                <input 
                  v-model="confirmInput"
                  type="text"
                  :placeholder="state.confirmText"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-red-500 focus:border-red-500"
                  @keyup.enter="state.confirmText && confirmInput === state.confirmText && handleConfirm()"
                />
              </div>
            </div>

            <!-- Actions -->
            <div class="px-6 py-4 bg-gray-50 flex flex-col-reverse sm:flex-row sm:space-x-3 space-y-3 space-y-reverse sm:space-y-0">
              <button
                @click="handleCancel"
                class="w-full sm:w-auto px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500 transition-colors"
              >
                {{ state.cancelText || 'Hủy' }}
              </button>
              <button
                @click="handleConfirm"
                :disabled="state.requireConfirmText && confirmInput !== state.confirmText"
                :class="[
                  'w-full sm:w-auto px-4 py-2 text-sm font-medium rounded-md focus:outline-none focus:ring-2 focus:ring-offset-2 transition-colors',
                  getConfirmButtonClasses(),
                  (state.requireConfirmText && confirmInput !== state.confirmText) ? 'opacity-50 cursor-not-allowed' : ''
                ]"
              >
                <div v-if="state.loading" class="flex items-center justify-center">
                  <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-current" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  {{ state.loadingText || 'Đang xử lý...' }}
                </div>
                <span v-else>
                  {{ state.confirmText || getDefaultConfirmText() }}
                </span>
              </button>
            </div>
          </div>
        </transition>
      </div>
    </transition>
  </teleport>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { 
  Trash2, 
  AlertTriangle, 
  Info,
  CheckCircle,
  XCircle
} from 'lucide-vue-next'

// Internal state
const state = reactive({
  show: false,
  type: 'warning',
  title: '',
  subtitle: '',
  message: '',
  details: '',
  confirmText: '',
  cancelText: '',
  requireConfirmText: false,
  loading: false,
  loadingText: '',
  closeOnBackdrop: true
})

// Local state
const confirmInput = ref('')
let resolvePromise = null

// Watchers
watch(() => state.show, (newShow) => {
  if (newShow) {
    confirmInput.value = ''
  }
})

// Methods
const handleConfirm = () => {
  if (state.requireConfirmText && confirmInput.value !== state.confirmText) {
    return
  }
  if (!state.loading) {
    state.show = false
    if (resolvePromise) {
      resolvePromise(true)
      resolvePromise = null
    }
  }
}

const handleCancel = () => {
  if (!state.loading) {
    state.show = false
    if (resolvePromise) {
      resolvePromise(false)
      resolvePromise = null
    }
  }
}

const handleBackdropClick = () => {
  if (state.closeOnBackdrop && !state.loading) {
    handleCancel()
  }
}

// Public method to show dialog
const showDialog = (options) => {
  return new Promise((resolve) => {
    // Update state
    Object.assign(state, {
      show: true,
      type: 'warning',
      title: '',
      subtitle: '',
      message: '',
      details: '',
      confirmText: '',
      cancelText: '',
      requireConfirmText: false,
      loading: false,
      loadingText: '',
      closeOnBackdrop: true,
      ...options
    })
    
    // Store resolve function
    resolvePromise = resolve
  })
}

// Style helpers
const getIcon = () => {
  const icons = {
    danger: Trash2,
    warning: AlertTriangle,
    info: Info,
    success: CheckCircle
  }
  return icons[state.type] || icons.warning
}

const getIconContainerClasses = () => {
  const classes = {
    danger: 'bg-red-100',
    warning: 'bg-yellow-100', 
    info: 'bg-blue-100',
    success: 'bg-green-100'
  }
  return classes[state.type] || classes.warning
}

const getIconClasses = () => {
  const classes = {
    danger: 'text-red-600',
    warning: 'text-yellow-600',
    info: 'text-blue-600', 
    success: 'text-green-600'
  }
  return classes[state.type] || classes.warning
}

const getHeaderClasses = () => {
  const classes = {
    danger: 'border-red-200 bg-red-50',
    warning: 'border-yellow-200 bg-yellow-50',
    info: 'border-blue-200 bg-blue-50',
    success: 'border-green-200 bg-green-50'
  }
  return classes[state.type] || classes.warning
}

const getConfirmButtonClasses = () => {
  const classes = {
    danger: 'text-white bg-red-600 hover:bg-red-700 focus:ring-red-500',
    warning: 'text-white bg-yellow-600 hover:bg-yellow-700 focus:ring-yellow-500',
    info: 'text-white bg-blue-600 hover:bg-blue-700 focus:ring-blue-500',
    success: 'text-white bg-green-600 hover:bg-green-700 focus:ring-green-500'
  }
  return classes[state.type] || classes.warning
}

const getDefaultTitle = () => {
  const titles = {
    danger: 'Xác nhận xóa',
    warning: 'Cảnh báo',
    info: 'Thông tin',
    success: 'Thành công'
  }
  return titles[state.type] || titles.warning
}

const getDefaultConfirmText = () => {
  const texts = {
    danger: 'Xóa',
    warning: 'Tiếp tục',
    info: 'OK',
    success: 'OK'
  }
  return texts[state.type] || texts.warning
}

// Expose methods
defineExpose({
  showDialog
})
</script>

<style scoped>
.modal-overlay-enter-active,
.modal-overlay-leave-active {
  transition: opacity 0.3s ease;
}

.modal-overlay-enter-from,
.modal-overlay-leave-to {
  opacity: 0;
}

.modal-content-enter-active,
.modal-content-leave-active {
  transition: all 0.3s ease;
}

.modal-content-enter-from,
.modal-content-leave-to {
  opacity: 0;
  transform: scale(0.95) translateY(-10px);
}
</style> 