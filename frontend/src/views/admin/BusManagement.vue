<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <!-- Header + Global Actions -->
    <header class="mb-8">
      <div class="flex items-start justify-between gap-4">
        <div>
          <h1 class="text-3xl font-bold text-gray-900 mb-2">Super Admin · Bus Management</h1>
          <p class="text-gray-600">Quản trị xuyên tất cả nhà xe (providers) · Chuẩn hoá danh mục · Duyệt dữ liệu</p>
        </div>
        <div class="flex gap-3">
          <button 
            @click="refreshAll"
            class="px-4 py-2 bg-white border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
          >
            <svg class="w-4 h-4 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
            </svg>
            Refresh
          </button>
          <button 
            @click="openImportWizard"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            <svg class="w-4 h-4 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"></path>
            </svg>
            Import (CSV/XLSX)
          </button>
          <button 
            @click="exportCurrent"
            class="px-4 py-2 bg-white border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
          >
            <svg class="w-4 h-4 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
            </svg>
            Export
          </button>
        </div>
      </div>
    </header>

    <!-- Tabs -->
    <nav class="mb-8 border-b border-gray-200">
      <div class="flex flex-wrap gap-1">
        <button
          v-for="tab in tabs"
          :key="tab.id"
          @click="activeTab = tab.id"
          :class="[
            'px-4 py-2 text-sm font-medium rounded-t-lg transition-colors',
            activeTab === tab.id 
              ? 'bg-white text-blue-600 border-b-2 border-blue-600' 
              : 'text-gray-500 hover:text-gray-700 hover:bg-gray-100'
          ]"
        >
          {{ tab.label }}
        </button>
      </div>
    </nav>

    <!-- OVERVIEW (Dashboard + Alerts) -->
    <section v-if="activeTab === 'overview'" class="space-y-6">
      <!-- KPI Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div class="flex items-center">
            <div class="p-2 bg-blue-100 rounded-lg">
              <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Bookings hôm nay</p>
              <p class="text-2xl font-bold text-gray-900">{{ kpis.bookingsToday ?? '-' }}</p>
            </div>
          </div>
        </div>
        
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div class="flex items-center">
            <div class="p-2 bg-green-100 rounded-lg">
              <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path>
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Tỉ lệ lấp đầy</p>
              <p class="text-2xl font-bold text-gray-900">{{ kpis.occupancyRate ?? '-' }}%</p>
            </div>
          </div>
        </div>
        
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div class="flex items-center">
            <div class="p-2 bg-red-100 rounded-lg">
              <svg class="w-6 h-6 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Huỷ/Hoãn</p>
              <p class="text-2xl font-bold text-gray-900">{{ kpis.cancellations ?? '-' }}</p>
            </div>
          </div>
        </div>
        
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div class="flex items-center">
            <div class="p-2 bg-purple-100 rounded-lg">
              <svg class="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path>
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Providers hoạt động</p>
              <p class="text-2xl font-bold text-gray-900">{{ kpis.activeProviders ?? '-' }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Alerts Table -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900">Alerts chất lượng dữ liệu</h3>
        </div>
        <div class="p-6">
          <div class="flex items-center gap-3 mb-4">
            <input 
              v-model="overviewFilters.query" 
              class="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Tìm theo từ khoá..." 
            />
            <button 
              @click="loadOverview"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              Lọc
            </button>
          </div>
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Loại</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Provider</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Mô tả</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Khuyến nghị</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Thời điểm</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="(row, idx) in overviewAlerts" :key="idx" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ row.type }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ row.ownerName }}</td>
                  <td class="px-6 py-4 text-sm text-gray-900">{{ row.description }}</td>
                  <td class="px-6 py-4 text-sm text-gray-900">{{ row.suggestion }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ row.at }}</td>
                </tr>
                <tr v-if="overviewAlerts.length === 0">
                  <td colspan="5" class="px-6 py-4 text-center text-sm text-gray-500">Không có dữ liệu</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </section>

    <!-- DICTIONARIES (Categories, Seat Maps, Route Catalog) -->
    <section v-if="activeTab === 'dictionaries'" class="space-y-6">
      <!-- Categories & Seat Maps -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Categories -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-200">
          <div class="px-6 py-4 border-b border-gray-200">
            <h3 class="text-lg font-semibold text-gray-900">Bus Categories</h3>
          </div>
          <div class="p-6">
            <div class="flex gap-3 mb-4">
              <input 
                v-model="dictFilters.categoryQuery" 
                class="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="Tìm..." 
              />
              <button 
                @click="loadCategories"
                class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
              >
                Lọc
              </button>
              <button 
                @click="openCategoryModal()"
                class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                <svg class="w-4 h-4 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
                </svg>
                Thêm
              </button>
            </div>
            <div class="overflow-x-auto">
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Code</th>
                    <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tên</th>
                    <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Mô tả</th>
                    <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr v-for="c in categories" :key="c.code" class="hover:bg-gray-50">
                    <td class="px-4 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ c.code }}</td>
                    <td class="px-4 py-4 whitespace-nowrap text-sm text-gray-900">{{ c.name }}</td>
                    <td class="px-4 py-4 text-sm text-gray-900">{{ c.description }}</td>
                    <td class="px-4 py-4 whitespace-nowrap text-sm font-medium">
                      <div class="flex gap-2">
                        <button 
                          @click="openCategoryModal(c)"
                          class="text-blue-600 hover:text-blue-900"
                        >
                          Sửa
                        </button>
                        <button 
                          @click="removeCategory(c)"
                          class="text-red-600 hover:text-red-900"
                        >
                          Xoá
                        </button>
                      </div>
                    </td>
                  </tr>
                  <tr v-if="categories.length===0">
                    <td colspan="4" class="px-4 py-4 text-center text-sm text-gray-500">Trống</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <!-- Seat Maps -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-200">
          <div class="px-6 py-4 border-b border-gray-200">
            <h3 class="text-lg font-semibold text-gray-900">Seat Map Templates</h3>
          </div>
          <div class="p-6">
            <div class="flex gap-3 mb-4">
              <input 
                v-model="dictFilters.seatMapQuery" 
                class="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="Tìm..." 
              />
              <button 
                @click="loadSeatMaps"
                class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
              >
                Lọc
              </button>
              <button 
                @click="openSeatMapModal()"
                class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                <svg class="w-4 h-4 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
                </svg>
                Thêm
              </button>
            </div>
            <div class="overflow-x-auto">
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Code</th>
                    <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tên</th>
                    <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tổng ghế</th>
                    <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Preview</th>
                    <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr v-for="s in seatMaps" :key="s.code" class="hover:bg-gray-50">
                    <td class="px-4 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ s.code }}</td>
                    <td class="px-4 py-4 whitespace-nowrap text-sm text-gray-900">{{ s.name }}</td>
                    <td class="px-4 py-4 whitespace-nowrap text-sm text-gray-900">{{ s.totalSeats }}</td>
                    <td class="px-4 py-4 whitespace-nowrap text-sm font-medium">
                      <button 
                        @click="previewSeatMap(s)"
                        class="text-blue-600 hover:text-blue-900"
                      >
                        Xem
                      </button>
                    </td>
                    <td class="px-4 py-4 whitespace-nowrap text-sm font-medium">
                      <div class="flex gap-2">
                        <button 
                          @click="openSeatMapModal(s)"
                          class="text-blue-600 hover:text-blue-900"
                        >
                          Sửa
                        </button>
                        <button 
                          @click="removeSeatMap(s)"
                          class="text-red-600 hover:text-red-900"
                        >
                          Xoá
                        </button>
                      </div>
                    </td>
                  </tr>
                  <tr v-if="seatMaps.length===0">
                    <td colspan="5" class="px-4 py-4 text-center text-sm text-gray-500">Trống</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      <!-- Route Catalog -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900">Route Catalog (OD & Bến chuẩn)</h3>
        </div>
        <div class="p-6">
          <div class="flex gap-3 mb-4">
            <input 
              v-model="dictFilters.routeCatalogQuery" 
              class="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Tìm theo OD/bến..." 
            />
            <button 
              @click="loadRouteCatalog"
              class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
            >
              Lọc
            </button>
            <button 
              @click="openRouteCatalogModal()"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              <svg class="w-4 h-4 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
              </svg>
              Thêm
            </button>
          </div>
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Code</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Origin</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Destination</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Origin Station</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Destination Station</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="r in routeCatalog" :key="r.code" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ r.code }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ r.origin }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ r.destination }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ r.originStationCode }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ r.destinationStationCode }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div class="flex gap-2">
                      <button 
                        @click="openRouteCatalogModal(r)"
                        class="text-blue-600 hover:text-blue-900"
                      >
                        Sửa
                      </button>
                      <button 
                        @click="removeRouteCatalog(r)"
                        class="text-red-600 hover:text-red-900"
                      >
                        Xoá
                      </button>
                    </div>
                  </td>
                </tr>
                <tr v-if="routeCatalog.length===0">
                  <td colspan="6" class="px-6 py-4 text-center text-sm text-gray-500">Trống</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </section>

    <!-- PROVIDERS -->
    <section v-if="activeTab === 'providers'" class="space-y-6">
      <div class="bg-white rounded-xl shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900">Providers</h3>
        </div>
        <div class="p-6">
          <div class="flex gap-3 mb-4">
            <input 
              v-model="providerFilters.query" 
              class="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Tìm theo tên/mã..." 
            />
            <select 
              v-model="providerFilters.status" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">-- Status --</option>
              <option value="ACTIVE">ACTIVE</option>
              <option value="BANNED">BANNED</option>
            </select>
            <button 
              @click="loadProviders"
              class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
            >
              Lọc
            </button>
          </div>
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Mã</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tên</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Routes</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Slots (7d)</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="p in providers" :key="p.code" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ p.code }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ p.name }}</td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span :class="[
                      'inline-flex px-2 py-1 text-xs font-semibold rounded-full',
                      p.status === 'ACTIVE' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                    ]">
                      {{ p.status }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ p.routesCount }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ p.next7dSlots }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div class="flex gap-2">
                      <button 
                        @click="openProviderModal(p)"
                        class="text-blue-600 hover:text-blue-900"
                      >
                        Sửa
                      </button>
                      <button 
                        v-if="p.status!=='BANNED'" 
                        @click="banProvider(p)"
                        class="text-yellow-600 hover:text-yellow-900"
                      >
                        Ban
                      </button>
                      <button 
                        v-else 
                        @click="unbanProvider(p)"
                        class="text-green-600 hover:text-green-900"
                      >
                        Unban
                      </button>
                    </div>
                  </td>
                </tr>
                <tr v-if="providers.length===0">
                  <td colspan="6" class="px-6 py-4 text-center text-sm text-gray-500">Trống</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </section>

    <!-- ROUTES -->
    <section v-if="activeTab === 'routes'" class="space-y-6">
      <div class="bg-white rounded-xl shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900">Routes</h3>
        </div>
        <div class="p-6">
          <div class="flex flex-wrap gap-3 mb-4">
            <input 
              v-model="routeFilters.owner" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Owner code..." 
            />
            <input 
              v-model="routeFilters.origin" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Origin..." 
            />
            <input 
              v-model="routeFilters.destination" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Destination..." 
            />
            <select 
              v-model="routeFilters.status" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">-- Status --</option>
              <option>DRAFT</option><option>PENDING_REVIEW</option><option>APPROVED</option><option>REJECTED</option><option>ARCHIVED</option>
            </select>
            <button 
              @click="loadRoutes"
              class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
            >
              Lọc
            </button>
            <button 
              @click="openRouteModal()"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              <svg class="w-4 h-4 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
              </svg>
              Thêm tuyến
            </button>
          </div>
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Owner</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Route Code</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Origin → Destination</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Future Slots</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="r in routes" :key="r.id" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ r.ownerCode }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ r.routeCode }}</td>
                  <td class="px-6 py-4 text-sm text-gray-900">{{ r.origin }} → {{ r.destination }}</td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span :class="[
                      'inline-flex px-2 py-1 text-xs font-semibold rounded-full',
                      r.status === 'APPROVED' ? 'bg-green-100 text-green-800' :
                      r.status === 'PENDING_REVIEW' ? 'bg-yellow-100 text-yellow-800' :
                      r.status === 'REJECTED' ? 'bg-red-100 text-red-800' :
                      r.status === 'ARCHIVED' ? 'bg-gray-100 text-gray-800' :
                      'bg-blue-100 text-blue-800'
                    ]">
                      {{ r.status }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ r.futureSlots }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div class="flex gap-2">
                      <button 
                        @click="openRouteModal(r)"
                        class="text-blue-600 hover:text-blue-900"
                      >
                        Sửa
                      </button>
                      <button 
                        v-if="r.status==='DRAFT'" 
                        @click="submitRouteReview(r)"
                        class="text-yellow-600 hover:text-yellow-900"
                      >
                        Submit review
                      </button>
                      <button 
                        v-if="r.status==='PENDING_REVIEW'" 
                        @click="approveRoute(r)"
                        class="text-green-600 hover:text-green-900"
                      >
                        Approve
                      </button>
                      <button 
                        v-if="r.status==='PENDING_REVIEW'" 
                        @click="rejectRoute(r)"
                        class="text-red-600 hover:text-red-900"
                      >
                        Reject
                      </button>
                      <button 
                        @click="openRouteDetail(r)"
                        class="text-purple-600 hover:text-purple-900"
                      >
                        Chi tiết
                      </button>
                    </div>
                  </td>
                </tr>
                <tr v-if="routes.length===0">
                  <td colspan="6" class="px-6 py-4 text-center text-sm text-gray-500">Trống</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </section>

    <!-- BUSES -->
    <section v-if="activeTab === 'buses'" class="space-y-6">
      <div class="bg-white rounded-xl shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900">Buses</h3>
        </div>
        <div class="p-6">
          <div class="flex flex-wrap gap-3 mb-4">
            <input 
              v-model="busFilters.owner" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Owner..." 
            />
            <input 
              v-model="busFilters.plate" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Biển số..." 
            />
            <select 
              v-model="busFilters.category" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">-- Category --</option>
              <option v-for="c in categories" :key="c.code" :value="c.code">{{ c.name }}</option>
            </select>
            <button 
              @click="loadBuses"
              class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
            >
              Lọc
            </button>
            <button 
              @click="openBusModal()"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              <svg class="w-4 h-4 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
              </svg>
              Thêm xe
            </button>
          </div>
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Owner</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Plate</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Category</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Seat Map</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="b in buses" :key="b.id" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ b.ownerCode }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ b.plateNumber }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ b.busCategoryCode }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ b.seatMapCode }}</td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span :class="[
                      'inline-flex px-2 py-1 text-xs font-semibold rounded-full',
                      b.status === 'ACTIVE' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                    ]">
                      {{ b.status }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div class="flex gap-2">
                      <button 
                        @click="openBusModal(b)"
                        class="text-blue-600 hover:text-blue-900"
                      >
                        Sửa
                      </button>
                      <button 
                        @click="previewSeatMapByCode(b.seatMapCode)"
                        class="text-purple-600 hover:text-purple-900"
                      >
                        Seat map
                      </button>
                    </div>
                  </td>
                </tr>
                <tr v-if="buses.length===0">
                  <td colspan="6" class="px-6 py-4 text-center text-sm text-gray-500">Trống</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </section>

    <!-- PRICEBOOKS & PRICES -->
    <section v-if="activeTab === 'pricebooks'" class="space-y-6">
      <div class="bg-white rounded-xl shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900">Pricebooks</h3>
        </div>
        <div class="p-6">
          <div class="flex flex-wrap gap-3 mb-4">
            <input 
              v-model="pricebookFilters.owner" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Owner..." 
            />
            <select 
              v-model="pricebookFilters.status" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">-- Status --</option>
              <option>DRAFT</option><option>PENDING_REVIEW</option><option>APPROVED</option><option>REJECTED</option><option>ARCHIVED</option>
            </select>
            <input 
              v-model="pricebookFilters.onDate" 
              type="date" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500" 
            />
            <button 
              @click="loadPricebooks"
              class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
            >
              Lọc
            </button>
            <button 
              @click="openPricebookModal()"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              <svg class="w-4 h-4 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
              </svg>
              Thêm Pricebook
            </button>
          </div>
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Owner</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Effective</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Lines</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="pb in pricebooks" :key="pb.id" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ pb.ownerCode }}</td>
                  <td class="px-6 py-4 text-sm text-gray-900">{{ pb.name }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ fmtRange(pb.effectiveFrom, pb.effectiveTo) }}</td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span :class="[
                      'inline-flex px-2 py-1 text-xs font-semibold rounded-full',
                      pb.status === 'APPROVED' ? 'bg-green-100 text-green-800' :
                      pb.status === 'PENDING_REVIEW' ? 'bg-yellow-100 text-yellow-800' :
                      pb.status === 'REJECTED' ? 'bg-red-100 text-red-800' :
                      pb.status === 'ARCHIVED' ? 'bg-gray-100 text-gray-800' :
                      'bg-blue-100 text-blue-800'
                    ]">
                      {{ pb.status }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ pb.linesCount }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div class="flex gap-2">
                      <button 
                        @click="openPricebookModal(pb)"
                        class="text-blue-600 hover:text-blue-900"
                      >
                        Sửa
                      </button>
                      <button 
                        v-if="pb.status==='PENDING_REVIEW'" 
                        @click="approvePricebook(pb)"
                        class="text-green-600 hover:text-green-900"
                      >
                        Approve
                      </button>
                      <button 
                        v-if="pb.status==='PENDING_REVIEW'" 
                        @click="rejectPricebook(pb)"
                        class="text-red-600 hover:text-red-900"
                      >
                        Reject
                      </button>
                      <button 
                        @click="openPricebookLines(pb)"
                        class="text-purple-600 hover:text-purple-900"
                      >
                        Lines
                      </button>
                    </div>
                  </td>
                </tr>
                <tr v-if="pricebooks.length===0">
                  <td colspan="6" class="px-6 py-4 text-center text-sm text-gray-500">Trống</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </section>

    <!-- SLOTS -->
    <section v-if="activeTab === 'slots'" class="space-y-6">
      <div class="bg-white rounded-xl shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900">Slots</h3>
        </div>
        <div class="p-6">
          <div class="flex flex-wrap gap-3 mb-4">
            <input 
              v-model="slotFilters.owner" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Owner..." 
            />
            <input 
              v-model="slotFilters.route" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Route code..." 
            />
            <input 
              v-model="slotFilters.date" 
              type="date" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500" 
            />
            <button 
              @click="loadSlots"
              class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
            >
              Lọc
            </button>
            <button 
              @click="openBulkGenerateModal"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              <svg class="w-4 h-4 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
              </svg>
              Bulk generate
            </button>
          </div>
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Owner</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Route</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Depart</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Bus</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Seats (sold/total)</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="s in slots" :key="s.id" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ s.ownerCode }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ s.routeCode }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ s.departTime }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ s.busPlate }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ s.sold }}/{{ s.total }}</td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span :class="[
                      'inline-flex px-2 py-1 text-xs font-semibold rounded-full',
                      s.status === 'ACTIVE' ? 'bg-green-100 text-green-800' :
                      s.status === 'CANCELLED' ? 'bg-red-100 text-red-800' :
                      'bg-gray-100 text-gray-800'
                    ]">
                      {{ s.status }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div class="flex gap-2">
                      <button 
                        @click="openSlotDetail(s)"
                        class="text-blue-600 hover:text-blue-900"
                      >
                        Chi tiết
                      </button>
                    </div>
                  </td>
                </tr>
                <tr v-if="slots.length===0">
                  <td colspan="7" class="px-6 py-4 text-center text-sm text-gray-500">Trống</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </section>

    <!-- LOCKS (Inventory Locks) -->
    <section v-if="activeTab === 'locks'" class="space-y-6">
      <div class="bg-white rounded-xl shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900">Inventory Locks</h3>
        </div>
        <div class="p-6">
          <div class="flex flex-wrap gap-3 mb-4">
            <input 
              v-model="lockFilters.slot" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Slot ID..." 
            />
            <button 
              @click="loadLocks"
              class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
            >
              Lọc
            </button>
          </div>
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Slot</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Seat</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">User</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Locked At</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">TTL</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Source</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="l in locks" :key="l.id" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ l.slotId }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ l.seatNo }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ l.userId }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ l.lockedAt }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ l.ttlSeconds }}s</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ l.source }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div class="flex gap-2">
                      <button 
                        @click="forceRelease(l)"
                        class="text-red-600 hover:text-red-900"
                      >
                        Force release
                      </button>
                    </div>
                  </td>
                </tr>
                <tr v-if="locks.length===0">
                  <td colspan="7" class="px-6 py-4 text-center text-sm text-gray-500">Trống</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </section>

    <!-- MODERATION (Approval Queue) -->
    <section v-if="activeTab === 'moderation'" class="space-y-6">
      <div class="bg-white rounded-xl shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900">Approval Queue</h3>
        </div>
        <div class="p-6">
          <div class="flex flex-wrap gap-3 mb-4">
            <select 
              v-model="moderationFilters.type" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">-- Type --</option>
              <option value="ROUTE">ROUTE</option>
              <option value="PRICEBOOK">PRICEBOOK</option>
              <option value="PROMOTION">PROMOTION</option>
            </select>
            <select 
              v-model="moderationFilters.status" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">-- Status --</option>
              <option>PENDING_REVIEW</option><option>APPROVED</option><option>REJECTED</option>
            </select>
            <button 
              @click="loadModeration"
              class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
            >
              Lọc
            </button>
          </div>
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Owner</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Submitted By</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">At</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="m in moderationQueue" :key="m.id" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ m.type }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ m.ownerCode }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ m.submittedBy }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ m.submittedAt }}</td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span :class="[
                      'inline-flex px-2 py-1 text-xs font-semibold rounded-full',
                      m.status === 'APPROVED' ? 'bg-green-100 text-green-800' :
                      m.status === 'PENDING_REVIEW' ? 'bg-yellow-100 text-yellow-800' :
                      'bg-red-100 text-red-800'
                    ]">
                      {{ m.status }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div class="flex gap-2">
                      <button 
                        @click="viewDiff(m)"
                        class="text-blue-600 hover:text-blue-900"
                      >
                        Diff
                      </button>
                      <button 
                        v-if="m.status==='PENDING_REVIEW'" 
                        @click="approveModeration(m)"
                        class="text-green-600 hover:text-green-900"
                      >
                        Approve
                      </button>
                      <button 
                        v-if="m.status==='PENDING_REVIEW'" 
                        @click="rejectModeration(m)"
                        class="text-red-600 hover:text-red-900"
                      >
                        Reject
                      </button>
                    </div>
                  </td>
                </tr>
                <tr v-if="moderationQueue.length===0">
                  <td colspan="6" class="px-6 py-4 text-center text-sm text-gray-500">Trống</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </section>

    <!-- HOLIDAYS & SURCHARGES -->
    <section v-if="activeTab === 'surcharges'" class="space-y-6">
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="bg-white rounded-xl shadow-sm border border-gray-200">
          <div class="px-6 py-4 border-b border-gray-200">
            <h3 class="text-lg font-semibold text-gray-900">Holidays</h3>
          </div>
          <div class="p-6">
            <div class="flex gap-3 mb-4">
              <input 
                v-model="surchargeFilters.holidayQuery" 
                class="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="Tìm..." 
              />
              <button 
                @click="loadHolidays"
                class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
              >
                Lọc
              </button>
              <button 
                @click="openHolidayModal()"
                class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                <svg class="w-4 h-4 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
                </svg>
                Thêm
              </button>
            </div>
            <div class="overflow-x-auto">
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Region</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr v-for="h in holidays" :key="h.id" class="hover:bg-gray-50">
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ h.date }}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ h.region }}</td>
                    <td class="px-6 py-4 text-sm text-gray-900">{{ h.name }}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div class="flex gap-2">
                        <button 
                          @click="openHolidayModal(h)"
                          class="text-blue-600 hover:text-blue-900"
                        >
                          Sửa
                        </button>
                        <button 
                          @click="removeHoliday(h)"
                          class="text-red-600 hover:text-red-900"
                        >
                          Xoá
                        </button>
                      </div>
                    </td>
                  </tr>
                  <tr v-if="holidays.length===0">
                    <td colspan="4" class="px-6 py-4 text-center text-sm text-gray-500">Trống</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-gray-200">
          <div class="px-6 py-4 border-b border-gray-200">
            <h3 class="text-lg font-semibold text-gray-900">Surcharges</h3>
          </div>
          <div class="p-6">
            <div class="flex gap-3 mb-4">
              <select 
                v-model="surchargeFilters.scope" 
                class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="">-- Scope --</option>
                <option>GLOBAL</option><option>PROVIDER</option><option>ROUTE</option>
              </select>
              <button 
                @click="loadSurcharges"
                class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
              >
                Lọc
              </button>
              <button 
                @click="openSurchargeModal()"
                class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                <svg class="w-4 h-4 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
                </svg>
                Thêm
              </button>
            </div>
            <div class="overflow-x-auto">
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Scope</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Rule</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Value</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date Range</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Priority</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr v-for="s in surcharges" :key="s.id" class="hover:bg-gray-50">
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ s.scope }}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ s.ruleType }}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ s.value }}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ fmtRange(s.from, s.to) }}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ s.priority }}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div class="flex gap-2">
                        <button 
                          @click="openSurchargeModal(s)"
                          class="text-blue-600 hover:text-blue-900"
                        >
                          Sửa
                        </button>
                        <button 
                          @click="removeSurcharge(s)"
                          class="text-red-600 hover:text-red-900"
                        >
                          Xoá
                        </button>
                      </div>
                    </td>
                  </tr>
                  <tr v-if="surcharges.length===0">
                    <td colspan="6" class="px-6 py-4 text-center text-sm text-gray-500">Trống</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900">Test giá (Price Resolver)</h3>
        </div>
        <div class="p-6">
          <div class="flex flex-wrap gap-3 mb-4">
            <input 
              v-model="priceTest.owner" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Owner..." 
            />
            <input 
              v-model="priceTest.route" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Route code..." 
            />
            <select 
              v-model="priceTest.category" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">-- Category --</option>
              <option v-for="c in categories" :key="c.code" :value="c.code">{{ c.name }}</option>
            </select>
            <input 
              v-model="priceTest.date" 
              type="date" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500" 
            />
            <button 
              @click="testPrice"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              Resolve
            </button>
          </div>
          <div class="text-sm text-gray-600" v-if="priceTestResult">
            Final Price: <strong class="text-green-600">{{ priceTestResult.final }}</strong>
            <span class="text-gray-500"> (base: {{ priceTestResult.base }}, surcharges: {{ priceTestResult.surcharges?.join(' + ') }})</span>
          </div>
        </div>
      </div>
    </section>

    <!-- AUDITS -->
    <section v-if="activeTab === 'audits'" class="space-y-6">
      <div class="bg-white rounded-xl shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900">Audit Logs</h3>
        </div>
        <div class="p-6">
          <div class="flex flex-wrap gap-3 mb-4">
            <input 
              v-model="auditFilters.entityType" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Entity type..." 
            />
            <input 
              v-model="auditFilters.entityId" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Entity ID..." 
            />
            <input 
              v-model="auditFilters.actor" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Actor..." 
            />
            <input 
              v-model="auditFilters.from" 
              type="date" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500" 
            />
            <input 
              v-model="auditFilters.to" 
              type="date" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500" 
            />
            <button 
              @click="loadAudits"
              class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
            >
              Lọc
            </button>
          </div>
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">At</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actor</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Action</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Entity</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Before</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">After</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="a in audits" :key="a.id" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ a.at }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ a.actor }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ a.action }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ a.entityType }}#{{ a.entityId }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <button 
                      @click="viewJson(a.before)"
                      class="text-blue-600 hover:text-blue-900"
                    >
                      Xem
                    </button>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <button 
                      @click="viewJson(a.after)"
                      class="text-blue-600 hover:text-blue-900"
                    >
                      Xem
                    </button>
                  </td>
                </tr>
                <tr v-if="audits.length===0">
                  <td colspan="6" class="px-6 py-4 text-center text-sm text-gray-500">Trống</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </section>

    <!-- IMPORT/EXPORT -->
    <section v-if="activeTab === 'importExport'" class="space-y-6">
      <div class="bg-white rounded-xl shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900">Import / Export</h3>
        </div>
        <div class="p-6">
          <div class="flex items-center gap-3 mb-4">
            <select 
              v-model="importType" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="routes">Routes</option>
              <option value="pricebooks">Pricebooks</option>
              <option value="slots">Slots Rules</option>
            </select>
            <input 
              type="file" 
              @change="handleImportFile"
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500" 
            />
            <button 
              @click="startImport"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              Start Import
            </button>
          </div>
          <div v-if="importPreview" class="text-sm bg-gray-50 p-4 rounded-lg mb-4">
            <p class="text-gray-700 mb-3">Preview: {{ importPreview.summary }}</p>
            <div class="flex gap-2">
              <button 
                @click="confirmImport"
                class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
              >
                Confirm
              </button>
              <button 
                @click="cancelImport"
                class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
              >
                Cancel
              </button>
            </div>
          </div>
          <div class="flex gap-3">
            <button 
              @click="exportCurrent"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              <svg class="w-4 h-4 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
              </svg>
              Export theo bộ lọc
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- MODALS PLACEHOLDER -->
    <div v-if="modal.visible" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3 class="text-lg font-semibold">{{ modal.title }}</h3>
          <button class="btn" @click="closeModal">✕</button>
        </div>
        <div class="modal-body">
          <!-- Tuỳ theo modal.type mà render form tương ứng -->
          <div v-if="modal.type === 'category'">
            <label class="lbl">Code</label><input v-model="modal.form.code" class="input" />
            <label class="lbl">Name</label><input v-model="modal.form.name" class="input" />
            <label class="lbl">Description</label><input v-model="modal.form.description" class="input" />
          </div>
          <div v-else-if="modal.type === 'seatMap'">
            <label class="lbl">Code</label><input v-model="modal.form.code" class="input" />
            <label class="lbl">Name</label><input v-model="modal.form.name" class="input" />
            <label class="lbl">Total seats</label><input v-model.number="modal.form.totalSeats" type="number" class="input" />
            <p class="hint">TODO: Chèn editor layout ghế (rows/cols/deck/types)</p>
          </div>
          <div v-else-if="modal.type === 'route'">
            <label class="lbl">Owner</label><input v-model="modal.form.ownerCode" class="input" />
            <label class="lbl">Route code</label><input v-model="modal.form.routeCode" class="input" />
            <label class="lbl">Origin</label><input v-model="modal.form.origin" class="input" />
            <label class="lbl">Destination</label><input v-model="modal.form.destination" class="input" />
            <label class="lbl">Status</label>
            <select v-model="modal.form.status" class="select">
              <option>DRAFT</option><option>PENDING_REVIEW</option><option>APPROVED</option><option>REJECTED</option><option>ARCHIVED</option>
            </select>
          </div>
          <div v-else-if="modal.type === 'bus'">
            <label class="lbl">Owner</label><input v-model="modal.form.ownerCode" class="input" />
            <label class="lbl">Plate</label><input v-model="modal.form.plateNumber" class="input" />
            <label class="lbl">Category</label>
            <select v-model="modal.form.busCategoryCode" class="select">
              <option v-for="c in categories" :key="c.code" :value="c.code">{{ c.name }}</option>
            </select>
            <label class="lbl">Seat Map</label>
            <select v-model="modal.form.seatMapCode" class="select">
              <option v-for="s in seatMaps" :key="s.code" :value="s.code">{{ s.name }}</option>
            </select>
          </div>
          <div v-else-if="modal.type === 'pricebook'">
            <label class="lbl">Owner</label><input v-model="modal.form.ownerCode" class="input" />
            <label class="lbl">Name</label><input v-model="modal.form.name" class="input" />
            <label class="lbl">Effective From</label><input v-model="modal.form.effectiveFrom" type="datetime-local" class="input" />
            <label class="lbl">Effective To</label><input v-model="modal.form.effectiveTo" type="datetime-local" class="input" />
            <label class="lbl">Status</label>
            <select v-model="modal.form.status" class="select">
              <option>DRAFT</option><option>PENDING_REVIEW</option><option>APPROVED</option><option>REJECTED</option><option>ARCHIVED</option>
            </select>
            <p class="hint">TODO: Editor Lines (route+category+basePrice) + check overlap client-side.</p>
          </div>
          <div v-else-if="modal.type === 'bulkGenerate'">
            <p class="hint">Thiết lập rule tạo slots hàng loạt:</p>
            <label class="lbl">Owner</label><input v-model="modal.form.ownerCode" class="input" />
            <label class="lbl">Route code</label><input v-model="modal.form.routeCode" class="input" />
            <label class="lbl">Start date</label><input v-model="modal.form.startDate" type="date" class="input" />
            <label class="lbl">End date</label><input v-model="modal.form.endDate" type="date" class="input" />
            <label class="lbl">Days of week</label><input v-model="modal.form.daysOfWeek" class="input" placeholder="[1,2,3,4,5,6,7]" />
            <label class="lbl">Depart time</label><input v-model="modal.form.departTime" type="time" class="input" />
            <label class="lbl">Duration (minutes)</label><input v-model.number="modal.form.durationMinutes" type="number" class="input" />
          </div>
          <div v-else-if="modal.type === 'holiday'">
            <label class="lbl">Date</label><input v-model="modal.form.date" type="date" class="input" />
            <label class="lbl">Region</label><input v-model="modal.form.region" class="input" />
            <label class="lbl">Name</label><input v-model="modal.form.name" class="input" />
          </div>
          <div v-else-if="modal.type === 'surcharge'">
            <label class="lbl">Scope</label>
            <select v-model="modal.form.scope" class="select">
              <option>GLOBAL</option><option>PROVIDER</option><option>ROUTE</option>
            </select>
            <label class="lbl">Rule Type</label>
            <select v-model="modal.form.ruleType" class="select">
              <option>PERCENT</option><option>FLAT</option>
            </select>
            <label class="lbl">Value</label><input v-model.number="modal.form.value" type="number" class="input" />
            <label class="lbl">From</label><input v-model="modal.form.from" type="date" class="input" />
            <label class="lbl">To</label><input v-model="modal.form.to" type="date" class="input" />
            <label class="lbl">Priority</label><input v-model.number="modal.form.priority" type="number" class="input" />
          </div>
          <div v-else>
            <p>Modal nội dung...</p>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn" @click="closeModal">Đóng</button>
          <button class="btn btn-primary" @click="saveModal">Lưu</button>
        </div>
      </div>
    </div>

    <!-- JSON Viewer (simple) -->
    <div v-if="jsonViewer.visible" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3 class="text-lg font-semibold">JSON</h3>
          <button class="btn" @click="jsonViewer.visible=false">✕</button>
        </div>
        <pre class="json-pre">{{ jsonViewer.text }}</pre>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'

