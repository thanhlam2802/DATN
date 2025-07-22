<template>
  <div class="space-y-6">
    <!-- Header Section -->
    <div class="sm:flex sm:items-center sm:justify-between">
      <div>
        <h3 class="text-lg font-medium leading-6 text-gray-900">Quản lý Chuyến xe</h3>
        <p class="mt-1 text-sm text-gray-500">Quản lý và theo dõi tất cả chuyến xe của nhà xe</p>
      </div>
      <div class="mt-4 sm:mt-0 flex items-center space-x-3">
        <!-- Auto-management controls -->
        <div class="flex items-center space-x-2">
          <button
            @click="tripManager.toggleAutoManager"
            :class="[
              'inline-flex items-center px-3 py-2 text-sm font-medium rounded-lg transition-colors border',
              tripManager.autoManagerEnabled.value 
                ? 'bg-green-50 text-green-700 border-green-200 hover:bg-green-100' 
                : 'bg-gray-50 text-gray-700 border-gray-200 hover:bg-gray-100'
            ]"
            title="Toggle automatic trip status management"
          >
            <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
            {{ tripManager.autoManagerEnabled.value ? '🟢 Auto ON' : '🔴 Auto OFF' }}
          </button>
          
          <button
            @click="handleManualSync"
            :disabled="tripManager.syncLoading.value"
            :class="[
              'inline-flex items-center px-3 py-2 text-sm font-medium rounded-lg transition-colors border',
              tripManager.syncLoading.value 
                ? 'bg-blue-50 text-blue-400 border-blue-200 opacity-70 cursor-not-allowed' 
                : 'bg-blue-50 text-blue-700 border-blue-200 hover:bg-blue-100'
            ]"
            title="Manually trigger auto-management check"
          >
            <template v-if="tripManager.syncLoading.value">
              <svg class="animate-spin -ml-1 mr-2 h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Đang đồng bộ...
            </template>
            <template v-else>
              <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
              </svg>
              Sync
            </template>
          </button>
          
          
        </div>
        
        <button @click="showAddModal = true" class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
          <svg class="-ml-1 mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          Thêm chuyến xe
        </button>
      </div>
    </div>

    <!-- Error Message -->
    <div v-if="tripManager.error.value" class="bg-red-50 border-l-4 border-red-400 p-4 rounded-md shadow-sm">
      <div class="flex">
        <div class="flex-shrink-0">
          <svg class="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
          </svg>
        </div>
        <div class="ml-3">
          <h3 class="text-sm font-medium text-red-800">Có lỗi xảy ra</h3>
          <p class="mt-1 text-sm text-red-700">{{ tripManager.error.value }}</p>
          <button @click="tripManager.clearError" class="mt-2 text-sm text-red-600 hover:text-red-500">
            Đóng thông báo
          </button>
        </div>
      </div>
    </div>

    <!-- Warning về xe bus -->
    <div v-if="!tripManager.loadingBuses.value && tripManager.availableBuses.value.length === 0" class="bg-yellow-50 border-l-4 border-yellow-400 p-4 rounded-md shadow-sm">
      <div class="flex">
        <div class="flex-shrink-0">
          <svg class="h-5 w-5 text-yellow-400" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
          </svg>
        </div>
        <div class="ml-3">
          <h3 class="text-sm font-medium text-yellow-800">Chưa có xe bus</h3>
          <p class="mt-1 text-sm text-yellow-700">
            Bạn chưa có xe bus nào. Vui lòng thêm xe bus trước khi tạo chuyến đi.
          </p>
          <p class="mt-1 text-xs text-yellow-600">
            💡 Gợi ý: Đi đến trang "Quản lý Xe buýt" để thêm xe bus mới.
          </p>
        </div>
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
              <option value="scheduled">Đã lên lịch</option>
              <option value="active">Đang hoạt động</option>
              <option value="delayed">Tạm dừng</option>
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
                <dd class="text-lg font-medium text-gray-900">{{ tripManager.stats.value.totalTrips }}</dd>
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
                <dd class="text-lg font-medium text-gray-900">{{ tripManager.stats.value.activeTrips }}</dd>
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
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3a1 1 0 011-1h6a1 1 0 011 1v4h3a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9a2 2 0 012-2h3z"/>
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Đã lên lịch</dt>
                <dd class="text-lg font-medium text-gray-900">{{ tripManager.stats.value.scheduledTrips }}</dd>
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
                <dd class="text-lg font-medium text-gray-900">{{ tripManager.stats.value.completedTrips }}</dd>
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
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Thời gian dự kiến</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Thời gian thực tế</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Xe</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Trạng thái</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Đã bán</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Hành động</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <!-- Loading State -->
            <tr v-if="tripManager.loading.value">
              <td colspan="8" class="px-6 py-8 text-center text-gray-500">
                <div class="flex items-center justify-center">
                  <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-blue-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Đang tải dữ liệu...
                </div>
              </td>
            </tr>
            
            <!-- Error State -->
            <tr v-else-if="tripManager.error.value">
              <td colspan="8" class="px-6 py-8 text-center text-red-500">
                <div class="flex items-center justify-center">
                  <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                  </svg>
                  {{ tripManager.error.value }}
                </div>
              </td>
            </tr>
            
            <!-- Empty State -->
            <tr v-else-if="filteredTrips.length === 0">
              <td colspan="8" class="px-6 py-8 text-center text-gray-500">
                <div class="flex flex-col items-center">
                  <svg class="w-12 h-12 text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17a2 2 0 11-4 0 2 2 0 014 0zM19 17a2 2 0 11-4 0 2 2 0 014 0z"></path>
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16V6a1 1 0 00-1-1H4a1 1 0 00-1 1v10a1 1 0 001 1h1m8-1a1 1 0 01-1 1H9m4-1V8a1 1 0 011-1h2.586a1 1 0 01.707.293l2.414 2.414a1 1 0 01.293.707V16a1 1 0 01-1 1h-1m-6-1a1 1 0 001 1h1M5 17a2 2 0 104 0M15 17a2 2 0 104 0M9 17h6"></path>
                  </svg>
                  <p>Chưa có chuyến xe nào</p>
                  <button @click="showAddModal = true" class="mt-2 text-blue-600 hover:text-blue-800">
                    Thêm chuyến xe đầu tiên
                  </button>
                </div>
              </td>
            </tr>
            
            <!-- Data Rows -->
            <tr v-else v-for="busSlot in filteredTrips" :key="busSlot.id">
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ busSlot.id }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ getRouteInfo(busSlot) }}</td>
              
              <!-- Scheduled Time -->
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                <div>{{ formatTime(busSlot.departureTime) }} - {{ formatTime(busSlot.arrivalTime) }}</div>
                <div class="text-xs text-gray-400">{{ busSlot.slotDate || 'Hôm nay' }}</div>
              </td>
              
              <!-- Actual Time & Delay Info -->
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <div v-if="busSlot.actualDepartureTime || busSlot.actualArrivalTime" class="space-y-1">
                  <div v-if="busSlot.actualDepartureTime" class="text-gray-700">
                    Đi: {{ getDisplayTime(busSlot.departureTime, busSlot.actualDepartureTime) }}
                  </div>
                  <div v-if="busSlot.actualArrivalTime" class="text-gray-700">
                    Đến: {{ getDisplayTime(busSlot.arrivalTime, busSlot.actualArrivalTime) }}
                  </div>
                  <div v-if="busSlot.delayReason" class="text-xs text-amber-600 flex items-center">
                    <span class="mr-1">{{ getDelayReasonIcon(busSlot.delayReason) }}</span>
                    {{ getDelayReasonText(busSlot.delayReason) }}
                  </div>
                </div>
                <div v-else class="text-xs text-gray-400">
                  Chưa cập nhật
                </div>
              </td>
              
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ getBusInfo(busSlot) }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatusBadgeClass(busSlot.status)" class="inline-flex px-2 py-1 text-xs font-semibold rounded-full">
                  {{ getStatusText(busSlot.status) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ busSlot.totalSeats - busSlot.availableSeats }}/{{ busSlot.totalSeats }}
                <div class="text-xs text-green-600">{{ formatPrice(busSlot.price) }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <div class="flex flex-col space-y-1">
                  <!-- Primary Actions -->
                  <div class="flex space-x-2">
                    <button @click="editTrip(busSlot)" class="text-blue-600 hover:text-blue-900 hover:cursor-pointer">Sửa</button>
                    <button 
                      @click="handleDeleteTrip(busSlot.id)" 
                      :disabled="tripManager.isTripButtonLoading(busSlot.id, 'delete')"
                      class="text-red-600 hover:text-red-900 hover:cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
                    >
                      <span v-if="tripManager.isTripButtonLoading(busSlot.id, 'delete')">
                        <svg class="animate-spin inline-block h-3 w-3 mr-1" fill="none" viewBox="0 0 24 24">
                          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                      </span>
                      <span>Xóa</span>
                    </button>
                    <button @click="openStatusUpdateModal(busSlot)" class="text-purple-600 hover:text-purple-900 hover:cursor-pointer" title="Update thời gian thực tế">
                      🕐
                    </button>
                  </div>
                  
                  <!-- Quick Status Actions -->
                  <div class="flex space-x-1" v-if="busSlot.status === 'SCHEDULED' || busSlot.status === 'IN_PROGRESS'">
                    <button 
                      v-if="busSlot.status === 'SCHEDULED'"
                      @click="handleQuickMarkInProgress(busSlot)" 
                      :disabled="tripManager.isTripButtonLoading(busSlot.id, 'start')"
                      class="px-2 py-1 text-xs bg-green-100 text-green-700 rounded hover:bg-green-200 disabled:opacity-70 disabled:cursor-not-allowed flex items-center"
                      title="Đánh dấu đang chạy"
                    >
                      <span v-if="tripManager.isTripButtonLoading(busSlot.id, 'start')" class="flex items-center">
                        <svg class="animate-spin -ml-1 mr-1 h-3 w-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                        Đang...
                      </span>
                      <span v-else>🚌 Bắt đầu</span>
                    </button>
                    <button 
                      v-if="busSlot.status === 'IN_PROGRESS'"
                      @click="handleQuickMarkCompleted(busSlot)" 
                      :disabled="tripManager.isTripButtonLoading(busSlot.id, 'complete')"
                      class="px-2 py-1 text-xs bg-gray-100 text-gray-700 rounded hover:bg-gray-200 disabled:opacity-70 disabled:cursor-not-allowed flex items-center"
                      title="Đánh dấu hoàn thành"
                    >
                      <span v-if="tripManager.isTripButtonLoading(busSlot.id, 'complete')" class="flex items-center">
                        <svg class="animate-spin -ml-1 mr-1 h-3 w-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                        Đang...
                      </span>
                      <span v-else>✅ Hoàn thành</span>
                    </button>
                  </div>
                  
                  <!-- Progress Indicator for IN_PROGRESS trips -->
                  <div v-if="busSlot.status === 'IN_PROGRESS'" class="w-full bg-gray-200 rounded-full h-1">
                    <div 
                      class="bg-green-500 h-1 rounded-full transition-all duration-300" 
                      :style="{ width: calculateProgress(busSlot) + '%' }"
                    ></div>
                  </div>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Trip Form Modal -->
    <TripFormModal
      :visible="showAddModal"
      :editing-trip="editingTrip"
      :available-buses="tripManager.availableBuses.value"
      :available-routes="tripManager.availableRoutes.value"
      :loading-buses="tripManager.loadingBuses.value"
      :loading-routes="tripManager.loadingRoutes.value"
      :loading="tripManager.loading.value"
      @close="closeModal"
      @save="handleSaveTrip"
    />

    <!-- Manual Status Update Modal -->
    <transition name="modal" appear>
      <div v-if="showStatusUpdateModal" @click="closeStatusUpdateModal" class="fixed inset-0 h-full w-full z-50 flex items-center justify-center bg-black-100 backdrop-blur-sm p-4 ">
        <div @click.stop class="relative w-full max-w-lg bg-white rounded-xl shadow-2xl overflow-hidden">
          
          <!-- Modal Header -->
          <div class="bg-gradient-to-r from-purple-600 to-purple-700 px-6 py-4 text-white">
            <h3 class="text-lg font-semibold">⏰ Cập nhật trạng thái thực tế</h3>
            <p class="text-sm opacity-90">Chuyến: {{ updatingTrip?.id }}</p>
          </div>
          
          <!-- Modal Body -->
          <div class="p-6 space-y-4">
            <!-- Status -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Trạng thái hiện tại</label>
              <select v-model="statusUpdateForm.status" class="w-full border border-gray-300 rounded-lg px-3 py-2">
                <option value="SCHEDULED">📅 Đã lên lịch</option>
                <option value="IN_PROGRESS">🚌 Đang chạy</option>
                <option value="COMPLETED">✅ Hoàn thành</option>
                <option value="CANCELLED">❌ Đã hủy</option>
              </select>
            </div>
            
            <!-- Actual Times -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Giờ khởi hành thực tế</label>
                <input 
                  v-model="statusUpdateForm.actualDepartureTime" 
                  type="datetime-local" 
                  class="w-full border border-gray-300 rounded-lg px-3 py-2"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Giờ đến thực tế</label>
                <input 
                  v-model="statusUpdateForm.actualArrivalTime" 
                  type="datetime-local" 
                  class="w-full border border-gray-300 rounded-lg px-3 py-2"
                />
              </div>
            </div>
            
            <!-- Delay Reason -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Lý do delay/sớm (nếu có)</label>
              <select v-model="statusUpdateForm.delayReason" class="w-full border border-gray-300 rounded-lg px-3 py-2">
                <option value="">-- Không có --</option>
                <option value="TRAFFIC_JAM">🚗 Kẹt xe</option>
                <option value="WEATHER">🌧️ Thời tiết xấu</option>
                <option value="VEHICLE_ISSUE">🔧 Sự cố xe</option>
                <option value="PASSENGER_DELAY">👥 Khách trễ</option>
                <option value="ROAD_ACCIDENT">⚠️ Tai nạn giao thông</option>
                <option value="FUEL_STOP">⛽ Dừng đổ xăng</option>
                <option value="DRIVER_BREAK">☕ Nghỉ giải lao</option>
                <option value="EARLY_ARRIVAL">🏃‍♂️ Đến sớm</option>
                <option value="OTHER">❓ Lý do khác</option>
              </select>
            </div>
            
            <!-- Current Location -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Vị trí hiện tại (optional)</label>
              <input 
                v-model="statusUpdateForm.currentLocation" 
                type="text" 
                placeholder="VD: Đang ở Hà Nội, sẽ đến Thanh Hóa lúc 15:30"
                class="w-full border border-gray-300 rounded-lg px-3 py-2"
              />
            </div>
            
            <!-- Driver Notes -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Ghi chú tài xế</label>
              <textarea 
                v-model="statusUpdateForm.driverNotes" 
                rows="2"
                placeholder="Ghi chú thêm từ tài xế hoặc điều hành..."
                class="w-full border border-gray-300 rounded-lg px-3 py-2"
              ></textarea>
            </div>
          </div>
          
          <!-- Modal Footer -->
          <div class="bg-gray-50 px-6 py-4 flex space-x-3 justify-end">
            <button 
              @click="closeStatusUpdateModal"
              class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50"
            >
              Hủy
            </button>
            <button 
              @click="handleSaveStatusUpdate"
              :disabled="tripManager.loading.value"
              class="px-4 py-2 text-sm font-medium text-white bg-purple-600 border border-transparent rounded-lg hover:bg-purple-700 disabled:opacity-50"
            >
              <span v-if="tripManager.loading.value">Đang lưu...</span>
              <span v-else>💾 Cập nhật</span>
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- Success Message -->
    <div v-if="successMessage" class="fixed bottom-4 left-1/2 -translate-x-1/2 bg-green-500 text-white px-4 py-2 rounded-md shadow-lg z-50">
      {{ successMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useTripManagement } from '@/composables/useTripManagement'
import TripFormModal from './TripFormModal.vue'
import { BusSlotStatus, DelayReason } from '@/api/busApi/busSlot'

// Initialize trip management composable
const tripManager = useTripManagement()

// UI State
const showAddModal = ref(false)
const editingTrip = ref(null)
const showStatusUpdateModal = ref(false)
const updatingTrip = ref(null)
const successMessage = ref('')
const debuggingAuto = ref(false)

// Show success message temporarily
const showSuccessMessage = (message) => {
  successMessage.value = message
  setTimeout(() => {
    successMessage.value = ''
  }, 3000)
}

// Filters
const filters = ref({
  route: '',
  status: '',
  date: ''
})

// Status update form
const statusUpdateForm = ref({
  status: '',
  actualDepartureTime: '',
  actualArrivalTime: '',
  delayReason: '',
  currentLocation: '',
  driverNotes: ''
})

// Computed
const filteredTrips = computed(() => {
  let filtered = tripManager.busSlots.value

  if (filters.value.route) {
    filtered = filtered.filter(slot => 
      slot.route && `${slot.route.origin} - ${slot.route.destination}`.includes(filters.value.route)
    )
  }
  
  if (filters.value.status) {
    const statusMap = {
      'active': BusSlotStatus.IN_PROGRESS,
      'delayed': BusSlotStatus.DELAYED,
      'completed': BusSlotStatus.COMPLETED,
      'scheduled': BusSlotStatus.SCHEDULED
    }
    filtered = filtered.filter(slot => slot.status === statusMap[filters.value.status])
  }
  
  if (filters.value.date) {
    filtered = filtered.filter(slot => slot.slotDate === filters.value.date)
  }

  return filtered
})

// Methods
const applyFilters = () => {
  // Filters are applied automatically through computed property
}

const editTrip = (busSlot) => {
  editingTrip.value = busSlot
  tripManager.setEditingTrip(busSlot) // Set editing trip ID for conflict checking
  showAddModal.value = true
}

const closeModal = () => {
  showAddModal.value = false
  editingTrip.value = null
  tripManager.resetForm()
  tripManager.setEditingTrip(null) // Clear editing trip ID
}

const openStatusUpdateModal = (trip) => {
  updatingTrip.value = trip
  
  // Pre-fill form với data hiện tại
  statusUpdateForm.value = {
    status: trip.status,
    actualDepartureTime: trip.actualDepartureTime || '',
    actualArrivalTime: trip.actualArrivalTime || '',
    delayReason: trip.delayReason || '',
    currentLocation: trip.currentLocation || '',
    driverNotes: ''
  }
  
  showStatusUpdateModal.value = true
}

const closeStatusUpdateModal = () => {
  showStatusUpdateModal.value = false
  updatingTrip.value = null
  statusUpdateForm.value = {
    status: '',
    actualDepartureTime: '',
    actualArrivalTime: '',
    delayReason: '',
    currentLocation: '',
    driverNotes: ''
  }
}

// Event Handlers
const handleSaveTrip = async (tripData) => {
  try {
    if (editingTrip.value) {
      await tripManager.updateTrip(editingTrip.value.id, tripData)
    } else {
      await tripManager.createTrip(tripData)
    }
    closeModal()
  } catch (error) {
    console.error('Error saving trip:', error)
    // Error is handled in composable and displayed in UI
  }
}

const handleDeleteTrip = async (tripId) => {
  if (confirm('Bạn có chắc chắn muốn xóa chuyến xe này?')) {
    try {
      await tripManager.deleteTrip(tripId)
    } catch (error) {
      console.error('Error deleting trip:', error)
    }
  }
}

const handleQuickMarkInProgress = async (trip) => {
  try {
    console.log('🚀 [UI] Starting trip:', trip.id)
    await tripManager.quickMarkInProgress(trip)
    console.log('✅ [UI] Trip started successfully')
    showSuccessMessage('🚌 Chuyến xe đã bắt đầu!')
  } catch (error) {
    console.error('❌ [UI] Error marking trip in progress:', error)
    
    // Show user-friendly error message
    alert(`Không thể bắt đầu chuyến xe: ${error.message || 'Lỗi không xác định'}`)
    
    // Auto-fallback: refresh data after 2 seconds
    setTimeout(async () => {
      console.log('🔄 [UI] Auto-refreshing data due to error...')
      try {
        await tripManager.loadBusSlots()
      } catch (refreshError) {
        console.error('❌ [UI] Error refreshing data:', refreshError)
      }
    }, 2000)
  }
}

const handleQuickMarkCompleted = async (trip) => {
  try {
    console.log('🏁 [UI] Completing trip:', trip.id)
    await tripManager.quickMarkCompleted(trip)
    console.log('✅ [UI] Trip completed successfully')
    showSuccessMessage('✅ Chuyến xe đã hoàn thành!')
  } catch (error) {
    console.error('❌ [UI] Error marking trip completed:', error)
    
    // Show user-friendly error message
    alert(`Không thể hoàn thành chuyến xe: ${error.message || 'Lỗi không xác định'}`)
    
    // Auto-fallback: refresh data after 2 seconds
    setTimeout(async () => {
      console.log('🔄 [UI] Auto-refreshing data due to error...')
      try {
        await tripManager.loadBusSlots()
      } catch (refreshError) {
        console.error('❌ [UI] Error refreshing data:', refreshError)
      }
    }, 2000)
  }
}

const handleSaveStatusUpdate = async () => {
  if (!updatingTrip.value) return
  
  try {
    await tripManager.updateTripStatus(updatingTrip.value.id, statusUpdateForm.value)
    closeStatusUpdateModal()
    showSuccessMessage('Cập nhật trạng thái thực tế thành công!')
  } catch (error) {
    console.error('Error updating trip status:', error)
  }
}

const handleManualSync = async () => {
  try {
    await tripManager.manualTriggerAutoManager()
  } catch (error) {
    console.error('Error in manual sync:', error)
  }
}



// Helper Methods
const getStatusBadgeClass = (status) => {
  switch (status) {
    case BusSlotStatus.IN_PROGRESS:
      return 'bg-green-100 text-green-800'
    case BusSlotStatus.SCHEDULED:
      return 'bg-blue-100 text-blue-800'
    case BusSlotStatus.DELAYED:
      return 'bg-yellow-100 text-yellow-800'
    case BusSlotStatus.COMPLETED:
      return 'bg-gray-100 text-gray-800'
    case BusSlotStatus.CANCELLED:
      return 'bg-red-100 text-red-800'
    case BusSlotStatus.ARCHIVED:
      return 'bg-gray-100 text-gray-500'
    default:
      return 'bg-gray-100 text-gray-800'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case BusSlotStatus.IN_PROGRESS:
      return 'Đang hoạt động'
    case BusSlotStatus.SCHEDULED:
      return 'Đã lên lịch'
    case BusSlotStatus.DELAYED:
      return 'Tạm dừng'
    case BusSlotStatus.COMPLETED:
      return 'Hoàn thành'
    case BusSlotStatus.CANCELLED:
      return 'Đã hủy'
    case BusSlotStatus.ARCHIVED:
      return 'Đã lưu trữ'
    default:
      return 'Không xác định'
  }
}

const getBusInfo = (busSlot) => {
  return busSlot.bus ? `${busSlot.bus.name} (${busSlot.bus.licensePlate})` : 'N/A'
}

const getRouteInfo = (busSlot) => {
  return busSlot.route ? `${busSlot.route.origin} - ${busSlot.route.destination}` : 'N/A'
}

const formatTime = (datetime) => {
  if (!datetime) return 'N/A'
  try {
    if (typeof datetime === 'string' && datetime.match(/^\d{2}:\d{2}:\d{2}$/)) {
      return datetime.substring(0, 5) // HH:MM:SS -> HH:MM
    }
    return new Date(datetime).toLocaleTimeString('vi-VN', { 
      hour: '2-digit', 
      minute: '2-digit' 
    })
  } catch {
    return datetime || 'N/A'
  }
}

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price)
}

