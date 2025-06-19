<template>
  <div v-if="loading" class="flex justify-center items-center min-h-screen bg-gray-50">
    <div class="text-center">
      <i class="fas fa-spinner fa-spin text-4xl text-blue-500"></i>
      <p class="text-xl text-gray-600 mt-4">Đang tải chi tiết khách sạn...</p>
    </div>
  </div>

  <div v-else-if="error" class="text-center py-20 bg-red-50">
    <p class="text-2xl text-red-600">Rất tiếc, đã có lỗi xảy ra!</p>
    <p class="text-gray-700 mt-2">{{ error }}</p>
    <router-link to="/" class="mt-4 inline-block bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700">
      Quay về trang chủ
    </router-link>
  </div>

  <main v-else-if="hotel" class="max-w-full mx-auto px-4 sm:px-6 lg:px-8 py-8 bg-gray-50" style="padding-top: 1rem;">
    <div class="sticky top-16 z-40 w-full rounded-lg border border-gray-200 bg-white shadow-lg p-3 mb-4 mt-4">
      <div class="flex flex-col md:flex-row items-stretch h-auto md:h-auto border border-gray-300 rounded-lg">
        <div
          class="relative flex flex-grow items-center p-3 bg-white border-b md:border-b-0 md:border-r border-gray-200 min-w-[200px] rounded-t-md md:rounded-l-md md:rounded-tr-none">
          <i class="fas fa-map-marker-alt text-blue-500 text-xl pr-3"></i>
          <div class="flex-1">
            <label class="text-xs text-gray-500">Địa điểm hoặc khách sạn</label>
            <input type="text" v-model="hotel.name"
              class="w-full bg-transparent font-semibold focus:outline-none text-gray-800" readonly />
          </div>
        </div>
        <div
          class="flex flex-grow-[2] items-center p-3 bg-white border-b md:border-b-0 md:border-r border-gray-200 min-w-[300px]">
          <i class="fas fa-calendar-alt text-blue-500 text-xl pr-3"></i>
          <div class="flex flex-1 items-center">
            <div class="flex-1">
              <label class="text-xs text-gray-500">Ngày nhận</label>
              <input type="date" v-model="searchParams.checkInDate" :min="today"
                class="w-full bg-transparent font-semibold focus:outline-none" />
            </div>
            <div class="px-4 text-center">
              <div v-if="numberOfNights > 0"
                class="text-xs font-semibold text-blue-600 bg-blue-100 rounded-full px-2 py-0.5 whitespace-nowrap">{{
                numberOfNights }} đêm</div>
            </div>
            <div class="flex-1">
              <label class="text-xs text-gray-500">Ngày trả</label>
              <input type="date" v-model="searchParams.checkOutDate" :min="minCheckOut"
                class="w-full bg-transparent font-semibold focus:outline-none" />
            </div>
          </div>
        </div>
        <button aria-label="Search"
          class="bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-3 font-bold transition flex items-center justify-center rounded-b-md md:rounded-l-none md:rounded-r-md"
          @click="onSearch">
          <i class="fas fa-search"></i>&nbsp; Cập nhật
        </button>
      </div>
    </div>

    <section class="bg-white rounded-xl shadow p-6 mb-8">
      <div class="flex flex-col lg:flex-row space-y-3 lg:space-y-0 lg:space-x-3 mb-8 h-auto lg:h-[500px]">
        <div class="flex-shrink-0 w-full lg:w-8/12 h-96 lg:h-full overflow-hidden rounded-lg shadow-md">
          <img v-if="hotel.imageUrls && hotel.imageUrls.length" :src="hotel.imageUrls[0]" :alt="hotel.name"
            class="w-full h-full object-cover transition-transform duration-300 hover:scale-105" />
        </div>
        <div class="flex lg:flex-col w-full lg:w-4/12 h-auto lg:h-full justify-between gap-3">
          <div v-for="(img, idx) in hotel.imageUrls.slice(1, 4)" :key="idx"
            class="h-48 lg:h-[160px] w-1/3 lg:w-full overflow-hidden rounded-lg shadow-md">
            <img :src="img" :alt="`Additional image ${idx + 1}`"
              class="w-full h-full object-cover transition-transform duration-300 hover:scale-105" />
          </div>
        </div>
      </div>

      <div class="flex flex-col sm:flex-row items-center justify-between mb-4">
        <div class="flex-1 text-left">
          <h1 class="text-3xl font-extrabold text-gray-900 mb-1">{{ hotel.name }}</h1>
          <div class="flex items-center flex-wrap gap-x-2 text-sm text-gray-600">
            <div class="flex items-center text-yellow-400">
              <i v-for="n in hotel.starRating" :key="n" class="fas fa-star"></i>
            </div>
            <span class="mx-2">•</span>
            <i class="fas fa-thumbs-up text-blue-500"></i>
            <span class="text-blue-600 font-semibold">{{ hotel.rating.toFixed(1) }}</span>
            <span>({{ hotel.reviewCount }} đánh giá)</span>
            <span class="mx-2">•</span>
            <span>{{ hotel.provinceName }}</span>
          </div>
        </div>
        <div class="flex items-center gap-4 mt-4 sm:mt-0">
          <div class="text-left"><span class="text-gray-500 text-sm block">Giá/phòng/đêm từ</span>
            <div class="text-2xl font-bold text-indigo-600 whitespace-nowrap">{{ minRoomPrice }}</div>
          </div>
          <button
            class="bg-indigo-600 hover:bg-indigo-700 text-white rounded-full px-6 py-2 text-base font-semibold transition focus:outline-none focus:ring-2 focus:ring-indigo-500"
            @click="scrollToRooms">Chọn phòng</button>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-4 mb-10">
        <div>
          <h2 class="text-xl font-bold text-gray-800 mb-4">Tổng quan</h2>
          <div class="text-sm text-gray-700 leading-relaxed space-y-3">
            <p :class="!showMoreOverview && 'line-clamp-4'" v-html="hotel.description"></p>
            <button @click="showMoreOverview = !showMoreOverview"
              class="text-blue-600 font-semibold text-sm hover:underline focus:outline-none">{{ showMoreOverview ? "Thu gọn" : "Xem thêm" }}</button>

            <ul class="space-y-2 pt-4">
              <li class="flex items-start"><i class="fas fa-map-marker-alt text-blue-500 w-5 mt-1"></i><span>{{
                  hotel.address }}</span></li>
              <li class="flex items-center"><i class="fas fa-phone-alt text-blue-500 w-5"></i><span>{{ hotel.phone
                  }}</span></li>
              <li class="flex items-center"><i class="fas fa-envelope text-blue-500 w-5"></i><span>{{ hotel.email
                  }}</span></li>
            </ul>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-800 mb-4">Các tiện ích nổi bật</h3>
          <div class="grid grid-cols-2 gap-y-3 text-sm text-gray-700 mb-4">
            <div v-for="amenity in visibleAmenities" :key="amenity.name"
              class="flex items-center space-x-2 font-medium">
              <i :class="amenity.icon || 'fas fa-check'" class="text-green-500 w-5"></i>
              <span>{{ amenity.name }}</span>
            </div>
          </div>
          <button v-if="allHotelAmenities.length > 6" @click="showAllAmenities = !showAllAmenities"
            class="mt-2 text-sm border border-gray-300 text-gray-700 rounded-full px-4 py-2 hover:bg-gray-100 focus:outline-none transition-colors duration-200">{{
              showAllAmenities ? "Ẩn bớt" : "Xem tất cả tiện ích" }}</button>
        </div>
      </div>
    </section>

    <section ref="roomsSectionRef" class="bg-white rounded-xl p-6 mb-8">
      <h2 class="text-2xl font-bold text-gray-800 mb-6">Những phòng trống tại {{ hotel.name }}</h2>

      <section class="bg-gray-50 rounded-xl shadow-inner p-6 mb-8">
        <h3 class="text-lg font-bold text-gray-800 pb-4 mb-2">Tìm kiếm nhanh hơn bằng cách chọn những tiện nghi bạn cần
        </h3>
        <div class="flex flex-wrap gap-4">
          <div v-for="filter in amenityFilters" :key="filter.id" class="flex items-center">
            <input :id="`filter-${filter.id}`" type="checkbox" :value="filter.id" v-model="selectedAmenities"
              class="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded" />
            <label :for="`filter-${filter.id}`" class="ml-2 block text-sm text-gray-900 cursor-pointer">{{ filter.label
              }}</label>
          </div>
        </div>
      </section>

      <div v-if="filteredRoomTypes.length === 0" class="text-center py-10">
        <p class="text-xl text-gray-700">Không tìm thấy phòng nào phù hợp với lựa chọn của bạn.</p>
      </div>
      <div v-else v-for="room in filteredRoomTypes" :key="room.id"
        class="flex flex-col md:flex-row gap-6 border border-gray-200 rounded-lg p-5 mb-6">
        <div class="relative flex flex-col gap-4 md:w-1/3">
          <h3 class="text-lg font-bold text-black">{{ room.roomType }}</h3>
          <div class="relative w-full h-48 overflow-hidden rounded-lg shadow-md">
            <transition :name="slideDirection === 'next' ? 'slide-right' : 'slide-left'">
              <img v-if="room.imageUrls && room.imageUrls.length" :key="roomImageIndex[room.id]"
                :src="room.imageUrls[roomImageIndex[room.id]]" :alt="room.roomType"
                class="w-full h-full object-cover absolute inset-0" />
            </transition>
            <button @click="prevRoomImage(room.id)"
              class="absolute left-2 top-1/2 transform -translate-y-1/2 bg-white/60 rounded-full p-2 hover:bg-white/80 z-10"><i
                class="fas fa-chevron-left text-gray-700"></i></button>
            <button @click="nextRoomImage(room.id)"
              class="absolute right-2 top-1/2 transform -translate-y-1/2 bg-white/60 rounded-full p-2 hover:bg-white/80 z-10"><i
                class="fas fa-chevron-right text-gray-700"></i></button>
          </div>
          <div class="flex flex-col gap-2 text-sm text-gray-700 font-semibold mt-2">
            <div class="flex items-center gap-2"><i class="fas fa-ruler-combined text-blue-500"></i><span>{{
              room.roomArea || 'N/A' }} m²</span></div>
            <div class="flex items-center gap-2"><i class="fas fa-smoking-ban text-gray-500"></i><span>Không hút
                thuốc</span></div>
            <div class="flex flex-wrap gap-x-6 gap-y-2 text-xs text-gray-500 font-normal mt-2">
              <div v-for="amenity in room.amenities.slice(0, 7)" :key="amenity.name" class="flex items-center gap-1">
                <i :class="amenity.icon || 'fas fa-check'"></i><span>{{ amenity.name }}</span>
              </div>
            </div>
          </div>
          <button @click="openModal(room)"
            class="font-bold text-sm flex items-center gap-1 hover:underline mt-2 text-blue-600"><i
              class="fas fa-info-circle"></i> Xem chi tiết phòng</button>
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
              <tr v-for="variant in room.availableVariants" :key="variant.id"
                class="border-b border-gray-100 last:border-b-0">
                <td class="py-4 px-4 align-top">
                  <p class="text-xs text-gray-500">{{ variant.variantName }}</p>
                  <p class="font-bold text-base text-black mb-1">{{ variant.hasBreakfast ? 'Bao gồm bữa sáng' :
                    'Không bao gồm bữa sáng' }}</p>
                  <div class="mt-2 space-y-1">
                    <div class="flex items-center gap-1 text-gray-500 text-xs"><i class="fas fa-bed w-4"></i><span>{{
                        room.bedType }}</span></div>
                    <div v-if="variant.cancellable" class="flex items-center gap-1 text-green-600 text-xs"><i
                        class="fas fa-check-circle w-4"></i><span>Miễn phí hủy phòng</span></div>
                    <div v-if="variant.payAtHotel" class="flex items-center gap-1 text-green-600 text-xs"><i
                        class="fas fa-check-circle w-4"></i><span>Thanh toán tại khách sạn</span></div>
                  </div>
                </td>
                <td class="text-center align-top py-4 px-4 text-xl text-gray-600 relative group">
                  <i class="fas fa-user-friends cursor-pointer pt-10"></i>
                  <div
                    class="absolute bottom-full mb-2 left-1/2 -translate-x-1/2 w-max bg-gray-800 text-white text-xs rounded py-1 px-2 opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap z-10">
                    Tối đa {{ room.maxAdults }} người lớn, {{ room.maxChildren }} trẻ em
                    <div
                      class="absolute top-full left-1/2 -translate-x-1/2 border-4 border-transparent border-t-gray-800">
                    </div>
                  </div>
                </td>
                <td class="text-center align-middle py-4 px-4">
                  <p class="text-red-600 font-bold text-base">{{ formatPrice(variant.price) }}</p>
                  <p class="text-xs text-gray-400 font-normal mt-1">Chưa bao gồm thuế và phí</p>
                </td>
                <td class="text-center align-middle py-4 px-4"><button
                    class="rounded-lg px-5 py-2 text-sm font-semibold transition-colors duration-200 shadow-md bg-blue-600 text-white hover:bg-blue-700">Chọn</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </section>

    <section v-if="reviewsList.length > 0" class="bg-white rounded-xl shadow p-6 mb-8">
      <div class="flex items-center gap-4 mb-8">
        <h2 class="text-2xl font-bold text-gray-800">{{ hotel.reviewCount }} đánh giá</h2>
      </div>
      <div class="space-y-6">
        <div v-for="review in paginatedReviews" :key="review.id"
          class="bg-white p-6 rounded-lg border border-gray-200 shadow-sm">
          <div class="flex items-start gap-6">
            <div class="flex-shrink-0 w-40">
              <div class="flex items-center gap-4">
                <img :alt="review.username" class="w-12 h-12 rounded-full object-cover"
                  :src="review.avatar || 'https://i.pravatar.cc/150?u=' + review.username" />
                <div>
                  <h3 class="text-base font-bold text-gray-900">{{ review.username }}</h3>
                  <p class="text-sm text-gray-500 mt-0.5">{{ formatDate(review.createdAt) }}</p>
                </div>
              </div>
            </div>
            <div class="flex-grow">
              <div class="flex items-center justify-between mb-2">
                <div
                  class="flex items-center gap-1.5 bg-blue-100 text-blue-800 font-bold text-sm px-2.5 py-1 rounded-full">
                  <i class="fas fa-star text-xs"></i><span>{{ review.rating.toFixed(1) }}</span></div>
              </div>
              <p class="text-gray-700 leading-relaxed">{{ review.comment }}</p>
            </div>
          </div>
        </div>
      </div>
      <div class="mt-8 flex flex-col sm:flex-row justify-between items-center gap-4">
        <div class="flex items-center gap-2">
          <label for="reviews-per-page" class="text-sm font-medium text-gray-700">Hiển thị:</label>
          <select id="reviews-per-page" v-model="reviewsPerPage"
            class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block p-1.5">
            <option v-for="option in reviewsPerPageOptions" :key="option.value" :value="option.value">{{ option.text }}
            </option>
          </select>
        </div>
        <div class="flex items-center gap-4">
          <span class="text-sm font-medium text-gray-700">Trang {{ currentPage }} / {{ totalPages }}</span>
          <div class="flex items-center gap-2">
            <button @click="prevPage" :disabled="currentPage === 1"
              class="inline-flex items-center justify-center h-8 w-8 rounded-full border bg-white text-gray-600 hover:bg-gray-100 disabled:bg-gray-50 disabled:text-gray-300 disabled:cursor-not-allowed transition-colors"><i
                class="fas fa-chevron-left text-xs"></i></button>
            <button @click="nextPage" :disabled="currentPage === totalPages"
              class="inline-flex items-center justify-center h-8 w-8 rounded-full border bg-white text-gray-600 hover:bg-gray-100 disabled:bg-gray-50 disabled:text-gray-300 disabled:cursor-not-allowed transition-colors"><i
                class="fas fa-chevron-right text-xs"></i></button>
          </div>
        </div>
      </div>
    </section>

    <section v-if="otherHotels.length > 0" class="w-full bg-[#f0f8ff] rounded-lg p-6 shadow-sm mb-8">
      <div class="mb-6">
        <h2 class="font-sans font-extrabold text-xl leading-6 text-gray-800">Cơ sở lưu trú khác bạn có thể thích</h2>
        <p class="font-sans text-sm text-gray-600">Những khách sạn tương tự trong khu vực</p>
      </div>
      <div aria-label="Accommodation suggestions" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <HotelCard v-for="other in otherHotels" :key="other.id" :hotel="other" />
      </div>
    </section>

  </main>

  <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center pt-15"
    style="backdrop-filter: blur(6px); background-color: rgba(0, 0, 0, 0.15)" @click.self="closeModal">
    <div
      class="bg-white rounded-xl shadow-xl max-w-4xl w-full mx-4 lg:mx-0 overflow-hidden relative flex flex-col md:flex-row"
      style="max-height: 80vh">
      <button @click="closeModal" class="absolute top-4 right-4 text-gray-500 hover:text-gray-800 z-10"><i
          class="fas fa-times fa-lg"></i></button>
      <div class="md:w-1/2 w-full flex flex-col bg-gray-50">
        <div class="flex-grow flex items-center justify-center p-4"><img
            v-if="selectedRoom.imageUrls && selectedRoom.imageUrls.length"
            :src="selectedRoom.imageUrls[modalImageIndex]" :alt="selectedRoom.roomType"
            class="object-contain max-h-full w-full rounded-lg transition-all duration-200" /></div>
        <div class="flex gap-2 justify-center p-2 bg-white">
          <img v-for="(img, idx) in selectedRoom.imageUrls" :key="idx" :src="img" :alt="`Ảnh ${idx + 1}`"
            class="w-16 h-16 object-cover rounded cursor-pointer border-2"
            :class="modalImageIndex === idx ? 'border-blue-500' : 'border-transparent'"
            @click="modalImageIndex = idx" />
        </div>
      </div>
      <div class="md:w-1/2 w-full p-6 overflow-y-auto" style="max-height: 80vh">
        <h2 class="text-2xl font-bold text-gray-800 mb-4">{{ selectedRoom.roomType }}</h2>
        <div class="mb-6 text-sm text-gray-700">
          <p class="flex items-center mb-2"><i class="fas fa-ruler-combined text-blue-500 mr-2"></i>Diện tích: <span
              class="font-semibold ml-1">{{ selectedRoom.roomArea }} m²</span></p>
          <p class="flex items-center"><i class="fas fa-user-friends text-blue-500 mr-2"></i>Sức chứa: <span
              class="font-semibold ml-1">{{ selectedRoom.maxAdults }} người lớn</span>, <span
              class="font-semibold ml-1">{{ selectedRoom.maxChildren }} trẻ em</span></p>
        </div>
        <div class="mb-6">
          <h3 class="text-lg font-semibold text-gray-800 mb-2">Toàn bộ tiện ích:</h3>
          <div class="flex flex-wrap gap-2">
            <div v-for="amenity in selectedRoom.amenities" :key="amenity.name"
              class="flex items-center space-x-2 bg-gray-100 rounded-full px-3 py-1 text-xs text-gray-700 mb-2">
              <i :class="amenity.icon || 'fas fa-check'" class="text-green-600"></i><span>{{ amenity.name }}</span>
            </div>
          </div>
        </div>
        <div>
          <h3 class="text-lg font-semibold text-gray-800 mb-2">Các lựa chọn dịch vụ & giá:</h3>
          <table class="w-full text-sm text-gray-700 border-collapse">
            <thead class="bg-gray-50 text-gray-800 font-semibold">
              <tr>
                <th class="text-left py-2 px-4 border-b border-gray-200">Lựa chọn</th>
                <th class="text-center py-2 px-4 border-b border-gray-200">Giá</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="variant in selectedRoom.availableVariants" :key="variant.id"
                class="border-b border-gray-100 last:border-b-0">
                <td class="py-3 px-4">
                  <div class="font-semibold">{{ variant.variantName }}</div>
                </td>
                <td class="text-center py-3 px-4 text-red-600 font-semibold">{{ formatPrice(variant.price) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getHotelById, getHotelReviews, searchHotels } from "@/api/hotelApi";
import HotelCard from '@/components/Home/HotelCard.vue';

const hotel = ref(null);
const reviewsList = ref([]);
const otherHotels = ref([]);
const loading = ref(true);
const error = ref(null);
const route = useRoute();
const router = useRouter();
const searchParams = ref({
  location: '',
  checkin: route.query.checkin || new Date().toISOString().split("T")[0],
  checkout: route.query.checkout || '',
  adults: Number(route.query.adults) || 2,
  children: Number(route.query.children) || 0,
  rooms: Number(route.query.rooms) || 1,
});

const showMoreOverview = ref(false);
const showAllAmenities = ref(false);
const showModal = ref(false);
const selectedRoom = ref(null);
const roomImageIndex = ref({});
const modalImageIndex = ref(0);
const slideDirection = ref("next");
const roomsSectionRef = ref(null);
const locationContainer = ref(null);
const guestsContainer = ref(null);
const showLocationDropdown = ref(false);
const showGuestsDropdown = ref(false);
const guestsError = ref('');
const errorTimeout = ref(null);
const lastLocation = ref('');

const amenityFilters = ref([
  { id: "cancellable", label: "Miễn phí hủy phòng" },
  { id: "hasBreakfast", label: "Có bữa sáng" },
  { id: "payAtHotel", label: "Thanh toán tại khách sạn" },
]);
const selectedAmenities = ref([]);

const fetchHotelData = async (hotelId) => {
  loading.value = true;
  error.value = null;
  const apiParams = {
    checkInDate: searchParams.value.checkin,
    checkOutDate: searchParams.value.checkout,
  };
  try {
    const [hotelResponse, reviewsResponse] = await Promise.all([
      getHotelById(hotelId, apiParams),
      getHotelReviews(hotelId)
    ]);

    if (hotelResponse.data?.statusCode === 200) {
      hotel.value = hotelResponse.data.data;
      searchParams.value.location = hotel.value.name;
      lastLocation.value = hotel.value.name;
      initializeRoomImageIndices();
      if (hotel.value.provinceId) {
        fetchOtherHotels(hotel.value.provinceId, hotelId);
      }
    } else {
      throw new Error(hotelResponse.data?.message || "Không thể tải thông tin khách sạn.");
    }

    if (reviewsResponse.data?.statusCode === 200) {
      reviewsList.value = reviewsResponse.data.data;
    } else {
      reviewsList.value = [];
    }
  } catch (err) {
    console.error("Lỗi khi tải dữ liệu chi tiết:", err);
    error.value = "Không tìm thấy khách sạn hoặc đã có lỗi xảy ra. Vui lòng thử lại.";
    hotel.value = null;
  } finally {
    loading.value = false;
  }
};

const fetchOtherHotels = async (provinceId, currentHotelId) => {
  try {
    const response = await searchHotels({ provinceId: provinceId, size: 4 });
    if (response.data?.statusCode === 200) {
      otherHotels.value = response.data.data.content.filter(h => h.id !== currentHotelId).slice(0, 3);
    }
  } catch (err) {
    console.error("Lỗi khi tải khách sạn tương tự:", err);
    otherHotels.value = [];
  }
}

const minCheckOut = computed(() => {
  if (!searchParams.value.checkin) return new Date().toISOString().split("T")[0];
  const d = new Date(searchParams.value.checkin);
  d.setDate(d.getDate() + 1);
  return d.toISOString().split("T")[0];
});

const numberOfNights = computed(() => {
  if (searchParams.value.checkin && searchParams.value.checkout) {
    const s = new Date(searchParams.value.checkin);
    const e = new Date(searchParams.value.checkout);
    if (e <= s) return 0;
    return Math.ceil((e - s) / (1000 * 60 * 60 * 24));
  }
  return 0;
});

const guestsDisplay = computed(() => `${searchParams.value.adults} người lớn, ${searchParams.value.children} trẻ em, ${searchParams.value.rooms} phòng`);

const allHotelAmenities = computed(() => {
  if (!hotel.value?.availableRooms) return [];
  const amenitiesMap = new Map();
  hotel.value.availableRooms.forEach(room => {
    room.amenities?.forEach(amenity => {
      if (!amenitiesMap.has(amenity.name)) {
        amenitiesMap.set(amenity.name, { ...amenity, icon: amenity.icon || 'fas fa-check' });
      }
    });
  });
  return Array.from(amenitiesMap.values());
});

const visibleAmenities = computed(() => showAllAmenities.value ? allHotelAmenities.value : allHotelAmenities.value.slice(0, 6));

const filteredRoomTypes = computed(() => {
  if (!hotel.value?.availableRooms) return [];

  const roomsCopy = JSON.parse(JSON.stringify(hotel.value.availableRooms));

  if (selectedAmenities.value.length === 0) {
    return roomsCopy;
  }

  const filteredRooms = roomsCopy.map(room => {
    const matchingVariants = room.availableVariants.filter(variant => {
      return selectedAmenities.value.every(amenityKey => variant[amenityKey] === true);
    });
    return { ...room, availableVariants: matchingVariants };
  }).filter(room => room.availableVariants.length > 0);

  return filteredRooms;
});

const minRoomPrice = computed(() => {
  if (!hotel.value?.availableRooms?.length) return "Đang cập nhật";
  let minPrice = Infinity;
  hotel.value.availableRooms.forEach(room => {
    room.availableVariants.forEach(variant => {
      if (variant.price < minPrice) minPrice = variant.price;
    });
  });
  return minPrice === Infinity ? "Liên hệ" : formatPrice(minPrice);
});

const currentPage = ref(1);
const reviewsPerPage = ref(5);
const reviewsPerPageOptions = ref([
  { value: 5, text: "5 đánh giá" },
  { value: 10, text: "10 đánh giá" },
]);

const totalPages = computed(() => {
  if (reviewsList.value.length === 0) return 1;
  return Math.ceil(reviewsList.value.length / reviewsPerPage.value);
});

const paginatedReviews = computed(() => {
  const start = (currentPage.value - 1) * reviewsPerPage.value;
  const end = start + reviewsPerPage.value;
  return reviewsList.value.slice(start, end);
});

const nextPage = () => { if (currentPage.value < totalPages.value) currentPage.value++; };
const prevPage = () => { if (currentPage.value > 1) currentPage.value--; };

watch(reviewsPerPage, () => { currentPage.value = 1; });

const formatPrice = (price) => {
  if (price == null || isNaN(price)) return "N/A";
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    currencyDisplay: 'code'
  }).format(price).replace('VND', ' VND');
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
};

