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
                            <div class="flex flex-col space-y-2 text-sm">
                                <label v-for="(lbl, k) in amenityLabels" :key="k"
                                    class="flex items-center space-x-3 cursor-pointer text-slate-700 hover:text-slate-900">
                                    <input type="checkbox" v-model="filterAmenities[k]"
                                        class="w-4 h-4 text-blue-600 border-slate-300 rounded focus:ring-blue-500 cursor-pointer" />
                                    <span>{{ lbl }}</span>
                                </label>
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

        <div class="mb-8 bg-white rounded-xl shadow-md border border-slate-200">
            <table class="min-w-full divide-y divide-slate-200">
                <thead class="bg-slate-50">
                    <tr>
                        <th class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Tên
                            khách sạn</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Loại
                            hình</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Hạng
                            sao</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">
                            Thành phố</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Giá
                            từ (VND)</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Đánh
                            giá</th>
                        <th class="px-6 py-3 text-right text-xs font-medium text-slate-500 uppercase tracking-wider">
                            Hành động</th>
                    </tr>
                </thead>
                <tbody class="bg-white divide-y divide-slate-100">
                    <tr v-if="paginatedHotels.length === 0">
                        <td colspan="7" class="px-6 py-10 text-center text-sm text-slate-500">
                            Không tìm thấy khách sạn nào phù hợp.
                        </td>
                    </tr>
                    <tr v-for="h in paginatedHotels" :key="h.id"
                        class="hover:bg-slate-50 transition-colors duration-150">
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-slate-900">{{ h.name }}</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-700">{{ h.type }}</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-700">{{ h.star }} sao</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-700">{{ h.city }}</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-700">{{ formatCurrency(h.minPrice) }}
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-700">{{ h.rating }} ({{ h.reviews }}
                            đánh giá)</td>
                        <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                            <div class="relative inline-block text-left">
                                <button @click.stop="toggleDropdown(h.id)" type="button"
                                    class="inline-flex justify-center w-8 h-8 rounded-full text-slate-500 hover:text-slate-700 hover:bg-slate-100 focus:outline-none focus:ring-0">
                                    <i class="fas fa-ellipsis-v flex items-center justify-center"></i>
                                </button>
                                <div v-if="activeDropdown === h.id"
                                    class="origin-top-right absolute right-7 -mt-8 w-48 rounded-md shadow-lg bg-white z-9999"
                                    role="menu" aria-orientation="vertical">
                                    <div class="py-1" role="none">
                                        <button @click.stop="viewDetails(h)"
                                            class="text-slate-700 flex items-center px-4 py-2 text-sm hover:bg-slate-100 hover:text-slate-900 w-full text-left"
                                            role="menuitem"><i class="fas fa-info-circle mr-3 text-blue-500"></i> Xem
                                            chi tiết</button>
                                        <button @click.stop="openModal('edit', h)"
                                            class="text-slate-700 flex items-center px-4 py-2 text-sm hover:bg-slate-100 hover:text-slate-900 w-full text-left"
                                            role="menuitem"><i class="fas fa-edit mr-3 text-green-500"></i> Chỉnh
                                            sửa</button>
                                        <button @click.stop="deleteHotel(h.id)"
                                            class="text-red-600 flex items-center px-4 py-2 text-sm hover:bg-red-50 hover:text-red-900 w-full text-left"
                                            role="menuitem"><i class="fas fa-trash-alt mr-3"></i> Xóa</button>
                                    </div>
                                </div>
                            </div>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <div class="flex flex-col sm:flex-row justify-between items-center mt-6 text-sm">
            <div class="flex items-center space-x-2 mb-4 sm:mb-0 text-slate-600">
                <label for="itemsPerPage">Hiển thị:</label>
                <select v-model="itemsPerPage" id="itemsPerPage"
                    class="border border-slate-300 rounded-md px-2 py-1 focus:outline-none focus:ring-2 focus:ring-blue-500">
                    <option v-for="option in itemsPerPageOptions" :key="option" :value="option">{{ option }}</option>
                </select>
                <span>kết quả trên tổng số <strong>{{ filteredHotels.length }}</strong></span>
            </div>

            <div class="w-full sm:w-auto">
                <nav v-if="totalPages > 1" aria-label="Pagination">
                    <ul class="flex items-center justify-center sm:justify-end space-x-1">
                        <li>
                            <button @click="prevPage" :disabled="currentPage === 1"
                                class="flex items-center justify-center w-9 h-9 rounded-full text-slate-600 bg-white hover:bg-slate-100 transition-colors disabled:opacity-50 disabled:cursor-not-allowed">
                                <i class="fas fa-chevron-left text-xs"></i>
                            </button>
                        </li>
                        <li v-for="(page, index) in displayedPages" :key="index">
                            <span v-if="page === '...'"
                                class="flex items-center justify-center w-9 h-9 text-slate-500">...</span>
                            <button v-else @click="changePage(page)" :class="[
                                'flex items-center justify-center w-9 h-9 rounded-full transition-colors',
                                currentPage === page ? 'bg-black text-white font-bold shadow-md' : 'text-slate-600 bg-white hover:bg-slate-100'
                            ]">{{ page }}</button>
                        </li>
                        <li>
                            <button @click="nextPage" :disabled="currentPage === totalPages"
                                class="flex items-center justify-center w-9 h-9 rounded-full text-slate-600 bg-white hover:bg-slate-100 transition-colors disabled:opacity-50 disabled:cursor-not-allowed">
                                <i class="fas fa-chevron-right text-xs"></i>
                            </button>
                        </li>
                    </ul>
                </nav>
            </div>
        </div>
        <transition name="fade">
            <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center px-4 sm:px-0 pt-2">
                <div class="absolute inset-0 backdrop-blur-sm" style="background-color: rgba(255, 255, 255, 0.6);"
                    @click="closeModal"></div>
                <div
                    class="relative bg-white rounded-xl shadow-xl w-full max-w-3xl max-h-[85vh] overflow-hidden flex flex-col border border-slate-300">
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
                                    <label class="block text-sm font-semibold text-slate-700 mb-1">Loại hình</label>
                                    <select v-model="newHotel.type"
                                        class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                                        <option disabled value="">Chọn loại hình</option>
                                        <option>Hotel</option>
                                        <option>Villa</option>
                                        <option>Apartment</option>
                                    </select>
                                </div>
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
                            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
                                <div>
                                    <label class="block text-sm font-semibold text-slate-700 mb-1">Thành phố</label>
                                    <select v-model="newHotel.city"
                                        class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                                        <option disabled value="">Chọn thành phố</option>
                                        <option>Hà Nội</option>
                                        <option>Hồ Chí Minh</option>
                                        <option>Đà Nẵng</option>
                                    </select>
                                </div>
                                <div>
                                    <label class="block text-sm font-semibold text-slate-700 mb-1">Quận/Huyện</label>
                                    <select v-model="newHotel.district"
                                        class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                                        <option disabled value="">Chọn quận/huyện</option>
                                        <option>Quận 1</option>
                                        <option>Quận 2</option>
                                    </select>
                                </div>
                                <div>
                                    <label class="block text-sm font-semibold text-slate-700 mb-1">Mã bưu chính</label>
                                    <input v-model="newHotel.postalCode" type="text" placeholder="Nhập mã bưu chính"
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
                                <h2 class="font-semibold text-slate-800 text-base">Thông tin phòng</h2>
                                <button type="button" @click="addRoom"
                                    class="text-blue-600 font-semibold border border-blue-600 rounded-md px-4 py-2 hover:bg-blue-50 transition shadow-sm">
                                    + Thêm loại phòng
                                </button>
                            </div>

                            <div v-for="(r, idx) in newHotel.rooms" :key="idx"
                                class="border border-slate-200 rounded-md p-4 space-y-4 mb-4 shadow-sm">
                                <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
                                    <div>
                                        <label class="block text-sm font-semibold text-slate-700 mb-1">Loại
                                            phòng</label>
                                        <input v-model="r.type" type="text"
                                            placeholder="Ví dụ: Phòng Deluxe, Phòng Superior"
                                            class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                                    </div>
                                    <div>
                                        <label class="block text-sm font-semibold text-slate-700 mb-1">Sức chứa</label>
                                        <select v-model="r.capacity"
                                            class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                                            <option disabled value="">Chọn sức chứa</option>
                                            <option>1</option>
                                            <option>2</option>
                                            <option>3</option>
                                            <option>4</option>
                                            <option>5+</option>
                                        </select>
                                    </div>
                                    <div>
                                        <label class="block text-sm font-semibold text-slate-700 mb-1">Giường</label>
                                        <select v-model="r.bedType"
                                            class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                                            <option disabled value="">Chọn loại giường</option>
                                            <option>Giường đơn</option>
                                            <option>Giường đôi</option>
                                            <option>Giường queen</option>
                                            <option>Giường king</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                    <div>
                                        <label class="block text-sm font-semibold text-slate-700 mb-1">Số lượng
                                            phòng</label>
                                        <input v-model.number="r.quantity" type="number" min="0"
                                            placeholder="Nhập số lượng phòng"
                                            class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                                    </div>
                                    <div>
                                        <label class="block text-sm font-semibold text-slate-700 mb-1">Giá phòng
                                            (VND)</label>
                                        <input v-model.number="r.price" type="number" min="0"
                                            placeholder="Nhập giá phòng"
                                            class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                                    </div>
                                </div>
                                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                    <div>
                                        <label class="block text-sm font-semibold text-slate-700 mb-1">Giảm giá
                                            (%)</label>
                                        <input v-model.number="r.discount" type="number" min="0" max="100"
                                            placeholder="Nhập % giảm giá"
                                            class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
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
                                </div>
                                <div class="flex justify-end">
                                    <button type="button" @click="removeRoom(idx)"
                                        class="text-red-600 text-sm font-semibold hover:underline">Xóa phòng</button>
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

                        <form v-else-if="activeTab === 2" class="space-y-6" autocomplete="off" novalidate
                            @submit.prevent="handleSubmit">
                            <section>
                                <h2 class="text-lg font-bold text-slate-800 mb-4">Tiện nghi khách sạn</h2>
                                <div
                                    class="grid grid-cols-1 sm:grid-cols-2 gap-x-8 gap-y-4 text-base font-semibold text-slate-900">
                                    <label v-for="(lbl, k) in amenityLabels" :key="k"
                                        class="flex items-center space-x-2">
                                        <input type="checkbox" v-model="form.amenities[k]"
                                            class="w-4 h-4 text-blue-600 border-slate-300 rounded focus:ring-blue-500" />
                                        <span>{{ lbl }}</span>
                                    </label>
                                </div>
                            </section>

                            <section>
                                <h2 class="text-lg font-bold text-slate-800 mb-4">Chính sách khách sạn</h2>
                                <div class="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-4 mb-6">
                                    <div>
                                        <label class="block text-sm font-semibold text-slate-700 mb-1">Giờ nhận
                                            phòng</label>
                                        <select v-model="form.policy.checkin"
                                            class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                                            <option disabled value="">Chọn giờ nhận phòng</option>
                                            <option>14:00</option>
                                            <option>15:00</option>
                                            <option>16:00</option>
                                        </select>
                                    </div>
                                    <div>
                                        <label class="block text-sm font-semibold text-slate-700 mb-1">Giờ trả
                                            phòng</label>
                                        <select v-model="form.policy.checkout"
                                            class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                                            <option disabled value="">Chọn giờ trả phòng</option>
                                            <option>11:00</option>
                                            <option>12:00</option>
                                            <option>13:00</option>
                                        </select>
                                    </div>
                                </div>
                                <div>
                                    <label class="block text-sm font-semibold text-slate-700 mb-1">Chính sách
                                        khác</label>
                                    <textarea v-model="form.policy.other" rows="4"
                                        placeholder="Nhập các chính sách bổ sung (ví dụ: chính sách hủy, vật nuôi, trẻ em...)"
                                        class="w-full border border-slate-300 rounded-md px-3 py-2 text-slate-900 resize-y focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"></textarea>
                                </div>
                            </section>

                            <div class="flex justify-between">
                                <button type="button" @click="activeTab = 1"
                                    class="px-4 py-2 border border-slate-300 rounded-md font-semibold text-slate-900 hover:bg-slate-100 shadow-sm">Quay
                                    lại</button>
                                <button type="submit"
                                    class="px-6 py-3 bg-black text-white rounded-md font-semibold hover:bg-gray-800 shadow-sm">{{
                                        modalMode === 'add' ? 'Hoàn tất đăng ký' : 'Lưu thay đổi' }}</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </transition>
    </div>
