<template>
  <div class="w-full min-h-screen text-lg md:text-xl relative bg-gray-50">
    <div class="relative">
      <BannerAndSearch />
    </div>
    <SignatureSection />
    <CouponSection />
    <div class="container mx-auto px-4 py-8 space-y-16">
      <section>
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-2xl font-bold text-gray-800">
            Gợi ý Khách sạn cho bạn
          </h2>
        </div>
        <div class="relative group">
          <div
            ref="hotelScrollContainer"
            @scroll="() => handleScroll('hotels')"
            class="flex overflow-x-auto space-x-6 pb-4 scrollbar-hide snap-x snap-mandatory"
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
            class="absolute top-1/2 left-0 transform -translate-y-1/2 -translate-x-1/2 bg-white rounded-full w-10 h-10 flex items-center justify-center shadow-lg text-gray-700 hover:bg-gray-100 transition opacity-0 group-hover:opacity-100 z-10"
          >
            <i class="fas fa-chevron-left"></i>
          </button>
          <button
            v-if="scrollState.hotels.canScrollRight"
            @click="scroll('hotels', 'right')"
            class="absolute top-1/2 right-0 transform -translate-y-1/2 translate-x-1/2 bg-white rounded-full w-10 h-10 flex items-center justify-center shadow-lg text-gray-700 hover:bg-gray-100 transition opacity-0 group-hover:opacity-100 z-10"
          >
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
        <div class="mt-8 text-center">
          <router-link
            :to="{ name: 'Hotel' }"
            class="inline-block bg-blue-50 text-blue-700 font-semibold rounded-lg px-6 py-3 hover:bg-blue-100 transition-colors duration-200"
          >
            Xem thêm Khách sạn
          </router-link>
        </div>
      </section>

      <section>
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-2xl font-bold text-gray-800">Tuyến xe phổ biến</h2>
        </div>
        <div class="relative group">
          <div
            ref="busScrollContainer"
            @scroll="() => handleScroll('buses')"
            class="flex overflow-x-auto space-x-6 pb-4 scrollbar-hide snap-x snap-mandatory"
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
            class="absolute top-1/2 left-0 transform -translate-y-1/2 -translate-x-1/2 bg-white rounded-full w-10 h-10 flex items-center justify-center shadow-lg text-gray-700 hover:bg-gray-100 transition opacity-0 group-hover:opacity-100 z-10"
          >
            <i class="fas fa-chevron-left"></i>
          </button>
          <button
            v-if="scrollState.buses.canScrollRight"
            @click="scroll('buses', 'right')"
            class="absolute top-1/2 right-0 transform -translate-y-1/2 translate-x-1/2 bg-white rounded-full w-10 h-10 flex items-center justify-center shadow-lg text-gray-700 hover:bg-gray-100 transition opacity-0 group-hover:opacity-100 z-10"
          >
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
        <div class="mt-8 text-center">
          <router-link
            :to="{ name: 'Bus' }"
            class="inline-block bg-blue-50 text-blue-700 font-semibold rounded-lg px-6 py-3 hover:bg-blue-100 transition-colors duration-200"
          >
            Xem thêm Tuyến xe
          </router-link>
        </div>
      </section>

      <section>
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-2xl font-bold text-gray-800">Chuyến bay giá tốt</h2>
        </div>
        <div class="relative group">
          <div
            ref="flightScrollContainer"
            @scroll="() => handleScroll('flights')"
            class="flex overflow-x-auto space-x-6 pb-4 scrollbar-hide snap-x snap-mandatory"
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
            class="absolute top-1/2 left-0 transform -translate-y-1/2 -translate-x-1/2 bg-white rounded-full w-10 h-10 flex items-center justify-center shadow-lg text-gray-700 hover:bg-gray-100 transition opacity-0 group-hover:opacity-100 z-10"
          >
            <i class="fas fa-chevron-left"></i>
          </button>
          <button
            v-if="scrollState.flights.canScrollRight"
            @click="scroll('flights', 'right')"
            class="absolute top-1/2 right-0 transform -translate-y-1/2 translate-x-1/2 bg-white rounded-full w-10 h-10 flex items-center justify-center shadow-lg text-gray-700 hover:bg-gray-100 transition opacity-0 group-hover:opacity-100 z-10"
          >
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
        <div class="mt-8 text-center">
          <router-link
            :to="{ name: 'Plane' }"
            class="inline-block bg-blue-50 text-blue-700 font-semibold rounded-lg px-6 py-3 hover:bg-blue-100 transition-colors duration-200"
          >
            Xem thêm Chuyến bay
          </router-link>
        </div>
      </section>

      <section>
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-2xl font-bold text-gray-800">Tour du lịch hấp dẫn</h2>
        </div>
        <div class="relative group">
          <div
            ref="tourScrollContainer"
            @scroll="() => handleScroll('tours')"
            class="flex overflow-x-auto space-x-6 pb-4 scrollbar-hide snap-x snap-mandatory"
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
            class="absolute top-1/2 left-0 transform -translate-y-1/2 -translate-x-1/2 bg-white rounded-full w-10 h-10 flex items-center justify-center shadow-lg text-gray-700 hover:bg-gray-100 transition opacity-0 group-hover:opacity-100 z-10"
          >
            <i class="fas fa-chevron-left"></i>
          </button>
          <button
            v-if="scrollState.tours.canScrollRight"
            @click="scroll('tours', 'right')"
            class="absolute top-1/2 right-0 transform -translate-y-1/2 translate-x-1/2 bg-white rounded-full w-10 h-10 flex items-center justify-center shadow-lg text-gray-700 hover:bg-gray-100 transition opacity-0 group-hover:opacity-100 z-10"
          >
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
        <div class="mt-8 text-center">
          <router-link
            :to="{ name: 'Tour' }"
            class="inline-block bg-blue-50 text-blue-700 font-semibold rounded-lg px-6 py-3 hover:bg-blue-100 transition-colors duration-200"
          >
            Xem thêm Tour du lịch
          </router-link>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from "vue";
import BannerAndSearch from "../components/Home/BannerAndSearch.vue";
import SignatureSection from "../components/Home/SignatureSection.vue";
import CouponSection from "../components/Home/CouponSection.vue";
import HotelCard from "../components/Home/HotelCard.vue";
import BusCard from "../components/Home/BusCard.vue";
import FlightCard from "../components/Home/FlightCard.vue";
import TourHomeCard from "../components/Home/TourHomeCard.vue";
import { hotels, buses, tours } from "../data/appData";
import { searchFlights } from '@/api/flightApi'

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

const flights = ref([])

const scroll = (type, direction) => {
  const container = containerRefs[type].value;
  if (container) {
    const scrollAmount = container.clientWidth * 0.8;
    const newScrollLeft =
      direction === "left"
        ? container.scrollLeft - scrollAmount
        : container.scrollLeft + scrollAmount;

    container.scrollTo({
      left: newScrollLeft,
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

onMounted(async () => {
  try {
    const res = await searchFlights({})
    flights.value = res.data
    console.log("=======================================")
    console.log(flights.value)
  } catch (e) {
    flights.value = []
  }

  Object.keys(containerRefs).forEach((type) => {
    import("vue").then(({ nextTick }) => {
      nextTick(() => handleScroll(type));
    });
  });

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
