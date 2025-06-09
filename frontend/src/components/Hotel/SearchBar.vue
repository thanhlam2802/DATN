<template>
    <div
        class="flex flex-wrap gap-3 bg-white border border-gray-300 rounded-full shadow-md px-6 py-5 w-full max-w-7xl mx-auto text-base text-gray-800">
        <div class="relative flex-1 min-w-[180px]">
            <div class="border border-gray-300 rounded-lg px-4 py-3 flex items-center justify-between cursor-pointer hover:bg-gray-50"
                @click="toggleLocationDropdown">
                <div class="flex items-center gap-2 truncate">
                    <i class="fas fa-map-marker-alt text-indigo-500"></i>
                    <span class="font-medium truncate">{{ selectedLocation }}</span>
                </div>
                <i class="fas fa-chevron-down ml-2 text-xs text-gray-500"></i>
            </div>
            <ul v-if="showLocationDropdown"
                class="absolute z-10 mt-1 w-full bg-white border border-gray-300 rounded-lg shadow-lg max-h-60 overflow-y-auto">
                <li v-for="location in locations" :key="location" @click="selectLocation(location)"
                    class="px-4 py-2 hover:bg-gray-100 cursor-pointer">
                    {{ location }}
                </li>
            </ul>
        </div>

        <span class="border-l h-6 border-gray-300 hidden sm:inline-block"></span>

        <div class="flex gap-2 flex-wrap sm:flex-nowrap">
            <div class="border border-gray-300 rounded-lg px-4 py-2 flex items-center gap-2">
                <label class="text-gray-600 whitespace-nowrap">Nhận phòng:</label>
                <input type="date" v-model="checkIn" :min="today"
                    class="flex-1 bg-transparent text-gray-800 focus:ring-2 focus:ring-indigo-400 focus:outline-none" />
            </div>
            <div class="border border-gray-300 rounded-lg px-4 py-2 flex items-center gap-2">
                <label class="text-gray-600 whitespace-nowrap">Trả phòng:</label>
                <input type="date" v-model="checkOut" :min="minCheckOut"
                    class="flex-1 bg-transparent text-gray-800 focus:ring-2 focus:ring-indigo-400 focus:outline-none" />
            </div>
        </div>

        <span class="border-l h-6 border-gray-300 hidden sm:inline-block"></span>

        <div class="flex gap-2 flex-wrap sm:flex-nowrap">
            <div class="border border-gray-300 rounded-lg px-4 py-2 flex items-center gap-2">
                <label class="text-gray-600 whitespace-nowrap">Người lớn:</label>
                <input type="number" v-model="adults" min="1"
                    class="w-16 bg-transparent text-gray-800 focus:ring-2 focus:ring-indigo-400 focus:outline-none" />
            </div>
            <div class="border border-gray-300 rounded-lg px-4 py-2 flex items-center gap-2">
                <label class="text-gray-600 whitespace-nowrap">Trẻ em:</label>
                <input type="number" v-model="children" min="0"
                    class="w-16 bg-transparent text-gray-800 focus:ring-2 focus:ring-indigo-400 focus:outline-none" />
            </div>
        </div>

        <button aria-label="Search"
            class="ml-auto bg-indigo-600 hover:bg-indigo-700 text-white rounded-full px-8 py-3 transition focus:outline-none focus:ring-2 focus:ring-indigo-500"
            @click="onSearch">
            <i class="fas fa-search text-base"></i>
        </button>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const selectedLocation = ref('Hồ Chí Minh')
const showLocationDropdown = ref(false)
const locations = [
    'Hồ Chí Minh',
    'Hà Nội',
    'Đà Nẵng',
    'Nha Trang',
    'Cần Thơ',
    'Vũng Tàu',
    'Huế',
    'Hội An'
]

const today = new Date().toISOString().split('T')[0]
const checkIn = ref(today)
const checkOut = ref('')
const adults = ref(1)
const children = ref(0)

const minCheckOut = computed(() => {
    if (checkIn.value) {
        const date = new Date(checkIn.value)
        date.setDate(date.getDate() + 1)
        return date.toISOString().split('T')[0]
    }
    return today
})

function toggleLocationDropdown() {
    showLocationDropdown.value = !showLocationDropdown.value
}

function selectLocation(location) {
    selectedLocation.value = location
    showLocationDropdown.value = false
}

function onSearch() {
    alert(`Tìm kiếm:
  - Địa điểm: ${selectedLocation.value}
  - Nhận phòng: ${checkIn.value}
  - Trả phòng: ${checkOut.value}
  - Người lớn: ${adults.value}
  - Trẻ em: ${children.value}`)
}
</script>

<style scoped>
@media (max-width: 640px) {
    .border-l {
        display: none !important;
    }
}
</style>
