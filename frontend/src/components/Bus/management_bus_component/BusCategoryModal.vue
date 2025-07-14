<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 overflow-y-auto" @click="closeModal">
    <div class="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
      <!-- Background overlay -->
      <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"></div>
      
      <!-- Modal panel -->
      <div @click.stop class="relative inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
        <div class="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
          <div class="sm:flex sm:items-start">
            <div class="w-full mt-3 text-center sm:mt-0 sm:text-left">
              <h3 class="text-lg leading-6 font-medium text-gray-900 mb-6">
                {{ isEditing ? 'Chỉnh sửa loại xe' : 'Thêm loại xe mới' }}
              </h3>
              
              <!-- Form -->
              <form @submit.prevent="handleSubmit" class="space-y-6">
                <!-- Category Name -->
                <div>
                  <label for="categoryName" class="block text-sm font-medium text-gray-700 mb-2">
                    Tên loại xe <span class="text-red-500">*</span>
                  </label>
                  <input
                    id="categoryName"
                    v-model="form.name"
                    type="text"
                    required
                    maxlength="100"
                    placeholder="VD: Limousine VIP"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-purple-500 focus:border-purple-500"
                  />
                  <p v-if="errors.name" class="text-red-500 text-xs mt-1">{{ errors.name }}</p>
                  <p class="text-xs text-gray-500 mt-1">Tối đa 100 ký tự</p>
                </div>

                <!-- Category Description (optional) -->
                <div>
                  <label for="categoryDescription" class="block text-sm font-medium text-gray-700 mb-2">
                    Mô tả (không bắt buộc)
                  </label>
                  <textarea
                    id="categoryDescription"
                    v-model="form.description"
                    rows="3"
                    maxlength="500"
                    placeholder="VD: Xe cao cấp với ghế ngồi thoải mái và dịch vụ 5 sao..."
                    class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-purple-500 focus:border-purple-500"
                  ></textarea>
                  <p v-if="errors.description" class="text-red-500 text-xs mt-1">{{ errors.description }}</p>
                  <p class="text-xs text-gray-500 mt-1">{{ form.description?.length || 0 }}/500 ký tự</p>
                </div>

                <!-- Category Features (predefined checkboxes) -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    Đặc điểm loại xe
                  </label>
                  <div class="grid grid-cols-2 gap-3">
                    <label v-for="feature in availableFeatures" :key="feature.id" class="flex items-center">
                      <input
                        v-model="form.features"
                        :value="feature.id"
                        type="checkbox"
                        class="rounded border-gray-300 text-purple-600 shadow-sm focus:border-purple-300 focus:ring focus:ring-offset-0 focus:ring-purple-200 focus:ring-opacity-50"
                      />
                      <span class="ml-2 text-sm text-gray-700">{{ feature.name }}</span>
                    </label>
                  </div>
                </div>

                <!-- Preview Section -->
                <div v-if="form.name" class="mt-6 p-4 bg-gray-50 rounded-md">
                  <h4 class="text-sm font-medium text-gray-900 mb-3">Xem trước:</h4>
                  <div class="space-y-2">
                    <div class="flex items-center">
                      <div :class="getPreviewIconClass()" class="w-8 h-8 rounded-lg flex items-center justify-center mr-3">
                        <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17a2 2 0 11-4 0 2 2 0 014 0zM19 17a2 2 0 11-4 0 2 2 0 014 0z"/>
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 6h3l2 7H9M9 17v-2a2 2 0 00-2-2H4"/>
                        </svg>
                      </div>
                      <div>
                        <div class="font-medium text-gray-900">{{ form.name }}</div>
                        <div class="text-sm text-gray-500">{{ form.description || 'Không có mô tả' }}</div>
                      </div>
                    </div>
                    <div v-if="form.features.length > 0" class="flex flex-wrap gap-1 mt-2">
                      <span v-for="featureId in form.features" :key="featureId" 
                            :class="getPreviewBadgeClass()" 
                            class="inline-flex px-2 py-1 text-xs font-semibold rounded-full">
                        {{ getFeatureName(featureId) }}
                      </span>
                    </div>
                  </div>
                </div>

                <!-- GraphQL Schema Preview -->
                <div v-if="showSchemaPreview" class="mt-6 p-4 bg-blue-50 rounded-md">
                  <h4 class="text-sm font-medium text-blue-900 mb-2">GraphQL Schema:</h4>
                  <pre class="text-xs text-blue-800 bg-blue-100 p-3 rounded overflow-x-auto">{{ generateGraphQLSchema() }}</pre>
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
            :disabled="isSubmitting || !canSubmit"
            class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-purple-600 text-base font-medium text-white hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500 sm:ml-3 sm:w-auto sm:text-sm disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <svg v-if="isSubmitting" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ isSubmitting ? 'Đang xử lý...' : (isEditing ? 'Cập nhật loại xe' : 'Tạo loại xe') }}
          </button>
          <button
            type="button"
            @click="closeModal"
            :disabled="isSubmitting"
            class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm disabled:opacity-50"
          >
            Hủy
          </button>
          <!-- Schema Preview Toggle -->
          <button
            type="button"
            @click="showSchemaPreview = !showSchemaPreview"
            class="mt-3 w-full inline-flex justify-center rounded-md border border-blue-300 shadow-sm px-4 py-2 bg-blue-50 text-base font-medium text-blue-700 hover:bg-blue-100 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
          >
            {{ showSchemaPreview ? 'Ẩn' : 'Xem' }} Schema
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { createBusCategory, updateBusCategory } from '@/api/busCategoryApi'

