<template>
  <div class="w-full p-6">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-slate-800 mb-4">Danh sách đánh giá khách sạn</h1>
      <div class="flex flex-col sm:flex-row items-center justify-between gap-4 mb-4">
        <div class="relative w-full sm:w-[430px]">
          <input type="text" v-model="searchQuery" placeholder="Tìm theo tên khách hàng, khách sạn hoặc nội dung"
            class="w-full pl-10 pr-4 py-2 h-12 text-base border border-slate-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-slate-900" />
          <i class="fas fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400"></i>
        </div>
        <div class="relative w-full sm:w-[180px]">
          <CustomSelect
            v-model="selectedRating"
            :options="ratingOptions"
            placeholder="Lọc theo sao"
            @update:modelValue="onRatingChange"
          />
        </div>
      </div>
    </div>
    <div class="mb-8">
      <div v-if="paginatedReviews.length === 0" class="text-center py-12">
        <div class="text-slate-400 mb-4">
          <i class="fas fa-star text-6xl"></i>
        </div>
        <h3 class="text-lg font-medium text-slate-600 mb-2">Không có đánh giá nào</h3>
        <p class="text-slate-500">Chưa có khách hàng nào đánh giá khách sạn</p>
      </div>
      
      <div v-else class="space-y-4">
        <div v-for="(review, index) in paginatedReviews" :key="review.id" 
          class="bg-white rounded-xl shadow-lg border border-slate-200 hover:shadow-xl transition-all duration-300 overflow-hidden">
          
          <div class="flex">
            <div class="w-80 p-6 border-r border-slate-100 flex-shrink-0">
              <div class="flex items-center space-x-4 mb-4">
                <div class="h-16 w-16 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center">
                  <span class="text-white font-semibold text-xl">{{ getInitials(review.customerName) }}</span>
                </div>
                <div>
                  <h3 class="font-semibold text-slate-900 text-lg">{{ review.customerName }}</h3>
                  <p class="text-sm text-slate-500">{{ review.customerEmail }}</p>
                </div>
              </div>
              
              <div class="flex items-center space-x-2">
                <div class="flex space-x-1">
                  <i v-for="star in 5" :key="star"
                    :class="[
                      'fas fa-star text-lg',
                      star <= review.rating ? 'text-yellow-400' : 'text-gray-300'
                    ]"
                  ></i>
                </div>
                <span class="text-sm font-medium text-slate-700">({{ review.rating }}/5)</span>
              </div>
            </div>
            
            <div class="flex-1 p-6">
              <div class="flex items-start justify-between mb-4">
                <div class="flex items-center space-x-2 text-sm text-slate-500">
                  <i class="fas fa-clock"></i>
                  <span>{{ formatDateTime(review.createdAt) }}</span>
                </div>
                <div class="flex items-center space-x-2">
                  <div class="bg-slate-100 px-3 py-1 rounded-full text-xs font-medium">
                    #{{ (currentPage - 1) * itemsPerPage + index + 1 }}
                  </div>
                  <button @click="viewReviewDetail(review)"
                    class="text-blue-600 hover:text-blue-800 p-2 rounded-full hover:bg-blue-50 transition-colors"
                    title="Xem chi tiết">
                    <i class="fas fa-eye text-sm"></i>
                  </button>
                  <button @click="deleteReview(review.id)"
                    class="text-red-600 hover:text-red-800 p-2 rounded-full hover:bg-red-50 transition-colors"
                    title="Xóa đánh giá">
                    <i class="fas fa-trash-alt text-sm"></i>
                  </button>
                </div>
              </div>
              
              <div class="text-slate-700 leading-relaxed">
                <div v-if="expandedReviews.includes(review.id) || !review.content || review.content.length <= 200">
                  {{ review.content || 'Không có nội dung đánh giá' }}
                </div>
                <div v-else>
                  {{ review.content.substring(0, 200) }}...
                  <button @click="toggleReviewContent(review.id)"
                    class="text-blue-600 hover:text-blue-800 font-medium ml-1">
                    Xem thêm
                  </button>
                </div>
              </div>
              
              <div v-if="review.adminResponse" class="mt-4 p-4 bg-blue-50 rounded-lg border-l-4 border-blue-400">
                <div class="flex items-center gap-2 mb-2">
                  <i class="fas fa-reply text-blue-600"></i>
                  <span class="font-medium text-blue-900">Phản hồi của quản lý</span>
                  <span class="text-sm text-blue-600">{{ formatDateTime(review.adminResponseAt) }}</span>
                </div>
                <p class="text-slate-700">{{ review.adminResponse }}</p>
                <div class="text-xs text-blue-600 mt-1">
                  Bởi: {{ review.adminResponseByName || 'Quản lý' }}
                </div>
              </div>
              
              <div v-else class="mt-4">
                <div class="flex items-start gap-4">
                  <textarea
                    v-model="review.draftResponse"
                    rows="3"
                    class="flex-1 border border-slate-300 rounded-lg p-3 text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="Nhập phản hồi của bạn..."
                    maxlength="1000"
                  ></textarea>
                  <button
                    @click="submitResponse(review)"
                    class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 text-sm disabled:opacity-50 transition-colors"
                    :disabled="!review.draftResponse?.trim() || review.submittingResponse"
                  >
                    <span v-if="!review.submittingResponse">Gửi</span>
                    <span v-else>Đang gửi...</span>
                  </button>
                </div>
                <div class="text-xs text-slate-500 mt-1">
                  {{ (review.draftResponse?.length || 0) }}/1000 ký tự
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="flex items-center justify-between bg-white rounded-xl shadow-lg border border-slate-200 px-6 py-4">
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

    <div v-if="showReviewModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click="closeReviewModal">
      <div class="bg-white rounded-xl shadow-xl max-w-2xl w-full mx-4 max-h-[90vh] flex flex-col" @click.stop>
        <!-- Header cố định -->
        <div class="flex justify-between items-center p-6 border-b border-slate-200 flex-shrink-0">
          <h2 class="text-xl font-bold text-slate-800">Chi tiết đánh giá</h2>
          <button @click="closeReviewModal" class="text-slate-500 hover:text-slate-700">
            <i class="fas fa-times text-xl"></i>
          </button>
        </div>
        
        <!-- Content có thể scroll -->
        <div class="flex-1 overflow-y-auto p-6 scrollbar-hide">
          <div v-if="selectedReview" class="space-y-6">
            <div class="flex items-center space-x-4">
              <div class="h-12 w-12 rounded-full bg-blue-100 flex items-center justify-center">
                <span class="text-lg font-medium text-blue-600">{{ getInitials(selectedReview.customerName) }}</span>
              </div>
              <div>
                <h3 class="text-lg font-semibold text-slate-900">{{ selectedReview.customerName }}</h3>
                <p class="text-sm text-slate-500">{{ selectedReview.customerEmail }}</p>
              </div>
            </div>

            <div class="border-t border-slate-200 pt-4">
              <h4 class="font-semibold text-slate-800 mb-2">Khách sạn</h4>
              <p class="text-slate-700">{{ selectedReview.hotelName }}</p>
              <p class="text-sm text-slate-500">{{ selectedReview.hotelAddress }}</p>
            </div>

            <div class="border-t border-slate-200 pt-4">
              <h4 class="font-semibold text-slate-800 mb-2">Đánh giá</h4>
              <div class="flex items-center space-x-2 mb-3">
                <div class="flex space-x-1">
                  <i v-for="star in 5" :key="star"
                    :class="[
                      'fas fa-star text-lg',
                      star <= selectedReview.rating ? 'text-yellow-400' : 'text-gray-300'
                    ]"
                  ></i>
                </div>
                <span class="text-lg font-medium text-slate-700">({{ selectedReview.rating }}/5)</span>
              </div>
              <div class="bg-slate-50 rounded-lg p-4">
                <p class="text-slate-800 whitespace-pre-wrap">{{ selectedReview.content }}</p>
              </div>
            </div>

            <div class="border-t border-slate-200 pt-4">
              <h4 class="font-semibold text-slate-800 mb-2">Thông tin khác</h4>
              <div class="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <span class="text-slate-500">Ngày đánh giá:</span>
                  <p class="text-slate-700">{{ formatDateTime(selectedReview.createdAt) }}</p>
                </div>
                <div>
                  <span class="text-slate-500">Cập nhật lần cuối:</span>
                  <p class="text-slate-700">{{ formatDateTime(selectedReview.updatedAt) }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Footer cố định -->
        <div class="flex justify-end space-x-3 p-6 border-t border-slate-200 flex-shrink-0">
          <button @click="closeReviewModal"
            class="px-4 py-2 text-slate-700 bg-slate-100 rounded-md hover:bg-slate-200 transition-colors">
            Đóng
          </button>
          <button @click="deleteReview(selectedReview.id)"
            class="px-4 py-2 text-white bg-red-600 rounded-md hover:bg-red-700 transition-colors">
            Xóa đánh giá
          </button>
        </div>
      </div>
    </div>

    <ConfirmDialog ref="confirmDialog" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { hotelAdminApi, respondToHotelReview } from '@/api/adminApi';
import CustomSelect from '@/components/CustomSelect.vue';
import ConfirmDialog from '@/components/ConfirmDialog.vue';
import { useAdminBreadcrumbStore } from '@/store/useAdminBreadcrumbStore';
import { useAdminAuth } from '@/composables/useAdminAuth';
import { useUserStore } from '@/store/UserStore';

const searchQuery = ref('');
const selectedRating = ref('');
const currentPage = ref(1);
const itemsPerPageStr = ref('5');
const itemsPerPageOptions = [5, 10, 20, 50, 'Tất cả'];

const reviews = ref([]);
const loading = ref(false);
const expandedReviews = ref([]);
const showReviewModal = ref(false);
const selectedReview = ref(null);
const reviewIdToDelete = ref(null);
const confirmDialog = ref(null);

const ratingOptions = [
  { label: 'Tất cả sao', value: '' },
  { label: '5 sao', value: '5' },
  { label: '4 sao', value: '4' },
  { label: '3 sao', value: '3' },
  { label: '2 sao', value: '2' },
  { label: '1 sao', value: '1' }
];

onMounted(async () => {
  console.log('Review component mounted');
  
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
  
  const { requireAdmin } = useAdminAuth();
  if (!requireAdmin('hotel')) {
    console.log('Không có quyền admin hotel');
    return;
  }
  
  console.log('Có quyền admin hotel, loading data...');
  
  const breadcrumbStore = useAdminBreadcrumbStore();
  breadcrumbStore.setBreadcrumb([
    { label: 'Đánh giá', active: true }
  ]);
  await fetchReviews();
});

const filteredReviews = computed(() => {
  let arr = reviews.value.slice();
  
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase();
    arr = arr.filter(review =>
      (review.customerName || '').toLowerCase().includes(q) ||
      (review.hotelName || '').toLowerCase().includes(q) ||
      (review.content || '').toLowerCase().includes(q) ||
      (review.customerEmail || '').toLowerCase().includes(q)
    );
  }
  
  if (selectedRating.value) {
    arr = arr.filter(review => review.rating === parseInt(selectedRating.value));
  }
  
  arr.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
  
  return arr;
});

