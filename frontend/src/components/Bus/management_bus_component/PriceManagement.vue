<template>
  <div class="space-y-6">
    <!-- Header Section -->
    <div class="sm:flex sm:items-center sm:justify-between">
      <div>
        <h3 class="text-lg font-medium leading-6 text-gray-900">Quản lý Giá vé</h3>
        <p class="mt-1 text-sm text-gray-500">Thiết lập và quản lý giá vé cho các tuyến đường</p>
      </div>
      <div class="mt-4 sm:mt-0 flex space-x-3">
        <button @click="showBulkUpdate = true" class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50">
          <svg class="-ml-1 mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
          </svg>
          Cập nhật hàng loạt
        </button>
        <button @click="showAddModal = true" class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700">
          <svg class="-ml-1 mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          Thêm bảng giá
        </button>
      </div>
    </div>

    <!-- Filters -->
    <div class="bg-white shadow rounded-lg p-6">
      <h4 class="text-sm font-medium text-gray-900 mb-4">Lọc bảng giá</h4>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Tuyến đường</label>
          <div class="relative">
            <select v-model="filters.route" class="appearance-none w-full bg-white border border-gray-300 rounded-lg shadow-sm pl-3 pr-10 py-2 text-sm text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
              <option value="">Tất cả tuyến</option>
              <option v-for="route in availableRoutes" :key="route.id" :value="route.id">{{ route.name }}</option>
            </select>
            <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
              <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
              </svg>
            </div>
          </div>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Loại vé</label>
          <div class="relative">
            <select v-model="filters.ticketType" class="appearance-none w-full bg-white border border-gray-300 rounded-lg shadow-sm pl-3 pr-10 py-2 text-sm text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
              <option value="">Tất cả loại</option>
              <option value="regular">Thường</option>
              <option value="vip">VIP</option>
              <option value="sleeper">Giường nằm</option>
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
            <select v-model="filters.status" class="appearance-none w-full bg-white border border-gray-300 rounded-lg shadow-sm pl-3 pr-10 py-2 text-sm text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
              <option value="">Tất cả trạng thái</option>
              <option value="active">Đang áp dụng</option>
              <option value="scheduled">Đã lên lịch</option>
              <option value="expired">Hết hạn</option>
            </select>
            <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
              <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
              </svg>
            </div>
          </div>
        </div>
        <div class="flex items-end">
          <button @click="applyFilters" class="w-full px-4 py-2 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500">
            Áp dụng lọc
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
                <dd class="text-lg font-medium text-gray-900">{{ formatPrice(averagePrice) }}</dd>
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
                <dt class="text-sm font-medium text-gray-500 truncate">Tổng bảng giá</dt>
                <dd class="text-lg font-medium text-gray-900">{{ prices.length }}</dd>
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
                <dt class="text-sm font-medium text-gray-500 truncate">Đang áp dụng</dt>
                <dd class="text-lg font-medium text-gray-900">{{ activePrices }}</dd>
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
                <dt class="text-sm font-medium text-gray-500 truncate">Đã lên lịch</dt>
                <dd class="text-lg font-medium text-gray-900">{{ scheduledPrices }}</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Price Table -->
    <div class="bg-white shadow overflow-hidden sm:rounded-md">
      <div class="px-6 py-4 border-b border-gray-200">
        <h4 class="text-lg font-medium text-gray-900">Bảng giá vé</h4>
      </div>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tuyến đường</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Loại vé</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Giá cơ bản</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Giá khuyến mãi</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Hiệu lực từ</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Hiệu lực đến</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Trạng thái</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Hành động</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="price in filteredPrices" :key="price.id">
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ price.routeName }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                <span :class="getTicketTypeBadgeClass(price.ticketType)" class="inline-flex px-2 py-1 text-xs font-semibold rounded-full">
                  {{ getTicketTypeText(price.ticketType) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatPrice(price.basePrice) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                <span v-if="price.promotionPrice" class="text-red-600 font-medium">{{ formatPrice(price.promotionPrice) }}</span>
                <span v-else class="text-gray-400">-</span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(price.validFrom) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                <span v-if="price.validTo">{{ formatDate(price.validTo) }}</span>
                <span v-else class="text-gray-400">Không giới hạn</span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatusBadgeClass(price.status)" class="inline-flex px-2 py-1 text-xs font-semibold rounded-full">
                  {{ getStatusText(price.status) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <div class="flex space-x-2">
                  <button @click="editPrice(price)" class="text-blue-600 hover:text-blue-900">Sửa</button>
                  <button @click="deletePrice(price.id)" class="text-red-600 hover:text-red-900">Xóa</button>
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
          <h3 class="text-lg font-medium text-gray-900 mb-4">{{ editingPrice ? 'Sửa bảng giá' : 'Thêm bảng giá mới' }}</h3>
          <form @submit.prevent="savePrice" class="space-y-4">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Tuyến đường</label>
                <div class="relative">
                  <select v-model="priceForm.routeId" required class="appearance-none w-full bg-white border border-gray-300 rounded-lg shadow-sm pl-3 pr-10 py-2 text-sm text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
                    <option value="">Chọn tuyến đường</option>
                    <option v-for="route in availableRoutes" :key="route.id" :value="route.id">{{ route.name }}</option>
                  </select>
                  <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                    <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                    </svg>
                  </div>
                </div>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Loại vé</label>
                <div class="relative">
                  <select v-model="priceForm.ticketType" required class="appearance-none w-full bg-white border border-gray-300 rounded-lg shadow-sm pl-3 pr-10 py-2 text-sm text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
                    <option value="">Chọn loại vé</option>
                    <option value="regular">Thường</option>
                    <option value="vip">VIP</option>
                    <option value="sleeper">Giường nằm</option>
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
                <label class="block text-sm font-medium text-gray-700 mb-2">Giá cơ bản (VND)</label>
                <input v-model="priceForm.basePrice" type="number" required class="w-full border-gray-300 rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Giá khuyến mãi (VND)</label>
                <input v-model="priceForm.promotionPrice" type="number" class="w-full border-gray-300 rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500">
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Hiệu lực từ</label>
                <input v-model="priceForm.validFrom" type="date" required class="w-full bg-white border border-gray-300 rounded-lg shadow-sm px-3 py-2 text-sm text-gray-700 placeholder-gray-400 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Hiệu lực đến</label>
                <input v-model="priceForm.validTo" type="date" class="w-full bg-white border border-gray-300 rounded-lg shadow-sm px-3 py-2 text-sm text-gray-700 placeholder-gray-400 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Ghi chú</label>
              <textarea v-model="priceForm.notes" rows="3" class="w-full border-gray-300 rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500"></textarea>
            </div>

            <div class="flex justify-end space-x-3 pt-4">
              <button @click="closeModal" type="button" class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 border border-gray-300 rounded-md hover:bg-gray-200">
                Hủy
              </button>
              <button type="submit" class="px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-md hover:bg-blue-700">
                {{ editingPrice ? 'Cập nhật' : 'Thêm mới' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Bulk Update Modal -->
    <div v-if="showBulkUpdate" class="fixed inset-0 bg-gray-200 bg-opacity-20 backdrop-blur-md overflow-y-auto h-full w-full z-50">
      <div class="relative top-20 mx-auto p-5 border w-11/12 md:w-1/2 shadow-lg rounded-md bg-white">
        <div class="mt-3">
          <h3 class="text-lg font-medium text-gray-900 mb-4">Cập nhật giá hàng loạt</h3>
          <form @submit.prevent="applyBulkUpdate" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Loại cập nhật</label>
              <div class="relative">
                <select v-model="bulkUpdate.type" required class="appearance-none w-full bg-white border border-gray-300 rounded-lg shadow-sm pl-3 pr-10 py-2 text-sm text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
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

            <div v-if="bulkUpdate.type">
              <label class="block text-sm font-medium text-gray-700 mb-2">
                {{ bulkUpdate.type === 'percentage' ? 'Phần trăm thay đổi (%)' : 'Số tiền thay đổi (VND)' }}
              </label>
              <input v-model="bulkUpdate.value" type="number" required class="w-full border-gray-300 rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500">
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Áp dụng cho</label>
              <div class="relative">
                <select v-model="bulkUpdate.applyTo" required class="appearance-none w-full bg-white border border-gray-300 rounded-lg shadow-sm pl-3 pr-10 py-2 text-sm text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
                  <option value="">Chọn phạm vi áp dụng</option>
                  <option value="all">Tất cả bảng giá</option>
                  <option value="route">Theo tuyến đường</option>
                  <option value="ticketType">Theo loại vé</option>
                </select>
                <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                  <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                  </svg>
                </div>
              </div>
            </div>

            <div v-if="bulkUpdate.applyTo === 'route'">
              <label class="block text-sm font-medium text-gray-700 mb-2">Chọn tuyến đường</label>
              <div class="relative">
                <select v-model="bulkUpdate.routeId" required class="appearance-none w-full bg-white border border-gray-300 rounded-lg shadow-sm pl-3 pr-10 py-2 text-sm text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
                  <option value="">Chọn tuyến đường</option>
                  <option v-for="route in availableRoutes" :key="route.id" :value="route.id">{{ route.name }}</option>
                </select>
                <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                  <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                  </svg>
                </div>
              </div>
            </div>

            <div v-if="bulkUpdate.applyTo === 'ticketType'">
              <label class="block text-sm font-medium text-gray-700 mb-2">Chọn loại vé</label>
              <div class="relative">
                <select v-model="bulkUpdate.ticketType" required class="appearance-none w-full bg-white border border-gray-300 rounded-lg shadow-sm pl-3 pr-10 py-2 text-sm text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
                  <option value="">Chọn loại vé</option>
                  <option value="regular">Thường</option>
                  <option value="vip">VIP</option>
                  <option value="sleeper">Giường nằm</option>
                </select>
                <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                  <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                  </svg>
                </div>
              </div>
            </div>

            <div class="flex justify-end space-x-3 pt-4">
              <button @click="showBulkUpdate = false" type="button" class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 border border-gray-300 rounded-md hover:bg-gray-200">
                Hủy
              </button>
              <button type="submit" class="px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-md hover:bg-blue-700">
                Áp dụng cập nhật
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// State
const showAddModal = ref(false)
const showBulkUpdate = ref(false)
const editingPrice = ref(null)

console.log('PriceManagement');

const filters = ref({
  route: '',
  ticketType: '',
  status: ''
})

const priceForm = ref({
  routeId: '',
  ticketType: '',
  basePrice: 0,
  promotionPrice: null,
  validFrom: '',
  validTo: '',
  notes: '',
  status: 'active'
})

const bulkUpdate = ref({
  type: '',
  value: 0,
  applyTo: '',
  routeId: '',
  ticketType: ''
})

const availableRoutes = ref([
  { id: 1, name: 'Hà Nội - TP.HCM' },
  { id: 2, name: 'Hà Nội - Đà Nẵng' },
  { id: 3, name: 'TP.HCM - Đà Lạt' },
  { id: 4, name: 'Hà Nội - Hải Phòng' }
])

const prices = ref([
  {
    id: 1,
    routeId: 1,
    routeName: 'Hà Nội - TP.HCM',
    ticketType: 'regular',
    basePrice: 800000,
    promotionPrice: 720000,
    validFrom: '2024-01-01',
    validTo: '2024-03-31',
    status: 'active',
    notes: 'Giá khuyến mãi Tết'
  },
  {
    id: 2,
    routeId: 1,
    routeName: 'Hà Nội - TP.HCM',
    ticketType: 'vip',
    basePrice: 1200000,
    promotionPrice: null,
    validFrom: '2024-01-01',
    validTo: null,
    status: 'active',
    notes: ''
  },
  {
    id: 3,
    routeId: 2,
    routeName: 'Hà Nội - Đà Nẵng',
    ticketType: 'regular',
    basePrice: 450000,
    promotionPrice: 400000,
    validFrom: '2024-02-01',
    validTo: '2024-02-29',
    status: 'scheduled',
    notes: 'Giá khuyến mãi tháng 2'
  },
  {
    id: 4,
    routeId: 3,
    routeName: 'TP.HCM - Đà Lạt',
    ticketType: 'sleeper',
    basePrice: 300000,
    promotionPrice: null,
    validFrom: '2024-01-01',
    validTo: '2023-12-31',
    status: 'expired',
    notes: ''
  }
])

// Computed
const filteredPrices = computed(() => {
  let filtered = prices.value

  if (filters.value.route) {
    filtered = filtered.filter(price => price.routeId === parseInt(filters.value.route))
  }
  
  if (filters.value.ticketType) {
    filtered = filtered.filter(price => price.ticketType === filters.value.ticketType)
  }
  
  if (filters.value.status) {
    filtered = filtered.filter(price => price.status === filters.value.status)
  }

  return filtered
})

const averagePrice = computed(() => {
  const total = prices.value.reduce((sum, price) => sum + price.basePrice, 0)
  return Math.round(total / prices.value.length)
})

const activePrices = computed(() => {
  return prices.value.filter(price => price.status === 'active').length
})

const scheduledPrices = computed(() => {
  return prices.value.filter(price => price.status === 'scheduled').length
})

// Methods
const formatPrice = (price) => {
  return price.toLocaleString() + 'đ'
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('vi-VN')
}

const getTicketTypeBadgeClass = (type) => {
  switch (type) {
    case 'regular':
      return 'bg-gray-100 text-gray-800'
    case 'vip':
      return 'bg-purple-100 text-purple-800'
    case 'sleeper':
      return 'bg-blue-100 text-blue-800'
    default:
      return 'bg-gray-100 text-gray-800'
  }
}

const getTicketTypeText = (type) => {
  switch (type) {
    case 'regular':
      return 'Thường'
    case 'vip':
      return 'VIP'
    case 'sleeper':
      return 'Giường nằm'
    default:
      return 'Không xác định'
  }
}

const getStatusBadgeClass = (status) => {
  switch (status) {
    case 'active':
      return 'bg-green-100 text-green-800'
    case 'scheduled':
      return 'bg-yellow-100 text-yellow-800'
    case 'expired':
      return 'bg-red-100 text-red-800'
    default:
      return 'bg-gray-100 text-gray-800'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'active':
      return 'Đang áp dụng'
    case 'scheduled':
      return 'Đã lên lịch'
    case 'expired':
      return 'Hết hạn'
    default:
      return 'Không xác định'
  }
}

const applyFilters = () => {
  // Filters are applied automatically through computed property
  console.log('Applying filters:', filters.value)
}

const editPrice = (price) => {
  editingPrice.value = price
  priceForm.value = { ...price }
  showAddModal.value = true
}

const deletePrice = (priceId) => {
  if (confirm('Bạn có chắc chắn muốn xóa bảng giá này?')) {
    prices.value = prices.value.filter(price => price.id !== priceId)
  }
}

const savePrice = () => {
  const routeName = availableRoutes.value.find(route => route.id === parseInt(priceForm.value.routeId))?.name || ''
  
  if (editingPrice.value) {
    // Update existing price
    const index = prices.value.findIndex(price => price.id === editingPrice.value.id)
    if (index !== -1) {
      prices.value[index] = { ...priceForm.value, routeName }
    }
  } else {
    // Add new price
    const newPrice = {
      ...priceForm.value,
      id: Date.now(),
      routeName
    }
    prices.value.push(newPrice)
  }
  closeModal()
}

const applyBulkUpdate = () => {
  let targetPrices = prices.value

  // Filter based on applyTo criteria
  if (bulkUpdate.value.applyTo === 'route') {
    targetPrices = prices.value.filter(price => price.routeId === parseInt(bulkUpdate.value.routeId))
  } else if (bulkUpdate.value.applyTo === 'ticketType') {
    targetPrices = prices.value.filter(price => price.ticketType === bulkUpdate.value.ticketType)
  }

  // Apply update
  targetPrices.forEach(price => {
    if (bulkUpdate.value.type === 'percentage') {
      price.basePrice = Math.round(price.basePrice * (1 + bulkUpdate.value.value / 100))
    } else if (bulkUpdate.value.type === 'fixed') {
      price.basePrice = price.basePrice + parseInt(bulkUpdate.value.value)
    }
  })

  showBulkUpdate.value = false
  bulkUpdate.value = {
    type: '',
    value: 0,
    applyTo: '',
    routeId: '',
    ticketType: ''
  }
}

const closeModal = () => {
  showAddModal.value = false
  editingPrice.value = null
  priceForm.value = {
    routeId: '',
    ticketType: '',
    basePrice: 0,
    promotionPrice: null,
    validFrom: '',
    validTo: '',
    notes: '',
    status: 'active'
  }
}
</script> 