<template>
  <main v-if="hotel" class="max-w-full mx-auto px-4 sm:px-6 lg:px-8 py-8" style="padding-top: 0;">
    <section class="bg-white rounded-xl shadow p-6 mb-8">
      <div class="flex space-x-3 mb-8 h-[500px]">
        <div class="flex-shrink-0 w-8/10 h-full overflow-hidden rounded-lg shadow-md">
          <img v-if="hotel.image" :alt="hotel.alt" :src="hotel.image"
            class="w-full h-full object-cover transition-transform duration-300 hover:scale-105" />
        </div>

        <div class="flex flex-col w-2/10 h-full justify-between">
          <div v-for="(img, idx) in hotel.additionalImages" :key="idx"
            class="h-[160px] overflow-hidden rounded-lg shadow-md">
            <img :src="img" :alt="`Additional image ${idx + 1}`"
              class="w-full h-full object-cover transition-transform duration-300 hover:scale-105" />
          </div>
        </div>
      </div>

      <div class="flex flex-col sm:flex-row items-center justify-between mb-4">
        <div class="flex-1 text-left">
          <h1 class="text-3xl font-extrabold text-gray-900 mb-1">{{ hotel.title }}</h1>
          <div class="flex items-center space-x-2 text-sm text-gray-600">
            <i class="fas fa-star text-blue-500"></i>
            <span class="text-blue-600 font-semibold">{{ hotel.rating }}</span>
            <span>({{ hotel.reviews }} reviews)</span>
            <span class="mx-2">•</span>
            <span>{{ hotel.location }}</span>
          </div>
        </div>
        <div class="flex items-center gap-4 mt-4 sm:mt-0">
          <div class="text-left">
            <span class="text-gray-500 text-sm block">Giá/phòng/đêm từ</span>
            <div class="text-2xl font-bold text-indigo-600 whitespace-nowrap">
              {{ minRoomPrice }} <span class="text-base font-normal">VND</span>
            </div>
          </div>
          <button
            class="bg-indigo-600 hover:bg-indigo-700 text-white rounded-full px-6 py-2 text-base font-semibold transition focus:outline-none focus:ring-2 focus:ring-indigo-500"
            @click="scrollToRooms">
            Chọn phòng
          </button>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-8 mb-10">
        <div>
          <h2 class="text-xl font-bold text-gray-800 mb-4">Tổng quan</h2>
          <ul class="text-sm text-gray-700 space-y-2 mb-4">
            <li class="flex items-center space-x-2">
              <i class="fas fa-home text-blue-500"></i>
              <span class="font-semibold">{{ hotel.details }}</span>
            </li>
            <li class="flex items-center space-x-2">
              <i class="far fa-calendar-alt text-pink-500"></i>
              <span class="font-semibold">Hủy miễn phí trong 48 giờ</span>
            </li>
          </ul>
          <p class="text-sm text-gray-700 leading-relaxed mb-4">
            Sunt ut elit cupidatat do quis incididunt sint mollit culpa consequat occaecat exercitati anim ad sint
            adipisicing nulla:
          </p>
          <ul class="text-sm text-gray-700 list-disc list-inside space-y-1 mb-4">
            <li>Sit reprehenderit elit incididunt</li>
            <li>Aute aliqua anim et duis occaecat</li>
            <li v-if="showMoreOverview">Proident tempor deserunt laborum nostrud.</li>
            <li v-if="showMoreOverview">Dolore pariatur exercitation nisi commodo.</li>
          </ul>
          <button @click="showMoreOverview = !showMoreOverview"
            class="text-blue-600 font-semibold text-sm hover:underline focus:outline-none">
            {{ showMoreOverview ? 'Show less' : 'Show more' }}
          </button>
        </div>

        <div>
          <h3 class="text-xl font-bold text-gray-800 mb-4">Các tiện ích nổi bật</h3>
          <div class="grid grid-cols-2 sm:grid-cols-3 gap-y-3 text-sm text-gray-700 mb-4">
            <div v-for="(amenity, index) in (showAllAmenities ? hotel.amenitiesList : hotel.amenitiesList.slice(0, 4))"
              :key="index" class="flex items-center space-x-2 font-medium">
              <i :class="amenity.icon" class="text-green-500"></i>
              <span>{{ amenity.name }}</span>
            </div>
          </div>
          <button @click="showAllAmenities = !showAllAmenities"
            class="mt-2 text-sm border border-gray-300 text-gray-700 rounded-full px-4 py-2 hover:bg-gray-100 focus:outline-none transition-colors duration-200">
            {{ showAllAmenities ? 'Show less' : 'Show all amenities' }}
          </button>
        </div>
      </div>
    </section>

    <section ref="roomsSectionRef" class="bg-white rounded-xl p-0 pt-0 mb-8">
      <h2 class="text-2xl font-bold text-gray-800 mb-6">Những phòng trống tại {{ hotel.title }}</h2>

      <div v-for="(room, idx) in roomTypes" :key="idx"
        class="flex flex-col md:flex-row gap-6 border border-gray-200 rounded-lg p-5 mb-6">
        <div class="relative flex flex-col gap-4 md:w-1/3">
          <span :class="['inline-block font-bold rounded-full px-3 py-1 text-sm self-start', room.labelClass]">
            {{ room.label }}
          </span>
          <div class="relative w-full h-48 overflow-hidden rounded-lg shadow-md">
            <transition :name="slideDirection === 'next' ? 'slide-right' : 'slide-left'">
              <img v-if="room.images && room.images.length" :key="roomImageIndex[room.id]"
                :src="room.images[roomImageIndex[room.id]]" :alt="`${room.label} Image ${roomImageIndex[room.id] + 1}`"
                class="w-full h-full object-cover absolute inset-0" />
            </transition>

            <button @click="prevRoomImage(room.id)"
              class="absolute left-2 top-1/2 transform -translate-y-1/2 bg-white/40 rounded-full p-2 hover:bg-white/45 z-10">
              <i class="fas fa-chevron-left text-gray-700"></i>
            </button>

            <button @click="nextRoomImage(room.id)"
              class="absolute right-2 top-1/2 transform -translate-y-1/2 bg-white/40 rounded-full p-2 hover:bg-white/45 z-10">
              <i class="fas fa-chevron-right text-gray-700"></i>
            </button>
          </div>

          <div class="flex flex-col gap-2 text-sm text-gray-700 font-semibold mt-2">
            <div class="flex items-center gap-2">
              <i class="fas fa-ruler-combined" :class="room.iconColor"></i>
              <span>{{ room.size }}</span>
            </div>
            <div class="flex items-center gap-2">
              <i class="fas fa-smoking-ban text-gray-500"></i>
              <span>Không hút thuốc</span>
            </div>
            <div class="flex flex-wrap gap-x-6 gap-y-2 text-xs text-gray-500 font-normal mt-2">
              <div v-for="(fac, i) in room.features" :key="i" class="flex items-center gap-1">
                <i :class="fac.icon"></i>
                <span>{{ fac.name }}</span>
              </div>
            </div>
          </div>
          <button @click="openModal(room)"
            class="font-bold text-sm flex items-center gap-1 hover:underline mt-2 text-gray-700">
            <i class="fas fa-info-circle"></i> Xem chi tiết phòng
          </button>
        </div>

        <div class="md:w-2/3 border border-gray-100 rounded-lg overflow-hidden shadow-sm">
          <table class="w-full text-sm text-gray-700 border-collapse">
            <thead class="bg-gray-50 text-gray-800 font-bold">
              <tr>
                <th class="text-left py-3 px-4 border-b border-gray-200 w-2/5">Lựa chọn phòng</th>
                <th class="text-center py-3 px-4 border-b border-gray-200 w-1/5">Khách</th>
                <th class="text-center py-3 px-4 border-b border-gray-200 w-2/5">Giá/phòng/đêm</th>
                <th class="w-16 border-b border-gray-200"></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(variant, vIdx) in room.variants" :key="vIdx" class="border-b border-gray-100 last:border-b-0">
                <td class="py-10 px-4 align-top">
                  <div class="text-xs text-gray-500 mb-1">{{ room.type }} - {{ room.rate }}</div>
                  <div class="font-bold text-sm mb-1">{{ variant.breakfast }}</div>
                  <div class="flex items-center gap-1 text-gray-500 text-xs mb-1">
                    <i class="fas fa-bed"></i>
                    <span>{{ room.bed }}</span>
                  </div>
                  <div class="flex items-center gap-1 text-green-600 text-xs mt-2">
                    <i class="fas fa-check-circle"></i>
                    <span>Áp dụng chính sách hủy phòng</span>
                    <i class="fas fa-info-circle text-gray-400" title="Thông tin chính sách hủy phòng"></i>
                  </div>
                </td>
                <td class="text-center align-top py-10 px-4 text-xl text-gray-600">
                  <i class="fas fa-user-friends"></i>
                </td>
                <td class="text-center align-top py-10 px-4">
                  <div class="mb-1">
                    <span :class="['inline-block text-white text-xs rounded-full px-2.5 py-0.5', room.tagClass]">
                      {{ room.tag }}
                    </span>
                  </div>
                  <div class="line-through text-xs text-gray-500 mt-1">
                    {{ variant.originalPrice }} VND
                  </div>
                  <div class="text-red-600 font-bold text-base mt-1">
                    {{ variant.discountedPrice }} VND
                  </div>
                  <div class="text-xs text-gray-500 mt-0.5">Chưa bao gồm thuế và phí</div>
                </td>
                <td class="text-center align-middle py-10 px-4">
                  <button @click="goToBooking(room, variant)"
                    :class="['rounded-lg px-5 py-2 text-sm font-semibold transition-colors duration-200 shadow-md', room.buttonClass]">
                    Chọn
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </section>

    <section class="bg-white rounded-xl shadow p-6">
      <h2 class="text-xl font-bold text-gray-800 mb-4">Đánh giá ({{ hotel.reviews }})</h2>
      <div class="max-h-96 overflow-y-auto space-y-4 mb-4 pr-2">
        <ul>
          <li v-for="(review, index) in reviewsList" :key="index"
            class="p-4 rounded-lg border border-gray-200 bg-gray-50 shadow-sm">
            <div class="flex items-center mb-3">
              <img :alt="review.name" class="w-12 h-12 rounded-full object-cover border-2 border-blue-400"
                :src="review.avatar" />
              <div class="ml-4">
                <h3 class="text-base font-semibold text-gray-900">{{ review.name }}</h3>
                <div class="flex items-center text-yellow-400 text-sm mt-1">
                  <i v-for="i in Math.floor(review.rating)" :key="i" class="fas fa-star"></i>
                  <i v-if="review.rating % 1 >= 0.5" class="fas fa-star-half-alt"></i>
                </div>
              </div>
            </div>
            <p class="text-sm text-gray-700 leading-relaxed">{{ review.comment }}</p>
          </li>
        </ul>
      </div>
    </section>
  </main>

  <div v-else class="text-center py-10">
    <p class="text-xl text-gray-700">Hotel not found.</p>
  </div>

  <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center pt-15"
    style="backdrop-filter: blur(6px); background-color: rgba(0,0,0,0.15);" @click.self="closeModal">
    <div
      class="bg-white rounded-xl shadow-xl max-w-3/5 w-full mx-4 lg:mx-0 overflow-hidden relative flex flex-col md:flex-row"
      style="max-height: 80vh;">
      <button @click="closeModal" class="absolute top-4 right-4 text-gray-500 hover:text-gray-800 z-10">
        <i class="fas fa-times fa-lg"></i>
      </button>

      <div class="md:w-1/2 w-full flex flex-col bg-gray-50">
        <div class="flex-grow flex items-center justify-center p-4">
          <img v-if="selectedRoom.images && selectedRoom.images.length" :src="selectedRoom.images[modalImageIndex]"
            :alt="`${selectedRoom.label} Room`"
            class="object-contain max-h-full w-full rounded-lg transition-all duration-200" />
        </div>

        <div class="flex gap-2 justify-center p-2 bg-white">
          <img v-for="(img, idx) in selectedRoom.images" :key="idx" :src="img" :alt="`Ảnh ${idx + 1}`"
            class="w-16 h-16 object-cover rounded cursor-pointer border-2"
            :class="modalImageIndex === idx ? 'border-blue-500' : 'border-transparent'"
            @click="modalImageIndex = idx" />
        </div>
      </div>

      <div class="md:w-1/2 w-full p-6 overflow-y-auto" style="max-height: 80vh;">
        <h2 class="text-2xl font-bold text-gray-800 mb-4">
          {{ selectedRoom.label }} – {{ selectedRoom.type }}
        </h2>
        <div class="mb-6 text-sm text-gray-700">
          <p class="flex items-center mb-2">
            <i class="fas fa-ruler-combined text-blue-500 mr-2"></i>
            Diện tích: <span class="font-semibold ml-1">{{ selectedRoom.size }}</span>
          </p>
          <p class="flex items-center">
            <i class="fas fa-user-friends text-blue-500 mr-2"></i>
            Sức chứa:
            <span class="font-semibold ml-1">{{ selectedRoom.maxAdults }} người lớn</span>,
            <span class="font-semibold ml-1">{{ selectedRoom.maxChildren }} trẻ em</span>
          </p>
        </div>

        <div class="mb-6">
          <h3 class="text-lg font-semibold text-gray-800 mb-2">Tiện ích:</h3>
          <div class="flex flex-wrap gap-2">
            <div v-for="(fac, i) in selectedRoom.features" :key="i"
              class="flex items-center space-x-2 bg-gray-100 rounded-full px-3 py-1 text-xs text-gray-700 mb-2">
              <i :class="fac.icon" class="text-green-600"></i>
              <span>{{ fac.name }}</span>
            </div>
          </div>
        </div>

        <div>
          <h3 class="text-lg font-semibold text-gray-800 mb-2">Các lựa chọn dịch vụ & giá:</h3>
          <table class="w-full text-sm text-gray-700 border-collapse">
            <thead class="bg-gray-50 text-gray-800 font-semibold">
              <tr>
                <th class="text-left py-2 px-4 border-b border-gray-200">Phiên bản</th>
                <th class="text-center py-2 px-4 border-b border-gray-200">Giá gốc</th>
                <th class="text-center py-2 px-4 border-b border-gray-200">Giá KM</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(variant, idx) in selectedRoom.variants" :key="idx"
                class="border-b border-gray-100 last:border-b-0">
                <td class="py-3 px-4">
                  <div class="font-semibold">{{ variant.breakfast }}</div>
                  <div class="text-xs text-gray-500 mt-1">
                    <span v-if="variant.hasBreakfast">Có bữa sáng</span>
                    <span v-else>Không bữa sáng</span>
                    <span class="mx-1">•</span>
                    <span v-if="variant.payAtHotel">Thanh toán tại khách sạn</span>
                    <span v-else>Thanh toán trước</span>
                    <span class="mx-1">•</span>
                    <span v-if="variant.cancellable">Hủy miễn phí</span>
                    <span v-else>Không hủy</span>
                  </div>
                </td>
                <td class="text-center py-3 px-4 line-through text-gray-500">
                  {{ variant.originalPrice }} VND
                </td>
                <td class="text-center py-3 px-4 text-red-600 font-bold">
                  {{ variant.discountedPrice }} VND
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue'
import { hotels } from '@/data/hotelData.js'
import { useRouter, useRoute } from 'vue-router'