const onSearch = () => {
  fetchHotelData(route.params.id);
  router.push({
    query: {
      checkin: searchParams.value.checkin,
      checkout: searchParams.value.checkout,
      adults: searchParams.value.adults,
      children: searchParams.value.children,
      rooms: searchParams.value.rooms,
    }
  });
};

const scrollToRooms = () => roomsSectionRef.value?.scrollIntoView({ behavior: 'smooth', block: 'start' });

const initializeRoomImageIndices = () => {
  hotel.value?.availableRooms?.forEach(room => { roomImageIndex.value[room.id] = 0; });
};

const nextRoomImage = (roomId) => {
  slideDirection.value = 'next';
  const room = hotel.value.availableRooms.find(r => r.id === roomId);
  if (!room?.imageUrls?.length) return;
  roomImageIndex.value[roomId] = (roomImageIndex.value[roomId] + 1) % room.imageUrls.length;
};

const prevRoomImage = (roomId) => {
  slideDirection.value = 'prev';
  const room = hotel.value.availableRooms.find(r => r.id === roomId);
  if (!room?.imageUrls?.length) return;
  roomImageIndex.value[roomId] = (roomImageIndex.value[roomId] - 1 + room.imageUrls.length) % room.imageUrls.length;
};

const openModal = (room) => {
  selectedRoom.value = room;
  modalImageIndex.value = 0;
  showModal.value = true;
};
const closeModal = () => {
  showModal.value = false;
  selectedRoom.value = null;
};

