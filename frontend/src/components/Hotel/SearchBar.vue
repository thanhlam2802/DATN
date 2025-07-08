<template>
    <transition name="slide-fade">
        <div v-if="guestsError"
            class="fixed top-5 right-5 z-[99999] flex items-center gap-3 bg-red-600 text-white font-semibold px-4 py-3 rounded-lg shadow-lg">
            <i class="fas fa-exclamation-circle"></i>
            <span>{{ guestsError }}</span>
        </div>
    </transition>

    <transition name="fade">
        <div v-if="isSearchFormActive" @click="isSearchFormActive = false" class="fixed inset-0 bg-black/70 z-5"></div>
    </transition>

    <section class="relative w-full max-w-7xl mx-auto px-4 mt-4">
        <div class="relative shadow-lg rounded-xl overflow-hidden h-96">
            <img alt="Thành phố Dubai về đêm" class="w-full h-full object-cover"
                src="https://cdn.pixabay.com/photo/2021/12/11/07/59/hotel-6862159_1280.jpg" />
            <div class="absolute inset-0 bg-black/20"></div>
        </div>
    </section>

    <form aria-label="Hotel search form" @submit.prevent="onSearch" @click="isSearchFormActive = true"
        class="bg-white rounded-md shadow-md p-6 space-y-6 max-w-[900px] mx-auto mt-[-60px] relative z-10">
        <button class="flex items-center gap-2 text-[#0072c6] font-semibold text-sm" type="button">
            <i class="fas fa-history"></i>
            <span>Khách sạn xem gần đây</span>
        </button>

        <div class="relative" ref="locationContainer">
            <label class="block mb-1 text-xs font-normal text-[#4a4a4a]" for="location">Thành phố, địa điểm hoặc tên
                khách sạn:</label>
            <div class="flex items-center gap-2 border rounded-md px-3 py-2 text-sm text-[#4a4a4a] transition-colors"
                :class="locationError ? 'border-red-500' : 'border-gray-300'">
                <i class="fas fa-map-marker-alt text-[#0072c6]"></i>
                <input class="w-full bg-transparent outline-none font-semibold" id="location" type="text"
                    v-model="searchParams.keyword" placeholder="Nhập thành phố hoặc tên khách sạn"
                    @focus="handleLocationFocus" autocomplete="off" />
            </div>
            <p v-if="locationError" class="text-red-500 text-xs mt-1 pl-1">{{ locationError }}</p>
            <div v-if="showLocationDropdown && suggestions.length > 0"
                class="absolute top-full mt-2 w-full max-h-60 overflow-y-auto bg-white border border-gray-200 rounded-md shadow-lg z-20">
                <ul>
                    <li v-for="loc in suggestions" :key="loc.type + '-' + loc.id" @click="selectLocation(loc.name)"
                        class="px-4 py-2 hover:bg-gray-100 cursor-pointer flex items-center gap-3">
                        <i
                            :class="loc.type === 'Province' ? 'fas fa-map-marked-alt text-gray-400' : 'fas fa-hotel text-gray-400'"></i>
                        <div>
                            <p class="font-semibold">{{ loc.name }}</p>
                            <p class="text-xs text-gray-500">{{ loc.type === 'Province' ? 'Tỉnh/Thành phố' : 'Khách sạn'
                                }}</p>
                        </div>
                    </li>
                </ul>
            </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-4 md:gap-6 items-end">
            <div>
                <label class="block mb-1 text-xs font-normal text-[#4a4a4a]" for="checkin">Nhận phòng:</label>
                <div class="flex items-center gap-2 border border-gray-300 rounded-md px-3 py-2 text-sm text-[#4a4a4a]">
                    <i class="far fa-calendar-alt text-[#0072c6]"></i>
                    <input class="w-full bg-transparent outline-none font-semibold" id="checkin" type="date"
                        v-model="searchParams.checkin" :min="today" />
                </div>
            </div>

            <div class="relative" ref="nightsContainer">
                <label class="block mb-1 text-xs font-normal text-[#4a4a4a]">Số đêm:</label>
                <button @click="showNightsDropdown = !showNightsDropdown" type="button"
                    class="flex items-center justify-between w-full gap-2 border border-gray-300 rounded-md px-3 py-2 text-sm text-[#4a4a4a] cursor-pointer">
                    <div class="flex items-center gap-2">
                        <i class="fas fa-moon text-[#0072c6]"></i>
                        <span class="font-semibold">{{ searchParams.nights }} đêm</span>
                    </div>
                    <i class="fas fa-chevron-down text-xs text-gray-500"></i>
                </button>
                <div v-if="showNightsDropdown"
                    class="absolute top-full mt-2 w-full max-h-60 overflow-y-auto bg-white border border-gray-200 rounded-md shadow-lg z-20">
                    <ul>
                        <li v-for="n in 30" :key="n" @click="selectNights(n)"
                            class="px-4 py-2 hover:bg-gray-100 cursor-pointer">
                            <p class="font-semibold">{{ n }} đêm</p>
                            <p class="text-xs text-gray-500">{{ calculateCheckoutDisplay(n) }}</p>
                        </li>
                    </ul>
                </div>
            </div>

            <div>
                <p class="text-xs font-semibold text-[#1a1a1a] mb-1">Trả phòng:</p>
                <p class="text-sm font-semibold text-[#1a1a1a]">{{ checkOutDateFormatted }}</p>
            </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-[1fr_auto] gap-4 items-end">
            <div class="relative" ref="guestsContainer">
                <label class="block mb-1 text-xs font-normal text-[#4a4a4a]">Khách và Phòng</label>
                <button @click="showGuestsDropdown = !showGuestsDropdown" type="button"
                    class="flex items-center justify-between w-[560px] gap-2 border border-gray-300 rounded-md px-3 py-2 text-sm text-[#4a4a4a] cursor-pointer">
                    <div class="flex items-center gap-2">
                        <i class="fas fa-user-friends text-[#0072c6]"></i>
                        <span class="font-semibold">{{ guestsDisplay }}</span>
                    </div>
                    <i class="fas fa-chevron-down text-xs text-gray-500"></i>
                </button>
                <div v-if="showGuestsDropdown"
                    class="absolute z-40 mt-2 w-full md:w-[320px] bg-white rounded-lg shadow-lg border border-gray-200 p-4">
                    <div class="flex items-center gap-3 mb-4">
                        <i class="fas fa-user text-lg text-gray-600 w-5 text-center"></i>
                        <span class="text-gray-900 font-semibold text-base">Người lớn</span>
                        <div class="ml-auto flex items-center gap-2">
                            <button @click="updateGuests('adults', -1)" :disabled="searchParams.adults <= 1"
                                type="button"
                                class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 disabled:opacity-50 flex items-center justify-center hover:bg-gray-50">-</button>
                            <span class="w-8 text-center font-semibold">{{ searchParams.adults }}</span>
                            <button @click="updateGuests('adults', 1)" type="button"
                                class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 flex items-center justify-center hover:bg-gray-50">+</button>
                        </div>
                    </div>
                    <div class="flex items-center gap-3 mb-4">
                        <i class="fas fa-child text-lg text-gray-600 w-5 text-center"></i>
                        <span class="text-gray-900 font-semibold text-base">Trẻ em</span>
                        <div class="ml-auto flex items-center gap-2">
                            <button @click="updateGuests('children', -1)" :disabled="searchParams.children <= 0"
                                type="button"
                                class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 disabled:opacity-50 flex items-center justify-center hover:bg-gray-50">-</button>
                            <span class="w-8 text-center font-semibold">{{ searchParams.children }}</span>
                            <button @click="updateGuests('children', 1)" type="button"
                                class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 flex items-center justify-center hover:bg-gray-50">+</button>
                        </div>
                    </div>
                    <div class="flex items-center gap-3">
                        <i class="fas fa-bed text-lg text-gray-600 w-5 text-center"></i>
                        <span class="text-gray-900 font-semibold text-base">Phòng</span>
                        <div class="ml-auto flex items-center gap-2">
                            <button @click="updateGuests('rooms', -1)" :disabled="searchParams.rooms <= 1" type="button"
                                class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 disabled:opacity-50 flex items-center justify-center hover:bg-gray-50">-</button>
                            <span class="w-8 text-center font-semibold">{{ searchParams.rooms }}</span>
                            <button @click="updateGuests('rooms', 1)" type="button"
                                class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 flex items-center justify-center hover:bg-gray-50">+</button>
                        </div>
                    </div>
                    <div class="text-right pt-4 mt-4 border-t border-gray-100">
                        <button @click="showGuestsDropdown = false" class="text-blue-600 font-semibold"
                            type="button">Xong</button>
                    </div>
                </div>
            </div>

            <button
                class="h-10 w-[270px] bg-[#ff5c00] hover:bg-[#e04e00] text-white font-semibold text-lg rounded-md px-12 flex items-center justify-center gap-2"
                type="submit">
                <i class="fas fa-search"></i>
                <span>Tìm khách sạn</span>
            </button>
        </div>
    </form>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { getAllProvinces } from '@/api/provinceApi';
