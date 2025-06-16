<template>
    <div>
        <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-4">
            <p class="text-base font-semibold text-gray-700">
                {{ cityLabel }} <br />
                {{ filteredHotels.length.toLocaleString() }} nơi lưu trú được tìm thấy
            </p>
            <div class="flex items-center space-x-4 mt-2 sm:mt-0">
                <label class="text-sm text-gray-600">Sắp xếp:</label>
                <select v-model="sortKey"
                    class="border border-gray-300 rounded px-2 py-1 text-sm focus:outline-none focus:ring-1 focus:ring-indigo-400">
                    <option value="default">Mặc định</option>
                    <option value="priceAsc">Giá thấp nhất</option>
                    <option value="priceDesc">Giá cao nhất</option>
                </select>
                <div class="flex items-center border border-gray-300 rounded-md p-0.5 shadow-sm">
                    <button @click="viewMode = 'list'"
                        :class="viewMode === 'list' ? 'bg-indigo-600 text-white' : 'text-gray-500 hover:bg-gray-100'"
                        class="px-2 py-1 rounded-md transition-colors duration-200" aria-label="Chế độ xem danh sách">
                        <i class="fas fa-list fa-fw"></i>
                    </button>
                    <button @click="viewMode = 'grid'"
                        :class="viewMode === 'grid' ? 'bg-indigo-600 text-white' : 'text-gray-500 hover:bg-gray-100'"
                        class="px-2 py-1 rounded-md transition-colors duration-200" aria-label="Chế độ xem lưới">
                        <i class="fas fa-th-large fa-fw"></i>
                    </button>
                </div>
            </div>
        </div>

        <div v-if="viewMode === 'list'" class="space-y-4">
            <HotelCard v-for="hotel in paginatedHotels" :key="hotel.id" v-bind="mapHotelProps(hotel)"
                :view-mode="viewMode" @click="goToDetail(hotel.id)" />
        </div>
        <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            <HotelCard v-for="hotel in paginatedHotels" :key="hotel.id" v-bind="mapHotelProps(hotel)"
                :view-mode="viewMode" @click="goToDetail(hotel.id)" />
        </div>

        <div aria-label="Pagination"
            class="flex justify-center items-center space-x-2 mt-8 text-gray-700 text-sm select-none">
            <button aria-label="Previous page"
                class="hover:text-white hover:bg-indigo-600 border border-gray-300 px-2 py-1 rounded-full transition"
                :class="{ 'opacity-50 cursor-not-allowed': currentPage === 1 }" :disabled="currentPage === 1"
                @click="goToPage(currentPage - 1)">
                <i class="fas fa-chevron-left"></i>
            </button>

            <template v-for="page in pagesToShow" :key="page">
                <span v-if="typeof page === 'string'" class="px-2">{{ page }}</span>
                <button v-else :aria-current="currentPage === page ? 'page' : null" :class="[
                    'rounded-full w-8 h-8 flex items-center justify-center font-semibold',
                    currentPage === page
                        ? 'bg-indigo-600 text-white'
                        : 'hover:text-indigo-600'
                ]" @click="goToPage(page)">
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
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import HotelCard from '@/components/Hotel/HotelCard.vue'
import { hotels as allHotels } from '@/data/hotelData.js'

const route = useRoute()
const router = useRouter()

const cityParam = ref(route.query.city || '')
watch(() => route.query.city, val => {
    cityParam.value = val || ''
    currentPage.value = 1
})

const cityLabel = computed(() => {
    if (cityParam.value) {
        return `Thành phố ${cityParam.value}`
    }
    return 'Tất cả địa điểm'
})

const itemsPerPage = 6
const currentPage = ref(1)
const totalHotels = computed(() => filteredHotels.value.length)
const totalPages = computed(() => Math.ceil(totalHotels.value / itemsPerPage))

const sortKey = ref('default')
const viewMode = ref('list')

const filteredHotels = computed(() => {
    if (cityParam.value) {
        return allHotels.filter(h => {
            return h.location === cityParam.value
        })
    }
    return allHotels.slice()
})

const sortedHotels = computed(() => {
    const arr = filteredHotels.value.slice()
    if (sortKey.value === 'priceAsc') {
        return arr.sort((a, b) => Number(a.price) - Number(b.price))
    }
    if (sortKey.value === 'priceDesc') {
        return arr.sort((a, b) => Number(b.price) - Number(a.price))
    }
    return arr
})

const paginatedHotels = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage
    return sortedHotels.value.slice(start, start + itemsPerPage)
})

const pagesToShow = computed(() => {
    const maxPagesDisplay = 5
    const pages = []
    if (totalPages.value <= maxPagesDisplay) {
        for (let i = 1; i <= totalPages.value; i++) pages.push(i)
    } else {
        let startPage = Math.max(1, currentPage.value - Math.floor(maxPagesDisplay / 2))
        let endPage = Math.min(totalPages.value, startPage + maxPagesDisplay - 1)
        if (startPage > 1) {
            pages.push(1)
            if (startPage > 2) pages.push('...')
        }
        for (let i = startPage; i <= endPage; i++) pages.push(i)
        if (endPage < totalPages.value) {
            if (endPage < totalPages.value - 1) pages.push('...')
            pages.push(totalPages.value)
        }
    }
    return pages
})

const goToPage = (page) => {
    if (page >= 1 && page <= totalPages.value) {
        currentPage.value = page
        window.scrollTo({ top: 0, behavior: 'smooth' })
    }
}

watch(sortKey, () => { currentPage.value = 1 })

watch(cityParam, () => {
    sortKey.value = 'default'
    viewMode.value = 'list'
})

const routerPush = useRouter()
const goToDetail = (id) => {
    routerPush.push({ name: 'HotelDetail', params: { id } })
}

const mapHotelProps = (hotel) => ({
    image: hotel.image,
    alt: hotel.title,
    location: hotel.location,
    title: hotel.title,
    details: hotel.details,
    amenities: hotel.amenities,
    rating: hotel.rating,
    reviews: hotel.reviews,
    originalPrice: Number(hotel.originalPrice),
    price: Number(hotel.price),
})
</script>
