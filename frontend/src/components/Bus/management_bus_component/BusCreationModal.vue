<template>
  <teleport to="body">
    <transition name="modal-overlay" appear>
      <div v-if="isOpen" @click="closeModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-6">
        <transition name="modal-content" appear>
          <div @click.stop class="relative bg-white rounded-xl shadow-2xl w-full max-w-4xl max-h-[90vh] flex flex-col overflow-hidden">
            <!-- Header -->
            <div class="bg-gradient-to-r from-blue-600 to-blue-700 px-6 py-4 flex-shrink-0">
              <div class="flex items-center justify-between">
                <h3 class="text-xl font-semibold text-white">
                  {{ isEditMode ? 'Chỉnh sửa xe buýt' : 'Tạo xe buýt mới' }}
                </h3>
                <button @click="closeModal" class="text-white/80 hover:text-white transition-colors">
                  <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                  </svg>
                </button>
              </div>
            </div>

            <!-- Content -->
            <div class="flex-1 overflow-y-auto">
              <form @submit.prevent="handleSubmit" class="p-6 space-y-8">
                
                <!-- Section 1: Image Upload -->
                <section class="space-y-4">
                  <div class="flex items-center space-x-2">
                    <div class="w-2 h-6 bg-blue-600 rounded-full"></div>
                    <h4 class="text-lg font-semibold text-gray-900">Hình ảnh xe buýt</h4>
                  </div>
                  
                  <div @dragover.prevent @drop.prevent="handleDrop" 
                       class="border-2 border-dashed border-gray-300 rounded-lg hover:border-blue-400 transition-colors duration-200">
        <div class="p-6">
                      <!-- Image Previews -->
                      <div v-if="existingImages.length > 0 || imagePreviews.length > 0" class="mb-6">
                        <div class="relative">
                          <div class="text-sm text-gray-600 mb-2 flex items-center justify-between">
                            <span>{{ existingImages.length + imagePreviews.length }} ảnh đã chọn</span>
                            <span class="text-xs text-gray-400" v-if="existingImages.length + imagePreviews.length > 6">Cuộn để xem thêm</span>
                          </div>
                          <div class="max-h-40 overflow-y-auto border border-gray-200 rounded-lg p-4 bg-gray-50">
                        <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-4">
                    <!-- Existing images -->
                          <div v-for="(image, index) in existingImages" :key="'existing-' + image.id" 
                               class="relative group">
                            <img :src="image.image.url" 
                                 class="w-full h-20 object-cover rounded-lg border-2 border-blue-200 shadow-sm" />
                            <button @click.prevent="removeExistingImage(index)" 
                                    class="absolute -top-2 -right-2 bg-red-500 hover:bg-red-600 text-white rounded-full w-6 h-6 flex items-center justify-center text-sm transition-colors opacity-0 group-hover:opacity-100">
                        ×
                      </button>
                            <div class="absolute bottom-1 left-1 bg-blue-600 text-white text-xs px-2 py-0.5 rounded">
                              Có sẵn
                            </div>
                    </div>
                    <!-- New uploaded images -->
                          <div v-for="(src, index) in imagePreviews" :key="'new-' + index" 
                               class="relative group">
                            <img :src="src" 
                                 class="w-full h-20 object-cover rounded-lg border-2 border-green-200 shadow-sm" />
                            <button @click.prevent="removeNewImage(index)" 
                                    class="absolute -top-2 -right-2 bg-red-500 hover:bg-red-600 text-white rounded-full w-6 h-6 flex items-center justify-center text-sm transition-colors opacity-0 group-hover:opacity-100">
                        ×
                      </button>
                            <div class="absolute bottom-1 left-1 bg-green-600 text-white text-xs px-2 py-0.5 rounded">
                              Mới
                                  </div>
                                </div>
                            </div>
                          </div>
                        </div>
                      </div>
                      
                      <!-- Upload Area -->
                      <div class="text-center">
                        <div class="mx-auto w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
                          <svg class="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"></path>
                          </svg>
                        </div>
                        <div class="space-y-2">
                          <label for="file-upload" class="relative cursor-pointer bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-lg transition-colors duration-200 inline-block">
                            <span>Chọn ảnh từ máy</span>
                            <input id="file-upload" name="file-upload" type="file" class="sr-only" multiple accept="image/*" @change="handleFileSelect">
                          </label>
                          <p class="text-sm text-gray-500">hoặc kéo và thả ảnh vào đây</p>
                          <p class="text-xs text-gray-400">PNG, JPG lên đến 10MB</p>
                        </div>
                      </div>
                    </div>
                  </div>
                </section>

                <!-- Section 2: Basic Information -->
                <section class="space-y-4">
                  <div class="flex items-center space-x-2">
                    <div class="w-2 h-6 bg-green-600 rounded-full"></div>
                    <h4 class="text-lg font-semibold text-gray-900">Thông tin cơ bản</h4>
                  </div>
                  
                  <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div class="space-y-2">
                      <label class="block text-sm font-medium text-gray-700">Tên xe buýt</label>
                      <input v-model="form.name" 
                             placeholder="VD: Limousine Hoàng Gia" 
                             required 
                             class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200"/>
                    </div>
                    
                    <div class="space-y-2">
                      <label class="block text-sm font-medium text-gray-700">Biển số xe</label>
                      <input v-model="form.licensePlate" 
                             placeholder="VD: 51F-12345" 
                             required 
                             class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200"/>
                    </div>
                    
                    <div class="space-y-2">
                      <label class="block text-sm font-medium text-gray-700">Tổng số ghế</label>
                      <input v-model.number="form.totalSeats" 
                             type="number" 
                             placeholder="VD: 40" 
                             min="1" 
                             max="100" 
                             required 
                             class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200"/>
                    </div>
                    
                    <div class="space-y-2">
                      <label class="block text-sm font-medium text-gray-700">Loại xe</label>
                      <select v-model="form.categoryId" 
                              required 
                              class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
                        <option disabled value="">Chọn loại xe</option>
                        <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                          {{ cat.name }}
                        </option>
                      </select>
                    </div>
                  </div>
                </section>

                <!-- Section 3: Amenities -->
                <section class="space-y-4">
                  <div class="flex items-center justify-between">
                    <div class="flex items-center space-x-2">
                      <div class="w-2 h-6 bg-purple-600 rounded-full"></div>
                      <h4 class="text-lg font-semibold text-gray-900">Tiện ích xe buýt</h4>
                    </div>
                    <span class="text-sm text-gray-500">{{ selectedAmenities.length }} tiện ích đã chọn</span>
                  </div>
                  
                  <!-- Auto-create note -->
                  <div class="bg-green-50 border border-green-200 rounded-lg p-4">
                    <div class="flex items-start space-x-3">
                      <div class="flex-shrink-0">
                        <svg class="w-5 h-5 text-green-600 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
                          <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
                        </svg>
                      </div>
                      <div>
                        <h5 class="text-sm font-medium text-green-800">Tự động tạo tiện ích</h5>
                        <p class="text-sm text-green-700 mt-1">
                          Hệ thống sẽ tự động tạo các tiện ích mới nếu chưa có trong cơ sở dữ liệu.
                        </p>
                      </div>
                    </div>
                  </div>
                  
                  <!-- Amenities Grid -->
                  <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-3">
                    <div v-for="amenity in availableAmenities" :key="amenity.name" 
                         class="relative flex items-center p-4 border rounded-lg cursor-pointer transition-all duration-200 hover:shadow-md"
                         :class="selectedAmenities.includes(amenity.name) 
                           ? 'border-blue-500 bg-blue-50 shadow-sm' 
                           : 'border-gray-200 hover:border-gray-300'"
                         @click="toggleAmenity(amenity.name)">
                      <div class="flex items-center space-x-3 w-full">
                        <div class="flex-shrink-0">
                          <component :is="amenity.icon" 
                                     class="w-5 h-5 transition-colors duration-200"
                                     :class="selectedAmenities.includes(amenity.name) ? 'text-blue-600' : 'text-gray-500'" />
                        </div>
                        <div class="flex-1 min-w-0">
                          <p class="text-sm font-medium text-gray-900 truncate">{{ amenity.name }}</p>
                        </div>
                        <div class="flex-shrink-0">
                          <div class="w-5 h-5 rounded border-2 flex items-center justify-center transition-all duration-200"
                               :class="selectedAmenities.includes(amenity.name) 
                                 ? 'border-blue-500 bg-blue-500' 
                                 : 'border-gray-300'">
                            <svg v-if="selectedAmenities.includes(amenity.name)" 
                                 class="w-3 h-3 text-white" fill="currentColor" viewBox="0 0 20 20">
                              <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"/>
                            </svg>
                          </div>
                  </div>
                </div>
              </div>
            </div>

                  <!-- Selected amenities summary -->
                  <div v-if="selectedAmenities.length > 0" class="bg-blue-50 border border-blue-200 rounded-lg p-4">
                    <h5 class="text-sm font-medium text-blue-800 mb-3">
                      Tiện ích đã chọn ({{ selectedAmenities.length }})
                    </h5>
                    <div class="flex flex-wrap gap-2">
                      <span v-for="amenity in selectedAmenities" :key="amenity" 
                            class="inline-flex items-center px-3 py-1 text-sm font-medium bg-blue-100 text-blue-800 rounded-full group hover:bg-blue-200 transition-colors duration-200">
                        {{ amenity }}
                        <button @click.stop="toggleAmenity(amenity)" 
                                class="ml-2 inline-flex items-center justify-center w-4 h-4 text-blue-600 hover:text-blue-800 opacity-70 group-hover:opacity-100 transition-opacity duration-200">
                          <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
                            <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"/>
                          </svg>
                        </button>
                      </span>
                    </div>
                  </div>
                </section>
                
              </form>
            </div>
            
            <!-- Footer Actions -->
            <div class="border-t bg-gray-50 px-6 py-4 flex-shrink-0">
            <div class="flex justify-end space-x-4">
                <button type="button" @click="closeModal" 
                        class="px-6 py-2.5 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors duration-200">
                  Hủy bỏ
                </button>
                <button type="submit" @click="handleSubmit" :disabled="isSubmitting" 
                        class="px-6 py-2.5 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200">
                  <span v-if="isSubmitting" class="flex items-center">
                    <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    {{ isEditMode ? 'Đang cập nhật...' : 'Đang tạo...' }}
                  </span>
                  <span v-else>
                    {{ isEditMode ? 'Cập nhật xe buýt' : 'Tạo xe buýt' }}
                  </span>
              </button>
              </div>
            </div>
        </div>
        </transition>
      </div>
    </transition>
  </teleport>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { 
  WifiIcon, 
  MonitorIcon, 
  CoffeeIcon, 
  BatteryIcon, 
  AirplayIcon, 
  LightbulbIcon,
  ShieldIcon,
  CameraIcon,
  ThermometerIcon,
  VolumeXIcon,
  MapPinIcon,
  HeartHandshakeIcon
} from 'lucide-vue-next';
import { ImageAPI } from '@/api/busApi/imageApi';
import { BusAPI } from '@/api/busApi/bus/api';
import * as BusCategoryAPI from '@/api/busApi/bus/categoryApi';
// @ts-ignore
import { useAuth } from '@/composables/useAuth';
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