/** TABS */
const tabs = [
  { id: 'overview', label: 'Overview' },
  { id: 'dictionaries', label: 'Dictionaries' },
  { id: 'providers', label: 'Providers' },
  { id: 'routes', label: 'Routes' },
  { id: 'buses', label: 'Buses' },
  { id: 'pricebooks', label: 'Pricebooks' },
  { id: 'slots', label: 'Slots' },
  { id: 'locks', label: 'Locks' },
  { id: 'moderation', label: 'Moderation' },
  { id: 'surcharges', label: 'Holidays & Surcharges' },
  { id: 'audits', label: 'Audits' },
  { id: 'importExport', label: 'Import/Export' },
] as const
const activeTab = ref<typeof tabs[number]['id']>('overview')

/** BUSINESS TYPES (interfaces tối giản) */
type Status = 'DRAFT' | 'PENDING_REVIEW' | 'APPROVED' | 'REJECTED' | 'ARCHIVED' | 'ACTIVE' | 'BANNED'
interface Provider { id: number; code: string; name: string; status: Status; routesCount?: number; next7dSlots?: number }
interface Category { code: string; name: string; description?: string }
interface SeatMap { code: string; name: string; totalSeats: number }
interface Route { id: number; ownerCode: string; routeCode: string; origin: string; destination: string; status: Status; futureSlots?: number }
interface Bus { id: number; ownerCode: string; plateNumber: string; busCategoryCode: string; seatMapCode: string; status: Status }
interface Pricebook { id: number; ownerCode: string; name: string; effectiveFrom: string; effectiveTo: string; status: Status; linesCount?: number }
interface Slot { id: number; ownerCode: string; routeCode: string; departTime: string; busPlate: string; sold: number; total: number; status: string }
interface Lock { id: number; slotId: number; seatNo: string; userId: string; lockedAt: string; ttlSeconds: number; source: string }
interface Audit { id: number; at: string; actor: string; action: string; entityType: string; entityId: string; before?: any; after?: any }
interface Holiday { id: number; date: string; region: string; name: string }
interface Surcharge { id: number; scope: 'GLOBAL'|'PROVIDER'|'ROUTE'; ruleType: 'PERCENT'|'FLAT'; value: number; from: string; to: string; priority: number }

