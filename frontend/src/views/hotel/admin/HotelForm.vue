<template>
    <div class="w-full p-6">
        <div v-if="mode === 'list'" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            <div class="bg-white rounded-xl shadow-md p-6 border border-slate-200 transition-all duration-300 hover:shadow-lg hover:-translate-y-1">
                <div class="flex items-start justify-between mb-4">
                    <div class="text-slate-600 font-semibold">Tổng số khách sạn</div>
                    <div class="bg-blue-50 p-3 rounded-full">
                        <i class="fas fa-hotel text-blue-500 text-xl"></i>
                    </div>
                </div>
                <div class="text-3xl font-bold text-slate-900">{{ hotelStatistics.totalHotels }}</div>
            </div>
            <div class="bg-white rounded-xl shadow-md p-6 border border-slate-200 transition-all duration-300 hover:shadow-lg hover:-translate-y-1">
                <div class="flex items-start justify-between mb-4">
                    <div class="text-slate-600 font-semibold">Tổng số phòng</div>
                    <div class="bg-green-50 p-3 rounded-full">
                        <i class="fas fa-door-open text-green-500 text-xl"></i>
                    </div>
                </div>
                <div class="text-3xl font-bold text-slate-900">{{ totalRooms }}</div>
            </div>
            <div class="bg-white rounded-xl shadow-md p-6 border transition-all duration-300 hover:shadow-lg hover:-translate-y-1 cursor-pointer" 
                 :class="isNearlyOutOfStockFilterActive ? 'border-orange-400 bg-orange-50' : 'border-slate-200'" 
                 @click="filterByRoomStatus('nearly_out_of_stock')">
                <div class="flex items-start justify-between mb-4">
                    <div class="text-slate-600 font-semibold">Tổng phòng gần hết</div>
                    <div class="bg-orange-50 p-3 rounded-full">
                        <i class="fas fa-exclamation-triangle text-orange-500 text-xl"></i>
                    </div>
                </div>
                <div class="text-3xl font-bold text-slate-900">{{ hotelStatistics.nearlyOutOfStockRooms }}</div>
                <div v-if="isNearlyOutOfStockFilterActive" class="mt-2 text-xs text-orange-600 font-medium">
                    <i class="fas fa-filter mr-1"></i>Đang lọc
                </div>
            </div>
            <div class="bg-white rounded-xl shadow-md p-6 border transition-all duration-300 hover:shadow-lg hover:-translate-y-1 cursor-pointer" 
                 :class="isOutOfStockFilterActive ? 'border-red-400 bg-red-50' : 'border-slate-200'" 
                 @click="filterByRoomStatus('out_of_stock')">
                <div class="flex items-start justify-between mb-4">
                    <div class="text-slate-600 font-semibold">Số lượng phòng đã hết</div>
                    <div class="bg-red-50 p-3 rounded-full">
                        <i class="fas fa-times-circle text-red-500 text-xl"></i>
                    </div>
                </div>
                <div class="text-3xl font-bold text-slate-900">{{ hotelStatistics.outOfStockRooms }}</div>
                <div v-if="isOutOfStockFilterActive" class="mt-2 text-xs text-red-600 font-medium">
                    <i class="fas fa-filter mr-1"></i>Đang lọc
                </div>
            </div>
        </div>
        <div v-if="mode === 'list'">
            <div class="mb-6">
                <h1 class="text-2xl font-bold text-slate-800 mb-4">Danh sách khách sạn</h1>
                <div class="flex flex-col sm:flex-row items-center gap-2">
                    <div class="flex flex-1 flex-col sm:flex-row items-center gap-2 w-full">
                        <div class="relative w-full sm:w-[300px]">
                            <input type="text" v-model="searchQuery" placeholder="Tìm theo tên khách sạn hoặc thành phố"
                                class="w-full sm:w-[350px] pl-10 pr-4 py-2 h-12 text-base border border-slate-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-slate-900" />
                            <i class="fas fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400"></i>
                        </div>
                        <div ref="filterDropdownContainer" class="relative sm:w-[140px]">
                            <button @click="toggleFilterDropdown"
                                class="bg-white ml-15 border border-slate-300 text-slate-700 hover:bg-slate-50 px-4 py-2 rounded-md shadow-sm transition-colors duration-200 flex items-center flex-nowrap justify-center sm:w-[140px] h-12 text-base">
                                <i class="fas fa-filter mr-2"></i>
                                <span>Bộ lọc</span>
                                <i class="fas ml-2"
                                    :class="{ 'fa-chevron-up': showFilterDropdown, 'fa-chevron-down': !showFilterDropdown }"></i>
                            </button>

                            <div v-if="showFilterDropdown"
                                class="origin-top-right absolute mt-2 ml-15 w-full sm:w-80 rounded-xl shadow-xl bg-white focus:outline-none z-20 border border-slate-200 flex flex-col"
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
                                        <div class="flex flex-col gap-3">
                                          <div>
                                            <label class="block text-xs font-semibold text-slate-700 mb-1">Từ ngày</label>
                                            <div class="relative">
                                              <input
                                                type="date"
                                                v-model="tempFilterCreatedAtFrom"
                                                class="w-full border border-slate-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                                placeholder="dd/mm/yyyy"
                                              />
                                            </div>
                                          </div>
                                          <div>
                                            <label class="block text-xs font-semibold text-slate-700 mb-1">Đến ngày</label>
                                            <div class="relative">
                                              <input
                                                type="date"
                                                v-model="tempFilterCreatedAtTo"
                                                class="w-full border border-slate-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                                placeholder="dd/mm/yyyy"
                                              />
                                            </div>
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
                                        <button @click="applyFilters"
                                            class="bg-blue-600 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors duration-200 shadow-sm ml-2">
                                            <i class="fas fa-check mr-2"></i> Áp dụng
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="w-full sm:w-[218px] ml-17">
                            <CustomSelect
                                v-model="filterProvince"
                                :options="[{ label: 'Tất cả thành phố', value: '' }, ...provinces.map(p => ({ label: p.name, value: p.id }))]"
                                placeholder="Tất cả thành phố"
                                class="w-full h-12 [&>button]:h-12 [&>button]:py-3 [&>button]:text-sm"
                                :direction="'down'"
                            />
                        </div>
                    </div>
                    <button @click="addHotel" class="bg-black hover:bg-gray-800 text-white px-4 py-2 rounded-md shadow-sm w-full sm:w-auto whitespace-nowrap">
                        Thêm khách sạn
                    </button>
                </div>
            </div>

            <div class="mb-8 bg-white rounded-xl shadow-lg border border-slate-200">
                <div class="overflow-x-auto">
                  <div class="overflow-y-auto h-[453px]">
                    <table class="min-w-[1200px] w-full divide-y divide-slate-200">
                      <thead class="bg-slate-100">
                        <tr>
                          <th class="sticky top-0 bg-slate-100 px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">STT</th>
                          <th class="sticky top-0 bg-slate-100 px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">Tên khách sạn</th>
                          <th class="sticky top-0 bg-slate-100 px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">Hạng sao</th>
                          <th class="sticky top-0 bg-slate-100 px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">Thành phố</th>
                          <th class="sticky top-0 bg-slate-100 px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">Giá từ (VND)</th>
                          <th class="sticky top-0 bg-slate-100 px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">Ngày tạo</th>
                          <th class="sticky top-0 bg-slate-100 px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">Ngày sửa</th>
                          <th class="sticky top-0 bg-slate-100 px-3 py-4 text-right text-xs font-bold text-slate-700 uppercase tracking-wider z-9999">Hành động</th>
                        </tr>
                      </thead>
                      <tbody class="bg-white divide-y divide-slate-100">
                        <tr v-if="paginatedHotels.length === 0" class="hover:bg-slate-50">
                            <td colspan="8" class="px-6 py-12 text-center">
                                <div class="flex flex-col items-center space-y-3">
                                    <i class="fas fa-search text-4xl text-slate-300"></i>
                                    <p class="text-lg font-medium text-slate-500">Không tìm thấy khách sạn nào</p>
                                    <p class="text-sm text-slate-400">Thử thay đổi bộ lọc hoặc tìm kiếm khác</p>
                                </div>
                            </td>
                        </tr>
                        <tr v-for="(h, index) in paginatedHotels" :key="h.id"
                            class="hover:bg-slate-50 transition-colors duration-150 cursor-pointer"
                            @click="() => viewHotelDetail(h)">
                            <td class="px-3 py-5 whitespace-nowrap">
                                <div class="text-sm font-medium text-slate-700 text-center">
                                    {{ (currentPage - 1) * itemsPerPage + index + 1 }}
                                </div>
                            </td>
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
                                    {{ formatCurrency(h.minDiscountedPrice != null ? h.minDiscountedPrice : h.startingPrice) }}
                                </div>
                                <div class="text-xs text-slate-500">VND/đêm</div>
                            </td>
                            <td class="px-3 py-5 whitespace-nowrap">
                                <span class="text-xs text-slate-700">{{ formatDateTime(h.createdAt) }}</span>
                            </td>
                            <td class="px-3 py-5 whitespace-nowrap">
                                <span class="text-xs text-slate-700">{{ formatDateTime(h.updatedAt) }}</span>
                            </td>
                            <td
                                class="px-3 py-5 whitespace-nowrap text-right align-middle">
                                <div class="relative inline-block text-left flex items-center justify-end h-full">
                                    <button :ref="el => setDropdownBtnRef(el, h.id)"
                                        @click.stop="toggleDropdown(h.id)" type="button"
                                        class="inline-flex justify-center items-center w-10 h-10 rounded-lg text-slate-500 hover:text-slate-700 hover:bg-slate-100 focus:outline-none transition-all duration-200 shadow-sm">
                                        <i class="fas fa-ellipsis-v flex items-center justify-center text-sm"></i>
                                    </button>
                                    <teleport to="body">
                                        <div v-if="activeDropdown === h.id"
                                            :style="{ position: 'absolute', top: dropdownMenuPosition.top + 'px', left: dropdownMenuPosition.left + 'px', right: 'auto', zIndex: 9999 }"
                                            class="min-w-40 bg-white border border-slate-200 rounded-lg shadow-lg">
                                            <button class="block w-full text-left px-4 py-2 hover:bg-blue-50"
                                                @click.stop="viewHotelDetail(h)"><i class="fas fa-eye mr-2"></i>Xem
                                                chi
                                                tiết</button>
                                            <button class="block w-full text-left px-4 py-2 hover:bg-blue-50"
                                                @click.stop="editHotel(h)"><i class="fas fa-edit mr-2"></i>Chỉnh
                                                sửa</button>
                                            <button
                                                class="block w-full text-left px-4 py-2 hover:bg-red-50 text-red-600"
                                                @click.stop="deleteHotel(h.id)"><i
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
                <div class="flex items-center justify-end gap-4 px-4 py-3 bg-white border-t border-slate-200 rounded-b-xl">
                  <div class="flex items-center gap-2">
                    <span class="text-sm text-gray-700 whitespace-nowrap">Số dòng</span>
                    <CustomSelect
                      v-model="itemsPerPageStr"
                      :options="itemsPerPageOptions.map(opt => ({ label: String(opt), value: String(opt) }))"
                      class="w-20 min-w-[100px] h-8 [&>button]:h-8 [&>button]:py-1 [&>button]:text-sm"
                      :direction="'up'"
                    />
                  </div>
                  <nav v-if="totalPages > 1 && itemsPerPageStr !== 'Tất cả'" aria-label="Pagination">
                    <ul class="inline-flex items-center space-x-1">
                      <li>
                        <button @click="prevPage" :disabled="currentPage === 1"
                          class="w-8 h-8 flex items-center justify-center rounded border border-slate-200 text-gray-500 hover:bg-gray-100 disabled:opacity-50">
                          <i class="fas fa-chevron-left text-xs"></i>
                        </button>
                      </li>
                      <li v-for="page in displayedPages" :key="page">
                        <button v-if="page !== '...'" @click="changePage(page)"
                          :class="['w-8 h-8 flex items-center justify-center rounded font-medium',
                            currentPage === page ? 'bg-orange-500 text-white border-orange-500' : 'text-gray-700 hover:bg-gray-100 border border-slate-200']">
                          {{ page }}
                        </button>
                        <span v-else class="w-8 h-8 flex items-center justify-center text-gray-400">...</span>
                      </li>
                      <li>
                        <button @click="nextPage" :disabled="currentPage === totalPages"
                          class="w-8 h-8 flex items-center justify-center rounded border border-slate-200 text-gray-500 hover:bg-gray-100 disabled:opacity-50">
                          <i class="fas fa-chevron-right text-xs"></i>
                        </button>
                      </li>
                    </ul>
                  </nav>
                </div>
            </div>
        </div>
        <div v-else-if="mode === 'edit'">
            <div class="bg-white rounded-xl shadow-lg border border-slate-200 p-8 max-w-6xl mx-auto">
                <div class="flex justify-between items-center border-b border-slate-200 pb-4 mb-4">
                    <h2 class="text-xl font-bold">
                        <span v-if="isEditMode && !isViewMode">Chỉnh sửa khách sạn</span>
                        <span v-else-if="isViewMode">Chi tiết khách sạn</span>
                        <span v-else>Thêm khách sạn mới</span>
                    </h2>
                    <div class="flex items-center space-x-3">
                        <template v-if="isViewMode">
                            <button @click="enableEditMode"
                                class="flex items-center justify-center w-10 h-10 rounded-lg text-slate-600 hover:text-blue-600 hover:bg-slate-100 transition-colors duration-200"
                                title="Chỉnh sửa">
                                <i class="fas fa-pencil-alt text-xl"></i>
                            </button>
                            <button @click="deleteHotel(newHotel.id)"
                                class="flex items-center justify-center w-10 h-10 rounded-lg text-red-600 hover:bg-red-50 transition-colors duration-200"
                                title="Xóa khách sạn">
                                <i class="fas fa-trash-alt text-xl"></i>
                            </button>
                        </template>
                        <template v-else-if="isEditMode && !isViewMode">
                            <button @click="saveChanges"
                                class="flex items-center justify-center w-10 h-10 rounded-lg text-green-600 hover:bg-green-50 transition-colors duration-200"
                                title="Lưu thay đổi">
                                <i class="fas fa-save text-xl"></i>
                            </button>
                        </template>
                        <button @click="handleBack"
                            class="flex items-center justify-center w-10 h-10 rounded-lg text-slate-600 hover:text-blue-600 hover:bg-slate-100 transition-colors duration-200"
                            title="Quay lại">
                            <i class="fas fa-arrow-left text-xl"></i>
                        </button>
                    </div>
                </div>
                <form class="space-y-8" autocomplete="off" novalidate @submit.prevent="submitHotel">
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
                        <div>
                            <label class="block text-sm font-semibold text-slate-700 mb-1">Tên khách sạn</label>
                            <input v-model="newHotel.name" type="text" placeholder="Nhập tên khách sạn"
                                :disabled="isViewMode" :class="[
                                    'w-full border border-slate-300 rounded-md px-3 py-2 placeholder-slate-400 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent',
                                    isViewMode ? 'bg-gray-100 cursor-not-allowed' : ''
                                ]" />
                        </div>
                        <div>
                            <label class="block text-sm font-semibold text-slate-700 mb-1">Hạng sao</label>
                            <CustomSelect
                                v-model="newHotel.starRating"
                                :options="[
                                    { label: '1 Sao', value: 1 },
                                    { label: '2 Sao', value: 2 },
                                    { label: '3 Sao', value: 3 },
                                    { label: '4 Sao', value: 4 },
                                    { label: '5 Sao', value: 5 }
                                ]"
                                placeholder="Chọn hạng sao"
                                :disabled="isViewMode"
                            />
                        </div>
                        <div>
                            <label class="block text-sm font-semibold text-slate-700 mb-1">Thành phố</label>
                            <CustomSelect
                                v-model="newHotel.provinceId"
                                :options="provinces.map(p => ({ label: p.name, value: p.id }))"
                                placeholder="Chọn thành phố"
                                :disabled="isViewMode"
                            />
                        </div>
                        <div>
                            <label class="block text-sm font-semibold text-slate-700 mb-1">Địa chỉ</label>
                            <input v-model="newHotel.address" type="text" placeholder="Nhập địa chỉ khách sạn"
                                :disabled="isViewMode" :class="[
                                    'w-full border border-slate-300 rounded-md px-3 py-2 placeholder-slate-400 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent',
                                    isViewMode ? 'bg-gray-100 cursor-not-allowed' : ''
                                ]" />
                        </div>
                        <div>
                            <label class="block text-sm font-semibold text-slate-700 mb-1">Số điện thoại</label>
                            <input v-model="newHotel.phone" type="text" placeholder="Nhập số điện thoại"
                                :disabled="isViewMode" :class="[
                                    'w-full border border-slate-300 rounded-md px-3 py-2 placeholder-slate-400 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent',
                                    isViewMode ? 'bg-gray-100 cursor-not-allowed' : ''
                                ]" />
                        </div>
                        <div>
                            <label class="block text-sm font-semibold text-slate-700 mb-1">Email</label>
                            <input v-model="newHotel.email" type="email" placeholder="Nhập email" :disabled="isViewMode"
                                :class="[
                                    'w-full border border-slate-300 rounded-md px-3 py-2 placeholder-slate-400 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent',
                                    isViewMode ? 'bg-gray-100 cursor-not-allowed' : ''
                                ]" />
                        </div>
                    </div>
                    <div>
                        <label class="block text-sm font-semibold text-slate-700 mb-1">Mô tả</label>
                        <div v-if="isViewMode" class="border border-slate-300 rounded-md p-4 bg-gray-50">
                            <HtmlContent :content="newHotel.description" />
                        </div>
                        <Ckeditor v-else v-model="newHotel.description" />
                    </div>
                    <div>
                        <label class="block text-sm font-semibold text-slate-700 mb-2">Hình ảnh khách sạn</label>
                        <div v-if="isViewMode" class="flex flex-wrap gap-3">
                            <div v-for="(img, idx) in newHotel.imageUrls" :key="'view-' + idx" class="relative">
                                <img :src="img"
                                    class="w-32 h-24 object-cover rounded-lg border border-slate-200 shadow-sm" />
                            </div>
                        </div>
                        <div v-else>
                            <input type="file" multiple accept="image/*" ref="hotelImageInput" style="display:none"
                                @change="handleHotelImageChange" />
                            <div class="flex items-center gap-2 mb-2">
                                <button type="button" @click="$refs.hotelImageInput.click()"
                                    class="w-40 h-28 border-2 border-dashed border-slate-300 rounded-md flex flex-col items-center justify-center text-slate-500 hover:border-blue-400 hover:text-blue-600 transition-colors">
                                    <i class="far fa-image text-2xl mb-1"></i>
                                    <span class="text-sm font-semibold">Thêm ảnh</span>
                                </button>
                                <span class="mx-2 text-slate-500 font-semibold">hoặc</span>
                                <button type="button" ref="hotelImageUrlBtn" @click="openHotelImageUrlPopup"
                                    class="flex flex-col items-center justify-center border border-blue-400 text-blue-600 rounded-md px-3 py-2 h-28 w-28 hover:bg-blue-50 transition">
                                    <i class="fas fa-link text-xl mb-1"></i>
                                    <span class="text-xs font-semibold">Thêm URL hình</span>
                                </button>
                            </div>
                            <div v-if="showHotelImageUrlPopup" class="custom-url-popup" :style="hotelImageUrlPopupStyle" ref="hotelImageUrlPopupRef">
                                <label class="block text-xs font-semibold text-slate-700 mb-1">Media URL</label>
                                <div class="flex items-center gap-2">
                                    <input
                                        v-model="hotelImageUrlInput"
                                        type="text"
                                        placeholder="Paste the media URL in the input."
                                        class="border border-slate-300 rounded px-2 py-1 text-sm w-64 flex-1"
                                    />
                                    <button @click="confirmHotelImageUrl" class="text-green-600 text-xl hover:text-green-800">✔</button>
                                    <button @click="closeHotelImageUrlPopup" class="text-red-500 text-xl hover:text-red-700">✗</button>
                                </div>
                                <div v-if="hotelImageUrlError" class="text-red-500 text-xs mt-1">{{ hotelImageUrlError }}</div>
                                <div class="text-xs text-slate-500 mt-1">Paste the media URL in the input.</div>
                            </div>
                            <div class="flex flex-wrap gap-3 mt-2">
                                <div v-for="(img, idx) in newHotel.imageUrls" :key="'old-' + idx"
                                    class="relative group">
                                    <img :src="img"
                                        class="w-32 h-24 object-cover rounded-lg border border-slate-200 shadow-sm" />
                                    <button type="button" @click="removeHotelImage(idx, true)"
                                        class="absolute top-0 right-0 bg-red-500 text-white rounded-full p-1 text-xs opacity-80 group-hover:opacity-100">✕</button>
                                </div>
                                <div v-for="(img, idx) in hotelImagePreviews" :key="'new-' + idx"
                                    class="relative group">
                                    <img :src="img"
                                        class="w-32 h-24 object-cover rounded-lg border border-slate-200 shadow-sm" />
                                    <button type="button" @click="removeHotelImage(idx)"
                                        class="absolute top-0 right-0 bg-red-500 text-white rounded-full p-1 text-xs opacity-80 group-hover:opacity-100">✕</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div>
                        <div class="flex justify-between items-center mb-4">
                            <h3 class="font-semibold text-slate-800 text-base">Thông tin các loại phòng</h3>
                            <button v-if="!isViewMode" type="button" @click="addRoom"
                                class="text-blue-600 font-semibold border border-blue-600 rounded-md px-4 py-2 hover:bg-blue-50 transition shadow-sm">
                                + Thêm loại phòng
                            </button>
                        </div>
                        <div v-for="(r, idx) in newHotel.availableRooms" :key="idx"
                            class="mb-6 border border-slate-200 rounded-xl shadow-sm p-6">
                            <div class="flex justify-between items-center mb-2">
                                <div class="font-semibold text-slate-800 text-base">{{ r.roomType || 'Loại phòng mới' }}
                                </div>
                                <button v-if="!isViewMode" type="button" @click="removeRoom(idx)"
                                    class="text-red-500 hover:underline text-xs ml-2">Xóa</button>
                            </div>
                            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-4">
                                <div>
                                    <label class="block text-sm font-semibold text-slate-700 mb-1">Loại phòng</label>
                                    <input v-model="r.roomType" type="text"
                                        placeholder="Ví dụ: Phòng Deluxe, Phòng Superior" :disabled="isViewMode" :class="[
                                            'w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent',
                                            isViewMode ? 'bg-gray-100 cursor-not-allowed' : ''
                                        ]" />
                                </div>
                                <div>
                                    <label class="block text-sm font-semibold text-slate-700 mb-1">Giường</label>
                                    <input v-model="r.bedType" type="text" placeholder="Nhập loại giường"
                                        :disabled="isViewMode" :class="[
                                            'w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent',
                                            isViewMode ? 'bg-gray-100 cursor-not-allowed' : ''
                                        ]" />
                                </div>
                                <div>
                                    <label class="block text-sm font-semibold text-slate-700 mb-1">Diện tích
                                        (m²)</label>
                                    <input v-model.number="r.roomArea" type="number" min="0"
                                        placeholder="Nhập diện tích phòng" :disabled="isViewMode" :class="[
                                            'w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent',
                                            isViewMode ? 'bg-gray-100 cursor-not-allowed' : ''
                                        ]" />
                                </div>
                            </div>
                            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-4">
                                <div>
                                    <label class="block text-sm font-semibold text-slate-700 mb-1">Số lượng
                                        phòng</label>
                                    <input v-model.number="r.roomQuantity" type="number" min="0"
                                        placeholder="Nhập số lượng phòng" :disabled="isViewMode" :class="[
                                            'w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent',
                                            isViewMode ? 'bg-gray-100 cursor-not-allowed' : ''
                                        ]" />
                                </div>
                                <div class="grid grid-cols-2 gap-4">
                                    <div>
                                        <label class="block text-sm font-semibold text-slate-700 mb-1">Người lớn</label>
                                        <input v-model.number="r.maxAdults" type="number" min="0"
                                            placeholder="Số người lớn" :disabled="isViewMode" :class="[
                                                'w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent',
                                                isViewMode ? 'bg-gray-100 cursor-not-allowed' : ''
                                            ]" />
                                    </div>
                                    <div>
                                        <label class="block text-sm font-semibold text-slate-700 mb-1">Trẻ em</label>
                                        <input v-model.number="r.maxChildren" type="number" min="0"
                                            placeholder="Số trẻ em" :disabled="isViewMode" :class="[
                                                'w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent',
                                                isViewMode ? 'bg-gray-100 cursor-not-allowed' : ''
                                            ]" />
                                    </div>
                                </div>
                            </div>
                            <div>
                                <label class="block text-sm font-semibold text-slate-700 mb-1">Tiện ích phòng</label>
                                <div class="flex flex-wrap gap-2 mb-2">
                                    <template v-for="a in amenities">
                                        <span v-if="r.amenities[a.id]" :key="'chip-' + a.id"
                                            class="flex items-center bg-blue-100 text-blue-700 px-1.5 py-0 rounded-full text-xs font-medium mr-2 mb-2 items-center justify-center"
                                            style="font-size: 0.8rem; min-height: 2rem;">
                                            <i v-if="a.icon" :class="a.icon + ' mr-2 text-xs'"
                                                style="min-width: 1.2em;"></i>
                                            {{ a.name }}
                                            <button v-if="!isViewMode" @click.prevent="removeRoomAmenity(idx, a.id)"
                                                class="ml-2 text-blue-500 hover:text-red-500 focus:outline-none text-base">×</button>
                                        </span>
                                    </template>
                                    <button v-if="!isViewMode" type="button" @click="openAmenityModal(idx)"
                                        class="flex items-center px-1.5 py-0 bg-slate-100 hover:bg-blue-100 text-slate-700 rounded-full text-xs font-medium border border-slate-200 mr-2 mb-2 items-center justify-center"
                                        style="font-size: 0.8rem; min-height: 2rem;">
                                        <i class="fas fa-plus mr-2 text-xs"></i> Thêm tiện ích
                                    </button>
                                </div>
                                <div v-if="showAmenityModal && amenityModalRoomIdx === idx">
                                    <div class="fixed inset-0 z-40" style="background: rgba(0,0,0,0.5);"></div>
                                    <div class="fixed inset-0 flex items-center justify-center z-50"
                                        @click.self="closeAmenityModal">
                                        <div class="bg-white rounded-xl shadow-lg p-8 w-full max-w-2xl relative">
                                            <h3 class="text-xl font-bold mb-6">Chọn tiện ích phòng</h3>
                                            <div class="grid grid-cols-3 gap-3 max-h-96 overflow-y-auto mb-6 pr-3">
                                                <button v-for="a in amenities" :key="'modal-' + a.id" type="button"
                                                    @click="toggleAmenityModalSelected(a.id)" :class="[
                                                        'flex items-center w-full min-w-0 px-3 py-2 rounded-lg border transition text-sm font-medium',
                                                        amenityModalSelected.includes(a.id)
                                                            ? 'bg-blue-100 border-blue-400 text-blue-700 shadow'
                                                            : 'bg-slate-50 border-slate-200 text-slate-700 hover:bg-blue-50'
                                                    ]" style="box-sizing: border-box;">
                                                    <i v-if="a.icon" :class="a.icon + ' mr-2 text-base'"
                                                        style="min-width: 1.5em;"></i>
                                                    <span class="truncate">{{ a.name }}</span>
                                                    <span v-if="amenityModalSelected.includes(a.id)"
                                                        class="ml-auto text-blue-500"><i
                                                            class="fas fa-check-circle"></i></span>
                                                </button>
                                            </div>
                                            <div class="flex justify-end gap-3 mt-6">
                                                <button @click="closeAmenityModal"
                                                    class="px-5 py-2 rounded bg-slate-100 text-slate-700 hover:bg-slate-200 text-base">Hủy</button>
                                                <button @click="confirmAmenityModal"
                                                    class="px-5 py-2 rounded bg-blue-600 text-white hover:bg-blue-700 text-base font-semibold">Xác
                                                    nhận</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="mt-4">
                                <label class="block text-sm font-semibold text-slate-700 mb-1">Hình ảnh phòng</label>
                                <div v-if="isViewMode" class="flex flex-wrap gap-3">
                                    <div v-for="(img, imgIdx) in r.imageUrls" :key="'viewroom-' + imgIdx"
                                        class="relative">
                                        <img :src="img"
                                            class="w-32 h-24 object-cover rounded-lg border border-slate-200 shadow-sm" />
                                    </div>
                                </div>
                                <div v-else>
                                    <input type="file" multiple accept="image/*" :ref="`roomImageInput${idx}`"
                                        style="display:none" @change="e => handleRoomImageChange(e, idx)" />
                                    <div class="flex items-center gap-2 mb-2">
                                        <button type="button" @click="triggerRoomImageInput(idx)"
                                            class="w-40 h-28 border-2 border-dashed border-slate-300 rounded-md flex flex-col items-center justify-center text-slate-500 hover:border-blue-400 hover:text-blue-600 transition-colors">
                                            <i class="far fa-image text-2xl mb-1"></i>
                                            <span class="text-sm font-semibold">Thêm ảnh</span>
                                        </button>
                                        <span class="mx-2 text-slate-500 font-semibold">hoặc</span>
                                        <button type="button" :ref="el => setRoomImageUrlBtnRef(el, idx)" @click="openRoomImageUrlPopup(idx)"
                                            class="flex flex-col items-center justify-center border border-blue-400 text-blue-600 rounded-md px-3 py-2 h-28 w-28 hover:bg-blue-50 transition">
                                            <i class="fas fa-link text-xl mb-1"></i>
                                            <span class="text-xs font-semibold">Thêm URL hình</span>
                                        </button>
                                    </div>
                                    <div v-if="showRoomImageUrlPopup[idx]" class="custom-url-popup" :style="roomImageUrlPopupStyle[idx]" ref="roomUrlPopupRefs" :data-popup-idx="idx">
                                        <label class="block text-xs font-semibold text-slate-700 mb-1">Media URL</label>
                                        <div class="flex items-center gap-2">
                                            <input
                                                v-model="roomImageUrlInput[idx]"
                                                type="text"
                                                placeholder="Paste the media URL in the input."
                                                class="border border-slate-300 rounded px-2 py-1 text-sm w-64 flex-1"
                                            />
                                            <button @click="confirmRoomImageUrl(idx)" class="text-green-600 text-xl hover:text-green-800">✔</button>
                                            <button @click="closeRoomImageUrlPopup(idx)" class="text-red-500 text-xl hover:text-red-700">✗</button>
                                        </div>
                                        <div v-if="roomImageUrlError[idx]" class="text-red-500 text-xs mt-1">{{ roomImageUrlError[idx] }}</div>
                                        <div class="text-xs text-slate-500 mt-1">Paste the media URL in the input.</div>
                                    </div>
                                    <div class="flex flex-wrap gap-3 mt-2">
                                        <div v-for="(img, imgIdx) in r.imagePreviews" :key="'roomimg-' + imgIdx"
                                            class="relative group">
                                            <img :src="img"
                                                class="w-20 h-20 object-cover rounded-lg border border-slate-200 shadow-sm" />
                                            <button type="button" @click="removeRoomImage(idx, imgIdx)"
                                                class="absolute top-0 right-0 bg-red-500 text-white rounded-full p-1 text-xs opacity-80 group-hover:opacity-100">✕</button>
                                        </div>
                                        <div v-for="(img, imgIdx) in r.imageUrls" :key="'oldroomimg-' + imgIdx"
                                            class="relative group">
                                            <img :src="img"
                                                class="w-32 h-24 object-cover rounded-lg border border-slate-200 shadow-sm" />
                                            <button type="button" @click="removeRoomImage(idx, imgIdx, true)"
                                                class="absolute top-0 right-0 bg-red-500 text-white rounded-full p-1 text-xs opacity-80 group-hover:opacity-100">✕</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="mt-6">
                                <div class="flex justify-between items-center mb-2">
                                    <h3 class="text-base font-semibold text-slate-800">Gói phòng</h3>
                                    <button v-if="!isViewMode" type="button" @click="addVariant(idx)"
                                        class="text-blue-600 text-xs font-semibold hover:underline">+ Thêm gói
                                        phòng</button>
                                </div>
                                <div class="flex flex-col gap-4">
                                  <div v-for="(v, vIdx) in r.availableVariants" :key="vIdx" class="bg-slate-50 rounded-xl p-4 shadow flex flex-col gap-3 relative">
                                    <div class="flex flex-col gap-2">
                                      <label class="text-sm font-semibold text-slate-700 mb-1">Tên gói</label>
                                      <input v-model="v.variantName" :disabled="isViewMode" placeholder="Tên gói"
                                        :class="[
                                          'w-full border border-slate-300 rounded-md px-3 py-2 placeholder-slate-400 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent',
                                          isViewMode ? 'bg-gray-100 cursor-not-allowed' : ''
                                        ]"
                                      />
                                    </div>
                                    <div class="flex flex-col gap-2">
                                      <label class="text-sm font-semibold text-slate-700 mb-1">Giá</label>
                                      <input v-model.number="v.price" :disabled="isViewMode" type="number" min="0" placeholder="Giá"
                                        :class="[
                                          'w-full border border-slate-300 rounded-md px-3 py-2 placeholder-slate-400 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent',
                                          isViewMode ? 'bg-gray-100 cursor-not-allowed' : ''
                                        ]"
                                      />
                                    </div>
                                    <div class="flex gap-6 flex-wrap">
                                      <label class="flex items-center gap-2 text-sm font-semibold text-slate-700"><input type="checkbox" v-model="v.hasBreakfast" :disabled="isViewMode" class="w-5 h-5 accent-blue-600" /> Bữa sáng</label>
                                      <label class="flex items-center gap-2 text-sm font-semibold text-slate-700"><input type="checkbox" v-model="v.cancellable" :disabled="isViewMode" class="w-5 h-5 accent-blue-600" /> Hủy miễn phí</label>
                                      <label class="flex items-center gap-2 text-sm font-semibold text-slate-700"><input type="checkbox" v-model="v.payAtHotel" :disabled="isViewMode" class="w-5 h-5 accent-blue-600" /> Thanh toán tại KS</label>
                                    </div>
                                    <div class="flex flex-col gap-2">
                                      <label class="text-sm font-semibold text-slate-700 mb-1">Thuế/Phí</label>
                                      <input v-model.number="v.taxAndFeeAmount" :disabled="isViewMode" type="number" min="0" placeholder="Thuế/Phí"
                                        :class="[
                                          'w-full border border-slate-300 rounded-md px-3 py-2 placeholder-slate-400 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent',
                                          isViewMode ? 'bg-gray-100 cursor-not-allowed' : ''
                                        ]"
                                      />
                                    </div>
                                    <div class="flex flex-row items-center gap-2 mb-1">
                                      <label class="text-sm font-semibold text-slate-700 min-w-max w-24">Giảm giá</label>
                                      <CustomSelect
                                        v-model="v.discountType"
                                        :options="[
                                          { label: 'Không giảm', value: '' },
                                          { label: 'VND', value: 'amount' },
                                          { label: '%', value: 'percent' }
                                        ]"
                                        :disabled="isViewMode"
                                        style="max-width: 160px; min-width: 150px; width: 150px;"
                                      />
                                      <input v-if="v.discountType" v-model.number="v.discountValue" :disabled="isViewMode" type="number" min="0"
                                        :class="[
                                          'w-30 border border-slate-300 rounded-md px-3 py-2 placeholder-slate-400 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition',
                                          isViewMode ? 'bg-gray-100 cursor-not-allowed' : ''
                                        ]"
                                        :placeholder="v.discountType === 'percent' ? '%' : 'VND'" />
                                      <span v-if="v.discountType === 'percent'" class="ml-1 text-base text-gray-500">%</span>
                                      <span v-else-if="v.discountType === 'amount'" class="ml-1 text-base text-gray-500">VND</span>
                                    </div>
                                    <div class="text-green-700 font-bold text-base">Giá sau giảm: {{ formatCurrency(getDiscountedPrice(v)) }}</div>
                                    <button v-if="!isViewMode" type="button" @click="removeVariant(idx, vIdx)" class="absolute top-2 right-2 text-red-500 text-base font-semibold">Xóa</button>
                                  </div>
                                  <div v-if="!r.availableVariants || r.availableVariants.length === 0" class="text-center text-slate-400 py-6 text-base bg-slate-50 rounded-xl">Chưa có gói phòng nào</div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div v-if="!isViewMode" class="flex justify-end mt-8">
                        <button type="submit"
                            class="font-semibold text-white bg-black rounded-md px-6 py-2 hover:bg-gray-800 transition shadow-sm">
                            {{ isEditMode ? 'Cập nhật' : 'Thêm khách sạn' }}
                        </button>
                    </div>
                </form>
            </div>
        </div>
        <ConfirmDialog ref="confirmDialog" />
    </div>
