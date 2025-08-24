<template>
  <div class="w-full p-6">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-slate-800 mb-4">Danh sách booking</h1>
             <div class="flex flex-col sm:flex-row items-center justify-between gap-2 mb-4">
         <div class="flex flex-col sm:flex-row items-center gap-3 w-full sm:w-auto">
           <div class="relative w-full sm:w-[300px]">
             <input type="text" v-model="searchQuery" placeholder="Tìm theo tên khách hàng hoặc khách sạn"
               class="w-full sm:w-[350px] pl-10 pr-4 py-2 h-12 text-base border border-slate-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-slate-900" />
             <i class="fas fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400"></i>
           </div>

           <div ref="filterDropdownContainer" class="relative w-full sm:w-auto ml-13">
             <button @click="toggleFilterDropdown"
               class="bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 px-4 py-2 rounded-md shadow-sm transition-colors duration-200 flex items-center justify-center h-12 text-base">
               <i class="fas fa-filter mr-2"></i>
               <span>Bộ lọc</span>
               <i class="fas ml-2"
                 :class="{ 'fa-chevron-up': showFilterDropdown, 'fa-chevron-down': !showFilterDropdown }"></i>
             </button>

             <div v-if="showFilterDropdown"
               class="origin-top-right absolute mt-2 w-full sm:w-80 rounded-xl shadow-xl bg-white focus:outline-none z-20 border border-slate-200 flex flex-col"
               style="max-height: calc(100vh - 12rem);">

               <div class="p-5 pb-4 border-b border-slate-100 flex-shrink-0">
                 <h3 class="text-lg font-bold text-slate-800">Tùy chọn lọc</h3>
               </div>

               <div class="p-5 overflow-y-auto">
                 <div class="mb-5">
                   <label class="block text-sm font-semibold text-slate-700 mb-2">Khoảng giá:</label>
                   <div class="flex gap-2">
                     <div class="flex-1">
                       <label class="block text-xs font-semibold text-slate-700 mb-1">Giá tối thiểu</label>
                       <div class="flex">
                         <input type="number" v-model="tempFilterPriceMin" min="0" step="100000"
                           class="w-full border border-slate-300 rounded-l-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                           placeholder="0" />
                         <div class="bg-slate-100 border border-slate-300 border-l-0 rounded-r-md px-3 py-2 text-sm text-slate-600 flex items-center">
                           đ
                         </div>
                       </div>
                     </div>
                     <div class="flex-1">
                       <label class="block text-xs font-semibold text-slate-700 mb-1">Giá tối đa</label>
                       <div class="flex">
                         <input type="number" v-model="tempFilterPriceMax" min="0" step="100000"
                           class="w-full border border-slate-300 rounded-l-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                           placeholder="0" />
                         <div class="bg-slate-100 border border-slate-300 border-l-0 rounded-r-md px-3 py-2 text-sm text-slate-600 flex items-center">
                           đ
                         </div>
                       </div>
                     </div>
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
                       <input type="date" v-model="tempFilterCreatedAtFrom"
                         class="w-full border border-slate-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                         placeholder="dd/mm/yyyy" />
                     </div>
                     <div>
                       <label class="block text-xs font-semibold text-slate-700 mb-1">Đến ngày</label>
                       <input type="date" v-model="tempFilterCreatedAtTo"
                         class="w-full border border-slate-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                         placeholder="dd/mm/yyyy" />
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
           
           <div class="w-full sm:w-[170px]">
             <CustomSelect 
               v-model="statusFilter" 
               :options="statusFilterOptions" 
               placeholder="Tất cả trạng thái"
               class="w-full h-12 [&>button]:h-12 [&>button]:py-3 [&>button]:text-sm"
               :direction="'down'" 
             />
           </div>
           
           <div class="w-full sm:w-[120px]">
             <CustomSelect 
               v-model="sortFilter" 
               :options="sortFilterOptions" 
               placeholder="Sắp xếp theo"
               class="w-full h-12 [&>button]:h-12 [&>button]:py-3 [&>button]:text-sm"
               :direction="'down'" 
             />
           </div>
         </div>
         
         <div class="flex items-center gap-2 bg-slate-100 rounded-lg p-1">
           <button @click="viewMode = 'table'" 
             :class="['p-2 rounded-md transition-all duration-200', 
               viewMode === 'table' ? 'bg-white shadow-sm text-blue-600' : 'text-slate-600 hover:text-slate-800']">
             <i class="fas fa-list text-sm"></i>
           </button>
           <button @click="viewMode = 'grid'" 
             :class="['p-2 rounded-md transition-all duration-200', 
               viewMode === 'grid' ? 'bg-white shadow-sm text-blue-600' : 'text-slate-600 hover:text-slate-800']">
             <i class="fas fa-th-large text-sm"></i>
           </button>
         </div>
       </div>
    </div>
    <div class="mb-8">
      <div v-if="viewMode === 'table'" class="bg-white rounded-xl shadow-lg border border-slate-200">
        <div class="overflow-x-auto">
          <div class="overflow-y-auto h-[412px]">
            <table class="min-w-[1000px] w-full divide-y divide-slate-200">
              <thead class="bg-slate-100 sticky top-0 z-10">
                                 <tr>
                   <th class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider bg-slate-100">STT</th>
                   <th class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider bg-slate-100">Mã đơn hàng</th>
                   <th class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider bg-slate-100">Tên khách hàng</th>
                   <th class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider bg-slate-100">Tên khách sạn</th>
                   <th class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider bg-slate-100">Ngày đặt phòng</th>
                   <th class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider bg-slate-100">Trạng thái</th>
                   <th class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider bg-slate-100">Tổng tiền</th>
                   <th class="px-3 py-4 text-right text-xs font-bold text-slate-700 uppercase tracking-wider bg-slate-100">Hành động</th>
                 </tr>
              </thead>
              <tbody class="bg-white divide-y divide-slate-100">
                                 <tr v-for="(b, index) in paginatedBookings" :key="b.id" 
                   class="hover:bg-slate-50 cursor-pointer"
                   @click="showBookingDetail(b)">
                   <td class="px-3 py-4 whitespace-nowrap text-sm text-slate-900">{{ (currentPage - 1) * itemsPerPage + index + 1 }}</td>
                   <td class="px-3 py-4 whitespace-nowrap text-sm font-medium text-slate-900">{{ b.orderId || '--' }}</td>
                   <td class="px-3 py-4 whitespace-nowrap text-sm font-medium text-slate-900">{{ b.customerName }}</td>
                   <td class="px-3 py-4 whitespace-nowrap text-sm text-slate-700">{{ b.hotelName }}</td>
                   <td class="px-3 py-4 whitespace-nowrap text-sm text-slate-600">{{ formatDateTime(b.createdAt) }}</td>
                   <td class="px-3 py-4 whitespace-nowrap">
                     <span :class="statusClass(b.status) + ' px-3 py-1 rounded-full font-semibold text-xs'">{{ statusLabel(b.status) }}</span>
                   </td>
                   <td class="px-3 py-4 whitespace-nowrap text-sm font-bold text-green-600">{{ formatCurrency(b.totalPrice) }} VND</td>
                  <td class="px-3 py-4 whitespace-nowrap text-right align-middle">
                    <div class="relative inline-block text-left flex items-center justify-end h-full">
                      <button :ref="el => setDropdownBtnRef(el, b.id)" @click.stop="toggleDropdown(b.id)" type="button"
                        class="inline-flex justify-center items-center w-10 h-10 rounded-lg text-slate-500 hover:text-slate-700 hover:bg-slate-100 focus:outline-none transition-all duration-200 shadow-sm">
                        <i class="fas fa-ellipsis-v flex items-center justify-center text-sm"></i>
                      </button>
                      <teleport to="body">
                        <div v-if="activeDropdown === b.id"
                          :style="{ position: 'absolute', top: dropdownMenuPosition.top + 'px', left: dropdownMenuPosition.left + 'px', right: 'auto', zIndex: 9999 }"
                          class="min-w-40 bg-white border border-slate-200 rounded-lg shadow-lg">
                                                     <button class="block w-full text-left px-4 py-2 hover:bg-blue-50"
                             @click.stop="viewBookingDetail(b)"><i class="fas fa-eye mr-2"></i>Xem chi tiết</button>
                           <button class="block w-full text-left px-4 py-2 hover:bg-green-50 text-green-600"
                             @click.stop="exportInvoice(b)"><i class="fas fa-file-invoice mr-2"></i>Xuất hóa đơn</button>
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
            <CustomSelect v-model="itemsPerPageStr" :options="itemsPerPageOptions.map(opt => ({ label: String(opt), value: String(opt) }))" class="w-20 min-w-[100px] h-8 [&>button]:h-8 [&>button]:py-1 [&>button]:text-sm" :direction="'up'" />
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

      <div v-else-if="viewMode === 'grid'" class="overflow-y-auto h-[500px]">
        <div v-if="paginatedBookings.length === 0" class="text-center py-12">
          <i class="fas fa-calendar-times text-4xl text-slate-300 mb-4"></i>
          <p class="text-lg font-medium text-slate-500">Không tìm thấy booking nào</p>
          <p class="text-sm text-slate-400">Thử thay đổi bộ lọc hoặc tìm kiếm khác</p>
        </div>
        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 mt-3 mb-3">
          <div v-for="(b, index) in paginatedBookings" :key="b.id" 
            class="bg-white rounded-xl shadow-lg border border-slate-200 hover:shadow-xl transition-all duration-300 hover:-translate-y-1 cursor-pointer"
            @click="showBookingDetail(b)">
            <div class="p-6">
               <div class="flex items-center justify-between mb-4">
                 <span class="text-sm font-medium text-slate-500">#{{ index + 1 }}</span>
                 <div class="flex items-center gap-2">
                   <span :class="statusClass(b.status) + ' px-3 py-1 rounded-full font-semibold text-xs'">{{ statusLabel(b.status) }}</span>
                   <div class="relative">
                                           <button @click.stop="toggleGridDropdown(b.id)" 
                        :data-grid-dropdown="b.id"
                        class="inline-flex justify-center items-center w-8 h-8 rounded-lg text-slate-500 hover:text-slate-700 hover:bg-slate-100 focus:outline-none transition-all duration-200">
                        <i class="fas fa-ellipsis-v flex items-center justify-center text-sm"></i>
                      </button>
                     <div v-if="activeGridDropdown === b.id" 
                       class="grid-dropdown-menu absolute right-0 top-full mt-1 min-w-40 bg-white border border-slate-200 rounded-lg shadow-lg z-50">
                       <button class="block w-full text-left px-4 py-2 hover:bg-blue-50"
                         @click.stop="viewBookingDetail(b)"><i class="fas fa-eye mr-2"></i>Xem chi tiết</button>
                       <button class="block w-full text-left px-4 py-2 hover:bg-green-50 text-green-600"
                         @click.stop="exportInvoice(b)"><i class="fas fa-file-invoice mr-2"></i>Xuất hóa đơn</button>
                     </div>
                   </div>
                 </div>
               </div>

                             <div class="mb-3">
                 <div class="flex items-center justify-between">
                   <span class="text-xs font-medium text-slate-500 uppercase tracking-wide">Mã đơn hàng:</span>
                   <span class="text-sm font-medium text-slate-900">{{ b.orderId || '--' }}</span>
                 </div>
               </div>
               
               <div class="mb-3">
                 <div class="flex items-center justify-between">
                   <span class="text-xs font-medium text-slate-500 uppercase tracking-wide">Tên khách hàng:</span>
                   <span class="text-sm font-semibold text-slate-900">{{ b.customerName }}</span>
                 </div>
               </div>
               
               <div class="mb-3">
                 <div class="flex items-center justify-between">
                   <span class="text-xs font-medium text-slate-500 uppercase tracking-wide">Tên khách sạn:</span>
                   <span class="text-sm font-medium text-slate-700">{{ b.hotelName }}</span>
                 </div>
               </div>
               
               <div class="mb-3">
                 <div class="flex items-center justify-between">
                   <span class="text-xs font-medium text-slate-500 uppercase tracking-wide">Ngày đặt phòng:</span>
                   <span class="text-sm text-slate-600">{{ formatDateTime(b.createdAt) }}</span>
                 </div>
               </div>
              
              <div class="border-t border-slate-200 pt-3">
                <div class="flex items-center justify-between">
                  <span class="text-xs font-medium text-slate-500 uppercase tracking-wide">Tổng tiền:</span>
                  <span class="font-bold text-base text-green-600">{{ formatCurrency(b.totalPrice) }} VND</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
     <div v-if="showDetailModal" class="fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center z-50 p-4" @click="closeBookingDetail">
       <div class="bg-white rounded-3xl shadow-2xl max-w-5xl w-full max-h-[90vh] overflow-hidden" @click.stop>
         <div class="flex items-center justify-between p-6 border-b border-slate-200 bg-white sticky top-0 z-10">
           <h2 class="text-xl font-bold text-slate-800">Chi tiết đặt phòng</h2>
           <button @click="closeBookingDetail" class="text-slate-400 hover:text-slate-600 transition-colors">
             <i class="fas fa-times text-xl"></i>
           </button>
         </div>
         
         <div class="overflow-y-auto max-h-[calc(90vh-120px)]" v-if="selectedBooking">
           <div class="p-8">
             <div class="flex items-center justify-between mb-8">
               <div>
                 <h3 class="text-2xl font-bold text-slate-800 mb-2">Booking #{{ selectedBooking.id }}</h3>
                 <p class="text-sm text-slate-600">{{ formatDateTime(selectedBooking.createdAt) }}</p>
               </div>
               <span :class="statusClass(selectedBooking.status) + ' px-4 py-2 rounded-full font-semibold text-sm'">
                 {{ statusLabel(selectedBooking.status) }}
               </span>
             </div>
             
             <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
               <div class="space-y-6">
                  <div class="bg-slate-50 rounded-lg p-6">
                    <h4 class="text-lg font-semibold text-slate-800 mb-4 flex items-center">
                      <i class="fas fa-hotel mr-2 text-blue-500"></i>
                      Thông tin khách sạn
                    </h4>
                    <div class="space-y-3">
                      <div class="flex justify-between items-center">
                        <span class="text-sm font-medium text-slate-600">Tên khách sạn:</span>
                        <span class="text-sm font-semibold text-slate-900">{{ selectedBooking.hotelName }}</span>
                      </div>
                      <div class="flex justify-between items-center">
                        <span class="text-sm font-medium text-slate-600">Loại phòng:</span>
                        <span class="text-sm text-slate-900">{{ selectedBooking.variantName || selectedBooking.roomType || '--' }}</span>
                      </div>
                    </div>
                  </div>
                 
                 <div class="bg-slate-50 rounded-lg p-6">
                   <h4 class="text-lg font-semibold text-slate-800 mb-4 flex items-center">
                     <i class="fas fa-user mr-2 text-green-500"></i>
                     Thông tin khách hàng
                   </h4>
                   <div class="space-y-3">
                     <div class="flex justify-between items-center">
                       <span class="text-sm font-medium text-slate-600">Tên khách hàng:</span>
                       <span class="text-sm font-semibold text-slate-900">{{ selectedBooking.customerName || '--' }}</span>
                     </div>
                     <div class="flex justify-between items-center">
                       <span class="text-sm font-medium text-slate-600">Số điện thoại:</span>
                       <span class="text-sm text-slate-900">{{ selectedBooking.customerPhone || selectedBooking.customer?.phone || selectedBooking.customer?.phoneNumber || '--' }}</span>
                     </div>
                     <div class="flex justify-between items-center">
                       <span class="text-sm font-medium text-slate-600">Email:</span>
                       <span class="text-sm text-slate-900">{{ selectedBooking.customerEmail || selectedBooking.customer?.email || '--' }}</span>
                     </div>
                   </div>
                 </div>
               </div>
               
               <div class="space-y-6">
                  <div class="bg-slate-50 rounded-lg p-6">
                    <h4 class="text-lg font-semibold text-slate-800 mb-4 flex items-center">
                      <i class="fas fa-calendar-alt mr-2 text-purple-500"></i>
                      Chi tiết đặt phòng
                    </h4>
                    <div class="space-y-3">
                      <div class="flex justify-between items-center">
                        <span class="text-sm font-medium text-slate-600">Số lượng phòng đã đặt:</span>
                        <span class="text-sm font-semibold text-slate-900">{{ selectedBooking.rooms || '--' }}</span>
                      </div>
                      <div class="flex justify-between items-center">
                        <span class="text-sm font-medium text-slate-600">Ngày nhận phòng:</span>
                        <span class="text-sm font-semibold text-slate-900">{{ formatDate(selectedBooking.checkInDate) || '--' }}</span>
                      </div>
                      <div class="flex justify-between items-center">
                        <span class="text-sm font-medium text-slate-600">Ngày trả phòng:</span>
                        <span class="text-sm font-semibold text-slate-900">{{ formatDate(selectedBooking.checkOutDate) || '--' }}</span>
                      </div>
                      <div class="flex justify-between items-center">
                        <span class="text-sm font-medium text-slate-600">Số người lớn:</span>
                        <span class="text-sm text-slate-900">{{ selectedBooking.numAdults || '--' }}</span>
                      </div>
                      <div class="flex justify-between items-center">
                        <span class="text-sm font-medium text-slate-600">Số trẻ em:</span>
                        <span class="text-sm text-slate-900">{{ selectedBooking.numChildren || '--' }}</span>
                      </div>
                    </div>
                  </div>
                 
                 <div class="bg-gradient-to-r from-green-50 to-emerald-50 rounded-lg p-6 border border-green-200">
                   <h4 class="text-lg font-semibold text-slate-800 mb-4 flex items-center">
                     <i class="fas fa-credit-card mr-2 text-green-600"></i>
                     Thông tin thanh toán
                   </h4>
                   <div class="space-y-3">
                     <div class="flex justify-between items-center">
                       <span class="text-sm font-medium text-slate-600">Mã đơn hàng:</span>
                       <span class="text-sm font-semibold text-slate-900">{{ selectedBooking.orderId || '--' }}</span>
                     </div>
                     <div class="flex justify-between items-center pt-2 border-t border-green-200">
                       <span class="text-base font-semibold text-slate-700">Tổng tiền:</span>
                       <span class="text-xl font-bold text-green-600">{{ formatCurrency(selectedBooking.totalPrice) }} VND</span>
                     </div>
                   </div>
                 </div>
               </div>
             </div>
           </div>
                 </div>
      </div>
    </div>
    
    <div v-if="showInvoiceModal" class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4" @click="showInvoiceModal = false">
      <div class="bg-white rounded-xl shadow-2xl max-w-4xl w-full max-h-[95vh] overflow-hidden" @click.stop>
        <div class="flex items-center justify-between p-4 border-b border-slate-200 bg-white sticky top-0 z-10">
          <h2 class="text-lg font-bold text-slate-800">Hóa đơn thanh toán</h2>
          <button @click="showInvoiceModal = false" class="text-slate-400 hover:text-slate-600 transition-colors">
            <i class="fas fa-times text-xl"></i>
          </button>
        </div>
        
        <div class="overflow-y-auto max-h-[calc(95vh-80px)]">
          <InvoiceTemplate 
            v-if="selectedInvoiceBooking"
            :invoice-data="selectedInvoiceBooking"
            :show-print-button="true"
            @close="showInvoiceModal = false"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue';
