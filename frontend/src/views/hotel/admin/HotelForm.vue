<template>
    <div class="w-full p-6">
        <div class="flex justify-between items-center mb-6">
            <h1 class="text-2xl font-bold text-slate-800">Danh sách khách sạn</h1>
            <button @click="openModal('add')"
                class="bg-black hover:bg-gray-800 text-white px-4 py-2 rounded-md shadow-sm">
                Thêm khách sạn
            </button>
        </div>

        <div class="flex flex-col sm:flex-row justify-between items-center mb-6 space-y-4 sm:space-y-0 sm:space-x-4">
            <div class="relative w-full sm:w-2/3">
                <input type="text" v-model="searchQuery" placeholder="Tìm kiếm khách sạn theo tên hoặc thành phố..."
                    class="w-full pl-10 pr-4 py-2 border border-slate-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-slate-900" />
                <i class="fas fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400"></i>
            </div>

            <div ref="filterDropdownContainer" class="relative w-full sm:w-1/3 text-right">
                <button @click="toggleFilterDropdown"
                    class="bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 px-4 py-2 rounded-md shadow-sm transition-colors duration-200 flex items-center justify-center sm:justify-between w-full">
                    <i class="fas fa-filter mr-2 sm:mr-3"></i>
                    <span>Bộ lọc</span>
                    <i class="fas ml-2"
                        :class="{ 'fa-chevron-up': showFilterDropdown, 'fa-chevron-down': !showFilterDropdown }"></i>
                </button>

                <div v-if="showFilterDropdown"
                    class="origin-top-right absolute right-0 mt-2 w-full sm:w-80 rounded-xl shadow-xl bg-white focus:outline-none z-20 border border-slate-200 flex flex-col"
                    style="max-height: calc(100vh - 12rem);">

                    <div class="p-5 pb-4 border-b border-slate-100 flex-shrink-0">
                        <h3 class="text-lg font-bold text-slate-800">Tùy chọn lọc</h3>
                    </div>

                    <div class="p-5 overflow-y-auto">
                        <div class="mb-5">
                            <label class="block text-sm font-semibold text-slate-700 mb-2">Hạng sao:</label>
                            <div class="flex flex-wrap gap-2">
                                <button v-for="n in 5" :key="n" @click="setFilterStar(n)"
                                    :class="{ 'bg-blue-600 text-white': filterStar === n, 'bg-slate-100 text-slate-700 hover:bg-slate-200': filterStar !== n }"
                                    class="px-3 py-1 rounded-full text-sm font-medium transition-colors duration-200">
                                    {{ n }} <i class="fas fa-star text-yellow-400"></i>
                                </button>
                                <button @click="setFilterStar('')"
                                    :class="{ 'bg-blue-600 text-white': filterStar === '', 'bg-slate-100 text-slate-700 hover:bg-slate-200': filterStar !== '' }"
                                    class="px-3 py-1 rounded-full text-sm font-medium transition-colors duration-200">
                                    Tất cả
                                </button>
                            </div>
                        </div>

                        <div class="mb-5">
                            <label class="block text-sm font-semibold text-slate-700 mb-2">Giá mỗi đêm (tối đa):</label>
                            <input type="range" v-model="filterPriceMax" min="0" max="10000000" step="100000"
                                class="w-full h-2 bg-blue-200 rounded-lg appearance-none cursor-pointer accent-blue-600">
                            <div class="flex justify-between text-sm font-medium text-slate-700 mt-2">
                                <span>Giá tối đa:</span>
                                <span class="text-blue-600">{{ formatCurrency(filterPriceMax) }} VND</span>
                            </div>
                        </div>

                        <div class="mb-0">
                            <label class="block text-sm font-semibold text-slate-700 mb-2">Tiện nghi:</label>
                            <div class="relative">
                                <div @click="showAmenityDropdown = !showAmenityDropdown"
                                    class="min-h-[40px] border border-slate-300 rounded-md px-3 py-2 bg-white flex flex-wrap items-center gap-2 cursor-pointer hover:border-blue-400 focus-within:border-blue-500 transition">
                                    <template v-if="Object.values(filterAmenities).some(v => v)">
                                        <span v-for="(lbl, k) in amenityLabels" v-if="filterAmenities[k]" :key="k"
                                            class="bg-blue-100 text-blue-700 px-2 py-1 rounded-full text-xs font-medium flex items-center gap-1">
                                            <i class="fas fa-check-circle text-blue-400"></i> {{ lbl }}
                                        </span>
                                    </template>
                                    <span v-if="!Object.values(filterAmenities).some(v => v)"
                                        class="text-slate-400 text-sm">Chọn tiện ích...</span>
                                    <i class="fas fa-chevron-down ml-auto text-slate-400"></i>
                                </div>
                                <div v-if="showAmenityDropdown"
                                    class="absolute left-0 mt-2 w-full z-30 bg-white border border-slate-200 rounded-lg shadow-xl p-2 grid grid-cols-2 sm:grid-cols-3 gap-2"
                                    @click.stop>
                                    <label v-for="(lbl, k) in amenityLabels" :key="k"
                                        class="flex items-center space-x-2 p-2 rounded hover:bg-blue-50 cursor-pointer text-slate-700">
                                        <input type="checkbox" v-model="filterAmenities[k]"
                                            class="w-4 h-4 text-blue-600 border-slate-300 rounded focus:ring-blue-500 cursor-pointer" />
                                        <span class="text-xs">{{ lbl }}</span>
                                    </label>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="p-5 pt-4 border-t border-slate-100 mt-auto flex-shrink-0">
                        <div class="flex justify-end">
                            <button @click="resetFilters"
                                class="bg-slate-100 text-slate-700 hover:bg-slate-200 px-4 py-2 rounded-md text-sm font-medium transition-colors duration-200 shadow-sm">
                                <i class="fas fa-redo-alt mr-2"></i> Đặt lại
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="mb-8 bg-white rounded-xl shadow-lg border border-slate-200 overflow-hidden">
            <div class="bg-gradient-to-r from-slate-50 to-slate-100 px-6 py-4 border-b border-slate-200">
                <h3 class="text-lg font-semibold text-slate-800">Danh sách khách sạn</h3>
                <p class="text-sm text-slate-600 mt-1">Quản lý thông tin khách sạn trong hệ thống</p>
            </div>
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-slate-200">
                    <thead class="bg-gradient-to-r from-slate-100 to-slate-200">
                        <tr>
                            <th class="px-6 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">
                                <div class="flex items-center space-x-1">
                                    <i class="fas fa-hotel text-blue-500"></i>
                                    <span>Tên khách sạn</span>
                                </div>
                            </th>
                            <th class="px-6 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">
                                <div class="flex items-center space-x-1">
                                    <i class="fas fa-star text-yellow-500"></i>
                                    <span>Hạng sao</span>
                                </div>
                            </th>
                            <th class="px-6 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">
                                <div class="flex items-center space-x-1">
                                    <i class="fas fa-map-marker-alt text-red-500"></i>
                                    <span>Thành phố</span>
                                </div>
                            </th>
                            <th class="px-6 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">
                                <div class="flex items-center space-x-1">
                                    <i class="fas fa-money-bill-wave text-green-500"></i>
                                    <span>Giá từ (VND)</span>
                                </div>
                            </th>
                            <th class="px-6 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">
                                <div class="flex items-center space-x-1">
                                    <i class="fas fa-star-half-alt text-purple-500"></i>
                                    <span>Đánh giá</span>
                                </div>
                            </th>
                            <th class="px-6 py-4 text-right text-xs font-bold text-slate-700 uppercase tracking-wider">
                                <div class="flex items-center justify-end space-x-1">
                                    <i class="fas fa-cogs text-slate-500"></i>
                                    <span>Hành động</span>
                                </div>
                            </th>
                        </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-slate-100">
                        <tr v-if="paginatedHotels.length === 0" class="hover:bg-slate-50">
                            <td colspan="6" class="px-6 py-12 text-center">
                                <div class="flex flex-col items-center space-y-3">
                                    <i class="fas fa-search text-4xl text-slate-300"></i>
                                    <p class="text-lg font-medium text-slate-500">Không tìm thấy khách sạn nào</p>
                                    <p class="text-sm text-slate-400">Thử thay đổi bộ lọc hoặc tìm kiếm khác</p>
                                </div>
                            </td>
                        </tr>
                        <tr v-for="(h, index) in paginatedHotels" :key="h.id"
                            class="hover:bg-slate-50 transition-colors duration-150">
                            <td class="px-6 py-5 whitespace-nowrap">
                                <div class="flex items-center space-x-3">
                                    <div class="flex-shrink-0">
                                        <div
                                            class="w-10 h-10 bg-gradient-to-br from-blue-500 to-purple-600 rounded-lg flex items-center justify-center">
                                            <i class="fas fa-hotel text-white text-sm"></i>
                                        </div>
                                    </div>
                                    <div>
                                        <div class="text-sm font-semibold text-slate-900">{{ h.name }}</div>
                                    </div>
                                </div>
                            </td>
                            <td class="px-6 py-5 whitespace-nowrap">
                                <div class="flex items-center space-x-1">
                                    <span class="text-sm font-medium text-slate-900">{{ h.starRating }}</span>
                                    <div class="flex space-x-0.5">
                                        <i v-for="star in h.starRating" :key="star"
                                            class="fas fa-star text-yellow-400 text-xs"></i>
                                    </div>
                                </div>
                            </td>
                            <td class="px-6 py-5 whitespace-nowrap">
                                <div class="flex items-center space-x-2">
                                    <i class="fas fa-map-marker-alt text-red-400 text-xs"></i>
                                    <span class="text-sm font-medium text-slate-700">{{ h.provinceName }}</span>
                                </div>
                            </td>
                            <td class="px-6 py-5 whitespace-nowrap">
                                <div class="text-sm font-bold text-green-600">
                                    {{ formatCurrency(h.startingPrice) }}
                                </div>
                                <div class="text-xs text-slate-500">VND/đêm</div>
                            </td>
                            <td class="px-6 py-5 whitespace-nowrap">
                                <div class="flex items-center space-x-2">
                                    <div class="flex items-center space-x-1">
                                        <span class="text-sm font-semibold text-slate-900">{{ h.rating }}</span>
                                        <div class="flex space-x-0.5">
                                            <i v-for="star in Math.floor(h.rating)" :key="star"
                                                class="fas fa-star text-yellow-400 text-xs"></i>
                                            <i v-if="h.rating % 1 > 0"
                                                class="fas fa-star-half-alt text-yellow-400 text-xs"></i>
                                        </div>
                                    </div>
                                    <span class="text-xs text-slate-500">({{ h.reviewCount }})</span>
                                </div>
                            </td>
                            <td class="px-6 py-5 whitespace-nowrap text-right">
                                <div class="relative inline-block text-left">
                                    <button :ref="el => setDropdownBtnRef(el, h.id)"
                                        @click.stop="openDropdown(h.id, $event)" type="button"
                                        class="inline-flex justify-center w-10 h-10 rounded-lg text-slate-500 hover:text-slate-700 hover:bg-slate-100 focus:outline-none transition-all duration-200 shadow-sm">
                                        <i class="fas fa-ellipsis-v flex items-center justify-center text-sm"></i>
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="flex flex-col sm:flex-row justify-between items-center mt-8 p-6 bg-white rounded-xl shadow-sm">
            <div class="flex items-center space-x-3 mb-4 sm:mb-0">
                <div class="flex items-center space-x-2">
                    <i class="fas fa-list-ul text-slate-500"></i>
                    <label for="itemsPerPage" class="text-sm font-medium text-slate-700">Hiển thị:</label>
                </div>
                <select v-model="itemsPerPage" id="itemsPerPage"
                    class="border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white shadow-sm">
                    <option v-for="option in itemsPerPageOptions" :key="option" :value="option">
                        {{ option === 'Tất cả' ? 'Tất cả' : option }}
                    </option>
                </select>
                <div class="flex items-center space-x-1 text-sm text-slate-600">
                    <span>kết quả trên tổng số</span>
                    <span class="font-bold text-blue-600">{{ filteredHotels.length }}</span>
                    <span>khách sạn</span>
                </div>
            </div>

            <div class="w-full sm:w-auto">
                <nav v-if="totalPages > 1 && itemsPerPage !== 'Tất cả'" aria-label="Pagination">
                    <div class="flex items-center space-x-2">
                        <div class="flex items-center space-x-1 text-sm text-slate-600">
                            <span>Trang</span>
                            <span class="font-semibold text-slate-900">{{ currentPage }}</span>
                            <span>trên</span>
                            <span class="font-semibold text-slate-900">{{ totalPages }}</span>
                        </div>
                        <ul class="flex items-center space-x-1">
                            <li>
                                <button @click="prevPage" :disabled="currentPage === 1"
                                    class="flex items-center justify-center w-10 h-10 rounded-lg text-slate-600 bg-white hover:bg-slate-50 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:bg-white shadow-sm">
                                    <i class="fas fa-chevron-left text-sm"></i>
                                </button>
                            </li>
                            <li v-for="(page, index) in displayedPages" :key="index">
                                <span v-if="page === '...'"
                                    class="flex items-center justify-center w-10 h-10 text-slate-400 font-medium">...</span>
                                <button v-else @click="changePage(page)" :class="[
                                    'flex items-center justify-center w-10 h-10 rounded-lg transition-all duration-200 font-medium shadow-sm',
                                    currentPage === page
                                        ? 'bg-gradient-to-r from-blue-600 to-blue-700 text-white shadow-md'
                                        : 'text-slate-700 bg-white hover:bg-slate-50 hover:shadow-md'
                                ]">{{ page }}</button>
                            </li>
                            <li>
                                <button @click="nextPage" :disabled="currentPage === totalPages"
                                    class="flex items-center justify-center w-10 h-10 rounded-lg text-slate-600 bg-white hover:bg-slate-50 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:bg-white shadow-sm">
                                    <i class="fas fa-chevron-right text-sm"></i>
                                </button>
                            </li>
                        </ul>
                    </div>
                </nav>
            </div>
        </div>
        <transition name="fade">
            <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center px-4 sm:px-0 pt-2">
                <div class="absolute inset-0 backdrop-blur-sm" style="background-color: rgba(30,41,59,0.5);"
                    @click="closeModal"></div>
                <div
                    class="relative bg-white rounded-xl shadow-xl w-full max-w-5xl max-h-[85vh] overflow-hidden flex flex-col border border-slate-300">
                    <div class="flex justify-between items-center border-b border-slate-200 px-6 py-4">
                        <h2 class="text-lg font-semibold text-slate-800">{{ modalMode === 'add' ? 'Thêm khách sạn' :
                            'Chỉnh sửa khách sạn' }}</h2>
                        <button @click="closeModal"
                            class="text-slate-500 hover:text-slate-700 p-2 rounded-full hover:bg-slate-100 transition-colors">✕</button>
                    </div>

                    <div class="flex border-b border-slate-200 text-sm select-none">
                        <button v-for="(tabName, index) in tabs" :key="index" @click="activeTab = index" :class="{
                            'flex-1 py-3 px-4 text-center font-semibold border-b-2 transition-colors duration-200': true,
                            'border-blue-600 text-blue-600 bg-blue-50': activeTab === index,
                            'border-transparent text-slate-600 hover:bg-slate-50': activeTab !== index
                        }">
                            {{ tabName }}
                        </button>
                    </div>

                    <div class="p-6 space-y-6 overflow-y-auto flex-1">
                        <form v-if="activeTab === 0" class="space-y-6" autocomplete="off" novalidate @submit.prevent>
                            <div>
                                <label class="block text-sm font-semibold text-slate-700 mb-1">Tên khách sạn</label>
                                <input v-model="newHotel.name" type="text" placeholder="Nhập tên khách sạn"
                                    class="w-full border border-slate-300 rounded-md px-3 py-2 placeholder-slate-400 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                            </div>
                            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                <div>
                                    <label class="block text-sm font-semibold text-slate-700 mb-1">Hạng sao</label>
                                    <select v-model="newHotel.star"
                                        class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                                        <option disabled value="">Chọn hạng sao</option>
                                        <option>1</option>
                                        <option>2</option>
                                        <option>3</option>
                                        <option>4</option>
                                        <option>5</option>
                                    </select>
                                </div>
                                <div>
                                    <label class="block text-sm font-semibold text-slate-700 mb-1">Thành phố</label>
                                    <select v-model="newHotel.provinceId"
                                        class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                                        <option disabled value="">Chọn thành phố</option>
                                        <option v-for="p in provinces" :key="p.id" :value="p.id">{{ p.name }}</option>
                                    </select>
                                </div>
                            </div>
                            <div>
                                <label class="block text-sm font-semibold text-slate-700 mb-1">Địa chỉ</label>
                                <div class="flex">
                                    <input v-model="newHotel.address" type="text" placeholder="Nhập địa chỉ khách sạn"
                                        class="flex-grow border border-slate-300 rounded-l-md px-3 py-2 placeholder-slate-400 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                                    <button type="button"
                                        class="border border-l-0 border-slate-300 rounded-r-md bg-slate-50 px-3 flex items-center justify-center text-slate-600 hover:bg-slate-100 focus:outline-none focus:ring-2 focus:ring-blue-500">
                                        <i class="fas fa-map-marker-alt"></i>
                                    </button>
                                </div>
                            </div>
                            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                <div>
                                    <label class="block text-sm font-semibold text-slate-700 mb-1">Số điện thoại</label>
                                    <input v-model="newHotel.phone" type="text" placeholder="Nhập số điện thoại"
                                        class="w-full border border-slate-300 rounded-md px-3 py-2 placeholder-slate-400 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                                </div>
                                <div>
                                    <label class="block text-sm font-semibold text-slate-700 mb-1">Email</label>
                                    <input v-model="newHotel.email" type="email" placeholder="Nhập email"
                                        class="w-full border border-slate-300 rounded-md px-3 py-2 placeholder-slate-400 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                                </div>
                            </div>
                            <div>
                                <label class="block text-sm font-semibold text-slate-700 mb-1">Mô tả</label>
                                <textarea v-model="newHotel.description" rows="4" placeholder="Mô tả về khách sạn"
                                    class="w-full border border-slate-300 rounded-md px-3 py-2 placeholder-slate-400 text-slate-900 resize-none focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"></textarea>
                            </div>
                            <div>
                                <label class="block text-sm font-semibold text-slate-700 mb-2">Hình ảnh khách
                                    sạn</label>
                                <button type="button"
                                    class="w-36 h-28 border-2 border-dashed border-slate-300 rounded-md flex flex-col items-center justify-center text-slate-500 hover:text-blue-600 hover:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors">
                                    <i class="far fa-image text-2xl mb-1"></i>
                                    Thêm ảnh
                                </button>
                            </div>
                            <div v-if="newHotel.imageUrls && newHotel.imageUrls.length"
                                class="flex flex-wrap gap-3 mt-2">
                                <img v-for="(img, idx) in newHotel.imageUrls" :key="idx" :src="img" alt="Hotel Image"
                                    class="w-20 h-20 object-cover rounded-lg border border-slate-200 shadow-sm hover:scale-105 transition-transform duration-200" />
                            </div>
                            <div class="flex justify-end">
                                <button type="button" @click="activeTab = 1"
                                    class="bg-black text-white text-sm font-semibold rounded-md px-5 py-2 hover:bg-gray-800 focus:outline-none focus:ring-2 focus:ring-offset-1 focus:ring-black shadow-sm">
                                    Tiếp tục
                                </button>
                            </div>
                        </form>

                        <form v-else-if="activeTab === 1" class="space-y-6" autocomplete="off" novalidate
                            @submit.prevent>
                            <div class="flex justify-between items-center mb-4">
                                <h2 class="font-semibold text-slate-800 text-base">Thông tin các loại phòng</h2>
                                <button type="button" @click="addRoom"
                                    class="text-blue-600 font-semibold border border-blue-600 rounded-md px-4 py-2 hover:bg-blue-50 transition shadow-sm">
                                    + Thêm loại phòng
                                </button>
                            </div>
                            <div v-for="(r, idx) in newHotel.rooms" :key="idx" class="mb-6">
                                <div class="border border-slate-200 rounded-xl shadow-sm">
                                    <div @click="r._open = !r._open"
                                        class="flex justify-between items-center px-6 py-4 cursor-pointer select-none bg-slate-50 rounded-t-xl">
                                        <div class="font-semibold text-slate-800 text-base">{{ r.type || 'Loại phòng mới' }}
                                        </div>
                                        <div class="flex items-center gap-2">
                                            <span class="text-xs text-slate-500">{{ r.bedType ? r.bedType + ',' : '' }}
                                                {{ r.area ? r.area + 'm²' : '' }}</span>
                                            <button type="button" @click.stop="removeRoom(idx)"
                                                class="text-red-500 hover:underline text-xs ml-2">Xóa</button>
                                            <i :class="r._open ? 'fas fa-chevron-up' : 'fas fa-chevron-down'"
                                                class="ml-2 text-slate-400"></i>
                                        </div>
                                    </div>
                                    <transition name="fade">
                                        <div v-show="r._open !== false" class="p-6 space-y-4 bg-white rounded-b-xl">
                                            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
                                                <div>
                                                    <label class="block text-sm font-semibold text-slate-700 mb-1">Loại
                                                        phòng</label>
                                                    <input v-model="r.type" type="text"
                                                        placeholder="Ví dụ: Phòng Deluxe, Phòng Superior"
                                                        class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                                                </div>
                                                <div>
                                                    <label
                                                        class="block text-sm font-semibold text-slate-700 mb-1">Giường</label>
                                                    <input v-model="r.bedType" type="text"
                                                        placeholder="Nhập loại giường"
                                                        class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                                                </div>
                                                <div>
                                                    <label class="block text-sm font-semibold text-slate-700 mb-1">Diện
                                                        tích (m²)</label>
                                                    <input v-model.number="r.area" type="number" min="0"
                                                        placeholder="Nhập diện tích phòng"
                                                        class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                                                </div>
                                            </div>
                                            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                                <div>
                                                    <label class="block text-sm font-semibold text-slate-700 mb-1">Số
                                                        lượng phòng</label>
                                                    <input v-model.number="r.quantity" type="number" min="0"
                                                        placeholder="Nhập số lượng phòng"
                                                        class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                                                </div>
                                                <div class="grid grid-cols-2 gap-4">
                                                    <div>
                                                        <label
                                                            class="block text-sm font-semibold text-slate-700 mb-1">Người
                                                            lớn</label>
                                                        <input v-model.number="r.maxAdults" type="number" min="0"
                                                            placeholder="Số người lớn"
                                                            class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                                                    </div>
                                                    <div>
                                                        <label
                                                            class="block text-sm font-semibold text-slate-700 mb-1">Trẻ
                                                            em</label>
                                                        <input v-model.number="r.maxChildren" type="number" min="0"
                                                            placeholder="Số trẻ em"
                                                            class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                                                    </div>
                                                </div>
                                            </div>
                                            <div>
                                                <label class="block text-sm font-semibold text-slate-700 mb-1">Tiện ích
                                                    phòng</label>
                                                <div class="flex flex-wrap gap-3">
                                                    <label v-for="a in amenities" :key="a.id"
                                                        class="flex items-center space-x-2">
                                                        <input type="checkbox" v-model="r.amenities[a.id]"
                                                            class="w-4 h-4 text-blue-600 border-slate-300 rounded focus:ring-blue-500" />
                                                        <span>{{ a.name }}</span>
                                                    </label>
                                                </div>
                                            </div>
                                            <div>
                                                <label class="block text-sm font-semibold text-slate-700 mb-1">Hình ảnh
                                                    phòng</label>
                                                <button type="button"
                                                    class="w-40 h-28 border-2 border-dashed border-slate-300 rounded-md flex flex-col items-center justify-center text-slate-500 hover:border-blue-400 hover:text-blue-600 transition-colors">
                                                    <i class="far fa-image text-2xl mb-1"></i>
                                                    <span class="text-sm font-semibold">Thêm ảnh</span>
                                                </button>
                                            </div>
                                            <div v-if="r.imageUrls && r.imageUrls.length"
                                                class="flex flex-wrap gap-3 mt-2">
                                                <img v-for="(img, idx) in r.imageUrls" :key="idx" :src="img"
                                                    alt="Room Image"
                                                    class="w-20 h-20 object-cover rounded-lg border border-slate-200 shadow-sm hover:scale-105 transition-transform duration-200" />
                                            </div>
                                            <div class="mt-6">
                                                <div class="flex justify-between items-center mb-2">
                                                    <h3 class="text-base font-semibold text-slate-800">Gói phòng</h3>
                                                    <button type="button" @click="addVariant(idx)"
                                                        class="text-blue-600 text-xs font-semibold hover:underline">+
                                                        Thêm gói phòng</button>
                                                </div>
                                                <div class="overflow-x-auto rounded-lg">
                                                    <table
                                                        class="w-full text-xs border border-slate-100 rounded-lg overflow-hidden">
                                                        <thead>
                                                            <tr class="bg-slate-100 text-slate-700">
                                                                <th class="py-2 px-3 font-semibold text-left">Tên gói
                                                                </th>
                                                                <th class="py-2 px-3 font-semibold text-center">Giá</th>
                                                                <th class="py-2 px-3 font-semibold text-center">Bữa sáng
                                                                </th>
                                                                <th class="py-2 px-3 font-semibold text-center">Hủy miễn
                                                                    phí</th>
                                                                <th class="py-2 px-3 font-semibold text-center">Thanh
                                                                    toán tại KS</th>
                                                                <th class="py-2 px-3 font-semibold text-center">Thuế/Phí
                                                                </th>
                                                                <th class="py-2 px-3 font-semibold text-center">Xóa</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <tr v-for="(v, vIdx) in r.variants" :key="vIdx"
                                                                class="border-t border-slate-100">
                                                                <td class="py-2 px-3">
                                                                    <input v-model="v.variantName" type="text"
                                                                        placeholder="Tên gói"
                                                                        class="w-full border border-slate-200 rounded px-2 py-1 text-xs" />
                                                                </td>
                                                                <td class="py-2 px-3">
                                                                    <input v-model.number="v.price" type="number"
                                                                        min="0" placeholder="Giá"
                                                                        class="w-full border border-slate-200 rounded px-2 py-1 text-xs" />
                                                                </td>
                                                                <td class="py-2 px-3 text-center">
                                                                    <input type="checkbox" v-model="v.hasBreakfast" />
                                                                </td>
                                                                <td class="py-2 px-3 text-center">
                                                                    <input type="checkbox" v-model="v.cancellable" />
                                                                </td>
                                                                <td class="py-2 px-3 text-center">
                                                                    <input type="checkbox" v-model="v.payAtHotel" />
                                                                </td>
                                                                <td class="py-2 px-3">
                                                                    <input v-model.number="v.taxAndFeeAmount"
                                                                        type="number" min="0" placeholder="Thuế/Phí"
                                                                        class="w-full border border-slate-200 rounded px-2 py-1 text-xs" />
                                                                </td>
                                                                <td class="py-2 px-3 text-center">
                                                                    <button type="button"
                                                                        @click="removeVariant(idx, vIdx)"
                                                                        class="text-red-500 hover:underline text-xs">Xóa</button>
                                                                </td>
                                                            </tr>
                                                            <tr v-if="!r.variants || r.variants.length === 0">
                                                                <td colspan="7" class="text-center text-slate-400 py-4">
                                                                    Chưa có gói phòng nào</td>
                                                            </tr>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                        </div>
                                    </transition>
                                </div>
                            </div>
                            <div class="flex justify-between mt-8">
                                <button type="button" @click="activeTab = 0"
                                    class="px-4 py-2 border border-slate-300 rounded-md font-semibold text-slate-900 hover:bg-slate-100 shadow-sm">Quay
                                    lại</button>
                                <button type="button"
                                    class="font-semibold text-white bg-black rounded-md px-6 py-2 hover:bg-gray-800 transition shadow-sm"
                                    @click="activeTab = 2">
                                    Tiếp tục
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </transition>
        <HotelDetailModal v-if="showDetailModal" :show="showDetailModal" :hotel="detailHotel"
            :amenityLabels="amenityLabels" @close="showDetailModal = false" />
        <teleport to="body">
            <div v-if="activeDropdown !== null" ref="dropdownMenuRef" :style="dropdownStyle"
                class="fixed z-[9999] w-64 rounded-xl shadow-2xl bg-white overflow-hidden" role="menu"
                aria-orientation="vertical">
                <div class="py-3" role="none">
                    <div class="px-4 py-2 bg-gradient-to-r from-slate-50 to-slate-100 border-b border-slate-200">
                        <h4 class="text-sm font-semibold text-slate-800">Hành động</h4>
                        <p class="text-xs text-slate-600 mt-1">{{ dropdownHotelName }}</p>
                    </div>
                    <div class="p-2">
                        <button @click.stop="viewDetails(dropdownHotel)"
                            class="text-slate-700 flex items-center px-4 py-3 text-sm hover:bg-blue-50 hover:text-blue-700 w-full text-left transition-all duration-200 rounded-lg group"
                            role="menuitem">
                            <div
                                class="w-8 h-8 bg-blue-100 rounded-lg flex items-center justify-center mr-3 group-hover:bg-blue-200 transition-colors">
                                <i class="fas fa-info-circle text-blue-600 text-sm"></i>
                            </div>
                            <div>
                                <span class="font-medium">Xem chi tiết</span>
                                <p class="text-xs text-slate-500 mt-0.5">Thông tin đầy đủ khách sạn</p>
                            </div>
                        </button>
                        <button @click.stop="openModal('edit', dropdownHotel)"
                            class="text-slate-700 flex items-center px-4 py-3 text-sm hover:bg-green-50 hover:text-green-700 w-full text-left transition-all duration-200 rounded-lg group mt-1"
                            role="menuitem">
                            <div
                                class="w-8 h-8 bg-green-100 rounded-lg flex items-center justify-center mr-3 group-hover:bg-green-200 transition-colors">
                                <i class="fas fa-edit text-green-600 text-sm"></i>
                            </div>
                            <div>
                                <span class="font-medium">Chỉnh sửa</span>
                                <p class="text-xs text-slate-500 mt-0.5">Cập nhật thông tin khách sạn</p>
                            </div>
                        </button>
                        <div class="border-t border-slate-200 my-2"></div>
                        <button @click.stop="deleteHotel(dropdownHotel.id)"
                            class="text-red-600 flex items-center px-4 py-3 text-sm hover:bg-red-50 hover:text-red-700 w-full text-left transition-all duration-200 rounded-lg group"
                            role="menuitem">
                            <div
                                class="w-8 h-8 bg-red-100 rounded-lg flex items-center justify-center mr-3 group-hover:bg-red-200 transition-colors">
                                <i class="fas fa-trash-alt text-red-600 text-sm"></i>
                            </div>
                            <div>
                                <span class="font-medium">Xóa</span>
                                <p class="text-xs text-slate-500 mt-0.5">Xóa khách sạn khỏi hệ thống</p>
                            </div>
                        </button>
                    </div>
                </div>
            </div>
        </teleport>
    </div>
