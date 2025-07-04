<script setup>
import { ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import BusSearchForm from '@/components/Bus/BusSearchForm.vue'

const router = useRouter()

// State management
const activeTab = ref('sleeping-bus') // 'sleeping-bus' hoặc 'shuttle-bus'
const searchResults = ref([])
const showResults = ref(false)
const selectedBus = ref(null)
const showSeatSelection = ref(false)
const isRoundtrip = ref(false) // Track roundtrip state for layout adjustment

// Scroll position management để fix lỗi scroll reset
const savedScrollPosition = ref(0)

// Tab data
const tabs = [
  { id: 'sleeping-bus', name: 'Xe Giường nằm', icon: 'fas fa-bed' },
  { id: 'shuttle-bus', name: 'Xe Trung chuyển', icon: 'fas fa-bus' }
]

// Popular destinations data
const popularDestinations = [
  {
    id: 'vung-tau',
    name: 'Vũng Tàu',
    title: 'Vé Xe Khách Đến Vũng Tàu',
    image: 'https://image.vietnam.travel/sites/default/files/styles/top_banner/public/2023-03/shutterstock_1773239276_0.jpg?itok=rMyM3vKD',
    alt: 'Bãi biển Vũng Tàu'
  },
  {
    id: 'da-lat',
    name: 'Đà Lạt',
    title: 'Vé Xe Khách Đến Đà Lạt',
    image: 'https://picsum.photos/400/200?random=2',
    alt: 'Thành phố Đà Lạt'
  },
  {
    id: 'nha-trang',
    name: 'Nha Trang',
    title: 'Vé Xe Khách Đến Nha Trang',
    image: 'https://images.unsplash.com/photo-1533002832-1721d16b4bb9?q=80&w=2778&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
    alt: 'Bãi biển Nha Trang'
  },
  {
    id: 'phan-thiet',
    name: 'Phan Thiết',
    title: 'Vé Xe Khách Đến Phan Thiết',
    image: 'https://images.unsplash.com/photo-1716479852874-22742b84fef5?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
    alt: 'Đồi cát Phan Thiết'
  },
  {
    id: 'sai-gon',
    name: 'Sài Gòn',
    title: 'Vé Xe Khách Đến TP.HCM',
    image: 'https://images.unsplash.com/photo-1617028214012-ad037508a10a?q=80&w=2340&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
    alt: 'Thành phố Hồ Chí Minh'
  },
  {
    id: 'da-nang',
    name: 'Đà Nẵng',
    title: 'Vé Xe Khách Đến Đà Nẵng',
    image: 'https://images.unsplash.com/photo-1559592413-7cec4d0cae2b?q=80&w=2728&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
    alt: 'Cầu Vàng Đà Nẵng'
  }
]

// Why choose us data
const whyChooseUsData = [
  {
    id: 'cheap-price',
    title: 'Giá rẻ mỗi ngày với ưu đãi đặc biệt',
    description: 'Đặt vé xe khách qua ứng dụng để nhận giá tốt nhất!',
    image: 'https://ik.imagekit.io/tvlk/image/imageResource/2017/05/10/1494407528373-a0e2c450b5cfac244d687d6fa8f5dd98.png?tr=dpr-2,h-150,q-75,w-150',
    alt: 'Biểu tượng giá rẻ'
  },
  {
    id: 'secure-payment',
    title: 'Phương thức thanh toán an toàn',
    description: 'Giao dịch trực tuyến an toàn với nhiều lựa chọn linh hoạt.',
    image: 'https://ik.imagekit.io/tvlk/image/imageResource/2017/05/10/1494407536280-ddcb70cab4907fa78468540ba722d25b.png?tr=dpr-2,h-150,q-75,w-150',
    alt: 'Thẻ thanh toán'
  },
  {
    id: 'support-24-7',
    title: 'Hỗ trợ khách hàng 24/7',
    description: 'Đội ngũ nhân viên luôn sẵn sàng giúp đỡ bạn.',
    image: 'https://ik.imagekit.io/tvlk/image/imageResource/2017/05/10/1494407541562-61b4438f5439c253d872e70dd7633791.png?tr=dpr-2,h-150,q-75,w-150',
    alt: 'Hỗ trợ 24/7'
  },
  {
    id: 'verified-reviews',
    title: 'Khách thực, đánh giá thực',
    description: 'Hơn 10.000.000 đánh giá đã được xác thực từ hành khách.',
    image: 'https://ik.imagekit.io/tvlk/image/imageResource/2017/05/10/1494407562736-ea624be44aec195feffac615d37ab492.png?tr=dpr-2,h-150,q-75,w-150',
    alt: 'Đánh giá xác thực'
  }
]

// Handle search from BusSearchForm
const handleBusSearch = (searchData) => {
  console.log('Tìm kiếm xe bus:', searchData)
  isRoundtrip.value = searchData.roundtrip
  // Thực hiện logic tìm kiếm xe bus ở đây
  // searchResults.value = await searchBuses(searchData)
  // showResults.value = true
}

// Handle roundtrip state change
const handleRoundtripChange = (roundtripState) => {
  isRoundtrip.value = roundtripState
}

// Handle destination click
const handleDestinationClick = (destinationName) => {
  console.log('Chọn điểm đến:', destinationName)
  // Có thể điều hướng đến trang tìm kiếm hoặc tự động điền form
  // router.push({ name: 'BusSearch', query: { destination: destinationName } })
}

// Handle image events
const handleImageError = (event) => {
  console.log('Lỗi tải hình ảnh:', event.target.src)
  // Fallback to a default image
  event.target.src = 'https://via.placeholder.com/400x200?text=No+Image'
}

const handleImageLoad = (event) => {
  console.log('Hình ảnh đã tải thành công:', event.target.src)
}
</script>

<template>
  <div>
    <section class="relative w-full mt-4 overflow-hidden rounded-xl pb-32 mb-16">
              <img src="https://images.unsplash.com/photo-1572675339312-3e8b094a544d?q=80&w=2340&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D" alt="Hình nền du lịch" class="w-full h-[500px] object-cover object-center brightness-50" />

      <div class="absolute top-4 left-1/2 transform -translate-x-1/2 z-10 w-full px-4 max-w-7xl">
        <div class="rounded-xl p-2">
          <div class="flex justify-center">
            <div class="w-max max-w-full">
              <nav class="mb-2">
                <ul class="flex items-center justify-center space-x-2 md:space-x-4 px-4 py-1 text-sm font-semibold overflow-x-auto scrollbar-hide">
                  <li v-for="tab in tabs" :key="tab.id">
                    <button @click="activeTab = tab.id"
                      :class="activeTab === tab.id ? 'bg-white-100 backdrop-blur-sm text-white shadow-sm' : 'text-white/80 hover:bg-white/20 hover:text-white'"
                      class="flex items-center space-x-2 px-6 py-2 rounded-full transition-all duration-300 whitespace-nowrap">
                      <i :class="tab.icon"></i>
                      <span>{{ tab.name }}</span>
                    </button>
                  </li>
                </ul>
              </nav>
              <div class="h-px bg-white/60 my-2"></div>
            </div>
          </div>

          <div class="rounded-lg mt-[80px]">
            <BusSearchForm 
              @search="handleBusSearch" 
              @roundtrip-change="handleRoundtripChange"
            />
          </div>
        </div>
      </div>
    </section>  

    <!-- Popular Shuttle Destination Section -->
    <section class="bg-gray-50 py-16 mt-20">
      <div class="max-w-7xl mx-auto px-4">
        <hr class="border-t border-gray-300 mb-8" />
        <h2 class="text-3xl font-bold text-center text-gray-800 mb-12">
          Điểm Đến Xe Trung Chuyển Phổ Biến
        </h2>
        
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div 
            v-for="destination in popularDestinations" 
            :key="destination.id"
            @click="handleDestinationClick(destination.name)" 
            class="relative rounded-lg shadow-lg group cursor-pointer hover:shadow-xl transition-all duration-300 overflow-hidden"
          >
            <img 
              :src="destination.image" 
              :alt="destination.alt" 
              class="w-full h-48 object-cover group-hover:scale-105 group-hover:blur-sm transition-all duration-300 overflow-hidden"
              loading="lazy"
              @error="handleImageError"
              @load="handleImageLoad"
            />
            <div class="absolute inset-0 flex flex-col items-center justify-center text-center p-4">
              <h3 class="text-white text-xl font-bold drop-shadow-lg">{{ destination.title }}</h3>
              <p class="text-white text-sm mt-2 opacity-90 drop-shadow-md">{{ destination.name }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <div class="bg-white text-black font-sans mt-12">
      <div class="max-w-7xl mx-auto px-4 py-8">
        <hr class="border-t border-gray-300 mb-8" />
        <h2 class="text-center font-semibold text-lg mb-8">
          Tại sao nên đặt chỗ xe bus của chúng tôi?
        </h2>
        <div class="flex flex-wrap justify-center gap-x-20 gap-y-8 text-center max-w-5xl mx-auto">
          <div 
            v-for="item in whyChooseUsData" 
            :key="item.id" 
            class="max-w-[180px]"
          >
            <img 
              :alt="item.alt" 
              :src="item.image"
              class="mx-auto mb-4" 
              height="100"
              width="100" 
            />
            <h3 class="font-semibold text-base mb-2">{{ item.title }}</h3>
            <p class="text-sm font-normal leading-relaxed">{{ item.description }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>