/** STATE */
// Overview
const kpis = reactive<{ bookingsToday?: number; occupancyRate?: number; cancellations?: number; activeProviders?: number }>({})
const overviewFilters = reactive({ query: '' })
const overviewAlerts = ref<{ type: string; ownerName: string; description: string; suggestion: string; at: string }[]>([])

// Dictionaries
const dictFilters = reactive({ categoryQuery: '', seatMapQuery: '', routeCatalogQuery: '' })
const categories = ref<Category[]>([])
const seatMaps = ref<SeatMap[]>([])
const routeCatalog = ref<any[]>([])

// Providers
const providerFilters = reactive({ query: '', status: '' as ''|'ACTIVE'|'BANNED' })
const providers = ref<Provider[]>([])

// Routes
const routeFilters = reactive({ owner: '', origin: '', destination: '', status: '' as Status|'' })
const routes = ref<Route[]>([])

// Buses
const busFilters = reactive({ owner: '', plate: '', category: '' })
const buses = ref<Bus[]>([])

// Pricebooks
const pricebookFilters = reactive({ owner: '', status: '' as Status|'', onDate: '' })
const pricebooks = ref<Pricebook[]>([])

// Slots
const slotFilters = reactive({ owner: '', route: '', date: '' })
const slots = ref<Slot[]>([])