</template>

<script>
import { hotelAdminApi } from '@/api/adminApi';
import { notifyHotelCreated, notifyHotelUpdated, notifyHotelDeleted } from '@/api/hotelApi';
import HotelDetailModal from './HotelDetailModal.vue';
import provinceApi from '@/api/provinceApi';
import AmenityApi from '@/api/AmenityApi';
import ConfirmDialog from '@/components/ConfirmDialog.vue';
import Ckeditor from '@/components/Ckeditor.vue';
import HtmlContent from '@/components/HtmlContent.vue';
import CustomSelect from '@/components/CustomSelect.vue';
import { useAdminBreadcrumbStore } from '@/store/useAdminBreadcrumbStore';
import { useAdminAuth } from '@/composables/useAdminAuth';
import { useUserStore } from '@/store/UserStore';
export default {
    name: 'HotelManager',
    components: { HotelDetailModal, ConfirmDialog, Ckeditor, HtmlContent, CustomSelect },
    data() {
        return {
            mode: 'list',
            isEditMode: false,
            isViewMode: false,
            fromDetailView: false,
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
                provinceId: '',
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
                        availableVariants: [
                            {
                                id: null,
                                variantName: '',
                                price: 0,
                                hasBreakfast: false,
                                cancellable: false,
                                payAtHotel: false,
                                taxAndFeeAmount: 0,
                                discountType: '',
                                discountValue: 0
                            }
                        ],
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
            currentPage: 1,
            itemsPerPageStr: '5',
            itemsPerPageOptions: [5, 10, 20, 50, 'Tất cả'],
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
            filterProvince: '',
            createdAtPresets: [
                { label: 'Hôm nay', value: 'today' },
                { label: 'Hôm qua', value: 'yesterday' },
                { label: '3 ngày trước', value: 'last3days' },
                { label: 'Tuần này', value: 'thisweek' },
                { label: 'Tuần trước', value: 'lastweek' },
                { label: 'Tháng này', value: 'thismonth' },
                { label: 'Tháng trước', value: 'lastmonth' }
            ],
            tempFilterStar: '',
            tempFilterPriceMax: 20000000,
            tempFilterCreatedAtPreset: '',
            tempFilterCreatedAtFrom: '',
            tempFilterCreatedAtTo: '',
            dropdownMenuPosition: { top: 0, left: 0 },

            hotelIdToDelete: null,
            showAmenityModal: false,
            amenityModalRoomIdx: null,
            amenityModalSelected: [],
            showHotelImageUrlPopup: false,
            hotelImageUrlInput: '',
            hotelImageUrlError: '',
            hotelImageUrlAnchor: null,
            showRoomImageUrlPopup: [false],
            roomImageUrlInput: [''],
            roomImageUrlError: [''],
            roomImageUrlAnchor: [null],
            roomImageUrlPopupStyle: [{}],
            roomUrlPopupRefs: [],
            hotelImageUrlPopupRef: null,
            // Thống kê khách sạn
            hotelStatistics: {
                totalHotels: 0,
                totalRooms: 0,
                availableRooms: 0,
                outOfStockRooms: 0,
                nearlyOutOfStockRooms: 0
            },
            // Filter theo trạng thái phòng
            roomStatusFilter: null,
        };
    },
    computed: {
        filteredHotels() {
            return [...this.hotels].sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt));
        },
        itemsPerPage() {
            return this.itemsPerPageStr === 'Tất cả' ? this.filteredHotels.length : Number(this.itemsPerPageStr);
        },
        totalPages() {
            const perPage = this.itemsPerPage;
            return Math.ceil(this.filteredHotels.length / (perPage || 1));
        },
        paginatedHotels() {
            if (this.itemsPerPageStr === 'Tất cả') {
                return this.filteredHotels;
            }
            const perPage = this.itemsPerPage;
            const start = (this.currentPage - 1) * perPage;
            return this.filteredHotels.slice(start, start + perPage);
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
        },
        totalRooms() {
            // Sử dụng thống kê từ API
            return this.hotelStatistics.totalRooms || 0;
        },
        totalAvailableRooms() {
            // Sử dụng thống kê từ API
            return this.hotelStatistics.availableRooms || 0;
        },
        isNearlyOutOfStockFilterActive() {
            return this.roomStatusFilter === 'nearly_out_of_stock';
        },
        isOutOfStockFilterActive() {
            return this.roomStatusFilter === 'out_of_stock';
        },
    },
    watch: {
        searchQuery() { this.currentPage = 1; this.fetchHotels(); },
        itemsPerPageStr() { this.currentPage = 1; this.fetchHotels(); },
        filterProvince() { this.currentPage = 1; this.fetchHotels(); },
        mode() {
            this.$nextTick(() => this.updateBreadcrumb());
        },
        isEditMode() {
            this.$nextTick(() => this.updateBreadcrumb());
        },
        isViewMode() {
            this.$nextTick(() => this.updateBreadcrumb());
        },
        fromDetailView() {
            this.$nextTick(() => this.updateBreadcrumb());
        },
        'newHotel.id'() {
            this.$nextTick(() => this.updateBreadcrumb());
        }
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
                    const res = await hotelAdminApi.getHotelById(hotel.id);
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
                                discountType: variant.discountType || '',
                                discountValue: (variant.discountValue && typeof variant.discountValue === 'object' && variant.discountValue !== null) ? Number(variant.discountValue) : (variant.discountValue || 0)
                            })),
                        })),
                        amenities: (detail.amenities || []).reduce((acc, a) => { acc[a.id] = true; return acc; }, {}),
                        policy: detail.policy || {},
                    };
                    this.showRoomImageUrlPopup = this.newHotel.availableRooms.map(() => false);
                    this.roomImageUrlInput = this.newHotel.availableRooms.map(() => '');
                    this.roomImageUrlError = this.newHotel.availableRooms.map(() => '');
                    this.roomImageUrlAnchor = this.newHotel.availableRooms.map(() => null);
                    this.roomImageUrlPopupStyle = this.newHotel.availableRooms.map(() => ({}));
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
            this.filterProvince = '';
            this.roomStatusFilter = null;
            this.showFilterDropdown = false;
            this.currentPage = 1;
            this.fetchHotels();
            this.fetchHotelStatistics();
        },
        async viewDetails(hotel) {
            this.isEditMode = true;
            this.isViewMode = true;
            this.modalMode = 'edit';
            this.loadProvincesAndAmenities();
            this.hotelImages = [];
            this.hotelImagePreviews = [];
            this.imagesToDelete = [];
            try {
                const res = await hotelAdminApi.getHotelById(hotel.id);
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
                            discountType: variant.discountType || '',
                            discountValue: (variant.discountValue && typeof variant.discountValue === 'object' && variant.discountValue !== null) ? Number(variant.discountValue) : (variant.discountValue || 0)
                        })),
                    })),
                    amenities: (detail.amenities || []).reduce((acc, a) => { acc[a.id] = true; return acc; }, {}),
                };
            } catch (e) {
                window.$toast('Không lấy được chi tiết khách sạn!', 'error');
                return;
            }
            this.mode = 'edit';
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
                availableVariants: [
                    {
                        id: null,
                        variantName: '',
                        price: 0,
                        hasBreakfast: false,
                        cancellable: false,
                        payAtHotel: false,
                        taxAndFeeAmount: 0,
                        discountType: '',
                        discountValue: 0
                    }
                ],
            });
            const idx = this.newHotel.availableRooms.length - 1;
            this.showRoomImageUrlPopup[idx] = false;
            this.roomImageUrlInput[idx] = '';
            this.roomImageUrlError[idx] = '';
            this.roomImageUrlAnchor[idx] = null;
            this.roomImageUrlPopupStyle[idx] = {};
        },
        removeRoom(index) {
            if (this.newHotel.availableRooms.length > 1) {
                this.newHotel.availableRooms.splice(index, 1);
                this.showRoomImageUrlPopup.splice(index, 1);
                this.roomImageUrlInput.splice(index, 1);
                this.roomImageUrlError.splice(index, 1);
                this.roomImageUrlAnchor.splice(index, 1);
                this.roomImageUrlPopupStyle.splice(index, 1);
            } else {
                window.$toast('Phải có ít nhất một loại phòng.', 'error');
            }
        },
        async deleteHotel(hotelId) {
            this.hotelIdToDelete = hotelId;
            const hotelToDelete = this.hotels.find(h => h.id === hotelId);
            const hotelName = hotelToDelete ? hotelToDelete.name : 'khách sạn này';
            this.activeDropdown = null;
            
            const result = await this.$refs.confirmDialog.showDialog({
                type: 'danger',
                title: 'Xác nhận xóa khách sạn',
                message: `Bạn có chắc chắn muốn xóa khách sạn "${hotelName}" không? Hành động này không thể hoàn tác.`,
                confirmText: 'Xóa',
                cancelText: 'Hủy'
            });
            
            if (result) {
                await this.onConfirmDelete();
            }
        },
        async onConfirmDelete() {
            try {
                const hotelToDelete = this.hotels.find(h => h.id === this.hotelIdToDelete);
                const hotelName = hotelToDelete ? hotelToDelete.name : 'Khách sạn';
                await hotelAdminApi.deleteHotel(this.hotelIdToDelete);
                const userStore = useUserStore();
                const userName = userStore.user?.name || userStore.user?.email || 'Admin';
                await notifyHotelDeleted(hotelName, userName);
                window.$toast('Xóa khách sạn thành công!', 'success');
                this.backToList();
                this.fetchHotels();
                await this.fetchHotelStatistics();
            } catch (e) {
                let msg = 'Xóa khách sạn thất bại!';
                if (e?.response?.data?.message) {
                    msg = e.response.data.message;
                } else if (e?.message) {
                    msg = e.message;
                }
                window.$toast(msg, 'error');
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
                provinceId: '',
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
                        availableVariants: [
                            {
                                id: null,
                                variantName: '',
                                price: 0,
                                hasBreakfast: false,
                                cancellable: false,
                                payAtHotel: false,
                                taxAndFeeAmount: 0,
                                discountType: '',
                                discountValue: 0
                            }
                        ],
                    }
                ],
                amenities: [],
                policy: {}
            };
            for (const key in this.form.amenities) { this.form.amenities[key] = false; }
            this.form.policy = { checkin: '', checkout: '', other: '' };
            this.modalMode = 'add';
            this.showRoomImageUrlPopup = this.newHotel.availableRooms.map(() => false);
            this.roomImageUrlInput = this.newHotel.availableRooms.map(() => '');
            this.roomImageUrlError = this.newHotel.availableRooms.map(() => '');
            this.roomImageUrlAnchor = this.newHotel.availableRooms.map(() => null);
            this.roomImageUrlPopupStyle = this.newHotel.availableRooms.map(() => ({}));
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
                    provinceId: this.filterProvince || undefined,
                    roomStatus: this.roomStatusFilter || undefined,
                    page: 0,
                    size: 1000,
                };
                const response = await hotelAdminApi.searchHotels(params);
                if (response.data && response.data.data) {
                    this.hotels = response.data.data.content || response.data.data.items || [];
                } else {
                    this.hotels = [];
                }
            } catch (error) {
                this.hotels = [];
            }
        },
        
        async fetchHotelStatistics() {
            try {
                const response = await hotelAdminApi.getHotelStatistics();
                if (response.data && response.data.data) {
                    this.hotelStatistics = response.data.data;
                }
            } catch (error) {
                console.error('Error fetching hotel statistics:', error);
                // Fallback values
                this.hotelStatistics = {
                    totalHotels: this.hotels.length,
                    totalRooms: 0,
                    availableRooms: 0,
                    outOfStockRooms: 0,
                    nearlyOutOfStockRooms: 0
                };
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
                taxAndFeeAmount: 0,
                discountType: '',
                discountValue: 0
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
                if (!this.imagesToDelete.includes(url)) {
                    this.imagesToDelete.push(url);
                }
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
                if (!room.deleteRoomImageUrls.includes(url)) {
                    room.deleteRoomImageUrls.push(url);
                }
                room.imageUrls.splice(imgIdx, 1);
            } else {
                room.imageFiles.splice(imgIdx, 1);
                room.imagePreviews.splice(imgIdx, 1);
            }
        },
        async submitHotel() {
            const validateResult = this.validateHotelForm();
            if (!validateResult.valid) {
                window.$toast(validateResult.message, 'error');
                return;
            }
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
                const userStore = useUserStore();
                const userName = userStore.user?.name || userStore.user?.email || 'Admin';
                if (this.modalMode === 'add') {
                    res = await hotelAdminApi.createHotel(formData);
                    await notifyHotelCreated(this.newHotel.name, userName);
                    window.$toast('Thêm khách sạn thành công!', 'success');
                } else {
                    res = await hotelAdminApi.updateHotel(hotelData.id, formData);
                    await notifyHotelUpdated(this.newHotel.name, userName);
                    window.$toast('Cập nhật khách sạn thành công!', 'success');
                }
                this.backToList();
                await this.fetchHotels();
                await this.fetchHotelStatistics();
            } catch (err) {
                let msg = 'Có lỗi khi lưu khách sạn!';
                if (err?.response?.data?.message) {
                    msg = err.response.data.message;
                } else if (err?.message) {
                    msg = err.message;
                }
                if (msg && msg.toLowerCase().startsWith('lỗi parse json:')) {
                    msg = msg.split(':').slice(1).join(':').trim();
                }
                window.$toast(msg, 'error');
                console.error('API error:', err);
            }
        },
        validateHotelForm() {
            if (!this.newHotel.name || !this.newHotel.name.trim()) {
                return { valid: false, message: 'Vui lòng nhập tên khách sạn.' };
            }
            if (!this.newHotel.starRating) {
                return { valid: false, message: 'Vui lòng chọn hạng sao.' };
            }
            if (!this.newHotel.provinceId) {
                return { valid: false, message: 'Vui lòng chọn thành phố.' };
            }
            if (!this.newHotel.address || !this.newHotel.address.trim()) {
                return { valid: false, message: 'Vui lòng nhập địa chỉ khách sạn.' };
            }
            if (!this.newHotel.phone || !this.newHotel.phone.trim()) {
                return { valid: false, message: 'Vui lòng nhập số điện thoại.' };
            }
            if (!this.newHotel.email || !this.newHotel.email.trim()) {
                return { valid: false, message: 'Vui lòng nhập email.' };
            }
            for (let i = 0; i < this.newHotel.availableRooms.length; i++) {
                const room = this.newHotel.availableRooms[i];
                const selectedAmenities = Object.keys(room.amenities || {}).filter(k => room.amenities[k]);
                if (!selectedAmenities.length) {
                    return {
                        valid: false,
                        message: `Vui lòng chọn ít nhất 1 tiện ích cho phòng số ${i + 1} (${room.roomType || 'Chưa đặt tên'})`
                    };
                }
            }
            return { valid: true };
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
            this.isViewMode = false;
            this.resetAll();
            this.loadProvincesAndAmenities();
            this.mode = 'edit';
        },
        async editHotel(hotel) {
            this.isEditMode = true;
            this.isViewMode = false;
            this.fromDetailView = false;
            this.modalMode = 'edit';
            this.loadProvincesAndAmenities();
            this.hotelImages = [];
            this.hotelImagePreviews = [];
            this.imagesToDelete = [];
            try {
                const res = await hotelAdminApi.getHotelById(hotel.id);
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
                            discountType: variant.discountType || '',
                            discountValue: (variant.discountValue && typeof variant.discountValue === 'object' && variant.discountValue !== null) ? Number(variant.discountValue) : (variant.discountValue || 0)
                        })),
                    })),
                    amenities: (detail.amenities || []).reduce((acc, a) => { acc[a.id] = true; return acc; }, {}),
                };
            } catch (e) {
                window.$toast('Không lấy được chi tiết khách sạn!', 'error');
                return;
            }
            this.mode = 'edit';
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
            this.fetchHotelStatistics();
        },
        filterByRoomStatus(roomStatus) {
            // If clicking the same filter, clear it
            if (this.roomStatusFilter === roomStatus) {
                this.roomStatusFilter = null;
            } else {
                this.roomStatusFilter = roomStatus;
            }
            this.currentPage = 1;
            this.fetchHotels();
            this.fetchHotelStatistics();
        },
        formatDate(date) {
            return date.toISOString().slice(0, 10);
        },
        formatDateTime(dt) {
            if (!dt) return '';
            const d = new Date(dt);
            return d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' }) + ' ' + d.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
        },
        backToList() {
            this.mode = 'list';
            this.isEditMode = false;
            this.isViewMode = false;
            this.resetAll();
        },
        async viewHotelDetail(hotel) {
            await this.viewDetails(hotel);
        },
        enableEditMode() {
            this.isViewMode = false;
            this.fromDetailView = true;
        },
        saveChanges() {
            this.submitHotel();
        },
        async cancelEdit() {
            this.isViewMode = true;
            if (this.newHotel.id) {
                try {
                    const res = await hotelAdminApi.getHotelById(this.newHotel.id);
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
                                discountType: variant.discountType || '',
                                discountValue: (variant.discountValue && typeof variant.discountValue === 'object' && variant.discountValue !== null) ? Number(variant.discountValue) : (variant.discountValue || 0)
                            })),
                        })),
                        amenities: (detail.amenities || []).reduce((acc, a) => { acc[a.id] = true; return acc; }, {}),
                    };
                } catch (e) {
                    window.$toast('Không lấy được chi tiết khách sạn!', 'error');
                }
            }
        },
        handleBack() {
            if (this.isEditMode && !this.isViewMode) {
                this.isViewMode = true;
            } else {
                this.backToList();
            }
        },
        autoResize(event) {
            event.target.style.height = 'auto';
            event.target.style.height = event.target.scrollHeight + 'px';
        },
        openAmenityModal(roomIdx) {
            this.amenityModalRoomIdx = roomIdx;
            const room = this.newHotel.availableRooms[roomIdx];
            this.amenityModalSelected = Object.keys(room.amenities || {}).filter(k => room.amenities[k]).map(id => Number(id));
            this.showAmenityModal = true;
        },
        closeAmenityModal() {
            this.showAmenityModal = false;
            this.amenityModalRoomIdx = null;
            this.amenityModalSelected = [];
        },
        confirmAmenityModal() {
            if (this.amenityModalRoomIdx !== null) {
                const room = this.newHotel.availableRooms[this.amenityModalRoomIdx];
                room.amenities = {};
                this.amenityModalSelected.forEach(id => {
                    room.amenities[id] = true;
                });
            }
            this.closeAmenityModal();
        },
        removeRoomAmenity(roomIdx, amenityId) {
            const room = this.newHotel.availableRooms[roomIdx];
            if (room.amenities && room.amenities[amenityId]) {
                delete room.amenities[amenityId];
            }
        },
        toggleAmenityModalSelected(id) {
            const idx = this.amenityModalSelected.indexOf(id);
            if (idx === -1) {
                this.amenityModalSelected.push(id);
            } else {
                this.amenityModalSelected.splice(idx, 1);
            }
        },
        updateBreadcrumb() {
            const breadcrumbStore = useAdminBreadcrumbStore();
            const items = [];
            if (this.mode === 'edit') {
                items.push({ label: 'Khách Sạn', onClick: this.backToList });
                if (!this.isEditMode) {
                    items.push({ label: 'Thêm Khách Sạn', active: true });
                } else if (this.isViewMode) {
                    items.push({ label: 'Chi Tiết Khách Sạn', active: true });
                } else if (this.isEditMode && !this.isViewMode && this.newHotel.id && this.fromDetailView) {
                    items.push({ label: 'Chi Tiết Khách Sạn', onClick: this.cancelEdit });
                    items.push({ label: 'Chỉnh Sửa Khách Sạn', active: true });
                } else if (this.isEditMode && !this.isViewMode) {
                    items.push({ label: 'Chỉnh Sửa Khách Sạn', active: true });
                }
            } else {
                items.push({ label: 'Khách Sạn', active: true });
            }
            breadcrumbStore.setBreadcrumb(items);
        },
        openHotelImageUrlPopup(e) {
            this.showHotelImageUrlPopup = true;
            this.hotelImageUrlInput = '';
            this.hotelImageUrlError = '';
            this.$nextTick(() => {
                const btn = this.$refs.hotelImageUrlBtn;
                if (btn) {
                    const rect = btn.getBoundingClientRect();
                    this.hotelImageUrlPopupStyle = {
                        position: 'absolute',
                        top: rect.bottom + window.scrollY + 8 + 'px',
                        left: rect.left + window.scrollX + 'px',
                    };
                }
            });
        },
        closeHotelImageUrlPopup() {
            this.showHotelImageUrlPopup = false;
            this.hotelImageUrlInput = '';
            this.hotelImageUrlError = '';
        },
        confirmHotelImageUrl() {
            const url = this.hotelImageUrlInput.trim();
            if (!url) {
                this.hotelImageUrlError = 'Vui lòng nhập URL.';
                return;
            }
            if (!/^https?:\/\//.test(url)) {
                this.hotelImageUrlError = 'URL không hợp lệ.';
                return;
            }
            if (!this.newHotel.imageUrls.includes(url)) {
                this.newHotel.imageUrls.push(url);
            }
            this.closeHotelImageUrlPopup();
        },
        setRoomImageUrlBtnRef(el, idx) {
            if (!this.roomImageUrlAnchor) this.roomImageUrlAnchor = [];
            this.roomImageUrlAnchor[idx] = el;
        },
        openRoomImageUrlPopup(idx) {
            this.showRoomImageUrlPopup[idx] = true;
            this.roomImageUrlInput[idx] = '';
            this.roomImageUrlError[idx] = '';
            this.$nextTick(() => {
                const btn = this.roomImageUrlAnchor[idx];
                if (btn) {
                    const rect = btn.getBoundingClientRect();
                    if (!this.roomImageUrlPopupStyle) this.roomImageUrlPopupStyle = [];
                    this.roomImageUrlPopupStyle[idx] = {
                        position: 'absolute',
                        top: rect.bottom + window.scrollY + 8 + 'px',
                        left: rect.left + window.scrollX + 'px',
                    };
                }
            });
        },
        closeRoomImageUrlPopup(idx) {
            this.showRoomImageUrlPopup[idx] = false;
            this.roomImageUrlInput[idx] = '';
            this.roomImageUrlError[idx] = '';
        },
        confirmRoomImageUrl(idx) {
            const url = (this.roomImageUrlInput[idx] || '').trim();
            if (!url) {
                this.roomImageUrlError[idx] = 'Vui lòng nhập URL.';
                return;
            }
            if (!/^https?:\/\//.test(url)) {
                this.roomImageUrlError[idx] = 'URL không hợp lệ.';
                return;
            }
            const room = this.newHotel.availableRooms[idx];
            if (!room.imageUrls) room.imageUrls = [];
            if (!room.imageUrls.includes(url)) {
                room.imageUrls.push(url);
            }
            this.closeRoomImageUrlPopup(idx);
        },
        handleClickOutsideRoomUrlPopup(e) {
            (this.showRoomImageUrlPopup || []).forEach((show, idx) => {
                if (show) {
                    const popupEls = this.$refs.roomUrlPopupRefs;
                    let popupEl = null;
                    if (Array.isArray(popupEls)) {
                        popupEl = popupEls.find(el => el && Number(el.dataset.popupIdx) === idx);
                    } else {
                        popupEl = popupEls;
                    }
                    if (popupEl && !popupEl.contains(e.target)) {
                        this.closeRoomImageUrlPopup(idx);
                    }
                }
            });
        },
        handleClickOutsideHotelImageUrlPopup(e) {
            if (this.showHotelImageUrlPopup) {
                const popupEl = this.$refs.hotelImageUrlPopupRef;
                if (popupEl && !popupEl.contains(e.target)) {
                    this.closeHotelImageUrlPopup();
                }
            }
        },
        getDiscountedPrice(v) {
            if (!v.discountType || !v.discountValue) return v.price;
            if (v.discountType === 'amount') return Math.max(0, v.price - v.discountValue);
            if (v.discountType === 'percent') return Math.max(0, v.price * (1 - v.discountValue / 100));
            return v.price;
        },
        getMinDiscountedPrice(room) {
            if (!room.availableVariants || room.availableVariants.length === 0) return 0;
            return Math.min(...room.availableVariants.map(v => this.getDiscountedPrice(v)));
        },
        getMinDiscountedPriceForHotel(hotel) {
            if (!hotel.availableRooms) return 0;
            let min = Infinity;
            hotel.availableRooms.forEach(room => {
                if (room.availableVariants) {
                    room.availableVariants.forEach(variant => {
                        const price = this.getDiscountedPrice(variant);
                        if (price < min) min = price;
                    });
                }
            });
            return min === Infinity ? 0 : min;
        },
    },
    mounted() {
        const userStore = useUserStore();
        console.log('UserStore:', userStore);
        console.log('User:', userStore.user);
        console.log('User roles:', userStore.user?.roles);
        
        if (!userStore.user) {
            console.log('No user data, trying to restore...');
            userStore.restoreUserFromToken().then(() => {
                console.log('After restore - User:', userStore.user);
                console.log('After restore - User roles:', userStore.user?.roles);
            });
        }
        
        const { requireAdmin } = useAdminAuth();
        if (!requireAdmin('hotel')) {
            console.log('Không có quyền admin hotel');
            return;
        }
        
        console.log('Có quyền admin hotel, loading data...');
        
        this.fetchHotels();
        this.loadProvincesAndAmenities();
        this.fetchHotelStatistics();
        document.addEventListener('click', this.handleOutsideClick, true);
        document.addEventListener('click', this.closeAmenityDropdown, true);
        const breadcrumbStore = useAdminBreadcrumbStore();
        const items = [];
        if (this.mode === 'edit') {
            items.push({ label: 'Khách Sạn', onClick: this.backToList });
            if (!this.isEditMode) {
                items.push({ label: 'Thêm Khách Sạn', active: true });
            } else if (this.isViewMode) {
                items.push({ label: 'Chi Tiết Khách Sạn', active: true });
            } else if (this.isEditMode && !this.isViewMode && this.newHotel.id && this.fromDetailView) {
                items.push({ label: 'Chi Tiết Khách Sạn', onClick: this.cancelEdit });
                items.push({ label: 'Chỉnh Sửa Khách Sạn', active: true });
            } else if (this.isEditMode && !this.isViewMode) {
                items.push({ label: 'Chỉnh Sửa Khách Sạn', active: true });
            }
        } else {
            items.push({ label: 'Khách Sạn', active: true });
        }
        breadcrumbStore.setBreadcrumb(items);
        document.addEventListener('mousedown', this.handleClickOutsideRoomUrlPopup);
        document.addEventListener('mousedown', this.handleClickOutsideHotelImageUrlPopup);
    },
    beforeUnmount() {
        document.removeEventListener('click', this.handleOutsideClick, true);
        this.removeDropdownListeners();
        document.removeEventListener('click', this.closeAmenityDropdown, true);
        document.removeEventListener('mousedown', this.handleClickOutsideRoomUrlPopup);
        document.removeEventListener('mousedown', this.handleClickOutsideHotelImageUrlPopup);
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

.custom-url-popup {
  position: absolute;
  z-index: 50;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 0.75rem;
  box-shadow: 0 4px 24px rgba(0,0,0,0.12);
  padding: 1rem;
  min-width: 320px;
}
</style>