import { hotelAdminApi } from '@/api/adminApi';
import { getAllHotelCustomers, getCustomerBookedRooms } from '@/api/hotelApi';
import { getCustomerById } from '@/api/CustomerApi';
import CustomSelect from '@/components/CustomSelect.vue';
import InvoiceTemplate from '@/components/InvoiceTemplate.vue';
import { useAdminBreadcrumbStore } from '@/store/useAdminBreadcrumbStore';
import { useAdminAuth } from '@/composables/useAdminAuth';
import { useUserStore } from '@/store/UserStore';

const searchQuery = ref('');
const statusFilter = ref('');
const sortFilter = ref('newest');
const viewMode = ref('table');
const currentPage = ref(1);
const itemsPerPageOptions = [5, 10, 20, 50, 'Tất cả'];
const itemsPerPageStr = ref('5');
const itemsPerPage = computed(() => itemsPerPageStr.value === 'Tất cả' ? filteredBookings.value.length : Number(itemsPerPageStr.value));

const showFilterDropdown = ref(false);
const filterDropdownContainer = ref(null);

const tempFilterPriceMin = ref(0);
const tempFilterPriceMax = ref(0);
const tempFilterCreatedAtPreset = ref('');
const tempFilterCreatedAtFrom = ref('');
const tempFilterCreatedAtTo = ref('');

