<template>
    <div class="container mx-auto px-4 py-8 relative">
        <h2 class="text-2xl font-bold mb-8">
            Coupons up to 50% for early bookings
        </h2>
        <button @click="scrollLeft"
            class="absolute left-0 top-1/2 transform -translate-y-1/2 z-10 bg-white shadow p-2 rounded-full hidden md:block">
            <i class="fas fa-chevron-left"></i>
        </button>
        <button @click="scrollRight"
            class="absolute right-0 top-1/2 transform -translate-y-1/2 z-10 bg-white shadow p-2 rounded-full hidden md:block">
            <i class="fas fa-chevron-right"></i>
        </button>
        <div ref="scrollContainer" class="flex space-x-4 overflow-x-auto pb-4 pt-8 px-4 scrollbar-hide scroll-smooth">
            <div v-for="(coupon, index) in coupons" :key="index" class="min-w-[300px] flex-shrink-0 relative">
                <div
                    class="absolute -top-4 -left-4 bg-amber-500 text-white px-3 py-1 rounded-full text-xs font-bold shadow-md z-10 whitespace-nowrap">
                    Only a few left!
                </div>

                <div class="bg-gray-50 p-6 rounded-lg shadow h-full">
                    <div class="flex items-start mb-4">
                        <div class="bg-gray-200 p-3 rounded-lg mr-4 flex-shrink-0">
                            <i class="fas fa-building text-3xl text-gray-600"></i>
                        </div>
                        <div class="flex-grow">
                            <div class="flex justify-between items-start mb-2">
                                <h3 class="font-bold text-lg text-gray-900 leading-tight pr-2">
                                    {{ coupon.title }}
                                </h3>
                                <i class="fas fa-info-circle text-gray-400 text-sm cursor-pointer flex-shrink-0"></i>
                            </div>
                            <div class="text-sm text-gray-600">{{ coupon.discount1 }}</div>
                            <div class="text-sm text-gray-600">{{ coupon.discount2 }}</div>
                        </div>
                    </div>

                    <div class="border-t-2 border-dashed border-gray-200 my-4"></div>

                    <div class="flex justify-between items-center">
                        <div class="flex items-center bg-blue-50 text-blue-700 px-3 py-1 rounded text-sm font-mono">
                            <i class="far fa-copy mr-2"></i> KSCUOITUANDNA
                        </div>
                        <button
                            class="whitespace-nowrap bg-blue-100 hover:bg-blue-200 text-blue-800 font-semibold py-2 px-5 rounded-full text-sm cursor-pointer ml-4">
                            Copy
                        </button>
                    </div>
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
            console.log('Clicked scrollLeft');
            console.log('scrollContainer ref:', this.$refs.scrollContainer);
            if (this.$refs.scrollContainer) {
                console.log('Current scrollLeft (before):', this.$refs.scrollContainer.scrollLeft);
                this.$refs.scrollContainer.scrollLeft -= 320;
                console.log('Current scrollLeft (after):', this.$refs.scrollContainer.scrollLeft);
            } else {
                console.log('scrollContainer ref is not available.');
            }
        },
        scrollRight() {
            console.log('Clicked scrollRight');
            console.log('scrollContainer ref:', this.$refs.scrollContainer);
            if (this.$refs.scrollContainer) {
                console.log('Current scrollLeft (before):', this.$refs.scrollContainer.scrollLeft);
                this.$refs.scrollContainer.scrollLeft += 320;
                console.log('Current scrollLeft (after):', this.$refs.scrollContainer.scrollLeft);
            } else {
                console.log('scrollContainer ref is not available.');
            }
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