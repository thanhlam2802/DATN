<template>
  <div class="w-full p-6">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-slate-800 mb-4">Danh sách booking</h1>
      <div class="flex flex-col sm:flex-row items-center gap-2 mb-4">
        <div class="relative w-full sm:w-[300px]">
          <input type="text" v-model="searchQuery" placeholder="Tìm theo tên khách hàng hoặc khách sạn"
            class="w-full sm:w-[350px] pl-10 pr-4 py-2 h-12 text-base border border-slate-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-slate-900" />
          <i class="fas fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400"></i>
        </div>
      </div>
    </div>
    <div class="mb-8 bg-white rounded-xl shadow-lg border border-slate-200">
      <div class="overflow-x-auto">
        <div class="overflow-y-auto h-[385px]">
          <table class="min-w-[1000px] w-full divide-y divide-slate-200">
            <thead class="bg-slate-100">
              <tr>
                <th class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">STT</th>
                <th class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">Tên khách hàng</th>
                <th class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">Tên khách sạn</th>
                <th class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">Ngày đặt phòng</th>
                <th class="px-3 py-4 text-left text-xs font-bold text-slate-700 uppercase tracking-wider">Trạng thái</th>
                <th class="px-3 py-4 text-right text-xs font-bold text-slate-700 uppercase tracking-wider">Tổng số tiền</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-slate-100">
              <tr v-if="paginatedBookings.length === 0">
                <td colspan="6" class="px-6 py-12 text-center text-slate-500">Không tìm thấy booking nào</td>
              </tr>
              <tr v-for="(b, index) in paginatedBookings" :key="b.id" class="hover:bg-slate-50 transition-colors duration-150">
                <td class="px-3 py-5 whitespace-nowrap">
                  <div class="text-sm font-medium text-slate-700 text-center">
                    {{ (currentPage - 1) * itemsPerPage + index + 1 }}
                  </div>
                </td>
                <td class="px-3 py-5 whitespace-nowrap font-semibold text-slate-900">{{ b.customerName }}</td>
                <td class="px-3 py-5 whitespace-nowrap">{{ b.hotelName }}</td>
                <td class="px-3 py-5 whitespace-nowrap">{{ formatDateTime(b.createdAt) }}</td>
                <td class="px-3 py-5 whitespace-nowrap">
                  <span :class="statusClass(b.status) + ' px-3 py-1 rounded-full font-semibold text-xs inline-block min-w-[110px] text-center'">{{ statusLabel(b.status) }}</span>
                </td>
                <td class="px-3 py-5 whitespace-nowrap text-right font-bold text-green-700">{{ formatCurrency(b.totalPrice) }} VND</td>
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { hotelAdminApi } from '@/api/adminApi';
import CustomSelect from '@/components/CustomSelect.vue';
import { useAdminBreadcrumbStore } from '@/store/useAdminBreadcrumbStore';
import { useAdminAuth } from '@/composables/useAdminAuth';
import { useUserStore } from '@/store/UserStore';

const searchQuery = ref('');
const currentPage = ref(1);
const itemsPerPageOptions = [5, 10, 20, 50, 'Tất cả'];
const itemsPerPageStr = ref('5');
const itemsPerPage = computed(() => itemsPerPageStr.value === 'Tất cả' ? filteredBookings.value.length : Number(itemsPerPageStr.value));

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
    if (res.data && res.data.data) {
      bookings.value = res.data.data;
    } else {
      bookings.value = [];
    }
  } catch (e) {
    bookings.value = [];
  }
});

const filteredBookings = computed(() => {
  let arr = bookings.value.slice();
  arr.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
  if (!searchQuery.value) return arr;
  const q = searchQuery.value.toLowerCase();
  return arr.filter(b =>
    (b.customerName || '').toLowerCase().includes(q) ||
    (b.hotelName || '').toLowerCase().includes(q)
  );
});

const totalPages = computed(() => Math.ceil(filteredBookings.value.length / (itemsPerPage.value || 1)));
const paginatedBookings = computed(() => {
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
    default: return status;
  }
}
function statusClass(status) {
  switch (status) {
    case 'PENDING_PAYMENT': return 'bg-yellow-100 text-yellow-800 border border-yellow-300';
    case 'PAID': return 'bg-green-100 text-green-800 border border-green-300';
    case 'CANCELLED': return 'bg-red-100 text-red-700 border border-red-300';
    default: return 'bg-gray-100 text-gray-700 border border-gray-300';
  }
}
</script> 