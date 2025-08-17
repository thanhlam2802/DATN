<template>
  <div class="min-h-screen  p-2">
    <!-- Header -->
    <div class="mb-8">
      <div class=" rounded-lg shadow-sm p-6 bg-white">
        <div class="flex justify-between items-center">
      <div>
            <h1 class="text-3xl font-bold text-gray-900">Quản lý Xe buýt</h1>
            <p class="mt-2 text-sm text-gray-600">Thêm, sửa, xóa và quản lý các xe buýt trong hệ thống.</p>
            <div class="mt-4 flex items-center space-x-4 text-sm text-gray-500">
              <span class="flex items-center">
                <div class="w-2 h-2 bg-green-400 rounded-full mr-2"></div>
                Tổng: {{ buses.length }} xe
              </span>
            </div>
      </div>
          <button 
            @click="openCreationModal()" 
            class="inline-flex items-center px-6 py-3 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-gradient-to-r from-blue-600 to-blue-700 hover:from-blue-700 hover:to-blue-800 transition-all duration-200 transform hover:scale-105"
          >
            <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
            </svg>
        Thêm xe buýt mới
      </button>
        </div>
      </div>
    </div>

    <!-- Loading and Error States -->
    <div v-if="loading" class="flex justify-center items-center py-20">
      <div class="text-center">
        <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        <p class="mt-4 text-gray-600">Đang tải dữ liệu...</p>
      </div>
    </div>

    <div v-if="error" class="bg-red-50 border-l-4 border-red-400 p-4 rounded-md shadow-sm">
      <div class="flex">
        <div class="flex-shrink-0">
          <svg class="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
          </svg>
        </div>
        <div class="ml-3">
          <h3 class="text-sm font-medium text-red-800">Có lỗi xảy ra</h3>
          <p class="mt-1 text-sm text-red-700">{{ error }}</p>
        </div>
      </div>
    </div>

    <!-- View Toggle -->
    <div v-if="!loading && !error" class="mb-6">
      <div class="bg-white rounded-lg shadow-sm p-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-4">
            <span class="text-sm font-medium text-gray-700">Hiển thị:</span>
            <div class="flex rounded-lg border border-gray-200 p-1">
              <button 
                @click="viewMode = 'card'"
                :class="[
                  'px-4 py-2 text-sm font-medium rounded-md transition-all duration-200',
                  viewMode === 'card' 
                    ? 'bg-blue-600 text-white shadow-sm' 
                    : 'text-gray-600 hover:text-gray-900 hover:bg-gray-50'
                ]"
              >
                <svg class="w-4 h-4 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path>
                </svg>
                Card
              </button>
              <button 
                @click="viewMode = 'table'"
                :class="[
                  'px-4 py-2 text-sm font-medium rounded-md transition-all duration-200',
                  viewMode === 'table' 
                    ? 'bg-blue-600 text-white shadow-sm' 
                    : 'text-gray-600 hover:text-gray-900 hover:bg-gray-50'
                ]"
              >
                <svg class="w-4 h-4 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M3 6h18m-9 8h9"></path>
                </svg>
                Bảng
              </button>
            </div>
          </div>
          <div class="flex items-center space-x-3">
            <!-- Filter Toggle -->
            <button
              @click="showFilters = !showFilters"
              :class="[
                'inline-flex items-center px-3 py-2 border border-gray-300 rounded-md text-sm font-medium transition-all duration-200',
                showFilters ? 'bg-blue-50 text-blue-700 border-blue-300' : 'bg-white text-gray-700 hover:bg-gray-50'
              ]"
            >
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.207A1 1 0 013 6.5V4z"></path>
              </svg>
              Bộ lọc
              <span v-if="activeFiltersCount > 0" class="ml-2 inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                {{ activeFiltersCount }}
              </span>
            </button>
            
            <!-- Sort Dropdown -->
            <div class="relative">
              <select 
                v-model="filters.sortBy"
                @change="filters.sortOrder = 'asc'"
                class="appearance-none bg-white border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="name">Sắp xếp: Tên A-Z</option>
                <option value="category">Sắp xếp: Loại xe</option>
                <option value="seats">Sắp xếp: Số ghế</option>
                <option value="createdAt">Sắp xếp: Ngày tạo</option>
              </select>
              <button
                @click="filters.sortOrder = filters.sortOrder === 'asc' ? 'desc' : 'asc'"
                class="absolute right-8 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
              >
               
              </button>
            </div>
            
            <!-- Search -->
            <div class="relative">
              <input 
                v-model="searchQuery"
                type="text" 
                placeholder="Tìm kiếm xe buýt..."
                class="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
              >
              <svg class="absolute left-3 top-2.5 h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
              </svg>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Advanced Filters Panel -->
    <transition name="filter-slide">
      <div v-if="showFilters && !loading && !error" class="mb-6">
        <div class="bg-white rounded-lg shadow-sm border border-gray-200">
          <div class="p-6">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-lg font-medium text-gray-900">Bộ lọc nâng cao</h3>
              <button 
                @click="clearFilters"
                class="text-sm text-blue-600 hover:text-blue-800 font-medium"
              >
                Xóa tất cả bộ lọc
              </button>
            </div>
            
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
              <!-- Category Filter -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Loại xe</label>
                <div class="space-y-2 max-h-32 overflow-y-auto border border-gray-200 rounded-md p-2">
                  <label v-for="category in busCategories" :key="category" class="flex items-center">
                    <input 
                      type="checkbox" 
                      :value="category"
                      v-model="filters.categories"
                      class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                    />
                    <span class="ml-2 text-sm text-gray-700">{{ category }}</span>
                  </label>
                </div>
                <p v-if="filters.categories.length > 0" class="mt-1 text-xs text-blue-600">
                  {{ filters.categories.length }} loại được chọn
                </p>
              </div>

              <!-- Seat Range Filter -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Số ghế</label>
                <div class="space-y-3">
                  <div class="flex items-center space-x-2">
                    <input 
                      v-model.number="filters.seatRange.min"
                      type="number" 
                      :min="availableSeatRange.min"
                      :max="filters.seatRange.max"
                      placeholder="Từ"
                      class="w-20 px-2 py-1 border border-gray-300 rounded text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                    />
                    <span class="text-gray-500">-</span>
                    <input 
                      v-model.number="filters.seatRange.max"
                      type="number" 
                      :min="filters.seatRange.min"
                      :max="availableSeatRange.max"
                      placeholder="Đến"
                      class="w-20 px-2 py-1 border border-gray-300 rounded text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                    />
                  </div>
                  <div class="text-xs text-gray-500">
                    Phạm vi: {{ availableSeatRange.min }} - {{ availableSeatRange.max }} ghế
                  </div>
                </div>
              </div>

              <!-- Date Range Filter -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Ngày tạo</label>
                <div class="space-y-2">
                  <input 
                    v-model="filters.dateRange.from"
                    type="date" 
                    class="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                    placeholder="Từ ngày"
                  />
                  <input 
                    v-model="filters.dateRange.to"
                    type="date" 
                    :min="filters.dateRange.from"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                    placeholder="Đến ngày"
                  />
                </div>
              </div>

              <!-- Summary -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Kết quả</label>
                <div class="bg-gray-50 rounded-md p-3 space-y-2">
                  <div class="flex items-center justify-between text-sm">
                    <span class="text-gray-600">Tổng xe buýt:</span>
                    <span class="font-medium text-gray-900">{{ buses.length }}</span>
                  </div>
                  <div class="flex items-center justify-between text-sm">
                    <span class="text-gray-600">Sau lọc:</span>
                    <span class="font-medium text-blue-600">{{ filteredBuses.length }}</span>
                  </div>
                  <div v-if="activeFiltersCount > 0" class="pt-2 border-t border-gray-200">
                    <div class="flex flex-wrap gap-1">
                      <span v-if="filters.categories.length > 0" class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                        {{ filters.categories.length }} loại xe
                      </span>
                      <span v-if="filters.seatRange.min > availableSeatRange.min || filters.seatRange.max < availableSeatRange.max" class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800">
                        {{ filters.seatRange.min }}-{{ filters.seatRange.max }} ghế
                      </span>
                      <span v-if="filters.dateRange.from || filters.dateRange.to" class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-purple-100 text-purple-800">
                        Thời gian
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- Card View -->
    <div v-if="!loading && !error && viewMode === 'card'" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div 
        v-for="bus in filteredBuses" 
        :key="bus.id"
        class="bg-white rounded-xl shadow-sm hover:shadow-lg transition-all duration-300 overflow-hidden group cursor-pointer"
        @click="openDetailModal(bus)"
      >
        <!-- Image Section -->
        <div class="relative h-48 overflow-hidden">
          <img 
            :src="getImageUrl(bus)" 
            alt="Ảnh xe"
            class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
          />
          <div class="absolute top-3 right-3">
            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
              {{ getBusCategoryName(bus) }}
            </span>
          </div>
          <div class="absolute bottom-3 left-3">
            <div class="flex space-x-1">
              <span v-if="bus.busImages && bus.busImages.length > 1" class="inline-flex items-center px-2 py-1 rounded-md text-xs font-medium bg-black bg-opacity-50 text-white">
                <svg class="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                </svg>
                {{ bus.busImages.length }}
              </span>
            </div>
          </div>
        </div>

        <!-- Content Section -->
        <div class="p-5">
          <div class="flex items-start justify-between">
            <div class="flex-1">
              <h3 class="text-lg font-semibold text-gray-900 group-hover:text-blue-600 transition-colors duration-200">
                {{ bus.name }}
              </h3>
              <p class="mt-1 text-sm text-gray-500">{{ bus.licensePlate }}</p>
            </div>
          </div>

          <div class="mt-4 space-y-2">
            <div class="flex items-center justify-between text-sm">
              <span class="text-gray-500">Loại xe:</span>
              <span class="font-medium text-gray-900">{{ getBusCategoryName(bus) }}</span>
            </div>
            <div class="flex items-center justify-between text-sm">
              <span class="text-gray-500">Số ghế:</span>
              <span class="font-medium text-gray-900">{{ bus.totalSeats }}</span>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="mt-5 flex space-x-2">
            <button 
              @click.stop="openDetailModal(bus)"
              class="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 text-sm font-medium py-2 px-3 rounded-lg transition-colors duration-200"
            >
              Xem chi tiết
            </button>
            <button 
              @click.stop="openCreationModal(bus)"
              class="flex-1 bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium py-2 px-3 rounded-lg transition-colors duration-200"
            >
              Chỉnh sửa
            </button>
            <button 
              @click.stop="confirmDelete(bus)"
              class="text-red-600 hover:text-red-700 hover:bg-red-50 p-1 rounded transition-colors"
              title="Xóa xe buýt"
            >
              <Trash2 class="h-4 w-4" />
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Table View -->
    <div v-if="!loading && !error && viewMode === 'table'" class="bg-white rounded-xl shadow-sm overflow-hidden">
      <div class="overflow-x-auto">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
              <th scope="col" class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Xe buýt</th>
              <th scope="col" class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Biển số</th>
              <th scope="col" class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Loại xe</th>
              <th scope="col" class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Số ghế</th>
              <th scope="col" class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Ảnh</th>
              <th scope="col" class="relative px-6 py-4"><span class="sr-only">Hành động</span></th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
            <tr 
              v-for="bus in filteredBuses" 
              :key="bus.id"
              class="hover:bg-gray-50 transition-colors duration-200 cursor-pointer"
              @click="openDetailModal(bus)"
            >
            <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="flex-shrink-0 h-12 w-16">
                    <img 
                      :src="getImageUrl(bus, '80x60')" 
                      alt="Ảnh xe" 
                      class="h-12 w-16 object-cover rounded-lg"
                    />
                  </div>
                  <div class="ml-4">
                    <div class="text-sm font-medium text-gray-900">{{ bus.name }}</div>
                  </div>
                </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ bus.licensePlate }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                  {{ getBusCategoryName(bus) }}
                </span>
              </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ bus.totalSeats }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                <span class="text-gray-400">{{ bus.busImages?.length || 0 }} ảnh</span>
              </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <div class="flex space-x-2 justify-end">
                  <button 
                    @click.stop="openDetailModal(bus)"
                    class="text-blue-600 hover:text-blue-800 transition-colors duration-200"
                    title="Xem chi tiết"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path>
                    </svg>
                  </button>
                  <button 
                    @click.stop="openCreationModal(bus)" 
                    class="text-indigo-600 hover:text-indigo-800 transition-colors duration-200"
                    title="Chỉnh sửa"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                    </svg>
                  </button>
                  <button 
                    @click.stop="confirmDelete(bus)" 
                    class="text-red-600 hover:text-red-800 transition-colors duration-200"
                    title="Xóa"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
                    </svg>
                  </button>
                </div>
            </td>
          </tr>
        </tbody>
      </table>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="!loading && !error && filteredBuses.length === 0" class="text-center py-20">
      <div class="bg-white rounded-xl shadow-sm p-12">
        <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17a2 2 0 11-4 0 2 2 0 014 0zM19 17a2 2 0 11-4 0 2 2 0 014 0z"></path>
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16V6a1 1 0 00-1-1H4a1 1 0 00-1 1v10a1 1 0 001 1h1m8-1a1 1 0 01-1 1H9m4-1V8a1 1 0 011-1h2.586a1 1 0 01.707.293l2.414 2.414a1 1 0 01.293.707V16a1 1 0 01-1 1h-1m-6-1a1 1 0 001 1h1M5 17a2 2 0 104 0M15 17a2 2 0 104 0M9 17h6"></path>
        </svg>
        <h3 class="mt-4 text-lg font-medium text-gray-900">Chưa có xe buýt nào</h3>
        <p class="mt-2 text-sm text-gray-500">
          {{ searchQuery ? 'Không tìm thấy xe buýt phù hợp với từ khóa tìm kiếm.' : 'Bắt đầu bằng cách thêm xe buýt đầu tiên của bạn.' }}
        </p>
        <div class="mt-6">
          <button 
            @click="openCreationModal()" 
            class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700"
          >
            Thêm xe buýt mới
          </button>
        </div>
      </div>
    </div>

    <!-- Detail Modal -->
    <transition name="modal" appear>
      <div v-if="isDetailModalOpen" class="fixed inset-0 z-50 overflow-y-auto" aria-labelledby="modal-title" role="dialog" aria-modal="true">
        <!-- Background overlay -->
        <div class="modal-backdrop fixed inset-0 bg-gray/100  backdrop-blur-sm transition-background" @click="closeDetailModal"></div>
        
        <div class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">        
          <span class="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">&#8203;</span>
          
          <div class="modal-content inline-block align-bottom bg-white rounded-xl text-left overflow-hidden shadow-2xl transform transition-all sm:my-8 sm:align-middle sm:max-w-4xl sm:w-full relative z-10" @click.stop>
            <div class="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
              <!-- Modal Header -->
              <div class="flex items-center justify-between mb-6 pb-4 border-b border-gray-200">
                <div>
                  <h3 class="text-xl leading-6 font-semibold text-gray-900" id="modal-title">
                    🚌 Chi tiết xe buýt
                  </h3>
                  <p class="mt-1 text-sm text-gray-500">{{ selectedBus?.name }}</p>
                </div>
                <button 
                  @click.stop="closeDetailModal"
                  class="rounded-full p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all duration-200"
                >
                  <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>

              <div v-if="selectedBus" class="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <!-- Image Gallery -->
                <div class="space-y-4">
                  <div v-if="selectedBus.busImages && selectedBus.busImages.length > 0" class="space-y-4">
                    <div class="relative">
                      <img 
                        :src="getImageUrl(selectedBus.busImages[currentImageIndex], '400x300')" 
                        :alt="`Ảnh xe ${selectedBus.name}`"
                        class="w-full h-64 object-cover rounded-lg transition-all duration-300"
                      />
                      <div v-if="selectedBus.busImages.length > 1" class="absolute inset-0 flex items-center justify-between p-4">
                        <button 
                          @click.stop="previousImage"
                          class="p-3 rounded-full bg-black bg-opacity-50 text-white hover:bg-opacity-70 transition-all duration-200 transform hover:scale-110"
                        >
                          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
                          </svg>
                        </button>
                        <button 
                          @click.stop="nextImage"
                          class="p-3 rounded-full bg-black bg-opacity-50 text-white hover:bg-opacity-70 transition-all duration-200 transform hover:scale-110"
                        >
                          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
                          </svg>
                        </button>
                      </div>
                      <div v-if="selectedBus.busImages.length > 1" class="absolute bottom-4 left-1/2 transform -translate-x-1/2">
                        <span class="px-3 py-1 rounded-full bg-black bg-opacity-60 text-white text-sm backdrop-blur-sm">
                          {{ currentImageIndex + 1 }} / {{ selectedBus.busImages.length }}
                        </span>
                      </div>
                    </div>
                    
                    <!-- Thumbnail Gallery -->
                    <div v-if="selectedBus.busImages.length > 1" class="grid grid-cols-4 gap-2">
                      <button
                        v-for="(image, index) in selectedBus.busImages"
                        :key="index"
                        @click.stop="currentImageIndex = index"
                        :class="[
                          'relative overflow-hidden rounded-lg border-2 transition-all duration-200 transform hover:scale-105',
                          currentImageIndex === index ? 'border-blue-500 ring-2 ring-blue-200' : 'border-gray-200 hover:border-gray-300'
                        ]"
                      >
                        <img 
                          :src="getImageUrl(image, '100x75')" 
                          :alt="`Thumbnail ${index + 1}`"
                          class="w-full h-16 object-cover transition-all duration-200"
                        />
                        <div v-if="currentImageIndex === index" class="absolute inset-0  bg-opacity-10 flex items-center justify-center">
                          <svg class="w-4 h-4 text-blue-600" fill="currentColor" viewBox="0 0 20 20">
                            <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"></path>
                          </svg>
                        </div>
                      </button>
                    </div>
                  </div>
                  <div v-else class="w-full h-64 bg-gray-200 rounded-lg flex items-center justify-center">
                    <div class="text-center text-gray-500">
                      <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                      </svg>
                      <p class="mt-2">Chưa có ảnh</p>
                    </div>
                  </div>
                </div>

                <!-- Bus Information -->
                <div class="space-y-6">
                  <div class="bg-gray-50 rounded-lg p-4">
                    <h4 class="text-lg font-medium text-gray-900 mb-4">Thông tin cơ bản</h4>
                    <dl class="space-y-3">
                      <div class="flex justify-between">
                        <dt class="text-sm font-medium text-gray-500">Tên xe:</dt>
                        <dd class="text-sm text-gray-900">{{ selectedBus.name }}</dd>
                      </div>
                      <div class="flex justify-between">
                        <dt class="text-sm font-medium text-gray-500">Biển số:</dt>
                        <dd class="text-sm text-gray-900">{{ selectedBus.licensePlate }}</dd>
                      </div>
                      <div class="flex justify-between">
                        <dt class="text-sm font-medium text-gray-500">Loại xe:</dt>
                        <dd class="text-sm text-gray-900">
                          <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                            {{ getBusCategoryName(selectedBus) }}
                          </span>
                        </dd>
                      </div>
                      <div class="flex justify-between">
                        <dt class="text-sm font-medium text-gray-500">Số ghế:</dt>
                        <dd class="text-sm text-gray-900">{{ selectedBus.totalSeats }}</dd>
                      </div>
                      <div class="flex justify-between">
                        <dt class="text-sm font-medium text-gray-500">Số ảnh:</dt>
                        <dd class="text-sm text-gray-900">{{ selectedBus.busImages?.length || 0 }}</dd>
                      </div>
                    </dl>
                  </div>

                  <div v-if="selectedBus.owner" class="bg-gray-50 rounded-lg p-4">
                    <h4 class="text-lg font-medium text-gray-900 mb-4">Thông tin chủ sở hữu</h4>
                    <dl class="space-y-3">
                      <div class="flex justify-between">
                        <dt class="text-sm font-medium text-gray-500">Tên:</dt>
                        <dd class="text-sm text-gray-900">{{ selectedBus.owner.name }}</dd>
                      </div>
                      <div class="flex justify-between">
                        <dt class="text-sm font-medium text-gray-500">Email:</dt>
                        <dd class="text-sm text-gray-900">{{ selectedBus.owner.email }}</dd>
                      </div>
                    </dl>
                  </div>

                  <div v-if="selectedBus.createdAt || selectedBus.updatedAt" class="bg-gray-50 rounded-lg p-4">
                    <h4 class="text-lg font-medium text-gray-900 mb-4">Thông tin thời gian</h4>
                    <dl class="space-y-3">
                      <div v-if="selectedBus.createdAt" class="flex justify-between">
                        <dt class="text-sm font-medium text-gray-500">Ngày tạo:</dt>
                        <dd class="text-sm text-gray-900">{{ formatDate(selectedBus.createdAt) }}</dd>
                      </div>
                      <div v-if="selectedBus.updatedAt" class="flex justify-between">
                        <dt class="text-sm font-medium text-gray-500">Cập nhật lần cuối:</dt>
                        <dd class="text-sm text-gray-900">{{ formatDate(selectedBus.updatedAt) }}</dd>
                      </div>
                    </dl>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- Modal Footer -->
            <div class="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse border-t border-gray-200">
              <button 
                @click.stop="openCreationModal(selectedBus)"
                type="button" 
                class="w-full inline-flex justify-center items-center rounded-lg border border-transparent shadow-sm px-4 py-2 bg-blue-600 text-base font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:ml-3 sm:w-auto sm:text-sm transition-all duration-200 transform hover:scale-105"
              >
                <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                </svg>
                Chỉnh sửa
              </button>
              <button 
                @click.stop="confirmDelete(selectedBus!)"
                type="button" 
                class="mt-3 w-full inline-flex justify-center items-center rounded-lg border border-transparent shadow-sm px-4 py-2 bg-red-600 text-base font-medium text-white hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm transition-all duration-200 transform hover:scale-105"
              >
                <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
                </svg>
                Xóa
              </button>
              <button 
                @click.stop="closeDetailModal"
                type="button" 
                class="mt-3 w-full inline-flex justify-center items-center rounded-lg border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:mt-0 sm:w-auto sm:text-sm transition-all duration-200"
              >
                <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                </svg>
                Đóng
              </button>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- Modals -->
    <BusCreationModal ref="creationModal" @bus-created="handleModalSubmit" @bus-updated="handleModalSubmit" />
    
    <!-- Remove old ConfirmDialog since we're using notification service -->
    <!-- <ConfirmDialog
      v-if="isConfirmDialogOpen"
      :message="'Bạn có chắc chắn muốn xóa xe buýt này không? Hành động này không thể hoàn tác.'"
      @confirm="handleDelete"
      @cancel="isConfirmDialogOpen = false; busToDelete = null"
    /> -->
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { BusAPI } from '@/api/busApi'
import type { Bus } from '@/api/busApi/types/common.types';
import BusCreationModal from './BusCreationModal.vue'
// @ts-ignore - Type definitions will be added later
import { toast, confirm, handleError } from '@/utils/notifications'
import { Trash2 } from 'lucide-vue-next'
// @ts-ignore
import { useAuth } from '@/composables/useAuth'

