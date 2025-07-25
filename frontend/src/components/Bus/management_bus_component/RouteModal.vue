<template>
  <teleport to="body">
    <transition name="modal-overlay" appear>
      <div v-if="isOpen" @click="closeModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
        <transition name="modal-content" appear>
          <div @click.stop class="relative bg-white rounded-xl shadow-2xl max-w-lg w-full mx-4 overflow-hidden">
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

                    <!-- Price Section -->
                    <div class="border-t border-gray-200 pt-4 mt-6">
                      <h4 class="text-lg font-medium text-gray-900 mb-4 flex items-center">
                        <svg class="w-5 h-5 mr-2 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1"/>
                        </svg>
                        Thiết lập giá vé
                      </h4>

                      <!-- Bus Categories Selection -->
                      <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-700 mb-2">
                          Loại xe áp dụng <span class="text-red-500">*</span>
                        </label>
                        <div class="grid grid-cols-2 gap-2 max-h-32 overflow-y-auto border border-gray-300 rounded-md p-2">
                          <label v-for="category in busCategories" :key="category.id" class="flex items-center space-x-2 p-2 hover:bg-gray-50 rounded">
                            <input 
                              type="checkbox" 
                              :value="category.id"
                              v-model="form.selectedCategories"
                              class="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
                            />
                            <span class="text-sm text-gray-700">{{ category.name }}</span>
                          </label>
                        </div>
                        <p v-if="errors.selectedCategories" class="text-red-500 text-xs mt-1">{{ errors.selectedCategories }}</p>
                      </div>

                      <!-- Price Settings -->
                      <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                        <div>
                          <label for="basePrice" class="block text-sm font-medium text-gray-700 mb-1">
                            Giá cơ sở (VND) <span class="text-red-500">*</span>
                          </label>
                          <input
                            id="basePrice"
                            v-model.number="form.basePrice"
                            type="number"
                            min="10000"
                            step="10000"
                            required
                            placeholder="500000"
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                          />
                          <p v-if="errors.basePrice" class="text-red-500 text-xs mt-1">{{ errors.basePrice }}</p>
                        </div>
                        <div>
                          <label for="promotionPrice" class="block text-sm font-medium text-gray-700 mb-1">
                            Giá khuyến mãi (VND)
                          </label>
                          <input
                            id="promotionPrice"
                            v-model.number="form.promotionPrice"
                            type="number"
                            min="10000"
                            step="10000"
                            placeholder="Để trống nếu không có"
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                          />
                        </div>
                      </div>

                      <!-- Date Range -->
                      <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                        <div>
                          <label for="validFrom" class="block text-sm font-medium text-gray-700 mb-1">
                            Áp dụng từ ngày <span class="text-red-500">*</span>
                          </label>
                          <input
                            id="validFrom"
                            v-model="form.validFrom"
                            type="date"
                            required
                            :min="today"
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                          />
                          <p v-if="errors.validFrom" class="text-red-500 text-xs mt-1">{{ errors.validFrom }}</p>
                        </div>
                        <div>
                          <label for="validTo" class="block text-sm font-medium text-gray-700 mb-1">
                            Áp dụng đến ngày <span class="text-red-500">*</span>
                          </label>
                          <input
                            id="validTo"
                            v-model="form.validTo"
                            type="date"
                            required
                            :min="form.validFrom || today"
                            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                          />
                          <p v-if="errors.validTo" class="text-red-500 text-xs mt-1">{{ errors.validTo }}</p>
                        </div>
                      </div>

                      <!-- Notes -->
                      <div class="mb-4">
                        <label for="notes" class="block text-sm font-medium text-gray-700 mb-1">
                          Ghi chú (tùy chọn)
                        </label>
                        <textarea
                          id="notes"
                          v-model="form.notes"
                          rows="2"
                          placeholder="VD: Giá áp dụng cho mùa cao điểm..."
                          class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                        ></textarea>
                      </div>
                    </div>

                    <!-- Preview -->
                    <div v-if="form.origin && form.destination" class="mt-4 p-3 bg-gray-50 rounded-md">
                      <h4 class="text-sm font-medium text-gray-900 mb-2">Xem trước:</h4>
                      <div class="text-sm text-gray-600 space-y-1">
                        <div>🛣️ <strong>{{ form.origin }}</strong> → <strong>{{ form.destination }}</strong></div>
                        <div>📏 Khoảng cách: <strong>{{ form.distanceKm || 0 }}km</strong></div>
                        <div>⏱️ Thời gian: <strong>{{ getFormattedDuration() }}</strong></div>
                        <div>🚌 Loại xe: <strong>{{ getSelectedCategoryNames() }}</strong></div>
                        <div>💰 Giá vé: <strong>{{ getFormattedPrice() }}</strong></div>
                        <div>🗓️ Áp dụng từ: <strong>{{ form.validFrom }}</strong> đến <strong>{{ form.validTo }}</strong></div>
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
        </transition>
      </div>
    </transition>
  </teleport>