const getDisplayTime = (scheduledTime, actualTime) => {
  if (!scheduledTime) return 'N/A'
  if (!actualTime) return formatTime(scheduledTime)
  
  const scheduled = new Date(scheduledTime)
  const actual = new Date(actualTime)
  const diff = Math.round((actual - scheduled) / (60 * 1000)) // diff in minutes
  
  const formattedTime = formatTime(actualTime)
  if (Math.abs(diff) < 5) {
    return `${formattedTime} (đúng giờ)`
  } else if (diff < 0) {
    return `${formattedTime} (sớm ${Math.abs(diff)} phút)`
  } else {
    return `${formattedTime} (trễ ${diff} phút)`
  }
}

const getDelayReasonIcon = (reason) => {
  switch (reason) {
    case DelayReason.TRAFFIC_JAM: return '🚗'
    case DelayReason.WEATHER: return '🌧️'
    case DelayReason.VEHICLE_ISSUE: return '🔧'
    case DelayReason.PASSENGER_DELAY: return '👥'
    case DelayReason.ROAD_ACCIDENT: return '⚠️'
    case DelayReason.FUEL_STOP: return '⛽'
    case DelayReason.DRIVER_BREAK: return '☕'
    case DelayReason.EARLY_ARRIVAL: return '🏃‍♂️'
    case DelayReason.OTHER: return '❓'
    default: return '❓'
  }
}

