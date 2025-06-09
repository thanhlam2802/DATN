<template>
    <div class="w-[420px] flex-shrink-0 rounded-2xl bg-white overflow-hidden shadow-2xl transition-all duration-300 scale-[0.95]"
        style="min-width: 450px; max-width: 450px;">
        <div class="relative h-48 overflow-hidden">
            <img :src="hotel.image" :alt="hotel.name"
                class="w-full h-full object-cover object-center transition-transform duration-300" />
        </div>
        <div class="p-4 space-y-2">
            <h3 class="font-bold text-xl text-gray-800 truncate">{{ hotel.name }}</h3>
            <div class="flex justify-between items-center">
                <span class="text-gray-500 text-base whitespace-nowrap">{{ hotel.price }}</span>
                <span class="text-blue-600 font-bold text-base whitespace-nowrap">{{ hotel.priceDetail }}</span>
            </div>
            <div class="flex items-center space-x-2">
                <span class="bg-blue-50 text-blue-700 px-2 py-1 rounded text-sm">{{ hotel.rating }}/5</span>
                <div class="flex space-x-1 text-yellow-400 text-sm">
                    <i v-for="n in starCounts.full" :key="'full-' + n" class="fas fa-star"></i>
                    <i v-if="starCounts.half === 1" class="fas fa-star-half-alt"></i>
                    <i v-for="n in starCounts.empty" :key="'empty-' + n" class="far fa-star text-gray-300"></i>
                </div>
            </div>
            <div class="flex items-center text-gray-600 text-sm">
                <i class="fas fa-calendar-alt mr-2 text-gray-500"></i>
                <span>{{ hotel.date }}</span>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "HotelCard",
    props: {
        hotel: {
            type: Object,
            required: true,
        },
    },
    computed: {
        starCounts() {
            const rating = parseFloat(this.hotel.rating);
            if (isNaN(rating)) return { full: 0, half: 0, empty: 5 };

            const full = Math.floor(rating);
            const half = rating - full >= 0.5 ? 1 : 0;
            const empty = 5 - full - half;

            return { full, half, empty };
        },
    },
};
</script>
<style scoped></style>