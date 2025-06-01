<script setup>
const props = defineProps({
  results: {
    type: Array,
    required: true
  },
  busType: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['select-bus'])

// Methods
const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price)
}

const selectBus = (bus) => {
  emit('select-bus', bus)
}

// Mock additional bus details cho API dễ dàng hơn
const getBusDetails = (bus) => {
  return {
    rating: (4.0 + Math.random() * 1).toFixed(1),
    totalReviews: Math.floor(Math.random() * 500) + 100,
    licensePlate: `${Math.floor(Math.random() * 90) + 10}${String.fromCharCode(65 + Math.floor(Math.random() * 26))}-${Math.floor(Math.random() * 900) + 100}.${Math.floor(Math.random() * 90) + 10}`,
    discount: Math.random() > 0.7 ? Math.floor(Math.random() * 20) + 5 : null,
    lastUpdated: '5 phút trước'
  }
}

// Format currency without symbol
const formatPriceShort = (price) => {
  return new Intl.NumberFormat('vi-VN').format(price)
}
</script>

<template>
  <div class="space-y-6">
    <!-- Enhanced Filter & Sort Header -->
    <div class="flex flex-col space-y-4 sm:space-y-0 sm:flex-row sm:justify-between sm:items-center bg-white rounded-lg shadow-sm p-4 border border-gray-100">
      <div class="flex items-center space-x-3">
        <h2 class="text-lg sm:text-xl font-bold text-gray-800">
          {{ results.length }} chuyến xe được tìm thấy
        </h2>
        <span class="hidden sm:inline-block px-2 py-1 bg-purple-100 text-purple-700 rounded-full text-xs font-medium">
          {{ busType === 'sleeping-bus' ? 'Giường nằm' : 'Ghế ngồi' }}
        </span>
      </div>
      
      <div class="flex items-center space-x-3">
        <span class="text-sm text-gray-600 hidden sm:block">Sắp xếp:</span>
        <select class="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-purple-500 focus:border-transparent bg-white">
          <option>Giá thấp nhất</option>
          <option>Giờ khởi hành</option>
          <option>Đánh giá cao nhất</option>
          <option>Thời gian ngắn nhất</option>
        </select>
        <button class="p-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
          <i class="fas fa-filter text-gray-600"></i>
        </button>
      </div>
    </div>

    <!-- Simplified Card Grid Layout -->
    <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
      <div
        v-for="bus in results"
        :key="bus.id"
        class="bg-white rounded-xl shadow-sm hover:shadow-lg transition-all duration-300 overflow-hidden border border-gray-100 group cursor-pointer transform hover:-translate-y-1"
        @click="selectBus(bus)"
      >
        <!-- Card Header với Image và Badge -->
        <div class="relative h-48 overflow-hidden">
          <img
            :src="bus.image"
            :alt="bus.company"
            class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
          />
          
          <!-- Overlay Gradient -->
          <div class="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent"></div>
          
          <!-- Top Badges -->
          <div class="absolute top-3 left-3 flex space-x-2">
            <span class="px-2 py-1 bg-white/90 backdrop-blur-sm text-gray-800 rounded-full text-xs font-medium">
              {{ getBusDetails(bus).licensePlate }}
            </span>
            <span v-if="getBusDetails(bus).discount" class="px-2 py-1 bg-red-500 text-white rounded-full text-xs font-medium">
              -{{ getBusDetails(bus).discount }}%
            </span>
          </div>
          
          <!-- Rating Badge -->
          <div class="absolute top-3 right-3 flex items-center space-x-1 bg-white/90 backdrop-blur-sm px-2 py-1 rounded-full">
            <i class="fas fa-star text-yellow-500 text-xs"></i>
            <span class="text-xs font-medium text-gray-800">{{ getBusDetails(bus).rating }}</span>
          </div>
          
          <!-- Bottom Company Info -->
          <div class="absolute bottom-3 left-3 right-3">
            <h3 class="text-white font-bold text-lg mb-1 truncate">{{ bus.company }}</h3>
            <p class="text-white/90 text-sm">{{ bus.busType }}</p>
          </div>
        </div>

        <!-- Simplified Card Body -->
        <div class="p-4 space-y-4">
          <!-- Route & Time -->
          <div class="space-y-3">
            <div class="flex items-center text-gray-700">
              <i class="fas fa-route text-purple-600 mr-2"></i>
              <span class="font-medium text-sm truncate">{{ bus.route }}</span>
            </div>
            
            <!-- Time Info Grid -->
            <div class="grid grid-cols-3 gap-2 bg-gray-50 rounded-lg p-3">
              <div class="text-center">
                <p class="text-xs text-gray-500 mb-1">Khởi hành</p>
                <p class="font-bold text-purple-600">{{ bus.departureTime }}</p>
              </div>
              <div class="text-center">
                <p class="text-xs text-gray-500 mb-1">Thời gian</p>
                <p class="font-medium text-gray-700 text-sm">{{ bus.duration }}</p>
              </div>
              <div class="text-center">
                <p class="text-xs text-gray-500 mb-1">Đến nơi</p>
                <p class="font-bold text-gray-800">{{ bus.arrivalTime }}</p>
              </div>
            </div>
          </div>

          <!-- Availability & Price -->
          <div class="space-y-3">
            <div class="flex items-center justify-between">
              <div class="flex items-center space-x-1">
                <i class="fas fa-chair text-green-600 text-sm"></i>
                <span class="text-green-600 font-medium text-sm">{{ bus.availableSeats }} chỗ trống</span>
              </div>
              <span class="text-xs text-gray-500">{{ getBusDetails(bus).lastUpdated }}</span>
            </div>

            <!-- Price Section -->
            <div class="flex items-center justify-between pt-3 border-t border-gray-100">
              <div>
                <div class="flex items-baseline space-x-1">
                  <span class="text-2xl font-bold text-purple-600">{{ formatPriceShort(bus.price) }}</span>
                  <span class="text-sm text-gray-500">VNĐ</span>
                </div>
                <p class="text-xs text-gray-400">mỗi người</p>
              </div>
              
              <button class="px-4 py-2 bg-purple-600 hover:bg-purple-700 text-white font-semibold rounded-lg transition-all duration-200 transform hover:scale-105 focus:ring-2 focus:ring-purple-500 focus:ring-offset-1">
                <i class="fas fa-ticket-alt mr-1"></i>
                Chọn
              </button>
            </div>
          </div>
        </div>

        <!-- Quick Info Footer -->
        <div class="px-4 pb-4">
          <div class="flex justify-between items-center text-xs text-gray-500 bg-gray-50 rounded-lg p-2">
            <div class="flex items-center space-x-2">
              <i class="fas fa-info-circle text-blue-500"></i>
              <span>{{ busType === 'sleeping-bus' ? 'Chọn ghế trước khi đặt' : 'Đặt vé ngay' }}</span>
            </div>
            <div class="flex items-center space-x-1">
              <i class="fas fa-eye text-gray-400"></i>
              <span>{{ getBusDetails(bus).totalReviews }} lượt xem</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Load More Button -->
    <div v-if="results.length > 0" class="text-center pt-6">
      <button class="px-6 py-3 border border-purple-600 text-purple-600 font-semibold rounded-lg hover:bg-purple-50 transition-all duration-200">
        <i class="fas fa-plus mr-2"></i>
        Xem thêm chuyến xe
      </button>
    </div>

    <!-- Empty State -->
    <div v-if="results.length === 0" class="text-center py-12">
      <div class="max-w-sm mx-auto">
        <div class="bg-gray-100 rounded-full w-24 h-24 flex items-center justify-center mx-auto mb-4">
          <i class="fas fa-search text-3xl text-gray-400"></i>
        </div>
        <h3 class="text-xl font-semibold text-gray-800 mb-2">Không tìm thấy chuyến xe</h3>
        <p class="text-gray-600 mb-6">Thử thay đổi điều kiện tìm kiếm hoặc ngày khởi hành khác</p>
        <button class="px-6 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition-colors">
          Tìm kiếm lại
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Enhanced Card Hover Effects */
.group:hover .bg-gradient-to-t {
  background: linear-gradient(to top, rgba(0,0,0,0.8), transparent, transparent);
}

/* Smooth image transitions */
img {
  transition: transform 0.3s ease;
}

/* Enhanced focus states */
button:focus-visible {
  outline: 2px solid #7c3aed;
  outline-offset: 2px;
}

/* Mobile optimizations */
@media (max-width: 640px) {
  .grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
}

/* Tablet optimizations */
@media (min-width: 768px) and (max-width: 1024px) {
  .xl\:grid-cols-3 {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style> 