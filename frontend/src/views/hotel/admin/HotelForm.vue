<template>
    <div class="w-full p-6">
        <div class="flex space-x-4 mb-6">
            <button :class="mainTab === 0 ? 'bg-blue-600 text-white' : 'bg-slate-100 text-slate-700'"
                class="px-6 py-2 rounded-md font-semibold shadow-sm" @click="switchMainTab(0)">
                Danh sách khách sạn
            </button>
            <button :class="mainTab === 1 ? 'bg-blue-600 text-white' : 'bg-slate-100 text-slate-700'"
                class="px-6 py-2 rounded-md font-semibold shadow-sm" @click="switchMainTab(1)">
                Thêm / Chỉnh sửa khách sạn
            </button>
        </div>
        <div v-if="mainTab === 0">
            <div class="flex justify-between items-center mb-6">
                <h1 class="text-2xl font-bold text-slate-800">Danh sách khách sạn</h1>
                <button @click="addHotel" class="bg-black hover:bg-gray-800 text-white px-4 py-2 rounded-md shadow-sm">
                    Thêm khách sạn
                </button>
            </div>

            <div
                class="flex flex-col sm:flex-row justify-between items-center mb-6 space-y-4 sm:space-y-0 sm:space-x-4">
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
                                        :class="{ 'bg-blue-600 text-white': tempFilterStar === n, 'bg-slate-100 text-slate-700 hover:bg-slate-200': tempFilterStar !== n }"
                                        class="px-3 py-1 rounded-full text-sm font-medium transition-colors duration-200">
                                        {{ n }} <i class="fas fa-star text-yellow-400"></i>
                                    </button>
                                    <button @click="setFilterStar('')"
                                        :class="{ 'bg-blue-600 text-white': tempFilterStar === '', 'bg-slate-100 text-slate-700 hover:bg-slate-200': tempFilterStar !== '' }"
                                        class="px-3 py-1 rounded-full text-sm font-medium transition-colors duration-200">
                                        Tất cả
                                    </button>
                                </div>
                            </div>

                            <div class="mb-5">
                                <label class="block text-sm font-semibold text-slate-700 mb-2">Giá mỗi đêm (tối
                                    đa):</label>
                                <input type="range" v-model="tempFilterPriceMax" min="0" max="20000000" step="100000"
                                    class="w-full h-2 bg-blue-200 rounded-lg appearance-none cursor-pointer accent-blue-600">
                                <div class="flex justify-between text-sm font-medium text-slate-700 mt-2">
                                    <span>Giá tối đa:</span>
                                    <span class="text-blue-600">{{ formatCurrency(tempFilterPriceMax) }} VND</span>
                                </div>
                            </div>

                            <div class="mb-5">
                                <label class="block text-sm font-semibold text-slate-700 mb-2">Thời gian tạo:</label>
                                <div class="flex flex-wrap gap-2 mb-2">
                                    <button v-for="preset in createdAtPresets" :key="preset.value"
                                        @click="setCreatedAtPreset(preset.value)"
                                        :class="tempFilterCreatedAtPreset === preset.value ? 'bg-blue-600 text-white' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'"
                                        class="px-3 py-1 rounded-full text-xs font-medium transition-colors duration-200">
                                        {{ preset.label }}
                                    </button>
                                </div>
                                <div v-if="tempFilterCreatedAtPreset === 'custom'" class="flex gap-2 items-center">
                                    <input type="date" v-model="tempFilterCreatedAtFrom"
                                        class="border border-slate-300 rounded px-2 py-1 text-sm" />
                                    <span>-</span>
                                    <input type="date" v-model="tempFilterCreatedAtTo"
                                        class="border border-slate-300 rounded px-2 py-1 text-sm" />
                                </div>
                            </div>
                        </div>

                        <div class="p-5 pt-4 border-t border-slate-100 mt-auto flex-shrink-0">
                            <div class="flex justify-end">
                                <button @click="resetFilters"
                                    class="bg-slate-100 text-slate-700 hover:bg-slate-200 px-4 py-2 rounded-md text-sm font-medium transition-colors duration-200 shadow-sm">
                                    <i class="fas fa-redo-alt mr-2"></i> Đặt lại
                                </button>
                                <button @click="applyFilters"
                                    class="bg-blue-600 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors duration-200 shadow-sm ml-2">
                                    <i class="fas fa-check mr-2"></i> Áp dụng
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
                <div class="overflow-x-auto w-full">
                    <table class="min-w-full w-full divide-y divide-slate-200">
                        <thead class="bg-gradient-to-r from-slate-100 to-slate-200">
                            <tr>
                                <th
                                    class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">
                                    <div class="flex items-center space-x-1">
                                        <i class="fas fa-hotel text-blue-500"></i>
                                        <span>Tên khách sạn</span>
                                    </div>
                                </th>
                                <th
                                    class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">
                                    <div class="flex items-center space-x-1">
                                        <i class="fas fa-star text-yellow-500"></i>
                                        <span>Hạng sao</span>
                                    </div>
                                </th>
                                <th
                                    class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">
                                    <div class="flex items-center space-x-1">
                                        <i class="fas fa-map-marker-alt text-red-500"></i>
                                        <span>Thành phố</span>
                                    </div>
                                </th>
                                <th
                                    class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">
                                    <div class="flex items-center space-x-1">
                                        <i class="fas fa-money-bill-wave text-green-500"></i>
                                        <span>Giá từ (VND)</span>
                                    </div>
                                </th>
                                <th
                                    class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">
                                    <div class="flex items-center space-x-1">
                                        <i class="fas fa-calendar-plus text-green-500"></i>
                                        <span>Ngày tạo</span>
                                    </div>
                                </th>
                                <th
                                    class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">
                                    <div class="flex items-center space-x-1">
                                        <i class="fas fa-calendar-edit text-blue-500"></i>
                                        <span>Ngày sửa</span>
                                    </div>
                                </th>
                                <th
                                    class="px-3 py-4 text-right text-xs font-bold text-slate-700 uppercase tracking-wider">
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
                                <td class="px-3 py-5 whitespace-nowrap">
                                    <div class="flex items-center space-x-3">
                                        <div>
                                            <div class="text-sm font-semibold text-slate-900">{{ h.name }}</div>
                                        </div>
                                    </div>
                                </td>
                                <td class="px-3 py-5 whitespace-nowrap">
                                    <div class="flex items-center space-x-1">
                                        <span class="text-sm font-medium text-slate-900">{{ h.starRating }}</span>
                                        <div class="flex space-x-0.5">
                                            <i v-for="star in h.starRating" :key="star"
                                                class="fas fa-star text-yellow-400 text-xs"></i>
                                        </div>
                                    </div>
                                </td>
                                <td class="px-3 py-5 whitespace-nowrap">
                                    <div class="flex items-center space-x-2">
                                        <i class="fas fa-map-marker-alt text-red-400 text-xs"></i>
                                        <span class="text-sm font-medium text-slate-700">{{ h.provinceName }}</span>
                                    </div>
                                </td>
                                <td class="px-3 py-5 whitespace-nowrap">
                                    <div class="text-sm font-bold text-green-600">
                                        {{ formatCurrency(h.startingPrice) }}
                                    </div>
                                    <div class="text-xs text-slate-500">VND/đêm</div>
                                </td>
                                <td class="px-3 py-5 whitespace-nowrap">
                                    <span class="text-xs text-slate-700">{{ formatDateTime(h.createdAt) }}</span>
                                </td>
                                <td class="px-3 py-5 whitespace-nowrap">
                                    <span class="text-xs text-slate-700">{{ formatDateTime(h.updatedAt) }}</span>
                                </td>
                                <td class="px-3 py-5 whitespace-nowrap text-right sticky right-0 bg-white z-10">
                                    <div class="relative inline-block text-left">
                                        <button :ref="el => setDropdownBtnRef(el, h.id)"
                                            @click.stop="toggleDropdown(h.id)" type="button"
                                            class="inline-flex justify-center w-10 h-10 rounded-lg text-slate-500 hover:text-slate-700 hover:bg-slate-100 focus:outline-none transition-all duration-200 shadow-sm">
                                            <i class="fas fa-ellipsis-v flex items-center justify-center text-sm"></i>
                                        </button>
                                        <teleport to="body">
                                            <div v-if="activeDropdown === h.id"
                                                :style="{ position: 'absolute', top: dropdownMenuPosition.top + 'px', left: dropdownMenuPosition.left + 'px', right: 'auto', zIndex: 9999 }"
                                                class="min-w-40 bg-white border border-slate-200 rounded-lg shadow-lg">
                                                <button class="block w-full text-left px-4 py-2 hover:bg-blue-50"
                                                    @click="editHotel(h)"><i class="fas fa-edit mr-2"></i>Chỉnh
                                                    sửa</button>
                                                <button
                                                    class="block w-full text-left px-4 py-2 hover:bg-red-50 text-red-600"
                                                    @click="deleteHotel(h.id)"><i
                                                        class="fas fa-trash-alt mr-2"></i>Xóa</button>
                                            </div>
                                        </teleport>
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
        </div>
        <div v-else>
            <div class="bg-white rounded-xl shadow-lg border border-slate-200 p-8 max-w-5xl mx-auto">
                <div class="flex justify-between items-center border-b border-slate-200 pb-4 mb-4">
                    <h2 class="text-xl font-bold">{{ isEditMode ? 'Chỉnh sửa khách sạn' : 'Thêm khách sạn mới' }}</h2>
                </div>
                <form class="space-y-8" autocomplete="off" novalidate @submit.prevent="submitHotel">
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
                        <div>
                            <label class="block text-sm font-semibold text-slate-700 mb-1">Tên khách sạn</label>
                            <input v-model="newHotel.name" type="text" placeholder="Nhập tên khách sạn"
                                class="w-full border border-slate-300 rounded-md px-3 py-2 placeholder-slate-400 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                        </div>
                        <div>
                            <label class="block text-sm font-semibold text-slate-700 mb-1">Hạng sao</label>
                            <select v-model="newHotel.starRating"
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
                        <div>
                            <label class="block text-sm font-semibold text-slate-700 mb-1">Địa chỉ</label>
                            <input v-model="newHotel.address" type="text" placeholder="Nhập địa chỉ khách sạn"
                                class="w-full border border-slate-300 rounded-md px-3 py-2 placeholder-slate-400 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                        </div>
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
                        <label class="block text-sm font-semibold text-slate-700 mb-2">Hình ảnh khách sạn</label>
                        <input type="file" multiple accept="image/*" ref="hotelImageInput" style="display:none"
                            @change="handleHotelImageChange" />
                        <button type="button" @click="$refs.hotelImageInput.click()"
                            class="w-40 h-28 border-2 border-dashed border-slate-300 rounded-md flex flex-col items-center justify-center text-slate-500 hover:border-blue-400 hover:text-blue-600 transition-colors">
                            <i class="far fa-image text-2xl mb-1"></i>
                            <span class="text-sm font-semibold">Thêm ảnh</span>
                        </button>
                        <div class="flex flex-wrap gap-3 mt-2">
                            <div v-for="(img, idx) in newHotel.imageUrls" :key="'old-' + idx" class="relative group">
                                <img :src="img"
                                    class="w-20 h-20 object-cover rounded-lg border border-slate-200 shadow-sm" />
                                <button @click="removeHotelImage(idx, true)"
                                    class="absolute top-0 right-0 bg-red-500 text-white rounded-full p-1 text-xs opacity-80 group-hover:opacity-100">✕</button>
                            </div>
                            <div v-for="(img, idx) in hotelImagePreviews" :key="'new-' + idx" class="relative group">
                                <img :src="img"
                                    class="w-20 h-20 object-cover rounded-lg border border-slate-200 shadow-sm" />
                                <button @click="removeHotelImage(idx)"
                                    class="absolute top-0 right-0 bg-red-500 text-white rounded-full p-1 text-xs opacity-80 group-hover:opacity-100">✕</button>
                            </div>
                        </div>
                    </div>
                    <div>
                        <div class="flex justify-between items-center mb-4">
                            <h3 class="font-semibold text-slate-800 text-base">Thông tin các loại phòng</h3>
                            <button type="button" @click="addRoom"
                                class="text-blue-600 font-semibold border border-blue-600 rounded-md px-4 py-2 hover:bg-blue-50 transition shadow-sm">
                                + Thêm loại phòng
                            </button>
                        </div>
                        <div v-for="(r, idx) in newHotel.availableRooms" :key="idx"
                            class="mb-6 border border-slate-200 rounded-xl shadow-sm p-6">
                            <div class="flex justify-between items-center mb-2">
                                <div class="font-semibold text-slate-800 text-base">{{ r.roomType || 'Loại phòng mới' }}
                                </div>
                                <button type="button" @click="removeRoom(idx)"
                                    class="text-red-500 hover:underline text-xs ml-2">Xóa</button>
                            </div>
                            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-4">
                                <div>
                                    <label class="block text-sm font-semibold text-slate-700 mb-1">Loại phòng</label>
                                    <input v-model="r.roomType" type="text"
                                        placeholder="Ví dụ: Phòng Deluxe, Phòng Superior"
                                        class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                                </div>
                                <div>
                                    <label class="block text-sm font-semibold text-slate-700 mb-1">Giường</label>
                                    <input v-model="r.bedType" type="text" placeholder="Nhập loại giường"
                                        class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                                </div>
                                <div>
                                    <label class="block text-sm font-semibold text-slate-700 mb-1">Diện tích
                                        (m²)</label>
                                    <input v-model.number="r.roomArea" type="number" min="0"
                                        placeholder="Nhập diện tích phòng"
                                        class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                                </div>
                            </div>
                            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-4">
                                <div>
                                    <label class="block text-sm font-semibold text-slate-700 mb-1">Số lượng
                                        phòng</label>
                                    <input v-model.number="r.roomQuantity" type="number" min="0"
                                        placeholder="Nhập số lượng phòng"
                                        class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                                </div>
                                <div class="grid grid-cols-2 gap-4">
                                    <div>
                                        <label class="block text-sm font-semibold text-slate-700 mb-1">Người lớn</label>
                                        <input v-model.number="r.maxAdults" type="number" min="0"
                                            placeholder="Số người lớn"
                                            class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                                    </div>
                                    <div>
                                        <label class="block text-sm font-semibold text-slate-700 mb-1">Trẻ em</label>
                                        <input v-model.number="r.maxChildren" type="number" min="0"
                                            placeholder="Số trẻ em"
                                            class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                                    </div>
                                </div>
                            </div>
                            <div>
                                <label class="block text-sm font-semibold text-slate-700 mb-1">Tiện ích phòng</label>
                                <div class="flex flex-wrap gap-3">
                                    <label v-for="a in amenities" :key="a.id" class="flex items-center space-x-2">
                                        <input type="checkbox" v-model="r.amenities[a.id]"
                                            class="w-4 h-4 text-blue-600 border-slate-300 rounded focus:ring-blue-500" />
                                        <span>{{ a.name }}</span>
                                    </label>
                                </div>
                            </div>
                            <div class="mt-4">
                                <label class="block text-sm font-semibold text-slate-700 mb-1">Hình ảnh phòng</label>
                                <input type="file" multiple accept="image/*" :ref="`roomImageInput${idx}`"
                                    style="display:none" @change="e => handleRoomImageChange(e, idx)" />
                                <button type="button" @click="triggerRoomImageInput(idx)"
                                    class="w-40 h-28 border-2 border-dashed border-slate-300 rounded-md flex flex-col items-center justify-center text-slate-500 hover:border-blue-400 hover:text-blue-600 transition-colors">
                                    <i class="far fa-image text-2xl mb-1"></i>
                                    <span class="text-sm font-semibold">Thêm ảnh</span>
                                </button>
                                <div class="flex flex-wrap gap-3 mt-2">
                                    <div v-for="(img, imgIdx) in r.imagePreviews" :key="'roomimg-' + imgIdx"
                                        class="relative group">
                                        <img :src="img"
                                            class="w-20 h-20 object-cover rounded-lg border border-slate-200 shadow-sm" />
                                        <button @click="removeRoomImage(idx, imgIdx)"
                                            class="absolute top-0 right-0 bg-red-500 text-white rounded-full p-1 text-xs opacity-80 group-hover:opacity-100">✕</button>
                                    </div>
                                    <div v-for="(img, imgIdx) in r.imageUrls" :key="'oldroomimg-' + imgIdx"
                                        class="relative group">
                                        <img :src="img"
                                            class="w-20 h-20 object-cover rounded-lg border border-slate-200 shadow-sm" />
                                        <button @click="removeRoomImage(idx, imgIdx, true)"
                                            class="absolute top-0 right-0 bg-red-500 text-white rounded-full p-1 text-xs opacity-80 group-hover:opacity-100">✕</button>
                                    </div>
                                </div>
                            </div>
                            <div class="mt-6">
                                <div class="flex justify-between items-center mb-2">
                                    <h3 class="text-base font-semibold text-slate-800">Gói phòng</h3>
                                    <button type="button" @click="addVariant(idx)"
                                        class="text-blue-600 text-xs font-semibold hover:underline">+ Thêm gói
                                        phòng</button>
                                </div>
                                <div class="overflow-x-auto rounded-lg">
                                    <table class="w-full text-xs border border-slate-100 rounded-lg overflow-hidden">
                                        <thead>
                                            <tr class="bg-slate-100 text-slate-700">
                                                <th class="py-2 px-3 font-semibold text-left">Tên gói</th>
                                                <th class="py-2 px-3 font-semibold text-center">Giá</th>
                                                <th class="py-2 px-3 font-semibold text-center">Bữa sáng</th>
                                                <th class="py-2 px-3 font-semibold text-center">Hủy miễn phí</th>
                                                <th class="py-2 px-3 font-semibold text-center">Thanh toán tại KS</th>
                                                <th class="py-2 px-3 font-semibold text-center">Thuế/Phí</th>
                                                <th class="py-2 px-3 font-semibold text-center">Xóa</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr v-for="(v, vIdx) in r.availableVariants" :key="vIdx"
                                                class="border-t border-slate-100">
                                                <td class="py-2 px-3">
                                                    <input v-model="v.variantName" type="text" placeholder="Tên gói"
                                                        class="w-full border border-slate-200 rounded px-2 py-1 text-xs" />
                                                </td>
                                                <td class="py-2 px-3">
                                                    <input v-model.number="v.price" type="number" min="0"
                                                        placeholder="Giá"
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
                                                    <input v-model.number="v.taxAndFeeAmount" type="number" min="0"
                                                        placeholder="Thuế/Phí"
                                                        class="w-full border border-slate-200 rounded px-2 py-1 text-xs" />
                                                </td>
                                                <td class="py-2 px-3 text-center">
                                                    <button type="button" @click="removeVariant(idx, vIdx)"
                                                        class="text-red-500 hover:underline text-xs">Xóa</button>
                                                </td>
                                            </tr>
                                            <tr v-if="!r.availableVariants || r.availableVariants.length === 0">
                                                <td colspan="7" class="text-center text-slate-400 py-4">Chưa có gói
                                                    phòng nào
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="flex justify-end mt-8">
                        <button type="submit"
                            class="font-semibold text-white bg-black rounded-md px-6 py-2 hover:bg-gray-800 transition shadow-sm">
                            {{ isEditMode ? 'Cập nhật' : 'Thêm khách sạn' }}
                        </button>
                    </div>
                </form>
            </div>
        </div>
        <ConfirmDialog v-if="showConfirmDialog" :message="confirmMessage" @confirm="onConfirmDelete"
            @cancel="showConfirmDialog = false" />
    </div>
