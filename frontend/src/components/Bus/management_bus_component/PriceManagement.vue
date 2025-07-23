<template>
  <div class="space-y-6">
    <!-- Header Section -->
    <div class="sm:flex sm:items-center sm:justify-between">
      <div>
        <h3 class="text-lg font-medium leading-6 text-gray-900">Quản lý Giá vé</h3>
        <p class="mt-1 text-sm text-gray-500">Thiết lập và quản lý giá vé cho các tuyến đường theo loại xe</p>
      </div>
      <div class="mt-4 sm:mt-0 flex space-x-3">
        <button @click="showBulkUpdateModal = true" class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50">
          <svg class="-ml-1 mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
          </svg>
          Cập nhật hàng loạt
        </button>
        <button @click="showAddModal = true" class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700">
          <svg class="-ml-1 mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          Thêm quy tắc giá
        </button>
      </div>
    </div>

    <!-- Error Display -->
    <div v-if="priceManager.error.value" class="bg-red-50 border-l-4 border-red-400 p-4 rounded-md">
      <div class="flex">
        <div class="flex-shrink-0">
          <svg class="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
          </svg>
        </div>
        <div class="ml-3">
          <h3 class="text-sm font-medium text-red-800">Có lỗi xảy ra</h3>
          <div class="mt-2 text-sm text-red-700">
            <p>{{ priceManager.error.value }}</p>
          </div>
          <div class="mt-4">
            <button @click="priceManager.clearError()" class="bg-red-100 px-3 py-1.5 rounded-md text-sm font-medium text-red-800 hover:bg-red-200">
              Đóng
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Filters -->
    <div class="bg-white shadow rounded-lg p-6">
      <h4 class="text-sm font-medium text-gray-900 mb-4">Lọc quy tắc giá</h4>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Tuyến đường</label>
          <div class="relative">
            <select v-model="priceManager.filters.value.route" class="appearance-none w-full bg-white border border-gray-300 rounded-lg shadow-sm pl-3 pr-10 py-2 text-sm text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
              <option value="">Tất cả tuyến</option>
              <option v-for="route in priceManager.allRoutes.value" :key="route.id" :value="route.id">{{ route.origin }} → {{ route.destination }}</option>
            </select>
            <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
              <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
              </svg>
            </div>
          </div>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Loại xe</label>
          <div class="relative">
            <select v-model="priceManager.filters.value.busCategory" class="appearance-none w-full bg-white border border-gray-300 rounded-lg shadow-sm pl-3 pr-10 py-2 text-sm text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
              <option value="">Tất cả loại xe</option>
              <option v-for="category in priceManager.allBusCategories.value" :key="category.id" :value="category.id">{{ category.name }}</option>
            </select>
            <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
              <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
              </svg>
            </div>
          </div>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Trạng thái</label>
          <div class="relative">
            <select v-model="priceManager.filters.value.status" class="appearance-none w-full bg-white border border-gray-300 rounded-lg shadow-sm pl-3 pr-10 py-2 text-sm text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
              <option value="">Tất cả trạng thái</option>
              <option value="active">Đang hiệu lực</option>
              <option value="scheduled">Sắp có hiệu lực</option>
              <option value="expired">Đã hết hạn</option>
            </select>
            <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
              <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
              </svg>
            </div>
          </div>
        </div>
        <div class="flex items-end space-x-2">
          <button @click="priceManager.applyFilters()" class="flex-1 px-4 py-2 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500">
            Lọc
          </button>
          <button @click="priceManager.clearFilters()" class="px-4 py-2 bg-white border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-gray-500">
            Xóa
          </button>
        </div>
      </div>
    </div>

    <!-- Price Summary Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-green-500 rounded-md flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Giá trung bình</dt>
                <dd class="text-lg font-medium text-gray-900">{{ priceManager.formatPrice(priceManager.priceStats.value.averageBasePrice) }}</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-blue-500 rounded-md flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 11h.01"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Tổng quy tắc giá</dt>
                <dd class="text-lg font-medium text-gray-900">{{ priceManager.priceStats.value.total }}</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-yellow-500 rounded-md flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Đang hiệu lực</dt>
                <dd class="text-lg font-medium text-gray-900">{{ priceManager.priceStats.value.active }}</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-purple-500 rounded-md flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Có khuyến mãi</dt>
                <dd class="text-lg font-medium text-gray-900">{{ priceManager.priceStats.value.totalPromotions }}</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Price Table -->
    <div class="bg-white shadow overflow-hidden sm:rounded-md">
      <div class="px-6 py-4 border-b border-gray-200">
        <h4 class="text-lg font-medium text-gray-900">Quy tắc giá vé</h4>
      </div>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tuyến đường</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Loại xe</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Giá cơ sở</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Giá khuyến mãi</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Hiệu lực từ</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Hiệu lực đến</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Trạng thái</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Hành động</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <!-- Loading State -->
            <tr v-if="priceManager.loading.value">
              <td colspan="8" class="px-6 py-8 text-center text-gray-500">
                <div class="flex items-center justify-center">
                  <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-blue-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 818-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 714 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Đang tải dữ liệu...
                </div>
              </td>
            </tr>
            
            <!-- Empty State -->
            <tr v-else-if="priceManager.filteredPrices.value.length === 0">
              <td colspan="8" class="px-6 py-8 text-center text-gray-500">
                <div class="flex flex-col items-center">
                  <svg class="w-12 h-12 text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"></path>
                  </svg>
                  <p class="text-lg font-medium text-gray-900 mb-1">Chưa có quy tắc giá nào</p>
                  <p class="text-gray-500">Bắt đầu bằng cách thêm quy tắc giá đầu tiên</p>
                </div>
              </td>
            </tr>
            
            <!-- Data Rows -->
            <tr v-else v-for="price in priceManager.filteredPrices.value" :key="price.id">
                              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ price.route.origin }} → {{ price.route.destination }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                <span :class="getBusCategoryBadgeClass(price.busCategory)" class="inline-flex px-2 py-1 text-xs font-semibold rounded-full">
                  {{ price.busCategory.name }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ priceManager.formatPrice(price.basePrice) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                <span v-if="price.promotionPrice" class="text-red-600 font-medium">{{ priceManager.formatPrice(price.promotionPrice) }}</span>
                <span v-else class="text-gray-400">-</span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(price.validFrom) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(price.validTo) }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatusBadgeClass(priceManager.getPriceStatus(price))" class="inline-flex px-2 py-1 text-xs font-semibold rounded-full">
                  {{ getStatusText(priceManager.getPriceStatus(price)) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <div class="flex space-x-2">
                  <button @click="editPrice(price)" class="text-blue-600 hover:text-blue-900">Sửa</button>
                  <button @click="deletePrice(price)" class="text-red-600 hover:text-red-900">Xóa</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Add/Edit Modal -->
    <div v-if="showAddModal" class="fixed inset-0 bg-gray-200 bg-opacity-20 backdrop-blur-md overflow-y-auto h-full w-full z-50">
      <div class="relative top-20 mx-auto p-5 border w-11/12 md:w-3/4 lg:w-1/2 shadow-lg rounded-md bg-white">
        <div class="mt-3">
          <h3 class="text-lg font-medium text-gray-900 mb-4">{{ priceManager.editingPriceId.value ? 'Sửa quy tắc giá' : 'Thêm quy tắc giá mới' }}</h3>
          <form @submit.prevent="savePrice" class="space-y-4">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Tuyến đường *</label>
                <div class="relative">
                  <select v-model="priceManager.priceForm.value.routeId" required class="appearance-none w-full bg-white border border-gray-300 rounded-lg shadow-sm pl-3 pr-10 py-2 text-sm text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
                    <option value="">Chọn tuyến đường</option>
                    <option v-for="route in priceManager.allRoutes.value" :key="route.id" :value="route.id">{{ route.origin }} → {{ route.destination }}</option>
                  </select>
                  <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                    <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                    </svg>
                  </div>
                </div>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Loại xe *</label>
                <div class="relative">
                  <select v-model="priceManager.priceForm.value.busCategoryId" required class="appearance-none w-full bg-white border border-gray-300 rounded-lg shadow-sm pl-3 pr-10 py-2 text-sm text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
                    <option value="">Chọn loại xe</option>
                    <option v-for="category in priceManager.allBusCategories.value" :key="category.id" :value="category.id">{{ category.name }}</option>
                  </select>
                  <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                    <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                    </svg>
                  </div>
                </div>
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Giá cơ sở (VND) *</label>
                <input v-model.number="priceManager.priceForm.value.basePrice" type="number" min="1" required class="w-full border-gray-300 rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Giá khuyến mãi (VND)</label>
                <input v-model.number="priceManager.priceForm.value.promotionPrice" type="number" min="1" class="w-full border-gray-300 rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500">
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Hiệu lực từ *</label>
                <input v-model="priceManager.priceForm.value.validFrom" type="date" required class="w-full bg-white border border-gray-300 rounded-lg shadow-sm px-3 py-2 text-sm text-gray-700 placeholder-gray-400 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Hiệu lực đến *</label>
                <input v-model="priceManager.priceForm.value.validTo" type="date" required class="w-full bg-white border border-gray-300 rounded-lg shadow-sm px-3 py-2 text-sm text-gray-700 placeholder-gray-400 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Ghi chú</label>
              <textarea v-model="priceManager.priceForm.value.notes" rows="3" class="w-full border-gray-300 rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500" placeholder="Ghi chú về quy tắc giá này (tùy chọn)"></textarea>
            </div>

            <div class="flex justify-end space-x-3 pt-4">
              <button @click="closeModal" type="button" class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 border border-gray-300 rounded-md hover:bg-gray-200">
                Hủy
              </button>
              <button type="submit" :disabled="priceManager.loading.value" class="px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-md hover:bg-blue-700 disabled:opacity-50">
                <span v-if="priceManager.loading.value">Đang xử lý...</span>
                <span v-else>{{ priceManager.editingPriceId.value ? 'Cập nhật' : 'Thêm mới' }}</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Bulk Update Modal -->
    <div v-if="showBulkUpdateModal" class="fixed inset-0 bg-gray-200 bg-opacity-20 backdrop-blur-md overflow-y-auto h-full w-full z-50">
      <div class="relative top-20 mx-auto p-5 border w-11/12 md:w-1/2 shadow-lg rounded-md bg-white">
        <div class="mt-3">
          <h3 class="text-lg font-medium text-gray-900 mb-4">Cập nhật giá hàng loạt</h3>
          <form @submit.prevent="applyBulkUpdate" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Loại cập nhật</label>
              <div class="relative">
                <select v-model="priceManager.bulkUpdate.value.type" required class="appearance-none w-full bg-white border border-gray-300 rounded-lg shadow-sm pl-3 pr-10 py-2 text-sm text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
                  <option value="">Chọn loại cập nhật</option>
                  <option value="percentage">Thay đổi theo phần trăm</option>
                  <option value="fixed">Thay đổi số tiền cố định</option>
                </select>
                <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                  <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                  </svg>
                </div>
              </div>
            </div>

            <div v-if="priceManager.bulkUpdate.value.type">
              <label class="block text-sm font-medium text-gray-700 mb-2">
                {{ priceManager.bulkUpdate.value.type === 'percentage' ? 'Phần trăm thay đổi (%)' : 'Số tiền thay đổi (VND)' }}
              </label>
              <input v-model.number="priceManager.bulkUpdate.value.value" type="number" required class="w-full border-gray-300 rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500">
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Áp dụng cho</label>
              <div class="relative">
                <select v-model="priceManager.bulkUpdate.value.applyTo" required class="appearance-none w-full bg-white border border-gray-300 rounded-lg shadow-sm pl-3 pr-10 py-2 text-sm text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
                  <option value="">Chọn phạm vi áp dụng</option>
                  <option value="all">Tất cả quy tắc giá</option>
                  <option value="route">Theo tuyến đường</option>
                  <option value="busCategory">Theo loại xe</option>
                </select>
                <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                  <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                  </svg>
                </div>
              </div>
            </div>

            <div v-if="priceManager.bulkUpdate.value.applyTo === 'route'">
              <label class="block text-sm font-medium text-gray-700 mb-2">Chọn tuyến đường</label>
              <div class="relative">
                <select v-model="priceManager.bulkUpdate.value.routeId" required class="appearance-none w-full bg-white border border-gray-300 rounded-lg shadow-sm pl-3 pr-10 py-2 text-sm text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
                  <option value="">Chọn tuyến đường</option>
                  <option v-for="route in priceManager.allRoutes.value" :key="route.id" :value="route.id">{{ route.origin }} → {{ route.destination }}</option>
                </select>
                <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                  <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                  </svg>
                </div>
              </div>
            </div>

            <div v-if="priceManager.bulkUpdate.value.applyTo === 'busCategory'">
              <label class="block text-sm font-medium text-gray-700 mb-2">Chọn loại xe</label>
              <div class="relative">
                <select v-model="priceManager.bulkUpdate.value.busCategoryId" required class="appearance-none w-full bg-white border border-gray-300 rounded-lg shadow-sm pl-3 pr-10 py-2 text-sm text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
                  <option value="">Chọn loại xe</option>
                  <option v-for="category in priceManager.allBusCategories.value" :key="category.id" :value="category.id">{{ category.name }}</option>
                </select>
                <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                  <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                  </svg>
                </div>
              </div>
            </div>

            <!-- Preview affected items -->
            <div v-if="priceManager.getBulkUpdateTargets.value.length > 0" class="bg-blue-50 p-4 rounded-md">
              <p class="text-sm text-blue-800 mb-2">
                <strong>{{ priceManager.getBulkUpdateTargets.value.length }}</strong> quy tắc giá sẽ được cập nhật:
              </p>
              <ul class="text-xs text-blue-700 space-y-1 max-h-32 overflow-y-auto">
                <li v-for="price in priceManager.getBulkUpdateTargets.value.slice(0, 10)" :key="price.id">
                  {{ price.route.origin }} → {{ price.route.destination }} - {{ price.busCategory.name }}
                </li>
                <li v-if="priceManager.getBulkUpdateTargets.value.length > 10" class="font-medium">
                  ... và {{ priceManager.getBulkUpdateTargets.value.length - 10 }} quy tắc khác
                </li>
              </ul>
            </div>

            <div class="flex justify-end space-x-3 pt-4">
              <button @click="closeBulkUpdateModal" type="button" class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 border border-gray-300 rounded-md hover:bg-gray-200">
                Hủy
              </button>
              <button type="submit" :disabled="priceManager.loading.value || priceManager.getBulkUpdateTargets.value.length === 0" class="px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-md hover:bg-blue-700 disabled:opacity-50">
                <span v-if="priceManager.loading.value">Đang cập nhật...</span>
                <span v-else>Áp dụng cập nhật</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Success Message -->
    <div v-if="successMessage" class="fixed bottom-4 left-1/2 -translate-x-1/2 bg-green-500 text-white px-4 py-2 rounded-md shadow-lg z-50">
      {{ successMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { usePriceManagement } from '@/composables/usePriceManagement'
// @ts-ignore
import { toast, confirm, handleError } from '@/utils/notifications'
import { PriceStatus } from '@/api/busApi'

console.log('🚀 [PriceManagement] Component loading with new GraphQL client...')

// Composable
const priceManager = usePriceManagement()

// UI State
const showAddModal = ref(false)
const showBulkUpdateModal = ref(false)
const successMessage = ref('')

// Methods
const showSuccessMessage = (message) => {
  successMessage.value = message
  setTimeout(() => {
    successMessage.value = ''
  }, 3000)
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('vi-VN')
}

const getBusCategoryBadgeClass = (category) => {
  // Simple color coding based on category name
  const name = category.name.toLowerCase()
  if (name.includes('vip') || name.includes('luxury')) {
    return 'bg-purple-100 text-purple-800'
  } else if (name.includes('giường') || name.includes('bed')) {
    return 'bg-blue-100 text-blue-800'
  } else {
    return 'bg-gray-100 text-gray-800'
  }
}

const getStatusBadgeClass = (status) => {
  switch (status) {
    case PriceStatus.ACTIVE:
      return 'bg-green-100 text-green-800'
    case PriceStatus.SCHEDULED:
      return 'bg-yellow-100 text-yellow-800'
    case PriceStatus.EXPIRED:
      return 'bg-red-100 text-red-800'
    default:
      return 'bg-gray-100 text-gray-800'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case PriceStatus.ACTIVE:
      return 'Đang hiệu lực'
    case PriceStatus.SCHEDULED:
      return 'Sắp có hiệu lực'
    case PriceStatus.EXPIRED:
      return 'Đã hết hạn'
    default:
      return 'Không xác định'
  }
}

const editPrice = (price) => {
  priceManager.setEditingPrice(price)
  showAddModal.value = true
}

const deletePrice = async (price) => {
  const confirmed = await confirm.delete(
    `quy tắc giá cho "${price.route.origin} → ${price.route.destination} - ${price.busCategory.name}"`,
    {
      details: 'Hành động này sẽ xóa quy tắc giá và có thể ảnh hưởng đến việc tính giá vé.'
    }
  )
  
  if (confirmed) {
    try {
      await priceManager.deletePrice(price.id)
      toast.deleted('quy tắc giá')
    } catch (error) {
      console.error('Error deleting price:', error)
      handleError.api(error, 'xóa quy tắc giá')
    }
  }
}

const savePrice = async () => {
  try {
    if (priceManager.editingPriceId.value) {
      await priceManager.updatePrice()
      toast.updated('quy tắc giá')
    } else {
      await priceManager.createPrice()
      toast.created('quy tắc giá')
    }
    closeModal()
  } catch (error) {
    console.error('Error saving price:', error)
    const action = priceManager.editingPriceId.value ? 'cập nhật' : 'tạo'
    handleError.api(error, `${action} quy tắc giá`)
  }
}

const applyBulkUpdate = async () => {
  try {
    const updatedPrices = await priceManager.applyBulkUpdate()
    toast.success(`Cập nhật hàng loạt thành công ${updatedPrices.length} quy tắc giá!`)
    closeBulkUpdateModal()
  } catch (error) {
    console.error('Error in bulk update:', error)
    handleError.api(error, 'cập nhật hàng loạt')
  }
}

const closeModal = () => {
  showAddModal.value = false
  priceManager.resetForm()
}

const closeBulkUpdateModal = () => {
  showBulkUpdateModal.value = false
  priceManager.resetBulkUpdate()
}

// Lifecycle
onMounted(async () => {
  console.log('🚀 [PriceManagement] Component mounted, initializing...')
  try {
    await priceManager.initialize()
    console.log('✅ [PriceManagement] Initialization completed')
  } catch (error) {
    console.error('❌ [PriceManagement] Initialization failed:', error)
  }
})

onUnmounted(() => {
  console.log('🛑 [PriceManagement] Component unmounting, cleaning up...')
  priceManager.cleanup()
})

console.log('✅ [PriceManagement] Component setup completed!')
</script> 