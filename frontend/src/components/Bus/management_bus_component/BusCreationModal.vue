<template>
  <teleport to="body">
    <transition name="modal-overlay" appear>
      <div v-if="isOpen" @click="closeModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
        <transition name="modal-content" appear>
          <div @click.stop class="relative bg-white rounded-xl shadow-2xl w-full max-w-3xl mx-4 overflow-hidden">
            <div class="p-6">
              <h3 class="text-lg font-medium text-gray-900">{{ isEditMode ? 'Chỉnh sửa xe buýt' : 'Tạo xe buýt mới' }}</h3>
              
              <form @submit.prevent="handleSubmit" class="mt-6 space-y-6">
                <!-- Image Uploader -->
                <div>
                  <label class="block text-sm font-medium text-gray-700">Hình ảnh xe</label>
                  <div @dragover.prevent @drop.prevent="handleDrop" class="mt-1 flex justify-center px-6 pt-5 pb-6 border-2 border-gray-300 border-dashed rounded-md">
                    <div class="space-y-1 text-center">
                      <!-- Existing images (from server) -->
                      <div v-if="existingImages.length > 0 || imagePreviews.length > 0" class="flex flex-wrap gap-4 justify-center mb-4">
                        <!-- Existing images -->
                        <div v-for="(image, index) in existingImages" :key="'existing-' + image.id" class="relative">
                          <img :src="image.image.url" class="h-24 w-24 object-cover rounded-md border-2 border-blue-300" />
                          <button @click.prevent="removeExistingImage(index)" class="absolute -top-2 -right-2 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center text-sm hover:bg-red-600 transition-colors">
                            ×
                          </button>
                          <div class="absolute bottom-1 left-1 bg-blue-600 text-white text-xs px-1 rounded">Có sẵn</div>
                        </div>
                        <!-- New uploaded images -->
                        <div v-for="(src, index) in imagePreviews" :key="'new-' + index" class="relative">
                          <img :src="src" class="h-24 w-24 object-cover rounded-md border-2 border-green-300" />
                          <button @click.prevent="removeNewImage(index)" class="absolute -top-2 -right-2 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center text-sm hover:bg-red-600 transition-colors">
                            ×
                          </button>
                          <div class="absolute bottom-1 left-1 bg-green-600 text-white text-xs px-1 rounded">Mới</div>
                        </div>
                      </div>
                      <div class="text-sm text-gray-600">
                        <label for="file-upload" class="relative cursor-pointer bg-white rounded-md font-medium text-indigo-600 hover:text-indigo-500">
                          <span>Tải ảnh lên</span>
                          <input id="file-upload" name="file-upload" type="file" class="sr-only" multiple @change="handleFileSelect">
                        </label>
                        <p class="pl-1">hoặc kéo và thả vào đây</p>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Bus Details -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <input v-model="form.name" placeholder="Tên xe (VD: Limousine Hoàng Gia)" required class="p-2 border rounded"/>
                  <input v-model="form.licensePlate" placeholder="Biển số xe (VD: 51F-12345)" required class="p-2 border rounded"/>
                  <input v-model.number="form.totalSeats" type="number" placeholder="Tổng số ghế" required class="p-2 border rounded"/>
                  <select v-model="form.categoryId" required class="p-2 border rounded">
                    <option disabled value="">Chọn loại xe</option>
                    <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
                  </select>
                </div>
                
                <div class="flex justify-end space-x-4">
                  <button type="button" @click="closeModal" class="px-4 py-2 rounded bg-gray-200">Hủy</button>
                  <button type="submit" :disabled="isSubmitting" class="px-4 py-2 rounded bg-indigo-600 text-white disabled:bg-indigo-300">
                    {{ isSubmitting ? (isEditMode ? 'Đang cập nhật...' : 'Đang tạo...') : (isEditMode ? 'Cập nhật xe buýt' : 'Tạo xe buýt') }}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </transition>
      </div>
    </transition>
  </teleport>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ImageAPI } from '@/api/busApi/imageApi';
import { BusAPI } from '@/api/busApi/bus/api';
import * as BusCategoryAPI from '@/api/busApi/bus/categoryApi';
import type { CreateBusInput, UpdateBusInput } from '@/api/busApi/bus/types';
import type { BusCategory } from '@/api/busApi/types/common.types';
import type { Bus } from '@/api/busApi/types/common.types';
// @ts-ignore
import { toast, handleError } from '@/utils/notifications'

const emit = defineEmits(['bus-created', 'bus-updated']);

const isOpen = ref(false);
const isSubmitting = ref(false);
const isEditMode = ref(false);
const editingBusId = ref<string | null>(null);
const imageFiles = ref<File[]>([]);
const imagePreviews = ref<string[]>([]);
const categories = ref<BusCategory[]>([]);
const existingImages = ref<any[]>([]);
const imagesToDelete = ref<string[]>([]);

