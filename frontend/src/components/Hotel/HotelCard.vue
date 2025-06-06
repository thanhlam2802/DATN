<template>
    <article class="flex gap-x-6 border border-gray-200 rounded-xl p-6 shadow-md bg-white cursor-pointer"
        @click="$emit('click')">
        <div class="w-110 h-60 rounded-lg overflow-hidden flex-shrink-0">
            <img :src="image" :alt="alt"
                class="w-full h-full object-cover transition-transform duration-300 ease-in-out hover:scale-105" />
        </div>

        <div class="flex flex-1 justify-between">
            <div class="pr-8 flex-1">
                <p class="text-sm text-gray-500 mb-2">{{ location }}</p>
                <h3 class="font-semibold text-xl mb-2 pt-2">{{ title }}</h3>
                <p class="text-base text-gray-700 mb-2 pt-2">{{ details }}</p>
                <p class="text-base text-gray-700 mb-2 pt-2">{{ amenities }}</p>
                <div class="flex items-center mb-1 pt-2">
                    <span class="bg-blue-50 text-blue-700 px-2 py-1 rounded text-sm">
                        {{ rating }}
                    </span>
                    <div class="ml-2 flex space-x-1 text-yellow-400 text-sm">
                        <i v-for="n in Math.floor(ratingValue)" :key="'full-' + n" class="fas fa-star"></i>
                        <i v-if="ratingValue % 1 >= 0.5" class="fas fa-star-half-alt"></i>
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
                    <span class="text-sm text-gray-400 line-through">{{ formatCurrency(originalPrice) }}</span>
                    <div class="font-bold text-lg text-indigo-600 whitespace-nowrap">
                        {{ formatCurrency(price) }}<span class="font-normal text-sm ml-1">/đêm</span>
                    </div>
                    <button
                        class="mt-4 bg-indigo-600 hover:bg-indigo-700 text-white rounded-full px-6 py-2 text-sm transition focus:outline-none focus:ring-2 focus:ring-indigo-500">
                        Chọn phòng
                    </button>
                </div>
            </div>
        </div>
    </article>
</template>

<script setup>
import { ref, computed } from 'vue'

const ratingValue = computed(() => parseFloat(props.rating) || 0)

const props = defineProps({
    image: String,
    alt: String,
    location: String,
    title: String,
    details: String,
    amenities: String,
    rating: String,
    reviews: String,
    originalPrice: Number,
    price: Number,
})

const isFavorited = ref(false)
const toggleFavorite = (event) => {
    event.stopPropagation()
    isFavorited.value = !isFavorited.value
}

const formatCurrency = (value) => {
    return value
        ? new Intl.NumberFormat('vi-VN').format(value) + ' VND'
        : '';
}
</script>
