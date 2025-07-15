<template>
  <aside class="w-full md:w-80 shrink-0">
    <div class="mb-6">
      <h3 class="text-lg font-medium mb-3">Khoảng giá</h3>
      <div class="relative">
        <button
          class="w-full flex justify-between items-center px-4 py-3 border rounded-lg"
        >
          <span>0 VND - 4.000.000 VND+</span>
          <ChevronDownIcon size="18" />
        </button>
      </div>
    </div>

    <div class="mb-6">
      <div class="flex justify-between items-center mb-3">
        <h3 class="text-lg font-medium">Điểm nổi bật của Xperience</h3>
        <XCircleIcon size="20" class="text-blue-500" />
      </div>
      <label class="flex items-center gap-2 mb-2">
        <input
          type="checkbox"
          class="w-5 h-5 rounded border-gray-300"
          v-model="highlightNew"
        />
        <span>Điều mới ở Xperience</span>
      </label>
    </div>

    <div class="mb-6">
      <h3 class="text-lg font-medium mb-3">Tags</h3>
      <div class="space-y-3">
        <p v-if="tagsLoading">Đang tải danh sách tags...</p>

        <label
          v-for="tag in tags"
          :key="tag.id"
          class="flex items-center gap-2"
        >
          <input
            type="checkbox"
            class="w-5 h-5 rounded border-gray-300"
            v-model="tag.checked"
          />
          <span>{{ tag.name }}</span>
        </label>
      </div>
    </div>
  </aside>
</template>
<script setup>
import { ref, onMounted } from "vue";
import { XCircleIcon, ChevronDownIcon } from "lucide-vue-next";

const highlightNew = ref(false);
const tags = ref([]);
const tagsLoading = ref(false);

// Hàm fetchTags được cập nhật để gọi API thật
const fetchTags = async () => {
  tagsLoading.value = true;
  console.log("Bắt đầu gọi API Spring Boot để lấy tags...");

  try {
    // Thay đổi URL này cho đúng với địa chỉ backend của bạn
    const response = await fetch("http://localhost:8080/api/tags");

    // Kiểm tra nếu request không thành công (ví dụ: lỗi 404, 500)
    if (!response.ok) {
      throw new Error(`Lỗi HTTP! Trạng thái: ${response.status}`);
    }

    // Lấy dữ liệu JSON từ response
    const dataFromApi = await response.json();

    // Thêm thuộc tính 'checked: false' vào mỗi tag để v-model hoạt động
    tags.value = dataFromApi.map((tag) => ({ ...tag, checked: false }));
    console.log("Lấy tags từ Spring Boot thành công!");
  } catch (error) {
    console.error("Lỗi khi lấy dữ liệu tags từ Spring Boot:", error);
    // Xử lý lỗi, ví dụ hiển thị thông báo cho người dùng
  } finally {
    tagsLoading.value = false;
  }
};

// onMounted vẫn giữ nguyên để gọi hàm khi component được tải
onMounted(() => {
  fetchTags();
});
</script>
