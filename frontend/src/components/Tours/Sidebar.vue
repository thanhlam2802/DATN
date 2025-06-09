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
      <h3 class="text-lg font-medium mb-3">Loại hoạt động hoặc hạng mục</h3>
      <div class="space-y-3">
        <label
          v-for="(item, index) in categories"
          :key="index"
          class="flex items-center gap-2"
        >
          <input
            type="checkbox"
            class="w-5 h-5 rounded border-gray-300"
            v-model="item.checked"
          />
          <span>{{ item.name }}</span>
        </label>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { ref } from "vue";
import { XCircleIcon, ChevronDownIcon } from "lucide-vue-next";

const highlightNew = ref(false);

const categories = ref([
  { name: "Thiên nhiên", checked: true },
  { name: "Ngắm cảnh", checked: false },
  { name: "Nghệ thuật & Văn hoá", checked: false },
  { name: "Tour dưới nước", checked: false },
  { name: "Tour trên đất liền", checked: false },
  { name: "Tour ẩm thực", checked: false },
  { name: "Tour theo chủ đề", checked: false },
  { name: "Tour trong ngày", checked: false },
  { name: "Tour nửa ngày", checked: false },
  { name: "Khám phá đảo", checked: false },
]);
</script>

<style scoped></style>
