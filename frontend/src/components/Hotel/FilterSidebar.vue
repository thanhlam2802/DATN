<template>
  <aside class="w-full lg:w-72 bg-white rounded-xl p-1 space-y-4">

    <div class="border border-gray-200 rounded-lg">
      <button aria-controls="price-range-slider" aria-expanded="true"
        class="w-full flex justify-between items-center px-4 py-3 bg-indigo-50 hover:bg-indigo-100 transition-colors text-indigo-800 font-medium text-base rounded-t-lg">
        <span>Khoảng giá</span>
      </button>

      <div id="price-range-slider" class="px-5 pt-6 pb-4 space-y-4">
        <Slider v-model="priceRange" :min="MIN_PRICE" :max="MAX_PRICE" :step="PRICE_STEP" :tooltips="false"
          :lazy="false" class="price-slider" />

        <div class="flex items-center justify-between gap-3 text-sm">
          <div class="w-1/2">
            <label for="min-price" class="block text-xs text-gray-500 mb-1">Tối thiểu</label>
            <div class="relative">
              <input id="min-price" type="text" v-model="minPriceFormatted"
                class="w-full border-gray-300 rounded-md shadow-sm text-sm focus:ring-indigo-500 focus:border-indigo-500" />
              <span class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400">VND</span>
            </div>
          </div>
          <div class="w-1/2">
            <label for="max-price" class="block text-xs text-gray-500 mb-1">Tối đa</label>
            <div class="relative">
              <input id="max-price" type="text" v-model="maxPriceFormatted"
                class="w-full border-gray-300 rounded-md shadow-sm text-sm focus:ring-indigo-500 focus:border-indigo-500" />
              <span class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400">VND</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-for="group in filters" :key="group.id" class="border border-gray-200 rounded-lg">
      <button :aria-controls="group.id" :aria-expanded="group.expanded.toString()" @click="toggleGroup(group)"
        class="w-full flex justify-between items-center px-4 py-3 bg-indigo-50 hover:bg-indigo-100 transition-colors text-indigo-800 font-medium text-base rounded-t-lg">
        <span>{{ group.title }}</span>
        <i :class="[
          'fas transition-transform duration-300 text-indigo-600',
          group.expanded ? 'fa-chevron-down' : 'fa-chevron-up',
        ]"></i>
      </button>

      <div v-show="group.expanded" :id="group.id" class="px-4 pt-3 pb-2 space-y-2">
        <label
          v-for="option in (group.showAll ? group.options : group.options.slice(0, group.visibleLimit || group.options.length))"
          :key="option.value"
          class="flex items-center space-x-2 text-gray-700 text-sm font-medium cursor-pointer hover:text-indigo-600 transition">
          <input type="checkbox" class="form-checkbox text-indigo-600 border-gray-300 rounded focus:ring-indigo-500"
            v-model="option.checked" />
          <span>{{ option.label }}</span>
        </label>

        <div v-if="group.options.length > (group.visibleLimit || group.options.length)" @click="toggleShowAll(group)"
          class="text-indigo-600 hover:text-indigo-800 text-sm font-semibold cursor-pointer pt-2 flex items-center gap-1">
          <span>{{ group.showAll ? 'Thu gọn' : 'Xem tất cả' }}</span>
          <i :class="['fas text-xs', group.showAll ? 'fa-arrow-up' : 'fa-arrow-down']"></i>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { reactive, ref, computed } from 'vue';
import Slider from '@vueform/slider';
import '@vueform/slider/themes/default.css';


// --- Dữ liệu cho thanh trượt giá ---
const MIN_PRICE = 0;
const MAX_PRICE = 24000000;
const PRICE_STEP = 100000;
const priceRange = ref([500000, 10000000]);

const minPriceFormatted = computed({
  get: () => priceRange.value[0].toLocaleString('vi-VN'),
  set: (value) => {
    const num = parseInt(value.replace(/[^0-9]/g, ''), 10);
    if (!isNaN(num) && num >= MIN_PRICE && num <= priceRange.value[1]) {
      priceRange.value[0] = num;
    }
  }
});

const maxPriceFormatted = computed({
  get: () => priceRange.value[1].toLocaleString('vi-VN'),
  set: (value) => {
    const num = parseInt(value.replace(/[^0-9]/g, ''), 10);
    if (!isNaN(num) && num <= MAX_PRICE && num >= priceRange.value[0]) {
      priceRange.value[1] = num;
    }
  }
});


// --- Dữ liệu cho các bộ lọc còn lại ---
const filters = reactive([
  {
    id: 'property-type',
    title: 'Loại hình',
    expanded: true,
    options: [
      { label: 'Khách sạn', value: 'hotel', checked: true },
      { label: 'Căn hộ', value: 'apartment', checked: false },
      { label: 'Nhà', value: 'house', checked: false },
      { label: 'Villa', value: 'villa', checked: true },
    ],
  },
  {
    id: 'reviews',
    title: 'Đánh giá',
    expanded: true,
    visibleLimit: 3,
    showAll: false,
    options: [
      { label: '4 sao trở lên', value: '4star-up', checked: false },
      { label: '3 sao trở lên', value: '3star-up', checked: false },
      { label: '2 sao trở lên', value: '2star-up', checked: false },
      { label: '1 sao trở lên', value: '1star-up', checked: false },
      { label: 'Không cần đánh giá', value: 'no-reviews', checked: false },
    ],
  },
  {
    id: 'amenities',
    title: 'Tiện nghi',
    expanded: true,
    visibleLimit: 3,
    showAll: false,
    options: [
      { label: 'Wifi miễn phí', value: 'wifi', checked: false },
      { label: 'Hồ bơi', value: 'pool', checked: false },
      { label: 'Bữa sáng miễn phí', value: 'breakfast', checked: false },
      { label: 'Chỗ đỗ xe', value: 'parking', checked: false },
      { label: 'Trung tâm thể dục (Gym)', value: 'gym', checked: false },
      { label: 'Cho phép thú cưng', value: 'pets', checked: false },
      { label: 'Spa & Massage', value: 'spa', checked: false },
    ],
  },
  {
    id: 'rooms-beds',
    title: 'Số phòng & giường',
    expanded: true,
    options: [
      { label: '2 giường trở lên', value: '2beds-up', checked: false },
      { label: '3 người trở lên', value: '3people-up', checked: false },
    ],
  },
  {
    id: 'accessibility',
    title: 'Tiện ích cho người khuyết tật',
    expanded: true,
    options: [
      { label: 'Thang máy', value: 'elevator', checked: false },
      { label: 'Lối đi cho xe lăn', value: 'wheelchair', checked: false },
    ],
  },
]);

function toggleGroup(group) {
  group.expanded = !group.expanded;
}

function toggleShowAll(group) {
  group.showAll = !group.showAll;
}
</script>

<style>
/* Tùy chỉnh màu sắc cho thanh trượt để hợp với theme */
.price-slider {
  --slider-connect-bg: #4f46e5;
  /* indigo-600 */
  --slider-tooltip-bg: #4f46e5;
  --slider-handle-ring-color: #a5b4fc;
}
</style>