// Amenities state
const selectedAmenities = ref<string[]>([]);

// Hard-coded available amenities
const availableAmenities = ref([
  { name: 'WiFi miễn phí', icon: WifiIcon },
  { name: 'TV/Màn hình giải trí', icon: MonitorIcon },
  { name: 'Nước uống miễn phí', icon: CoffeeIcon },
  { name: 'Sạc điện thoại USB', icon: BatteryIcon },
  { name: 'Điều hòa không khí', icon: AirplayIcon },
  { name: 'Ghế massage', icon: HeartHandshakeIcon },
  { name: 'Camera an ninh', icon: CameraIcon },
  { name: 'Cửa sổ tự động', icon: ShieldIcon },
  { name: 'Kiểm soát nhiệt độ', icon: ThermometerIcon },
  { name: 'Cách âm tốt', icon: VolumeXIcon },
  { name: 'GPS theo dõi', icon: MapPinIcon },
  { name: 'Đèn đọc sách', icon: LightbulbIcon }
]);

const form = reactive<Omit<CreateBusInput, 'imageIds' | 'ownerId'>>({
  name: '',
  licensePlate: '',
  totalSeats: 0,
  categoryId: '',
});

// Amenities functions
const toggleAmenity = (amenityName: string) => {
  const index = selectedAmenities.value.indexOf(amenityName);
  if (index > -1) {
    selectedAmenities.value.splice(index, 1);
  } else {
    selectedAmenities.value.push(amenityName);
  }
};

