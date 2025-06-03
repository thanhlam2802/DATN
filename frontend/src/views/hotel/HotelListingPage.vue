<template>
    <div>
        <p class="text-xs text-gray-700 font-semibold mb-4">
            {{ paginatedHotels.length }} stays in Tokyo, Japan
            <span v-if="totalHotels > paginatedHotels.length"> (of {{ totalHotels }})</span>
        </p>
        <HotelList :hotels="paginatedHotels" />

        <div aria-label="Pagination"
            class="flex justify-center items-center space-x-2 mt-12 text-gray-700 text-sm select-none">
            <button aria-label="Previous page"
                class="hover:text-white hover:bg-indigo-600 border border-gray-300 px-2 py-1 rounded-full transition"
                :class="{ 'opacity-50 cursor-not-allowed': currentPage === 1 }" :disabled="currentPage === 1"
                @click="goToPage(currentPage - 1)">
                <i class="fas fa-chevron-left"></i>
            </button>

            <template v-for="page in pagesToShow" :key="page">
                <span v-if="typeof page === 'string'" class="px-2">{{ page }}</span>
                <button v-else :aria-current="currentPage === page ? 'page' : null"
                    :class="{ 'bg-indigo-600 text-white': currentPage === page, 'hover:text-indigo-600': currentPage !== page }"
                    class="rounded-full w-8 h-8 flex items-center justify-center font-semibold"
                    @click="goToPage(page)">
                    {{ page }}
                </button>
            </template>

            <button aria-label="Next page"
                class="hover:text-white hover:bg-indigo-600 border border-gray-300 px-2 py-1 rounded-full transition"
                :class="{ 'opacity-50 cursor-not-allowed': currentPage === totalPages }"
                :disabled="currentPage === totalPages" @click="goToPage(currentPage + 1)">
                <i class="fas fa-chevron-right"></i>
            </button>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import HotelList from '@/components/Hotel/HotelList.vue';
import { hotels as allHotels } from '@/data/hotelData.js';

const itemsPerPage = 5; 
const currentPage = ref(1); 

const totalHotels = computed(() => allHotels.length);

const totalPages = computed(() => Math.ceil(totalHotels.value / itemsPerPage));

const paginatedHotels = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage;
    const end = start + itemsPerPage;
    return allHotels.slice(start, end);
});

const goToPage = (page) => {
    if (page >= 1 && page <= totalPages.value) {
        currentPage.value = page;
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }
};

const pagesToShow = computed(() => {
    const maxPagesDisplay = 5;
    const pages = [];

    if (totalPages.value <= maxPagesDisplay) {
        for (let i = 1; i <= totalPages.value; i++) {
            pages.push(i);
        }
    } else {
        const startPage = Math.max(1, currentPage.value - Math.floor(maxPagesDisplay / 2));
        const endPage = Math.min(totalPages.value, startPage + maxPagesDisplay - 1);

        if (startPage > 1) {
            pages.push(1);
            if (startPage > 2) {
                pages.push('...');
            }
        }

        for (let i = startPage; i <= endPage; i++) {
            pages.push(i);
        }

        if (endPage < totalPages.value) {
            if (endPage < totalPages.value - 1) {
                pages.push('...');
            }
            pages.push(totalPages.value);
        }
    }
    return pages;
});

// watch(allHotels, () => {
//     currentPage.value = 1;
// });
</script>