const roomsSectionRef = ref(null)

const minRoomPrice = computed(() => {
  if (!roomTypes.value.length) return ''
  let min = Number.POSITIVE_INFINITY
  roomTypes.value.forEach(room => {
    room.variants.forEach(variant => {
      const price = parseFloat((variant.discountedPrice + '').replace(/\./g, '').replace(/,/g, ''))
      if (!isNaN(price) && price < min) min = price
    })
  })
  return min !== Number.POSITIVE_INFINITY ? min.toLocaleString('vi-VN') : ''
})

const scrollToRooms = () => {
  if (roomsSectionRef.value) {
    roomsSectionRef.value.scrollIntoView({ behavior: 'smooth' })
  }
}

const showMoreOverview = ref(false)
const showAllAmenities = ref(false)

const showModal = ref(false)
const selectedRoom = ref(null)

const roomImageIndex = ref({})

const reviewsList = ref([
  {
    name: 'John Smith',
    avatar: 'https://randomuser.me/api/portraits/men/32.jpg',
    rating: 4.5,
    comment: 'Great place, excellent service, and very clean. Highly recommended!',
  },
  {
    name: 'Emily Johnson',
    avatar: 'https://randomuser.me/api/portraits/women/45.jpg',
    rating: 5,
    comment: 'Perfect location and super helpful host!',
  },
  {
    name: 'Michael Brown',
    avatar: 'https://randomuser.me/api/portraits/men/28.jpg',
    rating: 4,
    comment: 'Nice room, a bit noisy nhưng manageable.',
  },
  {
    name: 'Sarah Davis',
    avatar: 'https://randomuser.me/api/portraits/women/55.jpg',
    rating: 4.5,
    comment: 'Very comfy beds and great amenities.',
  },
  {
    name: 'Tom Wilson',
    avatar: 'https://randomuser.me/api/portraits/men/52.jpg',
    rating: 3.5,
    comment: 'Could improve on cleanliness, but overall fine.',
  },
  {
    name: 'Alex Martin',
    avatar: 'https://randomuser.me/api/portraits/men/45.jpg',
    rating: 4,
    comment: 'Good value for money and friendly staff.',
  },
])