</template>

<script setup>
import { ref, reactive, computed, watch, nextTick, onMounted } from 'vue'
import { RouteAPI } from '@/api/busApi/route/api';
// @ts-ignore
import { toast, handleError } from '@/utils/notifications'

// Emits
const emit = defineEmits(['route-created', 'route-updated'])

// Lifecycle
onMounted(async () => {
  await loadBusCategories()
})

// State
const isOpen = ref(false)
const isEditing = ref(false)
const isSubmitting = ref(false)
const editingRouteId = ref(null)

// New state for price management
const busCategories = ref([])
const today = new Date().toISOString().split('T')[0]
const existingCategoryIds = ref([])

// Form data
const form = reactive({
  origin: '',
  destination: '',
  distanceKm: null,
  estimatedHours: null,
  estimatedMinutes: 0,
  selectedCategories: [], // New for bus categories
  basePrice: null,
  promotionPrice: null,
  validFrom: today, // Default to today
  validTo: null,
  notes: ''
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

const getSelectedCategoryNames = () => {
  return busCategories.value
    .filter(cat => form.selectedCategories.includes(cat.id))
    .map(cat => cat.name)
    .join(', ')
}

const getFormattedPrice = () => {
  if (!form.basePrice) return 'Chưa đặt giá'
  
  const base = new Intl.NumberFormat('vi-VN').format(form.basePrice) + ' VND'
  if (form.promotionPrice && form.promotionPrice > 0) {
    const promo = new Intl.NumberFormat('vi-VN').format(form.promotionPrice) + ' VND'
    return `${base} (Khuyến mãi: ${promo})`
  }
  return base
}

// Methods
const loadBusCategories = async () => {
  try {
    const categories = await BusCategoryAPI.getAllBusCategories()
    busCategories.value = categories
  } catch (error) {
    console.error('❌ Error loading bus categories:', error)
  }
}

// Load existing price rules when editing a route
const loadExistingPriceRules = async (routeId) => {
  if (!routeId) return
  
  try {
    const allPrices = await PriceAPI.getAllRouteBusCategoryPrices()
    
    // Filter prices for this specific route
    const routePrices = allPrices.filter(price => price.route?.id === routeId)
    
    if (routePrices.length > 0) {
      // Extract category IDs and price data
      const existingCategoryIds = routePrices.map(price => price.busCategory?.id).filter(Boolean)
      const firstPrice = routePrices[0] // Use first price as template for common fields
      
      // Update form with existing data
      form.selectedCategories = existingCategoryIds
      form.basePrice = firstPrice.basePrice || 500000
      form.promotionPrice = firstPrice.promotionPrice || null
      form.promotionStartDate = firstPrice.promotionStartDate ? firstPrice.promotionStartDate.split('T')[0] : ''
      form.promotionEndDate = firstPrice.promotionEndDate ? firstPrice.promotionEndDate.split('T')[0] : ''
      form.notes = firstPrice.notes || ''
      
      // Store existing category IDs for comparison
      existingCategoryIds.value = existingCategoryIds
    } else {
      // Reset to defaults
      form.selectedCategories = []
      existingCategoryIds.value = []
    }
  } catch (error) {
    console.error('❌ Error loading existing price rules:', error)
  }
}

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

    // Load existing price rules
    loadExistingPriceRules(routeData.id)
  } else {
    // Create mode
    isEditing.value = false
    editingRouteId.value = null
    existingCategoryIds.value = [] // Reset for create mode
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
  form.selectedCategories = [] // Reset selected categories
  form.basePrice = null
  form.promotionPrice = null
  form.validFrom = today // Reset to today
  form.validTo = null
  form.notes = ''
  errors.value = {}
  existingCategoryIds.value = [] // Reset existing categories tracking
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

  if (form.selectedCategories.length === 0) {
    newErrors.selectedCategories = 'Vui lòng chọn ít nhất một loại xe'
  }

  if (!form.basePrice || form.basePrice <= 0) {
    newErrors.basePrice = 'Vui lòng nhập giá cơ sở hợp lệ'
  }

  if (form.validFrom === null) {
    newErrors.validFrom = 'Vui lòng chọn ngày áp dụng từ'
  }

  if (form.validTo === null) {
    newErrors.validTo = 'Vui lòng chọn ngày áp dụng đến'
  }

  if (form.validTo < form.validFrom) {
    newErrors.validTo = 'Ngày áp dụng đến phải sau ngày áp dụng từ'
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
    
    const routeInput = {
      origin: form.origin.trim(),
      destination: form.destination.trim(),
      distanceKm: form.distanceKm,
      estimatedDurationMinutes: totalMinutes
    }
    
    let routeResponse
    if (isEditing.value) {
      routeResponse = await RouteAPI.updateRoute(editingRouteId.value, routeInput)
      emit('route-updated', routeResponse)
    } else {
      // Create route first
      routeResponse = await RouteAPI.createRoute(routeInput)
      emit('route-created', routeResponse)
    }

    // Create price rules after route is created/updated
    if (form.selectedCategories.length > 0) {
      // For editing: only create prices for NEW categories (not existing ones)
      const categoriesToCreatePrices = isEditing.value 
        ? form.selectedCategories.filter(categoryId => !existingCategoryIds.value.includes(categoryId))
        : form.selectedCategories

      if (categoriesToCreatePrices.length > 0) {
        // Create price rules for each selected category
        const pricePromises = categoriesToCreatePrices.map(async (categoryId) => {
          const priceData = {
            routeId: routeResponse.id,
            busCategoryId: categoryId,
            basePrice: form.basePrice,
            promotionPrice: form.promotionPrice || undefined,
            promotionStartDate: form.promotionStartDate || undefined,
            promotionEndDate: form.promotionEndDate || undefined,
            notes: form.notes || undefined
          }
          
          return await PriceAPI.createRouteBusCategoryPrice(priceData)
        })

        const priceResults = await Promise.all(pricePromises)
      }
    }
    
    closeModal(); // Đóng modal sau khi thành công
  } catch (error) {
    console.error('❌ Error creating route:', error)
    const action = isEditing.value ? 'cập nhật' : 'tạo'
    handleError.api(error, `${action} tuyến đường`)
  } finally {
    isSubmitting.value = false
  }
}

// Expose methods for parent component
defineExpose({
  openModal
})

// Watch for distance changes to auto-suggest price
watch(() => form.distanceKm, (newDistance) => {
  if (newDistance && newDistance > 0 && !form.basePrice) {
    // Auto-suggest price based on distance (300 VND per km)
    form.basePrice = Math.round(newDistance * 300)
  }
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