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

    <section class="relative w-full">
        <img alt="Thành phố Dubai về đêm" class="w-full h-72 md:h-96 object-cover"
            src="https://cdn.pixabay.com/photo/2020/03/04/21/18/dubai-4902764_1280.jpg" />
    </section>

    <form aria-label="Hotel search form" @submit.prevent="onSearch" @click="isSearchFormActive = true"
        class="bg-white rounded-md shadow-md p-6 space-y-6 max-w-[900px] mx-auto mt-[-60px] relative z-10">
        <button class="flex items-center gap-2 text-[#0072c6] font-semibold text-sm" type="button">
            <i class="fas fa-history"></i>
            <span>Khách sạn xem gần đây</span>
        </button>

        <div class="relative" ref="locationContainer">
            <label class="block mb-1 text-xs font-normal text-[#4a4a4a]" for="location">Thành phố, địa điểm hoặc
                tên
                khách sạn:</label>
            <div class="flex items-center gap-2 border border-gray-300 rounded-md px-3 py-2 text-sm text-[#4a4a4a]">
                <i class="fas fa-map-marker-alt text-[#0072c6]"></i>
                <input class="w-full bg-transparent outline-none font-semibold" id="location" type="text"
                    v-model="selectedLocation" placeholder="Nhập thành phố hoặc tên khách sạn"
                    @focus="handleLocationFocus" />
            </div>
            <div v-if="showLocationDropdown && filteredLocations.length > 0"
                class="absolute top-full mt-2 w-full max-h-60 overflow-y-auto bg-white border border-gray-200 rounded-md shadow-lg z-20">
                <ul>
                    <li v-for="loc in filteredLocations" :key="loc" @click="selectLocation(loc)"
                        class="px-4 py-2 hover:bg-gray-100 cursor-pointer">
                        {{ loc }}
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
                        v-model="checkInDate" :min="today" />
                </div>
            </div>

            <div class="relative" ref="nightsContainer">
                <label class="block mb-1 text-xs font-normal text-[#4a4a4a]">Số đêm:</label>
                <button @click="showNightsDropdown = !showNightsDropdown" type="button"
                    class="flex items-center justify-between w-full gap-2 border border-gray-300 rounded-md px-3 py-2 text-sm text-[#4a4a4a] cursor-pointer">
                    <div class="flex items-center gap-2">
                        <i class="fas fa-moon text-[#0072c6]"></i>
                        <span class="font-semibold">{{ selectedNights }} đêm</span>
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
                    class="flex items-center w-full gap-2 border border-gray-300 rounded-md px-3 py-2 text-sm text-[#4a4a4a] cursor-pointer">
                    <i class="fas fa-user-friends text-[#0072c6]"></i>
                    <span class="font-semibold">{{ guestsDisplay }}</span>
                </button>
                <div v-if="showGuestsDropdown" aria-label="Guest and room selection"
                    class="absolute z-40 mt-2 w-full bg-white rounded-lg shadow-lg border border-gray-200 p-4"
                    role="listbox">
                    <div class="flex items-center gap-3 mb-4">
                        <i class="fas fa-user text-lg text-gray-600 w-5 text-center"></i>
                        <span class="text-gray-900 font-semibold text-base">Người lớn</span>
                        <div class="ml-auto flex items-center gap-2">
                            <button @click="updateGuests('adults', -1)" :disabled="adults <= 1"
                                aria-label="Decrease Người lớn"
                                class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 disabled:text-gray-300 disabled:border-gray-200 disabled:bg-gray-50 flex items-center justify-center hover:bg-gray-50"
                                type="button">−</button>
                            <span
                                class="w-8 h-8 flex items-center justify-center text-gray-900 font-semibold text-base select-none">{{
                                    adults }}</span>
                            <button @click="updateGuests('adults', 1)" aria-label="Increase Người lớn"
                                class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 flex items-center justify-center hover:bg-gray-50"
                                type="button">+</button>
                        </div>
                    </div>
                    <div class="flex items-center gap-3 mb-4">
                        <i class="fas fa-child text-lg text-gray-600 w-5 text-center"></i>
                        <span class="text-gray-900 font-semibold text-base">Trẻ em</span>
                        <div class="ml-auto flex items-center gap-2">
                            <button @click="updateGuests('children', -1)" :disabled="children <= 0"
                                aria-label="Decrease Trẻ em"
                                class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 disabled:text-gray-300 disabled:border-gray-200 disabled:bg-gray-50 flex items-center justify-center hover:bg-gray-50"
                                type="button">−</button>
                            <span
                                class="w-8 h-8 flex items-center justify-center text-gray-900 font-semibold text-base select-none">{{
                                    children }}</span>
                            <button @click="updateGuests('children', 1)" aria-label="Increase Trẻ em"
                                class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 flex items-center justify-center hover:bg-gray-50"
                                type="button">+</button>
                        </div>
                    </div>
                    <div class="flex items-center gap-3">
                        <i class="fas fa-bed text-lg text-gray-600 w-5 text-center"></i>
                        <span class="text-gray-900 font-semibold text-base">Phòng</span>
                        <div class="ml-auto flex items-center gap-2">
                            <button @click="updateGuests('rooms', -1)" :disabled="rooms <= 1"
                                aria-label="Decrease Phòng"
                                class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 disabled:text-gray-300 disabled:border-gray-200 disabled:bg-gray-50 flex items-center justify-center hover:bg-gray-50"
                                type="button">−</button>
                            <span
                                class="w-8 h-8 flex items-center justify-center text-gray-900 font-semibold text-base select-none">{{
                                    rooms }}</span>
                            <button @click="updateGuests('rooms', 1)" aria-label="Increase Phòng"
                                class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 flex items-center justify-center hover:bg-gray-50"
                                type="button">+</button>
                        </div>
                    </div>
                    <div class="text-right pt-4 mt-4 border-t border-gray-100">
                        <button @click="showGuestsDropdown = false"
                            class="text-blue-600 font-semibold text-base focus:outline-none hover:underline"
                            type="button">Xong</button>
                    </div>
                </div>
            </div>

            <button
                class="h-10 w-full md:w-auto bg-[#ff5c00] hover:bg-[#e04e00] text-white font-semibold text-lg rounded-md px-12 flex items-center justify-center gap-2"
                type="submit">
                <i class="fas fa-search"></i>
                <span>Tìm</span>
            </button>
        </div>
    </form>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, defineExpose } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const locationContainer = ref(null);
