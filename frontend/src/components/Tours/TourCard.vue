<script setup>
import { defineProps, defineEmits } from "vue";
import { MapPinIcon, StarIcon, BookmarkIcon } from "lucide-vue-next";

const props = defineProps({
  imageUrl: String,
  title: String,
  location: String,
  locationDetail: String,
  price: String,
  originalPrice: String,
  rating: Object,
});

const emit = defineEmits(["click"]);

const handleClick = () => {
  emit("click");
};
</script>

<template>
  <div
    class="rounded-lg border border-gray-200 overflow-hidden bg-white cursor-pointer hover:shadow-md transition"
    @click="handleClick"
  >
    <div class="relative">
      <img
        :src="props.imageUrl"
        :alt="props.title"
        class="w-full h-48 object-cover"
      />
      <button
        class="absolute top-3 right-3 bg-white/80 p-1 rounded-full"
        @click.stop
      >
        <BookmarkIcon size="20" class="text-gray-600" />
      </button>
    </div>
    <div class="p-4">
      <h3 class="font-medium text-lg mb-2 line-clamp-2">{{ props.title }}</h3>
      <div class="flex items-center gap-1 text-gray-600 mb-2">
        <MapPinIcon size="16" />
        <span class="text-sm"
          >{{ props.location }}, {{ props.locationDetail }}</span
        >
      </div>
      <div v-if="props.rating" class="flex items-center gap-1 mb-2">
        <StarIcon size="16" class="text-blue-500 fill-blue-500" />
        <span class="text-blue-500 font-medium">{{ props.rating.score }}</span>
        <span class="text-gray-500 text-sm">
          ({{ props.rating.reviews }} đánh giá)
        </span>
      </div>
      <div class="mt-2">
        <div class="text-gray-500 text-sm">Giá chỉ từ</div>
        <div class="flex items-center gap-2">
          <span class="text-orange-500 font-medium">{{ props.price }} VND</span>
          <span
            v-if="props.originalPrice"
            class="text-gray-500 text-sm line-through"
          >
            {{ props.originalPrice }} VND
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Style bổ sung nếu cần */
</style>
