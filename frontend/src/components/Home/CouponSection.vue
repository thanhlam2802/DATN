<template>
    <div class="container mx-auto px-4 py-8 relative">
        <h2 class="text-2xl font-bold mb-8">
            Coupons up to 50% for early bookings
        </h2>
        <button @click="scrollLeft"
            class="absolute left-0 top-1/2 transform -translate-y-1/2 z-10 bg-white shadow p-2 rounded-full">
            <i class="fas fa-chevron-left"></i>
        </button>
        <button @click="scrollRight"
            class="absolute right-0 top-1/2 transform -translate-y-1/2 z-10 bg-white shadow p-2 rounded-full">
            <i class="fas fa-chevron-right"></i>
        </button>
        <div ref="scrollContainer" class="flex space-x-4 overflow-x-auto pb-4 scrollbar-hide scroll-smooth">
            <div v-for="(coupon, index) in coupons" :key="index"
                class="min-w-[300px] bg-gray-50 p-6 rounded-lg shadow flex-shrink-0">
                <div class="flex justify-between mb-3">
                    <div class="text-amber-600 font-medium">Only a few left...</div>
                    <div class="bg-blue-100 text-blue-800 px-3 py-1 rounded-md text-sm">
                        {{ coupon.title }}
                    </div>
                </div>
                <div class="flex items-center mb-4">
                    <div class="bg-gray-200 p-3 rounded-lg mr-4">
                        <i class="fas fa-building text-3xl text-gray-600"></i>
                    </div>
                    <div>
                        <div class="text-sm text-gray-600">{{ coupon.discount1 }}</div>
                        <div class="text-sm text-gray-600">{{ coupon.discount2 }}</div>
                    </div>
                </div>
                <div class="flex justify-between items-center">
                    <div class="bg-blue-50 text-blue-700 px-3 py-1 rounded text-sm">
                        KSCUOITUANDNA
                    </div>
                    <button
                        class="whitespace-nowrap text-blue-600 border border-blue-600 px-4 py-1 rounded-full text-sm cursor-pointer">
                        Copy
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { coupons } from "../../data/appData";

export default {
    name: "CouponSection",
    data() {
        return {
            coupons: coupons,
        };
    },
    methods: {
        scrollLeft() {
            this.$refs.scrollContainer.scrollLeft -= 320;
        },
        scrollRight() {
            this.$refs.scrollContainer.scrollLeft += 320;
        },
    },
};
</script>

<style scoped>
.scrollbar-hide::-webkit-scrollbar {
    display: none;
}

.scrollbar-hide {
    -ms-overflow-style: none;
    scrollbar-width: none;
}
</style>
