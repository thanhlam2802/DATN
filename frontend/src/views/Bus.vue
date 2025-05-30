<script setup>
import { ref, nextTick } from 'vue'
import BusSearchForm from '../components/Bus/BusSearchForm.vue'
import BusResultList from '../components/Bus/BusResultList.vue'
import BusBooking from '../components/Bus/BusBooking.vue'
import BusSeatSelection from '../components/Bus/BusSeatSelection.vue'

// State management
const activeTab = ref('sleeping-bus') // 'sleeping-bus' hoặc 'shuttle-bus'
const searchResults = ref([])
const showResults = ref(false)
const selectedBus = ref(null)
const showBooking = ref(false)
const showSeatSelection = ref(false)

// Scroll position management để fix lỗi scroll reset
const savedScrollPosition = ref(0)

// Tab data
const tabs = [
  { id: 'sleeping-bus', name: 'Xe Giường nằm', icon: 'fas fa-bed' },
  { id: 'shuttle-bus', name: 'Xe Trung chuyển', icon: 'fas fa-bus' }
]

// Methods
const handleSearch = (searchData) => {
  // Simulate API call - sẽ thay thế bằng API thực từ backend
  console.log('Search data:', searchData)
  
  // Mock data cho demo
  const mockResults = [
    {
      id: 1,
      company: 'Phương Trang',
      busType: activeTab.value === 'sleeping-bus' ? 'Giường nằm' : 'Ghế ngồi',
      route: `${searchData.from} → ${searchData.to}`,
      departureTime: '06:00',
      arrivalTime: '14:00',
      duration: '8h',
      price: 350000,
      availableSeats: 12,
      image: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=300'
    },
    {
      id: 2,
      company: 'Hoàng Long',
      busType: activeTab.value === 'sleeping-bus' ? 'Giường nằm VIP' : 'Ghế ngồi',
      route: `${searchData.from} → ${searchData.to}`,
      departureTime: '08:30',
      arrivalTime: '16:30',
      duration: '8h',
      price: 420000,
      availableSeats: 8,
      image: 'https://images.unsplash.com/photo-1570125909232-eb263c188f7e?w=300'
    },
    {
      id: 3,
      company: 'Mai Linh Express',
      busType: activeTab.value === 'sleeping-bus' ? 'Giường nằm Premium' : 'Ghế ngồi Premium',
      route: `${searchData.from} → ${searchData.to}`,
      departureTime: '10:15',
      arrivalTime: '18:15',
      duration: '8h',
      price: 380000,
      availableSeats: 15,
      image: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=300'
    },
    {
      id: 4,
      company: 'Thành Bưởi',
      busType: activeTab.value === 'sleeping-bus' ? 'Giường nằm Cao cấp' : 'Ghế ngồi Deluxe',
      route: `${searchData.from} → ${searchData.to}`,
      departureTime: '14:30',
      arrivalTime: '22:30',
      duration: '8h',
      price: 450000,
      availableSeats: 6,
      image: 'https://images.unsplash.com/photo-1570125909232-eb263c188f7e?w=300'
    },
    {
      id: 5,
      company: 'Kumho Samco',
      busType: activeTab.value === 'sleeping-bus' ? 'Giường nằm VIP 24' : 'Ghế ngồi VIP',
      route: `${searchData.from} → ${searchData.to}`,
      departureTime: '16:45',
      arrivalTime: '00:45',
      duration: '8h',
      price: 520000,
      availableSeats: 3,
      image: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=300'
    }
  ]
  
  searchResults.value = mockResults
  showResults.value = true
  showBooking.value = false
  showSeatSelection.value = false
}

// Fix scroll position khi mở sidebar
const handleSelectBus = (bus) => {
  selectedBus.value = bus
  
  // Nếu là xe giường nằm, hiển thị giao diện chọn ghế
  if (activeTab.value === 'sleeping-bus') {
    // Lưu vị trí scroll hiện tại cho sidebar
    savedScrollPosition.value = window.pageYOffset || document.documentElement.scrollTop
    
    // Prevent body scroll khi sidebar mở
    document.body.style.overflow = 'hidden'
    document.body.style.position = 'fixed'
    document.body.style.top = `-${savedScrollPosition.value}px`
    document.body.style.width = '100%'
    
    showSeatSelection.value = true
    showBooking.value = false
  } else {
    // Xe trung chuyển đi thẳng đến trang đặt vé (không cần chọn ghế)
    showBooking.value = true
    showSeatSelection.value = false
    showResults.value = false // Ẩn results để hiển thị booking page
  }
}

