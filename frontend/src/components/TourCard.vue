<script setup lang="ts">
import { defineProps } from "vue";
import { MapPinIcon, StarIcon, BookmarkIcon } from "lucide-vue-next";
interface Rating {
  score: string;
  reviews: string;
}

interface Props {
  imageUrl: string;
  title: string;
  location: string;
  locationDetail: string;
  price: string;
  originalPrice?: string;
  rating?: Rating;
}

const props = defineProps<Props>();
</script>

<style scoped>
/* Nếu muốn style thêm, em có thể bổ sung ở đây */
</style>

<template>
  <div class="rounded-lg border border-gray-200 overflow-hidden bg-white">
    <div class="relative">
      <img :src="imageUrl" :alt="title" class="w-full h-48 object-cover" />
      <button class="absolute top-3 right-3 bg-white/80 p-1 rounded-full">
        <BookmarkIcon size="20" class="text-gray-600" />
      </button>
    </div>
    <div class="p-4">
      <h3 class="font-medium text-lg mb-2 line-clamp-2">{{ title }}</h3>
      <div class="flex items-center gap-1 text-gray-600 mb-2">
        <MapPinIcon size="16" />
        <span class="text-sm">{{ location }}, {{ locationDetail }}</span>
      </div>
      <div v-if="rating" class="flex items-center gap-1 mb-2">
        <StarIcon size="16" class="text-blue-500 fill-blue-500" />
        <span class="text-blue-500 font-medium">{{ rating.score }}</span>
        <span class="text-gray-500 text-sm"
          >({{ rating.reviews }} đánh giá)</span
        >
      </div>

      <div class="mt-2">
        <div class="text-gray-500 text-sm">Starting from</div>
        <div class="flex items-center gap-2">
          <span class="text-orange-500 font-medium">{{ price }} VND</span>
          <span v-if="originalPrice" class="text-gray-500 text-sm line-through">
            {{ originalPrice }} VND
          </span>
        </div>
      </div>
    </div>
  </div>
</template>
