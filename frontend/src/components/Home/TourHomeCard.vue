<template>
  <router-link
    :to="{ name: 'TourDetail', params: { id: tour.id } }"
    class="block w-[350px] h-[380px] flex-shrink-0 rounded-2xl bg-white overflow-hidden shadow-lg hover:shadow-2xl transition-all duration-300 transform hover:-translate-y-1"
  >
    <div class="relative h-48 overflow-hidden">
      <img
        :src="tour.imageUrl"
        :alt="tour.name"
        class="w-full h-full object-cover object-center group-hover:scale-105 transition-transform duration-300"
      />
    </div>
    <div class="p-4 flex flex-col justify-between h-[188px]">
      <div>
        <h3
          class="font-bold text-xl text-gray-800 truncate mb-2"
          :title="tour.name"
        >
          {{ tour.name }}
        </h3>

        <div class="flex items-center text-gray-600 text-sm mb-3">
          <i class="fas fa-map-marker-alt mr-2 text-gray-500"></i>
          <span class="truncate">Điểm đến: {{ tour.location }}</span>
        </div>

        <div class="flex items-center space-x-2 mb-3">
          <div class="flex items-center space-x-1 text-yellow-400 text-sm">
            <i
              v-for="n in starCounts.full"
              :key="'full-' + n"
              class="fas fa-star"
            ></i>
            <i v-if="starCounts.half === 1" class="fas fa-star-half-alt"></i>
            <i
              v-for="n in starCounts.empty"
              :key="'empty-' + n"
              class="far fa-star text-gray-300"
            ></i>
          </div>
          <span class="text-gray-600 text-sm"
            >({{ tour.reviewCount }} đánh giá)</span
          >
        </div>

        <div
          v-if="tour.duration"
          class="flex items-center text-gray-600 text-sm"
        >
          <i class="fas fa-clock mr-2 text-gray-500"></i>
          <span>{{ tour.duration }}</span>
        </div>
      </div>

      <div class="flex justify-end items-center pt-2">
        <span class="text-blue-600 font-bold text-xl whitespace-nowrap">{{
          formattedPrice
        }}</span>
      </div>
    </div>
  </router-link>
</template>

<script>
export default {
  name: "TourCard",
  props: {
    tour: {
      type: Object,
      required: true,
    },
  },
  computed: {
    starCounts() {
      const rating = parseFloat(this.tour.rating);
      if (isNaN(rating)) return { full: 0, half: 0, empty: 5 };

      const full = Math.floor(rating);
      const half = rating - full >= 0.5 ? 1 : 0;
      const empty = 5 - full - half;

      return { full, half, empty };
    },
    // Thêm computed để định dạng giá tiền
    formattedPrice() {
      if (typeof this.tour.price !== "number") {
        return this.tour.price;
      }
      // Định dạng theo kiểu tiền tệ Việt Nam
      return new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      }).format(this.tour.price);
    },
  },
};
</script>

<style scoped>
/* Thêm style để đảm bảo chiều cao cố định và flex hoạt động tốt */
.flex-col {
  height: calc(100% - 12rem); /* 100% of parent minus image height */
}
</style>
