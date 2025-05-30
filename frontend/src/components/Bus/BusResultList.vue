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

// Mock additional bus details - sẽ được thay thế bằng API từ backend
const getBusDetails = (bus) => {
  return {
    rating: 4.5,
    totalReviews: Math.floor(Math.random() * 500) + 100,
    licensePlate: bus.id === 1 ? '51B-123.45' : '79A-567.89',
    capacity: bus.busType.includes('Giường nằm') ? '34 giường' : '45 ghế',
    facilities: bus.busType.includes('Giường nằm') 
      ? ['WiFi miễn phí', 'Điều hòa', 'Giường nằm', 'Tivi', 'Nghỉ ăn', 'WC riêng']
      : ['WiFi miễn phí', 'Điều hòa', 'Ghế ngả', 'Sạc điện thoại'],
    policies: [
      'Hoàn vé trước 2h: 70% giá vé',
      'Đổi vé trước 4h: Phí 20.000đ',
      'Mang theo CMND/CCCD'
    ],
    pickupPoints: ['Bến xe Miền Đông', 'Sân bay Tân Sơn Nhất', 'Bến xe An Sương'],
    dropOffPoints: ['Bến xe Đà Nẵng', 'Sân bay Đà Nẵng', 'Trung tâm thành phố']
  }
}
</script>

