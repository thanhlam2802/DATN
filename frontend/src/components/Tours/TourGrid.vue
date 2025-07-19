<template>
  <div
    class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 sm:gap-6 px-2 sm:px-0"
  >
    <TourCard
      v-for="tour in tours"
      :key="tour.id"
      :image-url="tour.imageUrl"
      :title="tour.title"
      :location="tour.location"
      :location-detail="tour.locationDetail"
      :price="tour.price"
      :original-price="tour.originalPrice"
      :rating="tour.rating"
      @click="goToDetail(tour.id)"
    />
  </div>
</template>

<script setup>
import TourCard from "./TourCard.vue";
import { useRouter } from "vue-router";

// Nhận 'tours' từ component cha
defineProps({
  tours: {
    type: Array,
    required: true,
  },
});

const router = useRouter();
const goToDetail = (id) => {
  router.push(`/tours/${id}`);
};
</script>
