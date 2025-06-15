<template>
    <div class="sticky top-16 z-40 w-full rounded-lg border border-gray-200 bg-white shadow-lg p-4">
        <div class="flex items-stretch h-15 border border-gray-300 rounded-lg">
            <div ref="locationContainer"
                class="relative flex flex-grow cursor-pointer items-center p-3 bg-white hover:bg-gray-50 border-r border-gray-200 min-w-[220px] rounded-l-md"
                @click="toggleLocationDropdown">
                <i class="fas fa-map-marker-alt text-blue-500 text-xl pr-3"></i>
                <div class="flex-1">
                    <span class="font-semibold truncate text-gray-800">{{ selectedLocation }}</span>
                </div>
                <ul v-if="showLocationDropdown"
                    class="absolute top-full mt-2 left-0 z-20 w-full bg-white border border-gray-300 rounded-lg shadow-lg max-h-56 overflow-y-auto">
                    <li v-for="location in locations" :key="location" @click.stop="selectLocation(location)"
                        class="px-4 py-2 hover:bg-blue-100 cursor-pointer truncate">
                        {{ location }}
                    </li>
                </ul>
            </div>

            <div
                class="flex flex-grow-[2] items-center p-3 bg-white hover:bg-gray-50 border-r border-gray-200 min-w-[320px]">
                <i class="fas fa-calendar-alt text-blue-500 text-xl pr-3"></i>
                <div class="flex flex-1 items-center">
                    <div class="flex-1">
                        <label class="text-xs text-gray-500">Ngày nhận</label>
                        <input type="date" v-model="checkIn" :min="today"
                            class="w-full bg-transparent font-semibold focus:outline-none" />
                    </div>

                    <div class="px-4 text-center">
                        <div v-if="numberOfNights > 0"
                            class="text-xs font-semibold text-blue-600 bg-blue-50 rounded-full px-2 py-0.5 whitespace-nowrap">
                            {{ numberOfNights }} đêm
                        </div>
                        <div v-else class="text-gray-400">-</div>
                    </div>
                    <div class="flex-1">
                        <label class="text-xs text-gray-500">Ngày trả</label>
                        <input type="date" v-model="checkOut" :min="minCheckOut"
                            class="w-full bg-transparent font-semibold focus:outline-none" />
                    </div>
                </div>
            </div>

            <div class="flex flex-grow items-center p-3 bg-white hover:bg-gray-50 max-w-[430px]">
                <i class="fas fa-users text-blue-500 text-xl pr-3"></i>
                <div class="flex-1">
                    <label class="text-xs text-gray-500">Người lớn</label>
                    <input type="number" v-model.number="adults" min="1"
                        class="w-full bg-transparent font-semibold focus:outline-none" />
                </div>
                <div class="flex-1 ml-2">
                    <label class="text-xs text-gray-500">Trẻ em</label>
                    <input type="number" v-model.number="children" min="0"
                        class="w-full bg-transparent font-semibold focus:outline-none" />
                </div>
            </div>

            <button aria-label="Search"
                class="bg-blue-500 hover:bg-blue-600 text-white px-8 py-3 font-bold transition flex items-center justify-center rounded-r-md"
                @click="onSearch">
                <i class="fas fa-search"></i>
            </button>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';

const locationContainer = ref(null);
const selectedLocation = ref('Hồ Chí Minh');
const showLocationDropdown = ref(false);
const locations = [
    'Hồ Chí Minh', 'Hà Nội', 'Đà Nẵng', 'Nha Trang', 'Cần Thơ', 'Vũng Tàu', 'Huế', 'Hội An',
];
const today = new Date().toISOString().split('T')[0];
const checkIn = ref(today);
const checkOut = ref('');
const adults = ref(1);
const children = ref(0);

const minCheckOut = computed(() => {
    if (checkIn.value) {
        const date = new Date(checkIn.value);
        date.setDate(date.getDate() + 1);
        return date.toISOString().split('T')[0];
    }
    return today;
});

const numberOfNights = computed(() => {
    if (checkIn.value && checkOut.value) {
        const startDate = new Date(checkIn.value);
        const endDate = new Date(checkOut.value);

        if (endDate <= startDate) {
            return 0;
        }

        const diffTime = endDate.getTime() - startDate.getTime();

        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

        return diffDays;
    }
    return 0;
});

function toggleLocationDropdown() {
    showLocationDropdown.value = !showLocationDropdown.value;
}
function selectLocation(location) {
    selectedLocation.value = location;
    showLocationDropdown.value = false;
}
function onSearch() {
    alert(`Tìm kiếm: ${selectedLocation.value}, ${checkIn.value} - ${checkOut.value} (${numberOfNights.value} đêm), ${adults.value} lớn, ${children.value} trẻ em`);
}
const handleClickOutside = (event) => {
    if (locationContainer.value && !locationContainer.value.contains(event.target)) {
        showLocationDropdown.value = false;
    }
};
onMounted(() => {
    document.addEventListener('click', handleClickOutside);
});
onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside);
});
</script>

<style scoped>
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
}

input[type="date"]::-webkit-calendar-picker-indicator:hover {
    opacity: 1;
}
</style>