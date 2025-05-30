<template>
    <div class="w-[420px] flex-shrink-0 rounded-2xl bg-white overflow-hidden shadow-2xl transition-all duration-300 scale-[0.95]" style="min-width: 450px; max-width: 450px; flex-shrink: 0;">
        <div class="relative h-48 overflow-hidden">
            <img :src="tour.image" :alt="tour.name" class="w-full h-full object-cover object-center transition-transform duration-300" />
        </div>
        <div class="p-4">
            <h3 class="font-bold text-lg mb-2">{{ tour.name }}</h3>
            <div class="flex justify-between items-center mb-2">
                <div class="text-gray-700">Điểm đến: **{{ tour.destination }}**</div>
                <div class="text-gray-700 font-semibold">{{ tour.price }}</div>
            </div>
            <div class="mb-2">
                <span class="bg-blue-50 text-blue-700 px-2 py-1 rounded text-sm">{{ tour.rating }}</span>
                <div class="inline-block ml-2">
                    <i v-for="star in 5" :key="star" class="fas fa-star text-yellow-400 text-sm"></i>
                </div>
            </div>
            <div class="text-gray-600 text-sm">Thời lượng: {{ tour.duration }}</div>
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
};
</script>

<style scoped>
</style>