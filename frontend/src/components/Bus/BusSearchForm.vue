<script setup>
import { ref } from 'vue'

const props = defineProps({
  busType: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['search'])

// Form data
const formData = ref({
  from: '',
  to: '',
  departureDate: '',
  passengers: 1,
  location: ''
})

// Popular locations for quick selection
const popularLocations = [
  'Hồ Chí Minh',
  'Hà Nội', 
  'Đà Nẵng',
  'Nha Trang',
  'Đà Lạt',
  'Cần Thơ',
  'Huế',
  'Quy Nhơn'
]

// Methods
const handleSubmit = () => {
  if (!formData.value.from || !formData.value.to || !formData.value.departureDate) {
    alert('Vui lòng điền đầy đủ thông tin!')
    return
  }
  
  emit('search', { ...formData.value })
}

const swapLocations = () => {
  const temp = formData.value.from
  formData.value.from = formData.value.to
  formData.value.to = temp
}

// Set minimum date to today
const today = new Date().toISOString().split('T')[0]
</script>

<template>
  <div class="space-y-8">
    <!-- Main Search Form -->
    <div class="bg-gradient-to-r from-white to-gray-50 rounded-2xl border border-gray-200 shadow-xl p-8">
      <!-- Location Selection Row -->
      <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 items-end mb-6">
        <!-- From Location -->
        <div class="lg:col-span-4">
          <label class="block text-sm font-semibold text-gray-700 mb-3">
            <i class="fas fa-map-marker-alt mr-2 text-blue-500"></i>
            Từ đâu
          </label>
          <div class="relative group">
            <select 
              v-model="formData.from"
              class="w-full h-14 px-5 pr-12 bg-white border-2 border-gray-200 rounded-xl text-gray-700 font-medium shadow-sm
                     focus:border-purple-500 focus:ring-4 focus:ring-purple-100 focus:outline-none
                     hover:border-gray-300 hover:shadow-md
                     transition-all duration-300 ease-in-out
                     appearance-none cursor-pointer"
            >
              <option value="" disabled class="text-gray-400">Chọn điểm đi</option>
              <option v-for="location in popularLocations" :key="location" :value="location">
                {{ location }}
              </option>
            </select>
            <!-- Custom dropdown arrow -->
            <div class="absolute inset-y-0 right-0 flex items-center pr-4 pointer-events-none">
              <i class="fas fa-chevron-down text-gray-400 group-hover:text-purple-500 transition-colors duration-200"></i>
            </div>
          </div>
        </div>

        <!-- Swap Button -->
        <div class="lg:col-span-1 flex justify-center">
          <button
            @click="swapLocations"
            type="button"
            class="group h-14 w-14 rounded-full bg-gradient-to-br from-purple-500 to-blue-600 text-white shadow-lg 
                   hover:shadow-xl hover:scale-110 active:scale-95
                   transition-all duration-300 ease-in-out
                   flex items-center justify-center"
          >
            <i class="fas fa-exchange-alt text-lg group-hover:rotate-180 transition-transform duration-300"></i>
          </button>
        </div>

        <!-- To Location -->
        <div class="lg:col-span-4">
          <label class="block text-sm font-semibold text-gray-700 mb-3">
            <i class="fas fa-flag-checkered mr-2 text-green-500"></i>
            Đến đâu
          </label>
          <div class="relative group">
            <select 
              v-model="formData.to"
              class="w-full h-14 px-5 pr-12 bg-white border-2 border-gray-200 rounded-xl text-gray-700 font-medium shadow-sm
                     focus:border-purple-500 focus:ring-4 focus:ring-purple-100 focus:outline-none
                     hover:border-gray-300 hover:shadow-md
                     transition-all duration-300 ease-in-out
                     appearance-none cursor-pointer"
            >
              <option value="" disabled class="text-gray-400">Chọn điểm đến</option>
              <option v-for="location in popularLocations" :key="location" :value="location">
                {{ location }}
              </option>
            </select>
            <div class="absolute inset-y-0 right-0 flex items-center pr-4 pointer-events-none">
              <i class="fas fa-chevron-down text-gray-400 group-hover:text-purple-500 transition-colors duration-200"></i>
            </div>
          </div>
        </div>

        <!-- Departure Date -->
        <div class="lg:col-span-3">
          <label class="block text-sm font-semibold text-gray-700 mb-3">
            <i class="fas fa-calendar-alt mr-2 text-orange-500"></i>
            Ngày đi
          </label>
          <div class="relative">
            <input
              v-model="formData.departureDate"
              type="date"
              :min="today"
              class="w-full h-14 px-5 bg-white border-2 border-gray-200 rounded-xl text-gray-700 font-medium shadow-sm
                     focus:border-purple-500 focus:ring-4 focus:ring-purple-100 focus:outline-none
                     hover:border-gray-300 hover:shadow-md
                     transition-all duration-300 ease-in-out
                     cursor-pointer"
            />
            <div class="absolute inset-y-0 right-0 flex items-center pr-4 pointer-events-none">
              <i class="fas fa-calendar text-gray-400"></i>
            </div>
          </div>
        </div>
      </div>

      <!-- Additional Options Row -->
      <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-12 gap-6 items-end">
        <!-- Number of Passengers -->
        <div class="xl:col-span-3">
          <label class="block text-sm font-semibold text-gray-700 mb-3">
            <i class="fas fa-users mr-2 text-indigo-500"></i>
            Số lượng người
          </label>
          <div class="relative group">
            <select 
              v-model="formData.passengers"
              class="w-full h-14 px-5 pr-12 bg-white border-2 border-gray-200 rounded-xl text-gray-700 font-medium shadow-sm
                     focus:border-purple-500 focus:ring-4 focus:ring-purple-100 focus:outline-none
                     hover:border-gray-300 hover:shadow-md
                     transition-all duration-300 ease-in-out
                     appearance-none cursor-pointer"
            >
              <option v-for="i in 10" :key="i" :value="i">{{ i }} người</option>
            </select>
            <div class="absolute inset-y-0 right-0 flex items-center pr-4 pointer-events-none">
              <i class="fas fa-chevron-down text-gray-400 group-hover:text-purple-500 transition-colors duration-200"></i>
            </div>
          </div>
        </div>

        <!-- Specific Location -->
        <div class="xl:col-span-6">
          <label class="block text-sm font-semibold text-gray-700 mb-3">
            <i class="fas fa-search-location mr-2 text-pink-500"></i>
            Địa điểm cụ thể (tùy chọn)
          </label>
          <div class="relative">
            <input
              v-model="formData.location"
              type="text"
              placeholder="Ví dụ: Bến xe Miền Đông, Sân bay..."
              class="w-full h-14 px-5 pr-12 bg-white border-2 border-gray-200 rounded-xl text-gray-700 font-medium shadow-sm
                     focus:border-purple-500 focus:ring-4 focus:ring-purple-100 focus:outline-none
                     hover:border-gray-300 hover:shadow-md
                     transition-all duration-300 ease-in-out
                     placeholder:text-gray-400"
            />
            <div class="absolute inset-y-0 right-0 flex items-center pr-4 pointer-events-none">
              <i class="fas fa-map-pin text-gray-400"></i>
            </div>
          </div>
        </div>

        <!-- Search Button -->
        <div class="xl:col-span-3">
          <button
            @click="handleSubmit"
            type="button"
            class="w-full h-14 bg-gradient-to-r from-purple-600 via-blue-600 to-indigo-700
                   hover:from-purple-700 hover:via-blue-700 hover:to-indigo-800
                   text-white font-bold rounded-xl shadow-lg hover:shadow-xl
                   transform hover:-translate-y-1 active:scale-95
                   transition-all duration-300 ease-in-out
                   flex items-center justify-center space-x-3
                   group"
          >
            <i class="fas fa-search text-lg group-hover:scale-110 transition-transform duration-200"></i>
            <span>Tìm kiếm</span>
            <i class="fas fa-arrow-right opacity-0 group-hover:opacity-100 group-hover:translate-x-1 transition-all duration-200"></i>
          </button>
        </div>
      </div>
    </div>

    <!-- Bus Type Info Card -->
    <div class="relative overflow-hidden rounded-2xl border border-gray-200 shadow-lg">
      <div class="absolute inset-0 bg-gradient-to-r from-blue-50 via-purple-50 to-indigo-50"></div>
      <div class="relative p-6">
        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-4">
            <div class="w-16 h-16 bg-gradient-to-br from-purple-500 to-blue-600 rounded-xl flex items-center justify-center shadow-lg">
              <i :class="busType === 'sleeping-bus' ? 'fas fa-bed' : 'fas fa-bus'" class="text-2xl text-white"></i>
            </div>
            <div>
              <h3 class="text-xl font-bold text-gray-800">
                {{ busType === 'sleeping-bus' ? 'Xe Giường nằm' : 'Xe Trung chuyển' }}
              </h3>
              <p class="text-gray-600 mt-1">
                {{ busType === 'sleeping-bus' 
                  ? 'Thoải mái cho các chuyến đi dài, có thể nằm nghỉ ngơi' 
                  : 'Phù hợp cho các chuyến đi ngắn, giá thành tiết kiệm' 
                }}
              </p>
            </div>
          </div>
          
          <!-- Features -->
          <div class="hidden lg:flex space-x-4">
            <div class="flex items-center space-x-2 bg-white bg-opacity-70 rounded-full px-4 py-2">
              <i class="fas fa-shield-alt text-green-500"></i>
              <span class="text-sm font-medium text-gray-700">An toàn</span>
            </div>
            <div class="flex items-center space-x-2 bg-white bg-opacity-70 rounded-full px-4 py-2">
              <i class="fas fa-clock text-blue-500"></i>
              <span class="text-sm font-medium text-gray-700">Đúng giờ</span>
            </div>
            <div class="flex items-center space-x-2 bg-white bg-opacity-70 rounded-full px-4 py-2">
              <i class="fas fa-star text-yellow-500"></i>
              <span class="text-sm font-medium text-gray-700">Chất lượng</span>
            </div>
          </div>
        </div>
        
        <!-- Mobile Features -->
        <div class="lg:hidden mt-4 flex flex-wrap gap-2">
          <div class="flex items-center space-x-2 bg-white bg-opacity-70 rounded-full px-3 py-1">
            <i class="fas fa-shield-alt text-green-500 text-sm"></i>
            <span class="text-xs font-medium text-gray-700">An toàn</span>
          </div>
          <div class="flex items-center space-x-2 bg-white bg-opacity-70 rounded-full px-3 py-1">
            <i class="fas fa-clock text-blue-500 text-sm"></i>
            <span class="text-xs font-medium text-gray-700">Đúng giờ</span>
          </div>
          <div class="flex items-center space-x-2 bg-white bg-opacity-70 rounded-full px-3 py-1">
            <i class="fas fa-star text-yellow-500 text-sm"></i>
            <span class="text-xs font-medium text-gray-700">Chất lượng</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Quick Tips -->
    <div class="bg-gradient-to-r from-amber-50 to-orange-50 border border-amber-200 rounded-xl p-4">
      <div class="flex items-start space-x-3">
        <div class="w-8 h-8 bg-amber-100 rounded-full flex items-center justify-center flex-shrink-0">
          <i class="fas fa-lightbulb text-amber-600"></i>
        </div>
        <div>
          <h4 class="font-semibold text-amber-800 mb-1">Mẹo đặt vé</h4>
          <ul class="text-sm text-amber-700 space-y-1">
            <li>• Đặt vé trước 2-3 ngày để có giá tốt nhất</li>
            <li>• Kiểm tra kỹ thông tin trước khi thanh toán</li>
            <li>• Mang theo CMND/CCCD khi lên xe</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Custom styles for enhanced UI */
select {
  background-image: none;
}

/* Custom focus effects */
.focus-ring:focus {
  @apply ring-4 ring-purple-100 ring-opacity-50;
}

/* Hover animations */
@keyframes float-up {
  0% { transform: translateY(0px); }
  100% { transform: translateY(-2px); }
}

.hover-float:hover {
  animation: float-up 0.3s ease-out forwards;
}

/* Gradient text effect */
.gradient-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* Input focus glow effect */
input:focus, select:focus {
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1), 0 0 20px rgba(139, 92, 246, 0.1);
}

/* Mobile optimizations */
@media (max-width: 768px) {
  .grid-mobile-stack > * {
    grid-column: 1 / -1;
  }
}
</style> 