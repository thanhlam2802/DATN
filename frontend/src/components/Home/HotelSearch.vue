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
                        <li v-for="loc in filteredLocations" :key="loc" @click.stop="selectLocation(loc)"
                            class="px-4 py-3 hover:bg-blue-100 cursor-pointer truncate font-semibold">
                            {{ loc }}
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
                <button @click="onSearch"
                    class="w-full h-16 bg-orange-500 hover:bg-orange-600 transition-colors text-white font-bold text-xl rounded-lg shadow-lg flex items-center justify-center gap-3">
                    <i class="fas fa-search"></i>
                    <span>Tìm kiếm</span>
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// Refs for DOM elements
const locationContainer = ref(null);
const locationInput = ref(null);
const guestsContainer = ref(null);
const dateContainer = ref(null);
const guestsDropdown = ref(null);
const calendarDropdown = ref(null); // Ref for the calendar itself

// Component State
const showLocationDropdown = ref(false);
const showGuestsDropdown = ref(false);
const guestsError = ref('');
const today = new Date().toISOString().split('T')[0];

// Search Parameters
const hotelSearchParams = reactive({
    location: '',
    checkin: today,
    checkout: new Date(new Date().setDate(new Date().getDate() + 1)).toISOString().split('T')[0],
    adults: 2,
    children: 0,
    rooms: 1,
});

// Location Logic
const lastSelectedLocation = ref('');
const allLocations = ['Hồ Chí Minh', 'Hà Nội', 'Đà Nẵng', 'Nha Trang', 'Cần Thơ', 'Vũng Tàu', 'Huế', 'Hội An', 'Phú Quốc', 'Đà Lạt', 'Quy Nhơn', 'Phan Thiết'];

const filteredLocations = computed(() => {
    if (!hotelSearchParams.location) return allLocations;
    return allLocations.filter(loc =>
        loc.toLowerCase().includes(hotelSearchParams.location.toLowerCase())
    );
});

// Computed property for number of nights
const numberOfNights = computed(() => {
    if (!hotelSearchParams.checkin || !hotelSearchParams.checkout) return 0;
    const start = new Date(hotelSearchParams.checkin);
    const end = new Date(hotelSearchParams.checkout);
    if (end <= start) return 0;
    const diff = end.getTime() - start.getTime();
    return Math.ceil(diff / (1000 * 60 * 60 * 24));
});

// --- SEARCH & LOCATION FUNCTIONS ---
function onSearch() {
    if (!hotelSearchParams.location && lastSelectedLocation.value) {
        hotelSearchParams.location = lastSelectedLocation.value;
    }
    sessionStorage.setItem('lastSearchLocation', hotelSearchParams.location);
    router.push({
        name: 'HotelListing',
        query: { ...hotelSearchParams }
    });
}

function selectLocation(location) {
    hotelSearchParams.location = location;
    lastSelectedLocation.value = location;
    showLocationDropdown.value = false;
}

function handleLocationFocus() {
    hotelSearchParams.location = '';
    showLocationDropdown.value = true;
}


// --- CALENDAR LOGIC (WITH TELEPORT) ---
const calendarState = reactive({
    visible: false,
    target: 'checkin',
    viewDate: new Date(),
});
const calendarPosition = ref({}); // Store calendar position dynamically
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

// MODIFIED: Function to open calendar and calculate its position
function openCalendar(target) {
    if (!dateContainer.value) return;

    const rect = dateContainer.value.getBoundingClientRect();
    calendarPosition.value = {
        position: 'absolute',
        top: `${rect.bottom + window.scrollY + 4}px`, // position below the input + 4px margin
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

// --- GUESTS LOGIC ---
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

// --- WATCHERS & LIFECYCLE ---
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
    // Close location dropdown
    if (locationContainer.value && !locationContainer.value.contains(event.target)) {
        showLocationDropdown.value = false;
        if (hotelSearchParams.location === '') {
            hotelSearchParams.location = lastSelectedLocation.value;
        }
    }
    // Close guests dropdown
    if (
        guestsContainer.value && !guestsContainer.value.contains(event.target) &&
        (!guestsDropdown.value || !guestsDropdown.value.contains(event.target))
    ) {
        showGuestsDropdown.value = false;
    }
    // Close calendar
    if (
        calendarState.visible &&
        dateContainer.value && !dateContainer.value.contains(event.target) &&
        (!calendarDropdown.value || !calendarDropdown.value.contains(event.target))
    ) {
        calendarState.visible = false;
    }
};

onMounted(() => {
    const savedLocation = sessionStorage.getItem('lastSearchLocation');
    if (savedLocation) {
        hotelSearchParams.location = savedLocation;
        lastSelectedLocation.value = savedLocation;
    } else {
        hotelSearchParams.location = 'Hồ Chí Minh';
        lastSelectedLocation.value = 'Hồ Chí Minh';
    }
    document.addEventListener('click', handleClickOutside, true);
});

onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside, true);
});

</script>