</template>

<script>
export default {
    name: 'HotelManager',
    data() {
        return {
            showModal: false, activeTab: 0, tabs: ['Thông tin cơ bản', 'Phòng & Giá', 'Tiện nghi'],
            hotels: [
                { id: 1, name: 'Khách sạn Majestic Sài Gòn', type: 'Hotel', star: 5, city: 'Hồ Chí Minh', address: '01 Đồng Khởi, Bến Nghé, Quận 1', postalCode: '70000', description: 'Khách sạn cổ kính, sang trọng ngay trung tâm thành phố.', minPrice: 2500000, rating: 4.7, reviews: 1500, rooms: [{ type: 'Phòng Deluxe Hướng Sông', capacity: '2', bedType: 'Giường đôi', quantity: 10, price: 2800000, discount: 10 }], amenities: { wifi: true, parking: true, pool: true, restaurant: true, spa: true, gym: true, ac: true, breakfast: true, elevator: true }, policy: { checkin: '14:00', checkout: '12:00', other: 'Không hút thuốc trong phòng.' } },
                { id: 2, name: 'InterContinental Hanoi Landmark72', type: 'Hotel', star: 5, city: 'Hà Nội', address: 'Tòa nhà Keangnam Hanoi Landmark72, Phạm Hùng, Mễ Trì', postalCode: '10000', description: 'Khách sạn cao nhất Hà Nội với tầm nhìn toàn cảnh.', minPrice: 3200000, rating: 4.8, reviews: 1800, rooms: [{ type: 'Phòng Superior', capacity: '2', bedType: 'Giường king', quantity: 15, price: 3500000, discount: 5 }], amenities: { wifi: true, parking: true, pool: true, restaurant: true, spa: true, gym: true, ac: true, breakfast: true, elevator: true }, policy: { checkin: '15:00', checkout: '11:00', other: 'Phụ phí cho trẻ em trên 12 tuổi.' } },
                { id: 3, name: 'A La Carte Da Nang Beach', type: 'Hotel', star: 4, city: 'Đà Nẵng', address: '200 Võ Nguyên Giáp, Phước Mỹ, Sơn Trà', postalCode: '55000', description: 'Khách sạn hiện đại với hồ bơi vô cực trên tầng thượng.', minPrice: 1800000, rating: 4.5, reviews: 1200, rooms: [{ type: 'Căn hộ Studio Biển', capacity: '2', bedType: 'Giường đôi', quantity: 8, price: 2000000, discount: 0 }], amenities: { wifi: true, parking: true, pool: true, restaurant: true, spa: false, gym: true, ac: true, breakfast: false, elevator: true }, policy: { checkin: '14:00', checkout: '12:00', other: 'Hủy phòng miễn phí 24h trước khi nhận phòng.' } },
                { id: 4, name: 'Fusion Resort Phu Quoc', type: 'Villa', star: 5, city: 'Phú Quốc', address: 'Biển Cửa Cạn, Ấp 5, Xã Cửa Cạn', postalCode: '92000', description: 'Khu nghỉ dưỡng biệt thự sang trọng với hồ bơi riêng.', minPrice: 5000000, rating: 4.9, reviews: 900, rooms: [{ type: 'Biệt thự 1 phòng ngủ Hồ bơi riêng', capacity: '2', bedType: 'Giường king', quantity: 5, price: 5500000, discount: 15 }], amenities: { wifi: true, parking: true, pool: true, restaurant: true, spa: true, gym: true, ac: true, breakfast: true, elevator: false }, policy: { checkin: '15:00', checkout: '12:00', other: 'Giá đã bao gồm tất cả các bữa ăn.' } },
                { id: 5, name: 'Dalat Edensee Lake Resort & Spa', type: 'Resort', star: 5, city: 'Đà Lạt', address: 'Khu du lịch hồ Tuyền Lâm, Phường 4', postalCode: '67000', description: 'Khu nghỉ dưỡng ven hồ Tuyền Lâm thơ mộng.', minPrice: 2200000, rating: 4.6, reviews: 1100, rooms: [{ type: 'Phòng Mimosa Superior', capacity: '2', bedType: 'Giường đôi', quantity: 20, price: 2200000, discount: 0 }], amenities: { wifi: true, parking: true, pool: false, restaurant: true, spa: true, gym: true, ac: true, breakfast: true, elevator: true }, policy: { checkin: '14:00', checkout: '12:00', other: 'Có dịch vụ xe đưa đón sân bay.' } },
                { id: 6, name: 'Hotel de l\'Opera Hanoi - MGallery', type: 'Hotel', star: 5, city: 'Hà Nội', address: '29 Tràng Tiền, Hoàn Kiếm', postalCode: '10000', description: 'Kiến trúc Pháp cổ điển ngay gần Nhà hát Lớn.', minPrice: 2900000, rating: 4.7, reviews: 950, rooms: [{ type: 'Phòng L\'Opera Grand', capacity: '2', bedType: 'Giường King', quantity: 7, price: 3200000, discount: 10 }], amenities: { wifi: true, parking: true, pool: true, restaurant: true, spa: true, gym: true, ac: true, breakfast: true, elevator: true }, policy: { checkin: '14:00', checkout: '12:00', other: '' } },
                { id: 7, name: 'The Reverie Saigon', type: 'Hotel', star: 5, city: 'Hồ Chí Minh', address: '22-36 Nguyễn Huệ, Bến Nghé, Quận 1', postalCode: '70000', description: 'Khách sạn xa hoa, lộng lẫy bậc nhất Sài Gòn.', minPrice: 7500000, rating: 4.9, reviews: 800, rooms: [{ type: 'Phòng Deluxe', capacity: '2', bedType: 'Giường King', quantity: 12, price: 7500000, discount: 0 }], amenities: { wifi: true, parking: true, pool: true, restaurant: true, spa: true, gym: true, ac: true, breakfast: true, elevator: true }, policy: { checkin: '15:00', checkout: '12:00', other: '' } },
                { id: 8, name: 'Six Senses Ninh Van Bay', type: 'Villa', star: 5, city: 'Nha Trang', address: 'Vịnh Ninh Vân, Ninh Hòa', postalCode: '65000', description: 'Biệt thự trên vịnh biển hoang sơ và riêng tư.', minPrice: 9800000, rating: 4.9, reviews: 750, rooms: [{ type: 'Hill Top Pool Villa', capacity: '2', bedType: 'Giường King', quantity: 4, price: 10500000, discount: 5 }], amenities: { wifi: true, parking: false, pool: true, restaurant: true, spa: true, gym: true, ac: true, breakfast: true, elevator: false }, policy: { checkin: '14:00', checkout: '12:00', other: 'Chỉ đến được bằng tàu cao tốc.' } },
                { id: 9, name: 'Topas Ecolodge', type: 'Lodge', star: 4, city: 'Sa Pa', address: 'Thôn Lếch, Xã Thanh Bình', postalCode: '33000', description: 'Nghỉ dưỡng sinh thái trên đỉnh núi với view ruộng bậc thang.', minPrice: 4500000, rating: 4.8, reviews: 600, rooms: [{ type: 'Premium Executive Bungalow', capacity: '2', bedType: 'Giường đôi', quantity: 10, price: 4500000, discount: 0 }], amenities: { wifi: true, parking: true, pool: true, restaurant: true, spa: true, gym: false, ac: false, breakfast: true, elevator: false }, policy: { checkin: '14:00', checkout: '11:00', other: '' } },
                { id: 10, name: 'Mia Resort Mui Ne', type: 'Resort', star: 4, city: 'Phan Thiết', address: '24 Nguyễn Đình Chiểu, Hàm Tiến', postalCode: '80000', description: 'Khu nghỉ dưỡng boutique bên bờ biển.', minPrice: 1500000, rating: 4.5, reviews: 850, rooms: [{ type: 'Sapa House Garden View', capacity: '2', bedType: 'Giường đôi', quantity: 15, price: 1500000, discount: 10 }], amenities: { wifi: true, parking: true, pool: true, restaurant: true, spa: true, gym: false, ac: true, breakfast: true, elevator: false }, policy: { checkin: '14:00', checkout: '12:00', other: 'Miễn phí cho trẻ dưới 6 tuổi.' } },
            ],
            newHotel: { id: null, name: '', type: '', star: '', address: '', city: '', district: '', postalCode: '', description: '', rooms: [{ type: '', capacity: '', bedType: '', quantity: 0, price: 0, discount: 0 }], amenities: {}, policy: {} },
            modalMode: 'add', activeDropdown: null,
            searchQuery: '', showFilterDropdown: false, filterStar: '', filterPriceMax: 10000000,
            filterAmenities: { wifi: false, parking: false, pool: false, restaurant: false, spa: false, gym: false, ac: false, breakfast: false, elevator: false },
            form: { amenities: { wifi: false, parking: false, pool: false, restaurant: false, spa: false, gym: false, ac: false, breakfast: false, elevator: false }, policy: { checkin: '', checkout: '', other: '' } },
            amenityLabels: { wifi: 'WiFi miễn phí', parking: 'Bãi đỗ xe', pool: 'Hồ bơi', restaurant: 'Nhà hàng', spa: 'Spa', gym: 'Phòng tập gym', ac: 'Điều hòa', breakfast: 'Bữa sáng', elevator: 'Thang máy' },
            currentPage: 1, itemsPerPage: 5, itemsPerPageOptions: [5, 10, 15, 20],
        };
    },
    computed: {
        filteredHotels() {
            return this.hotels.filter(hotel => {
                const matchesSearchQuery = hotel.name.toLowerCase().includes(this.searchQuery.toLowerCase()) || hotel.city.toLowerCase().includes(this.searchQuery.toLowerCase());
                const matchesStar = this.filterStar === '' || hotel.star === parseInt(this.filterStar);
                const matchesPrice = hotel.minPrice <= this.filterPriceMax;
                const matchesAmenities = Object.keys(this.filterAmenities).every(amenityKey => !this.filterAmenities[amenityKey] || (hotel.amenities && hotel.amenities[amenityKey]));
                return matchesSearchQuery && matchesStar && matchesPrice && matchesAmenities;
            });
        },
        totalPages() {
            return Math.ceil(this.filteredHotels.length / this.itemsPerPage);
        },
        paginatedHotels() {
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
        itemsPerPage() { this.currentPage = 1; }
    },
    methods: {
        formatCurrency(value) { return value == null ? 'N/A' : new Intl.NumberFormat('vi-VN').format(value); },
        openModal(mode, hotel = null) {
            this.resetAll(); this.modalMode = mode; this.activeTab = 0;
            if (mode === 'edit' && hotel) {
                this.newHotel = JSON.parse(JSON.stringify(hotel));
                this.form.amenities = { ...this.newHotel.amenities }; this.form.policy = { ...this.newHotel.policy };
            }
            this.showModal = true;
        },
        closeModal() { this.showModal = false; this.activeDropdown = null; },
        toggleDropdown(hotelId) { this.activeDropdown = this.activeDropdown === hotelId ? null : hotelId; },
        toggleFilterDropdown() { this.showFilterDropdown = !this.showFilterDropdown; },
        setFilterStar(star) { this.filterStar = star; },
        resetFilters() { this.searchQuery = ''; this.filterStar = ''; this.filterPriceMax = 10000000; for (const key in this.filterAmenities) { this.filterAmenities[key] = false; } },
        viewDetails(hotel) { alert(`Xem chi tiết khách sạn: ${hotel.name}\nĐịa chỉ: ${hotel.address}\nMô tả: ${hotel.description}`); this.activeDropdown = null; },
        addRoom() { this.newHotel.rooms.push({ type: '', capacity: '', bedType: '', quantity: 0, price: 0, discount: 0 }); },
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
        resetAll() { this.newHotel = { id: null, name: '', type: '', star: '', address: '', city: '', district: '', postalCode: '', description: '', rooms: [{ type: '', capacity: '', bedType: '', quantity: 0, price: 0, discount: 0 }], amenities: {}, policy: {} }; for (const key in this.form.amenities) { this.form.amenities[key] = false; } this.form.policy = { checkin: '', checkout: '', other: '' }; this.modalMode = 'add'; },
        changePage(page) { if (page >= 1 && page <= this.totalPages) this.currentPage = page; },
        nextPage() { if (this.currentPage < this.totalPages) this.currentPage++; },
        prevPage() { if (this.currentPage > 1) this.currentPage--; },

        handleOutsideClick(event) {                
            if (this.activeDropdown !== null) {
                const clickedWithinActionContainer = event.target.closest('.relative.inline-block');
                if (!clickedWithinActionContainer) {
                    this.activeDropdown = null;
                }               
            }

            if (this.showFilterDropdown) {
                const filterContainerElement = this.$refs.filterDropdownContainer;
                if (filterContainerElement && !filterContainerElement.contains(event.target)) {
                    this.showFilterDropdown = false;
                }
            }
        }
    },
    mounted() {
        document.addEventListener('click', this.handleOutsideClick);
    },
    beforeUnmount() {
        document.removeEventListener('click', this.handleOutsideClick);
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
