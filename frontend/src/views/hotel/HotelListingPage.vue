<template>
    <main class="grid grid-cols-1 lg:grid-cols-[280px_1fr] gap-x-8 gap-y-6 max-w-full lg:w-[1320px]">
        <aside class="pt-40 pb-5">
            <FilterSidebar v-model:filters="activeFilters" />
        </aside>

        <div>
            <div class="sticky top-16 w-full flex justify-center mb-6 lg:-translate-x-[156px] z-30 mt-4">
                <div ref="searchWidgetContainer" class="w-full rounded-lg border border-gray-200 bg-white shadow-lg p-3">
                    <div
                        class="flex flex-col md:flex-row items-stretch h-auto md:h-auto border border-gray-300 rounded-lg">

                        <div ref="locationContainer"
                            class="relative flex flex-grow cursor-pointer items-center p-3 bg-white hover:bg-gray-50 border-b md:border-b-0 md:border-r border-gray-200 min-w-[200px] rounded-t-md md:rounded-l-md md:rounded-tr-none">
                            <i class="fas fa-map-marker-alt text-blue-500 text-xl pr-3"></i>
                            <div class="flex-1">
                                <label class="text-xs text-gray-500">Địa điểm hoặc khách sạn</label>
                                <input type="text" v-model="searchParams.location" @focus="handleLocationFocus"
                                    class="w-full bg-transparent font-semibold focus:outline-none text-gray-800"
                                    placeholder="Tìm kiếm..." autocomplete="off" />
                            </div>
                            <div v-if="showLocationDropdown && suggestions.length > 0"
                                class="absolute top-full mt-2 left-0 z-20 w-full bg-white border border-gray-300 rounded-lg shadow-lg max-h-56 overflow-y-auto">
                                <ul>
                                    <li v-for="loc in suggestions" :key="loc.type + '-' + loc.id"
                                        @click="selectLocation(loc.name)"
                                        class="px-4 py-2 hover:bg-gray-100 cursor-pointer flex items-center gap-3">
                                        <i
                                            :class="loc.type === 'Province' ? 'fas fa-map-marked-alt text-gray-400' : 'fas fa-hotel text-gray-400'"></i>
                                        <div>
                                            <p class="font-semibold">{{ loc.name }}</p>
                                            <p class="text-xs text-gray-500">{{ loc.type === 'Province' ? 'Tỉnh/Thành phố' : 'Khách sạn' }}</p>
                                        </div>
                                    </li>
                                </ul>
                            </div>
                        </div>

                        <div
                            class="flex flex-grow-[2] items-center p-3 bg-white hover:bg-gray-50 border-b md:border-b-0 md:border-r border-gray-200 min-w-[300px]">
                            <i class="fas fa-calendar-alt text-blue-500 text-xl pr-3"></i>
                            <div class="flex flex-1 items-center">
                                <div class="flex-1">
                                    <label class="text-xs text-gray-500">Ngày nhận</label>
                                    <input type="date" v-model="searchParams.checkin" :min="today"
                                        class="w-full bg-transparent font-semibold focus:outline-none" />
                                </div>
                                <div class="px-4 text-center">
                                    <div v-if="numberOfNights > 0"
                                        class="text-xs font-semibold text-blue-600 bg-blue-100 rounded-full px-2 py-0.5 whitespace-nowrap">
                                        {{ numberOfNights }} đêm
                                    </div>
                                </div>
                                <div class="flex-1">
                                    <label class="text-xs text-gray-500">Ngày trả</label>
                                    <input type="date" v-model="searchParams.checkout" :min="minCheckOut"
                                        class="w-full bg-transparent font-semibold focus:outline-none" />
                                </div>
                            </div>
                        </div>

                        <div ref="guestsContainer" class="relative flex-grow">
                            <div @click="showGuestsDropdown = !showGuestsDropdown"
                                class="flex items-center p-3 bg-white hover:bg-gray-50 cursor-pointer h-full max-w-[300px]">
                                <i class="fas fa-users text-blue-500 text-xl pr-3"></i>
                                <div class="flex-1">
                                    <label class="text-xs text-gray-500">Khách và Phòng</label>
                                    <p class="font-semibold truncate text-gray-800">{{ guestsDisplay }}</p>
                                </div>
                            </div>
                            <div v-if="showGuestsDropdown" aria-label="Guest and room selection"
                                class="absolute top-full mt-2 right-0 z-30 w-[320px] bg-white rounded-lg shadow-lg border border-gray-200 p-4"
                                role="listbox">
                                <div class="flex items-center gap-3 mb-4">
                                    <i class="fas fa-user text-lg text-gray-600 w-5 text-center"></i>
                                    <span class="text-gray-900 font-semibold text-base">Người lớn</span>
                                    <div class="ml-auto flex items-center gap-2">
                                        <button @click.stop="updateGuests('adults', -1)"
                                            :disabled="searchParams.adults <= 1"
                                            class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 disabled:text-gray-300 disabled:border-gray-200 disabled:bg-gray-50 flex items-center justify-center hover:bg-gray-50"
                                            type="button">−</button>
                                        <span
                                            class="w-8 h-8 flex items-center justify-center text-gray-900 font-semibold text-base select-none">{{
                                                searchParams.adults }}</span>
                                        <button @click.stop="updateGuests('adults', 1)"
                                            class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 flex items-center justify-center hover:bg-gray-50"
                                            type="button">+</button>
                                    </div>
                                </div>
                                <div class="flex items-center gap-3 mb-4">
                                    <i class="fas fa-child text-lg text-gray-600 w-5 text-center"></i>
                                    <span class="text-gray-900 font-semibold text-base">Trẻ em</span>
                                    <div class="ml-auto flex items-center gap-2">
                                        <button @click.stop="updateGuests('children', -1)"
                                            :disabled="searchParams.children <= 0"
                                            class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 disabled:text-gray-300 disabled:border-gray-200 disabled:bg-gray-50 flex items-center justify-center hover:bg-gray-50"
                                            type="button">−</button>
                                        <span
                                            class="w-8 h-8 flex items-center justify-center text-gray-900 font-semibold text-base select-none">{{
                                                searchParams.children }}</span>
                                        <button @click.stop="updateGuests('children', 1)"
                                            class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 flex items-center justify-center hover:bg-gray-50"
                                            type="button">+</button>
                                    </div>
                                </div>
                                <div class="flex items-center gap-3">
                                    <i class="fas fa-bed text-lg text-gray-600 w-5 text-center"></i>
                                    <span class="text-gray-900 font-semibold text-base">Phòng</span>
                                    <div class="ml-auto flex items-center gap-2">
                                        <button @click.stop="updateGuests('rooms', -1)"
                                            :disabled="searchParams.rooms <= 1"
                                            class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 disabled:text-gray-300 disabled:border-gray-200 disabled:bg-gray-50 flex items-center justify-center hover:bg-gray-50"
                                            type="button">−</button>
                                        <span
                                            class="w-8 h-8 flex items-center justify-center text-gray-900 font-semibold text-base select-none">{{
                                                searchParams.rooms }}</span>
                                        <button @click.stop="updateGuests('rooms', 1)"
                                            class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 flex items-center justify-center hover:bg-gray-50"
                                            type="button">+</button>
                                    </div>
                                </div>
                                <p v-if="guestsError" class="text-red-500 text-xs mt-3 text-center">{{ guestsError }}
                                </p>
                            </div>
                        </div>

                        <button aria-label="Search"
                            class="bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-3 font-bold transition flex items-center justify-center rounded-b-md md:rounded-l-none md:rounded-r-md"
                            @click="onSearch">
                            <i class="fas fa-search"></i>
                        </button>
                    </div>
                </div>
            </div>

            <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-4 pt-7">
                <h1 class="font-bold text-gray-800">
                    <span class="block text-2xl">{{ locationDisplay }}</span>
                    <span class="block text-base font-normal text-gray-600">{{ hotelCountDisplay }}</span>
                </h1>
                <div class="flex items-center space-x-4 mt-2 sm:mt-0">
                    <span class="text-sm font-semibold">Xếp theo:</span>
                    <select v-model="sortKey"
                        class="border border-gray-300 rounded-md px-2 py-1.5 text-sm focus:outline-none focus:ring-1 focus:ring-indigo-400 shadow-sm mr-5">
                        <option value="default">Mặc định</option>
                        <option value="priceAsc">Giá thấp nhất</option>
                        <option value="priceDesc">Giá cao nhất</option>
                        <option value="ratingDesc">Đánh giá cao nhất</option>
                    </select>
                    <div class="flex items-center border border-gray-300 rounded-md p-0.5 shadow-sm">
                        <button @click="viewMode = 'list'"
                            :class="viewMode === 'list' ? 'bg-indigo-600 text-white' : 'text-gray-500 hover:bg-gray-100'"
                            class="px-2 py-1 rounded-md transition-colors duration-200"
                            aria-label="Chế độ xem danh sách"><i class="fas fa-list fa-fw"></i></button>
                        <button @click="viewMode = 'grid'"
                            :class="viewMode === 'grid' ? 'bg-indigo-600 text-white' : 'text-gray-500 hover:bg-gray-100'"
                            class="px-2 py-1 rounded-md transition-colors duration-200" aria-label="Chế độ xem lưới"><i
                                class="fas fa-th-large fa-fw"></i></button>
                    </div>
                </div>
            </div>

            <div :class="viewMode === 'list' ? 'space-y-4' : 'grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-6'">
                <HotelCard v-for="hotel in paginatedHotels" :key="hotel.id" :view-mode="viewMode" :image="hotel.image"
                    :alt="hotel.title" :location="hotel.location" :title="hotel.title" :stars="hotel.stars"
                    :rating="hotel.rating" :full-address="hotel.fullAddress" :reviews="hotel.reviews"
                    :details="hotel.details" :amenities="hotel.amenities" :original-price="hotel.originalPrice"
                    :price="hotel.price" :is-favorited="favoritedHotels.has(hotel.id)" @click="goToDetail(hotel.id)"
                    @toggle-favorite="toggleFavorite(hotel.id)" />
            </div>

            <div aria-label="Pagination"
                class="flex justify-center items-center space-x-2 mt-8 mb-3 text-gray-700 text-sm select-none">
                <button aria-label="Previous page"
                    class="hover:text-white hover:bg-indigo-600 border border-gray-300 w-8 h-8 rounded-full transition flex items-center justify-center"
                    :class="{ 'opacity-50 cursor-not-allowed': currentPage === 1 }" :disabled="currentPage === 1"
                    @click="goToPage(currentPage - 1)"><i class="fas fa-chevron-left"></i></button>
                <template v-for="page in pagesToShow" :key="page">
                    <span v-if="typeof page === 'string'" class="px-2">...</span>
                    <button v-else :aria-current="currentPage === page ? 'page' : null" :class="[
                        'rounded-full w-8 h-8 flex items-center justify-center font-semibold',
                        currentPage === page ?
                            'bg-indigo-600 text-white shadow-md'
                            : 'hover:bg-indigo-100 hover:text-indigo-600'
                    ]" @click="goToPage(page)">
                        {{ page }}
                    </button>
                </template>
                <button aria-label="Next page"
                    class="hover:text-white hover:bg-indigo-600 border border-gray-300 w-8 h-8 rounded-full transition flex items-center justify-center"
                    :class="{ 'opacity-50 cursor-not-allowed': currentPage === totalPages }"
                    :disabled="currentPage === totalPages" @click="goToPage(currentPage + 1)"><i
                        class="fas fa-chevron-right"></i></button>
            </div>
        </div>
    </main>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import HotelCard from '@/components/Hotel/HotelCard.vue'
