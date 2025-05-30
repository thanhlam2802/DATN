<template>
    <div class="container mx-auto px-4 relative z-20 w-full">
        <div class="bg-white/20 backdrop-blur-md rounded-lg shadow-lg p-4 transition-all duration-300 hover:shadow-xl">
            <div class="flex flex-wrap items-end -mx-2">
                <div class="w-full md:flex md:space-x-4">
                    <div class="flex-1 mb-4 md:mb-0 px-2">
                        <div class="flex flex-col">
                            <span class="text-sm font-semibold mb-1 text-gray-700">Địa điểm</span>
                            <div
                                class="flex items-center border border-gray-300 rounded-lg p-2 bg-white focus-within:border-blue-500 transition-colors">
                                <i class="fas fa-map-marker-alt text-blue-500 mr-2"></i>
                                <input type="text" placeholder="Nhập nơi đi đến của bạn"
                                    class="w-full text-sm text-gray-700 font-semibold focus:outline-none bg-transparent" />
                            </div>
                        </div>
                    </div>
                    <div class="flex-1 mb-4 md:mb-0 px-2">
                        <div class="flex flex-col">
                            <span class="text-sm font-semibold mb-1 text-gray-700">Ngày đến</span>
                            <div
                                class="flex items-center border border-gray-300 rounded-lg p-2 bg-white focus-within:border-blue-500 transition-colors cursor-pointer">
                                <i class="far fa-calendar-alt text-blue-500 mr-2"></i>
                                <input type="date"
                                    class="w-full text-sm text-gray-700 font-semibold focus:outline-none bg-transparent" />
                            </div>
                        </div>
                    </div>
                    <div class="flex-1 mb-4 md:mb-0 px-2">
                        <div class="flex flex-col">
                            <span class="text-sm font-semibold mb-1 text-gray-700">Ngày đi</span>
                            <div
                                class="flex items-center border border-gray-300 rounded-lg p-2 bg-white focus-within:border-blue-500 transition-colors cursor-pointer">
                                <i class="far fa-calendar-alt text-blue-500 mr-2"></i>
                                <input type="date"
                                    class="w-full text-sm text-gray-700 font-semibold focus:outline-none bg-transparent" />
                            </div>
                        </div>
                    </div>
                    <div class="flex-1 mb-4 md:mb-0 px-2">
                        <div class="flex flex-col">
                            <span class="text-sm font-semibold mb-1 text-gray-700">Người</span>
                            <div
                                class="flex items-center border border-gray-300 rounded-lg p-2 bg-white focus-within:border-blue-500 transition-colors cursor-pointer">
                                <i class="fas fa-user text-blue-500 mr-2"></i>
                                <select
                                    class="w-full text-sm text-gray-700 font-semibold focus:outline-none bg-transparent">
                                    <option>1 người</option>
                                    <option>2 người</option>
                                    <option>3 người</option>
                                    <option>4+ người</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="flex-1 mb-4 md:mb-0 px-2">
                        <div class="flex flex-col">
                            <span class="text-sm font-semibold mb-1 text-gray-700">Dịch vụ</span>
                            <div
                                class="flex items-center border border-gray-300 rounded-lg p-2 bg-white focus-within:border-blue-500 transition-colors cursor-pointer">
                                <i class="fas fa-concierge-bell text-blue-500 mr-2"></i>
                                <select
                                    class="w-full text-sm text-gray-700 font-semibold focus:outline-none bg-transparent">
                                    <option>Khách sạn</option>
                                    <option>Xe buýt</option>
                                    <option>Máy bay</option>
                                    <option>Tour</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="flex-1 mb-4 md:mb-0 px-2">
                        <div class="flex flex-col">
                            <span class="text-sm font-semibold mb-1 text-gray-700">Loại phòng</span>
                            <div
                                class="flex items-center border border-gray-300 rounded-lg p-2 bg-white focus-within:border-blue-500 transition-colors cursor-pointer">
                                <i class="fas fa-bed text-blue-500 mr-2"></i>
                                <select
                                    class="w-full text-sm text-gray-700 font-semibold focus:outline-none bg-transparent">
                                    <option>1 phòng</option>
                                    <option>2 phòng</option>
                                    <option>Studio</option>
                                    <option>Suite</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="w-full md:w-auto mt-4 md:mt-0 px-2 md:self-end">
                        <button
                            class="!rounded-button whitespace-nowrap bg-blue-600 hover:bg-blue-700 text-white py-2 px-6 rounded-full cursor-pointer flex items-center justify-center w-full md:w-auto transition-colors duration-200">
                            <i class="fas fa-search mr-2"></i>
                            Tìm kiếm
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