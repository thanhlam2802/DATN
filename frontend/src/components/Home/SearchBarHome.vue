<template>
    <div class="container mx-auto px-4 relative z-20 w-full">
        <div
            class="bg-white backdrop-blur-md rounded-full shadow-lg p-2 border border-gray-300 transition-all duration-300 hover:shadow-xl max-w-[3000px] mx-auto">
            <div class="flex flex-wrap items-end -mx-2">
                <div class="w-full md:flex md:space-x-4">

                    <div class="mb-3 md:mb-0 px-6 min-w-[220px]">
                        <div class="flex flex-col">
                            <span class="text-xs font-medium mb-1 text-gray-700">Địa điểm</span>
                            <div
                                class="flex items-center border border-gray-300 rounded-lg p-1.5 bg-white focus-within:border-blue-500 transition-colors">
                                <i class="fas fa-map-marker-alt text-blue-500 mr-2 text-sm"></i>
                                <input type="text" placeholder="Nhập nơi đi đến của bạn"
                                    class="w-full text-xs text-gray-700 font-medium focus:outline-none bg-transparent truncate" />
                            </div>
                        </div>
                    </div>

                    <div class="flex-1 mb-3 md:mb-0 px-2">
                        <div class="flex flex-col">
                            <span class="text-xs font-medium mb-1 text-gray-700">Ngày đến</span>
                            <div
                                class="flex items-center border border-gray-300 rounded-lg p-1.5 bg-white focus-within:border-blue-500 transition-colors cursor-pointer">
                                <i class="far fa-calendar-alt text-blue-500 mr-2 text-sm"></i>
                                <input type="date"
                                    class="w-full text-xs text-gray-700 font-medium focus:outline-none bg-transparent" />
                            </div>
                        </div>
                    </div>

                    <div class="flex-1 mb-3 md:mb-0 px-2">
                        <div class="flex flex-col">
                            <span class="text-xs font-medium mb-1 text-gray-700">Ngày đi</span>
                            <div
                                class="flex items-center border border-gray-300 rounded-lg p-1.5 bg-white focus-within:border-blue-500 transition-colors cursor-pointer">
                                <i class="far fa-calendar-alt text-blue-500 mr-2 text-sm"></i>
                                <input type="date"
                                    class="w-full text-xs text-gray-700 font-medium focus:outline-none bg-transparent" />
                            </div>
                        </div>
                    </div>

                    <div class="w-[120px] mb-3 md:mb-0 px-2">
                        <div class="flex flex-col">
                            <span class="text-xs font-medium mb-1 text-gray-700">Người</span>
                            <div
                                class="flex items-center border border-gray-300 rounded-lg p-1.5 bg-white focus-within:border-blue-500 transition-colors cursor-pointer">
                                <i class="fas fa-user text-blue-500 mr-2 text-sm"></i>
                                <select
                                    class="w-full text-xs text-gray-700 font-medium focus:outline-none bg-transparent">
                                    <option>1 người</option>
                                    <option>2 người</option>
                                    <option>3 người</option>
                                    <option>4+ người</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div class="w-[130px] mb-3 md:mb-0 px-2">
                        <div class="flex flex-col">
                            <span class="text-xs font-medium mb-1 text-gray-700">Dịch vụ</span>
                            <div
                                class="flex items-center border border-gray-300 rounded-lg p-1.5 bg-white focus-within:border-blue-500 transition-colors cursor-pointer">
                                <i class="fas fa-concierge-bell text-blue-500 mr-2 text-sm"></i>
                                <select
                                    class="w-full text-xs text-gray-700 font-medium focus:outline-none bg-transparent">
                                    <option>Khách sạn</option>
                                    <option>Xe buýt</option>
                                    <option>Máy bay</option>
                                    <option>Tour</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div class="w-[130px] mb-3 md:mb-0 px-2">
                        <div class="flex flex-col">
                            <span class="text-xs font-medium mb-1 text-gray-700">Loại phòng</span>
                            <div
                                class="flex items-center border border-gray-300 rounded-lg p-1.5 bg-white focus-within:border-blue-500 transition-colors cursor-pointer">
                                <i class="fas fa-bed text-blue-500 mr-2 text-sm"></i>
                                <select
                                    class="w-full text-xs text-gray-700 font-medium focus:outline-none bg-transparent">
                                    <option>1 phòng</option>
                                    <option>2 phòng</option>
                                    <option>Studio</option>
                                    <option>Suite</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div class="w-full md:w-auto mt-3 md:mt-0 px-6 md:self-center">
                        <button
                            class="w-10 h-10 bg-blue-600 hover:bg-blue-700 text-white rounded-full cursor-pointer flex items-center justify-center transition-colors duration-200">
                            <i class="fas fa-search text-sm"></i>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "SearchBarHome",
};
</script>

<style scoped>
input[type="date"]::-webkit-calendar-picker-indicator {
    filter: invert(0.5);
    opacity: 0.7;
    cursor: pointer;
}
</style>