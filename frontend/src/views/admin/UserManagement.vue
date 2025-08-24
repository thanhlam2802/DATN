
<template>
  <div class="p-6">
    <div class="mb-6">
      <h1 class="text-3xl font-bold text-gray-800 mb-2">Quản lý Người dùng</h1>
      <p class="text-gray-600">Quản lý tất cả người dùng trong hệ thống</p>
    </div>

    <!-- Thống kê tổng quan -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
      <div class="bg-white p-4 rounded-lg shadow-sm border">
        <div class="flex items-center">
          <div class="p-2 bg-blue-100 rounded-lg">
            <i class="fas fa-users text-blue-600"></i>
          </div>
          <div class="ml-3">
            <p class="text-sm text-gray-600">Tổng người dùng</p>
            <p class="text-2xl font-bold text-gray-900">{{ totalUsers }}</p>
          </div>
        </div>
      </div>
      <div class="bg-white p-4 rounded-lg shadow-sm border">
        <div class="flex items-center">
          <div class="p-2 bg-green-100 rounded-lg">
            <i class="fas fa-user-check text-green-600"></i>
          </div>
          <div class="ml-3">
            <p class="text-sm text-gray-600">Đang hoạt động</p>
            <p class="text-2xl font-bold text-gray-900">{{ activeUsers }}</p>
          </div>
        </div>
      </div>
      <div class="bg-white p-4 rounded-lg shadow-sm border">
        <div class="flex items-center">
          <div class="p-2 bg-yellow-100 rounded-lg">
            <i class="fas fa-user-clock text-yellow-600"></i>
          </div>
          <div class="ml-3">
            <p class="text-sm text-gray-600">Vô hiệu hóa</p>
            <p class="text-2xl font-bold text-gray-900">{{ inactiveUsers }}</p>
          </div>
        </div>
      </div>

    </div>

    <!-- Bộ lọc và tìm kiếm -->
    <div class="bg-white p-6 rounded-xl shadow-lg mb-6">
      <div class="flex flex-col lg:flex-row gap-4 items-center justify-between">
        <div class="flex flex-col lg:flex-row gap-4 flex-1">
          <!-- Tìm kiếm -->
          <div class="relative w-full lg:w-85">
            <input
              type="text"
              v-model="searchQuery"
              placeholder="Tìm theo tên, email hoặc số điện thoại"
              class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
            <i class="fas fa-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
          </div>

          <!-- Lọc theo role -->
          <div class="w-full lg:w-48">
            <CustomSelect
              v-model="roleFilter"
              :options="roleFilterOptions"
              placeholder="Tất cả role"
              class="w-full"
            />
          </div>

          <!-- Lọc theo trạng thái -->
          <div class="w-full lg:w-48">
            <CustomSelect
              v-model="statusFilter"
              :options="statusFilterOptions"
              placeholder="Tất cả trạng thái"
              class="w-full"
            />
          </div>
        </div>

        <!-- Nút thêm user mới -->
        <button
          @click="showAddUserModal = true"
          class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors duration-200 flex items-center gap-2"
        >
          <i class="fas fa-plus"></i>
          Thêm người dùng
        </button>
      </div>
    </div>

    <!-- Success message -->
    <div v-if="successMessage" class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4">
      {{ successMessage }}
    </div>

    <!-- Error message -->
    <div v-if="error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
      <div class="flex items-start">
        <i class="fas fa-exclamation-triangle mt-0.5 mr-2 flex-shrink-0"></i>
        <div>
          <p class="font-medium">{{ error }}</p>
          <p v-if="error.includes('Lỗi hệ thống')" class="text-sm mt-1">
            Nếu vấn đề vẫn tiếp tục, vui lòng liên hệ quản trị viên hoặc thử lại sau.
          </p>
        </div>
      </div>
    </div>

         <!-- Bảng người dùng -->
     <div class="bg-white rounded-xl shadow-lg overflow-hidden">
       <div class="overflow-x-auto">
         <div class="overflow-y-auto h-[395px]">
           <table class="min-w-[1200px] w-full text-sm text-left">
                          <thead class="bg-slate-100 sticky top-0 z-10">
               <tr>
                 <th class="bg-slate-100 px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">STT</th>
                 <th class="bg-slate-100 px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">Người dùng</th>
                 <th class="bg-slate-100 px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">Role</th>
                                   <th class="bg-slate-100 px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">Số điện thoại</th>
                  <th class="bg-slate-100 px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">Ngày đăng ký</th>
                  <th class="bg-slate-100 px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">Ngày sinh</th>
                  <th class="bg-slate-100 px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">Trạng thái</th>
                 <th class="bg-slate-100 px-3 py-4 text-right text-xs font-bold text-slate-700 uppercase tracking-wider">Hành động</th>
               </tr>
             </thead>
                       <tbody class="bg-white divide-y divide-slate-100">
                               <!-- Loading state -->
                <tr v-if="loading" class="hover:bg-slate-50">
                  <td colspan="8" class="px-6 py-12 text-center">
                    <div class="flex items-center justify-center">
                      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                      <span class="ml-2 text-gray-600">Đang tải dữ liệu...</span>
                    </div>
                  </td>
                </tr>
                
                <!-- Empty state -->
                <tr v-else-if="!loading && paginatedUsers.length === 0" class="hover:bg-slate-50">
                  <td colspan="8" class="px-6 py-12 text-center">
                   <div class="flex flex-col items-center space-y-3">
                     <i class="fas fa-search text-4xl text-slate-300"></i>
                     <p class="text-lg font-medium text-slate-500">Không tìm thấy người dùng nào</p>
                     <p class="text-sm text-slate-400">Thử thay đổi bộ lọc hoặc tìm kiếm khác</p>
                   </div>
                 </td>
               </tr>
               
               <!-- User rows -->
               <tr
                 v-else
                 v-for="(user, index) in paginatedUsers"
                 :key="user.id"
                 class="hover:bg-slate-50 transition-colors duration-150 cursor-pointer"
               >
                 <td class="px-3 py-5 whitespace-nowrap">
                   <div class="text-sm font-medium text-slate-700 text-center">
                     {{ itemsPerPage === 0 ? index + 1 : (currentPage - 1) * itemsPerPage + index + 1 }}
                   </div>
                 </td>
                                                            <td class="px-3 py-5 whitespace-nowrap">
                 <div class="flex items-center space-x-3">
                   <div class="max-w-[175px]">
                     <div class="text-sm font-semibold text-slate-900 truncate" :title="user.name">{{ user.name }}</div>
                   </div>
                 </div>
               </td>
               <td class="px-3 py-5 whitespace-nowrap">
                 <div class="flex flex-wrap gap-1">
                   <span
                     v-for="role in getUniqueRoles(user.roles)"
                     :key="role"
                     :class="getRoleClass(role)"
                     class="px-2 py-1 text-xs font-medium rounded-full"
                   >
                     {{ getRoleLabel(role) }}
                   </span>
                 </div>
               </td>
                               <td class="px-3 py-5 whitespace-nowrap">
                  <div class="text-sm font-medium text-slate-700">{{ user.phone || '--' }}</div>
                </td>
                <td class="px-3 py-5 whitespace-nowrap">
                  <span class="text-xs text-slate-700">{{ formatDate(user.createdAt) }}</span>
                </td>
                <td class="px-3 py-5 whitespace-nowrap">
                  <span class="text-xs text-slate-700">{{ user.birthday ? formatDate(user.birthday) : '--' }}</span>
                </td>
               <td class="px-3 py-5 whitespace-nowrap">
                 <span
                   :class="getStatusClass(user.status)"
                   class="px-3 py-1 text-xs font-medium rounded-full"
                 >
                   {{ getStatusLabel(user.status) }}
                 </span>
               </td>
               <td class="px-3 py-5 whitespace-nowrap text-right">
                 <div class="flex items-center justify-end gap-2">
                   <button
                     @click="viewUser(user)"
                     class="text-blue-600 hover:text-blue-800 p-1 rounded transition-colors duration-200"
                     title="Xem chi tiết"
                   >
                     <i class="fas fa-eye"></i>
                   </button>
                   <button
                     @click="editUser(user)"
                     class="text-green-600 hover:text-green-800 p-1 rounded transition-colors duration-200"
                     title="Chỉnh sửa"
                   >
                     <i class="fas fa-edit"></i>
                   </button>
                   <button
                     @click="toggleUserStatus(user)"
                     :class="user.status === 'ACTIVE' ? 'text-red-600 hover:text-red-800' : 'text-green-600 hover:text-green-800'"
                     class="p-1 rounded transition-colors duration-200"
                     :title="user.status === 'ACTIVE' ? 'Vô hiệu hóa' : 'Kích hoạt'"
                   >
                     <i :class="user.status === 'ACTIVE' ? 'fas fa-ban' : 'fas fa-check'"></i>
                   </button>
                   <button
                     @click="confirmDeleteUser(user)"
                     class="text-red-600 hover:text-red-800 p-1 rounded transition-colors duration-200"
                     title="Xóa người dùng"
                   >
                     <i class="fas fa-trash"></i>
                   </button>
                 </div>
               </td>
             </tr>
           </tbody>
         </table>
       </div>
     </div>

                           <!-- Phân trang -->
        <div class="flex items-center justify-end gap-4 px-4 py-3 bg-white border-t border-slate-200 rounded-b-xl">
           <div class="flex items-center gap-2">
             <span class="text-sm text-gray-700 whitespace-nowrap">Số dòng</span>
             <CustomSelect
               v-model="itemsPerPageStr"
               :options="itemsPerPageOptions.map(opt => ({ label: String(opt), value: String(opt) }))"
               class="w-20 min-w-[100px] h-8 [&>button]:h-8 [&>button]:py-1 [&>button]:text-sm"
               :direction="'up'"
             />
           </div>
                                 
            <nav v-if="totalUsersCount > 0 && itemsPerPage !== 0" aria-label="Pagination">
            <ul class="inline-flex items-center space-x-1">
              <li>
                <button @click="prevPage" :disabled="currentPage === 1"
                  class="w-8 h-8 flex items-center justify-center rounded border border-slate-200 text-gray-500 hover:bg-gray-100 disabled:opacity-50">
                  <i class="fas fa-chevron-left text-xs"></i>
                </button>
              </li>
              <li v-for="page in displayedPages" :key="page">
                <button v-if="page !== '...'" @click="goToPage(page)"
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

    <!-- Modal xem/chỉnh sửa user -->
    <div
      v-if="isModalOpen"
      class="fixed inset-0 z-50 flex justify-center items-center p-4"
      style="background-color: rgba(0, 0, 0, 0.6);"
    >
      <div class="bg-white rounded-3xl shadow-2xl w-full max-w-3xl max-h-[90vh] flex flex-col modal-container">
        <!-- Header cố định -->
        <div class="flex items-start justify-between p-6 border-b bg-white sticky top-0 z-10">
          <h2 class="text-2xl font-bold text-gray-900">
            {{ isEditing ? 'Chỉnh sửa người dùng' : 'Chi tiết người dùng' }}
          </h2>
          <button @click="closeModal" class="text-2xl text-gray-400 hover:text-gray-600">&times;</button>
        </div>

        <!-- Nội dung có thể scroll -->
        <div class="p-6 space-y-6 overflow-y-auto flex-1">
          <!-- Thông tin cơ bản -->
          <div class="flex items-start gap-4">
            <div class="flex-1">
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Họ tên</label>
                  <input
                    v-model="currentUser.name"
                    :disabled="!isEditing"
                    type="text"
                    minlength="2"
                    maxlength="200"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
                    placeholder="Nhập họ tên (ít nhất 2 ký tự)"
                  />
                  <p v-if="isEditing" class="text-xs text-gray-500 mt-1">Tên phải có ít nhất 2 ký tự</p>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Email</label>
                  <input
                    v-model="currentUser.email"
                    :disabled="!isEditing"
                    type="email"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Số điện thoại</label>
                  <input
                    v-model="currentUser.phone"
                    :disabled="!isEditing"
                    type="tel"
                    :placeholder="currentUser.phone || '--'"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Ngày sinh</label>
                  <input
                    :value="formatDateForInput(currentUser.birthday)"
                    @input="updateBirthday"
                    :disabled="!isEditing"
                    type="date"
                    :placeholder="currentUser.birthday ? formatDate(currentUser.birthday) : '--'"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Giới tính</label>
                  <CustomSelect
                    v-model="currentUser.gender"
                    :options="genderOptions"
                    placeholder="Chọn giới tính"
                    :disabled="!isEditing"
                    class="w-full"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Ngày đăng ký</label>
                  <input
                    :value="formatDate(currentUser.createdAt)"
                    disabled
                    type="text"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-100"
                  />
                </div>
              </div>
              <div class="mt-4">
                <label class="block text-sm font-medium text-gray-700 mb-1">Địa chỉ</label>
                <textarea
                  v-model="currentUser.address"
                  :disabled="!isEditing"
                  rows="3"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
                  :placeholder="currentUser.address || '--'"
                ></textarea>
              </div>
            </div>
          </div>

          <!-- Role và trạng thái -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-3">Vai trò</label>
              <div class="flex flex-wrap gap-4">
                <div
                  v-for="role in availableRoles"
                  :key="role.value"
                  class="flex items-center gap-3"
                >
                  <label class="flex items-center gap-2 cursor-pointer">
                    <input
                      v-model="currentUser.roles"
                      :value="role.value"
                      :disabled="!isEditing"
                      type="checkbox"
                      class="sr-only"
                    />
                    <div
                      :class="[
                        'relative w-12 h-6 rounded-full transition-all duration-300 ease-in-out',
                        currentUser.roles?.includes(role.value)
                          ? 'bg-orange-500 shadow-lg' 
                          : 'bg-gray-300',
                        !isEditing && 'opacity-60'
                      ]"
                    >
                      <div
                        :class="[
                          'absolute top-1 w-4 h-4 bg-white rounded-full shadow-md transition-all duration-300 ease-in-out',
                          currentUser.roles?.includes(role.value)
                            ? 'right-1' 
                            : 'left-1'
                        ]"
                      ></div>
                    </div>
                    <span class="text-sm font-medium text-gray-700 min-w-[80px]">{{ role.label }}</span>
                  </label>
                </div>
              </div>

              <!-- Hiển thị lỗi validation cho roles nếu có -->
              <p v-if="error && error.includes('Vai trò')" class="text-xs text-red-600 mt-2">
                <i class="fas fa-exclamation-circle mr-1"></i>
                {{ error }}
              </p>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Trạng thái tài khoản</label>
              <CustomSelect
                v-model="currentUser.status"
                :options="statusOptions"
                placeholder="Chọn trạng thái"
                :disabled="!isEditing"
                class="w-full"
              />
            </div>
          </div>


        </div>

        <!-- Footer cố định -->
        <div class="flex justify-end gap-3 p-6 border-t bg-gray-50 sticky bottom-0 z-10">
          <button
            @click="closeModal"
            class="px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors duration-200"
          >
            Đóng
          </button>
          <button
            v-if="!isEditing"
            @click="startEditing"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors duration-200"
          >
            Chỉnh sửa
          </button>
          <button
            v-if="isEditing"
            @click="saveUser"
            class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors duration-200"
          >
            Lưu thay đổi
          </button>
        </div>
      </div>
    </div>

    <!-- Modal thêm user mới -->
    <div
      v-if="showAddUserModal"
      class="fixed inset-0 z-50 flex justify-center items-center p-4"
      style="background-color: rgba(0, 0, 0, 0.6);"
    >
      <div class="bg-white rounded-3xl shadow-2xl w-full max-w-2xl max-h-[90vh] flex flex-col modal-container">
        <!-- Header cố định -->
        <div class="flex items-start justify-between p-6 border-b bg-white sticky top-0 z-10">
          <h2 class="text-2xl font-bold text-gray-900">Thêm người dùng mới</h2>
          <button @click="showAddUserModal = false" class="text-2xl text-gray-400 hover:text-gray-600">&times;</button>
        </div>

        <!-- Nội dung có thể scroll -->
        <form @submit.prevent="addNewUser" class="p-6 space-y-4 overflow-y-auto flex-1">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Họ tên *</label>
              <input
                v-model="newUser.name"
                type="text"
                required
                minlength="2"
                maxlength="200"
                :class="[
                  'w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500',
                  error && error.includes('Tên') ? 'border-red-500 focus:ring-red-500' : 'border-gray-300'
                ]"
                placeholder="Nhập họ tên (ít nhất 2 ký tự)"
              />
              <!-- Hiển thị lỗi validation cho tên nếu có -->
              <p v-if="error && error.includes('Tên')" class="text-xs text-red-600 mt-1">
                <i class="fas fa-exclamation-circle mr-1"></i>
                {{ error }}
              </p>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Email *</label>
              <input
                v-model="newUser.email"
                type="email"
                required
                :class="[
                  'w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500',
                  error && error.includes('Email') ? 'border-red-500 focus:ring-red-500' : 'border-gray-300'
                ]"
                placeholder="Nhập email hợp lệ"
              />
              <!-- Hiển thị lỗi validation cho email nếu có -->
              <p v-if="error && error.includes('Email')" class="text-xs text-red-600 mt-1">
                <i class="fas fa-exclamation-circle mr-1"></i>
                {{ error }}
              </p>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Số điện thoại</label>
              <input
                v-model="newUser.phone"
                type="tel"
                placeholder="Nhập số điện thoại"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Ngày sinh</label>
              <input
                v-model="newUser.birthday"
                type="date"
                placeholder="--"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Giới tính</label>
              <CustomSelect
                v-model="newUser.gender"
                :options="genderOptions"
                placeholder="--"
                class="w-full"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Mật khẩu *</label>
              <input
                v-model="newUser.password"
                type="password"
                required
                minlength="6"
                :class="[
                  'w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500',
                  error && error.includes('Mật khẩu') ? 'border-red-500 focus:ring-red-500' : 'border-gray-300'
                ]"
                placeholder="Nhập mật khẩu (ít nhất 6 ký tự)"
              />
              <p class="text-xs text-gray-500 mt-1">Mật khẩu phải có ít nhất 6 ký tự</p>
              <!-- Hiển thị lỗi validation cho mật khẩu nếu có -->
              <p v-if="error && error.includes('Mật khẩu')" class="text-xs text-red-600 mt-1">
                <i class="fas fa-exclamation-circle mr-1"></i>
                {{ error }}
              </p>
            </div>
          </div>
          
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Địa chỉ</label>
            <textarea
              v-model="newUser.address"
              rows="3"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Nhập địa chỉ đầy đủ..."
            ></textarea>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Vai trò *</label>
            <div class="flex flex-wrap gap-4">
              <div
                v-for="role in availableRoles"
                :key="role.value"
                class="flex items-center gap-3"
              >
                <label class="flex items-center gap-2 cursor-pointer">
                  <input
                    v-model="newUser.roles"
                    :value="role.value"
                    type="checkbox"
                    class="sr-only"
                  />
                  <div
                    :class="[
                      'relative w-12 h-6 rounded-full transition-all duration-300 ease-in-out',
                      newUser.roles?.includes(role.value)
                        ? 'bg-orange-500 shadow-lg' 
                        : 'bg-gray-300'
                    ]"
                  >
                    <div
                      :class="[
                        'absolute top-1 w-4 h-4 bg-white rounded-full shadow-md transition-all duration-300 ease-in-out',
                        newUser.roles?.includes(role.value)
                          ? 'right-1' 
                          : 'left-1'
                      ]"
                    ></div>
                  </div>
                  <span class="text-sm font-medium text-gray-700 min-w-[80px]">{{ role.label }}</span>
                </label>
              </div>
            </div>
            <p class="text-xs text-gray-500 mt-2">Chọn một hoặc nhiều vai trò cho người dùng</p>

            <!-- Hiển thị lỗi validation cho roles nếu có -->
            <p v-if="error && error.includes('Vai trò')" class="text-xs text-red-600 mt-1">
              <i class="fas fa-exclamation-circle mr-1"></i>
              {{ error }}
            </p>
          </div>
        </form>

        <!-- Footer cố định -->
        <div class="flex justify-end gap-3 p-6 border-t bg-gray-50 sticky bottom-0 z-10">
          <button
            @click="() => { showAddUserModal = false; error.value = null; }"
            class="px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors duration-200"
          >
            Hủy
          </button>
          <button
            @click="addNewUser"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors duration-200"
          >
            Thêm người dùng
          </button>
        </div>
      </div>
    </div>

    <!-- ConfirmDialog component -->
    <ConfirmDialog ref="confirmDialog" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import userManagementApi from "@/api/userManagementApi";
