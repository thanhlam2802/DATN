<template>
  <div class="w-full min-h-screen text-base md:text-lg relative">
    <div class="relative">
      <BannerAndSearch />
    </div>
    <SignatureSection />
    <CouponSection />
    <div class="container mx-auto px-4 py-8 space-y-12 md:space-y-16">
      <section>
        <div
          class="flex flex-col sm:flex-row sm:justify-between sm:items-center mb-4 gap-2"
        >
          <h2 class="text-xl sm:text-2xl font-bold text-gray-800">
            Gợi ý Khách sạn cho bạn
          </h2>
          <router-link
            :to="{ name: 'Hotel' }"
            class="text-sm sm:text-base text-blue-600 font-semibold hover:underline self-end sm:self-center"
          >
            Xem tất cả
          </router-link>
        </div>

        <div v-if="loading.hotels" class="text-center p-10">
          <p class="text-gray-600">Đang tìm những khách sạn tốt nhất...</p>
        </div>
        <div
          v-else-if="error.hotels"
          class="text-center p-10 bg-red-50 text-red-600 rounded-lg"
        >
          <p>Rất tiếc, đã có lỗi xảy ra. Vui lòng thử lại sau.</p>
        </div>
        <div v-else-if="hotels.length > 0" class="relative group">
          <div
            ref="hotelScrollContainer"
            @scroll="() => handleScroll('hotels')"
            class="flex overflow-x-auto space-x-4 sm:space-x-6 pb-4 scrollbar-hide snap-x snap-mandatory -mx-4 px-4"
          >
            <HotelCard
              v-for="hotel in hotels"
              :key="hotel.id"
              :hotel="hotel"
              class="snap-start"
            />
          </div>
          <button
            v-if="scrollState.hotels.canScrollLeft"
            @click="scroll('hotels', 'left')"
            class="hidden md:flex absolute top-1/2 left-0 transform -translate-y-1/2 -translate-x-1/2 bg-white rounded-full w-10 h-10 items-center justify-center shadow-lg text-gray-700 hover:bg-gray-100 transition opacity-0 group-hover:opacity-100 z-10"
          >
            <i class="fas fa-chevron-left"></i>
          </button>
          <button
            v-if="scrollState.hotels.canScrollRight"
            @click="scroll('hotels', 'right')"
            class="hidden md:flex absolute top-1/2 right-0 transform -translate-y-1/2 translate-x-1/2 bg-white rounded-full w-10 h-10 items-center justify-center shadow-lg text-gray-700 hover:bg-gray-100 transition opacity-0 group-hover:opacity-100 z-10"
          >
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </section>

      <section>
        <div
          class="flex flex-col sm:flex-row sm:justify-between sm:items-center mb-4 gap-2"
        >
          <h2 class="text-xl sm:text-2xl font-bold text-gray-800">
            Tuyến xe phổ biến
          </h2>
          <router-link
            :to="{ name: 'Bus' }"
            class="text-sm sm:text-base text-blue-600 font-semibold hover:underline self-end sm:self-center"
          >
            Xem tất cả
          </router-link>
        </div>
        <div class="relative group">
          <div
            ref="busScrollContainer"
            @scroll="() => handleScroll('buses')"
            class="flex overflow-x-auto space-x-4 sm:space-x-6 pb-4 scrollbar-hide snap-x snap-mandatory -mx-4 px-4"
          >
            <BusCard
              v-for="bus in buses"
              :key="bus.id"
              :bus="bus"
              class="snap-start"
            />
          </div>
          <button
            v-if="scrollState.buses.canScrollLeft"
            @click="scroll('buses', 'left')"
            class="hidden md:flex absolute top-1/2 left-0 transform -translate-y-1/2 -translate-x-1/2 bg-white rounded-full w-10 h-10 items-center justify-center shadow-lg text-gray-700 hover:bg-gray-100 transition opacity-0 group-hover:opacity-100 z-10"
          >
            <i class="fas fa-chevron-left"></i>
          </button>
          <button
            v-if="scrollState.buses.canScrollRight"
            @click="scroll('buses', 'right')"
            class="hidden md:flex absolute top-1/2 right-0 transform -translate-y-1/2 translate-x-1/2 bg-white rounded-full w-10 h-10 items-center justify-center shadow-lg text-gray-700 hover:bg-gray-100 transition opacity-0 group-hover:opacity-100 z-10"
          >
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </section>

      <section>
        <div
          class="flex flex-col sm:flex-row sm:justify-between sm:items-center mb-4 gap-2"
        >
          <h2 class="text-xl sm:text-2xl font-bold text-gray-800">
            Chuyến bay giá tốt
          </h2>
          <router-link
            :to="{ name: 'Plane' }"
            class="text-sm sm:text-base text-blue-600 font-semibold hover:underline self-end sm:self-center"
          >
            Xem tất cả
          </router-link>
        </div>
        <div class="relative group">
          <div
            ref="flightScrollContainer"
            @scroll="() => handleScroll('flights')"
            class="flex overflow-x-auto space-x-4 sm:space-x-6 pb-4 scrollbar-hide snap-x snap-mandatory -mx-4 px-4"
          >
            <FlightCard
              v-for="flight in flights"
              :key="flight.id"
              :flight="flight"
              class="snap-start"
            />
          </div>
          <button
            v-if="scrollState.flights.canScrollLeft"
            @click="scroll('flights', 'left')"
            class="hidden md:flex absolute top-1/2 left-0 transform -translate-y-1/2 -translate-x-1/2 bg-white rounded-full w-10 h-10 items-center justify-center shadow-lg text-gray-700 hover:bg-gray-100 transition opacity-0 group-hover:opacity-100 z-10"
          >
            <i class="fas fa-chevron-left"></i>
          </button>
          <button
            v-if="scrollState.flights.canScrollRight"
            @click="scroll('flights', 'right')"
            class="hidden md:flex absolute top-1/2 right-0 transform -translate-y-1/2 translate-x-1/2 bg-white rounded-full w-10 h-10 items-center justify-center shadow-lg text-gray-700 hover:bg-gray-100 transition opacity-0 group-hover:opacity-100 z-10"
          >
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </section>

      <section>
        <div
          class="flex flex-col sm:flex-row sm:justify-between sm:items-center mb-4 gap-2"
        >
          <h2 class="text-xl sm:text-2xl font-bold text-gray-800">
            Tour du lịch hấp dẫn
          </h2>
          <router-link
            :to="{ name: 'Tour' }"
            class="text-sm sm:text-base text-blue-600 font-semibold hover:underline self-end sm:self-center"
          >
            Xem tất cả
          </router-link>
        </div>

        <div v-if="loading.tours" class="text-center p-10">
          <p class="text-gray-600">Đang tìm các tour hấp dẫn nhất...</p>
        </div>
        <div
          v-else-if="error.tours"
          class="text-center p-10 bg-red-50 text-red-600 rounded-lg"
        >
          <p>Rất tiếc, đã có lỗi xảy ra khi tải tour. Vui lòng thử lại sau.</p>
        </div>

        <div v-else class="relative group">
          <div
            ref="tourScrollContainer"
            @scroll="() => handleScroll('tours')"
            class="flex overflow-x-auto space-x-4 sm:space-x-6 pb-4 scrollbar-hide snap-x snap-mandatory -mx-4 px-4"
          >
            <TourHomeCard
              v-for="tour in tours"
              :key="tour.id"
              :tour="tour"
              class="snap-start"
            />
          </div>
          <button
            v-if="scrollState.tours.canScrollLeft"
            @click="scroll('tours', 'left')"
            class="hidden md:flex absolute top-1/2 left-0 transform -translate-y-1/2 -translate-x-1/2 bg-white rounded-full w-10 h-10 items-center justify-center shadow-lg text-gray-700 hover:bg-gray-100 transition opacity-0 group-hover:opacity-100 z-10"
          >
            <i class="fas fa-chevron-left"></i>
          </button>
          <button
            v-if="scrollState.tours.canScrollRight"
            @click="scroll('tours', 'right')"
            class="hidden md:flex absolute top-1/2 right-0 transform -translate-y-1/2 translate-x-1/2 bg-white rounded-full w-10 h-10 items-center justify-center shadow-lg text-gray-700 hover:bg-gray-100 transition opacity-0 group-hover:opacity-100 z-10"
          >
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from "vue";
import axios from "axios";
import BannerAndSearch from "../components/Home/BannerAndSearch.vue";
import SignatureSection from "../components/Home/SignatureSection.vue";
import CouponSection from "../components/Home/CouponSection.vue";
import HotelCard from "../components/Home/HotelCard.vue";
import BusCard from "../components/Home/BusCard.vue";
import FlightCard from "../components/Home/FlightCard.vue";
import TourHomeCard from "../components/Home/TourHomeCard.vue";
import { searchFlights } from "@/api/flightApi";
import { searchHotels } from "../api/hotelApi";