import { searchHotels } from '@/api/hotelApi';

const router = useRouter();

const locationContainer = ref(null);
const nightsContainer = ref(null);
const guestsContainer = ref(null);

const showLocationDropdown = ref(false);
const showNightsDropdown = ref(false);
const showGuestsDropdown = ref(false);
const guestsError = ref('');
const locationError = ref('');
const errorTimeout = ref(null);
const isSearchFormActive = ref(false);

const provinces = ref([]);
const hotelSuggestions = ref([]);
const debounceTimer = ref(null);
const locationBeforeEdit = ref('');
const today = new Date().toISOString().split('T')[0];

const searchParams = ref({
    keyword: '',
    checkin: today,
    nights: 1,
    adults: 2,
    children: 0,
    rooms: 1,
});

const checkoutDate = computed(() => {
    if (!searchParams.value.checkin) return null;
    const date = new Date(searchParams.value.checkin);
    date.setDate(date.getDate() + searchParams.value.nights);
    return date;
});

const checkOutDateFormatted = computed(() => {
    if (!checkoutDate.value) return "Vui lòng chọn ngày nhận phòng";
    return new Intl.DateTimeFormat('vi-VN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }).format(checkoutDate.value);
});

const guestsDisplay = computed(() => `${searchParams.value.adults} người lớn, ${searchParams.value.children} Trẻ em, ${searchParams.value.rooms} phòng`);

const suggestions = computed(() => {
    const keyword = searchParams.value.keyword.toLowerCase();
    const provinceResults = provinces.value
        .filter(p => p.name.toLowerCase().includes(keyword))
        .map(p => ({ id: `p-${p.id}`, name: p.name, type: 'Province' }));
    const hotelResults = hotelSuggestions.value.map(h => ({ id: `h-${h.id}`, name: h.name, type: 'Hotel' }));
    return [...provinceResults, ...hotelResults];
});

watch(() => searchParams.value.keyword, (newKeyword) => {
    if (newKeyword) {
        locationError.value = '';
    }
    clearTimeout(debounceTimer.value);
    if (newKeyword) {
        debounceTimer.value = setTimeout(() => {
            fetchHotelSuggestions(newKeyword);
        }, 300);
    } else {
        hotelSuggestions.value = [];
    }
});

function handleLocationFocus() {
    locationBeforeEdit.value = searchParams.value.keyword;
    searchParams.value.keyword = '';
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

function calculateCheckoutDisplay(nights) {
    if (!searchParams.value.checkin) return '';
    const date = new Date(searchParams.value.checkin);
    date.setDate(date.getDate() + nights);
    return `Trả phòng: ` + new Intl.DateTimeFormat('vi-VN', { weekday: 'short', day: '2-digit', month: '2-digit' }).format(date);
}

function selectLocation(locationName) {
    searchParams.value.keyword = locationName;
    showLocationDropdown.value = false;
}

function selectNights(nights) {
    searchParams.value.nights = nights;
    showNightsDropdown.value = false;
}

function updateGuests(type, amount) {
    if (errorTimeout.value) clearTimeout(errorTimeout.value);
    guestsError.value = '';

    const params = searchParams.value;

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
    if (!searchParams.value.keyword || !searchParams.value.keyword.trim()) {
        locationError.value = 'Vui lòng nhập địa điểm hoặc tên khách sạn.';
        return;
    }

    localStorage.setItem('lastSearchParams', JSON.stringify({
        location: searchParams.value.keyword,
        checkin: searchParams.value.checkin,
        nights: searchParams.value.nights,
        adults: searchParams.value.adults,
        children: searchParams.value.children,
        rooms: searchParams.value.rooms,
    }));

    isSearchFormActive.value = false;
    showLocationDropdown.value = false;
    showNightsDropdown.value = false;
    showGuestsDropdown.value = false;

    const query = {
        keyword: searchParams.value.keyword,
        checkInDate: searchParams.value.checkin,
        checkOutDate: checkoutDate.value.toISOString().split('T')[0],
        numAdults: searchParams.value.adults,
        numChildren: searchParams.value.children,
        rooms: searchParams.value.rooms,
    };
    router.push({ name: 'HotelListing', query });
}

const handleClickOutside = (event) => {
    setTimeout(() => {
        if (locationContainer.value && !locationContainer.value.contains(event.target)) {
            showLocationDropdown.value = false;
            if (searchParams.value.keyword.trim() === '' && locationBeforeEdit.value) {
                searchParams.value.keyword = locationBeforeEdit.value;
            }
        }
        if (nightsContainer.value && !nightsContainer.value.contains(event.target)) showNightsDropdown.value = false;
        if (guestsContainer.value && !guestsContainer.value.contains(event.target)) showGuestsDropdown.value = false;
    }, 0);
};

onMounted(async () => {
    const savedSearch = localStorage.getItem('lastSearchParams');
    if (savedSearch) {
        const parsedParams = JSON.parse(savedSearch);

        const todayStr = new Date().toISOString().split('T')[0];
        let checkinDate = new Date(parsedParams.checkin || todayStr);
        const todayDate = new Date(todayStr);

        if (checkinDate < todayDate) {
            checkinDate = todayDate;
        }

        searchParams.value.checkin = checkinDate.toISOString().split('T')[0];
        searchParams.value.nights = parsedParams.nights || 1;
        searchParams.value.keyword = parsedParams.location || '';
        searchParams.value.adults = parsedParams.adults || 2;
        searchParams.value.children = parsedParams.children || 0;
        searchParams.value.rooms = parsedParams.rooms || 1;
    }
    try {
        const response = await getAllProvinces();
        provinces.value = response.data?.data || [];
    } catch (error) { console.error("Could not fetch provinces:", error); }
    document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside);
});
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}

.slide-fade-enter-active,
.slide-fade-leave-active {
    transition: all 0.4s ease-out;
}

.slide-fade-enter-from,
.slide-fade-leave-to {
    transform: translateX(100%);
    opacity: 0;
}

input[type=number]::-webkit-inner-spin-button,
input[type=number]::-webkit-outer-spin-button {
    -webkit-appearance: none;
    margin: 0;
}

input[type=number] {
    -moz-appearance: textfield;
    appearance: textfield;
}

input:focus {
    outline: none;
}

input[type="date"]::-webkit-calendar-picker-indicator {
    cursor: pointer;
    opacity: 0.6;
    filter: invert(0.4) sepia(1) saturate(5) hue-rotate(180deg);
}

input[type="date"]::-webkit-calendar-picker-indicator:hover {
    opacity: 1;
}
</style>