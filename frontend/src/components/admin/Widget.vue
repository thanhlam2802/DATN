<template>
  <div class="bg-white rounded-xl shadow p-6 flex items-center gap-4">
    <div class="text-3xl text-indigo-500">
      <i :class="icon"></i>
    </div>
    <div>
      <div class="text-gray-500 text-sm">{{ title }}</div>
      <div class="text-2xl font-bold text-gray-900">{{ value }}</div>
    </div>
  </div>
</template>
<script setup>
const props = defineProps({
  title: String,
  icon: String,
  value: [String, Number],
});
</script>
