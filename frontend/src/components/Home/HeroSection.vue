<template>
    <div class="relative w-full h-[500px] mt-4 overflow-hidden rounded-xl">
        <div class="absolute inset-0 flex rounded-xl"
            :class="{ 'transition-transform duration-700 ease-in-out': isAnimating }"
            :style="{ transform: `translateX(-${currentIndex * 100}%)` }">
            <div v-for="(img, i) in images" :key="i" class="w-full flex-shrink-0 h-[500px]">
                <img :src="img" alt="Travel Banner"
                    class="w-full h-full object-cover object-center brightness-75 rounded-xl" />
            </div>
        </div>
        <div class="absolute inset-0 flex flex-col items-center justify-center text-center text-white px-4 z-10">
            <div class="mb-4 -mt-16 md:-mt-20 flex flex-col items-center">
                <h1 class="text-4xl md:text-6xl font-bold mb-2 drop-shadow-lg leading-tight">
                    Khám phá thế giới cùng chúng tôi
                </h1>
                <p class="text-lg md:text-xl drop-shadow-md max-w-2xl">
                    Đặt tour, khách sạn, xe buýt và nhiều hơn nữa chỉ với vài cú click!
                </p>
            </div>
        </div>
        <button
            class="absolute left-4 top-1/2 transform -translate-y-1/2 z-20 bg-white/30 hover:bg-white/60 text-white p-3 rounded-full flex items-center justify-center transition-colors duration-300"
            @click="prevImage">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
            </svg>
        </button>
        <button
            class="absolute right-4 top-1/2 transform -translate-y-1/2 z-20 bg-white/30 hover:bg-white/60 text-white p-3 rounded-full flex items-center justify-center transition-colors duration-300"
            @click="nextImage">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
            </svg>
        </button>
        <div class="absolute bottom-4 left-1/2 transform -translate-x-1/2 z-20 flex space-x-2">
            <span v-for="(img, i) in realImages" :key="i"
                class="w-3 h-3 rounded-full cursor-pointer transition-colors duration-300"
                :class="currentIndex === i + 1 ? 'bg-white' : 'bg-white/50'" @click="goToImage(i)">
            </span>
        </div>
    </div>
</template>

<script>
import { heroBannerImages } from "@/data/appData.js";

export default {
    name: "HeroSection",
    data() {
        return {
            realImages: heroBannerImages,
            currentIndex: 1,
            intervalId: null,
            isAnimating: true,
        };
    },
    computed: {
        images() {
            const images = this.realImages;
            return [images[images.length - 1], ...images, images[0]];
        }
    },
    mounted() {
        this.startImageSlider();
    },
    beforeUnmount() {
        clearInterval(this.intervalId);
    },
    methods: {
        startImageSlider() {
            this.intervalId = setInterval(this.nextImage, 5000);
        },
        nextImage() {
            if (this.currentIndex < this.images.length - 1) {
                this.currentIndex++;
                this.isAnimating = true;
            }
            if (this.currentIndex === this.images.length - 1) {
                setTimeout(() => {
                    this.isAnimating = false;
                    this.currentIndex = 1;
                }, 700);
            }
        },
        prevImage() {
            if (this.currentIndex > 0) {
                this.currentIndex--;
                this.isAnimating = true;
            }
            if (this.currentIndex === 0) {
                setTimeout(() => {
                    this.isAnimating = false;
                    this.currentIndex = this.images.length - 2;
                }, 700);
            }
        },
        goToImage(index) {
            this.isAnimating = true;
            this.currentIndex = index + 1;
        }
    },
};
</script>

<style scoped>
img {
    transition: transform 8s ease;
}

img:hover {
    transform: scale(1.05);
}
</style>