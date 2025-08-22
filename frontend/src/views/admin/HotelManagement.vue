<template>
  <div>
    <h1 class="text-3xl font-bold text-gray-800 mb-4">
      Quản lý hệ thống Khách sạn
    </h1>

    <div class="border-b border-gray-200 mb-6">
      <nav class="-mb-px flex space-x-6">
        <button
          v-for="tab in tabs"
          :key="tab.id"
          @click="activeTab = tab.id"
          :class="[
            'whitespace-nowrap py-3 px-1 border-b-2 font-medium text-sm flex items-center gap-2',
            activeTab === tab.id
              ? 'border-indigo-500 text-indigo-600'
              : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300',
          ]"
        >
          <i :class="tab.icon"></i>
          <span>{{ tab.name }}</span>
          <span
            v-if="tab.id === 'pending' && pendingHotels.length > 0"
            class="ml-2 bg-red-100 text-red-600 text-xs font-bold px-2 py-0.5 rounded-full"
            >{{ pendingHotels.length }}</span
          >
        </button>
      </nav>
    </div>

    <div>
      <div v-if="activeTab === 'pending'">
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-bold text-gray-700">
            Danh sách Khách sạn chờ phê duyệt
          </h2>
            <div class="flex gap-3">
              <input v-model="pendingHotelSearch" placeholder="Tìm theo tên khách sạn hoặc thành phố" class="border border-slate-300 rounded-md px-3 py-2 w-72"/>
              <CustomSelect
                v-model="pendingHotelFilterProvince"
                :options="[{ label: 'Tất cả thành phố', value: '' }, ...provinces.map(p => ({ label: p.name, value: p.id }))]"
                placeholder="Tất cả thành phố"
                class="w-48"
              />
            </div>
          </div>

          <div class="overflow-x-auto">
            <div class="overflow-y-auto h-[324px]">
              <table class="min-w-[1000px] w-full divide-y divide-slate-200">
                <thead class="bg-slate-100">
                  <tr>
                    <th class="sticky top-0 bg-slate-100 px-3 py-3 text-left text-xs font-bold text-slate-700 uppercase">ID</th>
                    <th class="sticky top-0 bg-slate-100 px-3 py-3 text-left text-xs font-bold text-slate-700 uppercase">Tên khách sạn</th>
                    <th class="sticky top-0 bg-slate-100 px-3 py-3 text-left text-xs font-bold text-slate-700 uppercase">Hạng sao</th>
                    <th class="sticky top-0 bg-slate-100 px-3 py-3 text-left text-xs font-bold text-slate-700 uppercase">Thành phố</th>
                    <th class="sticky top-0 bg-slate-100 px-3 py-3 text-left text-xs font-bold text-slate-700 uppercase">Địa chỉ</th>
                    <th class="sticky top-0 bg-slate-100 px-3 py-3 text-left text-xs font-bold text-slate-700 uppercase">Ngày tạo</th>
                    <th class="sticky top-0 bg-slate-100 px-3 py-3 text-left text-xs font-bold text-slate-700 uppercase">Trạng thái duyệt</th>
                    <th class="sticky top-0 bg-slate-100 px-3 py-3 text-right text-xs font-bold text-slate-700 uppercase">Hành động</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-slate-100">
                  <tr v-for="h in paginatedPendingHotels" :key="h.id" class="hover:bg-slate-50">
                    <td class="px-3 py-3 text-sm text-slate-700">{{ h.id }}</td>
                    <td class="px-3 py-3 text-sm font-semibold text-slate-900">{{ h.name }}</td>
                    <td class="px-3 py-3 text-sm text-slate-700">
                      <div class="flex items-center space-x-1">
                        <span class="text-sm font-medium text-slate-900">{{ h.starRating }}</span>
                        <div class="flex space-x-0.5">
                          <i v-for="star in h.starRating" :key="star"
                            class="fas fa-star text-yellow-400 text-xs"></i>
                        </div>
                      </div>
                    </td>
                    <td class="px-3 py-3 text-sm text-slate-700">
                      <div class="flex items-center space-x-2">
                        <i class="fas fa-map-marker-alt text-red-400 text-xs"></i>
                        <span class="text-sm font-medium text-slate-700">{{ h.provinceName }}</span>
                      </div>
                    </td>
                    <td class="px-3 py-3 text-sm text-slate-700">{{ h.address }}</td>
                    <td class="px-3 py-3 text-sm text-slate-700">{{ formatDateTime(h.createdAt) }}</td>
                    <td class="px-3 py-3 text-sm text-slate-700">
                      <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                        <i class="fas fa-clock mr-1"></i>
                        Chờ duyệt
                      </span>
                    </td>
                    <td class="px-3 py-3 text-sm text-right">
                      <button @click="viewPendingHotelDetail(h)" class="px-3 py-1.5 bg-blue-100 rounded mr-2 hover:bg-blue-200 text-blue-700">Xem</button>
                      <button @click="approveHotel(h)" class="px-3 py-1.5 bg-green-600 text-white rounded mr-2 hover:bg-green-700">Duyệt</button>
                      <button @click="rejectHotel(h)" class="px-3 py-1.5 bg-red-600 text-white rounded hover:bg-red-700">Từ chối</button>
                    </td>
                  </tr>
                  <tr v-if="paginatedPendingHotels.length === 0">
                    <td colspan="8" class="px-3 py-8 text-center text-slate-500">Không có khách sạn nào chờ phê duyệt</td>
                  </tr>
                </tbody>
              </table>
        </div>
      </div>

          <!-- Phân trang cho pending hotels -->
          <div class="flex items-center justify-end gap-4 px-4 py-3 bg-white border-t border-slate-200 rounded-b-xl mt-4">
            <div class="flex items-center gap-2">
              <span class="text-sm text-gray-700 whitespace-nowrap">Số dòng</span>
              <CustomSelect
                v-model="pendingHotelItemsPerPageStr"
                :options="pendingHotelItemsPerPageOptions.map(opt => ({ label: String(opt), value: String(opt) }))"
                class="w-20 min-w-[100px] h-8 [&>button]:h-8 [&>button]:py-1 [&>button]:text-sm"
                :direction="'up'"
              />
            </div>
            <nav v-if="pendingHotelTotalPages > 1 && pendingHotelItemsPerPageStr !== 'Tất cả'" aria-label="Pagination">
              <ul class="inline-flex items-center space-x-1">
                <li>
                  <button @click="prevPendingHotelPage" :disabled="pendingHotelCurrentPage === 1"
                    class="w-8 h-8 flex items-center justify-center rounded border border-slate-200 text-gray-500 hover:bg-gray-100 disabled:opacity-50">
                    <i class="fas fa-chevron-left text-xs"></i>
                  </button>
                </li>
                <li v-for="page in pendingHotelDisplayedPages" :key="page">
                  <button v-if="page !== '...'" @click="changePendingHotelPage(page)"
                    :class="['w-8 h-8 flex items-center justify-center rounded font-medium',
                      pendingHotelCurrentPage === page ? 'bg-orange-500 text-white border-orange-500' : 'text-gray-700 hover:bg-gray-100 border border-slate-200']">
                    {{ page }}
                  </button>
                  <span v-else class="w-8 h-8 flex items-center justify-center text-gray-400">...</span>
                </li>
                <li>
                  <button @click="nextPendingHotelPage" :disabled="pendingHotelCurrentPage === pendingHotelTotalPages"
                    class="w-8 h-8 flex items-center justify-center rounded border border-slate-200 text-gray-500 hover:bg-gray-100 disabled:opacity-50">
                    <i class="fas fa-chevron-right text-xs"></i>
                  </button>
                </li>
              </ul>
            </nav>
          </div>
        </div>
      </div>

      <div v-if="activeTab === 'amenities'">
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-bold text-gray-700">
              Quản lý Tiện ích chung
            </h2>
            <button @click="openAmenityModal()"
              class="bg-indigo-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-indigo-700"
            >
              <i class="fas fa-plus mr-2"></i>Thêm Tiện ích
            </button>
          </div>

          <div class="mb-4 flex gap-3">
            <input v-model="amenitySearch" placeholder="Tìm theo tên tiện ích" class="border border-slate-300 rounded-md px-3 py-2 w-72"/>
          </div>

          <div class="overflow-x-auto">
            <div class="overflow-y-auto h-[324px]">
              <table class="min-w-[700px] w-full divide-y divide-slate-200">
                <thead class="bg-slate-100">
                  <tr>
                    <th class="sticky top-0 bg-slate-100 px-3 py-3 text-left text-xs font-bold text-slate-700 uppercase">ID</th>
                    <th class="sticky top-0 bg-slate-100 px-3 py-3 text-left text-xs font-bold text-slate-700 uppercase">Tên</th>
                    <th class="sticky top-0 bg-slate-100 px-3 py-3 text-left text-xs font-bold text-slate-700 uppercase">Icon</th>
                    <th class="sticky top-0 bg-slate-100 px-3 py-3 text-left text-xs font-bold text-slate-700 uppercase">Trạng thái</th>
                    <th class="sticky top-0 bg-slate-100 px-3 py-3 text-right text-xs font-bold text-slate-700 uppercase">Hành động</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-slate-100">
                  <tr v-for="a in paginatedAmenities" :key="a.id" class="hover:bg-slate-50">
                    <td class="px-3 py-3 text-sm text-slate-700">{{ a.id }}</td>
                    <td class="px-3 py-3 text-sm font-semibold text-slate-900">{{ a.name }}</td>
                    <td class="px-3 py-3 text-sm text-slate-700">
                      <i v-if="a.icon" :class="a.icon" class="text-lg"></i>
                      <span v-else class="text-slate-400">(Không có)</span>
                    </td>
                    <td class="px-3 py-3 text-sm text-slate-700">
                      <span :class="[
                        'px-2 py-1 text-xs font-medium rounded-full',
                        a.status === 'ACTIVE' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                      ]">
                        {{ a.status === 'ACTIVE' ? 'Hoạt động' : 'Không hoạt động' }}
                      </span>
                    </td>
                    <td class="px-3 py-3 text-sm text-right">
                      <button @click="openAmenityModal(a)" class="px-3 py-1.5 bg-slate-100 rounded mr-2 hover:bg-slate-200">Sửa</button>
                      <button @click="confirmDeleteAmenity(a)" class="px-3 py-1.5 bg-red-600 text-white rounded hover:bg-red-700">Xóa</button>
                    </td>
                  </tr>
                  <tr v-if="paginatedAmenities.length === 0">
                    <td colspan="5" class="px-3 py-8 text-center text-slate-500">Không có dữ liệu</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          
          <div class="flex items-center justify-end gap-4 px-4 py-3 bg-white border-t border-slate-200 rounded-b-xl mt-4">
            <div class="flex items-center gap-2">
              <span class="text-sm text-gray-700 whitespace-nowrap">Số dòng</span>
              <CustomSelect
                v-model="amenityItemsPerPageStr"
                :options="amenityItemsPerPageOptions.map(opt => ({ label: String(opt), value: String(opt) }))"
                class="w-20 min-w-[100px] h-8 [&>button]:h-8 [&>button]:py-1 [&>button]:text-sm"
                :direction="'up'"
              />
            </div>
            <nav v-if="amenityTotalPages > 1 && amenityItemsPerPageStr !== 'Tất cả'" aria-label="Pagination">
              <ul class="inline-flex items-center space-x-1">
                <li>
                  <button @click="prevAmenityPage" :disabled="amenityCurrentPage === 1"
                    class="w-8 h-8 flex items-center justify-center rounded border border-slate-200 text-gray-500 hover:bg-gray-100 disabled:opacity-50">
                    <i class="fas fa-chevron-left text-xs"></i>
                  </button>
                </li>
                <li v-for="page in amenityDisplayedPages" :key="page">
                  <button v-if="page !== '...'" @click="changeAmenityPage(page)"
                    :class="['w-8 h-8 flex items-center justify-center rounded font-medium',
                      amenityCurrentPage === page ? 'bg-orange-500 text-white border-orange-500' : 'text-gray-700 hover:bg-gray-100 border border-slate-200']">
                    {{ page }}
                  </button>
                  <span v-else class="w-8 h-8 flex items-center justify-center text-gray-400">...</span>
                </li>
                <li>
                  <button @click="nextAmenityPage" :disabled="amenityCurrentPage === amenityTotalPages"
                    class="w-8 h-8 flex items-center justify-center rounded border border-slate-200 text-gray-500 hover:bg-gray-100 disabled:opacity-50">
                    <i class="fas fa-chevron-right text-xs"></i>
                  </button>
                </li>
              </ul>
            </nav>
          </div>
        </div>

        <div v-if="showAmenityModal" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50" @click="closeAmenityModal">
          <div class="bg-white rounded-xl shadow-xl w-full max-w-md p-6" @click.stop>
            <h3 class="text-lg font-bold mb-4">{{ editingAmenity?.id ? 'Sửa Tiện ích' : 'Thêm Tiện ích' }}</h3>
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium mb-1">Tên</label>
                <input v-model="amenityForm.name" class="w-full border border-slate-300 rounded-md px-3 py-2" placeholder="Tên tiện ích"/>
              </div>
              <div>
                <label class="block text-sm font-medium mb-1">Icon (FontAwesome class)</label>
                <input v-model="amenityForm.icon" class="w-full border border-slate-300 rounded-md px-3 py-2" placeholder="fas fa-wifi"/>
                <div class="text-xs text-slate-400 mt-1">Ví dụ: fas fa-wifi, fas fa-parking, fas fa-swimming-pool</div>
              </div>
              <div>
                <label class="block text-sm font-medium mb-1">Trạng thái</label>
                <CustomSelect
                  v-model="amenityForm.status"
                  :options="[
                    { value: 'ACTIVE', label: 'Hoạt động' },
                    { value: 'INACTIVE', label: 'Không hoạt động' }
                  ]"
                  placeholder="Chọn trạng thái"
                />
              </div>
            </div>
            <div class="mt-6 flex justify-end gap-2">
              <button @click="closeAmenityModal" class="px-4 py-2 bg-slate-100 rounded hover:bg-slate-200">Hủy</button>
              <button @click="saveAmenity" class="px-4 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700">Lưu</button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="activeTab === 'areas'">
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-bold text-gray-700">
              Quản lý Khu vực / Địa điểm
            </h2>
            <button @click="openProvinceModal()"
              class="bg-indigo-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-indigo-700"
            >
              <i class="fas fa-plus mr-2"></i>Thêm Khu vực
            </button>
          </div>

          <div class="mb-4 flex gap-3">
            <input v-model="provinceSearch" placeholder="Tìm theo tên khu vực" class="border border-slate-300 rounded-md px-3 py-2 w-72"/>
          </div>

          <div class="overflow-x-auto">
            <div class="overflow-y-auto h-[364px]">
              <table class="min-w-[700px] w-full divide-y divide-slate-200">
                <thead class="bg-slate-100">
                  <tr>
                    <th class="sticky top-0 bg-slate-100 px-3 py-3 text-left text-xs font-bold text-slate-700 uppercase">ID</th>
                    <th class="sticky top-0 bg-slate-100 px-3 py-3 text-left text-xs font-bold text-slate-700 uppercase">Tên</th>
                    <th class="sticky top-0 bg-slate-100 px-3 py-3 text-left text-xs font-bold text-slate-700 uppercase">Ảnh</th>
                    <th class="sticky top-0 bg-slate-100 px-3 py-3 text-left text-xs font-bold text-slate-700 uppercase">Số KS</th>
                    <th class="sticky top-0 bg-slate-100 px-3 py-3 text-right text-xs font-bold text-slate-700 uppercase">Hành động</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-slate-100">
                  <tr v-for="p in paginatedProvinces" :key="p.id" class="hover:bg-slate-50">
                    <td class="px-3 py-3 text-sm text-slate-700">{{ p.id }}</td>
                    <td class="px-3 py-3 text-sm font-semibold text-slate-900">{{ p.name }}</td>
                    <td class="px-3 py-3 text-sm text-slate-700">
                      <img v-if="p.imageUrl" :src="p.imageUrl" class="h-10 w-16 object-cover rounded border"/>
                      <span v-else class="text-slate-400">(Không có)</span>
                    </td>
                    <td class="px-3 py-3 text-sm text-slate-700">{{ p.hotelCount }}</td>
                    <td class="px-3 py-3 text-sm text-right">
                      <button @click="openProvinceModal(p)" class="px-3 py-1.5 bg-slate-100 rounded mr-2 hover:bg-slate-200">Sửa</button>
                      <button @click="confirmDeleteProvince(p)" class="px-3 py-1.5 bg-red-600 text-white rounded hover:bg-red-700">Xóa</button>
                    </td>
                  </tr>
                  <tr v-if="paginatedProvinces.length === 0">
                    <td colspan="5" class="px-3 py-8 text-center text-slate-500">Không có dữ liệu</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          
          <div class="flex items-center justify-end gap-4 px-4 py-3 bg-white border-t border-slate-200 rounded-b-xl mt-4">
            <div class="flex items-center gap-2">
              <span class="text-sm text-gray-700 whitespace-nowrap">Số dòng</span>
              <CustomSelect
                v-model="provinceItemsPerPageStr"
                :options="provinceItemsPerPageOptions.map(opt => ({ label: String(opt), value: String(opt) }))"
                class="w-20 min-w-[100px] h-8 [&>button]:h-8 [&>button]:py-1 [&>button]:text-sm"
                :direction="'up'"
              />
            </div>
            <nav v-if="provinceTotalPages > 1 && provinceItemsPerPageStr !== 'Tất cả'" aria-label="Pagination">
              <ul class="inline-flex items-center space-x-1">
                <li>
                  <button @click="prevProvincePage" :disabled="provinceCurrentPage === 1"
                    class="w-8 h-8 flex items-center justify-center rounded border border-slate-200 text-gray-500 hover:bg-gray-100 disabled:opacity-50">
                    <i class="fas fa-chevron-left text-xs"></i>
                  </button>
                </li>
                <li v-for="page in provinceDisplayedPages" :key="page">
                  <button v-if="page !== '...'" @click="changeProvincePage(page)"
                    :class="['w-8 h-8 flex items-center justify-center rounded font-medium',
                      provinceCurrentPage === page ? 'bg-orange-500 text-white border-orange-500' : 'text-gray-700 hover:bg-gray-100 border border-slate-200']">
                    {{ page }}
                  </button>
                  <span v-else class="w-8 h-8 flex items-center justify-center text-gray-400">...</span>
                </li>
                <li>
                  <button @click="nextProvincePage" :disabled="provinceCurrentPage === provinceTotalPages"
                    class="w-8 h-8 flex items-center justify-center rounded border border-slate-200 text-gray-500 hover:bg-gray-100 disabled:opacity-50">
                    <i class="fas fa-chevron-right text-xs"></i>
                  </button>
                </li>
              </ul>
            </nav>
          </div>
        </div>

        <div v-if="showProvinceModal" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50" @click="closeProvinceModal">
          <div class="bg-white rounded-xl shadow-xl w-full max-w-md p-6" @click.stop>
            <h3 class="text-lg font-bold mb-4">{{ editingProvince?.id ? 'Sửa Khu vực' : 'Thêm Khu vực' }}</h3>
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium mb-1">Tên</label>
                <input v-model="provinceForm.name" class="w-full border border-slate-300 rounded-md px-3 py-2" placeholder="Tên khu vực"/>
              </div>
              <div>
                <label class="block text-sm font-medium mb-1">Ảnh</label>
                <div class="flex items-center gap-2">
                  <input ref="provinceFileInput" type="file" accept="image/*" class="hidden" @change="onProvinceFileChange" />
                  <button @click="triggerProvinceFile" class="px-3 py-2 bg-slate-100 rounded hover:bg-slate-200">Chọn file</button>
                  <span class="text-sm text-slate-600 truncate max-w-[220px]">{{ provinceForm.fileName || 'Chưa chọn file' }}</span>
                </div>
                <div class="text-xs text-slate-400 mt-1">Hoặc dán URL:</div>
                <input v-model="provinceForm.imageUrl" class="w-full border border-slate-300 rounded-md px-3 py-2 mt-1" placeholder="https://..."/>
              </div>
            </div>
            <div class="mt-6 flex justify-end gap-2">
              <button @click="closeProvinceModal" class="px-4 py-2 bg-slate-100 rounded hover:bg-slate-200">Hủy</button>
              <button @click="saveProvince" class="px-4 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700">Lưu</button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <ConfirmDialog ref="confirmDialog" />
    
    <!-- Modal từ chối khách sạn -->
    <div v-if="showRejectModal" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50" @click="closeRejectModal">
      <div class="bg-white rounded-xl shadow-xl w-full max-w-md p-6" @click.stop>
        <h3 class="text-lg font-bold mb-4 text-red-600">
          <i class="fas fa-times-circle mr-2"></i>
          Từ chối khách sạn
        </h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium mb-1">Tên khách sạn</label>
            <div class="p-3 bg-slate-50 rounded-md border">
              <strong>{{ rejectingHotel?.name }}</strong>
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium mb-1">Lý do từ chối <span class="text-red-500">*</span></label>
            <textarea 
              v-model="rejectReason" 
              class="w-full border border-slate-300 rounded-md px-3 py-2 h-24 resize-none" 
              placeholder="Nhập lý do từ chối khách sạn..."
            ></textarea>
            <div class="text-xs text-slate-400 mt-1">Lý do này sẽ được gửi đến Admin Hotel để họ biết và sửa chữa</div>
          </div>
        </div>
        <div class="mt-6 flex justify-end gap-2">
          <button @click="closeRejectModal" class="px-4 py-2 bg-slate-100 rounded hover:bg-slate-200">Hủy</button>
          <button @click="confirmRejectHotel" class="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700">
            <i class="fas fa-times mr-2"></i>Từ chối
          </button>
        </div>
      </div>
    </div>

    <!-- Modal xem chi tiết khách sạn -->
    <div v-if="showHotelDetailModal" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50" @click="closeHotelDetailModal">
      <div class="modal-content bg-white rounded-2xl shadow-xl w-full max-w-6xl max-h-[90vh] flex flex-col" @click.stop>
        <!-- Header cố định -->
        <div class="p-6 border-b border-slate-200 bg-white rounded-t-2xl sticky top-0 z-10">
          <div class="flex justify-between items-center">
            <h3 class="text-xl font-bold text-slate-800">
              <i class="fas fa-hotel mr-2 text-blue-600"></i>
              Chi tiết khách sạn: {{ selectedHotel?.name }}
            </h3>
            <button @click="closeHotelDetailModal" class="text-slate-400 hover:text-slate-600 text-2xl">
              <i class="fas fa-times"></i>
            </button>
          </div>
        </div>

        <!-- Content có thể scroll -->
        <div class="p-6 space-y-6 overflow-y-auto flex-1">
          <!-- Thông tin cơ bản -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-2">Tên khách sạn</label>
              <div class="p-3 bg-slate-50 rounded-md border text-slate-900">{{ selectedHotel?.name || 'N/A' }}</div>
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-2">Hạng sao</label>
              <div class="p-3 bg-slate-50 rounded-md border">
                <div class="flex items-center space-x-1">
                  <span class="text-slate-900">{{ selectedHotel?.starRating || 'N/A' }}</span>
                  <div class="flex space-x-0.5">
                    <i v-for="star in (selectedHotel?.starRating || 0)" :key="star"
                      class="fas fa-star text-yellow-400 text-sm"></i>
                  </div>
                </div>
              </div>
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-2">Thành phố</label>
              <div class="p-3 bg-slate-50 rounded-md border text-slate-900">{{ selectedHotel?.provinceName || 'N/A' }}</div>
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-2">Địa chỉ</label>
              <div class="p-3 bg-slate-50 rounded-md border text-slate-900">{{ selectedHotel?.address || 'N/A' }}</div>
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-2">Số điện thoại</label>
              <div class="p-3 bg-slate-50 rounded-md border text-slate-900">{{ selectedHotel?.phone || 'N/A' }}</div>
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-2">Email</label>
              <div class="p-3 bg-slate-50 rounded-md border text-slate-900">{{ selectedHotel?.email || 'N/A' }}</div>
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-2">Trạng thái</label>
              <div class="p-3 bg-slate-50 rounded-md border">
                <span v-if="selectedHotel?.status === 'ACTIVE'" class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                  <i class="fas fa-check-circle mr-1"></i>
                  Hoạt động
                </span>
                <span v-else class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800">
                  <i class="fas fa-times-circle mr-1"></i>
                  Không hoạt động
                </span>
              </div>
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-2">Trạng thái duyệt</label>
              <div class="p-3 bg-slate-50 rounded-md border">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                  <i class="fas fa-clock mr-1"></i>
                  Chờ duyệt
                </span>
              </div>
            </div>
          </div>

          <!-- Mô tả -->
          <div>
            <label class="block text-sm font-semibold text-slate-700 mb-2">Mô tả</label>
            <div class="p-4 bg-slate-50 rounded-md border min-h-[100px]">
              <div v-if="selectedHotel?.description" v-html="selectedHotel.description" class="text-slate-900"></div>
              <div v-else class="text-slate-400 italic">Không có mô tả</div>
            </div>
          </div>

          <!-- Hình ảnh khách sạn -->
          <div>
            <label class="block text-sm font-semibold text-slate-700 mb-2">Hình ảnh khách sạn</label>
            <div v-if="selectedHotel?.imageUrls && selectedHotel.imageUrls.length > 0" class="flex flex-wrap gap-3">
              <div v-for="(img, idx) in selectedHotel.imageUrls" :key="idx" class="relative">
                <img :src="img" class="w-32 h-24 object-cover rounded-lg border border-slate-200 shadow-sm" />
              </div>
            </div>
            <div v-else class="p-4 bg-slate-50 rounded-md border text-slate-400 italic">
              Không có hình ảnh
            </div>
          </div>

          <!-- Thông tin các loại phòng -->
          <div>
            <h4 class="font-semibold text-slate-800 text-lg mb-4">Thông tin các loại phòng</h4>
            <div v-if="selectedHotel?.availableRooms && selectedHotel.availableRooms.length > 0" class="space-y-4">
              <div v-for="(room, idx) in selectedHotel.availableRooms" :key="idx" class="border border-slate-200 rounded-xl p-4 bg-slate-50">
                <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                  <div>
                    <label class="block text-sm font-semibold text-slate-700 mb-1">Loại phòng</label>
                    <div class="p-2 bg-white rounded border text-slate-900">{{ room.roomType || 'N/A' }}</div>
                  </div>
                  <div>
                    <label class="block text-sm font-semibold text-slate-700 mb-1">Giường</label>
                    <div class="p-2 bg-white rounded border text-slate-900">{{ room.bedType || 'N/A' }}</div>
                  </div>
                  <div>
                    <label class="block text-sm font-semibold text-slate-700 mb-1">Diện tích (m²)</label>
                    <div class="p-2 bg-white rounded border text-slate-900">{{ room.roomArea || 'N/A' }}</div>
                  </div>
                </div>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                  <div>
                    <label class="block text-sm font-semibold text-slate-700 mb-1">Số lượng phòng</label>
                    <div class="p-2 bg-white rounded border text-slate-900">{{ room.roomQuantity || 'N/A' }}</div>
                  </div>
                  <div class="grid grid-cols-2 gap-4">
                    <div>
                      <label class="block text-sm font-semibold text-slate-700 mb-1">Người lớn</label>
                      <div class="p-2 bg-white rounded border text-slate-900">{{ room.maxAdults || 'N/A' }}</div>
                    </div>
                    <div>
                      <label class="block text-sm font-semibold text-slate-700 mb-1">Trẻ em</label>
                      <div class="p-2 bg-white rounded border text-slate-900">{{ room.maxChildren || 'N/A' }}</div>
                    </div>
                  </div>
                </div>

                <!-- Tiện ích phòng -->
                <div class="mb-4">
                  <label class="block text-sm font-semibold text-slate-700 mb-2">Tiện ích phòng</label>
                  <div v-if="room.amenities && room.amenities.length > 0" class="flex flex-wrap gap-2">
                    <span v-for="amenity in room.amenities" :key="amenity.id" class="flex items-center bg-blue-100 text-blue-700 px-2 py-1 rounded-full text-xs font-medium">
                      <i v-if="amenity.icon" :class="amenity.icon + ' mr-1 text-xs'"></i>
                      {{ amenity.name }}
                    </span>
                  </div>
                  <div v-else class="text-slate-400 italic">Không có tiện ích</div>
                </div>

                <!-- Hình ảnh phòng -->
                <div class="mb-4">
                  <label class="block text-sm font-semibold text-slate-700 mb-2">Hình ảnh phòng</label>
                  <div v-if="room.imageUrls && room.imageUrls.length > 0" class="flex flex-wrap gap-2">
                    <div v-for="(img, imgIdx) in room.imageUrls" :key="imgIdx" class="relative">
                      <img :src="img" class="w-20 h-20 object-cover rounded-lg border border-slate-200 shadow-sm" />
                    </div>
                  </div>
                  <div v-else class="text-slate-400 italic">Không có hình ảnh</div>
                </div>

                                 <!-- Gói phòng -->
                 <div>
                   <label class="block text-sm font-semibold text-slate-700 mb-2">Gói phòng</label>
                   <div v-if="room.availableVariants && room.availableVariants.length > 0" class="space-y-3">
                     <div v-for="(variant, vIdx) in room.availableVariants" :key="vIdx" class="bg-white rounded-lg p-3 border border-slate-200">
                                              <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
                         <div>
                           <label class="block text-xs font-semibold text-slate-700 mb-1">Tên gói</label>
                           <div class="text-sm text-slate-900">{{ variant.variantName || 'N/A' }}</div>
                         </div>
                         <div>
                           <label class="block text-xs font-semibold text-slate-700 mb-1">Giá</label>
                           <div class="text-sm font-bold text-green-600">{{ formatCurrency(variant.price) }} VND</div>
                         </div>
                       </div>
                       
                                               <!-- Giảm giá -->
                        <div v-if="variant.discountType && variant.discountValue" class="mt-2">
                          <div class="flex items-center gap-2">
                            <span class="text-xs font-semibold text-slate-700">Giảm giá:</span>
                            <span class="text-xs font-bold text-red-600">
                              {{ variant.discountType === 'percent' ? variant.discountValue + '%' : formatCurrency(variant.discountValue) + ' VND' }}
                            </span>
                          </div>
                          <div class="text-green-700 font-bold text-sm mt-1">
                            Giá sau giảm: {{ formatCurrency(getDiscountedPrice(variant)) }} VND
                          </div>
                        </div>
                      <div class="flex gap-4 mt-2 text-xs text-slate-600">
                        <span v-if="variant.hasBreakfast" class="flex items-center">
                          <i class="fas fa-check-circle text-green-500 mr-1"></i>Bữa sáng
                        </span>
                        <span v-if="variant.cancellable" class="flex items-center">
                          <i class="fas fa-check-circle text-green-500 mr-1"></i>Hủy miễn phí
                        </span>
                        <span v-if="variant.payAtHotel" class="flex items-center">
                          <i class="fas fa-check-circle text-green-500 mr-1"></i>Thanh toán tại KS
                        </span>
                      </div>
                      <div v-if="variant.taxAndFeeAmount > 0" class="mt-2 text-xs text-slate-600">
                        Thuế/Phí: {{ formatCurrency(variant.taxAndFeeAmount) }} VND
                      </div>
                    </div>
                  </div>
                  <div v-else class="text-slate-400 italic">Không có gói phòng</div>
                </div>
              </div>
            </div>
            <div v-else class="p-4 bg-slate-50 rounded-md border text-slate-400 italic">
              Không có thông tin phòng
            </div>
          </div>
        </div>

        <!-- Footer cố định -->
        <div class="p-6 border-t border-slate-200 bg-slate-50 rounded-b-2xl sticky bottom-0 z-10">
          <div class="flex justify-end">
            <button @click="closeHotelDetailModal" class="px-6 py-2 bg-slate-600 text-white rounded-lg hover:bg-slate-700 transition-colors">
              Đóng
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { provinceAdminApi, amenityAdminApi, hotelAdminApi } from '@/api/adminApi';
import ConfirmDialog from '@/components/ConfirmDialog.vue';
import CustomSelect from '@/components/CustomSelect.vue';

