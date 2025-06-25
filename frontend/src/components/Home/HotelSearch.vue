<template>
    <div class="p-4 rounded-lg min-h-[250px]">
        <div class="flex flex-col lg:flex-row items-center gap-2 pt-15 pb-2">

            <div class="w-full lg:w-4/12">
                <div class="relative w-full h-full group">
                    <label
                        class="absolute -top-2.5 left-3 px-1 bg-white text-gray-500 text-xs font-semibold rounded-md z-10">
                        Thành phố, khách sạn, điểm đến
                    </label>
                    <div ref="locationContainer"
                        class="flex items-center bg-white rounded-lg border border-gray-300 overflow-hidden shadow-sm h-16 cursor-pointer">
                        <i class="fas fa-map-marker-alt text-blue-500 text-xl px-4"></i>
                        <div class="py-2 flex-1">
                            <input ref="locationInput" type="text" v-model="hotelSearchParams.location"
                                @focus="handleLocationFocus"
                                class="w-full bg-transparent font-bold text-lg focus:outline-none text-gray-800"
                                placeholder="Tìm kiếm..." />
                        </div>
                    </div>
                    <ul v-if="showLocationDropdown"
                        class="absolute top-full mt-1 z-30 w-full bg-white border border-gray-300 rounded-lg shadow-lg max-h-56 overflow-y-auto">
                        <li v-for="loc in suggestions" :key="loc.type + '-' + loc.id"
                            @click.stop="selectLocation(loc.name)"
                            class="px-4 py-2 hover:bg-blue-100 cursor-pointer flex items-center gap-3">
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

            <div class="w-full lg:w-4/12">
                <div class="relative w-full h-full group">
                    <label
                        class="absolute -top-2.5 left-3 px-1 bg-white text-gray-500 text-xs font-semibold rounded-md z-10">
                        Ngày nhận - trả phòng
                    </label>
                    <div ref="dateContainer"
                        class="flex items-center bg-white rounded-lg border border-gray-300 overflow-hidden shadow-sm h-16">
                        <i class="far fa-calendar-alt text-blue-500 text-xl px-4"></i>
                        <div class="flex-1 cursor-pointer py-2 h-full flex flex-col justify-center"
                            @click="openCalendar('checkin')">
                            <p class="text-gray-900 font-bold text-lg">{{ formatDate(hotelSearchParams.checkin) }}</p>
                            <p class="text-gray-500 text-sm">{{ getDayOfWeek(hotelSearchParams.checkin) }}</p>
                        </div>
                        <div class="px-3 text-center">
                            <div v-if="numberOfNights > 0"
                                class="text-sm font-semibold text-blue-600 bg-blue-100 rounded-full px-3 py-1 whitespace-nowrap">
                                {{ numberOfNights }} đêm
                            </div>
                        </div>
                        <div class="flex-1 cursor-pointer py-2 h-full flex flex-col justify-center text-left"
                            @click="openCalendar('checkout')">
                            <p class="text-gray-900 font-bold text-lg">{{ formatDate(hotelSearchParams.checkout) }}</p>
                            <p class="text-gray-500 text-sm">{{ getDayOfWeek(hotelSearchParams.checkout) }}</p>
                        </div>
                    </div>

                    <Teleport to="body">
                        <div v-if="calendarState.visible" ref="calendarDropdown" :style="calendarPosition"
                            class="z-50 bg-white rounded-lg shadow-xl border border-gray-200 p-4 w-[320px] max-h-[400px] overflow-y-auto">
                            <div class="flex justify-between items-center mb-3">
                                <button @click="prevMonth"
                                    class="w-8 h-8 flex items-center justify-center rounded-full hover:bg-gray-100">&lt;</button>
                                <span class="font-semibold text-lg">{{ monthYearLabel }}</span>
                                <button @click="nextMonth"
                                    class="w-8 h-8 flex items-center justify-center rounded-full hover:bg-gray-100">&gt;</button>
                            </div>
                            <div class="grid grid-cols-7 gap-1 text-center text-sm">
                                <div v-for="day in weekdays" :key="day" class="font-semibold text-gray-500 p-1">{{ day
                                    }}</div>
                                <div v-for="day in days" :key="day.date.toISOString()" @click="selectDate(day)" :class="[
                                    'p-1 rounded-full cursor-pointer flex items-center justify-center w-9 h-9 mx-auto',
                                    day.isCurrentMonth ? 'text-gray-800' : 'text-gray-300',
                                    day.isDisabled ? 'text-gray-300 cursor-not-allowed opacity-50' : 'hover:bg-blue-100',
                                    day.isSelected ? 'bg-blue-600 text-white font-bold' : ''
                                ]">
                                    {{ day.date.getDate() }}
                                </div>
                            </div>
                        </div>
                    </Teleport>
                </div>
            </div>

            <div class="w-full lg:w-3/12">
                <div class="relative w-full h-full group">
                    <label
                        class="absolute -top-2.5 left-3 px-1 bg-white text-gray-500 text-xs font-semibold rounded-md z-10">
                        Khách
                    </label>
                    <div ref="guestsContainer" @click="showGuestsDropdown = !showGuestsDropdown"
                        class="flex items-center bg-white rounded-lg border border-gray-300 overflow-hidden shadow-sm h-16 cursor-pointer">
                        <i class="fas fa-users text-blue-500 text-xl px-4"></i>
                        <div class="py-2 flex-1 overflow-hidden">
                            <p class="text-gray-900 font-bold text-lg truncate">{{ hotelSearchParams.adults }} người lớn
                            </p>
                            <p class="text-gray-500 text-sm truncate">{{ hotelSearchParams.children }} trẻ em, {{
                                hotelSearchParams.rooms }} phòng</p>
                        </div>
                    </div>
                    <div v-if="showGuestsDropdown" ref="guestsDropdown"
                        class="absolute top-full mt-1 right-0 z-30 w-[320px] bg-white rounded-lg shadow-lg border border-gray-200 p-4">
                        <div class="flex items-center gap-3 mb-4">
                            <i class="fas fa-user text-lg text-gray-600 w-5 text-center"></i>
                            <span class="text-gray-900 font-semibold text-base">Người lớn</span>
                            <div class="ml-auto flex items-center gap-2">
                                <button @click.stop="updateGuests('adults', -1)"
                                    :disabled="hotelSearchParams.adults <= 1"
                                    class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 disabled:text-gray-300 flex items-center justify-center hover:bg-gray-50"
                                    type="button">−</button>
                                <span class="w-8 h-8 flex items-center justify-center font-semibold">{{
                                    hotelSearchParams.adults }}</span>
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
                                    :disabled="hotelSearchParams.children <= 0"
                                    class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 disabled:text-gray-300 flex items-center justify-center hover:bg-gray-50"
                                    type="button">−</button>
                                <span class="w-8 h-8 flex items-center justify-center font-semibold">{{
                                    hotelSearchParams.children }}</span>
                                <button @click.stop="updateGuests('children', 1)"
                                    class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 flex items-center justify-center hover:bg-gray-50"
                                    type="button">+</button>
                            </div>
                        </div>
                        <div class="flex items-center gap-3">
                            <i class="fas fa-bed text-lg text-gray-600 w-5 text-center"></i>
                            <span class="text-gray-900 font-semibold text-base">Phòng</span>
                            <div class="ml-auto flex items-center gap-2">
                                <button @click.stop="updateGuests('rooms', -1)" :disabled="hotelSearchParams.rooms <= 1"
                                    class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 disabled:text-gray-300 flex items-center justify-center hover:bg-gray-50"
                                    type="button">−</button>
                                <span class="w-8 h-8 flex items-center justify-center font-semibold">{{
                                    hotelSearchParams.rooms }}</span>
                                <button @click.stop="updateGuests('rooms', 1)"
                                    class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 flex items-center justify-center hover:bg-gray-50"
                                    type="button">+</button>
                            </div>
                        </div>
                        <p v-if="guestsError" class="text-red-500 text-xs mt-3 text-center">{{ guestsError }}</p>
                    </div>
                </div>
            </div>

            <div class="w-full lg:w-3/12 flex items-center justify-center pt-0">
                <button @click="onSearch" :disabled="isSearching"
                    class="w-full h-16 bg-orange-500 hover:bg-orange-600 transition-colors text-white font-bold text-xl rounded-lg shadow-lg flex items-center justify-center gap-3"
                    :class="{ 'bg-gray-400 hover:bg-gray-400 cursor-not-allowed': isSearching }">
                    <div v-if="isSearching" class="flex items-center justify-center space-x-1">
                        <span class="w-2.5 h-2.5 bg-white rounded-full animate-bounce [animation-delay:-0.3s]"></span>
                        <span class="w-2.5 h-2.5 bg-white rounded-full animate-bounce [animation-delay:-0.15s]"></span>
                        <span class="w-2.5 h-2.5 bg-white rounded-full animate-bounce"></span>
                    </div>
                    <div v-else class="flex items-center justify-center gap-3">
                        <i class="fas fa-search"></i>
                        <span>Tìm kiếm</span>
                    </div>
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { getAllProvinces } from '@/api/provinceApi';
import { searchHotels } from '@/api/hotelApi';