const nightsContainer = ref(null);
const guestsContainer = ref(null);

const selectedLocation = ref('');
const lastSelectedLocation = ref('');
const showLocationDropdown = ref(false);
const showNightsDropdown = ref(false);
const showGuestsDropdown = ref(false);

const locations = ['Hồ Chí Minh', 'Hà Nội', 'Đà Nẵng', 'Nha Trang', 'Cần Thơ', 'Vũng Tàu', 'Huế', 'Hội An', 'Phú Quốc', 'Đà Lạt'];
const today = new Date().toISOString().split('T')[0];

const checkInDate = ref(today);
const selectedNights = ref(1);
const adults = ref(2);
const children = ref(0);
const rooms = ref(1);

const guestsError = ref('');
const errorTimeout = ref(null);

const isSearchFormActive = ref(false);

async function fetchUserLocation() {
    try {
        const response = await fetch('http://ip-api.com/json/');
        if (!response.ok) throw new Error('Network response was not ok.');

        const data = await response.json();
        let matchedCity = 'Hồ Chí Minh';

        if (data && data.status === 'success' && data.city) {
            const userCity = data.city.toLowerCase();
            if (userCity.includes('ho chi minh')) matchedCity = 'Hồ Chí Minh';
            else if (userCity.includes('hanoi')) matchedCity = 'Hà Nội';
            else if (userCity.includes('da nang')) matchedCity = 'Đà Nẵng';
            else matchedCity = data.city;
        }

        selectedLocation.value = matchedCity;
        lastSelectedLocation.value = matchedCity;
    } catch (error) {
        console.error("Could not fetch location:", error);
        selectedLocation.value = 'Hồ Chí Minh';
        lastSelectedLocation.value = 'Hồ Chí Minh';
    }
}

function handleLocationFocus() {
    selectedLocation.value = '';
    showLocationDropdown.value = true;
}