// State variables
const hotels = ref([]);
const flights = ref([]);
const tours = ref([]);
const buses = ref([]); 

const loading = reactive({
  hotels: true,
  tours: true,
});
const error = reactive({
  hotels: null,
  tours: null,
});

// Hàm fetch dữ liệu
const fetchHotels = async () => {
  try {
    loading.hotels = true;
    error.hotels = null;
    const response = await searchHotels({ size: 8, sortBy: "popular" });

    if (response.data && response.data.statusCode === 200) {
      hotels.value = response.data.data.content;
    } else {
      throw new Error(response.data.message || "Lỗi không xác định từ server");
    }
  } catch (err) {
    console.error("Lỗi khi tải danh sách khách sạn:", err);
    error.hotels = "Không thể tải dữ liệu khách sạn. Vui lòng thử lại sau.";
  } finally {
    loading.hotels = false;
    nextTick(() => handleScroll("hotels"));
  }
};
const fetchTours = async () => {
  try {
    loading.tours = true;
    error.tours = null;
    const response = await axios.get("http://localhost:8080/api/v1/tours", {
      params: { size: 8 },
    });

    // SỬA LỖI Ở ĐÂY: Kiểm tra bằng statusCode thay vì success
    if (response.data && response.data.statusCode === 200) {
      tours.value = response.data.data.content;
    } else {
      // Ném lỗi nếu cấu trúc response không như ý muốn
      throw new Error(response.data.message || "Lỗi không xác định từ server");
    }
  } catch (err) {
    console.error("Lỗi khi tải danh sách tour:", err);
    error.tours = "Không thể tải dữ liệu tour. Vui lòng thử lại sau.";
  } finally {
    loading.tours = false;
    nextTick(() => {
      handleScroll("tours");
    });
  }
};