const props = defineProps({
  id: {
    type: [String, Number],
    required: true,
  },
})

const hotel = ref(null)
const roomTypes = ref([])

const initializeRoomIndices = () => {
  roomTypes.value.forEach((room) => {
    roomImageIndex.value[room.id] = 0
  })
}

const modalImageIndex = ref(0)
watch(selectedRoom, (room) => {
  if (room && room.images && room.images.length) {
    modalImageIndex.value = 0
  }
})

const findHotel = () => {
  const foundHotel = hotels.find((h) => h.id === parseInt(props.id))
  if (foundHotel) {
    hotel.value = {
      ...foundHotel,
      details: '6 khách • 4 giường • 1 phòng tắm riêng',
      amenitiesList: [
        { name: 'Nhà bếp', icon: 'fas fa-utensils' },
        { name: 'Bộ sơ cứu', icon: 'fas fa-first-aid' },
        { name: 'TV', icon: 'fas fa-tv' },
        { name: 'Đồ uống miễn phí', icon: 'fas fa-wine-glass-alt' },
        { name: 'Điều hòa', icon: 'fas fa-snowflake' },
        { name: 'Thuê xe đạp', icon: 'fas fa-bicycle' },
        { name: 'Wifi', icon: 'fas fa-wifi' },
        { name: 'Hệ thống sưởi', icon: 'fas fa-thermometer-half' },
      ],
      additionalImages: [
        'https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/10027533-8936a739daf46bb17c623254e65eadfb.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666',
        'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10027533-73d20c6f7dc45a59568b54c2931f4bcb.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666',
        'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10027533-a1b9e5ddc7b8cc55108aee73bbfee7c3.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666',
      ],
      price: '273000',
    }

    roomTypes.value = [
      {
        id: 1,
        label: 'Tiêu chuẩn',
        labelClass: 'bg-blue-100 text-blue-800',
        iconColor: 'text-blue-500',
        linkColor: 'text-blue-600',
        images: [
          'https://storage.googleapis.com/a1aa/image/344db20c-d038-4d9b-a473-fefd7c443548.jpg',
          'https://storage.googleapis.com/a1aa/image/68c6fc18-96ef-43c2-3bae-b96409260526.jpg',
        ],
        size: '17.0 m²',
        maxAdults: 2,
        maxChildren: 1,
        features: [
          { name: 'Vòi tắm đứng', icon: 'fas fa-shower' },
          { name: 'Tủ lạnh', icon: 'fas fa-snowflake' },
          { name: 'Khu vực chờ', icon: 'fas fa-chair' },
          { name: 'Máy lạnh', icon: 'fas fa-fan' },
        ],
        type: 'Standard Room',
        rate: 'Standard Rate',
        bed: '1 giường cỡ king',
        tag: 'Vị trí tốt',
        tagClass: 'bg-blue-500',
        buttonClass: 'bg-blue-600 text-white hover:bg-blue-700',
        variants: [
          {
            breakfast: 'Không bao gồm bữa sáng',
            hasBreakfast: false,
            cancellable: false,
            payAtHotel: false,
            originalPrice: '933.217',
            discountedPrice: '699.913',
          },
          {
            breakfast: 'Bao gồm bữa sáng',
            hasBreakfast: true,
            cancellable: true,
            payAtHotel: true,
            originalPrice: '1.033.217',
            discountedPrice: '799.913',
          },
        ],
      },
      {
        id: 2,
        label: 'Cao cấp',
        labelClass: 'bg-green-100 text-green-800',
        iconColor: 'text-green-500',
        linkColor: 'text-green-600',
        images: [
          'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/67778274-8c40a3ba78c6007e2e18af54a57143db.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320',
          'https://storage.googleapis.com/a1aa/image/0216c56f-c55e-4854-998f-8a6098451dd3.jpg',
        ],
        size: '20.0 m²',
        maxAdults: 2,
        maxChildren: 2,
        features: [
          { name: 'Bồn tắm', icon: 'fas fa-bath' },
          { name: 'Wifi miễn phí', icon: 'fas fa-wifi' },
          { name: 'TV màn hình phẳng', icon: 'fas fa-tv' },
          { name: 'Máy lạnh', icon: 'fas fa-fan' },
        ],
        type: 'Deluxe Room',
        rate: 'Deluxe Rate',
        bed: '1 giường cỡ queen',
        tag: 'Phổ biến',
        tagClass: 'bg-green-500',
        buttonClass: 'bg-green-600 text-white hover:bg-green-700',
        variants: [
          {
            breakfast: 'Không bao gồm bữa sáng',
            hasBreakfast: false,
            cancellable: false,
            payAtHotel: false,
            originalPrice: '1.200.000',
            discountedPrice: '950.000',
          },
          {
            breakfast: 'Bao gồm bữa sáng',
            hasBreakfast: true,
            cancellable: true,
            payAtHotel: false,
            originalPrice: '1.400.000',
            discountedPrice: '1.100.000',
          },
        ],
      },
      {
        id: 3,
        label: 'Thượng hạng',
        labelClass: 'bg-red-100 text-red-800',
        iconColor: 'text-red-500',
        linkColor: 'text-red-600',
        images: [
          'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/67778274-d22561c06b9bcec727af7a5fb1378627.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320',
          'https://storage.googleapis.com/a1aa/image/27a56a3d-1d5e-4fa7-720b-0fe8b04c4867.jpg',
        ],
        size: '22.0 m²',
        maxAdults: 3,
        maxChildren: 1,
        features: [
          { name: 'Bar mini', icon: 'fas fa-wine-glass-alt' },
          { name: 'Sofa', icon: 'fas fa-sofa' },
          { name: 'TV màn hình phẳng', icon: 'fas fa-tv' },
          { name: 'Máy lạnh', icon: 'fas fa-fan' },
        ],
        type: 'Superior Room',
        rate: 'Superior Rate',
        bed: '1 giường cỡ king',
        tag: 'Ưu đãi đặc biệt',
        tagClass: 'bg-red-500',
        buttonClass: 'bg-red-600 text-white hover:bg-red-700',
        variants: [
          {
            breakfast: 'Không bao gồm bữa sáng',
            hasBreakfast: false,
            cancellable: false,
            payAtHotel: false,
            originalPrice: '1.500.000',
            discountedPrice: '1.100.000',
          },
          {
            breakfast: 'Bao gồm bữa sáng + minibar',
            hasBreakfast: true,
            cancellable: true,
            payAtHotel: false,
            originalPrice: '1.700.000',
            discountedPrice: '1.300.000',
          },
        ],
      },
    ]
    initializeRoomIndices()
  } else {
    hotel.value = null
  }
}

