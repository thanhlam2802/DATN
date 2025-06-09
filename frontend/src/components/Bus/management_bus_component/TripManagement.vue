<template>
  <div class="space-y-6">
    <!-- Header Section -->
    <div class="sm:flex sm:items-center sm:justify-between">
      <div>
        <h3 class="text-lg font-medium leading-6 text-gray-900">Quản lý Chuyến xe</h3>
        <p class="mt-1 text-sm text-gray-500">Quản lý và theo dõi tất cả chuyến xe của nhà xe</p>
      </div>
      <div class="mt-4 sm:mt-0">
        <button @click="showAddModal = true" class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
          <svg class="-ml-1 mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          Thêm chuyến xe
        </button>
      </div>
    </div>

    <!-- Filters Section -->
    <div class="bg-white shadow rounded-lg p-6">
      <h4 class="text-sm font-medium text-gray-900 mb-4">Lọc chuyến xe</h4>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Tuyến đường</label>
          <div class="relative">
            <select v-model="filters.route" class="appearance-none w-full bg-white border border-gray-300 rounded-lg shadow-sm pl-3 pr-10 py-2 text-sm text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
              <option value="">Tất cả tuyến</option>
              <option value="hanoi-hochiminh">Hà Nội - TP.HCM</option>
              <option value="hanoi-danang">Hà Nội - Đà Nẵng</option>
              <option value="hochiminh-danang">TP.HCM - Đà Nẵng</option>
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
              <option value="active">Đang hoạt động</option>
              <option value="inactive">Tạm dừng</option>
              <option value="completed">Hoàn thành</option>
            </select>
            <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
              <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
              </svg>
            </div>
          </div>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Ngày khởi hành</label>
          <input v-model="filters.date" type="date" class="w-full bg-white border border-gray-300 rounded-lg shadow-sm px-3 py-2 text-sm text-gray-700 placeholder-gray-400 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200">
        </div>
        <div class="flex items-end">
          <button @click="applyFilters" class="w-full px-4 py-2 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500">
            Áp dụng lọc
          </button>
        </div>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-blue-500 rounded-md flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h5v-4l-2-2h-3v6z"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Tổng chuyến xe</dt>
                <dd class="text-lg font-medium text-gray-900">{{ stats.totalTrips }}</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>
      
      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-green-500 rounded-md flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Đang hoạt động</dt>
                <dd class="text-lg font-medium text-gray-900">{{ stats.activeTrips }}</dd>
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
                <dt class="text-sm font-medium text-gray-500 truncate">Tạm dừng</dt>
                <dd class="text-lg font-medium text-gray-900">{{ stats.inactiveTrips }}</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-gray-500 rounded-md flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2z"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Hoàn thành</dt>
                <dd class="text-lg font-medium text-gray-900">{{ stats.completedTrips }}</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Trips Table -->
    <div class="bg-white shadow overflow-hidden sm:rounded-md">
      <div class="px-6 py-4 border-b border-gray-200">
        <h4 class="text-lg font-medium text-gray-900">Danh sách chuyến xe</h4>
      </div>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Mã chuyến</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tuyến đường</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Thời gian</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Xe</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Trạng thái</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Đã bán</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Hành động</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="trip in filteredTrips" :key="trip.id">
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ trip.code }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ trip.route }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                <div>{{ trip.departureTime }}</div>
                <div class="text-xs text-gray-400">{{ trip.date }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ trip.busNumber }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatusBadgeClass(trip.status)" class="inline-flex px-2 py-1 text-xs font-semibold rounded-full">
                  {{ getStatusText(trip.status) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ trip.soldSeats }}/{{ trip.totalSeats }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <div class="flex space-x-2">
                  <button @click="editTrip(trip)" class="text-blue-600 hover:text-blue-900">Sửa</button>
                  <button @click="deleteTrip(trip.id)" class="text-red-600 hover:text-red-900">Xóa</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Add/Edit Modal with Beautiful Design -->
    <transition name="modal" appear>
                             <div v-if="showAddModal" @click="handleBackdropClick" class="fixed inset-0 h-full w-full z-50 flex items-center justify-center bg-black-100 bg-opacity-20 backdrop-blur-sm p-4">
        <div @click.stop class="relative w-full max-w-2xl bg-white rounded-xl shadow-2xl overflow-hidden transform">
          
          <!-- Modal Header -->
          <div class="bg-white px-6 py-4 border-b border-gray-200">
            <div class="flex items-center justify-between">
              <div class="flex items-center space-x-3">
                <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
                  <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3a1 1 0 011-1h6a1 1 0 011 1v4h3a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9a2 2 0 012-2h3z"/>
                  </svg>
                </div>
                <div>
                  <h3 class="text-xl font-semibold text-gray-900">{{ editingTrip ? 'Sửa chuyến xe' : 'Thêm chuyến xe mới' }}</h3>
                  <p class="text-gray-600 text-sm">{{ editingTrip ? 'Cập nhật thông tin chuyến xe' : 'Tạo chuyến xe cho tuyến đường' }}</p>
                </div>
              </div>
              <button @click="closeModal" class="text-gray-400 hover:text-gray-600 focus:outline-none transition-colors duration-200">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                </svg>
              </button>
            </div>
          </div>

          <!-- Modal Content -->
          <div class="px-6 py-6">
            <form @submit.prevent="saveTrip" class="space-y-6">
              
              <!-- Trip Information Section -->
              <div class="space-y-4">
                <div class="flex items-center space-x-2 mb-4">
                  <div class="w-2 h-2 bg-blue-600 rounded-full"></div>
                  <h4 class="text-lg font-medium text-gray-900">Thông tin chuyến xe</h4>
                </div>
                
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div class="space-y-2">
                    <label class="block text-sm font-medium text-gray-700">
                      Mã chuyến <span class="text-red-500">*</span>
                    </label>
                    <div class="relative">
                      <input 
                        v-model="tripForm.code" 
                        type="text" 
                        required 
                        placeholder="VD: BUS001"
                        class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 hover:border-gray-400"
                      >
                      <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 20l4-16m2 16l4-16M6 9h14M4 15h14"/>
                        </svg>
                      </div>
                    </div>
                  </div>
                  
                  <div class="space-y-2">
                    <label class="block text-sm font-medium text-gray-700">
                      Tuyến đường <span class="text-red-500">*</span>
                    </label>
                    <div class="relative">
                      <select 
                        v-model="tripForm.route" 
                        required 
                        class="appearance-none w-full pl-10 pr-10 py-3 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 hover:border-gray-400 bg-white"
                      >
                        <option value="">Chọn tuyến đường</option>
                        <option value="Hà Nội - TP.HCM">Hà Nội - TP.HCM</option>
                        <option value="Hà Nội - Đà Nẵng">Hà Nội - Đà Nẵng</option>
                        <option value="TP.HCM - Đà Nẵng">TP.HCM - Đà Nẵng</option>
                      </select>
                      <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
                        </svg>
                      </div>
                      <div class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none">
                        <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                        </svg>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div class="space-y-2">
                    <label class="block text-sm font-medium text-gray-700">
                      Ngày khởi hành <span class="text-red-500">*</span>
                    </label>
                    <div class="relative">
                      <input 
                        v-model="tripForm.date" 
                        type="date" 
                        required 
                        class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 hover:border-gray-400"
                      >
                      <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3a1 1 0 011-1h6a1 1 0 011 1v4h3a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9a2 2 0 012-2h3z"/>
                        </svg>
                      </div>
                    </div>
                  </div>
                  
                  <div class="space-y-2">
                    <label class="block text-sm font-medium text-gray-700">
                      Giờ khởi hành <span class="text-red-500">*</span>
                    </label>
                    <div class="relative">
                      <input 
                        v-model="tripForm.departureTime" 
                        type="time" 
                        required 
                        class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 hover:border-gray-400"
                      >
                      <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                        </svg>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div class="space-y-2">
                    <label class="block text-sm font-medium text-gray-700">
                      Số xe <span class="text-red-500">*</span>
                    </label>
                    <div class="relative">
                      <input 
                        v-model="tripForm.busNumber" 
                        type="text" 
                        required 
                        placeholder="VD: BKS-001"
                        class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 hover:border-gray-400"
                      >
                      <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 14v3m4-3v3m4-3v3M3 21h18M3 10h18M3 7l9-4 9 4M4 10.5V21"/>
                        </svg>
                      </div>
                    </div>
                  </div>
                  
                  <div class="space-y-2">
                    <label class="block text-sm font-medium text-gray-700">
                      Số ghế <span class="text-red-500">*</span>
                    </label>
                    <div class="relative">
                      <input 
                        v-model="tripForm.totalSeats" 
                        type="number" 
                        required 
                        min="1"
                        placeholder="40"
                        class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 hover:border-gray-400"
                      >
                      <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"/>
                        </svg>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </form>
          </div>

          <!-- Modal Footer -->
          <div class="bg-gray-50 px-6 py-4 border-t border-gray-200 flex items-center justify-between">
            <div class="text-sm text-gray-500">
              <span class="flex items-center">
                <svg class="w-4 h-4 mr-1 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h5v-4l-2-2h-3v6z"/>
                </svg>
                Quản lý chuyến xe
              </span>
            </div>
            <div class="flex space-x-3">
              <button 
                @click="closeModal" 
                type="button" 
                class="px-6 py-2.5 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2 transition-all duration-200 shadow-sm"
              >
                Hủy bỏ
              </button>
              <button 
                @click="saveTrip" 
                type="submit" 
                class="px-6 py-2.5 text-sm font-medium text-white bg-gradient-to-r from-blue-600 to-blue-700 border border-transparent rounded-lg hover:from-blue-700 hover:to-blue-800 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-all duration-200 shadow-md hover:shadow-lg transform hover:scale-105"
              >
                {{ editingTrip ? 'Cập nhật chuyến' : 'Tạo chuyến xe' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

// State
const showAddModal = ref(false)
const editingTrip = ref(null)
const filters = ref({
  route: '',
  status: '',
  date: ''
})

const tripForm = ref({
  code: '',
  route: '',
  date: '',
  departureTime: '',
  busNumber: '',
  totalSeats: 40,
  soldSeats: 0,
  status: 'active'
})

const trips = ref([
  {
    id: 1,
    code: 'BUS001',
    route: 'Hà Nội - TP.HCM',
    date: '2024-01-15',
    departureTime: '08:00',
    busNumber: 'BKS-001',
    totalSeats: 40,
    soldSeats: 25,
    status: 'active'
  },
  {
    id: 2,
    code: 'BUS002',
    route: 'Hà Nội - Đà Nẵng',
    date: '2024-01-15',
    departureTime: '14:30',
    busNumber: 'BKS-002',
    totalSeats: 40,
    soldSeats: 32,
    status: 'active'
  },
  {
    id: 3,
    code: 'BUS003',
    route: 'TP.HCM - Đà Nẵng',
    date: '2024-01-14',
    departureTime: '20:00',
    busNumber: 'BKS-003',
    totalSeats: 40,
    soldSeats: 40,
    status: 'completed'
  }
])

// Computed
const stats = computed(() => ({
  totalTrips: trips.value.length,
  activeTrips: trips.value.filter(trip => trip.status === 'active').length,
  inactiveTrips: trips.value.filter(trip => trip.status === 'inactive').length,
  completedTrips: trips.value.filter(trip => trip.status === 'completed').length
}))

const filteredTrips = computed(() => {
  let filtered = trips.value

  if (filters.value.route) {
    filtered = filtered.filter(trip => trip.route.includes(filters.value.route))
  }
  
  if (filters.value.status) {
    filtered = filtered.filter(trip => trip.status === filters.value.status)
  }
  
  if (filters.value.date) {
    filtered = filtered.filter(trip => trip.date === filters.value.date)
  }

  return filtered
})

// Methods
const getStatusBadgeClass = (status) => {
  switch (status) {
    case 'active':
      return 'bg-green-100 text-green-800'
    case 'inactive':
      return 'bg-yellow-100 text-yellow-800'
    case 'completed':
      return 'bg-gray-100 text-gray-800'
    default:
      return 'bg-gray-100 text-gray-800'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'active':
      return 'Đang hoạt động'
    case 'inactive':
      return 'Tạm dừng'
    case 'completed':
      return 'Hoàn thành'
    default:
      return 'Không xác định'
  }
}

const applyFilters = () => {
  // Filters are applied automatically through computed property
  console.log('Applying filters:', filters.value)
}

const editTrip = (trip) => {
  editingTrip.value = trip
  tripForm.value = { ...trip }
  showAddModal.value = true
}

const deleteTrip = (tripId) => {
  if (confirm('Bạn có chắc chắn muốn xóa chuyến xe này?')) {
    trips.value = trips.value.filter(trip => trip.id !== tripId)
  }
}

const saveTrip = () => {
  if (editingTrip.value) {
    // Update existing trip
    const index = trips.value.findIndex(trip => trip.id === editingTrip.value.id)
    if (index !== -1) {
      trips.value[index] = { ...tripForm.value }
    }
  } else {
    // Add new trip
    const newTrip = {
      ...tripForm.value,
      id: Date.now(),
      soldSeats: 0
    }
    trips.value.push(newTrip)
  }
  closeModal()
}

console.log('TripManagement');

const handleBackdropClick = () => {
  closeModal()
}

const closeModal = () => {
  showAddModal.value = false
  editingTrip.value = null
  tripForm.value = {
    code: '',
    route: '',
    date: '',
    departureTime: '',
    busNumber: '',
    totalSeats: 40,
    soldSeats: 0,
    status: 'active'
  }
}

onMounted(() => {
  // Initialize component
})
</script>

<style scoped>
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

/* Modal Content Animation */
.modal-enter-active .transform {
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.modal-leave-active .transform {
  transition: all 0.2s ease-in-out;
}

.modal-enter-from .transform {
  opacity: 0;
  transform: scale(0.95) translateY(-20px);
}

.modal-enter-to .transform {
  opacity: 1;
  transform: scale(1) translateY(0);
}

.modal-leave-from .transform {
  opacity: 1;
  transform: scale(1) translateY(0);
}

.modal-leave-to .transform {
  opacity: 0;
  transform: scale(0.95) translateY(-10px);
}

/* Input hover effects */
.group:hover input {
  border-color: #9ca3af;
}

/* Button animations */
.transition-all:hover {
  transform: translateY(-1px);
}

/* Gradient animation */
.bg-gradient-to-r:hover {
  background-size: 200% 200%;
  animation: gradient-shift 0.3s ease;
}

@keyframes gradient-shift {
  0% { background-position: 0% 50%; }
  100% { background-position: 100% 50%; }
}

/* Loading animation for inputs */
.focus\:ring-2:focus {
  animation: ring-pulse 0.3s ease-out;
}

@keyframes ring-pulse {
  0% { box-shadow: 0 0 0 0 rgba(59, 130, 246, 0.5); }
  100% { box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1); }
}
</style> 