<template>
    <article class="flex gap-x-6 border border-gray-200 rounded-xl p-6 shadow-md bg-white cursor-pointer"
        @click="$emit('click')">
        <div class="w-110 h-60 rounded-lg overflow-hidden flex-shrink-0">
            <img :src="image" :alt="alt"
                class="w-full h-full object-cover transition-transform duration-300 ease-in-out hover:scale-105" />
        </div>

        <div class="flex flex-1 justify-between">
            <div class="pr-8 flex-1">
                <p class="text-sm text-gray-500 mb-2">{{ subtitle }}</p>
                <h3 class="font-semibold text-xl mb-2 pt-2">{{ title }}</h3>
                <p class="text-base text-gray-700 mb-2 pt-2">{{ details }}</p>
                <p class="text-base text-gray-700 mb-2 pt-2">{{ amenities }}</p>
                <div class="flex items-center mb-1 pt-2">
                    <span class="bg-blue-50 text-blue-700 px-2 py-1 rounded text-sm">
                        {{ rating }}/5
                    </span>
                    <div class="ml-2 flex space-x-1 text-yellow-400 text-sm">
                        <i v-for="n in starCounts.full" :key="'full-' + n" class="fas fa-star"></i>
                        <i v-if="starCounts.half === 1" class="fas fa-star-half-alt"></i>
                        <i v-for="n in starCounts.empty" :key="'empty-' + n" class="far fa-star text-gray-300"></i>
                    </div>
                </div>
                <span class="text-gray-400 text-sm block mb-2 pt-2">({{ reviews }} reviews)</span>
            </div>

            <div class="flex flex-col justify-between items-end w-36">
                <button aria-label="Add to favorites" class="text-2xl transition-colors"
                    :class="isFavorited ? 'text-red-500' : 'text-gray-400 hover:text-red-500'"
                    @click.stop="toggleFavorite">
                    <i :class="isFavorited ? 'fas fa-heart' : 'far fa-heart'"></i>
                </button>

                <div class="text-right mt-4">
                    <span class="text-sm text-gray-400 line-through">{{ originalPrice }}</span>
                    <div class="font-bold text-xl text-indigo-600">
                        {{ price }}
                        <span class="font-normal text-base">/night</span>
                    </div>
                </div>
            </div>
        </div>
    </article>
</template>

<script setup>
import { ref } from 'vue'

import { computed } from 'vue'

const ratingValue = computed(() => parseFloat(props.rating) || 0)

const starCounts = computed(() => {
    const full = Math.floor(ratingValue.value)
    const half = ratingValue.value - full >= 0.5 ? 1 : 0
    const empty = 5 - full - half
    return { full, half, empty }
})

const props = defineProps({
    image: String,
    alt: String,
    subtitle: String,
    title: String,
    details: String,
    amenities: String,
    rating: String,
    reviews: String,
    originalPrice: String,
    price: String,
})

const isFavorited = ref(false)
const toggleFavorite = (event) => {
    event.stopPropagation()
    isFavorited.value = !isFavorited.value
}

</script>