const router = useRouter();

const locationContainer = ref(null);
const locationInput = ref(null);
const guestsContainer = ref(null);
const dateContainer = ref(null);
const guestsDropdown = ref(null);
const calendarDropdown = ref(null);

const showLocationDropdown = ref(false);
const showGuestsDropdown = ref(false);
const guestsError = ref('');
const today = new Date().toISOString().split('T')[0];
const isSearching = ref(false);
const provinces = ref([]);
const hotelSuggestions = ref([]);
const debounceTimer = ref(null);

const hotelSearchParams = reactive({
    location: '',
    checkin: today,
    checkout: new Date(new Date().setDate(new Date().getDate() + 1)).toISOString().split('T')[0],
    adults: 2,
    children: 0,
    rooms: 1,
});

const lastSelectedLocation = ref('');

const suggestions = computed(() => {
    const keyword = hotelSearchParams.location.toLowerCase();
    const provinceResults = provinces.value
        .filter(p => p.name.toLowerCase().includes(keyword))
        .map(p => ({ id: `p-${p.id}`, name: p.name, type: 'Province' }));
    const hotelResults = hotelSuggestions.value.map(h => ({ id: `h-${h.id}`, name: h.name, type: 'Hotel' }));
    return [...provinceResults, ...hotelResults];
});