import FilterSidebar from '@/components/Hotel/FilterSidebar.vue'
import { searchHotels } from '@/api/hotelApi.js'
import { getAllProvinces } from '@/api/provinceApi.js'

const route = useRoute()
const router = useRouter()

const locationContainer = ref(null)
const searchWidgetContainer = ref(null);
const guestsContainer = ref(null);
const showLocationDropdown = ref(false)
const showGuestsDropdown = ref(false);
const guestsError = ref('');
const errorTimeout = ref(null);

const today = new Date().toISOString().split('T')[0]

const hotels = ref([]);
const provinces = ref([]);
const hotelSuggestions = ref([]);
const debounceTimer = ref(null);
const locationBeforeEdit = ref('');

const searchParams = ref({
    location: '',
    checkin: '',
    checkout: '',
    adults: 2,
    children: 0,
    rooms: 1,
})

const activeLocationQuery = ref('');

const guestsDisplay = computed(() => {
    return `${searchParams.value.adults} người lớn, ${searchParams.value.children} trẻ em, ${searchParams.value.rooms} phòng`;
});

const suggestions = computed(() => {
    const keyword = searchParams.value.location.toLowerCase();
    const provinceResults = provinces.value
        .filter(p => p.name.toLowerCase().includes(keyword))
        .map(p => ({ id: `p-${p.id}`, name: p.name, type: 'Province' }));
    const hotelResults = hotelSuggestions.value.map(h => ({ id: `h-${h.id}`, name: h.name, type: 'Hotel' }));
    return [...provinceResults, ...hotelResults];
});

