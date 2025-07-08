<template>
    <article v-if="viewMode === 'list'"
        class="lg:w-[996px] flex border border-gray-200 rounded-xl overflow-hidden shadow-md bg-white cursor-pointer lg:h-[226px]"
        @click="$emit('click')">
        <div class="w-80 flex-shrink-0 relative">
            <div class="w-full h-full flex flex-col">
                <div class="flex-1 relative overflow-hidden lg:w-[300px] lg:h-[160px]">
                    <img :src="mainImage" :alt="alt"
                        class="w-full h-full object-cover transition-transform duration-300 ease-in-out hover:scale-105" />
                    <p
                        class="absolute top-3 left-3 bg-black bg-opacity-50 text-white text-xs font-semibold px-3 py-1 rounded-full flex items-center z-10">
                        <i class="fas fa-map-marker-alt mr-2"></i>
                        <span>{{ location }}</span>
                    </p>
                </div>

                <div v-if="hasMultipleImages" class="flex gap-1 lg:w-[300px] lg:h-[64px] pt-1">
                    <div v-for="(img, index) in displayImages.slice(0, 3)" :key="index"
                        class="flex-1 relative overflow-hidden lg:w-[100px] lg:h-[64px]">
                        <img :src="img" :alt="`${alt} ${index + 1}`"
                            class="w-full h-full object-cover transition-transform duration-300 ease-in-out hover:scale-105" />
                        <div v-if="index === 2 && additionalImagesCount > 0"
                            class="absolute inset-0 bg-black bg-opacity-60 flex items-center justify-center text-white text-xs font-semibold">
                            +{{ additionalImagesCount }}
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="flex flex-1 flex-col sm:flex-row sm:p-6 sm:pl-1 sm:pt-5 justify-between gap-4">
            <div class="flex-1">
                <h3 class="font-semibold text-xl truncate">{{ title }}</h3>
                <div class="flex items-center text-gray-600 text-sm my-1.5 pt-2">
                    <i class="fas fa-hotel mr-2 text-blue-500"></i>
                    <span class="mr-2">Khách sạn</span>
                    <div class="flex items-center text-yellow-400">
                        <i v-for="n in stars" :key="'star-' + n" class="fas fa-star text-sm"></i>
                    </div>
                </div>
                <div class="flex items-center mb-2 pt-2">
                    <i class="far fa-thumbs-up mr-1.5 text-blue-500"></i>
                    <span class="bg-blue-50 text-blue-700 px-2 py-0.5 rounded text-sm font-semibold">
                        {{ rating }}
                    </span>
                    <span class="text-gray-500 text-xs ml-2">({{ reviews }} đánh giá)</span>
                </div>
                <p v-if="details" class="text-base text-gray-600 mb-2 line-clamp-2">{{ details }}</p>
                <div v-if="amenities && amenities.length > 0" class="flex flex-wrap items-center gap-2 mt-2 mb-2 pt-2">
                    <div v-for="amenity in visibleAmenities" :key="amenity.id"
                        class="flex items-center px-2.5 py-1 bg-gray-100 rounded-full text-xs text-gray-700 font-medium">
                        <i :class="amenity.icon || 'fas fa-check'" class="mr-1.5 text-gray-500"></i>
                        <span>{{ amenity.name }}</span>
                    </div>
                    <div v-if="hiddenAmenitiesCount > 0"
                        class="flex items-center px-2.5 py-1 bg-gray-100 rounded-full text-xs text-gray-700 font-medium">
                        <span>+{{ hiddenAmenitiesCount }}</span>
                    </div>
                </div>
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
            <img :src="mainImage" :alt="alt"
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
import { computed } from 'vue';

const props = defineProps({
    image: {
        type: String,
        required: true,
    },
    images: {
        type: Array,
        default: () => [],
    },
    alt: {
        type: String,
        default: 'Hotel Image',
    },
    location: String,
    title: String,
    details: String,
    amenities: {
        type: Array,
        default: () => [],
    },
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

const mainImage = computed(() => {
    return props.image || 'https://via.placeholder.com/320x230.png?text=Hotel+Image';
});

const displayImages = computed(() => {
    if (props.images && props.images.length > 0) {
        return props.images;
    }
    return [props.image];
});

const hasMultipleImages = computed(() => {
    return props.images && props.images.length > 1;
});

const additionalImagesCount = computed(() => {
    if (props.images && props.images.length > 3) {
        return props.images.length - 3;
    }
    return 0;
});

const visibleAmenities = computed(() => {
    if (!props.amenities) return [];
    return props.amenities.slice(0, 3);
});

const hiddenAmenitiesCount = computed(() => {
    if (!props.amenities || props.amenities.length <= 3) return 0;
    return props.amenities.length - 3;
});

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