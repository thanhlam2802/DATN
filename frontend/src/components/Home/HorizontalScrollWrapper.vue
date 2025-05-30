<template>
    <div>
        <div ref="scrollContainer" class="flex overflow-x-auto space-x-6 scrollbar-hide scroll-smooth"
            @mouseenter="pauseAutoScroll" @mouseleave="startAutoScroll" style="scroll-behavior: smooth;">
            <slot />
        </div>
        <div class="flex justify-center mt-8 space-x-2">
            <span v-for="(dot, index) in dotCount" :key="index" class="w-3 h-3 rounded-full cursor-pointer" :class="{
                'bg-blue-600': activeDot === index,
                'bg-gray-300': activeDot !== index
            }" @click="scrollToDot(index)"></span>
        </div>
    </div>
</template>

<script>
export default {
    name: "HorizontalScrollWrapper",
    props: {
        itemWidth: {
            type: Number,
            default: 300,
        },
        itemCount: {
            type: Number,
            required: true,
        },
    },
    data() {
        return {
            autoScrollTimer: null,
            activeDot: 0,
        };
    },
    computed: {
        dotCount() {
            const itemsPerPage = Math.floor(
                (this.$refs.scrollContainer?.clientWidth || 900) / this.itemWidth
            );
            return Math.ceil(this.itemCount / itemsPerPage);
        },
    },
    methods: {
        applyFocusEffect() {
            const container = this.$refs.scrollContainer;
            const children = Array.from(container.children);
            const containerCenter = container.scrollLeft + container.clientWidth / 2;

            children.forEach((child) => {
                const childCenter = child.offsetLeft + child.offsetWidth / 2;
                const distance = Math.abs(containerCenter - childCenter);

                if (distance < child.offsetWidth / 2) {
                    child.style.transform = 'scale(1.05)';
                    child.style.opacity = '1';
                    child.style.transition = 'transform 0.3s';
                    child.style.zIndex = 10;
                } else {
                    child.style.transform = 'scale(1)';
                    child.style.opacity = '1';
                    child.style.transition = 'transform 0.3s';
                    child.style.zIndex = 1;
                }
            });
        },
        onScroll() {
            this.applyFocusEffect();
            this.updateActiveDot();
        },
        updateActiveDot() {
            const container = this.$refs.scrollContainer;
            const itemsPerPage = Math.floor(container.clientWidth / this.itemWidth);
            const dotWidth = itemsPerPage * this.itemWidth;
            this.activeDot = Math.floor(container.scrollLeft / dotWidth);
        },
        autoScroll() {
            const container = this.$refs.scrollContainer;
            const maxScrollLeft = container.scrollWidth - container.clientWidth;
            let nextScrollLeft = container.scrollLeft + 400;
            if (nextScrollLeft > maxScrollLeft) nextScrollLeft = 0;
            container.scrollTo({ left: nextScrollLeft, behavior: "smooth" });
            this.onScroll();
        },
        scrollToDot(index) {
            const container = this.$refs.scrollContainer;
            const itemsPerPage = Math.floor(container.clientWidth / this.itemWidth);
            const scrollPos = index * itemsPerPage * this.itemWidth;
            container.scrollTo({ left: scrollPos, behavior: "smooth" });
            this.activeDot = index;
            this.restartAutoScroll();
            this.applyFocusEffect();
        },
        startAutoScroll() {
            if (this.autoScrollTimer) return;
            this.autoScrollTimer = setInterval(this.autoScroll, 4000);
        },
        pauseAutoScroll() {
            clearInterval(this.autoScrollTimer);
            this.autoScrollTimer = null;
        },
        restartAutoScroll() {
            this.pauseAutoScroll();
            this.startAutoScroll();
        },
    },
    mounted() {
        this.startAutoScroll();
        this.$refs.scrollContainer.addEventListener('scroll', this.onScroll);
        this.$nextTick(this.applyFocusEffect);
    },
    beforeUnmount() {
        this.pauseAutoScroll();
        this.$refs.scrollContainer.removeEventListener('scroll', this.onScroll);
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
