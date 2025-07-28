<template>
  <div class="w-full p-6">
    <div v-if="mode === 'list'">
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-slate-800 mb-4">Danh sách khách hàng đặt khách sạn</h1>
        <div class="flex flex-col sm:flex-row items-center gap-2">
          <div class="flex flex-1 flex-col sm:flex-row items-center gap-2 w-full">
            <div class="relative w-full sm:w-[300px]">
              <input type="text" v-model="searchQuery" placeholder="Tìm theo tên, email hoặc SĐT"
                class="w-full sm:w-[350px] pl-10 pr-4 py-2 h-12 text-base border border-slate-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-slate-900" />
              <i class="fas fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="mb-8 bg-white rounded-xl shadow-lg border border-slate-200">
        <div class="overflow-x-auto">
          <table class="min-w-[1000px] w-full divide-y divide-slate-200">
            <thead class="bg-slate-100">
              <tr>
                <th class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">STT</th>
                <th class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">Tên khách hàng</th>
                <th class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">Email</th>
                <th class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">Số điện thoại</th>
                <th class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">Số lượt đặt</th>
                <th class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">Ngày đặt phòng gần nhất</th>
                <th class="px-3 py-4 text-right text-xs font-bold text-slate-700 uppercase tracking-wider">Hành động</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-slate-100">
              <tr v-if="paginatedCustomers.length === 0">
                <td colspan="7" class="px-6 py-12 text-center text-slate-500">Không tìm thấy khách hàng nào</td>
              </tr>
              <tr v-for="(c, index) in paginatedCustomers" :key="c.id" class="hover:bg-slate-50 transition-colors duration-150 cursor-pointer" @click="viewCustomer(c)">
                <td class="px-3 py-5 whitespace-nowrap">
                  <div class="text-sm font-medium text-slate-700 text-center">
                    {{ (currentPage - 1) * itemsPerPage + index + 1 }}
                  </div>
                </td>
                <td class="px-3 py-5 whitespace-nowrap font-semibold text-slate-900">{{ c.fullName }}</td>
                <td class="px-3 py-5 whitespace-nowrap">{{ c.email }}</td>
                <td class="px-3 py-5 whitespace-nowrap">{{ c.phone }}</td>
                <td class="px-3 py-5 whitespace-nowrap">{{ c.bookingCount ?? 0 }}</td>
                <td class="px-3 py-5 whitespace-nowrap">{{ formatDateTime(c.latestBookingDate) }}</td>
                <td class="px-3 py-5 whitespace-nowrap text-right align-middle">
                  <div class="relative inline-block text-left flex items-center justify-end h-full">
                    <button :ref="el => setDropdownBtnRef(el, c.id)"
                      @click.stop="toggleDropdown(c.id)" type="button"
                      class="inline-flex justify-center items-center w-10 h-10 rounded-lg text-slate-500 hover:text-slate-700 hover:bg-slate-100 focus:outline-none transition-all duration-200 shadow-sm">
                      <i class="fas fa-ellipsis-v flex items-center justify-center text-sm"></i>
                    </button>
                    <teleport to="body">
                      <div v-if="activeDropdown === c.id"
                        :style="{ position: 'absolute', top: dropdownMenuPosition.top + 'px', left: dropdownMenuPosition.left + 'px', right: 'auto', zIndex: 9999 }"
                        class="min-w-40 bg-white border border-slate-200 rounded-lg shadow-lg">
                        <button class="block w-full text-left px-4 py-2 hover:bg-blue-50"
                          @click.stop="viewCustomer(c)"><i class="fas fa-eye mr-2"></i>Xem chi tiết</button>
                        <button class="block w-full text-left px-4 py-2 hover:bg-blue-50"
                          @click.stop="editCustomer(c)"><i class="fas fa-edit mr-2"></i>Chỉnh sửa</button>
                      </div>
                    </teleport>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
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
    </div>
    <div v-else-if="mode === 'detail'">
      <div class="mb-6">
        <div class="flex border-b border-slate-200 mb-6">
          <button :class="['px-6 py-3 font-semibold', activeTab === 0 ? 'border-b-2 border-blue-600 text-blue-600' : 'text-slate-700']" @click="activeTab = 0">Chi tiết</button>
          <button :class="['px-6 py-3 font-semibold', activeTab === 1 ? 'border-b-2 border-blue-600 text-blue-600' : 'text-slate-700']" @click="activeTab = 1">Lịch sử đặt phòng</button>
        </div>
        <div v-if="activeTab === 0">
          <div class="bg-white rounded-2xl shadow-xl border border-slate-200 p-10 w-full max-w-4xl mx-auto relative">
            <div class="flex justify-between items-center mb-8">
              <h2 class="text-xl font-bold">Thông tin khách hàng</h2>
              <div class="flex items-center gap-2">
                <button @click="enableEditMode" class="flex items-center justify-center w-10 h-10 rounded-lg text-slate-600 hover:text-blue-600 hover:bg-slate-100 transition-colors duration-200" title="Chỉnh sửa">
                  <i class="fas fa-pencil-alt text-xl"></i>
                </button>
                <button @click="handleBack" class="flex items-center justify-center w-10 h-10 rounded-lg text-slate-600 hover:text-blue-600 hover:bg-slate-100 transition-colors duration-200" title="Quay lại">
                  <i class="fas fa-arrow-left text-xl"></i>
                </button>
              </div>
            </div>
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-8">
              <div>
                <label class="block text-sm font-semibold text-slate-700 mb-1">Họ tên</label>
                <input :value="selectedCustomer.fullName || '--'" disabled class="w-full border border-slate-300 rounded-md px-3 py-2 text-base text-slate-900 bg-gray-100 cursor-not-allowed" />
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-700 mb-1">Email</label>
                <input :value="selectedCustomer.email || '--'" disabled class="w-full border border-slate-300 rounded-md px-3 py-2 text-base text-slate-900 bg-gray-100 cursor-not-allowed" />
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-700 mb-1">Số điện thoại</label>
                <input :value="selectedCustomer.phone || '--'" disabled class="w-full border border-slate-300 rounded-md px-3 py-2 text-base text-slate-900 bg-gray-100 cursor-not-allowed" />
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-700 mb-1">Ngày sinh</label>
                <input :value="selectedCustomer.dob || '--'" disabled class="w-full border border-slate-300 rounded-md px-3 py-2 text-base text-slate-900 bg-gray-100 cursor-not-allowed" />
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-700 mb-1">Số hộ chiếu</label>
                <input :value="selectedCustomer.passport || '--'" disabled class="w-full border border-slate-300 rounded-md px-3 py-2 text-base text-slate-900 bg-gray-100 cursor-not-allowed" />
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-700 mb-1">Giới tính</label>
                <input :value="selectedCustomer.gender === true ? 'Nam' : selectedCustomer.gender === false ? 'Nữ' : '--'" disabled class="w-full border border-slate-300 rounded-md px-3 py-2 text-base text-slate-900 bg-gray-100 cursor-not-allowed" />
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-700 mb-1">Số lượt đặt</label>
                <input :value="selectedCustomer.bookingCount != null ? selectedCustomer.bookingCount : '--'" disabled class="w-full border border-slate-300 rounded-md px-3 py-2 text-base text-slate-900 bg-gray-100 cursor-not-allowed" />
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-700 mb-1">Ngày đặt phòng gần nhất</label>
                <input :value="formatDateTime(selectedCustomer.latestBookingDate) || '--'" disabled class="w-full border border-slate-300 rounded-md px-3 py-2 text-base text-slate-900 bg-gray-100 cursor-not-allowed" />
              </div>
            </div>
          </div>
        </div>
        <div v-else-if="activeTab === 1">
          <div class="bg-white rounded-2xl shadow-xl border border-slate-200 p-10 w-full max-w-6xl mx-auto">
            <div class="flex justify-between items-center mb-8">
              <h2 class="text-xl font-bold mb-6">Lịch sử đặt phòng</h2>
              <button @click="handleBack" class="flex items-center justify-center w-10 h-10 rounded-lg text-slate-600 hover:text-blue-600 hover:bg-slate-100 transition-colors duration-200" title="Quay lại">
                <i class="fas fa-arrow-left text-xl"></i>
              </button>
            </div>
            <div>
              <table class="min-w-full w-full border-separate border-spacing-0">
                <thead>
                  <tr class="bg-slate-100">
                    <th class="px-6 py-4 text-left text-base font-bold text-slate-700 uppercase rounded-tl-2xl">Ngày đặt</th>
                    <th class="px-6 py-4 text-left text-base font-bold text-slate-700 uppercase">Tên khách sạn</th>
                    <th class="px-6 py-4 text-left text-base font-bold text-slate-700 uppercase">Loại phòng</th>
                    <th class="px-6 py-4 text-left text-base font-bold text-slate-700 uppercase rounded-tr-2xl">Tên gói phòng</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="bookedRooms.length === 0">
                    <td colspan="4" class="px-6 py-12 text-center text-slate-500">Khách hàng chưa đặt phòng nào</td>
                  </tr>
                  <tr v-for="(r, idx) in bookedRooms" :key="idx" class="hover:bg-slate-50 transition-colors border-b border-slate-100">
                    <td class="px-6 py-4 whitespace-nowrap text-slate-900 font-semibold">{{ formatDateTime(r.createdAt) }}</td>
                    <td class="px-6 py-4 whitespace-nowrap">{{ r.hotelName }}</td>
                    <td class="px-6 py-4 whitespace-nowrap">{{ r.roomType }}</td>
                    <td class="px-6 py-4 whitespace-nowrap">{{ r.variantName }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-else-if="mode === 'edit'">
      <div class="mb-6">
        <div class="bg-white rounded-2xl shadow-xl border border-slate-200 p-10 w-full max-w-4xl mx-auto relative">
          <div class="flex justify-between items-center mb-8">
            <h2 class="text-xl font-bold">Chỉnh sửa thông tin khách hàng</h2>
            <div class="flex items-center gap-2">
              <button @click="handleBack" class="flex items-center justify-center w-10 h-10 rounded-lg text-slate-600 hover:text-blue-600 hover:bg-slate-100 transition-colors duration-200" title="Quay lại">
                <i class="fas fa-arrow-left text-xl"></i>
              </button>
            </div>
          </div>
          <form @submit.prevent="handleSave" class="grid grid-cols-1 sm:grid-cols-2 gap-8">
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1">Họ tên</label>
              <input v-model="editForm.fullName" type="text" class="w-full border border-slate-300 rounded-md px-3 py-2 text-base text-slate-900" />
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1">Email</label>
              <input v-model="editForm.email" type="email" class="w-full border border-slate-300 rounded-md px-3 py-2 text-base text-slate-900" />
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1">Số điện thoại</label>
              <input v-model="editForm.phone" type="text" class="w-full border border-slate-300 rounded-md px-3 py-2 text-base text-slate-900" />
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1">Ngày sinh</label>
              <input v-model="editForm.dob" type="date" class="w-full border border-slate-300 rounded-md px-3 py-2 text-base text-slate-900" />
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1">Số hộ chiếu</label>
              <input v-model="editForm.passport" type="text" placeholder="Nhập số hộ chiếu" class="w-full border border-slate-300 rounded-md px-3 py-2 text-base text-slate-900" />
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1">Giới tính</label>
              <CustomSelect v-model="editForm.gender" :options="genderOptions" placeholder="Chọn giới tính" />
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1">Số lượt đặt</label>
              <input :value="selectedCustomer.bookingCount != null ? selectedCustomer.bookingCount : '--'" disabled class="w-full border border-slate-300 rounded-md px-3 py-2 text-base text-slate-900 bg-gray-100 cursor-not-allowed" />
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-700 mb-1">Ngày đặt phòng gần nhất</label>
              <input :value="formatDateTime(selectedCustomer.latestBookingDate) || '--'" disabled class="w-full border border-slate-300 rounded-md px-3 py-2 text-base text-slate-900 bg-gray-100 cursor-not-allowed" />
            </div>
            <div class="col-span-2 flex justify-end mt-8">
              <button type="submit" class="font-semibold text-white bg-black rounded-md px-6 py-2 hover:bg-gray-800 transition shadow-sm">Lưu</button>
              <button type="button" @click="cancelEdit" class="ml-3 font-semibold text-slate-700 bg-slate-100 rounded-md px-6 py-2 hover:bg-slate-200 transition shadow-sm">Hủy</button>
            </div>
          </form>
        </div>
      </div>
    </div>
    <ConfirmDialog v-if="showConfirmDialog" :message="confirmMessage" @confirm="onConfirmDelete" @cancel="showConfirmDialog = false" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick, onUnmounted } from 'vue';
