<template>
  <div>
    <div class="border-b border-gray-200 pb-4 mb-6">
      <h1 class="text-2xl font-bold text-gray-800">Đánh giá dịch vụ</h1>
      <p class="mt-1 text-sm text-gray-500">
        Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi. Vui lòng chia sẻ đánh giá
        của bạn để giúp chúng tôi cải thiện.
      </p>
    </div>

    <div v-if="isLoading" class="text-center py-10">
      <i class="fas fa-spinner fa-spin text-4xl text-blue-500"></i>
      <p class="mt-2 text-gray-600">Đang tải dữ liệu...</p>
    </div>

    <div
      v-else-if="error"
      class="text-center py-10 px-6 bg-red-50 border border-red-200 rounded-lg"
    >
      <i class="fas fa-exclamation-triangle text-4xl text-red-500"></i>
      <p class="mt-2 font-semibold text-red-700">{{ error }}</p>
    </div>

    <div v-else>
      <div v-if="completedServices.length > 0" class="space-y-4">
        <div
          v-for="service in completedServices"
          :key="service.bookingId"
          class="flex flex-col md:flex-row items-center justify-between p-4 border border-gray-200 rounded-lg bg-white hover:shadow-md transition-shadow duration-200"
        >
          <div
            class="flex items-center gap-4 mb-4 md:mb-0 text-center md:text-left"
          >
            <div
              class="flex-shrink-0 w-12 h-12 flex items-center justify-center bg-gray-100 rounded-full"
            >
              <i :class="service.icon" class="text-xl text-gray-500"></i>
            </div>
            <div>
              <p class="font-semibold text-gray-800">
                {{ service.serviceName }}
              </p>
              <p class="text-xs text-gray-500">
                Hoàn thành ngày: {{ service.completionDate }}
              </p>
            </div>
          </div>
          <div class="flex-shrink-0">
            <button
              v-if="!service.isReviewed"
              @click="openReviewModal(service)"
              class="bg-blue-500 text-white font-bold py-2 px-4 rounded-lg hover:bg-blue-600 transition-colors text-sm"
            >
              <i class="fas fa-star mr-2"></i>Viết đánh giá
            </button>
            <div
              v-else
              @click="viewReview(service)"
              class="flex items-center text-green-600 text-sm cursor-pointer hover:underline"
            >
              <i class="fas fa-check-circle mr-2"></i>
              <span>Đã đánh giá</span>
            </div>
          </div>
        </div>
      </div>
      <div
        v-else
        class="text-center py-10 px-6 border-2 border-dashed border-gray-200 rounded-lg"
      >
        <div
          class="flex justify-center items-center mx-auto w-16 h-16 bg-gray-100 rounded-full mb-4"
        >
          <i class="fas fa-inbox text-2xl text-gray-400"></i>
        </div>
        <h3 class="text-lg font-medium text-gray-800">Chưa có gì ở đây cả</h3>
        <p class="text-sm text-gray-500 mt-1">
          Bạn không có dịch vụ nào đã hoàn thành để đánh giá.
        </p>
      </div>
    </div>

    <ReviewModal
      :show="isModalVisible"
      :service="selectedService"
      @close="closeReviewModal"
      @submit="handleReviewSubmit"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { reviewService } from "@/api/ReviewApi.js";
import { getAccessToken, clearToken } from "@/services/TokenService";
import ReviewModal from "@/components/User/Sidebar/components/ReviewModal.vue";

// --- STATE MANAGEMENT ---
const completedServices = ref([]);
const isLoading = ref(true);
const error = ref(null);
const user = ref(null);
const isModalVisible = ref(false);
const selectedService = ref(null);

// --- HELPER FUNCTION ---
const decodeUserFromToken = () => {
  const token = getAccessToken();
  if (!token) return null;
  try {
    const payloadBase64 = token.split(".")[1];
    const decodedJson = atob(
      payloadBase64.replace(/-/g, "+").replace(/_/g, "/")
    );
    return JSON.parse(decodedJson);
  } catch (e) {
    console.error("Token không hợp lệ, đang xóa token:", e);
    clearToken();
    return null;
  }
};

// ==========================================================
// --- HÀM ĐƯỢC BỔ SUNG ---
// ==========================================================
const fetchCompletedServices = async () => {
  isLoading.value = true;
  error.value = null;

  if (!user.value || !user.value.userId) {
    error.value = "Vui lòng đăng nhập để xem thông tin này.";
    isLoading.value = false;
    return;
  }

  try {
    const response = await reviewService.getCompletedServices(
      user.value.userId
    );
    completedServices.value = response.data;
  } catch (err) {
    console.error("Lỗi khi tải danh sách dịch vụ:", err);
    error.value = "Không thể tải dữ liệu. Vui lòng thử lại sau.";
  } finally {
    isLoading.value = false;
  }
};

// --- LIFECYCLE HOOK ---
onMounted(() => {
  // 1. Giải mã token để lấy thông tin user
  user.value = decodeUserFromToken();

  // 2. Nếu có user, bắt đầu tải dữ liệu dịch vụ
  if (user.value) {
    fetchCompletedServices();
  } else {
    // 3. Nếu không có user, báo lỗi
    error.value = "Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.";
    isLoading.value = false;
  }
});
// ==========================================================
// --- KẾT THÚC PHẦN BỔ SUNG ---
// ==========================================================

// --- CÁC HÀM XỬ LÝ CHO MODAL ---
const openReviewModal = (service) => {
  selectedService.value = service;
  isModalVisible.value = true;
};

const closeReviewModal = () => {
  isModalVisible.value = false;
  selectedService.value = null;
};

const handleReviewSubmit = async (reviewData) => {
  if (!selectedService.value || !user.value) return;

  const requestPayload = {
    userId: user.value.userId,
    entityId: selectedService.value.serviceId,
    entityType: selectedService.value.serviceType,
    rating: reviewData.rating,
    content: reviewData.content,
  };

  try {
    await reviewService.createReview(requestPayload);
    closeReviewModal();
    await fetchCompletedServices();

    alert("Cảm ơn bạn đã gửi đánh giá!");
  } catch (err) {
    console.error("Lỗi khi gửi đánh giá:", err);
    alert("Đã xảy ra lỗi khi gửi đánh giá. Vui lòng thử lại.");
  }
};

const viewReview = (service) => {
  alert(
    `Chức năng xem lại đánh giá cho "${service.serviceName}" đang được phát triển.`
  );
};
</script>
