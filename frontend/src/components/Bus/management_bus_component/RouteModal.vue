<template>
  <Transition name="modal" appear>
  <div v-if="isOpen" class="fixed inset-0 z-50 overflow-y-auto transition-all duration-300" @click="closeModal">
    <div class="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0
    transition-all duration-300 bg-black-100 bg-opacity-20 backdrop-blur-sm p-4">
    
      <!-- Modal panel -->
      <div @click.stop class="relative inline-block align-bottom bg-white 
      rounded-lg text-left overflow-hidden shadow-xl transform transition-all 
      sm:my-8 sm:align-middle sm:max-w-lg sm:w-full top-30">
        <div class="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
          <div class="sm:flex sm:items-start">
            <div class="w-full mt-3 text-center sm:mt-0 sm:text-left">
              <h3 class="text-lg leading-6 font-medium text-gray-900 mb-6">
                {{ isEditing ? 'Chỉnh sửa tuyến đường' : 'Thêm tuyến đường mới' }}
              </h3>
              
              <!-- Form -->
              <form @submit.prevent="handleSubmit" class="space-y-4">
                <!-- Origin (Điểm đi) -->
                <div>
                  <label for="origin" class="block text-sm font-medium text-gray-700 mb-1">
                    Điểm đi <span class="text-red-500">*</span>
                  </label>
                  <input
                    id="origin"
                    v-model="form.origin"
                    type="text"
                    required
                    placeholder="VD: Hà Nội"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                  />
                  <p v-if="errors.origin" class="text-red-500 text-xs mt-1">{{ errors.origin }}</p>
                </div>

                <!-- Destination (Điểm đến) -->
                <div>
                  <label for="destination" class="block text-sm font-medium text-gray-700 mb-1">
                    Điểm đến <span class="text-red-500">*</span>
                  </label>
                  <input
                    id="destination"
                    v-model="form.destination"
                    type="text"
                    required
                    placeholder="VD: TP. Hồ Chí Minh"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                  />
                  <p v-if="errors.destination" class="text-red-500 text-xs mt-1">{{ errors.destination }}</p>
                </div>

                <!-- Distance -->
                <div>
                  <label for="distanceKm" class="block text-sm font-medium text-gray-700 mb-1">
                    Khoảng cách (km) <span class="text-red-500">*</span>
                  </label>
                  <input
                    id="distanceKm"
                    v-model.number="form.distanceKm"
                    type="number"
                    min="1"
                    required
                    placeholder="VD: 1700"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                  />
                  <p v-if="errors.distanceKm" class="text-red-500 text-xs mt-1">{{ errors.distanceKm }}</p>
                </div>

                <!-- Estimated Duration -->
                <div>
                  <label for="estimatedHours" class="block text-sm font-medium text-gray-700 mb-1">
                    Thời gian di chuyển ước tính <span class="text-red-500">*</span>
                  </label>
                  <div class="grid grid-cols-2 gap-3">
                    <div>
                      <input
                        id="estimatedHours"
                        v-model.number="form.estimatedHours"
                        type="number"
                        min="0"
                        max="24"
                        required
                        placeholder="Giờ"
                        class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                      />
                      <p class="text-xs text-gray-500 mt-1">Giờ</p>
                    </div>
                    <div>
                      <input
                        id="estimatedMinutes"
                        v-model.number="form.estimatedMinutes"
                        type="number"
                        min="0"
                        max="59"
                        placeholder="Phút"
                        class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                      />
                      <p class="text-xs text-gray-500 mt-1">Phút</p>
                    </div>
                  </div>
                  <p v-if="errors.estimatedDurationMinutes" class="text-red-500 text-xs mt-1">{{ errors.estimatedDurationMinutes }}</p>
                </div>

                <!-- Preview -->
                <div v-if="form.origin && form.destination" class="mt-4 p-3 bg-gray-50 rounded-md">
                  <h4 class="text-sm font-medium text-gray-900 mb-2">Xem trước:</h4>
                  <div class="text-sm text-gray-600 space-y-1">
                    <div>🛣️ <strong>{{ form.origin }}</strong> → <strong>{{ form.destination }}</strong></div>
                    <div>📏 Khoảng cách: <strong>{{ form.distanceKm || 0 }}km</strong></div>
                    <div>⏱️ Thời gian: <strong>{{ getFormattedDuration() }}</strong></div>
                  </div>
                </div>
              </form>
            </div>
          </div>
        </div>
        
        <!-- Modal actions -->
        <div class="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
          <button
            type="button"
            @click="handleSubmit"
            :disabled="isSubmitting"
            class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-indigo-600 text-base font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:ml-3 sm:w-auto sm:text-sm disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <svg v-if="isSubmitting" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ isSubmitting ? 'Đang xử lý...' : (isEditing ? 'Cập nhật' : 'Tạo tuyến') }}
          </button>
          <button
            type="button"
            @click="closeModal"
            :disabled="isSubmitting"
            class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm disabled:opacity-50"
          >
            Hủy
          </button>
        </div>
      </div>
    </div>
  </div>
