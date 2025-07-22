<template>
    <router-link :to="`/hotel/${hotel.id}`"
        class="block w-[350px] h-auto flex-shrink-0 rounded-2xl bg-white overflow-hidden shadow-lg hover:shadow-xl hover:-translate-y-1.5 transition-all duration-300 group mt-3">

        <div class="relative h-48 overflow-hidden">
            <img :src="hotel.imageUrl || 'https://placehold.co/350x192/EBF5FF/7F9CF5?text=No+Image'" :alt="hotel.name"
                class="w-full h-full object-cover object-center transition-transform duration-300 hover:scale-110" />
            <p
                class="absolute top-3 left-3 bg-black bg-opacity-50 text-white text-xs font-semibold px-3 py-1 rounded-full flex items-center z-10">
                <i class="fas fa-map-marker-alt mr-2"></i>
                <span>{{ hotel.provinceName || 'Việt Nam' }}</span>
            </p>
        </div>

        <div class="py-1 px-4 pt-4 flex flex-col justify-between" style="height: calc(100% - 12rem);">
            <div>
                <h3 class="font-bold text-xl text-gray-800 leading-tight h-14 overflow-hidden" :title="hotel.name">
                    {{ hotel.name }}
                </h3>

                <div class="mt-0">
                    <div class="flex items-center text-gray-600 text-sm">
                        <i class="fas fa-hotel mr-2 text-blue-500"></i>
                        <span class="mr-2">Khách sạn</span>
                        <i v-for="n in hotel.starRating" :key="'full-' + n" class="fas fa-star text-yellow-400"></i>
                    </div>

                    <div class="text-gray-600 text-xs mt-2 flex items-center">
                        <i class="far fa-thumbs-up mr-1.5 text-gray-400"></i>
                        <span class="font-bold text-blue-700">{{ hotel.rating.toFixed(1) }}</span>/5
                        <span class="ml-1">({{ hotel.reviewCount }} đánh giá)</span>
                    </div>
                </div>
            </div>

            <div class="mt-2 pt-1 mb-2 border-t border-gray-100">
                <p>
                    <span class="text-gray-500 text-sm">Giá từ</span><br>
                    <template v-if="showDiscount">
                        <span class="text-gray-400 font-bold text-xs line-through block">{{
                            formatPrice(hotel.startingPrice) }}</span>
                        <span class="text-red-500 font-bold text-base block">{{ formatPrice(hotel.minDiscountedPrice)
                            }}</span>
                    </template>
                    <template v-else>
                        <span class="text-red-500 font-bold text-base block">{{ formatPrice(hotel.startingPrice)
                            }}</span>
                    </template>
                    <span class="block text-xs text-gray-400 font-normal mt-1">Chưa bao gồm thuế và phí</span>
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
        showDiscount() {
            return (
                this.hotel.minDiscountedPrice != null &&
                this.hotel.startingPrice != null &&
                Number(this.hotel.minDiscountedPrice) < Number(this.hotel.startingPrice)
            );
        },
        formatPrice() {
            return (price) => {
                if (price === null || typeof price === 'undefined') return "Liên hệ";
                return new Intl.NumberFormat('vi-VN', {
                    style: 'currency',
                    currency: 'VND',
                    currencyDisplay: 'code'
                }).format(Number(price)).replace('VND', ' VND');
            };
        }
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