const activeTab = ref("pending");
const tabs = ref([
  { id: "pending", name: "Chờ Phê duyệt", icon: "fas fa-hourglass-half" },
  { id: "amenities", name: "Quản lý Tiện ích", icon: "fas fa-swimming-pool" },
  { id: "areas", name: "Quản lý Khu vực", icon: "fas fa-map-marked-alt" },
]);

// Pending hotels management
const pendingHotels = ref([]);
const pendingHotelSearch = ref('');
const pendingHotelFilterProvince = ref('');
const filteredPendingHotels = computed(() => {
  let result = pendingHotels.value;
  
  // Filter by search
  if (pendingHotelSearch.value.trim()) {
    const searchTerm = pendingHotelSearch.value.trim().toLowerCase();
    result = result.filter(h => 
      (h.name && h.name.toLowerCase().includes(searchTerm)) ||
      (h.provinceName && h.provinceName.toLowerCase().includes(searchTerm))
    );
  }
  
  // Filter by province
  if (pendingHotelFilterProvince.value) {
    result = result.filter(h => h.provinceId == pendingHotelFilterProvince.value);
  }
  
  return result;
});

// Pagination for pending hotels
const pendingHotelItemsPerPageOptions = ref([5, 10, 20, 50, 'Tất cả']);
const pendingHotelItemsPerPageStr = ref('5');
const pendingHotelCurrentPage = ref(1);
const pendingHotelTotalPages = computed(() => {
  const total = filteredPendingHotels.value.length;
  const itemsPerPage = parseInt(pendingHotelItemsPerPageStr.value);
  if (itemsPerPage === 0) return 1;
  return Math.ceil(total / itemsPerPage);
});