import hotelApi from '@/api/hotelApi';
import * as customerApi from '@/api/CustomerApi';
import CustomSelect from '@/components/CustomSelect.vue';
import ConfirmDialog from '@/components/ConfirmDialog.vue';
import { useAdminBreadcrumbStore } from '@/store/useAdminBreadcrumbStore';

const editForm = ref({});
const searchQuery = ref('');
const itemsPerPageStr = ref('10');
const itemsPerPageOptions = [5, 10, 20, 50, 'Tất cả'];
const currentPage = ref(1);

const customers = ref([]);
const loading = ref(false);
const error = ref('');

const mode = ref('list');

function viewCustomer(c) {
  mode.value = 'detail';
  selectedCustomer.value = { ...c };
  editForm.value = { ...c };
  activeTab.value = 0;
  isEditMode.value = false;
  isViewMode.value = true;
  fromDetailView.value = false;
  updateBreadcrumb();
  fetchBookedRooms(c.id);
}
function enableEditMode() {
  mode.value = 'edit';
  isEditMode.value = true;
  isViewMode.value = false;
  fromDetailView.value = true;
  updateBreadcrumb();
}
function editCustomer(c) {
  mode.value = 'edit';
  selectedCustomer.value = { ...c };
  editForm.value = { ...c };
  isEditMode.value = true;
  isViewMode.value = false;
  fromDetailView.value = false;
  activeTab.value = 0;
  updateBreadcrumb();
}
function cancelEdit() {
  if (fromDetailView.value) {
    mode.value = 'detail';
    isEditMode.value = false;
    isViewMode.value = true;
    editForm.value = { ...selectedCustomer.value };
    updateBreadcrumb();
  } else {
    mode.value = 'list';
    isEditMode.value = false;
    isViewMode.value = false;
    updateBreadcrumb();
  }
}
function handleBack() {
  if (mode.value === 'edit' && fromDetailView.value) {
    mode.value = 'detail';
    isEditMode.value = false;
    isViewMode.value = true;
    editForm.value = { ...selectedCustomer.value };
    updateBreadcrumb();
  } else if (mode.value === 'edit') {
    mode.value = 'list';
    isEditMode.value = false;
    isViewMode.value = false;
    updateBreadcrumb();
  } else {
    mode.value = 'list';
    updateBreadcrumb();
  }
}
function updateBreadcrumb() {
  const breadcrumbStore = useAdminBreadcrumbStore();
  const items = [];
  if (mode.value === 'list') {
    items.push({ label: 'Khách hàng', active: true });
  } else if (mode.value === 'detail') {
    items.push({ label: 'Khách hàng', onClick: () => { mode.value = 'list'; updateBreadcrumb(); } });
    items.push({ label: 'Chi tiết khách hàng', active: true });
  } else if (mode.value === 'edit') {
    items.push({ label: 'Khách hàng', onClick: () => { mode.value = 'list'; updateBreadcrumb(); } });
    if (fromDetailView.value) {
      items.push({ label: 'Chi tiết khách hàng', onClick: cancelEdit });
      items.push({ label: 'Chỉnh sửa khách hàng', active: true });
    } else {
      items.push({ label: 'Chỉnh sửa khách hàng', active: true });
    }
  }
  breadcrumbStore.setBreadcrumb(items);
}

