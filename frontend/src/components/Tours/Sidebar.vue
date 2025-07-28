<template>
  <aside class="w-full md:w-80 shrink-0 space-y-6 p-4 md:p-0">
    <div class="filter-group border-b pb-4 mb-4">
      <button
        @click="resetFilters"
        class="w-full flex items-center justify-center gap-2 px-4 py-2 bg-gray-200 text-gray-700 font-medium rounded-lg hover:bg-gray-300 transition-colors duration-200"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
          class="lucide lucide-rotate-ccw"
        >
          <path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8" />
          <path d="M3 3v5h5" />
        </svg>
        <span>Xóa bộ lọc</span>
      </button>
    </div>

    <div class="filter-group">
      <h3 class="text-lg font-medium mb-3">Điểm đến</h3>
      <input
        type="text"
        v-model.lazy="destination"
        placeholder="Nhập tên thành phố, khu vực..."
        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition"
      />
    </div>

    <div class="filter-group">
      <h3 class="text-lg font-medium mb-3">Khoảng giá</h3>
      <div class="relative p-4 border rounded-lg">
        <div class="flex justify-between items-center text-sm mb-2">
          <span>{{ minPrice.toLocaleString() }} VND</span>
          <span>{{ maxPrice.toLocaleString() }} VND</span>
        </div>
        <div class="relative h-1 bg-gray-200 rounded-full">
          <div
            class="absolute h-1 bg-blue-500 rounded-full"
            :style="sliderStyle"
          ></div>
          <input
            type="range"
            :min="0"
            :max="5000000"
            step="100000"
            v-model.number="minPrice"
            class="absolute w-full h-1 top-0 bg-transparent appearance-none pointer-events-none"
          />
          <input
            type="range"
            :min="0"
            :max="5000000"
            step="100000"
            v-model.number="maxPrice"
            class="absolute w-full h-1 top-0 bg-transparent appearance-none pointer-events-none"
          />
        </div>
      </div>
    </div>

    <div class="filter-group">
      <h3 class="text-lg font-medium mb-3">Đánh giá</h3>
      <div
        class="flex items-center justify-center space-x-1 p-4 border rounded-lg"
      >
        <span
          v-for="star in 5"
          :key="star"
          @click="minRating = star"
          class="cursor-pointer"
        >
          <svg
            class="w-8 h-8"
            :class="star <= minRating ? 'text-yellow-400' : 'text-gray-300'"
            fill="currentColor"
            viewBox="0 0 20 20"
          >
            <path
              d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.286 3.957a1 1 0 00.95.69h4.162c.969 0 1.371 1.24.588 1.81l-3.368 2.448a1 1 0 00-.364 1.118l1.287 3.957c.3.921-.755 1.688-1.54 1.118l-3.368-2.448a1 1 0 00-1.176 0l-3.368 2.448c-.784.57-1.838-.197-1.539-1.118l1.287-3.957a1 1 0 00-.364-1.118L2.05 9.384c-.783-.57-.38-1.81.588-1.81h4.162a1 1 0 00.95-.69L9.049 2.927z"
            />
          </svg>
        </span>
      </div>
    </div>

    <div class="filter-group">
      <h3 class="text-lg font-medium mb-3">Tags</h3>
      <div class="space-y-3 p-4 border rounded-lg max-h-60 overflow-y-auto">
        <p v-if="tagsLoading">Đang tải danh sách tags...</p>
        <p v-else-if="tags.length === 0">Không tìm thấy tags.</p>
        <label
          v-for="tag in tags"
          :key="tag.id"
          class="flex items-center gap-3 cursor-pointer"
        >
          <input
            type="checkbox"
            class="w-5 h-5 rounded border-gray-300 text-blue-600 focus:ring-blue-500"
            :value="tag.name"
            v-model="selectedTags"
          />
          <span class="text-sm">{{ tag.name }}</span>
        </label>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { ref, onMounted, watch, computed } from "vue";

const emit = defineEmits(["update-filters"]);

// --- Giá trị mặc định cho các bộ lọc ---
const DEFAULT_MIN_PRICE = 0;
const DEFAULT_MAX_PRICE = 5000000;

// --- State cho tất cả bộ lọc ---
const destination = ref("");
const minPrice = ref(DEFAULT_MIN_PRICE);
const maxPrice = ref(DEFAULT_MAX_PRICE);
const minRating = ref(0);
const selectedTags = ref([]);

// --- State cho việc tải dữ liệu tags ---
const tags = ref([]);
const tagsLoading = ref(false);

// --- HÀM MỚI: Reset tất cả các bộ lọc về giá trị mặc định ---
const resetFilters = () => {
  destination.value = "";
  minPrice.value = DEFAULT_MIN_PRICE;
  maxPrice.value = DEFAULT_MAX_PRICE;
  minRating.value = 0;
  selectedTags.value = [];
  // Việc thay đổi các ref này sẽ tự động kích hoạt `watch` bên dưới
};

// --- Theo dõi tất cả các bộ lọc ---
watch(
  [destination, minPrice, maxPrice, selectedTags, minRating],
  () => {
    if (minPrice.value > maxPrice.value) {
      [minPrice.value, maxPrice.value] = [maxPrice.value, minPrice.value];
    }

    emit("update-filters", {
      destination: destination.value,
      minPrice: minPrice.value,
      maxPrice: maxPrice.value,
      minRating: minRating.value,
      tags: selectedTags.value,
    });
  },
  { deep: true }
);

// --- Hàm fetchTags và onMounted không đổi ---
const fetchTags = async () => {
  tagsLoading.value = true;
  try {
    const response = await fetch("http://localhost:8080/api/tags");
    if (!response.ok) throw new Error(`HTTP Error: ${response.status}`);
    const dataFromApi = await response.json();
    tags.value = dataFromApi;
  } catch (error) {
    console.error("Lỗi khi lấy dữ liệu tags:", error);
  } finally {
    tagsLoading.value = false;
  }
};

onMounted(fetchTags);

// --- Style cho slider không đổi ---
const sliderStyle = computed(() => {
  const max = 5000000;
  const leftPercent = (minPrice.value / max) * 100;
  const rightPercent = (maxPrice.value / max) * 100;
  return {
    left: `${leftPercent}%`,
    width: `${rightPercent - leftPercent}%`,
  };
});
</script>

<style scoped>
/* Style để các thanh trượt giá có thể tương tác */
input[type="range"]::-webkit-slider-thumb {
  -webkit-appearance: none;
  pointer-events: all;
  width: 1.25rem; /* 20px */
  height: 1.25rem; /* 20px */
  border-radius: 9999px;
  background-color: white;
  border: 1px solid #3b82f6; /* blue-500 */
  cursor: pointer;
}

input[type="range"]::-moz-range-thumb {
  pointer-events: all;
  width: 1.25rem;
  height: 1.25rem;
  border-radius: 9999px;
  background-color: white;
  border: 1px solid #3b82f6;
  cursor: pointer;
}
</style>