// Emits
const emit = defineEmits(['category-created', 'category-updated'])

// State
const isOpen = ref(false)
const isEditing = ref(false)
const isSubmitting = ref(false)
const editingCategoryId = ref(null)
const showSchemaPreview = ref(false)

// Form data - matching BusCategory entity
const form = reactive({
  name: '',
  description: '',
  features: [] // Additional features for UX
})

// Form errors
const errors = ref({})

// Available features for better UX
const availableFeatures = [
  { id: 'wifi', name: 'WiFi miễn phí' },
  { id: 'ac', name: 'Điều hòa' },
  { id: 'toilet', name: 'Nhà vệ sinh' },
  { id: 'entertainment', name: 'Giải trí' },
  { id: 'meal', name: 'Bữa ăn' },
  { id: 'blanket', name: 'Chăn gối' },
  { id: 'usb', name: 'Cổng sạc USB' },
  { id: 'reclining', name: 'Ghế nằm' }
]

// Computed
const canSubmit = computed(() => {
  return form.name.trim().length > 0
})

// Methods
const openModal = (categoryData = null) => {
  console.log('🚀 Opening BusCategoryModal:', categoryData)
  
  // Reset form
  resetForm()
  
  if (categoryData) {
    // Edit mode
    isEditing.value = true
    editingCategoryId.value = categoryData.id
    form.name = categoryData.name
    form.description = categoryData.description || ''
    form.features = categoryData.features || []
  } else {
    // Create mode
    isEditing.value = false
    editingCategoryId.value = null
  }
  
  isOpen.value = true
}

const closeModal = () => {
  if (isSubmitting.value) return
  
  isOpen.value = false
  showSchemaPreview.value = false
  resetForm()
  
  // Wait for animation to complete
  setTimeout(() => {
    isEditing.value = false
    editingCategoryId.value = null
  }, 300)
}

const resetForm = () => {
  form.name = ''
  form.description = ''
  form.features = []
  errors.value = {}
}

const validateForm = () => {
  const newErrors = {}
  
  if (!form.name.trim()) {
    newErrors.name = 'Vui lòng nhập tên loại xe'
  } else if (form.name.trim().length < 2) {
    newErrors.name = 'Tên loại xe phải có ít nhất 2 ký tự'
  } else if (form.name.trim().length > 100) {
    newErrors.name = 'Tên loại xe không được vượt quá 100 ký tự'
  }
  
  if (form.description && form.description.length > 500) {
    newErrors.description = 'Mô tả không được vượt quá 500 ký tự'
  }
  
  errors.value = newErrors
  return Object.keys(newErrors).length === 0
}

