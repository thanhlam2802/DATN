<template>
  <div class="space-y-6">
    <!-- Bước 1: Thông tin cơ bản -->
    <div v-if="currentStep === 1" class="space-y-6">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- Tên nhà xe -->
        <div class="space-y-2">
          <label class="block text-sm font-medium text-gray-700">Tên nhà xe <span class="text-red-500">*</span></label>
          <input
            v-model="formData.companyName"
            type="text"
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200"
            placeholder="Nhập tên nhà xe"
          />
          <p v-if="validationErrors.companyName" class="text-sm text-red-600">{{ validationErrors.companyName }}</p>
        </div>

        <!-- Loại xe -->
        <div class="space-y-2">
          <label class="block text-sm font-medium text-gray-700">Loại xe <span class="text-red-500">*</span></label>
          <select
            v-model="formData.busType"
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200 appearance-none bg-white"
          >
            <option value="">Chọn loại xe</option>
            <option value="standard">Trung chuyển</option>
            <option value="sleeper">Giường nằm</option>
          </select>
          <p v-if="validationErrors.busType" class="text-sm text-red-600">{{ validationErrors.busType }}</p>
        </div>

        <!-- Biển số xe -->
        <div class="space-y-2">
          <label class="block text-sm font-medium text-gray-700">Biển số xe <span class="text-red-500">*</span></label>
          <input
            v-model="formData.licensePlate"
            type="text"
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200"
            placeholder="VD: 51A-123.45"
          />
          <p v-if="validationErrors.licensePlate" class="text-sm text-red-600">{{ validationErrors.licensePlate }}</p>
        </div>
      </div>

      <!-- Mô tả -->
      <div class="space-y-2">
        <label class="block text-sm font-medium text-gray-700">Mô tả</label>
        <textarea
          v-model="formData.description"
          rows="3"
          class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200"
          placeholder="Mô tả về xe (tùy chọn)"
        ></textarea>
      </div>

      <!-- Upload ảnh -->
      <div class="space-y-4">
        <label class="block text-sm font-medium text-gray-700">Hình ảnh xe <span class="text-red-500">*</span></label>
        
        <!-- Ảnh ngoại thất -->
        <div class="space-y-2">
          <label class="block text-sm text-gray-600">Ảnh ngoại thất</label>
          <div class="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center hover:border-gray-400 transition-colors">
            <input
              ref="exteriorFileInput"
              type="file"
              accept="image/*"
              multiple
              class="hidden"
              @change="handleFileSelect($event, 'exterior')"
            />
            
            <div v-if="exteriorImageCount === 0">
              <svg class="mx-auto h-12 w-12 text-gray-400" stroke="currentColor" fill="none" viewBox="0 0 48 48">
                <path d="M28 8H12a4 4 0 00-4 4v20m32-12v8m0 0v8a4 4 0 01-4 4H12a4 4 0 01-4-4v-4m32-4l-3.172-3.172a4 4 0 00-5.656 0L28 28M8 32l9.172-9.172a4 4 0 015.656 0L28 28m0 0l4 4m4-24h8m-4-4v8m-12 4h.02" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
              <p class="mt-2 text-sm text-gray-600">
                <button @click="triggerFileInput('exterior')" class="font-medium text-blue-600 hover:text-blue-500">Chọn ảnh ngoại thất</button>
              </p>
              <p class="text-xs text-gray-500">PNG, JPG lên đến 10MB</p>
            </div>
            
            <!-- Simple status display -->
            <div v-else class="flex items-center justify-center space-x-3">
              <svg class="w-8 h-8 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
              <span class="text-green-600 font-medium">Đã chọn {{ exteriorImageCount }} ảnh ngoại thất</span>
              <button @click="triggerFileInput('exterior')" class="text-blue-600 hover:text-blue-700 text-sm">
                Chọn lại
              </button>
            </div>
          </div>
        </div>

        <!-- Ảnh nội thất -->
        <div class="space-y-2">
          <label class="block text-sm text-gray-600">Ảnh nội thất</label>
          <div class="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center hover:border-gray-400 transition-colors">
            <input
              ref="interiorFileInput"
              type="file"
              accept="image/*"
              multiple
              class="hidden"
              @change="handleFileSelect($event, 'interior')"
            />
            
            <div v-if="interiorImageCount === 0">
              <svg class="mx-auto h-12 w-12 text-gray-400" stroke="currentColor" fill="none" viewBox="0 0 48 48">
                <path d="M28 8H12a4 4 0 00-4 4v20m32-12v8m0 0v8a4 4 0 01-4 4H12a4 4 0 01-4-4v-4m32-4l-3.172-3.172a4 4 0 00-5.656 0L28 28M8 32l9.172-9.172a4 4 0 015.656 0L28 28m0 0l4 4m4-24h8m-4-4v8m-12 4h.02" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
              <p class="mt-2 text-sm text-gray-600">
                <button @click="triggerFileInput('interior')" class="font-medium text-blue-600 hover:text-blue-500">Chọn ảnh nội thất</button>
              </p>
              <p class="text-xs text-gray-500">PNG, JPG lên đến 10MB</p>
            </div>
            
            <!-- Simple status display -->
            <div v-else class="flex items-center justify-center space-x-3">
              <svg class="w-8 h-8 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
              <span class="text-green-600 font-medium">Đã chọn {{ interiorImageCount }} ảnh nội thất</span>
              <button @click="triggerFileInput('interior')" class="text-blue-600 hover:text-blue-700 text-sm">
                Chọn lại
              </button>
            </div>
          </div>
        </div>
        
        <p v-if="validationErrors.images" class="text-sm text-red-600">{{ validationErrors.images }}</p>
      </div>
    </div>

    <!-- Bước 2: Lịch trình -->
    <div v-if="currentStep === 2" class="space-y-6">
      <!-- Điểm đi/đến -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="space-y-4">
          <h4 class="font-medium text-gray-900">Điểm khởi hành</h4>
          <div class="space-y-3">
            <div>
              <label class="block text-sm font-medium text-gray-700">Thành phố <span class="text-red-500">*</span></label>
              <select
                v-model="formData.departure.city"
                class="mt-1 w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 appearance-none bg-white"
              >
                <option value="">Chọn thành phố</option>
                <option value="hanoi">Hà Nội</option>
                <option value="hcm">TP. Hồ Chí Minh</option>
                <option value="danang">Đà Nẵng</option>
                <option value="cantho">Cần Thơ</option>
              </select>
              <p v-if="validationErrors.departureCity" class="text-sm text-red-600">{{ validationErrors.departureCity }}</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">Địa chỉ cụ thể</label>
              <input
                v-model="formData.departure.address"
                type="text"
                class="mt-1 w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="Địa chỉ bến xe/điểm đón"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">Giờ khởi hành <span class="text-red-500">*</span></label>
              <input
                v-model="formData.departure.time"
                type="time"
                class="mt-1 w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              />
              <p v-if="validationErrors.departureTime" class="text-sm text-red-600">{{ validationErrors.departureTime }}</p>
            </div>
          </div>
        </div>

        <div class="space-y-4">
          <h4 class="font-medium text-gray-900">Điểm đến</h4>
          <div class="space-y-3">
            <div>
              <label class="block text-sm font-medium text-gray-700">Thành phố <span class="text-red-500">*</span></label>
              <select
                v-model="formData.arrival.city"
                class="mt-1 w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 appearance-none bg-white"
              >
                <option value="">Chọn thành phố</option>
                <option value="hanoi">Hà Nội</option>
                <option value="hcm">TP. Hồ Chí Minh</option>
                <option value="danang">Đà Nẵng</option>
                <option value="cantho">Cần Thơ</option>
              </select>
              <p v-if="validationErrors.arrivalCity" class="text-sm text-red-600">{{ validationErrors.arrivalCity }}</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">Địa chỉ cụ thể</label>
              <input
                v-model="formData.arrival.address"
                type="text"
                class="mt-1 w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="Địa chỉ bến xe/điểm trả"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">Giờ đến <span class="text-red-500">*</span></label>
              <input
                v-model="formData.arrival.time"
                type="time"
                class="mt-1 w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              />
              <p v-if="validationErrors.arrivalTime" class="text-sm text-red-600">{{ validationErrors.arrivalTime }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Ngày hoạt động -->
      <div class="space-y-3">
        <label class="block text-sm font-medium text-gray-700">Ngày hoạt động <span class="text-red-500">*</span></label>
        <div class="grid grid-cols-4 md:grid-cols-7 gap-2">
          <label v-for="day in weekDays" :key="day.id" class="flex items-center space-x-2 cursor-pointer">
            <input
              v-model="formData.operatingDays"
              :value="day.value"
              type="checkbox"
              class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
            />
            <span class="text-sm text-gray-700">{{ day.name }}</span>
          </label>
        </div>
        <p v-if="validationErrors.operatingDays" class="text-sm text-red-600">{{ validationErrors.operatingDays }}</p>
      </div>

      <!-- Thời gian di chuyển -->
      <div class="space-y-2">
        <label class="block text-sm font-medium text-gray-700">Thời gian di chuyển <span class="text-red-500">*</span></label>
        <input
          v-model="formData.travelTime"
          type="text"
          class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          placeholder="VD: 8 giờ 30 phút"
        />
        <p v-if="validationErrors.travelTime" class="text-sm text-red-600">{{ validationErrors.travelTime }}</p>
      </div>

      <!-- Điểm dừng chân -->
      <div class="space-y-4">
        <div class="flex items-center justify-between">
          <label class="block text-sm font-medium text-gray-700">Điểm dừng chân</label>
          <button
            @click="addStopPoint"
            type="button"
            class="px-3 py-1 text-sm text-blue-600 hover:text-blue-700 border border-blue-300 rounded-md hover:bg-blue-50 transition-colors"
          >
            + Thêm điểm dừng
          </button>
        </div>
        
        <div v-if="formData.stopPoints.length === 0" class="text-center py-8 text-gray-500">
          <p>Chưa có điểm dừng chân nào</p>
        </div>

        <div v-for="(stop, index) in formData.stopPoints" :key="stop.id" class="border border-gray-200 rounded-lg p-4 space-y-3">
          <div class="flex items-center justify-between">  
            <h5 class="font-medium text-gray-900">Điểm dừng {{ index + 1 }}</h5>
            <button
              @click="removeStopPoint(index)"
              type="button"
              class="text-red-600 hover:text-red-700 text-sm"
            >
              Xóa
            </button>
          </div>
          
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700">Tên điểm dừng</label>
              <input
                v-model="stop.name"
                type="text"
                class="mt-1 w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="VD: Trạm dừng Dầu Giây"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">Địa chỉ</label>
              <input
                v-model="stop.address"
                type="text"
                class="mt-1 w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="Địa chỉ cụ thể"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">Giờ đến</label>
              <input
                v-model="stop.arrivalTime"
                type="time"
                class="mt-1 w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">Giờ khởi hành</label>
              <input
                v-model="stop.departureTime"
                type="time"
                class="mt-1 w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Bước 3: Giá vé & Dịch vụ -->
    <div v-if="currentStep === 3" class="space-y-6">
      <!-- Giá vé -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="space-y-2">
          <label class="block text-sm font-medium text-gray-700">Giá vé (VNĐ) <span class="text-red-500">*</span></label>
          <input
            v-model.number="formData.ticketPrice"
            type="number"
            min="0"
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            placeholder="350000"
          />
          <p v-if="validationErrors.ticketPrice" class="text-sm text-red-600">{{ validationErrors.ticketPrice }}</p>
        </div>

        <div class="space-y-2">
          <label class="block text-sm font-medium text-gray-700">Phần trăm giảm giá (%)</label>
          <input
            v-model.number="formData.discountPercent"
            type="number"
            min="0"
            max="100"
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            placeholder="0"
          />
          <p v-if="validationErrors.discountPercent" class="text-sm text-red-600">{{ validationErrors.discountPercent }}</p>
        </div>
      </div>

      <!-- Dịch vụ trên xe -->
      <div class="space-y-3">
        <label class="block text-sm font-medium text-gray-700">Dịch vụ trên xe</label>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-3">
          <label v-for="service in availableServices" :key="service.id" class="flex items-center space-x-2 cursor-pointer p-3 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors">
            <input
              v-model="formData.services"
              :value="service.id"
              type="checkbox"
              class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
            />
            <span class="text-lg">{{ service.icon }}</span>
            <span class="text-sm text-gray-700">{{ service.name }}</span>
          </label>
        </div>
      </div>

      <!-- Chính sách -->
      <div class="space-y-6">
        <h4 class="font-medium text-gray-900">Chính sách</h4>
        
        <div class="space-y-2">
          <label class="block text-sm font-medium text-gray-700">Chính sách vé</label>
          <textarea
            v-model="formData.policies.ticketPolicy"
            rows="3"
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            placeholder="Quy định về việc mua vé, thay đổi vé..."
          ></textarea>
        </div>

        <div class="space-y-2">
          <label class="block text-sm font-medium text-gray-700">Chính sách hủy vé</label>
          <textarea
            v-model="formData.policies.cancelPolicy"
            rows="3"
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            placeholder="Quy định về việc hủy vé, hoàn tiền..."
          ></textarea>
        </div>

        <div class="space-y-2">
          <label class="block text-sm font-medium text-gray-700">Chính sách hành lý</label>
          <textarea
            v-model="formData.policies.baggagePolicy"
            rows="3"
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            placeholder="Quy định về hành lý, cân nặng cho phép..."
          ></textarea>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// Props
defineProps({
  currentStep: {
    type: Number,
    required: true
  },
  formData: {
    type: Object,
    required: true
  },
  validationErrors: {
    type: Object,
    default: () => ({})
  },
  availableServices: {
    type: Array,
    default: () => []
  },
  weekDays: {
    type: Array,
    default: () => []
  },
  addStopPoint: {
    type: Function,
    required: true
  },
  removeStopPoint: {
    type: Function,
    required: true
  }
})

// Refs for file inputs
const exteriorFileInput = ref(null)
const interiorFileInput = ref(null)

// Simple image count trackers
const exteriorImageCount = ref(0)
const interiorImageCount = ref(0)

// Methods for file handling
const triggerFileInput = (type) => {
  if (type === 'exterior') {
    exteriorFileInput.value?.click()
  } else {
    interiorFileInput.value?.click()
  }
}

const handleFileSelect = (event, type) => {
  const files = Array.from(event.target.files)
  if (files.length > 0) {
    // Simply count selected files without processing
    if (type === 'exterior') {
      exteriorImageCount.value = files.length
    } else if (type === 'interior') {
      interiorImageCount.value = files.length
    }
  }
  // Reset input to allow selecting same files again
  event.target.value = ''
}
</script> 