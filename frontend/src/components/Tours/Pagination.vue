<template>
  <nav
    v-if="totalPages > 1"
    class="flex items-center justify-center space-x-2 mt-8"
    aria-label="Pagination"
  >
    <button
      @click="changePage(currentPage - 1)"
      :disabled="currentPage === 0"
      class="px-3 py-2 text-sm font-medium text-gray-600 bg-white border border-gray-300 rounded-lg hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
    >
      Trước
    </button>

    <button
      v-for="page in pageNumbers"
      :key="page"
      @click="changePage(page - 1)"
      :class="[
        'px-3 py-2 text-sm font-medium border rounded-lg',
        page === currentPage + 1
          ? 'text-white bg-blue-500 border-blue-500 z-10'
          : 'text-gray-600 bg-white border-gray-300 hover:bg-gray-100',
      ]"
    >
      {{ page }}
    </button>

    <button
      @click="changePage(currentPage + 1)"
      :disabled="currentPage === totalPages - 1"
      class="px-3 py-2 text-sm font-medium text-gray-600 bg-white border border-gray-300 rounded-lg hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
    >
      Sau
    </button>
  </nav>
</template>

<script setup>
import { defineProps, defineEmits, computed } from "vue";

const props = defineProps({
  currentPage: {
    type: Number,
    required: true,
  },
  totalPages: {
    type: Number,
    required: true,
  },
});

const emit = defineEmits(["change-page"]);

const changePage = (page) => {
  if (page >= 0 && page < props.totalPages) {
    emit("change-page", page);
  }
};

// Logic để chỉ hiển thị một số lượng trang nhất định (ví dụ: 1 ... 4 5 6 ... 10)
// Ở đây làm đơn giản để dễ hiểu: hiển thị tất cả các trang
const pageNumbers = computed(() => {
  return Array.from({ length: props.totalPages }, (_, i) => i + 1);
});
</script>