import { useUserStore } from "@/store/UserStore";
import { getAccessToken } from "@/services/TokenService";
import CustomSelect from "@/components/CustomSelect.vue";
import ConfirmDialog from "@/components/ConfirmDialog.vue";

// Store
const userStore = useUserStore();

// Reactive data
const searchQuery = ref("");
const roleFilter = ref("");
const statusFilter = ref("");
const currentPage = ref(1);
const itemsPerPageStr = ref('5');
const itemsPerPageOptions = [5, 10, 20, 50, 'Tất cả'];
const isModalOpen = ref(false);
const showAddUserModal = ref(false);
const isEditing = ref(false);
const currentUser = ref({});

// Refs
const confirmDialog = ref(null);
const newUser = ref({
  name: "",
  email: "",
  phone: "",
  password: "",
  roles: [], // Sử dụng array thay vì string
  address: "",
  birthday: "", // Sửa từ birthDate thành birthday để khớp với backend
  gender: ""
});

// Success message
const successMessage = ref("");

// Real data from API
const users = ref([]);
const totalUsersCount = ref(0); // Tổng số users từ API response
const loading = ref(false);
const error = ref(null);

// Computed properties
const filteredUsers = computed(() => {
  let filtered = users.value;

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    filtered = filtered.filter(user =>
      user.name.toLowerCase().includes(query) ||
      user.email.toLowerCase().includes(query) ||
      (user.phone && user.phone.includes(query))
    );
  }

  if (roleFilter.value) {
    // Nhóm các role tương tự lại với nhau
    const roleGroups = {
      'ADMIN_HOTELS': ['ADMIN_HOTELS', 'HOTEL_SUPPLIER'],
      'ADMIN_FLIGHTS': ['ADMIN_FLIGHTS', 'FLIGHT_SUPPLIER'],
      'ADMIN_TOURS': ['ADMIN_TOURS', 'TOUR_SUPPLIER'],
      'ADMIN_BUSES': ['ADMIN_BUSES', 'BUS_SUPPLIER'],
      'USER': ['USER'],
      'SUPER_ADMIN': ['SUPER_ADMIN']
    };
    
    const allowedRoles = roleGroups[roleFilter.value] || [roleFilter.value];
    filtered = filtered.filter(user =>
      user.roles.some(userRole => allowedRoles.includes(userRole))
    );
  }

  if (statusFilter.value) {
    filtered = filtered.filter(user => user.status === statusFilter.value);
  }

  return filtered;
});