const handleLocationFocus = () => {
  lastLocation.value = searchParams.value.location;
  searchParams.value.location = '';
  showLocationDropdown.value = false;
};

const updateGuests = (type, amount) => {
  const params = searchParams.value;
  if (errorTimeout.value) clearTimeout(errorTimeout.value);
  guestsError.value = '';

  if (type === 'adults') {
    const newAdults = params.adults + amount;
    if (newAdults >= 1) {
      params.adults = newAdults;
      if (params.adults < params.rooms) {
        params.rooms = params.adults;
      }
    }
  } else if (type === 'children') {
    params.children = Math.max(0, params.children + amount);
  } else if (type === 'rooms') {
    const newRooms = params.rooms + amount;
    if (amount > 0) {
      if (newRooms > params.adults) {
        guestsError.value = 'Số phòng không thể nhiều hơn số người lớn.';
        errorTimeout.value = setTimeout(() => { guestsError.value = '' }, 3000);
      } else {
        params.rooms = newRooms;
      }
    } else {
      params.rooms = Math.max(1, newRooms);
    }
  }
};

const handleClickOutside = (event) => {
  if (guestsContainer.value && !guestsContainer.value.contains(event.target)) {
    showGuestsDropdown.value = false;
  }
};

watch(() => route.params.id, (newId, oldId) => {
  if (newId && newId !== oldId) {
    fetchHotelData(newId);
    window.scrollTo(0, 0);
  }
}, { immediate: true });

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
  if (!searchParams.value.checkout || new Date(searchParams.value.checkout) <= new Date(searchParams.value.checkin)) {
    searchParams.value.checkout = minCheckOut.value;
  }
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<style scoped>
.line-clamp-4 {
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-clamp: 4;
}

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