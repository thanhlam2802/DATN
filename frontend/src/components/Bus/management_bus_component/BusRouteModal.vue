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
            <slot :currentStep="currentStep" :formData="formData" :updateFormData="updateFormData">
              <!-- Default content if no slot provided -->
              <div class="text-center text-gray-500">
                <p>Modal content goes here</p>
              </div>
            </slot>
          </div>
        </div>

        <!-- Modal Footer -->
        <div class="bg-gray-50 px-6 py-4 border-t border-gray-200 flex items-center justify-between flex-shrink-0">
          <div class="flex space-x-3">
            <button @click="saveDraft" type="button" 
                    class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500">
              Lưu nháp
            </button>
            <button @click="closeModal" type="button" 
                    class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500">
              Quay lại
            </button>
          </div>
          
          <div class="flex space-x-3">
            <button v-if="currentStep > 1" @click="previousStep" type="button" 
                    class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500">
              Trước
            </button>
            <button v-if="currentStep < totalSteps" @click="nextStep" type="button" 
                    class="px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500">
              Tiếp
            </button>
            <button v-if="currentStep === totalSteps" @click="saveForm" type="button" 
                    class="px-4 py-2 text-sm font-medium text-white bg-green-600 border border-transparent rounded-md hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500">
              {{ isEditing ? 'Cập nhật' : 'Hoàn tất' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { computed } from 'vue'

// Props
const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false
  },
  currentStep: {
    type: Number,
    default: 1
  },
  totalSteps: {
    type: Number,
    default: 3
  },
  steps: {
    type: Array,
    required: true
  },
  formData: {
    type: Object,
    default: () => ({})
  },
  isEditing: {
    type: Boolean,
    default: false
  },
  allowBackdropClose: {
    type: Boolean,
    default: true
  }
})

// Emits
const emit = defineEmits([
  'close',
  'save-draft', 
  'save-form',
  'next-step',
  'previous-step',
  'update-form-data'
])

// Computed
const modalTitle = computed(() => {
  return props.isEditing ? 'Sửa tuyến đường' : 'Thêm tuyến đường mới'
})

// Methods
const handleBackdropClick = () => {
  if (props.allowBackdropClose) {
    closeModal()
  }
}

const closeModal = () => {
  emit('close')
}

const saveDraft = () => {
  emit('save-draft', props.formData)
}

const saveForm = () => {
  emit('save-form', props.formData)
}

const nextStep = () => {
  emit('next-step')
}

const previousStep = () => {
  emit('previous-step')
}

const updateFormData = (data) => {
  emit('update-form-data', data)
}
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