const form = reactive<Omit<CreateBusInput, 'imageIds' | 'ownerId'>>({
  name: '',
  licensePlate: '',
  totalSeats: 0,
  categoryId: '',
});

onMounted(async () => {
  categories.value = await BusCategoryAPI.getAllBusCategories();
});

const openModal = (bus: Bus | null = null) => {
  if (bus) {
    // Edit mode
    isEditMode.value = true;
    editingBusId.value = bus.id;
    form.name = bus.name;
    form.licensePlate = bus.licensePlate;
    form.totalSeats = bus.totalSeats;
    // @ts-ignore allow optional
    form.categoryId = (bus as any).categoryId ?? (bus.category as any)?.id ?? '';
    
    // Load existing images
    existingImages.value = bus.busImages ? [...bus.busImages] : [];
    imagesToDelete.value = [];
  } else {
    // Create mode
    isEditMode.value = false;
    editingBusId.value = null;
  }
  isOpen.value = true;
};
const closeModal = () => { 
  isOpen.value = false; 
  isEditMode.value = false;
  editingBusId.value = null;
  resetForm(); 
};

const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files) {
    addFiles([...target.files]);
  }
};

const handleDrop = (event: DragEvent) => {
  if (event.dataTransfer?.files) {
    addFiles([...event.dataTransfer.files]);
  }
};

const addFiles = (files: File[]) => {
  files.forEach(file => {
    imageFiles.value.push(file);
    imagePreviews.value.push(URL.createObjectURL(file));
  });
};

const removeImage = (index: number) => {
  imageFiles.value.splice(index, 1);
  imagePreviews.value.splice(index, 1);
};

const removeExistingImage = (index: number) => {
  const imageToDelete = existingImages.value[index];
  if (imageToDelete && imageToDelete.id) {
    imagesToDelete.value.push(imageToDelete.id);
  }
  existingImages.value.splice(index, 1);
};

const removeNewImage = (index: number) => {
  imageFiles.value.splice(index, 1);
  imagePreviews.value.splice(index, 1);
};

const handleSubmit = async () => {
  isSubmitting.value = true;
  try {
    // 1. Upload images and get their IDs (if any new images)
    let imageIds: string[] = [];
    if (imageFiles.value.length > 0) {
      console.log('Uploading', imageFiles.value.length, 'images...');
      
      const uploadPromises = imageFiles.value.map(async (file, index) => {
        try {
          console.log(`Uploading image ${index + 1}:`, file.name);
          const uploadResult = await ImageAPI.uploadImage(file);
          console.log(`Upload result ${index + 1}:`, uploadResult);
          
          if (!uploadResult || !uploadResult.url || !uploadResult.publicId) {
            console.error(`Invalid upload result for file ${file.name}:`, uploadResult);
            throw new Error(`Upload failed for ${file.name}: missing url or publicId`);
          }
          
          const imageRecord = await ImageAPI.createImageRecord(uploadResult);
          console.log(`Created image record ${index + 1}:`, imageRecord);
          return imageRecord;
        } catch (error) {
          console.error(`Error processing image ${file.name}:`, error);
          throw error;
        }
      });
      
      const imageRecords = await Promise.all(uploadPromises);
      
      // Filter out any undefined records and get IDs
      imageIds = imageRecords
        .filter(record => record && record.id)
        .map(record => record.id);
      
      console.log('Final image IDs:', imageIds);
    }

    // 2. Create or Update bus
    const ownerId = 11; // TODO: Get ownerId from logged-in user state
    
    if (isEditMode.value && editingBusId.value) {
      // Update existing bus
      const updateInput: UpdateBusInput = {
        name: form.name,
        licensePlate: form.licensePlate,
        totalSeats: form.totalSeats,
        categoryId: form.categoryId,
        ownerId,
        ...(imageIds.length > 0 && { imageIds }) // Only include imageIds if there are new images
        // TODO: Implement imagesToDelete when backend supports it
        // ...(imagesToDelete.value.length > 0 && { imagesToDelete: imagesToDelete.value })
      };
      
      await BusAPI.updateBus(editingBusId.value, updateInput);
      emit('bus-updated');
    } else {
      // Create new bus
      const createInput: CreateBusInput = {
        ...form,
        ownerId,
        imageIds,
      };
      
      await BusAPI.createBus(createInput);
      emit('bus-created');
    }
    
    closeModal();
  } catch (error) {
    console.error('Submit error:', error);
    const action = isEditMode.value ? 'cập nhật' : 'tạo'
    handleError.api(error, `${action} xe buýt`)
  } finally {
    isSubmitting.value = false;
  }
};

const resetForm = () => {
  Object.assign(form, { name: '', licensePlate: '', totalSeats: 0, categoryId: '' });
  imageFiles.value = [];
  imagePreviews.value = [];
  existingImages.value = [];
  imagesToDelete.value = [];
};

defineExpose({ openModal });
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