const buses = ref<Bus[]>([]);
const busCategories = ref<string[]>([]);
const error = ref<string | null>(null);
const loading = ref(false);
const creationModal = ref<any>(null); // Use any for now to avoid type issues

// New reactive variables for enhanced UI
const viewMode = ref<'card' | 'table'>('card');
const searchQuery = ref('');
const isDetailModalOpen = ref(false);
const selectedBus = ref<Bus | null>(null);
const currentImageIndex = ref(0);

// Filter state
const showFilters = ref(false);
const filters = ref({
  categories: [] as string[],
  seatRange: {
    min: 0,
    max: 100
  },
  dateRange: {
    from: '',
    to: ''
  },
  sortBy: 'name', // 'name', 'category', 'seats', 'createdAt'
  sortOrder: 'asc' // 'asc', 'desc'
});

// Filter computed
const availableSeatRange = computed(() => {
  if (buses.value.length === 0) return { min: 0, max: 100 };
  
  const seats = buses.value.map(bus => bus.totalSeats);
  return {
    min: Math.min(...seats),
    max: Math.max(...seats)
  };
});

const filteredBuses = computed(() => {
  let result = buses.value;
  
  // Search filter
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(bus => 
      bus.name.toLowerCase().includes(query) ||
      bus.licensePlate.toLowerCase().includes(query) ||
      getBusCategoryName(bus).toLowerCase().includes(query)
    );
  }
  
  // Category filter
  if (filters.value.categories.length > 0) {
    result = result.filter(bus => {
      const categoryName = getBusCategoryName(bus);
      return filters.value.categories.includes(categoryName);
    });
  }
  
  // Seat range filter
  result = result.filter(bus => 
    bus.totalSeats >= filters.value.seatRange.min && 
    bus.totalSeats <= filters.value.seatRange.max
  );
  
  // Date range filter
  if (filters.value.dateRange.from) {
    const fromDate = new Date(filters.value.dateRange.from);
    result = result.filter(bus => {
      if (!bus.createdAt) return true;
      const busDate = new Date(bus.createdAt);
      return busDate >= fromDate;
    });
  }
  
  if (filters.value.dateRange.to) {
    const toDate = new Date(filters.value.dateRange.to);
    toDate.setHours(23, 59, 59, 999); // End of day
    result = result.filter(bus => {
      if (!bus.createdAt) return true;
      const busDate = new Date(bus.createdAt);
      return busDate <= toDate;
    });
  }
  
  // Sort
  result.sort((a, b) => {
    let aValue, bValue;
    
    switch (filters.value.sortBy) {
      case 'name':
        aValue = a.name.toLowerCase();
        bValue = b.name.toLowerCase();
        break;
      case 'category':
        aValue = getBusCategoryName(a).toLowerCase();
        bValue = getBusCategoryName(b).toLowerCase();
        break;
      case 'seats':
        aValue = a.totalSeats;
        bValue = b.totalSeats;
        break;
      case 'createdAt':
        aValue = new Date(a.createdAt || 0).getTime();
        bValue = new Date(b.createdAt || 0).getTime();
        break;
      default:
        aValue = a.name.toLowerCase();
        bValue = b.name.toLowerCase();
    }
    
    if (aValue < bValue) return filters.value.sortOrder === 'asc' ? -1 : 1;
    if (aValue > bValue) return filters.value.sortOrder === 'asc' ? 1 : -1;
    return 0;
  });
  
  return result;
});