const minCheckOut = computed(() => {
    if (searchParams.value.checkin) {
        const d = new Date(searchParams.value.checkin)
        d.setDate(d.getDate() + 1)
        return d.toISOString().split('T')[0]
    }
    return today
})

const numberOfNights = computed(() => {
    if (searchParams.value.checkin && searchParams.value.checkout) {
        const s = new Date(searchParams.value.checkin)
        const e = new Date(searchParams.value.checkout)
        if (e <= s) return 0
        const diff = e - s
        return Math.ceil(diff / (1000 * 60 * 60 * 24))
    }
    return 0
})

watch(() => searchParams.value.checkin, nv => {
    const s = new Date(nv)
    const e = new Date(searchParams.value.checkout)
    if (e <= s) {
        const ne = new Date(s)
        ne.setDate(ne.getDate() + 1)
        searchParams.value.checkout = ne.toISOString().split('T')[0]
    }
})

watch(() => searchParams.value.location, (newKeyword) => {
    if (newKeyword && newKeyword !== activeLocationQuery.value) {
        clearTimeout(debounceTimer.value);
        debounceTimer.value = setTimeout(() => {
            fetchHotelSuggestions(newKeyword);
        }, 300);
    } else {
        hotelSuggestions.value = [];
    }
});

function handleLocationFocus() {
    locationBeforeEdit.value = searchParams.value.location;
    showLocationDropdown.value = true;
}

