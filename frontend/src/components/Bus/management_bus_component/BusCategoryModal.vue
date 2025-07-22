<template>
  <Transition name="modal-fade">
    <div v-if="isOpen" class="fixed inset-0 z-50 overflow-y-auto flex items-center justify-center" @click="closeModal">
      <!-- Background overlay with blur -->
      <div class="fixed inset-0 bg-black/30 backdrop-blur-sm transition-opacity" aria-hidden="true"></div>
      
      <!-- Modal panel -->
      <div @click.stop class="relative bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:max-w-lg sm:w-full m-4">
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

                <!-- Category Features REMOVED -->

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
       
        </div>
      </div>
    </div>
  </Transition>
</template>

<style>
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}
.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-active .transform,
.modal-fade-leave-active .transform {
  transition: all 0.3s ease;
}

.modal-fade-enter-from .transform,
.modal-fade-leave-to .transform {
  transform: scale(0.95);
  opacity: 0;
}
</style>
<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { BusCategoryAPI } from '@/api/busApi'
import type { BusCategory, CreateBusCategoryInput, UpdateBusCategoryInput } from '@/api/busApi/bus/types'

// Emits
const emit = defineEmits(['category-created', 'category-updated'])

// State
const isOpen = ref(false)
const isEditing = ref(false)
const isSubmitting = ref(false)
const editingCategoryId = ref<string | null>(null)


// Form data
interface CategoryForm {
  name: string;
  description: string;
}
const form = reactive<CategoryForm>({
  name: '',
  description: '',
});

// Form errors
const errors = ref<{ name?: string; description?: string }>({})

// Computed
const canSubmit = computed(() => {
  return form.name.trim().length > 0 && !isSubmitting.value
})

// Methods
const openModal = (data: unknown) => {
  resetForm();

  const category = data as Partial<BusCategory>;

  if (category?.id) {
    // Edit mode
    isEditing.value = true;
    editingCategoryId.value = category.id;
    form.name = category.name || '';
  } else {
    // Create or Duplicate mode
    isEditing.value = false;
    editingCategoryId.value = null;
    form.name = category?.name || '';
  }

  isOpen.value = true;
};

const closeModal = () => {
  isOpen.value = false
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
  errors.value = {}
}

const validateForm = () => {
  const newErrors: { name?: string; description?: string } = {}
  
  if (!form.name.trim()) {
    newErrors.name = 'Vui lòng nhập tên loại xe'
  } else if (form.name.trim().length < 2) {
    newErrors.name = 'Tên loại xe phải có ít nhất 2 ký tự'
  } else if (form.name.trim().length > 100) {
    newErrors.name = 'Tên loại xe không được vượt quá 100 ký tự'
  }
  
  // Note: Description is not part of the current backend DTO/GraphQL schema for create/update.
  // If it were, validation would go here.
  // if (form.description && form.description.length > 500) {
  //   newErrors.description = 'Mô tả không được vượt quá 500 ký tự'
  // }
  
  errors.value = newErrors
  return Object.keys(newErrors).length === 0
}

const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }
  
  isSubmitting.value = true
  
  try {
    const input: CreateBusCategoryInput = {
      name: form.name.trim(),
    };
    
    let response: BusCategory;
    if (isEditing.value && editingCategoryId.value) {
      response = await BusCategoryAPI.updateBusCategory(
        editingCategoryId.value, 
        input as UpdateBusCategoryInput
      );
      emit('category-updated', response);
    } else {
      response = await BusCategoryAPI.createBusCategory(input);
      emit('category-created', response);
    }
        closeModal(); 
  } catch (err: unknown) {
    
    const error = err as any;
    const errorMessage = error.graphqlErrors?.[0]?.message || error.message || 'Có lỗi xảy ra khi lưu loại xe. Vui lòng thử lại.';
    alert(errorMessage);
  } finally {
    isSubmitting.value = false
  }
}



// Helper methods
const getPreviewIconClass = () => {  // Check if form name contains any keywords
  const name = form.name.toLowerCase()
  if (name.includes('limousine') || name.includes('limo')) return 'bg-purple-500'
  if (name.includes('vip') || name.includes('cao cấp')) return 'bg-yellow-500'
  if (name.includes('giường') || name.includes('nằm')) return 'bg-green-500'
  if (name.includes('trung chuyển') || name.includes('standard')) return 'bg-blue-500'
  
  return 'bg-gray-500'
}


// Expose methods for parent component
defineExpose({
  openModal
})

// Watch for ESC key
watch(isOpen, (newValue) => {
  if (newValue) {
    const handleEsc = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        closeModal()
      }
    };
    
    document.addEventListener('keydown', handleEsc);
    
    // Cleanup listener when the modal closes
    const unwatch = watch(isOpen, (isNowOpen) => {
      if (!isNowOpen) {
        document.removeEventListener('keydown', handleEsc);
        unwatch(); // Stop watching itself
      }
    });
  }
});
</script> 