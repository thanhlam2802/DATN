<template>
  <section class="relative w-full h-[500px] mt-4 overflow-hidden rounded-xl">
    <img
      :src="heroImage"
      alt="Hình nền du lịch"
      class="w-full h-full object-cover object-center brightness-50"
    />

    <div
      class="absolute top-4 left-1/2 transform -translate-x-1/2 z-10 w-full px-4 max-w-7xl"
    >
      <div class="rounded-xl p-2">
        <div class="flex justify-center">
          <div class="w-max max-w-full">
            <nav class="mb-2">
              <ul
                class="flex items-center justify-center space-x-2 md:space-x-4 px-2 py-1 text-sm font-semibold overflow-x-auto scrollbar-hide"
              >
                <li v-for="tab in tabs" :key="tab.id">
                  <button
                    @click="activeTab = tab.id"
                    :class="
                      activeTab === tab.id
                        ? 'bg-white text-blue-600 shadow-sm': 'text-white/80 hover:bg-white/20 hover:text-white'"class="flex items-center space-x-2 px-4 py-2 rounded-full transition-all duration-300 whitespace-nowrap">
                    <i :class="tab.icon"></i>
                    <span>{{ tab.name }}</span>
                  </button>
                </li>
              </ul>
            </nav>
            <div class="h-px bg-white/60 my-2"></div>
          </div>
        </div>

        <div class="rounded-lg">
          <HotelSearch v-if="activeTab === 'hotel'" />
          <FlightSearch
            v-if="activeTab === 'flight'"
            v-model:tripType="tripType"
          />
          <BusSearch
            v-if="activeTab === 'bus'"
            v-model:busTripType="busTripType"
          />
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from "vue";
import HotelSearch from "./HotelSearch.vue";
import FlightSearch from "./FlightSearch.vue";
import BusSearch from "./BusSearch.vue";

const heroImage =
  "https://images.unsplash.com/photo-1534351590666-13e3e96b5017?q=80&w=2940&auto=format&fit=crop";
const activeTab = ref("hotel");

const tabs = [
  { id: "hotel", name: "Khách sạn", icon: "fas fa-hotel" },
  { id: "bus", name: "Vé xe khách", icon: "fas fa-bus" },
  { id: "tour", name: "Tour du lịch", icon: "fas fa-globe" },
];

const tripType = ref("round-trip");
const busTripType = ref("round-trip");
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