// Statistics from API
const statistics = ref({
  totalUsers: 0,
  activeUsers: 0,
  inactiveUsers: 0
});

const totalUsers = computed(() => statistics.value.totalUsers);
const activeUsers = computed(() => statistics.value.activeUsers);
const inactiveUsers = computed(() => statistics.value.inactiveUsers);

const itemsPerPage = computed(() => {
  const result = itemsPerPageStr.value === 'Tất cả' ? 0 : Number(itemsPerPageStr.value);
  return result;
});

const totalPages = computed(() => {
  if (itemsPerPage.value === 0) {
    return 1; // When showing all, only 1 page
  }
  // Sử dụng số users sau khi filter để tính đúng số trang
  const result = Math.ceil(filteredUsers.value.length / itemsPerPage.value);
  return result;
});

const paginatedUsers = computed(() => {
  if (itemsPerPage.value === 0) {
    return filteredUsers.value; // Show all filtered users
  }
  
  // Tính toán pagination dựa trên filteredUsers
  const startIndex = (currentPage.value - 1) * itemsPerPage.value;
  const endIndex = startIndex + itemsPerPage.value;
  return filteredUsers.value.slice(startIndex, endIndex);
});

const displayedPages = computed(() => {
  if (itemsPerPage.value === 0) {
    return [1]; // Only show page 1 when displaying all
  }
  
  const total = totalPages.value; 
  const current = currentPage.value; 
  const result = [];
  if (total <= 7) { 
    for (let i = 1; i <= total; i++) { 
      result.push(i); 
    } 
  } else {
    result.push(1);
    let start = Math.max(2, current - 1); 
    let end = Math.min(total - 1, current + 1);
    if (current > 4) result.push('...');
    if (current <= 4) { 
      start = 2; 
      end = 4; 
    }
    if (current >= total - 3) { 
      start = total - 3; 
      end = total - 1; 
    }
    for (let i = start; i <= end; i++) { 
      result.push(i); 
    }
    if (current < total - 3) result.push('...');
    result.push(total);
  }
  return result;
});

