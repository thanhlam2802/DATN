<template>
  <aside class="w-full md:w-80 shrink-0">
    <div class="mb-6">
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

    <div class="mb-6">
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

// 1. Định nghĩa sự kiện mà component sẽ phát ra
const emit = defineEmits(["update-filters"]);

// --- State cho Bộ lọc ---
const minPrice = ref(0);
const maxPrice = ref(5000000);
const selectedTags = ref([]); // Chỉ lưu tên của các tag được chọn

// --- State cho việc tải dữ liệu tags ---
const tags = ref([]);
const tagsLoading = ref(false);

// 2. Theo dõi sự thay đổi của các bộ lọc và phát sự kiện
watch(
  [minPrice, maxPrice, selectedTags],
  () => {
    // Đảm bảo minPrice luôn nhỏ hơn hoặc bằng maxPrice
    if (minPrice.value > maxPrice.value) {
      // Hoán đổi giá trị nếu người dùng kéo slider min vượt qua max
      [minPrice.value, maxPrice.value] = [maxPrice.value, minPrice.value];
    }

    console.log("Filters changed, emitting:", {
      minPrice: minPrice.value,
      maxPrice: maxPrice.value,
      tags: selectedTags.value,
    });

    // Gửi đối tượng chứa tất cả các giá trị lọc hiện tại
    emit("update-filters", {
      minPrice: minPrice.value,
      maxPrice: maxPrice.value,
      // Gửi danh sách tên các tag đã chọn
      tags: selectedTags.value,
    });
  },
  { deep: true }
); // `deep: true` cần thiết để theo dõi thay đổi trong mảng selectedTags

// Hàm fetchTags không thay đổi, vẫn gọi API để lấy danh sách tags
const fetchTags = async () => {
  tagsLoading.value = true;
  try {
    const response = await fetch("http://localhost:8080/api/tags");
    if (!response.ok) throw new Error(`HTTP Error: ${response.status}`);
    const dataFromApi = await response.json();
    tags.value = dataFromApi; // Không cần thêm 'checked' nữa
  } catch (error) {
    console.error("Lỗi khi lấy dữ liệu tags:", error);
    tags.value = []; // Reset về mảng rỗng nếu có lỗi
  } finally {
    tagsLoading.value = false;
  }
};

onMounted(() => {
  fetchTags();
});

// Style cho thanh slider để hiển thị vùng đã chọn
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