</template>

<script>
import hotelApi from '@/api/hotelApi';
import HotelDetailModal from './HotelDetailModal.vue';
import provinceApi from '@/api/provinceApi';
import AmenityApi from '@/api/AmenityApi';
import ConfirmDialog from '@/components/ConfirmDialog.vue';
export default {
    name: 'HotelManager',
    components: { HotelDetailModal, ConfirmDialog },
    data() {
        return {
            mainTab: 0,
            isEditMode: false,
            showModal: false, activeTab: 0, tabs: ['Thông tin cơ bản', 'Phòng & Giá'],
            hotels: [],
            newHotel: {
                id: null,
                name: '',
                starRating: '',
                address: '',
                phone: '',
                email: '',
                description: '',
                imageUrls: [],
                availableRooms: [
                    {
                        id: null,
                        roomType: '',
                        bedType: '',
                        roomArea: 0,
                        roomQuantity: 0,
                        maxAdults: 0,
                        maxChildren: 0,
                        imageUrls: [],
                        imageFiles: [],
                        imagePreviews: [],
                        amenities: {},
                        availableVariants: [],
                    }
                ],
                amenities: [],
                policy: {}
            },
            modalMode: 'add', activeDropdown: null,
            searchQuery: '', showFilterDropdown: false, filterStar: '', filterPriceMax: 20000000,
            filterAmenities: {},
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
            hotelImages: [],
            hotelImagePreviews: [],
            imagesToDelete: [],
            filterCreatedAtPreset: '',
            filterCreatedAtFrom: '',
            filterCreatedAtTo: '',
            createdAtPresets: [
                { label: 'Hôm nay', value: 'today' },
                { label: 'Hôm qua', value: 'yesterday' },
                { label: '3 ngày trước', value: 'last3days' },
                { label: 'Tuần này', value: 'thisweek' },
                { label: 'Tuần trước', value: 'lastweek' },
                { label: 'Tháng này', value: 'thismonth' },
                { label: 'Tháng trước', value: 'lastmonth' },
                { label: 'Tùy chọn', value: 'custom' }
            ],
            tempFilterStar: '',
            tempFilterPriceMax: 20000000,
            tempFilterCreatedAtPreset: '',
            tempFilterCreatedAtFrom: '',
            tempFilterCreatedAtTo: '',
            dropdownMenuPosition: { top: 0, left: 0 },
            showConfirmDialog: false,
            confirmMessage: '',
            hotelIdToDelete: null,
        };
    },
    computed: {
        filteredHotels() {
            return [...this.hotels].sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt));
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
        searchQuery() { this.currentPage = 1; this.fetchHotels(); },
        itemsPerPage() { this.currentPage = 1; this.fetchHotels(); },
    },
    methods: {
        formatCurrency(value) { return value == null ? 'N/A' : new Intl.NumberFormat('vi-VN').format(value); },
        async openModal(mode, hotel = null) {
            this.activeDropdown = null;
            this.hotelImages = [];
            this.hotelImagePreviews = [];
            this.imagesToDelete = [];
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
                        starRating: detail.starRating || '',
                        address: detail.address || '',
                        phone: detail.phone || '',
                        email: detail.email || '',
                        description: detail.description || '',
                        imageUrls: detail.imageUrls || [],
                        availableRooms: (detail.availableRooms || []).map(room => ({
                            id: room.id || null,
                            roomType: room.roomType || '',
                            bedType: room.bedType || '',
                            roomArea: room.roomArea || 0,
                            roomQuantity: room.roomQuantity || 0,
                            maxAdults: room.maxAdults || 0,
                            maxChildren: room.maxChildren || 0,
                            imageUrls: room.imageUrls || [],
                            imageFiles: room.imageFiles || [],
                            imagePreviews: room.imagePreviews || [],
                            amenities: (room.amenities || []).reduce((acc, a) => { acc[a.id] = true; return acc; }, {}),
                            availableVariants: (room.availableVariants || []).map(variant => ({
                                id: variant.id || null,
                                variantName: variant.variantName || '',
                                price: (variant.price && typeof variant.price === 'object' && variant.price !== null) ? Number(variant.price) : (variant.price || 0),
                                hasBreakfast: variant.hasBreakfast || false,
                                cancellable: variant.cancellable || false,
                                payAtHotel: variant.payAtHotel || false,
                                taxAndFeeAmount: (variant.taxAndFeeAmount && typeof variant.taxAndFeeAmount === 'object' && variant.taxAndFeeAmount !== null) ? Number(variant.taxAndFeeAmount) : (variant.taxAndFeeAmount || 0),
                            })),
                        })),
                        amenities: (detail.amenities || []).reduce((acc, a) => { acc[a.id] = true; return acc; }, {}),
                        policy: detail.policy || {},
                    };
                } catch (e) {
                    window.$toast('Không lấy được chi tiết khách sạn!', 'error');
                    return;
                }
            }
            this.showModal = true;
        },
        closeModal() { this.showModal = false; this.activeDropdown = null; this.removeDropdownListeners(); },
        toggleDropdown(hotelId) {
            if (this.activeDropdown === hotelId) {
                this.activeDropdown = null;
                return;
            }
            this.activeDropdown = hotelId;
            this.$nextTick(() => {
                const btn = this.dropdownBtnRefMap && this.dropdownBtnRefMap[hotelId];
                if (btn) {
                    const rect = btn.getBoundingClientRect();
                    this.dropdownMenuPosition = {
                        top: rect.bottom + window.scrollY - 20,
                        left: rect.left + window.scrollX - 165,
                    };
                }
            });
        },
        toggleFilterDropdown() {
            this.showFilterDropdown = !this.showFilterDropdown;
            if (this.showFilterDropdown) {
                this.tempFilterStar = this.filterStar;
                this.tempFilterPriceMax = this.filterPriceMax;
                this.tempFilterCreatedAtPreset = this.filterCreatedAtPreset;
                this.tempFilterCreatedAtFrom = this.filterCreatedAtFrom;
                this.tempFilterCreatedAtTo = this.filterCreatedAtTo;
            }
        },
        setFilterStar(star) {
            this.tempFilterStar = star;
        },
        resetFilters() {
            this.tempFilterStar = '';
            this.tempFilterPriceMax = 20000000;
            this.tempFilterCreatedAtPreset = '';
            this.tempFilterCreatedAtFrom = '';
            this.tempFilterCreatedAtTo = '';
            this.filterStar = '';
            this.filterPriceMax = 20000000;
            this.filterCreatedAtPreset = '';
            this.filterCreatedAtFrom = '';
            this.filterCreatedAtTo = '';
            this.showFilterDropdown = false;
            this.currentPage = 1;
            this.fetchHotels();
        },
        async viewDetails(hotel) {
            try {
                const res = await hotelApi.getHotelById(hotel.id);
                this.detailHotel = res.data.data;
                this.showDetailModal = true;
            } catch (e) {
                window.$toast('Không lấy được chi tiết khách sạn!', 'error');
            }
            this.activeDropdown = null;
        },
        addRoom() {
            this.newHotel.availableRooms.push({
                id: null,
                roomType: '',
                bedType: '',
                roomArea: 0,
                roomQuantity: 0,
                maxAdults: 0,
                maxChildren: 0,
                imageUrls: [],
                imageFiles: [],
                imagePreviews: [],
                amenities: {},
                availableVariants: [],
            });
        },
        removeRoom(index) {
            if (this.newHotel.availableRooms.length > 1) {
                this.newHotel.availableRooms.splice(index, 1);
            } else {
                window.$toast('Phải có ít nhất một loại phòng.', 'error');
            }
        },
        deleteHotel(hotelId) {
            this.hotelIdToDelete = hotelId;
            this.confirmMessage = 'Bạn có chắc chắn muốn xóa khách sạn này không?';
            this.showConfirmDialog = true;
            this.activeDropdown = null;
        },
        async onConfirmDelete() {
            this.showConfirmDialog = false;
            try {
                await hotelApi.deleteHotel(this.hotelIdToDelete);
                window.$toast('Xóa khách sạn thành công!', 'success');
                this.fetchHotels();
            } catch (e) {
                window.$toast('Xóa khách sạn thất bại!', 'error');
            }
            this.hotelIdToDelete = null;
        },
        handleSubmit() {
            let minPrice = Infinity;
            this.newHotel.availableRooms.forEach(r => { const finalPrice = r.price * (1 - (r.discount || 0) / 100); if (finalPrice < minPrice) minPrice = finalPrice; });
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
                starRating: '',
                address: '',
                phone: '',
                email: '',
                description: '',
                imageUrls: [],
                availableRooms: [
                    {
                        id: null,
                        roomType: '',
                        bedType: '',
                        roomArea: 0,
                        roomQuantity: 0,
                        maxAdults: 0,
                        maxChildren: 0,
                        imageUrls: [],
                        imageFiles: [],
                        imagePreviews: [],
                        amenities: {},
                        availableVariants: [],
                    }
                ],
                amenities: [],
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
                const params = {
                    keyword: this.searchQuery,
                    minStarRating: this.filterStar || undefined,
                    minPrice: 0,
                    maxPrice: this.filterPriceMax < 20000000 ? this.filterPriceMax : undefined,
                    createdAtFrom: this.filterCreatedAtFrom || undefined,
                    createdAtTo: this.filterCreatedAtTo || undefined,
                    page: this.currentPage - 1,
                    size: this.itemsPerPage === 'Tất cả' ? 1000 : this.itemsPerPage,
                };
                const response = await hotelApi.searchHotels(params);
                if (response.data && response.data.data) {
                    this.hotels = response.data.data.content || response.data.data.items || [];
                } else {
                    this.hotels = [];
                }
            } catch (error) {
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
            if (!this.newHotel.availableRooms[roomIdx].availableVariants) this.newHotel.availableRooms[roomIdx].availableVariants = [];
            this.newHotel.availableRooms[roomIdx].availableVariants.push({
                id: null,
                variantName: '',
                price: 0,
                hasBreakfast: false,
                cancellable: false,
                payAtHotel: false,
                taxAndFeeAmount: 0
            });
        },
        removeVariant(roomIdx, vIdx) {
            const variants = this.newHotel.availableRooms[roomIdx].availableVariants;
            if (variants.length <= 1) {
                window.$toast('Phòng phải có ít nhất một gói phòng.', 'error');
                return;
            }
            variants.splice(vIdx, 1);
        },
        closeAmenityDropdown(e) {
            const rel = this.$el.querySelector('.relative');
            if (!rel || !rel.contains(e.target)) {
                this.showAmenityDropdown = false;
            }
        },
        handleHotelImageChange(e) {
            const files = Array.from(e.target.files);
            for (const file of files) {
                this.hotelImages.push(file);
                const reader = new FileReader();
                reader.onload = (ev) => this.hotelImagePreviews.push(ev.target.result);
                reader.readAsDataURL(file);
            }
        },
        removeHotelImage(idx, isOld = false) {
            if (isOld) {
                const url = this.newHotel.imageUrls[idx];
                this.imagesToDelete.push(url);
                this.newHotel.imageUrls.splice(idx, 1);
            } else {
                this.hotelImages.splice(idx, 1);
                this.hotelImagePreviews.splice(idx, 1);
            }
        },
        handleRoomImageChange(e, roomIdx) {
            const files = Array.from(e.target.files);
            const room = this.newHotel.availableRooms[roomIdx];
            if (!room.imageFiles) room.imageFiles = [];
            if (!room.imagePreviews) room.imagePreviews = [];
            room.imageFiles.push(...files);
            for (const file of files) {
                const reader = new FileReader();
                reader.onload = (ev) => room.imagePreviews.push(ev.target.result);
                reader.readAsDataURL(file);
            }
        },
        triggerRoomImageInput(roomIdx) {
            this.$refs[`roomImageInput${roomIdx}`][0].click();
        },
        removeRoomImage(roomIdx, imgIdx, isOld = false) {
            const room = this.newHotel.availableRooms[roomIdx];
            if (isOld) {
                if (!room.deleteRoomImageUrls) room.deleteRoomImageUrls = [];
                const url = room.imageUrls[imgIdx];
                room.deleteRoomImageUrls.push(url);
                room.imageUrls.splice(imgIdx, 1);
            } else {
                room.imageFiles.splice(imgIdx, 1);
                room.imagePreviews.splice(imgIdx, 1);
            }
        },
        async submitHotel() {
            const hotelData = { ...this.newHotel };
            hotelData.availableRooms = hotelData.availableRooms.map((r, idx) => {
                const room = { ...r };
                if (!room.id) delete room.id;
                if (!room.imageUrls) room.imageUrls = [];
                room.amenities = Object.keys(room.amenities || {}).filter(k => room.amenities[k]).map(id => ({ id: Number(id) }));
                room.availableVariants = (room.availableVariants || []).map(v => ({
                    ...v,
                    hasBreakfast: !!v.hasBreakfast,
                    cancellable: !!v.cancellable,
                    payAtHotel: !!v.payAtHotel,
                    price: Number(v.price || 0),
                    taxAndFeeAmount: Number(v.taxAndFeeAmount || 0)
                }));
                delete room.imageFiles;
                delete room.imagePreviews;
                return room;
            });
            if (hotelData.amenities && !Array.isArray(hotelData.amenities)) {
                hotelData.amenities = Object.keys(hotelData.amenities).filter(k => hotelData.amenities[k]).map(id => ({ id: Number(id) }));
            }
            delete hotelData.policy;
            const invalidRoomIdx = hotelData.availableRooms.findIndex(r => {
                if (!r.availableVariants || r.availableVariants.length === 0) return true;
                const hasValidVariant = r.availableVariants.some(v => v.variantName && v.variantName.trim() && v.price > 0);
                return !hasValidVariant;
            });
            if (invalidRoomIdx !== -1) {
                window.$toast(`Vui lòng tạo ít nhất một gói phòng hợp lệ (có tên và giá > 0) cho phòng số ${invalidRoomIdx + 1} (${hotelData.availableRooms[invalidRoomIdx].roomType || 'Chưa đặt tên'})`, 'error');
                return;
            }
            if (hotelData.availableRooms && hotelData.availableRooms.length) {
                hotelData.availableRooms.forEach(room => {
                    if (room.deleteRoomImageUrls) {
                        delete room.deleteRoomImageUrls;
                    }
                });
            }
            const formData = new FormData();
            if (!hotelData) {
                window.$toast('Dữ liệu khách sạn bị rỗng!', 'error');
                return;
            }
            const hotelJson = JSON.stringify(hotelData);
            formData.append('hotel', hotelJson);
            if (this.hotelImages && this.hotelImages.length) {
                for (let i = 0; i < this.hotelImages.length; i++) {
                    formData.append('images', this.hotelImages[i]);
                }
            }
            if (this.newHotel.availableRooms && this.newHotel.availableRooms.length) {
                this.newHotel.availableRooms.forEach((room, idx) => {
                    let key = '';
                    if (room.id) {
                        key = `roomImages_${room.id}`;
                    } else if (room.roomType) {
                        key = `roomImages_${room.roomType.normalize('NFD').replace(/\p{Diacritic}/gu, '').replace(/\s+/g, '_')}`;
                    } else {
                        key = `roomImages_${idx}`;
                    }
                    key = key.toLowerCase();
                    if (room.imageFiles && room.imageFiles.length) {
                        for (let j = 0; j < room.imageFiles.length; j++) {
                            formData.append(key, room.imageFiles[j]);
                        }
                    }
                });
            }
            if (this.imagesToDelete && this.imagesToDelete.length) {
                this.imagesToDelete.forEach(url => formData.append('deleteImageUrls', url));
            }
            if (this.newHotel.availableRooms && this.newHotel.availableRooms.length) {
                this.newHotel.availableRooms.forEach((room, idx) => {
                    if (room.deleteRoomImageUrls && Array.isArray(room.deleteRoomImageUrls) && room.deleteRoomImageUrls.length > 0) {
                        if (room.id) {
                            const key = `deleteRoomImageUrls_${room.id}`;
                            formData.append(key, JSON.stringify(room.deleteRoomImageUrls));
                        }
                    }
                });
            }
            try {
                let res;
                if (this.modalMode === 'add') {
                    res = await hotelApi.createHotel(formData);
                    window.$toast('Thêm khách sạn thành công!', 'success');
                } else {
                    res = await hotelApi.updateHotel(hotelData.id, formData);
                    window.$toast('Cập nhật khách sạn thành công!', 'success');
                }
                this.closeModal();
                await this.fetchHotels();
                this.mainTab = 0;
                this.resetAll();
            } catch (err) {
                window.$toast('Có lỗi khi lưu khách sạn: ' + (err?.response?.data?.message || err.message), 'error');
                console.error('API error:', err);
            }
        },
        switchMainTab(tabIdx) {
            this.mainTab = tabIdx;
            if (tabIdx === 1 && !this.isEditMode) {
                this.resetAll();
                this.loadProvincesAndAmenities();
            }
            if (tabIdx === 0) {
                this.resetAll();
                this.isEditMode = false;
            }
        },
        addHotel() {
            this.isEditMode = false;
            this.resetAll();
            this.loadProvincesAndAmenities();
            this.mainTab = 1;
        },
        async editHotel(hotel) {
            this.isEditMode = true;
            this.modalMode = 'edit';
            this.loadProvincesAndAmenities();
            this.hotelImages = [];
            this.hotelImagePreviews = [];
            this.imagesToDelete = [];
            try {
                const res = await hotelApi.getHotelById(hotel.id);
                const detail = res.data.data || {};
                this.newHotel = {
                    id: detail.id,
                    name: detail.name || '',
                    starRating: detail.starRating || '',
                    address: detail.address || '',
                    provinceId: detail.provinceId || '',
                    phone: detail.phone || '',
                    email: detail.email || '',
                    description: detail.description || '',
                    imageUrls: detail.imageUrls || [],
                    availableRooms: (detail.availableRooms || []).map(room => ({
                        id: room.id,
                        roomType: room.roomType || '',
                        bedType: room.bedType || '',
                        roomArea: room.roomArea || 0,
                        roomQuantity: room.roomQuantity || 0,
                        maxAdults: room.maxAdults || 0,
                        maxChildren: room.maxChildren || 0,
                        imageUrls: room.imageUrls || [],
                        imageFiles: [],
                        imagePreviews: [],
                        amenities: (room.amenities || []).reduce((acc, a) => { acc[a.id] = true; return acc; }, {}),
                        availableVariants: (room.availableVariants || []).map(variant => ({
                            id: variant.id || null,
                            variantName: variant.variantName || '',
                            price: (variant.price && typeof variant.price === 'object' && variant.price !== null) ? Number(variant.price) : (variant.price || 0),
                            hasBreakfast: variant.hasBreakfast || false,
                            cancellable: variant.cancellable || false,
                            payAtHotel: variant.payAtHotel || false,
                            taxAndFeeAmount: (variant.taxAndFeeAmount && typeof variant.taxAndFeeAmount === 'object' && variant.taxAndFeeAmount !== null) ? Number(variant.taxAndFeeAmount) : (variant.taxAndFeeAmount || 0),
                        })),
                    })),
                    amenities: (detail.amenities || []).reduce((acc, a) => { acc[a.id] = true; return acc; }, {}),
                };
            } catch (e) {
                window.$toast('Không lấy được chi tiết khách sạn!', 'error');
                return;
            }
            this.mainTab = 1;
        },
        loadProvincesAndAmenities() {
            if (!this.provinces.length) {
                provinceApi.getAllProvinces().then(res => {
                    this.provinces = res.data.data || res.data;
                });
            }
            if (!this.amenities.length) {
                AmenityApi.getAllAmenities().then(res => {
                    this.amenities = res.data.data || res.data;
                    this.filterAmenities = {};
                    this.amenities.forEach(a => { this.filterAmenities[a.id] = false; });
                });
            }
        },
        toggleAmenity(amenities, amenityKey) {
            amenities[amenityKey] = !amenities[amenityKey];
        },
        setCreatedAtPreset(preset) {
            this.tempFilterCreatedAtPreset = preset;
            const today = new Date();
            const startOfWeek = new Date(today);
            startOfWeek.setDate(today.getDate() - today.getDay() + 1);
            const endOfWeek = new Date(startOfWeek);
            endOfWeek.setDate(startOfWeek.getDate() + 6);
            const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
            const endOfMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0);
            if (preset === 'today') {
                this.tempFilterCreatedAtFrom = this.formatDate(today);
                this.tempFilterCreatedAtTo = this.formatDate(today);
            } else if (preset === 'yesterday') {
                const y = new Date(today); y.setDate(y.getDate() - 1);
                this.tempFilterCreatedAtFrom = this.formatDate(y);
                this.tempFilterCreatedAtTo = this.formatDate(y);
            } else if (preset === 'last3days') {
                const d = new Date(today); d.setDate(d.getDate() - 2);
                this.tempFilterCreatedAtFrom = this.formatDate(d);
                this.tempFilterCreatedAtTo = this.formatDate(today);
            } else if (preset === 'thisweek') {
                this.tempFilterCreatedAtFrom = this.formatDate(startOfWeek);
                this.tempFilterCreatedAtTo = this.formatDate(endOfWeek);
            } else if (preset === 'lastweek') {
                const lastWeekStart = new Date(startOfWeek); lastWeekStart.setDate(startOfWeek.getDate() - 7);
                const lastWeekEnd = new Date(endOfWeek); lastWeekEnd.setDate(endOfWeek.getDate() - 7);
                this.tempFilterCreatedAtFrom = this.formatDate(lastWeekStart);
                this.tempFilterCreatedAtTo = this.formatDate(lastWeekEnd);
            } else if (preset === 'thismonth') {
                this.tempFilterCreatedAtFrom = this.formatDate(startOfMonth);
                this.tempFilterCreatedAtTo = this.formatDate(endOfMonth);
            } else if (preset === 'lastmonth') {
                const lastMonthStart = new Date(today.getFullYear(), today.getMonth() - 1, 1);
                const lastMonthEnd = new Date(today.getFullYear(), today.getMonth(), 0);
                this.tempFilterCreatedAtFrom = this.formatDate(lastMonthStart);
                this.tempFilterCreatedAtTo = this.formatDate(lastMonthEnd);
            } else if (preset === 'custom') {
            }
        },
        applyFilters() {
            this.filterStar = this.tempFilterStar;
            this.filterPriceMax = this.tempFilterPriceMax;
            this.filterCreatedAtPreset = this.tempFilterCreatedAtPreset;
            this.filterCreatedAtFrom = this.tempFilterCreatedAtFrom;
            this.filterCreatedAtTo = this.tempFilterCreatedAtTo;
            this.showFilterDropdown = false;
            this.currentPage = 1;
            this.fetchHotels();
        },
        formatDate(date) {
            return date.toISOString().slice(0, 10);
        },
        formatDateTime(dt) {
            if (!dt) return '';
            const d = new Date(dt);
            return d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' }) + ' ' + d.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
        },
    },
    mounted() {
        this.fetchHotels();
        this.loadProvincesAndAmenities();
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

select option[disabled][value=""] {
    color: #a0aec0;
}

.table-actions-sticky {
    position: sticky;
    right: 0;
    background: white;
    z-index: 10;
}
</style>