const numberOfNights = computed(() => {
    if (!hotelSearchParams.checkin || !hotelSearchParams.checkout) return 0;
    const start = new Date(hotelSearchParams.checkin);
    const end = new Date(hotelSearchParams.checkout);
    if (end <= start) return 0;
    const diff = end.getTime() - start.getTime();
    return Math.ceil(diff / (1000 * 60 * 60 * 24));
});

async function onSearch() {
    if (!hotelSearchParams.location.trim() && lastSelectedLocation.value) {
        hotelSearchParams.location = lastSelectedLocation.value;
    }
    if (!hotelSearchParams.location.trim()) {
        return;
    }

    isSearching.value = true;
    localStorage.setItem('lastSearchParams', JSON.stringify(hotelSearchParams));

    const query = {
        keyword: hotelSearchParams.location,
        checkInDate: hotelSearchParams.checkin,
        checkOutDate: hotelSearchParams.checkout,
        numAdults: hotelSearchParams.adults,
        numChildren: hotelSearchParams.children,
        rooms: hotelSearchParams.rooms,
    };

    try {
        await router.push({ name: 'HotelListing', query });
    } catch (error) {
        console.error("Navigation failed:", error);
    } finally {
        isSearching.value = false;
    }
}

function selectLocation(location) {
    hotelSearchParams.location = location;
    lastSelectedLocation.value = location;
    showLocationDropdown.value = false;
    hotelSuggestions.value = [];
}

