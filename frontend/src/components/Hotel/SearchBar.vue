<template>
    <div
        class="sticky top-19 bg-white z-10 flex flex-wrap items-center gap-3 border border-gray-300 rounded-full shadow-md px-4 py-4 w-full max-w-[1000px] mx-auto text-base text-gray-800">
        <div class="relative flex-1 min-w-0 max-w-[170px] pl-4">
            <div class="flex items-center justify-between border border-gray-300 rounded-lg px-3 py-2 cursor-pointer hover:bg-gray-50"
                @click="toggleLocationDropdown">
                <div class="flex items-center gap-2 truncate">
                    <i class="fas fa-map-marker-alt text-indigo-500"></i>
                    <span class="font-medium truncate">{{ selectedLocation }}</span>
                </div>
                <i class="fas fa-chevron-down ml-2 text-xs text-gray-500"></i>
            </div>
            <ul v-if="showLocationDropdown"
                class="absolute z-10 mt-1 w-full bg-white border border-gray-300 rounded-lg shadow-lg max-h-56 overflow-y-auto">
                <li v-for="location in locations" :key="location" @click="selectLocation(location)"
                    class="px-4 py-2 hover:bg-gray-100 cursor-pointer truncate">
                    {{ location }}
                </li>
            </ul>
        </div>

        <span class="border-l h-6 border-gray-300 hidden sm:inline-block"></span>

        <div class="flex gap-2 flex-1 min-w-0 max-w-[500px]">
            <div class="flex-1 min-w-0 border border-gray-300 rounded-lg px-3 py-3 flex items-center gap-2">
                <label class="text-gray-600 whitespace-nowrap text-sm">Ngày nhận:</label>
                <input type="date" v-model="checkIn" :min="today"
                    class="flex-1 min-w-0 bg-transparent text-gray-800 focus:ring-2 focus:ring-indigo-400 focus:outline-none text-sm" />
            </div>
            <div class="flex-1 min-w-0 border border-gray-300 rounded-lg px-3 py-3 flex items-center gap-2">
                <label class="text-gray-600 whitespace-nowrap text-sm">Ngày trả:</label>
                <input type="date" v-model="checkOut" :min="minCheckOut"
                    class="flex-1 min-w-0 bg-transparent text-gray-800 focus:ring-2 focus:ring-indigo-400 focus:outline-none text-sm" />
            </div>
        </div>

        <span class="border-l h-6 border-gray-300 hidden sm:inline-block"></span>

        <div class="flex gap-2 flex-1 min-w-0 max-w-[300px]">
            <div class="flex-1 min-w-0 border border-gray-300 rounded-lg px-3 py-3 flex items-center gap-2">
                <label class="text-gray-600 whitespace-nowrap text-sm">Người lớn:</label>
                <input type="number" v-model.number="adults" min="1"
                    class="w-16 bg-transparent text-gray-800 focus:ring-2 focus:ring-indigo-400 focus:outline-none text-sm" />
            </div>
            <div class="flex-1 min-w-0 border border-gray-300 rounded-lg px-3 py-3 flex items-center gap-2">
                <label class="text-gray-600 whitespace-nowrap text-sm">Trẻ em:</label>
                <input type="number" v-model.number="children" min="0"
                    class="w-16 bg-transparent text-gray-800 focus:ring-2 focus:ring-indigo-400 focus:outline-none text-sm" />
            </div>
        </div>

        <button aria-label="Search"
            class="ml-auto bg-indigo-600 hover:bg-indigo-700 text-white rounded-full px-6 py-3 mr-4 transition focus:outline-none focus:ring-2 focus:ring-indigo-500 text-sm"
            @click="onSearch">
            <i class="fas fa-search"></i>
        </button>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const selectedLocation = ref('Hồ Chí Minh');
const showLocationDropdown = ref(false);
const locations = [
    'Hồ Chí Minh',
    'Hà Nội',
    'Đà Nẵng',
    'Nha Trang',
    'Cần Thơ',
    'Vũng Tàu',
    'Huế',
    'Hội An',
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

function toggleLocationDropdown() {
    showLocationDropdown.value = !showLocationDropdown.value;
}

function selectLocation(location) {
    selectedLocation.value = location;
    showLocationDropdown.value = false;
}

function onSearch() {
    alert(`Tìm kiếm:
  - Địa điểm: ${selectedLocation.value}
  - Nhận phòng: ${checkIn.value}
  - Trả phòng: ${checkOut.value}
  - Người lớn: ${adults.value}
  - Trẻ em: ${children.value}`);
}
</script>

<style scoped>
@media (max-width: 640px) {
    .flex-wrap {
        gap: 2px !important;
        padding: 0.5rem !important;
    }

    input,
    button {
        font-size: 0.875rem !important;
    }
}
</style>