</template>

<script>
import hotelApi from '@/api/hotelApi';
import HotelDetailModal from './HotelDetailModal.vue';
import provinceApi from '@/api/provinceApi';
import AmenityApi from '@/api/AmenityApi';
export default {
    name: 'HotelManager',
    components: { HotelDetailModal },
    data() {
        return {
            showModal: false, activeTab: 0, tabs: ['Thông tin cơ bản', 'Phòng & Giá'],
            hotels: [],
            newHotel: {
                id: null,
                name: '',
                star: '',
                address: '',
                city: '',
                phone: '',
                email: '',
                description: '',
                imageUrls: [],
                rooms: [
                    {
                        type: '',
                        bedType: '',
                        area: 0,
                        quantity: 0,
                        maxAdults: 0,
                        maxChildren: 0,
                        imageUrls: [],
                        amenities: {},
                        variants: [],
                        _open: true,
                    }
                ],
                amenities: {},
                policy: {}
            },
            modalMode: 'add', activeDropdown: null,
            searchQuery: '', showFilterDropdown: false, filterStar: '', filterPriceMax: 10000000,
            filterAmenities: { wifi: false, parking: false, pool: false, restaurant: false, spa: false, gym: false, ac: false, breakfast: false, elevator: false },
            showAmenityDropdown: false,
            form: { amenities: { wifi: false, parking: false, pool: false, restaurant: false, spa: false, gym: false, ac: false, breakfast: false, elevator: false }, policy: { checkin: '', checkout: '', other: '' } },
            amenityLabels: { wifi: 'WiFi miễn phí', parking: 'Bãi đỗ xe', pool: 'Hồ bơi', restaurant: 'Nhà hàng', spa: 'Spa', gym: 'Phòng tập gym', ac: 'Điều hòa', breakfast: 'Bữa sáng', elevator: 'Thang máy' },
            currentPage: 1, itemsPerPage: 20, itemsPerPageOptions: [5, 10, 15, 20, 50, 'Tất cả'],
            dropdownBtnRefMap: {},
            dropdownStyle: {},
            dropdownHotel: null,
            dropdownHotelName: '',
            dropdownMenuRef: null,
            showDetailModal: false,
            detailHotel: null,
            provinces: [],
            amenities: [],
        };
    },
    computed: {
        filteredHotels() {
            return this.hotels.filter(hotel => {
                const matchesSearchQuery = hotel.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
                    (hotel.provinceName && hotel.provinceName.toLowerCase().includes(this.searchQuery.toLowerCase()));
                const matchesStar = this.filterStar === '' || hotel.starRating === parseInt(this.filterStar);
                const matchesPrice = hotel.startingPrice <= this.filterPriceMax;
                const matchesAmenities = Object.keys(this.filterAmenities).every(amenityKey =>
                    !this.filterAmenities[amenityKey] || (hotel.amenities && hotel.amenities[amenityKey]));
                return matchesSearchQuery && matchesStar && matchesPrice && matchesAmenities;
            });
        },
        totalPages() {
            return Math.ceil(this.filteredHotels.length / this.itemsPerPage);
        },
        paginatedHotels() {
            if (this.itemsPerPage === 'Tất cả') {
                return this.filteredHotels;
            }
            const start = (this.currentPage - 1) * this.itemsPerPage;
            const end = start + this.itemsPerPage;
            return this.filteredHotels.slice(start, end);
        },
        displayedPages() {
            const total = this.totalPages; const current = this.currentPage; const result = [];
            if (total <= 7) { for (let i = 1; i <= total; i++) { result.push(i); } }
            else {
                result.push(1);
                let start = Math.max(2, current - 1); let end = Math.min(total - 1, current + 1);
                if (current > 4) result.push('...');
                if (current <= 4) { start = 2; end = 4; }
                if (current >= total - 3) { start = total - 3; end = total - 1; }
                for (let i = start; i <= end; i++) { result.push(i); }
                if (current < total - 3) result.push('...');
                result.push(total);
            }
            return result;
        }
    },
    watch: {
        searchQuery() { this.currentPage = 1; },
        filterStar() { this.currentPage = 1; },
        filterPriceMax() { this.currentPage = 1; },
        filterAmenities: { handler() { this.currentPage = 1; }, deep: true },
        itemsPerPage() { this.currentPage = 1; },
    },
    methods: {
        formatCurrency(value) { return value == null ? 'N/A' : new Intl.NumberFormat('vi-VN').format(value); },
        async openModal(mode, hotel = null) {
            this.activeDropdown = null;
            if (!this.provinces.length) {
                try {
                    const res = await provinceApi.getAllProvinces();
                    this.provinces = res.data.data || res.data;
                } catch { }
            }
            if (!this.amenities.length) {
                try {
                    const res = await AmenityApi.getAllAmenities();
                    this.amenities = res.data.data || res.data;
                } catch { }
            }
            this.resetAll();
            this.modalMode = mode;
            this.activeTab = 0;
            if (mode === 'edit' && hotel) {
                try {
                    const res = await hotelApi.getHotelById(hotel.id);
                    const detail = res.data.data || {};
                    this.newHotel = {
                        id: detail.id,
                        name: detail.name || '',
                        star: detail.starRating || '',
                        address: detail.address || '',
                        provinceId: detail.provinceId || '',
                        phone: detail.phone || '',
                        email: detail.email || '',
                        description: detail.description || '',
                        imageUrls: detail.imageUrls || [],
                        rooms: (detail.availableRooms || []).map(room => ({
                            type: room.roomType || '',
                            bedType: room.bedType || '',
                            area: room.roomArea || 0,
                            quantity: room.roomQuantity || 0,
                            maxAdults: room.maxAdults || 0,
                            maxChildren: room.maxChildren || 0,
                            imageUrls: room.imageUrls || [],
                            amenities: (room.amenities || []).reduce((acc, a) => { acc[a.id] = true; return acc; }, {}),
                            variants: (room.availableVariants || []).map(variant => ({
                                variantName: variant.variantName || '',
                                price: (variant.price && typeof variant.price === 'object' && variant.price !== null) ? Number(variant.price) : (variant.price || 0),
                                hasBreakfast: variant.hasBreakfast || false,
                                cancellable: variant.cancellable || false,
                                payAtHotel: variant.payAtHotel || false,
                                taxAndFeeAmount: (variant.taxAndFeeAmount && typeof variant.taxAndFeeAmount === 'object' && variant.taxAndFeeAmount !== null) ? Number(variant.taxAndFeeAmount) : (variant.taxAndFeeAmount || 0),
                            })),
                            _open: true,
                        })),
                        amenities: (detail.amenities || []).reduce((acc, a) => { acc[a.id] = true; return acc; }, {}),
                        policy: detail.policy || {},
                    };
                } catch (e) {
                    alert('Không lấy được chi tiết khách sạn!');
                    return;
                }
            }
            this.showModal = true;
        },
        closeModal() { this.showModal = false; this.activeDropdown = null; this.removeDropdownListeners(); },
        toggleDropdown(hotelId) { this.activeDropdown = this.activeDropdown === hotelId ? null : hotelId; },
        toggleFilterDropdown() { this.showFilterDropdown = !this.showFilterDropdown; },
        setFilterStar(star) { this.filterStar = star; },
        resetFilters() { this.searchQuery = ''; this.filterStar = ''; this.filterPriceMax = 10000000; for (const key in this.filterAmenities) { this.filterAmenities[key] = false; } },
        async viewDetails(hotel) {
            try {
                const res = await hotelApi.getHotelById(hotel.id);
                this.detailHotel = res.data.data;
                this.showDetailModal = true;
            } catch (e) {
                alert('Không lấy được chi tiết khách sạn!');
            }
            this.activeDropdown = null;
        },
        addRoom() {
            this.newHotel.rooms.push({
                type: '',
                bedType: '',
                area: 0,
                quantity: 0,
                maxAdults: 0,
                maxChildren: 0,
                imageUrls: [],
                amenities: {},
                variants: [],
                _open: true,
            });
        },
        removeRoom(index) { if (this.newHotel.rooms.length > 1) { this.newHotel.rooms.splice(index, 1); } else { alert('Phải có ít nhất một loại phòng.'); } },
        deleteHotel(hotelId) { if (confirm('Bạn có chắc chắn muốn xóa khách sạn này không?')) { this.hotels = this.hotels.filter(h => h.id !== hotelId); } this.activeDropdown = null; },
        handleSubmit() {
            let minPrice = Infinity;
            this.newHotel.rooms.forEach(r => { const finalPrice = r.price * (1 - (r.discount || 0) / 100); if (finalPrice < minPrice) minPrice = finalPrice; });
            this.newHotel.minPrice = minPrice === Infinity ? 0 : minPrice;
            this.newHotel.amenities = { ...this.form.amenities }; this.newHotel.policy = { ...this.form.policy };
            if (this.modalMode === 'add') {
                this.newHotel.id = this.hotels.length > 0 ? Math.max(...this.hotels.map(h => h.id)) + 1 : 1;
                this.newHotel.rating = 4.0; this.newHotel.reviews = 0;
                this.hotels.unshift(JSON.parse(JSON.stringify(this.newHotel)));
            } else {
                const index = this.hotels.findIndex(h => h.id === this.newHotel.id);
                if (index !== -1) this.hotels.splice(index, 1, JSON.parse(JSON.stringify(this.newHotel)));
            }
            this.closeModal();
        },
        resetAll() {
            this.newHotel = {
                id: null,
                name: '',
                star: '',
                address: '',
                city: '',
                phone: '',
                email: '',
                description: '',
                imageUrls: [],
                rooms: [
                    {
                        type: '',
                        bedType: '',
                        area: 0,
                        quantity: 0,
                        maxAdults: 0,
                        maxChildren: 0,
                        imageUrls: [],
                        amenities: {},
                        variants: [],
                        _open: true,
                    }
                ],
                amenities: {},
                policy: {}
            };
            for (const key in this.form.amenities) { this.form.amenities[key] = false; }
            this.form.policy = { checkin: '', checkout: '', other: '' };
            this.modalMode = 'add';
        },
        changePage(page) { if (page >= 1 && page <= this.totalPages) this.currentPage = page; },
        nextPage() { if (this.currentPage < this.totalPages) this.currentPage++; },
        prevPage() { if (this.currentPage > 1) this.currentPage--; },
        async fetchHotels() {
            try {
                console.log('Đang gọi API lấy danh sách khách sạn...');
                const params = {
                    page: this.currentPage - 1,
                    size: this.itemsPerPage,
                };
                console.log('Params:', params);
                const response = await hotelApi.searchHotels(params);
                console.log('Response từ API:', response);

                if (response.data && response.data.data) {
                    this.hotels = response.data.data.content || response.data.data.items || [];
                    console.log('Danh sách khách sạn đã load:', this.hotels);
                } else {
                    console.error('Cấu trúc response không đúng:', response);
                    this.hotels = [];
                }
            } catch (error) {
                console.error('Lỗi khi lấy danh sách khách sạn:', error);
                this.hotels = [];
            }
        },
        handleOutsideClick(event) {
            if (this.activeDropdown !== null) {
                const menu = this.$refs.dropdownMenuRef;
                const btn = this.dropdownBtnRefMap && this.dropdownBtnRefMap[this.activeDropdown];
                if (
                    (!menu || !menu.contains(event.target)) &&
                    (!btn || !btn.contains(event.target))
                ) {
                    this.activeDropdown = null;
                    this.removeDropdownListeners();
                }
            }
            if (this.showFilterDropdown) {
                const filterContainerElement = this.$refs.filterDropdownContainer;
                if (filterContainerElement && !filterContainerElement.contains(event.target)) {
                    this.showFilterDropdown = false;
                }
            }
        },
        isNearBottom(index) {
            return index >= this.paginatedHotels.length - 3;
        },
        openDropdown(hotelId, event) {
            if (this.activeDropdown === hotelId) {
                this.activeDropdown = null;
                this.removeDropdownListeners();
                return;
            }
            this.activeDropdown = hotelId;
            this.$nextTick(() => {
                console.log('openDropdown: refs map', this.dropdownBtnRefMap);
                this.updateDropdownPosition();
                this.addDropdownListeners();
            });
            const hotel = this.paginatedHotels.find(h => h.id === hotelId);
            this.dropdownHotel = hotel;
            this.dropdownHotelName = hotel ? hotel.name : '';
        },
        updateDropdownPosition() {
            if (!this.activeDropdown) return;
            const btn = this.dropdownBtnRefMap && this.dropdownBtnRefMap[this.activeDropdown];
            if (!btn) {
                this.dropdownStyle = {
                    top: '40vh',
                    left: '50vw',
                    transform: 'translate(-50%, 0)',
                };
                console.warn('Không lấy được ref button, fallback ra giữa màn hình');
                return;
            }
            const rect = btn.getBoundingClientRect();
            const menuHeight = 260;
            const spaceBelow = window.innerHeight - rect.bottom;
            const spaceAbove = rect.top;
            let top, left;
            if (spaceBelow >= menuHeight + 16) {
                top = rect.bottom + 4;
            } else if (spaceAbove >= menuHeight + 16) {
                top = rect.top - menuHeight - 4;
            } else if (spaceBelow >= spaceAbove) {
                top = window.innerHeight - menuHeight - 8;
                if (top < 0) top = 8;
            } else {
                top = 8;
            }
            left = rect.right - 256 - 28;
            if (left < 8) left = 8;
            this.dropdownStyle = {
                top: top + 'px',
                left: left + 'px',
            };
            console.log('updateDropdownPosition: style', this.dropdownStyle);
        },
        addDropdownListeners() {
            window.addEventListener('scroll', this.updateDropdownPosition, true);
            window.addEventListener('resize', this.updateDropdownPosition);
        },
        removeDropdownListeners() {
            window.removeEventListener('scroll', this.updateDropdownPosition, true);
            window.removeEventListener('resize', this.updateDropdownPosition);
        },
        setDropdownBtnRef(el, id) {
            if (!this.dropdownBtnRefMap) this.dropdownBtnRefMap = {};
            if (el) {
                this.dropdownBtnRefMap[id] = el;
            } else {
                delete this.dropdownBtnRefMap[id];
            }
        },
        addVariant(roomIdx) {
            if (!this.newHotel.rooms[roomIdx].variants) this.newHotel.rooms[roomIdx].variants = [];
            this.newHotel.rooms[roomIdx].variants.push({
                variantName: '',
                price: 0,
                hasBreakfast: false,
                cancellable: false,
                payAtHotel: false,
                taxAndFeeAmount: 0,
            });
        },
        removeVariant(roomIdx, vIdx) {
            this.newHotel.rooms[roomIdx].variants.splice(vIdx, 1);
        },
        closeAmenityDropdown(e) {
            if (!this.$el.querySelector('.relative').contains(e.target)) {
                this.showAmenityDropdown = false;
            }
        },
    },
    mounted() {
        this.fetchHotels();
        document.addEventListener('click', this.handleOutsideClick, true);
        document.addEventListener('click', this.closeAmenityDropdown, true);
    },
    beforeUnmount() {
        document.removeEventListener('click', this.handleOutsideClick, true);
        this.removeDropdownListeners();
        document.removeEventListener('click', this.closeAmenityDropdown, true);
    }
};
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
    transition: opacity .3s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}

input[type="range"]::-webkit-slider-thumb {
    -webkit-appearance: none;
    appearance: none;
    width: 16px;
    height: 16px;
    border-radius: 50%;
    background: #2563EB;
    cursor: pointer;
    box-shadow: 0 0 2px rgba(0, 0, 0, 0.3);
    margin-top: -6px;
}

input[type="range"]::-moz-range-thumb {
    width: 16px;
    height: 16px;
    border-radius: 50%;
    background: #2563EB;
    cursor: pointer;
    box-shadow: 0 0 2px rgba(0, 0, 0, 0.3);
}

input[type="range"]::-webkit-slider-runnable-track {
    width: 100%;
    height: 4px;
    background: #E2E8F0;
    border-radius: 2px;
}

input[type="range"]::-moz-range-track {
    width: 100%;
    height: 4px;
    background: #E2E8F0;
    border-radius: 2px;
}
</style>