const availableRoles = [
  { value: 'USER', label: 'Người dùng' },
  { value: 'ADMIN_HOTELS', label: 'Admin Khách sạn' },
  { value: 'ADMIN_FLIGHTS', label: 'Admin Chuyến bay' },
  { value: 'ADMIN_TOURS', label: 'Admin Tour' },
  { value: 'ADMIN_BUSES', label: 'Admin Xe buýt' },
  { value: 'SUPER_ADMIN', label: 'Super Admin' }
];

// Options cho các dropdown
const genderOptions = [
  { value: '', label: 'Chọn giới tính' },
  { value: 'MALE', label: 'Nam' },
  { value: 'FEMALE', label: 'Nữ' },
  { value: 'OTHER', label: 'Khác' }
];

const statusOptions = [
  { value: 'ACTIVE', label: 'Đang hoạt động' },
  { value: 'INACTIVE', label: 'Vô hiệu hóa' }
];

const roleFilterOptions = [
  { value: '', label: 'Tất cả role' },
  { value: 'USER', label: 'Người dùng' },
  { value: 'ADMIN_HOTELS', label: 'Admin Khách sạn' },
  { value: 'ADMIN_FLIGHTS', label: 'Admin Chuyến bay' },
  { value: 'ADMIN_TOURS', label: 'Admin Tour' },
  { value: 'ADMIN_BUSES', label: 'Admin Xe buýt' },
  { value: 'SUPER_ADMIN', label: 'Super Admin' }
];

const statusFilterOptions = [
  { value: '', label: 'Tất cả trạng thái' },
  { value: 'ACTIVE', label: 'Đang hoạt động' },
  { value: 'INACTIVE', label: 'Vô hiệu hóa' }
];

// Methods
const viewUser = (user) => {
  console.log('Viewing user:', user);
  console.log('User birthday:', user.birthday);
  console.log('User birthday type:', typeof user.birthday);
  console.log('Formatted birthday for input:', formatDateForInput(user.birthday));
  console.log('All user fields:', Object.keys(user));
  console.log('User data structure:', JSON.stringify(user, null, 2));
  currentUser.value = { ...user };
  isEditing.value = false;
  isModalOpen.value = true;
};

const editUser = (user) => {
  console.log('Editing user:', user);
  console.log('User birthday:', user.birthday);
  console.log('All user fields:', Object.keys(user));
  console.log('User data structure:', JSON.stringify(user, null, 2));
  currentUser.value = { ...user };
  isEditing.value = true;
  isModalOpen.value = true;
};

const startEditing = () => {
  isEditing.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
  isEditing.value = false;
  currentUser.value = {};
  error.value = null; // Xóa lỗi khi đóng modal
};



const saveUser = () => {
  updateUser(currentUser.value);
};

const toggleUserStatus = (user) => {
  const newStatus = user.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
  updateUserStatus(user, newStatus);
};

const confirmDeleteUser = async (user) => {
  const confirmed = await confirmDialog.value.showDialog({
    type: 'danger',
    title: 'Xác nhận xóa người dùng',
    message: `Bạn có chắc chắn muốn xóa người dùng "${user.name}"?`,
    details: `Email: ${user.email}\nID: ${user.id}\n\nHành động này không thể hoàn tác. Tất cả dữ liệu của người dùng sẽ bị xóa vĩnh viễn.`,
    confirmText: 'Xóa người dùng',
    cancelText: 'Hủy'
  });
  
  if (confirmed) {
    await deleteUser(user);
  }
};

const deleteUser = async (user) => {
  try {
    const response = await userManagementApi.deleteUser(user.id);
    
    if (response.statusCode === 200) {
      await fetchUsers();
      
      // Hiển thị thông báo thành công
      successMessage.value = 'Người dùng đã được xóa thành công!';
      setTimeout(() => {
        successMessage.value = '';
      }, 3000);
    } else {
      error.value = response.message || 'Có lỗi xảy ra khi xóa người dùng';
    }
  } catch (err) {
    console.error('Error deleting user:', err);
    
    if (err.response?.status === 500) {
      error.value = 'Lỗi hệ thống. Vui lòng thử lại sau hoặc liên hệ quản trị viên.';
    } else if (err.response?.data?.message) {
      error.value = err.response.data.message;
    } else {
      error.value = 'Không thể xóa người dùng. Vui lòng thử lại sau.';
    }
  }
};