const filterPriceMin = ref(0);
const filterPriceMax = ref(0);
const filterCreatedAtPreset = ref('');
const filterCreatedAtFrom = ref('');
const filterCreatedAtTo = ref('');

const showDetailModal = ref(false);
const selectedBooking = ref(null);
const showInvoiceModal = ref(false);
const selectedInvoiceBooking = ref(null);

const activeDropdown = ref(null);
const dropdownMenuPosition = ref({ top: 0, left: 0 });
const dropdownBtnRefMap = ref({});

// Grid dropdown variables
const activeGridDropdown = ref(null);

const createdAtPresets = [
  { label: 'Hôm nay', value: 'today' },
  { label: 'Hôm qua', value: 'yesterday' },
  { label: '3 ngày trước', value: '3_days_ago' },
  { label: 'Tuần này', value: 'this_week' },
  { label: 'Tuần trước', value: 'last_week' },
  { label: 'Tháng này', value: 'this_month' },
  { label: 'Tháng trước', value: 'last_month' }
];

const statusFilterOptions = [
  { label: 'Tất cả trạng thái', value: '' },
  { label: 'Chờ thanh toán', value: 'PENDING_PAYMENT' },
  { label: 'Đã thanh toán', value: 'PAID' },
  { label: 'Đã hủy', value: 'CANCELLED' },
  { label: 'Hoàn tiền', value: 'REFUNDED' }
];