function formatDate(date) {
    if (!date) return '';
    const userTimezoneOffset = date.getTimezoneOffset() * 60000;
    const adjustedDate = new Date(date.getTime() + userTimezoneOffset);
    return new Intl.DateTimeFormat('vi-VN', {
        weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
    }).format(adjustedDate);
}

const filteredLocations = computed(() => {
    if (!selectedLocation.value) return locations;
    return locations.filter(location =>
        location.toLowerCase().includes(selectedLocation.value.toLowerCase())
    );
});

const checkOutDate = computed(() => {
    if (!checkInDate.value) return null;
    const date = new Date(checkInDate.value);
    date.setDate(date.getDate() + selectedNights.value);
    return date;
});

const checkOutDateFormatted = computed(() => {
    if (!checkOutDate.value) return "Vui lòng chọn ngày nhận phòng";
    return formatDate(checkOutDate.value);
});

const calculateCheckoutDisplay = (nights) => {
    if (!checkInDate.value) return '';
    const date = new Date(checkInDate.value);
    date.setDate(date.getDate() + nights);
    return formatDate(date);
};

const guestsDisplay = computed(() => {
    return `${adults.value} người lớn, ${children.value} Trẻ em, ${rooms.value} phòng`;
});

function selectLocation(location) {
    selectedLocation.value = location;
    lastSelectedLocation.value = location;
    showLocationDropdown.value = false;
    sessionStorage.setItem('lastSearchLocation', location);
}

function selectNights(nights) {
    selectedNights.value = nights;
    showNightsDropdown.value = false;
}

function updateGuests(type, amount) {
    if (errorTimeout.value) clearTimeout(errorTimeout.value);
    guestsError.value = '';

    if (type === 'adults') {
        const newAdults = adults.value + amount;
        if (newAdults >= 1) {
            adults.value = newAdults;
            if (adults.value < rooms.value) {
                rooms.value = adults.value;
            }
        }
    } else if (type === 'children') {
        children.value = Math.max(0, children.value + amount);
    } else if (type === 'rooms') {
        const newRooms = rooms.value + amount;
        if (amount > 0) {
            if (newRooms > adults.value) {
                guestsError.value = 'Số phòng không thể nhiều hơn số người lớn.';
                errorTimeout.value = setTimeout(() => {
                    guestsError.value = '';
                }, 3000);
            } else {
                rooms.value = newRooms;
            }
        } else {
            rooms.value = Math.max(1, newRooms);
        }
    }
}

function onSearch() {
    isSearchFormActive.value = false;
    showLocationDropdown.value = false;
    showNightsDropdown.value = false;
    showGuestsDropdown.value = false;

    if (!selectedLocation.value && lastSelectedLocation.value) {
        selectedLocation.value = lastSelectedLocation.value;
    }

    sessionStorage.setItem('lastSearchLocation', selectedLocation.value);

    const checkoutDateValue = checkOutDate.value.toISOString().split('T')[0];

    router.push({
        name: 'HotelListing',
        query: {
            location: selectedLocation.value,
            checkin: checkInDate.value,
            checkout: checkoutDateValue,
            adults: adults.value,
            children: children.value,
            rooms: rooms.value,
        }
    });
}

const handleClickOutside = (event) => {
    if (locationContainer.value && !locationContainer.value.contains(event.target)) {
        if (showLocationDropdown.value) {
            showLocationDropdown.value = false;
            if (selectedLocation.value === '') {
                selectedLocation.value = lastSelectedLocation.value;
            }
        }
    }
    if (nightsContainer.value && !nightsContainer.value.contains(event.target)) {
        showNightsDropdown.value = false;
    }
    if (guestsContainer.value && !guestsContainer.value.contains(event.target)) {
        showGuestsDropdown.value = false;
    }
};

onMounted(() => {
    const savedLocation = sessionStorage.getItem('lastSearchLocation');
    if (savedLocation) {
        selectedLocation.value = savedLocation;
        lastSelectedLocation.value = savedLocation;
    } else {
        fetchUserLocation();
    }
    document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside);
});

const getSearchData = () => {
    return {
        checkin: checkInDate.value,
        checkout: checkOutDate.value,
        adults: adults.value,
        children: children.value,
        rooms: rooms.value
    };
};

defineExpose({
    getSearchData
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