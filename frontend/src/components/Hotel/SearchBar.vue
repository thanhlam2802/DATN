<template>
    <div
        class="flex items-center gap-2 bg-white border border-gray-300 rounded-full shadow-md px-3 py-2 w-full max-w-lg mx-auto text-sm text-gray-800">
        <div class="flex items-center gap-1 px-2 py-1 rounded-full hover:bg-gray-100 cursor-pointer select-none">
            <i class="fas fa-map-marker-alt text-indigo-500"></i>
            <span class="font-medium">Hồ Chí Minh</span>
            <button @click="getCurrentLocation" aria-label="Get current location"
                class="ml-2 text-indigo-600 hover:text-indigo-800" title="Vị trí hiện tại">
                <i class="fas fa-location-arrow"></i>
            </button>
        </div>

        <span class="border-l h-6 border-gray-300"></span>

        <input type="date" v-model="date"
            class="px-2 py-1 rounded-full border border-gray-300 focus:outline-none focus:ring-1 focus:ring-indigo-500"
            aria-label="Select date" />

        <span class="border-l h-6 border-gray-300"></span>

        <select v-model="guests"
            class="px-2 py-1 rounded-full border border-gray-300 focus:outline-none focus:ring-1 focus:ring-indigo-500"
            aria-label="Select number of guests">
            <option v-for="n in 10" :key="n" :value="n">{{ n }} người</option>
        </select>

        <button aria-label="Search"
            class="ml-auto bg-indigo-600 hover:bg-indigo-700 text-white rounded-full px-7 py-2 transition focus:outline-none focus:ring-2 focus:ring-indigo-500"
            @click="onSearch">
            <i class="fas fa-search text-sm"></i>
        </button>
    </div>
</template>

<script setup>
import { ref } from 'vue'

const date = ref('')
const guests = ref(2)

function getCurrentLocation() {
    if (!navigator.geolocation) {
        alert('Trình duyệt của bạn không hỗ trợ vị trí.')
        return
    }
    navigator.geolocation.getCurrentPosition(
        (position) => {
            console.log('Vị trí hiện tại:', position.coords.latitude, position.coords.longitude)
            alert(`Vị trí hiện tại: ${position.coords.latitude.toFixed(4)}, ${position.coords.longitude.toFixed(4)}`)
        },
        (error) => {
            alert('Không thể lấy vị trí hiện tại: ' + error.message)
        }
    )
}

function onSearch() {
    alert(`Tìm kiếm: Địa điểm = Hồ Chí Minh, Ngày = ${date.value}, Số người = ${guests.value}`)
}
</script>

<style scoped>
/* Nếu bạn muốn input và select đồng bộ height, border-radius: full và padding giống nhau */
/* Có thể tùy chỉnh thêm */
</style>