const sortFilterOptions = [
  { label: 'Mới nhất', value: 'newest' },
  { label: 'Cũ nhất', value: 'oldest' }
];

watch(itemsPerPageStr, (newValue) => {
  if (newValue === 'Tất cả') {
    currentPage.value = 1;
  }
});

watch([statusFilter, sortFilter], () => {
  currentPage.value = 1;
});

const bookings = ref([]);

const { requireAdmin } = useAdminAuth();

onMounted(async () => {
  const userStore = useUserStore();
  console.log('UserStore:', userStore);
  console.log('User:', userStore.user);
  console.log('User roles:', userStore.user?.roles);
  
  if (!userStore.user) {
    console.log('No user data, trying to restore...');
    await userStore.restoreUserFromToken();
    console.log('After restore - User:', userStore.user);
    console.log('After restore - User roles:', userStore.user?.roles);
  }
  
  if (!requireAdmin('hotel')) {
    console.log('Không có quyền admin hotel');
    return;
  }
  
  console.log('Có quyền admin hotel, loading data...');

  const breadcrumbStore = useAdminBreadcrumbStore();
  breadcrumbStore.setBreadcrumb([
    { label: 'Booking', active: true }
  ]);
  try {
    const res = await hotelAdminApi.getAllHotelBookings();
    console.log('API Response:', res);
    console.log('Response data:', res.data);
    console.log('Bookings data:', res.data?.data);
    
    if (res.data && res.data.data) {
      bookings.value = res.data.data;
      console.log('Loaded bookings:', bookings.value);
      if (bookings.value.length > 0) {
        console.log('First booking structure:', bookings.value[0]);
        console.log('First booking keys:', Object.keys(bookings.value[0]));
      }
    } else {
      bookings.value = [];
      console.log('No bookings data found');
    }
  } catch (e) {
    console.error('Error loading bookings:', e);
    bookings.value = [];
  }
  
  document.addEventListener('click', handleOutsideClick, true);
});