onMounted(async () => {
  // ✅ Load categories by owner ID (BusCategory thuộc về doanh nghiệp)
  const { requireUserId } = useAuth()
  const ownerId = requireUserId()
  categories.value = await BusCategoryAPI.getBusCategoriesByOwnerId(ownerId);
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
    
    // Load existing amenities from backend (now supported in schema)
    selectedAmenities.value = bus.amenities && Array.isArray(bus.amenities) 
      ? bus.amenities.map(a => a.name) 
      : [];
    
  } else {
    // Create mode
    isEditMode.value = false;
    editingBusId.value = null;
    selectedAmenities.value = [];
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
      
      const uploadPromises = imageFiles.value.map(async (file, index) => {
        try {
          const uploadResult = await ImageAPI.uploadImage(file);
          
          if (!uploadResult || !uploadResult.url || !uploadResult.publicId) {
            console.error(`Invalid upload result for file ${file.name}:`, uploadResult);
            throw new Error(`Upload failed for ${file.name}: missing url or publicId`);
          }
          
          const imageRecord = await ImageAPI.createImageRecord(uploadResult);
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
      
    }

    // 2. Create or Update bus
    const { requireUserId } = useAuth();
    const ownerId = parseInt(requireUserId()); // ✅ Sử dụng token động
    
    if (isEditMode.value && editingBusId.value) {
      // Update existing bus
      const updateInput: UpdateBusInput = {
        name: form.name,
        licensePlate: form.licensePlate,
        totalSeats: form.totalSeats,
        categoryId: form.categoryId,
        ownerId,
        amenityNames: selectedAmenities.value, // Send amenityNames
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
        amenityNames: selectedAmenities.value, // Send amenityNames
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
  selectedAmenities.value = []; // Reset amenities selection
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