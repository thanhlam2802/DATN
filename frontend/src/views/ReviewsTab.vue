<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h2 class="text-3xl font-bold text-gray-800">Phản hồi & Đánh giá</h2>
      <div class="flex space-x-2">
        <select v-model="filterRating" class="border rounded px-2 py-1 text-sm">
          <option value="all">Tất cả sao</option>
          <option v-for="n in 5" :key="n" :value="n.toString()">
            {{ n }} sao
          </option>
        </select>
        <select v-model="filterStatus" class="border rounded px-2 py-1 text-sm">
          <option value="all">Tất cả trạng thái</option>
          <option value="replied">Đã phản hồi</option>
          <option value="pending">Chưa phản hồi</option>
        </select>
      </div>
    </div>

    <div v-if="isLoading" class="text-center text-gray-500 py-10">
      <p>Đang tải dữ liệu đánh giá...</p>
    </div>

    <div
      v-else-if="error"
      class="text-center text-red-500 bg-red-50 p-4 rounded-lg"
    >
      <p>Đã xảy ra lỗi khi tải dữ liệu: {{ error }}</p>
    </div>

    <div v-else-if="filteredReviews.length > 0" class="space-y-4">
      <div
        v-for="review in filteredReviews"
        :key="review.id"
        class="bg-white p-6 rounded-lg shadow-sm space-y-4"
      >
        <div class="flex justify-between items-start">
          <div>
            <div class="flex items-center gap-2">
              <h4 class="font-semibold text-gray-900">{{ review.customer }}</h4>
              <span class="text-sm text-gray-500">{{ review.date }}</span>
            </div>
            <p class="text-sm text-gray-600 mt-1">
              Tour: {{ review.tourName }}
            </p>
            <div class="flex mt-1">
              <StarIcon
                v-for="i in 5"
                :key="i"
                class="w-4 h-4"
                :class="
                  i <= review.rating ? 'text-yellow-400' : 'text-gray-300'
                "
              />
            </div>
          </div>
          <span
            class="px-3 py-1 text-sm rounded-full"
            :class="
              review.reply
                ? 'bg-green-100 text-green-700'
                : 'bg-gray-100 text-gray-600'
            "
          >
            {{ review.reply ? "Đã phản hồi" : "Chưa phản hồi" }}
          </span>
        </div>

        <div class="text-gray-700">{{ review.comment }}</div>

        <div v-if="review.reply" class="mt-4 pl-4 border-l-4 border-blue-200">
          <div class="bg-blue-50 rounded-lg p-4">
            <div class="flex items-center gap-2 mb-2">
              <span class="font-medium text-blue-900">Phản hồi của bạn</span>
              <span class="text-sm text-blue-600">{{ review.reply.date }}</span>
            </div>
            <p class="text-gray-700">{{ review.reply.content }}</p>
          </div>
        </div>

        <div v-else class="mt-4">
          <div class="flex items-start gap-4">
            <textarea
              v-model="review.draftReply"
              rows="2"
              class="flex-1 border rounded-lg p-2 text-sm"
              placeholder="Nhập phản hồi của bạn..."
            ></textarea>
            <button
              @click="submitReply(review)"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 text-sm disabled:opacity-50"
              :disabled="!review.draftReply.trim()"
            >
              Gửi
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="text-center text-gray-500 py-10">
      <p>Không có đánh giá nào phù hợp với bộ lọc.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { StarIcon } from "lucide-vue-next";

// --- State Management ---
const reviews = ref([]);
const isLoading = ref(true);
const error = ref(null);
const filterRating = ref("all");
const filterStatus = ref("all");

// --- API & Data Fetching ---
const ownerId = 1; // Trong ứng dụng thực tế, ID này sẽ lấy từ store hoặc session

async function fetchReviews() {
  isLoading.value = true;
  error.value = null;
  try {
    const response = await fetch(`/api/v1/reviews/tours/owner/${ownerId}`);
    if (!response.ok) {
      throw new Error("Không thể kết nối tới máy chủ.");
    }
    const data = await response.json();

    // Ánh xạ dữ liệu từ API sang cấu trúc frontend cần
    reviews.value = data.map((reviewFromApi) => ({
      id: reviewFromApi.id,
      customer: reviewFromApi.author,
      tourName: reviewFromApi.tourName, // Đã kích hoạt để hiển thị tên tour
      rating: reviewFromApi.rating,
      date: reviewFromApi.date,
      comment: reviewFromApi.content,
      reply: reviewFromApi.reply, // Giả định API trả về object reply
      draftReply: "", // Trạng thái riêng của frontend
    }));
  } catch (err) {
    error.value = err.message;
    console.error("Lỗi khi tải đánh giá:", err);
  } finally {
    isLoading.value = false;
  }
}

async function submitReply(review) {
  if (!review.draftReply.trim()) return;

  try {
    const response = await fetch("/api/v1/replies", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        reviewId: review.id,
        content: review.draftReply,
      }),
    });

    if (!response.ok) {
      throw new Error("Gửi phản hồi thất bại.");
    }

    const newReply = await response.json();
    // Giả sử API trả về { content, createdAt }
    review.reply = {
      content: newReply.content,
      date: new Date(newReply.createdAt).toLocaleDateString("vi-VN"),
    };
    review.draftReply = "";
  } catch (err) {
    alert(err.message);
    console.error("Lỗi khi gửi phản hồi:", err);
  }
}

// --- Lifecycle Hooks ---
onMounted(() => {
  fetchReviews();
});

// --- Computed Properties ---
const filteredReviews = computed(() =>
  reviews.value.filter((review) => {
    const ratingMatch =
      filterRating.value === "all" ||
      review.rating.toString() === filterRating.value;
    const statusMatch =
      filterStatus.value === "all" ||
      (filterStatus.value === "replied" && review.reply) ||
      (filterStatus.value === "pending" && !review.reply);
    return ratingMatch && statusMatch;
  })
);
</script>

<style scoped>
/* CSS không thay đổi */
</style>
