<template>
  <transition name="modal" appear>
    <div v-if="isVisible" @click="handleBackdropClick" 
         class="fixed inset-0 h-full w-full z-50 flex items-center justify-center bg-black-100 bg-opacity-20 backdrop-blur-sm p-4">
      <div @click.stop class="relative w-full max-w-4xl bg-white rounded-lg shadow-2xl overflow-hidden max-h-[90vh] flex flex-col mx-auto">
        <!-- Modal Header -->
        <div class="bg-gray-50 px-6 py-4 border-b border-gray-200 flex-shrink-0">
          <div class="flex items-center justify-between">
            <h3 class="text-lg font-semibold text-gray-900">
              {{ modalTitle }}
            </h3>
            <button @click="closeModal" class="text-gray-400 hover:text-gray-500 focus:outline-none">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </button>
          </div>
          
          <!-- Steps Navigation -->
          <div class="mt-4 flex items-center">
            <div v-for="(step, index) in steps" :key="index" class="flex items-center">
              <div class="flex items-center">
                <div :class="[
                  'w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium transition-colors duration-200',
                  currentStep === index + 1 
                    ? 'bg-blue-600 text-white' 
                    : currentStep > index + 1 
                      ? 'bg-green-500 text-white'
                      : 'bg-gray-200 text-gray-600'
                ]">
                  <svg v-if="currentStep > index + 1" class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
                  </svg>
                  <span v-else>{{ index + 1 }}</span>
                </div>
                <span :class="[
                  'ml-2 text-sm font-medium transition-colors duration-200',
                  currentStep === index + 1 ? 'text-blue-600' : 'text-gray-500'
                ]">{{ step.title }}</span>
              </div>
              <div v-if="index < steps.length - 1" class="flex-1 h-0.5 bg-gray-200 mx-4">
                <div :class="[
                  'h-full transition-all duration-300',
                  currentStep > index + 1 ? 'bg-green-500' : 'bg-gray-200'
                ]"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- Modal Content - Responsive Height -->
        <div class="flex-1 overflow-y-auto min-h-0">
          <div class="px-6 py-6">
            <BusRouteSteps 
              :currentStep="currentStep"
              :formData="formData"
              :validationErrors="validationErrors"
            />
          </div>
        </div>

        <!-- Modal Footer -->
        <div class="bg-gray-50 px-6 py-4 border-t border-gray-200 flex items-center justify-between flex-shrink-0">
          <div class="flex items-center space-x-3">
            <!-- Nút quay lại -->
            <button
              v-if="canGoPrevious"
              @click="previousStep"
              :disabled="isLoading"
              class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Quay lại
            </button>
            
            <!-- Loading indicator khi đang xử lý -->
            <div v-if="isLoading" class="flex items-center space-x-2 text-indigo-600">
              <svg class="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <span class="text-sm font-medium">
                {{ currentStep === totalSteps ? 'Đang tạo tuyến xe...' : 'Đang xử lý...' }}
              </span>
            </div>
          </div>

          <div class="flex items-center space-x-3">
            <!-- Nút hủy -->
            <button
              @click="closeModal"
              :disabled="isLoading"
              class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Hủy
            </button>

            <!-- Nút tiếp theo hoặc lưu -->
            <button
              v-if="currentStep < totalSteps"
              @click="nextStep"
              :disabled="!canGoNext || isLoading"
              class="px-4 py-2 text-sm font-medium text-white bg-indigo-600 border border-transparent rounded-md shadow-sm hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Tiếp theo
            </button>
            
            <button
              v-else
              @click="handleSave"
              :disabled="!validateCurrentStep() || isLoading"
              class="px-4 py-2 text-sm font-medium text-white bg-indigo-600 border border-transparent rounded-md shadow-sm hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <span v-if="!isLoading">{{ isEditing ? 'Cập nhật' : 'Tạo tuyến xe' }}</span>
              <span v-else class="flex items-center space-x-2">
                <svg class="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                <span>Đang tạo...</span>
              </span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { computed } from 'vue'
import BusRouteSteps from './BusRouteSteps.vue'
import { useBusRouteModal } from '@/composables/useBusRouteModal'

// Sử dụng composable
const {
  isVisible,
  currentStep,
  isEditing,
  steps,
  formData,
  validationErrors,
  isLoading,
  totalSteps,
  canGoNext,
  canGoPrevious,
  closeModal,
  nextStep,
  previousStep,
  validateCurrentStep,
  saveForm
} = useBusRouteModal()

// Emits
const emit = defineEmits(['route-created', 'route-updated'])

// Methods
const handleBackdropClick = () => {
  // Always allow backdrop close for now
  closeModal()
}

const handleSave = async () => {
  try {
    const result = await saveForm()
    
    if (result.success) {
      // Emit event để parent component biết có route mới
      if (isEditing.value) {
        emit('route-updated', result.data)
      } else {
        emit('route-created', result.data)
      }
      
      // Hiển thị thông báo thành công
      console.log('✅ Success:', result.message)
      // TODO: Thêm toast notification
      
      // Đóng modal sau 1s
      setTimeout(() => {
        closeModal()
      }, 1000)
    }
  } catch (error) {
    console.error('❌ Error saving route:', error)
    // TODO: Hiển thị error notification
    alert(error.message || 'Có lỗi xảy ra khi lưu tuyến xe')
  }
}

// Computed
const modalTitle = computed(() => {
  return isEditing.value ? 'Chỉnh sửa tuyến xe' : 'Thêm tuyến xe mới'
})

const currentStepTitle = computed(() => {
  const step = steps.value.find(s => s.id === currentStep.value)
  return step ? step.title : ''
})
</script>

<style scoped>
/* Modal Transition Styles */
.modal-enter-active {
  transition: all 0.3s ease-out;
}

.modal-leave-active {
  transition: all 0.2s ease-in;
}

.modal-enter-from {
  opacity: 0;
}

.modal-enter-to {
  opacity: 1;
}

.modal-leave-from {
  opacity: 1;
}

.modal-leave-to {
  opacity: 0;
}

/* Modal Content Animation */
.modal-enter-active .relative {
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.modal-leave-active .relative {
  transition: all 0.2s ease-in-out;
}

.modal-enter-from .relative {
  opacity: 0;
  transform: scale(0.95) translateY(-10px);
}

.modal-enter-to .relative {
  opacity: 1;
  transform: scale(1) translateY(0);
}

.modal-leave-from .relative {
  opacity: 1;
  transform: scale(1) translateY(0);
}

.modal-leave-to .relative {
  opacity: 0;
  transform: scale(0.95) translateY(-5px);
}

/* Scroll styling */
.flex-1::-webkit-scrollbar {
  width: 6px;
}

.flex-1::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.flex-1::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.flex-1::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style> 