const addNewUser = async () => {
  if (!newUser.value.name || !newUser.value.email || !newUser.value.password) {
    error.value = 'Vui lòng điền đầy đủ thông tin bắt buộc';
    return;
  }

  if (!newUser.value.roles || newUser.value.roles.length === 0) {
    error.value = 'Vui lòng chọn ít nhất một vai trò cho người dùng';
    return;
  }

  // Kiểm tra tên (ít nhất 2 ký tự)
  if (newUser.value.name.trim().length < 2) {
    error.value = 'Tên phải có ít nhất 2 ký tự';
    return;
  }

  // Kiểm tra email hợp lệ
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(newUser.value.email)) {
    error.value = 'Email không hợp lệ';
    return;
  }

  // Kiểm tra mật khẩu (ít nhất 6 ký tự)
  if (newUser.value.password.length < 6) {
    error.value = 'Mật khẩu phải có ít nhất 6 ký tự';
    return;
  }

  // Tạo cấu trúc dữ liệu đầy đủ
  const userData = {
    name: newUser.value.name.trim(),
    email: newUser.value.email.trim().toLowerCase(),
    password: newUser.value.password,
    roles: [...newUser.value.roles], // Copy array để không ảnh hưởng đến newUser.value.roles
    role: newUser.value.roles.length > 0 ? newUser.value.roles.join(',') : '' // Chuyển array thành string để tương thích với backend
  };

  // Thêm logic gán role tự động vào userData.roles
  console.log('=== ROLE AUTO-ASSIGNMENT LOGIC ===');
  console.log('Original roles selected:', newUser.value.roles);
  console.log('Initial userData.roles:', userData.roles);
  
  // Xử lý ADMIN_HOTELS và HOTEL_SUPPLIER
  if (newUser.value.roles.includes('ADMIN_HOTELS')) {
    console.log('ADMIN_HOTELS detected, adding HOTEL_SUPPLIER');
    if (!userData.roles.includes('HOTEL_SUPPLIER')) {
      userData.roles.push('HOTEL_SUPPLIER');
      console.log('HOTEL_SUPPLIER added to roles');
      console.log('userData.roles after adding HOTEL_SUPPLIER:', userData.roles);
    } else {
      console.log('HOTEL_SUPPLIER already exists in roles');
    }
  } else {
    // Nếu không chọn ADMIN_HOTELS thì bỏ HOTEL_SUPPLIER
    if (userData.roles.includes('HOTEL_SUPPLIER')) {
      const index = userData.roles.indexOf('HOTEL_SUPPLIER');
      userData.roles.splice(index, 1);
      console.log('HOTEL_SUPPLIER removed from roles (ADMIN_HOTELS not selected)');
    }
  }
  
  // Xử lý ADMIN_FLIGHTS và FLIGHT_SUPPLIER
  if (newUser.value.roles.includes('ADMIN_FLIGHTS')) {
    console.log('ADMIN_FLIGHTS detected, adding FLIGHT_SUPPLIER');
    if (!userData.roles.includes('FLIGHT_SUPPLIER')) {
      userData.roles.push('FLIGHT_SUPPLIER');
      console.log('FLIGHT_SUPPLIER added to roles');
    } else {
      console.log('FLIGHT_SUPPLIER already exists in roles');
    }
  } else {
    // Nếu không chọn ADMIN_FLIGHTS thì bỏ FLIGHT_SUPPLIER
    if (userData.roles.includes('FLIGHT_SUPPLIER')) {
      const index = userData.roles.indexOf('FLIGHT_SUPPLIER');
      userData.roles.splice(index, 1);
      console.log('FLIGHT_SUPPLIER removed from roles (ADMIN_FLIGHTS not selected)');
    }
  }
  
  // Xử lý ADMIN_TOURS và TOUR_SUPPLIER
  if (newUser.value.roles.includes('ADMIN_TOURS')) {
    console.log('ADMIN_TOURS detected, adding TOUR_SUPPLIER');
    if (!userData.roles.includes('TOUR_SUPPLIER')) {
      userData.roles.push('TOUR_SUPPLIER');
      console.log('TOUR_SUPPLIER added to roles');
    } else {
      console.log('TOUR_SUPPLIER already exists in roles');
    }
  } else {
    // Nếu không chọn ADMIN_TOURS thì bỏ TOUR_SUPPLIER
    if (userData.roles.includes('TOUR_SUPPLIER')) {
      const index = userData.roles.indexOf('TOUR_SUPPLIER');
      userData.roles.splice(index, 1);
      console.log('TOUR_SUPPLIER removed from roles (ADMIN_TOURS not selected)');
    }
  }
  
  // Xử lý ADMIN_BUSES và BUS_SUPPLIER
  if (newUser.value.roles.includes('ADMIN_BUSES')) {
    console.log('ADMIN_BUSES detected, adding BUS_SUPPLIER');
    if (!userData.roles.includes('BUS_SUPPLIER')) {
      userData.roles.push('BUS_SUPPLIER');
      console.log('BUS_SUPPLIER added to roles');
    } else {
      console.log('BUS_SUPPLIER already exists in roles');
    }
  } else {
    // Nếu không chọn ADMIN_BUSES thì bỏ BUS_SUPPLIER
    if (userData.roles.includes('BUS_SUPPLIER')) {
      const index = userData.roles.indexOf('BUS_SUPPLIER');
      userData.roles.splice(index, 1);
      console.log('BUS_SUPPLIER removed from roles (ADMIN_BUSES not selected)');
    }
  }
  
  // Cập nhật field role sau khi thêm role tự động
  // Gửi cả role chính và roles array để backend có thể xử lý
  const mainRoles = userData.roles.filter(role => 
    role.startsWith('ADMIN_') || role === 'USER' || role === 'SUPER_ADMIN'
  );
  userData.role = mainRoles.join(',');
  
  // Thêm field roles để backend có thể xử lý (nếu hỗ trợ)
  userData.roles = [...userData.roles]; // Đảm bảo roles array được gửi
  
  console.log('Main roles to send to backend:', mainRoles);
  console.log('Role string to send to backend:', userData.role);
  console.log('Full roles array to send to backend:', userData.roles);
  
  console.log('Final roles after auto-assignment:', userData.roles);
  console.log('Final role string after auto-assignment:', userData.role);
  console.log('=== END ROLE LOGIC ===');
  
  // Debug: Kiểm tra xem roles có đúng không
  console.log('🔍 DEBUG: userData.roles contains ADMIN_HOTELS:', userData.roles.includes('ADMIN_HOTELS'));
  console.log('🔍 DEBUG: userData.roles contains HOTEL_SUPPLIER:', userData.roles.includes('HOTEL_SUPPLIER'));
  console.log('🔍 DEBUG: userData.roles contains ADMIN_FLIGHTS:', userData.roles.includes('ADMIN_FLIGHTS'));
  console.log('🔍 DEBUG: userData.roles contains FLIGHT_SUPPLIER:', userData.roles.includes('FLIGHT_SUPPLIER'));

  // Thêm các trường tùy chọn nếu có giá trị
  if (newUser.value.phone && newUser.value.phone.trim() !== '') {
    userData.phone = newUser.value.phone.trim();
  }
  
  if (newUser.value.birthday && newUser.value.birthday.trim() !== '') {
    // Chuyển đổi định dạng ngày từ YYYY-MM-DD sang ISO string
    const date = new Date(newUser.value.birthday);
    if (!isNaN(date.getTime())) {
      userData.birthday = date.toISOString();
    }
  }
  
  if (newUser.value.gender && newUser.value.gender.trim() !== '') {
    userData.gender = newUser.value.gender.trim();
  }
  
  if (newUser.value.address && newUser.value.address.trim() !== '') {
    userData.address = newUser.value.address.trim();
  }

  console.log('Original roles selected:', newUser.value.roles);
  console.log('Final roles to be sent:', userData.roles);
  console.log('Final role string to be sent:', userData.role);
  console.log('Sending user data:', userData); // Debug log
  
  // Kiểm tra cuối cùng trước khi gửi
  if (userData.roles.includes('ADMIN_HOTELS') && !userData.roles.includes('HOTEL_SUPPLIER')) {
    console.error('❌ ERROR: ADMIN_HOTELS selected but HOTEL_SUPPLIER not found in final roles!');
  } else if (userData.roles.includes('ADMIN_HOTELS') && userData.roles.includes('HOTEL_SUPPLIER')) {
    console.log('✅ SUCCESS: ADMIN_HOTELS and HOTEL_SUPPLIER both present in final roles');
  }
  
  if (userData.roles.includes('ADMIN_FLIGHTS') && !userData.roles.includes('FLIGHT_SUPPLIER')) {
    console.error('❌ ERROR: ADMIN_FLIGHTS selected but FLIGHT_SUPPLIER not found in final roles!');
  } else if (userData.roles.includes('ADMIN_FLIGHTS') && userData.roles.includes('FLIGHT_SUPPLIER')) {
    console.log('✅ SUCCESS: ADMIN_FLIGHTS and FLIGHT_SUPPLIER both present in final roles');
  }
  
  if (userData.roles.includes('ADMIN_TOURS') && !userData.roles.includes('TOUR_SUPPLIER')) {
    console.error('❌ ERROR: ADMIN_TOURS selected but TOUR_SUPPLIER not found in final roles!');
  } else if (userData.roles.includes('ADMIN_TOURS') && userData.roles.includes('TOUR_SUPPLIER')) {
    console.log('✅ SUCCESS: ADMIN_TOURS and TOUR_SUPPLIER both present in final roles');
  }
  
  if (userData.roles.includes('ADMIN_BUSES') && !userData.roles.includes('BUS_SUPPLIER')) {
    console.error('❌ ERROR: ADMIN_BUSES selected but BUS_SUPPLIER not found in final roles!');
  } else if (userData.roles.includes('ADMIN_BUSES') && userData.roles.includes('BUS_SUPPLIER')) {
    console.log('✅ SUCCESS: ADMIN_BUSES and BUS_SUPPLIER both present in final roles');
  }
  
  createUser(userData);
};

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
};

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
};

