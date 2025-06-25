<template>
    <article v-if="viewMode === 'list'"
        class="w-full flex border border-gray-200 rounded-xl overflow-hidden shadow-md bg-white cursor-pointer min-h-[230px]"
        @click="$emit('click')">
        <div class="w-80 flex-shrink-0 relative">
            <img :src="image" :alt="alt"
                class="w-full h-full object-cover transition-transform duration-300 ease-in-out hover:scale-105" />
            <p
                class="absolute top-3 left-3 bg-black bg-opacity-50 text-white text-xs font-semibold px-3 py-1 rounded-full flex items-center z-10">
                <i class="fas fa-map-marker-alt mr-2"></i>
                <span>{{ location }}</span>
            </p>
        </div>

        <div class="flex flex-1 flex-col sm:flex-row p-4 sm:p-6 justify-between gap-4">
            <div class="flex-1">
                <h3 class="font-semibold text-xl truncate">{{ title }}</h3>
                <div class="flex items-center text-gray-600 text-sm my-1.5">
                    <i class="fas fa-hotel mr-2 text-blue-500"></i>
                    <span class="mr-2">Khách sạn</span>
                    <div class="flex items-center text-yellow-400">
                        <i v-for="n in stars" :key="'star-' + n" class="fas fa-star text-sm"></i>
                    </div>
                </div>
                <div class="flex items-center mb-2">
                    <i class="far fa-thumbs-up mr-1.5 text-blue-500"></i>
                    <span class="bg-blue-50 text-blue-700 px-2 py-0.5 rounded text-sm font-semibold">
                        {{ rating }}
                    </span>
                    <span class="text-gray-500 text-xs ml-2">({{ reviews }} đánh giá)</span>
                </div>
                <p v-if="details" class="text-base text-gray-600 mb-2 line-clamp-2">{{ details }}</p>
                <p v-if="amenities" class="text-sm text-gray-600 mb-1 line-clamp-1">
                    {{ amenities }}
                </p>
                <p v-if="fullAddress"
                    class="text-sm text-gray-600 flex items-start mt-12 pt-2 border-t border-gray-100">
                    <i class="fas fa-map-marker-alt mr-2 text-gray-400 mt-1 flex-shrink-0"></i>
                    <span>{{ fullAddress }}</span>
                </p>
            </div>

            <div class="hidden sm:block border-l border-gray-200 mx-2"></div>

            <div class="flex flex-col justify-between items-start sm:items-end w-full sm:w-40 flex-shrink-0">
                <button aria-label="Add to favorites" class="text-2xl transition-colors mb-2"
                    :class="props.isFavorited ? 'text-red-500' : 'text-gray-400 hover:text-red-500'"
                    @click.stop="toggleFavorite">
                    <i :class="props.isFavorited ? 'fas fa-heart' : 'far fa-heart'"></i>
                </button>
                <div class="text-left sm:text-right w-full">
                    <span v-if="originalPrice" class="text-sm text-gray-500 line-through">{{
                        formatCurrency(originalPrice) }}</span>
                    <p class="font-bold text-xl text-red-500 whitespace-nowrap">{{ formatCurrency(price).replace('₫',
                        'VND') }}</p>
                    <p class="text-xs text-gray-400 font-normal mt-1">Chưa bao gồm thuế và phí</p>
                    <button
                        class="mt-3 w-full bg-orange-600 hover:bg-orange-700 text-white rounded-lg px-4 py-2 text-base font-semibold transition">
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
            <p
                class="absolute top-3 left-3 bg-black bg-opacity-50 text-white text-xs font-semibold px-3 py-1 rounded-full flex items-center z-10">
                <i class="fas fa-map-marker-alt mr-2"></i>
                <span>{{ location }}</span>
            </p>
            <button aria-label="Add to favorites"
                class="absolute top-3 right-3 text-2xl text-white transition-transform transform hover:scale-110"
                @click.stop="toggleFavorite">
                <i :class="props.isFavorited ? 'fas fa-heart text-red-500' : 'far fa-heart'"></i>
            </button>
        </div>
        <div class="p-4 flex-1 flex flex-col justify-between">
            <div>
                <h3 class="font-semibold text-lg truncate">{{ title }}</h3>
                <div class="flex items-center text-gray-600 text-xs mt-1 mb-2">
                    <i class="fas fa-hotel mr-1.5 text-blue-500"></i>
                    <span class="mr-1.5">Khách sạn</span>
                    <div class="flex items-center text-yellow-400">
                        <i v-for="n in stars" :key="'grid-star-' + n" class="fas fa-star text-xs"></i>
                    </div>
                </div>
                <p v-if="details" class="text-sm text-gray-700 mb-2 line-clamp-2">{{ details }}</p>
            </div>
            <div class="mt-2 flex items-center justify-between">
                <div class="flex items-center">
                    <i class="far fa-thumbs-up mr-1.5 text-blue-500"></i>
                    <span class="bg-blue-50 text-blue-700 px-2 py-0.5 rounded text-xs font-semibold">
                        {{ rating }}
                    </span>
                    <span class="text-gray-500 text-xs ml-2">({{ reviews }} đánh giá)</span>
                </div>
            </div>
        </div>
        <div class="p-4 border-t border-gray-100 flex items-center justify-between">
            <div>
                <p class="text-red-500 font-bold text-xl">{{ formatCurrency(price).replace('₫', 'VND') }}</p>
                <p class="text-xs text-gray-400 font-normal">Chưa bao gồm thuế và phí</p>
            </div>
            <button
                class="bg-orange-600 hover:bg-orange-700 text-white rounded-full px-5 py-1.5 text-sm font-semibold transition">
                Chọn
            </button>
        </div>
    </article>
</template>

<script setup>

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
    fullAddress: String,
    stars: {
        type: Number,
        default: 0
    },
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
    isFavorited: {
        type: Boolean,
        default: false,
    },
});

const emit = defineEmits(['click', 'toggle-favorite']);

const toggleFavorite = (event) => {
    event.stopPropagation();
    emit('toggle-favorite');
};

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
    line-clamp: 1;
}

.line-clamp-2 {
    overflow: hidden;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    line-clamp: 2;
}
</style>