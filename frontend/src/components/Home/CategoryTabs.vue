<template>
  <div class="container mx-auto px-4 py-8">
    <div class="flex border-b mb-4 pb-2 overflow-x-auto whitespace-nowrap">
      <button v-for="(tab, index) in tabs" :key="index"
        class="!rounded-button py-2 px-4 font-medium cursor-pointer flex-shrink-0" :class="activeTab === tab.id
          ? 'text-blue-600 border-b-2 border-blue-600'
          : 'text-gray-600 hover:text-gray-800'" @click="activeTab = tab.id">
        {{ tab.name }}
      </button>
    </div>

    <div v-if="activeTab === 'hotels'">
      <HorizontalScrollWrapper :key="activeTab" :itemCount="hotels.length">
        <HotelCard v-for="hotel in hotels" :key="hotel.id" :hotel="hotel" class="min-w-[300px] flex-shrink-0 mb-4" />
      </HorizontalScrollWrapper>
    </div>

    <div v-else-if="activeTab === 'bus'">
      <HorizontalScrollWrapper :key="activeTab" :itemCount="buses.length">
        <BusCard v-for="bus in buses" :key="bus.id" :bus="bus" class="min-w-[300px] flex-shrink-0 mb-4" />
      </HorizontalScrollWrapper>
    </div>

    <div v-else-if="activeTab === 'flights'">
      <HorizontalScrollWrapper :key="activeTab" :itemCount="flights.length">
        <FlightCard v-for="flight in flights" :key="flight.id" :flight="flight"
          class="min-w-[300px] flex-shrink-0 mb-4" />
      </HorizontalScrollWrapper>
    </div>

    <div v-else-if="activeTab === 'tours'">
      <HorizontalScrollWrapper :key="activeTab" :itemCount="tours.length">
        <TourHomeCard v-for="tour in tours" :key="tour.id" :tour="tour" class="min-w-[300px] flex-shrink-0 mb-4" />
      </HorizontalScrollWrapper>
    </div>
  </div>
</template>

<script>
import HotelCard from "./HotelCard.vue";
import BusCard from "./BusCard.vue";
import FlightCard from "./FlightCard.vue";
import TourHomeCard from "./TourHomeCard.vue";
import HorizontalScrollWrapper from "./HorizontalScrollWrapper.vue";

import { tabs, hotels, buses, flights, tours } from "../../data/appData";

export default {
  name: "CategoryTabs",
  components: {
    HotelCard,
    BusCard,
    FlightCard,
    TourHomeCard,
    HorizontalScrollWrapper,
  },
  data() {
    return {
      activeTab: "hotels",
      tabs,
      hotels,
      buses,
      flights,
      tours,
    };
  },
};
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