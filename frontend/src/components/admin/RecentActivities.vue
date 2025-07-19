<template>
  <div class="bg-white rounded-xl shadow p-6">
    <div class="font-semibold mb-4 text-gray-700">Hoạt động gần đây</div>
    <ul class="divide-y divide-gray-200">
      <li
        v-for="(item, idx) in activities"
        :key="idx"
        class="py-2 flex items-center gap-3"
      >
        <span class="text-xs text-gray-400 w-14">{{ item.time }}</span>
        <span class="font-semibold text-indigo-600">{{ item.user }}</span>
        <span class="text-gray-700">{{ item.action }}</span>
      </li>
    </ul>
  </div>
</template>
<script setup>
const props = defineProps({
  activities: Array,
});
</script>