function handleOutsideClick(event) {
  // Handle table dropdown
  if (activeDropdown.value !== null) {
    const btn = dropdownBtnRefMap.value && dropdownBtnRefMap.value[activeDropdown.value];
    const dropdownMenu = document.querySelector('.dropdown-menu');
    if (!btn || (!btn.contains(event.target) && (!dropdownMenu || !dropdownMenu.contains(event.target)))) {
      activeDropdown.value = null;
    }
  }
  
  // Handle grid dropdown
  if (activeGridDropdown.value !== null) {
    const gridDropdownMenus = document.querySelectorAll('.grid-dropdown-menu');
    const gridDropdownButtons = document.querySelectorAll('[data-grid-dropdown]');
    let clickedInsideGridDropdown = false;
    
    gridDropdownMenus.forEach(menu => {
      if (menu.contains(event.target)) {
        clickedInsideGridDropdown = true;
      }
    });
    
    gridDropdownButtons.forEach(button => {
      if (button.contains(event.target)) {
        clickedInsideGridDropdown = true;
      }
    });
    
    if (!clickedInsideGridDropdown) {
      console.log('Click outside grid dropdown, closing...');
      activeGridDropdown.value = null;
    }
  }
  
  if (filterDropdownContainer.value && !filterDropdownContainer.value.contains(event.target)) {
    showFilterDropdown.value = false;
  }
}

