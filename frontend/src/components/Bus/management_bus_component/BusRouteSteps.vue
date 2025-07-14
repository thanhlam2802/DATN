<template>
  <div class="space-y-6">
    <!-- Bước 1: Thông tin xe & tuyến -->
    <div v-if="currentStep === 1" class="space-y-6">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- Loại xe -->
        <div class="space-y-2">
          <label class="block text-sm font-medium text-gray-700">Loại xe <span class="text-red-500">*</span></label>
          <select
            v-model="formData.busType"
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors duration-200 appearance-none bg-white"
          >
            <option value="">Chọn loại xe</option>
            <option value="standard">Trung chuyển (16 ghế)</option>
            <option value="sleeper">Giường nằm (36 ghế)</option>
          </select>
          <p v-if="validationErrors.busType" class="text-sm text-red-600">{{ validationErrors.busType }}</p>
        </div>

        <!-- Biển số xe -->
        <div class="space-y-2">
          <label class="block text-sm font-medium text-gray-700">Biển số xe <span class="text-red-500">*</span></label>
          <input
            v-model="formData.licensePlate"
            type="text"
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors duration-200"
            placeholder="VD: 51A-123.45"
          />
          <p v-if="validationErrors.licensePlate" class="text-sm text-red-600">{{ validationErrors.licensePlate }}</p>
        </div>
      </div>

      <!-- Điểm đi/đến -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="space-y-4">
          <h4 class="font-medium text-gray-900">Điểm khởi hành</h4>
          <div class="space-y-3">
            <div>
              <label class="block text-sm font-medium text-gray-700">Thành phố <span class="text-red-500">*</span></label>
              <select
                v-model="formData.departure.city"
                class="mt-1 w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 appearance-none bg-white"
              >
                <option value="">Chọn thành phố</option>
                <option value="hanoi">Hà Nội</option>
                <option value="hcm">TP. Hồ Chí Minh</option>
                <option value="danang">Đà Nẵng</option>
                <option value="cantho">Cần Thơ</option>
                <option value="nhatrang">Nha Trang</option>
                <option value="dalat">Đà Lạt</option>
              </select>
              <p v-if="validationErrors.departureCity" class="text-sm text-red-600">{{ validationErrors.departureCity }}</p>
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
                class="mt-1 w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 appearance-none bg-white"
              >
                <option value="">Chọn thành phố</option>
                <option value="hanoi">Hà Nội</option>
                <option value="hcm">TP. Hồ Chí Minh</option>
                <option value="danang">Đà Nẵng</option>
                <option value="cantho">Cần Thơ</option>
                <option value="nhatrang">Nha Trang</option>
                <option value="dalat">Đà Lạt</option>
              </select>
              <p v-if="validationErrors.arrivalCity" class="text-sm text-red-600">{{ validationErrors.arrivalCity }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Bước 2: Thời gian & giá vé -->
    <div v-if="currentStep === 2" class="space-y-6">
      <!-- Thời gian di chuyển -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="space-y-2">
          <label class="block text-sm font-medium text-gray-700">Giờ khởi hành <span class="text-red-500">*</span></label>
          <input
            v-model="formData.departure.time"
            type="time"
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
          />
          <p v-if="validationErrors.departureTime" class="text-sm text-red-600">{{ validationErrors.departureTime }}</p>
        </div>

        <div class="space-y-2">
          <label class="block text-sm font-medium text-gray-700">Giờ đến <span class="text-red-500">*</span></label>
          <input
            v-model="formData.arrival.time"
            type="time"
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
          />
          <p v-if="validationErrors.arrivalTime" class="text-sm text-red-600">{{ validationErrors.arrivalTime }}</p>
        </div>
      </div>

      <!-- Thời gian di chuyển ước tính -->
      <div class="space-y-2">
        <label class="block text-sm font-medium text-gray-700">Thời gian di chuyển ước tính <span class="text-red-500">*</span></label>
        <input
          v-model="formData.travelTime"
          type="text"
          class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
          placeholder="VD: 8 giờ 30 phút hoặc 12 giờ"
        />
        <p class="text-xs text-gray-500">Nhập theo định dạng: "8 giờ 30 phút" hoặc "12 giờ"</p>
        <p v-if="validationErrors.travelTime" class="text-sm text-red-600">{{ validationErrors.travelTime }}</p>
      </div>

      <!-- Giá vé -->
      <div class="space-y-2">
        <label class="block text-sm font-medium text-gray-700">Giá vé (VNĐ) <span class="text-red-500">*</span></label>
        <input
          v-model.number="formData.ticketPrice"
          type="number"
          min="0"
          step="1000"
          class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
          placeholder="350000"
        />
        <p class="text-xs text-gray-500">Giá vé tính theo VNĐ</p>
        <p v-if="validationErrors.ticketPrice" class="text-sm text-red-600">{{ validationErrors.ticketPrice }}</p>
      </div>

      <!-- Preview thông tin -->
      <div class="bg-gray-50 rounded-lg p-4 space-y-3">
        <h4 class="font-medium text-gray-900">Xem trước thông tin tuyến xe</h4>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
          <div>
            <span class="text-gray-600">Tuyến:</span>
            <span class="ml-2 font-medium">{{ formData.departure.city || 'Chưa chọn' }} → {{ formData.arrival.city || 'Chưa chọn' }}</span>
          </div>
          <div>
            <span class="text-gray-600">Loại xe:</span>
            <span class="ml-2 font-medium">{{ getBusTypeName(formData.busType) }}</span>
          </div>
          <div>
            <span class="text-gray-600">Biển số:</span>
            <span class="ml-2 font-medium">{{ formData.licensePlate || 'Chưa nhập' }}</span>
          </div>
          <div>
            <span class="text-gray-600">Thời gian:</span>
            <span class="ml-2 font-medium">{{ formData.departure.time || '--:--' }} - {{ formData.arrival.time || '--:--' }}</span>
          </div>
          <div>
            <span class="text-gray-600">Thời gian di chuyển:</span>
            <span class="ml-2 font-medium">{{ formData.travelTime || 'Chưa nhập' }}</span>
          </div>
          <div>
            <span class="text-gray-600">Giá vé:</span>
            <span class="ml-2 font-medium text-indigo-600">{{ formatPrice(formData.ticketPrice) }}đ</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

// Props
const props = defineProps({
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
  }
})

// Helper methods
const getBusTypeName = (type) => {
  const typeMap = {
    'standard': 'Trung chuyển (16 ghế)',
    'sleeper': 'Giường nằm (36 ghế)'
  }
  return typeMap[type] || 'Chưa chọn'
}

const formatPrice = (price) => {
  if (!price) return '0'
  return new Intl.NumberFormat('vi-VN').format(price)
}

// Computed
const cityOptions = computed(() => [
  { value: 'hanoi', label: 'Hà Nội' },
  { value: 'hcm', label: 'TP. Hồ Chí Minh' },
  { value: 'danang', label: 'Đà Nẵng' },
  { value: 'cantho', label: 'Cần Thơ' },
  { value: 'nhatrang', label: 'Nha Trang' },
  { value: 'dalat', label: 'Đà Lạt' }
])
</script> 