const goToPage = (page) => {
  if (page !== '...' && page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
};

const getRoleClass = (role) => {
  const map = {
    'USER': 'bg-gray-100 text-gray-800',
    'ADMIN_HOTELS': 'bg-blue-100 text-blue-800',
    'HOTEL_SUPPLIER': 'bg-blue-100 text-blue-800', // Gộp với ADMIN_HOTELS
    'ADMIN_FLIGHTS': 'bg-purple-100 text-purple-800',
    'FLIGHT_SUPPLIER': 'bg-purple-100 text-purple-800', // Gộp với ADMIN_FLIGHTS
    'ADMIN_TOURS': 'bg-yellow-100 text-yellow-800',
    'TOUR_SUPPLIER': 'bg-yellow-100 text-yellow-800', // Gộp với ADMIN_TOURS
    'ADMIN_BUSES': 'bg-pink-100 text-pink-800',
    'BUS_SUPPLIER': 'bg-pink-100 text-pink-800', // Gộp với ADMIN_BUSES
    'SUPER_ADMIN': 'bg-red-100 text-red-800'
  };
  return map[role] || 'bg-gray-100 text-gray-800';
};

// Hàm gộp các role tương tự để tránh hiển thị trùng lặp
const getUniqueRoles = (roles) => {
  console.log('🔍 getUniqueRoles input:', roles);
  
  // Nhóm các role tương tự - chỉ giữ lại role chính
  const roleGroups = {
    'HOTEL': ['ADMIN_HOTELS', 'HOTEL_SUPPLIER'],
    'FLIGHT': ['ADMIN_FLIGHTS', 'FLIGHT_SUPPLIER'],
    'TOUR': ['ADMIN_TOURS', 'TOUR_SUPPLIER'],
    'BUS': ['ADMIN_BUSES', 'BUS_SUPPLIER']
  };
  
  const uniqueRoles = [];
  const processedGroups = new Set();
  
  for (const role of roles) {
    let isGroupRole = false;
    
    // Kiểm tra xem role có thuộc nhóm nào không
    for (const [groupKey, groupRoles] of Object.entries(roleGroups)) {
      if (groupRoles.includes(role)) {
        isGroupRole = true;
        
        // Nếu nhóm này chưa được xử lý, thêm role chính (ADMIN_*)
        if (!processedGroups.has(groupKey)) {
          uniqueRoles.push(groupRoles[0]); // Lấy ADMIN_* thay vì SUPPLIER
          processedGroups.add(groupKey);
          console.log(`✅ Gộp role ${role} vào nhóm ${groupKey}, thêm ${groupRoles[0]}`);
        } else {
          console.log(`⏭️ Bỏ qua role ${role} vì nhóm ${groupKey} đã được xử lý`);
        }
        break;
      }
    }
    
    // Nếu không thuộc nhóm nào, thêm trực tiếp
    if (!isGroupRole) {
      uniqueRoles.push(role);
      console.log(`➕ Thêm role trực tiếp: ${role}`);
    }
  }
  
  console.log('🎯 getUniqueRoles output:', uniqueRoles);
  return uniqueRoles;
};

const getRoleLabel = (role) => {
  // Gộp các role tương tự
  const roleMap = {
    'HOTEL_SUPPLIER': 'Admin Khách sạn',
    'FLIGHT_SUPPLIER': 'Admin Chuyến bay',
    'TOUR_SUPPLIER': 'Admin Tour',
    'BUS_SUPPLIER': 'Admin Xe buýt'
  };
  
  if (roleMap[role]) {
    return roleMap[role];
  }
  
  const roleObj = availableRoles.find(r => r.value === role);
  return roleObj ? roleObj.label : role;
};

const getStatusClass = (status) => {
  const map = {
    'ACTIVE': 'bg-green-100 text-green-800',
    'INACTIVE': 'bg-yellow-100 text-yellow-800'
  };
  return map[status] || 'bg-gray-100 text-gray-800';
};

const getStatusLabel = (status) => {
  const map = {
    'ACTIVE': 'Đang hoạt động',
    'INACTIVE': 'Vô hiệu hóa'
  };
  return map[status] || status;
};

const getGenderLabel = (gender) => {
  const map = {
    'MALE': 'Nam',
    'FEMALE': 'Nữ',
    'OTHER': 'Khác'
  };
  return map[gender] || gender;
};

const formatCurrency = (value) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(value || 0);

const formatDate = (dateString) => {
  if (!dateString) return 'N/A';
  try {
    return new Date(dateString).toLocaleDateString("vi-VN");
  } catch (error) {
    return 'N/A';
  }
};

// Hàm format ngày cho input type="date" (YYYY-MM-DD)
const formatDateForInput = (dateString) => {
  console.log('formatDateForInput input:', dateString, typeof dateString);
  if (!dateString) return '';
  try {
    // Xử lý nếu dateString là array (từ backend có thể trả về array)
    if (Array.isArray(dateString) && dateString.length >= 3) {
      // Assuming [year, month, day] format
      const year = dateString[0];
      const month = String(dateString[1]).padStart(2, '0');
      const day = String(dateString[2]).padStart(2, '0');
      const result = `${year}-${month}-${day}`;
      console.log('formatDateForInput array result:', result);
      return result;
    }
    
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return '';
    const result = date.toISOString().split('T')[0]; // Trả về YYYY-MM-DD
    console.log('formatDateForInput date result:', result);
    return result;
  } catch (error) {
    console.log('formatDateForInput error:', error);
    return '';
  }
};

// Hàm cập nhật ngày sinh khi edit
const updateBirthday = (event) => {
  if (isEditing.value) {
    const dateValue = event.target.value;
    if (dateValue) {
      // Chuyển từ YYYY-MM-DD sang ISO string
      const date = new Date(dateValue);
      currentUser.value.birthday = date.toISOString();
    } else {
      currentUser.value.birthday = null;
    }
  }
};

// Watch for filter changes to reset pagination
const resetPagination = () => {
  currentPage.value = 1;
};

// API functions
const fetchUsers = async () => {
  // Kiểm tra quyền trước khi gọi API
  if (!userStore.user || !userStore.user.roles) {
    error.value = 'Không thể xác thực người dùng. Vui lòng đăng nhập lại.';
    return;
  }
  
  const hasSuperAdminRole = userStore.user.roles.some(role => role === 'SUPER_ADMIN');
  if (!hasSuperAdminRole) {
    error.value = 'Bạn không có quyền truy cập dữ liệu này.';
    return;
  }
  
  loading.value = true;
  error.value = null;
  try {
    // Xử lý role filter để gửi lên API
    let apiRoleFilter = roleFilter.value;
    if (roleFilter.value === 'ADMIN_HOTELS') {
      apiRoleFilter = 'ADMIN_HOTELS,HOTEL_SUPPLIER';
    } else if (roleFilter.value === 'ADMIN_FLIGHTS') {
      apiRoleFilter = 'ADMIN_FLIGHTS,FLIGHT_SUPPLIER';
    } else if (roleFilter.value === 'ADMIN_TOURS') {
      apiRoleFilter = 'ADMIN_TOURS,TOUR_SUPPLIER';
    } else if (roleFilter.value === 'ADMIN_BUSES') {
      apiRoleFilter = 'ADMIN_BUSES,BUS_SUPPLIER';
    }
    
    // Khi có filter, luôn load tất cả users để frontend có thể filter và paginate chính xác
    const response = await userManagementApi.getAllUsers({
      search: searchQuery.value,
      role: apiRoleFilter,
      status: statusFilter.value,
      page: 0, // Luôn bắt đầu từ trang 0
      size: 1000 // Load tất cả users
    });
    
         if (response.statusCode === 200) {
       users.value = response.data.content || response.data;
       totalUsersCount.value = response.data.totalElements || response.data.total; // Lấy tổng số users từ API
       
               // Debug: Kiểm tra dữ liệu users
        console.log('Users loaded:', users.value);
        if (users.value.length > 0) {
          const firstUser = users.value[0];
          console.log('First user data:', firstUser);
          console.log('First user birthday:', firstUser.birthday);
          console.log('All user fields:', Object.keys(firstUser));
          console.log('User data structure:', JSON.stringify(firstUser, null, 2));
        }
     } else {
      error.value = response.message || 'Có lỗi xảy ra khi tải dữ liệu';
    }
  } catch (err) {
    console.error('UserManagement - Error fetching users:', err);
    console.error('UserManagement - Error details:', err.response?.data);
    console.error('UserManagement - Error status:', err.response?.status);
    
    // Xử lý lỗi 500 (Internal Server Error)
    if (err.response?.status === 500) {
      error.value = 'Lỗi hệ thống. Vui lòng thử lại sau hoặc liên hệ quản trị viên.';
    } else {
      error.value = 'Không thể kết nối đến server';
    }
  } finally {
    loading.value = false;
  }
};

const fetchStatistics = async () => {
  // Kiểm tra quyền trước khi gọi API
  if (!userStore.user || !userStore.user.roles) {
    return;
  }
  
  const hasSuperAdminRole = userStore.user.roles.some(role => role === 'SUPER_ADMIN');
  if (!hasSuperAdminRole) {
    return;
  }
  
  try {
    const response = await userManagementApi.getUserStatistics();
    
    if (response.statusCode === 200) {
      statistics.value = response.data;
    }
  } catch (err) {
    console.error('Error fetching statistics:', err);
    console.error('Error response:', err.response?.data);
    
    // Xử lý lỗi 500 (Internal Server Error)
    if (err.response?.status === 500) {
      console.error('System error when fetching statistics');
    }
  }
};

const createUser = async (userData) => {
  // Kiểm tra quyền trước khi gọi API
  if (!userStore.user || !userStore.user.roles) {
    error.value = 'Không thể xác thực người dùng. Vui lòng đăng nhập lại.';
    return;
  }
  
  const hasSuperAdminRole = userStore.user.roles.some(role => role === 'SUPER_ADMIN');
  if (!hasSuperAdminRole) {
    error.value = 'Bạn không có quyền tạo người dùng mới.';
    return;
  }
  
  try {
    console.log('Creating user with data:', userData);
    const response = await userManagementApi.createUser(userData);
    console.log('API response:', response);
    
    if (response.statusCode === 200 || response.statusCode === 201) {
      await fetchUsers(); // Refresh the list
      showAddUserModal.value = false;
      // Reset form
      newUser.value = {
        name: "",
        email: "",
        phone: "",
        password: "",
        roles: [],
        address: "",
        birthday: "", // Sửa từ birthDate thành birthday
        gender: ""
      };
      // Clear error and show success message
      error.value = null;
      successMessage.value = 'Người dùng đã được tạo thành công!';
      
      // Auto hide success message after 3 seconds
      setTimeout(() => {
        successMessage.value = '';
      }, 3000);
    } else {
      error.value = response.message || 'Có lỗi xảy ra khi tạo người dùng';
    }
  } catch (err) {
    console.error('Error creating user:', err);
    console.error('Error response:', err.response?.data);
    console.error('Error status:', err.response?.status);
    
    // Xử lý lỗi 500 (Internal Server Error)
    if (err.response?.status === 500) {
      error.value = 'Lỗi hệ thống. Vui lòng thử lại sau hoặc liên hệ quản trị viên.';
      return;
    }
    
    // Log chi tiết lỗi validation nếu có
    if (err.response?.data?.errors) {
      console.error('Validation errors:', err.response.data.errors);
      
      // Hiển thị lỗi validation chi tiết
      const errorMessages = [];
      Object.entries(err.response.data.errors).forEach(([field, message]) => {
        const fieldMap = {
          'name': 'Tên',
          'email': 'Email',
          'password': 'Mật khẩu',
          'role': 'Vai trò',
          'roles': 'Vai trò'
        };
        const fieldName = fieldMap[field] || field;
        errorMessages.push(`${fieldName}: ${message}`);
      });
      
      if (errorMessages.length > 0) {
        error.value = errorMessages.join(', ');
        return;
      }
    }
    
    // Hiển thị lỗi chi tiết từ server nếu có
    if (err.response?.data?.message) {
      error.value = err.response.data.message;
    } else if (err.response?.data?.error) {
      error.value = err.response.data.error;
    } else {
      error.value = 'Không thể tạo người dùng. Vui lòng kiểm tra lại thông tin.';
    }
  }
};

const updateUser = async (userData) => {
  // Kiểm tra quyền trước khi gọi API
  if (!userStore.user || !userStore.user.roles) {
    error.value = 'Không thể xác thực người dùng. Vui lòng đăng nhập lại.';
    return;
  }
  
  const hasSuperAdminRole = userStore.user.roles.some(role => role === 'SUPER_ADMIN');
  if (!hasSuperAdminRole) {
    error.value = 'Bạn không có quyền cập nhật người dùng.';
    return;
  }
  
  // Kiểm tra tên (ít nhất 2 ký tự)
  if (!userData.name || userData.name.trim().length < 2) {
    error.value = 'Tên phải có ít nhất 2 ký tự';
    return;
  }

  // Kiểm tra roles
  if (!userData.roles || userData.roles.length === 0) {
    error.value = 'Vui lòng chọn ít nhất một vai trò cho người dùng';
    return;
  }
  
  try {
    // Chuẩn bị dữ liệu để gửi lên API
    const updateData = {
      id: userData.id,
      name: userData.name?.trim(),
      email: userData.email?.trim(),
      phone: userData.phone?.trim(),
      roles: userData.roles,
      role: userData.roles.join(','), // Chuyển array thành string để tương thích với backend
      status: userData.status
    };
    
    // Thêm logic gán role tự động cho updateUser
    console.log('=== UPDATE USER ROLE AUTO-ASSIGNMENT LOGIC ===');
    console.log('Original roles for update:', userData.roles);
    
    // Xử lý ADMIN_HOTELS và HOTEL_SUPPLIER
    if (userData.roles.includes('ADMIN_HOTELS')) {
      console.log('ADMIN_HOTELS detected in update, adding HOTEL_SUPPLIER');
      if (!updateData.roles.includes('HOTEL_SUPPLIER')) {
        updateData.roles.push('HOTEL_SUPPLIER');
        console.log('HOTEL_SUPPLIER added to update roles');
      } else {
        console.log('HOTEL_SUPPLIER already exists in update roles');
      }
    } else {
      // Nếu không chọn ADMIN_HOTELS thì bỏ HOTEL_SUPPLIER
      if (updateData.roles.includes('HOTEL_SUPPLIER')) {
        const index = updateData.roles.indexOf('HOTEL_SUPPLIER');
        updateData.roles.splice(index, 1);
        console.log('HOTEL_SUPPLIER removed from update roles (ADMIN_HOTELS not selected)');
      }
    }
    
    // Xử lý ADMIN_FLIGHTS và FLIGHT_SUPPLIER
    if (userData.roles.includes('ADMIN_FLIGHTS')) {
      console.log('ADMIN_FLIGHTS detected in update, adding FLIGHT_SUPPLIER');
      if (!updateData.roles.includes('FLIGHT_SUPPLIER')) {
        updateData.roles.push('FLIGHT_SUPPLIER');
        console.log('FLIGHT_SUPPLIER added to update roles');
      } else {
        console.log('FLIGHT_SUPPLIER already exists in update roles');
      }
    } else {
      // Nếu không chọn ADMIN_FLIGHTS thì bỏ FLIGHT_SUPPLIER
      if (updateData.roles.includes('FLIGHT_SUPPLIER')) {
        const index = updateData.roles.indexOf('FLIGHT_SUPPLIER');
        updateData.roles.splice(index, 1);
        console.log('FLIGHT_SUPPLIER removed from update roles (ADMIN_FLIGHTS not selected)');
      }
    }
    
    // Xử lý ADMIN_TOURS và TOUR_SUPPLIER
    if (userData.roles.includes('ADMIN_TOURS')) {
      console.log('ADMIN_TOURS detected in update, adding TOUR_SUPPLIER');
      if (!updateData.roles.includes('TOUR_SUPPLIER')) {
        updateData.roles.push('TOUR_SUPPLIER');
        console.log('TOUR_SUPPLIER added to update roles');
      } else {
        console.log('TOUR_SUPPLIER already exists in update roles');
      }
    } else {
      // Nếu không chọn ADMIN_TOURS thì bỏ TOUR_SUPPLIER
      if (updateData.roles.includes('TOUR_SUPPLIER')) {
        const index = updateData.roles.indexOf('TOUR_SUPPLIER');
        updateData.roles.splice(index, 1);
        console.log('TOUR_SUPPLIER removed from update roles (ADMIN_TOURS not selected)');
      }
    }
    
    // Xử lý ADMIN_BUSES và BUS_SUPPLIER
    if (userData.roles.includes('ADMIN_BUSES')) {
      console.log('ADMIN_BUSES detected in update, adding BUS_SUPPLIER');
      if (!updateData.roles.includes('BUS_SUPPLIER')) {
        updateData.roles.push('BUS_SUPPLIER');
        console.log('BUS_SUPPLIER added to update roles');
      } else {
        console.log('BUS_SUPPLIER already exists in update roles');
      }
    } else {
      // Nếu không chọn ADMIN_BUSES thì bỏ BUS_SUPPLIER
      if (updateData.roles.includes('BUS_SUPPLIER')) {
        const index = updateData.roles.indexOf('BUS_SUPPLIER');
        updateData.roles.splice(index, 1);
        console.log('BUS_SUPPLIER removed from update roles (ADMIN_BUSES not selected)');
      }
    }
    
    // Cập nhật field role sau khi thêm role tự động
    // Chỉ gửi role chính (ADMIN_*) vì backend chỉ nhận 1 role
    const mainRoles = updateData.roles.filter(role => 
      role.startsWith('ADMIN_') || role === 'USER' || role === 'SUPER_ADMIN'
    );
    updateData.role = mainRoles.join(',');
    
    console.log('Main roles to send to backend (update):', mainRoles);
    console.log('Role string to send to backend (update):', updateData.role);
    
    console.log('Final update roles after auto-assignment:', updateData.roles);
    console.log('Final update role string after auto-assignment:', updateData.role);
    console.log('=== END UPDATE ROLE LOGIC ===');
    
    // Thêm các trường tùy chọn nếu có giá trị
    if (userData.birthday && userData.birthday.trim() !== '') {
      const date = new Date(userData.birthday);
      if (!isNaN(date.getTime())) {
        updateData.birthday = date.toISOString();
      }
    }
    
    if (userData.gender && userData.gender.trim() !== '') {
      updateData.gender = userData.gender.trim();
    }
    
    if (userData.address && userData.address.trim() !== '') {
      updateData.address = userData.address.trim();
    }
    
    console.log('Updating user with data:', updateData);
    const response = await userManagementApi.updateUser(userData.id, updateData);
    if (response.statusCode === 200) {
      await fetchUsers(); // Refresh the list
      closeModal();
      // Show success message
      successMessage.value = 'Người dùng đã được cập nhật thành công!';
      
      // Auto hide success message after 3 seconds
      setTimeout(() => {
        successMessage.value = '';
      }, 3000);
    } else {
      error.value = response.message || 'Có lỗi xảy ra khi cập nhật người dùng';
    }
  } catch (err) {
    console.error('Error updating user:', err);
    console.error('Error response:', err.response?.data);
    
    // Xử lý lỗi 500 (Internal Server Error)
    if (err.response?.status === 500) {
      error.value = 'Lỗi hệ thống. Vui lòng thử lại sau hoặc liên hệ quản trị viên.';
      return;
    }
    
    // Hiển thị lỗi validation chi tiết nếu có
    if (err.response?.data?.errors) {
      const errorMessages = [];
      Object.entries(err.response.data.errors).forEach(([field, message]) => {
        const fieldMap = {
          'name': 'Tên',
          'email': 'Email',
          'role': 'Vai trò',
          'roles': 'Vai trò'
        };
        const fieldName = fieldMap[field] || field;
        errorMessages.push(`${fieldName}: ${message}`);
      });
      
      if (errorMessages.length > 0) {
        error.value = errorMessages.join(', ');
        return;
      }
    }
    
    // Hiển thị lỗi chung
    if (err.response?.data?.message) {
      error.value = err.response.data.message;
    } else {
      error.value = 'Không thể cập nhật người dùng';
    }
  }
};

const updateUserStatus = async (user, newStatus) => {
  // Kiểm tra quyền trước khi gọi API
  if (!userStore.user || !userStore.user.roles) {
    error.value = 'Không thể xác thực người dùng. Vui lòng đăng nhập lại.';
    return;
  }
  
  const hasSuperAdminRole = userStore.user.roles.some(role => role === 'SUPER_ADMIN');
  if (!hasSuperAdminRole) {
    error.value = 'Bạn không có quyền cập nhật trạng thái người dùng.';
    return;
  }
  
  try {
    const response = await userManagementApi.updateUserStatus(user.id, newStatus);
    if (response.statusCode === 200) {
      await fetchUsers(); // Refresh the list
      // Show success message
      const statusText = newStatus === 'ACTIVE' ? 'kích hoạt' : 'vô hiệu hóa';
      successMessage.value = `Người dùng đã được ${statusText} thành công!`;
      
      // Auto hide success message after 3 seconds
      setTimeout(() => {
        successMessage.value = '';
      }, 3000);
    } else {
      error.value = response.message || 'Có lỗi xảy ra khi cập nhật trạng thái';
    }
  } catch (err) {
    console.error('Error updating user status:', err);
    console.error('Error response:', err.response?.data);
    
    // Xử lý lỗi 500 (Internal Server Error)
    if (err.response?.status === 500) {
      error.value = 'Lỗi hệ thống. Vui lòng thử lại sau hoặc liên hệ quản trị viên.';
      return;
    }
    
    if (err.response?.data?.message) {
      error.value = err.response.data.message;
    } else {
      error.value = 'Không thể cập nhật trạng thái người dùng';
    }
  }
};

// Watch for filter changes to reset pagination and fetch data
watch([searchQuery, roleFilter, statusFilter], () => {
  currentPage.value = 1;
  fetchUsers();
  // Clear success message when filters change
  if (successMessage.value) {
    successMessage.value = null;
  }
});

// Watch for newUser changes to clear errors
watch([() => newUser.value.name, () => newUser.value.email, () => newUser.value.password, () => newUser.value.roles], () => {
  if (error.value) {
    error.value = null;
  }
  if (successMessage.value) {
    successMessage.value = null;
  }
});

// Watch for itemsPerPageStr changes
watch(itemsPerPageStr, (newValue, oldValue) => {
  currentPage.value = 1;
});

// Lifecycle
onMounted(() => {
  // Kiểm tra quyền truy cập
  if (!userStore.user || !userStore.user.roles) {
    error.value = 'Không thể xác thực người dùng. Vui lòng đăng nhập lại.';
    return;
  }
  
  const hasSuperAdminRole = userStore.user.roles.some(role => role === 'SUPER_ADMIN');
  if (!hasSuperAdminRole) {
    error.value = 'Bạn không có quyền truy cập trang này. Chỉ Super Admin mới có thể quản lý người dùng.';
    return;
  }
  
  fetchUsers();
  fetchStatistics();
});
</script>

<style scoped>
/* Custom scrollbar for table */
.overflow-x-auto::-webkit-scrollbar {
  height: 8px;
}

.overflow-x-auto::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.overflow-x-auto::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.overflow-x-auto::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* Đảm bảo modal có góc bo tròn */
.modal-container {
  border-radius: 24px !important;
  overflow: hidden;
}
</style>