// Locks
const lockFilters = reactive({ slot: '' })
const locks = ref<Lock[]>([])

// Moderation
const moderationFilters = reactive({ type: '' as ''|'ROUTE'|'PRICEBOOK'|'PROMOTION', status: '' as ''|Status })
const moderationQueue = ref<any[]>([])

// Holidays & Surcharges
const surchargeFilters = reactive({ holidayQuery: '', scope: '' })
const holidays = ref<Holiday[]>([])
const surcharges = ref<Surcharge[]>([])
const priceTest = reactive({ owner: '', route: '', category: '', date: '' })
const priceTestResult = ref<{ base: number; final: number; surcharges: string[] }|null>(null)

// Audits
const auditFilters = reactive({ entityType: '', entityId: '', actor: '', from: '', to: '' })
const audits = ref<Audit[]>([])

// Import/Export
const importType = ref<'routes'|'pricebooks'|'slots'>('routes')
const importPreview = ref<{ summary: string }|null>(null)

// Modal & JSON viewer
const modal = reactive<{ visible: boolean; title?: string; type?: string; form: any }>({ visible: false, form: {} })
const jsonViewer = reactive<{ visible: boolean; text: string }>({ visible: false, text: '' })

/** UTILS */
const fmtRange = (from?: string, to?: string) => {
  if (!from || !to) return '-'
  return new Date(from).toLocaleString() + ' → ' + new Date(to).toLocaleString()
}