const showCustomerFormModal = ref(false);
const formMode = ref('add');
const selectedCustomer = ref(null);
const showConfirmDialog = ref(false);
const confirmMessage = ref('');
const customerIdToDelete = ref(null);

const activeDropdown = ref(null);
const dropdownBtnRefMap = ref({});
const dropdownMenuPosition = ref({ top: 0, left: 0 });
const activeTab = ref(0);
const bookedRooms = ref([]);

const isEditMode = ref(false);
const isViewMode = ref(false);
const fromDetailView = ref(false);

const genderOptions = [
  { label: 'Nam', value: 'true' },
  { label: 'Nữ', value: 'false' }
];

function setDropdownBtnRef(el, id) {
  if (!dropdownBtnRefMap.value) dropdownBtnRefMap.value = {};
  if (el) {
    dropdownBtnRefMap.value[id] = el;
  } else {
    delete dropdownBtnRefMap.value[id];
  }
}
function toggleDropdown(customerId) {
  if (activeDropdown.value === customerId) {
    activeDropdown.value = null;
    return;
  }
  activeDropdown.value = customerId;
  nextTick(() => {
    const btn = dropdownBtnRefMap.value && dropdownBtnRefMap.value[customerId];
    if (btn) {
      const rect = btn.getBoundingClientRect();
      dropdownMenuPosition.value = {
        top: rect.bottom + window.scrollY - 20,
        left: rect.left + window.scrollX - 165,
      };
    }
  });
}
onMounted(() => {
  document.addEventListener('click', handleOutsideClick, true);
});
onUnmounted(() => {
  document.removeEventListener('click', handleOutsideClick, true);
});
function handleOutsideClick(event) {
  if (activeDropdown.value !== null) {
    const btn = dropdownBtnRefMap.value && dropdownBtnRefMap.value[activeDropdown.value];
    if (!btn || !btn.contains(event.target)) {
      activeDropdown.value = null;
    }
  }
}

