<template>
    <div class="w-[420px] flex-shrink-0 rounded-2xl bg-white overflow-hidden shadow-2xl transition-all duration-300 scale-[0.95]"
        style="min-width: 450px; max-width: 450px;">
        <div class="relative h-48 overflow-hidden">
            <img :src="tour.image" :alt="tour.name"
                class="w-full h-full object-cover object-center transition-transform duration-300" />
        </div>
        <div class="p-4 space-y-2">
            <h3 class="font-bold text-xl text-gray-800 truncate">{{ tour.name }}</h3>
            <div class="flex justify-between items-center">
                <span class="text-gray-600 text-sm truncate">Điểm đến: {{ tour.destination }}</span>
                <span class="text-blue-600 font-bold text-base whitespace-nowrap">{{ tour.price }}</span>
            </div>
            <div class="flex items-center space-x-2">
                <span class="bg-blue-50 text-blue-700 px-2 py-1 rounded text-sm">{{ tour.rating }}/5</span>
                <div class="flex space-x-1 text-yellow-400 text-sm">
                    <i v-for="n in starCounts.full" :key="'full-' + n" class="fas fa-star"></i>
                    <i v-if="starCounts.half === 1" class="fas fa-star-half-alt"></i>
                    <i v-for="n in starCounts.empty" :key="'empty-' + n" class="far fa-star text-gray-300"></i>
                </div>
            </div>
            <div class="flex items-center text-gray-600 text-sm">
                <i class="fas fa-clock mr-2 text-gray-500"></i>
                <span>{{ tour.duration }}</span>
            </div>
        </div>
    </div>
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
    },
};
</script>
<style scoped></style>