/** LOADERS (TODO: thay bằng gọi API thật) */
async function refreshAll() {
  await Promise.all([loadOverview(), loadCategories(), loadSeatMaps(), loadRouteCatalog(), loadProviders(), loadRoutes(), loadBuses(), loadPricebooks(), loadSlots(), loadLocks(), loadModeration(), loadHolidays(), loadSurcharges(), loadAudits()])
}
async function loadOverview() {
  // TODO: GET /dashboard/kpis ; /dashboard/alerts
  kpis.bookingsToday = 120; kpis.occupancyRate = 73; kpis.cancellations = 5; kpis.activeProviders = 12
  overviewAlerts.value = [
    { type: 'PRICE_OVERLAP', ownerName: 'NXANH', description: 'Giá trùng hiệu lực trên SGN-DAD', suggestion: 'Gộp pricebook hoặc điều chỉnh khoảng ngày', at: new Date().toISOString() },
  ]
}
async function loadCategories() { categories.value = [{ code: 'SLP', name: 'Giường nằm 40' }, { code: 'LIMO9', name: 'Limousine 9' }] }
async function loadSeatMaps() { seatMaps.value = [{ code: 'SLP40', name: 'Sleeper 40', totalSeats: 40 }] }
async function loadRouteCatalog() { routeCatalog.value = [{ code: 'SGN-DAD', origin: 'Saigon', destination: 'Da Nang', originStationCode: 'BXM', destinationStationCode: 'BXDN' }] }
async function loadProviders() { providers.value = [{ id:1, code:'NXANH', name:'Nhà Xe Xanh', status:'ACTIVE', routesCount:12, next7dSlots:84 }] }
async function loadRoutes() { routes.value = [{ id:1, ownerCode:'NXANH', routeCode:'NXANH-SGN-DAD-01', origin:'Saigon', destination:'Da Nang', status:'DRAFT', futureSlots:10 }] }
async function loadBuses() { buses.value = [{ id:1, ownerCode:'NXANH', plateNumber:'51F-123.45', busCategoryCode:'SLP', seatMapCode:'SLP40', status:'ACTIVE' }] }
async function loadPricebooks() { pricebooks.value = [{ id:1, ownerCode:'NXANH', name:'Giá T1/2025', effectiveFrom:'2025-01-01T00:00:00Z', effectiveTo:'2025-01-31T23:59:59Z', status:'PENDING_REVIEW', linesCount:12 }] }
async function loadSlots() { slots.value = [{ id:1, ownerCode:'NXANH', routeCode:'NXANH-SGN-DAD-01', departTime:'2025-01-10 22:00', busPlate:'51F-123.45', sold:12, total:40, status:'OPEN' }] }
// async function loadLocks() { locks.value = [{ id:1, slotId:1, seatNo:'A01', userId:'u_123', lockedAt:new Date().toISOString(), ttlSeconds:480, source:'web' }] }
async function loadModeration() { moderationQueue.value = [{ id:1, type:'ROUTE', ownerCode:'NXANH', submittedBy:'admin_b', submittedAt:new Date().toISOString(), status:'PENDING_REVIEW' }] }
async function loadHolidays() { holidays.value = [{ id:1, date:'2025-02-01', region:'VN', name:'Tết' }] }
async function loadSurcharges() { surcharges.value = [{ id:1, scope:'GLOBAL', ruleType:'PERCENT', value:15, from:'2025-02-01', to:'2025-02-05', priority:10 }] }
async function loadAudits() { audits.value = [{ id:1, at:new Date().toISOString(), actor:'superadmin', action:'APPROVE', entityType:'PRICEBOOK', entityId:'1', before:{status:'PENDING_REVIEW'}, after:{status:'APPROVED'} }] }

