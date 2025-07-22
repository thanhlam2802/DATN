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
    <div v-if="isLoading" class="flex justify-center items-center py-20">
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
    <div v-if="!isLoading && !error" class="mb-6">
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

    <!-- Card View -->
    <div v-if="!isLoading && !error && viewMode === 'card'" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div 
        v-for="bus in filteredBuses" 
        :key="bus.id"
        class="bg-white rounded-xl shadow-sm hover:shadow-lg transition-all duration-300 overflow-hidden group cursor-pointer"
        @click="openDetailModal(bus)"
      >
        <!-- Image Section -->
        <div class="relative h-48 overflow-hidden">
          <img 
            :src="(bus.busImages?.[0] as any)?.image?.url || 'https://via.placeholder.com/400x200?text=Không+có+ảnh'" 
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
              @click.stop="openDeleteConfirm(bus.id)"
              class="bg-red-600 hover:bg-red-700 text-white text-sm font-medium py-2 px-3 rounded-lg transition-colors duration-200"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Table View -->
    <div v-if="!isLoading && !error && viewMode === 'table'" class="bg-white rounded-xl shadow-sm overflow-hidden">
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
                      :src="(bus.busImages?.[0] as any)?.image?.url || 'https://via.placeholder.com/80x60?text=No+Image'" 
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
                    @click.stop="openDeleteConfirm(bus.id)" 
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
    <div v-if="!isLoading && !error && filteredBuses.length === 0" class="text-center py-20">
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
    <div v-if="isDetailModalOpen" class="fixed inset-0 z-50 overflow-y-auto" aria-labelledby="modal-title" role="dialog" aria-modal="true">
      <!-- Background overlay -->
      <div class="fixed inset-0 bg-black-100 backdrop-blur-sm" @click="closeDetailModal"></div>
      
      <div class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">        
        <span class="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">&#8203;</span>
        
        <div class="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-4xl sm:w-full relative z-10" @click.stop>
          <div class="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
            <!-- Modal Header -->
            <div class="flex items-center justify-between mb-6">
              <h3 class="text-lg leading-6 font-medium text-gray-900" id="modal-title">
                Chi tiết xe buýt: {{ selectedBus?.name }}
              </h3>
              <button 
                @click.stop="closeDetailModal"
                class="rounded-md text-gray-400 hover:text-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
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
                      :src="(selectedBus.busImages[currentImageIndex] as any)?.image?.url || 'https://via.placeholder.com/400x300'" 
                      :alt="`Ảnh xe ${selectedBus.name}`"
                      class="w-full h-64 object-cover rounded-lg"
                    />
                    <div v-if="selectedBus.busImages.length > 1" class="absolute inset-0 flex items-center justify-between p-4">
                      <button 
                        @click.stop="previousImage"
                        class="p-2 rounded-full bg-black bg-opacity-50 text-white hover:bg-opacity-70 transition-all"
                      >
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
                        </svg>
                      </button>
                      <button 
                        @click.stop="nextImage"
                        class="p-2 rounded-full bg-black bg-opacity-50 text-white hover:bg-opacity-70 transition-all"
                      >
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
                        </svg>
                      </button>
                    </div>
                    <div v-if="selectedBus.busImages.length > 1" class="absolute bottom-4 left-1/2 transform -translate-x-1/2">
                      <span class="px-3 py-1 rounded-full bg-black bg-opacity-50 text-white text-sm">
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
                        'relative overflow-hidden rounded-lg border-2 transition-all',
                        currentImageIndex === index ? 'border-blue-500' : 'border-gray-200 hover:border-gray-300'
                      ]"
                    >
                      <img 
                        :src="(image as any)?.image?.url || 'https://via.placeholder.com/100'" 
                        :alt="`Thumbnail ${index + 1}`"
                        class="w-full h-16 object-cover"
                      />
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
          <div class="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
            <button 
              @click.stop="openCreationModal(selectedBus)"
              type="button" 
              class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-blue-600 text-base font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:ml-3 sm:w-auto sm:text-sm transition-colors duration-200"
            >
              Chỉnh sửa
            </button>
            <button 
              @click.stop="openDeleteConfirm(selectedBus!.id)"
              type="button" 
              class="mt-3 w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-red-600 text-base font-medium text-white hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm transition-colors duration-200"
            >
              Xóa
            </button>
            <button 
              @click.stop="closeDetailModal"
              type="button" 
              class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:mt-0 sm:w-auto sm:text-sm transition-colors duration-200"
            >
              Đóng
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modals -->
    <BusCreationModal ref="creationModal" @bus-created="handleModalSubmit" @bus-updated="handleModalSubmit" />
    <ConfirmDialog
      v-if="isConfirmDialogOpen"
      :message="'Bạn có chắc chắn muốn xóa xe buýt này không? Hành động này không thể hoàn tác.'"
      @confirm="handleDelete"
      @cancel="isConfirmDialogOpen = false; busToDelete = null"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { BusAPI } from '@/api/busApi/bus/api';
import type { Bus } from '@/api/busApi/types/common.types';
import BusCreationModal from './BusCreationModal.vue';
import ConfirmDialog from '@/components/ConfirmDialog.vue';

const buses = ref<Bus[]>([]);
const isLoading = ref(true);
const error = ref<string | null>(null);
const creationModal = ref<{ openModal: (bus?: Bus | null) => void } | null>(null);
const busToDelete = ref<string | null>(null);
const isConfirmDialogOpen = ref(false);

// New reactive variables for enhanced UI
const viewMode = ref<'card' | 'table'>('card');
const searchQuery = ref('');
const isDetailModalOpen = ref(false);
const selectedBus = ref<Bus | null>(null);
const currentImageIndex = ref(0);

const filteredBuses = computed(() => {
  if (!searchQuery.value) return buses.value;
  
  const query = searchQuery.value.toLowerCase();
  return buses.value.filter(bus => 
    bus.name.toLowerCase().includes(query) ||
    bus.licensePlate.toLowerCase().includes(query) ||
    getBusCategoryName(bus).toLowerCase().includes(query)
  );
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

const loadBuses = async () => {
  isLoading.value = true;
  error.value = null;
  try {
    // Thay đổi duy nhất ở đây: Gọi hàm mới với ownerId cố định
    const ownerId = "11";
    const result = await BusAPI.getBusesByOwnerId(ownerId);
    buses.value = result;
    
    
  } catch (err) {
    error.value = "Không thể tải danh sách xe buýt.";  } finally {
    isLoading.value = false;
  }
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

const handleModalSubmit = () => {
  loadBuses();
};

const openDeleteConfirm = (id: string) => {
  busToDelete.value = id;
  isConfirmDialogOpen.value = true;
  // Đóng detail modal nếu đang mở
  if (isDetailModalOpen.value) {
    closeDetailModal();
  }
};

const handleDelete = async () => {
  if (!busToDelete.value) return;
  try {
    await BusAPI.deleteBus(busToDelete.value);
    loadBuses();
  } catch (err) {
    error.value = "Xóa xe buýt thất bại.";
  } finally {
    isConfirmDialogOpen.value = false;
    busToDelete.value = null;
  }
};

onMounted(loadBuses);
</script> 