const filteredBookings = computed(() => {
  let arr = bookings.value.slice();
  
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase();
    arr = arr.filter(b =>
      (b.customerName || '').toLowerCase().includes(q) ||
      (b.hotelName || '').toLowerCase().includes(q)
    );
  }
  
  if (statusFilter.value) {
    arr = arr.filter(b => b.status === statusFilter.value);
  }
  
  if (filterPriceMin.value > 0) {
    arr = arr.filter(b => b.totalPrice >= filterPriceMin.value);
  }
  if (filterPriceMax.value > 0) {
    arr = arr.filter(b => b.totalPrice <= filterPriceMax.value);
  }
  
  if (filterCreatedAtFrom.value || filterCreatedAtTo.value) {
    arr = arr.filter(b => {
      const bookingDate = new Date(b.createdAt);
      const fromDate = filterCreatedAtFrom.value ? new Date(filterCreatedAtFrom.value) : null;
      const toDate = filterCreatedAtTo.value ? new Date(filterCreatedAtTo.value + 'T23:59:59') : null;
      
      if (fromDate && toDate) {
        return bookingDate >= fromDate && bookingDate <= toDate;
      } else if (fromDate) {
        return bookingDate >= fromDate;
      } else if (toDate) {
        return bookingDate <= toDate;
      }
      return true;
    });
  }
  
  if (sortFilter.value === 'newest') {
    arr.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
  } else if (sortFilter.value === 'oldest') {
    arr.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
  }
  
  return arr;
});

const totalPages = computed(() => Math.ceil(filteredBookings.value.length / (itemsPerPage.value || 1)));
const paginatedBookings = computed(() => {
  if (viewMode.value === 'grid') return filteredBookings.value;
  
  if (itemsPerPageStr.value === 'Tất cả') return filteredBookings.value;
  const start = (currentPage.value - 1) * itemsPerPage.value;
  return filteredBookings.value.slice(start, start + itemsPerPage.value);
});

const displayedPages = computed(() => {
  const total = totalPages.value;
  const current = currentPage.value;
  const result = [];
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
});

function changePage(page) { if (page >= 1 && page <= totalPages.value) currentPage.value = page; }
function nextPage() { if (currentPage.value < totalPages.value) currentPage.value++; }
function prevPage() { if (currentPage.value > 1) currentPage.value--; }

function toggleFilterDropdown() {
  showFilterDropdown.value = !showFilterDropdown.value;
}