/** ACTIONS (TODO: nối API thật) */
function openImportWizard(){ activeTab.value = 'importExport' }
function exportCurrent(){ /* TODO: GET /export/{type}?filters... */ }
function handleImportFile(e: Event){ /* TODO: parse + upload */ }
function startImport(){ importPreview.value = { summary: '100 rows · 5 errors' } }
function confirmImport(){ /* TODO: POST confirm */ importPreview.value = null }
function cancelImport(){ importPreview.value = null }

function openCategoryModal(row?: Category){ modal.visible=true; modal.title=row?'Sửa category':'Thêm category'; modal.type='category'; modal.form=row?{...row}:{ code:'', name:'', description:'' } }
function removeCategory(row: Category){ /* TODO */ }

function openSeatMapModal(row?: SeatMap){ modal.visible=true; modal.title=row?'Sửa seat map':'Thêm seat map'; modal.type='seatMap'; modal.form=row?{...row}:{ code:'', name:'', totalSeats:40 } }
function previewSeatMap(row: SeatMap){ /* TODO: open preview */ }
function previewSeatMapByCode(code: string){ const row = seatMaps.value.find(s=>s.code===code); if(row) previewSeatMap(row) }
function removeSeatMap(row: SeatMap){ /* TODO */ }

function openRouteCatalogModal(row?: any){ modal.visible=true; modal.title=row?'Sửa route catalog':'Thêm route catalog'; modal.type='routeCatalog'; modal.form=row?{...row}:{ code:'', origin:'', destination:'', originStationCode:'', destinationStationCode:'' } }
function removeRouteCatalog(row: any){ /* TODO */ }