const fetchHotelSuggestions = async (keyword) => {
    try {
        const response = await searchHotels({ keyword: keyword, size: 5 });
        if (response.data?.statusCode === 200) {
            hotelSuggestions.value = response.data.data.content;
        }
    } catch (error) { console.error("Could not fetch hotel suggestions:", error); }
};

function selectLocation(loc) {
    searchParams.value.location = loc
    showLocationDropdown.value = false
}

function updateGuests(type, amount) {
    const params = searchParams.value;
    if (errorTimeout.value) clearTimeout(errorTimeout.value);
    guestsError.value = '';

    if (type === 'adults') {
        const newAdults = params.adults + amount;
        if (newAdults >= 1) {
            params.adults = newAdults;
            if (params.adults < params.rooms) {
                params.rooms = params.adults;
            }
        }
    } else if (type === 'children') {
        params.children = Math.max(0, params.children + amount);
    } else if (type === 'rooms') {
        const newRooms = params.rooms + amount;
        if (amount > 0) {
            if (newRooms > params.adults) {
                guestsError.value = 'Số phòng không thể nhiều hơn số người lớn.';
                errorTimeout.value = setTimeout(() => { guestsError.value = '' }, 3000);
            } else {
                params.rooms = newRooms;
            }
        } else {
            params.rooms = Math.max(1, newRooms);
        }
    }
}

function onSearch() {
    const searchData = {
        location: searchParams.value.location,
        checkin: searchParams.value.checkin,
        checkout: searchParams.value.checkout,
        adults: searchParams.value.adults,
        children: searchParams.value.children,
        rooms: searchParams.value.rooms,
    };
    localStorage.setItem('lastSearchParams', JSON.stringify(searchData));

    const query = {
        ...route.query,
        keyword: searchParams.value.location,
        checkInDate: searchParams.value.checkin,
        checkOutDate: searchParams.value.checkout,
        numAdults: searchParams.value.adults,
        numChildren: searchParams.value.children,
        rooms: searchParams.value.rooms,
        page: 1
    }
    router.push({ query })
}

const handleClickOutside = (event) => {
    if (searchWidgetContainer.value && !searchWidgetContainer.value.contains(event.target)) {
        showLocationDropdown.value = false;
        showGuestsDropdown.value = false;
        if (searchParams.value.location.trim() === '' && locationBeforeEdit.value) {
            searchParams.value.location = locationBeforeEdit.value;
        }
    } else {
        if (locationContainer.value && !locationContainer.value.contains(event.target)) {
            showLocationDropdown.value = false;
        }
        if (guestsContainer.value && !guestsContainer.value.contains(event.target)) {
            showGuestsDropdown.value = false;
        }
    }
};

onMounted(async () => {
    try {
        const response = await getAllProvinces();
        provinces.value = response.data?.data || [];
    } catch (error) {
        console.error("Could not fetch provinces:", error);
    }
    document.addEventListener('click', handleClickOutside)
});

onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside)
});

const activeFilters = ref({ starRating: 0, priceRange: [0, 20000000], amenities: {} })
const sortKey = ref('default')
const viewMode = ref('list')

const filteredHotels = computed(() => {
    let arr = hotels.value
    if (activeFilters.value.starRating) {
        arr = arr.filter(h => h.stars === activeFilters.value.starRating)
    }
    const ams = Object.keys(activeFilters.value.amenities).filter(k => activeFilters.value.amenities[k])
    if (ams.length) arr = arr.filter(h => ams.every(a => h.amenities.includes(a)))
    const [minP, maxP] = activeFilters.value.priceRange
    return arr.filter(h => h.price >= minP && h.price <= maxP)
})

