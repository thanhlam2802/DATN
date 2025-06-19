<template>
    <router-link :to="`/hotel/${hotel.id}`"
        class="block w-[350px] h-auto flex-shrink-0 rounded-2xl bg-white overflow-hidden shadow-lg hover:shadow-xl hover:-translate-y-1.5 transition-all duration-300 group mt-3">
        <div class="relative h-48 overflow-hidden">
            <img :src="hotel.imageUrl || 'https://placehold.co/350x192/EBF5FF/7F9CF5?text=No+Image'" :alt="hotel.name"
                class="w-full h-full object-cover object-center transition-transform duration-300 group-hover:scale-110" />
        </div>

        <div class="p-4 flex flex-col justify-between" style="height: calc(100% - 12rem);">
            <div>
                <p class="text-sm text-gray-500 mb-1 flex items-center">
                    <i class="fas fa-map-marker-alt mr-2 text-gray-400"></i>
                    {{ hotel.provinceName || 'Việt Nam' }}
                </p>

                <h3 class="font-bold text-xl text-gray-800 leading-tight h-14 overflow-hidden" :title="hotel.name">
                    {{ hotel.name }}
                </h3>

                <div class="flex items-center space-x-1 text-yellow-400 text-sm mt-2">
                    <i v-for="n in hotel.starRating" :key="'full-' + n" class="fas fa-star"></i>
                    <i v-for="n in (5 - (hotel.starRating || 0))" :key="'empty-' + n"
                        class="far fa-star text-gray-300"></i>
                    <span class="text-gray-600 ml-2 text-xs">
                        <span class="font-bold text-blue-700">{{ hotel.rating.toFixed(1) }}</span>/5 ({{
                            hotel.reviewCount }} đánh giá)
                    </span>
                </div>
            </div>

            <div class="mt-3 pt-3 border-t border-gray-100">
                <p class="text-right">
                    <span class="text-gray-500 text-sm">Giá từ</span><br>
                    <span class="text-red-500 font-bold text-xl">{{ formattedPrice }}</span>
                </p>
            </div>
        </div>
    </router-link>
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
        formattedPrice() {
            if (this.hotel.startingPrice === null || typeof this.hotel.startingPrice === 'undefined') {
                return "Liên hệ";
            }
            const priceNumber = Number(this.hotel.startingPrice);

            return new Intl.NumberFormat('vi-VN', {
                style: 'currency',
                currency: 'VND',
                currencyDisplay: 'code'
            }).format(priceNumber).replace('VND', ' VND');
        },
    },
};
</script>

<style scoped>
.shadow-lg {
    box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.07), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

.shadow-xl {
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

h3 {
    display: -webkit-box;
    -webkit-line-clamp: 2;
    line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
}
</style>
