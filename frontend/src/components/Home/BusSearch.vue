<template>
    <div class="p-4 rounded-lg min-h-[250px]">
        <div class="flex flex-wrap items-center justify-between gap-4 mt-4 mb-4">
            <div class="flex items-center gap-2 p-1 bg-black/50 rounded-full">
                <button @click="$emit('update:busTripType', 'round-trip')"
                    :class="busTripType === 'round-trip' ? 'bg-white text-blue-600 shadow-md' : 'text-white/80 hover:bg-white/10'"
                    class="px-4 py-1.5 text-sm font-semibold rounded-full transition-all duration-300">
                    Khứ hồi
                </button>
                <button @click="$emit('update:busTripType', 'one-way')"
                    :class="busTripType === 'one-way' ? 'bg-white text-blue-600 shadow-md' : 'text-white/80 hover:bg-white/10'"
                    class="px-4 py-1.5 text-sm font-semibold rounded-full transition-all duration-300">
                    Một chiều
                </button>
            </div>
            <div class="flex items-center gap-4">
                <div class="relative">
                    <button class="flex items-center gap-2 text-white font-semibold text-sm">
                        <i class="fas fa-users"></i>
                        <span>1 Hành khách</span>
                        <i class="fas fa-chevron-down text-xs"></i>
                    </button>
                </div>
            </div>
        </div>
        <div class="flex flex-col lg:flex-row items-center gap-2 pt-8 pb-2">
            <div class="w-full lg:w-5/12 flex items-center gap-2">
                <div class="relative w-full group">
                    <label for="from-bus"
                        class="absolute -top-2 left-3 px-1 bg-white text-gray-500 text-xs font-semibold rounded-md">Từ</label>
                    <div
                        class="flex items-center bg-white rounded-lg border border-gray-300 overflow-hidden shadow-sm h-16">
                        <i class="fas fa-bus text-blue-500 text-xl px-4"></i>
                        <div class="py-2">
                            <p class="text-gray-900 font-bold text-lg">Thành phố Hồ Chí Minh</p>
                        </div>
                    </div>
                </div>
                <button aria-label="Đảo chiều"
                    class="flex-shrink-0 bg-white/30 hover:bg-white/50 transition-colors rounded-full w-10 h-10 flex items-center justify-center border-2 border-white/50 text-white shadow-lg">
                    <i class="fas fa-exchange-alt"></i>
                </button>
                <div class="relative w-full group">
                    <label for="to-bus"
                        class="absolute -top-2 left-3 px-1 bg-white text-gray-500 text-xs font-semibold rounded-md">Đến</label>
                    <div
                        class="flex items-center bg-white rounded-lg border border-gray-300 overflow-hidden shadow-sm h-16">
                        <i class="fas fa-bus text-blue-500 text-xl px-4"></i>
                        <div class="py-2">
                            <p class="text-gray-900 font-bold text-lg">Bình Định</p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="w-full lg:w-4/12 flex items-center gap-2">
                <div class="relative w-full group">
                    <label for="departure-bus"
                        class="absolute -top-2 left-3 px-1 bg-white text-gray-500 text-xs font-semibold rounded-md">Ngày
                        đi</label>
                    <div
                        class="flex items-center bg-white rounded-lg border border-gray-300 overflow-hidden shadow-sm h-16">
                        <i class="far fa-calendar-alt text-blue-500 text-xl px-4"></i>
                        <div class="py-2">
                            <p class="text-gray-900 font-bold text-lg">17/06/2025</p>
                        </div>
                    </div>
                </div>
                <div class="relative w-full group"
                    :class="{ 'opacity-50 cursor-not-allowed': busTripType === 'one-way' }">
                    <label for="return-bus"
                        class="absolute -top-2 left-3 px-1 bg-white text-gray-500 text-xs font-semibold rounded-md">Ngày
                        về</label>
                    <div
                        class="flex items-center bg-white rounded-lg border border-gray-300 overflow-hidden shadow-sm h-16">
                        <i class="far fa-calendar-alt text-blue-500 text-xl px-4"></i>
                        <div class="py-2">
                            <p class="text-gray-500 font-bold text-lg">
                                <span v-if="busTripType === 'round-trip'">19/06/2025</span>
                                <span v-else>--/--/----</span>
                            </p>
                        </div>
                    </div>
                    <div v-if="busTripType === 'one-way'" class="absolute inset-0 bg-gray-200/40 rounded-lg"></div>
                </div>
            </div>
            <div class="w-full lg:w-3/12 flex items-center justify-center pt-0">
                <button
                    class="w-full h-16 bg-orange-500 hover:bg-orange-600 transition-colors text-white font-bold text-xl rounded-lg shadow-lg flex items-center justify-center gap-3">
                    <i class="fas fa-search"></i>
                    <span>Tìm kiếm</span>
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
defineProps({
    busTripType: String
});
defineEmits(['update:busTripType']);
</script>