const itemsPerPage = computed(() => 
  itemsPerPageStr.value === 'Tất cả' ? filteredReviews.value.length : Number(itemsPerPageStr.value)
);

const totalPages = computed(() => 
  Math.ceil(filteredReviews.value.length / (itemsPerPage.value || 1))
);

const paginatedReviews = computed(() => {
  if (itemsPerPageStr.value === 'Tất cả') return filteredReviews.value;
  const start = (currentPage.value - 1) * itemsPerPage.value;
  return filteredReviews.value.slice(start, start + itemsPerPage.value);
});

const displayedPages = computed(() => {
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

async function fetchReviews() {
  loading.value = true;
  console.log('Fetching reviews...'); 
  try {
            const res = await hotelAdminApi.getAllHotelReviews();
    console.log('API response:', res);
    if (res.data && res.data.data) {
      reviews.value = res.data.data;
    } else {
      reviews.value = [];
    }
  } catch (error) {
    console.error('Error fetching reviews:', error);
    reviews.value = [];
    if (error.response && error.response.status === 405) {
      window.$toast && window.$toast('API endpoint chưa được implement ở backend!', 'error');
    } else if (error.response && error.response.status === 500) {
      window.$toast && window.$toast('Lỗi server: ' + (error.response.data?.message || 'Internal server error'), 'error');
    } else {
      window.$toast && window.$toast('Lỗi khi tải danh sách đánh giá: ' + (error.message || 'Unknown error'), 'error');
    }
  } finally {
    loading.value = false;
    console.log('Reviews loaded:', reviews.value.length);
  }
}

function onRatingChange(value) {
  selectedRating.value = value;
  currentPage.value = 1;
}

function toggleReviewContent(reviewId) {
  const index = expandedReviews.value.indexOf(reviewId);
  if (index > -1) {
    expandedReviews.value.splice(index, 1);
  } else {
    expandedReviews.value.push(reviewId);
  }
}

function viewReviewDetail(review) {
  selectedReview.value = review;
  showReviewModal.value = true;
}

function closeReviewModal() {
  showReviewModal.value = false;
  selectedReview.value = null;
}

async function deleteReview(reviewId) {
  reviewIdToDelete.value = reviewId;
  const reviewToDelete = reviews.value.find(r => r.id === reviewId);
  const reviewInfo = reviewToDelete ? `${reviewToDelete.customerName} - ${reviewToDelete.hotelName}` : 'đánh giá này';
  
  const result = await confirmDialog.value.showDialog({
    type: 'danger',
    title: 'Xác nhận xóa đánh giá',
    message: `Bạn có chắc chắn muốn xóa đánh giá của "${reviewInfo}" không? Hành động này không thể hoàn tác.`,
    confirmText: 'Xóa',
    cancelText: 'Hủy'
  });
  
  if (result) {
    await onConfirmDelete();
  }
}

async function onConfirmDelete() {
  try {
    await hotelAdminApi.deleteHotelReview(reviewIdToDelete.value);
    window.$toast && window.$toast('Xóa đánh giá thành công!', 'success');
    await fetchReviews();
    closeReviewModal();
  } catch (error) {
    window.$toast && window.$toast('Xóa đánh giá thất bại!', 'error');
  }
  reviewIdToDelete.value = null;
}

async function submitResponse(review) {
  if (!review.draftResponse?.trim()) {
    window.$toast && window.$toast('Vui lòng nhập phản hồi trước khi gửi.', 'warning');
    return;
  }

  try {
    review.submittingResponse = true;
    await respondToHotelReview(review.id, review.draftResponse);
    
    review.adminResponse = review.draftResponse;
    review.adminResponseAt = new Date().toISOString();
    review.adminResponseByName = 'Quản lý'; 
    review.draftResponse = '';
    
    window.$toast && window.$toast('Gửi phản hồi thành công!', 'success');
  } catch (error) {
    console.error('Error submitting response:', error);
    window.$toast && window.$toast('Gửi phản hồi thất bại!', 'error');
  } finally {
    review.submittingResponse = false;
  }
}

function changePage(page) {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
}

function nextPage() {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
}

function prevPage() {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
}

function formatDateTime(dt) {
  if (!dt) return '';
  const d = new Date(dt);
  return d.toLocaleDateString('vi-VN', { 
    day: '2-digit', 
    month: '2-digit', 
    year: 'numeric' 
  }) + ' ' + d.toLocaleTimeString('vi-VN', { 
    hour: '2-digit', 
    minute: '2-digit' 
  });
}

function getInitials(name) {
  if (!name) return '?';
  return name
    .split(' ')
    .map(word => word.charAt(0))
    .join('')
    .toUpperCase()
    .slice(0, 2);
}
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.scrollbar-hide {
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* Internet Explorer 10+ */
}

.scrollbar-hide::-webkit-scrollbar {
  display: none; /* Safari and Chrome */
}
</style> 