function setCreatedAtPreset(preset) {
  tempFilterCreatedAtPreset.value = preset;
  const today = new Date();
  
  switch (preset) {
    case 'today':
      tempFilterCreatedAtFrom.value = today.toISOString().split('T')[0];
      tempFilterCreatedAtTo.value = today.toISOString().split('T')[0];
      break;
    case 'yesterday':
      const yesterday = new Date(today);
      yesterday.setDate(today.getDate() - 1);
      tempFilterCreatedAtFrom.value = yesterday.toISOString().split('T')[0];
      tempFilterCreatedAtTo.value = yesterday.toISOString().split('T')[0];
      break;
    case '3_days_ago':
      const threeDaysAgo = new Date(today);
      threeDaysAgo.setDate(today.getDate() - 3);
      tempFilterCreatedAtFrom.value = threeDaysAgo.toISOString().split('T')[0];
      tempFilterCreatedAtTo.value = threeDaysAgo.toISOString().split('T')[0];
      break;
    case 'this_week':
      const startOfWeek = new Date(today);
      startOfWeek.setDate(today.getDate() - today.getDay());
      tempFilterCreatedAtFrom.value = startOfWeek.toISOString().split('T')[0];
      tempFilterCreatedAtTo.value = today.toISOString().split('T')[0];
      break;
    case 'last_week':
      const lastWeekStart = new Date(today);
      lastWeekStart.setDate(today.getDate() - today.getDay() - 7);
      const lastWeekEnd = new Date(today);
      lastWeekEnd.setDate(today.getDate() - today.getDay() - 1);
      tempFilterCreatedAtFrom.value = lastWeekStart.toISOString().split('T')[0];
      tempFilterCreatedAtTo.value = lastWeekEnd.toISOString().split('T')[0];
      break;
    case 'this_month':
      const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
      tempFilterCreatedAtFrom.value = startOfMonth.toISOString().split('T')[0];
      tempFilterCreatedAtTo.value = today.toISOString().split('T')[0];
      break;
    case 'last_month':
      const lastMonthStart = new Date(today.getFullYear(), today.getMonth() - 1, 1);
      const lastMonthEnd = new Date(today.getFullYear(), today.getMonth(), 0);
      tempFilterCreatedAtFrom.value = lastMonthStart.toISOString().split('T')[0];
      tempFilterCreatedAtTo.value = lastMonthEnd.toISOString().split('T')[0];
      break;
    default:
      tempFilterCreatedAtFrom.value = '';
      tempFilterCreatedAtTo.value = '';
  }
}

function applyFilters() {
  filterPriceMin.value = tempFilterPriceMin.value;
  filterPriceMax.value = tempFilterPriceMax.value;
  filterCreatedAtPreset.value = tempFilterCreatedAtPreset.value;
  filterCreatedAtFrom.value = tempFilterCreatedAtFrom.value;
  filterCreatedAtTo.value = tempFilterCreatedAtTo.value;
  showFilterDropdown.value = false;
  currentPage.value = 1;
}

function resetFilters() {
  tempFilterPriceMin.value = 0;
  tempFilterPriceMax.value = 0;
  tempFilterCreatedAtPreset.value = '';
  tempFilterCreatedAtFrom.value = '';
  tempFilterCreatedAtTo.value = '';
  
  filterPriceMin.value = 0;
  filterPriceMax.value = 0;
  filterCreatedAtPreset.value = '';
  filterCreatedAtFrom.value = '';
  filterCreatedAtTo.value = '';
  
  showFilterDropdown.value = false;
  currentPage.value = 1;
}

async function showBookingDetail(booking) {
  console.log('=== BOOKING DETAIL DEBUG ===');
  console.log('Booking detail:', booking);
  console.log('Customer object:', booking.customer);
  console.log('Customer ID:', booking.customerId || booking.customer_id);
  console.log('All booking keys:', Object.keys(booking));
  
  console.log('customerName:', booking.customerName);
  console.log('customerPhone:', booking.customerPhone);
  console.log('customerEmail:', booking.customerEmail);
  console.log('room:', booking.room);
  console.log('roomQuantity:', booking.roomQuantity);
  console.log('roomType:', booking.roomType);
  console.log('checkInDate:', booking.checkInDate);
  console.log('checkOutDate:', booking.checkOutDate);
  console.log('numberOfNights:', booking.numberOfNights);
  console.log('numberOfGuests:', booking.numberOfGuests);
  console.log('paymentMethod:', booking.paymentMethod);
  console.log('notes:', booking.notes);
  
  const customerId = booking.customerId || booking.customer_id || booking.userId;
  console.log('Customer ID to fetch:', customerId);
  
  if (customerId && !booking.customer) {
    try {
      console.log('Fetching customer details for ID:', customerId);
      const customerRes = await getCustomerById(customerId);
      console.log('Customer API response:', customerRes);
      if (customerRes.data && customerRes.data.data) {
        booking.customer = customerRes.data.data;
        console.log('Fetched customer details:', booking.customer);
      } else {
        console.log('No customer data in response');
      }
    } catch (error) {
      console.error('Error fetching customer details:', error);
    }
  } else if (booking.customer) {
    console.log('Customer data already exists:', booking.customer);
  } else {
    console.log('No customer ID found and no customer object');
  }
  
  if (!booking.customer && booking.customerName) {
    try {
      console.log('Fetching all customers to find:', booking.customerName);
      const allCustomersRes = await getAllHotelCustomers();
      console.log('All customers response:', allCustomersRes);
      if (allCustomersRes.data && allCustomersRes.data.data) {
        const customer = allCustomersRes.data.data.find(c => 
          c.name === booking.customerName || 
          c.fullName === booking.customerName ||
          c.username === booking.customerName
        );
        if (customer) {
          booking.customer = customer;
          console.log('Found customer from all customers list:', customer);
        } else {
          console.log('Customer not found in all customers list');
        }
      }
    } catch (error) {
      console.error('Error fetching all customers:', error);
    }
  }
  
  if (booking.customer) {
    booking.customerPhone = booking.customer.phone || booking.customer.phoneNumber;
    booking.customerEmail = booking.customer.email;
    console.log('Using customer data from found customer:', {
      phone: booking.customerPhone,
      email: booking.customerEmail
    });
  }
  
  console.log('Using existing booking data:', {
    checkInDate: booking.checkInDate,
    checkOutDate: booking.checkOutDate,
    numAdults: booking.numAdults,
    numChildren: booking.numChildren,
    rooms: booking.rooms,
    roomType: booking.roomType,
    variantName: booking.variantName,
    customerPhone: booking.customerPhone,
    customerEmail: booking.customerEmail
  });
  
  selectedBooking.value = booking;
  showDetailModal.value = true;
  console.log('=== END DEBUG ===');
}

