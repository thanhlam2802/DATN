<template>
  <aside class="w-full lg:w-72 bg-white rounded-xl p-1 space-y-4">
    <div v-for="group in filters" :key="group.id" class="border border-gray-200 rounded-lg">
      <button :aria-controls="group.id" :aria-expanded="group.expanded.toString()" @click="toggleGroup(group)"
        class="w-full flex justify-between items-center px-4 py-3 bg-gray-50 hover:bg-indigo-50 transition-colors text-gray-800 font-medium text-base rounded-t-lg">
        <span>{{ group.title }}</span>
        <i :class="[
          'fas transition-transform duration-300',
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
import { reactive } from 'vue';

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
    id: 'price',
    title: 'Khoảng giá',
    expanded: true,
    options: [
      { label: 'Dưới 500.000đ', value: 'under-500k', checked: true },
      { label: '500.000đ - 1.000.000đ', value: '500k-1m', checked: true },
      { label: '1.000.000đ - 2.000.000đ', value: '1m-2m', checked: false },
      { label: 'Trên 2.000.000đ', value: 'above-2m', checked: false },
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