const paginatedPendingHotels = computed(() => {
  const itemsPerPage = pendingHotelItemsPerPageStr.value === 'Tất cả' ? 0 : parseInt(pendingHotelItemsPerPageStr.value);
  
  if (itemsPerPage === 0) {
    return filteredPendingHotels.value;
  }
  
  const start = (pendingHotelCurrentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredPendingHotels.value.slice(start, end);
});

const pendingHotelDisplayedPages = computed(() => {
  const totalPages = pendingHotelTotalPages.value;
  const currentPage = pendingHotelCurrentPage.value;
  const pages = [];

  if (totalPages <= 5) {
    for (let i = 1; i <= totalPages; i++) {
      pages.push(i);
    }
  } else {
    if (currentPage <= 3) {
      pages.push(1, 2, 3, '...', totalPages);
    } else if (currentPage >= totalPages - 2) {
      pages.push(1, '...', totalPages - 2, totalPages - 1, totalPages);
    } else {
      pages.push(1, '...', currentPage - 1, currentPage, currentPage + 1, '...', totalPages);
    }
  }
  return pages;
});

function changePendingHotelPage(page) {
  if (page === '...') return;
  pendingHotelCurrentPage.value = parseInt(page);
}

function prevPendingHotelPage() {
  if (pendingHotelCurrentPage.value > 1) {
    pendingHotelCurrentPage.value--;
  }
}

function nextPendingHotelPage() {
  if (pendingHotelCurrentPage.value < pendingHotelTotalPages.value) {
    pendingHotelCurrentPage.value++;
  }
}







async function fetchPendingHotels() {
  try {
    console.log('Fetching pending hotels...');
    const params = {
      keyword: pendingHotelSearch.value,
      provinceId: pendingHotelFilterProvince.value || undefined,
      approvalStatus: 'PENDING',
      page: 0,
      size: 1000,
    };
    console.log('Pending hotel search params:', params);
    
    const response = await hotelAdminApi.searchHotels(params);
    console.log('Pending hotel response:', response);
    
    if (response.data && response.data.data) {
      const hotelData = response.data.data.content || response.data.data.items || response.data.data;
      console.log('Pending hotel data:', hotelData);
      
      if (Array.isArray(hotelData)) {
        pendingHotels.value = hotelData;
      } else if (hotelData && Array.isArray(hotelData.content)) {
        pendingHotels.value = hotelData.content;
      } else {
        pendingHotels.value = [];
      }
    } else {
      console.log('No pending hotel data in response');
      pendingHotels.value = [];
    }
    
    console.log('Final pending hotels array:', pendingHotels.value);
  } catch (error) {
    console.error('Error fetching pending hotels:', error);
    pendingHotels.value = [];
  }
}



function formatDateTime(dt) {
  if (!dt) return '';
  const d = new Date(dt);
  return d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' }) + ' ' + d.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
}

// Pending hotel actions
async function viewPendingHotelDetail(hotel) {
  try {
    // Lấy thông tin chi tiết khách sạn
    const response = await hotelAdminApi.getHotelById(hotel.id);
    if (response.data && response.data.data) {
      selectedHotel.value = response.data.data;
      showHotelDetailModal.value = true;
    } else {
      window.$toast && window.$toast('Không thể lấy thông tin chi tiết khách sạn', 'error');
    }
  } catch (error) {
    console.error('Error fetching hotel details:', error);
    window.$toast && window.$toast('Không thể lấy thông tin chi tiết khách sạn', 'error');
  }
}

async function approveHotel(hotel) {
  try {
    const ok = await confirmDialog.value?.showDialog({
      type: 'success',
      title: 'Xác nhận duyệt khách sạn',
      message: `Bạn có chắc chắn muốn duyệt khách sạn "${hotel.name}" không?`,
      confirmText: 'Duyệt',
      cancelText: 'Hủy'
    });
    
    if (!ok) return;
    
    await hotelAdminApi.approveHotel(hotel.id);
    window.$toast && window.$toast('Duyệt khách sạn thành công!', 'success');
    await fetchPendingHotels();
  } catch (e) {
    window.$toast && window.$toast('Duyệt khách sạn thất bại', 'error');
  }
}

function rejectHotel(hotel) {
  rejectingHotel.value = hotel;
  rejectReason.value = '';
  showRejectModal.value = true;
}

function closeRejectModal() {
  showRejectModal.value = false;
  rejectingHotel.value = null;
  rejectReason.value = '';
}

async function confirmRejectHotel() {
  if (!rejectReason.value.trim()) {
    window.$toast && window.$toast('Vui lòng nhập lý do từ chối', 'warning');
    return;
  }
  
  try {
    await hotelAdminApi.rejectHotel(rejectingHotel.value.id, { reason: rejectReason.value });
    window.$toast && window.$toast('Từ chối khách sạn thành công!', 'success');
    closeRejectModal();
    await fetchPendingHotels();
  } catch (e) {
    window.$toast && window.$toast('Từ chối khách sạn thất bại', 'error');
  }
}

function closeHotelDetailModal() {
  showHotelDetailModal.value = false;
  selectedHotel.value = null;
}

function formatCurrency(value) {
  if (value == null || value === undefined) return 'N/A';
  return new Intl.NumberFormat('vi-VN').format(value);
}

function getDiscountedPrice(variant) {
  if (!variant.discountType || !variant.discountValue) return variant.price;
  
  if (variant.discountType === 'amount') {
    return Math.max(0, variant.price - variant.discountValue);
  } else if (variant.discountType === 'percent') {
    return Math.max(0, variant.price * (1 - variant.discountValue / 100));
  }
  
  return variant.price;
}

const provinces = ref([]);
const provinceSearch = ref('');
const filteredProvinces = computed(() => {
  const q = provinceSearch.value.trim().toLowerCase();
  if (!q) return provinces.value;
  return provinces.value.filter(p => (p.name || '').toLowerCase().includes(q));
});

const showProvinceModal = ref(false);
const editingProvince = ref(null);
const provinceForm = ref({ name: '', imageUrl: '', file: null, fileName: '' });
const provinceFileInput = ref(null);
const confirmDialog = ref(null);

// Reject hotel modal
const showRejectModal = ref(false);
const rejectingHotel = ref(null);
const rejectReason = ref('');

// Hotel detail modal
const showHotelDetailModal = ref(false);
const selectedHotel = ref(null);

async function fetchProvinces() {
  try {
    const res = await provinceAdminApi.getAll();
    provinces.value = res.data?.data || res.data || [];
  } catch (e) {
    provinces.value = [];
    window.$toast && window.$toast('Không tải được danh sách khu vực', 'error');
  }
}

function openProvinceModal(p = null) {
  editingProvince.value = p ? { ...p } : null;
  provinceForm.value = {
    name: p?.name || '',
    imageUrl: p?.imageUrl || '',
    file: null,
    fileName: ''
  };
  showProvinceModal.value = true;
}

function closeProvinceModal() {
  showProvinceModal.value = false;
  editingProvince.value = null;
}

async function saveProvince() {
  const name = provinceForm.value.name?.trim();
  const imageUrl = provinceForm.value.imageUrl?.trim();
  const file = provinceForm.value.file;
  const payload = { name, imageUrl };
  if (!payload.name) {
    window.$toast && window.$toast('Vui lòng nhập tên khu vực', 'warning');
    return;
  }
  try {
    if (editingProvince.value?.id) {
      await provinceAdminApi.updateMultipart(editingProvince.value.id, { name, file, imageUrl });
      window.$toast && window.$toast('Cập nhật khu vực thành công!', 'success');
    } else {
      await provinceAdminApi.createMultipart({ name, file, imageUrl });
      window.$toast && window.$toast('Thêm khu vực thành công!', 'success');
    }
    closeProvinceModal();
    await fetchProvinces();
  } catch (e) {
    window.$toast && window.$toast('Lưu khu vực thất bại', 'error');
  }
}

function onProvinceFileChange(e) {
  const f = e.target.files?.[0];
  if (!f) return;
  provinceForm.value.file = f;
  provinceForm.value.fileName = f.name;
}

function triggerProvinceFile() {
  provinceFileInput.value && provinceFileInput.value.click();
}

async function confirmDeleteProvince(p) {
  const ok = await confirmDialog.value?.showDialog({
    type: 'danger',
    title: 'Xác nhận xóa khu vực',
    message: `Bạn có chắc chắn muốn xóa "${p.name}" không?`,
    confirmText: 'Xóa',
    cancelText: 'Hủy'
  });
  if (!ok) return;
  try {
    await provinceAdminApi.remove(p.id);
    window.$toast && window.$toast('Xóa khu vực thành công!', 'success');
    await fetchProvinces();
  } catch (e) {
    window.$toast && window.$toast('Xóa khu vực thất bại', 'error');
  }
}

// Amenities
const amenities = ref([]);
const amenitySearch = ref('');
const filteredAmenities = computed(() => {
  const q = amenitySearch.value.trim().toLowerCase();
  if (!q) return amenities.value;
  return amenities.value.filter(a => (a.name || '').toLowerCase().includes(q));
});

const showAmenityModal = ref(false);
const editingAmenity = ref(null);
const amenityForm = ref({ name: '', icon: '', status: 'ACTIVE' });

async function fetchAmenities() {
  try {
    const res = await amenityAdminApi.getAll();
    amenities.value = res.data?.data || res.data || [];
  } catch (e) {
    amenities.value = [];
    window.$toast && window.$toast('Không tải được danh sách tiện ích', 'error');
  }
}

function openAmenityModal(a = null) {
  editingAmenity.value = a ? { ...a } : null;
  amenityForm.value = {
    name: a?.name || '',
    icon: a?.icon || '',
    status: a?.status || 'ACTIVE'
  };
  showAmenityModal.value = true;
}

function closeAmenityModal() {
  showAmenityModal.value = false;
  editingAmenity.value = null;
}

async function saveAmenity() {
  const name = amenityForm.value.name?.trim();
  const icon = amenityForm.value.icon?.trim();
  const status = amenityForm.value.status;
  const payload = { name, icon, status };
  if (!payload.name) {
    window.$toast && window.$toast('Vui lòng nhập tên tiện ích', 'warning');
    return;
  }
  try {
    if (editingAmenity.value?.id) {
      await amenityAdminApi.update(editingAmenity.value.id, payload);
      window.$toast && window.$toast('Cập nhật tiện ích thành công!', 'success');
    } else {
      await amenityAdminApi.create(payload);
      window.$toast && window.$toast('Thêm tiện ích thành công!', 'success');
    }
    closeAmenityModal();
    await fetchAmenities();
  } catch (e) {
    window.$toast && window.$toast('Lưu tiện ích thất bại', 'error');
  }
}

async function confirmDeleteAmenity(a) {
  const ok = await confirmDialog.value?.showDialog({
    type: 'danger',
    title: 'Xác nhận xóa tiện ích',
    message: `Bạn có chắc chắn muốn xóa "${a.name}" không?`,
    confirmText: 'Xóa',
    cancelText: 'Hủy'
  });
  if (!ok) return;
  try {
    await amenityAdminApi.remove(a.id);
    window.$toast && window.$toast('Xóa tiện ích thành công!', 'success');
    await fetchAmenities();
  } catch (e) {
    window.$toast && window.$toast('Xóa tiện ích thất bại', 'error');
  }
}

const provinceItemsPerPageOptions = ref([5, 10, 20, 50, 'Tất cả']);
const provinceItemsPerPageStr = ref('5');
const provinceCurrentPage = ref(1);
const provinceTotalPages = computed(() => {
  const total = filteredProvinces.value.length;
  const itemsPerPage = parseInt(provinceItemsPerPageStr.value);
  if (itemsPerPage === 0) return 1; // Handle 'Tất cả' case
  return Math.ceil(total / itemsPerPage);
});

const paginatedProvinces = computed(() => {
  const itemsPerPage = provinceItemsPerPageStr.value === 'Tất cả' ? 0 : parseInt(provinceItemsPerPageStr.value);
  
  if (itemsPerPage === 0) {
    return filteredProvinces.value; // Trả về tất cả
  }
  
  const start = (provinceCurrentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredProvinces.value.slice(start, end);
});

const provinceDisplayedPages = computed(() => {
  const totalPages = provinceTotalPages.value;
  const currentPage = provinceCurrentPage.value;
  const pages = [];

  if (totalPages <= 5) {
    for (let i = 1; i <= totalPages; i++) {
      pages.push(i);
    }
  } else {
    if (currentPage <= 3) {
      pages.push(1, 2, 3, '...', totalPages);
    } else if (currentPage >= totalPages - 2) {
      pages.push(1, '...', totalPages - 2, totalPages - 1, totalPages);
    } else {
      pages.push(1, '...', currentPage - 1, currentPage, currentPage + 1, '...', totalPages);
    }
  }
  return pages;
});

function changeProvincePage(page) {
  if (page === '...') return;
  provinceCurrentPage.value = parseInt(page);
}

function prevProvincePage() {
  if (provinceCurrentPage.value > 1) {
    provinceCurrentPage.value--;
  }
}

function nextProvincePage() {
  if (provinceCurrentPage.value < provinceTotalPages.value) {
    provinceCurrentPage.value++;
  }
}

const amenityItemsPerPageOptions = ref([5, 10, 20, 50, 'Tất cả']);
const amenityItemsPerPageStr = ref('5');
const amenityCurrentPage = ref(1);
const amenityTotalPages = computed(() => {
  const total = filteredAmenities.value.length;
  const itemsPerPage = parseInt(amenityItemsPerPageStr.value);
  if (itemsPerPage === 0) return 1; // Handle 'Tất cả' case
  return Math.ceil(total / itemsPerPage);
});

const paginatedAmenities = computed(() => {
  const itemsPerPage = amenityItemsPerPageStr.value === 'Tất cả' ? 0 : parseInt(amenityItemsPerPageStr.value);
  
  if (itemsPerPage === 0) {
    return filteredAmenities.value; // Trả về tất cả
  }
  
  const start = (amenityCurrentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredAmenities.value.slice(start, end);
});

const amenityDisplayedPages = computed(() => {
  const totalPages = amenityTotalPages.value;
  const currentPage = amenityCurrentPage.value;
  const pages = [];

  if (totalPages <= 5) {
    for (let i = 1; i <= totalPages; i++) {
      pages.push(i);
    }
  } else {
    if (currentPage <= 3) {
      pages.push(1, 2, 3, '...', totalPages);
    } else if (currentPage >= totalPages - 2) {
      pages.push(1, '...', totalPages - 2, totalPages - 1, totalPages);
    } else {
      pages.push(1, '...', currentPage - 1, currentPage, currentPage + 1, '...', totalPages);
    }
  }
  return pages;
});

function changeAmenityPage(page) {
  if (page === '...') return;
  amenityCurrentPage.value = parseInt(page);
}

function prevAmenityPage() {
  if (amenityCurrentPage.value > 1) {
    amenityCurrentPage.value--;
  }
}

function nextAmenityPage() {
  if (amenityCurrentPage.value < amenityTotalPages.value) {
    amenityCurrentPage.value++;
  }
}

onMounted(() => {
  if (activeTab.value === 'areas') fetchProvinces();
  if (activeTab.value === 'amenities') fetchAmenities();
  if (activeTab.value === 'pending') fetchPendingHotels();
  
  // Thêm event listener để đóng modal khi click bên ngoài
  document.addEventListener('click', (e) => {
    if (showHotelDetailModal.value && !e.target.closest('.modal-content')) {
      closeHotelDetailModal();
    }
  });
});

watch(activeTab, (val) => {
  if (val === 'areas') {
    if (!provinces.value || provinces.value.length === 0) {
      fetchProvinces();
    }
  }
  if (val === 'amenities') {
    if (!amenities.value || amenities.value.length === 0) {
      fetchAmenities();
    }
  }

  if (val === 'pending') {
    if (!pendingHotels.value || pendingHotels.value.length === 0) {
      fetchPendingHotels();
    }
  }
});

watch(provinceItemsPerPageStr, () => {
  provinceCurrentPage.value = 1;
});

// Watch for search changes to reset pagination
watch(provinceSearch, () => {
  provinceCurrentPage.value = 1;
});



// Watch for pending hotel search and filter changes
watch([pendingHotelSearch, pendingHotelFilterProvince], () => {
  pendingHotelCurrentPage.value = 1;
  fetchPendingHotels();
});



// Watch for pending hotel pagination changes
watch(pendingHotelItemsPerPageStr, () => {
  pendingHotelCurrentPage.value = 1;
});

watch(amenityItemsPerPageStr, () => {
  amenityCurrentPage.value = 1;
});

watch(amenitySearch, () => {
  amenityCurrentPage.value = 1;
});
</script>