async function fetchCustomers() {
  loading.value = true;
  error.value = '';
  try {
    const res = await hotelApi.getAllHotelCustomers();
    if (res.data && res.data.data) {
      customers.value = res.data.data;
    } else {
      customers.value = [];
    }
  } catch (e) {
    error.value = 'Không lấy được danh sách khách hàng!';
    customers.value = [];
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  fetchCustomers();
  updateBreadcrumb();
});

watch([mode, formMode], () => {
  updateBreadcrumb();
});

const filteredCustomers = computed(() => {
  if (!searchQuery.value) return customers.value;
  const q = searchQuery.value.toLowerCase();
  return customers.value.filter(c =>
    (c.fullName || '').toLowerCase().includes(q) ||
    (c.email || '').toLowerCase().includes(q) ||
    (c.phone || '').includes(q)
  );
});

const itemsPerPage = computed(() => itemsPerPageStr.value === 'Tất cả' ? filteredCustomers.value.length : Number(itemsPerPageStr.value));
const totalPages = computed(() => Math.ceil(filteredCustomers.value.length / (itemsPerPage.value || 1)));
const paginatedCustomers = computed(() => {
  if (itemsPerPageStr.value === 'Tất cả') return filteredCustomers.value;
  const start = (currentPage.value - 1) * itemsPerPage.value;
  return filteredCustomers.value.slice(start, start + itemsPerPage.value);
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

function formatDateTime(dt) {
  if (!dt) return '';
  const d = new Date(dt);
  return d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' }) + ' ' + d.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
}

function openAddCustomer() {
  formMode.value = 'add';
  selectedCustomer.value = null;
  showCustomerFormModal.value = true;
}
function handleSave() {
  if (isEditMode.value) {
    updateCustomer();
  }
}
async function updateCustomer() {
  try {
    const res = await customerApi.updateCustomer(selectedCustomer.value.id, editForm.value);
    window.$toast && window.$toast('Cập nhật khách hàng thành công!', 'success');
    fetchCustomers();
    isEditMode.value = false;
    isViewMode.value = true;
    mode.value = 'list';
    updateBreadcrumb();
  } catch (e) {
    window.$toast && window.$toast('Cập nhật khách hàng thất bại!', 'error');
  }
}
function closeCustomerFormModal() {
  showCustomerFormModal.value = false;
  if (isEditMode.value) {
    isViewMode.value = true;
    formMode.value = 'view';
    updateBreadcrumb();
  }
}
function onCustomerSaved() {
  showCustomerFormModal.value = false;
  fetchCustomers();
  if (isEditMode.value) {
    isViewMode.value = true;
    formMode.value = 'view';
    updateBreadcrumb();
  }
}
function confirmDeleteCustomer(c) {
  customerIdToDelete.value = c.id;
  confirmMessage.value = 'Bạn có chắc chắn muốn xóa khách hàng này không?';
  showConfirmDialog.value = true;
}
async function onConfirmDelete() {
  showConfirmDialog.value = false;
  try {
    await customerApi.deleteCustomer(customerIdToDelete.value);
    window.$toast && window.$toast('Xóa khách hàng thành công!', 'success');
    fetchCustomers();
  } catch (e) {
    window.$toast && window.$toast('Xóa khách hàng thất bại!', 'error');
  }
  customerIdToDelete.value = null;
}
async function fetchBookedRooms(customerId) {
  bookedRooms.value = [];
  try {
    const res = await hotelApi.getCustomerBookedRooms(customerId);
    if (res.data && res.data.data) {
      bookedRooms.value = res.data.data;
    }
  } catch (e) {
    bookedRooms.value = [];
  }
}
</script>

<style scoped>
.animate-fadeIn {
  animation: fadeIn 0.2s ease;
}
@keyframes fadeIn {
  from { opacity: 0; transform: scale(0.95); }
  to { opacity: 1; transform: scale(1); }
}
</style> 