const handleSubmit = async () => {
  if (!validateForm()) {
    console.log('❌ Validation failed:', errors.value)
    return
  }
  
  isSubmitting.value = true
  
  try {
    // Prepare data matching backend BusCategory entity
    const categoryData = {
      name: form.name.trim(),
      description: form.description?.trim() || null,
      features: form.features // Additional field for frontend only
    }
    
    // Generate GraphQL variables for the API call
    const graphqlVariables = generateGraphQLVariables(categoryData)
    
    console.log('📤 Submitting category data:', categoryData)
    console.log('🔗 GraphQL Variables:', graphqlVariables)
    
    if (isEditing.value) {
      // Update existing category via GraphQL
      const response = await updateBusCategory(editingCategoryId.value, graphqlVariables)
      console.log('✅ Category updated:', response)
      emit('category-updated', response.data)
    } else {
      // Create new category via GraphQL
      const response = await createBusCategory(graphqlVariables)
      console.log('✅ Category created:', response)
      emit('category-created', response.data)
    }
    
    closeModal()
  } catch (error) {
    console.error('❌ Error saving category:', error)
    alert('Có lỗi xảy ra khi lưu loại xe. Vui lòng thử lại.')
  } finally {
    isSubmitting.value = false
  }
}

// GraphQL Schema Generation
const generateGraphQLSchema = () => {
  const operation = isEditing.value ? 'updateBusCategory' : 'createBusCategory'
  const variables = generateGraphQLVariables()
  
  return `
mutation ${operation.charAt(0).toUpperCase() + operation.slice(1)} {
  ${operation}(
    ${Object.entries(variables)
      .map(([key, value]) => {
        if (typeof value === 'string') return `${key}: "${value}"`
        if (Array.isArray(value)) return `${key}: [${value.map(v => `"${v}"`).join(', ')}]`
        return `${key}: ${value}`
      })
      .join('\n    ')}
  ) {
    id
    name
    description
    createdAt
    updatedAt
  }
}`
}

const generateGraphQLVariables = (data = null) => {
  const formData = data || {
    name: form.name.trim(),
    description: form.description?.trim() || null,
    features: form.features
  }
  
  const variables = {
    name: formData.name
  }
  
  if (formData.description) {
    variables.description = formData.description
  }
  
  if (isEditing.value) {
    variables.id = editingCategoryId.value
  }
  
  return variables
}

// Helper methods
const getFeatureName = (featureId) => {
  const feature = availableFeatures.find(f => f.id === featureId)
  return feature ? feature.name : featureId
}

const getPreviewIconClass = () => {
  const colorMap = {
    'Trung chuyển': 'bg-blue-500',
    'Giường nằm': 'bg-green-500',
    'Limousine': 'bg-purple-500',
    'VIP': 'bg-yellow-500'
  }
  
  // Check if form name contains any keywords
  const name = form.name.toLowerCase()
  if (name.includes('limousine') || name.includes('limo')) return 'bg-purple-500'
  if (name.includes('vip') || name.includes('cao cấp')) return 'bg-yellow-500'
  if (name.includes('giường') || name.includes('nằm')) return 'bg-green-500'
  if (name.includes('trung chuyển') || name.includes('standard')) return 'bg-blue-500'
  
  return 'bg-gray-500'
}

const getPreviewBadgeClass = () => {
  const name = form.name.toLowerCase()
  if (name.includes('limousine')) return 'bg-purple-100 text-purple-800'
  if (name.includes('vip')) return 'bg-yellow-100 text-yellow-800'
  if (name.includes('giường')) return 'bg-green-100 text-green-800'
  return 'bg-blue-100 text-blue-800'
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