const activeFiltersCount = computed(() => {
  let count = 0;
  if (filters.value.categories.length > 0) count++;
  if (filters.value.seatRange.min > availableSeatRange.value.min || 
      filters.value.seatRange.max < availableSeatRange.value.max) count++;
  if (filters.value.dateRange.from || filters.value.dateRange.to) count++;
  return count;
});

const getBusCategoryName = (bus: Bus): string => {
  // Enhanced category name retrieval with better fallbacks
  if (bus.categoryName) {
    return bus.categoryName;
  }

  return 'Chưa phân loại';
};

const formatDate = (dateString: string | null | undefined): string => {
  if (!dateString) {
    return 'Không rõ';
  }
  try {
    const date = new Date(dateString);
    // Kiểm tra xem date có hợp lệ không
    if (isNaN(date.getTime())) {
      return 'Không rõ';
    }
    return date.toLocaleDateString('vi-VN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch {
    return 'Không rõ';
  }
};

const openDetailModal = (bus: Bus) => {
  selectedBus.value = bus;
  currentImageIndex.value = 0;
  isDetailModalOpen.value = true;
};

const closeDetailModal = () => {
  isDetailModalOpen.value = false;
  selectedBus.value = null;
  currentImageIndex.value = 0;
};

const previousImage = () => {
  if (selectedBus.value?.busImages) {
    currentImageIndex.value = currentImageIndex.value > 0 
      ? currentImageIndex.value - 1 
      : selectedBus.value.busImages.length - 1;
  }
};

const nextImage = () => {
  if (selectedBus.value?.busImages) {
    currentImageIndex.value = currentImageIndex.value < selectedBus.value.busImages.length - 1 
      ? currentImageIndex.value + 1 
      : 0;
  }
};

// Function đơn giản để get image URL, test multiple possible structures
const getImageUrl = (busOrImage: any, size: string = '400x200') => {
  let imageUrl = null;
  
  // If it's a bus object, try different possible structures
  if (busOrImage?.busImages && Array.isArray(busOrImage.busImages) && busOrImage.busImages.length > 0) {
    const firstImage = busOrImage.busImages[0];
    
    // Try các khả năng structure khác nhau:
    imageUrl = firstImage?.image?.url ||           // Nested: busImages[0].image.url
               firstImage?.url ||                  // Direct: busImages[0].url  
               firstImage?.imageUrl ||             // Alternative: busImages[0].imageUrl
               firstImage?.image;                  // Direct string: busImages[0].image
  }
  // Direct image object (for modal)
  else if (busOrImage?.image?.url) {
    imageUrl = busOrImage.image.url;
  }
  // Already a URL string
  else if (typeof busOrImage === 'string') {
    imageUrl = busOrImage;
  }
  
  // If found URL, return it
  if (imageUrl && typeof imageUrl === 'string' && imageUrl.length > 0) {
    return imageUrl;
  }
  
  // Fallback placeholder
  return `data:image/svg+xml,${encodeURIComponent(`
    <svg width="400" height="200" xmlns="http://www.w3.org/2000/svg">
      <rect width="100%" height="100%" fill="#f3f4f6"/>
      <text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" 
            font-family="Arial, sans-serif" font-size="14" fill="#6b7280">
        Không có ảnh
      </text>
    </svg>
  `)}`;
};



const loadBuses = async () => {
  loading.value = true;
  error.value = null;
  try {
    const { requireUserId } = useAuth()
    const ownerId = requireUserId() // ✅ Sử dụng requireUserId từ enhanced useAuth
    const result = await BusAPI.getBusesByOwnerId(ownerId);
    
    
    buses.value = result;
    
    // Load unique categories from buses
    loadBusCategories();
    
    // Initialize seat range filter
    const seatRange = availableSeatRange.value;
    filters.value.seatRange.min = seatRange.min;
    filters.value.seatRange.max = seatRange.max;
    
  } catch (err) {
    error.value = "Không thể tải danh sách xe buýt.";  
  } finally {
    loading.value = false;
  }
};

const loadBusCategories = async () => {
  try {
    // Get unique categories from buses and API
    const uniqueCategories = new Set<string>();
    
    // From current buses
    buses.value.forEach(bus => {
      const categoryName = getBusCategoryName(bus);
      if (categoryName && categoryName !== 'Chưa phân loại') {
        uniqueCategories.add(categoryName);
      }
    });
    
    // ✅ From API - load all global categories
    try {
      const { getAllBusCategories } = await import('@/api/busApi/bus/categoryApi');
      const allCategories = await getAllBusCategories();
      allCategories.forEach(cat => uniqueCategories.add(cat.name));
    } catch (err) {
      // Error loading categories from API is not critical
    }
    
    busCategories.value = Array.from(uniqueCategories).sort();
  } catch (error) {
    console.error('Error loading bus categories:', error);
  }
};

// Filter methods
const clearFilters = () => {
  filters.value.categories = [];
  const seatRange = availableSeatRange.value;
  filters.value.seatRange.min = seatRange.min;
  filters.value.seatRange.max = seatRange.max;
  filters.value.dateRange.from = '';
  filters.value.dateRange.to = '';
  filters.value.sortBy = 'name';
  filters.value.sortOrder = 'asc';
};

const toggleSort = (sortBy: string) => {
  if (filters.value.sortBy === sortBy) {
    filters.value.sortOrder = filters.value.sortOrder === 'asc' ? 'desc' : 'asc';
  } else {
    filters.value.sortBy = sortBy;
    filters.value.sortOrder = 'asc';
  }
};

const getSortIcon = (sortBy: string) => {
  if (filters.value.sortBy !== sortBy) return 'M7 16V4m0 0L3 8m4-4l4 4m6 0v12m0 0l4-4m-4 4l-4-4'; // neutral
  return filters.value.sortOrder === 'asc' 
    ? 'M3 4h13M3 8h9m-9 4h6m4 0l4-4m0 0l4 4m-4-4v12' // asc
    : 'M3 4h13M3 8h9m-9 4h9m5-4v12m0 0l-4-4m4 4l4-4'; // desc
};

const openCreationModal = (bus: Bus | null = null) => {
  if (creationModal.value) {
    creationModal.value.openModal(bus);
  }
  // Đóng detail modal nếu đang mở
  if (isDetailModalOpen.value) {
    closeDetailModal();
  }
};

const handleModalSubmit = async (bus: any) => {
  try {
    // Reload buses to get latest data
    await loadBuses();
    
    // Show appropriate success message
    if (bus?.isNew) {
      toast.created(`xe buýt ${bus?.name || bus?.id || 'mới'}`);
    } else {
      toast.updated(`xe buýt ${bus?.name || bus?.id || ''}`);
    }
    
  } catch (err) {
    console.error('Error reloading buses:', err);
    handleError.api(err, 'tải lại danh sách xe buýt');
  }
};

const confirmDelete = async (bus: Bus) => {
  const confirmed = await confirm.delete(
    `xe buýt ${(bus as any)?.name || bus.id}`,
    {
      details: 'Thao tác này sẽ xóa xe buýt và có thể ảnh hưởng đến các chuyến đi đã lên lịch.'
    }
  );
  
  if (confirmed) {
    try {
      await BusAPI.deleteBus(bus.id);
      
      // Remove from local state
      buses.value = buses.value.filter(b => b.id !== bus.id);
      
      // Show success toast
      toast.deleted('xe buýt');
      
    } catch (err) {
      console.error('Error deleting bus:', err);
      
      // Handle specific error types
      const errorMessage = (err as any)?.message || '';
      if (errorMessage.includes('foreign key') || errorMessage.includes('constraint')) {
        toast.foreignKeyError('xe buýt', 'chuyến đi hoặc đặt chỗ');
      } else {
        handleError.api(err, 'xóa xe buýt');
      }
    }
  }
};

onMounted(() => {
  loadBuses();
});
</script>

<style scoped>
/* Filter Slide Animation */
.filter-slide-enter-active,
.filter-slide-leave-active {
  transition: all 0.3s ease;
}

.filter-slide-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.filter-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* Custom scrollbar for filter sections */
.overflow-y-auto::-webkit-scrollbar {
  width: 4px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 2px;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 2px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* Modal Transition Styles */
.modal-enter-active {
  transition: all 0.3s ease-out;
}

.modal-leave-active {
  transition: all 0.2s ease-in;
}

.modal-enter-from {
  opacity: 0;
}

.modal-enter-to {
  opacity: 1;
}

.modal-leave-from {
  opacity: 1;
}

.modal-leave-to {
  opacity: 0;
}

/* Modal Backdrop Animation */
.modal-enter-active .modal-backdrop {
  transition: opacity 0.3s ease-out;
}

.modal-leave-active .modal-backdrop {
  transition: opacity 0.2s ease-in;
}

.modal-enter-from .modal-backdrop {
  opacity: 0;
}

.modal-enter-to .modal-backdrop {
  opacity: 1;
}

.modal-leave-from .modal-backdrop {
  opacity: 1;
}

.modal-leave-to .modal-backdrop {
  opacity: 0;
}

/* Modal Content Animation */
.modal-enter-active .modal-content {
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.modal-leave-active .modal-content {
  transition: all 0.2s ease-in-out;
}

.modal-enter-from .modal-content {
  opacity: 0;
  transform: scale(0.95) translateY(-20px);
}

.modal-enter-to .modal-content {
  opacity: 1;
  transform: scale(1) translateY(0);
}

.modal-leave-from .modal-content {
  opacity: 1;
  transform: scale(1) translateY(0);
}

.modal-leave-to .modal-content {
  opacity: 0;
  transform: scale(0.95) translateY(-10px);
}
</style> 