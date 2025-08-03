<template>
  <div
    v-if="show"
    @click.self="close"
    class="fixed inset-0 bg-black bg-opacity-500 z-40 flex justify-center items-center"
  >
    <div
      class="bg-white rounded-lg shadow-xl w-full max-w-md m-4 transform transition-all"
      :class="show ? 'scale-100' : 'scale-95'"
    >
      <div class="p-4 border-b">
        <h3 class="text-lg font-semibold text-gray-800">Viết đánh giá cho</h3>
        <p class="text-sm text-gray-600">{{ service.serviceName }}</p>
      </div>

      <div class="p-4 space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1"
            >Xếp hạng của bạn</label
          >
          <div
            class="flex items-center space-x-1 text-2xl cursor-pointer text-gray-300"
          >
            <i
              v-for="star in 5"
              :key="star"
              class="fas fa-star transition-colors"
              :class="{
                'text-yellow-400': star <= rating,
                'hover:text-yellow-300': true,
              }"
              @click="rating = star"
            ></i>
          </div>
        </div>

        <div>
          <label
            for="reviewContent"
            class="block text-sm font-medium text-gray-700"
            >Bình luận của bạn</label
          >
          <textarea
            v-model="content"
            id="reviewContent"
            rows="4"
            placeholder="Dịch vụ này thế nào? Bạn có muốn chia sẻ thêm điều gì không?"
            class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2 focus:ring-blue-500 focus:border-blue-500"
          ></textarea>
        </div>
      </div>

      <div class="px-4 py-3 bg-gray-50 flex justify-end space-x-3 rounded-b-lg">
        <button
          @click="close"
          class="px-4 py-2 bg-white border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50"
        >
          Hủy
        </button>
        <button
          @click="submitReview"
          :disabled="isSubmitting || rating === 0"
          class="px-4 py-2 bg-blue-600 border border-transparent rounded-md text-sm font-medium text-white hover:bg-blue-700 disabled:bg-blue-300 disabled:cursor-not-allowed"
        >
          <i v-if="isSubmitting" class="fas fa-spinner fa-spin mr-2"></i>
          {{ isSubmitting ? "Đang gửi..." : "Gửi đánh giá" }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits } from "vue";

const props = defineProps({
  show: Boolean,
  service: Object,
});

const emit = defineEmits(["close", "submit"]);

const rating = ref(0);
const content = ref("");
const isSubmitting = ref(false);

const close = () => {
  if (!isSubmitting.value) {
    emit("close");
  }
};

const submitReview = () => {
  if (rating.value === 0) return;

  isSubmitting.value = true;

  emit("submit", {
    rating: rating.value,
    content: content.value,
  });

  // Reset state sau khi submit (có thể chờ component cha xử lý xong rồi reset)
  // isSubmitting.value = false;
  // rating.value = 0;
  // content.value = '';
};
</script>