function openProviderModal(row?: Provider){ modal.visible=true; modal.title=row?'Sửa provider':'Thêm provider'; modal.type='provider'; modal.form=row?{...row}:{ code:'', name:'', status:'ACTIVE' } }
function banProvider(row: Provider){ /* TODO: POST /providers/{id}/ban */ }
function unbanProvider(row: Provider){ /* TODO: POST /providers/{id}/unban */ }

function openRouteModal(row?: Route){ modal.visible=true; modal.title=row?'Sửa route':'Thêm route'; modal.type='route'; modal.form=row?{...row}:{ ownerCode:'', routeCode:'', origin:'', destination:'', status:'DRAFT' } }
function submitRouteReview(row: Route){ /* TODO: POST /routes/{id}/submit-review */ }
function approveRoute(row: Route){ /* TODO: POST /routes/{id}/approve */ }
function rejectRoute(row: Route){ /* TODO: POST /routes/{id}/reject */ }
function openRouteDetail(row: Route){ /* TODO: open detail drawer */ }

function openBusModal(row?: Bus){ modal.visible=true; modal.title=row?'Sửa bus':'Thêm bus'; modal.type='bus'; modal.form=row?{...row}:{ ownerCode:'', plateNumber:'', busCategoryCode:'', seatMapCode:'' } }