const sortedHotels = computed(() => {
    const arr = [...filteredHotels.value]
    if (sortKey.value === 'priceAsc') return arr.sort((a, b) => a.price - b.price)
    if (sortKey.value === 'priceDesc') return arr.sort((a, b) => b.price - a.price)
    if (sortKey.value === 'ratingDesc') return arr.sort((a, b) => b.rating - a.rating)
    return arr
})

const itemsPerPage = 6
const currentPage = ref(1)
const totalPages = computed(() => Math.ceil(sortedHotels.value.length / itemsPerPage))

const locationDisplay = computed(() => {
    if (activeLocationQuery.value) {
        return activeLocationQuery.value;
    }
    return 'Tất cả địa điểm';
});

const hotelCountDisplay = computed(() => {
    const count = filteredHotels.value.length.toLocaleString();
    return `${count} nơi lưu trú được tìm thấy`;
});
const paginatedHotels = computed(() =>
    sortedHotels.value.slice((currentPage.value - 1) * itemsPerPage, currentPage.value * itemsPerPage)
)

const pagesToShow = computed(() => {
    const maxD = 5, p = [], tp = totalPages.value, cp = currentPage.value
    if (tp <= maxD + 2) {
        for (let i = 1; i <= tp; i++) p.push(i)
    } else {
        p.push(1)
        if (cp > 3) p.push('...')
        let start = Math.max(2, cp - 1), end = Math.min(tp - 1, cp + 1)
        if (cp <= 3) { start = 2; end = 4 }
        if (cp >= tp - 2) { start = tp - 3; end = tp - 1 }
        for (let i = start; i <= end; i++) p.push(i)
        if (cp < tp - 2) p.push('...')
        p.push(tp)
    }
    return [...new Set(p)]
})

watch([sortKey, activeFilters], () => { currentPage.value = 1 }, { deep: true })

const fetchHotels = async (queryParams) => {
    try {
        const apiParams = {
            keyword: queryParams.keyword,
            checkInDate: queryParams.checkInDate,
            checkOutDate: queryParams.checkOutDate,
            numAdults: queryParams.numAdults,
            numChildren: queryParams.numChildren,
            rooms: queryParams.rooms,
            size: 1000
        };
        const response = await searchHotels(apiParams);
        if (response.data?.statusCode === 200) {
            const hotelDtos = response.data.data.content;
            hotels.value = hotelDtos.map(h => ({
                id: h.id,
                title: h.name,
                location: h.provinceName || h.address,
                details: h.description,
                amenities: h.amenities?.map(a => a.name).join(', '),
                rating: h.rating?.toFixed(1) || 'N/A',
                reviews: h.reviewCount || 0,
                originalPrice: null,
                price: h.startingPrice || 0,
                image: h.imageUrl || 'https://via.placeholder.com/320x230.png?text=Hotel+Image',
                stars: h.starRating,
                fullAddress: h.address
            }));
        } else {
            hotels.value = [];
        }
    } catch (error) {
        console.error("Failed to fetch hotels:", error);
        hotels.value = [];
    }
};

watch(() => route.query, q => {
    const queryParams = {
        keyword: q.keyword || '',
        checkin: q.checkInDate || today,
        checkout: q.checkOutDate || minCheckOut.value,
        adults: Number(q.numAdults) || 2,
        children: Number(q.numChildren) || 0,
        rooms: Number(q.rooms) || 1,
    };
    searchParams.value = { ...queryParams, location: queryParams.keyword };
    activeLocationQuery.value = queryParams.keyword;
    currentPage.value = Number(q.page) || 1;
    fetchHotels(queryParams);
}, { immediate: true, deep: true })

function goToPage(pg) {
    if (pg >= 1 && pg <= totalPages.value) {
        currentPage.value = pg
        router.push({ query: { ...route.query, page: pg } })
        window.scrollTo({ top: 0, behavior: 'smooth' })
    }
}

function goToDetail(id) {
    router.push({ name: 'HotelDetail', params: { id }, query: route.query })
}

const favoritedHotels = ref(new Set());

const toggleFavorite = (hotelId) => {
    if (favoritedHotels.value.has(hotelId)) {
        favoritedHotels.value.delete(hotelId);
    } else {
        favoritedHotels.value.add(hotelId);
    }
};
</script>

<style scoped>
.line-clamp-1 {
    overflow: hidden;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 1;
    line-clamp: 1;
}

.line-clamp-2 {
    overflow: hidden;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    line-clamp: 2;
}
</style>