function closeBookingDetail() {
  showDetailModal.value = false;
  selectedBooking.value = null;
}

function setDropdownBtnRef(el, id) {
  if (!dropdownBtnRefMap.value) dropdownBtnRefMap.value = {};
  if (el) {
    dropdownBtnRefMap.value[id] = el;
  } else {
    delete dropdownBtnRefMap.value[id];
  }
}

function toggleDropdown(id) {
  if (activeDropdown.value === id) {
    activeDropdown.value = null;
    return;
  }
  activeDropdown.value = id;
  nextTick(() => {
    const btn = dropdownBtnRefMap.value && dropdownBtnRefMap.value[id];
    if (btn) {
      const rect = btn.getBoundingClientRect();
      dropdownMenuPosition.value = {
        top: rect.bottom + window.scrollY - 20,
        left: rect.left + window.scrollX - 165,
      };
    }
  });
}

// Grid dropdown function
function toggleGridDropdown(id) {
  console.log('Toggle grid dropdown for ID:', id, 'Current active:', activeGridDropdown.value);
  if (activeGridDropdown.value === id) {
    activeGridDropdown.value = null;
    console.log('Closing dropdown');
  } else {
    activeGridDropdown.value = id;
    console.log('Opening dropdown');
  }
}

function viewBookingDetail(booking) {
  showBookingDetail(booking);
  activeDropdown.value = null;
  activeGridDropdown.value = null;
}

function exportInvoice(booking) {
  console.log('Exporting invoice for booking:', booking.id);
  
  if (!booking.customer && (booking.customerId || booking.customer_id)) {
    const customerId = booking.customerId || booking.customer_id;
    getCustomerById(customerId).then(customerRes => {
      if (customerRes.data && customerRes.data.data) {
        booking.customer = customerRes.data.data;
        booking.customerPhone = booking.customer.phone || booking.customer.phoneNumber;
        booking.customerEmail = booking.customer.email;
      }
      selectedInvoiceBooking.value = booking;
      showInvoiceModal.value = true;
    }).catch(error => {
      console.error('Error fetching customer details:', error);
      selectedInvoiceBooking.value = booking;
      showInvoiceModal.value = true;
    });
  } else {
    selectedInvoiceBooking.value = booking;
    showInvoiceModal.value = true;
  }
  
  activeDropdown.value = null;
  activeGridDropdown.value = null;
}

function formatDate(date) {
  if (!date) return '';
  const d = new Date(date);
  return d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
}

function formatDateTime(dt) {
  if (!dt) return '';
  const d = new Date(dt);
  return d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' }) + ' ' + d.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
}
function formatCurrency(value) {
  if (value == null) return '0';
  return new Intl.NumberFormat('vi-VN').format(value);
}
function statusLabel(status) {
  switch (status) {
    case 'PENDING_PAYMENT': return 'Chờ thanh toán';
    case 'PAID': return 'Đã thanh toán';
    case 'CANCELLED': return 'Đã hủy';
    case 'REFUNDED': return 'Hoàn tiền';
    default: return status;
  }
}
function statusClass(status) {
  switch (status) {
    case 'PENDING_PAYMENT': return 'bg-yellow-100 text-yellow-800 border border-yellow-300';
    case 'PAID': return 'bg-green-100 text-green-800 border border-green-300';
    case 'CANCELLED': return 'bg-red-100 text-red-700 border border-red-300';
    case 'REFUNDED': return 'bg-blue-100 text-blue-800 border border-blue-300';
    default: return 'bg-gray-100 text-gray-700 border border-gray-300';
  }
}
</script> 