<template>
  <div class="space-y-4 sm:space-y-6">
    <!-- Enhanced Header với responsive -->
    <div class="flex flex-col space-y-3 sm:space-y-0 sm:flex-row sm:justify-between sm:items-center">
      <h2 class="text-xl sm:text-2xl font-bold text-gray-800 text-center sm:text-left">
        Tìm thấy {{ results.length }} chuyến xe
      </h2>
      <div class="flex items-center justify-center sm:justify-end space-x-2 sm:space-x-4 text-sm text-gray-600">
        <span class="hidden sm:inline">Sắp xếp theo:</span>
        <span class="sm:hidden">Sắp xếp:</span>
        <select class="border border-gray-300 rounded-lg px-3 py-1.5 text-sm focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all">
          <option>Giá thấp nhất</option>
          <option>Giờ khởi hành</option>
          <option>Thời gian di chuyển</option>
          <option>Đánh giá cao nhất</option>
        </select>
      </div>
    </div>

    <!-- Enhanced Results List với responsive tối ưu -->
    <div class="space-y-4 sm:space-y-6">
      <div
        v-for="bus in results"
        :key="bus.id"
        class="bg-white rounded-lg sm:rounded-xl shadow-md hover:shadow-lg transition-all duration-300 overflow-hidden border border-gray-100 transform hover:-translate-y-1"
      >
        <div class="p-4 sm:p-6">
          <!-- Main Bus Information với responsive grid -->
          <div class="grid grid-cols-1 lg:grid-cols-12 gap-4 sm:gap-6 mb-4 sm:mb-6">
            <!-- Bus Image với responsive sizing -->
            <div class="lg:col-span-3">
              <div class="relative">
                <img
                  :src="bus.image"
                  :alt="bus.company"
                  class="w-full h-32 sm:h-40 object-cover rounded-lg"
                />
                <!-- Enhanced License Plate với responsive positioning -->
                <div class="absolute bottom-2 left-2 sm:bottom-3 sm:left-3">
                  <span class="bg-blue-100 text-blue-800 text-xs font-medium px-2 py-1 rounded-md shadow-sm backdrop-blur-sm">
                    {{ getBusDetails(bus).licensePlate }}
                  </span>
                </div>
              </div>
            </div>

            <!-- Bus Information với enhanced responsive layout -->
            <div class="lg:col-span-6 space-y-3 sm:space-y-4">
              <!-- Company & Rating với responsive typography -->
              <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between space-y-2 sm:space-y-0">
                <div>
                  <h3 class="text-lg sm:text-xl font-bold text-gray-800 truncate">{{ bus.company }}</h3>
                  <p class="text-sm text-gray-600">{{ bus.busType }}</p>
                </div>
                <div class="text-left sm:text-right">
                  <div class="flex items-center space-x-1">
                    <span class="text-yellow-500 text-lg">★</span>
                    <span class="font-medium text-sm sm:text-base">{{ getBusDetails(bus).rating }}</span>
                    <span class="text-gray-500 text-xs sm:text-sm">({{ getBusDetails(bus).totalReviews }})</span>
                  </div>
                  <p class="text-xs text-gray-500 mt-1">{{ getBusDetails(bus).capacity }}</p>
                </div>
              </div>

              <!-- Route Information với enhanced responsive layout -->
              <div class="bg-gray-50 rounded-lg p-3 sm:p-4">
                <div class="flex items-center text-gray-700 mb-2 sm:mb-3">
                  <i class="fas fa-route mr-2 text-purple-600 text-sm sm:text-base"></i>
                  <span class="font-medium text-sm sm:text-base truncate">{{ bus.route }}</span>
                </div>
                
                <!-- Time Information với responsive grid -->
                <div class="grid grid-cols-3 gap-2 sm:gap-4 text-sm">
                  <div class="text-center">
                    <p class="text-gray-500 text-xs sm:text-sm">Khởi hành</p>
                    <p class="font-bold text-base sm:text-lg text-purple-600">{{ bus.departureTime }}</p>
                  </div>
                  <div class="text-center">
                    <p class="text-gray-500 text-xs sm:text-sm">Thời gian</p>
                    <p class="font-medium text-gray-700 text-sm sm:text-base">{{ bus.duration }}</p>
                    <div class="flex justify-center mt-1">
                      <div class="w-6 sm:w-8 border-t border-gray-300"></div>
                    </div>
                  </div>
                  <div class="text-center">
                    <p class="text-gray-500 text-xs sm:text-sm">Đến nơi</p>
                    <p class="font-bold text-base sm:text-lg text-gray-800">{{ bus.arrivalTime }}</p>
                  </div>
                </div>
              </div>

              <!-- Available Seats với responsive layout -->
              <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between space-y-2 sm:space-y-0">
                <div class="flex items-center text-sm">
                  <i class="fas fa-chair mr-2 text-green-600"></i>
                  <span class="text-green-600 font-medium">
                    Còn {{ bus.availableSeats }} chỗ trống
                  </span>
                </div>
                <div class="text-sm text-gray-600">
                  <i class="fas fa-clock mr-1"></i>
                  <span class="text-xs sm:text-sm">Cập nhật 5 phút trước</span>
                </div>
              </div>
            </div>

            <!-- Price & Action với responsive layout -->
            <div class="lg:col-span-3 flex flex-row lg:flex-col justify-between lg:justify-between items-center lg:items-end space-x-4 lg:space-x-0 lg:space-y-4 mt-4 lg:mt-0">
              <div class="text-left lg:text-right">
                <p class="text-sm text-gray-500">Giá từ</p>
                <p class="text-2xl sm:text-3xl font-bold text-purple-600">
                  {{ formatPrice(bus.price) }}
                </p>
                <p class="text-xs text-gray-400">/người</p>
                <p class="text-xs text-green-600 mt-1">
                  <i class="fas fa-tag mr-1"></i>Giá tốt nhất
                </p>
              </div>

              <button
                @click="selectBus(bus)"
                class="w-full sm:w-auto lg:w-full px-4 sm:px-6 py-2.5 sm:py-3 text-white font-semibold rounded-lg transition-all duration-200 hover:shadow-lg transform hover:-translate-y-0.5 bg-purple-600 hover:bg-purple-700 whitespace-nowrap"
              >
                <i class="fas fa-ticket-alt mr-2"></i>
                <span class="hidden sm:inline">Chọn chuyến này</span>
                <span class="sm:hidden">Chọn</span>
              </button>
            </div>
          </div>

          <!-- Detailed Information Tabs với responsive grid -->
          <div class="border-t border-gray-100 pt-4 sm:pt-6">
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 sm:gap-6">
              <!-- Facilities với enhanced mobile layout -->
              <div>
                <h4 class="font-medium text-gray-800 mb-2 sm:mb-3 flex items-center text-sm sm:text-base">
                  <i class="fas fa-star mr-2 text-yellow-500"></i>
                  Tiện ích
                </h4>
                <div class="flex flex-wrap gap-1.5 sm:gap-2">
                  <span
                    v-for="facility in getBusDetails(bus).facilities.slice(0, 4)"
                    :key="facility"
                    class="px-2 py-1 bg-blue-50 text-blue-700 rounded-full text-xs font-medium"
                  >
                    {{ facility }}
                  </span>
                  <span v-if="getBusDetails(bus).facilities.length > 4" class="text-xs text-blue-600 cursor-pointer hover:underline">
                    +{{ getBusDetails(bus).facilities.length - 4 }} khác
                  </span>
                </div>
              </div>

              <!-- Pickup Points với mobile optimization -->
              <div>
                <h4 class="font-medium text-gray-800 mb-2 sm:mb-3 flex items-center text-sm sm:text-base">
                  <i class="fas fa-map-marker-alt mr-2 text-green-500"></i>
                  Điểm đón
                </h4>
                <div class="space-y-1">
                  <p
                    v-for="point in getBusDetails(bus).pickupPoints.slice(0, 2)"
                    :key="point"
                    class="text-xs sm:text-sm text-gray-600 truncate"
                  >
                    • {{ point }}
                  </p>
                  <p class="text-xs text-blue-600 cursor-pointer hover:underline">
                    + {{ getBusDetails(bus).pickupPoints.length - 2 }} điểm khác
                  </p>
                </div>
              </div>

              <!-- Policies với responsive layout -->
              <div class="sm:col-span-2 lg:col-span-1">
                <h4 class="font-medium text-gray-800 mb-2 sm:mb-3 flex items-center text-sm sm:text-base">
                  <i class="fas fa-info-circle mr-2 text-orange-500"></i>
                  Chính sách
                </h4>
                <div class="space-y-1">
                  <p
                    v-for="policy in getBusDetails(bus).policies.slice(0, 2)"
                    :key="policy"
                    class="text-xs sm:text-sm text-gray-600"
                  >
                    • {{ policy }}
                  </p>
                  <p class="text-xs text-blue-600 cursor-pointer hover:underline">
                    Xem thêm chính sách
                  </p>
                </div>
              </div>
            </div>
          </div>

          <!-- Enhanced Premium Features cho sleeping bus -->
          <div v-if="busType === 'sleeping-bus'" class="mt-4 sm:mt-6 pt-4 sm:pt-6 border-t border-gray-100">
            <div class="bg-gradient-to-r from-purple-50 to-blue-50 rounded-lg p-3 sm:p-4">
              <h4 class="font-medium text-gray-800 mb-2 sm:mb-3 flex items-center text-sm sm:text-base">
                <i class="fas fa-crown mr-2 text-purple-600"></i>
                Dịch vụ cao cấp
              </h4>
              <div class="grid grid-cols-2 sm:grid-cols-4 gap-2 sm:gap-3 text-xs">
                <div class="flex items-center">
                  <i class="fas fa-bed mr-1 text-purple-600"></i>
                  <span class="truncate">Giường nằm thương gia</span>
                </div>
                <div class="flex items-center">
                  <i class="fas fa-utensils mr-1 text-orange-600"></i>
                  <span class="truncate">Suất ăn miễn phí</span>
                </div>
                <div class="flex items-center">
                  <i class="fas fa-blanket mr-1 text-blue-600"></i>
                  <span class="truncate">Chăn gối cao cấp</span>
                </div>
                <div class="flex items-center">
                  <i class="fas fa-headphones mr-1 text-green-600"></i>
                  <span class="truncate">Hệ thống giải trí</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Enhanced Quick Actions với responsive layout -->
          <div class="mt-4 sm:mt-6 flex flex-col sm:flex-row sm:items-center sm:justify-between space-y-3 sm:space-y-0 text-sm">
            <div class="flex flex-wrap justify-center sm:justify-start gap-3 sm:gap-4">
              <button class="text-blue-600 hover:text-blue-800 flex items-center transition-colors">
                <i class="fas fa-map mr-1 text-xs"></i>
                <span class="text-xs sm:text-sm">Xem lộ trình</span>
              </button>
              <button class="text-blue-600 hover:text-blue-800 flex items-center transition-colors">
                <i class="fas fa-comments mr-1 text-xs"></i>
                <span class="text-xs sm:text-sm">Đánh giá ({{ getBusDetails(bus).totalReviews }})</span>
              </button>
              <button class="text-blue-600 hover:text-blue-800 flex items-center transition-colors">
                <i class="fas fa-share mr-1 text-xs"></i>
                <span class="text-xs sm:text-sm">Chia sẻ</span>
              </button>
            </div>
            <div class="text-gray-500 text-center sm:text-right">
              <span class="text-xs sm:text-sm">Mã chuyến: BUS{{ bus.id.toString().padStart(6, '0') }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Enhanced No Results với responsive layout -->
    <div v-if="results.length === 0" class="text-center py-8 sm:py-12">
      <i class="fas fa-search text-4xl sm:text-6xl text-gray-300 mb-3 sm:mb-4"></i>
      <h3 class="text-lg sm:text-xl font-medium text-gray-600 mb-2">
        Không tìm thấy chuyến xe phù hợp
      </h3>
      <p class="text-sm sm:text-base text-gray-500">
        Vui lòng thử thay đổi điều kiện tìm kiếm
      </p>
    </div>
  </div>
</template>

<style scoped>
/* Add any additional styling if needed */
</style> 