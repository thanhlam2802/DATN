<template>
  <div class="space-y-6">
    <!-- Header -->
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

    <!-- Thống kê -->

    <!-- Danh sách đánh giá -->
    <div v-if="filteredReviews.length > 0" class="space-y-4">
      <div
        v-for="review in filteredReviews"
        :key="review.id"
        class="bg-white p-6 rounded-lg shadow-sm space-y-4"
      >
        <!-- Thông tin người đánh giá -->
        <div class="flex justify-between items-start">
          <div>
            <div class="flex items-center gap-2">
              <h4 class="font-semibold text-gray-900">{{ review.customer }}</h4>
              <span class="text-sm text-gray-500">{{ review.date }}</span>
            </div>
            <p class="text-sm text-gray-600 mt-1">{{ review.tour }}</p>
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

        <!-- Nội dung đánh giá -->
        <div class="text-gray-700">{{ review.comment }}</div>

        <!-- Phần phản hồi -->
        <div v-if="review.reply" class="mt-4 pl-4 border-l-4 border-blue-200">
          <div class="bg-blue-50 rounded-lg p-4">
            <div class="flex items-center gap-2 mb-2">
              <span class="font-medium text-blue-900">Phản hồi của bạn</span>
              <span class="text-sm text-blue-600">{{ review.reply.date }}</span>
            </div>
            <p class="text-gray-700">{{ review.reply.content }}</p>
          </div>
        </div>

        <!-- Form phản hồi -->
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
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 text-sm"
            >
              Gửi phản hồi
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="text-center text-gray-500 py-10">
      <p>Không có đánh giá nào</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { StarIcon } from "lucide-vue-next";

// Dữ liệu mẫu
const reviews = ref([
  {
    id: 1,
    customer: "Nguyễn Văn A",
    tour: "Hạ Long Bay 3N2Đ",
    rating: 5,
    date: "2024-01-10",
    comment:
      "Tour rất tuyệt vời! Hướng dẫn viên nhiệt tình, cảnh đẹp, đồ ăn ngon!",
    reply: {
      content:
        "Cảm ơn bạn đã đánh giá tích cực. Chúng tôi rất vui khi bạn hài lòng với chuyến đi!",
      date: "2024-01-11",
    },
    draftReply: "",
  },
  {
    id: 2,
    customer: "Trần Thị B",
    tour: "Sapa 2N1Đ",
    rating: 4,
    date: "2024-01-08",
    comment: "Chuyến đi rất thú vị, tuy nhiên thời gian hơi gấp rút.",
    reply: null,
    draftReply: "",
  },
  {
    id: 3,
    customer: "Lê Văn C",
    tour: "Phú Quốc 4N3Đ",
    rating: 3,
    date: "2024-01-05",
    comment:
      "Tour có nhiều điểm hay nhưng khách sạn hơi chật, cần cải thiện thêm.",
    reply: null,
    draftReply: "",
  },
]);

const filterRating = ref("all");
const filterStatus = ref("all");

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

const averageRating = computed(
  () =>
    reviews.value.reduce((sum, r) => sum + r.rating, 0) / reviews.value.length
);

function countStatus(status) {
  return reviews.value.filter((r) => r.status === status).length;
}

function submitReply(review) {
  if (!review.draftReply.trim()) return;

  review.reply = {
    content: review.draftReply,
    date: new Date().toISOString().split("T")[0],
  };
  review.draftReply = "";
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