function openPricebookModal(row?: Pricebook){ modal.visible=true; modal.title=row?'Sửa pricebook':'Thêm pricebook'; modal.type='pricebook'; modal.form=row?{...row}:{ ownerCode:'', name:'', effectiveFrom:'', effectiveTo:'', status:'DRAFT' } }
function approvePricebook(row: Pricebook){ /* TODO */ }
function rejectPricebook(row: Pricebook){ /* TODO */ }
function openPricebookLines(row: Pricebook){ /* TODO: open price lines editor */ }

function openBulkGenerateModal(){ modal.visible=true; modal.title='Bulk Generate Slots'; modal.type='bulkGenerate'; modal.form={ ownerCode:'', routeCode:'', startDate:'', endDate:'', daysOfWeek:'[1,2,3,4,5,6,7]', departTime:'22:00', durationMinutes: 900 } }
function openSlotDetail(row: Slot){ /* TODO */ }

function loadLocks(){ locks.value = locks.value } // placeholder (đã có loader ở refreshAll)
function forceRelease(row: Lock){ /* TODO: POST /locks/force-release */ }

function viewDiff(row: any){ jsonViewer.visible = true; jsonViewer.text = JSON.stringify(row, null, 2) }
function approveModeration(row: any){ /* TODO */ }
function rejectModeration(row: any){ /* TODO */ }

function openHolidayModal(row?: Holiday){ modal.visible=true; modal.title=row?'Sửa holiday':'Thêm holiday'; modal.type='holiday'; modal.form=row?{...row}:{ date:'', region:'VN', name:'' } }
function removeHoliday(row: Holiday){ /* TODO */ }

function openSurchargeModal(row?: Surcharge){ modal.visible=true; modal.title=row?'Sửa surcharge':'Thêm surcharge'; modal.type='surcharge'; modal.form=row?{...row}:{ scope:'GLOBAL', ruleType:'PERCENT', value:10, from:'', to:'', priority:10 } }
function removeSurcharge(row: Surcharge){ /* TODO */ }

function testPrice(){
  // TODO: gọi GET /prices/resolve?owner=&route=&category=&onDate=
  priceTestResult.value = { base: 450000, final: 517500, surcharges: ['GLOBAL +15%'] }
}

function openBusModalForRoute(routeCode: string){ /* optional helper */ }

function openRouteDetailDrawer(){ /* optional */ }

function openSlotCalendar(){ /* optional calendar view */ }

function openSlotAssignment(){ /* optional: assign bus/driver */ }

function openSeatMapEditor(){ /* optional: advanced editor */ }

function openProviderPolicy(){ /* optional: provider policy mapping */ }

function openRouteValidationReport(){ /* optional: quality checks */ }

function openPriceOverlapCheck(){ /* optional: client-side validator */ }

function openImportTemplates(){ /* download CSV/XLSX templates */ }

/** Modal helpers */
function closeModal(){ modal.visible=false; modal.type=undefined; modal.title=undefined; modal.form={} }
function saveModal(){
  // TODO: thêm logic gọi API theo modal.type
  closeModal()
}

function viewJson(obj: any){ jsonViewer.visible=true; jsonViewer.text = JSON.stringify(obj, null, 2) }

onMounted(() => { refreshAll() })
</script>

<style scoped>
/* Custom styles for modals and JSON viewer */
.modal { 
  position: fixed; 
  inset: 0; 
  background: rgba(0,0,0,0.4); 
  display: grid; 
  place-items: center; 
  z-index: 999; 
}

.modal-content { 
  background: #fff; 
  width: min(760px, 94vw); 
  border-radius: 12px; 
  padding: 12px; 
  border: 1px solid #eee; 
}

.modal-header { 
  display: flex; 
  align-items: center; 
  justify-content: space-between; 
  border-bottom: 1px solid #f0f0f0; 
  padding-bottom: 6px; 
  margin-bottom: 8px; 
}

.modal-body { 
  display: grid; 
  gap: 8px; 
}

.modal-footer { 
  display: flex; 
  justify-content: flex-end; 
  gap: 8px; 
  margin-top: 8px; 
}

.lbl { 
  font-size: 12px; 
  color: #444; 
}

.hint { 
  font-size: 12px; 
  color: #6b7280; 
}

.json-pre { 
  max-height: 60vh; 
  overflow: auto; 
  background: #0b1020; 
  color: #e5e7eb; 
  padding: 10px; 
  border-radius: 8px; 
}
</style>