const handleSeatSelected = (seatData) => {
  // Cập nhật thông tin ghế đã chọn vào selectedBus
  selectedBus.value = {
    ...selectedBus.value,
    selectedSeats: seatData.seats,
    totalPrice: seatData.totalPrice
  }
  
  // Restore body scroll trước khi chuyển trang
  document.body.style.overflow = ''
  document.body.style.position = ''
  document.body.style.top = ''
  document.body.style.width = ''
  
  // Chuyển sang trang đặt vé
  showSeatSelection.value = false
  showBooking.value = true
  showResults.value = false // Ẩn results để hiển thị booking page
  
  // Scroll to top cho booking page
  nextTick(() => {
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    })
  })
}

// Fix scroll position khi đóng sidebar
const handleCloseSeatSelection = () => {
  showSeatSelection.value = false
  selectedBus.value = null
  
  // Restore body scroll và vị trí
  document.body.style.overflow = ''
  document.body.style.position = ''
  document.body.style.top = ''
  document.body.style.width = ''
  
  // Restore scroll position với animation mượt
  nextTick(() => {
    window.scrollTo({
      top: savedScrollPosition.value,
      behavior: 'auto' // Instant restore, không cần smooth vì user mong đợi quay về vị trí cũ
    })
  })
}

const handleBackToResults = () => {
  showBooking.value = false
  showSeatSelection.value = false
  showResults.value = true // Hiển thị lại results
  selectedBus.value = null
  
  // Restore scroll nếu cần
  if (document.body.style.overflow === 'hidden') {
    document.body.style.overflow = ''
    document.body.style.position = ''
    document.body.style.top = ''
    document.body.style.width = ''
    
    nextTick(() => {
      window.scrollTo({
        top: savedScrollPosition.value,
        behavior: 'smooth'
      })
    })
  } else {
    // Scroll về vị trí search results
    nextTick(() => {
      window.scrollTo({
        top: 300, // Scroll to results section
        behavior: 'smooth'
      })
    })
  }
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 via-purple-50 to-indigo-50">
    <!-- Mobile-first responsive container -->
    <div class="container mx-auto px-3 sm:px-4 lg:px-6 xl:px-8 py-3 sm:py-4 lg:py-6 xl:py-8">
      <!-- Enhanced Header Section với responsive tối ưu -->
      <div class="flex flex-col space-y-4 mb-4 sm:mb-6 lg:mb-8 animate-fade-in">
        <!-- Mobile header layout -->
        <div class="flex justify-between items-start">
          <div class="flex-1">
            <h1 class="text-xl sm:text-2xl lg:text-3xl xl:text-4xl font-bold text-gray-800 mb-1 sm:mb-2 leading-tight">
              Đặt vé xe Bus
            </h1>
            <p class="text-xs sm:text-sm lg:text-base text-gray-600">
              Tìm kiếm và đặt vé xe bus trên toàn quốc
            </p>
          </div>
          <div class="flex items-center ml-4">
            <div class="text-right">
              <h2 class="text-2xl sm:text-3xl lg:text-4xl xl:text-5xl font-bold animate-bounce bg-gradient-to-r from-purple-600 to-blue-600 bg-clip-text text-transparent">
                BUS
              </h2>
              <p class="text-xs sm:text-sm text-gray-500">Dịch vụ vận chuyển</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Enhanced Banner Section với responsive tối ưu -->
      <div class="mb-4 sm:mb-6 lg:mb-8 animate-slide-in-up">
        <div class="relative overflow-hidden rounded-lg sm:rounded-xl lg:rounded-2xl xl:rounded-3xl shadow-lg sm:shadow-xl lg:shadow-2xl bg-gradient-to-r from-purple-600 via-blue-600 to-indigo-700">
          <!-- Background Pattern -->
          <div class="absolute inset-0 opacity-10 sm:opacity-15 lg:opacity-20">
            <div class="absolute inset-0 bg-pattern-dots animate-float"></div>
          </div>
          
          <!-- Content với responsive layout tối ưu -->
          <div class="relative px-3 sm:px-4 lg:px-6 xl:px-8 py-6 sm:py-8 lg:py-12 xl:py-16 text-white">
            <div class="grid grid-cols-1 lg:grid-cols-2 gap-4 sm:gap-6 lg:gap-8 items-center">
              <div class="space-y-2 sm:space-y-3 lg:space-y-4 xl:space-y-6 text-center lg:text-left">
                <h2 class="text-xl sm:text-2xl lg:text-3xl xl:text-4xl 2xl:text-5xl font-bold animate-text-glow leading-tight">
                  Chuyến đi an toàn
                </h2>
                <p class="text-sm sm:text-base lg:text-lg xl:text-xl opacity-90 max-w-md mx-auto lg:mx-0">
                  Trải nghiệm dịch vụ xe bus chất lượng cao với hệ thống đặt vé hiện đại
                </p>
                
                <!-- Enhanced Features với responsive grid -->
                <div class="grid grid-cols-1 sm:grid-cols-3 gap-2 sm:gap-3 lg:gap-4 xl:gap-6 text-xs sm:text-sm lg:text-base mt-3 sm:mt-4">
                  <div class="flex items-center justify-center lg:justify-start animate-pulse bg-white bg-opacity-10 rounded-lg p-2 sm:p-3">
                    <i class="fas fa-shield-alt mr-2 text-green-400 text-sm sm:text-base lg:text-lg"></i>
                    <span class="text-black">An toàn 100%</span>
                  </div>
                  <div class="flex items-center justify-center lg:justify-start animate-pulse bg-white bg-opacity-10 rounded-lg p-2 sm:p-3">
                    <i class="fas fa-clock mr-2 text-yellow-400 text-sm sm:text-base lg:text-lg"></i>
                    <span class="text-black">Đúng giờ</span>
                  </div>
                  <div class="flex items-center justify-center lg:justify-start animate-pulse bg-white bg-opacity-10 rounded-lg p-2 sm:p-3">
                    <i class="fas fa-star mr-2 text-orange-400 text-sm sm:text-base lg:text-lg"></i>
                    <span class="text-black">5 sao chất lượng</span>
                  </div>
                </div>
              </div>
              
              <!-- Enhanced Animated Bus Icon -->
              <div class="flex justify-center lg:justify-end mt-4 lg:mt-0">
                <div class="relative">
                  <div class="animate-bus-move">
                    <i class="fas fa-bus text-3xl sm:text-4xl lg:text-6xl xl:text-8xl text-white opacity-80 filter drop-shadow-lg"></i>
                  </div>
                  <!-- Enhanced smoke effect -->
                  <div class="absolute -top-1 sm:-top-2 lg:-top-4 -right-1 sm:-right-2 animate-smoke">
                    <div class="w-1 sm:w-1.5 lg:w-2 h-1 sm:h-1.5 lg:h-2 bg-white opacity-30 rounded-full"></div>
                    <div class="w-0.5 sm:w-1 h-0.5 sm:h-1 bg-white opacity-20 rounded-full ml-0.5 sm:ml-1 mt-0.5 sm:mt-1"></div>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- Enhanced Statistics với responsive grid tối ưu -->
            <div class="mt-4 sm:mt-6 lg:mt-8 xl:mt-12 grid grid-cols-2 lg:grid-cols-4 gap-2 sm:gap-3 lg:gap-4 xl:gap-6">
              <div class="text-center bg-white bg-opacity-20 rounded-lg sm:rounded-xl p-2 sm:p-3 lg:p-4 animate-counter">
                <div class="text-lg sm:text-xl lg:text-2xl xl:text-3xl font-bold text-yellow-400">10K+</div>
                <div class="text-xs sm:text-sm text-black opacity-90">Khách hàng</div>
              </div>
              <div class="text-center bg-white bg-opacity-20 rounded-lg sm:rounded-xl p-2 sm:p-3 lg:p-4 animate-counter animation-delay-200">
                <div class="text-lg sm:text-xl lg:text-2xl xl:text-3xl font-bold text-green-400">500+</div>
                <div class="text-xs sm:text-sm text-black opacity-90">Chuyến xe/ngày</div>
              </div>
              <div class="text-center bg-white bg-opacity-20 rounded-lg sm:rounded-xl p-2 sm:p-3 lg:p-4 animate-counter animation-delay-400">
                <div class="text-lg sm:text-xl lg:text-2xl xl:text-3xl font-bold text-blue-400">100+</div>
                <div class="text-xs sm:text-sm text-black opacity-90">Tuyến đường</div>
              </div>
              <div class="text-center bg-white bg-opacity-20 rounded-lg sm:rounded-xl p-2 sm:p-3 lg:p-4 animate-counter animation-delay-600">
                <div class="text-lg sm:text-xl lg:text-2xl xl:text-3xl font-bold text-purple-400">24/7</div>
                <div class="text-xs sm:text-sm text-black opacity-90">Hỗ trợ</div>
              </div>
            </div>
          </div>
          
          <!-- Enhanced Floating Elements - responsive sizes -->
          <div class="absolute top-2 sm:top-4 lg:top-10 left-2 sm:left-4 lg:left-10 animate-float">
            <div class="w-1 sm:w-2 lg:w-4 h-1 sm:h-2 lg:h-4 bg-white opacity-20 rounded-full"></div>
          </div>
          <div class="absolute top-4 sm:top-8 lg:top-20 right-4 sm:right-8 lg:right-20 animate-float animation-delay-1000">
            <div class="w-2 sm:w-3 lg:w-6 h-2 sm:h-3 lg:h-6 bg-white opacity-15 rounded-full"></div>
          </div>
          <div class="absolute bottom-2 sm:bottom-4 lg:bottom-10 left-1/4 animate-float animation-delay-500">
            <div class="w-1 sm:w-2 lg:w-3 h-1 sm:h-2 lg:h-3 bg-white opacity-25 rounded-full"></div>
          </div>
        </div>
      </div>

      <!-- Enhanced Booking Form Section với responsive tối ưu -->
      <div v-if="!showBooking && !showSeatSelection" class="bg-white rounded-lg sm:rounded-xl lg:rounded-2xl shadow-md sm:shadow-lg lg:shadow-xl border border-gray-100 p-3 sm:p-4 lg:p-6 xl:p-8 mb-4 sm:mb-6 lg:mb-8 animate-slide-in-left">
        <!-- Enhanced Tabs với mobile optimization hoàn toàn -->
        <div class="flex flex-col space-y-3 sm:space-y-4 lg:space-y-6 mb-4 sm:mb-6 lg:mb-8">
          <!-- Tabs container responsive -->
          <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center space-y-3 sm:space-y-0">
            <div class="w-full sm:w-auto">
              <div class="flex bg-gray-100 p-1 rounded-lg sm:rounded-xl w-full sm:w-fit overflow-hidden">
                <button
                  v-for="tab in tabs"
                  :key="tab.id"
                  @click="activeTab = tab.id; searchResults = []; showResults = false; showBooking = false; showSeatSelection = false;"
                  class="flex-1 sm:flex-none flex items-center justify-center space-x-1 sm:space-x-2 px-3 sm:px-4 lg:px-6 py-2 sm:py-3 lg:py-4 rounded-md sm:rounded-lg text-xs sm:text-sm lg:text-base font-semibold transition-all duration-300 transform hover:scale-105"
                  :class="activeTab === tab.id 
                    ? 'bg-gradient-to-r from-purple-600 to-blue-600 text-white shadow-md sm:shadow-lg scale-105' 
                    : 'text-gray-600 hover:text-gray-800 hover:bg-gray-200'"
                >
                  <i :class="tab.icon" class="text-sm sm:text-base lg:text-lg"></i>
                  <span class="truncate">{{ tab.name }}</span>
                </button>
              </div>
            </div>
            
            <!-- Tab info với mobile responsive -->
            <div class="w-full sm:w-auto">
              <div class="flex justify-center sm:justify-end">
                <div class="inline-flex items-center bg-blue-50 border border-blue-200 rounded-full px-3 sm:px-4 py-1 sm:py-2">
                  <i class="fas fa-info-circle text-blue-500 mr-2 text-sm"></i>
                  <span class="text-xs sm:text-sm text-blue-700 font-medium">
                    {{ activeTab === 'sleeping-bus' ? 'Chọn ghế trước khi đặt' : 'Đặt vé ngay' }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Search Form -->
        <BusSearchForm 
          :busType="activeTab"
          @search="handleSearch" 
        />
      </div>

      <!-- Search Results với enhanced responsive -->
      <div v-if="showResults && !showBooking && !showSeatSelection" class="animate-fade-in">
        <div class="mb-3 sm:mb-4 lg:mb-6 flex flex-col space-y-3 sm:space-y-0 sm:flex-row sm:justify-between sm:items-center">
          <div class="text-center sm:text-left">
            <h3 class="text-lg sm:text-xl lg:text-2xl font-bold text-gray-800">Kết quả tìm kiếm</h3>
            <p class="text-sm sm:text-base text-gray-600">Tìm thấy {{ searchResults.length }} chuyến xe phù hợp</p>
          </div>
          
          <!-- Enhanced Sort và filter options cho mobile -->
          <div class="flex space-x-2 w-full sm:w-auto">
            <select class="flex-1 sm:flex-none px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all">
              <option>Sắp xếp theo giá</option>
              <option>Sắp xếp theo giờ</option>
              <option>Sắp xếp theo đánh giá</option>
            </select>
            <button class="px-3 sm:px-4 py-2 bg-gray-100 rounded-lg text-sm hover:bg-gray-200 transition-colors whitespace-nowrap">
              <i class="fas fa-filter"></i>
              <span class="hidden sm:inline ml-2">Lọc</span>
            </button>
          </div>
        </div>
        
        <BusResultList 
          :results="searchResults"
          :busType="activeTab"
          @select-bus="handleSelectBus"
        />
      </div>

      <!-- Seat Selection Sidebar với scroll position fix -->
      <BusSeatSelection
        v-if="showSeatSelection && selectedBus"
        :selectedBus="selectedBus"
        :busType="activeTab"
        @seat-selected="handleSeatSelected"
        @close="handleCloseSeatSelection"
      />

      <!-- Enhanced Booking Page với responsive design -->
      <div v-if="showBooking && selectedBus" class="animate-slide-in-right">
        <!-- Enhanced Breadcrumb cho mobile -->
        <div class="mb-3 sm:mb-4 lg:mb-6 flex items-center space-x-2 text-sm text-gray-600">
          <button @click="handleBackToResults" class="flex items-center space-x-1 hover:text-purple-600 transition-colors">
            <i class="fas fa-arrow-left"></i>
            <span>Quay lại</span>
          </button>
          <i class="fas fa-chevron-right text-xs"></i>
          <span class="truncate">
            {{ activeTab === 'sleeping-bus' ? 'Thông tin đặt vé - Giường nằm' : 'Thông tin đặt vé - Ghế ngồi' }}
          </span>
        </div>
        
        <!-- Enhanced container cho booking page -->
        <div class="bg-white rounded-lg sm:rounded-xl lg:rounded-2xl shadow-md sm:shadow-lg lg:shadow-xl border border-gray-100 overflow-hidden">
          <BusBooking 
            :selectedBus="selectedBus"
            :busType="activeTab"
            @back="handleBackToResults"
          />
        </div>
      </div>

      <!-- Enhanced floating action button cho mobile -->
      <div class="fixed bottom-4 right-4 sm:hidden z-30">
        <div v-if="!showBooking && !showSeatSelection" class="relative">
          <button class="w-12 h-12 sm:w-14 sm:h-14 bg-gradient-to-r from-purple-600 to-blue-600 text-white rounded-full shadow-lg flex items-center justify-center hover:shadow-xl transition-all duration-300 transform hover:scale-110">
            <i class="fas fa-search text-base sm:text-lg"></i>
          </button>
          <!-- Enhanced Notification badge -->
          <div v-if="searchResults.length > 0" class="absolute -top-2 -right-2 w-5 h-5 sm:w-6 sm:h-6 bg-red-500 text-white text-xs font-bold rounded-full flex items-center justify-center animate-pulse">
            {{ searchResults.length > 99 ? '99+' : searchResults.length }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Enhanced responsive container với breakpoints tối ưu */
.container {
  max-width: 1400px;
}

/* Mobile-first responsive improvements với spacing tối ưu */
@media (max-width: 640px) {
  .container {
    padding-left: 0.75rem;
    padding-right: 0.75rem;
  }
}

@media (min-width: 641px) and (max-width: 1024px) {
  .container {
    padding-left: 1rem;
    padding-right: 1rem;
  }
}

@media (min-width: 1025px) {
  .container {
    padding-left: 1.5rem;
    padding-right: 1.5rem;
  }
}

/* Enhanced animations với performance optimizations */
@keyframes fade-in {
  from { 
    opacity: 0; 
    transform: translateY(20px);
  }
  to { 
    opacity: 1; 
    transform: translateY(0);
  }
}

@keyframes slide-in-up {
  from { 
    opacity: 0; 
    transform: translateY(50px);
  }
  to { 
    opacity: 1; 
    transform: translateY(0);
  }
}

@keyframes slide-in-left {
  from { 
    opacity: 0; 
    transform: translateX(-50px);
  }
  to { 
    opacity: 1; 
    transform: translateX(0);
  }
}

@keyframes slide-in-right {
  from { 
    opacity: 0; 
    transform: translateX(50px);
  }
  to { 
    opacity: 1; 
    transform: translateX(0);
  }
}

@keyframes float {
  0%, 100% { 
    transform: translateY(0px) rotate(0deg);
  }
  33% { 
    transform: translateY(-8px) rotate(1deg);
  }
  66% { 
    transform: translateY(-4px) rotate(-1deg);
  }
}

@keyframes bus-move {
  0%, 100% { 
    transform: translateX(0px) rotate(0deg) scale(1);
  }
  25% { 
    transform: translateX(8px) rotate(2deg) scale(1.05);
  }
  50% { 
    transform: translateX(0px) rotate(0deg) scale(1);
  }
  75% { 
    transform: translateX(-8px) rotate(-2deg) scale(1.05);
  }
}

@keyframes text-glow {
  0%, 100% { 
    text-shadow: 0 0 10px rgba(255,255,255,0.5);
    filter: brightness(1);
  }
  50% { 
    text-shadow: 0 0 25px rgba(255,255,255,0.8), 0 0 35px rgba(255,255,255,0.6);
    filter: brightness(1.1);
  }
}

@keyframes counter {
  from { 
    transform: scale(0.8) translateY(20px); 
    opacity: 0;
  }
  to { 
    transform: scale(1) translateY(0); 
    opacity: 1;
  }
}

@keyframes smoke {
  0% { 
    opacity: 0.4; 
    transform: translateY(0px) scale(1);
  }
  50% { 
    opacity: 0.2; 
    transform: translateY(-15px) scale(1.3);
  }
  100% { 
    opacity: 0; 
    transform: translateY(-30px) scale(1.8);
  }
}

/* Enhanced animation classes */
.animate-fade-in {
  animation: fade-in 0.8s ease-out forwards;
}

.animate-slide-in-up {
  animation: slide-in-up 1s ease-out forwards;
}

.animate-slide-in-left {
  animation: slide-in-left 0.8s ease-out forwards;
}

.animate-slide-in-right {
  animation: slide-in-right 0.8s ease-out forwards;
}

.animate-float {
  animation: float 4s ease-in-out infinite;
}

.animate-bus-move {
  animation: bus-move 6s ease-in-out infinite;
}

.animate-text-glow {
  animation: text-glow 3s ease-in-out infinite;
}

.animate-counter {
  animation: counter 1s ease-out forwards;
}

.animate-smoke {
  animation: smoke 3s ease-out infinite;
}

/* Enhanced delay classes */
.animation-delay-200 { animation-delay: 0.2s; }
.animation-delay-400 { animation-delay: 0.4s; }
.animation-delay-500 { animation-delay: 0.5s; }
.animation-delay-600 { animation-delay: 0.6s; }
.animation-delay-1000 { animation-delay: 1s; }

/* Enhanced background pattern với responsive sizes */
.bg-pattern-dots {
  background-image: radial-gradient(circle, rgba(255,255,255,0.15) 1px, transparent 1px);
  background-size: 16px 16px;
}

@media (min-width: 640px) {
  .bg-pattern-dots {
    background-size: 20px 20px;
  }
}

@media (min-width: 1024px) {
  .bg-pattern-dots {
    background-size: 24px 24px;
  }
}

/* Enhanced scroll lock để prevent scroll issues */
.scroll-lock {
  overflow: hidden !important;
  position: fixed !important;
  width: 100% !important;
}

/* Reduced motion for accessibility */
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
  
  .animate-bounce {
    animation: none !important;
  }
}

/* Enhanced focus states for accessibility */
button:focus-visible,
select:focus-visible {
  outline: 2px solid #646ae7;
  outline-offset: 2px;
  box-shadow: 0 0 0 4px rgba(100, 106, 231, 0.1);
}

/* High contrast mode support */
@media (prefers-contrast: high) {
  .bg-gradient-to-r {
    background: #646ae7 !important;
  }
  
  .text-gray-600 {
    color: #000 !important;
  }
  
  .bg-gradient-to-br {
    background: linear-gradient(135deg, #1e40af 0%, #7c3aed 100%) !important;
  }
}

/* Custom scrollbar for WebKit browsers */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f8fafc;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #646ae7, #4f46e5);
  border-radius: 3px;
  transition: background 0.3s ease;
}

::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #5b21b6, #3730a3);
}

/* Enhanced mobile responsiveness */
@media (max-width: 480px) {
  .container {
    padding-left: 0.5rem;
    padding-right: 0.5rem;
  }
  
  /* Optimize for very small screens */
  .text-xl {
    font-size: 1.125rem !important;
  }
  
  .space-y-4 > * + * {
    margin-top: 0.75rem !important;
  }
}

/* Tablet specific optimizations */
@media (min-width: 768px) and (max-width: 1023px) {
  .container {
    max-width: 100%;
    padding-left: 2rem;
    padding-right: 2rem;
  }
}

/* Desktop large screen optimizations */
@media (min-width: 1440px) {
  .container {
    max-width: 1600px;
  }
}

/* Print styles */
@media print {
  .animate-bounce,
  .animate-pulse,
  .animate-float,
  .animate-bus-move,
  .animate-text-glow,
  .animate-counter,
  .animate-smoke {
    animation: none !important;
  }
  
  .shadow-lg,
  .shadow-xl,
  .shadow-2xl {
    box-shadow: none !important;
  }
  
  .bg-gradient-to-r,
  .bg-gradient-to-br {
    background: #fff !important;
    color: #000 !important;
  }
}

/* Enhanced touch targets for mobile */
@media (max-width: 768px) {
  button {
    min-height: 44px;
    min-width: 44px;
  }
  
  select {
    min-height: 44px;
  }
}

/* Enhanced loading states */
.loading {
  position: relative;
  overflow: hidden;
}

.loading::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent);
  animation: loading-shimmer 1.5s infinite;
}

@keyframes loading-shimmer {
  0% { left: -100%; }
  100% { left: 100%; }
}

/* Dark mode support (for future implementation) */
@media (prefers-color-scheme: dark) {
  .bg-white {
    background-color: #1f2937;
    color: #f9fafb;
  }
  
  .text-gray-600 {
    color: #d1d5db;
  }
  
  .text-gray-800 {
    color: #f3f4f6;
  }
  
  .border-gray-100 {
    border-color: #374151;
  }
}
</style>