const getDelayReasonText = (reason) => {
  switch (reason) {
    case DelayReason.TRAFFIC_JAM: return 'Kẹt xe'
    case DelayReason.WEATHER: return 'Thời tiết xấu'
    case DelayReason.VEHICLE_ISSUE: return 'Sự cố xe'
    case DelayReason.PASSENGER_DELAY: return 'Khách trễ'
    case DelayReason.ROAD_ACCIDENT: return 'Tai nạn giao thông'
    case DelayReason.FUEL_STOP: return 'Dừng đổ xăng'
    case DelayReason.DRIVER_BREAK: return 'Nghỉ giải lao'
    case DelayReason.EARLY_ARRIVAL: return 'Đến sớm'
    case DelayReason.OTHER: return 'Lý do khác'
    default: return 'Không xác định'
  }
}

const calculateProgress = (trip) => {
  if (trip.status !== BusSlotStatus.IN_PROGRESS) return 0
  
  const now = new Date()
  const departure = trip.actualDepartureTime ? new Date(trip.actualDepartureTime) : new Date(trip.departureTime)
  const arrival = trip.actualArrivalTime ? new Date(trip.actualArrivalTime) : new Date(trip.arrivalTime)
  
  const totalDuration = arrival.getTime() - departure.getTime()
  const elapsedTime = now.getTime() - departure.getTime()
  
  let progress = (elapsedTime / totalDuration) * 100
  
  // Constrain to 0-100
  progress = Math.max(0, Math.min(100, progress))
  
  return Math.round(progress)
}

// Lifecycle
onMounted(async () => {
  console.log('🚀 [UI] TripManagement component mounted')
  await tripManager.initialize()
  console.log('✅ [UI] TripManager initialized')
  console.log('🔍 [UI] Auto-management enabled:', tripManager.autoManagerEnabled.value)
  console.log('🔍 [UI] Current trips count:', tripManager.busSlots.value.length)
})

onUnmounted(() => {
  console.log('🛑 [UI] TripManagement component unmounting')
  tripManager.cleanup()
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
</style>