// Logic xử lý cuộn ngang
const hotelScrollContainer = ref(null);
const busScrollContainer = ref(null);
const flightScrollContainer = ref(null);
const tourScrollContainer = ref(null);

const scrollState = reactive({
  hotels: { canScrollLeft: false, canScrollRight: true },
  buses: { canScrollLeft: false, canScrollRight: true },
  flights: { canScrollLeft: false, canScrollRight: true },
  tours: { canScrollLeft: false, canScrollRight: true },
});

const containerRefs = {
  hotels: hotelScrollContainer,
  buses: busScrollContainer,
  flights: flightScrollContainer,
  tours: tourScrollContainer,
};

const scroll = (type, direction) => {
  const container = containerRefs[type].value;
  if (container) {
    const scrollAmount = container.clientWidth * 0.8;
    container.scrollTo({
      left:
        direction === "left"
          ? container.scrollLeft - scrollAmount
          : container.scrollLeft + scrollAmount,
      behavior: "smooth",
    });
  }
};

const handleScroll = (type) => {
  const container = containerRefs[type].value;
  if (container) {
    scrollState[type].canScrollLeft = container.scrollLeft > 1;
    scrollState[type].canScrollRight =
      container.scrollLeft < container.scrollWidth - container.clientWidth - 1;
  }
};

// Lifecycle Hooks
onMounted(async () => {
  // Gọi các API
  fetchHotels();
  fetchTours();

  try {
    const res = await searchFlights({});
    flights.value = res.data;
  } catch (e) {
    flights.value = [];
    console.error("Lỗi khi tải chuyến bay:", e);
  }

  // Khởi tạo trạng thái scroll
  Object.keys(containerRefs).forEach((type) => {
    nextTick(() => handleScroll(type));
  });

  // Theo dõi thay đổi kích thước cửa sổ để cập nhật trạng thái scroll
  const resizeObserver = new ResizeObserver(() => {
    Object.keys(containerRefs).forEach(handleScroll);
  });

  Object.values(containerRefs).forEach((ref) => {
    if (ref.value) resizeObserver.observe(ref.value);
  });

  onUnmounted(() => {
    resizeObserver.disconnect();
  });
});
</script>

<style scoped>
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}

.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