function handleLocationFocus() {
    lastSelectedLocation.value = hotelSearchParams.location;
    hotelSearchParams.location = '';
    showLocationDropdown.value = true;
}

const calendarState = reactive({
    visible: false,
    target: 'checkin',
    viewDate: new Date(),
});
const calendarPosition = ref({});
const weekdays = ['T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'CN'];
const monthYearLabel = computed(() => calendarState.viewDate.toLocaleString('vi-VN', { month: 'long', year: 'numeric', timeZone: 'UTC' }));

const minCheckOut = computed(() => {
    const nextDay = new Date(hotelSearchParams.checkin);
    nextDay.setUTCDate(nextDay.getUTCDate() + 1);
    return nextDay.toISOString().split('T')[0];
});

const days = computed(() => {
    const year = calendarState.viewDate.getUTCFullYear();
    const month = calendarState.viewDate.getUTCMonth();
    const firstDayOfMonth = new Date(Date.UTC(year, month, 1));
    const lastDayOfMonth = new Date(Date.UTC(year, month + 1, 0));
    const daysArray = [];
    const minDateUTC = new Date(today);
    minDateUTC.setUTCHours(0, 0, 0, 0);
    const minCheckoutDate = new Date(minCheckOut.value);
    minCheckoutDate.setUTCHours(0, 0, 0, 0);
    const minSelectableDate = calendarState.target === 'checkout' ? minCheckoutDate : minDateUTC;
    const selectedDateUTC = new Date(hotelSearchParams[calendarState.target]);
    selectedDateUTC.setUTCHours(0, 0, 0, 0);
    const startDayOfWeek = (firstDayOfMonth.getUTCDay() + 6) % 7;
    for (let i = startDayOfWeek; i > 0; i--) {
        daysArray.push({ date: new Date(Date.UTC(year, month, 1 - i)), isCurrentMonth: false, isDisabled: true });
    }
    for (let i = 1; i <= lastDayOfMonth.getUTCDate(); i++) {
        const date = new Date(Date.UTC(year, month, i));
        const isDisabled = date < minSelectableDate;
        const isSelected = date.getTime() === selectedDateUTC.getTime();
        daysArray.push({ date, isCurrentMonth: true, isDisabled, isSelected });
    }
    const endDayOfWeek = (lastDayOfMonth.getUTCDay() + 6) % 7;
    for (let i = 1; i < 7 - endDayOfWeek; i++) {
        daysArray.push({ date: new Date(Date.UTC(year, month + 1, i)), isCurrentMonth: false, isDisabled: true });
    }
    return daysArray;
});

function prevMonth() {
    calendarState.viewDate.setUTCMonth(calendarState.viewDate.getUTCMonth() - 1);
    calendarState.viewDate = new Date(calendarState.viewDate);
}
function nextMonth() {
    calendarState.viewDate.setUTCMonth(calendarState.viewDate.getUTCMonth() + 1);
    calendarState.viewDate = new Date(calendarState.viewDate);
}

function openCalendar(target) {
    if (!dateContainer.value) return;

    const rect = dateContainer.value.getBoundingClientRect();
    calendarPosition.value = {
        position: 'absolute',
        top: `${rect.bottom + window.scrollY + 4}px`,
        left: `${rect.left + window.scrollX}px`,
    };

    calendarState.target = target;
    calendarState.viewDate = new Date(hotelSearchParams[target]);
    calendarState.visible = true;
}

function selectDate(day) {
    if (!day.isDisabled) {
        const selected = day.date;
        hotelSearchParams[calendarState.target] = selected.toISOString().split('T')[0];
        calendarState.visible = false;
    }
}
function formatDate(dateString) {
    if (!dateString) return '';
    const options = { day: '2-digit', month: '2-digit', year: 'numeric', timeZone: 'UTC' };
    return new Date(dateString).toLocaleDateString('vi-VN', options);
}
function getDayOfWeek(dateString) {
    if (!dateString) return '';
    const options = { weekday: 'long', timeZone: 'UTC' };
    return new Date(dateString).toLocaleDateString('vi-VN', options);
}

function updateGuests(type, amount) {
    guestsError.value = '';
    const params = hotelSearchParams;
    if (type === 'adults') {
        const newAdults = params.adults + amount;
        if (newAdults >= 1) {
            params.adults = newAdults;
            if (params.adults < params.rooms) params.rooms = params.adults;
        }
    } else if (type === 'children') {
        params.children = Math.max(0, params.children + amount);
    } else if (type === 'rooms') {
        const newRooms = params.rooms + amount;
        if (newRooms > params.adults) {
            guestsError.value = 'Số phòng không thể nhiều hơn số người lớn.';
            setTimeout(() => { guestsError.value = '' }, 3000);
        } else {
            params.rooms = Math.max(1, newRooms);
        }
    }
}

watch(() => hotelSearchParams.checkin, (newVal) => {
    const checkinDate = new Date(newVal);
    const checkoutDate = new Date(hotelSearchParams.checkout);
    if (checkoutDate <= checkinDate) {
        const nextDay = new Date(checkinDate);
        nextDay.setDate(nextDay.getDate() + 1);
        hotelSearchParams.checkout = nextDay.toISOString().split('T')[0];
    }
}, { immediate: true });

const handleClickOutside = (event) => {
    if (locationContainer.value && !locationContainer.value.contains(event.target)) {
        showLocationDropdown.value = false;
        if (hotelSearchParams.location === '') {
            hotelSearchParams.location = lastSelectedLocation.value;
        }
    }
    if (
        guestsContainer.value && !guestsContainer.value.contains(event.target) &&
        (!guestsDropdown.value || !guestsDropdown.value.contains(event.target))
    ) {
        showGuestsDropdown.value = false;
    }
    if (
        calendarState.visible &&
        dateContainer.value && !dateContainer.value.contains(event.target) &&
        (!calendarDropdown.value || !calendarDropdown.value.contains(event.target))
    ) {
        calendarState.visible = false;
    }
};

watch(() => hotelSearchParams.location, (newKeyword) => {
    if (!showLocationDropdown.value || !newKeyword) {
        hotelSuggestions.value = [];
        return;
    }
    clearTimeout(debounceTimer.value);
    debounceTimer.value = setTimeout(() => {
        fetchHotelSuggestions(newKeyword);
    }, 300);
});

const fetchHotelSuggestions = async (keyword) => {
    try {
        const response = await searchHotels({ keyword: keyword, size: 5 });
        if (response.data?.statusCode === 200) {
            hotelSuggestions.value = response.data.data.content;
        }
    } catch (error) { console.error("Could not fetch hotel suggestions:", error); }
};

onMounted(async () => {
    const savedSearch = localStorage.getItem('lastSearchParams');
    if (savedSearch) {
        Object.assign(hotelSearchParams, JSON.parse(savedSearch));
        lastSelectedLocation.value = hotelSearchParams.location;
    } else {
        hotelSearchParams.location = 'Hồ Chí Minh';
        lastSelectedLocation.value = 'Hồ Chí Minh';
    }

    try {
        const response = await getAllProvinces();
        provinces.value = response.data?.data || [];
    } catch (error) {
        console.error("Could not fetch provinces:", error);
    }
    document.addEventListener('click', handleClickOutside, true);
});

onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside, true);
});

</script>