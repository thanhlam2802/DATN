<template>
  <aside class="w-full lg:w-72 bg-white rounded-xl border border-gray-200 p-5 space-y-6 shadow-md"
    style="max-height: 950px; overflow-y: auto;">
    <div v-for="(group, idx) in filters" :key="group.id" class="pb-4">
      <button :aria-controls="group.id" :aria-expanded="group.expanded.toString()" @click="toggleGroup(idx)"
        class="w-full flex justify-between items-center text-gray-800 font-medium text-base hover:text-indigo-600 transition">
        <span>{{ group.title }}</span>
        <i :class="[
          'fas',
          group.expanded ? 'fa-chevron-down' : 'fa-chevron-right',
          'text-xs transition-transform duration-200',
          group.expanded ? 'rotate-0' : 'rotate-90'
        ]">
        </i>
      </button>
      <div v-if="group.expanded" :id="group.id" class="mt-3 space-y-2" v-show="group.options.length > 0">
        <label v-for="(option, i) in group.options" :key="option.value"
          class="flex items-center space-x-2 text-gray-700 text-sm font-medium cursor-pointer hover:text-indigo-600 transition">
          <input type="checkbox" class="form-checkbox text-indigo-600 border-gray-300 rounded focus:ring-indigo-500"
            v-model="option.checked" />
          <span>{{ option.label }}</span>
        </label>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { reactive } from 'vue'

const filters = reactive([
  {
    id: 'property-type',
    title: 'Property type',
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
    title: 'Price',
    expanded: true,
    options: [
      { label: 'Dưới 500.000', value: 'under-500k', checked: true },
      { label: 'Từ 500.000 đến 1.000.000', value: '500k-1m', checked: true },
      { label: 'Từ 1.000.000 đến 2.000.000', value: '1m-2m', checked: false },
      { label: 'Trên 2.000.000', value: 'above-2m', checked: false },
    ],
  },
  {
    id: 'reviews',
    title: 'Reviews',
    expanded: true,
    options: [
      { label: '4 sao trở lên', value: '4star-up', checked: false },
      { label: '3 sao trở lên', value: '3star-up', checked: false },
      { label: '2 sao trở lên', value: '2star-up', checked: false }
    ],
  },
  {
    id: 'amenities',
    title: 'Amenities',
    expanded: true,
    options: [
      { label: 'Wifi miễn phí', value: 'wifi', checked: false },
      { label: 'Bế bởi', value: 'pool', checked: false },
      { label: 'Bữa sáng miễn phí', value: 'breakfast', checked: false }
    ],
  },
  {
    id: 'rooms-beds',
    title: 'Rooms & beds',
    expanded: true,
    options: [
      { label: '2 giường trở lên', value: '2beds-up', checked: false },
      { label: '3 người trở lên', value: '3people-up', checked: false }
    ],
  },
  {
    id: 'accessibility',
    title: 'Accessibility',
    expanded: true,
    options: [
      { label: 'Thang máy', value: 'elevator', checked: false },
      { label: 'Xe lăn truy cập', value: 'wheelchair', checked: false }
    ],
  },
])

function toggleGroup(index) {
  filters[index].expanded = !filters[index].expanded
}
</script>