</transition>
</template>

<script setup>
import { ref, reactive, computed, watch, nextTick } from 'vue'
import { RouteAPI } from '@/api/busApi/route/api';

// Emits
const emit = defineEmits(['route-created', 'route-updated'])

// State
const isOpen = ref(false)
const isEditing = ref(false)
const isSubmitting = ref(false)
const editingRouteId = ref(null)

// Form data
const form = reactive({
  origin: '',
  destination: '',
  distanceKm: null,
  estimatedHours: null,
  estimatedMinutes: 0
})

// Form errors
const errors = ref({})

// Computed
const getFormattedDuration = () => {
  const hours = form.estimatedHours || 0
  const minutes = form.estimatedMinutes || 0
  
  if (hours === 0 && minutes === 0) return '0 phút'
  if (hours === 0) return `${minutes} phút`
  if (minutes === 0) return `${hours} giờ`
  
  return `${hours} giờ ${minutes} phút`
}

// Methods
const openModal = (routeData = null) => {
  // Reset form
  resetForm()
  
  if (routeData) {
    // Edit mode
    isEditing.value = true
    editingRouteId.value = routeData.id
    form.origin = routeData.origin
    form.destination = routeData.destination
    form.distanceKm = routeData.distanceKm
    
    // Convert minutes to hours and minutes
    const totalMinutes = routeData.estimatedDurationMinutes
    form.estimatedHours = Math.floor(totalMinutes / 60)
    form.estimatedMinutes = totalMinutes % 60
  } else {
    // Create mode
    isEditing.value = false
    editingRouteId.value = null
  }
  
  isOpen.value = true
}

const closeModal = () => {
  isOpen.value = false
  isSubmitting.value = false
  resetForm()
  
  // Reset editing state
  setTimeout(() => {
    isEditing.value = false
    editingRouteId.value = null
  }, 300)
}

const resetForm = () => {
  form.origin = ''
  form.destination = ''
  form.distanceKm = null
  form.estimatedHours = null
  form.estimatedMinutes = 0
  errors.value = {}
}

const validateForm = () => {
  const newErrors = {}
  
  if (!form.origin?.trim()) {
    newErrors.origin = 'Vui lòng nhập điểm đi'
  }
  
  if (!form.destination?.trim()) {
    newErrors.destination = 'Vui lòng nhập điểm đến'
  }
  
  if (form.origin?.trim() === form.destination?.trim()) {
    newErrors.destination = 'Điểm đến phải khác điểm đi'
  }
  
  if (!form.distanceKm || form.distanceKm <= 0) {
    newErrors.distanceKm = 'Vui lòng nhập khoảng cách hợp lệ'
  }
  
  if (!form.estimatedHours || form.estimatedHours <= 0) {
    newErrors.estimatedDurationMinutes = 'Vui lòng nhập thời gian di chuyển'
  }
  
  errors.value = newErrors
  return Object.keys(newErrors).length === 0
}

const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }
  
  isSubmitting.value = true
  
  try {
    const totalMinutes = (form.estimatedHours * 60) + (form.estimatedMinutes || 0)
    
    const input = {
      origin: form.origin.trim(),
      destination: form.destination.trim(),
      distanceKm: form.distanceKm,
      estimatedDurationMinutes: totalMinutes
    }
    
    let response
    if (isEditing.value) {
      response = await RouteAPI.updateRoute({ ...input, id: editingRouteId.value })
      emit('route-updated', response)
    } else {
      response = await RouteAPI.createRoute(input)
      emit('route-created', response)
    }
    closeModal(); // Đóng modal sau khi thành công
  } catch (error) {
    // Hiển thị lỗi cho người dùng (có thể qua một toast notification)
  } finally {
    isSubmitting.value = false
  }
}

// Expose methods for parent component
defineExpose({
  openModal
})

// Watch for ESC key
watch(isOpen, (newValue) => {
  if (newValue) {
    const handleEsc = (e) => {
      if (e.key === 'Escape') {
        closeModal()
      }
    }
    
    document.addEventListener('keydown', handleEsc)
    
    // Cleanup
    return () => {
      document.removeEventListener('keydown', handleEsc)
    }
  }
})
</script> 