onMounted(findHotel)
watch(() => props.id, findHotel)

const router = useRouter()
const route = useRoute()

const goToBooking = (room, variant) => {
  const hotelId = props.id
  const query = {
    hotelTitle: hotel.value.title,
    hotellocation: hotel.value.location,
    hotelRating: hotel.value.rating,
    hotelReviews: hotel.value.reviews,
    hotelImage: hotel.value.image,
    hotelDetails: hotel.value.details,
    roomLabel: room.label,
    roomType: room.type,
    roomRate: room.rate,
    roomBed: room.bed,
    variantBreakfast: variant.breakfast,
    variantOriginalPrice: variant.originalPrice,
    variantDiscountedPrice: variant.discountedPrice,
    checkin: route.query.checkin || '',
    checkout: route.query.checkout || '',
    guests: route.query.guests || '1',
  }

  router.push({
    name: 'HotelBooking',
    params: { id: hotelId },
    query,
  })
}

const openModal = (room) => {
  selectedRoom.value = JSON.parse(JSON.stringify(room))
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  selectedRoom.value = null
}

const slideDirection = ref('next')

const nextRoomImage = (roomId) => {
  slideDirection.value = 'next'
  const room = roomTypes.value.find((r) => r.id === roomId)
  if (!room || !room.images) return
  const count = room.images.length
  roomImageIndex.value[roomId] = (roomImageIndex.value[roomId] + 1) % count
}

const prevRoomImage = (roomId) => {
  slideDirection.value = 'prev'
  const room = roomTypes.value.find((r) => r.id === roomId)
  if (!room || !room.images) return
  const count = room.images.length
  roomImageIndex.value[roomId] =
    (roomImageIndex.value[roomId] - 1 + count) % count
}
</script>

<style scoped>
.slide-left-enter-active,
.slide-left-leave-active,
.slide-right-enter-active,
.slide-right-leave-active {
  transition: transform 0.3s ease;
}

.slide-left-enter-from {
  transform: translateX(100%);
}

.slide-left-leave-to {
  transform: translateX(-100%);
}

.slide-right-enter-from {
  transform: translateX(-100%);
}

.slide-right-leave-to {
  transform: translateX(100%);
}
</style>
