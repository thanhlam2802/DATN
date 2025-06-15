<template>
    <article v-if="viewMode === 'list'"
        class="w-full flex border border-gray-200 rounded-xl overflow-hidden shadow-md bg-white cursor-pointer min-h-[230px]"
        @click="$emit('click')">
        <div class="w-80 flex-shrink-0">
            <img :src="image" :alt="alt"
                class="w-full h-full object-cover transition-transform duration-300 ease-in-out hover:scale-105" />
        </div>

        <div class="flex flex-1 flex-col sm:flex-row p-4 sm:p-6 justify-between gap-4">
            <div class="flex-1">
                <p class="text-sm text-gray-500 mb-1 truncate" style="font-size: 15px;">{{ location }}</p>
                <h3 class="font-semibold text-xl mb-2 truncate" style="font-size: 23px;">{{ title }}</h3>
                <div class="flex items-center mb-2">
                    <span class="bg-blue-50 text-blue-700 px-2 py-0.5 rounded text-sm font-semibold">
                        {{ rating }}
                    </span>
                    <div class="ml-2 flex space-x-1 text-yellow-400 text-sm">
                        <i v-for="n in fullStars" :key="'full-' + n" class="fas fa-star"></i>
                        <i v-if="halfStar" class="fas fa-star-half-alt"></i>
                    </div>
                    <span class="text-gray-500 text-xs ml-2">({{ reviews }} reviews)</span>
                </div>
                <p class="text-base text-gray-600 mb-2 line-clamp-2">{{ details }}</p>
                <p class="text-sm text-gray-600 mb-1 line-clamp-1">
                    {{ amenities }}
                </p>
            </div>

            <div class="hidden sm:block border-l border-gray-200 mx-2"></div>

            <div class="flex flex-col justify-between items-start sm:items-end w-full sm:w-40 flex-shrink-0">
                <button aria-label="Add to favorites" class="text-2xl transition-colors mb-2"
                    :class="isFavorited ? 'text-red-500' : 'text-gray-400 hover:text-red-500'"
                    @click.stop="toggleFavorite">
                    <i :class="isFavorited ? 'fas fa-heart' : 'far fa-heart'"></i>
                </button>
                <div class="text-left sm:text-right w-full">
                    <span v-if="originalPrice" class="text-sm text-gray-500 line-through">{{
                        formatCurrency(originalPrice) }}</span>
                    <div class="font-bold text-xl text-blue-600 whitespace-nowrap">
                        {{ formatCurrency(price).replace('₫', 'VND') }}<span
                            class="font-normal text-sm ml-1">/đêm</span>
                    </div>
                    <button
                        class="mt-3 w-full bg-blue-600 hover:bg-blue-700 text-white rounded-lg px-4 py-2 text-base font-semibold transition">
                        Chọn phòng
                    </button>
                </div>
            </div>
        </div>
    </article>

    <article v-else
        class="border border-gray-200 rounded-xl overflow-hidden shadow-md bg-white cursor-pointer flex flex-col"
        @click="$emit('click')">
        <div class="w-full h-48 overflow-hidden relative">
            <img :src="image" :alt="alt"
                class="w-full h-full object-cover transition-transform duration-300 hover:scale-105" />
            <button aria-label="Add to favorites"
                class="absolute top-3 right-3 text-2xl text-white transition-transform transform hover:scale-110"
                @click.stop="toggleFavorite">
                <i :class="isFavorited ? 'fas fa-heart text-red-500' : 'far fa-heart'"></i>
            </button>
        </div>
        <div class="p-4 flex-1 flex flex-col justify-between">
            <div>
                <p class="text-sm text-gray-500 mb-1 truncate">{{ location }}</p>
                <h3 class="font-semibold text-lg mb-1 truncate">{{ title }}</h3>
                <p class="text-sm text-gray-700 mb-2 line-clamp-2">{{ details }}</p>
            </div>
            <div class="mt-2 flex items-center justify-between">
                <div class="flex items-center">
                    <span class="bg-blue-50 text-blue-700 px-2 py-0.5 rounded text-xs font-semibold">
                        {{ rating }}
                    </span>
                    <div class="ml-2 flex space-x-1 text-yellow-400 text-xs">
                        <i v-for="n in fullStars" :key="'gfull-' + n" class="fas fa-star"></i>
                        <i v-if="halfStar" class="fas fa-star-half-alt"></i>
                    </div>
                </div>
                <span class="text-gray-400 text-xs">({{ reviews }} reviews)</span>
            </div>
        </div>
        <div class="p-4 border-t border-gray-100 flex items-center justify-between">
            <div>
                <span class="text-base font-bold text-blue-600">{{ formatCurrency(price).replace('₫', 'VND') }}</span>
                <span class="text-sm text-gray-500">/đêm</span>
            </div>
            <button
                class="bg-blue-600 hover:bg-blue-700 text-white rounded-full px-5 py-1.5 text-sm font-semibold transition">
                Chọn
            </button>
        </div>
    </article>
</template>

<script setup>
import { ref, computed } from 'vue';

const props = defineProps({
    image: {
        type: String,
        required: true,
    },
    alt: {
        type: String,
        default: 'Hotel Image',
    },
    location: String,
    title: String,
    details: String,
    amenities: String,
    rating: [String, Number],
    reviews: [String, Number],
    originalPrice: Number,
    price: {
        type: Number,
        required: true,
    },
    viewMode: {
        type: String,
        default: 'list',
    },
});

defineEmits(['click']);

const isFavorited = ref(false);

const toggleFavorite = (event) => {
    event.stopPropagation();
    isFavorited.value = !isFavorited.value;
};

const ratingValue = computed(() => parseFloat(props.rating) || 0);

const fullStars = computed(() => Math.floor(ratingValue.value));

const halfStar = computed(() => ratingValue.value % 1 >= 0.5);

const formatCurrency = (value) => {
    if (value == null) return '';
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND',
    }).format(value);
};
</script>

<style scoped>
.line-clamp-1 {
    overflow: hidden;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 1;
}

.line-clamp-2 {
    overflow: hidden;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
}
</style>